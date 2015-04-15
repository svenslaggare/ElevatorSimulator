package elevatorsimulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


/**
 * Contains statistics information about an interval
 * @author Anton Jansson and Kristoffer Uggla Lingvall
 *
 */
public class StatsInterval {
	private final double startTime;
	private final int num;
	
	private long numGenerated;
	private long numExists;
			
	private long numUp;
	private long numDown;
	private long numInterfloors;
	
	private double totalWaitTime;
	private double totalSquaredWaitTime;
	private double totalRideTime;
	private long numWaitsOver60s;
	private double longestWaitTime = 0;
	
	private final int[] elevatorCarDistribution;
	
	/**
	 * Creates a new interval
	 * @param startTime The start time of the interval
	 * @param numElevators The number of elevator cars
	 */
	private StatsInterval(double startTime, int numElevators) {
		this.startTime = startTime;
		this.num = -1;
		this.elevatorCarDistribution = new int[numElevators];
	}
	
	/**
	 * Creates a new interval
	 * @param num The number of the interval
	 * @param numElevators The number of elevator cars
	 */
	private StatsInterval(int num, int numElevators) {
		this.num = num;
		this.startTime = -1;
		this.elevatorCarDistribution = new int[numElevators];
	}
	
	/**
	 * Creates a new time-based interval
	 * @param startTime The start time
	 * @param numElevators The number of elevator cars
	 */
	public static StatsInterval newTimeInterval(double startTime, int numElevators) {
		return new StatsInterval(startTime, numElevators);
	}
	
	/**
	 * Creates a new poll-based interval
	 * @param num The number of the interval
	 * @param numElevators The number of elevator cars
	 */
	public static StatsInterval newPollInterval(int num, int numElevators) {
		return new StatsInterval(num, numElevators);
	}
	
	/**
	 * Returns the start time of the interval in seconds since the simulation started
	 */
	public double getStartTime() {
		return this.startTime;
	}

	/**
	 * Returns the interval number
	 */
	public int getNum() {
		return this.num;
	}
	
	/**
	 * Returns the number of generated passengers
	 */
	public long getNumGenerated() {
		return numGenerated;
	}
	
	/**
	 * Increases the number of generated passengers
	 */
	public void increaseNumGenerated() {
		this.numGenerated++;
	}

	/**
	 * Returns the number of passenger that has exited
	 */
	public long getNumExists() {
		return numExists;
	}

	/**
	 * Increases the number of exits
	 */
	public void increaseNumExists() {
		this.numExists++;
	}
	
	/**
	 * Returns the number of up travels
	 */
	public long getNumUp() {
		return numUp;
	}
	
	/**
	 * Increases the number of up travel
	 */
	public void increaseNumUp() {
		this.numUp++;
	}

	/**
	 * Returns the number of down travels
	 */
	public long getNumDown() {
		return numDown;
	}
	
	/**
	 * Increases the number of down travels
	 */
	public void increaseNumDown() {
		this.numDown++;
	}

	/**
	 * Returns the number of interfloor travels
	 */
	public long getNumInterfloors() {
		return numInterfloors;
	}
	
	/**
	 * Increases the number of interfloor travels
	 */
	public void increaseNumInterfloors() {
		this.numInterfloors++;
	}

	/**
	 * Returns the total wait time
	 */
	public double getTotalWaitTime() {
		return totalWaitTime;
	}
	
	/**
	 * Increases the total wait time
	 * @param waitTime The wait time to increase by
	 */
	public void increaseTotalWaitTime(double waitTime) {
		this.longestWaitTime = Math.max(longestWaitTime, waitTime);
		this.totalWaitTime += waitTime;
	}

	/**
	 * Returns the total squared wait time
	 */
	public double getTotalSquaredWaitTime() {
		return totalSquaredWaitTime;
	}
	
	/**
	 * Increases the total squared wait time 
	 * @param squaredWaitTime The squared wait time to increase by
	 */
	public void increaseTotalSquaredWaitTime(double squaredWaitTime) {
		this.totalSquaredWaitTime += squaredWaitTime;
	}

	/**
	 * Returns the total ride time
	 */
	public double getTotalRideTime() {
		return totalRideTime;
	}
	
	/**
	 * Increases the total ride time
	 * @param rideTime The ride time to increase by
	 */
	public void increaseTotalRideTime(double rideTime) {
		this.totalRideTime += rideTime;
	}
	
	/**
	 * Returns the number of waits over 60s
	 * @return
	 */
	public long getNumWaitsOver60s() {
		return numWaitsOver60s;
	}
	
	/**
	 * Increases the number of waits over 60s
	 */
	public void increaseNumWaitsOver60s() {
		this.numWaitsOver60s++;
	}
	
	/**
	 * Returns the longest wait time
	 */
	public double getLongestWaitTime() {
		return this.longestWaitTime;
	}
			
	/**
	 * Calculates the average wait time
	 */
	public double averageWaitTime() {
		return this.totalWaitTime / (double)this.numExists;
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
		return this.totalRideTime / (double)this.numExists;
	}
	
	/**
	 * Returns the % of the wait times over 60 s
	 */
	public double percentageOver60s() {
		return (this.numWaitsOver60s / (double)this.numExists) * 100;
	}
	
	/**
	 * Returns the elevator car distribution
	 */
	public int[] getElevatorCarDistribution() {
		return this.elevatorCarDistribution;
	}
	
	/**
	 * Increases the number of served passenger for the given elevator car
	 * @param elevatorCarId The elevator car id
	 */
	public void increaseElevatorCarServed(int elevatorCarId) {
		this.elevatorCarDistribution[elevatorCarId]++;
	}
	
	/**
	 * Averages the given intervals and returns a new interval
	 * @param intervals The intervals
	 */
	public static StatsInterval average(List<StatsInterval> intervals) {
		StatsInterval averageInterval = StatsInterval.newTimeInterval(
			intervals.get(0).startTime,
			intervals.get(0).elevatorCarDistribution.length);
		
		for (StatsInterval interval : intervals) {
			averageInterval.numGenerated += interval.numGenerated;
			averageInterval.numExists += interval.numExists;
			averageInterval.numUp += interval.numUp;
			averageInterval.numDown += interval.numDown;
			averageInterval.numInterfloors += interval.numInterfloors;
			averageInterval.totalWaitTime += interval.totalWaitTime;
			averageInterval.totalSquaredWaitTime += interval.totalSquaredWaitTime;
			averageInterval.totalRideTime += interval.totalRideTime;
			averageInterval.numWaitsOver60s += interval.numWaitsOver60s;
			
			for (int i = 0; i < interval.elevatorCarDistribution.length; i++) {
				averageInterval.elevatorCarDistribution[i] += interval.elevatorCarDistribution[i];
			}
		}
		
		averageInterval.numGenerated = averageInterval.numGenerated / intervals.size();
		averageInterval.numExists = averageInterval.numExists/ intervals.size();
		averageInterval.numUp = averageInterval.numUp / intervals.size();
		averageInterval.numDown = averageInterval.numDown / intervals.size();
		averageInterval.numInterfloors = averageInterval.numInterfloors / intervals.size();
		averageInterval.totalWaitTime = averageInterval.totalWaitTime / intervals.size();
		averageInterval.totalSquaredWaitTime = averageInterval.totalSquaredWaitTime / intervals.size();
		averageInterval.totalRideTime = averageInterval.totalRideTime / intervals.size();
		averageInterval.numWaitsOver60s = averageInterval.numWaitsOver60s / intervals.size();
			
		for (int i = 0; i < averageInterval.elevatorCarDistribution.length; i++) {
			averageInterval.elevatorCarDistribution[i] = averageInterval.elevatorCarDistribution[i] / intervals.size();
		}
		
		return averageInterval;
	}
	
	/**
	 * Averages the given hour intervals
	 * @param hourIntervals The hour intervals
	 */
	public static List<StatsInterval> averageHours(List<List<StatsInterval>> hourIntervals) {
		List<StatsInterval> averageHourStats = new ArrayList<StatsInterval>();
		int minNumIntervals = Integer.MAX_VALUE;
		for (List<StatsInterval> intervals : hourIntervals) {
			minNumIntervals = Math.min(minNumIntervals, intervals.size());
		}
		
		for (int i = 0; i < minNumIntervals; i++) {
			List<StatsInterval> hourTotal = new ArrayList<StatsInterval>();
			
			for (List<StatsInterval> runIntervals : hourIntervals) {
				hourTotal.add(runIntervals.get(i));
			}			
			
			averageHourStats.add(StatsInterval.average(hourTotal));
		}
		
		return averageHourStats;
	}
	
	/**
	 * Exports the given statistics
	 * @param fileName The name of the file to export to
	 * @param intervals The intervals to export
	 * @param intervalLengthSec The length of the intervals in seconds
	 */
	public static void exportStats(String fileName, List<StatsInterval> intervals, double intervalLengthSec) {
		try {
			File dataDir = new File("data");
			if (!dataDir.exists()) {
				dataDir.mkdir();
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter("data/" + fileName + ".csv"));
			writer.write("Hour;");
			writer.write("Number generated passengers;");
			writer.write("Number served passengers;");
			writer.write("Average wait time;");
			writer.write("Average squared wait time;");
			writer.write("Average ride time;");
			writer.write("Wait times over 60 sec;");
			writer.write("Number of up travels;");
			writer.write("Number of down travels;");
			writer.write("Number of interfloor travels;");
			
			for (int i = 0; i < intervals.get(0).elevatorCarDistribution.length; i++) {
				writer.write("Served passangers elevator " + i + ";");
			}
			
			writer.write("\n");
			
			for (StatsInterval interval : intervals) {
				writer.write((int)(interval.getStartTime() / intervalLengthSec) + ";");
				writer.write(interval.getNumGenerated() + ";");
				writer.write(interval.getNumExists() + ";");
				writer.write(interval.averageWaitTime() + ";");
				writer.write(interval.averageSquaredWaitTime() + ";");
				writer.write(interval.averageRideTime() + ";");
				writer.write(interval.percentageOver60s() + ";");
				writer.write(interval.getNumUp() + ";");
				writer.write(interval.getNumDown() + ";");
				writer.write(interval.getNumInterfloors() + ";");
				
				for (int served : interval.elevatorCarDistribution) {
					writer.write(served + ";");
				}
				
				writer.write("\n");
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}