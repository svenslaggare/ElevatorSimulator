/**
 * Represents a passenger
 * @author Anton Jansson
 *
 */
public class Passenger {
	private final int destinationFloor;
	private final long timeOfArrival;
	
	/**
	 * Creates a new passenger
	 * @param destinationFloor The destination floor
	 * @param clock The simulator clock
	 */
	public Passenger(int destinationFloor, SimulatorClock clock) {
		this.destinationFloor = destinationFloor;		
		this.timeOfArrival = clock.timeNow();
	}
	
	/**
	 * Returns the destination floor
	 */
	public int getDestinationFloor() {
		return destinationFloor;
	}
	
	/**
	 * Returns the time of arrival
	 */
	public long getTimeOfArrival() {
		return timeOfArrival;
	}
}
