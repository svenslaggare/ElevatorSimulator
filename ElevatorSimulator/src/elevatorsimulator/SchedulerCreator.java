package elevatorsimulator;

/**
 * Represents an interface for creating a scheduler
 * @author Anton Jansson and Kristoffer Uggla Lingvall
 *
 */
public interface SchedulerCreator {
	/**
	 * Creates a scheduler for the given building
	 * @param building The building
	 */
	SchedulingAlgorithm createScheduler(Building building);
}
