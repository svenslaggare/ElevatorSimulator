/**
 * 
 */
package marl.environments.TafficLights;

import java.util.LinkedList;
import java.util.Queue;


/**
 * @author pds
 * @since  2013-05-22
 *
 */
public class Lane
{
    private int        capacity_;
    private Queue<Car> occupants_;
    private Junction   from_;
    private Junction   to_;
    

    /**
     * 
     */
    public Lane(int capacity, Junction from, Junction to)
    {
        capacity_  = capacity;
        occupants_ = new LinkedList<>();
        from_      = from;
        to_        = to;
    }
    
    
    public int getCapacity() {
        return capacity_;
    }
    public Queue<Car> getOccupants() {
        return occupants_;
    }
    public Junction getFrom() {
        return from_;
    }
    public Junction getTo() {
        return to_;
    }
}
