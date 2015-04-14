/**
 * 
 */
package marl.environments.KArmedBandit;

import marl.agents.StatelessAgent;
import marl.environments.StatelessEnvironment;
import marl.utility.Config;


/**
 * <p>Defines an environment for the k-armed bandit problem:
 * <p>There are k independent, stateless slot machines.
 * <p>Each machine gives reward according to a different, unknown probability distribution.
 * <p>The agent should decide, each time, which slot-machine to pull.
 * 
 * 
 * <p>K-Armed Bandit environment requires the following the configuration file
 * for it to work:
 * 
 * ## K-Armed Bandit Environment settings
 * #  the slot-machine data - each machine rewards according to a normal distribution:
 * means = 101,102,103,104
 * stds  = 10,10,10,10
 *
 * @author Erel Segal the Levite
 * @since  2012-12-12
 * 
 */
public class KArmedBanditEnvironment<A extends StatelessAgent<KArmedBanditEnvironment<A>>>
    extends StatelessEnvironment<A>
{
    /**
     * An extension on the StatelessEnvironment.Tuple to include the extra
     * information required by the K-armed bandit environment.
     * @author pds
     * @since  2013-03-08
     */
    private class Tuple
        extends StatelessEnvironment.Tuple<KArmedBanditEnvironment<A>, A> {
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
     * The configuration of the K-Armed Bandit environment.
     */
    protected Config cfg_;
    /**
     * The reward machine to generate the rewards for the players actions.
     */
    protected RewardProbabilityDistributions machines_;
    
    
    
    /**
     * @param cfg The configuration for the K-Armed Bandit
     *            environment.
     */
	public KArmedBanditEnvironment(Config cfg)
	{
		cfg_ = cfg;
	}

	
	/* (non-Javadoc)
     * @see marl.environments.Environment#initialise()
     */
	@Override
	public void initialise() {
		machines_ = new RewardProbabilityDistributions(cfg_);
	}

	
	/* (non-Javadoc)
     * @see marl.environments.Environment#reset(int)
     */
	@Override
	public void reset(int episodeNo) {
	    // call the parent class reset
		super.reset(episodeNo);

        // reset the agent
        tuple_.agent_.reset(episodeNo);
	}

	
	/* (non-Javadoc)
     * @see marl.environments.Environment#add(marl.agents.Agent)
     */
	@Override
	public boolean add(A agent) {
		if( tuple_.agent_ == null ) {
		    tuple_.agent_ = agent;
		    tuple_.agent_.add(this);
			return true;
		}
		return false;
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
		return time_ > 0;
	}

	
	/* (non-Javadoc)
     * @see marl.environments.Environment#performAction(marl.agents.Agent, int)
     */
	@Override
	public void performAction(A agent, int action) {
		if( agent.equals(tuple_.agent_) )
			tuple_.action_ = action;
	}

	
	/* (non-Javadoc)
     * @see marl.environments.Environment#incrementTime()
     */
	@Override
	public void incrementTime() {
		// Let the player decide on his next action
	    tuple_.agent_.step(time_);

		// calculate the random reward
		double reward = machines_.getReward(tuple_.action_);

		// Update
		tuple_.agent_.update(reward, true);
		tuple_.addReward(reward);

		time_++;
	}
}
