package elevatorsimulator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Represents the control system for the elevator
 * @author Anton Jansson
 *
 */
public class ControlSystem {
	private final Building building;
	private final Queue<Passenger> hallCallQueue = new LinkedList<Passenger>();
	
	/**
	 * Creates a new control system for the given building
	 * @param building The building
	 */
	public ControlSystem(Building building) {
		this.building = building;
	}
	
	/**
	 * Handles the given hall call
	 * @param passenger The passenger that made the call
	 */
	public void handleHallCall(Passenger passenger) {
		this.hallCallQueue.add(passenger);
	}
	
	/**
	 * Marks that the 
	 * @param passenger
	 */
	public void hallCallHandled(Passenger passenger) {
		this.hallCallQueue.remove(passenger);
	}
	
	/**
	 * Updates the control system
	 * @param simulator The simulator
	 * @param duration The elapsed time since the last time step
	 */
	public void update(Simulator simulator, long duration) {
		if (!this.hallCallQueue.isEmpty()) {			
			for (Passenger passenger : this.hallCallQueue) {
				for (ElevatorCar elevator : this.building.getElevatorCars()) {
					//Dispatch calls
					if (elevator.getDirection() == Direction.NONE && passenger.getArrivalFloor() != elevator.getFloor()) {
						elevator.moveTowards(simulator, passenger.getArrivalFloor());
						simulator.elevatorDebugLog(elevator.getId(), "Movings towards floor " + passenger.getArrivalFloor() + ".");
						break;
					}
				}
			}
		}
		
		for (ElevatorCar elevator : simulator.getBuilding().getElevatorCars()) {
//			if (elevator.hasStopTimeStarted() && elevator.stopTimePassed(simulator.getClock())) {
//				simulator.elevatorDebugLog(elevator.getId(), "Stop time passed.");
//				elevator.endStopTime(simulator.getClock());
//			}
		}
	}
}
