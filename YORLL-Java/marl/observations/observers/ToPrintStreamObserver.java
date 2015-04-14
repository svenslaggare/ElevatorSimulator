package marl.observations.observers;

import java.io.PrintStream;

import marl.observations.Observation;
import marl.observations.PushObserver;

public class ToPrintStreamObserver implements PushObserver {
    
    protected PrintStream printStream;
    
    protected ToPrintStreamObserver() {}
    
    /**
     * Create a ToPrintStreamObserver with the specified print stream.
     * @param out The print stream to print to
     */
    public ToPrintStreamObserver(PrintStream out) {
        printStream = out;
    }

    /**
     * Writes the observation to the file, if the observation is null then
     * an empty line is written.
     * @see marl.visualisation.PushObserver#push(marl.observations.Observation)
     */
    @Override
    public void push(Observation o) {
        if( printStream != null ) {
            if( o != null )
                printStream.println( observationToString(o) );
            else
                printStream.println();
        }
    }

    /**
     * Here in case of extensions of ToFileObserver wish to be created.
     * 
     * @param o The observation to be turned into a string
     * @return A String representation of the observation
     */
    protected String observationToString(Observation o) {
        return o.toString();
    }

    /**
     * Close the file.
     */
    public void close() {
        if( printStream != null )
            printStream.close();
    }
    
}