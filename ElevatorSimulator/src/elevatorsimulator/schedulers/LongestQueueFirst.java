package elevatorsimulator.schedulers;

import java.util.Queue;

import elevatorsimulator.Direction;
import elevatorsimulator.ElevatorCar;
import elevatorsimulator.ElevatorCar.State;
import elevatorsimulator.Passenger;
import elevatorsimulator.SchedulingAlgorithm;
import elevatorsimulator.Simulator;

/**
 * Implements the 'Longest queue first' scheduling algorithm
 * @author Anton Jansson
 *
 */
public class LongestQueueFirst implements SchedulingAlgorithm {
	@Override
	public void passengerArrived(Simulator simulator, Passenger passenger) {
		
	}

	@Override
	public void passengerBoarded(Simulator simulator, ElevatorCar elevatorCar,	Passenger passenger) {
		
	}
	
	@Override
	public void passengerExited(Simulator simulator, ElevatorCar elevatorCar, Passenger passenger) {

	}
	
	private static enum HandleType {
		NONE,
		DISPATCH,
		STOP
	}
	
	@Override
	public void update(Simulator simulator) {
		Queue<Passenger> hallCallQueue = simulator.getControlSystem().getHallQueue();
		
		if (!hallCallQueue.isEmpty()) {			
			for (Passenger passenger : hallCallQueue) {
				ElevatorCar closestElevator = null;
				int minDeltaFloor = 0;
				HandleType type = HandleType.NONE;
				
				for (ElevatorCar elevator : simulator.getBuilding().getElevatorCars()) {
					if (!elevator.canPickupPassenger(passenger)) {
						continue;
					}
					
					int deltaFloor = Math.abs(elevator.getFloor() - passenger.getArrivalFloor());
					boolean isCandidate = false;
					
					//Dispatch calls
					if (elevator.getState() == State.IDLE && type != HandleType.STOP) {
						if (passenger.getArrivalFloor() != elevator.getFloor()) {
							isCandidate = true;		
							type = HandleType.DISPATCH;
						}
					}
					
					//Check if to stop at the next floor
					if (elevator.getState() == State.MOVING) {
						Direction dir = Direction.getDirection(passenger.getArrivalFloor(), passenger.getDestinationFloor());
	
						if (elevator.getDirection() == dir) {
//							boolean correctFloor = false;
//							
//							if (elevator.getDirection() == Direction.UP) {
//								correctFloor = elevator.getFloor() + 1 == passenger.getArrivalFloor();
//							} else if (elevator.getDirection() == Direction.DOWN) {
//								correctFloor = elevator.getFloor() - 1 == passenger.getArrivalFloor();
//							}
														
							if (elevator.nextFloor() == passenger.getArrivalFloor()) {
								type = HandleType.STOP;
								isCandidate = true;
							}
						}
					}
					
					if (isCandidate) {
						if (closestElevator != null) {
							if (deltaFloor < minDeltaFloor) {
								closestElevator = elevator;
								minDeltaFloor = deltaFloor;
							}
						} else {
							closestElevator = elevator;
							minDeltaFloor = deltaFloor;
						}
					}
				}
				
				if (closestElevator != null) {
					switch (type) {
					case DISPATCH:
						simulator.elevatorDebugLog(closestElevator.getId(), "Movings towards floor " + passenger.getArrivalFloor() + ".");
						closestElevator.moveTowards(simulator, passenger.getArrivalFloor());
						break;
					case STOP:
						closestElevator.stopElevatorAtNextFloor();
						break;
					default:
						break;
					}
				}
			}
		}
	}

	@Override
	public void onIdle(Simulator simulator, ElevatorCar elevatorCar) {
		//elevatorCar.moveTowards(simulator, 0);
	}
	
	@Override
	public void onTurned(Simulator simulator, ElevatorCar elevatorCar) {

	}
	
	@Override
	public String toString() {
		return "LongestQueueFirst";
	}
}
