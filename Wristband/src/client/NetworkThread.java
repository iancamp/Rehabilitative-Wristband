package client;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * NetworkThread is the class responsible for listening on the network interface, and putting data points into a queue
 * as they come in.
 * @author Group 1
 *
 */
public class NetworkThread extends Thread implements SerialPortEventListener{
	private final String USTRING = "arduts"; //The unique string prefix we are looking for.
	private final String ACKSTRING = "ack"; //Unique ACK string prefix for an ACK response
	private final int TO_INCREMENT = 200; //How many ms the searchtimeout will increment by after failure
	private final int NUMATTEMPTS = 5;//Number of attempts that will be made to connect.
	private int searchtimeout = 300; //How many ms the thread will wait for response
	private final double TIMEOUT_THRESHOLD = 5; //If no message is received in this time period, connection is assumed lost.
	
	//THIS QUEUE SHOULD *NEVER* BE READ FROM DIRECTLY! ONLY TEMPORARY STORAGE UNTIL MOVED TO PROPER LINKED LIST IN CORE
	private LinkedBlockingDeque<DataPoint> databuffer; //Queue will hold all data that comes in over the network interface
	private boolean running; //Is the thread running
	private Random rand;
	private double starttime;
	private double lastreceived; //Last time a message was received from the device.
	private boolean timeout; //Whether or not the device has timed out. True if no message received in last 5 seconds.
	private boolean randomon = false; //Whether or not random generation is turned on (used to test without arduino)
	private SerialPort serialPort;
	private int foundcom; //0 if still initializing. -1 if com not found. 1 if com found.
	private int attempts; //How many attempts the program will make in trying to find the comm port.
	private boolean reset = false; //Whether or not we need to reset. True if reset is called for.
	private int threshold = 0; //Current threshold that should be sent over
	private boolean sendthreshold = false; //Whether or not the threshold needs to be sent and updated on the device.
	private boolean ack = false; //Whether or not we received an ack response from the Arduino after sending threshold.
	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	*/
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 1000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

	
	/**
	 * Creates a NetworkThread. Is responsible for collection data from the network and putting it into a buffer to be copied.
	 */
	public NetworkThread(){
		databuffer = new LinkedBlockingDeque<DataPoint>();
		running = true;
		rand = new Random();
		foundcom = 0;
		timeout = false;
		lastreceived = -1;
		attempts = NUMATTEMPTS;
		//if (!randomon){ //If random is not turned on, start comm port setup.
		//	startup();
		//}
		starttime = System.currentTimeMillis();
	}
	
	/**
	 * Resets the starttime variable. Necessary to ensure that time=0 is when the experiment actually starts.
	 */
	public void resetTime(){
		starttime = System.currentTimeMillis();
	}
	
	/**
	 * Returns the timeout value. If true, the device has stopped sending data to the application.
	 * @return Returns true if the device has stopped responding. Otherwise returns false.
	 */
	public boolean getTimeOut(){
		return timeout;
	}
	
	/**
	 * Returns the foundcom value, which can be one of the following:
	 * 1:  The comm port has been found
	 * 0:  The comm port is still being searched for
	 * -1: The comm port was not found
	 * @return Returns a value representing whether or not the comm port has been found.
	 */
	public int getFoundCom(){
		return foundcom;
	}
	
	/**
	 * Checks if the thread is currently running.
	 * @return Returns true if the thread is still running.
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Used to deactivate the thread. Should be called before threads are joined at the end of the application.
	 */
	public void stopRunning() {
		running = false;
	}
	
	/**
	 * Copies data from the NetworkThread queue into the given core data list. NOTE: This deletes *all* data inside
	 * the NetworkThread queue.
	 * @param data The current data list.
	 */
	public void copyFromQueue(LinkedList<DataPoint> data){
		databuffer.drainTo(data); //Moves all data from the buffer to the core data list.
	}
	
	/**
	 * Generates a fake data point to be used, and gives it the current time.
	 * @return Returns a randomly generated data point.
	 */
	private DataPoint generateFakeData(){
		float t = rand.nextFloat();
		int s = rand.nextInt(100);
		return new DataPoint(t*s, (System.currentTimeMillis() - starttime)/1000l);
	}
	
	/**
	 * Increments searchtimeout by TO_INCREMENT, never exceeding 5 seconds
	 */
	private void incrementSearchTO(){
		searchtimeout+=TO_INCREMENT;
		if (searchtimeout>5000){
			searchtimeout=5000;
		}
	}
	
	/**
	 * Call to attempt a restart if the device failed to connect.
	 * Only allowed to be called if the program failed to connect. Otherwise will do nothing.
	 */
	public void restartFailedToConnect(){
		if (foundcom == -1){
			reset = true;
		}
		foundcom = 0;
	}
	
	/**
	 * If the device times out during an active connection, this function will attempt a reconnection.
	 */
	public void restartTimeout(){
		closePort();
		timeout = false;
		reset = true;
	}
	
	/**
	 * Attempts to find and set up the comm port. Called as part of the class initialization
	 * unless random mode is turned on.
	 */
	private void startup() {
		//System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyACM0");
		attempts = NUMATTEMPTS;
		foundcom = 0;
		boolean found = false;
		CommPortIdentifier portId = null;
		//First, Find an instance of serial port as set in PORT_NAMES.
		while (attempts > 0 && !found){
			Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
			while (portEnum.hasMoreElements()) {
				CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
				System.out.println(currPortId.getName());

				if (attemptCom(currPortId)){ //If the current comm port is the arduino
					found=true;
					System.out.println("SUCCESS!!!!");
					break;
				}
			}
			if (!found){
				incrementSearchTO(); //We didn't find com port, so search longer next time.
				System.out.println("FAILURE #" + (6 - attempts));
			}
			attempts--;
		}
		if (!found) {
			System.out.println("Could not find COM port.");
			foundcom = -1;
		}
		reset = false;
	}
	
	/**
	 * Attempts to listen on a certain comm port to find the arduino.
	 * @param portId The given comm port
	 * @return Returns true if the comm port was found. Otherwise false
	 */
	private boolean attemptCom(CommPortIdentifier portId){
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
			sleep(searchtimeout);//Wait for timeout period for a response.
			
			if (foundcom == 1){
				return true;
			}
			else {
				closePort(); //Close the bad com port.
				return false;
			}
		} catch (Exception e) {
			System.err.println(e.toString());
		}
		return false;
	}
	
	/**
	 * Parses the given string and tries to find the unique prefix sent by the arduino. Sets the foundcom flag to 1 if successful.
	 * @param s The given string that is read in.
	 */
	private void parse(String s){
		if (s.length() >= USTRING.length() && s.contains(USTRING)){
			foundcom = 1; //We have found the com port, so set the flag.
		}
	}
	
	/**
	 * Parses the given string, finds the unique prefix, and returns the number located beyond the unique prefix.
	 * @param s The given string that is read in.
	 * @param u Unique string given to compare to.
	 * @return Returns the magnitude located within the string. Returns -1 if the data is incorrect.
	 */
	private float parseAndCrop(String s, String u){
		if (s.length() >= u.length() && s.contains(u)){
			//Make sure the incoming data is not damaged and contains the correct string prefix.
			String temp = s.substring(u.length(), s.length()); //Get the magnitude as a String
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
		return -1; //This should be caught and not be used to generate data
	}

	/**
	 * Closes the current open comm port to prevent locking.
	 */
	private void closePort() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
			foundcom = 0;
		}
	}
	
	/**
	 * This function should be called whenever the application terminates. Closes the comm port to prevent locking,
	 * and stops the execution of the thread.
	 */
	public synchronized void close(){
		closePort();
		running = false;
	}

	/**
	 * This function listens on the comm port for messages from the arduino
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				lastreceived = System.currentTimeMillis(); //We received a message, so update the time.
				String inputLine=input.readLine();
				if (foundcom < 1){
					parse(inputLine); //Check if the com port message is on this line
				}
				if (foundcom == 1){ //We found the com port, so start reading the data in
					float val = parseAndCrop(inputLine, USTRING);
					if (Math.abs(val + 1) < 0.01){
						//val = -1, meaning not an arduts message. Could be an ACK, so check for that.
						val = parseAndCrop(inputLine, ACKSTRING);
						if (Math.abs(val + 1) < 0.01){
							//val = -1, meaning error.
							System.out.println("Garbage Data");
						}
						else {
							ack = true;
							System.out.println("ACK received with value " + val);
						}
					}
					else{ //if value is valid, add it to the list
						databuffer.addLast(new DataPoint(val, (System.currentTimeMillis() - starttime)/1000l));
						//System.out.println("VALUE: " + val);
					}
				}
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
	
	/**
	 * This function should be called to set the threshold within NetworkThread and to make NetworkThread send
	 * the threshold to the device.
	 * @param threshold The new threshold value.
	 */
	public synchronized void setThreshold(int threshold){
		this.threshold = threshold;
		sendthreshold = true;
	}
	
	/**
	 * Attempts to send the threshold to the device. Will only function if the device is currently connected.
	 */
	private void sendThreshold(){
		if (foundcom == 1 && !reset && !timeout){ //Check for flags. Do not attempt to send data if the device has any connection error.
			System.out.println("TRYING TO SEND: "+threshold);
			String tosend = Integer.toString(threshold) +'\n';
			byte[] barray = tosend.getBytes();
			try {
				output.write(barray);
			} catch (Exception e){
				System.out.println("Failed to write characters to Arduino");
			}
			double curtime = System.currentTimeMillis();
			try {
				sleep(300); //Wait 300 ms for a response
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (ack){
				ack = false;
				sendthreshold = false; //Only stop threshold flag if the send succeeded.
			}
			else {
				System.out.println("No ACK received from Arduino");
			}
		}
	}
	
	

	/**
	 * The main loop of the NetworkThread. Reads in data from the network and puts it in the buffer.
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if (!randomon && foundcom != 1){
			startup();
		}
		else if (randomon){
			foundcom = 1;
		}
		while(running){
			if (randomon){
				DataPoint d = generateFakeData();
				databuffer.addLast(d);
			}
			if (reset && foundcom != 1){
				startup();
			}
			if (sendthreshold && !randomon){
				sendThreshold();
			}
			try {
				sleep(250); //Thread should sleep for 250 ms to match the c code. Can possibly be removed later, may not be necessary.
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double temptime = (System.currentTimeMillis() - lastreceived)/1000;
			if (foundcom == 1 && temptime > TIMEOUT_THRESHOLD && !randomon){
				System.out.println("TIMEOUT AT: " + temptime);
				timeout = true;
				closePort();
				System.out.println("Timeout has occurred. Check the device connection.");
			}
		}
		
		System.out.println("NetworkThread has finished running.");
		
	}
	
}
