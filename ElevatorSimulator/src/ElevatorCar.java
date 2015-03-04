import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents an elevator car
 * @author Anton Jansson
 *
 */
public class ElevatorCar {
	private int id;
	
	private int floor;
	private Direction direction;

	private boolean traveling = false;
	private int destinationFloor;
	
	private final List<Passenger> passengers;
	
	private final ElevatorCarConfiguration configuration;
	
	private long lastMovement;
	private long stopTimeStart;
	private boolean stopTimeStarted;
	
	/**
	 * Creates a new elevator
	 * @param id The id of the elevator
	 * @param startFloor The start floor
	 * @param configuration The configuration
	 */
	public ElevatorCar(int id, int startFloor, ElevatorCarConfiguration configuration) {
		this.id = id;
		this.floor = startFloor;
		this.direction = Direction.NONE;
		this.passengers = new ArrayList<Passenger>();
		this.configuration = configuration;
	}

	/**
	 * Returns the id of the elevator
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Returns the floor
	 */
	public int getFloor() {
		return floor;
	}

	/**
	 * Returns the direction
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * Seths the direction of the elevator
	 * @param direction The direction
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	/**
	 * Indicates if the elevator car is traveling between floors
	 */
	public boolean isTraveling() {
		return traveling;
	}
	
	/**
	 * Returns the destination floor
	 */
	public int getDestinationFloor() {
		return destinationFloor;
	}

	/**
	 * Returns the passengers
	 */
	public List<Passenger> getPassengers() {
		return passengers;
	}
	
	/**
	 * Indicates if the stop time has started
	 */
	public boolean hasStopTimeStarted() {
		return this.stopTimeStarted;
	}
	
	/**
	 * Begins the stop time
	 * @param clock The clock
	 */
	public void beginStopTime(SimulatorClock clock) {
		this.stopTimeStart = clock.timeNow();
		this.stopTimeStarted = true;
	}
	
	/**
	 * Checks if the stop time the as passed
	 * @param clock The clock
	 */
	public boolean stopTimePassed(SimulatorClock clock) {
		long duration = clock.durationFromRealTime(clock.timeNow() - this.stopTimeStart);
		return duration >= clock.secondsToTime(this.configuration.getStopTime());
	}
	
	/**
	 * Ends the stop time
	 * @param clock The simulator clock
	 */
	public void endStopTime(SimulatorClock clock) {
		this.stopTimeStarted = false;
		this.lastMovement = clock.timeNow();
		this.traveling = true;
	}
	
	/**
	 * Updates the elevator car
	 * @param The simulator
	 */
	public void update(Simulator simulator) {
		if (this.direction != Direction.NONE && !this.stopTimeStarted) {
			long timeNow = simulator.getClock().timeNow();
			SimulatorClock clock = simulator.getClock();
			long duration = clock.durationFromRealTime(timeNow - this.lastMovement);
			
			if (duration >= clock.secondsToTime(this.configuration.getFloorTime())) {				
				if (this.direction == Direction.UP) {
					this.floor++;
				} else if (this.direction == Direction.DOWN) {
					this.floor--;
				}
				
				for (Passenger passenger : new LinkedList<Passenger>(this.passengers)) {
					if (this.floor == passenger.getDestinationFloor()) {
						simulator.elevatorLog(this.id, "Passenger #" + passenger.getId() + " exited at floor " + this.floor + ".");
						this.passengers.remove(passenger);
					}
				}
				
				//If no passengers, stop.
				if (this.passengers.size() == 0) {
					this.direction = Direction.NONE;
				}
				
				this.lastMovement = timeNow;
				this.traveling = false;
			}			
		}
	}
	
	/**
	 * Pickups a new passenger
	 * @param passenger The passenger to pickup
	 */
	public void pickUp(Passenger passenger) {
		this.passengers.add(passenger);
		
		if (this.direction == Direction.UP) {
			this.destinationFloor = Math.max(this.destinationFloor, passenger.getDestinationFloor());
		} else if (this.direction == Direction.DOWN) {
			this.destinationFloor = Math.min(this.destinationFloor, passenger.getDestinationFloor());
		}
	}
}
