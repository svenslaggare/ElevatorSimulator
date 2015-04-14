package marl.agents.learning.sarsa;

import marl.agents.selection.Argmax;
import marl.agents.selection.EGreedy;
import marl.environments.State;
import marl.utility.Config;

public class EGreedySarsa<S extends State<S>>
    extends DiscreteSarsa<S>
{
	private EGreedy egreedy;
	
	public EGreedySarsa(Config cfg)
	{
		super(cfg);
		egreedy = new EGreedy(cfg);
	}

	@Override
	public int _select(S state)
	{
		double[] qValues = qTable.get(state);
		if( evaluationMode )
            return Argmax.select(qValues);
		else
		    return egreedy.select(qValues);
	}
	

    /**
     * Decreases the value of epsilon in the EGreedy selection
     * algorithm.
     * @param episodeNo The episode number
     */
	public void decreaseEpsilon(int episodeNo)
	{
		egreedy.decreaseEpsilon(episodeNo);
	}

}
