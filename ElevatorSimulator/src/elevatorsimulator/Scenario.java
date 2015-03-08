package elevatorsimulator;
import java.util.List;

/**
 * Represents a scenario
 * @author Anton Jansson
 *
 */
public class Scenario {
	private final int numElevatorCars;
	private final List<FloorBuilder> floors;
	private final TrafficProfile trafficProfile;
	
	private final ElevatorCarConfiguration elevatorCarConfiguration;
	
	/**
	 * Creates a new scenario
	 * @param numElevatorCars The number of elevator cars
	 * @param elevatorCarConfiguration The configuration for the elevator car
	 * @param floors The floors
	 * @param trafficProfile The traffic profile
	 */
	public Scenario(int numElevatorCars, ElevatorCarConfiguration elevatorCarConfiguration, List<FloorBuilder> floors, TrafficProfile trafficProfile) {
		this.numElevatorCars = numElevatorCars;
		this.elevatorCarConfiguration = elevatorCarConfiguration;
		this.floors = floors;
		this.trafficProfile = trafficProfile;
	}
	
	/**
	 * Represents a builder for a floor
	 */
	public static class FloorBuilder {
		private int numResidents;
		
		/**
		 * Creates a new floor
		 * @param numResidents The number of residents
		 */
		public FloorBuilder(int numResidents) {
			this.numResidents = numResidents;
		}
		
		public int getNumResidents() {
			return numResidents;
		}
		
		public void setNumResidents(int numResidents) {
			this.numResidents = numResidents;
		}
	}
	
	/**
	 * Creates a new building
	 */
	public Building createBuilding() {
		Floor[] floors = new Floor[this.floors.size()];
		for (int i = 0; i < floors.length; i++) {
			FloorBuilder floor = this.floors.get(i);
			floors[i] = new Floor(i, floor.numResidents, this.trafficProfile);
		}
		
		return new Building(floors, this.numElevatorCars, 0, this.elevatorCarConfiguration);
	}
}
