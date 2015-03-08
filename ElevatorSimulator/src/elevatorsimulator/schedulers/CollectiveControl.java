package elevatorsimulator.schedulers;

import java.util.Queue;

import elevatorsimulator.Direction;
import elevatorsimulator.ElevatorCar;
import elevatorsimulator.ElevatorCar.State;
import elevatorsimulator.Passenger;
import elevatorsimulator.SchedulingAlgorithm;
import elevatorsimulator.Simulator;

/**
 * Represents the collective control scheduling algorithm
 * @author Anton Jansson
 *
 */
public class CollectiveControl implements SchedulingAlgorithm {
	@Override
	public void passengerArrived(Simulator simulator, Passenger passenger) {
		
	}

	@Override
	public void update(Simulator simulator) {
		Queue<Passenger> hallCallQueue = simulator.getControlSystem().getHallQueue();
		
		if (!hallCallQueue.isEmpty()) {			
			for (Passenger passenger : hallCallQueue) {
				for (ElevatorCar elevator : simulator.getBuilding().getElevatorCars()) {
					//Dispatch calls
					if (elevator.getState() == State.IDLE && passenger.getArrivalFloor() != elevator.getFloor()) {
						simulator.elevatorDebugLog(elevator.getId(), "Movings towards floor " + passenger.getArrivalFloor() + ".");
						elevator.moveTowards(simulator, passenger.getArrivalFloor());
						break;
					}
					
					if (elevator.getState() == State.MOVING) {
						Direction dir = Direction.getDirection(passenger.getArrivalFloor(), passenger.getDestinationFloor());
	
						if (elevator.getDirection() == dir) {
							boolean correctFloor = false;
							
							if (elevator.getDirection() == Direction.UP) {
								correctFloor = elevator.getFloor() + 1 == passenger.getArrivalFloor();
							} else if (elevator.getDirection() == Direction.DOWN) {
								correctFloor = elevator.getFloor() - 1 == passenger.getArrivalFloor();
							}
														
							if (correctFloor) {
								elevator.stopElevatorAtNextFloor();
								break;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void onIdle(Simulator simulator, ElevatorCar elevatorCar) {
		//elevatorCar.moveTowards(simulator, 0);
	}
}
