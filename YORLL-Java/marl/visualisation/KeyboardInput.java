package marl.visualisation;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * <p>This Class was written by Tim Wright, 12-11-2008. All credit goes to
 * him, thank you very much Tim 'tis a very useful class indeed!</p>
 * <p>It was access and copied on 12-11-2008, from
 * <a href="http://www.gamedev.net/reference/programming/features/javainput/page2.asp">
 * http://www.gamedev.net/reference/programming/features/javainput/page2.asp}</a>.</p>
 * 
 * 
 * @author Tim Wright
 *  
 */
public class KeyboardInput implements KeyListener, InputMonitor
{
    private static final int KEY_COUNT = 256;
    
    private enum KeyState {
        RELEASED, // Not down
        PRESSED,  // Down, but not the first time
        ONCE      // Down for the first time
    }
    
    // Current state of the keyboard
    private boolean[] currentKeys = null;
    
    // Polled keyboard state
    private KeyState[] keys = null;
    
    public KeyboardInput() {
        currentKeys = new boolean[ KEY_COUNT ];
        keys = new KeyState[ KEY_COUNT ];
        
        clear();
    }
    
    /**
	 * @see scopesproject.io.InputMonitor#poll()
	 */
    @Override
    public synchronized void poll() {
        for( int i = 0; i < KEY_COUNT; ++i ) {
            // Set the key state 
            if( currentKeys[ i ] ) {
                // If the key is down now, but was not
                // down last frame, set it to ONCE,
                // otherwise, set it to PRESSED
                if( keys[ i ] == KeyState.RELEASED )
                    keys[ i ] = KeyState.ONCE;
                else
                    keys[ i ] = KeyState.PRESSED;
            } else {
                keys[ i ] = KeyState.RELEASED;
            }
        }
    }
    /**
	 * @see scopesproject.io.InputMonitor#clear()
	 */
    @Override
    public synchronized void clear() {
    	for( int i = 0; i < KEY_COUNT; ++i ) {
    		currentKeys[ i ] = false;
    		keys[ i ] = KeyState.RELEASED;
        }
    }
    
    /**
     * <p>Check to see if the specified {@link KeyEvent key event}
     * <code>keyCode</code> was pressed or still is down since the
     * input monitor was last polled.</p>
     * @param keyCode The key code to check
     * @return True if was pressed or still is pressed
     */
    public boolean keyDown( int keyCode ) {
        return keys[ keyCode ] == KeyState.ONCE ||
        keys[ keyCode ] == KeyState.PRESSED;
    }
    
    /**
     * <p>Check to see if the specified {@link KeyEvent key event}
     * <code>keyCode</code> was pressed and wasn't down last time the
     * input monitor was last polled.</p>
     * @param keyCode The key code to be check
     * @return True if was pressed not last time
     */
    public boolean keyDownOnce( int keyCode ) {
        return keys[ keyCode ] == KeyState.ONCE;
    }
    
    /**
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    @Override
    public synchronized void keyPressed( KeyEvent e ) {
        int keyCode = e.getKeyCode();
        if( keyCode >= 0 && keyCode < KEY_COUNT ) {
            currentKeys[ keyCode ] = true;
        }
    }
    
    /**
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    @Override
    public synchronized void keyReleased( KeyEvent e ) {
        int keyCode = e.getKeyCode();
        if( keyCode >= 0 && keyCode < KEY_COUNT ) {
            currentKeys[ keyCode ] = false;
        }
    }
    
    /**
     * <p>This method is not needed.</p>
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    @Override
    public void keyTyped( KeyEvent e ) {
        // Not needed
    }
}
