/**
 * 
 */
package marl.environments.TafficLights;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;


/**
 * @author pds
 * @since  2013-05-22
 *
 */
public class Road
{
    private Collection<Lane> lanes_;
    private Point2D          startPoint_;
    private Point2D          endPoint_;
    private double           length_;
    

    /**
     * 
     */
    public Road()
    {
        lanes_      = new ArrayList<>();
        startPoint_ = new Point2D.Double();
        endPoint_   = new Point2D.Double();
        length_     = 0.0d;
    }
    
    
    public Collection<Lane> getLanes() {
        return lanes_;
    }
    public Point2D getStartPoint() {
        return startPoint_;
    }
    public Point2D getEndPoint() {
        return endPoint_;
    }
    public double getLength() {
        return length_;
    }
}
