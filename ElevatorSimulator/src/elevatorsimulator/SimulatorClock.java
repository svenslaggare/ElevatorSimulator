package elevatorsimulator;
/**
 * Represents a clock for the simulator
 * @author Anton Jansson
 *
 */
public class SimulatorClock {
	public static final double TIME_SCALE = 1e-9;
	public static final long NANOSECONDS_PER_SECOND = 1000000000;
	private final double simulationSpeed = 1.0;
	
	private final long simulationStarted;
	
	/**
	 * Creates a new simulator clock
	 */
	public SimulatorClock() {
		this.simulationStarted = System.nanoTime();
	}
	
	/**
	 * Returns the current time
	 */
	public long timeNow() {
		return System.nanoTime() - this.simulationStarted;
	}
	
	/**
	 * Returns the amount of time that has been simulated
	 */
	public long simulatedTime() {
		return (long)(timeNow() * simulationSpeed);
	}
 	
	/**
	 * Returns the duration in simulation time from real time
	 * @param durationRealTime The duration in real time
	 */
	public long durationFromRealTime(long durationRealTime) {
		return (long)(durationRealTime * simulationSpeed);
	}
	
	/**
	 * Returns the elapsed time since the given time
	 * @param time The time in real time
	 */
	public long elapsedSinceRealTime(long time) {
		return durationFromRealTime(timeNow() - time);
	}
	
	/**
	 * Returns the time returned by the 'timeNow' method in seconds
	 * @param time The time in the clock
	 */
	public double asSecond(long time) {
		return time * TIME_SCALE;
	}
	
	/**
	 * Returns the given amount of seconds in the clocks time
	 * @param time The number of seconds
	 */
	public long secondsToTime(double time) {
		return (long)(time / TIME_SCALE);
	}
}
