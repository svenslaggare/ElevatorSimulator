package elevatorsimulator;
/**
 * Represents a traffic profile
 * @author Anton Jansson
 *
 */
public class TrafficProfile {
	private Interval[] arrivalRates;	
	private final long intervalLength = 10 * 60 * SimulatorClock.NANOSECONDS_PER_SECOND;
	
	/**
	 * Represents an interval
	 * @author Anton Jansson
	 *
	 */
	public static class Interval {
		private final double averageArrivals;
		private final double upRate;
		private final double downRate;
		
		/**
		 * Creates a new interval
		 * @param averageArrivals The average amount of arrivals in the interval
		 * @param upRate The up rate
		 * @param downRate The down rate
		 */
		public Interval(double averageArrivals, double upRate, double downRate) {
			this.averageArrivals = averageArrivals;
			this.upRate = upRate;
			this.downRate = downRate;
		}

		/**
		 * Returns the average amount of arrivals
		 */
		public double getAverageArrivals() {
			return averageArrivals;
		}
		
		/**
		 * Returns the up rate
		 */
		public double getUpRate() {
			return upRate;
		}

		/**
		 * Returns the down rate
		 */
		public double getDownRate() {
			return downRate;
		}	
		
		/**
		 * Returns the interfloor rate
		 */
		public double getInterfloorRate() {
			return 1.0 - (this.upRate + this.downRate);
		}
		
		/**
		 * Calculates the average number of arrival for the given floor
		 * @param building The building
		 * @param floor The floor
		 */
		public double averageNumberOfArrivals(Building building, Floor floor) {
			if (floor.getFloorNumber() == Building.LOBBY) {
				return this.averageArrivals * this.upRate;
			} else {
				return 
					this.averageArrivals
					* (this.getInterfloorRate() + this.getDownRate()) 
					* (floor.getNumResidents() / (double)building.getTotalNumberOfResidents());
			}
		}
		
		/**
		 * Returns the probability that a new destination is the given floor
		 * @param building The building
		 * @param currentFloor The current floor
		 * @param destinationFloor The destination floor
		 */
		public double destinationFloorProbability(Building building, Floor currentFloor, Floor destinationFloor) {
			if (currentFloor.getFloorNumber() == Building.LOBBY) {
				return destinationFloor.getNumResidents() / (double)building.getTotalNumberOfResidents();
			} else if (destinationFloor.getFloorNumber() == Building.LOBBY) {
				return this.getDownRate();
			} else {
				return 
					(destinationFloor.getNumResidents() / (double)(building.getTotalNumberOfResidents() - currentFloor.getNumResidents()))
					* (1.0 - this.getDownRate());
			}
		}
	}
	
	/**
	 * Creates a new traffic profile
	 * @param arrivalRates The arrival rates
	 */
	public TrafficProfile(Interval[] arrivalRates) {
		this.arrivalRates = arrivalRates;
	}
	
	/**
	 * Returns the arrival rate for the given time
	 */
	public Interval getArrivalRate(long time) {
		int intervalIndex = (int)((time / intervalLength) % this.arrivalRates.length);
		return this.arrivalRates[intervalIndex];
	}
}
