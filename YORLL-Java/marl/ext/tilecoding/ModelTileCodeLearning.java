/**
 * 
 */
package marl.ext.tilecoding;

import marl.agents.learning.DiscreteValueTable;
import marl.agents.learning.LearningAlgorithm;
import marl.agents.selection.Argmax;
import marl.agents.selection.EGreedy;
import marl.environments.Model;
import marl.utility.Config;


/**
 * @author pds
 *
 */
public class ModelTileCodeLearning<S extends TileCodingState<S>> extends
        LearningAlgorithm<S>
{
    private Model<S>           model_;
    private DiscreteValueTable vTable_;
    private double             alpha_,
                               gamma_;
    private int                nTilings_;
    private EGreedy            eGreedy_;    // The Egreedy selection algorithm
    private TileCoding         tileCoding_;

    /**
     * 
     */
    public ModelTileCodeLearning(Config cfg, TileCodingEnvironment<?, ?> env)
    {
        alpha_      = cfg.getDouble("alpha");
        gamma_      = cfg.getDouble("gamma");
        nTilings_   = cfg.getInt("num_tilings");
        int nStates = cfg.getInt("num_states");

        tileCoding_ = new TileCoding(cfg, env);
        if( nStates == -1 )
            vTable_ = new DiscreteValueTable();
        else
            vTable_ = new DiscreteValueTable(nStates);
        
        eGreedy_ = new EGreedy(cfg);
    }
    
    

    /**
     * Set the environment model
     */
    public void setEnvironmentModel(Model<S> model)
    {
        model_ = model;
    }


    /* (non-Javadoc)
     * @see marl.agents.learning.LearningAlgorithm#select(marl.environments.State)
     */
    @Override
    public int select(S state)
    {
        // get the V(S) values
        double[] values = new double[numActions];
        
        // get the activated tiles
        Tile[]   tiles  = new Tile[nTilings_];
        // initialise an array for the values
        double   weight;
        
        
        for( int i=0; i<numActions; i++ ) {
            Model.Sample<S> sample = model_.getSample(state, i);
            tileCoding_.getTiles(tiles, sample.next);
            weight = sumWeights(tiles);
            
            values[i]  = sample.reward;
            values[i] += gamma_ * weight;
        }
        if( evaluationMode )
            return Argmax.select(values);
        else
            return eGreedy_.select(values);
    }
    
    /**
     * Decreases the value of epsilon in the Egreedy selection algorithm.
     * @param episodeNo The episode number
     */
    public void decreaseEpsilon(int episodeNo)
    {
        eGreedy_.decreaseEpsilon(episodeNo);
    }


    /* (non-Javadoc)
     * @see marl.agents.learning.LearningAlgorithm#update(marl.environments.State, marl.environments.State, int, double)
     */
    @Override
    public void update(S curState, S newState, int action, double reward)
    {
        if( evaluationMode ) {
            double target = reward,
                   weight;
            
            if( newState != null ) {
                // calculate the target
                Tile[] newTiles = new Tile[nTilings_];
                tileCoding_.getTiles(newTiles, newState);
                weight = sumWeights(newTiles);
                
                target += gamma_ * weight;
            }
            
            // Get all the tiles of the current
            Tile[] curTiles = new Tile[nTilings_];
            tileCoding_.getTiles(curTiles, curState);
            weight = sumWeights(curTiles);
            
            double error  = target - weight;
            
            // update each tile giving each a portion of the total reward
            for( int i=0; i<nTilings_; i++ )
                updateTile(error, curTiles[i]);
        }
    }



    private double sumWeights(Tile[] tiles) {
        double weight;
        weight = 0.0;
        for( int i=0; i<nTilings_; i++ )
            weight += vTable_.get(tiles[i]);
        return weight;
    }
    private void updateTile(double error, Tile tile) {
        double weight = vTable_.get(tile);
//        double error  = target - weight;
        vTable_.put(tile, weight + ((alpha_/(double)nTilings_) * error));
    }



    
    /**
     * @return The number of tiles being learnt about
     */
    public int getNoTiles() {
        return tileCoding_.getNoTiles();
    }
}
