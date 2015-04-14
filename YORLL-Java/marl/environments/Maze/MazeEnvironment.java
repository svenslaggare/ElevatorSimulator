/**
 * 
 */
package marl.environments.Maze;

import java.io.IOException;

import marl.agents.Agent;
import marl.environments.Environment;
import marl.environments.PuddleWorld.PuddleWorldAction;
import marl.observations.Observation;
import marl.utility.Config;

/**
 * Maze environment requires the following in the configuration file for it
 * to work:
 * 
 * 
 * ## Maze Environment settings
 * # maze layout file
 * num_agents  = 1
 * maze_width  = 12
 * maze_height = 12
 * maze_file   = path/to/the/mazeLayout.txt
 * num_states  = 144
 * #  visual the experiment [true|false]
 * display_visualiser = true
 * 
 * 
 * On top of this the maze layout file which should look like this, where the
 * lower case letter indicates the start position of the agent the capital the
 * goal, the asterisks `*` indicate maze walls, and periods `.` indicate maze
 * paths:
 * 
 *      ************
 *      *...*......*
 *      *a*.*.****.*
 *      ***.*....*.*
 *      *....***.*.*
 *      ****.*.*.*.*
 *      *..*.*.*.*.*
 *      **.*.*.*.*.*
 *      *........*.*
 *      ******.***.*
 *      *......*A..*
 *      ************
 *      
 *      
 *      ~~ OR ~~
 *      
 *      
 *      ************************
 *      *...*......**......*...*
 *      *a*.*.****.*..****.*.*b*
 *      ***.*....*....*....*.***
 *      *....***.*.**.*.***....*
 *      ****.*.*.*.**.*.*.*.****
 *      *..*.*.*.*.**.*.*.*.*..*
 *      **.*.*.*.*.**.*.*.*.*.**
 *      *........*.**.*........*
 *      ******.***.**.***.******
 *      *......*D..**..C*......*
 *      **..****************.***
 *      ***.****************..**
 *      *......*A..**..B*......*
 *      ******.***.**.***.******
 *      *........*.**.*........*
 *      **.*.*.*.*.**.*.*.*.*.**
 *      *..*.*.*.*.**.*.*.*.*..*
 *      ****.*.*.*.**.*.*.*.****
 *      *....***.*.**.*.***....*
 *      ***.*....*....*....*.***
 *      *c..*.****..*.****.*.*d*
 *      *...*......**......*...*
 *      ************************
 * 
 * @author pds
 * @since  2013-03-07
 *
 */
public class MazeEnvironment<A extends Agent<MazeEnvironment<A>>>
    implements Environment<MazeState, A> {
    
    /**
     * An extension of the Environment.Tuple object to house the extra
     * information the Maze Environment needs per agent.
     * @author pds
     * @since  2013-03-07
     *
     */
    private class MazeTuple extends Environment.Tuple<MazeEnvironment<A>, MazeState, A> {
        /**
         * The letter the agent of this tuple is associated with.
         */
        public char      letter_;
        /**
         * The starting state the agent of this tuple is associated with.
         */
        public MazeState start_;
        /**
         * The goal state the agent of this tuple is associated with.
         */
        public MazeState goal_;
        
        public void reset() {
            state.set(start_);
            next.set(state);
            sumReward = 0.0d;
        }
    }
    
    /**
     * The character which represents a wall in the Maze.
     */
    public static final char MAZE_WALL = '*';
    /**
     * The character which represents a path in the Maze.
     */
    public static final char MAZE_PATH = '.';
    /**
     * The actions that are available in the Puddle World Environment.
     */
    protected static final PuddleWorldAction[] envActions_ = PuddleWorldAction.values();
    
    /**
     * The current time of the environment in the current episode.
     */
    protected int      time_;
    /**
     * The configuration of the Maze environment.
     */
    protected Config   cfg_;
    /**
     * The layout of the Maze environment.
     */
    protected char[][] layout_;
    /**
     * The height of the Maze.
     */
    protected int      height_;
    /**
     * The width of the Maze.
     */
    protected int      width_;
    /**
     * The number of agents in the Maze.
     */
    protected int      nAgents_;
    
    
    /**
     * The collection of agents in the Maze.
     */
    protected MazeEnvironment<A>.MazeTuple[] tuples_;
    
    
    //Visualiser
    private MazeVisualiser visualiser_;
    
    
    /**
     * @param cfg The configuration for this Maze environment
     */
    public MazeEnvironment(Config cfg) {
        cfg_ = cfg;
    }
    

    @SuppressWarnings("unchecked")
    @Override
    public void initialise()
    {
        try {
            // Get the configuration for the environment
            nAgents_ = cfg_.getInt("num_agents");
            width_   = cfg_.getInt("maze_width");
            height_  = cfg_.getInt("maze_height");
            String mazeFilePath = cfg_.getString("maze_file");
    
            // Load in the maze
            MazeFileParser mazeFile = new MazeFileParser(width_, height_);
            mazeFile.readFile(mazeFilePath);
            layout_ = mazeFile.getMaze();
    

            // Search for start and goal positions for the agents
            tuples_ = (MazeEnvironment<A>.MazeTuple[])(new MazeEnvironment<?>.MazeTuple[nAgents_]);
            int agentsFound = 0;
            for( int y=0; y<height_; y++ ) {
                for( int x=0; x<width_; x++ ) {
                    // if found a lower case letter
                    if( Character.isLowerCase(layout_[y][x]) ) {
                        MazeTuple tuple   = new MazeTuple();
                        tuple.letter_ = Character.toUpperCase(layout_[y][x]);
                        tuple.agent  = null;
                        tuple.start_  = new MazeState(x, y);
                        tuple.state  = new MazeState(tuple.start_);
                        tuple.next   = new MazeState(tuple.start_);

                        for( int dy=0; dy<height_; dy++ ) {
                            for( int dx=0; dx<width_; dx++ ) {
                                // if found an upper case letter
                                if( Character.isUpperCase(layout_[dy][dx]) && tuple.letter_ == layout_[dy][dx] ) {
                                    tuple.letter_ = Character.toLowerCase(layout_[dy][dx]);
                                    tuple.goal_   = new MazeState(dx, dy);
                                }
                            }
                        }

                        if( Character.isLowerCase(tuple.letter_) )
                            tuples_[agentsFound++] = tuple;
                    }
                }
            }

            // Remove all traces of the start and terminal points
            for( int y=0; y<height_; y++ )
                for( int x=0; x<width_; x++ )
                    if( Character.isAlphabetic(layout_[y][x]) )
                        layout_[y][x] = MAZE_PATH;
            
            
            // initialise the visualiser
            if( cfg_.getBoolean("display_visualiser") ) {
                visualiser_ = new MazeVisualiser(cfg_,layout_);
                visualiser_.set(0, width_, 0, height_, width_, height_);
            }
            
        } catch(IOException ex) {
            // do nothing
        }
    }

    @Override
    public void reset(int episodeNo)
    {
        // Reset the environment
        for( int i=0; i<nAgents_; i++ ) {
            tuples_[i].reset();
            tuples_[i].agent.reset(episodeNo);
        }
        time_ = 0;
    }

    @Override
    public boolean add(A agent)
    {
        for( int i=0; i<nAgents_; i++ ) {
            if( tuples_[i].agent == null ) {   // assume that a MazeAgent has been provided
                tuples_[i].agent = agent;
                tuples_[i].agent.add(this);
                return true;
            }
        }

        return false;
    }

    @Override
    public MazeState getState(A agent)
    {
        for( int i=0; i<nAgents_; i++ )
            if( tuples_[i].agent == agent )
                return tuples_[i].state;
        
        return null;
    }

    @Override
    public boolean inTerminalState()
    {
        for( int i=0; i<nAgents_; i++ )
            if( !tuples_[i].state.equals(tuples_[i].goal_) )
                return false;

        return true;
    }

    @Override
    public void performAction(A agent, int action)
    {
        for( int i=0; i<nAgents_; i++ )
            if( tuples_[i].agent == agent )
                attemptAction(tuples_[i], action);
    }
    /**
     * Attempts the action without conflict resolution, this means
     * it makes note of the request action of the agent in the tuple
     * and checks the basic validility of the action. Conflict resolution
     * happens once all agents have attempted their actions.
     */
    private void attemptAction(MazeTuple tuple, int action)
    {
        move(tuple.next, action);
        if( !isValidPosition(tuple.next) )
            tuple.next.set(tuple.state);  
    }

    @Override
    public void incrementTime()
    {
        // inform the visualiser
        informVisualiser();
        
        
        // Let each agent choose what to do
        for( int i=0; i<nAgents_; i++ )
            tuples_[i].agent.step(time_);

        // handle conflicts
        conflictResolution();

        // update
        for( int i=0; i<nAgents_; i++ ) {
            tuples_[i].agent.update(-1.0d, false);
            tuples_[i].addReward(-1.0d);
            if( tuples_[i].state.equals(tuples_[i].goal_) ) {
                tuples_[i].agent.update(100.0d, true);
                tuples_[i].addReward(100.0d);
            }
        }
        
        if( inTerminalState() )
            // inform the visualiser
            informVisualiser();

        time_++;               // increment the time counter
    }
    
    private void informVisualiser() {
        if( visualiser_ != null ) {
            Observation ob = new Observation(1, 2*nAgents_, 0);
            for( int i=0; i<nAgents_; i++ ) {
                ob.setDouble((2*i),   tuples_[i].state.getXPosition());
                ob.setDouble((2*i)+1, tuples_[i].state.getYPosition());
            }
            visualiser_.push(ob);
        }
    }
    
    
    public double getSumReward(A agent)
    {
        for( int i=0; i<nAgents_; i++ )
            if( tuples_[i].agent == agent )
                return tuples_[i].sumReward;
        
        return -1.0;
    }

    /**
     * Determine if there are any conflicts to resolve and if
     * so resolve them. There are 3 different ways to resolve
     * conflict:
     *      - negate actions of all involved
     *      - randomly choose who may perform the action
     *      - choose by preference who may perform the action
     *      
     *      
     * This conflict resolution blocks the action from taking place. It assumes that
     * the current state, hence the starting state, must be valid and then blocks any
     * movements that would leave two agents in the same location in the maze.
     */
    private void conflictResolution()
    {
        // for each agent
        for( int i=0; i<nAgents_; i++ ) {
            // if moving do conflict checking
            if( !tuples_[i].next.equals(tuples_[i].state) ) {
                boolean hasConflict = false;
                // check for other agents moving into the same state
                for( int j=0; j<nAgents_; j++ )
                    if( i != j )    // if not the same agent
                        // if going to be in the same location and the other one is moving also
                        if( tuples_[i].next.equals(tuples_[j].next) )
                            hasConflict = true;

                if( !hasConflict )
                    tuples_[i].state.set(tuples_[i].next);   // allow the move
                else
                    tuples_[i].next.set(tuples_[i].state);   // block movement
            }
        }
    }
    
    
    @Override
    public int getNumActions(A agent) {
        return envActions_.length;
    }

    /**
     * Moves the given state in the direction the action
     * specifies with no regard to validity.
     */
    protected void move(MazeState state, int action)
    {
        switch( MazeAction.values()[action] ) {
            case EAST:
                state.setXPosition(state.getXPosition() + 1);
                break;
            case WEST:
                state.setXPosition(state.getXPosition() - 1);
                break;
            case SOUTH:
                state.setYPosition(state.getYPosition() + 1);
                break;
            case NORTH:
                state.setYPosition(state.getYPosition() - 1);
                break;
        }
    }

    /**
     * Returns true if the specified state is a valid position
     * in the maze for an agent to be in.
     * @return True if a valid position, otherwise false
     */
    protected boolean isValidPosition(MazeState state)
    {
        int dx = state.getXPosition(),
            dy = state.getYPosition();
    
            return (dx>=0 && dx<width_ && dy>=0 && dy<height_ && layout_[dy][dx]!=MAZE_WALL);
    }
}
