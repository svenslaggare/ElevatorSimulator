package elevatorsimulator.reinforcementlearning;

import java.util.Queue;

import elevatorsimulator.*;
import elevatorsimulator.schedulers.ReinforcementLearning;
import marl.environments.Environment;

public class ElevatorSystemEnvironment implements Environment<ElevatorSystemState, ElevatorSystemAgent> {
	private int time;
	private Tuple<ElevatorSystemEnvironment, ElevatorSystemState, ElevatorSystemAgent> tuple;
	private final Simulator simulator;
	
	/**
	 * Creates a new elevator system environment
	 * @param simulator The simulator
	 */
	public ElevatorSystemEnvironment(Simulator simulator) {
		this.simulator = simulator;
	}
	
	@Override
	public void initialise() {
		this.tuple = new Tuple<ElevatorSystemEnvironment, ElevatorSystemState, ElevatorSystemAgent>();
		this.tuple.agent = null;
		this.tuple.state = new ElevatorSystemState();
		this.tuple.next = new ElevatorSystemState();
	}

	@Override
	public void reset(int episodeNo) {
		this.tuple.sumReward = 0;
		this.tuple.lastReward = 0;
		this.tuple.agent.reset(episodeNo);
		this.time = 0;
	}

	@Override
	public boolean add(ElevatorSystemAgent agent) {
		this.tuple.agent = agent;
		this.tuple.agent.add(this);
		return true;
	}

	@Override
	public ElevatorSystemState getState(ElevatorSystemAgent agent) {
		return this.tuple.state;
	}

	@Override
	public int getNumActions(ElevatorSystemAgent agent) {
		return ElevatorSystemAgent.Action.values().length;
	}

	@Override
	public boolean inTerminalState() {
		return !this.simulator.canGenerateArrivals();
	}

	@Override
	public void performAction(ElevatorSystemAgent agent, int action) {
		ReinforcementLearning multiScheduler = (ReinforcementLearning)this.simulator.getControlSystem().getScheduler();
		multiScheduler.switchTo(this.simulator, action);		
	}
	
	/**
	 * Calculates the reward
	 */
	private double calculateReward() {
		StatsInterval interval = this.simulator.getStats().getPollInterval();
		
		double waitingASWT = 0;
		SimulatorClock clock = this.simulator.getClock();
		
		Queue<Passenger> waitQueue = this.simulator.getControlSystem().getHallQueue();
		for (Passenger passenger : waitQueue) {
			double waitTimeSec = clock.asSecond(passenger.waitTime(this.simulator.getClock()));
			waitingASWT += waitTimeSec * waitTimeSec;
		}
		
		if (waitingASWT > 0) {
			waitingASWT /= waitQueue.size();
		}
				
		double servedASWT = interval.getTotalSquaredWaitTime();
		
		if (interval.getNumExists() > 0) {
			servedASWT /= interval.getNumExists();
		}
			
//		double elevatorASWT = 0.0;
//		int numInElevator = 0;
//		
//		for (ElevatorCar elevator : this.simulator.getBuilding().getElevatorCars()) {
//			for (Passenger passenger : elevator.getPassengers()) {
//				double waitTimeSec = clock.asSecond(passenger.waitTime(this.simulator.getClock()));
//				elevatorASWT += waitTimeSec * waitTimeSec;
//				numInElevator++;
//			}
//		}
		
//		if (numInElevator > 0) {
//			elevatorASWT /= numInElevator;
//		}
		
//		return -(waitingASWT + servedASWT + elevatorASWT);
		return -servedASWT;
	}
	
	/**
	 * Rewards the last state
	 */
	public void rewardLastState() {
		this.tuple.state.update(this.simulator.getStats().getPollInterval());
		double reward = calculateReward();
		this.tuple.agent.update(reward, false);
		this.tuple.addReward(reward);		
	}

	@Override
	public void incrementTime() {
		this.tuple.state.update(this.simulator.getStats().getPollInterval());
		double reward = calculateReward();
		this.tuple.agent.update(reward, false);
		this.tuple.addReward(reward);		
		this.tuple.agent.step(this.time);
		this.time++;
	}
	
	/**
	 * Returns the total reward
	 */
	public double totalReward() {
		return this.tuple.sumReward;
	}
}
