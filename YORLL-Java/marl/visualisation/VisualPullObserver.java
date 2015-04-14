/**
 * 
 */
package marl.visualisation;

import java.awt.Component;


import marl.observations.Observable;
import marl.observations.Observation;
import marl.observations.PullObserver;
import marl.utility.struct.ArrayQueue;


/**
 * @author pds
 *
 */
@SuppressWarnings("serial")
public abstract class VisualPullObserver extends Screen implements PullObserver
{
    public static final int DEFAULT_MAXSIZE = -1,
                            DEFAULT_HERTZ   = 4;
    
    private   Observable              observable;
    protected ArrayQueue<Observation> observations;
    private   long                    waitTime;
    private   int                     waitNanos;

    /**
     * 
     */
    public VisualPullObserver(Observable observable)
    {
        this(observable, DEFAULT_MAXSIZE, DEFAULT_HERTZ);
    }
    public VisualPullObserver(Observable observable, int maxSize) {
        this(observable, maxSize, DEFAULT_HERTZ);
    }
    public VisualPullObserver(Observable observable, int maxSize, int hertz) {
        this.observable   = observable;
        this.observations = new ArrayQueue<>(Observation[].class, maxSize);
        this.waitTime     = (long) (1000.0    / (double)hertz);
        if( (hertz-(waitTime*1000)) > 0 )
            this.waitNanos    = (int)  (1000000.0 / (double)(hertz-(this.waitTime*1000)));
        else
            this.waitNanos    = 0;
        
        // Start the Refresher Thread
        (new Thread(this)).start();
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
            // perform the pull method
            pull(observable.getObservation());
            
            synchronized (this) {
                try {
                    this.wait(waitTime, waitNanos);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public void pull(Observation observation) {
        // get a new observation
        synchronized (observations) {
            observations.offer(observation);
        }
    }
}
