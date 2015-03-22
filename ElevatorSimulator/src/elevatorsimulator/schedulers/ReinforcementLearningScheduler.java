package elevatorsimulator.schedulers;

import java.util.ArrayList;
import java.util.List;

import elevatorsimulator.ElevatorCar;
import elevatorsimulator.Passenger;
import elevatorsimulator.SchedulingAlgorithm;
import elevatorsimulator.Simulator;

/**
 * Represents a scheduler that uses Reinforcement learning
 * @author Anton Jansson
 *
 */
public class ReinforcementLearningScheduler implements SchedulingAlgorithm {
	private final List<SchedulingAlgorithm> schedulers = new ArrayList<SchedulingAlgorithm>();
	private int activeScheduler;
	
	/**
	 * Creates a new Reinforcement learning scheduler
	 * @param schedulers The schedulers
	 */
	public ReinforcementLearningScheduler(List<SchedulingAlgorithm> schedulers) {
		this.schedulers.addAll(schedulers);
	}
	
	private SchedulingAlgorithm activeScheduler() {
		return this.schedulers.get(this.activeScheduler);
	}
	
	/**
	 * Switches the active scheduler to the given
	 * @param simulator The simulator
	 * @param scheduler The scheduler
	 */
	public void switchTo(Simulator simulator, int scheduler) {
		boolean hasSwitched = false;
		
		if (this.activeScheduler != scheduler) {
			//The strategy has switched
			hasSwitched = true;
		}
		
		this.activeScheduler = scheduler;
		
		if (hasSwitched) {
			this.activeScheduler().changedTo(simulator);
		}
	}
	
	@Override
	public String toString() {
		return "ReinforcementLearning";
	}
	
	@Override
	public void passengerArrived(Simulator simulator, Passenger passenger) {
		this.schedulers.get(this.activeScheduler).passengerArrived(simulator, passenger);
	}
	
	@Override
	public void passengerBoarded(Simulator simulator, ElevatorCar elevatorCar,	Passenger passenger) {
		this.schedulers.get(this.activeScheduler).passengerBoarded(simulator, elevatorCar, passenger);
	}
	
	@Override
	public void passengerExited(Simulator simulator, ElevatorCar elevatorCar, Passenger passenger) {
		this.schedulers.get(this.activeScheduler).passengerExited(simulator, elevatorCar, passenger);
	}

	@Override
	public void update(Simulator simulator) {
		this.schedulers.get(this.activeScheduler).update(simulator);
	}

	@Override
	public void onIdle(Simulator simulator, ElevatorCar elevatorCar) {
		this.schedulers.get(this.activeScheduler).onIdle(simulator, elevatorCar);
	}
	
	@Override
	public void onTurned(Simulator simulator, ElevatorCar elevatorCar) {
		this.schedulers.get(this.activeScheduler).onTurned(simulator, elevatorCar);
	}
	
	@Override
	public void changedTo(Simulator simulator) {

	}
}
