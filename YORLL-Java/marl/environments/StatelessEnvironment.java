/**
 * 
 */
package marl.environments;

import marl.agents.StatelessAgent;


/**
 * <p>Defines an environment with a single state - "Stateless" - and an integer
 * time.</p>
 * <p>If you extend this environment, you don't have to implement the "getState"
 * method, as the state is the same each time.</p>
 * @author Erel Segal the Levite
 * @since 2012-12-09
 */
public abstract class StatelessEnvironment<A extends StatelessAgent<?>> implements
        Environment<Stateless, A>
{
    /**
     * The stateless state.
     */
    protected final Stateless state_ = new Stateless();
    /**
     * The current time of the environment in the current episode.
     */
    protected int             time_;

    @Override public abstract void initialise();

    @Override public void reset(int episodeNo) {
        time_ = 0;
    }

    @Override public abstract boolean add(A agent);

    @Override public Stateless getState(A agent) {
        return state_;
    }

    @Override public abstract int getNumActions(A agent);
    
    @Override public abstract boolean inTerminalState();

    @Override public abstract void performAction(A agent, int action);

    @Override public void incrementTime() {
        time_++;
    }
    
    
    
    /**
     * A Generic container, to be extended when necessary, to hold an
     * environment's information for a particular agent.
     * @author pds
     * @since  2013-03-08
     *
     * @param <E> The Environment
     * @param <A> The Agent
     */
    public static class Tuple<E extends StatelessEnvironment<A>, A extends StatelessAgent<E>> {
        /**
         * The Agent of this Tuple.
         */
        public A agent_;
        /**
         * The sum total reward the agent has received in the current episode.
         */
        public double sumReward_;
        /**
         * The last reward the agent has received.
         */
        public double lastReward_;
        
        /**
         * Adds a reward to the agent.
         * @param reward The reward to be added
         */
        public void addReward(double reward) {
            sumReward_ += (lastReward_ = reward);
        }
    }
}
