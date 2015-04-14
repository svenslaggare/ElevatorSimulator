package experiments.example;

import marl.agents.Agent;
import marl.agents.learning.sarsa.EGreedySarsa;
import marl.environments.Maze.MazeEnvironment;
import marl.environments.Maze.MazeState;
import marl.utility.Config;

public class SingleAgentMazeAgent
	extends Agent<MazeEnvironment<SingleAgentMazeAgent>>
{
	private Config        cfg_;
	private int           action_;
	private MazeState     currentState_,
					      previousState_;
	
	private EGreedySarsa<MazeState>
					      learning_;
	
	public SingleAgentMazeAgent(Config cfg)
	{
		cfg_ = cfg;
	}

	@Override
	public void initialise()
	{
		learning_ = new EGreedySarsa<>(cfg_);
	}
	
	@Override
	public void add(MazeEnvironment<SingleAgentMazeAgent> env) {
	    super.add(env);
        learning_.inform(env.getNumActions(this));
	}

	@Override
	public void reset(int episodeNo)
	{
		// decrease the value of epsilon
		learning_.decreaseEpsilon(episodeNo);
		
		currentState_   = new MazeState();
		previousState_  = new MazeState();
		// clear actions
		action_         = 0;
		
		// perceive the reset state
		perceive();
	}

	@Override
	public void update(double reward, boolean terminal)
	{
		// perceive the environment
		perceive();
		// update the learning algorithm
		if( !terminal )
			learning_.update(previousState_, currentState_, action_, reward);
		else
			learning_.update(currentState_, null, action_, reward);
	}

	@Override
	protected void perceive()
	{
	    // store the previous state
		previousState_.set(currentState_);
	    // perceive the state
	    currentState_.set(environment.getState(this));
	    // inform the learning of available actions -- done in store
	    //learning_.inform(actions_.length);
	}

	@Override
	protected void reason(int time)
	{
	    // use E-greedy to select the next action
	    action_ = learning_.select(currentState_);
	}

	@Override
	protected void act()
	{
	    environment.performAction(this, action_);
	}

}
