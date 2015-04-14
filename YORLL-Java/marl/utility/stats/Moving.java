/**
 * 
 */
package marl.utility.stats;

import java.util.ArrayList;

import flanagan.analysis.Regression;


/**
 * Moving keeps track of the last maxSize values added. With this information
 * it is able to calculate the average, variance, and gradient
 * 
 * @author pds
 *
 */
public class Moving
{
    private ArrayList<Double> al;
    int    maxSize,
           numElementsSeen;
    double sumOfValues,
           sumOfSquares,
           sumCumulative;

    /**
     * 
     */
    public Moving(int maxSize)
    {
        this.maxSize = maxSize;
        al           = new ArrayList<>();
        empty();
    }
    
    public Moving(Moving moving)
    {
        this(moving.maxSize);
        add(moving);
    }
    
    
    /**
     * Removes all the elements from the data set and restores this object to
     * its starting state.
     */
    public void empty()
    {
        al.clear();
        sumOfValues       = 0.0;
        sumOfSquares      = 0.0;
        numElementsSeen   = 0;
        sumCumulative     = 0.0;
    }
    
    
    /**
     * Adds the given element into the data set.
     * @param d The element to be added
     */
    public void add(double d)
    {
        // add the newest element
        al.add(d);
        sumOfValues  += d;
        sumOfSquares += Math.pow(d, 2);
        
        // add to the cumulative average
        sumCumulative += d;
        numElementsSeen++;
        
        // remove as necessary
        if( al.size() > maxSize ) {
            double removed = al.remove(0);
            sumOfValues          -= removed;
            sumOfSquares -= Math.pow(removed, 2);
        }
    }
    /**
     * Adds all the elements of the given moving.
     * @param moving The moving object to add all the elements of
     */
    public void add(Moving moving)
    {
        for( int i=0; i < moving.al.size(); i++ )
            add(moving.al.get(i));
    }
    
    
    /**
     * @return The size of the current data set
     */
    public int size()
    {
        return al.size();
    }
    
    
    /**
     * @return The current average of all the elements
     */
    public double getAverage()
    {
        if( al.size() == 0 )
            return 0.0;
        else
            return sumOfValues / (double)al.size();
    }
    
    
    /**
     * @return The cumulative value
     */
    public double getCumulative()
    {
        return sumCumulative;
    }
    
    
    /**
     * @return The cumulative average value of data points seen
     */
    public double getCumulativeAverage()
    {
        return sumCumulative / (double)numElementsSeen;
    }
    
    
    /**
     * This method requires at least two elements in the data set.
     * @return The variance of the current data set
     */
    public double getVariance()
    {
        if( al.size() < 2 )
            return 0.0;
        else
            return (((double)al.size() * sumOfSquares) - Math.pow(sumOfValues, 2.0))
                                                / (double)(al.size() * (al.size()-1));
    }
    
    
    /**
     * @return The newest element to be added
     */
    public double getNewest()
    {
        return al.get(al.size() - 1);
    }
    
    
    /**
     * This method requires at least three elements in the data set.
     * @return The gradient of the linear regression
     */
    public double getGradient()
    {
        if( al.size() < 3 )
            return 0.0;
        
        double[] x = new double[al.size()];
        double[] y = new double[al.size()];
        
        for( int i=0; i<al.size(); i++ ) {
            x[i] = i;
            y[i] = al.get(i);
        }
        
        Regression regression = new Regression(x, y);
        regression.linear();
        return regression.getCoeff()[1];
    }
}
