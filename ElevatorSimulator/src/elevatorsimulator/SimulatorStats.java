package elevatorsimulator;
/**
 * Contains stats about the simulator
 * @author Anton Jansson
 *
 */
public class SimulatorStats {
	private final SimulatorClock clock;
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
	
	/**
	 * Holds statistics for the simulator
	 * @param simulator The simulator
	 */
	public SimulatorStats(Simulator simulator) {
		this.clock = simulator.getClock();
		int numFloors = simulator.getBuilding().getFloors().length;
		this.numResidents = simulator.getBuilding().getTotalNumberOfResidents();
		this.passengerFloorArrivals = new int[numFloors];
		this.passengerFloorExits = new int[numFloors];
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
		}
		
		if (passenger.getArrivalFloor() == 0) {
			this.numUp++;
		}
		
		if (passenger.getDestinationFloor() == 0) {
			this.numDown++;
		}
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
		
		if (waitTime > clock.secondsToTime(60)) {
			this.numWaitsOver60s++;
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
		
		System.out.println("----Floor arrivals----");
		for (int floor = 0; floor < this.passengerFloorArrivals.length; floor++) {
			System.out.println(floor + ": " + this.passengerFloorArrivals[floor]);
		}
		
		System.out.println("----Floor exits----");
		for (int floor = 0; floor < this.passengerFloorExits.length; floor++) {
			System.out.println(floor + ": " + this.passengerFloorExits[floor]);
		}
	}
}
