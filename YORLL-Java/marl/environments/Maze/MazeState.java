/**
 * 
 */
package marl.environments.Maze;

import marl.environments.State;

/**
 * @author pds
 * @since  2013-03-07
 *
 */
public class MazeState implements State<MazeState> {
    
    /**
     * The x coordinate.
     */
    private int x_;
    /**
     * The y coordinate.
     */
    private int y_;
    
    /**
     * Create a default Maze state.
     */
    public MazeState() {
        this(0,0);
    }
    /**
     * Create a Maze state with the specified coordinates.
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public MazeState(int x, int y) {
        x_ = x;
        y_ = y;
    }
    /**
     * Create a Maze state with the same coordinates as the specified MazeState.
     * @param that The MazeState whose coordinates this MazeState should copy
     */
    public MazeState(MazeState that)
    {
        this(that.x_, that.y_);
    }
    
    

    /**
     * @return int The x coordinate of this state
     */
    public int getXPosition() { return x_; }
    /**
     * @return int the y coordinate of this state
     */
    public int getYPosition() { return y_; }

    /**
     * @param int The new x coordinate of this state
     */
    public void setXPosition(int x) { x_ = x; }
    /**
     * @param int The new y coordinate of this state
     */
    public void setYPosition(int y) { y_ = y; }

    /**
     * @param int x The new x coordinate of this state
     * @param int y The new y coordinate of this state
     */
    public void setPosition(int x, int y)
    {
        x_ = x;
        y_ = y;
    }
    /**
     * @param that The state to copy the position of
     */
    @Override
    public void set(MazeState that)
    {
        setPosition(that.x_, that.y_);
    }
    
    
    

    @Override
    public boolean equals(Object obj)
    {
        if( obj instanceof MazeState )
            return cmp(this, (MazeState)obj);
        else
            return false;
    }
    @Override
    public int hashCode()
    {
        return (1000 * y_) + x_;
    }
    
    
    private static boolean cmp(MazeState a, MazeState b)
    {
        return (a.x_ == b.x_) && (a.y_ == b.y_);
    }
    
    @Override
    public String toString()
    {
        return "MazeState[x="+x_+", y="+y_+"]";
    }
}
