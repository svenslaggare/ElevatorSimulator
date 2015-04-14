/**
 * 
 */
package marl.utility;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;


/**
 * @author pds
 * @since  2013-05-07
 *
 */
public class CompileResults extends FileReader
{
    public static final int OPTION_AVG = 1;


    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            // handle exception
        }
        
        
        int returnVal;
        
        final JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("."));
        fc.setDialogTitle("Select Result Files to be Compiled");
        fc.setMultiSelectionEnabled(true);
        returnVal = fc.showOpenDialog(null);
        
      //Exit if not no folder chosen
        if( returnVal != JFileChooser.APPROVE_OPTION )
            return;
        
      //Ensure selected file is a directory
        File[] fs = fc.getSelectedFiles();
      //Find all .txt files in folder
        ArrayList<File> ffs = new ArrayList<>();
        for( File f: fs ) {
            if( f.isFile() && f.getName().endsWith(".txt") )
                ffs.add(f);
        }
        
      //Get the column to apply to
        String columnStr = JOptionPane.showInputDialog(null, "Select which column to apply to");
        int column = Integer.parseInt(columnStr);
        
      //Get what to do to the column
//        Object[] possibilities = {"average"};
//        String s = (String)JOptionPane.showInputDialog(
//                            null,
//                            "What should be done to the column",
//                            "Customized Dialog",
//                            JOptionPane.PLAIN_MESSAGE,
//                            null,
//                            possibilities,
//                            "average");
        
      //Get the current date
        String curDate = (new SimpleDateFormat("yyyyMMdd-HHmmss")).format(new Date());
      //Output File
        PrintStream fileOut = new PrintStream(new File(fc.getCurrentDirectory().getPath()+"/compiled"+curDate+".txt"));
      //Compile results
        CompileResults cr = new CompileResults(column);
        ArrayList<ArrayList<Double>> compiled = new ArrayList<>();
        for( File resFile: ffs ) {
            cr.readFile(resFile.getAbsolutePath());
            compiled.add(cr.getCompiledResults(OPTION_AVG));
            
            fileOut.print(resFile.getName().substring(0, resFile.getName().length()-4)+" ");
        }
        fileOut.println();
        
      //Get the number of rows
        int numRows = compiled.get(0).size();
      //Output the compiled results
        for( int i=0; i<numRows; i++ ) {
            for( int j=0; j<compiled.size(); j++ ) {
                fileOut.print(compiled.get(j).get(i)+" ");
            }
            fileOut.println();
        }
        
        fileOut.close();
    }

    
    private int    column_;
    private String delimiter_;
    
    private ArrayList<ArrayList<Double>> entries_;
    private ArrayList<Double>            running_;
    /**
     * 
     */
    public CompileResults(int column)
    {
        this(column, " ");
    }
    public CompileResults(int column, String delimiter)
    {
        column_    = column;
        delimiter_ = delimiter;
        reset();
    }
    private void reset() {
        entries_   = new ArrayList<>();
        running_   = new ArrayList<>();
        
        entries_.add(running_);
        
    }

    
    public ArrayList<Double> getCompiledResults(int option) {
        if( entries_.size() == 0 )
            return null;
        
        ArrayList<Double> compiled = new ArrayList<>();
        switch( option ) {
            
            case OPTION_AVG:
            default:
                int numRows = entries_.get(0).size();
                int numCols = entries_.size();
                
                for( int i=0; i<numRows; i++ ) {
                    double d = 0.0d;
                    for( int j=0; j<numCols; j++ )
                        d += entries_.get(j).get(i);
                    
                    compiled.add(d/(double)numCols);
                }
                break;
        }
        
        return compiled;
    }
    
    @Override
    public void readFile(String path) throws IOException {
      //Reset
        reset();
      //Read the file
        super.readFile(path);
      //Remove empty entries
        for( int i=0; i<entries_.size(); i++ ) {
            if( entries_.get(i).size() == 0 ) {
                entries_.remove(i);
                i = 0;
            }
        }
    }

    /* (non-Javadoc)
     * @see marl.utility.FileReader#parseLine(java.lang.String)
     */
    @Override
    protected void parseLine(String line) {
        if( line.trim().length() == 0 ) {
            running_ = new ArrayList<>();
            entries_.add(running_);
        }
        else {
            String[] columns = line.split(delimiter_);
            
            if( columns.length > column_ )
                running_.add(Double.parseDouble(columns[column_]));
        }
    }
}
