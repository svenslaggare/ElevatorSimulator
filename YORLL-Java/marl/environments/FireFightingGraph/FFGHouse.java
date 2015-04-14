/**
 * 
 */
package marl.environments.FireFightingGraph;


/**
 * @author pds
 * @since  2013
 *
 */
public class FFGHouse
{
    private int fireLevel_;

    /**
     * 
     */
    public FFGHouse()
    {
        fireLevel_ = 0;
    }
    
    
    
    public int getFireLevel() {
        return fireLevel_;
    }
    
    public void setFireLevel(int fireLevel) {
        fireLevel_ = fireLevel;
    }
}
