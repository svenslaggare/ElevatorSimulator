package elevatorsimulator;

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
	
	private StatsInterval globalInterval;
	private final int[] passengerFloorArrivals;
	private final int[] passengerFloorExits;
	
	private final ElevatorCar[] elevatorCars;
	private boolean printFloorsAndElevators = false;
		
	private StatsInterval pollInterval;
	private int intervalNum = 0;
	
	private StatsInterval currentStatsInterval;
	private final List<StatsInterval> statsIntervals = new ArrayList<StatsInterval>();
	
	/**
	 * The lengtho of a stats interval in seconds
	 */
	public static final double INTERVAL_LENGTH_SEC = 60 * 60;
	
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
		
		this.globalInterval = StatsInterval.newTimeInterval(0, this.elevatorCars.length);
		this.pollInterval = StatsInterval.newPollInterval(0, this.elevatorCars.length);
		this.currentStatsInterval = StatsInterval.newTimeInterval(0, this.elevatorCars.length);
	}
	
	/**
	 * Marks that a passenger has been generated
	 * @param The passenger
	 */
	public void generatedPassenger(Passenger passenger) {
		this.updateGeneratedPassenger(this.globalInterval, passenger);
		this.updateGeneratedPassenger(this.currentStatsInterval, passenger);
		this.updateGeneratedPassenger(this.pollInterval, passenger);
		this.passengerFloorArrivals[passenger.getArrivalFloor()]++;
		this.passengerFloorExits[passenger.getDestinationFloor()]++;
	}
	
	/**
	 * Updates the stats when a passenger is generated
	 * @param statsInterval The stats interval
	 * @param passenger The passenger
	 */
	private void updateGeneratedPassenger(StatsInterval statsInterval, Passenger passenger) {
		statsInterval.increaseNumGenerated();
		
		if (passenger.getArrivalFloor() != 0 && passenger.getDestinationFloor() != 0) {
			statsInterval.increaseNumInterfloors();
		}
		
		if (passenger.getArrivalFloor() == 0) {
			statsInterval.increaseNumUp();
		}
		
		if (passenger.getDestinationFloor() == 0) {
			statsInterval.increaseNumDown();
		}
	}
	
	/**
	 * Marks that the given pasenger has exited the given elevator car
	 * @param elevatorCar The elevator car
	 * @param passenger The passenger
	 */
	public void passengerExited(ElevatorCar elevatorCar, Passenger passenger) {
		this.updatePassengerExited(this.globalInterval, elevatorCar, passenger);
		this.updatePassengerExited(this.currentStatsInterval, elevatorCar, passenger);
		this.updatePassengerExited(this.pollInterval, elevatorCar, passenger);
		
		double waitTimeSec = this.clock.asSecond(passenger.waitTime(this.clock));
		if (waitTimeSec > 1000) {
			System.out.println(
				passenger.getId() + ": "
			    + passenger.getArrivalFloor() + "->" + passenger.getDestinationFloor()
			    + ": " + waitTimeSec + "s");
		}
	}
	
	/**
	 * Updates the stats when a passenger exits
	 * @param statsInterval The stats interval
	 * @param elevatorCar The elevator car
	 * @param passenger The passenger
	 */
	private void updatePassengerExited(StatsInterval statsInterval, ElevatorCar elevatorCar, Passenger passenger) {
		statsInterval.increaseNumExists();

		double waitTimeSec = this.clock.asSecond(passenger.waitTime(this.clock));
		statsInterval.increaseTotalWaitTime(waitTimeSec);	
		statsInterval.increaseTotalSquaredWaitTime(waitTimeSec * waitTimeSec);
		
		double rideTimeSec = this.clock.asSecond(passenger.rideTime(this.clock));
		statsInterval.increaseTotalRideTime(rideTimeSec);
		
		if (waitTimeSec > 60) {
			statsInterval.increaseNumWaitsOver60s();
		}
		
		statsInterval.increaseElevatorCarServed(elevatorCar.getId());
	}
	
	/**
	 * Calculates the average wait time
	 */
	public double averageWaitTime() {
		return this.globalInterval.averageWaitTime();
	}
	
	/**
	 * Calculates the average squared wait time
	 */
	public double averageSquaredWaitTime() {
		return this.globalInterval.averageSquaredWaitTime();
	}
	
	/**
	 * Calculates the average ride time
	 */
	public double averageRideTime() {
		return this.globalInterval.averageRideTime();
	}
	
	/**
	 * Returns the % of the wait times over 60 s
	 */
	public double percentageOver60s() {
		return this.globalInterval.percentageOver60s();
	}
			
	/**
	 * Prints the statistics
	 */
	public void printStats() {
		System.out.println("Simulated time: " + clock.asSecond(clock.simulatedTime()) + " s");
		System.out.println("Number of residents: " + this.numResidents);
		System.out.println("Number generated passengers: " + this.globalInterval.getNumGenerated());
		System.out.println("Number served passengers: " + this.globalInterval.getNumExists());
		System.out.println("Average wait time: " + this.averageWaitTime() + " s");
		System.out.println("Average squared wait time: " + this.averageSquaredWaitTime() + " s");
		System.out.println("Average ride time: " + this.averageRideTime() + " s");
		System.out.println("Wait times over 60 sec: " + this.percentageOver60s() + "%");
		System.out.println("Longest wait time: " + this.globalInterval.getLongestWaitTime() + " s");
		
		System.out.println("Number of up travels: " + this.globalInterval.getNumUp());
		System.out.println("Number of down travels: " + this.globalInterval.getNumDown());
		System.out.println("Number of interfloor travels: " + this.globalInterval.getNumInterfloors());
		
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
	 * Returns the global interval
	 */
	public StatsInterval getGlobalInterval() {
		return this.globalInterval;
	}
	
	/**
	 * Returns the stats intervals
	 */
	public List<StatsInterval> getStatsIntervals() {
		return this.statsIntervals;
	}
	
	/**
	 * Exports the statistics
	 * @param fileName The name of the file to export to
	 */
	public void exportStats(String fileName) {
		List<StatsInterval> globalInterval = new ArrayList<StatsInterval>();
		globalInterval.add(this.globalInterval);
		StatsInterval.exportStats(fileName + "", globalInterval, INTERVAL_LENGTH_SEC);	
		StatsInterval.exportStats(fileName + "-Hour", this.statsIntervals, INTERVAL_LENGTH_SEC);	
	}
	
	/**
	 * Updates the statistics
	 */
	public void update() {
		double timeNow = this.simulator.getClock().timeNowSec();
		double duration = timeNow - this.currentStatsInterval.getStartTime();
		if (duration >= INTERVAL_LENGTH_SEC) {
			this.statsIntervals.add(this.currentStatsInterval);
			this.currentStatsInterval = StatsInterval.newTimeInterval(timeNow, this.elevatorCars.length);
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
		this.pollInterval = StatsInterval.newPollInterval(this.intervalNum++, this.elevatorCars.length);
	}
	
	/**
	 * Resets the stats
	 */
	public void reset() {				
		this.intervalNum = 0;
		this.globalInterval = StatsInterval.newTimeInterval(0, this.elevatorCars.length);
		this.pollInterval = StatsInterval.newPollInterval(0, this.elevatorCars.length);
		this.currentStatsInterval = StatsInterval.newTimeInterval(0, this.elevatorCars.length);
		this.statsIntervals.clear();
		
		for (int i = 0; i < passengerFloorArrivals.length; i++) {
			passengerFloorArrivals[i] = 0;
		}
		
		for (int i = 0; i < passengerFloorExits.length; i++) {
			passengerFloorExits[i] = 0;
		}				
	}
}
