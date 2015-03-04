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
		return System.nanoTime();
	}
	
	/**
	 * Returns the duration in simulation time from real time
	 * @param durationRealTime The duration in real time
	 */
	public long durationFromRealTime(long durationRealTime) {
		return durationRealTime;
	}
	
	/**
	 * Returns the time returned by the 'timeNow' method in seconds
	 * @param time The time in the clock
	 */
	public long asSecond(long time) {
		return (long)(time * TIME_SCALE);
	}
	
	/**
	 * Returns the given amount of seconds in the clocks time
	 * @param time The number of seconds
	 */
	public long secondsToTime(double time) {
		return (long)(time / TIME_SCALE);
	}
}
