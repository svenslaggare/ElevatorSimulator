/**
 * 
 */
package marl.environments.MountainCar;

import marl.ext.tilecoding.TileCodingState;

/**
 * @author pds
 * @since  2013-03-07
 *
 */
public class MountainCarState implements TileCodingState<MountainCarState> {
    
    /**
     * The position of the car.
     */
    private double position_;
    /**
     * The velocity of the car.
     */
    private double velocity_;
    
    
    
    
    /**
     * 
     */
    public MountainCarState() {
        this(0.0d,0.0d);
    }
    public MountainCarState(double position, double velocity) {
        position_ = position;
        velocity_ = velocity;
    }
    public MountainCarState(MountainCarState that) {
        this(that.position_, that.velocity_);
    }
    
    

    /**
     * @return double The position of this state
     */
    public double getPosition() { return position_; }
    /**
     * @return double The velocity of this state
     */
    public double getVelocity() { return velocity_; }

    /**
     * @param double The new position of this state
     */
    public void setPosition(double position) { position_ = position; }
    /**
     * @param double The new velocity of this state
     */
    public void setVelocity(double velocity) { velocity_ = velocity; }

    /**
     * @param double x The new position of this state
     * @param double y The new velocity of this state
     */
    public void set(double position, double velocity)
    {
        position_ = position;
        velocity_ = velocity;
    }
    /**
     * @param that The state to copy the position of
     */
    @Override
    public void set(MountainCarState that)
    {
        set(that.position_, that.velocity_);
    }
    
    
    
    // Tile Coding State extras
    @Override
    public double getFeature(int featureNo) {
        switch( featureNo ) {
            case 0:  return position_;
            default: return velocity_;
        }
    }
    
    

    @Override
    public boolean equals(Object obj)
    {
        if( obj instanceof MountainCarState )
            return cmp(this, (MountainCarState)obj);
        else
            return false;
    }
    
    
    private static boolean cmp(MountainCarState a, MountainCarState b)
    {
        return (a.position_ == b.position_) && (a.velocity_ == b.velocity_);
    }
    
    @Override
    public String toString()
    {
        return "State[position="+position_+", velocity="+velocity_+"]";
    }
    
}
