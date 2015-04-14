package marl.agents.learning.sarsa;

import marl.agents.learning.LearningAlgorithm;
import marl.agents.learning.qlearning.DiscreteQTable;
import marl.environments.State;
import marl.utility.Config;

public abstract class DiscreteSarsa<S extends State<S>>
	extends LearningAlgorithm<S>
{
    protected DiscreteQTable qTable;    // The Q table
    protected double         alpha;     // The learning rate
    protected double         gamma;     // The discount factor
    protected int            nActions;  // The number of actions
    
    protected S              potentialState;
    protected int            potentialAction;
                                         // The next action
	
	
	public DiscreteSarsa(Config cfg)
	{
		alpha = cfg.getDouble("alpha");
		gamma = cfg.getDouble("gamma");
        
        {
        	try{ 
        		int nStates = cfg.getInt("num_states");
        		qTable = new DiscreteQTable(nStates);
        	} catch(NumberFormatException ex) {
        		qTable = new DiscreteQTable();
        	}
                
            qTable.reset();
        }
        
        potentialState  = null;
        potentialAction = -1;
	}
	
	
	@Override
	public final int select(S state) {
	    // make sure that the select function aligns with the update function
	    if( potentialState == null || !state.equals(potentialState) )
	        return _select(state);
	    else
	        return potentialAction;
	}
	abstract protected int _select(S state);
	


	/**
	 * Note: Select *must* have been called before update otherwise action_
	 *       will not have been set.
	 */
	@Override
	public void update(
			S curState, S newState,
			int action, double reward)
	{
	    if( !evaluationMode ) {
    		double[] curQValues = qTable.get(curState);
    
            // Get the old and max Q values
            double   oldQ, newQ, nextQ = 0.0;
    		
    		if( newState != null ) {
    		    double[] newQValues = qTable.get(newState);
    		    potentialState     = newState;
    		    potentialAction    = _select(newState);
    	        nextQ               = newQValues[potentialAction];
    		}
    		else
    		    potentialState     = null;
            
            oldQ = curQValues[action];
            
            newQ = oldQ + (alpha * (reward + (gamma*nextQ) - oldQ));
            
            qTable.put(curState, action, newQ);
	    }
	}
	
	

    /**
     * Inform the SARSA of the available actions for the
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
