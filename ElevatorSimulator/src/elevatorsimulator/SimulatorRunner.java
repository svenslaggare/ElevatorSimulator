package elevatorsimulator;

import java.util.ArrayList;
import java.util.List;

import elevatorsimulator.schedulers.*;

/**
 * Runs the simulator
 * @author Anton Jansson
 *
 */
public class SimulatorRunner {
	private final SimulatorSettings settings;
	private final List<Scenario> scenarios;
	private final List<SchedulerCreator> schedulerCreators;
	
	/**
	 * Creates a new simulator runner
	 * @param settings The settings to use
	 * @param scenarios The scenarios
	 * @param schedulerCreators The schedulers to use
	 */
	public SimulatorRunner(SimulatorSettings settings, List<Scenario> scenarios, List<SchedulerCreator> schedulerCreators) {
		this.settings = settings;
		this.scenarios = scenarios;
		this.schedulerCreators = schedulerCreators;
	}
		
	/**
	 * Runs the simulator with the specified scenarios, schedulers and traffic profiles
	 */
	public void run() {
		for (Scenario scenario : this.scenarios) {
			for (SchedulerCreator schedulerCreator : this.schedulerCreators) {
				Simulator simulator = new Simulator(scenario, this.settings, schedulerCreator);
				simulator.run();
			}
		}
	}
	
	public static void main(String[] args) {
		int[] floors = new int[] {
			0, 80, 70, 90, 80, 115, 120, 90, 80, 90, 80, 100, 80, 80, 50
		};
			
		TrafficProfile.Interval[] arrivalRates = new TrafficProfile.Interval[1];
		arrivalRates[0] = new TrafficProfile.Interval(0.03, 1, 0.0);
		
		List<Scenario> scenarios = new ArrayList<Scenario>();
		scenarios.add(new Scenario(3, ElevatorCarConfiguration.defaultConfiguration(), floors, new TrafficProfile(arrivalRates)));
				
		List<SchedulerCreator> schedulerCreators = new ArrayList<SchedulerCreator>();
		schedulerCreators.add(new SchedulerCreator() {		
			@Override
			public SchedulingAlgorithm createScheduler(Building building) {
				return new CollectiveControl(); 
			}
		});
		
		schedulerCreators.add(new SchedulerCreator() {		
			@Override
			public SchedulingAlgorithm createScheduler(Building building) {
				return new Zoning(building.getElevatorCars().length, building); 
			}
		});
		
		schedulerCreators.add(new SchedulerCreator() {		
			@Override
			public SchedulingAlgorithm createScheduler(Building building) {
				return new LongestQueueFirst();
			}
		});
		
		schedulerCreators.add(new SchedulerCreator() {		
			@Override
			public SchedulingAlgorithm createScheduler(Building building) {
				return new RoundRobin(building, false);
			}
		});
		
		schedulerCreators.add(new SchedulerCreator() {		
			@Override
			public SchedulingAlgorithm createScheduler(Building building) {
				return new RoundRobin(building, true);
			}
		});
		
		SimulatorSettings settings = new SimulatorSettings(100, 30);	
		SimulatorRunner runner = new SimulatorRunner(settings, scenarios, schedulerCreators);
		runner.run();
	}
}
