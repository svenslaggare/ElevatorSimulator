package elevatorsimulator.reinforcementlearning;

import elevatorsimulator.Building;
import elevatorsimulator.ElevatorCar;
import marl.agents.Agent;
import marl.agents.learning.qlearning.EGreedyQLearning; 
import marl.utility.Config;

/**
 * Represents an elevator system agent
 * @author Anton Jansson
 *
 */
public class ElevatorCarAgent extends Agent<ElevatorSystemEnvironment> {
	/**
	 * The actions for the agent
	 * @author Anton Jansson
	 *
	 */
	public enum Action {
		NONE,
//		CONTINUE,
		STOP_AT_NEXT,
		MOVE_UP,
		MOVE_DOWN
	}
	
	private final Config config;
	private final Building building;
	private final ElevatorCar elevatorCar;
	
	private Action action;
	private ElevatorCarState currentState;
	private ElevatorCarState prevState;
	private EGreedyQLearning<ElevatorCarState> learning;
	
	private int[] actionDistribution = new int[Action.values().length];
	
	/**
	 * Creates a new elevator system agent
	 * @param config The configuration for the learner
	 * @param building The building
	 * @param elevatorCar The elevator car
	 */
	public ElevatorCarAgent(Config config, Building building, ElevatorCar elevatorCar) {
		this.config = config;
		this.building = building;
		this.elevatorCar = elevatorCar;
	}
	
	/**
	 * Returns the elevator car for the agent
	 */
	public ElevatorCar getElevatorCar() {
		return this.elevatorCar;
	}
	
	/**
	 * Returns the action distribution
	 */
	public int[] getActionDistribution() {
		return actionDistribution;
	}

	@Override
	public void initialise() {
		this.learning = new EGreedyQLearning<>(this.config);
	}

	@Override
	public void add(ElevatorSystemEnvironment env) {
		super.add(env);
		this.learning.inform(env.getNumActions(this));
	}
	
	@Override
	public void reset(int episodeNo) {
		// decrease the value of epsilon
		this.learning.decreaseEpsilon(episodeNo);
						
		this.currentState = new ElevatorCarState(this.building, this.elevatorCar);
		this.prevState = new ElevatorCarState(this.building, this.elevatorCar);
		
		this.action = Action.NONE;
		
		// perceive the reset state
		this.perceive();
		
		for (int i = 0; i < this.actionDistribution.length; i++) {
			this.actionDistribution[i] = 0;
		}
	}

	@Override
	public void update(double reward, boolean terminal) {
		// perceive the environment
		perceive();
				
		// update the learning algorithm
		if (!terminal) {
			this.learning.update(this.prevState, this.currentState, this.action.ordinal(), reward);
		} else {
			this.learning.update(this.currentState, null, this.action.ordinal(), reward);
		}
	}

	@Override
	protected void perceive() {
		this.prevState.set(this.currentState);
		this.currentState.set(this.environment.getState(this));
	}

	@Override
	protected void reason(int time) {
		this.action = Action.values()[this.learning.select(this.currentState)];
	}

	@Override
	protected void act() {
		this.environment.performAction(this, this.action.ordinal());
		this.actionDistribution[this.action.ordinal()]++;
	}
}
