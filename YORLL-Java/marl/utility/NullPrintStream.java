/**
 * 
 */
package marl.utility;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;


/**
 * A simple implementation of a print stream that does not ever print.
 * 
 * @author pds
 * @since  2013-03-08
 *
 */
public class NullPrintStream extends PrintStream
{
    /**
     * Create a Print Stream and does not print.
     */
    public NullPrintStream()
    {
        super(new OutputStream() {
            @Override public void write(int b) throws IOException {}
        });
    }
}
