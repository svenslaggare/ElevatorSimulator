package marl.agents.learning.qlearning;

import java.util.HashMap;
import java.util.Map;

import marl.environments.State;


/**
 * Discrete Q-Table is an implementation of a Q-Table specifically made for
 * Discrete Q-Learning. It allows for simple access and updates of Q values
 * within the table of state representations with discrete actions. That is to
 * say natural number valued actions.
 *
 * The table uses a state access control mechanism, by this I mean it doesn't
 * keep track itself of the number of actions each state representation has but
 * assumes that it will be informed if the number of actions it should be
 * considering changes.
 *
 * A nice feature of this Q-Table is that it isn't required before hand to
 * know the number of state representations that will be encountered along the
 * way but can dynamically increase it's size when needed. This obviously has a
 * latent cost so where possible it is recommended to use the
 * DiscreteQTable(int nStates) as the constructor to give it a large starting
 * size since it defaults to a maximum size of 10.
 * 
 * Note: That if you underestimate the number of states in the constructor the
 * dynamic increase of size will still happen.
 * 
 * @author Pete Scopes
 * @version 06/09/2012
 */
public class DiscreteQTable
{
    private static final class QValues {
        double[] values;
        public QValues(int nActions, double initialValue) {
            values = new double[nActions];
            for( int i=0; i<nActions; i++ )
                values[i] = initialValue;
        }
    }
    /**
     * These values are used for the normalisation state values, if you require
     * states to have higher values than this either reconsider your choice of
     * values or alter them in you local copy of the library.
     */
	public static final double MAX_VALUE =  1000000.0,
							   MIN_VALUE = -1000000.0;
    /**
     * The initial value that the value should take.
     */
    public static double       INITIAL_VALUE = 0.0d;
    public static void setInitialValue(double value) {
        INITIAL_VALUE = value;
    }
    /**
     * The Q table
     */
//    private double[][] qValues_;
    /**
     * The hash table holding the links between
     * hashed states and their positions in the Q Table
     */
//    private long[]     hashTable_;
    private HashMap<Integer, QValues> hashTable;
    /**
     * The current size of the table
     */
//    private int        size_;
    /**
     * The maximum size of the Q table
     */
    private int        maxSize;
    /**
     * The number of actions
     */
    private int        nActions_;
    
    private Map<Integer, Integer> stateUsage = new HashMap<Integer, Integer>();
    

    /**
     * Constructor for objects of class DiscreteQTable
     */
    public DiscreteQTable()
    {
        this(10);
    }
    /**
     * @param nStates The maximum number of states
     */
    public DiscreteQTable(int nStates)
    {
        if( nStates <= 0 )
            throw new IllegalArgumentException();
        
        maxSize   = nStates;
        hashTable = new HashMap<>(maxSize);
        reset();
    }
    
    
    /**
     * Resets the Q-Table so that it is empty.
     */
    public void reset()
    {
//        size_      = 0;
//        qValues_   = new double[maxSize_][];
//        hashTable_ = new long[maxSize_];
        hashTable.clear();
    }
    
    /**
     * Store a Q-Value of a State in the Q-Table.
     * @param state  The state to store the value against
     * @param action The action to store the value against
     * @param value  The value to be stored
     */
    public void put(State<?> state, int action, double value)
    {
        // Get the hash key of the given state, creating if needed
//        int hashKey = add(state);
        
        // Normalise the value
        if( value < MIN_VALUE ) 	value = MIN_VALUE;
        if( value > MAX_VALUE )		value = MAX_VALUE;
        
        // Update the Q value of the given state and action
//        qValues_[hashKey][action] = value;
        int hashCode = state.hashCode();
        QValues qValues = hashTable.get(hashCode);
        if( qValues != null ) {
            qValues.values[action] = value;
        } else {
            qValues = new QValues(nActions_, INITIAL_VALUE);
            qValues.values[action] = value;
            hashTable.put(hashCode, qValues);
        }
        
        if (this.stateUsage.containsKey(hashCode)) {
        	this.stateUsage.put(hashCode, this.stateUsage.get(hashCode) + 1);
        } else {
        	this.stateUsage.put(hashCode, 1);
        }
    }
    
    /**
     * Retrieve the Q-Value of the given State.
     */
    public double[] get(State<?> state)
    {
        // Get the has key with no safety
//        int hashKey = add(state);
        
        // return the state action pairs
//        return qValues_[hashKey].clone();
        int hashCode = state.hashCode();
        QValues qValues = hashTable.get(hashCode);
        if( qValues == null ) {
            qValues = new QValues(nActions_, INITIAL_VALUE);
            hashTable.put(hashCode, qValues);
        }
        return qValues.values.clone();
    }
    
    /**
     * Returns true if the Q table is aware of the specified state
     * representation.
     * 
     * @param state The state to be checked
     * @return True if the state is present in the Q Table, otherwise false
     */
    public boolean has(State<?> state)
    {
//        return getHashKey(state) != -1;
        return hashTable.containsKey(state.hashCode());
    }
    
    
    /**
     * Inform the Q Table of the current number of actions available for the
     * next state(s) to be queried.
     * @param nActions The number of actions available in the current state
     */
    public void inform(int nActions)
    {
        nActions_ = nActions;
    }
    
    /**
     * Returns the size of the table
     */
    public int size() {
    	return this.hashTable.size();
    }
    
    /**
     * Returns the state usage
     */
    public Map<Integer, Integer> stateUsage() {
    	return this.stateUsage;
    }
    

    /**
     * Returns the has key of the given state or -1 if the state is not present
     * in the Q table.
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
     * Returns the hash key of the given state and if said state is not present
     * it is added and given a hash key.
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
//                double[][] save = new double[maxSize_][];
//                for( int i=0; i<size_; i++ )
//                    save[i] = qValues_[i];
//                
//                qValues_ = save;
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
//        hashTable_[hashKey] = hashValue;
//        qValues_[hashKey]   = new double[nActions_];
//        for( int i=0; i<nActions_; i++ )    // initialise the Q values
//            qValues_[hashKey][i] = INITIAL_VALUE;
//        
//        return hashKey;
//    }
}
