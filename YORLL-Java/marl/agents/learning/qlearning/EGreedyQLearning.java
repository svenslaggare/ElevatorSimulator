package marl.agents.learning.qlearning;

import marl.agents.selection.Argmax;
import marl.agents.selection.EGreedy;
import marl.environments.State;
import marl.utility.Config;

public class EGreedyQLearning<S extends State<S>>
    extends DiscreteQLearning<S>
{
    /**
     * Uses Epsilon greedy exploration
     */
	private EGreedy egreedy;
	
	public EGreedyQLearning(Config cfg)
	{
		super(cfg);
		egreedy = new EGreedy(cfg);
	}

	@Override
	public int select(S state)
	{
		double[] qValues = qTable.get(state);
		if( evaluationMode )
            return Argmax.select(qValues);
		else
		    return egreedy.select(qValues);
	}
	
    /**
     * Decreases the value of epsilon in the EGreedy selection algorithm.
     * @param int episodeNo The episode number
     */
	public void decreaseEpsilon(int episodeNo)
	{
		egreedy.decreaseEpsilon(episodeNo);
	}

}
