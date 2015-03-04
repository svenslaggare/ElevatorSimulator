import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * The main class for the simulator
 * @author Anton Jansson
 *
 */
public class Simulator {
	private final SimulatorClock clock = new SimulatorClock();
	private final Random random = new Random();
	private final Building building;
	
	private final int[] generatedArrivals;
	
	/**
	 * Creates a new simulator
	 */
	public Simulator() {
		List<Scenario.FloorBuilder> floors = new ArrayList<Scenario.FloorBuilder>();
		floors.add(new Scenario.FloorBuilder(300, 5));
		floors.add(new Scenario.FloorBuilder(50, 2));
		floors.add(new Scenario.FloorBuilder(30, 3));
		floors.add(new Scenario.FloorBuilder(60, 1));
		
		this.building = new Scenario(3, floors).createBuilding();
		this.generatedArrivals = new int[floors.size()];
	}
	
	/**
	 * Returns the simulator clock
	 */
	public SimulatorClock getClock() {
		return clock;
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
	 * Moves the simulation forward one time step
	 * @param duration The elapsed time since the last time step
	 */
	public void moveForward(long duration) {
		this.building.update(this, duration);
	}
	
	/**
	 * Logs the given line
	 * @param line The line
	 */
	public void log(String line) {
		System.out.println(new Date().toString() + ": " + line);
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
		long simulationTime = 10 * 1000;
		long startTime = System.currentTimeMillis();
		Simulator simulator = new Simulator();
		long prevStep = System.nanoTime();
		
		while ((System.currentTimeMillis() - startTime) < simulationTime) {
			long timeNowNano = System.nanoTime();
			simulator.moveForward(simulator.getClock().durationFromRealTime(timeNowNano - prevStep));
			prevStep = timeNowNano;
		}
		
		System.out.println("-----STATS----");
		simulator.printStats();		
	}
}
