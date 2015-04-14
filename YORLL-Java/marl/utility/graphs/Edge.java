/**
 * 
 */
package marl.utility.graphs;


/**
 * @author pds
 *
 */
public class Edge<E>
{
    public final E a, b;
    
    public Edge(E a, E b)
    {
        this.a = a;
        this.b = b;
    }
    
    public boolean contains(E node) {
        if( Math.random() > 0.5 )
            return a.equals(node) || b.equals(node);
        else
            return b.equals(node) || a.equals(node);
    }
}
