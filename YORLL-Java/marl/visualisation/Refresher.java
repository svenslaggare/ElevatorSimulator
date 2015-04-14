package marl.visualisation;

import java.awt.Component;


/**
 * A simple {@link Runnable runnable} class that will, if run,
 * attempt to cause the specified {@link Component component} to
 * be repainted the specified number of times a second.
 * @author pds
 *
 */
public class Refresher implements Runnable
{
    private Component comp_;
    private long      waitTime_;
    private int       waitNanos_;

    /**
     * Construct a Refresher to refresh the specified component
     * <code>hertz</code> times a second.
     * @param comp  The component to be affected
     * @param hertz The number of times a second the refresh should be called
     */
    public Refresher(Component comp, double hertz)
    {
        comp_     = comp;
        waitTime_ = (long) (1000.0 / hertz);
        if( (hertz-(waitTime_*1000)) > 0 )
            this.waitNanos_    = (int)  (1000000.0 / (double)(hertz-(this.waitTime_*1000)));
        else
            this.waitNanos_    = 0;
    }


    /**
     * This Thread will run until the program exits calling
     * {@link Component#invalidate() invalidate} and then
     * {@link Component#repaint() repaint} the specified number
     * of times a second.
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        while (true) {
            comp_.invalidate();
            comp_.repaint();
            
            synchronized (this) {
                try {
                    this.wait(waitTime_, waitNanos_);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
