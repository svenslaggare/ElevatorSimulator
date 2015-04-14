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
 * @since 2012-12-25
*/
public class RewardMarkovianProcesses
{
    /**
     * The state of the different machines.
     */
	private int[] states_;
	
	/**
	 * The transition probabilities for the different machines.
	 */
	private double[][][] transitionProbabilities_;
	/**
	 * The transition rewards for the different machines. 
	 */
	private double[][][] transitionRewards_;

	/**
	 * @param cfg The configuration of this Reward Markovian Process
	 */
	public RewardMarkovianProcesses(Config cfg)
	{
		// Currently, the markovian parameters are hard-coded.
		// The example is taken from: http://www.citeulike.org/user/erelsegal-halevi/article/11854990
		
		// TODO: read the parameters from the configuration file

		int numMachines = cfg.getInt("numMachines");
		
		states_ = new int[numMachines];
		transitionProbabilities_ = new double[numMachines][][];
		transitionRewards_ = new double[numMachines][][];

		// machine 0:
		transitionProbabilities_[0] = new double[][] {
				/* from state 0: */ new double[] {0.3, 0.7}, 
				/* from state 1: */ new double[] {0.7, 0.3} 
		};
		transitionRewards_[0] = new double[][] {
				/* from state 0: */ new double[] {1, 10}, 
				/* from state 1: */ new double[] {1, 10} 
		};

		// machine 1:
		transitionProbabilities_[1] = new double[][] {
				/* from state 0: */ new double[] {0.9, 0.1}, 
				/* from state 1: */ new double[] {0.1, 0.9} 
		};
		transitionRewards_[1] = new double[][] {
				/* from state 0: */ new double[] {1, 10}, 
				/* from state 1: */ new double[] {1, 10} 
		};
	}
	
	
	/**
	 * @return The states of all the machines
	 */
	public int[] getStates() {
		return states_;
	}


	/**
	 * @return The number of available actions an agent can perform
	 */
	public int getNumActions() {
		return states_.length;
	}

	/**
	 * Calculates the reward for the pulling the arm of the specified machine
	 * updating the state of the specified machine based on the transition
	 * probability of the machine changing state.
	 * 
	 * @param selectedMachine The machine whose arm will be pulled
	 * @return                The reward for selecting the specified machine
	 */
	public double getReward(int selectedMachine) {
		if( selectedMachine >= states_.length )
			throw new IllegalArgumentException("Agent selected machine #"+selectedMachine+", but there are only "+states_.length+" machines");
		
		int      stateOfSelectedMachine                  = states_[selectedMachine];
		double[] transitionProbabilitiesFromCurrentState = transitionProbabilities_[selectedMachine][stateOfSelectedMachine];
		int      nextStateOfSelectedMachine              = Rand.INSTANCE.randomIndex(transitionProbabilitiesFromCurrentState);
		
		double   reward = transitionRewards_[selectedMachine][stateOfSelectedMachine][nextStateOfSelectedMachine];
		
		states_[selectedMachine] = nextStateOfSelectedMachine;
		
		System.out.println("Machine "+selectedMachine+" moved from "+stateOfSelectedMachine+" to "+nextStateOfSelectedMachine+" and gave "+reward);
		return reward;
	}
}
