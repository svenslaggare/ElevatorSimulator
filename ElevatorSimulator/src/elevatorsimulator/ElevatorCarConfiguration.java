package elevatorsimulator;
/**
 * Represents a configuration for an elevator car
 * @author Anton Jansson
 *
 */
public class ElevatorCarConfiguration {
	private final int capacity;
	private final double floorTime;
	private final double stopTime;
	private final double startTime;
	private final double doorTime;
	
	private static final ElevatorCarConfiguration DEFAULT_CONFIGURATION = new ElevatorCarConfiguration(8, 1.5, 2.6, 2.6, 1);
	
	/**
	 * Creates a new elevator car configuration
	 * @param capacity The capacity
	 * @param floorTime The floor time
	 * @param stopTime The stop time
	 * @param doorTime The door time
	 */
	public ElevatorCarConfiguration(int capacity, double floorTime, double stopTime, double startTime, double doorTime) {
		this.capacity = capacity;
		this.floorTime = floorTime;
		this.stopTime = stopTime;
		this.startTime = startTime;
		this.doorTime = doorTime;
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
	
	/**
	 * Returns the start time
	 */
	public double getStartTime() {
		return startTime;
	}
	
	/**
	 * Returns the door time
	 */
	public double getDoorTime() {
		return doorTime;
	}
	
	/**
	 * Returns the default configuration
	 * @return
	 */
	public static ElevatorCarConfiguration defaultConfiguration() {
		return DEFAULT_CONFIGURATION;
	}
}
