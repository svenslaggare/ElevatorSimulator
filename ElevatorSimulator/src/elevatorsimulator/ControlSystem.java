package elevatorsimulator;
import java.util.LinkedList;
import java.util.Queue;

import elevatorsimulator.schedulers.CollectiveControl;

/**
 * Represents the control system for the elevator
 * @author Anton Jansson
 *
 */
public class ControlSystem {
	private final Simulator simulator;
	private final Queue<Passenger> hallCallQueue = new LinkedList<Passenger>();
	private final SchedulingAlgorithm scheduler = new CollectiveControl();
	
	/**
	 * Creates a new control system for the given simulator
	 * @param simulator The simulator
	 */
	public ControlSystem(Simulator simulator) {
		this.simulator = simulator;
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
//		if (!this.hallCallQueue.isEmpty()) {			
//			for (Passenger passenger : this.hallCallQueue) {
//				for (ElevatorCar elevator : this.simulator.getBuilding().getElevatorCars()) {
//					//Dispatch calls
//					if (elevator.getState() == ElevatorCar.State.IDLE && passenger.getArrivalFloor() != elevator.getFloor()) {
//						simulator.elevatorDebugLog(elevator.getId(), "Movings towards floor " + passenger.getArrivalFloor() + ".");
//						elevator.moveTowards(simulator, passenger.getArrivalFloor());
//						break;
//					}
//					
//					//This implements the 'Collective control algorithm'.
//					if (elevator.getState() == ElevatorCar.State.MOVING) {
//						Direction dir = Direction.getDirection(passenger.getArrivalFloor(), passenger.getDestinationFloor());
//
//						if (elevator.getDirection() == dir) {
//							boolean correctFloor = false;
//							
//							if (elevator.getDirection() == Direction.UP) {
//								correctFloor = elevator.getFloor() + 1 == passenger.getArrivalFloor();
//							} else if (elevator.getDirection() == Direction.DOWN) {
//								correctFloor = elevator.getFloor() - 1 == passenger.getArrivalFloor();
//							}
//														
//							if (correctFloor) {
//								elevator.stopElevatorAtNextFloor();
//								break;
//							}
//						}
//					}
//				}
//			}
//		}
		
		this.scheduler.update(simulator);
	}
}
