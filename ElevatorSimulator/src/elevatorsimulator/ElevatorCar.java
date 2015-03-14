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
	private int destinationFloor;
	
	private final List<Passenger> passengers;
	
	private final ElevatorCarConfiguration configuration;
	
	/**
	 * The possible states the elevator can be in
	 * @author Anton Jansson
	 *
	 */
	public static enum State {
		IDLE,
		MOVING,
		DECELERATING,
		STOPPED,
		ACCELERATING,
		TURNING
	}
	
	private State state = State.IDLE;
	private boolean stopAtNext = false;
	private long lastMovement;			
	private long stopStartTime;
	private long startStartTime;
	private long intervalEnterStart;
	private long turnStartTime;
	private double boardWaitTime = 0.0;
	
	private long numPassengers;
	
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
	 * Returns the state of the elevator
	 */
	public State getState() {
		return this.state;
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
	 * Indicates if the elevator car is empty
	 */
	public boolean isEmpty() {
		return passengers.isEmpty();
	}
	
	/**
	 * Returns the number of passengers handled
	 */
	public long getNumPassengers() {
		return numPassengers;
	}

	/**
	 * Stops the elevator
	 * @param simulator The simulator
	 */
	public void stopElevator(Simulator simulator) {
		simulator.elevatorDebugLog(id, "Starts to slow down.");
		this.stopStartTime = simulator.getClock().timeNow();
		this.state = State.DECELERATING;
	}
	
	/**
	 * Stops the elevator at the next floor
	 */
	public void stopElevatorAtNextFloor() {
		this.stopAtNext = true;
	}
	
	/**
	 * Indicates if the elevator has stopped
	 * @param simulator The simulator
	 */
	private boolean hasStopped(Simulator simulator) {
		SimulatorClock clock = simulator.getClock();
		
		double stopTime = this.configuration.getStopTime() + this.configuration.getDoorTime();
		if (clock.elapsedSinceRealTime(this.stopStartTime) >= clock.secondsToTime(stopTime)) {
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
		simulator.elevatorDebugLog(this.id, "Starts accelerating the elevator.");
		this.startStartTime = simulator.getClock().timeNow();
		this.state = State.ACCELERATING;
	}
	
	/**
	 * Indicates if the elevator has started
	 * @param simulator The simulator
	 */
	private boolean hasStarted(Simulator simulator) {
		SimulatorClock clock = simulator.getClock();
		
		if (clock.elapsedSinceRealTime(this.startStartTime) >= clock.secondsToTime(this.configuration.getStartTime())) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Begins the door time
	 * @param simulator The simulator
	 */
	private void beginDoorTime(Simulator simulator) {
		simulator.elevatorDebugLog(this.id, "Starts closing doors.");
		this.intervalEnterStart = simulator.getClock().timeNow();
	}
	
	
	/**
	 * Changes the direction of the elevator
	 * @param simulator The simulator
	 */
	public void turnElevator(Simulator simulator) {
		if (this.direction != Direction.NONE) {
			this.state = State.TURNING;
			this.turnStartTime = simulator.getClock().timeNow();
			this.direction = this.direction.oppositeDir();
		}
	}
	
	/**
	 * Indicates if the elevator has turned
	 * @param simulator The simulator
	 */
	private boolean hasTurned(Simulator simulator) {
		SimulatorClock clock = simulator.getClock();
		
		double turnTime = this.configuration.getStopTime() + this.configuration.getStartTime();
		if (clock.elapsedSinceRealTime(this.turnStartTime) >= clock.secondsToTime(turnTime)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Updates the elevator car
	 * @param The simulator
	 */
	public void update(Simulator simulator) {
		long timeNow = simulator.getClock().timeNow();
		SimulatorClock clock = simulator.getClock();
		
		switch (this.state) {
		case MOVING:
			{
				//Check if the elevator has moved to the next floor
				long duration = clock.durationFromRealTime(timeNow - this.lastMovement);
				if (duration >= clock.secondsToTime(this.configuration.getFloorTime())) {		
					if (this.direction == Direction.UP) {
						this.floor++;
					} else if (this.direction == Direction.DOWN) {
						this.floor--;
					}
					
					this.lastMovement = timeNow;
					
					//Check if to stop
					if (this.stopAtNext) {
						this.stopElevator(simulator);
						this.stopAtNext = false;
					}
					
					//Check if any passenger wants to go off
					for (Passenger passenger : new LinkedList<Passenger>(this.passengers)) {
						if (this.floor == passenger.getDestinationFloor()) {
							this.stopElevator(simulator);
							return;
						}
					}
					
					//If the destination floor has been reached, stop.
					if (this.floor == this.destinationFloor) {
						this.stopElevator(simulator);
						simulator.elevatorDebugLog(id, "Terminated movement at floor " + this.floor + ".");
						this.direction = Direction.NONE;
					}
				}
			}
			break;
		case STOPPED:
			{
				//Let of passengers if the current floor is their destination
				boolean leaved = false;
				for (Passenger passenger : new LinkedList<Passenger>(this.passengers)) {
					if (this.floor == passenger.getDestinationFloor()) {
						simulator.elevatorLog(this.id, "Passenger #" + passenger.getId() + " exited at floor " + this.floor + ".");
						simulator.log("Passenger #" + passenger.getId() + " stats: " + passenger.getStats(simulator.getClock()) + ".");
						simulator.getStats().passengerExited(passenger);
						this.passengers.remove(passenger);
						leaved = true;
					}
				}
				
				if (this.passengers.size() > 0) {
					if (leaved) {
						this.beginDoorTime(simulator);
					}
				} else {
					//If there are no more passengers, the elevator is idle and waits on the floor.
					this.direction = Direction.NONE;
					this.state = State.IDLE;
					simulator.getControlSystem().elevatorIdle(this);
					return;
				}
				
				//Check if the doors has closed and the elevator starts moving again
				long duration = timeNow - this.intervalEnterStart;
				if (clock.durationFromRealTime(duration) >= clock.secondsToTime(this.configuration.getDoorTime())) {
					this.startElevator(simulator);
				}
			}
			break;
		case ACCELERATING:
			{
				if (this.hasStarted(simulator)) {
					this.state = State.MOVING;
					this.lastMovement = timeNow;
					simulator.elevatorDebugLog(this.id, "Has started.");
				}
			}
			break;
		case DECELERATING:
			{
				if (this.hasStopped(simulator)) {
					this.state = State.STOPPED;
					simulator.elevatorDebugLog(this.id, "Has stopped at floor " + this.floor + ".");
				}
			}
			break;
		case TURNING:
			{
				if (this.hasTurned(simulator)) {
					this.state = State.MOVING;
				}
			}
			break;
			default:
				break;
		}
	}
	
	/**
	 * Indicates if the elevator can pickup one passenger
	 */
	public boolean canPickupPassenger() {
		return this.canPickupPassenger(1);
	}

	/**
	 * Indicates if the elevator can pickup the given passenger
	 * @param capacity The passenger capacity
	 */
	public boolean canPickupPassenger(int capacity) {
		return this.passengers.size() + capacity <= this.configuration.getCapacity();
	}
	
	/**
	 * Indicates if the elevator can pickup the given passenger
	 * @param passenger The passenger
	 */
	public boolean canPickupPassenger(Passenger passenger) {
		return this.canPickupPassenger(passenger.getCapacity());
	}
	
	/**
	 * Indicates if a passenger can board the elevator
	 * @param simulator The simulator
	 */
	public boolean canBoard(Simulator simulator) {
		return simulator.getClock().elapsedSinceRealTime(this.intervalEnterStart) >= simulator.getClock().secondsToTime(this.boardWaitTime);
	}
	
	/**
	 * Pickups a new passenger
	 * @param simulator The simulator
	 * @param passenger The passenger to pickup
	 */
	public void pickUp(Simulator simulator, Passenger passenger) {
		if (this.state == State.IDLE) {
			this.state = State.STOPPED;
		}
		
		passenger.rideStarted(simulator.getClock());
		this.passengers.add(passenger);
		this.beginDoorTime(simulator);
		this.boardWaitTime = 1.0;
		this.numPassengers++;
		
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
		if (this.floor != targetFloor) {
			Direction dir = Direction.getDirection(this.floor, targetFloor);
			
			if (this.state != State.MOVING) {
				this.destinationFloor = targetFloor;
				this.direction = dir;
				this.startElevator(simulator);
			} else {
				if (this.direction == dir) {
					this.destinationFloor = targetFloor;
				} else {
					this.destinationFloor = targetFloor;
					this.turnElevator(simulator);
				}
			}
		}
	}
	
	/**
	 * Resets the elevator car
	 */
	public void reset() {
		this.state = State.IDLE;
		this.stopAtNext = false;
		this.direction = Direction.NONE;
		this.passengers.clear();
		this.numPassengers = 0;
		this.lastMovement = 0;
		this.floor = 0;
		this.intervalEnterStart = 0;
	}
}
