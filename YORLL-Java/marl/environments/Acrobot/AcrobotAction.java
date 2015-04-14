/**
 * 
 */
package marl.environments.Acrobot;

/**
 * @author pds
 * @since  2013-03-07
 *
 */
public enum AcrobotAction {
    REVERSE(-1.0d), NEUTRAL(0.0d), FORWARD(1.0d);
    
    public final double value;
    
    private AcrobotAction(double value)
    {
        this.value = value;
    }
}
