/**
 * 
 */
package marl.environments.MountainCar;

import marl.agents.Agent;
import marl.environments.Model;
import marl.ext.tilecoding.TileCodingEnvironment;
import marl.observations.Observation;
import marl.utility.Config;
import marl.utility.Rand;


/**
 * Mountain Car environment requires the following in the configuration file
 * for it to work:
 * 
 * ## Mountain Car Environment settings
 * #  display visualiser [true|false]
 * display_visualiser = true
 * 
 * @author pds
 * @since  2013-03-07
 *
 */
public class MountainCarEnvironment<A extends Agent<MountainCarEnvironment<A>>>
    implements TileCodingEnvironment<MountainCarState, A>, Model<MountainCarState> {
    
    /**
     * The minimum position a car can have in the environment.
     */
    protected static final double MIN_POSITION  = -1.20d;
    /**
     * The maximum position a car can have in the environment.
     */
    protected static final double MAX_POSITION  =  0.60d;
    /**
     * The minimum velocity a car can have in the environment.
     */
    protected static final double MIN_VELOCITY  = -0.07d;
    /**
     * The maximum velocity a car can have in the environment.
     */
    protected static final double MAX_VELOCITY  =  0.07d;
    
    
    protected static final double GOAL_POSITION =  0.50d;
    
    /**
     * The factor to which the acceleration of the car should scaled.
     */
    protected static final double ACCELERATION_FACTOR =  0.0010d;
    /**
     * The factor to which gravity helps to acceleration the car toward the
     * valley trough.
     */
    protected static final double GRAVITY_FACTOR      = -0.0025d;
    /**
     * The frequency of this hill peaks in the environment.
     */
    protected static final double HILL_PEAK_FREQUENCY =  3.0000d;
    
    /**
     * The default starting state of the car in the environment.
     */
    protected static final MountainCarState DEFAULT   = new MountainCarState(-0.5d, 0.0d);
    
    /**
     * The actions that are available in the Mountain Car Environment.
     */
    protected static final MountainCarAction[] envActions_ = MountainCarAction.values();
    
    
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
     * The Tuple to hold the single agent information.
     */
    protected ModelTuple<MountainCarEnvironment<A>, MountainCarState, A> tuple_;
    /**
     * The current time of the environment in the current episode.
     */
    protected int time_;
    /**
     * The configuration of the Mountain Car environment.
     */
    protected Config cfg_;
    
    
    //Visualiser
//    private MountainCarVisualiser visualiser_;
    private MountainCarVisualiser2 visualiser2_;
    private int                   divisionsX = 200,
                                  divisionsY = 200;
    
    
    /**
     * @param cfg The configuration for this Mountain Car environment
     */
    public MountainCarEnvironment(Config cfg) {
        cfg_ = cfg;
    }
    
    
    /* (non-Javadoc)
     * @see marl.environments.Model#getSample(marl.environments.State, int)
     */
    @Override
    public marl.environments.Model.Sample<MountainCarState> getSample(
            MountainCarState state, int action) {
        
        tuple_.sample_.next.set(state);
        move(tuple_.sample_.next, action);
        tuple_.sample_.reward = getReward(tuple_.sample_.next);
        
        return tuple_.sample_;
    }
    /**
     * Calculate the reward for the 
     * @return
     */
    public double getReward(MountainCarState state) {
        if( isTerminal(state) ) {
            return rewardAtGoal_;
        } else {
            return rewardPerStep_;
        }
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
        transitionNoise_ = 0.0d;
        
        // initialise the agent tuple
        tuple_           = new ModelTuple<>();
        tuple_.sample_   = new Sample<>(new MountainCarState(), 0.0, false);
        
        
        // initialise the visualiser
        if( cfg_.getBoolean("display_visualiser") ) {
//            visualiser_ = new MountainCarVisualiser(10000);
//            visualiser_.set((int)(MIN_POSITION*divisionsX), (int)(MAX_POSITION*divisionsX), (int)(MIN_VELOCITY*divisionsY), (int)(MAX_VELOCITY*divisionsY), divisionsX, divisionsY);
//            visualiser_.renderGrid(false);
            
            visualiser2_ = new MountainCarVisualiser2(100);
            visualiser2_.set((int)(MIN_POSITION*divisionsX), (int)(MAX_POSITION*divisionsX), (int)(MIN_VELOCITY*divisionsY), (int)(MAX_VELOCITY*divisionsY));
        }
    }


    /* (non-Javadoc)
     * @see marl.environments.Environment#reset(int)
     */
    @Override
    public void reset(int episodeNo) {
        tuple_.state = new MountainCarState(DEFAULT);
        if( randomStarts_ ) {
            tuple_.state.set(
                    DEFAULT.getPosition()+.25d*(Rand.INSTANCE.nextDouble()-.5d),
                    DEFAULT.getVelocity()+.025d*(Rand.INSTANCE.nextDouble()-.5d));
        }
        tuple_.next = new MountainCarState();

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
    public MountainCarState getState(A agent) {
        if( agent == tuple_.agent )
            return tuple_.state;
        else
            return null;
    }
    public MountainCarState getState() {
        double position = (MAX_POSITION - MIN_POSITION)*Rand.INSTANCE.nextDouble() + MIN_POSITION;
        double velocity = (MAX_VELOCITY - MIN_VELOCITY)*Rand.INSTANCE.nextDouble() + MIN_VELOCITY;
        return new MountainCarState(position, velocity);
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
    public boolean isTerminal(MountainCarState state) {
        return state.getPosition() >= GOAL_POSITION;
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
    private void move(MountainCarState state, int action) {
        double acceleration = ACCELERATION_FACTOR;
        
        double position = state.getPosition();
        double velocity = state.getVelocity();

        //Noise should be at most
        double thisNoise=2.0d*ACCELERATION_FACTOR*transitionNoise_*(Rand.INSTANCE.nextDouble()-.5d);

        velocity += (thisNoise+((envActions_[action].value)) * (acceleration)) + getSlope(position) * (GRAVITY_FACTOR);
        if (velocity > MAX_VELOCITY) {
            velocity = MAX_VELOCITY;
        }
        if (velocity < MIN_VELOCITY) {
            velocity = MIN_VELOCITY;
        }
        position += velocity;
        if (position > MAX_POSITION) {
            position = MAX_POSITION;
        }
        if (position < MIN_POSITION) {
            position = MIN_POSITION;
        }
        if (position == MIN_POSITION && velocity < 0) {
            velocity = 0;
        }
        
        state.set(position, velocity);
    }

    /**
     * Get the height of the hill at this position
     * @param queryPosition
     * @return
     */
    public double getHeightAtPosition(double queryPosition) {
        return -Math.sin(HILL_PEAK_FREQUENCY * (queryPosition));
    }

    /**
     * Get the slop of the hill at this position
     * @param queryPosition
     * @return
     */
    public double getSlope(double queryPosition) {
        /*The curve is generated by cos(hillPeakFrequency(x-pi/2.0)) so the 
         * pseudo-derivative is cos(hillPeakFrequency* x) 
         */
        return Math.cos(HILL_PEAK_FREQUENCY * queryPosition);
    }
    
    
    /* (non-Javadoc)
     * @see marl.environments.Environment#incrementTime()
     */
    @Override
    public void incrementTime() {
        // inform the visualiser
        informVisualiser(false);
        
        
        tuple_.agent.step(time_);
        double reward = rewardPerStep_;
        tuple_.agent.update(reward, false);
        if( inTerminalState() ) {
            tuple_.agent.update(rewardAtGoal_,  true);

            // inform the visualiser
            informVisualiser(true);
        }

        tuple_.addReward(reward);
        time_++;
    }
    
    private void informVisualiser(boolean terminal) {
        if( visualiser2_ != null ) {
            Observation ob = new Observation(0, 2, 1);
            ob.setDouble(0, tuple_.state.getPosition()*divisionsX);
            ob.setDouble(1, tuple_.state.getVelocity()*divisionsY);
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
            case 0:  return MAX_POSITION;
            default: return MAX_VELOCITY;
        }
    }
    @Override
    public double getMinimumValue(int feature) {
        switch( feature ) {
            case 0:  return MIN_POSITION;
            default: return MIN_VELOCITY;
        }
    }
    @Override
    public int getNumFeatures() {
        return 2;
    }
}
