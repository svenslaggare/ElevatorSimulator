/**
 * 
 */
package marl.environments.FireFightingGraph;

import marl.environments.State;


/**
 * @author pds
 * @since  2013
 *
 */
public class FFGState implements State<FFGState>
{
    @SuppressWarnings("unused")
	private FFGHouse[] neighbourhood_;

    /**
     * 
     */
    public FFGState(int n)
    {
        neighbourhood_ = new FFGHouse[n];
    }

    @Override
    public void set(FFGState s) {
        // TODO Auto-generated method stub
        
    }
}
