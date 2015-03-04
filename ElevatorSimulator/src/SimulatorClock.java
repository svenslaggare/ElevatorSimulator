/**
 * Represents a clock for the simulator
 * @author Anton Jansson
 *
 */
public class SimulatorClock {
	public static final double TIME_SCALE = 1e-9;
	
	/**
	 * Returns the current now
	 */
	public long timeNow() {
		return System.currentTimeMillis();
	}
	
	/**
	 * Returns the duration in simulation time from real time
	 * @param durationRealTime The duration in real time
	 */
	public long durationFromRealTime(long durationRealTime) {
		return durationRealTime;
	}
}
