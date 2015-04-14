/**
 * 
 */
package marl.observations.observers;

import java.awt.Color;
import java.awt.Graphics2D;

import marl.observations.Observable;
import marl.observations.Observation;
import marl.visualisation.VisualPullObserver;

/**
 * @author scopes
 *
 */
@SuppressWarnings("serial")
public class Tile2dPullObserver extends VisualPullObserver {
    private double minX, maxX, minY, maxY;
    
    public Tile2dPullObserver(Observable observable) {
        super(observable, 1);
    }
    public Tile2dPullObserver(Observable observable, int hertz) {
        super(observable, 1, hertz);
    }
    
    public void set(double minX, double maxX, double minY, double maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    /* (non-Javadoc)
     * @see marl.visualisation.Screen#render(java.awt.Graphics2D)
     */
    @Override
    protected void render(Graphics2D g) {
        
        if( observations != null ) {
            double rangeX = maxX - minX,
                   rangeY = maxY - minY,
                   shiftX = drawable.width  / rangeX,
                   shiftY = drawable.height / rangeY;
            
            g.setColor(Color.white);
            g.drawRect(drawable.x, drawable.y, drawable.width, drawable.height);
            Observation o;
            synchronized (observations) {
                o = observations.peek();
            }
            if( o != null ) {
                for( int i=0; i<o.getInt(0); i++ ) {
                    int posX   = (int) ((o.getDouble((4*i) + 0)-(double)minX) * shiftX),
                        posY   = (int) ((o.getDouble((4*i) + 1)-(double)minY) * shiftY),
                        width  = (int) ((o.getDouble((4*i) + 2))              * shiftX),
                        height = (int) ((o.getDouble((4*i) + 3))              * shiftY);
    
                    g.setColor(Color.white);
                    g.drawRect(drawable.x + posX, drawable.y + posY, width, height);
    
                    g.setColor(new Color(255, 255, 255, 50));
                    g.fillRect(drawable.x + posX, drawable.y + posY, width, height);
                }
            }
        }
    }
    
}
