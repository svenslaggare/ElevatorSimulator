/**
 * 
 */
package marl.environments.Swarms;

import java.util.HashMap;

import marl.agents.Agent;
import marl.environments.Environment;
import marl.utility.Config;

/**
 * @author pds
 * @since  2013-07-04
 *
 */
public class SwarmEnvironment<E, A extends Agent<SwarmEnvironment<E,A>>>
    implements Environment<SwarmState<E>, A> {
    
    protected class SwarmTuple extends Environment.ModelTuple<SwarmEnvironment<E,A>, SwarmState<E>, A> {
        /**
         * Called when the environment is reset.
         */
        public void reset(int episodeNo) {
            //TODO reset position
            agent.reset(episodeNo);
        }
    }
    
    /**
     * The actions that are available in the Swarm Environment.
     */
    protected static final SwarmAction[] envActions_ = SwarmAction.values();
    
    /**
     * The current time of the environment in the current episode.
     */
    protected int time_;
    /**
     * The configuration of the Swarm Environment.
     */
    protected Config cfg_;
    /**
     * The number of agents in the Swarm.
     */
    protected int    nAgents_;
    
    /**
     * The collection of agents in the Swarm.
     */
    protected HashMap<Integer, SwarmEnvironment<E, A>.SwarmTuple> tuples_;
    
    /**
     * 
     */
    public SwarmEnvironment(Config cfg) {
        cfg_ = cfg;
    }
    
    
    
    @Override
    public void initialise() {
        nAgents_ = cfg_.getInt("swarm.nAgents");
        tuples_  = new HashMap<>(nAgents_);
    }
    
    @Override
    public void reset(int episodeNo) {
        // Reset the environment
        for( SwarmTuple tuple: tuples_.values() )
            tuple.reset(episodeNo);
        
        time_ = 0;
    }
    
    @Override
    public boolean add(A agent) {
        if( tuples_.size() < nAgents_ ) {
            SwarmTuple tuple = new SwarmTuple();
            tuple.agent = agent;
            tuple.agent.add(this);
            tuples_.put(agent.hashCode(), tuple);
            return true;
        }
        return false;
    }
    
    @Override
    public SwarmState<E> getState(A agent) {
        SwarmTuple tuple = tuples_.get(agent.hashCode());
        if( tuple != null )
            return tuple.state;
        return null;
    }
    
    @Override
    public int getNumActions(A agent) {
        return envActions_.length;
    }
    
    @Override
    public boolean inTerminalState() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public void performAction(A agent, int action) {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see marl.environments.Environment#incrementTime()
     */
    @Override
    public void incrementTime() {
        // TODO Auto-generated method stub
        
    }
    
}
