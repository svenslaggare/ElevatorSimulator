package elevatorsimulator.schedulers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import elevatorsimulator.Building;
import elevatorsimulator.ElevatorCar;
import elevatorsimulator.Passenger;
import elevatorsimulator.SchedulingAlgorithm;
import elevatorsimulator.Simulator;
import elevatorsimulator.ElevatorCar.State;

/**
 * Implements the 'RoundRobin' scheduling algorithm
 * @author Anton Jansson and Kristoffer Uggla Lingvall
 *
 */
public class RoundRobin implements SchedulingAlgorithm {
	private List<ElevatorData> elevators = new ArrayList<ElevatorData>();
	private int nextElevator = 0;
	private boolean isUpPeak = false;
	
	/**
	 * Contains data about an elevator
	 */
	private static class ElevatorData {
		public final ElevatorCar elevator;
		public final Queue<Passenger> queue = new LinkedList<Passenger>();
		
		public ElevatorData(ElevatorCar elevator) {
			this.elevator = elevator;
		}
	}
	
	/**
	 * Creates a new instance of the 'RoundRobin' class
	 * @param building The building
	 * @param isUpPeak Indicates if the up-peak variant is used
	 */
	public RoundRobin(Building building, boolean isUpPeak) {
		for (ElevatorCar elevator : building.getElevatorCars()) {
			this.elevators.add(new ElevatorData(elevator));
		}
		
		this.isUpPeak = isUpPeak;
	}
		
	@Override
	public void passengerArrived(Simulator simulator, Passenger passenger) {
		this.elevators.get(this.nextElevator).queue.add(passenger);
		this.nextElevator = (nextElevator + 1) % this.elevators.size();
	}
	
	@Override
	public void passengerBoarded(Simulator simulator, ElevatorCar elevatorCar,	Passenger passenger) {
		
	}
	
	@Override
	public void passengerExited(Simulator simulator, ElevatorCar elevatorCar, Passenger passenger) {

	}

	@Override
	public void update(Simulator simulator) {
		for (ElevatorData elevator : this.elevators) {
			ElevatorCar elevatorCar = elevator.elevator;	
			
			if (elevatorCar.getState() == State.IDLE) {
				Passenger toHandle = null;
				if (!elevator.queue.isEmpty()) {
					toHandle = elevator.queue.remove();
				}
				
				if (toHandle != null) {
					elevator.elevator.moveTowards(simulator, toHandle.getArrivalFloor());
					continue;
				}
			}
		}
	}

	@Override
	public void onIdle(Simulator simulator, ElevatorCar elevatorCar) {
		if (this.isUpPeak && this.elevators.get(elevatorCar.getId()).queue.isEmpty()) {
			elevatorCar.moveTowards(simulator, 0);
		}
	}
	
	@Override
	public void onTurned(Simulator simulator, ElevatorCar elevatorCar) {

	}
	
	@Override
	public void changedTo(Simulator simulator) {
		//Clear queues
		for (ElevatorData elevatorData : this.elevators) {
			elevatorData.queue.clear();
		}
		
		//Add to queues
		for (Passenger passenger : simulator.getControlSystem().getHallQueue()) {
			this.passengerArrived(simulator, passenger);
		}
	}
	
	@Override
	public String toString() {
		if (!this.isUpPeak) {
			return "Round Robin";
		} else {
			return "Up-Peak Group Elevator";
		}
	}
}
