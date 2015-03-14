package elevatorsimulator;
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
		
	private SimulatorStatsInterval currentInterval = new SimulatorStatsInterval(0);
	private int intervalNum = 0;
	
	/**
	 * Contains stats for the last interval
	 * @author Anton Jansson
	 *
	 */
	public static class SimulatorStatsInterval {
		private int num;
		private int numUp;
		private int numDown;
		private int numInterfloors;
		
		private double totalSquaredWaitTime;
		private long numExists;
		
		/**
		 * Creates a new interval
		 */
		public SimulatorStatsInterval(int num) {
			this.num = num;
		}
		
		/**
		 * Returns the interval number
		 */
		public int getNum() {
			return this.num;
		}
		
		/**
		 * Returns the number of up passengers
		 */
		public int getNumUp() {
			return numUp;
		}

		/**
		 * Returns the number of down passengers
		 */
		public int getNumDown() {
			return numDown;
		}

		/**
		 * Returns the number of interfloors passengers
		 */
		public int getNumInterfloor() {
			return numInterfloors;
		}
		
		/**
		 * Returns the total squared wait time
		 */
		public double getTotalSquaredWaitTime() {
			return totalSquaredWaitTime;
		}

		/**
		 * Returns the number of exits
		 */
		public long getNumExists() {
			return numExists;
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
		this.passengerFloorArrivals[passenger.getArrivalFloor()]++;
		this.passengerFloorExits[passenger.getDestinationFloor()]++;
		
		if (passenger.getArrivalFloor() != 0 && passenger.getDestinationFloor() != 0) {
			this.numInterfloors++;
			this.currentInterval.numInterfloors++;
		}
		
		if (passenger.getArrivalFloor() == 0) {
			this.numUp++;
			this.currentInterval.numUp++;
		}
		
		if (passenger.getDestinationFloor() == 0) {
			this.numDown++;
			this.currentInterval.numDown++;
		}
	}
	
	/**
	 * Marks that a passenger has exited
	 */
	public void passengerExited(Passenger passenger) {
		this.numExists++;
		this.currentInterval.numExists++;
		
		long waitTime = passenger.waitTime(this.clock);
		this.totalWaitTime += waitTime;
		
		double waitTimeSec = this.clock.asSecond(waitTime);
		this.totalSquaredWaitTime += waitTimeSec * waitTimeSec;
		this.currentInterval.totalSquaredWaitTime += waitTimeSec * waitTimeSec;
		
		long rideTime = passenger.rideTime(this.clock);
		this.totalRideTime += rideTime;
		
		if (waitTime > clock.secondsToTime(60)) {
			this.numWaitsOver60s++;
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
	 * Returns the current interval
	 */
	public SimulatorStatsInterval getInterval() {
		return this.currentInterval;
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
	
	/**
	 * Resets the interval
	 */
	public void resetInterval() {
		this.currentInterval = new SimulatorStatsInterval(this.intervalNum++);
	}
	
	/**
	 * Resets the stats
	 */
	public void reset() {
		this.intervalNum = 0;
		this.currentInterval = new SimulatorStatsInterval(this.intervalNum++);
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
