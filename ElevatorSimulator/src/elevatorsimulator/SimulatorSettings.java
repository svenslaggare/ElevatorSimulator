package elevatorsimulator;

/**
 * Contains settings for the simulator
 * @author Anton Jansson
 *
 */
public class SimulatorSettings {
	private final double simulationSpeed;
	private final double simulationTimeInSec;
	
	/**
	 * Creates new settings
	 * @param simulationSpeed The simulation speed
	 * @param simulationTimeInSec The simulation time in seconds
	 */
	public SimulatorSettings(double simulationSpeed, double simulationTimeInSec) {
		if (simulationSpeed < 1.0) {
			throw new IllegalArgumentException("simulationSpeed must be >= 1.0");
		}
		
		this.simulationSpeed = simulationSpeed;
		this.simulationTimeInSec = simulationTimeInSec;
	}
	
	/**
	 * Returns the simulation speed
	 */
	public double getSimulationSpeed() {
		return simulationSpeed;
	}
	
	/**
	 * Returns the simulation time in seconds
	 */
	public double getSimulationTimeInSec() {
		return simulationTimeInSec;
	}	
}
