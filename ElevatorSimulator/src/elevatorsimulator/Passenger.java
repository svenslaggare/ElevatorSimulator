package elevatorsimulator;
import java.text.DecimalFormat;

/**
 * Represents a passenger
 * @author Anton Jansson and Kristoffer Uggla Lingvall
 *
 */
public class Passenger {
	private final long id;
	private final int arrivalFloor;
	private final int destinationFloor;
	private final int capacity;
	private boolean boarded;
	
	private final long timeOfArrival;
	private long timeOfRideStarted;
	
	private static final DecimalFormat statsFormat = new DecimalFormat("#.###");
	
	/**
	 * Creates a new passenger
	 * @param id The id of the passenger
	 * @param arrivalFloor The floor the passenger arrived on
	 * @param destinationFloor The destination floor
	 * @parma capacity How much capacity the passenger effect
	 * @param clock The simulator clock
	 */
	public Passenger(long id, int arrivalFloor, int destinationFloor, int capacity, SimulatorClock clock) {
		this.id = id;
		this.arrivalFloor = arrivalFloor;
		this.destinationFloor = destinationFloor;		
		this.capacity = capacity;
		this.timeOfArrival = clock.timeNow();
	}
	
	/**
	 * Returns the id
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * Returns the arrival floor
	 */
	public int getArrivalFloor() {
		return arrivalFloor;
	}
	
	/**
	 * Returns the destination floor
	 */
	public int getDestinationFloor() {
		return destinationFloor;
	}
	
	/**
	 * Returns the direction of the passenger
	 */
	public Direction getDirection() {
		return Direction.getDirection(arrivalFloor, destinationFloor);
	}
	
	/**
	 * Returns the capacity
	 */
	public int getCapacity() {
		return capacity;
	}
	
	/**
	 * Indicates if the passenger has boarded any elevator
	 */
	public boolean hasBoarded() {
		return this.boarded;
	}
	
	/**
	 * Boards an elevator
	 */
	public void board() {
		this.boarded = true;
	}
	
	/**
	 * Returns the time of arrival
	 */
	public long getTimeOfArrival() {
		return timeOfArrival;
	}
	
	/**
	 * Returns the time of the ride started
	 */
	public long getTimeOfRideStarted() {
		return timeOfRideStarted;
	}
	
	/**
	 * Marks that the ride for the passenger has started
	 * @param clock The clock
	 */
	public void rideStarted(SimulatorClock clock) {
		this.timeOfRideStarted = clock.timeNow();
	}
	
	/**
	 * Calculates the wait time
	 * @param clock The simulator clock
	 */
	public long waitTime(SimulatorClock clock) {
		if (this.boarded) {
			return clock.durationFromRealTime(this.timeOfRideStarted - this.timeOfArrival);
		} else {
			return clock.durationFromRealTime(clock.timeNow() - this.timeOfArrival);
		}
	}
	
	/**
	 * Calculates the ride time
	 * @param clock The simulator clock
	 */
	public long rideTime(SimulatorClock clock) {
		return clock.durationFromRealTime(clock.timeNow() - this.timeOfRideStarted);
	}
	
	/**
	 * Returns the statistics for the passenger
	 * @param clock The clock
	 */
	public String getStats(SimulatorClock clock) {
		return 
			"Wait time: " + statsFormat.format(clock.asSecond(this.waitTime(clock))) + " s"
			+ " Ride time: " + statsFormat.format(clock.asSecond(this.rideTime(clock))) + " s";
	}
	
	@Override
	public String toString() {
		return "{ id: " + this.id + ", travel: " + this.arrivalFloor + "->" + this.destinationFloor + " }";
	}
}
