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
    private LinkedList<DataPoint> extinctionData;
    private NetworkThread wristbandInterface;
    private float baseline;
    private int threshold;
    private float sum;
    private double timerem;
    private int outliers;
    private boolean startBaseline;
    private boolean startLearning;
    private boolean startExtinction;
    private double startTime;
    private double timeinphase;
    private double minutes;
    private int learnphases;
    private int phasenum;
    private double baselinetime; //Time that was spent in baseline phase
    private double timeslice; //timeslice to calculate how big each phase should be.
    private double endslice; //When current slice of learning phase will end
    private double pausetime; //Time at the time of a pause event.
    private boolean ispaused; //Whether or not the program is in a paused state.
    private double[] low; //Low occurrences for all three phases as percentages.
    private double[] high; //High occurrences for all three phases as percentages.
    private double[] medium; //Medium occurrences for all three phases as percentages.
    private double[] lowcount;
    private double[] mediumcount;
    private double[] highcount;
    


    /**
     * Constructor to create a Baselining object
     */
    public Baselining() {
        baselineData = new LinkedList<DataPoint>();
        learningData = new LinkedList<DataPoint>();
        extinctionData = new LinkedList<DataPoint>();
        wristbandInterface = new NetworkThread();
        wristbandInterface.start();
        sum = 0;
        baseline = 0;
        threshold = 0;
        timerem = 0;
        outliers = 0;
        startBaseline = false;
        startLearning = false;
        startExtinction = false;
        low = new double[3];
        high = new double[3];
        medium = new double[3];
        lowcount = new double[2];
        mediumcount = new double[2];
        highcount = new double[2];
        for (int i = 0; i < 3; i++){
        	low[i] = 0;
        	high[i] = 0;
        	medium[i] = 0;
        }
        for (int i = 0; i < 2; i++){
        	lowcount[i] = 0;
        	mediumcount[i] = 0;
        	highcount[i] = 0;
        }
        baselinetime = 2; //Default time is 2 minutes unless changed.
    }
    
    /**
     * Returns an array representing the low percentages for each phase.
     * @return 0, 1, and 2 in the array are percentages for baseline, learning, and extinction, respectively.
     */
    public double[] getLowPercentages(){
    	return low;
    }
    
    /**
     * Returns an array representing the medium percentages for each phase.
     * @return 0, 1, and 2 in the array are percentages for baseline, learning, and extinction, respectively.
     */
    public double[] getMediumPercentages(){
    	return medium;
    }
    
    /**
     * Returns an array representing the high percentages for each phase.
     * @return 0, 1, and 2 in the array are percentages for baseline, learning, and extinction, respectively.
     */
    public double[] getHighPercentages(){
    	return high;
    }
    
    /**
     * Returns the current learning phase number that the application is in.
     * @return Returns the current 
     */
    public int getPhaseNum(){
    	return phasenum;
    }
    
    /**
     * Returns the number of learn phases for the Learning Phase.
     * @return The number of learn phases for the Learning Phase.
     */
    public int getLearnPhases(){
    	return learnphases;
    }
    
    /**
     * Returns the data from the learning phase.
     * @return The data from the learning phase.
     */
    public LinkedList<DataPoint> getLearningData(){
    	return learningData;
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

    public boolean getStartLearning(){
        return startLearning;
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

    /**
     * Returns a boolean representing whether application is in baseline phase.
     * @return Returns true if in baseline phase.
     */
    public boolean getstartBaseline() {
        return startBaseline;
    }

    /**
     * Return a boolean representing whether application is in extinction phase.
     * @return Returns true if in extinction phase.
     */
    public boolean getStartExtinction(){
    	return startExtinction;
    }

    /**
     * Adds the sum of a list to the baseline's sum and if any point's magnitude is larger than max, sets max to that magnitude
     *
     * @param list The current list of unadded baseline points.
     */
    public void updateSumMax(LinkedList<DataPoint> list) {
        for (DataPoint currentPoint : list) {
            if (currentPoint.getMagnitude() < 15.0) {
                outliers++;
            } else {
                sum += currentPoint.getMagnitude();
            }
            currentPoint.setPhase("Baseline");
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
     * Getter for extinctionData
     * @return The linked list of data from the extinction phase.
     */
    public LinkedList<DataPoint> getExtinctionData(){
    	return extinctionData;
    }
    
    /**
     * Calculates percentages for Baseline Phase and puts high/med/low into data points for that phase.
     */
    public void baselineBackCalculate(){
    	int highcount = 0;
    	int lowcount = 0;
    	int mediumcount = 0;
    	for (DataPoint p : baselineData){
    		if (p.getMagnitude() > threshold){
    			p.setMovement("high");
    			highcount++;
    		}
    		else if (p.getMagnitude() < 15){
    			p.setMovement("low");
    			lowcount++;
    		}
    		else {
    			p.setMovement("medium");
    			mediumcount++;
    		}
    	}
    	int s = baselineData.size();
    	if (s > 0){
    		low[0] = Math.floor(100*lowcount/s)/100;
    		medium[0] = Math.floor(100*mediumcount/s)/100;
    		high[0] = Math.floor(100*highcount/s)/100;
    	}
    }

    /**
     * Gets newest data point from wristband, adds the data point to the list of
     * session data, and calculates current baseline
     */
    public void updateData() {
    	LinkedList<DataPoint> temporaryNewData = new LinkedList<DataPoint>();
    	wristbandInterface.copyFromQueue(temporaryNewData);
    	if (!ispaused && startBaseline && ((System.currentTimeMillis() - startTime) < timeinphase)) {
    		updateSumMax(temporaryNewData);
    		baselineData.addAll(temporaryNewData);
    		baseline = sum / (baselineData.size() - outliers);
    		timerem = (minutes - ((System.currentTimeMillis() - startTime) / 60000.0));}
    	else if (!ispaused && startBaseline){
    		startBaseline=false;
    	}

    	if (!ispaused && startLearning && ((System.currentTimeMillis() - startTime) < timeinphase)) {
    		learningData.addAll(temporaryNewData);
    		for (DataPoint currentpoint : temporaryNewData) {
    			movementLearn(currentpoint);}
    		timerem = (minutes - ((System.currentTimeMillis() - startTime) / 60000.0));
    		if (minutes - timerem > endslice){
    			phasenum++;
    			endslice+=timeslice;
    		}
    	}
    	else if (!ispaused && startLearning){
    		
    		startLearning=false;
    		}
    	if (!ispaused && startExtinction && ((System.currentTimeMillis() - startTime) < timeinphase)) {
    		extinctionData.addAll(temporaryNewData);
    		for (DataPoint currentpoint : temporaryNewData) {
    			learn(currentpoint);
    			currentpoint.setPhase("Extinction");
    		}
    		timerem = (minutes - ((System.currentTimeMillis() - startTime) / 60000.0));}
    	else if (!ispaused){startExtinction=false;}
    	}

    /**
     * Pauses the execution of either phase. Updates time to ensure timing is consistent after pause.
     */
    public void pause(){
    	if (!ispaused){
    		ispaused = true;
    		pausetime = System.currentTimeMillis();
    	}
    	else {
    		ispaused = false;
    		startTime += (System.currentTimeMillis() - pausetime);
    	}
    }
    
    /**
     * Calls cancel() function and clears all collected data from learning phase.
     */
    public void learningCancel(){
    	cancel();
    	learningData.clear();
    	high[1] = 0;
    	medium[1] = 0;
    	low[1] = 0;
    	highcount[0] = 0;
    	mediumcount[0] = 0;
    	lowcount[0] = 0;
    }
    
    /**
     * Calls cancel() function and clears all collected data from baseline phase.
     */
    public void baselineCancel(){
    	cancel();
    	baselineData.clear();
    	sum = 0;
        baseline = 0;
        timerem = 0;
        outliers = 0;
        high[0] = 0;
        medium[0] = 0;
        low[0] = 0;
    }
    
    /**
     * Calls cancel() function and clears all collected data from extinction phase.
     */
    public void extinctionCancel(){
    	cancel();
    	extinctionData.clear();
    	high[2] = 0;
    	medium[2] = 0;
    	low[2] = 0;
    	highcount[1] = 0;
    	mediumcount[1] = 0;
    	lowcount[1] = 0;
    }
    
    /**
     * Cancels the current phase execution and returns to the main screen.
     */
    private void cancel(){
    	ispaused = false;
    	startBaseline = false;
    	startLearning = false;
    	startExtinction = false;
    }
    
    public void extinctionPhase(double minutes){
    	 wristbandInterface.resetTime();
         LinkedList<DataPoint> emptytrash = new LinkedList<DataPoint>();
         wristbandInterface.copyFromQueue(emptytrash);
         startExtinction = true;
         this.minutes = minutes;
         baselinetime = minutes;
         timeinphase = (minutes * 60 * 1000);
         timerem = minutes;
         startTime = System.currentTimeMillis();
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
        baselinetime = minutes;
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
    	baselineBackCalculate();
        wristbandInterface.resetTime();
        LinkedList<DataPoint> emptytrash = new LinkedList<DataPoint>();
        wristbandInterface.copyFromQueue(emptytrash);
        startLearning = true;
        this.minutes = minutes;
        if (minutes < baselinetime){
        	baselinetime = minutes;
        }
        learnphases = (int)(minutes/baselinetime);
        if (learnphases == 0){
        	learnphases = 1;
        }
        timeinphase = (minutes * 60 * 1000);
        timeslice = minutes/learnphases;
        endslice = timeslice;
        timerem = minutes;
        phasenum = 1;
        startTime = System.currentTimeMillis();
    }
    
    /**
     * Adds movement value to given data point. Also calculates high/med/low percentages
     * @param currentpoint Baseline data point.
     */
    public void learn(DataPoint currentpoint){
    	if (startLearning){
    		if (currentpoint.getMagnitude() <= 15.0) {
    			currentpoint.setMovement("Low");
    			lowcount[0]++;
    		} else if (currentpoint.getMagnitude() <= threshold) {
    			currentpoint.setMovement("Medium");
    			mediumcount[0]++;
    		} else {
    			currentpoint.setMovement("High");
    			highcount[0]++;
    		}
    		low[1] = Math.floor(100*lowcount[0]/learningData.size())/100;
			medium[1] = Math.floor(100*mediumcount[0]/learningData.size())/100;
			high[1] = Math.floor(100*highcount[0]/learningData.size())/100;
    	}
    	else { //Else extinction phase
    		if (currentpoint.getMagnitude() <= 15.0) {
    			currentpoint.setMovement("Low");
    			lowcount[1]++;
    		} else if (currentpoint.getMagnitude() <= threshold) {
    			currentpoint.setMovement("Medium");
    			mediumcount[1]++;
    		} else {
    			currentpoint.setMovement("High");
    			highcount[1]++;
    		}
    		low[2] = Math.floor(100*lowcount[1]/extinctionData.size())/100;
			medium[2] = Math.floor(100*mediumcount[1]/extinctionData.size())/100;
			high[2] = Math.floor(100*highcount[1]/extinctionData.size())/100;
    	}
    }

    /**
     * Adds a movement String to each DataPoint based on its magnitude
     *
     * @param Baseline data
     */
    public void movementLearn(DataPoint currentpoint) {
        learn(currentpoint);
        currentpoint.setPhase("Learning" + phasenum);
    }


}