package elevatorsimulator.reinforcementlearning2;

import elevatorsimulator.Simulator;
import elevatorsimulator.SimulatorStats.SimulatorStatsInterval;
import elevatorsimulator.schedulers.MultiScheduler;
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
		MultiScheduler multiScheduler = (MultiScheduler)this.simulator.getControlSystem().getScheduler();
		multiScheduler.switchTo(action);		
	}
	
	/**
	 * Calculates the reward
	 */
	private double calculateReward() {
		SimulatorStatsInterval interval = this.simulator.getStats().getInterval();
		return -interval.getTotalSquaredWaitTime() / interval.getNumExists();
	}

	@Override
	public void incrementTime() {
		this.tuple.state.update(this.simulator.getStats().getInterval());
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
