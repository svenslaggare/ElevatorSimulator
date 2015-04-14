/**
 * 
 */
package marl.environments.Acrobot;

import marl.agents.Agent;
import marl.environments.Model;
import marl.ext.tilecoding.TileCodingEnvironment;
import marl.utility.Rand;

/**
 * @author pds
 * @since  2013-03-07
 *
 */
public class AcrobotEnvironment<A extends Agent<AcrobotEnvironment<A>>>
    implements TileCodingEnvironment<AcrobotState, A>, Model<AcrobotState> {
    
    /**
     * The goal position of an Acrobot agent in the environment.
     */
    protected static final double GOAL_POSITION = 1.0d;
    
    /**
     * The actions that are available in the Acrobot Environment.
     */
    protected static final AcrobotAction[] envActions_ = AcrobotAction.values();
    
    
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
    protected ModelTuple<AcrobotEnvironment<A>, AcrobotState, A> tuple_;
    /**
     * The current time of the environment in the current episode.
     */
    protected int time_;
    
    /**
     * 
     */
    public AcrobotEnvironment() {}


    /* (non-Javadoc)
     * @see marl.environments.Model#getSample(marl.environments.State, int)
     */
    @Override
    public marl.environments.Model.Sample<AcrobotState> getSample(
            AcrobotState state, int action) {
        
        tuple_.sample_.next.set(state);
        AcrobotState.perform(tuple_.sample_.next, envActions_[action], transitionNoise_);
        
        tuple_.sample_.reward   = getReward(tuple_.sample_.next);
        tuple_.sample_.terminal = isTerminal(tuple_.sample_.next);
        
        
        
        
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
        randomStarts_    = true;
        transitionNoise_ = 0.0d;
        
        // initialise the agent tuple
        tuple_           = new ModelTuple<>();
        tuple_.sample_   = new Sample<>(new AcrobotState(), 0.0, false);
    }


    /* (non-Javadoc)
     * @see marl.environments.Environment#reset(int)
     */
    @Override
    public void reset(int episodeNo) {
        tuple_.state = new AcrobotState();
        if( randomStarts_ ) {
            tuple_.state.set(
                    Rand.INSTANCE.nextDouble() -.5d,
                    Rand.INSTANCE.nextDouble() -.5d,
                    Rand.INSTANCE.nextDouble() -.5d,
                    Rand.INSTANCE.nextDouble() -.5d);
        }
        tuple_.next = new AcrobotState();
        
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
    public AcrobotState getState(A agent) {
        if( agent == tuple_.agent )
            return tuple_.state;
        else
            return null;
    }
    public AcrobotState getState() {
        return new AcrobotState(
                    Rand.INSTANCE.nextDouble() -.5d,
                    Rand.INSTANCE.nextDouble() -.5d,
                    Rand.INSTANCE.nextDouble() -.5d,
                    Rand.INSTANCE.nextDouble() -.5d);
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
    public static boolean isTerminal(AcrobotState state) {
        double feet_height = -(AcrobotState.l1 * Math.cos(state.getTheta1()) + AcrobotState.l2 * Math.cos(state.getTheta2()));

        //New Code
        double firstJointEndHeight = AcrobotState.l1 * Math.cos(state.getTheta1());
        //Second Joint height (relative to first joint)
        double secondJointEndHeight = AcrobotState.l2 * Math.sin(Math.PI / 2 - state.getTheta1() - state.getTheta2());
        
        feet_height = -(firstJointEndHeight + secondJointEndHeight);
        return (feet_height > GOAL_POSITION);
    }


    /* (non-Javadoc)
     * @see marl.environments.Environment#performAction(marl.agents.Agent, int)
     */
    @Override
    public void performAction(A agent, int action) {
        if( agent == tuple_.agent ) {
            tuple_.next.set(tuple_.state);
            AcrobotState.perform(tuple_.next, envActions_[action], transitionNoise_);
            tuple_.state.set(tuple_.next);
        }
    }
    

    /* (non-Javadoc)
     * @see marl.environments.Environment#incrementTime()
     */
    @Override
    public void incrementTime() {
        tuple_.agent.step(time_);
        double reward = rewardPerStep_;
        tuple_.agent.update(reward, false);
        if( inTerminalState() )
            tuple_.agent.update(rewardAtGoal_,  true);

        tuple_.addReward(reward);
        time_++;
    }
    private double getReward(AcrobotState state) {
        if( isTerminal(state) )
            return rewardAtGoal_;
        else
            return rewardPerStep_;
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
            case 0:  return AcrobotState.maxTheta1;
            case 1:  return AcrobotState.maxTheta2;
            case 2:  return AcrobotState.maxTheta1Dot;
            default: return AcrobotState.maxTheta2Dot;
        }
    }
    @Override
    public double getMinimumValue(int feature) {
        switch( feature ) {
            case 0:  return -AcrobotState.maxTheta1;
            case 1:  return -AcrobotState.maxTheta2;
            case 2:  return -AcrobotState.maxTheta1Dot;
            default: return -AcrobotState.maxTheta2Dot;
        }
    }
    @Override
    public int getNumFeatures() {
        return 4;
    }
}
