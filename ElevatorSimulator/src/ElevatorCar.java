import java.util.ArrayList;
import java.util.List;

/**
 * Represents an elevator car
 * @author Anton Jansson
 *
 */
public class ElevatorCar {
	private int floor;
	private Direction direction;
	private final List<Passenger> passengers;
	
	/**
	 * Creates a new elevator
	 * @param startFloor The start floor
	 */
	public ElevatorCar(int startFloor) {
		this.floor = startFloor;
		this.direction = Direction.NONE;
		this.passengers = new ArrayList<Passenger>();
	}

	/**
	 * Returns the floor
	 */
	public int getFloor() {
		return floor;
	}

	/**
	 * Returns the direction
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Returns the passengers
	 */
	public List<Passenger> getPassengers() {
		return passengers;
	}
	
	/**
	 * Updates the elevator car
	 * @param The simulator
	 */
	public void update(Simulator simulator) {
		
	}
}
