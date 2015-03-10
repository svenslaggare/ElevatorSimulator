package elevatorsimulator.reinforcementlearning;

import java.util.ArrayList;
import java.util.List;

import elevatorsimulator.Building;
import elevatorsimulator.Direction;
import elevatorsimulator.Passenger;
import elevatorsimulator.reinforcementlearning.State.ElevatorCar.PassengerState;
import elevatorsimulator.reinforcementlearning.State.Floor.CallState;

/**
 * Represents the state of the agent
 * @author Anton Jansson
 *
 */
public class State {
	private final List<State.Floor> floors = new ArrayList<State.Floor>();
	private final List<State.ElevatorCar> elevatorCars = new ArrayList<State.ElevatorCar>();
	
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
		
		private final CallState state;
		
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
	}
	
	/**
	 * The state of an elevator car
	 * @author Anton Jansson
	 *
	 */
	public static class ElevatorCar {
		public static enum PassengerState {
			EMPTY,
			HAS_CAPACITY,
			FULL
		}
		
		private final int floor;
		private final Direction direction;
		private final PassengerState state;
		
		/**
		 * Creates a new elevator car state
		 * @param floor The floor of the elevator
		 * @param direction The direction
		 * @param state The passenger state
		 */
		public ElevatorCar(int floor, Direction direction, PassengerState state) {
			this.floor = floor;
			this.direction = direction;
			this.state = state;
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
			return state;
		}	
	}

	/**
	 * Creates the state from the given building
	 * @param building The building
	 */
	public State(Building building) {
		for (elevatorsimulator.Floor floor : building.getFloors()) {
			boolean hasUp = false;
			boolean hasDown = false;
			Floor.CallState state = CallState.NONE;
			
			for (Passenger passenger : floor.getWaitingQueue()) {
				if (passenger.getDirection() == Direction.UP) {
					hasUp = true;
				}
				
				if (passenger.getDirection() == Direction.DOWN) {
					hasUp = hasDown;
				}
			}
			
			if (hasUp && hasDown) {
				state = CallState.BOTH;
			} else if (hasUp) {
				state = CallState.UP;
			} else if (hasDown) {
				state = CallState.DOWN;
			}
			
			this.floors.add(new Floor(state));
		}
		
		for (elevatorsimulator.ElevatorCar elevator : building.getElevatorCars()) {
			ElevatorCar.PassengerState state = PassengerState.EMPTY;
			
			if (elevator.isEmpty()) {
				state = PassengerState.EMPTY;
			} else if (elevator.canPickupPassenger(1)) {
				state = PassengerState.HAS_CAPACITY;
			} else {
				state = PassengerState.FULL;
			}
			
			this.elevatorCars.add(new ElevatorCar(elevator.getFloor(), elevator.getDirection(), state));
		}
	}

	/**
	 * Returns the floors
	 */
	public List<State.Floor> getFloors() {
		return floors;
	}
	
	/**
	 * Returns the elevator cars
	 */
	public List<State.ElevatorCar> getElevatorCars() {
		return elevatorCars;
	}
}
