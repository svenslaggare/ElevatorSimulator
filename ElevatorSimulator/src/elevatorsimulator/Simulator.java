package elevatorsimulator;
import java.util.*;


/**
 * The main class for the simulator
 * @author Anton Jansson
 *
 */
public class Simulator {
	private final SimulatorClock clock = new SimulatorClock();
	private final Random random = new Random();
	private final SimulatorStats stats;
	
	private final Building building;
	private final ControlSystem controlSystem;
	
	private final int[] generatedArrivals;
	
	private long passengerId = 0;
	
	private final boolean debugMode = false;
	
	/**
	 * Creates a new simulator
	 * @param scenario The scenario
	 */
	public Simulator(Scenario scenario) {		
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
		//System.out.println(new Date().toString() + ": " + line);
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
	
	public static void main(String[] args) {
		long simulationTime = 30 * 1000;
		long startTime = System.currentTimeMillis();
		
		List<Scenario.FloorBuilder> floors = new ArrayList<Scenario.FloorBuilder>();
		floors.add(new Scenario.FloorBuilder(300, 2));
		floors.add(new Scenario.FloorBuilder(50, 2*10000));
		floors.add(new Scenario.FloorBuilder(30, 3*10000));
		floors.add(new Scenario.FloorBuilder(60, 2*10000));
		
		Simulator simulator = new Simulator(new Scenario(3, new ElevatorCarConfiguration(8, 1.5, 2.6, 2.6, 1), floors));
		long prevStep = System.nanoTime();
		
		while ((System.currentTimeMillis() - startTime) < simulationTime) {
			long timeNowNano = System.nanoTime();
			simulator.moveForward(simulator.getClock().durationFromRealTime(timeNowNano - prevStep));
			prevStep = timeNowNano;
		}
		
		simulator.log("Simulation finished.");
		System.out.println("--------------------STATS--------------------");
		simulator.stats.printStats();
		//simulator.printStats();		
	}
	
	/**
	 * Returns the next passenger id
	 */
	public long nextPassengerId() {
		return this.passengerId++;
	}
}
