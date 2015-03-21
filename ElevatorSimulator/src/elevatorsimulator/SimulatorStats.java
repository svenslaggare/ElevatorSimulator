package elevatorsimulator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Contains stats about the simulator
 * @author Anton Jansson
 *
 */
public class SimulatorStats {
	private final SimulatorClock clock;
	private final Simulator simulator;
	private final long numResidents;
	private long numGenerated;
	private long numExists;
	
	private final int[] passengerFloorArrivals;
	private final int[] passengerFloorExits;
	
	private long numUp;
	private long numDown;
	private long numInterfloors;
	
	private long totalWaitTime;
	private double totalSquaredWaitTime;
	private long totalRideTime;
	private long numWaitsOver60s;
	
	private final ElevatorCar[] elevatorCars;
	private boolean printFloorsAndElevators = false;
		
	private StatsInterval pollInterval = StatsInterval.newPollInterval(0);
	private int intervalNum = 0;
	
	private StatsInterval currentStatsInterval = StatsInterval.newTimeInterval(0);
	private final List<StatsInterval> statsIntervals = new ArrayList<StatsInterval>();
	private final double intervalLengthSec = 60 * 60;
	
	/**
	 * Contains statistics about an interval
	 * @author Anton Jansson
	 *
	 */
	public static class StatsInterval {
		private final double startTime;
		private final int num;
		
		private long numGenerated;
		private long numExists;
				
		private long numUp;
		private long numDown;
		private long numInterfloors;
		
		private long totalWaitTime;
		private double totalSquaredWaitTime;
		private long totalRideTime;
		private long numWaitsOver60s;
		
		/**
		 * Creates a new interval
		 * @param startTime The start time of the interval
		 */
		private StatsInterval(double startTime) {
			this.startTime = startTime;
			this.num = -1;
		}
		
		/**
		 * Creates a new interval
		 * @param num The number of the interval
		 */
		private StatsInterval(int num) {
			this.num = num;
			this.startTime = -1;
		}
		
		/**
		 * Creates a new time-based interval
		 * @param startTime The start time
		 */
		public static StatsInterval newTimeInterval(double startTime) {
			return new StatsInterval(startTime);
		}
		
		/**
		 * Creates a new poll-based interval
		 * @param num The number of the interval
		 */
		public static StatsInterval newPollInterval(int num) {
			return new StatsInterval(num);
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
		
		public long getNumGenerated() {
			return numGenerated;
		}

		public long getNumExists() {
			return numExists;
		}

		public long getNumUp() {
			return numUp;
		}

		public long getNumDown() {
			return numDown;
		}

		public long getNumInterfloors() {
			return numInterfloors;
		}

		public long getTotalWaitTime() {
			return totalWaitTime;
		}

		public double getTotalSquaredWaitTime() {
			return totalSquaredWaitTime;
		}

		public long getTotalRideTime() {
			return totalRideTime;
		}

		public long getNumWaitsOver60s() {
			return numWaitsOver60s;
		}
				
		/**
		 * Calculates the average wait time
		 */
		public double averageWaitTime(SimulatorClock clock) {
			return clock.asSecond(this.totalWaitTime) / (double)this.numExists;
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
		public double averageRideTime(SimulatorClock clock) {
			return clock.asSecond(this.totalRideTime) / (double)this.numExists;
		}
		
		/**
		 * Returns the % of the wait times over 60 s
		 */
		public double percentageOver60s() {
			return (this.numWaitsOver60s / (double)this.numExists) * 100;
		}
	}
	
	/**
	 * Holds statistics for the simulator
	 * @param simulator The simulator
	 */
	public SimulatorStats(Simulator simulator) {
		this.simulator = simulator;
		this.clock = simulator.getClock();
		int numFloors = simulator.getBuilding().getFloors().length;
		this.numResidents = simulator.getBuilding().getTotalNumberOfResidents();
		this.passengerFloorArrivals = new int[numFloors];
		this.passengerFloorExits = new int[numFloors];
		this.elevatorCars = simulator.getBuilding().getElevatorCars();
	}
	
	/**
	 * Marks that a passenger has been generated
	 * @param The passenger
	 */
	public void generatedPassenger(Passenger passenger) {
		this.numGenerated++;
		this.currentStatsInterval.numGenerated++;
		
		this.passengerFloorArrivals[passenger.getArrivalFloor()]++;
		this.passengerFloorExits[passenger.getDestinationFloor()]++;
		
		if (passenger.getArrivalFloor() != 0 && passenger.getDestinationFloor() != 0) {
			this.numInterfloors++;
			this.pollInterval.numInterfloors++;
			this.currentStatsInterval.numInterfloors++;
		}
		
		if (passenger.getArrivalFloor() == 0) {
			this.numUp++;
			this.pollInterval.numUp++;
			this.currentStatsInterval.numUp++;
		}
		
		if (passenger.getDestinationFloor() == 0) {
			this.numDown++;
			this.pollInterval.numDown++;
			this.currentStatsInterval.numDown++;
		}
	}
	
	/**
	 * Marks that a passenger has exited
	 */
	public void passengerExited(Passenger passenger) {
		this.numExists++;
		this.pollInterval.numExists++;
		this.currentStatsInterval.numExists++;
		
		long waitTime = passenger.waitTime(this.clock);
		this.totalWaitTime += waitTime;
		this.currentStatsInterval.totalWaitTime += waitTime;
		
		double waitTimeSec = this.clock.asSecond(waitTime);
		this.totalSquaredWaitTime += waitTimeSec * waitTimeSec;
		this.pollInterval.totalSquaredWaitTime += waitTimeSec * waitTimeSec;
		this.currentStatsInterval.totalSquaredWaitTime += waitTimeSec * waitTimeSec;
		
		long rideTime = passenger.rideTime(this.clock);
		this.totalRideTime += rideTime;
		this.currentStatsInterval.totalRideTime += rideTime;
		
		if (waitTime > clock.secondsToTime(60)) {
			this.numWaitsOver60s++;
			this.currentStatsInterval.numWaitsOver60s++;
			this.simulator.log(passenger.getArrivalFloor() + "->" + passenger.getDestinationFloor());
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
	 * Returns the % of the wait times over 60 s
	 */
	public double percentageOver60s() {
		return (this.numWaitsOver60s / (double)this.numExists) * 100;
	}
			
	/**
	 * Prints the statistics
	 */
	public void printStats() {
		System.out.println("Simulated time: " + clock.asSecond(clock.simulatedTime()) + " s");
		System.out.println("Number of residents: " + this.numResidents);
		System.out.println("Number generated passengers: " + this.numGenerated);
		System.out.println("Number served passengers: " + this.numExists);
		System.out.println("Average wait time: " + this.averageWaitTime() + " s");
		System.out.println("Average squared wait time: " + this.averageSquaredWaitTime() + " s");
		System.out.println("Average ride time: " + this.averageRideTime() + " s");
		System.out.println("Wait times over 60 sec: " + this.percentageOver60s() + "%");
		
		System.out.println("Number of up travels: " + this.numUp);
		System.out.println("Number of down travels: " + this.numDown);
		System.out.println("Number of interfloor travels: " + this.numInterfloors);
		
		if (this.printFloorsAndElevators) {
			System.out.println("----Floor arrivals----");
			for (int floor = 0; floor < this.passengerFloorArrivals.length; floor++) {
				System.out.println(floor + ": " + this.passengerFloorArrivals[floor]);
			}
			
			System.out.println("----Floor exits----");
			for (int floor = 0; floor < this.passengerFloorExits.length; floor++) {
				System.out.println(floor + ": " + this.passengerFloorExits[floor]);
			}
			
			System.out.println("----Elevators----");
			for (ElevatorCar elevator : this.elevatorCars) {
				System.out.println(elevator.getId() + ": " + elevator.getNumPassengers());
			}
		}
	}
	
	/**
	 * Exports the statistics
	 * @param fileName The name of the file to export to
	 */
	public void exportStats(String fileName) {
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
			writer.write("\n");
			
			for (StatsInterval interval : this.statsIntervals) {
				writer.write((int)(interval.getStartTime() / this.intervalLengthSec) + ";");
				writer.write(interval.numGenerated + ";");
				writer.write(interval.numExists + ";");
				writer.write(interval.averageWaitTime(this.clock) + ";");
				writer.write(interval.averageSquaredWaitTime() + ";");
				writer.write(interval.averageRideTime(this.clock) + ";");
				writer.write(interval.percentageOver60s() + ";");
				writer.write(interval.numUp + ";");
				writer.write(interval.numDown + ";");
				writer.write(interval.numInterfloors + "");
				writer.write("\n");
			}
			
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Updates the statistics
	 */
	public void update() {
		double timeNow = this.simulator.getClock().timeNowSec();
		double duration = timeNow - this.currentStatsInterval.getStartTime();
		if (duration >= this.intervalLengthSec) {
			this.statsIntervals.add(this.currentStatsInterval);
			this.currentStatsInterval = StatsInterval.newTimeInterval(timeNow);
		}
	}
	
	/**
	 * Marks that the simulation is finished
	 */
	public void done() {
		this.statsIntervals.add(this.currentStatsInterval);
	}
	
	/**
	 * Returns the current poll interval
	 */
	public StatsInterval getPollInterval() {
		return this.pollInterval;
	}
	
	/**
	 * Resets the poll interval
	 */
	public void resetPollInterval() {
		this.pollInterval = StatsInterval.newPollInterval(this.intervalNum++);
	}
	
	/**
	 * Resets the stats
	 */
	public void reset() {
		this.intervalNum = 0;
		this.pollInterval = StatsInterval.newPollInterval(0);
		this.currentStatsInterval = StatsInterval.newTimeInterval(0);
		this.statsIntervals.clear();
		
		this.numGenerated = 0;
		this.numExists = 0;
		
		for (int i = 0; i < passengerFloorArrivals.length; i++) {
			passengerFloorArrivals[i] = 0;
		}
		
		for (int i = 0; i < passengerFloorExits.length; i++) {
			passengerFloorExits[i] = 0;
		}
				
		this.numUp = 0;
		this.numDown = 0;
		this.numInterfloors = 0;
		
		this.totalWaitTime = 0;
		this.totalSquaredWaitTime = 0;
		this.totalRideTime = 0;
		this.numWaitsOver60s = 0;
	}
}
