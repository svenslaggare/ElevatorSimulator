/**
 * 
 */
package marl.utility.struct;

/**
 * A means of storing a set of Ranges
 * 
 * @author pds
 * @since  2012-10-12
 *
 */
public class Bounds implements Comparable<Bounds>
{
    /**
     * 
     * @param bounds
     * @param points
     * @return
     */
    public static int compare(Bounds[] bounds, double[] points) {
        int rtn;
        for( int i=0; i<bounds.length; i++ )
            if( (rtn = bounds[i].compareTo(points[i])) != 0 )
                return rtn;
        
        return 0;
    }
    /**
     * 
     * @param bounds
     * @param points
     * @return
     */
    public static int compare(Bounds[] bounds, Bounds[] points) {
        int rtn;
        for( int i=0; i<bounds.length; i++ )
            if( (rtn = bounds[i].compareTo(points[i])) != 0 )
                return rtn;
        
        return 0;
    }
    /**
     * Tests to see if half the range of the specified bounds are infinitesimal.
     * I.e. smaller than the specified infinitesimal value.
     * @param bounds        The bounds to be checked
     * @param infinitesimal The value of infinitesimality
     * @return True if infinitesimal, false otherwise
     */
    public static boolean infinitesimal(Bounds bounds, double infinitesimal) {
        return Math.abs(bounds.upper_ - bounds.midpoint()) / 2.0d < infinitesimal;
                
    }
    /**
     * 
     * @param one
     * @param two
     * @return
     */
    public static boolean areNeighbours(Bounds[] one, Bounds[] two) {
        if( one.length != two.length )
            return false;
        
        
        int nBounds = one.length,
            matches = 0;
        
        // check that bounds either match or are adjacent
        for( int i=0; i<nBounds; i++ ) {
            // they must either match
            if( one[i].equals(two[i]) )
                matches++;
            
                // or be adjacent
            else if( !(Math.abs(one[i].getLower()-two[i].getUpper()) == 0.0d ^
                        Math.abs(one[i].getUpper()-two[i].getLower()) == 0.0d) )
                return false;
        }
        
        return matches + 1 == nBounds;
    }
    /**
     * 
     * @param one
     * @param two
     * @return
     */
    public static boolean intersect(Bounds[] one, Bounds[] two) {
        boolean rtn = true;
        for( int i=0; i<one.length; i++ )
            rtn &= one[i].getLower() < two[i].getUpper() && one[i].getUpper() > two[i].getLower();
        
        return rtn;
    }
    
    
    public static double[] getCoords(Bounds[] bounds) {
        double[] coords = new double[bounds.length];
        for( int i=0; i<bounds.length; i++ )
            coords[i] = bounds[i].lower_;
        
        return coords;
    }
    public static double[] getDimensions(Bounds[] bounds) {
        double[] coords = new double[bounds.length];
        for( int i=0; i<bounds.length; i++ )
            coords[i] = bounds[i].range();
        
        return coords;
    }
    public static double[] getMidPoints(Bounds[] bounds) {
        double[] coords = new double[bounds.length];
        for( int i=0; i<bounds.length; i++ )
            coords[i] = bounds[i].midpoint();
        
        return coords;
    }
    
    
    
    
    /**
     * The lower bound.
     */
    private double lower_;
    /**
     * The upper bound.
     */
    private double upper_;
    
    
    

    public Bounds(double lower, double upper)
    {
        lower_     = lower;
        upper_     = upper;
        
    }
    public Bounds(Bounds that) {
        lower_     = that.lower_;
        upper_     = that.upper_;
    }

    
    public double getLower() {
        return lower_;
    }    
    public void setLower(double lower) {
        lower_ = lower;
    }
    public double getUpper() {
        return upper_;
    }    
    public void setUpper(double upper) {
        upper_ = upper;
    }
    
    
    
    public double range() {
        return upper_ - lower_;
    }
    public double midpoint() {
        return ((upper_ - lower_) / 2.0) + lower_;
    }
    public boolean infinitesimal(double infinitesimal) {
        return infinitesimal(this, infinitesimal);
    }
    
    
    public boolean contains(double p) {
        return p >= lower_ && p <= upper_;
    }
    
    
    @Override
    public int compareTo(Bounds that) {
        if( this.upper_ < that.lower_ || Math.abs(this.upper_-that.lower_) == 0.0d )
            return -1;
        if( this.lower_ > that.upper_ || Math.abs(this.lower_-that.upper_) == 0.0d )
            return  1;
        
        return 0;
    }
    public int compareTo(double p) {
        if( this.upper_ < p )
            return -1;
        if( this.lower_ > p )
            return 1;
        
        return 0;
    }
    @Override
    public boolean equals(Object obj) {
        if( obj instanceof Bounds ) {
            Bounds that = (Bounds)obj;
            return  Math.abs(this.lower_ - that.lower_) == 0.0d &&
                    Math.abs(this.upper_ - that.upper_) == 0.0d;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "[" + lower_ + "," + upper_ + "]";
    }
    @Override
    public Bounds clone() {
        return new Bounds(this);
    }
}
