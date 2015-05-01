package client;


import java.lang.String;
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
    private int threshold;
    private float sum;
    private float max;
    private double timerem;
    private int outliers;

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
        max = 0;
        timerem = 0;
        outliers = 0;
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
     * Returns the time remaining in whatever phase it is currently in.
     * @return The time remaining in whatever phase it is in.
     */
    public double getTimerem(){
        return timerem;
    }

    /**
     * Sets the Baselining object's threshold
     * @param threshold Value, which user sets, that the patient must reach to activate the toy
     */
    public void setThreshold(int threshold){ this.threshold = threshold; }

    public void setAllThresholds(int threshold){
        setThreshold(threshold);
        wristbandInterface.setThreshold(threshold);
    }

    /**
     * Returns the network thread of the baseline
     * @return The baseline's network thread
     */
    public NetworkThread getWristbandInterface(){return wristbandInterface;}

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
        testBaseline.max = (float) 2.0;

        return testBaseline;
    }

    /**
     * Collects the most recent data from the wristband then sorts the list 
     * of new data
     */
   /* public void addNewDataToSessionList(){
        LinkedList<DataPoint> newData = new LinkedList<DataPoint>();
      
        wristbandInterface.copyFromQueue(newData);
        Collections.sort(newData);
        
    }    */

    /**
     * Adds the sum of a list to the baseline's sum and if any point's magnitude is larger than max, sets max to that magnitude
     * @param list
     *
     */
    public void updateSumMax(LinkedList<DataPoint> list){
        for(DataPoint currentPoint:list){
            if(max < currentPoint.getMagnitude()){max = currentPoint.getMagnitude();}
            if(currentPoint.getMagnitude() < 15.0){
                outliers++;
            }
            else{sum += currentPoint.getMagnitude();}
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
        //System.out.println(temporaryNewData.size());

        updateSumMax(temporaryNewData);

        sessionData.addAll(temporaryNewData);
        baseline = sum/(sessionData.size() - outliers);

    }

    /**
     * Adds a himedlo String to each DataPoint based on its magnitude
     * @param Baseline data
     */
    public void himedlo(LinkedList<DataPoint> data){
        for(DataPoint currentpoint : data){
            if(currentpoint.getMagnitude() <= (.25*max)){currentpoint.himedlo = "Low";}
            else if(currentpoint.getMagnitude() <= (.75 * max)){currentpoint.himedlo = "Medium";}
            else{currentpoint.himedlo = "High";}
        }}

    /**
     * Updates data for an amount of time and computes threshold
     * @param number of minutes, how long the learning phase will be, and the threshold for the learning phase
     * @return The baselining for the phase
     */
    public Baselining baselinePhase(double minutes){
        wristbandInterface.resetTime();
        Baselining session = new Baselining();
        LinkedList<DataPoint> tempdata = new LinkedList<DataPoint>();
        session.wristbandInterface.copyFromQueue(tempdata);
        long timeinphase = (long) (minutes*60*1000);
        timerem = (long) minutes;
        long startTime = System.currentTimeMillis();
        while((System.currentTimeMillis()-startTime)< timeinphase){
            timerem =  (minutes - ((System.currentTimeMillis() - startTime)/timeinphase));
            session.updateData();
        }
        return session;
    }

    /**
     * Activates toy when movement exceeds threshold.  Also increases threshold when hit too many times.
     * @param minutes for each learningphase, number of phases, and current threshold
     */
    public void learningPhase(double minutes, int phasecount, double threshold) {
        while (phasecount != 0) {

        }
    }

    /**
     * Calculates the current baseline
     * @param magnitude
     */
    /*public void changeBaseline(float magnitude){
        sum += magnitude;
        baseline = sum/sessionData.size();
    }*/

}
 

