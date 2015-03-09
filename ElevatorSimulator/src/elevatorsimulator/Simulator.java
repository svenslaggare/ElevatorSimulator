package elevatorsimulator;
import java.util.*;

/**
 * The main class for the simulator
 * @author Anton Jansson
 *
 */
public class Simulator {
	private final SimulatorSettings settings;
	private final SimulatorClock clock;
	private final Random random = new Random(1337);
	private final SimulatorStats stats;
	
	private final Building building;
	private final ControlSystem controlSystem;
	
	private long simulationStartTime = System.currentTimeMillis();	
	
	private long passengerId = 0;
	
	private final boolean enableLog = false;
	private final boolean debugMode = true;
	
	/**
	 * Creates a new simulator
	 * @param scenario The scenario
	 */
	public Simulator(Scenario scenario, SimulatorSettings settings) {
		this.settings = settings;
		this.clock = new SimulatorClock(settings.getSimulationSpeed());
		this.building = scenario.createBuilding();
		this.controlSystem = new ControlSystem(this);
		this.stats = new SimulatorStats(this);
	}
	
	/**
	 * Returns the simulator clock
	 */
	public SimulatorClock getClock() {
		return clock;
	}
	
	/**
	 * Returns the simulator statistics
	 */
	public SimulatorStats getStats() {
		return stats;
	}
	
	/**
	 * Returns the random generator
	 */
	public Random getRandom() {
		return random;
	}
	
	/**
	 * Returns the building
	 */
	public Building getBuilding() {
		return building;
	}
	
	/**
	 * Returns the control system
	 */
	public ControlSystem getControlSystem() {
		return controlSystem;
	}
	
	/**
	 * Moves the simulation forward one time step
	 * @param duration The elapsed time since the last time step
	 */
	public void moveForward(long duration) {
		this.building.update(this, duration);
		this.controlSystem.update(duration);
	}
	
	/**
	 * Logs the given line
	 * @param line The line
	 */
	public void log(String line) {
		if (enableLog) {
			System.out.println(new Date().toString() + ": " + line);
		}
	}
	
	/**
	 * Logs the given line for an elevator
	 * @param elevatorId The id of the elevator
	 * @param line The line
	 */
	public void elevatorLog(int elevatorId, String line) {
		this.log("Elevator " + elevatorId + ": " + line);
	}
	
	/**
	 * Logs the given debug line for an elevator
	 * @param elevatorId The id of the elevator
	 * @param line The line
	 */
	public void elevatorDebugLog(int elevatorId, String line) {
		if (this.debugMode) {
			this.elevatorLog(elevatorId, line);
		}
	}
	
	/**
	 * Marks that an arrival has been generated
	 * @param passenger The passenger
	 */
	public void arrivalGenerated(Passenger passenger) {
		this.stats.generatedPassenger(passenger);
	}
	
	/**
	 * Indicates if new arrivals can be generated
	 */
	public boolean canGenerateArrivals() {
		return (System.currentTimeMillis() - this.simulationStartTime) < this.settings.getSimulationTimeInSec() * 1000;
	}
			
	/**
	 * Indicates if all floors are empty
	 */
	private boolean floorsEmpty() {
		for (Floor floor : this.building.getFloors()) {
			if (!floor.getWaitingQueue().isEmpty()) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Indicates if all the elevator cars are empty
	 */
	private boolean elevatorsEmpty() {
		for (ElevatorCar elevator : this.building.getElevatorCars()) {
			if (!elevator.getPassengers().isEmpty()) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Runs the simulation
	 */
	public void run() {
		long prevStep = clock.timeNow();
		
		System.out.println(new Date() + ": Simulation started.");
		
		while (true) {
			long timeNowNano = System.nanoTime();
			moveForward(this.clock.durationFromRealTime(timeNowNano - prevStep));
			prevStep = timeNowNano;
			
			if (!this.canGenerateArrivals()) {
				if (this.floorsEmpty() && this.elevatorsEmpty()) {
					break;
				}
			}
		}	
		
		System.out.println(new Date() + ": Simulation finished.");		
		System.out.println("--------------------" + this.controlSystem.getSchedulerName() + "--------------------");
		this.stats.printStats();		
	}
	
	public static void main(String[] args) {
		List<Scenario.FloorBuilder> floors = new ArrayList<Scenario.FloorBuilder>();
		floors.add(new Scenario.FloorBuilder(0));
		floors.add(new Scenario.FloorBuilder(80));
		floors.add(new Scenario.FloorBuilder(70));
		floors.add(new Scenario.FloorBuilder(90));		
		floors.add(new Scenario.FloorBuilder(80));
		floors.add(new Scenario.FloorBuilder(115));
		floors.add(new Scenario.FloorBuilder(120));
		floors.add(new Scenario.FloorBuilder(90));
		floors.add(new Scenario.FloorBuilder(80));
		floors.add(new Scenario.FloorBuilder(50));	
		
//		TrafficProfile.Interval[] arrivalRates = new TrafficProfile.Interval[2];
//		arrivalRates[0] = new TrafficProfile.Interval(20.0, 0.9, 0.1);
//		arrivalRates[1] = new TrafficProfile.Interval(20.0, 0.2, 0.7);
		
		TrafficProfile.Interval[] arrivalRates = new TrafficProfile.Interval[1];
		//arrivalRates[0] = new TrafficProfile.Interval(0.12, 1.0, 0.0);
		//arrivalRates[0] = new TrafficProfile.Interval(0.03, 0.45, 0.45);
		//arrivalRates[0] = new TrafficProfile.Interval(0.03, 0.1, 0.9);
		arrivalRates[0] = new TrafficProfile.Interval(0.06, 0.45, 0.45);
		
		Simulator simulator = new Simulator(
			new Scenario(
				3,
				new ElevatorCarConfiguration(8, 1.5, 2.6, 2.6, 1),
				floors,
				new TrafficProfile(arrivalRates)),
			new SimulatorSettings(100, 30));
		
		simulator.run();
	}
	
	/**
	 * Returns the next passenger id
	 */
	public long nextPassengerId() {
		return this.passengerId++;
	}
}
