import java.util.List;

/**
 * Represents a scenario
 * @author Anton Jansson
 *
 */
public class Scenario {
	private final int numElevatorCars;
	private final List<FloorBuilder> floors;
	
	private final ElevatorCarConfiguration elevatorCarConfiguration;
	
	/**
	 * Creates a new scenario
	 * @param numElevatorCars The number of elevator cars
	 * @param floors The floors
	 */
	public Scenario(int numElevatorCars, ElevatorCarConfiguration elevatorCarConfiguration, List<FloorBuilder> floors) {
		this.numElevatorCars = numElevatorCars;
		this.elevatorCarConfiguration = elevatorCarConfiguration;
		this.floors = floors;
	}
	
	/**
	 * Represents a builder for a floor
	 */
	public static class FloorBuilder {
		private int numResidents;
		private double averageArrivalRate;
		
		/**
		 * Creates a new floor
		 * @param numResidents The number of residents
		 * @param averageArrivalRate The average arrival rate
		 */
		public FloorBuilder(int numResidents, double averageArrivalRate) {
			this.numResidents = numResidents;
			this.averageArrivalRate = averageArrivalRate;
		}
		
		public int getNumResidents() {
			return numResidents;
		}
		
		public void setNumResidents(int numResidents) {
			this.numResidents = numResidents;
		}
		
		public double getAverageArrivalRate() {
			return averageArrivalRate;
		}
		
		public void setAverageArrivalRate(double averageArrivalRate) {
			this.averageArrivalRate = averageArrivalRate;
		}
	}
	
	/**
	 * Creates a new building
	 */
	public Building createBuilding() {
		Floor[] floors = new Floor[this.floors.size()];
		
		for (int i = 0; i < floors.length; i++) {
			FloorBuilder floor = this.floors.get(i);
			floors[i] = new Floor(i, floor.numResidents, floor.averageArrivalRate);
		}
		
		return new Building(floors, this.numElevatorCars, 0, this.elevatorCarConfiguration);
	}
}
