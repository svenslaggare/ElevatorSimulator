/**
 * 
 */
package marl.ext.tilecoding;

import marl.agents.learning.LearningAlgorithm;
import marl.agents.learning.qlearning.DiscreteQTable;
import marl.agents.selection.Argmax;
import marl.agents.selection.EGreedy;
import marl.utility.Config;

/**
 * @author pds
 * 
 * TODO make sure this is working properly, compare to ModelTileCoding
 *
 */
public class TileCodeLearning<S extends TileCodingState<S>>
	extends LearningAlgorithm<S>
{
	private DiscreteQTable qTable_;       // the Q table
	private double         alpha_,        // the learning rate
						   gamma_;        // the discount factor
	private int            nTilings_;     // number of tilings
	private EGreedy        eGreedy_;      // The Egreedy selection algorithm
	private TileCoding     tileCoding_;   // the tile coding

	/**
	 * 
	 */
	public TileCodeLearning(Config cfg, TileCodingEnvironment<?, ?> env)
	{
	    alpha_      = cfg.getDouble("alpha");
	    gamma_      = cfg.getDouble("gamma");
        nTilings_   = cfg.getInt("num_tilings");
	    int nStates = cfg.getInt("num_states");;

        tileCoding_ = new TileCoding(cfg, env);
	    if( nStates == -1 )
	        qTable_ = new DiscreteQTable();
	    else
	        qTable_ = new DiscreteQTable(nStates);
	    qTable_.reset();

	    eGreedy_ = new EGreedy(cfg);
	}
	
	
	/* (non-Javadoc)
     * @see marl.agents.learning.LearningAlgorithm#select(marl.environments.State)
     */
	@Override
	public int select(S state)
	{
	    // Get all the tiles of this state representation
	    Tile[] tiles = new Tile[nTilings_];
	    tileCoding_.getTiles(tiles, state);
	    
	    // initialise a container for the sum of the q values
	    // and sum them
	    double[] qValues     = new double[numActions],
	    	     tileQValues;
	    for( int k=0; k<numActions; k++ )
	        qValues[k] = 0;

	    for( int j=0; j<nTilings_; j++ ) {
	        tileQValues = qTable_.get(tiles[j]);
	        for( int k=0; k<numActions; k++ )
	            qValues[k] += tileQValues[k];
	    }
		    
	
	    // select the best action
	    if( evaluationMode )
            return Argmax.select(qValues);
	    else
	        return eGreedy_.select(qValues);
	}
	
	/**
	 * Decreases the value of epsilon in the Egreedy selection algorithm.
	 * @param episodeNo The episode number
	 */
	public void decreaseEpsilon(int episodeNo)
	{
		eGreedy_.decreaseEpsilon(episodeNo);
	}
	
	
	@Override
	public void inform(int nActions)
	{
	    super.inform(nActions);
		qTable_.inform(nActions);
	}

	/* (non-Javadoc)
     * @see marl.agents.learning.LearningAlgorithm#update(marl.environments.State, marl.environments.State, int, double)
     */
	@Override
	public void update(
			S curState, S newState,
			int action, double reward)
	{
	    if( !evaluationMode ) {
            // Get all the tiles for the current state
            Tile[] curStates = new Tile[nTilings_];
            tileCoding_.getTiles(curStates, curState);
            
            // get the current Q values
            double newQ[] = new double[nTilings_];
            
            
    	    if( newState != null ) {
        	    // Get all the tiles of the new states
        	    Tile[] newStates = new Tile[nTilings_];
        	    tileCoding_.getTiles(newStates, newState);
        	    
        	    for( int i=0; i<nTilings_; i++ ) {
                    // Get the new states' Q values
                    double[] newQValues = qTable_.get(newStates[i]);
                             newQ[i]    = newQValues[eGreedy_.select(newQValues)];
        	    }
    	    } else {
    	        for( int i=0; i<nTilings_; i++ )
    	            newQ[i] = 0.0;
    	    }
    	    
    	    for( int i=0; i<nTilings_; i++ ) {
    	        double curQ  = qTable_.get(curStates[i])[action];
    	        double val   = curQ + (( alpha_ * (reward + (gamma_*newQ[i]) - curQ)) / (double)nTilings_);
    	        qTable_.put(curStates[i], action, val);   // commit the update to the Q table
    	    }
	    }
	}
    
	/**
	 * @return The number of tiles being learnt about
	 */
    public int getNoTiles() {
        return tileCoding_.getNoTiles();
    }
	
}
