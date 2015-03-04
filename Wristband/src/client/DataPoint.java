package client;

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

	public float getMagnitude() {
		return magnitude;
	}

	public void setMagnitude(float magnitude) {
		this.magnitude = magnitude;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

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
