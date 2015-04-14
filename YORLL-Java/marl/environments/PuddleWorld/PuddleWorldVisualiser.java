/**
 * 
 */
package marl.environments.PuddleWorld;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

import marl.observations.observers.FadeAway2dPushObserver;


/**
 * @author pds
 * @since  2013-03-07
 *
 */
@SuppressWarnings("serial")
public class PuddleWorldVisualiser extends FadeAway2dPushObserver
{
    private Puddle[]        puddle    = null;
    private Path2D.Double[] shape     = null;
    private AffineTransform transform = null;
    /**
     * 
     */
    public PuddleWorldVisualiser()
    {
        super();
        transform = new AffineTransform();
        transform.translate(drawable.x, drawable.y);
        transform.scale(drawable.width, drawable.height);
    }
    /**
     * @param maxSize
     */
    public PuddleWorldVisualiser(int maxSize)
    {
        super(maxSize);
        transform = new AffineTransform();
        transform.translate(drawable.x, drawable.y);
        transform.scale(drawable.width, drawable.height);
    }
    
    public void setPuddle(Puddle puddle)
    {
        this.puddle = new Puddle[1];
        this.shape  = new Path2D.Double[1];
        this.puddle[0] = puddle;
        if( puddle instanceof Puddle.Poly ) {
            shape[0] = (Path2D.Double)((Puddle.Poly)puddle).getShape();
            shape[0] = new Path2D.Double(shape[0]);
            shape[0].transform(transform);
        }
    }
    public void setPuddles(Puddle[] puddle)
    {
        this.puddle = puddle;
        this.shape  = new Path2D.Double[puddle.length];
        for( int i=0; i<puddle.length; i++ ) {
            if( puddle[i] instanceof Puddle.Poly ) {
                shape[i] = (Path2D.Double)((Puddle.Poly)puddle[i]).getShape();
                shape[i] = new Path2D.Double(shape[i]);
                shape[i].transform(transform);
            }
        }
    }
    
    
    @Override
    protected void render(Graphics2D g) {
        if( puddle != null ) {
            for( int i=0; i<puddle.length; i++ ) {
                Stroke s = g.getStroke();
                if( puddle[i] instanceof Puddle.Line ) {
                    Puddle.Line p   = (Puddle.Line)puddle[i];
                    int    numParts = p.getNumLines();
                    double radius   = p.getRadius();
        
                    // draw on the puddles
                    g.setColor(new Color(0.0f, 0.0f, 1.0f, 0.25f));                
                    g.setStroke(new BasicStroke((float)((double)drawable.width * (2.0*radius)), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    for( int j=0; j<numParts; j++ ) {
                        Line2D.Double coords = p.getPart(j);
                        g.drawLine(
                                drawable.x + (int)((double)drawable.width *coords.x1),
                                drawable.y + (int)((double)drawable.height*coords.y1),
                                drawable.x + (int)((double)drawable.width *coords.x2),
                                drawable.y + (int)((double)drawable.height*coords.y2));
                    }
                }
                if( puddle[0] instanceof Puddle.Poly ) {
                    Puddle.Poly p  = (Puddle.Poly)puddle[0];
                    double radius  = p.getRadius();
                    
                 // draw on the puddles
                    g.setColor(new Color(0.0f, 0.0f, 1.0f, 0.75f));                
                    g.setStroke(new BasicStroke((float)((double)drawable.width * (radius)), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g.fill(shape[i]);
    
                    g.setColor(new Color(0.0f, 0.0f, 1.0f, 0.25f));  
                    Line2D.Double coords;
                    for( int j=0; (coords = p.getLine(j)) != null; j++ ) {
                        g.drawLine(
                                drawable.x + (int)((double)drawable.width *coords.x1),
                                drawable.y + (int)((double)drawable.height*coords.y1),
                                drawable.x + (int)((double)drawable.width *coords.x2),
                                drawable.y + (int)((double)drawable.height*coords.y2));
                    }
                }
                g.setStroke(s);
            }
        }
        // draw on the terminal area
        g.setColor(new Color(0.0f, 1.0f, 0.0f, 0.25f));
        g.fillRect(
                drawable.x + (int) ((double)drawable.width *0.95),
                drawable.y + (int) ((double)drawable.height*0.95),
                (int) ((double)drawable.width *0.05),
                (int) ((double)drawable.height*0.05));
        
        
        // then do standard render
        super.render(g);
    }
}
