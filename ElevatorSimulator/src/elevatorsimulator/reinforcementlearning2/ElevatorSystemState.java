package elevatorsimulator.reinforcementlearning2;

import elevatorsimulator.Simulator;
import elevatorsimulator.SimulatorStats.SimulatorStatsInterval;
import marl.environments.State;

/**
 * Represents the state for the elevator system
 * @author Anton Jansson
 *
 */
public class ElevatorSystemState implements State<ElevatorSystemState> {
	private int totalPassengers;
	private double up;
	private double down;
	private double interfloor;
	
	private static final double RATE_EPSILON = 0.01;
	private static final int TOTAL_EPSILON = 1000;
	
	/**
	 * Creates a default elevator system state
	 */
	public ElevatorSystemState() {
		this.totalPassengers = 0;
		this.up = 0;
		this.down = 0;
		this.interfloor = 0;
	}
	
	/**
	 * Creates a new elevator system state from the given simulator
	 * @param simulator The simulator
	 */
	public ElevatorSystemState(Simulator simulator) {
		
	}
	
	/**
	 * Returns the total number of passengers
	 */
	public int getTotalPassengers() {
		return totalPassengers;
	}
		
	/**
	 * Returns the up
	 */
	public double getUp() {
		return up;
	}

	/**
	 * Returns the down
	 */
	public double getDown() {
		return down;
	}

	/**
	 * Gets the interfloor
	 */
	public double getInterfloor() {
		return interfloor;
	}
	
	/**
	 * Updates the state using the given interval
	 * @param interval The interval
	 */
	public void update(SimulatorStatsInterval interval) {
		this.totalPassengers = interval.getNumUp() + interval.getNumDown() + interval.getNumInterfloor();
		this.up = interval.getNumUp() / (double)this.totalPassengers;
		this.down = interval.getNumDown() / (double)this.totalPassengers;
		this.interfloor = interval.getNumInterfloor() / (double)this.totalPassengers;
	}

	@Override
	public void set(ElevatorSystemState other) {
		this.totalPassengers = other.totalPassengers;
		this.up = other.up;
		this.down = other.down;
		this.interfloor = other.interfloor;
	}
	
	/**
	 * Returns the hash code for the given double 
	 * @param x
	 * @return
	 */
	private static int hashCode(double x) {
		return (int)(x / RATE_EPSILON);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = hashCode(down);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = hashCode(interfloor);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (totalPassengers / TOTAL_EPSILON);
		temp = hashCode(up);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
			
		if (obj == null) {
			return false;
		}
			
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		ElevatorSystemState other = (ElevatorSystemState) obj;
		
		if (this.totalPassengers / TOTAL_EPSILON != other.totalPassengers / TOTAL_EPSILON) {
			return false;
		}
		
		if (hashCode(up) != hashCode(other.up)) {
			return false;
		}
		
		if (hashCode(down) != hashCode(other.down)) {
			return false;
		}
		
		if (hashCode(interfloor) != hashCode(other.interfloor)) {
			return false;
		}
		
		return true;
	}	
}
