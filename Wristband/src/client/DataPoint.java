package client;

/**
 * DataPoint which holds both a time and a magnitude value to keep track of motion.
 * @author Group 1
 *
 */
public class DataPoint implements Comparable{
	private float magnitude;
	private double time;
	private String movement; //previously himedlo
	private String phase;
	
	/**
	 * Creates a DataPoint object to hold data as it comes in.
	 * @param magnitude
	 * @param time
	 */
	public DataPoint(float magnitude,double time) {
		this.magnitude = magnitude;
		this.time = time;
		this.movement = "";
	}

	/**
	* Getter for the movement value
	* @return Returns the string corresponding to the movement (high,medium,low)
	*/
	public String getMovement(){
		return movement;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	/**
	* Setter for the movement value
	* @param movement The new movement value
	*/
	public void setMovement(String movement){
		this.movement = movement;
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
	
	/**
	 * Prints a DataPoint in a meaningful way
	 * @param d The datapoint to represent as a string
	 * @return The string representing the DataPoint ("<time,magnitude,movement>")
	 * @Override
	 */
	public String toString(){
		return "<" + this.getTime() + "," + this.getMagnitude() + "," + this.getMovement() + ">";
	}

	
}
