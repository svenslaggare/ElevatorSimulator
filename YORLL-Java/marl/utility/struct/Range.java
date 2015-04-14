/**
 * 
 */
package marl.utility.struct;

/**
 * @author pds
 * @since  2013-03-26
 *
 */
public abstract class Range<T extends Comparable<T>> {
    
    /**
     * Tests to see if the two specified Ranges are adjacent to see other.
     * @param a Range a
     * @param b Range b
     * @return True if Range a is adjacent to Range b, false otherwise
     */
    public static <E extends Comparable<E>> boolean areAdjacent(Range<E> a, Range<E> b) {
        return a.lower_.compareTo(b.upper_) == 0 ||
                a.upper_.compareTo(b.lower_) == 0;
    }
    
    /**
     * The lower value of this Range.
     */
    private T lower_;
    /**
     * The upper value of this Range.
     */
    private T upper_;
    
    /**
     * Creates a new Range from the lower value to the upper value.
     * @param lower The lower value of this Range
     * @param upper The upper value of this Range
     */
    public Range(T lower, T upper) {
        lower_ = lower;
        upper_ = upper;
    }
    
    
    /**
     * Tests to see if the specified value falls within this Range.
     * @param value The value to be checked
     * @return True if the value is within the Range, otherwise false
     */
    public boolean contains(T value) {
        return value.compareTo(lower_) >= 0 && value.compareTo(upper_) <= 0;
    }
    
    /**
     * @return The lower value of this Range
     */
    public T getLower() {
        return lower_;
    }
    /**
     * @return The upper value of this Range
     */
    public T getUpper() {
        return upper_;
    }
    /**
     * Sets the lower value of this Range.
     * @param lower The new lower value
     */
    public void setLower(T lower) {
        lower_ = lower;
    }
    /**
     * Sets the upper value of this Range.
     * @param upper The new upper value
     */
    public void setUpper(T upper) {
        upper_ = upper;
    }
    
    
    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if( obj instanceof Range<?> ) {
            Range<?> that = (Range<?>)obj;
            
            if( that.lower_.getClass().equals(this.lower_.getClass()) &&
                    that.upper_.getClass().equals(this.upper_.getClass()) ) {
                return this.lower_.compareTo((T)that.lower_) == 0 &&
                        this.upper_.compareTo((T)that.upper_) == 0;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "["+lower_+".."+upper_+"]";
    }
}
