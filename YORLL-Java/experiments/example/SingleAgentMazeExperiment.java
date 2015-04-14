/**
 * 
 */
package experiments.example;

import java.io.IOException;

import marl.environments.Maze.MazeEnvironment;
import marl.utility.Config;
import marl.utility.Rand;

/**
 * @author pds
 *
 */
public class SingleAgentMazeExperiment {

	/**
	 * @param args
	 */
	public static void main(String[] args)
			throws IOException
	{
	    // Check to see if a configuration file was given, else use default
	    String configurationPath;
	    if( args.length > 1 )
	        configurationPath = args[1];
	    else
	        configurationPath = "experiments/example/configSingleAgentMaze.ini";

	    
	    // Load the configuration
	    Config cfg = new Config();
	    cfg.readFile(configurationPath);
	    
	    // seed the random function
	    if( cfg.getInt("rand_seed") == -1 )
	    	Rand.INSTANCE.setSeed(System.currentTimeMillis());
	    else
	    	Rand.INSTANCE.setSeed((long)cfg.getInt("rand_seed"));


	    // Obtain from the configuration how to run the experiment
	    int totalRuns   = cfg.getInt("total_runs"),
	        maxEpisodes = cfg.getInt("max_episodes"),
	        maxSteps    = cfg.getInt("max_steps"),
	        nAgents     = cfg.getInt("num_agents");
	    
	    
	    // Create the environment and agent
	    MazeEnvironment<SingleAgentMazeAgent> env    = new MazeEnvironment<>(cfg);
	    SingleAgentMazeAgent[]                agents = new SingleAgentMazeAgent[nAgents];
	    for( int i=0; i<nAgents; i++ )
	    	agents[i] = new SingleAgentMazeAgent(cfg);


	    // Open results file
	    //std::ofstream resFile;
	    //resFile.open(cfg->getString("RESULT_FILE"), std::ios::out | std::ios::trunc);

	    System.out.println("Starting Experiment");
	    long start = System.currentTimeMillis();
	    for( int runNo=0; runNo<totalRuns; runNo++ ) {
	        // initialise the environment and agent(s)
	        env.initialise();
	        for( int i=0; i<nAgents; i++ )
	        	agents[i].initialise();
	        // Add the agent into the environment
	        for( int i=0; i<nAgents; i++ )
	        	env.add(agents[i]);

	        // Output where we are up to
	        System.out.println("Beginning run #" + runNo);

	        for( int episodeNo=0; episodeNo<maxEpisodes; episodeNo++ ) {
	            // Reset the environment
	            env.reset(episodeNo);
	            int step;
	            for( step=0; step<maxSteps && !env.inTerminalState(); step++ ) {
	                env.incrementTime();
	            }
	            System.out.println("\tRun #" + (runNo+1) + "\tEpisode #" + (episodeNo+1) + "\tcompleted with " + step + "\tsteps");
	            //resFile   << step << std::endl;
	        }
	    }

	    System.out.println();
	    System.out.println("-- End of Experiment--");
	    long end = System.currentTimeMillis();
	    System.out.println("Experiment ran for " + (end-start) + "ms");
	    //resFile.close();
		
	}

}
