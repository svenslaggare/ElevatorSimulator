package marl.environments;

import marl.agents.Agent;


/**
 * Env (environment) is an abstract class that must be extended in your own
 * experiment. An environment should contain all the logic for interactions of
 * agents; this could mean this is a wrapper class to connect the agents to an
 * external system or actually contain the modeling code itself.
 * 
 * Please see the abstract methods for further information.
 * 
 * @author Pete Scopes
 * @version 06/09/2012
 */
public interface Environment<S extends State<?>, A extends Agent<?>>
{
    /**
     * Initialises the Environment ready for agents to be added.
     * This includes such actions as removing all current agents.
     * In this way this method should be able to be called more
     * than once, i.e. at the beginning of each run.
     */
    void initialise();
    
    /**
     * Resets the Environment back to the initial conditions but
     * doesn't remove agents. This method is to be called in between
     * episodes.
     * @param episodeNo The current episode number
     */
    void reset(int episodeNo);
    
    /**
     * Adds an agent into the environment. If the environment is going
     * successfully add the agent it must also call the {@link Agent#add(Environment)}
     * method so that the agent knows the environment it has been added
     * to.
     * @param agent The agent to be added into the Environment
     * @return True if the agent was successfully added
     */
    boolean add(A agent);
    
    /**
     * Gets the state of the Environment as seen by the specified agent.
     * @param agent The agent wishing to perceive the environment
     * @return The state of the Env
     */
    S getState(A agent);
    
    /**
     * Given an agent this method returns the number of possible actions the
     * agent may perform in its current state. Since some environments
     * may allow different actions depending on the state this method
     * allows for agents to grab that information at anytime. It could
     * be the case that the set of actions never change, if that is the case
     * then this method will only be called when agents are being added.
     * @param agent The agent requiring its actions
     * @return The number of actions currently available
     */
    int getNumActions(A agent);
    
    /**
     * Tests the environment to see if the terminal state has been reached.
     * @return True if the environment is in a goal state
     */
    boolean inTerminalState();
    
    /**
     * Given an Agent and an action this function makes note of the
     * requested action and when all agents have specified their
     * intentions the environment will attempt all actions and
     * give each agent its reward. This allows for the environment to
     * handle conflict resolution, e.g. in a grid world two agents may not
     * be allowed to occupy the same position and therefore one, or both, 
     * agents may be denied their requested action.
     * @param agent   The agent to perform the action
     * @param action  The action to be taken as a vector
     */
    void performAction(A agent, int action);
    
    /**
     * This method should firstly call each agent that has been added to
     * perform a step, which in turn should notify the environment what
     * its intended action is. The environment can decide upon the ordering.
     * Once all agents have performed their step the environment should
     * perform any conflict resolution; conflicts can be resolved in the
     * following ways:
     *  - mutually  = decline all agents involved in the conflict
     *  - orderedly = decline agents in a specified order, such as priority
     *  - randomly  = decline agents in a random order
     *
     * Finally this method should inform the agent(s) of their reward for
     * the state->action->state transition.
     *
     * Note: That if the agent has reached a terminal state then the method
     * should also call the agents update terminal method with its reward
     * for entering the goal.
     *
     * Note: The time counter should be incremented each step.
     * state.
     */
    void incrementTime();
    
    
    
    /**
     * A Generic container, to be extended when necessary, to hold an
     * environment's information for a particular agent.
     * @author Scopes
     * @since  2013-03-07
     *
     * @param <E> The Environment
     * @param <S> The State
     * @param <A> The Agent
     */
    public static class Tuple<E extends Environment<S,A>, S extends State<?>, A extends Agent<E>> {
        /**
         * The Agent of this Tuple.
         */
        public A agent;
        /**
         * The state associated with this agent, that is the state of the
         * environment the agent can currently perceive.
         */
        public S state;
        /**
         * The a state to be used to store the potential next state of an agent
         * before the environment fully commits to the state transition.
         */
        public S next;
        /**
         * The sum total reward the agent has received in the current episode.
         */
        public double sumReward;
        /**
         * The last reward the agent has received.
         */
        public double lastReward;
        
        /**
         * Adds a reward to the agent.
         * @param reward The reward to be added
         */
        public void addReward(double reward) {
            sumReward += (lastReward = reward);
        }
    }
    /**
     * A Generic container, to be extended when necessary, to hold an
     * environments information for a particular agent including the model
     * sample.
     * @author Scopes
     * @since  2013-03-07
     *
     * @param <S>
     * @param <A>
     */
    public static class ModelTuple<E extends Environment<S,A>, S extends State<S>, A extends Agent<E>>
        extends Tuple<E,S,A> {
        /**
         * The last sample action for the agent of this Tuple.
         */
        public Model.Sample<S> sample_;
    }
}
