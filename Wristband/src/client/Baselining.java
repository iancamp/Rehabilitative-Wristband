package client;


import java.util.LinkedList;
import java.util.Collections;

/**
 * Baselining class
 * Objects calculates the average of the movement from the wristband
 * @author Group 1
 *
 */
public class Baselining {
    private LinkedList<DataPoint> sessionData;
    private NetworkThread wristbandInterface;
    //DataPoint newPoint;
    private float baseline;
    private float threshold;
    private float sum;
    
    /**
     * Constructor to create a Baselining object
     */
    public Baselining(){
        sessionData = new LinkedList<DataPoint>();
        wristbandInterface = new NetworkThread();
        wristbandInterface.start();
        sum = 0;
        baseline = 0;
        threshold = 0;
    }
    
    /**
     * Returns the sum of magnitudes of data in the current session
     * @return The sum of the magnitudes collected in current session
     */
    public float getSum(){
        return sum;
    }
    
    /**
     * Returns the current averaged baseline for the current session
     * @return The average of the magnitudes collected in current session
     */
    public float getBaseline(){
        return baseline;
    }
    
    /**
     * Returns the last data point found in the session list
     * @return The last data point
     */
    public DataPoint getLastPoint(){
        return sessionData.getLast();
    }
    
    
    
    /**
     * Use to create a baselining object for testing purposes
     * @return A Baselining object used in tests
     */
    public static Baselining generateTestBasline(){
        Baselining testBaseline = new Baselining();
        testBaseline.sessionData.add(new DataPoint(1,1));
        testBaseline.sessionData.add(new DataPoint(2,2));
        //testBaseline.newPoint = new DataPoint(2,2);
        testBaseline.sum = (float) 3;
        testBaseline.baseline = (float) 1.5;
        
        return testBaseline;
    }
    
    /**
     * Collects the most recent data from the wristband then sorts the list 
     * of new data
     */
    public void addNewDataToSessionList(){
        LinkedList<DataPoint> newData = new LinkedList<DataPoint>();
      
        wristbandInterface.copyFromQueue(newData);
        Collections.sort(newData);
        
    }    
    
    /**
     * Calculates the current baseline
     * @param list 
     */
    public void calculateBaseline(LinkedList<DataPoint> list){
        for(DataPoint currentPoint:list){
            sum += currentPoint.getMagnitude();
        }
    }
    
    /**
     * Getter for LinkedList of session data
     * @return A linked list of the data points collected in the current session
     */
    public LinkedList<DataPoint> getSessionData(){
        return sessionData;
    }
    
    /**
     * Gets newest data point from wristband, adds the data point to the list of
     * session data, and calculates current baseline
     */
    public void updateData(){
       LinkedList<DataPoint> temporaryNewData = new LinkedList<DataPoint>();
      
       wristbandInterface.copyFromQueue(temporaryNewData);
       System.out.println(temporaryNewData.size());
       
      calculateBaseline(temporaryNewData);
      
      sessionData.addAll(temporaryNewData);
      baseline = sum/sessionData.size();
       
    }
    
    /**
     * Calculates the current baseline
     * @param magnitude  
     */
    public void changeBaseline(float magnitude){
        sum += magnitude;
        baseline = sum/sessionData.size();
    }
    
}
 