package marl.agents.learning.qlearning;

import marl.agents.learning.LearningAlgorithm;
import marl.agents.selection.Argmax;
import marl.environments.State;
import marl.utility.Config;


/**
 * Discrete Q-Learning is an abstract class which uses a discrete Q-Table to
 * store Q values for each state representation given to it and all actions
 * available in that state representation.
 *
 * The discrete nature of this learning algorithm is in actions; that is to say
 * that actions must be natural numbers not doubles. Furthermore the algorithm
 * looks at the values of actions not the indices and casts
 * them to an integer when learning about them.
 *
 * Note: This version of Q-Learning only can handle one action at a time to
 * learn about.
 * 
 * @author Pete Scopes
 * @version 06/09/2012
 */
public abstract class DiscreteQLearning<S extends State<S>>
	extends LearningAlgorithm<S>
{
    protected DiscreteQTable qTable;    // The Q table
    protected double         alpha;     // The learning rate
    protected double         gamma;     // The discount factor


    /**
     * Constructor for objects of class DiscreteQLearning
     */
    public DiscreteQLearning(Config cfg)
    {
        alpha = cfg.getDouble("alpha");
        gamma = cfg.getDouble("gamma");
        
        if( cfg.hasParam("num_states") ) {
            int nStates = cfg.getInt("num_states");
            if( nStates != -1 )
                qTable = new DiscreteQTable(nStates);
        }
        if( qTable == null )
            qTable = new DiscreteQTable();
        qTable.reset();
    }
    
    
    @Override
    public void update(S curState, S newState,
                       int action, double reward)
    {
        if( !evaluationMode ) {
            // Get the current Q value
        	double[] curQValues = qTable.get(curState);
        	
        	// Get the old and max Q values
            double   oldQ, newQ, maxQ = 0.0;
        	
        	if( newState != null ) {
                double[] newQValues = qTable.get(newState);
                maxQ    = newQValues[Argmax.select(newQValues)];
        	}
        	
            
            
            oldQ    = curQValues[action];
            
            newQ = oldQ + (alpha * (reward + (gamma*maxQ) - oldQ));
            
            qTable.put(curState, action, newQ);
        }
    }
    
    /**
     * Inform the q-learning of the available actions for the
     * specified state. This should be called as and when needed
     * to inform the learning algorithm that it is looking up a
     * state the the specified number of actions.
     * 
     * @param nActions The number of actions current available
     */
	@Override
    public void inform(int nActions)
    {
        super.inform(nActions);
        qTable.inform(nActions);
    }
	
	/**
	 * Returns the Q-table
	 * @return
	 */
	public DiscreteQTable table() {
		return this.qTable;
	}
}
