/**
 * 
 */
package marl.environments;


/**
 * There are some cases where an environment is stateless. In that case the
 * Stateless object can be used in lieu of defining a state object for that
 * experiment.
 * 
 * The Stateless object is immutable, as far as a non-enum can be, and overrides
 * the equals and hashCode so that all stateless objects are equivalent. Note
 * that the hashCode for a stateless object is 1
 * 
 * @author pds
 *
 */
public class Stateless implements State<Stateless>
{
    /**
     * The hash code for ALL stateless objects.
     */
    public static final int hashCode_ = 1;
    
    /* (non-Javadoc)
     * @see marl.environments.State#set(marl.environments.State)
     */
    @Override
    public void set(Stateless s) {
        // since this is a stateless object then it doesn't make sense for it
        // to be able to be altered
    }
    
    
    /**
     * If the given object is a stateless state then return true otherwise
     * false
     * @return True if another stateless object
     */
    @Override
    public boolean equals(Object obj) {
        if( obj instanceof Stateless )
            return true;
        else
            return false;
    }
    
    @Override
    public int hashCode() {
        return hashCode_;
    }
}
