/**
 * 
 */
package marl.observations.observers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * @author scopes
 *
 */
public class ToFileObserver extends ToPrintStreamObserver
{
    /**
     * Create a ToFileObserver which writes its observations to the
     * specified file.
     * @param filename The file to write out to
     * @throws FileNotFoundException If the file is not found
     */
    public ToFileObserver(String filename) {
        try {
            printStream = new PrintStream(new File( filename ));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
