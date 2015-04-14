package elevatorsimulator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import elevatorsimulator.schedulers.*;

/**
 * Runs the simulator
 * @author Anton Jansson
 *
 */
public class SimulatorRunner {
	private final int numRuns;
	private final SimulatorSettings settings;
	private final List<Scenario> scenarios;
	private final List<SchedulerCreator> schedulerCreators;
	
	public final static int NUM_DATA_RUNS = 1000;
	public final static long DATA_RUN_SEED = 1337 * 4711;
	
	/**
	 * Creates a new simulator runner
	 * @param numRuns The number of runs
	 * @param settings The settings to use
	 * @param scenarios The scenarios
	 * @param schedulerCreators The schedulers to use
	 */
	public SimulatorRunner(int numRuns, SimulatorSettings settings, List<Scenario> scenarios, List<SchedulerCreator> schedulerCreators) {
		this.numRuns = numRuns;
		this.settings = settings;
		this.scenarios = scenarios;
		this.schedulerCreators = schedulerCreators;
	}
		
	/**
	 * Runs the simulator with the specified scenarios, schedulers and traffic profiles
	 */
	public void run() {
		Random seedGenerator = new Random(DATA_RUN_SEED);
		
		long[] randSeeds = new long[this.numRuns];
		for (int i = 0; i < this.numRuns; i++) {
			randSeeds[i] = seedGenerator.nextLong();
		}
		
		for (Scenario scenario : this.scenarios) {
			System.out.println("----------------Running scenario " + scenario.getName() +  "----------------");
						
			for (SchedulerCreator schedulerCreator : this.schedulerCreators) {
				List<StatsInterval> stats = new ArrayList<StatsInterval>();
				List<List<StatsInterval>> hourStats = new ArrayList<List<StatsInterval>>();
				String name = "";
				
				for (int i = 0; i < this.numRuns; i++) {
					Simulator simulator = new Simulator(scenario, this.settings, schedulerCreator, randSeeds[i]);
					simulator.setExportStats(false);
					simulator.run();
					stats.add(simulator.getStats().getGlobalInterval());
					hourStats.add(simulator.getStats().getStatsIntervals());
					
					if (name == "") {
						name = simulator.getSimulationName();
					}
				}
				
				List<StatsInterval> averageStats = new ArrayList<StatsInterval>();
				averageStats.add(StatsInterval.average(stats));
				StatsInterval.exportStats(name, averageStats, SimulatorStats.INTERVAL_LENGTH_SEC);
				
				List<StatsInterval> averageHourStats = StatsInterval.averageHours(hourStats);			
				StatsInterval.exportStats(name + "-Hour", averageHourStats, SimulatorStats.INTERVAL_LENGTH_SEC);
			}
			
			System.out.println("----------------End scenario----------------");
		}
	}
	
	public static void main(String[] args) {
		List<Scenario> scenarios = new ArrayList<Scenario>();
		
		scenarios.add(Scenarios.createMediumBuilding(2));
//		scenarios.add(Scenarios.createMediumBuilding(3));
//		scenarios.add(Scenarios.createLargeBuilding(3));
//		scenarios.add(Scenarios.createLargeBuilding(4));
		
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
		
		schedulerCreators.add(new SchedulerCreator() {		
			@Override
			public SchedulingAlgorithm createScheduler(Building building) {
				return new ThreePassageGroupElevator(building);
			}
		});
		
		SimulatorSettings settings = new SimulatorSettings(0.01, 24 * 60 * 60);	
		SimulatorRunner runner = new SimulatorRunner(NUM_DATA_RUNS, settings, scenarios, schedulerCreators);
		runner.run();
	}
}
