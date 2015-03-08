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
	private final Random random = new Random();
	private final SimulatorStats stats;
	
	private final Building building;
	private final ControlSystem controlSystem;
	
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
		this.controlSystem = new ControlSystem(this.building);
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
		this.controlSystem.update(this, duration);
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
	 * Runs the simulation
	 */
	public void run() {
		long simulationTime = this.settings.getSimulationTimeInSec() * 1000;
		long startTime = System.currentTimeMillis();				
		long prevStep = clock.timeNow();
		
		System.out.println(new Date() + ": Simulation started.");
		
		while ((System.currentTimeMillis() - startTime) < simulationTime) {
			long timeNowNano = System.nanoTime();
			moveForward(this.clock.durationFromRealTime(timeNowNano - prevStep));
			prevStep = timeNowNano;
		}	
		
		System.out.println(new Date() + ": Simulation finished.");		
		System.out.println("--------------------STATS--------------------");
		this.stats.printStats();		
	}
	
	public static void main(String[] args) {
		List<Scenario.FloorBuilder> floors = new ArrayList<Scenario.FloorBuilder>();
		floors.add(new Scenario.FloorBuilder(0));
		floors.add(new Scenario.FloorBuilder(50));
		floors.add(new Scenario.FloorBuilder(30));
		floors.add(new Scenario.FloorBuilder(60));
				
		TrafficProfile.Interval[] arrivalRates = new TrafficProfile.Interval[2];
		arrivalRates[0] = new TrafficProfile.Interval(10.0, 0.9, 0.1);
		arrivalRates[1] = new TrafficProfile.Interval(10.0, 0.2, 0.7);
		
		Simulator simulator = new Simulator(
			new Scenario(
				1,
				new ElevatorCarConfiguration(8, 1.5, 2.6, 2.6, 1),
				floors,
				new TrafficProfile(arrivalRates)),
			new SimulatorSettings(100, 60));
		
		simulator.run();
	}
	
	/**
	 * Returns the next passenger id
	 */
	public long nextPassengerId() {
		return this.passengerId++;
	}
}
