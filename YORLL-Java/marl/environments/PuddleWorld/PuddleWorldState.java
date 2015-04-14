/**
 * 
 */
package marl.environments.PuddleWorld;

import marl.ext.tilecoding.TileCodingState;

/**
 * @author pds
 * @since  2013-03-07
 *
 */
public class PuddleWorldState implements TileCodingState<PuddleWorldState>
{
    /**
     * The x coordinate of the Puddle World State.
     */
    private double x_;
    /**
     * The y coordinate of the Puddle World State.
     */
    private double y_;
    
    
    
    public PuddleWorldState(){ this(0.0,0.0); }
    public PuddleWorldState(double x, double y)
    {
        x_ = x;
        y_ = y;
    }
    public PuddleWorldState(PuddleWorldState that)
    {
        this(that.x_, that.y_);
    }
    
    

    /**
     * @return The x coordinate of this state
     */
    public double getXPosition() { return x_; }
    /**
     * @return the y coordinate of this state
     */
    public double getYPosition() { return y_; }

    /**
     * @param The new x coordinate of this state
     */
    public void setXPosition(double x) { x_ = x; }
    /**
     * @param The new y coordinate of this state
     */
    public void setYPosition(double y) { y_ = y; }

    /**
     * @param x The new x coordinate of this state
     * @param y The new y coordinate of this state
     */
    public void set(double x, double y)
    {
        x_ = x;
        y_ = y;
    }
    /**
     * @param that The state to copy the position of
     */
    @Override
    public void set(PuddleWorldState that)
    {
        set(that.x_, that.y_);
    }
    
    
    /**
     * Move the x position by the given value
     * @param val
     */
    public void moveX(double val)
    {
        x_ += val;
    }
    /**
     * Move the y position by the given value
     * @param val
     */
    public void moveY(double val)
    {
        y_ += val;
    }
    
    
    
    // Tile Coding State extras
    @Override
    public double getFeature(int featureNo) {
        switch( featureNo ) {
            case 0:  return x_;
            default: return y_;
        }
    }
    
    
    

    @Override
    public boolean equals(Object obj)
    {
        if( obj instanceof PuddleWorldState )
            return cmp(this, (PuddleWorldState)obj);
        else
            return false;
    }
    
    
    private static boolean cmp(PuddleWorldState a, PuddleWorldState b)
    {
        return (a.x_ == b.x_) && (a.y_ == b.y_);
    }
    
    @Override
    public String toString()
    {
        return "PuddleWorldState[x="+x_+", y="+y_+"]";
    }
}
