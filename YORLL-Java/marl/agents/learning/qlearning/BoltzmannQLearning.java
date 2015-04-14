/**
 * 
 */
package marl.agents.learning.qlearning;

import marl.agents.selection.Argmax;
import marl.agents.selection.Boltzmann;
import marl.environments.State;
import marl.utility.Config;

/**
 * @author pds
 *
 */
public class BoltzmannQLearning<S extends State<S>>
    extends DiscreteQLearning<S>
{
	private Boltzmann softmax;
	
	public BoltzmannQLearning(Config cfg)
	{
		super(cfg);
		softmax = new Boltzmann(cfg);
	}

	
	@Override
	public int select(S state)
	{
		double[] qValues = qTable.get(state);
		if( evaluationMode )
            return Argmax.select(qValues);
		else
		    return softmax.select(qValues);
	}

}
