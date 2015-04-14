/**
 * 
 */
package marl.visualisation;

import marl.observations.Observation;
import marl.observations.PushObserver;


/**
 * @author pds
 *
 */
@SuppressWarnings("serial")
public abstract class VisualPushObserver extends Screen implements PushObserver
{
    @Override
    public abstract void push(Observation o);
}
