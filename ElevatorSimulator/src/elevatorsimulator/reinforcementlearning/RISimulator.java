package elevatorsimulator.reinforcementlearning;

import java.io.IOException;

import elevatorsimulator.Building;
import elevatorsimulator.ElevatorCar;
import elevatorsimulator.ElevatorCarConfiguration;
import elevatorsimulator.Passenger;
import elevatorsimulator.Scenario;
import elevatorsimulator.SchedulerCreator;
import elevatorsimulator.SchedulingAlgorithm;
import elevatorsimulator.Simulator;
import elevatorsimulator.SimulatorSettings;
import elevatorsimulator.TrafficProfile;
import marl.utility.Config;
import marl.utility.Rand;

/**
 * Represents a simulator using reinforcement learning
 * @author Anton Jansson
 *
 */
public class RISimulator {
	public static void main(String[] args) throws IOException {
		Config config = new Config();
		config.readFile("src/elevatorsimulator/reinforcementlearning/config.ini");
		
		if (config.getInt("rand_seed") == -1) {
			Rand.INSTANCE.setSeed(System.currentTimeMillis());
		} else {
			Rand.INSTANCE.setSeed((long)config.getInt("rand_seed"));
		}
		
		//Create the simulator
		int[] floors = new int[] {
			0, 80, 70
		};
		
		TrafficProfile.Interval[] arrivalRates = new TrafficProfile.Interval[1];
//		arrivalRates[0] = new TrafficProfile.Interval(0.01, 0.45, 0.45);
		arrivalRates[0] = new TrafficProfile.Interval(0.01, 1.0, 0);
		
		SchedulerCreator creator = new SchedulerCreator() {		
			@Override
			public SchedulingAlgorithm createScheduler(Building building) {
				return new SchedulingAlgorithm() {				
					@Override
					public void update(Simulator simulator) {
					
					}
										
					@Override
					public void passengerArrived(Simulator simulator, Passenger passenger) {
						
					}
					

					@Override
					public void passengerBoarded(Simulator simulator, ElevatorCar elevatorCar,	Passenger passenger) {
						
					}
					
					@Override
					public void passengerExited(Simulator simulator, ElevatorCar elevatorCar, Passenger passenger) {

					}
					
					@Override
					public void onIdle(Simulator simulator, ElevatorCar elevatorCar) {
						
					}
					
					@Override
					public void onTurned(Simulator simulator, ElevatorCar elevatorCar) {

					}
				};
			}
		};
		
		Simulator simulator = new Simulator(
			new Scenario(
				1,
				ElevatorCarConfiguration.defaultConfiguration(),
				floors,
				new TrafficProfile(arrivalRates)),
			new SimulatorSettings(5000, 0.2),
			creator);
	    
	    // Obtain from the configuration how to run the experiment
	    int totalRuns = config.getInt("total_runs");
	    int maxEpisodes = config.getInt("max_episodes");
	    int nAgents = simulator.getBuilding().getElevatorCars().length;
	    	    
	    // Create the environment and agent
	    ElevatorSystemEnvironment env = new ElevatorSystemEnvironment(config, simulator);
	    
	    ElevatorCarAgent[] agents = new ElevatorCarAgent[nAgents];
	    
	    for(int i = 0;  i < nAgents; i++) {
	    	agents[i] = new ElevatorCarAgent(
	    		config,
	    		simulator.getBuilding(),
	    		simulator.getBuilding().getElevatorCars()[i]);
	    }

	    System.out.println("Starting Experiment");
	    long start = System.currentTimeMillis();
	    for(int runNo = 0; runNo < totalRuns; runNo++) {
	        // initialise the environment and agent(s)
	        env.initialise();
	        
	        for(int i = 0; i < nAgents; i++) {
	        	agents[i].initialise();
	        }
	        	
	        // Add the agent into the environment
	        for(int i = 0; i < nAgents; i++) {
	        	env.add(agents[i]);
	        }
	        	
	        // Output where we are up to
	        System.out.println("Beginning run #" + runNo);
	        
	        for (int episodeNo = 0; episodeNo < maxEpisodes; episodeNo++) {
	            // Reset the environment
	            env.reset(episodeNo);
	            simulator.reset();
	            simulator.start();
	            
	            while (simulator.advance()) {
	            	env.incrementTime();
	            }          
	            
	            System.out.println(
	            	"\tRun #" + (runNo + 1) + "\tEpisode #" + (episodeNo + 1)
	            	+ " Reward: " + env.totalReward() + " Average SWT: " + simulator.getStats().averageSquaredWaitTime() + "s");
	            
	            for (ElevatorCarAgent agent : agents) {
	            	for (int i = 0; i < agent.getActionDistribution().length; i++) {
	            		System.out.println("\t" + ElevatorCarAgent.Action.values()[i] + ": " + agent.getActionDistribution()[i]);
	            	}
	            }
	        }
	    }

	    System.out.println();
	    System.out.println("-- End of Experiment--");
	    long end = System.currentTimeMillis();
	    System.out.println("Experiment ran for " + (end-start) + "ms");	
	    simulator.printStats();
	}
}
