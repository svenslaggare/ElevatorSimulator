/**
 * 
 */
package marl.agents.selection;

/**
 * The Decay class contains a set of decay functions that can either be used
 * via the static methods provided or by initialising the class giving type of
 * decay function required. Please see the static methods to see which
 * parameters in the class instantiation affect the function.
 * 
 * @author pds
 * @since  2013-05-30
 *
 */
public abstract class Decay implements Exploration {
    public static void main(String[] args) {
        int maxTime = 100,
            start   = (int) ((double)maxTime * 0.1d),
            over    = (int) ((double)maxTime * 0.8d);
        
        
        Decay constant      = new Decay(Type.CONSTANT,      maxTime, 0.85,       start, over){@Override public int select(double[] actionValuePairs) {return 0;}},
              step          = new Decay(Type.STEP,          over/2,  0.25, 0.75, start, over){@Override public int select(double[] actionValuePairs) {return 0;}},
              linear        = new Decay(Type.LINEAR,        over,    0.10, 0.90, start, over){@Override public int select(double[] actionValuePairs) {return 0;}},
              exponential   = new Decay(Type.EXPONENTIAL,   over/16, start, over){@Override public int select(double[] actionValuePairs) {return 0;}},
              weibull       = new Decay(Type.WEIBULL,       over/8,  start, over){@Override public int select(double[] actionValuePairs) {return 0;}},
              hill          = new Decay(Type.HILL,          over/4,  start, over){@Override public int select(double[] actionValuePairs) {return 0;}},
              smoothcompact = new Decay(Type.SMOOTHCOMPACT, over,    start, over){@Override public int select(double[] actionValuePairs) {return 0;}},
              sine          = new Decay(Type.SINE,          over,    start, over){@Override public int select(double[] actionValuePairs) {return 0;}},
              dampedsine    = new Decay(Type.DAMPEDSINE,    over,    start, over){@Override public int select(double[] actionValuePairs) {return 0;}};
        
        
        System.out.println(
                "constant\tstep\tlinear\texponential\tweibull\thill\tsmoothcompact\tsine\tdampedsine");
        for( int i=0; i<maxTime; i++) {
            double ct = constant.decay(i),
                   sp = step.decay(i),
                   lr = linear.decay(i),
                   el = exponential.decay(i),
                   wl = weibull.decay(i),
                   hl = hill.decay(i),
                   st = smoothcompact.decay(i),
                   se = sine.decay(i),
                   de = dampedsine.decay(i);
            
            System.out.format("%.5f\t%.5f\t%.5f\t%.5f\t%.5f\t%.5f\t%.5f\t%.5f\t%.5f\n",
                    ct, sp, lr, el, wl, hl, st, se, de);
        }
    }
    
    public static enum Type {
        CONSTANT, STEP, LINEAR, EXPONENTIAL, WEIBULL, HILL, SMOOTHCOMPACT,
        SINE, DAMPEDSINE;
    }
    
    /**
     * The Constant decay function experiences no decay.
     * 
     * @param t The current time since the beginning of the decay
     * @param L The time to full decay
     * @param k Not used
     * @return The decayed value
     */
    public static double constant(double t, double L, double k) {
        return 1;
    }
    /**
     * The Step decay function experiences no decay while <code>t < L</code>
     * then it switches to full decay. 
     * 
     * @param t The current time since the beginning of the decay
     * @param L The time to full decay
     * @param k Not used
     * @return The decayed value
     */
    public static double step(double t, double L, double k) {
        return t < L ? 1 : 0;
    }
    /**
     * The Linear decay function experiences a steady decay (constant gradient)
     * from <code>t = 0</code> until <code>t = L</code> where the value becomes
     * 0.
     * 
     * @param t The current time since the beginning of the decay
     * @param L The time to full decay
     * @param k Not used
     * @return The decayed value
     */
    public static double linear(double t, double L, double k) {
        return 1 - t/L;
    }
    /**
     * The Exponential decay function experiences exponential decay where the
     * half-life of the function is <code>L</code>. The value will never reach
     * 0.
     * 
     * @param t The current time since the beginning of the decay
     * @param L The time to half decay
     * @param k Not used
     * @return The decayed value
     */
    public static double exponential(double t, double L, double k) {
        return Math.exp( - (t/L) * Math.log(2) );
    }
    /**
     * The Weibull decay function is equivalent to exponential when
     * <code>k = 1</code>. Values of <code>k > 1</code> will be
     * a convex curve above the exponential curve until <code>t = L</code> then
     * will decrease more quickly; conversely values of <code>k < 1</code> will be
     * a concave curve below the exponential curve until <code>t - L</code> then
     * will decrease more slowly. The value will never reach 0.
     * 
     * 
     * @param t The current time since the beginning of the decay
     * @param L The time to half decay
     * @param k The Shape parameter
     * @return The decayed value
     */
    public static double weibull(double t, double L, double k) {
        return Math.exp( - Math.pow(t/L,  k) * Math.log(2) );
    }
    /**
     * The Hill decay function is similar to the Weibull decay function except
     * is isn't equivalent to the exponential curve function when
     * <code>k = 1</code>. The value will never reach 0.
     * 
     * @param t The current time since the beginning of the decay
     * @param L The time to half decay
     * @param k The Shape parameter
     * @return The decayed value
     */
    public static double hill(double t, double L, double k) {
        return 1 / (1 + Math.pow(t/L, k));
    }
    /**
     * The Smooth-Compact decay function achieves full decay at
     * <code>t = L</code>.
     * 
     * @param t The current time since the beginning of the decay
     * @param L The time to full decay
     * @param k The Shape parameter
     * @return The decayed value
     */
    public static double smoothcompact(double t, double L, double k) {
        return Math.exp( k - (k / (1 - Math.pow(t/L, 2))) );
    }
    /**
     * The Sine decay function decays the value following the first quarter of
     * a cosine cycle starting at 1 and finishing at 0.
     * 
     * @param t The current time since the beginning of the decay
     * @param L The time to full decay
     * @param k Not used
     * @return
     */
    public static double sine(double t, double L, double k) {
        return Math.cos( (2*Math.PI*t) / (4 * L) );
    }
    /**
     * The Damped Sine Wave decay function decays a sine curve exponentially
     * so that the half-life is ~<code>0.693 * L</code> and the frequency
     * is ~<code>k</code>.
     * 
     * @param t The current time since the beginning of the decay
     * @param L The decay constant, the half-life is <code>0.693*L</code>
     * @param k The frequency constant, where ~<code>k</code> is the frequency.
     * @return The decayed value
     */
    public static double dampedsine(double t, double L, double k) {
        return Math.exp(-(t/L)) * Math.cos(k * 2*Math.PI * (t/L));
    }
    
    
    private Type   type;
    
    private double L;
    private double k;
    private double min, max, dif;
    private double start;
    private double over;
    
    
    /**
     * @param type The type of decay function that should be used
     */
    protected Decay(Type type) {
        this(type, 0.0d, 1.0d, 0.0d, 1.0d, 0.0d, 1.0d);
    }
    /**
     * @param type  The type of decay function that should be used
     * @param L     The time to either full or half decay
     * @param over  The time to decay over
     */
    protected Decay(Type type, double L, double over) {
        this(type, L, 1.0d, 0.0d, 1.0d, 0.0d, over);
    }
    /**
     * @param type  The type of decay function that should be used
     * @param L     The time to either full or half decay
     * @param start The time to wait before starting the decay
     * @param over  The time to decay over
     */
    protected Decay(Type type, double L, double start, double over) {
        this(type, L, 1.0d, 0.0d, 1.0d, start, over);
    }
    /**
     * @param type    The type of decay function that should be used
     * @param L       The time to either full or half decay
     * @param initial The initial/maximum value for the decaying value
     * @param start   The time to wait before starting the decay
     * @param over    The time to decay over
     */
    protected Decay(Type type, double L, double initial, double start, double over) {
        this(type, L, 1.0d, 0.0d, initial, start, over);
    }
    /**
     * @param type  The type of decay function that should be used
     * @param L     The time to either full or half decay
     * @param min   The minimum value allowed
     * @param max   The maximum value allowed
     * @param start The time to wait before starting the decay
     * @param over  The time to decay over
     */
    protected Decay(Type type, double L, double min, double max, double start, double over) {
        this(type, L, 1.0d, min, max, start, over);
    }
    /**
     * @param type  The type of decay function that should be used
     * @param L     The time to either full or half decay
     * @param k     The Shape parameter
     * @param min   The minimum value allowed
     * @param max   The maximum value allowed
     * @param start The time to wait before starting the decay
     * @param over  The time to decay over
     */
    protected Decay(Type type, double L, double k, double min, double max, double start, double over) {
        this.type  = type;
        this.L     = L;
        this.k     = k;
        this.min   = min;
        this.max   = max;
        this.dif   = max - min;
        this.start = start;
        this.over  = over;
    }
    
    
    /**
     * @param type The new type of decay function
     */
    public void setType(Type type) {
        this.type = type;
    }
    /**
     * @param L The time ti either full or half decay
     */
    public void setL(double L) {
        this.L = L;
    }
    /**
     * @param k The Shape parameter
     */
    public void setK(double k) {
        this.k = k;
    }
    /**
     * @param min The minimum value allowed
     */
    public void setMin(double min) {
        this.min = min;
        this.dif = this.max - this.min;
    }
    /**
     * @param max The maximum value
     */
    public void setMax(double max) {
        this.max = max;
        this.dif = this.max - this.min;
    }
    /**
     * @param start The time to wait before starting to decay
     */
    public void setStart(double start) {
        this.start = start;
    }
    /**
     * @param over The time to decay over
     */
    public void setOver(double over) {
        this.over = over;
    }
    
    
    /**
     * @see   Decay#constant(double, double, double)
     * @see   Decay#step(double, double, double)
     * @see   Decay#linear(double, double, double)
     * @see   Decay#exponential(double, double, double)
     * @see   Decay#weibull(double, double, double)
     * @see   Decay#hill(double, double, double)
     * @see   Decay#smoothcompact(double, double, double)
     * @see   Decay#sine(double, double, double)
     * @see   Decay#dampedsine(double, double, double)
     * 
     * @param t The current time
     * @return  The decayed value
     */
    public double decay(double t) {
        switch( type ) {
            case CONSTANT:
                return dif*constant(t, L, k) + min;
                
            case STEP:
                return dif*step(t-start, L, k) + min;

            default:
            case LINEAR:
                if( t < start )
                    return max;
                if( t > start+over )
                    return min;
                
                return dif*linear(t-start, L, k) + min;
                
            case EXPONENTIAL:
                if( t < start )
                    return max;
                if( t > start+over )
                    return min;
                
                return dif*exponential(t-start, L, k) + min;
                
            case WEIBULL:
                if( t < start )
                    return max;
                if( t > start+over )
                    return min;
                
                return dif*weibull(t-start, L, k) + min;
                
            case HILL:
                if( t < start )
                    return max;
                if( t > start+over )
                    return min;
                
                return dif*hill(t-start, L, k) + min;
                
            case SMOOTHCOMPACT:
                if( t < start )
                    return max;
                if( t > start+over )
                    return min;
                
                return dif*smoothcompact(t-start, L, k) + min;
                
            case SINE:
                if( t < start )
                    return max;
                if( t > start+over )
                    return min;
                
                return dif*sine(t-start, L, k) + min;
                
            case DAMPEDSINE:
                if( t < start )
                    return max;
                if( t > start+over )
                    return min;
                
                return dif*dampedsine(t-start, L, k) + min;
        }
        
    }
}