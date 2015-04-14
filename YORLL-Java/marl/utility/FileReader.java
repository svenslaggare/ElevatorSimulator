package marl.utility;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataInputStream;


/**
 * File Reader is an abstract class that is useful for quickly
 * developing text file parsers. Config.h is one such example
 * which uses the File Reader to parse configuration files.
 * 
 * @author Pete Scopes
 * @version 06/09/2012
 */
public abstract class FileReader
{
    /**
     * Opens the file, parses each line calling the abstract method
     * parseLine(String line) and then closes the file.
     * @throws FileNotFoundException if the file could not be opened
     * @param path The path to the config file to be read
     */
    public void readFile(String path)
        throws IOException
    {
        String line;           // initialise the line
        FileInputStream fstream = null;
        BufferedReader  br      = null;
        try {                  // open the file and
            fstream = new FileInputStream(path);
            // Get the object of DataInputStream
            br = new BufferedReader(new InputStreamReader(new DataInputStream(fstream)));
        
            while( (line = br.readLine()) != null )     // while still lines to parse
                parseLine(line);                        // parse the line
        } finally {
        	if( br != null )
        		br.close();
        	if( fstream != null )
        		fstream.close();
        }
    }
    
    
    
    /**
     * Every line in the file is passed to this method to parse.
     * @param line The line of data to be parsed
     */
    protected abstract void parseLine(String line);



    
    /**
     * Erases everything after the last "t" from the source
     */
    protected static String eraseRight(String source, String t)
    {
        String str = source;
        return str.substring(0, str.indexOf(t));
    }
    /**
     * Erases everything before the first "t" from the source
     */
    protected static String eraseLeft(String source, String t)
    {
        String str = source;
        return str.substring(str.lastIndexOf(t)+1);
    }
}
