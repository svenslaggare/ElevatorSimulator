/**
 * 
 */
package marl.utility.graphs;

import java.util.Comparator;


/**
 * @author pds
 *
 */
public class WeightedEdge<E> extends Edge<E>
{
    public double weight;
    
    
    public WeightedEdge(E a, E b)
    {
        this(a, b, 0.0);
    }
    public WeightedEdge(E a, E b, double weight)
    {
        super(a,b);
        this.weight = weight;
    }
    

    public static final class WeightedEdgeCompator implements Comparator<WeightedEdge<?>> {

        @Override
        public int compare(WeightedEdge<?> a, WeightedEdge<?> b) {
            double diff = a.weight - b.weight;
            if( diff > 0 ) return  1;
            if( diff < 0 ) return -1;
            return 0;
        }
        
    }
}
