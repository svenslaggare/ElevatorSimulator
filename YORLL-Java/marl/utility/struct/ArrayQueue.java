/**
 * 
 */
package marl.utility.struct;

import java.lang.reflect.Array;


/**
 * @author pds
 *
 */
public class ArrayQueue<E>
{
    private Class<E[]> class_;
    private E[]        array_;
    private int        head_,
                       tail_;

    /**
     * 
     */
    public ArrayQueue(Class<E[]> c, int maxSize)
    {
        if( maxSize <= 0 )
            throw new IllegalArgumentException("Maximum size must be positive");
        
        class_ = c;
        array_ = class_.cast(Array.newInstance(class_.getComponentType(), maxSize));
        head_  = tail_ = 0;
    }
    
    
    /**
     * @return The number of elements currently stored in this array queue
     */
    public int size() {
        if( isEmpty() )
            return 0;
        else
            return 1 + (((head_ - tail_) + array_.length) % array_.length);
    }
    
    
    /** 
     * @return The maximum number of elements this queue may store
     */
    public int maxSize() {
        return array_.length;
    }
    
    
    /**
     * Clears the queue
     */
    public void clear() {
        array_ = class_.cast(Array.newInstance(class_.getComponentType(), array_.length));
        head_  = tail_ = 0;
    }
    
    
    /**
     * @param obj The object to be searched for
     * @return True if the specified object is contained within this queue
     */
    public boolean contains(Object obj) {
        if( obj != null ) {
            for( int i=0, size=size(); i<size; i++ )
                if( obj.equals(array_[(i+tail_)%array_.length]) )
                    return true;
        }
        
        return false;
    }
    
    
    /**
     * @return True if the queue is empty
     */
    public boolean isEmpty() {
        return array_[head_] == null;
    }
    
    
    /**
     * @return A normalised version of the array
     */
    public Object[] toArray() {
        Object[] objs = new Object[array_.length];
        for( int i=0, size=size(); i<size; i++ )
            objs[i] = get(i);
        
        return objs;
    }
    
    
    /**
     * Gets the "i"th element of the queue where the first element, position 0,
     * is the head of the queue.
     * 
     * @param i The position of the element to retrieve
     * @return The element at the "i"th position in the queue
     * @throws IndexOutOfBoundsException if the index is out of bounds
     */
    public E get(int i) {
        if( i < 0 || i >= size() )
            throw new IndexOutOfBoundsException();
        
        int elem = ((head_-i)+array_.length) % array_.length;
        return array_[elem];
    }
    /**
     * Adds the specified element into the tail of the queue, if the queue will
     * exceed the maximum size then the head of the list is polled off and the
     * element that was polled is returned.
     * 
     * @param e The element to be offered into the queue
     * @return The element that has been replaced, null if no such element
     */
    public E offer(E e) {
        E rtn = ( size() == maxSize() ) ? poll() : null;
        if( isEmpty() ) {
            array_[tail_] = e;
        } else {
            tail_ = ((tail_-1) + array_.length) % array_.length;
            array_[tail_] = e;
        }
        
        return rtn;
    }
    public E peek() {
        return array_[head_];
    }
    public E poll() {
        E e = array_[head_];
        array_[head_] = null;
        if( tail_ > head_ ) {
            head_ = (head_-1 >= 0) ? head_-1 : head_-1+array_.length;
        } else {
            head_ = (head_-1 >= 0) ? head_-1 : 0;
        }
        
        if( isEmpty() )
            head_ = tail_ = 0;
        return e;
    }
    
    
    @Override
    public String toString() {
        if( isEmpty() )
            return "[]";
        
        String str = "";
        for( int i=0, size=size(); i<size; i++ )
            str += ", "+get(i);
        
        return "[" + str.substring(2) + "]";
    }
}
