package elevatorsimulator;
/**
 * Contains stats about the simulator
 * @author Anton Jansson
 *
 */
public class SimulatorStats {
	private final SimulatorClock clock;
	private long numExists;
	private long totalWaitTime;
	private double totalSquaredWaitTime;
	private long totalRideTime;
	private long numRidesOver60s;
	
	public SimulatorStats(SimulatorClock clock) {
		this.clock = clock;
	}
	
	/**
	 * Marks that a passenger has exited
	 */
	public void passengerExited(Passenger passenger) {
		this.numExists++;
		
		long waitTime = passenger.waitTime(this.clock);
		this.totalWaitTime += waitTime;
		double waitTimeSec = this.clock.asSecond(waitTime);
		this.totalSquaredWaitTime += waitTimeSec * waitTimeSec;
		
		long rideTime = passenger.rideTime(this.clock);
		this.totalRideTime += rideTime;
		
		if (rideTime > clock.secondsToTime(60)) {
			this.numRidesOver60s++;
		}
	}
	
	/**
	 * Calculates the average wait time
	 */
	public double averageWaitTime() {
		return this.clock.asSecond(this.totalWaitTime) / (double)this.numExists;
	}
	
	/**
	 * Calculates the average squared wait time
	 */
	public double averageSquaredWaitTime() {
		return this.totalSquaredWaitTime / (double)this.numExists;
	}
	
	/**
	 * Calculates the average ride time
	 */
	public double averageRideTime() {
		return this.clock.asSecond(this.totalRideTime) / (double)this.numExists;
	}
	
	/**
	 * Returns the % of the rides over 60 s
	 */
	public double percentageOver60s() {
		return (this.numRidesOver60s / (double)this.numExists) * 100;
	}
	
	/**
	 * Prints the statistics
	 */
	public void printStats() {
		System.out.println("Simulated time: " + clock.asSecond(clock.simulatedTime()));
		System.out.println("Num served passengers: " + this.numExists);
		System.out.println("Average wait time: " + this.averageWaitTime());
		System.out.println("Average squared wait time: " + this.averageSquaredWaitTime());
		System.out.println("Average ride time: " + this.averageRideTime());
		System.out.println("% of rides over 60 s: " + this.percentageOver60s());
	}
}
