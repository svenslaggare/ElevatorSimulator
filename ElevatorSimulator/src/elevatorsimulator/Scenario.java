package elevatorsimulator;

/**
 * Represents a scenario
 * @author Anton Jansson
 *
 */
public class Scenario {
	private final int numElevatorCars;
	private final int[] floorResidents;
	private final TrafficProfile trafficProfile;
	
	private final ElevatorCarConfiguration elevatorCarConfiguration;
	
	/**
	 * Creates a new scenario
	 * @param numElevatorCars The number of elevator cars
	 * @param elevatorCarConfiguration The configuration for the elevator car
	 * @param floorResidents The residents on each floor
	 * @param trafficProfile The traffic profile
	 */
	public Scenario(int numElevatorCars, ElevatorCarConfiguration elevatorCarConfiguration, int[] floorResidents, TrafficProfile trafficProfile) {
		this.numElevatorCars = numElevatorCars;
		this.elevatorCarConfiguration = elevatorCarConfiguration;
		this.floorResidents = floorResidents;
		this.trafficProfile = trafficProfile;
	}
		
	/**
	 * Creates a new building
	 */
	public Building createBuilding() {
		Floor[] floors = new Floor[this.floorResidents.length];
		for (int i = 0; i < floors.length; i++) {
			int numResidents = this.floorResidents[i];
			floors[i] = new Floor(i, numResidents, this.trafficProfile);
		}
		
		return new Building(floors, this.numElevatorCars, 0, this.elevatorCarConfiguration);
	}
}
