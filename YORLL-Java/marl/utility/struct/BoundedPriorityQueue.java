package marl.utility.struct;

import java.util.Comparator;
import java.util.PriorityQueue;


@SuppressWarnings("serial")
public class BoundedPriorityQueue<S> extends PriorityQueue<S>
{
    private int maxSize_;

    public BoundedPriorityQueue(int maxSize, Comparator<? super S> comparator)
    {
        super(maxSize, comparator);
        maxSize_ = maxSize;
    }
    
    @Override
    public boolean offer(S elem) {
        remove(elem);
        // if within the max size add
        if( size()+1 <= maxSize_ )
            return super.offer(elem);
        else {
            // otherwise add this tile and remove
            // the last element in the list
            super.offer(elem);
            S elemToRemove = elem;
            for( S t: this )
                elemToRemove = t;
            
            remove(elemToRemove);
            
            // if the given element wasn't removed
            return elem != elemToRemove;
        }
    }
}
