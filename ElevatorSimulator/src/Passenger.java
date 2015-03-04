/**
 * Represents a passenger
 * @author Anton Jansson
 *
 */
public class Passenger {
	private final long id;
	private final int destinationFloor;
	private final long timeOfArrival;
	
	/**
	 * Creates a new passenger
	 * @param id The id of the passenger
	 * @param destinationFloor The destination floor
	 * @param clock The simulator clock
	 */
	public Passenger(long id, int destinationFloor, SimulatorClock clock) {
		this.id = id;
		this.destinationFloor = destinationFloor;		
		this.timeOfArrival = clock.timeNow();
	}
	
	/**
	 * Returns the id
	 */
	public long getId() {
		return id;
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
