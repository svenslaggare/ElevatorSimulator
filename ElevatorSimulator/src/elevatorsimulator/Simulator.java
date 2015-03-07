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
	
	private final int[] generatedArrivals;
	
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
		this.stats = new SimulatorStats(this.clock);
		this.building = scenario.createBuilding();
		this.controlSystem = new ControlSystem(this.building);
		this.generatedArrivals = new int[this.building.getFloors().length];
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
	 * Marks that an arrival has been generated at the given floor
	 * @param floor The floor
	 */
	public void arrivalGenerated(int floor) {
		this.stats.generatedPassenger();
		this.generatedArrivals[floor]++;
	}
	
	/**
	 * Returns the total number of generated arrivals
	 */
	private long totalNumArrivalsGenerated() {
		long total = 0;
		
		for (int i = 0; i < this.generatedArrivals.length; i++) {
			total += this.generatedArrivals[i];
		}
		
		return total;
	}
	
	/**
	 * Prints statistics about the simulator
	 */
	public void printStats() {
		System.out.println("Total: " + totalNumArrivalsGenerated());
		
		for (int i = 0; i < this.generatedArrivals.length; i++) {
			System.out.println(i + ": " + this.generatedArrivals[i]);
		}
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
		floors.add(new Scenario.FloorBuilder(300, 0.5));
		floors.add(new Scenario.FloorBuilder(50, 2));
		floors.add(new Scenario.FloorBuilder(30, 1));
		floors.add(new Scenario.FloorBuilder(60, 1.5));
//			
//		floors.add(new Scenario.FloorBuilder(300, 4));
//		floors.add(new Scenario.FloorBuilder(50, 3));
//		floors.add(new Scenario.FloorBuilder(30, 2));
//		floors.add(new Scenario.FloorBuilder(60, 1));
				
		Simulator simulator = new Simulator(
			new Scenario(1, new ElevatorCarConfiguration(8, 1.5, 2.6, 2.6, 1), floors),
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
