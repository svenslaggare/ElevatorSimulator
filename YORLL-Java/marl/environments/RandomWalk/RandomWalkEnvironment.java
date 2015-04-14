/**
 * 
 */
package marl.environments.RandomWalk;

import marl.agents.Agent;
import marl.environments.Model;
import marl.ext.tilecoding.TileCodingEnvironment;
import marl.observations.Observation;
import marl.observations.observers.FadeAway2dPushObserver;
import marl.utility.Config;
import marl.utility.Rand;


/**
 * @author pds
 * @since  2013-07-12
 *
 */
public class RandomWalkEnvironment<A extends Agent<RandomWalkEnvironment<A>>>
    implements TileCodingEnvironment<RandomWalkState, A>, Model<RandomWalkState>
{
    /**
     * The default initial state once the environment has been reset.
     */
    protected static final RandomWalkState DEFAULT_INITIAL_STATE = new RandomWalkState();
    
    /**
     * The actions that are available in the Puddle World Environment.
     */
    protected static final RandomWalkAction[] envActions_ = RandomWalkAction.values();
    
    
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
    protected double   transitionNoise_;/**
     * The Tuple to hold the single agent information.
     */
    protected ModelTuple<RandomWalkEnvironment<A>, RandomWalkState, A> tuple_;
    /**
     * The current time of the environment in the current episode.
     */
    protected int time_;
    /**
     * The configuration of the Puddle World environment.
     */
    protected Config cfg_;
    
    
    //Visualiser
    protected FadeAway2dPushObserver visualiser_;
    protected int                    divisionsX,
                                     divisionsY = 1;
    
    
    
    /**
     * The size of the environment including the goal state
     */
    protected int size_;

    /**
     * @param cfg The configuration for this Puddle World environment
     */
    public RandomWalkEnvironment(Config cfg)
    {
        cfg_ = cfg;
    }

    /**
     * @return The size of the environment including the goal state
     */
    public int getSize() {
        return size_;
    }

    
    /* (non-Javadoc)
     * @see marl.environments.Model#getSample(marl.environments.State, int)
     */
    @Override
    public marl.environments.Model.Sample<RandomWalkState> getSample(
            RandomWalkState state, int action) {
        tuple_.sample_.next.set(state);
        move(tuple_.sample_.next, action);
        if( (tuple_.sample_.terminal = isTerminal(tuple_.sample_.next)) )
            tuple_.sample_.reward = rewardAtGoal_;
        else
            tuple_.sample_.reward = rewardPerStep_;
        
        return tuple_.sample_;
    }

    @Override
    public void initialise() {
        // initialise the rewards
        rewardPerStep_   = -1.0d;
        rewardAtGoal_    =  0.0d;
        
        // initialise random starts and noise
        randomStarts_    = false;
        transitionNoise_ = 0.0d;
        
        // initialise the agent tuple
        tuple_           = new ModelTuple<>();
        tuple_.sample_   = new Sample<>(new RandomWalkState(), 0.0d, false);
        
        // initialise the size of the environment
        size_            = cfg_.getInt("randomwalk_size");
        
        // initialise the visualiser
        if( cfg_.getBoolean("display_visualiser") ) {
            divisionsX       = size_;
            visualiser_      = new FadeAway2dPushObserver(2*cfg_.getInt("max_steps"));
            visualiser_.set(0, size_, 0, 0, divisionsX, divisionsY);
        }
    }

    @Override
    public void reset(int episodeNo) {
        tuple_.state = new RandomWalkState(DEFAULT_INITIAL_STATE);
        if( randomStarts_ )
            tuple_.state = new RandomWalkState(Rand.INSTANCE.nextInt(size_));
        time_             = 0;
        tuple_.next      = new RandomWalkState();
        tuple_.sumReward = 0.0d;
        
        // reset the agent
        tuple_.agent.reset(episodeNo);
    }

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

    @Override
    public RandomWalkState getState(A agent) {
        if( agent == tuple_.agent )
            return tuple_.state;
        else
            return null;
    }

    @Override
    public int getNumActions(A agent) {
        return envActions_.length;
    }

    @Override
    public boolean inTerminalState() {
        return isTerminal(tuple_.state);
    }
    protected boolean isTerminal(RandomWalkState state) {
        return state.getPosition() == size_;
    }

    @Override
    public void performAction(A agent, int action) {
        if( agent == tuple_.agent ) {
            tuple_.next.set(tuple_.state);
            move(tuple_.next, action);
            tuple_.state.set(tuple_.next);
        }
        
    }
    protected void move(RandomWalkState state, int action) {
        // if move should be "blocked"
        if( Rand.INSTANCE.nextDouble() >= transitionNoise_ ) {
            switch( envActions_[action] ) {
                case FORWARD:
                    if( state.getPosition() < size_ )
                        state.movePosition(1);
                    break;
                case BACKWARD:
                    if( state.getPosition() > 0 )
                        state.movePosition(-1);
            }
        }
    }

    @Override
    public void incrementTime() {
        // inform the visualiser
        informVisualiser(false);
        
        // ...
        tuple_.agent.step(time_);
        tuple_.agent.update(rewardPerStep_, false);
        tuple_.addReward(rewardPerStep_);
        if( inTerminalState() ) {
            tuple_.agent.update(rewardAtGoal_, true);
            tuple_.addReward(rewardAtGoal_);

            // inform the visualiser
            informVisualiser(true);
        }
        
        time_++;
    }
    
    private void informVisualiser(boolean terminal) {
        if( visualiser_ != null ) {
            Observation ob = new Observation(0, 2, 1);
            ob.setDouble(0, tuple_.state.getPosition());
            ob.setDouble(1, 0);
            ob.setChar(0, terminal ? 't' : ' ');
            visualiser_.push(ob);
        }
    }
    
    
    /**
     * @return The sum reward of the agent
     */
    public double getSumReward() {
        return tuple_.sumReward;
    }
    
    

    @Override
    public int getNumFeatures() {
        return 1;
    }

    @Override
    public double getMinimumValue(int feature) {
        return 0;
    }

    @Override
    public double getMaximumValue(int feature) {
        return size_;
    }
    
    
}
