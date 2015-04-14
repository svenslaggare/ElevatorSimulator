package marl.agents.learning;

import marl.environments.State;


/**
 * LearningAlgorithm is an interface which describes how an agent should be able
 * to interact with learning algorithms in general. This allows for each agent
 * to learn individually or collectively (in the case of a centralised learner).
 * Learning algorithms should be able to:
 * 
 *  - Select an action based solely on the state provided
 *  - Update itself based upon the state it was in, the state it moved into,
 *    the action taken, and the received reward for said state transition
 *  - Be informed of the number of actions currently available to select from
 * 
 * The library provides a fair few different learning algorithms built in and
 * ready to use and has separated them out in such a way that customisations
 * should be easy.
 * For example: If your agent must decide upon which action to take and one
 * needs a magnitude you could create an extra method in your learning algorithm
 * to account for this extra information to be called after said action has
 * been selected.
 * 
 * @author Pete Scopes
 * @since 2012-09-06
 */
public abstract class LearningAlgorithm<S extends State<S>>
{
    /**
     * The number of actions currently available for the learning algorithm to
     * select from.
     */
    protected int     numActions;
    /**
     * This flag determines whether the learning algorithm is in evaluation
     * mode or not. When in evaluation mode the learning algorithm should cease
     * learning and follow the evaluation policy not the learning policy.
     */
    protected boolean evaluationMode;
    
    
    /**
     * Given the state this method should return the action that should be
     * performed. The number of actions available must be up-to-date.
     * 
     * @see LearningAlgorithm#inform(int)
     * @param state The state for which the action(s) are requested
     * @return The action selected
     */
    public abstract int select(S state);
    
    /**
     * Update the knowledge of the learning algorithm based upon the state it
     * was in (curState), the state it transitioned into (newState), the action
     * which was performed, and the received reward for the state transition.
     * 
     * @param curState The state the actions were performed
     * @param newState The state which was moved into, or null if now in terminal state
     * @param action   The action(s) performed
     * @param reward   The reward given for the state/action(s)
     */
    public abstract void update(S curState, S newState,
                                int action, double reward);
    
    
    
    /**
     * Informs the Learning Algorithm of the actions it may now choose between.
     * If the number of actions never change then the this may be called just
     * after initialisation and then never again; if it can change then it must
     * be called <em>before</em> the select method is called.
     * 
     * @see LearningAlgorithm#select(State)
     * @param nActions The number of actions available to select between
     */
    public void inform(int nActions)
    {
        numActions = nActions;
    }
    
    
    /**
     * Call this method when you wish to evaluate the learnt policy so far. This
     * will cause the agent to <em>stop learning</em> and follow the policy for
     * evaluation.
     * 
     * @param active True if the learning algorithm should be in evaluation
     *               mode, False otherwise
     */
    public void evaluationMode(boolean active) {
        evaluationMode = active;
    }
}
