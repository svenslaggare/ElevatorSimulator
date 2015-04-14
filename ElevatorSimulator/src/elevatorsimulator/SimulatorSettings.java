package elevatorsimulator;

/**
 * Contains settings for the simulator
 * @author Anton Jansson and Kristoffer Uggla Lingvall
 *
 */
public class SimulatorSettings {
	private final double timeStep;
	private final double simulationTimeInSec;
	
	/**
	 * Creates new settings
	 * @param timeStep The time step
	 * @param simulationTimeInSec The simulation time in seconds
	 */
	public SimulatorSettings(double timeStep, double simulationTimeInSec) {
		this.timeStep = timeStep;
		this.simulationTimeInSec = simulationTimeInSec;
	}

	/**
	 * Returns the time step (in seconds)
	 */
	public double getTimeStep() {
		return timeStep;
	}

	/**
	 * Returns the simulation time
	 */
	public double getSimulationTimeInSec() {
		return simulationTimeInSec;
	}		
}
