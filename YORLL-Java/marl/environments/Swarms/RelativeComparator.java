/**
 * 
 */
package marl.environments.Swarms;

import java.awt.geom.Point2D;
import java.util.Comparator;


/**
 * @author pds
 * @since  2013-07-03
 *
 */
public class RelativeComparator implements Comparator<Point2D>
{

    /**
     * 
     */
    public RelativeComparator()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public int compare(Point2D a, Point2D b) {
        double diff = b.distanceSq(0,0) - a.distanceSq(0,0);
        if( diff > 0 )      return  1;
        else if( diff < 0 ) return -1;
        else                return  0;
    }
}
