package elevatorsimulator.reinforcementlearning2;

import java.util.Map;

import marl.agents.Agent;
import marl.agents.learning.qlearning.EGreedyQLearning;
import marl.agents.learning.sarsa.EGreedySarsa;
import marl.utility.Config;

/**
 * Represents an elevator system agent
 * @author Anton Jansson
 *
 */
public class ElevatorSystemAgent extends Agent<ElevatorSystemEnvironment> {
	private Config config; 
	private int action;
	private ElevatorSystemState currentState;
	private ElevatorSystemState prevState;
	private EGreedySarsa<ElevatorSystemState> learning;
	private int[] actionDistribution = new int[Action.values().length];
	
	public enum Action {
		COLLECTIVE_CONTROL,
		ZONING
	}
	
	/**
	 * Creates a new elevator system agent
	 * @param config The config
	 */
	public ElevatorSystemAgent(Config config) {
		this.config = config;
	}
	
	/**
	 * Returns the action distribution
	 */
	public int[] getActionDistribution() {
		return actionDistribution;
	}
	
	/**
	 * Returns the size of the state space
	 */
	public int getStateSpace() {
		return this.learning.table().size();
	}
	
	/**
	 * Returns the state usage
	 */
	public Map<Integer, Integer> stateUsage() {
		return this.learning.table().stateUsage();
	}
	
	@Override
	public void initialise() {
		this.learning = new EGreedySarsa<>(this.config);
	}
	
	@Override
	public void add(ElevatorSystemEnvironment env) {
		super.add(env);
		this.learning.inform(Action.values().length);
	}

	@Override
	public void reset(int episodeNo) {
		this.learning.decreaseEpsilon(episodeNo);
		
		this.currentState = new ElevatorSystemState();
		this.prevState = new ElevatorSystemState();
		
		this.action = 0;
		
		// perceive the reset state
		perceive();
		
		for (int i = 0; i < this.actionDistribution.length; i++) {
			this.actionDistribution[i] = 0;
		}
	}

	@Override
	public void update(double reward, boolean terminal) {
		// perceive the environment
		perceive();
		
		// update the learning algorithm
		if(!terminal) {
			this.learning.update(this.prevState, this.currentState, this.action, reward);
		} else {
			this.learning.update(this.currentState, null, this.action, reward);
		}
	}

	@Override
	protected void perceive() {
	    // store the previous state
		this.prevState.set(this.currentState);
		
	    // perceive the state
		this.currentState.set(env_.getState(this));
	}

	@Override
	protected void reason(int time) {
	    // use E-greedy to select the next action
	    this.action = this.learning.select(this.currentState);
	    
	    if (this.action == 1) {
	    	this.action = 1;
	    } else {
	    	this.action = this.action * 1;
	    }
	}

	@Override
	protected void act() {
		this.env_.performAction(this, this.action);
		this.actionDistribution[this.action]++;
	}
}
