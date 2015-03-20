package elevatorsimulator.reinforcementlearning;

import elevatorsimulator.SimulatorStats.StatsInterval;
import marl.environments.State;

/**
 * Represents the state for the elevator system
 * @author Anton Jansson
 *
 */
public class ElevatorSystemState implements State<ElevatorSystemState> {
	private int intervalNum;
	private int totalPassengers;
	private double up;
	private double down;
	private double interfloor;
	
	private static final double RATE_EPSILON = 0.05;
	private static final int TOTAL_EPSILON = 100;
	
	private final static Type STATE_TYPE = Type.TRAFFIC;
	/**
	 * The type of the state
	 * @author Anton Jansson
	 *
	 */
	public static enum Type {
		TRAFFIC,
		TIME
	}
	
	/**
	 * Creates a default elevator system state
	 */
	public ElevatorSystemState() {
		this.intervalNum = 0;
		this.totalPassengers = 0;
		this.up = 0;
		this.down = 0;
		this.interfloor = 0;
	}
		
	/**
	 * Returns the interval number
	 */
	public int getIntervalNum() {
		return this.intervalNum;
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
	public void update(StatsInterval interval) {
		if (STATE_TYPE == Type.TIME) {
			this.intervalNum = interval.getNum();
		} else if (STATE_TYPE == Type.TRAFFIC) {
			this.totalPassengers = (int)(interval.getNumUp() + interval.getNumDown() + interval.getNumInterfloors());
			this.up = interval.getNumUp() / (double)this.totalPassengers;
			this.down = interval.getNumDown() / (double)this.totalPassengers;
			this.interfloor = interval.getNumInterfloors() / (double)this.totalPassengers;
		}
	}

	@Override
	public void set(ElevatorSystemState other) {
		if (STATE_TYPE == Type.TIME) {
			this.intervalNum = other.intervalNum;
		} else if (STATE_TYPE == Type.TRAFFIC) {
			this.totalPassengers = other.totalPassengers;
			this.up = other.up;
			this.down = other.down;
			this.interfloor = other.interfloor;
		}
	}
	
	private static int hashCode(double x) {
		return (int)(x / RATE_EPSILON);
	}
		
	@Override
	public int hashCode() {
		if (STATE_TYPE == Type.TIME) {
			return this.intervalNum;
		} else if (STATE_TYPE == Type.TRAFFIC) {
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
		} else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (STATE_TYPE == Type.TIME) {
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
			return this.intervalNum == other.intervalNum;
		} else if (STATE_TYPE == Type.TRAFFIC) {
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
		} else {
			return false;
		}
	}	
}

