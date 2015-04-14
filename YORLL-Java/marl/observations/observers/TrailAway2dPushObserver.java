/**
 * 
 */
package marl.observations.observers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import marl.observations.Observation;
import marl.utility.struct.ArrayQueue;
import marl.visualisation.VisualPushObserver;

/**
 * @author pds
 * @since  2013-07-04
 *
 */
@SuppressWarnings("serial")
public class TrailAway2dPushObserver extends VisualPushObserver {
    
    public static final int DEFAULT_MAXSIZE = 50;

    private int minX, maxX, minY, maxY;
    
    private int                     episodeLength;
    private ArrayQueue<Observation> queue;
    private Observation             cur;
    private int                     pos;
    
    
    public TrailAway2dPushObserver(int episodeLength) {
        this(episodeLength, DEFAULT_MAXSIZE);
    }
    public TrailAway2dPushObserver(int episodeLength, int maxSize) {
        this.episodeLength = episodeLength;
        queue = new ArrayQueue<>(Observation[].class, maxSize);
        newTrail();
    }
    
    public void set(int minX, int maxX, int minY, int maxY) {
        this.minX       = minX;
        this.maxX       = maxX;
        this.minY       = minY;
        this.maxY       = maxY;
    }
    
    /* (non-Javadoc)
     * @see marl.visualisation.VisualPushObserver#push(marl.observations.Observation)
     */
    @Override
    public void push(Observation o) {
        if( o != null ) {
            double rangeX = maxX - minX,
                   rangeY = maxY - minY;
            double scaleX = drawable.width  / rangeX,
                   scaleY = drawable.height / rangeY;
            double x      = o.getDouble(0) - minX,
                   y      = o.getDouble(1) - minY;
            
            
            cur.setDouble(pos+0, (int)Math.round((double)drawable.x + (scaleX*x)));
            cur.setDouble(pos+1, (int)Math.round((double)drawable.y + (scaleY*y)));
            cur.setInt(0, cur.getInt(0)+1);
            pos+=2;
            
            if( (o.getNumChars() > 0 && o.getChar(0) == 't') || pos >= episodeLength*2 ) {
                newTrail();
            }
        }
    }
    /**
     * 
     */
    private void newTrail() {
        cur = new Observation(1, episodeLength*2, 0);
        pos = 0;
        queue.offer(cur);
    }
    
    /* (non-Javadoc)
     * @see marl.visualisation.Screen#render(java.awt.Graphics2D)
     */
    @Override
    protected void render(Graphics2D g) {
        if( queue.size() > 0 ) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            Stroke s = g.getStroke();
            
            for( int i=queue.size()-1; i>=0; i-- ) {
                // Calculate the "temperature" of the block in question
                float temperature = 1 - ((float)i/(float)queue.size());
                temperature = Math.min(1.0f, temperature);
                temperature = (float)Math.pow(temperature, 5);
                // Use this to calculate the colour
                float red   = (temperature < 0.5f) ? 1.0f             : (1.0f-(2.0f*(temperature-0.5f))),
                      green = (temperature < 0.5f) ? temperature*2.0f : (1.0f-(2.0f*(temperature-0.5f))),
                      blue  = (temperature > 0.5f) ? Math.min(1.0f, 0.4f+temperature) : 0.0f,
                      alpha = (temperature < 1.0f) ? Math.min(1.0f, 0.2f+temperature) : 1.0f;
                g.setColor(new Color(red, green, blue, alpha));
                g.setStroke(new BasicStroke(10f-(9*(1-temperature)), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                /* */
                Path2D.Double path = new Path2D.Double();
                Observation o = queue.get(i);
                if( o.getInt(0) > 3 ) {
                    path.moveTo((int)o.getDouble(0), (int)o.getDouble(1));
                    
                    for( int j=2; j<o.getInt(0); j++ ) {
                        path.curveTo(
                                o.getDouble((j*2) - 2), o.getDouble((j*2) - 1),
                                o.getDouble((j*2) - 2), o.getDouble((j*2) - 1),
                                o.getDouble((j*2) + 0), o.getDouble((j*2) + 1));
                    }
                    g.draw(path);
                }/**/
                
                
                /* * /
                // get the trail
                Observation o = queue.get(i);
                for( int j=2; j<2*o.getInt(0); j+=2 ) {
                    int x1 = (int)o.getDouble(j-2),
                        y1 = (int)o.getDouble(j-1),
                        x2 = (int)o.getDouble(j+0),
                        y2 = (int)o.getDouble(j+1);
                    
                    g.drawLine(x1,y1, x2,y2);
                }/**/
            }
            
            g.setStroke(s);
            // Draw the outer box
            g.setColor(Color.WHITE);
            g.drawRect(drawable.x, drawable.y, drawable.width, drawable.height);
        }
    }
    
}
