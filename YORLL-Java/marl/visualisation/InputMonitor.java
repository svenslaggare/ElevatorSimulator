package marl.visualisation;

/**
 * <p>For frame by frame input monitoring.</p>
 * 
 * @author scopes
 *
 */
public interface InputMonitor {

	/**
	 * This method should be called every frame
	 * <b>before</b> any calls for the current state
	 * of the monitor are made.
	 * 
	 * Note. It is recommended that this is synchronised.
	 */
	public abstract void poll();

	/**
	 * This resets the monitors state back to the initial
	 * state.
	 * 
	 * Note. It is recommended that this is synchronised.
	 */
	public abstract void clear();

}