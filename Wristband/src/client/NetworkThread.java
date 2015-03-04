package client;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.LinkedBlockingDeque;


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
	 * @return
	 */
	public boolean isRunning() {
		return running;
	}

	/**
	 * Used to deactivate the thread.
	 * @param running Whether or not the thread should continue running.
	 */
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	/**
	 * Copies data from the NetworkThread queue into the given core data list. NOTE: This deletes *all* data inside
	 * the NetworkThread queue.
	 * @param data The current data list.
	 */
	public void copyFromQueue(LinkedList<DataPoint> data){
		databuffer.drainTo(data); //Moves all data from the buffer to the core data list.
	}
	
	//Returns a fake double data point. Will be deleted at a later point.
	public DataPoint generateFakeData(){
		float t = rand.nextFloat();
		int s = rand.nextInt(100);
		int sign = rand.nextInt(2);
		if (sign != 1){
			sign = -1;
		}
		return new DataPoint(t*s*sign, (System.currentTimeMillis() - starttime)/1000l);
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
	
	//test
	public static void main(String[] args){
		NetworkThread nthread = new NetworkThread();
		nthread.start();
	}
	
}
