/**
 * 
 */
package marl.utility.struct;

import java.math.BigDecimal;

/**
 * 
 * @author pds
 * @since  2013-01-31
 *
 */
public class Bounds2 implements Comparable<Bounds2>
{
    private static final BigDecimal TWO       = new BigDecimal("2");
    
    
    /**
     * 
     * @param bounds
     * @param points
     * @return
     */
    public static int compare(Bounds2[] bounds, double[] points) {
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
    public static int compare(Bounds2[] bounds, Bounds2[] points) {
        int rtn;
        for( int i=0; i<bounds.length; i++ )
            if( (rtn = bounds[i].compareTo(points[i])) != 0 )
                return rtn;
        
        return 0;
    }
    /**
     * 
     * @param lower
     * @param upper
     * @return
     */
    public static boolean infinitesimal(double lower, double upper) {
//        return Math.abs(upper - (((upper - lower) / 2.0) + lower)) < PRECISION;
//        return Math.abs(upper - lower) / 2.0 < PRECISION;
        return BigDecimal.valueOf(upper).subtract(BigDecimal.valueOf(lower)).divide(TWO).compareTo(BigDecimal.ZERO) <= 0;
    }
    /**
     * 
     * @param one
     * @param two
     * @return
     */
    public static boolean areNeighbours(Bounds2[] one, Bounds2[] two) {
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
//            else if( !(Math.abs(one[i].getLower()-two[i].getUpper())<PRECISION ^
//                        Math.abs(one[i].getUpper()-two[i].getLower())<PRECISION) )
            else if( !(one[i].lower_.subtract(two[i].upper_).abs().compareTo(BigDecimal.ZERO) <= 0 ^
                       one[i].upper_.subtract(two[i].lower_).abs().compareTo(BigDecimal.ZERO) <= 0) )
                return false;
        }
        
        return matches + 1 == nBounds;
        
//        int nBounds = one.length,
//            count   = 0,
//            feature = 0;
//        
//        
//        // check for bounds that mismatch by one
//        for( int i=0; i<nBounds; i++ )
//            if( !one[i].equals(two[i]) ) {
//                count++;
//                feature = i;
//            }
//        
//        if( count == 1 ) {
//            // if the mismatch coincides with joining edges, must be a neighbour
//            if( Math.abs(one[feature].getLower()-two[feature].getUpper())<PRECISION ^
//                    Math.abs(one[feature].getUpper()-two[feature].getLower())<PRECISION )
//                return true;
//        }
//        
//        return false;
    }
    /**
     * 
     * @param one
     * @param two
     * @return
     */
    public static boolean intersect(Bounds2[] one, Bounds2[] two) {
        boolean rtn = true;
        for( int i=0; i<one.length; i++ )
            rtn &= one[i].getLower() < two[i].getUpper() && one[i].getUpper() > two[i].getLower();
        
        return rtn;
    }
    
    
    
    /**
     * The lower bound.
     */
    private BigDecimal lower_;
    /**
     * The upper bound.
     */
    private BigDecimal upper_;
    /**
     * The precision of this bounds, defaults to {@link Bounds2#PRECISION}
     */
//    private BigDecimal precision_;
    
    
    
    
    /**
     * 
     */
    public Bounds2(double lower, double upper) {
        lower_     = BigDecimal.valueOf(lower);
        upper_     = BigDecimal.valueOf(upper);
    }
    public Bounds2(Bounds2 that) {
        this.lower_     = new BigDecimal(that.lower_.doubleValue());
        this.upper_     = new BigDecimal(that.upper_.doubleValue());
    }

    
    public double getLower() {
        return lower_.doubleValue();
    }    
    public void setLower(double lower) {
        lower_ = new BigDecimal(lower);
    }
    public double getUpper() {
        return upper_.doubleValue();
    }    
    public void setUpper(double upper) {
        upper_ = new BigDecimal(upper);
    }
    
    
    
    public double range() {
        return upper_.subtract(lower_).doubleValue();
    }
    public double midpoint() {
        return upper_.subtract(lower_).divide(TWO).add(lower_).doubleValue();
    }
    public boolean infinitesimal() {
        return upper_.subtract(lower_).divide(TWO).abs().compareTo(BigDecimal.ZERO) <= 0;
    }
    
    
    public boolean contains(double p) {
//        return p >= lower_ && p <= upper_;
        BigDecimal point = BigDecimal.valueOf(p);
        return point.compareTo(lower_) >= 0 && point.compareTo(upper_) <= 0;
    }
    
    
    @Override
    public int compareTo(Bounds2 that) {
        if( this.upper_.compareTo(that.lower_) < 0 || this.upper_.subtract(that.lower_).abs().compareTo(BigDecimal.ZERO) < 0 )
            return -1;
        if( this.lower_.compareTo(that.upper_) < 0 || this.lower_.subtract(that.upper_).abs().compareTo(BigDecimal.ZERO) < 0 )
            return  1;
        
//        if( this.upper_ < that.lower_ || Math.abs(this.upper_-that.lower_) < PRECISION )
//            return -1;
//        if( this.lower_ > that.upper_ || Math.abs(this.lower_-that.upper_) < PRECISION )
//            return  1;
        
        return 0;
    }
    public int compareTo(double p) {
        BigDecimal point = BigDecimal.valueOf(p);
        if( upper_.compareTo(point) < 0 )
            return -1;
        if( lower_.compareTo(point) > 0 )
            return 1;
//        if( this.upper_ < p )
//            return -1;
//        if( this.lower_ > p )
//            return 1;
        
        return 0;
    }
    @Override
    public boolean equals(Object obj) {
        if( obj instanceof Bounds2 ) {
            Bounds2 that = (Bounds2)obj;
//            return  Math.abs(this.lower_ - that.lower_) < PRECISION &&
//                    Math.abs(this.upper_ - that.upper_) < PRECISION;
            return this.lower_.subtract(that.lower_).abs().compareTo(BigDecimal.ZERO) < 0 &&
                    this.upper_.subtract(that.upper_).abs().compareTo(BigDecimal.ZERO) < 0;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "[" + lower_ + "," + upper_ + "]";
    }
    @Override
    public Bounds2 clone() {
        return new Bounds2(this);
    }
    
}
