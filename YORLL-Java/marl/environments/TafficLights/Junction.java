/**
 * 
 */
package marl.environments.TafficLights;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @author pds
 * @since  2013-05-22
 *
 */
public class Junction
{
    public static class Terminal extends Junction {
        @Override
        public boolean addExit(Lane exit) {
            return false;
        }
    }
    public static class Generator extends Junction {
        @Override
        public boolean addEntrance(Lane entrance) {
            return false;
        }
    }
    
    private Collection<Lane> entranceLanes_;
    private Collection<Lane> exitLanes_;
    private Lane             openLane_;
    

    /**
     * 
     */
    public Junction()
    {
        entranceLanes_ = new ArrayList<>();
        exitLanes_     = new ArrayList<>();
        openLane_      = null;
    }
    
    
    public boolean addEntrance(Lane entrance) {
        return entranceLanes_.add(entrance);
    }
    public boolean addExit(Lane exit) {
        return exitLanes_.add(exit);
    }
    public Lane getOpenLane() {
        return openLane_;
    }
}
