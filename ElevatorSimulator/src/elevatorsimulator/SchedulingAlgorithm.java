package elevatorsimulator;

/**
 * Represents a scheduling algorithm
 * @author Anton Jansson
 *
 */
public interface SchedulingAlgorithm {	
	/**
	 * Marks that a passenger has arrived
	 * @param simulator The simulator
	 * @param passenger The passenger to handle
	 */
	public void passengerArrived(Simulator simulator, Passenger passenger);
	
	/**
	 * Marks that the given passenger has boarded the given elevator car
	 * @param simulator The simulator
	 * @param elevatorCar The elevator car
	 * @param passenger The passenger
	 */
	public void passengerBoarded(Simulator simulator, ElevatorCar elevatorCar, Passenger passenger);
	
	/**
	 * Marks that the given passenger has exited the given elevator car
	 * @param simulator The simulator
	 * @param elevatorCar The elevator car
	 * @param passenger The passenger
	 */
	public void passengerExited(Simulator simulator, ElevatorCar elevatorCar, Passenger passenger);
	
	/**
	 * Updates the scheduler
	 * @param simulator The simulator
	 */
	public void update(Simulator simulator);
	
	/**
	 * The action to execute when the given elevator car is idle
	 * @param simulator The simulator
	 * @param elevatorCar The elevator car
	 */
	public void onIdle(Simulator simulator, ElevatorCar elevatorCar);
	
	/**
	 * The action to execute when the given elevator car has turned
	 * @param simulator The simulator
	 * @param elevatorCar The elevator car
	 */
	public void onTurned(Simulator simulator, ElevatorCar elevatorCar);
}
