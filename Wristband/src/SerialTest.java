import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import java.util.Enumeration;


public class SerialTest implements SerialPortEventListener {
	private SerialPort serialPort;
	private int foundcom; //0 if still initializing. -1 if com not found. 1 if com found.
	private final String USTRING = "arduts"; //The unique string prefix we are looking for.
	
	
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 1000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

	public void initialize() {
                // the next line is for Raspberry Pi and 
                // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
                //System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
		foundcom = 0;
		boolean found = false;
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
		
		//First, Find an instance of serial port as set in PORT_NAMES.
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			System.out.println(currPortId.getName());
			
			if (attemptCom(currPortId)){ //If the current comm port is the arduino
				found=true;
				System.out.println("SUCCESS!!!!");
				break;
			}
		}
		if (!found) {
			System.out.println("Could not find COM port.");
			foundcom = -1;
			return;
		}
	}
	
	public boolean attemptCom(CommPortIdentifier portId){
		
		
		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);
			
			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			Thread.sleep(300); //Sleep for 300 MS to see if the message we wanted came through.
			if (foundcom == 1){
				return true;
			}
			else {
				close(); //Close the bad com port.
				return false;
			}
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return false;
	}
	
	public void parse(String s){
		if (s.length() >= USTRING.length() && s.contains(USTRING)){
			foundcom = 1; //We have found the com port, so set the flag.
		}
	}
	
	public float parseAndCrop(String s){
		if (s.length() >= USTRING.length() && s.contains(USTRING)){
			//Make sure the incoming data is not damaged and contains the correct string prefix.
			String temp = s.substring(USTRING.length(), s.length()); //Get the magnitude as a String
			//Now convert it into a number.
			try{
				float val = Float.parseFloat(temp);
				return val;
			} 
			catch (Exception e){
				//If something goes wrong with this value, we don't want it to crash
				System.out.println("Garbage data");
				return -1;
			}
		}
		System.out.println("Garbage data");
		return -1; //This should be caught and not be used to generate data
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine=input.readLine();
				if (foundcom < 1){
					parse(inputLine); //Check if the com port message is on this line
				}
				if (foundcom == 1){ //We found the com port, so start reading the data in
					float val = parseAndCrop(inputLine);
					if (Math.abs(val + 1) < 0.01){
						//val = -1, i.e. an error. Do nothing since error was already printed.
					}
					else{
						System.out.println("VALUE: " + val);
					}
				}
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}

	public static void main(String[] args) throws Exception {
		SerialTest main = new SerialTest();
		main.initialize();
		Thread t=new Thread() {
			public void run() {
				//the following line will keep this app alive for 1000 seconds,
				//waiting for events to occur and responding to them (printing incoming messages to console).
				try {Thread.sleep(1000000);} catch (InterruptedException ie) {}
			}
		};
		t.start();
		System.out.println("Started");
	}
}
