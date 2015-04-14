package marl.agents.learning;

import java.util.HashMap;

import marl.environments.State;


/**
 * Discrete Value Table is an implementation of a value table specifically made
 * for YORLL. It allows for simple access and updates of state values.
 *
 * A nice feature of this Value Table is that it isn't required before hand to
 * know the number of state representations that will be encountered along the
 * way but can dynamically increase it's size when needed. This obviously has a
 * latent cost so where possible it is recommended to use the
 * DiscreteValueTable(int nStates) as the constructor to give it a large
 * starting size since it defaults to a maximum size of 10.
 * 
 * Note: That if you underestimate the number of states in the constructor the
 * dynamic increase of size will still happen.
 * 
 * @author Pete Scopes
 * @since 2012-10-04
 */
public class DiscreteValueTable
{
    /**
     * These values are used for the normalisation state values, if you require
     * states to have higher values than this either reconsider your choice of
     * values or alter them in you local copy of the library.
     */
    public static final double MAX_VALUE     =  1000000.0d,
                               MIN_VALUE     = -1000000.0d;
    /**
     * The initial value that the value should take.
     */
    public static double       INITIAL_VALUE = 0.0d;
    public static void setInitialValue(double value) {
        INITIAL_VALUE = value;
    }
    
    /**
     * The Value Table containing the values of every hashed state.
     */
//    private double[] valueTable_;
    /**
     * The hash table holding the links between hashed states and
     * their positions in the Value Table.
     */
//    private long[]   hashTable_;
    private HashMap<Integer, Double> hashTable_;
    /**
     * The current size of the table.
     */
//    private int      size_;
    /**
     * The current maximum size of the table.
     */
    private int      maxSize_;

    
    /**
     * Constructor for objects of the {@link DiscreteValueTable}
     */
    public DiscreteValueTable()
    {
        this(10);
    }
    /**
     * Constructs a new Discrete Value Table with the specified number states
     * initially allowed for.
     * @see DiscreteValueTable
     * @param nStates The maximum number states (must be greater than zero)
     * @throws IllegalArgumentException If the value of nStates is less than or
     *                                  equal to zero
     */
    public DiscreteValueTable(int nStates)
    {
        if( nStates <= 0 )
            throw new IllegalArgumentException();
        
        maxSize_ = nStates;
        hashTable_ = new HashMap<>(maxSize_);
        reset();
    }
    
    
    /**
     * Resets the Value Table so that it is returned to its original starting
     * state.
     */
    public void reset()
    {
//        size_ = 0;
//        valueTable_ = new double[maxSize_];
//        hashTable_  = new long[maxSize_];
        hashTable_.clear();
    }
    
    /**
     * Store the given value of the given state.
     * @param state  The state to store the value against
     * @param value  The value to be stored
     */
    public void put(State<?> state, double value)
    {
        // Get the hash key of the given state, creating if needed
//        int hashKey = add(state);
        
        // Normalise the value
        if( value < MIN_VALUE )     value = MIN_VALUE;
        if( value > MAX_VALUE )     value = MAX_VALUE;
        
        // Update the Q value of the given state and action
//        valueTable_[hashKey] = value;
        hashTable_.put(state.hashCode(), value);
    }
    
    /**
     * Retrieve the Value of the given State.
     */
    public double get(State<?> state)
    {
        // Get the has key with no safety
//        int hashKey = add(state);
        
        // return the state action pairs
//        return valueTable_[hashKey];
        Double value = hashTable_.get(state.hashCode());
        if( value != null )
            return value;
        else {
            hashTable_.put(state.hashCode(), INITIAL_VALUE);
            return INITIAL_VALUE;
        }
    }
    
    /**
     * Returns true if the table is aware of the specified
     * state.
     * @param state The state to be checked
     * @return True if the state is present in the Table, otherwise false
     */
    public boolean has(State<?> state)
    {
//        return getHashKey(state) != -1;
        return hashTable_.containsKey(state.hashCode());
    }
    
    

    /**
     * Returns the has key of the given state or -1 if the state
     * is not present in the table.
     * @param state The state whose hash key is wanted
     * @return The hash key of the given state, or -1
     */
//    private int getHashKey(State<?> state)
//    {
//        int hashValue = state.hashCode();
//        int hashKey   = 0;
//        
//        // find the hash entry
//        for( ; hashKey<size_; hashKey++ )
//            if( hashTable_[hashKey] == hashValue )
//                return hashKey;
//        
//        return -1;
//    }

    /**
     * Returns the hash key of the given state and if said state is not
     * present it is added and given a hash key.
     * @param state The state to be added
     * @return The hash key of the state
     */
//    private int add(State<?> state)
//    {
//        int hashValue = state.hashCode();
//        int hashKey   = 0;
//        
//        // find the hash entry
//        for( ; hashKey<size_; hashKey++ )
//            if( hashTable_[hashKey] == hashValue )
//                return hashKey;
//        
//        if( size_ == maxSize_ ) {
//            maxSize_ *= 2;
//            {   // double the size of the memory stores
//                double[] save = new double[maxSize_];
//                for( int i=0; i<size_; i++ )
//                    save[i] = valueTable_[i];
//                
//                valueTable_ = save;
//            }
//            {   // double the size of the hash table
//                long[] save = new long[maxSize_];
//                for( int i=0; i<size_; i++ )
//                    save[i] = hashTable_[i];
//                
//                hashTable_ = save;
//            }
//        }
//    
//        // The state was not found so add it
//        size_++;
//        hashTable_[hashKey]  = hashValue;
//        valueTable_[hashKey] = INITIAL_VALUE;
//        
//        return hashKey;
//    }
}
