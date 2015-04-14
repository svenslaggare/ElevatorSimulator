package marl.agents.selection;

import marl.utility.Rand;


/**
 * Argmax is an implementation of Argument Maximum selection policy.
 *
 * The Argmax policy is to select the action with the largest associated Q-Value
 * breaking ties by a random choice between the tying actions. Thus only one
 * action is ever selected.
 * 
 * @author Pete Scopes
 * @version 2012-09-06
 */
public class Argmax
{
    /**
     * Constructor for objects of class Argmax
     */
    private Argmax() {}
    
    
    
    /**
     * Selects the argument maximum ranked action from the given actions.
     * @param DiscreteActionValues The actions, with their values, to select from
     * @param int nActions         The number of actions to choose from
     * @return The argmax action
     */
    public static int select(double[] stateActionPairs)
    {
        int    nTies      = 1,                    // actually the number of ties plus 1
               bestAction = 0;                    // get the first option and for now
        double bestValue  = stateActionPairs[0],  // assume it is the best action/value
               value;                             // create space for a temp value to be stored
        
        
                                                 // start searching for the argmax action
                                                 // obviously no need to check the first one!
        for( int i=1; i<stateActionPairs.length; i++ ) {
            value = stateActionPairs[i];
            if( value > bestValue ) {            // if the action is better
                bestValue  = value;              // store the new best value
                bestAction = i;                  // and action
                nTies      = 1;                  // and reset the number of ties
            }
            else if( value == bestValue ) {      // if the same as the best value
                nTies++;                         // increment the number of ties
                                                 // randomly decide between them
                if( (Rand.INSTANCE.nextInt()%nTies) == 0 ) {
                    bestValue  = value;
                    bestAction = i;
                }
            }
        }
        
        return bestAction;
    }
    
    public static boolean compare(double[] pairsA, double[] pairsB)
    {
        return selectNoRandom(pairsA) == selectNoRandom(pairsB);
    }
    private static int selectNoRandom(double[] stateActionPairs)
    {
        int    bestAction = 0;                    // get the first option and for now
        double bestValue  = stateActionPairs[0],  // assume it is the best action/value
               value;                             // create space for a temp value to be stored
        
        
                                                  // start searching for the argmax action
                                                  // obviously no need to check the first one!
        for( int i=1; i<stateActionPairs.length; i++ ) {
            value = stateActionPairs[i];
            if( value > bestValue ) {             // if the action is better
                bestValue  = value;               // store the new best value
                bestAction = i;                   // and action
            }
        }
        
        return bestAction;
    }
    
    public static boolean inverseCompare(double[] pairsA, double[] pairsB)
    {
        return inverseSelectNoRandom(pairsA) == inverseSelectNoRandom(pairsB);
    }
    private static int inverseSelectNoRandom(double[] stateActionPairs)
    {
        int    bestAction = 0;                    // get the first option and for now
        double bestValue  = stateActionPairs[0],  // assume it is the best action/value
               value;                             // create space for a temp value to be stored
        
        
                                                  // start searching for the argmax action
                                                  // obviously no need to check the first one!
        for( int i=1; i<stateActionPairs.length; i++ ) {
            value = stateActionPairs[i];
            if( value < bestValue ) {             // if the action is better
                bestValue  = value;               // store the new best value
                bestAction = i;                   // and action
            }
        }
        
        return bestAction;
    }
}
