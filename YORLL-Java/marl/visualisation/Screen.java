package marl.visualisation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author pds
 *
 */
@SuppressWarnings("serial")
public abstract class Screen extends JPanel
{
    private static final int DEFAULT_REFRESH_RATE = 40;
    
    private static final class Hertz {
        long time = System.currentTimeMillis();
        long rendertime;
        int  rate;
    }
    
    // Basic screen fields
    private   Hertz       hertz;
    protected Rectangle   drawable;

    /**
     * 
     */
    public Screen()
    {
        super();
        init();

        // initialise the frame
        JFrame frame = new JFrame("Observations");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Stop resizing
        frame.setResizable(false);
        // finish setting up the frame
        frame.setContentPane(this);
        frame.pack();
        // Make it visible
        frame.setVisible(true);
    }
    private void init() {
        // Set the preferred size
        super.setPreferredSize(new Dimension(620, 620));
        // Set to double buffer
        super.setDoubleBuffered(true);
        
        // ...
        hertz    = new Hertz();
        drawable = new Rectangle(10, 10, 600, 600);
        super.setMinimumSize(new Dimension((int)drawable.getWidth(), (int)drawable.getHeight()));
        
        // ...
        hertz.rate = DEFAULT_REFRESH_RATE;
        
        

        // Start the Refresher Thread
        (new Thread(new Refresher(this, hertz.rate))).start();
    }
    
    
    
    @Override
    public void paint(Graphics graphics) {
        // Cast the graphics to Graphics2D
        Graphics2D g = (Graphics2D)graphics;
        
        // make note of the beginning of the paint operation
        hertz.rendertime = System.currentTimeMillis();
        // repaint the screen
        {
            // Set the rendering hits
            setRenderingHints(g);
            
            // Clear the screen
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            
            // Select the drawer to draw the onto the screen
            render(g);
        }
        // Draw the refresh rate
        hertz.rendertime = System.currentTimeMillis() - hertz.rendertime;
        drawHertz(g);
    }
    /**
     * 
     * @param g The graphics to be used to render an image
     */
    protected abstract void render(Graphics2D g);
    /**
     * <p>Set the screens rendering hints.</p>
     * @param g The graphics to have its rendering hints set
     */
    private void setRenderingHints(Graphics2D g)
    {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,     RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_DITHERING,           RenderingHints.VALUE_DITHER_ENABLE);
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,   RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,       RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,      RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    private void drawHertz(Graphics2D g)
    {
        long now = System.currentTimeMillis(),
             dif = now - hertz.time;
        dif = dif == 0 ? 1 : dif;
        int  hz  = (int) (1000.0 / (double)dif);
        hertz.time = now;
        
        Color c = Color.LIGHT_GRAY;
        if( hz < 0.25*hertz.rate )      c = Color.RED;
        else if( hz < 0.50*hertz.rate ) c = Color.ORANGE;
        else if( hz < 0.75*hertz.rate ) c = Color.YELLOW;
        
        drawString(g, Color.LIGHT_GRAY, drawable.x+2, drawable.y+drawable.height-30, hertz.rendertime+"ms");
        drawString(g, c, drawable.x+2, drawable.y+drawable.height-10, hz + "Hz");
    }
    public static void drawString(Graphics2D g, Color c, int x, int y, String str)
    {
        g.setFont(new Font(null, Font.PLAIN, 12));
        g.setColor(c);
        g.drawString(str, x, y);
    }
}
