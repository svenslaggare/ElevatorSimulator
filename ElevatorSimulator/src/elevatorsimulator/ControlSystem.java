package elevatorsimulator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents the control system for the elevator
 * @author Anton Jansson
 *
 */
public class ControlSystem {
	private final Simulator simulator;
	private final Queue<Passenger> hallCallQueue = new LinkedList<Passenger>();
	private final SchedulingAlgorithm scheduler;
	
	/**
	 * Creates a new control system for the given simulator
	 * @param simulator The simulator
	 * @param The scheduler
	 */
	public ControlSystem(Simulator simulator, SchedulingAlgorithm scheduler) {
		this.simulator = simulator;
		this.scheduler = scheduler;
		
		for (ElevatorCar elevator : simulator.getBuilding().getElevatorCars()) {
			this.elevatorIdle(elevator);
		}
	}
	
	/**
	 * Returns the scheduler
	 */
	public SchedulingAlgorithm getScheduler() {
		return this.scheduler;
	}
	
	/**
	 * Returns the name of the scheduler
	 */
	public String getSchedulerName() {
		return this.scheduler.toString();
	}
	
	/**
	 * Returns the queue for the passenger waiting to be handled
	 */
	public Queue<Passenger> getHallQueue() {
		return this.hallCallQueue;
	}
	
	/**
	 * Handles the given hall call
	 * @param passenger The passenger that made the call
	 */
	public void handleHallCall(Passenger passenger) {
		this.hallCallQueue.add(passenger);
		this.scheduler.passengerArrived(this.simulator, passenger);
	}
	
	/**
	 * Marks that the given call has been handled by the given elevator
	 * @param elevatorCar The elevator car
	 * @param passenger The passenger
	 */
	public void hallCallHandled(ElevatorCar elevatorCar, Passenger passenger) {
		this.hallCallQueue.remove(passenger);
		this.scheduler.passengerBoarded(this.simulator, elevatorCar, passenger);
	}
	
	/**
	 * Marks that the given elevator car is idle
	 * @param elevator The elevator car
	 */
	public void elevatorIdle(ElevatorCar elevatorCar) {
		this.scheduler.onIdle(simulator, elevatorCar);
	}
	
	/**
	 * Marks that the given elevator has turned
 	 * @param elevatorCar The elevator car
	 */
	public void elevatorTurned(ElevatorCar elevatorCar) {
		this.scheduler.onTurned(simulator, elevatorCar);
	}
	
	/**
	 * Updates the control system
	 * @param duration The elapsed time since the last time step
	 */
	public void update(long duration) {		
		this.scheduler.update(simulator);
	}
	
	/**
	 * Resets the control system
	 */
	public void reset() {
		this.hallCallQueue.clear();
	}
}
