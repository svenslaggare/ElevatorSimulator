package marl.agents.selection;

import marl.utility.Config;
import marl.utility.Rand;


/**
 * Boltzmann is an implementation of Soft Max action selection policy.
 *
 * The Boltzmann policy is to each time give a probability to to all actions
 * that may happen but weight them such that the larger their Q-Value the more
 * probable they are to be picked but also allow a temperature, tau, that should
 * decrease over time which makes actions selection more random the "hotter"
 * it is.
 * 
 * @author Pete Scopes
 * @version 06/09/2012
 */
public class Boltzmann
    extends Decay
{
    /*
    private double tau_,             // The temperature parameter
                   minTau_;          // The minimum value of tau
    private int    maxEpisodes_,     // The maximum number of episodes
                   shouldDecrease_;  // 1 if decreasing tau, 0 otherwise

    private double localTau_,        // The value tau currently has
                   decreaseAmount_;  // Computed amount tau should decrease episode
    private int    decreaseStart_,   // The episode to start decreasing tau
                   decreaseOver_;    // The number of episodes to decrease tau over
    */
    
    //
    private double tau;

    /**
     * Constructor for objects of class SoftMax
     */
    public Boltzmann(Config cfg)
    {
        super(Decay.Type.CONSTANT);
        tau = cfg.getDouble("tau");
        super.setMax(tau);
        
        //Add extras to the decay function
        if( cfg.getBoolean("tau_should_decay") ) {
            //Set type
            if( cfg.hasParam("tau_decay") )
                super.setType(Decay.Type.valueOf(cfg.getString("tau_decay")));
            //Set over
            if( cfg.hasParam("tau_decay_over") ) {
                double over = cfg.getDouble("tau_decay_over");
                if( over < 0 )
                    over = cfg.getInt("max_episodes");
                super.setOver(over);
                super.setL(over);
            }
            //Set minimum
            if( cfg.hasParam("tau_decay_minimum") )
                super.setMin(cfg.getDouble("tau_decay_minimum"));
            //Set start
            if( cfg.hasParam("tau_decay_start") )
                super.setStart(cfg.getDouble("tau_decay_start"));
            
            //Set L
            if( cfg.hasParam("tau_decay_L") )
                super.setL(cfg.getDouble("tau_decay_L"));
            //Set k
            if( cfg.hasParam("tau_decay_k") )
                super.setK(cfg.getDouble("tau_decay_k"));
        }
        
        /*
        tau_            = cfg.getDouble("tau");
        maxEpisodes_    = cfg.getInt("max_episodes");
        shouldDecrease_ = cfg.getInt("should_decrease");
    
    
        localTau_        = tau_;
        minTau_          = cfg.getDouble("min_tau");
        decreaseStart_   = cfg.getInt("decrease_start");
        decreaseOver_    = cfg.getInt("decrease_over");
        decreaseAmount_  = tau_ / ((double)decreaseOver_<0 ? ((double)maxEpisodes_ - (double)decreaseStart_) : (double)decreaseOver_);
        */
    }
    
    
    
    /**
     * Selects an action using the SoftMax selection mechanism.
     * @param stateActionPairs The actions, with their values, to select from
     * @return The soft max action
     */
    @Override
    public int select(double[] stateActionPairs)
    {
        double random     = /*Math.random()*/Rand.INSTANCE.nextDouble(),
               lowerBound = 0.0,
               upperBound = 0.0;
        
        double sumExp     = 0.0;
        
        for( int i=0; i<stateActionPairs.length; i++ )       // Calculate the sum of the exponientals
            sumExp += Math.exp(getExponent(stateActionPairs[i]));
        
        for( int i=0; i<stateActionPairs.length; i++ ) {     // Select the softmax action
            lowerBound  = upperBound;                        // and escape the method when found
            upperBound += Math.exp(getExponent(stateActionPairs[i])) / sumExp;
            if( random >= lowerBound && random < upperBound )
                return i;
            
        }
        
        return 0;
    }
    private double getExponent(double value) {
        if( value != 0.0 )
            return value/tau;
        else
            return 0.0;
    }
    
    /**
     * Set the local Tau value to what it should be based upon the given
     * episode number.
     * 
     * @param int episodeNo The episode number
     */
    public void decreaseTau(int episodeNo)
    {
        tau = super.decay(episodeNo);
        
        /*
        if (shouldDecrease_ == 1)               // on/off switch
        {
            if (episodeNo >= decreaseStart_)
            {
                if (localTau_ > minTau_)    // decrease the tau value
                    localTau_ = tau_ - ((double)(episodeNo-decreaseStart_) * decreaseAmount_);
                if (localTau_ <= minTau_)   // ensure it is above zero
                    localTau_  = minTau_;   // (as two if statements to stop rounding errors!)
            }
        }
        */
    }
}
