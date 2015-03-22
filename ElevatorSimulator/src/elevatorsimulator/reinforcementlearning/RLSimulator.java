package elevatorsimulator.reinforcementlearning;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import elevatorsimulator.Building;
import elevatorsimulator.Scenarios;
import elevatorsimulator.SchedulerCreator;
import elevatorsimulator.SchedulingAlgorithm;
import elevatorsimulator.Simulator;
import elevatorsimulator.SimulatorClock;
import elevatorsimulator.SimulatorSettings;
import elevatorsimulator.SimulatorStats;
import elevatorsimulator.StatsInterval;
import elevatorsimulator.schedulers.LongestQueueFirst;
import elevatorsimulator.schedulers.ReinforcementLearningScheduler;
import elevatorsimulator.schedulers.RoundRobin;
import elevatorsimulator.schedulers.ThreePassageGroupElevator;
import elevatorsimulator.schedulers.Zoning;
import marl.utility.Config;
import marl.utility.Rand;

/**
 * Represents a simulator using reinforcement learning
 * @author Anton Jansson
 *
 */
public class RLSimulator {	
	public static void main(String[] args) throws IOException {
		Config config = new Config();
		config.readFile("src/elevatorsimulator/reinforcementlearning/config.ini");
		
		if (config.getInt("rand_seed") == -1) {
			Rand.INSTANCE.setSeed(System.currentTimeMillis());
		} else {
			Rand.INSTANCE.setSeed((long)config.getInt("rand_seed"));
		}
		
		//Create the simulator
		SchedulerCreator creator = new SchedulerCreator() {		
			@Override
			public SchedulingAlgorithm createScheduler(Building building) {
				List<SchedulingAlgorithm> schedulers = new ArrayList<SchedulingAlgorithm>();
				schedulers.add(new LongestQueueFirst());
				schedulers.add(new Zoning(building.getElevatorCars().length, building));
				schedulers.add(new RoundRobin(building, false));
				schedulers.add(new ThreePassageGroupElevator(building));
				return new ReinforcementLearningScheduler(schedulers);
			}
		};
		
		Simulator simulator = new Simulator(
			Scenarios.createLargeBuilding(3),
			new SimulatorSettings(0.01, 24 * 60 * 60),
			creator,
			1337);
	    
	    // Obtain from the configuration how to run the experiment
	    int maxEpisodes = config.getInt("max_episodes");
	    double intervalLearningLength = 10 * 60;
	    		
	    // Create the environment and agent
	    ElevatorSystemEnvironment env = new ElevatorSystemEnvironment(simulator);    
	    ElevatorSystemAgent agent = new ElevatorSystemAgent(config);

	    Random seedGenerator = new Random(4711 * 1337);
		int dataRuns = 15;
		
		long[] randSeeds = new long[dataRuns];
		for (int i = 0; i < dataRuns; i++) {
			randSeeds[i] = seedGenerator.nextLong();
		}	
	    
	    System.out.println("Starting Experiment");
	    long start = System.currentTimeMillis();
	    
        // Initialize the environment and agent
        env.initialise();
        agent.initialise();
        	
        // Add the agent into the environment
        env.add(agent);
        
        List<StatsInterval> globalStats = new ArrayList<StatsInterval>();
        List<List<StatsInterval>> hourStats = new ArrayList<List<StatsInterval>>();
        	
        for (int episodeNo = 0; episodeNo < maxEpisodes; episodeNo++) {
            // Reset the environment
            env.reset(episodeNo);
            
            boolean isDataRun = episodeNo >= maxEpisodes - dataRuns;
            
            //Check if data run
            if (isDataRun) {
            	simulator.reset(randSeeds[dataRuns - (maxEpisodes - episodeNo)]);
            } else {            
            	simulator.reset();
            }
            
            simulator.start();
            
            long lastInterval = 0;
            SimulatorClock clock = simulator.getClock();
            List<Long> exited = new ArrayList<Long>();
            
            while (simulator.advance()) {
            	if (clock.elapsedSinceRealTime(lastInterval) >= clock.secondsToTime(intervalLearningLength)) {
	            	env.incrementTime();
	            	exited.add(simulator.getStats().getPollInterval().getNumExists());
	            	simulator.getStats().resetPollInterval();
	            	lastInterval = clock.timeNow();
            	}
            }          
            
            env.rewardLastState();
            
            globalStats.add(simulator.getStats().getGlobalInterval());
            hourStats.add(simulator.getStats().getStatsIntervals());
            
            System.out.println(
            	"\tEpisode #" + (episodeNo + 1)
            	+ " Reward: " + env.totalReward() + " Average SWT: " + simulator.getStats().averageSquaredWaitTime() + "s"
            	+ " State space: " + agent.getStateSpace());
            
            for (int i = 0; i < agent.getActionDistribution().length; i++) {
        		System.out.println("\t" + ElevatorSystemAgent.Action.values()[i] + ": " + agent.getActionDistribution()[i]);
        	}
            	                      
            if (isDataRun) {
	            System.out.print("\t0: ");
	            int i = 0;
	            int count = 0;
	            int hour = 0;
	            for (ElevatorSystemAgent.Action action : agent.getActionUsage()) {
	            	System.out.print(action.toString().charAt(0) + ": " + exited.get(i) + " ");
	            	count++;
	            	i++;
	            	
	            	if (count == 6) {
	            		hour++;
	            		System.out.println();
	            		System.out.print("\t" + hour + ": ");
	            		count = 0;
	            	}
	            }
            }
            
            System.out.println();
        }
	    
	    System.out.println();
	    System.out.println("-- End of Experiment--");
	    long end = System.currentTimeMillis();
	    System.out.println("Experiment ran for " + (end - start) + "ms");	
	    
	    //Export statistics
		List<StatsInterval> averageStats = new ArrayList<StatsInterval>();
		averageStats.add(StatsInterval.average(globalStats));
		StatsInterval.exportStats(simulator.getSimulationName(), averageStats, SimulatorStats.INTERVAL_LENGTH_SEC);
		
		List<StatsInterval> averageHourStats = StatsInterval.averageHours(hourStats);			
		StatsInterval.exportStats(simulator.getSimulationName() + "-Hour", averageHourStats, SimulatorStats.INTERVAL_LENGTH_SEC);
	}
}
