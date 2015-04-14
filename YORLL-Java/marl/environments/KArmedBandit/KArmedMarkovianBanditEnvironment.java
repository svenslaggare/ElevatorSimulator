/**
 * 
 */
package marl.environments.KArmedBandit;

import marl.agents.Agent;
import marl.environments.*;
import marl.utility.Config;


/**
 * <p>Defines an environment for the stateful k-armed bandit problem:
 * <p>There are k independent slot machines.
 * <p>Each machine gives reward according to a different, unknown markov process.
 * <p>The agent should decide, each time, which slot-machine to pull.
 * 
 * 
 * <p>K-Armed Markovian Bandit environment requires the following the
 * configuration file for it to work: 
 * 
 * ## K-Armed Markovian Bandit Environment settings
 * #  the slot-machine data - each machine rewards according to a normal distribution:
 * means = 100,200,300,400
 * stds = 10,10,10,10
 * 
 *
 * @author Erel Segal the Levite
 * @since 2012-12-12
 */
public class KArmedMarkovianBanditEnvironment<A extends Agent<KArmedMarkovianBanditEnvironment<A>>>
    implements Environment<KArmedMarkovianBanditState, A>
{
    /**
     * An extension on the Environment.Tuple to include the extra information
     * required by the K-armed Markovian bandit environment.
     * @author pds
     * @since  2013-03-08
     */
    private class Tuple
        extends Environment.Tuple<KArmedMarkovianBanditEnvironment<A>,
                                  KArmedMarkovianBanditState, A> {
        /**
         * The last action played by the agent.
         */
        public int action_;
    }
    
    /**
     * The Tuple to hold the single agent information.
     */
    protected Tuple tuple_;
    /**
     * The time of the environment in the current episode.
     */
    protected int   time_;
    /**
     * The maximum alloted time allowed within an episode.
     */
    protected int   maxTime_;
    /**
     * The configuration of the K-Armed Markovian Bandit environment.
     */
    protected Config cfg_;
    /**
     * The reward machine to generate the rewards for the players actions.
     */
    protected RewardMarkovianProcesses machines_;
    
    

    /**
     * @param cfg The configuration for the K-Armed Markovian Bandit
     *            environment.
     */
	public KArmedMarkovianBanditEnvironment(Config cfg)
	{
		cfg_ = cfg;
	}

	
	/* (non-Javadoc)
     * @see marl.environments.Environment#initialise()
     */
	@Override
	public void initialise() {
	    // initialise the reward machine
		machines_ = new RewardMarkovianProcesses(cfg_);
		
		// set the maximum time
		maxTime_  = cfg_.getInt("maxTime");
		
        // reset the environment
        time_             = 0;
        tuple_.sumReward = 0.0d;
		
		// initialise the agent tuple
		tuple_    = new Tuple();
	}

	
	/* (non-Javadoc)
     * @see marl.environments.Environment#reset(int)
     */
	@Override
	public void reset(int episodeNo) {
	    // reset the agent
		tuple_.agent.reset(episodeNo);
	}

	
	/* (non-Javadoc)
     * @see marl.environments.Environment#add(marl.agents.Agent)
     */
	@Override
	public boolean add(A agent) {
		if( tuple_.agent == null ) {
		    tuple_.agent = agent;
		    tuple_.agent.add(this);
			return true;
		}
		return false;
	}

	
	/* (non-Javadoc)
     * @see marl.environments.Environment#getState(marl.agents.Agent)
     */
    @Override
    public KArmedMarkovianBanditState getState(A agent) {
        return new KArmedMarkovianBanditState(machines_.getStates());
    }

    
    /* (non-Javadoc)
     * @see marl.environments.Environment#getNumActions(marl.agents.Agent)
     */
	@Override
	public int getNumActions(A agent) {
		return machines_.getNumActions();
	}

	
	/* (non-Javadoc)
     * @see marl.environments.Environment#inTerminalState()
     */
	@Override
	public boolean inTerminalState() {
		return time_ > maxTime_;
	}

	
	/* (non-Javadoc)
     * @see marl.environments.Environment#performAction(marl.agents.Agent, int)
     */
	@Override
	public void performAction(A agent, int action) {
		if( agent.equals(tuple_.agent) )
			tuple_.action_ = action;
	}

	
	/* (non-Javadoc)
     * @see marl.environments.Environment#incrementTime()
     */
	@Override
	public void incrementTime() {
		// Let the player decide on his next action
		tuple_.agent.step(time_);

		// calculate the random reward
		double reward = machines_.getReward(tuple_.action_);

		// Update
		tuple_.agent.update(reward, true);
		tuple_.sumReward += reward;

		time_++;
	}
}
