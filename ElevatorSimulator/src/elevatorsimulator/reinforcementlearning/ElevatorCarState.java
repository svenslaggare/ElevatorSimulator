package elevatorsimulator.reinforcementlearning;

import java.util.ArrayList;
import java.util.List;

import marl.environments.State;
import elevatorsimulator.Building;
import elevatorsimulator.Direction;
import elevatorsimulator.ElevatorCar;
import elevatorsimulator.Passenger;

/**
 * Represents the state of an elevator car agent
 * @author Anton Jansson
 *
 */
public class ElevatorCarState implements State<ElevatorCarState> {	
	public static enum PassengerState {
		EMPTY,
		HAS_CAPACITY,
		FULL
	}
	
	private int floor;
	private Direction direction;
	private PassengerState passengerState;
	private int nextStop;
	private final List<ElevatorCarState.Floor> floors = new ArrayList<ElevatorCarState.Floor>();
	
	/**
	 * The state of a floor
	 * @author Anton Jansson
	 *
	 */
	public static class Floor {
		public static enum CallState {
			NONE,
			UP,
			DOWN,
			BOTH
		}
		
		private CallState state;
		
		/**
		 * Create a new floor state
		 * @param state The call state
		 */
		public Floor(CallState state) {
			this.state = state;
		}

		/**
		 * Returns the call state
		 */
		public CallState getState() {
			return state;
		}		
		
		/**
		 * Sets the state
		 * @param state The new state
		 */
		public void setState(CallState state) {
			this.state = state;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Floor)) {
				return false;
			}
			
			Floor floor = (Floor)obj;
			return this.state == floor.state;
		}
		
		@Override
		public int hashCode() {
			return this.state.hashCode();
		}
	}
	
	/**
	 * Creates the state from the given elevator
	 * @param building The building
	 * @param elevatorCar The elevator car
	 */
	public ElevatorCarState(Building building, ElevatorCar elevatorCar) {
		for (elevatorsimulator.Floor floor : building.getFloors()) {
			boolean hasUp = false;
			boolean hasDown = false;
			Floor.CallState state = Floor.CallState.NONE;
			
			for (Passenger passenger : floor.getWaitingQueue()) {
				if (passenger.getDirection() == Direction.UP) {
					hasUp = true;
				}
				
				if (passenger.getDirection() == Direction.DOWN) {
					hasUp = hasDown;
				}
			}
			
			if (hasUp && hasDown) {
				state = Floor.CallState.BOTH;
			} else if (hasUp) {
				state = Floor.CallState.UP;
			} else if (hasDown) {
				state = Floor.CallState.DOWN;
			}
			
			this.floors.add(new Floor(state));
		}
		
		PassengerState state = PassengerState.EMPTY;
		
		if (elevatorCar.isEmpty()) {
			state = PassengerState.EMPTY;
		} else if (elevatorCar.canPickupPassenger(1)) {
			state = PassengerState.HAS_CAPACITY;
		} else {
			state = PassengerState.FULL;
		}
		
		int nextStop = -1;
		int minDelta = Integer.MAX_VALUE;
		
		for (Passenger passenger : elevatorCar.getPassengers()) {
			int delta = Math.abs(passenger.getDestinationFloor() - elevatorCar.getFloor());
			
			if (delta < minDelta) {
				minDelta = delta;
				nextStop = passenger.getDestinationFloor();
			}
		}
		
		if (nextStop == -1) {
			nextStop = elevatorCar.getDestinationFloor();
		}
		
		this.passengerState = state;
		this.nextStop = nextStop;
		this.floor = elevatorCar.getFloor();
		this.direction = elevatorCar.getDirection();
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
	 * Returns the passenger state
	 */
	public PassengerState getState() {
		return passengerState;
	}	
	
	/**
	 * Returns the next stop
	 */
	public int getNextStop() {
		return nextStop;
	}
	
	/**
	 * Returns the floors
	 */
	public List<ElevatorCarState.Floor> getFloors() {
		return floors;
	}
	
	/**
	 * Updates the state of the elevator car
	 * @param building The building
	 * @param elevatorCar The elevator car
	 */
	public void updateState(Building building, ElevatorCar elevatorCar) {
		int i = 0;
		for (elevatorsimulator.Floor floor : building.getFloors()) {
			boolean hasUp = false;
			boolean hasDown = false;
			Floor.CallState state = Floor.CallState.NONE;
			
			for (Passenger passenger : floor.getWaitingQueue()) {
				if (passenger.getDirection() == Direction.UP) {
					hasUp = true;
				}
				
				if (passenger.getDirection() == Direction.DOWN) {
					hasUp = hasDown;
				}
			}
			
			if (hasUp && hasDown) {
				state = Floor.CallState.BOTH;
			} else if (hasUp) {
				state = Floor.CallState.UP;
			} else if (hasDown) {
				state = Floor.CallState.DOWN;
			}
			
			this.floors.get(i).setState(state);
			i++;
		}
		
		PassengerState state = PassengerState.EMPTY;
		
		if (elevatorCar.isEmpty()) {
			state = PassengerState.EMPTY;
		} else if (elevatorCar.canPickupPassenger(1)) {
			state = PassengerState.HAS_CAPACITY;
		} else {
			state = PassengerState.FULL;
		}
		
		int nextStop = -1;
		int minDelta = Integer.MAX_VALUE;
		
		for (Passenger passenger : elevatorCar.getPassengers()) {
			int delta = Math.abs(passenger.getDestinationFloor() - elevatorCar.getFloor());
			
			if (delta < minDelta) {
				minDelta = delta;
				nextStop = passenger.getDestinationFloor();
			}
		}
		
		if (nextStop == -1) {
			nextStop = elevatorCar.getDestinationFloor();
		}
		
		this.passengerState = state;
		this.nextStop = nextStop;
		this.floor = elevatorCar.getFloor();
		this.direction = elevatorCar.getDirection();
	}
	
	@Override
	public void set(ElevatorCarState state) {
		this.direction = state.direction;
		this.floor = state.floor;
		this.nextStop = state.nextStop;
		this.passengerState = state.passengerState;
		
		this.floors.clear();
		
		for (Floor floor : state.floors) {
			this.floors.add(floor);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result	+ ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + floor;
		result = prime * result + ((floors == null) ? 0 : floors.hashCode());
		result = prime * result + nextStop;
		result = prime * result + ((passengerState == null) ? 0 : passengerState.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ElevatorCarState)) {
			return false;
		}
		
		ElevatorCarState other = (ElevatorCarState)obj;
		
		if (this.floors.size() != other.floors.size()) {
			return false;
		}
		
		for (int i = 0; i < this.floors.size(); i++) {
			if (!this.floors.get(i).equals(other.floors.get(i))) {
				return false;
			}
		}
				
		return this.direction == other.direction
				&& this.floor == other.floor
				&& this.nextStop == other.nextStop
				&& this.passengerState == other.passengerState;
	}
}
