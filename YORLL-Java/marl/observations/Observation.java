/**
 * 
 */
package marl.observations;


/**
 * Observations can be of any type of information what an experiment can have.
 * Observations may be used in 
 * 
 * @author pds
 *
 */
public class Observation
{
    private int[]    ints;
    private double[] doubles;
    private char[]   chars;

    /**
     * 
     */
    public Observation(int numInts, int numDoubles, int numChars)
    {
        ints    = new int[numInts];
        doubles = new double[numDoubles];
        chars   = new char[numChars];
    }
    
    public Observation(Observation that)
    {
        ints    = that.ints.clone();
        doubles = that.doubles.clone();
        chars   = that.chars.clone();
    }
    
    /**
     * @param which The position of the integer required
     * @return The integer at the requested position in the integer array
     */
    public int getInt(int which) {
        return ints[which];
    }
    /**
     * @param which The position of the double required
     * @return The double at the requested position in the double array
     */
    public double getDouble(int which) {
        return doubles[which];
    }
    /**
     * @param which The position of the character required
     * @return The character at the requested position in the character array
     */
    public char getChar(int which) {
        return chars[which];
    }
    
    
    /**
     * @return The number of integers in this observation
     */
    public int getNumInts() {
        return ints.length;
    }
    /**
     * @return The number of doubles in this observation
     */
    public int getNumDoubles() {
        return doubles.length;
    }
    /**
     * @return The number of characters in this observation
     */
    public int getNumChars() {
        return chars.length;
    }
    
    
    /**
     * Sets the value of the integer at the given position in the integer array.
     * @param which The position in the integer array to be set
     * @param value The value which it should be assigned
     */
    public void setInt(int which, int value) {
        ints[which] = value;
    }
    /**
     * Sets the value of the double at the given position in the double array.
     * @param which The position in the double array to be set
     * @param value The value which it should be assigned
     */
    public void setDouble(int which, double value) {
        doubles[which] = value;
    }
    /**
     * Sets the value of the character at the given position in the character
     * array.
     * @param which The position in the character array to be set
     * @param value The value which it should be assigned
     */
    public void setChar(int which, char value) {
        chars[which] = value;
    }
    
    
    @Override
    public String toString() {
        String rtn = new String();
        for( int i=0; i<ints.length; i++ )
            rtn += " "+ints[i];
        for( int i=0; i<doubles.length; i++ )
            rtn += " "+doubles[i];
        for( int i=0; i<chars.length; i++ )
            rtn += " "+chars[i];
        
        if( rtn.length() > 0 )
            rtn = rtn.substring(1);
        
        return rtn;
    }
}
