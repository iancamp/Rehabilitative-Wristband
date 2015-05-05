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
    private LinkedList<DataPoint> baselineData;
    private LinkedList<DataPoint> learningData;
    private NetworkThread wristbandInterface;
    private float baseline;
    private int threshold;
    private float sum;
    private double timerem;
    private int outliers;
    private boolean startBaseline;
    private boolean startLearning;
    private boolean garbage;
    private double startTime;
    private double timeinphase;
    private double minutes;
    private int learnphases;


    /**
     * Constructor to create a Baselining object
     */
    public Baselining() {
        baselineData = new LinkedList<DataPoint>();
        learningData = new LinkedList<DataPoint>();
        wristbandInterface = new NetworkThread();
        wristbandInterface.start();
        sum = 0;
        baseline = 0;
        threshold = 0;
        timerem = 0;
        outliers = 0;
        startBaseline = false;
        startLearning = false;
    }

    /**
     * Returns the sum of magnitudes of data in the current session
     *
     * @return The sum of the magnitudes collected in current session
     */
    public float getSum() {
        return sum;
    }

    /**
     * Returns the current averaged baseline for the current session
     *
     * @return The average of the magnitudes collected in current session
     */
    public float getBaseline() {
        return baseline;
    }

    /**
     * Sets the baseline flag
     *
     * @param boolean to set flag to
     */
    public void setStartBaseline(boolean thing) {
        startBaseline = thing;
    }

    /**
     * Set learning flag
     *
     * @param boolean to set flag to
     */
    public void setStartLearning(boolean thing) {
        startLearning = thing;
    }

    /**
     * Returns the time remaining in whatever phase it is currently in.
     *
     * @return The time remaining in whatever phase it is in.
     */
    public double getTimerem() {
        return timerem;
    }

    /**
     * Gets the current threshold
     *
     * @return The current threshold
     */
    public int getThreshold() {
        return threshold;
    }

    /**
     * Sets the Baselining object's threshold
     *
     * @param threshold Value, which user sets, that the patient must reach to activate the toy
     */
    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setAllThresholds(int threshold) {
        setThreshold(threshold);
        wristbandInterface.setThreshold(threshold);
    }

    /**
     * sets learnphases
     *
     * @param number to change learnphases too
     */
    public void setLearnphases(int phase) {
        learnphases = phase;
    }

    /**
     * Returns the network thread of the baseline
     *
     * @return The baseline's network thread
     */
    public NetworkThread getWristbandInterface() {
        return wristbandInterface;
    }

    /**
     * Returns the last data point found in the session list
     *
     * @return The last data point
     */
    public DataPoint getLastPoint() {
        return baselineData.getLast();
    }

    public boolean getstartBaseline() {
        return startBaseline;
    }


    /**
     * Adds the sum of a list to the baseline's sum and if any point's magnitude is larger than max, sets max to that magnitude
     *
     * @param list
     */
    public void updateSumMax(LinkedList<DataPoint> list) {
        for (DataPoint currentPoint : list) {
            if (currentPoint.getMagnitude() < 15.0) {
                outliers++;
            } else {
                sum += currentPoint.getMagnitude();
            }
        }
    }

    /**
     * Getter for LinkedList of session data
     *
     * @return A linked list of the data points collected in the current session
     */
    public LinkedList<DataPoint> getbaselineData() {
        return baselineData;
    }

    /**
     * Gets newest data point from wristband, adds the data point to the list of
     * session data, and calculates current baseline
     */
    public void updateData() {
        LinkedList<DataPoint> temporaryNewData = new LinkedList<DataPoint>();
        wristbandInterface.copyFromQueue(temporaryNewData);
        if (startBaseline && ((System.currentTimeMillis() - startTime) < timeinphase)) {
            if (!garbage) {
                garbage = true;
            } else {
                updateSumMax(temporaryNewData);
                baselineData.addAll(temporaryNewData);
                baseline = sum / (baselineData.size() - outliers);
                timerem = (minutes - ((System.currentTimeMillis() - startTime) / 60000.0));
            }
        } else if (startLearning && ((System.currentTimeMillis() - startTime) < timeinphase) && learnphases > 0) {
            if (!garbage) {
                garbage = true;
            } else {
                for (DataPoint currentpoint : temporaryNewData) {
                    if (currentpoint.getMagnitude() >= threshold) {
                    }
                }
            }
            learningData.addAll(temporaryNewData);
        }
    }


    /**
     * Updates data for an amount of time and computes threshold
     *
     * @param number of minutes, how long the learning phase will be, and the threshold for the learning phase
     * @return The baselining for the phase
     */
    public void baselinePhase(double minutes) {
        wristbandInterface.resetTime();
        LinkedList<DataPoint> emptytrash = new LinkedList<DataPoint>();
        wristbandInterface.copyFromQueue(emptytrash);
        startBaseline = true;
        this.minutes = minutes;
        timeinphase = (minutes * 60 * 1000);
        timerem = minutes;
        startTime = System.currentTimeMillis();
    }

    /**
     * Activates toy when movement exceeds threshold.
     *
     * @param minutes for the learning phase
     */
    public void learningPhase(double minutes) {
        wristbandInterface.resetTime();
        LinkedList<DataPoint> emptytrash = new LinkedList<DataPoint>();
        wristbandInterface.copyFromQueue(emptytrash);
        startLearning = true;
        learnphases = (int) Math.ceil(minutes / 3);
        this.minutes = Math.ceil(minutes / learnphases);
        timeinphase = (minutes * 60 * 1000);
        timerem = minutes;
        startTime = System.currentTimeMillis();
    }

    /**
     * Resets learning upon time hitting 0
     */
    public void resetlearn(){
        learningData = new LinkedList<DataPoint>();
        learnphases--;
        startTime = System.currentTimeMillis();
    }


    /**
     * Adds a movement String to each DataPoint based on its magnitude
     *
     * @param Baseline data
     */
    public void movement(LinkedList<DataPoint> data) {
        for (DataPoint currentpoint : data) {
            if (currentpoint.getMagnitude() <= 15.0) {
                currentpoint.setMovement("Low");
            } else if (15.0 < currentpoint.getMagnitude() && currentpoint.getMagnitude() <= threshold) {
                currentpoint.setMovement("Medium");
            } else if (currentpoint.getMagnitude() > threshold) {
                currentpoint.setMovement("High");
            }
        }
    }


}