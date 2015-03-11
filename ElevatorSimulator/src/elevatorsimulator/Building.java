package elevatorsimulator;
/**
 * Represents a building
 * @author Anton Jansson
 *
 */
public class Building {
	private final Floor[] floors;
	private final ElevatorCar[] elevatorCars;
	
	/**
	 * The lobby floor
	 */
	public static final int LOBBY = 0;
	
	/**
	 * Creates a new building
	 * @param floors The floors
	 * @param numElevatorCars The number of elevator cars
	 * @param startFloor The start floor for the elevator cars
	 * @param elevatorCarConfiguration The configuration for the elevator cars
	 */
	public Building(Floor[] floors, int numElevatorCars, int startFloor, ElevatorCarConfiguration elevatorCarConfiguration) {
		if (floors.length < 2) {
			throw new IllegalArgumentException("The number of floors in the building must be >= 2.");
		}
		
		this.floors = floors;
		
		this.elevatorCars = new ElevatorCar[numElevatorCars];
		for (int i = 0; i < this.elevatorCars.length; i++) {
			this.elevatorCars[i] = new ElevatorCar(i, startFloor, elevatorCarConfiguration);
		}
	}
	
	/**
	 * Returns the number of floors in the building
	 */
	public int numFloors() {
		return floors.length;
	}
	
	/**
	 * Returns the floors
	 * @return
	 */
	public Floor[] getFloors() {
		return floors;
	}

	/**
	 * Returns the elevator cars
	 */
	public ElevatorCar[] getElevatorCars() {
		return elevatorCars;
	}
	
	/**
	 * Returns the total number of residents
	 */
	public int getTotalNumberOfResidents() {
		int total = 0;
		
		for (Floor floor : this.floors) {
			total += floor.getNumResidents();
		}
		
		return total;
	}
	
	/**
	 * Updates the building
	 * @param simulator The simulator
	 * @param The elapsed time since the last time step
	 */
	public void update(Simulator simulator, long duration) {
		for (int i = 0; i < this.floors.length; i++) {
			this.floors[i].update(simulator, duration);
		}
		
		for (int i = 0; i < this.elevatorCars.length; i++) {
			this.elevatorCars[i].update(simulator);
		}
	}
	
	/**
	 * Resets the building
	 */
	public void reset() {
		for (Floor floor : this.floors) {
			floor.reset();
		}
		
		for (ElevatorCar elevator : this.elevatorCars) {
			elevator.reset();
		}
	}
}
