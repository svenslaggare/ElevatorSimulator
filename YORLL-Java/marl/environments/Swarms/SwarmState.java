/**
 * 
 */
package marl.environments.Swarms;

import java.awt.geom.Point2D;
import java.util.PriorityQueue;

import marl.environments.State;
import marl.utility.Compass;

/**
 * @author pds
 * @since  2013-07-04
 *
 */
public class SwarmState<E> implements State<SwarmState<E>> {
    
    private Point2D.Double   position_;
    private Compass.Ordinal  direction_;
    private PriorityQueue<E> scope_;

    /**
     * 
     */
    public SwarmState()
    {
        position_  = new Point2D.Double();
        direction_ = Compass.Ordinal.EAST;
        scope_     = new PriorityQueue<>();
    }

    @Override
    public void set(SwarmState<E> that) {
        this.position_.setLocation(that.position_);
        this.direction_ = that.direction_;
        this.scope_     = new PriorityQueue<>(that.scope_);
    }
    
}
