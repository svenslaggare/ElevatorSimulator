package marl.agents.selection;


/**
 * The Exploration interface defines a means for agents to choose between
 * different possible actions based upon some numerical valuation.
 * 
 * Agents inform the Environment which action they are to take by giving it the
 * reference integer assigned to said action. The integers are assigned from
 * 0 - n, where n is the number of available actions. Because of this an array
 * of doubles with the action/value pairs can be provided and then an
 * implementing algorithm will return the index of the action chosen.
 * 
 * @author pds
 *
 */
public interface Exploration
{
    /**
     * This method should select one of the indices of the given array and
     * return it. How it is decided and what the values should represent are
     * defined by the implementing class.
     * 
     * @param actionValuePairs An array of values for the index action choice
     * @return The index action that has been selected
     */
    int select(double[] actionValuePairs);
}
