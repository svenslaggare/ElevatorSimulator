import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents a floor in the building
 * @author Anton Jansson
 *
 */
public class Floor {
	private final int floorNumber;
	private final int numResidents;
	private final double averageArrivalRate;
	private final Queue<Passenger> waitingQueue;
	
	//private final PoissonDistribution random;
	private long timeLeft = 0;
	private boolean isFirst = true;
	
	/**
	 * Creates a new floor
	 * @param floorNumber The floor number
	 * @param numResidents The number of residents
	 * @param averageArrivalRate The average arrival rate
	 */
	public Floor(int floorNumber, int numResidents, double averageArrivalRate) {
		this.floorNumber = floorNumber;
		this.numResidents = numResidents;
		this.averageArrivalRate = averageArrivalRate;
		this.waitingQueue = new LinkedList<Passenger>();
		
		//this.random = new PoissonDistribution(this.averageArrivalRate);
	}
	
	/**
	 * Returns the floor number
	 */
	public int getFloorNumber() {
		return floorNumber;
	}

	/**
	 * Returns the number residents
	 */
	public int getNumResidents() {
		return numResidents;
	}

	/**
	 * Returns the average arrival rate
	 * @return
	 */
	public double getAverageArrivalRate() {
		return averageArrivalRate;
	}
	
	/**
	 * Returns the waiting queue for the floor
	 */
	public Queue<Passenger> getWaitingQueue() {
		return waitingQueue;
	}
	
	/**
	 * Indicates if a passenger arrived
	 * @param simulator The simulator
	 * @param duration The elapsed time since the last time step
	 */
//	private boolean passengerArrived(Simulator simulator, long duration) {
//		//double p = (duration * SimulatorClock.TIME_SCALE) * this.averageArrivalRate;
//		//return simulator.getRandom().nextDouble() <= p;
//		return simulator.getRandom().nextDouble() <= this.random.cumulativeProbability((int)(this.timeSinceLastArrival * SimulatorClock.TIME_SCALE));
//	}
	
	private void generateNextArrival(Simulator simulator) {
		double nextTime = (-Math.log(1.0 - simulator.getRandom().nextDouble()) * (this.averageArrivalRate));
		this.timeLeft = (long)(nextTime / SimulatorClock.TIME_SCALE);
	}
	
	/**
	 * Tries to generate a new arrival on the floor. The success of the method depends on the probability.
	 * @param simulator The simulator
	 * @param duration The elapsed time since the last time step
	 * @return True if generated
	 */
	public boolean tryGenerateNewArrival(Simulator simulator, long duration) {
//		this.timeSinceLastArrival += duration;
//		
//		if (this.passengerArrived(simulator, duration)) {
//			Passenger newPassenger = new Passenger(
//					simulator.getRandom().nextInt(simulator.getBuilding().numFloors()),
//					simulator.getClock());
//			
//			this.waitingQueue.add(newPassenger);
//			
//			simulator.log(
//					"Generated a new passenger at floor "
//					+ this.floorNumber + " with the destination: "
//					+ newPassenger.getDestinationFloor() + ".");
//			
//			simulator.arrivalGenerated(this.floorNumber);
//			
//			this.timeSinceLastArrival = 0;
//			return true;
//		}
		
		if (this.isFirst) {
			this.generateNextArrival(simulator);
			this.isFirst = false;
			return false;
		}
		
		this.timeLeft -= duration;
		
		if (this.timeLeft <= 0) {
			Passenger newPassenger = new Passenger(
					simulator.getRandom().nextInt(simulator.getBuilding().numFloors()),
					simulator.getClock());
			
			this.waitingQueue.add(newPassenger);
			
//			simulator.log(
//					"Generated a new passenger at floor "
//					+ this.floorNumber + " with the destination: "
//					+ newPassenger.getDestinationFloor() + ".");
			
			simulator.arrivalGenerated(this.floorNumber);
			
			this.generateNextArrival(simulator);
			return true;
		}
			
		return false;
	}
}
