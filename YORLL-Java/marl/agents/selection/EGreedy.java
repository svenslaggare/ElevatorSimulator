package marl.agents.selection;

import marl.utility.Config;
import marl.utility.Rand;


/**
 * E-Greedy (Epsilon-Greedy) is an implementation of the E-Greedy action
 * selection policy.
 *
 * The E-Greedy policy is to be greedy (select the Argmax action) with
 * probability 1-epsilon and then with epsilon probability select a random
 * action which could be the {@link Argmax} action.
 * 
 * @author Pete Scopes 
 * @version 06/09/2012
 */
public class EGreedy
    extends Decay
{
    /*
    double   epsilon_,                 // Probability of a random action selection
             minEpsilon_;              // Minimum value epsilon can take
    int      maxEpisodes_;             // Maximum number of episodes
    boolean  shouldDecrease_;          // 1 if decreasing epsilon, 0 otherwise
    
    double   localEpsilon_,            // The value epsilon currently has
             decreaseAmount_;          // Computed epsilon decrease amount per episode
    int      decreaseStart_,           // The episode to start decreasing epsilon
             decreaseOver_;            // The number of episodes to decrease epsilon over
    */
    
    //
    double epsilon;


    /**
     * Constructor for objects of class EGreedy
     */
    public EGreedy(Config cfg)
    {
        super(Decay.Type.CONSTANT);
        epsilon = cfg.getDouble("epsilon");
        super.setMax(epsilon);
        
        //Add extras to the decay function
        if( cfg.getBoolean("epsilon_should_decay") ) {
            //Set type
            if( cfg.hasParam("epsilon_decay") )
                super.setType(Decay.Type.valueOf(cfg.getString("epsilon_decay")));
            //Set over
            if( cfg.hasParam("epsilon_decay_over") ) {
                double over = cfg.getDouble("epsilon_decay_over");
                if( over < 0 )
                    over = cfg.getInt("max_episodes");
                super.setOver(over);
                super.setL(over);
            }
            //Set minimum
            if( cfg.hasParam("epsilon_decay_minimum") )
                super.setMin(cfg.getDouble("epsilon_decay_minimum"));
            //Set start
            if( cfg.hasParam("epsilon_decay_start") )
                super.setStart(cfg.getDouble("epsilon_decay_start"));
            
            //Set L
            if( cfg.hasParam("epsilon_decay_L") )
                super.setL(cfg.getDouble("epsilon_decay_L"));
            //Set k
            if( cfg.hasParam("epsilon_decay_k") )
                super.setK(cfg.getDouble("epsilon_decay_k"));
        }
        
        
        /*
        epsilon_        = cfg.getDouble("epsilon");
        maxEpisodes_    = cfg.getInt("max_episodes");
        shouldDecrease_ = cfg.getInt("should_decrease") != 0;
        
        
        localEpsilon_   = epsilon_;
        minEpsilon_     = cfg.getDouble("min_epsilon");
        decreaseStart_  = cfg.getInt("decrease_start");
        decreaseOver_   = cfg.getInt("decrease_over");
        decreaseAmount_ = epsilon_ / ((double)decreaseOver_<0 ? ((double)maxEpisodes_ - (double)decreaseStart_) : (double)decreaseOver_);
        */
    }
    
    
    /**
     * Selects an action using the E-greedy selection mechanism.
     * @param stateActionPairs The actions, with their values, to select from
     * @return The E-greedy action selection
     */
    @Override
    public int select(double[] stateActionPairs)
    {
     // with epsilon probability choose a random action
        if (epsilon > Rand.INSTANCE.nextDouble()) {
        	int action = Rand.INSTANCE.nextInt(stateActionPairs.length);
//        	System.out.println("\tRandom action: " + action);
            return action;
        } else {
            return Argmax.select(stateActionPairs);
        }
        
        /*
        // with epsilon probability choose a random action
        if( localEpsilon_ > Rand.INSTANCE.nextDouble() )
            return Rand.INSTANCE.nextInt(stateActionPairs.length);
        else
            return Argmax.select(stateActionPairs);
            */
    }
    
    
    /**
     * This method sets the epsilon value based upon the settings in the
     * configuration file and the episode number given.
     * 
     * @param episodeNo The episode number
     */
    public void decreaseEpsilon(int episodeNo)
    {
        epsilon = super.decay(episodeNo);
        
        /*
        if( shouldDecrease_ )               // on/off switch
        {
            if( episodeNo >= decreaseStart_ )
            {
                if( localEpsilon_ > minEpsilon_ )   // decrease the epsilon value
                    localEpsilon_ = epsilon_ - ((episodeNo-decreaseStart_) * decreaseAmount_);
                if( localEpsilon_ <= minEpsilon_ )  // ensure it is above zero
                    localEpsilon_  = minEpsilon_;   // (as two if statements to stop rounding errors!)
            }
        }
        */
    }
}
