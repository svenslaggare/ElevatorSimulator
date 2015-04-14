/**
 * 
 */
package marl.environments.Maze;

import java.io.IOException;

import marl.utility.FileReader;

/**
 * @author pds
 * @since  2013-03-07
 * 
 */
public class MazeFileParser
	extends FileReader
{
    /**
     * A counter for which line in the file we are at.
     */
	private int      line_; 
	/**
	 * The store for the maze being read in.
	 */
	private char[][] maze_;
	
	
	public MazeFileParser(int width, int height)
	{
		maze_ = new char[height][width];
	}
	
	@Override
	public void readFile(String path) throws IOException
	{
		line_ = 0;
		super.readFile(path);
	}
	
	/**
     * Returns the maze as a 2 dimensional array, the size of
     * which is defined by the constructor.
     * @return The parsed maze as a 2 dimensional array
     */
	public char[][] getMaze()
	{
		return maze_;
	}

	@Override
	protected void parseLine(String line)
	{
	    line = line.trim();             // remove whitespace

	    int size = line.length();       // copy the line data into the maze array
	    for( int i=0; i<size; i++ )
	        maze_[line_][i] = line.charAt(i);

	    line_++;                        // increment the line counter
	}

}
