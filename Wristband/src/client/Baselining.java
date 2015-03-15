package client;


import java.util.LinkedList;
import java.util.Collections;

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
        sum = 0;
        baseline = 0;
        threshold = 0;
    }
    
    /**
     * Returns the sum of magnitudes of data in the current session
     * @return sum
     */
    public float getSum(){
        return sum;
    }
    
    public float getBaseline(){
        return baseline;
    }
    
    public DataPoint getLastPoint(){
        return sessionData.getLast();
    }
    
    
    
    /**
     * Use to create a baselining object for testing purposes
     * @return Baselining
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
     * Sorts the list of new data from the wristband
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
    
    
    
    /*
    Getter for LinkedList of session data
    */
    public LinkedList<DataPoint> getSessionData(){
        return sessionData;
    }
    
    /*
    Gets newest data point from wristband, adds the data point to the list of
    session data, and calculates current baseline
    */
    public void updateData(){
       LinkedList<DataPoint> temporaryNewData = new LinkedList<DataPoint>();
      
       wristbandInterface.copyFromQueue(temporaryNewData);
       
      calculateBaseline(temporaryNewData);
      
      sessionData.addAll(temporaryNewData);
        sessionData.add(wristbandInterface.generateFakeData());
      
      baseline = sum/sessionData.size();
       
    }
    
    /*
    Calculates the current baseline
    */
    public void changeBaseline(float magnitude){
        sum += magnitude;
        baseline = sum/sessionData.size();
    }
    
}
 