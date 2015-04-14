package elevatorsimulator;
/**
 * Represents the possible movement directions of an elevator car
 * @author Anton Jansson and Kristoffer Uggla Lingvall
 *
 */
public enum Direction {
	NONE,
	UP,
	DOWN;
		
	/**
	 * Returns the direction between the given floors
	 * @param currentFloor The current floor
	 * @param destinationFloor The destination floor
	 */
	public static Direction getDirection(int currentFloor, int destinationFloor) {
		int dir = destinationFloor - currentFloor;
		
		if (dir < 0) {
			return DOWN;
		} else if (dir > 0) {
			return UP;
		} else {
			return NONE;
		}
	}
	
	/**
	 * Returns the opposite direction
	 */
	public Direction oppositeDir() { 
		if (this == UP) {
			return DOWN;
		} else if (this == DOWN) {
			return UP;
		} else {
			return NONE;
		}
	}
}
