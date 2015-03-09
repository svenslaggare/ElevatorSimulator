package elevatorsimulator;
import java.util.LinkedList;
import java.util.Queue;

import elevatorsimulator.schedulers.*;

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
	 */
	public ControlSystem(Simulator simulator) {
		this.simulator = simulator;
//		this.scheduler = new CollectiveControl();
		this.scheduler = new Zoning(
			simulator.getBuilding().getElevatorCars().length,
			simulator.getBuilding());
//		this.scheduler = new LongestQueueFirst();
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
	 * Marks that the 
	 * @param passenger
	 */
	public void hallCallHandled(Passenger passenger) {
		this.hallCallQueue.remove(passenger);
	}
	
	/**
	 * Marks that the given elevator is idle
	 * @param elevator The elevator
	 */
	public void elevatorIdle(ElevatorCar elevator) {
		this.scheduler.onIdle(simulator, elevator);
	}
	
	/**
	 * Updates the control system
	 * @param duration The elapsed time since the last time step
	 */
	public void update(long duration) {		
		this.scheduler.update(simulator);
	}
}
