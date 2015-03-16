package client;

/**
 * DataPoint which holds both a time and a magnitude value to keep track of motion.
 * @author Group 1
 *
 */
public class DataPoint implements Comparable{
	private float magnitude;
	private double time;
	
	/**
	 * Creates a DataPoint object to hold data as it comes in.
	 * @param magnitude
	 * @param time
	 */
	public DataPoint(float magnitude,double time) {
		this.magnitude = magnitude;
		this.time = time;
	}

	/**
	 * Returns the magnitude value.
	 * @return Returns the magnitude value.
	 */
	public float getMagnitude() {
		return magnitude;
	}

	/**
	 * Sets the magnitude value.
	 * @param magnitude The given magnitude.
	 */
	public void setMagnitude(float magnitude) {
		this.magnitude = magnitude;
	}

	/**
	 * Returns the time stored in this DataPoint.
	 * @return Returns the time value.
	 */
	public double getTime() {
		return time;
	}

	/**
	 * Sets the time paramter.
	 * @param time The given time parameter.
	 */
	public void setTime(double time) {
		this.time = time;
	}

	/**
	 * Function implements Comparable interface; compares based on time.
	 */
	@Override
	public int compareTo(Object arg0) {
		DataPoint other = (DataPoint)arg0;
		if (other.time < this.time){
			return -1;
		}
		else if (other.time > this.time){
			return 1;
		}
		return 0;
	}
	
	
	
	
}
