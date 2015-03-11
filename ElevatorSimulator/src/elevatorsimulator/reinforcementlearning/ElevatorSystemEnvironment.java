package elevatorsimulator.reinforcementlearning;

import elevatorsimulator.ElevatorCar;
import elevatorsimulator.ElevatorCar.State;
import elevatorsimulator.Passenger;
import elevatorsimulator.Simulator;
import elevatorsimulator.SimulatorClock;
import marl.environments.Environment;
import marl.utility.Config;

/**
 * Represents the environment for the elevator system
 * @author Anton Jansson
 *
 */
public class ElevatorSystemEnvironment implements Environment<ElevatorCarState, ElevatorCarAgent> {
	private int time;
	private final Config config;
	private final Simulator simulator;
	private int numAgents;
		
	private Tuple<ElevatorSystemEnvironment, ElevatorCarState, ElevatorCarAgent>[] tuples;
	private static final ElevatorCarAgent.Action[] actions = ElevatorCarAgent.Action.values();
	
	/**
	 * Creates a new environment
	 * @param config The configuration for the environment
	 * @param simulator The simulator
	 */
	public ElevatorSystemEnvironment(Config config, Simulator simulator) {
		this.config = config;
		this.simulator = simulator;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialise() {
		this.numAgents = simulator.getBuilding().getElevatorCars().length;
		
		this.tuples = (Tuple<ElevatorSystemEnvironment, ElevatorCarState, ElevatorCarAgent>[])new Tuple[this.numAgents];
		
		for (int i = 0; i < this.tuples.length; i++) {
			Tuple<ElevatorSystemEnvironment, ElevatorCarState, ElevatorCarAgent> agentTuple = 
					new Tuple<ElevatorSystemEnvironment, ElevatorCarState, ElevatorCarAgent>();
			
			ElevatorCar elevatorCar = simulator.getBuilding().getElevatorCars()[i];
			
			agentTuple.agent_ = null;
			agentTuple.state_ = new ElevatorCarState(simulator.getBuilding(), elevatorCar);
			agentTuple.next_ = new ElevatorCarState(simulator.getBuilding(), elevatorCar);
			this.tuples[i] = agentTuple;
		}
	}

	@Override
	public void reset(int episodeNo) {
		//Reset the environment
		for (int i = 0; i < this.numAgents; i++) {
			this.tuples[i].sumReward_ = 0;
			this.tuples[i].agent_.reset(episodeNo);
		}
		
		this.time = 0;
	}

	@Override
	public boolean add(ElevatorCarAgent agent) {
		for (int i = 0; i < this.numAgents; i++) {
			if (this.tuples[i].agent_ == null) {
				this.tuples[i].agent_ = agent;
				this.tuples[i].agent_.add(this);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public ElevatorCarState getState(ElevatorCarAgent agent) {
		for (int i = 0; i < this.numAgents; i++) {
			if (this.tuples[i].agent_ == agent) {
				return this.tuples[i].state_;
			}
		}
		
		return null;
	}

	@Override
	public int getNumActions(ElevatorCarAgent agent) {
		return ElevatorSystemEnvironment.actions.length;
	}

	@Override
	public boolean inTerminalState() {
		return !this.simulator.canGenerateArrivals();
	}

	@Override
	public void performAction(ElevatorCarAgent agent, int actionNum) {
		for (int i = 0; i < this.tuples.length; i++) {
			if (this.tuples[i].agent_ == agent) {
				ElevatorCarAgent.Action action = ElevatorSystemEnvironment.actions[actionNum];
				
				switch (action) {
//				case CONTINUE:
//					break;
//				case STOP_AT_NEXT:					
//					break;
				case MOVE_DOWN:
					if (agent.getElevatorCar().getState() == State.IDLE) {
						agent.getElevatorCar().moveTowards(this.simulator, 0);
					}
					break;
//				case MOVE_UP:
//					break;
				default:
					break;				
				}
			}
		}
	}

	/**
	 * Calculates the reward
	 */
	private double calculateReward() {
		double reward = 0;
		SimulatorClock clock = this.simulator.getClock();
		
		for (Passenger passenger : this.simulator.getControlSystem().getHallQueue()) {
			double waitTimeSec = clock.asSecond(passenger.waitTime(this.simulator.getClock()));
			reward += waitTimeSec * waitTimeSec;
		}
		
		return -reward;
	}
	
	@Override
	public void incrementTime() {
        // Let each agent choose what to do
        for(int i = 0; i < this.numAgents; i++) {
            this.tuples[i].agent_.step(this.time);
        }
        
        // Update
        for(int i = 0; i < this.numAgents; i++) {
        	double reward = this.calculateReward();
        	this.tuples[i].agent_.update(reward, false);
        	this.tuples[i].addReward(reward);
        }
        
        this.time++;
	}
	
	/**
	 * Returns the total reward
	 * @return
	 */
	public double totalReward() {
		double total = 0;
		
		for (int i = 0; i < this.numAgents; i++) {
			total += this.tuples[i].sumReward_;
		}
		
		return total;
	}
}
