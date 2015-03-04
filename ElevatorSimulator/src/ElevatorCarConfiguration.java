/**
 * Represents a configuration for an elevator car
 * @author Anton Jansson
 *
 */
public class ElevatorCarConfiguration {
	private final int capacity;
	private final double floorTime;
	private final double stopTime;
	
	/**
	 * Creates a new elevator car configuration
	 * @param capacity The capacity
	 * @param floorTime The floor time
	 * @param stopTime The stop time
	 */
	public ElevatorCarConfiguration(int capacity, double floorTime, double stopTime) {
		this.capacity = capacity;
		this.floorTime = floorTime;
		this.stopTime = stopTime;
	}
	
	/**
	 * Returns the capacity
	 */
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * Returns the floor time
	 */
	public double getFloorTime() {
		return floorTime;
	}
	
	/**
	 * Returns the stop time
	 */
	public double getStopTime() {
		return stopTime;
	}
}
