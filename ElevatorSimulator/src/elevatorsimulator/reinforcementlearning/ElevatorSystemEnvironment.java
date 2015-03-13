package elevatorsimulator.reinforcementlearning;

import elevatorsimulator.Direction;
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
	@SuppressWarnings("unused")
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
			
			agentTuple.agent = null;
			agentTuple.state = new ElevatorCarState(simulator.getBuilding(), elevatorCar);
			agentTuple.next = new ElevatorCarState(simulator.getBuilding(), elevatorCar);
			this.tuples[i] = agentTuple;
		}
	}

	@Override
	public void reset(int episodeNo) {
		//Reset the environment
		for (int i = 0; i < this.numAgents; i++) {
			ElevatorCar elevatorCar = this.tuples[i].agent.getElevatorCar();
			this.tuples[i].state = new ElevatorCarState(simulator.getBuilding(), elevatorCar);
			this.tuples[i].next = new ElevatorCarState(simulator.getBuilding(), elevatorCar);
			this.tuples[i].sumReward = 0;
			this.tuples[i].agent.reset(episodeNo);
		}
		
		this.time = 0;
	}

	@Override
	public boolean add(ElevatorCarAgent agent) {
		for (int i = 0; i < this.numAgents; i++) {
			if (this.tuples[i].agent == null) {
				this.tuples[i].agent = agent;
				this.tuples[i].agent.add(this);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public ElevatorCarState getState(ElevatorCarAgent agent) {
		for (int i = 0; i < this.numAgents; i++) {
			if (this.tuples[i].agent == agent) {
				return this.tuples[i].state;
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
			if (this.tuples[i].agent == agent) {
				ElevatorCarAgent.Action action = ElevatorSystemEnvironment.actions[actionNum];
				ElevatorCar elevatorCar = agent.getElevatorCar();
				elevatorsimulator.Floor[] floors = this.simulator.getBuilding().getFloors();
				
				switch (action) {
				case STOP_AT_NEXT:		
					if (elevatorCar.getState() != State.IDLE) {
						int nextFloorIndex = elevatorCar.getFloor();
						
						if (elevatorCar.getDirection() == Direction.UP) {
							nextFloorIndex++;
						} else if (elevatorCar.getDirection() == Direction.DOWN) {
							nextFloorIndex--;
						}
						
						if (nextFloorIndex >= 0 && nextFloorIndex < floors.length) {
							elevatorsimulator.Floor nextFloor = floors[nextFloorIndex];
							
							if (!nextFloor.getWaitingQueue().isEmpty()) {
								boolean hasInDir = false;
								
								for (Passenger passenger : nextFloor.getWaitingQueue()) {
									if (passenger.getDirection() == elevatorCar.getDirection()) {
										hasInDir = true;
										break;
									}
								}
								
								if (hasInDir) {
									elevatorCar.stopElevatorAtNextFloor();
								}
							}
						}
					}
					break;
				case MOVE_DOWN:
					if (elevatorCar.getState() == State.IDLE) {
						int lowestFloor = elevatorCar.getFloor();
						for (Passenger passenger : this.simulator.getControlSystem().getHallQueue()) {
							if (passenger.getArrivalFloor() < lowestFloor) {
								lowestFloor = passenger.getArrivalFloor();
							}
						}
						
						elevatorCar.moveTowards(simulator, lowestFloor);
					}
					break;
				case MOVE_UP:
					if (elevatorCar.getState() == State.IDLE) {
						int highestFloor = elevatorCar.getFloor();
						for (Passenger passenger : this.simulator.getControlSystem().getHallQueue()) {
							if (passenger.getArrivalFloor() > highestFloor) {
								highestFloor = passenger.getArrivalFloor();
							}
						}
						
						elevatorCar.moveTowards(simulator, highestFloor);
					}
					break;
				default:
					break;				
				}
			}
			
//			this.tuples[i].next_.updateState(this.simulator.getBuilding(), agent.getElevatorCar());
//			this.tuples[i].next_.set(this.tuples[i].state_);  
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
		
		return -reward * 1E-9;
	}
	
	@Override
	public void incrementTime() {
        // Let each agent choose what to do
        for(int i = 0; i < this.numAgents; i++) {
            this.tuples[i].agent.step(this.time);
            
			this.tuples[i].next.updateState(this.simulator.getBuilding(), this.tuples[i].agent.getElevatorCar());
            this.tuples[i].state.set(this.tuples[i].next);
//			this.tuples[i].next_.set(this.tuples[i].state_);  
        }
        
        // Update
        for(int i = 0; i < this.numAgents; i++) {
        	double reward = this.calculateReward();
        	this.tuples[i].agent.update(reward, false);
        	this.tuples[i].addReward(reward);
        }
        
        this.time++;
	}
	
	/**
	 * Returns the total reward
	 */
	public double totalReward() {
		double total = 0;
		
		for (int i = 0; i < this.numAgents; i++) {
			total += this.tuples[i].sumReward;
		}
		
		return total;
	}
}
