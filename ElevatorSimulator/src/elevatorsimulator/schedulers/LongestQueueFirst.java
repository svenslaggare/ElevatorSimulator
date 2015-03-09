package elevatorsimulator.schedulers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

import elevatorsimulator.Direction;
import elevatorsimulator.ElevatorCar;
import elevatorsimulator.Passenger;
import elevatorsimulator.SchedulingAlgorithm;
import elevatorsimulator.Simulator;
import elevatorsimulator.ElevatorCar.State;

/**
 * Implements the 'Longest Queue First' scheduling algorithm
 * @author Anton Jansson
 *
 */
public class LongestQueueFirst implements SchedulingAlgorithm {
	@Override
	public void passengerArrived(Simulator simulator, Passenger passenger) {

	}

	private static enum HandleType {
		NONE,
		DISPATCH,
		STOP
	}
	
	@Override
	public void update(Simulator simulator) {
		Queue<Passenger> hallCallQueue = simulator.getControlSystem().getHallQueue();
		List<Passenger> hallCalls = new ArrayList<Passenger>(hallCallQueue);
		Collections.sort(hallCalls, new Comparator<Passenger>() {
			@Override
			public int compare(Passenger p1, Passenger p2) {
				return Double.compare(
					p2.waitTime(simulator.getClock()),
					p1.waitTime(simulator.getClock()));
			}
		});
		
		if (!hallCalls.isEmpty()) {			
			for (Passenger passenger : hallCalls) {
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
							boolean correctFloor = false;
							
							if (elevator.getDirection() == Direction.UP) {
								correctFloor = elevator.getFloor() + 1 == passenger.getArrivalFloor();
							} else if (elevator.getDirection() == Direction.DOWN) {
								correctFloor = elevator.getFloor() - 1 == passenger.getArrivalFloor();
							}
														
							if (correctFloor) {
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

	}	
	
	@Override
	public String toString() {
		return "LongestQueueFirst";
	}
}
