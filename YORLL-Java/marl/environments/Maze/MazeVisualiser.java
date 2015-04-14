/**
 * 
 */
package marl.environments.Maze;

import java.awt.Color;
import java.awt.Graphics2D;


import marl.observations.Observation;
import marl.utility.Config;
import marl.utility.struct.ArrayQueue;
import marl.visualisation.VisualPushObserver;



/**
 * @author pds
 *
 */
@SuppressWarnings("serial")
public class MazeVisualiser extends VisualPushObserver
{
    private static final Color[] colours = {
        new Color(  0,  0,180),
        new Color(175, 13,102),
        new Color(146,248, 70),
        new Color(255,200, 47),
        new Color(255,118,  0),
        new Color(185,185,185),
        new Color(235,235,222),
        new Color(100,100,100),
        new Color(255,255,  0),
        new Color( 55, 19,112),
        new Color(255,255,150),
        new Color(202, 62, 94),
        new Color(205,145, 63),
        new Color( 12, 75,100),
        new Color(255,  0,  0),
        new Color(175,155, 50),
        new Color( 37, 70, 25),
        new Color(121, 33,135),
        new Color( 83,140,208),
        new Color(  0,154, 37),
        new Color(178,220,205),
        new Color(255,152,213),
        new Color(  0,  0, 74),
        new Color(175,200, 74),
        new Color( 63, 25, 12),
        new Color(255,255,255),
    };
    
    public static final int DEFAULT_MAXSIZE = 1000;
    public static final double ALPHA_SHIFT_MIN_VALUE = 1.5,
                               ALPHA_SHIFT_CURVE     = 10;
    
    private int minX, maxX, minY, maxY;
    private int divisionsX, divisionsY;
    
    
    private ArrayQueue<Observation> queue;
    private int[][][]               counter;
    private int                     maxCount;
    
    private int nAgents_;
    private char[][] maze_;

    /**
     * 
     */
    public MazeVisualiser(Config cfg, char[][] maze)
    {
        queue = new ArrayQueue<>(Observation[].class, DEFAULT_MAXSIZE);
        nAgents_ = cfg.getInt("num_agents");
        maze_    = maze;
    }


    /**
     * @param maxSize
     */
    public MazeVisualiser(Config cfg, char[][] maze, int maxSize)
    {
        queue = new ArrayQueue<>(Observation[].class, maxSize);
        nAgents_ = cfg.getInt("num_agents");
        maze_    = maze;
    }
    
    public void set(int minX, int maxX, int minY, int maxY, int divisionsX, int divisionsY) {
        this.minX       = minX;
        this.maxX       = maxX;
        this.minY       = minY;
        this.maxY       = maxY;
        this.divisionsX = divisionsX;
        this.divisionsY = divisionsY;
        
        counter  = new int[nAgents_][divisionsY][divisionsX];
        maxCount = 0;
    }

    /* (non-Javadoc)
     * @see visualisation.Screen#render(java.awt.Graphics2D)
     */
    @Override
    public void push(Observation o) {
        if( o != null && counter != null ) {
            double rangeX = maxX - minX,
                   rangeY = maxY - minY;
            double blockX = rangeX / divisionsX,
                   blockY = rangeY / divisionsY;
            
            {
                for( int i=0; i<nAgents_; i++ ) {
                    int dx = (int) ((o.getDouble(2*i)    -minX) / blockX),
                        dy = (int) ((o.getDouble((2*i)+1)-minY) / blockY);
                    synchronized( counter ) {
                        counter[i][dy][dx]++;
                    }
                    if( counter[i][dy][dx] > maxCount )
                        maxCount = counter[i][dy][dx];
                }
            }
            
            o = queue.offer(o);
            if( o != null ) {

                for( int i=0; i<nAgents_; i++ ) {
                    int dx = (int) ((o.getDouble(2*i)    -minX) / blockX),
                        dy = (int) ((o.getDouble((2*i)+1)-minY) / blockY);
                    synchronized( counter ) {
                        counter[i][dy][dx]--;
                    }
                }
            }
        }
        
    }
    
    
    @Override
    protected void render(Graphics2D g) {

        int count = 0;
        int sizeX  = (int)((double)drawable.width  / (double)divisionsX),
            sizeY  = (int)((double)drawable.height / (double)divisionsY);
        
        
        // draw on the underlining maze
        g.setColor(Color.GRAY);
        for( int y=0; y<maze_.length; y++ ) {
            for( int x=0; x<maze_[y].length; x++ )
                if( maze_[y][x] == MazeEnvironment.MAZE_WALL )
                    g.fillRect(
                            (int)Math.round((double)drawable.x + ((double)x*sizeX)),
                            (int)Math.round((double)drawable.y + (int)((double)y*sizeY)),
                            (int)Math.round(sizeX), (int)Math.round(sizeY));
        }
        
        
        // set the graphics state
        if( queue != null && counter != null ) {
            for( int i=0; i<nAgents_; i++ ) {
                for( int y=0; y<divisionsY; y++ ) {
                    for( int x=0; x<divisionsX; x++ ) {
                        if( counter[i][y][x] > 0 ) {
                            synchronized( counter ) {
                                float alpha = (float)counter[i][y][x]/(float)maxCount;
                                alpha = (float) ( Math.log((ALPHA_SHIFT_CURVE*alpha)+ALPHA_SHIFT_MIN_VALUE)
                                                / Math.log(ALPHA_SHIFT_CURVE+ALPHA_SHIFT_MIN_VALUE) );
                                g.setColor(new Color(
                                        colours[i].getRed(),
                                        colours[i].getGreen(),
                                        colours[i].getBlue(),
                                        (int) (alpha*255.0f)));
                                
                                
                            }
                            g.fillRect(
                                    (int)Math.round((double)drawable.x + ((double)x*sizeX) + ((double)i*(sizeX/(double)nAgents_)) ),
                                    (int)Math.round((double)drawable.y + (int)((double)y*sizeY)),
                                    (int)Math.round(sizeX/nAgents_), (int)Math.round(sizeY));
                        }
                        if( counter[i][y][x] > count )
                            count = counter[i][y][x];
                    }
                }
            }
            maxCount = count;
            
        }
        
        // draw grid
        g.setColor(Color.WHITE);
        g.drawRect(drawable.x, drawable.y, drawable.width, drawable.height);
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
}
