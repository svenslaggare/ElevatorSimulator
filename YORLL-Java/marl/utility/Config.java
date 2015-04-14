package marl.utility;

import java.util.Map;
import java.util.HashMap;


/**
 * Config is a utility class that allows the user to easily read
 * in configuration values from a given configuration file. The
 * file must be in the following format:
 *
 *      # comments must be on separate lines
 *      ; and are denoted but either a '#' or a ';'
 *      ; being the *first* character of the line
 *
 *      # values can be integers, doubles, strings
 *      # arrays of integers, boolean, and arrays of doubles
 *      # i.e. integer array
 *      variableName1 = 1,2,3,4,5,6,7,8,9
 *      ; a string
 *      variableName2 = This is a string
 *      ; a double
 *      variableName3 = -0.07
 *      ; a boolean [true|false]
 *      variableName4 = true
 *
 * You must know the exact name of the variable you want remembering
 * it is cASe SenSitiVe. Use the appropriate method to retrieve the
 * correct type.
 * 
 * @author Pete Scopes
 * @version 06/09/2012
 */
public class Config extends FileReader
{
    /**
     * Mapping to hold the variable names and their value.
     */
    private Map<String, String> lookup;
    
    
    public Config()
    {
        lookup = new HashMap<String, String>();
    }
    
    /**
     * Returns True if there is such a parameter as requested
     * @param name The name of variable to check for
     * @return True if there is a parameter, false otherwise
     */
    public boolean hasParam(String name)
    {
        try {
            return lookup.get(name) != null;
        } catch(Exception e) {}
        
        return false;
    }

    /**
     * Returns the value of the given named variable as a string.
     * @param name The name of the variable
     * @return The value of the requested variable
     */
    public String getString(String name)
    {
        try {
            return lookup.get(name);
        } catch(Exception e) {}
        
        return null;
    }
    /**
     * Returns the value of the given named variable as an boolean [true|false]
     * @param name The name of the variable
     * @return True of False
     * @since  2013-03-07
     */
    public boolean getBoolean(String name)
    {
        if( "true".equals(getString(name)) )
            return true;
        else
            return false;
    }
    /**
     * Returns the value of the given named variable as an integer.
     * @param name The name of the variable
     * @return The value of the requested variable
     */
    public int    getInt(String name)
        throws NumberFormatException
    {
        return Integer.parseInt(getString(name));
    }
    /**
     * Returns the value of the given named variable as a double.
     * @param name The name of the variable
     * @return The value of the requested variable
     */
    public double  getDouble(String name)
        throws NumberFormatException
    {
        return Double.parseDouble(getString(name));
    }



    /**
     * Returns the value of the given named variable as an array
     * of int.
     * 
     * Note: Does not give you the size of the array.
     * @param name The name of the variable
     * @return The value of the requested variable
     */
    public int[] getIntArray(String name)
        throws NumberFormatException
    {
        String[] data  = getString(name).split(",");
        int[]    value = new int[data.length];
        for( int i=0; i<data.length; i++ )
            value[i] = Integer.parseInt(data[i].trim());
        
        return value;
    }
    /**
     * Returns the value of the given named variable as an array
     * of double.
     * 
     * Note: Does not give you the size of the array.
     * @param name The name of the variable
     * @return The value of the requested variable
     */
    public double[] getDoubleArray(String name)
        throws NumberFormatException
    {
        String[] data  = getString(name).split(",");
        double[] value = new double[data.length];
        for( int i=0; i<data.length; i++ )
            value[i] = Double.parseDouble(data[i].trim());
        
        return value;
    }
    
    /**
     * Returns the value of the given named variable as an array
     * of characters.
     * 
     * @param name The name of the variable
     * @return The value of the requested variable
     */
    public char[] getCharArray(String name)
    {
        String[] raw  = getString(name).split(",");
        char[]   data = new char[raw.length];
        for( int i=0; i<raw.length; i++ )
            data[i] = raw[i].charAt(0);
        
        return data;
    }
    
    
    
    /**
     * @Override
     * @See MARL/Utility/FileReader.h
     */
    @Override
    protected void parseLine(String line)
    {
        String name, value;    // initialise the name and value
                               // ignore comments and empty lines
        line = line.trim();
        if (line.length() > 0 && line.charAt(0) != '#' && line.charAt(0) != ';')
        {
            line = line.trim();     // extract the variable name and value and
            name  = line;           // add it into the lookup map
            name  = eraseRight(name,"=").trim();
            value = line;
            value = eraseLeft(value,"=").trim();
    
            lookup.put(name, value);
        }
    }
}
