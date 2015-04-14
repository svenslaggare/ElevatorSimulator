/**
 * 
 */
package marl.environments.RandomWalk;

import marl.ext.tilecoding.TileCodingState;


/**
 * @author pds
 * @since  2013-07-12
 *
 */
public class RandomWalkState implements TileCodingState<RandomWalkState>
{
    /**
     * The position in the walk.
     */
    private int position;

    
    public RandomWalkState()
    {
        position = 0;
    }
    public RandomWalkState(int position) {
        this.position = position;
    }
    public RandomWalkState(RandomWalkState that) {
        set(that);
    }

    
    /**
     * @return The position in the walk
     */
    public int getPosition() {
        return position;
    }
    /**
     * Sets the position in the walk
     * @param position The new position in the walk
     */
    public void setPosition(int position) {
        this.position = position;
    }
    /**
     * Move the position of this state by the specified amount. Can be negative.
     * Does not check to see if the new position is valid.
     * 
     * @param amount The amount to move by
     */
    public void movePosition(int amount) {
        this.position += amount;
    }

    /**
     * Set the position of this state to the same as that state.
     * @param that That state
     */
    @Override
    public void set(RandomWalkState that) {
        this.position = that.position;
    }


    /**
     * @param featureNo The number of the feature to get
     */
    @Override
    public double getFeature(int featureNo) {
        return position;
    }
    
    @Override
    public String toString() {
        return "State["+position+"]";
    }
    
    @Override
    public int hashCode() {
        return position;
    }
    
    @Override
    public boolean equals(Object obj) {
        if( obj instanceof RandomWalkState ) {
            RandomWalkState that = (RandomWalkState)obj;
            return this.position == that.position;
        }
        return false;
    }
}
