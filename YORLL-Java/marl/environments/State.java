package marl.environments;


/**
 * State is an interface which defines a the key aspects of an environmental
 * state. States must be able to be copied such that once they have been
 * copied if the equals(Object that) method was called it would return true.
 * The equals method must also be properly implemented so that two states may
 * be compared to see if they are the same. Finally a state should be able to
 * provide a unique hash code; clearly if one, or more, of the state features
 * are continuous rather than discrete then this method is non-sensical and
 * can be ignored though it should be noted that this will be parts of the
 * library like the DiscreteQTable will not properly work.
 * 
 * Note. If you do have a continuous environment you could make the State check
 * a discretised version of the state in the equals and hashCode method but it
 * must be the same in both methods (i.e. if the two objects are equal then
 * their hash codes should be the same also).
 * 
 * @author Pete Scopes
 * @version 06/09/2012
 */
public interface State<S extends State<S>>
{
    /**
     * Copy the state information of the given state into this state such that
     * if the equals(Object that) method was then to be called between this
     * object and the given object it would return true.
     * 
     * @param s The state object to be copied
     */
    void set(S s);
    
    
    /**
     * Allow States to be compared to one another.
     * @return True if the states are equivalent, otherwise false
     */
    @Override
    boolean equals(Object that);
    

    /**
     * Allow the State to be hashed.
     * @return A hash code representing this state
     */
    @Override
    int hashCode();
    
    
}
