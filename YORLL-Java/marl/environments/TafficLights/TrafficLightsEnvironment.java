/**
 * 
 */
package marl.environments.TafficLights;

import marl.agents.Agent;
import marl.environments.Environment;


/**
 * @author pds
 * @since  2013
 *
 */
public class TrafficLightsEnvironment<A extends Agent<TrafficLightsEnvironment<A>>>
    implements Environment<TrafficLightsState, A>
{

    /**
     * 
     */
    public TrafficLightsEnvironment()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void initialise() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void reset(int episodeNo) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean add(A agent) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TrafficLightsState getState(A agent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getNumActions(A agent) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean inTerminalState() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void performAction(A agent, int action) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void incrementTime() {
        // TODO Auto-generated method stub
        
    }
}
