package elevatorsimulator.reinforcementlearning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import marl.agents.Agent;
import marl.agents.learning.qlearning.*;
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
	private BoltzmannQLearning<ElevatorSystemState> learning;
	
	private int[] actionDistribution = new int[Action.values().length];
	private final List<Action> actions = new ArrayList<Action>();
	
	public enum Action {
		LONGEST_QUEUE_FIRST,
		ZONING,
		ROUND_ROBIN,
		THREE_PASSAGE_GROUP_ELEVATOR
	}
	
	/**
	 * Creates a new elevator system agent
	 * @param config The config
	 */
	public ElevatorSystemAgent(Config config) {
		this.config = config;
	}
	
	/**
	 * Sets if the agent is in evaluation mode or learning mode
	 * @param active True if evaluation mode
	 */
	public void evaluationMode(boolean active) {
		this.learning.evaluationMode(active);
	}
	
	/**
	 * Returns the action distribution
	 */
	public int[] getActionDistribution() {
		return actionDistribution;
	}
	
	/**
	 * Returns the action usage
	 */
	public List<Action> getActionUsage() {
		return this.actions;
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
		this.learning = new BoltzmannQLearning<>(this.config);
	}
	
	@Override
	public void add(ElevatorSystemEnvironment env) {
		super.add(env);
		this.learning.inform(Action.values().length);
	}

	@Override
	public void reset(int episodeNo) {
//		this.learning.decreaseEpsilon(episodeNo);
		
		this.currentState = new ElevatorSystemState();
		this.prevState = new ElevatorSystemState();
		
		this.action = 0;
		
		// perceive the reset state
		perceive();
		
		for (int i = 0; i < this.actionDistribution.length; i++) {
			this.actionDistribution[i] = 0;
		}
		
		this.actions.clear();
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
		this.currentState.set(environment.getState(this));
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
		this.environment.performAction(this, this.action);
		this.actionDistribution[this.action]++;
		this.actions.add(Action.values()[this.action]);
	}
}
