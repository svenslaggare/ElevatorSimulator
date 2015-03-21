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
			System.out.println("----------------Running scenario " + scenario.getName() +  "----------------");
			long randSeed = System.currentTimeMillis();
			for (SchedulerCreator schedulerCreator : this.schedulerCreators) {
				Simulator simulator = new Simulator(scenario, this.settings, schedulerCreator, randSeed);
				simulator.run();
			}
			System.out.println("----------------End scenario----------------");
		}
	}
	
	public static void main(String[] args) {
		List<Scenario> scenarios = new ArrayList<Scenario>();
		
		scenarios.add(Scenarios.createMediumBuilding(2));
		scenarios.add(Scenarios.createMediumBuilding(3));
		scenarios.add(Scenarios.createLargeBuilding(3));
		scenarios.add(Scenarios.createLargeBuilding(4));
		
		List<SchedulerCreator> schedulerCreators = new ArrayList<SchedulerCreator>();
		schedulerCreators.add(new SchedulerCreator() {		
			@Override
			public SchedulingAlgorithm createScheduler(Building building) {
				return new LongestQueueFirst(); 
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
				return new RoundRobin(building, false);
			}
		});
		
		schedulerCreators.add(new SchedulerCreator() {		
			@Override
			public SchedulingAlgorithm createScheduler(Building building) {
				return new RoundRobin(building, true);
			}
		});
		
		SimulatorSettings settings = new SimulatorSettings(0.01, 24 * 60 * 60);	
		SimulatorRunner runner = new SimulatorRunner(settings, scenarios, schedulerCreators);
		runner.run();
	}
}
