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
		int i = 0;
		for (Scenario scenario : this.scenarios) {
			System.out.println("----------------Running scenario #" + i + "----------------");
			for (SchedulerCreator schedulerCreator : this.schedulerCreators) {
				Simulator simulator = new Simulator(scenario, this.settings, schedulerCreator);
				simulator.run();
			}
			System.out.println("----------------End scenario----------------");
			
			i++;
		}
	}
	
	public static void main(String[] args) {
		List<Scenario> scenarios = new ArrayList<Scenario>();
		
		int[] floors = new int[] {
			0, 80, 70, 90, 80, 115, 120, 90, 80, 90, 80, 100, 80, 80, 50
		};
			
//		TrafficProfile.Interval[] upPeakScenario = new TrafficProfile.Interval[1];
//		upPeakScenario[0] = new TrafficProfile.Interval(0.03, 0.9, 0.1);		
//		scenarios.add(new Scenario(3, ElevatorCarConfiguration.defaultConfiguration(), floors, new TrafficProfile(upPeakScenario)));
//				
//		TrafficProfile.Interval[] downPeakScenario = new TrafficProfile.Interval[1];
//		downPeakScenario[0] = new TrafficProfile.Interval(0.03, 0.1, 0.9);		
//		scenarios.add(new Scenario(3, ElevatorCarConfiguration.defaultConfiguration(), floors, new TrafficProfile(downPeakScenario)));
		scenarios.add(new Scenario(3, ElevatorCarConfiguration.defaultConfiguration(), floors, TrafficProfiles.WEEK_DAY_PROFILE));
		
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
