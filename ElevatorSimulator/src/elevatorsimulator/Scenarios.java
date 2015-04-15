package elevatorsimulator;

/**
 * Contains scenarios
 * @author Anton Jansson and Kristoffer Uggla Lingvall
 *
 */
public class Scenarios {	
	private Scenarios() {
		
	}
	
	/**
	 * Creates a medium-sized building
	 * @param numElevatorCars The number of elevator cars
	 */
	public static Scenario createMediumBuilding(int numElevatorCars) {
		return new Scenario(
			"MediumBuilding-" + numElevatorCars,
			numElevatorCars,
			ElevatorCarConfiguration.defaultConfiguration(),
			new int[] {
				0,
				65,
				80,
				75,
				85,
				90,
				90,
				75,
				80,
				70
			},
			TrafficProfiles.WEEK_DAY_PROFILE);
	}
	
	/**
	 * Creates a large-sized building
	 * @param numElevatorCars The number of elevator cars
	 */
	public static Scenario createLargeBuilding(int numElevatorCars) {
		return new Scenario(
			"LargeBuilding-" + numElevatorCars,
			numElevatorCars,
			ElevatorCarConfiguration.defaultConfiguration(),
			new int[] {
				0,
				70,
				70,
				75,
				85,
				75,
				80,
				90,
				90,
				85,
				75,
				80,
				75,
				90,
				70,
				70
			},
			TrafficProfiles.WEEK_DAY_PROFILE);
	}
}
