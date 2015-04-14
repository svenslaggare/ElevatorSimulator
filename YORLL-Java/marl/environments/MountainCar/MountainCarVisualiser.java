/**
 * 
 */
package marl.environments.MountainCar;

import java.awt.Color;
import java.awt.Graphics2D;

import marl.observations.observers.FadeAway2dPushObserver;


/**
 * @author pds
 * @since  2013-03-07
 *
 */
@SuppressWarnings("serial")
public class MountainCarVisualiser extends FadeAway2dPushObserver
{
    

    /**
     * 
     */
    public MountainCarVisualiser()
    {
        super();
    }


    /**
     * @param maxSize
     */
    public MountainCarVisualiser(int maxSize)
    {
        super(maxSize);
    }
    
    
    @Override
    protected void render(Graphics2D g) {
        double range = MountainCarEnvironment.MAX_POSITION  - MountainCarEnvironment.MIN_POSITION,
               goal  = MountainCarEnvironment.GOAL_POSITION -  MountainCarEnvironment.MIN_POSITION,
               pos   = goal / range;
        // draw on the terminal area
        g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.25f));
        g.fillRect(
                drawable.x + (int) ((double)drawable.width * pos),
                drawable.y,
                (int) ((double)drawable.width * (1.0-pos)),
                (int) ((double)drawable.height));
        
        super.render(g);
    }
}
