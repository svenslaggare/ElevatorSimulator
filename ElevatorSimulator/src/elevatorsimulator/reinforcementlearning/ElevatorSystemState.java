package elevatorsimulator.reinforcementlearning;

//import java.util.ArrayList;
//import java.util.List;
//
//import marl.environments.State;
//import elevatorsimulator.Building;
//import elevatorsimulator.Direction;
//import elevatorsimulator.Passenger;

/**
 * Represents the state of the agent
 * @author Anton Jansson
 *
 */
//public class ElevatorSystemState implements State<ElevatorSystemState> {
//	private final List<ElevatorSystemState.Floor> floors = new ArrayList<ElevatorSystemState.Floor>();
//	private final List<ElevatorSystemState.ElevatorCar> elevatorCars = new ArrayList<ElevatorSystemState.ElevatorCar>();
//	
//	/**
//	 * The state of a floor
//	 * @author Anton Jansson
//	 *
//	 */
//	public static class Floor {
//		public static enum CallState {
//			NONE,
//			UP,
//			DOWN,
//			BOTH
//		}
//		
//		private final CallState state;
//		
//		/**
//		 * Create a new floor state
//		 * @param state The call state
//		 */
//		public Floor(CallState state) {
//			this.state = state;
//		}
//
//		/**
//		 * Returns the call state
//		 */
//		public CallState getState() {
//			return state;
//		}		
//		
//		@Override
//		public boolean equals(Object obj) {
//			if (!(obj instanceof Floor)) {
//				return false;
//			}
//			
//			Floor floor = (Floor)obj;
//			return this.state == floor.state;
//		}
//		
//		@Override
//		public int hashCode() {
//			return this.state.hashCode();
//		}
//	}
//	
//	/**
//	 * The state of an elevator car
//	 * @author Anton Jansson
//	 *
//	 */
//	public static class ElevatorCar {
//		public static enum PassengerState {
//			EMPTY,
//			HAS_CAPACITY,
//			FULL
//		}
//		
//		private final int floor;
//		private final Direction direction;
//		private final PassengerState state;
//		private final int nextStop;
//		
//		/**
//		 * Creates a new elevator car state
//		 * @param floor The floor of the elevator
//		 * @param direction The direction
//		 * @param state The passenger state
//		 * @param nextStop The floor of the next stop
//		 */
//		public ElevatorCar(int floor, Direction direction, PassengerState state, int nextStop) {
//			this.floor = floor;
//			this.direction = direction;
//			this.state = state;
//			this.nextStop = nextStop;
//		}
//
//		/**
//		 * Returns the floor
//		 */
//		public int getFloor() {
//			return floor;
//		}
//
//		/**
//		 * Returns the direction
//		 */
//		public Direction getDirection() {
//			return direction;
//		}
//
//		/**
//		 * Returns the passenger state
//		 */
//		public PassengerState getState() {
//			return state;
//		}	
//		
//		/**
//		 * Returns the next stop
//		 */
//		public int getNextStop() {
//			return nextStop;
//		}
//		
//		@Override
//		public boolean equals(Object obj) {
//			if (!(obj instanceof ElevatorCar)) {
//				return false;
//			}
//			
//			ElevatorCar elevator = (ElevatorCar)obj;
//			
//			return this.floor == elevator.floor
//					&& this.direction == elevator.direction
//					&& this.state == elevator.state
//					&& this.nextStop == elevator.nextStop;
//		}
//		
//		@Override
//		public int hashCode() {
//			final int prime = 31;
//			int result = 1;
//			result = prime * result	+ ((direction == null) ? 0 : direction.hashCode());
//			result = prime * result + floor;
//			result = prime * result + nextStop;
//			result = prime * result + ((state == null) ? 0 : state.hashCode());
//			return result;
//		}
//	}
//
//	/**
//	 * Creates the state from the given building
//	 * @param building The building
//	 */
//	public ElevatorSystemState(Building building) {
//		for (elevatorsimulator.Floor floor : building.getFloors()) {
//			boolean hasUp = false;
//			boolean hasDown = false;
//			Floor.CallState state = Floor.CallState.NONE;
//			
//			for (Passenger passenger : floor.getWaitingQueue()) {
//				if (passenger.getDirection() == Direction.UP) {
//					hasUp = true;
//				}
//				
//				if (passenger.getDirection() == Direction.DOWN) {
//					hasUp = hasDown;
//				}
//			}
//			
//			if (hasUp && hasDown) {
//				state = Floor.CallState.BOTH;
//			} else if (hasUp) {
//				state = Floor.CallState.UP;
//			} else if (hasDown) {
//				state = Floor.CallState.DOWN;
//			}
//			
//			this.floors.add(new Floor(state));
//		}
//		
//		for (elevatorsimulator.ElevatorCar elevator : building.getElevatorCars()) {
//			ElevatorCar.PassengerState state = ElevatorCar.PassengerState.EMPTY;
//			
//			if (elevator.isEmpty()) {
//				state = ElevatorCar.PassengerState.EMPTY;
//			} else if (elevator.canPickupPassenger(1)) {
//				state = ElevatorCar.PassengerState.HAS_CAPACITY;
//			} else {
//				state = ElevatorCar.PassengerState.FULL;
//			}
//			
//			int nextStop = -1;
//			int minDelta = Integer.MAX_VALUE;
//			
//			for (Passenger passenger : elevator.getPassengers()) {
//				int delta = Math.abs(passenger.getDestinationFloor() - elevator.getFloor());
//				
//				if (delta < minDelta) {
//					minDelta = delta;
//					nextStop = passenger.getDestinationFloor();
//				}
//			}
//			
//			if (nextStop == -1) {
//				nextStop = elevator.getDestinationFloor();
//			}
//			
//			this.elevatorCars.add(new ElevatorCar(elevator.getFloor(), elevator.getDirection(), state, nextStop));
//		}
//	}
//
//	/**
//	 * Returns the floors
//	 */
//	public List<ElevatorSystemState.Floor> getFloors() {
//		return floors;
//	}
//	
//	/**
//	 * Returns the elevator cars
//	 */
//	public List<ElevatorSystemState.ElevatorCar> getElevatorCars() {
//		return elevatorCars;
//	}
//	
//	@Override
//	public void set(ElevatorSystemState state) {
//		this.elevatorCars.clear();
//		this.floors.clear();
//		
//		for (Floor floor : state.floors) {
//			this.floors.add(floor);
//		}
//		
//		for (ElevatorCar elevator : state.elevatorCars) {
//			this.elevatorCars.add(elevator);
//		}
//	}
//	
//	@Override
//	public boolean equals(Object obj) {
//		if (!(obj instanceof ElevatorSystemState)) {
//			return false;
//		}
//		
//		ElevatorSystemState other = (ElevatorSystemState)obj;
//		
//		if (this.floors.size() != other.floors.size()) {
//			return false;
//		}
//		
//		if (this.elevatorCars.size() != other.elevatorCars.size()) {
//			return false;
//		}
//		
//		for (int i = 0; i < this.floors.size(); i++) {
//			if (!this.floors.get(i).equals(other.floors.get(i))) {
//				return false;
//			}
//		}
//		
//		for (int i = 0; i < this.elevatorCars.size(); i++) {
//			if (!this.elevatorCars.get(i).equals(other.elevatorCars.get(i))) {
//				return false;
//			}
//		}
//		
//		return true;
//	}
//	
//	@Override
//	public int hashCode() {
//		int floorHashCode = 0;
//		
//		int i = 1;
//		for (Floor floor : this.floors) {
//			floorHashCode *= floor.hashCode() * i;
//			i++;
//		}
//		
//		int elevatorHashCode = 0;
//		for (ElevatorCar elevatorCar : elevatorCars) {
//			elevatorHashCode *= elevatorCar.hashCode();
//		}
//		
//		return floorHashCode ^ elevatorHashCode;
//	}
//}
