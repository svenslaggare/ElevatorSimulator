package elevatorsimulator;
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
		
	private boolean hasStopped;
	
	private boolean beginStop;
	private long stopStartTime;
	
	private boolean beginStart;
	private long startStartTime;
	
	private long intervalEnterStart;
	
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
	 * Sets the direction of the elevator
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
	 * Indicates if the elevator has stopped
	 */
	public boolean hasStopped() {
		return this.hasStopped;
	}
	
	/**
	 * Stops the elevator
	 * @param simulator The simulator
	 */
	public void stopElevator(Simulator simulator) {
		simulator.elevatorDebugLog(id, "Start stop.");
		this.stopStartTime = simulator.getClock().timeNow();
		this.beginStop = true;
	}
	
	/**
	 * Indicates if the elevator has stopped
	 * @param simulator The simulator
	 */
	public boolean hasStopped(Simulator simulator) {
		SimulatorClock clock = simulator.getClock();
		
		double stopTime = this.configuration.getStopTime() + this.configuration.getDoorTime();
		if (clock.elapsedSinceRealTime(this.stopStartTime) >= clock.secondsToTime(stopTime)) {
			this.beginStop = false;
			this.hasStopped = true;
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Starts the elevator
	 * @param simulator The simulator
	 */
	public void startElevator(Simulator simulator) {
		this.hasStopped = false;
		this.beginStart = true;
		this.startStartTime = simulator.getClock().timeNow();
	}
	
	/**
	 * Updates the elevator car
	 * @param The simulator
	 */
	public void update(Simulator simulator) {
		long timeNow = simulator.getClock().timeNow();
		SimulatorClock clock = simulator.getClock();
		
		if (this.direction != Direction.NONE) {
			long duration = clock.durationFromRealTime(timeNow - this.lastMovement);
			
			if (duration >= clock.secondsToTime(this.configuration.getFloorTime())) {				
				if (this.direction == Direction.UP) {
					this.floor++;
				} else if (this.direction == Direction.DOWN) {
					this.floor--;
				}
								
				//Check if any passenger wants to go off
				for (Passenger passenger : new LinkedList<Passenger>(this.passengers)) {
					if (this.floor == passenger.getDestinationFloor()) {
						simulator.elevatorLog(this.id, "Passenger #" + passenger.getId() + " exited at floor " + this.floor + ".");
						simulator.log("Passenger #" + passenger.getId() + " stats: " + passenger.getStats(simulator.getClock()));
						simulator.getStats().passengerExited(passenger);
						this.passengers.remove(passenger);
					}
				}
				
				if (this.passengers.size() == 0) {
					//If the destination floor has been reached, stop.
					if (this.floor == this.destinationFloor) {
						simulator.elevatorDebugLog(id, "Terminated movement at floor " + this.floor + ".");
						this.direction = Direction.NONE;
					}
				}
								
				this.lastMovement = timeNow;
				this.traveling = false;
			}		
			
			//Check if the elevator has stopped
			if (this.beginStop && this.hasStopped(simulator)) {
				this.intervalEnterStart = timeNow;
			}
		}
	}
	
	/**
	 * Indicates if the elevator can pickup the given passenger
	 * @param passenger The passenger
	 */
	public boolean canPickupPassenger(Passenger passenger) {
		return this.passengers.size() + passenger.getCapacity() <= this.configuration.getCapacity();
	}
	
	/**
	 * Pickups a new passenger
	 * @param clock The simulator clock
	 * @param passenger The passenger to pickup
	 */
	public void pickUp(SimulatorClock clock, Passenger passenger) {
		passenger.rideStarted(clock);
		this.passengers.add(passenger);
		
		if (this.direction == Direction.UP) {
			this.destinationFloor = Math.max(this.destinationFloor, passenger.getDestinationFloor());
		} else if (this.direction == Direction.DOWN) {
			this.destinationFloor = Math.min(this.destinationFloor, passenger.getDestinationFloor());
		}
	}
	
	/**
	 * Moves towards the given floor
	 * @param simulator The simulator
	 * @param targetFloor The floor to move towards
	 */
	public void moveTowards(Simulator simulator, int targetFloor) {
		this.direction = Direction.getDirection(this.floor, targetFloor);
		this.destinationFloor = targetFloor;
	}
}
