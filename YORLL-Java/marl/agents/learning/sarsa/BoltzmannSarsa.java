package marl.agents.learning.sarsa;

import marl.agents.selection.Argmax;
import marl.agents.selection.Boltzmann;
import marl.environments.State;
import marl.utility.Config;

public class BoltzmannSarsa<S extends State<S>>
    extends DiscreteSarsa<S>
{
	private Boltzmann softmax_;
	
	public BoltzmannSarsa(Config cfg)
	{
		super(cfg);
		softmax_ = new Boltzmann(cfg);
	}

	@Override
	public int _select(S state)
	{
		double[] qValues = qTable.get(state);
		if( evaluationMode )
            return Argmax.select(qValues);
		else
		    return softmax_.select(qValues);
	}

}
