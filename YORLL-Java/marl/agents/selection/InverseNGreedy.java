package marl.agents.selection;

import marl.utility.Config;
import marl.utility.Rand;


/**
 * epsilon-1/n-Greedy:
 *
 * The E-Greedy policy is to be greedy (select the Argmax action) with
 * probability 1-epsilon and then with epsilon probability select a random
 * action which could be the {@link Argmax} action.
 * 
 * In this policy, epsilon = 1/n, where n is the episode number.
 * 
 * @author Erel Segal the Levite 
 * @version 13/12/2012
 */
public class InverseNGreedy
    implements Exploration
{
	double   localEpsilon_;            // The value epsilon currently has



	/**
	 * Constructor for objects of class EGreedy
	 */
	public InverseNGreedy(Config cfg)
	{
		localEpsilon_   = 1;
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
		if( localEpsilon_ > Rand.INSTANCE.nextDouble() )
			return Rand.INSTANCE.nextInt(stateActionPairs.length);
		else
			return Argmax.select(stateActionPairs);
	}


	/**
	 * This method sets the epsilon value based upon the settings in the
	 * configuration file and the episode number given.
	 * 
	 * @param episodeNo The episode number
	 */
	public void decreaseEpsilon(int episodeNo)
	{
		localEpsilon_ = (episodeNo>0? 1.0/(double)episodeNo: 1.0);
	}
}
