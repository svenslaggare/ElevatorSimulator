/**
 * 
 */
package marl.environments.Acrobot;

import marl.ext.tilecoding.TileCodingState;
import marl.utility.Rand;

/**
 * @author pds
 * @since  2013
 *
 */
public class AcrobotState implements TileCodingState<AcrobotState> {
    /*STATIC CONSTANTS*/
    public final static double maxTheta1    = Math.PI;
    public final static double maxTheta2    = Math.PI;
    public final static double maxTheta1Dot = 4 * Math.PI;
    public final static double maxTheta2Dot = 9 * Math.PI;
    public final static double m1           = 1.0;
    public final static double m2           = 1.0;
    public final static double l1           = 1.0;
    public final static double l2           = 1.0;
    public final static double lc1          = 0.5;
    public final static double lc2          = 0.5;
    public final static double I1           = 1.0;
    public final static double I2           = 1.0;
    public final static double g            = 9.8;
    public final static double dt           = 0.05;
    
    
    private double theta1_;
    private double theta2_;
    private double theta1Dot_;
    private double theta2Dot_;
    
    /**
     * 
     */
    public AcrobotState() {
        theta1_    = theta2_    = 0.0;
        theta1Dot_ = theta2Dot_ = 0.0;
    }
    public AcrobotState(double theta1, double theta2, double theta1Dot, double theta2Dot) {
        set(theta1, theta2, theta1Dot, theta2Dot);
    }
    public AcrobotState(AcrobotState that)
    {
        set(that);
    }
    
    
    /**
     * @param theta1    The new value for theta1
     * @param theta2    The new value for theta2
     * @param theta1Dot The new value for theta1Dot
     * @param theta2Dot The new value for theta2Dot
     */
    public void set(double theta1, double theta2, double theta1Dot, double theta2Dot) {
        theta1_    = theta1;
        theta2_    = theta2;
        theta1Dot_ = theta1Dot;
        theta2Dot_ = theta2Dot;
    }
    
    /* (non-Javadoc)
     * @see marl.environments.State#set(marl.environments.State)
     */
    @Override
    public void set(AcrobotState that) {
        this.theta1_    = that.theta1_;
        this.theta2_    = that.theta2_;
        this.theta1Dot_ = that.theta1Dot_;
        this.theta2Dot_ = that.theta2Dot_;
    }
    
    /**
     * @return the theta1
     */
    public double getTheta1() {
        return theta1_;
    }
    
    /**
     * @return the theta2
     */
    public double getTheta2() {
        return theta2_;
    }
    
    /**
     * @return the theta1Dot
     */
    public double getTheta1Dot() {
        return theta1Dot_;
    }
    
    /**
     * @return the theta2Dot
     */
    public double getTheta2Dot() {
        return theta2Dot_;
    }
    
    
    /**
     * @param theta1 the theta1 to set
     */
    public void setTheta1(double theta1) {
        this.theta1_ = theta1;
    }
    
    /**
     * @param theta2 the theta2 to set
     */
    public void setTheta2(double theta2) {
        this.theta2_ = theta2;
    }
    
    /**
     * @param theta1Dot the theta1Dot to set
     */
    public void setTheta1Dot(double theta1Dot) {
        this.theta1Dot_ = theta1Dot;
    }
    
    /**
     * @param theta2Dot the theta2Dot to set
     */
    public void setTheta2Dot(double theta2Dot) {
        this.theta2Dot_ = theta2Dot;
    }
    public static final void perform(AcrobotState state, AcrobotAction action, double transitionNoise) {
        
        double torque = action.value;
        double d1;
        double d2;
        double phi_2;
        double phi_1;

        double theta2_ddot;
        double theta1_ddot;

        //torque is in [-1,1]
        //We'll make noise equal to at most +/- 1
        double theNoise=transitionNoise*2.0d*(Rand.INSTANCE.nextDouble()-.5d);

        torque+=theNoise;

        int count = 0;
        while (!AcrobotEnvironment.isTerminal(state) && count < 4) {
            count++;

            d1 = m1 * Math.pow(lc1, 2) + m2 * (Math.pow(l1, 2) + Math.pow(lc2, 2) + 2 * l1 * lc2 * Math.cos(state.theta2_)) + I1 + I2;
            d2 = m2 * (Math.pow(lc2, 2) + l1 * lc2 * Math.cos(state.theta2_)) + I2;

            phi_2 = m2 * lc2 * g * Math.cos(state.theta1_ + state.theta2_ - Math.PI / 2.0);
            phi_1 = -(m2 * l1 * lc2 * Math.pow(state.theta2Dot_, 2) * Math.sin(state.theta2_) - 2 * m2 * l1 * lc2 * state.theta1Dot_ * state.theta2Dot_ * Math.sin(state.theta2_)) + (m1 * lc1 + m2 * l1) * g * Math.cos(state.theta1_ - Math.PI / 2.0) + phi_2;

            theta2_ddot = (torque + (d2 / d1) * phi_1 - m2 * l1 * lc2 * Math.pow(state.theta1Dot_, 2) * Math.sin(state.theta2_) - phi_2) / (m2 * Math.pow(lc2, 2) + I2 - Math.pow(d2, 2) / d1);
            theta1_ddot = -(d2 * theta2_ddot + phi_1) / d1;

            state.theta1Dot_ += theta1_ddot * dt;
            state.theta2Dot_ += theta2_ddot * dt;

            state.theta1_ += state.theta1Dot_ * dt;
            state.theta2_ += state.theta2Dot_ * dt;
        }
        if (Math.abs(state.theta1Dot_) > maxTheta1Dot) {
            state.theta1Dot_ = Math.signum(state.theta1Dot_) * maxTheta1Dot;
        }

        if (Math.abs(state.theta2Dot_) > maxTheta2Dot) {
            state.theta2Dot_ = Math.signum(state.theta2Dot_) * maxTheta2Dot;
        }
        /* Put a hard constraint on the Acrobot physics, thetas MUST be in [-PI,+PI]
         * if they reach a top then angular velocity becomes zero
         */
        if (Math.abs(state.theta2_) > Math.PI) {
            state.theta2_ = Math.signum(state.theta2_) * Math.PI;
            state.theta2Dot_ = 0;
        }
        if (Math.abs(state.theta1_) > Math.PI) {
            state.theta1_ = Math.signum(state.theta1_) * Math.PI;
            state.theta1Dot_ = 0;
        }
    }
    
    
    
    // Tile Coding State extras
    @Override
    public double getFeature(int featureNo) {
        switch( featureNo ) {
            case 0:  return theta1_;
            case 1:  return theta2_;
            case 2:  return theta1Dot_;
            default: return theta2Dot_;
        }
    }
    
    
    

    @Override
    public boolean equals(Object obj)
    {
        if( obj instanceof AcrobotState )
            return cmp(this, (AcrobotState)obj);
        else
            return false;
    }
    
    
    private static boolean cmp(AcrobotState a, AcrobotState b)
    {
        return (a.theta1_ == b.theta1_) && (a.theta2_ == b.theta2_) &&
                (a.theta1Dot_ == b.theta1Dot_) && (a.theta2Dot_ == b.theta2Dot_);
    }
    
    @Override
    public String toString()
    {
        return "AcrobotState[theta1="+theta1_+", theta2="+theta2_+
                ", theta1Dot="+theta1Dot_+", theta2Dot="+theta2Dot_+"]";
    }
}
