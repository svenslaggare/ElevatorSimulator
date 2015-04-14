package elevatorsimulator;
/**
 * Represents a traffic profile
 * @author Anton Jansson and Kristoffer Uggla Lingvall
 *
 */
public class TrafficProfile {
	private final Interval[] arrivalRates;	
	private final long intervalLengthInMin = 10;
	
	/**
	 * Represents an interval
	 * @author Anton Jansson and Kristoffer Uggla Lingvall
	 *
	 */
	public static class Interval {
		private final double averageArrivalRatio;
		private final double upRate;
		private final double downRate;
		
		/**
		 * Creates a new interval
		 * @param averageArrivalRatio The average arrival ratio in the HC% metric
		 * @param upRate The up rate
		 * @param downRate The down rate
		 */
		public Interval(double averageArrivalRatio, double upRate, double downRate) {
			this.averageArrivalRatio = averageArrivalRatio * 2;
			this.upRate = upRate;
			this.downRate = downRate;
		}

		/**
		 * Returns the average arrival ratio in the HC% metric
		 */
		public double getAverageArrivalRatio() {
			return averageArrivalRatio;
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
				return 
					building.getTotalNumberOfResidents()
					* this.averageArrivalRatio
					* this.upRate;
			} else {
				return 
					building.getTotalNumberOfResidents()
					* this.averageArrivalRatio
					* (this.getInterfloorRate() + this.getDownRate()) 
					* (floor.getNumResidents() / (double)building.getTotalNumberOfResidents());
			}
		}
		
		/**
		 * Returns the probability that a new destination is the given floor
		 * @param building The building
		 * @param arrivalFloor The arrival floor
		 * @param destinationFloor The destination floor
		 */
		public double destinationFloorProbability(Building building, Floor arrivalFloor, Floor destinationFloor) {
			if (arrivalFloor.getFloorNumber() == Building.LOBBY) {
				return destinationFloor.getNumResidents() / (double)building.getTotalNumberOfResidents();
			} else if (destinationFloor.getFloorNumber() == Building.LOBBY) {
				return this.getDownRate() + this.getUpRate();
			} else {
				return 
					(destinationFloor.getNumResidents() / (double)(building.getTotalNumberOfResidents() - arrivalFloor.getNumResidents()))
					* getInterfloorRate();
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
	 * Returns the length of the interval in minutes
	 */
	public long lengthInMinutes() {
		return this.intervalLengthInMin;
	}
	
	/**
	 * Returns the length of the interval
	 */
	public long length() {
		return this.intervalLengthInMin * 60 * SimulatorClock.NANOSECONDS_PER_SECOND;
	}
	
	/**
	 * Returns data for the interval at the given time
	 */
	public Interval getIntervalData(long time) {
		int intervalIndex = (int)((time / this.length()) % this.arrivalRates.length);
		return this.arrivalRates[intervalIndex];
	}
}
