/**
 * 
 */
package marl.environments.TafficLights;

import java.util.ArrayList;
import java.util.List;


/**
 * @author pds
 * @since  2013-05-22
 *
 */
public class Car
{
    private List<Road> route_;
    private int        current_;
    private Behaviour  behaviour_;
    

    /**
     * 
     */
    public Car()
    {
        route_     = new ArrayList<>();
        current_   = -1;
        behaviour_ = Behaviour.Calm;
    }
    
    public List<Road> getRoute() {
        return route_;
    }
    public int getCurrent() {
        return current_;
    }
    public Behaviour getBehaviour() {
        return behaviour_;
    }
}
