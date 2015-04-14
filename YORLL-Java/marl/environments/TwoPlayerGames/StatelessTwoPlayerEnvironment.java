/**
 * 
 */
package marl.environments.TwoPlayerGames;

import marl.agents.StatelessAgent;
import marl.environments.StatelessEnvironment;
import marl.utility.Config;


/**
 * Stateless Two Player environment requires the following in the configuration
 * file for it to work:
 * 
 * ## Stateless Two Player Environment settings
 * #  number of actions of players A and B
 * playerA_nActions = 2
 * playerB_nActions = 2
 * #  the payoff matrices for players A and B
 * playerA_payoffs  = 3,0, 0,2
 * playerB_payoffs  = 2,0, 0,3
 * 
 * 
 * @author pds
 * @since  2013-03-08
 *
 */
public class StatelessTwoPlayerEnvironment<A extends StatelessAgent<StatelessTwoPlayerEnvironment<A>>>
    extends StatelessEnvironment<A>
{
    /**
     * An extension on the StatelessEnvironment.Tuple to include the extra
     * information required by the Stateless Two Player environment.
     * @author pds
     * @since  2013-03-08
     */
    private class Tuple
        extends StatelessEnvironment.Tuple<StatelessTwoPlayerEnvironment<A>, A> {
        /**
         * The last action played by the agent.
         */
        public int action_;
    }
    
    
    /**
     * The Tuple to hold player A's information.
     */
    protected Tuple playerA_;
    /**
     * The Tuple to hold player B's information.
     */
    protected Tuple playerB_;
    /**
     * The configuration of the Puddle World environment.
     */
    protected Config cfg_;
    /**
     * The payoff matrix for both players.
     */
    protected StatelessPayoffMatrix payoffs_;

    
    
    
    /**
     * cfg The configuration for this Stateless Two Player environment
     */
    public StatelessTwoPlayerEnvironment(Config cfg)
    {
        cfg_ = cfg;
    }

    
    /* (non-Javadoc)
     * @see marl.environments.Environment#initialise()
     */
    @Override
    public void initialise() {
        // initialise the payoff matrix
        payoffs_ = new StatelessPayoffMatrix(cfg_);
        
        // initialise the player's tuples
        playerA_ = new Tuple();
        playerB_ = new Tuple();
    }

    
    /* (non-Javadoc)
     * @see marl.environments.Environment#add(marl.agents.Agent)
     */
    @Override
    public boolean add(A agent) {
        if( playerA_.agent_ == null ) {
            playerA_.agent_ = agent;
            playerA_.agent_.add(this);
            return true;
        }
        else if( playerB_.agent_ == null ) {
            playerB_.agent_ = agent;
            playerB_.agent_.add(this);
            return true;
        }
        return false;
    }

    
    /* (non-Javadoc)
     * @see marl.environments.Environment#getNumActions(marl.agents.Agent)
     */
    @Override
    public int getNumActions(A agent) {
        if( agent.equals(playerA_.agent_) )
            return payoffs_.getNumActions(StatelessPayoffMatrix.playerA);
        if( agent.equals(playerB_.agent_) )
            return payoffs_.getNumActions(StatelessPayoffMatrix.playerB);
        
        return 0;
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
        if( agent.equals(playerA_.agent_) )
            playerA_.action_ = action;
        if( agent.equals(playerB_.agent_) )
            playerB_.action_ = action;
        
    }

    
    /* (non-Javadoc)
     * @see marl.environments.Environment#incrementTime()
     */
    @Override
    public void incrementTime() {
        // Let the two players decide on their next action
        playerA_.agent_.step(time_);
        playerB_.agent_.step(time_);
        
        // Update
        playerA_.agent_.update(payoffs_.getPlayerAPayoff(playerA_.action_, playerB_.action_), true);
        playerB_.agent_.update(payoffs_.getPlayerBPayoff(playerA_.action_, playerB_.action_), true);
        
        time_++;
    }

    
    /**
     * @return The last reward for player A
     */
    public int getPlayerAReward() {
        return payoffs_.getPlayerAPayoff(playerA_.action_, playerB_.action_);
    }
    /**
     * @return The last reward for player B
     */
    public int getPlayerBReward() {
        return payoffs_.getPlayerBPayoff(playerA_.action_, playerB_.action_);
    }
    /**
     * @return The sum of the last rewards for players A and B
     */
    public int getSumReward() {
        return payoffs_.getPlayerAPayoff(playerA_.action_, playerB_.action_)
             + payoffs_.getPlayerBPayoff(playerA_.action_, playerB_.action_);
    }
}
