package elevatorsimulator;
/**
 * Represents a clock for the simulator
 * @author Anton Jansson
 *
 */
public class SimulatorClock {
	public static final double TIME_SCALE = 1e-9;
	public static final long NANOSECONDS_PER_SECOND = 1000000000;		
	private final double timeStep;
	private double simulatedTime = 0.0;
	
	/**
	 * Creates a new simulator clock
	 * @param timeStep The time step
	 */
	public SimulatorClock(double timeStep) {
		this.timeStep = timeStep;
		this.simulatedTime = 0;
	}
	
	/**
	 * Returns the current time
	 */
	public long timeNow() {
		return (long)(this.simulatedTime * NANOSECONDS_PER_SECOND);
	}
	
	/**
	 * Returns the amount of time that has been simulated
	 */
	public long simulatedTime() {
		return (long)(this.simulatedTime * NANOSECONDS_PER_SECOND);
	}
 	
	/**
	 * Returns the duration in simulation time from real time
	 * @param durationRealTime The duration in real time
	 */
	public long durationFromRealTime(long durationRealTime) {
		return durationRealTime;
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
	 * @param seconds The number of seconds
	 */
	public long secondsToTime(double seconds) {
		return (long)(seconds / TIME_SCALE);
	}
	
	/**
	 * Returns the given amount of minutes in the clocks time
	 * @param time The number of seconds
	 */
	public long minutesToTime(double minutes) {
		return (long)((minutes * 60) / TIME_SCALE);
	}
	
	/**
	 * Resets the clock
	 */
	public void reset() {
		this.simulatedTime = 0;
	}
	
	/**
	 * Advances the simulation by the time step
	 */
	public void step() {
		this.simulatedTime += this.timeStep;
	}
}
