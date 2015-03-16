package client;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * NetworkThread is the class responsible for listening on the network interface, and putting data points into a queue
 * as they come in.
 * @author Group 1
 *
 */
public class NetworkThread extends Thread{
	//THIS QUEUE SHOULD *NEVER* BE READ FROM DIRECTLY! ONLY TEMPORARY STORAGE UNTIL MOVED TO PROPER LINKED LIST IN CORE
	private LinkedBlockingDeque<DataPoint> databuffer; //Queue will hold all data that comes in over the network interface
	private boolean running; //Is the thread running
	private Random rand;
	private double starttime;
	
	
	/**
	 * Creates a NetworkThread. Is responsible for collection data from the network and putting it into a buffer to be copied.
	 */
	public NetworkThread(){
		databuffer = new LinkedBlockingDeque<DataPoint>();
		running = true;
		rand = new Random();
		starttime = System.currentTimeMillis();
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
	 * The main loop of the NetworkThread. Reads in data from the network and puts it in the buffer.
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(running){
			DataPoint d = generateFakeData();
			databuffer.addLast(d);
			//System.out.println(d.getMagnitude() + " " + d.getTime());
			
			
			
			try {
				sleep(250); //Thread should sleep for 250 ms to match the c code. Can possibly be removed later, may not be necessary.
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
	}
	
}
