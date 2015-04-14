/**
 * 
 */
package marl.environments.MountainCar;

/**
 * @author pds
 * @since  2013-03-07
 *
 */
public enum MountainCarAction {
    REVERSE(-1.0d), NEUTRAL(0.0d), FORWARD(1.0d);
    
    public final double value;
    
    private MountainCarAction(double value)
    {
        this.value = value;
    }
}
