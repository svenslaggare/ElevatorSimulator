package marl.agents;

import marl.agents.learning.LearningAlgorithm;
import marl.agents.selection.Argmax;
import marl.agents.selection.Exploration;
import marl.environments.Environment;


/**
 * An Agent is an abstract class that must be extended on a per-experiment
 * basis. An agent should contain all the logic for the agent to learn and
 * select actions. There are pre-built learning algorithms which may be used
 * directly or modified through extension, likewise for action selection
 * algorithms.
 * 
 * If the learning algorithms that are contained within the library are not
 * what is required then it is recommended that you implement your own using
 * the {@link LearningAlgorithm} interface. The same should be said for the
 * the {@link Exploration} interface for action selection.
 * NB. Exceptions can be made, see {@link Argmax} for example.
 * 
 * @author Pete Scopes
 * @version 06/09/2012
 */
public abstract class Agent<E extends Environment<?, ?>>
{
    /**
     * Stores the environment of the exact class type used
     */
    protected E environment;
    

    /**
     * Initialise the agent so that it is ready to be added into an environment.
     * This process should include:
     *  - selecting a learning algorithm
     *  - selecting an action selection algorithm
     */
    public abstract void initialise();
    
    /**
     * Reset the agent so that it is ready to restart the episode of the
     * specified number.
     * 
     * An agent <em>should</em> also perceive its initial state in the
     * environment at this point.
     * @param episodeNo The current episode number
     */
    public abstract void reset(int episodeNo);
    
    /**
     * To be called by the environment when the agent is added so that the
     * agent can reference to the environment.
     * @param env The environment the agent is in
     */
    public void add(E env)
    {
        environment = env;
    }
    
    /**
     * Calls in turn reason and then act. Perception of the environment
     * should happen immediately when the agent is called to reset so that
     * is aware of its starting state and during the update call so that
     * it knows which state it has arrived in before it updates.
     * @param time The current time step
     */
    public void step(int time)
    {
        // reason about the next action to take
        reason(time);
        // perform the action
        act();
    }
    
    /**
     * Called by the Environment once the action has been performed
     * and thus the reward has been confirmed. This should update
     * the learning algorithm with the reward given for the latest
     * action. The agent should also perceive its environment at this
     * point to see what it's latest action did.
     * @param reward   The reward obtained for the last action performed
     * @param terminal True if this is the terminal update, otherwise false
     */
    public abstract void update(double reward, boolean terminal);
    
    
    /**
     * Perceives the current state of the environment from the point-of-view of
     * this agent and returns that state.
     */
    protected abstract void perceive();
    
    /**
     * Reason about the current state of itself and the environment and decide
     * what action should be taken, if any.
     * @param time The current time
     */
    protected abstract void reason(int time);
    
    /**
     * Performs the action that was decided whilst the agent was reasoning.
     */
    protected abstract void act();
}
