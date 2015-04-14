/**
 * 
 */
package marl.environments.KArmedBandit;

import marl.utility.Config;
import marl.utility.Rand;


/**
 * A collection of K normal probability distributions, each with a different mean and std.
 * 
 * @author Erel Segal the Levite
 * @since  2012-12-12
*/
public class RewardProbabilityDistributions {
    
    /**
     * The mean probability distributions.
     */
    private double[] means_;
    /**
     * The std probability distributions.
     */
    private double[] stds_;

	public RewardProbabilityDistributions(Config cfg)
	{
		// Get the means and stds of the distributions:
		means_ = cfg.getDoubleArray("means");
		stds_  = cfg.getDoubleArray("stds");

		// Make sure they are the same size:
		if( means_.length != stds_.length )
			throw new IllegalArgumentException("There are "+means_.length+" means but there are "+stds_.length+" stds - the number must be the same");
	}


	/**
	 * @return The number of available actions
	 */
	public int getNumActions() {
		return means_.length;
	}

	/**
	 * Given an action to perform this will return the reward for performing
	 * that action.
	 * @param action The action to perform
	 * @return       The reward for performing the specified action
	 */
	public double getReward(int action) {
		if( action >= means_.length )
			throw new IllegalArgumentException("Agent selected action #"+action+", but there are only "+means_.length+" actions");
		return Rand.INSTANCE.nextGaussian() * stds_[action] + means_[action];
	}
}
