/**
 * Represents a building
 * @author Anton Jansson
 *
 */
public class Building {
	private final Floor[] floors;
	private final ElevatorCar[] elevatorCars;
	
	/**
	 * Creates a new building
	 * @param floors The floors
	 * @param numElevatorCars The number of elevator cars
	 * @param startFloor The start floor for the elevator cars
	 */
	public Building(Floor[] floors, int numElevatorCars, int startFloor) {
		this.floors = floors;
		
		this.elevatorCars = new ElevatorCar[numElevatorCars];
		for (int i = 0; i < this.elevatorCars.length; i++) {
			this.elevatorCars[i] = new ElevatorCar(startFloor);
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
	 * Updates the building
	 * @param simulator The simulator
	 * @param The elapsed time since the last time step
	 */
	public void update(Simulator simulator, long duration) {
		for (int i = 0; i < this.floors.length; i++) {
			this.floors[i].tryGenerateNewArrival(simulator, duration);
		}
		
		for (int i = 0; i < this.elevatorCars.length; i++) {
			this.elevatorCars[i].update(simulator);
		}
	}
}
