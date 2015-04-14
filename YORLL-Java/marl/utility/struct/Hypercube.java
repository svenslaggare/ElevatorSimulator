/**
 * 
 */
package marl.utility.struct;

/**
 * @author pds
 * @since  2013-03-26
 *
 */
public class Hypercube {
    
    /**
     * Checks to see if the two specified Hypercubes, a and b, are adjacent
     * and returns the result
     * @param a One of the two hypercubes
     * @param b The other of the two hypercubes
     * @return -1 if a and b aren't adjacent, otherwise the dimension they adjoin
     */
    public static int adjacent(Hypercube a, Hypercube b) {
        assert(a.getNum() == b.getNum());
        
        
        int nBounds   = a.getNum(),
            matches   = 0,
            adjoining = -1;
        
        // check that bounds either match or are adjacent
        for( int i=0; i<nBounds; i++ ) {
            // they must either match
            if( a.coords_[i] == b.coords_[i] &&
                    a.dimensions_[i] == b.dimensions_[i] )
                matches++;
            
                // or be adjacent
            else if( !(a.getCoord(i) == b.getCoord(i)+b.getDimension(i) ^
                    a.getCoord(i)+a.getDimension(i) == b.getCoord(i)) )
                return -1;
            
                // make note of the adjoining edge
            else
                adjoining = i;
        }
        
        if( matches + 1 == nBounds )
            return adjoining;
        else
            return -1;
    }
    /**
     * Checks to see if the two specified Hypercubes, a and b, intersect and
     * returns the result.
     * @param a One of the two hypercubes
     * @param b The other of the two hypercubes
     * @return True if the Hypercubes intersect, false otherwise
     */
    public static boolean intersect(Hypercube a, Hypercube b) {
        boolean rtn = true;
        for( int i=0; i<a.getNum(); i++ )
            rtn &= a.getCoord(i) < b.getCoord(i)+b.getDimension(i) && a.getCoord(i)+a.getDimension(i) > b.getCoord(i);
        
        return rtn;
    }
    
    /**
     * The array of the lower corner of the hypercube.
     */
    private double[] coords_;
    /**
     * The array of the dimensions of the edges of the hypercube.
     */
    private double[] dimensions_;
    
    
    
    /**
     * Creates a point Hypercube with the specified number of dimensions.
     * @param dimensions The nuber of dimensions for the hypercube to have
     */
    public Hypercube(int dimensions) {
        coords_     = new double[dimensions];
        dimensions_ = new double[dimensions];
    }
    
    /**
     * Creates the specified Hypercube.
     */
    public Hypercube(double[] coords, double[] dimensions) {
        assert(coords.length > 0);
        assert(coords.length == dimensions.length);
        coords_     = coords;
        dimensions_ = dimensions;
    }
    
    /**
     * Creates a Hypercube based upon the specified Hypercube.
     * @param that The Hypercube to copy
     */
    public Hypercube(Hypercube that) {
        this.coords_     = that.coords_.clone();
        this.dimensions_ = that.dimensions_.clone();
    }

    /**
     * @return The number of dimensions this hypercube has
     */
    public int getNum() {
        return coords_.length;
    }
    
    /**
     * @return The array of coordinates
     */
    public double[] getCoords() {
        return coords_;
    }
    /**
     * @return The array of dimensions
     */
    public double[] getDimensions() {
        return dimensions_;
    }
    /**
     * @param at The dimension in question
     * @return The coordinate in the specified dimension
     */
    public double getCoord(int at) {
        return coords_[at];
    }
    /**
     * @param at The dimension in question
     * @return The length of the edge in the specified dimension
     */
    public double getDimension(int at) {
        return dimensions_[at];
    }
    
    /**
     * Sets the coordinates of this hypercube.
     * @param coords The new coordinates
     */
    public void setCoords(double[] coords) {
        assert(coords.length == dimensions_.length);
        coords_ = coords;
    }
    /**
     * Sets the dimensions of this hypercube.
     * @param dimensions The new dimensions
     */
    public void setDimensions(double[] dimensions) {
        assert(dimensions.length == coords_.length);
        dimensions_ = dimensions;
    }
    /**
     * Sets the coordinate of the specified dimension.
     * @param at    The dimension to be set
     * @param value The new coordinate
     */
    public void setCoord(int at, double value) {
        coords_[at] = value;
    }
    /**
     * Sets the length of the edge of the specified dimension.
     * @param at    The dimension to be set
     * @param value The new length of the edge
     */
    public void setDimension(int at, double value) {
        dimensions_[at] = value;
    }
    /**
     * Sets the coordinate and the length of the edge of the specified
     * dimension.
     * @param at        The dimension to be set
     * @param coord     The new coordinate
     * @param dimension The new length of the edge
     */
    public void set(int at, double coord, double dimension) {
        coords_[at]     = coord;
        dimensions_[at] = dimension;
    }
    
    
    
    /**
     * Returns the midpoint of the specified dimension of the Hypercube between
     * the coordinate and the coordinate plus the edge's length.
     * @param at The dimension to get the midpoint of
     * @return The midpoint of the specified dimension
     */
    public double midpoint(int at) {
        return coords_[at] + (dimensions_[at] / 2.0d);
    }
    /**
     * Checks to see if the given point in n-dimensional space is contained
     * within this hypercube
     * @param point The point to be checked
     * @return True if the point is within the hypercube, false otherwise
     */
    public boolean contains(double[] point) {
        assert(point.length == coords_.length);
        for( int i=0; i<point.length; i++ )
            if( point[i] < coords_[i] && (point[i]-coords_[i]) > dimensions_[i] )
                return false;
        
        return true;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if( obj instanceof Hypercube ) {
            Hypercube that = (Hypercube)obj;
            if( this.coords_.length == that.coords_.length ) {
                for( int i=0; i<coords_.length; i++ )
                    if( this.coords_[i] != that.coords_[i] ||
                            this.dimensions_[i] != that.dimensions_[i] )
                        return false;
                return true;
            }
        }
        return false;
    }
    @Override
    public String toString() {
        if( coords_ == null )
            return "[]";
        String str = new String();
        for( int i=0; i<coords_.length; i++ )
            str += ",("+coords_[i]+" : "+dimensions_[i]+")";
        return "["+str.substring(1)+"]";
    }
    /**
     * Calculates the percentage of this hypercube that is filled by the given
     * hypercube. This method assumes that the dimensions of the two hypercubes
     * are the same.
     * 
     * @param that The other hypercube
     * @return The percentage that hypercube fills this hypercube
     * @throws IllegalArgumentException If the number of dimensions do not match
     */
    public double percentage(Hypercube that) {
        // calculate the intersection of the two hypercubes
        Hypercube inter = intersection(that);
        if( inter == null )
            return 0.0d;
        
        double thisHyperVolume = 1.0d, interHyperVolume = 1.0d;
        for( int i=0; i<this.dimensions_.length; i++ ) {
            thisHyperVolume  *= this.dimensions_[i];
            interHyperVolume *= inter.dimensions_[i];
        }
        return interHyperVolume / thisHyperVolume;
    }
    /**
     * Calculates and returns the intersection hypercube of this and that
     * hypercube.
     * 
     * @param  that
     * @return The intersection hypercube of this and that hypercube, or null
     *         if they don't intersect.
     * @throws IllegalArgumentException If the number of dimensions do not match
     */
    public Hypercube intersection(Hypercube that) {
        if( this.coords_.length != that.coords_.length )
            throw new IllegalArgumentException("Hypercubes must have the same dimensions.");
        Hypercube inter = new Hypercube(this.dimensions_.length);
        for( int i=0; i<this.dimensions_.length; i++ ) {
            if( this.coords_[i] <= that.coords_[i] )
                _calcIntersection(this, that, inter, i);
            else
                _calcIntersection(that, this, inter, i);
            
            if( inter.dimensions_[i] <= 0 )
                return null;
        }
        return inter;
    }
    /**
     * @param a     The "left" most hypercube
     * @param b     The other hypercube
     * @param inter The hypercube intersection being calculated
     * @param i     The dimension in question
     */
    public void _calcIntersection(Hypercube a, Hypercube b, Hypercube inter, int i) {
        double diff = b.coords_[i] - a.coords_[i];
        if( a.dimensions_[i] >= b.dimensions_[i]+diff ) {
            inter.coords_[i]     = b.coords_[i];
            inter.dimensions_[i] = b.dimensions_[i];
        } else {
            inter.coords_[i]     = b.coords_[i];
            inter.dimensions_[i] = a.dimensions_[i]-diff;
        }
    }
}
