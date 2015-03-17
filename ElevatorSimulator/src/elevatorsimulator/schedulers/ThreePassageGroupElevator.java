package elevatorsimulator.schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import elevatorsimulator.Building;
import elevatorsimulator.Direction;
import elevatorsimulator.ElevatorCar;
import elevatorsimulator.Passenger;
import elevatorsimulator.SchedulingAlgorithm;
import elevatorsimulator.Simulator;
import elevatorsimulator.ElevatorCar.State;

/**
 * Implements the 'Three passage group elevator' scheduling algorithm
 * @author Anton Jansson
 *
 */
public class ThreePassageGroupElevator implements SchedulingAlgorithm {
	private List<ElevatorData> elevators = new ArrayList<ElevatorData>();
	private Map<ElevatorCar, ElevatorData> elevatorToData = new HashMap<>();
	
	private static enum PassageType {
		NONE,
		P1,
		P2,
		P3;
		
		/**
		 * Indicates if the given passage type is better than the current
		 * @param passageType The passage type
		 */
		public boolean isBetter(PassageType passageType) {
			return passageType.ordinal() < this.ordinal();
		}
	}
	
	/**
	 * Contains data about an elevator car
	 */
	private static class ElevatorData {
		public final ElevatorCar elevatorCar;
		public final Queue<Passenger> callQueue = new LinkedList<Passenger>();
		
		public ElevatorData(ElevatorCar elevatorCar) {
			this.elevatorCar = elevatorCar;
		}
	}
	
	/**
	 * Creates a new instance of the 'ThreePassageGroupElevator' class.
	 * @param building The building
	 */
	public ThreePassageGroupElevator(Building building) {
		for (ElevatorCar elevator : building.getElevatorCars()) {
			ElevatorData elevatorData = new ElevatorData(elevator);
			this.elevators.add(elevatorData);
			this.elevatorToData.put(elevator, elevatorData);
		}
	}
	
	/**
	 * Determines the type of call
	 * @param elevatorCar The elevator car
	 * @param passenger The passenger
	 */
	private PassageType getType(ElevatorCar elevatorCar, Passenger passenger) {
		Direction passDir = passenger.getDirection();
		Direction carDir = elevatorCar.getDirection();
		
		int currentFloor = elevatorCar.nextFloor();	
		boolean carIsOver = currentFloor > passenger.getArrivalFloor();
		boolean carIsUnder = currentFloor < passenger.getArrivalFloor();
		
		if (carIsOver) {
			if (passDir == carDir) {
				if (carDir == Direction.DOWN) {
					return PassageType.P1;
				} else {
					return PassageType.P3;
				}
			} else {
				return PassageType.P2;
			}
		} else if (carIsUnder) {
			if (passDir == carDir) {
				if (carDir == Direction.UP) {
					return PassageType.P1;
				} else {
					return PassageType.P3;
				}
			} else {
				return PassageType.P2;
			}
		} else {
			//Same floor
			if (elevatorCar.getState() == State.IDLE || elevatorCar.getState() == State.STOPPED) {
				return PassageType.NONE;
			}
			
			if (passDir == carDir) {
				return PassageType.P1;
			} else {
				return PassageType.P2;
			}
		}
	}
	
	private Set<Passenger> getCarCalls(Simulator simulator, int minFloor, int maxFloor) {
		Set<Passenger> carCalls = new HashSet<>();
		
		for (ElevatorCar elevatorCar : simulator.getBuilding().getElevatorCars()) {
			for (Passenger passenger : elevatorCar.getPassengers()) {
				if (passenger.getDestinationFloor() > minFloor && passenger.getDestinationFloor() < maxFloor) {
					carCalls.add(passenger);
				}
			}
		}	
		
		return carCalls;
	}
	
	private Set<Passenger> getHallCalls(Simulator simulator, int minFloor, int maxFloor) {
		Set<Passenger> hallCalls = new HashSet<>();
		
		//TODO: Correct this
		for (ElevatorData elevatorData : this.elevators) {
			for (Passenger passenger : elevatorData.callQueue) {
				if (passenger.getDestinationFloor() > minFloor && passenger.getDestinationFloor() < maxFloor) {
					hallCalls.add(passenger);
				}
			}
		}
		
		return hallCalls;
	}
	
	private double calculateStopTime(Simulator simulator, ElevatorCar elevatorCar, Passenger passengerToHandle) {
		int k = passengerToHandle.getArrivalFloor();
		int j = passengerToHandle.getDestinationFloor(); //Find if this is true
		
		int nkPass = 0;
		
		for (Passenger passenger : simulator.getControlSystem().getHallQueue()) {
			if (passenger.getArrivalFloor() == passengerToHandle.getArrivalFloor()
				&& passenger.getDirection() == passengerToHandle.getDirection()) {
				nkPass++;
			}
		}
		
		int fik = 0;
		int fiActual = 0;
		
		if (elevatorCar.getDirection() == Direction.UP) {
			fik = simulator.getBuilding().numFloors() - elevatorCar.getFloor();
			fiActual = simulator.getBuilding().numFloors();
		} else if (elevatorCar.getDirection() == Direction.DOWN) {
			fik = elevatorCar.getFloor();
			fiActual = 0;
		} else {
			fik = Math.max(simulator.getBuilding().numFloors() - elevatorCar.getFloor(), elevatorCar.getFloor());
			
			if (fik == elevatorCar.getFloor()) {
				fiActual = 0;
			} else {
				fiActual = simulator.getBuilding().numFloors() - 1;
			}
		}
		
		double Pik = 0.0;
		
		if (nkPass == 1) {
			Pik = 1.0 - 1.0 / fik;
		} else {
			Pik = Math.exp(-(double)nkPass / fik);
		}
		
		double sik = fik * (1.0 - Pik);
		
		double liNet = 0;
		
		if (fik > 1) {
			for (int l = 2; l <= fik; l++) {
				double product = 1;
				
				for (int j2 = fik - l + 1; j2 <= fik; j2++) {
					product *= Pik;
				}
				
				liNet += product;
			}
		}		
		
		double fiFarthest = fiActual - liNet;
		
		Set<Passenger> Ckj = this.getCarCalls(simulator, k, j);
		Set<Passenger> Hkj = this.getHallCalls(simulator, k, j);
		Set<Passenger> CHkj = new HashSet<>();
		CHkj.addAll(Ckj);
		CHkj.addAll(Hkj);
		
		int skjMandatory = Ckj.size() + Hkj.size() - CHkj.size();
		double skjExtra = sik * ((double)Math.abs(j - k) - 1.0 - skjMandatory) / (double)fik;
		
		return Pik + sik + liNet + fiFarthest + skjMandatory + skjExtra;
	}
		
	@Override
	public void passengerArrived(Simulator simulator, Passenger passenger) {
		ElevatorData bestElevator = null;
		PassageType bestType = PassageType.P3;
		
		for (ElevatorData elevator : this.elevators) {
			PassageType type = this.getType(elevator.elevatorCar, passenger);
			
			if (bestElevator == null) {
				bestElevator = elevator;
				bestType = type;
				continue;
			}
			
			if (bestType.isBetter(type)) {
				bestType = type;
				bestElevator = elevator;
			}
		}
		
		bestElevator.callQueue.add(passenger);
	}
	
	@Override
	public void passengerBoarded(Simulator simulator, ElevatorCar elevatorCar, Passenger passenger) {
		this.elevatorToData.get(elevatorCar).callQueue.remove(passenger);
	}

	@Override
	public void update(Simulator simulator) {
		for (ElevatorData elevatorData : this.elevators) {
			ElevatorCar elevatorCar = elevatorData.elevatorCar;	
			
			if (elevatorCar.getState() == State.MOVING) {
				boolean stopAtNext = false;
				
				for (Passenger passenger : elevatorData.callQueue) {
					PassageType type = this.getType(elevatorCar, passenger);
					
					if (type == PassageType.P1) {
						if (passenger.getArrivalFloor() == elevatorCar.nextFloor()) {
							stopAtNext = true;
							break;
						}
					}
				}
				
				if (stopAtNext) {
					elevatorCar.stopElevatorAtNextFloor();
				}
			} else if (elevatorCar.getState() == State.IDLE) {				
				Passenger toHandle = null;
				if (!elevatorData.callQueue.isEmpty()) {
					toHandle = elevatorData.callQueue.remove();
				}
				
				if (toHandle != null) {
					elevatorCar.moveTowards(simulator, toHandle.getArrivalFloor());
				}
			}
		}
	}

	@Override
	public void onIdle(Simulator simulator, ElevatorCar elevatorCar) {

	}
	
	@Override
	public String toString() {
		return "ThreePassageGroupElevator";
	}
}
