package experiments.example;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import marl.environments.PuddleWorld.PuddleWorldEnvironment;
import marl.utility.Config;
import marl.utility.Rand;
import marl.utility.stats.Moving;

public class PuddleWorldExperiment
{

	public static void main(String[] args)
		throws IOException
	{
	    // Check to see if a configuration file was given, else use default
	    String configurationPath;
	    if( args.length > 1 )
	        configurationPath = args[1];
	    else
	        configurationPath = "experiments/example/configPuddleWorld.ini";

	    
	    // Load the configuration
	    Config cfg = new Config();
	    cfg.readFile(configurationPath);
	    
	    // seed the random function
	    if( cfg.getInt("rand_seed") == -1 )
	    	Rand.INSTANCE.setSeed(System.currentTimeMillis());
	    else
	    	Rand.INSTANCE.setSeed((long)cfg.getInt("rand_seed"));
	    
	    // Create the environment and agent
	    PuddleWorldEnvironment<PuddleWorldAgentTC> env   = new PuddleWorldEnvironment<>(cfg);
	    PuddleWorldAgentTC                         agent = new PuddleWorldAgentTC(cfg);


	    // Obtain from the configuration how to run the experiment
	    int totalRuns   = cfg.getInt("total_runs"),
	        maxEpisodes = cfg.getInt("max_episodes"),
	        maxSteps    = cfg.getInt("max_steps"),
	        maxUpdates  = cfg.getInt("max_updates"),
	        movingSize  = cfg.getInt("max_moving_size");
	    
	    // Create the moving average calculation -- for random starting
	    Moving moving  = ( movingSize == -1 ) ? new Moving(500) : new Moving(movingSize);


	    // Open results file // write/trunc
	    PrintStream fileOut = new PrintStream(new File(cfg.getString("result_file")));

	    System.out.println("Starting Experiment");
	    long start = System.currentTimeMillis();
	    for( int runNo=0; runNo<totalRuns; runNo++ ) {
	        // initialise the environment and agent(s)
	        env.initialise();
	        agent.initialise();
	        // Add the agent into the environment
	        env.add(agent);
	        //...
	        moving.empty();

	        // Output where we are up to
	        System.out.println("Beginning run #" + (runNo+1));

	        for( int episodeNo=0, updates=0; (maxUpdates!=-1 && updates<maxUpdates) || (maxUpdates==-1 && episodeNo<maxEpisodes); episodeNo++ ) {
	            // Reset the environment
	            env.reset(episodeNo);
	            int step;
	            for( step=0; step<maxSteps && (maxUpdates==-1 || updates<maxUpdates) &&  !env.inTerminalState(); step++, updates++ ) {
	                env.incrementTime();
	            }
	            moving.add(env.getSumReward());
	            
	            
	            System.out.format("run=%d eps=%d rwd=%.1f cum=%.1f tiles=%d%n", (runNo+1), episodeNo+1, moving.getAverage(), moving.getCumulative(), agent.getNoTiles());
	            
	            fileOut.format("%d %d %.1f %.1f %d%n", (runNo+1), episodeNo+1, moving.getAverage(), moving.getCumulative(), agent.getNoTiles());
	        }
	        fileOut.println();
	    }

	    System.out.println();
	    System.out.println("-- End of Experiment--");
	    long end = System.currentTimeMillis();
	    System.out.println("Experiment ran for " + (end-start) + "ms");
	    fileOut.close();
		
	}

}
