package elevatorsimulator;
import java.util.LinkedList;
import java.util.Queue;

import elevatorsimulator.ElevatorCar.State;

/**
 * Represents a floor in the building
 * @author Anton Jansson
 *
 */
public class Floor {
	private final int floorNumber;
	private final int numResidents;
	private final TrafficProfile traficProfile;
	
	private final Queue<Passenger> waitingQueue;
	
	private long timeLeft = 0;
	private boolean isFirst = true;
	
	private long lastIntervalStart = 0;
	private TrafficProfile.Interval interval;
	private RandomValueGenerator<Floor> destinationFloorGenerator;
	
	/**
	 * Creates a new floor
	 * @param floorNumber The floor number
	 * @param numResidents The number of residents
	 * @param trafficProfile The traffic profile
	 */
	public Floor(int floorNumber, int numResidents, TrafficProfile trafficProfile) {
		this.floorNumber = floorNumber;
		this.numResidents = numResidents;
		this.traficProfile = trafficProfile;
		this.waitingQueue = new LinkedList<Passenger>();
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
	 * Returns the waiting queue for the floor
	 */
	public Queue<Passenger> getWaitingQueue() {
		return waitingQueue;
	}
	
	/**
	 * Sets the interval
	 * @param simulator The simulator
	 */
	private void setInterval(Simulator simulator) {
		this.interval = this.traficProfile.getIntervalData(simulator.getClock().elapsedSinceRealTime(0));
		this.destinationFloorGenerator = new RandomValueGenerator<Floor>(simulator.getRandom());
		
		for (Floor floor : simulator.getBuilding().getFloors()) {
			if (floor != this) {
				double probability = this.interval.destinationFloorProbability(
					simulator.getBuilding(),
					this,
					floor);
				
				//System.out.println(this.floorNumber + " -> " + floor.floorNumber + ": " + probability);
					
				this.destinationFloorGenerator.addValue(probability, floor);
			}
		}
	}
	
	/**
	 * Generates the next arrival time
	 * @param simulator The simulator
	 */
	private void generateNextTimeArrival(Simulator simulator) {
		double averageArrivalRate = this.interval.averageNumberOfArrivals(
			simulator.getBuilding(),
			this) / (double)this.traficProfile.lengthInMinutes();
		
		double nextTime = (-Math.log(1.0 - simulator.getRandom().nextDouble()) / averageArrivalRate);
		this.timeLeft = simulator.getClock().minutesToTime(nextTime);
	}
	
	/**
	 * Generates a random destination floor
	 * @param simulator The simulator
	 */
	private int generateRandomDestination(Simulator simulator) {
		return this.destinationFloorGenerator.randomValue().floorNumber;
	}
	
	/**
	 * Marks that the given hall call has been handled
	 * @param simulator The simulator
	 * @param passenger The passenger
	 */
	private void hallCallHandled(Simulator simulator, Passenger passenger) {
		this.waitingQueue.remove(passenger);
		simulator.getControlSystem().hallCallHandled(passenger);
		passenger.board();
	}
	
	/**
	 * Updates the floor
	 * @param simulator The simulator
	 * @param duration The elapsed time since the last time step
	 */
	public void update(Simulator simulator, long duration) {
		if (!this.waitingQueue.isEmpty()) {
			for (Passenger passenger : new LinkedList<Passenger>(this.waitingQueue)) {
				for (ElevatorCar elevator : simulator.getBuilding().getElevatorCars()) {
					if (elevator.getState() == State.STOPPED || elevator.getState() == State.IDLE) {
//						simulator.elevatorLog(elevator.getId(), "Trying to pick up passenger at floor: " + passenger.getArrivalFloor());
						
						//Check if the elevator can pickup the passenger
						if (!elevator.canPickupPassenger(passenger) || !elevator.canBoard(simulator)) {
							continue;
						}
						
						boolean canPickup = false;
						
						Direction dir = Direction.getDirection(this.floorNumber, passenger.getDestinationFloor());
						
						if (this.floorNumber == elevator.getFloor()) {
							canPickup = 
								elevator.getDirection() == Direction.NONE
								|| elevator.getDirection() == dir;
						}
						
						
						if (canPickup) {
							simulator.elevatorLog(elevator.getId(), "Picked up passenger #" + passenger.getId() + " at floor "
								+ this.floorNumber + " with the destination of "
								+ passenger.getDestinationFloor() + ".");
								
							elevator.setDirection(dir);
							elevator.pickUp(simulator, passenger);
							this.hallCallHandled(simulator, passenger);
							break;
						}
					}
				}
			}
		}
		
		//Check if the next interval has started
		SimulatorClock clock = simulator.getClock();
		long timeNow = clock.timeNow();
		long intervalDuration = timeNow - this.lastIntervalStart;
		
		if (clock.durationFromRealTime(intervalDuration) >= this.traficProfile.length() || interval == null) {
//			if (this.floorNumber == 0 && interval != null) { 
//				simulator.getStats().printStats();
//			}
			
			this.setInterval(simulator);
			this.lastIntervalStart = timeNow;
			this.generateNextTimeArrival(simulator);
		}
				
		if (simulator.canGenerateArrivals()) {
			this.tryGenerateNewArrival(simulator, duration);
		}
	}
	
	/**
	 * Tries to generate a new arrival on the floor. The success of the method depends on the probability.
	 * @param simulator The simulator
	 * @param duration The elapsed time since the last time step
	 * @return True if generated
	 */
	public boolean tryGenerateNewArrival(Simulator simulator, long duration) {
		if (this.isFirst) {
			this.generateNextTimeArrival(simulator);
			this.isFirst = false;
			return false;
		}
		
		this.timeLeft -= duration;
		
		if (this.timeLeft <= 0) {
			int randFloor = generateRandomDestination(simulator);
			
			Passenger newPassenger = new Passenger(
				simulator.nextPassengerId(),
				this.floorNumber,
				randFloor,
				1,
				simulator.getClock());
			
			this.waitingQueue.add(newPassenger);		
			simulator.getControlSystem().handleHallCall(newPassenger);
			
			simulator.log(
				"Generated a new passenger at floor "
				+ this.floorNumber + " with the destination: "
				+ newPassenger.getDestinationFloor() + ".");
			
			simulator.arrivalGenerated(newPassenger);
			
			this.generateNextTimeArrival(simulator);
			return true;
		}
			
		return false;
	}

	/**
	 * Resets the floor
	 */
	public void reset() {
		this.waitingQueue.clear();
		this.interval = null;
		this.isFirst = true;
		this.timeLeft = 0;
	}
}
