/**
 * 
 */
package marl.observations.observers;

import java.awt.Color;
import java.awt.Graphics2D;


import marl.observations.Observation;
import marl.utility.struct.ArrayQueue;
import marl.visualisation.VisualPushObserver;



/**
 * @author pds
 *
 */
@SuppressWarnings("serial")
public class FadeAway2dPushObserver extends VisualPushObserver
{
    public static final int    DEFAULT_MAXSIZE       = 1000;
    
    private int minX, maxX, minY, maxY;
    private int divisionsX, divisionsY;
    
    
    protected boolean                 renderGrid = true;  
    private   ArrayQueue<Observation> queue;
    private   int[][]                 counter,
                                      terminals;
    private   int                     maxCount;
    
    public FadeAway2dPushObserver() {
        queue = new ArrayQueue<>(Observation[].class, DEFAULT_MAXSIZE);
    }
    public FadeAway2dPushObserver(int maxSize) {
        queue = new ArrayQueue<>(Observation[].class, maxSize);
    }
    
    public void set(int minX, int maxX, int minY, int maxY, int divisionsX, int divisionsY) {
        this.minX       = minX;
        this.maxX       = maxX;
        this.minY       = minY;
        this.maxY       = maxY;
        this.divisionsX = divisionsX;
        this.divisionsY = divisionsY;
        
        counter   = new int[divisionsY][divisionsX];
        terminals = new int[divisionsY][divisionsX];
        maxCount  = 0;
    }
    public void renderGrid(boolean render) {
        renderGrid = render;
    }
    
    @Override
    public void push(Observation o) {
        if( o != null && counter != null ) {
            double rangeX = maxX - minX,
                   rangeY = maxY - minY;
            double blockX = rangeX / divisionsX,
                   blockY = rangeY / divisionsY;
            
            {
                int dx = (int) ((o.getDouble(0)-minX) / blockX),
                    dy = (int) ((o.getDouble(1)-minY) / blockY);
                dx = Math.max(0, Math.min(divisionsX-1, dx));
                dy = Math.max(0, Math.min(divisionsY-1, dy));
                synchronized( counter ) {
                    counter[dy][dx]++;
                }
                if( counter[dy][dx] > maxCount )
                    maxCount = counter[dy][dx];
                
                if( o.getNumChars() > 0 && o.getChar(0) == 't' )
                    synchronized( terminals ) {
                        terminals[dy][dx]++;
                    }
            }
            
            o = queue.offer(o);
            if( o != null ) {
    
                int dx = (int) ((o.getDouble(0)-minX) / blockX),
                    dy = (int) ((o.getDouble(1)-minY) / blockY);
                dx = Math.max(0, Math.min(divisionsX-1, dx));
                dy = Math.max(0, Math.min(divisionsY-1, dy));
                synchronized( counter ) {
                    counter[dy][dx]--;
                }
                if( o.getNumChars() > 0 && o.getChar(0) == 't' )
                    synchronized( terminals ) {
                        terminals[dy][dx]--;
                    }
            }
        }
        
    }
    
    
    /* (non-Javadoc)
     * @see visualisation.Screen#render(java.awt.Graphics2D)
     */
    @Override
    protected void render(Graphics2D g) {
        // set the graphics state
        if( queue != null && counter != null ) {
            int count = 0, counterXY;
            double sizeX  = (double)drawable.width  / (double)divisionsX,
                   sizeY  = (double)drawable.height / (double)divisionsY;
            
            for( int y=0; y<divisionsY; y++ ) {
                for( int x=0; x<divisionsX; x++ ) {
                    synchronized( counter ) {
                        counterXY = counter[y][x];
                    }
                    if( counterXY > 0 ) {
                        // Calculate the "temperature" of the block in question
                        float temperature = (float)counterXY/(float)maxCount;
                        temperature = Math.min(1.0f, temperature);
                        // Use this to calculate the colour
                        float red   = (temperature < 0.5f)  ? 1.0f             : (1.0f-(2.0f*(temperature-0.5f))),
                              green = (temperature < 0.5f)  ? temperature*2.0f : (1.0f-(2.0f*(temperature-0.5f))),
                              blue  = (temperature > 0.5f)  ? Math.min(1.0f, 0.4f+temperature) : 0.0f,
                              alpha = (temperature < 0.25f) ? Math.min(1.0f, 0.4f+(4.0f*temperature)) : 1.0f;
                        g.setColor(new Color(red, green, blue, alpha));
                        
                        g.fillRect(
                                (int)Math.round((double)drawable.x + ((double)x*sizeX)),
                                (int)Math.round((double)drawable.y + (int)((double)y*sizeY)),
                                (int)Math.round(sizeX), (int)Math.round(sizeY));
                    }
                    if( counterXY > count )
                        count = counterXY;
                }
            }
            maxCount = count;
            
            // draw grid
            g.setColor(Color.WHITE);
            g.drawRect(drawable.x, drawable.y, drawable.width, drawable.height);
            if( renderGrid ) {
                for( int i=1; i<divisionsX; i++ )
                    g.drawLine(
                            (int)Math.round((double)drawable.x + ((double)i*sizeX)),
                            drawable.y,
                            (int)Math.round((double)drawable.x + ((double)i*sizeX)),
                            (int)drawable.getMaxY());
                for( int i=1; i<divisionsY; i++ )
                    g.drawLine(
                            drawable.x,
                            (int)Math.round((double)drawable.y + ((double)i*sizeY)),
                            (int)drawable.getMaxX(),
                            (int)Math.round((double)drawable.y + ((double)i*sizeY)));
            }
            
            
            // draw on terminals
            g.setColor(Color.GREEN);
            for( int y=0; y<divisionsY; y++ ) {
                for( int x=0; x<divisionsX; x++ ) {
                    boolean terminal = false;
                    synchronized( terminals ) {
                        terminal = terminals[y][x]>0;
                    }
                    
                    if( terminal )
                        g.drawRect(
                                (int)Math.round((double)drawable.x + ((double)x*sizeX)),
                                (int)Math.round((double)drawable.y + (int)((double)y*sizeY)),
                                (int)Math.round(sizeX), (int)Math.round(sizeY));
                }
            }
        }
    }
}
