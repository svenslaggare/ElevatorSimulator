/**
 * 
 */
package marl.environments.CartPole;

import marl.agents.Agent;
import marl.environments.Model;
import marl.ext.tilecoding.TileCodingEnvironment;
import marl.utility.Rand;

/**
 * @author pds
 * @since  2013
 *
 */
public class CartPoleEnvironment<A extends Agent<CartPoleEnvironment<A>>>
    implements TileCodingEnvironment<CartPoleState, A>, Model<CartPoleState> {

    /*STATIC CONSTANTS*/
    protected final static double GRAVITY = 9.8;
    protected final static double MASSCART = 1.0;
    protected final static double MASSPOLE = 0.1;
    protected final static double TOTAL_MASS = (MASSPOLE + MASSCART);
    protected final static double LENGTH = 0.5;     /* actually half the pole's length */

    protected final static double POLEMASS_LENGTH = (MASSPOLE * LENGTH);
    protected final static double FORCE_MAG = 10.0;
    protected final static double TAU = 0.02;   /* seconds between state updates */

    protected final static double FOURTHIRDS     = 4.0d / 3.0d;
    protected final static double FAILURE_LEFTCARTBOUND   = -2.4d;
    protected final static double FAILURE_RIGHTCARTBOUND  =  2.4d;
    protected final static double FAILURE_LEFTANGLEBOUND  = -Math.toRadians(12.0d);
    protected final static double FAILURE_RIGHTANGLEBOUND =  Math.toRadians(12.0d);
    
    protected final static double MAXIMUM_LEFTCARTBOUND   = -2.5d;
    protected final static double MAXIMUM_RIGHTCARTBOUND  =  2.5d;
    protected final static double MAXIMUM_LEFTANGLEBOUND  = -Math.toRadians(12.1d);
    protected final static double MAXIMUM_RIGHTANGLEBOUND =  Math.toRadians(12.1d);
    protected final static double MAXIMUM_CARTVELOCITY    = 10.0d;
    protected final static double MAXIMUM_POLEVELOCITY    = 10.0d;
    
    /**
     * The actions that are available in the Cart Pole Environment.
     */
    protected static final CartPoleAction[] envActions_   = CartPoleAction.values();
    
    
    //These are configurable
    /**
     * The reward an agent receives per step in the environment.
     */
    protected double   rewardPerStep_;
    /**
     * The reward an agent receives upon termination.
     */
    protected double   rewardAtTermination_;
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
    protected ModelTuple<CartPoleEnvironment<A>, CartPoleState, A> tuple_;
    /**
     * The current time of the environment in the current episode.
     */
    protected int time_;
    
    /**
     * 
     */
    public CartPoleEnvironment() {}
    
    /* (non-Javadoc)
     * @see marl.environments.Model#getSample(marl.environments.State, int)
     */
    @Override
    public marl.environments.Model.Sample<CartPoleState> getSample(
            CartPoleState state, int action) {
        
        tuple_.sample_.next.set(state);
        perform(tuple_.sample_.next, envActions_[action]);
        
        tuple_.sample_.reward   = isTerminal(tuple_.sample_.next) ? rewardAtTermination_ : rewardPerStep_;
        tuple_.sample_.terminal = isTerminal(tuple_.sample_.next);
        
        
        return tuple_.sample_;
    }


    /* (non-Javadoc)
     * @see marl.environments.Environment#initialise()
     */
    @Override
    public void initialise() {
        // initialise the rewards
        rewardPerStep_       = -1.0d;
        rewardAtTermination_ =  0.0d;
        
        // initialise random starts and noise
        randomStarts_    = true;
        transitionNoise_ = 0.0d;
        
        // initialise the agent tuple
        tuple_           = new ModelTuple<>();
        tuple_.sample_   = new Sample<CartPoleState>(new CartPoleState(), 0.0, false);
    }


    /* (non-Javadoc)
     * @see marl.environments.Environment#reset(int)
     */
    @Override
    public void reset(int episodeNo) {
        tuple_.state = new CartPoleState();
        if( randomStarts_ ) {
            //Going to have the random start states be near to equilibrium
            tuple_.state.set(
                    Rand.INSTANCE.nextDouble()-.5d,
                    Rand.INSTANCE.nextDouble()-.5d,
                    (Rand.INSTANCE.nextDouble()-.5d)/8.0d,
                    (Rand.INSTANCE.nextDouble()-.5d)/8.0d);
        }
        tuple_.next = new CartPoleState();
        
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
    public CartPoleState getState(A agent) {
        if( agent == tuple_.agent )
            return tuple_.state;
        else
            return null;
    }
    public CartPoleState getState() {
        return new CartPoleState(
                Rand.INSTANCE.nextDouble()-.5d,
                Rand.INSTANCE.nextDouble()-.5d,
                (Rand.INSTANCE.nextDouble()-.5d)/8.0d,
                (Rand.INSTANCE.nextDouble()-.5d)/8.0d);
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
    public boolean isTerminal(CartPoleState state) {
        if( state.getCartLocation() < FAILURE_LEFTCARTBOUND  ||
            state.getCartLocation() > FAILURE_RIGHTCARTBOUND ||
            state.getPoleAngle()    < FAILURE_LEFTANGLEBOUND ||
            state.getPoleAngle()    > FAILURE_RIGHTANGLEBOUND ) {
                return true;
        } /* to signal failure */
        return false;
    }

    /* (non-Javadoc)
     * @see marl.environments.Environment#performAction(marl.agents.Agent, int)
     */
    @Override
    public void performAction(A agent, int action) {
        if( agent == tuple_.agent ) {
            tuple_.next.set(tuple_.state);
            perform(tuple_.next, envActions_[action]);
            tuple_.state.set(tuple_.next);
        }
    }
    private void perform(CartPoleState state, CartPoleAction action) {
        // collect the values from the state
        double x         = state.getCartLocation(),
               x_dot     = state.getCartVelocity(),
               theta     = state.getPoleAngle(),
               theta_dot = state.getPoleVelocity();
        
        //...
        double xacc;
        double thetaacc;
        double force;
        double costheta;
        double sintheta;
        double temp;

        switch( action ) {
            case LEFT:
                force = FORCE_MAG;
                break;
            default:
            case RIGHT:
                force = -FORCE_MAG;
                break;
        }

        //Noise of 1.0 means possibly full opposite action
        double thisNoise=2.0d*transitionNoise_*FORCE_MAG*(Rand.INSTANCE.nextDouble()-.5d);

        force+=thisNoise;

        costheta = Math.cos(theta);
        sintheta = Math.sin(theta);

        temp = (force + POLEMASS_LENGTH * theta_dot * theta_dot * sintheta) / TOTAL_MASS;

        thetaacc = (GRAVITY * sintheta - costheta * temp) / (LENGTH * (FOURTHIRDS - MASSPOLE * costheta * costheta / TOTAL_MASS));

        xacc = temp - POLEMASS_LENGTH * thetaacc * costheta / TOTAL_MASS;

        /*** Update the four state variables, using Euler's method. ***/
        x += TAU * x_dot;
        x_dot += TAU * xacc;
        theta += TAU * theta_dot;
        theta_dot += TAU * thetaacc;

        /**These probably never happen because the pole would crash **/
        while (theta >= Math.PI) {
            theta -= 2.0d * Math.PI;
        }
        while (theta < -Math.PI) {
            theta += 2.0d * Math.PI;
        }
        
        
        // force values within maximum bounds
        x         = Math.max( MAXIMUM_LEFTCARTBOUND,  x);
        x         = Math.min( MAXIMUM_RIGHTCARTBOUND, x);
        x_dot     = Math.max(-MAXIMUM_CARTVELOCITY, x_dot);
        x_dot     = Math.min( MAXIMUM_CARTVELOCITY, x_dot);
        theta     = Math.max( MAXIMUM_LEFTANGLEBOUND,  theta);
        theta     = Math.min( MAXIMUM_RIGHTANGLEBOUND, theta);
        theta_dot = Math.max(-MAXIMUM_POLEVELOCITY, theta_dot);
        theta_dot = Math.min( MAXIMUM_POLEVELOCITY, theta_dot);
        
        // update the state values
        state.setCartLocation(x);
        state.setCartVelocity(x_dot);
        state.setPoleAngle(theta);
        state.setPoleVelocity(theta_dot);
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
            tuple_.agent.update(rewardAtTermination_, true);
        
        tuple_.addReward(reward);
        time_++;
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
            case 0:  return MAXIMUM_RIGHTCARTBOUND;
            case 1:  return MAXIMUM_CARTVELOCITY;
            case 2:  return MAXIMUM_RIGHTANGLEBOUND;
            default: return MAXIMUM_POLEVELOCITY;
        }
    }
    @Override
    public double getMinimumValue(int feature) {
        switch( feature ) {
            case 0:  return  MAXIMUM_LEFTCARTBOUND;
            case 1:  return -MAXIMUM_CARTVELOCITY;
            case 2:  return  MAXIMUM_LEFTANGLEBOUND;
            default: return -MAXIMUM_POLEVELOCITY;
        }
    }
    @Override
    public int getNumFeatures() {
        return 4;
    }
    
}
