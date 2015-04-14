/**
 * 
 */
package marl.environments.PuddleWorld;

import marl.agents.Agent;
import marl.environments.Model;
import marl.ext.tilecoding.TileCodingEnvironment;
import marl.observations.Observation;
import marl.utility.Config;
import marl.utility.Rand;


/**
 * Puddle World Environment requires the following in the configuration file
 * for it to work:
 * 
 * ## Puddle World Environment settings
 * #  display visualiser [true|false]
 * display_visualiser = true
 * 
 * @author pds
 * @since  2013-03-07
 *
 */
public class PuddleWorldEnvironment<A extends Agent<PuddleWorldEnvironment<A>>>
    implements TileCodingEnvironment<PuddleWorldState, A>, Model<PuddleWorldState>
{
    /**
     * The minimum position an agent can have on the x-axis.
     */
    protected static final double MIN_X_POSITION = 0.00d;
    /**
     * The maximum position an agent can have on the x-axis.
     */
    protected static final double MAX_X_POSITION = 1.00d;
    /**
     * The minimum position an agent can have on the y-axis.
     */
    protected static final double MIN_Y_POSITION = 0.00d;
    /**
     * The maximum position an agent can have on the y-axis.
     */
    protected static final double MAX_Y_POSITION = 1.00d;

    
    /**
     * The distance an agent moves in of the compass directions per turn without
     * transition noise.
     */
    protected static final double DISTANCE_MOVE  = 0.05d;
    /**
     * The default starting state for agents in this environment.
     */
    protected static final PuddleWorldState DEFAULT = new PuddleWorldState(0.10d, 0.10d);
    
    /**
     * The actions that are available in the Puddle World Environment.
     */
    protected static final PuddleWorldAction[] envActions_ = PuddleWorldAction.values();
    
    
    //These are configurable
    /**
     * The reward an agent receives per step in the environment.
     */
    protected double   rewardPerStep_;
    /**
     * The reward an agent receives upon reaching the goal state.
     */
    protected double   rewardAtGoal_;
    /**
     * True if environment should have random starting positions,
     * false otherwise.
     */
    protected boolean  randomStarts_;
    /**
     * True if the environment should have transition noise, that is agents
     * might move slightly different to how they requested.
     */
    protected double   transitionNoise_;
    /**
     * The Puddles present in the environment.
     */
    protected Puddle[] puddles_;
    
    
    /**
     * The Tuple to hold the single agent information.
     */
    protected ModelTuple<PuddleWorldEnvironment<A>, PuddleWorldState, A> tuple_;
    /**
     * The current time of the environment in the current episode.
     */
    protected int time_;
    /**
     * The configuration of the Puddle World environment.
     */
    protected Config cfg_;
    
    
    //Visualiser
//    protected PuddleWorldVisualiser visualiser_;
    protected int                   divisionsX = (int) (1.0d / DISTANCE_MOVE),
                                    divisionsY = (int) (1.0d / DISTANCE_MOVE);
    protected PuddleWorldVisualiser2 visualiser2_;
    
    /**
     * @param cfg The configuration for this Puddle World environment
     */
    public PuddleWorldEnvironment(Config cfg) {
        cfg_ = cfg;
    }


    /* (non-Javadoc)
     * @see marl.environments.Model#getSample(marl.environments.State, int)
     */
    @Override
    public marl.environments.Model.Sample<PuddleWorldState> getSample(
            PuddleWorldState state, int action) {
        tuple_.sample_.next.set(state);
        move(tuple_.sample_.next, action);
        if( (tuple_.sample_.terminal = isTerminal(tuple_.sample_.next)) )
            tuple_.sample_.reward = rewardAtGoal_;
        else
            tuple_.sample_.reward = rewardPerStep_ + puddleFine(tuple_.sample_.next);
        
        return tuple_.sample_;
    }


    /* (non-Javadoc)
     * @see marl.environments.Environment#initialise()
     */
    @Override
    public void initialise() {
        // initialise the rewards
        rewardPerStep_   = -1.0d;
        rewardAtGoal_    =  0.0d;
        
        // initialise random starts and noise
        randomStarts_    = false;
        transitionNoise_ = 0.01d;
        
        // initialise the puddles
        puddles_         = new Puddle[2];
        puddles_[0]      = new Puddle.Line(0.1d, 0.10d, 0.75d, 0.45d, 0.75d);
        puddles_[1]      = new Puddle.Line(0.1d, 0.45d, 0.40d, 0.45d, 0.80d);
        
        // initialise the agent tuple
        tuple_           = new ModelTuple<>();
        tuple_.sample_   = new Sample<>(new PuddleWorldState(), 0.0, false);
        
        
        // initialise the visualiser
        if( cfg_.getBoolean("display_visualiser") ) {
//            visualiser_ = new PuddleWorldVisualiser(5000);
//            visualiser_.set((int)(MIN_X_POSITION*divisionsX), (int)(MAX_X_POSITION*divisionsX), (int)(MIN_Y_POSITION*divisionsY), (int)(MAX_Y_POSITION*divisionsY), divisionsX, divisionsY);
//            visualiser_.renderGrid(false);
            // set the visualiser puddle
//            visualiser_.setPuddles(puddles_);
            
            //
            visualiser2_ = new PuddleWorldVisualiser2(cfg_.getInt("max_steps"), 10);
            visualiser2_.set((int)(MIN_X_POSITION*divisionsX), (int)(MAX_X_POSITION*divisionsX), (int)(MIN_Y_POSITION*divisionsY), (int)(MAX_Y_POSITION*divisionsY));
            visualiser2_.setPuddles(puddles_);
        }
    }


    /* (non-Javadoc)
     * @see marl.environments.Environment#reset(int)
     */
    @Override
    public void reset(int episodeNo) {
        tuple_.state = new PuddleWorldState(DEFAULT);
        if( randomStarts_ ) {
            do {
                tuple_.state.set(
                        0.95 * Rand.INSTANCE.nextDouble(),
                        0.95 * Rand.INSTANCE.nextDouble());
            } while( isTerminal(tuple_.state) );
        }
        tuple_.next = new PuddleWorldState();
        
        time_             = 0;
        tuple_.sumReward = 0.0d;
        
        // reset the agent
        tuple_.agent.reset(episodeNo);
    }


    /* (non-Javadoc)
     * @see marl.environments.Environment#add(marl.agents.Agent)
     */
    @Override
    public boolean add(A agent) {
        if( tuple_.agent == null ) {
            tuple_.agent = agent;
            agent.add(this);
            
            return true;
        }
        else
            return false;
    }


    /* (non-Javadoc)
     * @see marl.environments.Environment#getState(marl.agents.Agent)
     */
    @Override
    public PuddleWorldState getState(A agent) {
        if( agent == tuple_.agent )
            return tuple_.state;
        else
            return null;
    }
    public PuddleWorldState getState() {
        double x = (MAX_X_POSITION - MIN_X_POSITION)*Rand.INSTANCE.nextDouble() + MIN_X_POSITION;
        double y = (MAX_Y_POSITION - MIN_Y_POSITION)*Rand.INSTANCE.nextDouble() + MIN_Y_POSITION;
        return new PuddleWorldState(x, y);
    }


    /* (non-Javadoc)
     * @see marl.environments.Environment#getNumActions(marl.agents.Agent)
     */
    @Override
    public int getNumActions(A agent) {
        return envActions_.length;
    }


    /* (non-Javadoc)
     * @see marl.environments.Environment#inTerminalState()
     */
    @Override
    public boolean inTerminalState() {
        return isTerminal(tuple_.state);
    }
    public boolean isTerminal(PuddleWorldState state) {
        return state.getXPosition() >= 0.95 && state.getYPosition() >= 0.95;
    }


    /* (non-Javadoc)
     * @see marl.environments.Environment#performAction(marl.agents.Agent, int)
     */
    @Override
    public void performAction(A agent, int action) {
        if( agent == tuple_.agent ) {
            tuple_.next.set(tuple_.state);
            move(tuple_.next, action);
            tuple_.state.set(tuple_.next);
        }
    }


    private void move(PuddleWorldState state, int action) {
        // move in the requested direction
        switch( envActions_[action] ) {
            case EAST:
                state.moveX(+DISTANCE_MOVE);
                break;
            case WEST:
                state.moveX(-DISTANCE_MOVE);
                break;
            case NORTH:
                state.moveY(+DISTANCE_MOVE);
                break;
            case SOUTH:
                state.moveY(-DISTANCE_MOVE);
                break;
        }
        
        // add Gaussian noise
        double XNoise = Rand.INSTANCE.nextGaussian() * transitionNoise_ * DISTANCE_MOVE;
        double YNoise = Rand.INSTANCE.nextGaussian() * transitionNoise_ * DISTANCE_MOVE;
        state.moveX( XNoise );
        state.moveY( YNoise );
        
        
        // push into the bounds
        if( state.getXPosition() > MAX_X_POSITION ) state.setXPosition(MAX_X_POSITION);
        if( state.getXPosition() < MIN_X_POSITION ) state.setXPosition(MIN_X_POSITION);
        if( state.getYPosition() > MAX_X_POSITION ) state.setYPosition(MAX_X_POSITION);
        if( state.getYPosition() < MIN_X_POSITION ) state.setYPosition(MIN_X_POSITION);
    }
    /* (non-Javadoc)
     * @see marl.environments.Environment#incrementTime()
     */
    @Override
    public void incrementTime() {
        // inform the visualiser
        informVisualiser(false);
        
        
        tuple_.agent.step(time_);
        double reward = rewardPerStep_ + puddleFine(tuple_.state);
        tuple_.agent.update(reward, false);
        if( inTerminalState() ) {
            tuple_.agent.update(rewardAtGoal_, true);

            // inform the visualiser
            informVisualiser(true);
        }
        
        tuple_.addReward(reward);
        time_++;
    }
    /**
     * @param state The state to get the fine for
     * @return The fine for being in the a puddle
     */
    private double puddleFine(PuddleWorldState state)
    {
        double totalFine = 0.0d;
        for( int i=0; i<puddles_.length; i++ )
            totalFine += puddles_[i].calcFine(state);
        
        return totalFine;
    }
    
    private void informVisualiser(boolean terminal) {
        if( visualiser2_ != null ) {
            Observation ob = new Observation(0, 2, 1);
            ob.setDouble(0, tuple_.state.getXPosition()*divisionsX);
            ob.setDouble(1, tuple_.state.getYPosition()*divisionsY);
            ob.setChar(0, terminal ? 't' : ' ');
//            visualiser_.push(ob);
            visualiser2_.push(ob);
        }
    }
    
    
    /**
     * @return The sum reward of the agent
     */
    public double getSumReward() {
        return tuple_.sumReward;
    }
    
    
    // Tile Coding Environment extras
    @Override
    public double getMaximumValue(int feature) {
        switch( feature ) {
            case 0:  return MAX_X_POSITION;
            default: return MAX_Y_POSITION;
        }
    }
    @Override
    public double getMinimumValue(int feature) {
        switch( feature ) {
            case 0:  return MIN_X_POSITION;
            default: return MIN_Y_POSITION;
        }
    }
    @Override
    public int getNumFeatures() {
        return 2;
    }
}
