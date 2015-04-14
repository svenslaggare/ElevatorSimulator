package marl.utility.struct;

import java.util.Iterator;


/**
 * Splay tree code from http://en.wikipedia.org/wiki/Splay_tree
 * Accessed on 2012-04-17
 * 
 * The SplayBTree uses templates to store the data in the tree,
 * this allows it to hold any type of data in there from integers
 * to objects. For an object to properly be usable in SplayBTree
 * it must implement the operators '==', '>', and '<'
 * 
 * @author Pete Scopes
 * @version 2012-09-06
 */
public class SplayTree<T extends Comparable<T>>
    implements Iterable<T>
{
    
    private static final class Node<T extends Comparable<T>> {
        public T data;
        public Node<T> parent, left, right;
        
        public Node(T data) {
            this.data = data;
            parent = left = right = null;
        }
    }
    private static final class SplayTreeIterator<T extends Comparable<T>>
        implements Iterator<T>
    {
        private SplayTree.Node<T> node = null,
                                  next = null;
        public SplayTreeIterator(SplayTree.Node<T> node)
        {
            this.next = node;
        }
        
        @Override
        public boolean hasNext() {
            return next != null;
        }
        @Override
        public T next() {
            // if no next
            if( next == null )
                return null;
            
            // if first time select the left most node
            if( node == null ) {
                while( next.left != null )
                    next = next.left;
            }
            node = next;
            
            if( next.right != null ) {
                next = next.right;
                while( next.left != null )
                    next = next.left;
            } else {
                while( next.parent != null && next == next.parent.right )
                    next = next.parent;
                next = next.parent;
            }
            
            
            return node.data;
        }
        @Override
        public void remove() {
            throw new RuntimeException("Cannot remove items from the tree - tree alters shape when items are removed");
        }
        
    }
    
    private int     size_;
    private Node<T> root_;

    /**
     * Constructor for objects of class SplayBTree
     */
    public SplayTree()
    {
        root_ = null;
        destroy();
    }
    
    
    
    

    /**
     * Returns the number of nodes that are currently
     * in the tree.
     * @return The size of the tree
     */
    public int size()
    {
        return size_;
    }

    /**
     * Destroys the tree, this will delete all the all the
     * nodes within the tree. It has to iterate through all
     * the nodes in the tree properly deleting them.
     */
    public void  destroy()
    {
        Node<T> node = root_,
                temp;
        
        // iteratively destroy the Tree
        while( node != null ) {
            if( node.left != null )        // check first for left children
                node = node.left;
            else if( node.right != null )  // then check for right children
                node = node.right;
            else {                         // if neither go back to the parent
                temp = node.parent;        // and delete the this node
                if( temp != null ) {
                    if( node == temp.left )
                        temp.left  = null;
                    else
                        temp.right = null;
                    
                    node.parent = null;
                }
                
                node = temp;
            }
        }
        
        // delete the root
        root_ = null;
        // reset the size of the tree
        size_ = 0;
    }
    /**
     * Inserts the given key into the Tree and then performs
     * a Splaying operation on the newly added key.
     */
    public void  insert(T key)
    {
        // if an empty tree create the root node
        if( root_ == null )
            root_ = new Node<T>(key);
        
        // the case 2 says that we must splay newly inserted node to root
        else {
            Node<T> temp1 = null,
                    temp2 = root_,
                    par   = null;
            
            while( temp2 != null ) {  // find the place to insert
                temp1 = temp2;
                int compResult = temp2.data.compareTo(key);
                if( compResult > 0 ) // temp2.data > key
                    temp2 = temp2.left;
                else if( compResult < 0 ) // temp2.data < key
                    temp2 = temp2.right;
                else {
                    if( temp2.data.equals(key) )
                        return ;
                }
            }
    
            par = temp1;          // insert the new key
            if( temp1.data.compareTo(key) > 0 ) { // temp1.data > key
                temp1.left  = new Node<T>(key);
                temp1       = temp1.left;
            }
            else {
                temp1.right = new Node<T>(key);
                temp1       = temp1.right;
            }
            temp1.parent = par;
    
            splay(temp1);
        }
        
        // increment the size of the tree
        size_++;
    }
    /**
     * Removes the given key from the Tree properly reformatting
     * the Tree as required.
     */
    public void  remove(T key)
    {
        Node<T> x = null, y = null, s = null;
        
        x = lookup(key, root_);
        if( x == null )
            return ;
        if( x == root_ ) {
            // if root has no children
            if( (x.left == null) && (x.right == null) ) {
                destroy();
                return ;
            }
            // if root has only left child
            else if( (x.left != null) && (x.right == null) ) {
                root_        = x.left;
                root_.parent = null;
            }
            // if root has only right child
            else if( (x.left == null) && (x.right != null) ) {
                root_        = x.right;
                root_.parent = null;
            }
            // root must have both left and right children
            else {
                s = successor(x);      // get the successor
                if( s != x.right ) {  // if not immediate
                    splay(s);         // splay the successor
                    remove(x.data);   // then remove x
                    return ;
                }
                else {
                    y = s;
                    s.parent      = x.parent;
                    x.left.parent = s;
                    s.left        = x.left;
                }
    
                root_ = y;
            }
        }
        else if( x.data.equals(key) ) {
            // if the deleted element is leaf
            if( (x.left == null) && (x.right == null) ) {
                y = x.parent;
                if( x ==  (x.parent.right) )
                    y.right = null;
                else
                    y.left  = null;
            }
            // if delete element having left child only
            else if( (x.left != null) && (x.right == null) ) {
                if( x == (x.parent.left) ) {
                    y             = x.parent;
                    x.left.parent = y;
                    y.left        = x.left;
                }
                else {
                    y             = x.parent;
                    x.left.parent = y;
                    y.right       = x.left;
                }
            }
            // if deleted element having right child only
            else if( (x.left == null) && (x.right != null) ) {
                if( x == (x.parent.left) ) {
                    y              = x.parent;
                    x.right.parent = y;
                    y.left         = x.right;
                }
                else {
                    y              = x.parent;
                    x.right.parent = y;
                    y.right        = x.right;
                }
            }
            // if the deleted element having two children
            else if( (x.left != null) && (x.right != null) ) {
    
                s = successor(x);  // get the successor
                if( x == (x.parent.left) ) {
                    if( s != x.right ) {
                        y = s.parent;
                        if( s.right != null ) {
                            s.right.parent = y;
                            y.left         = s.right;
                        }
                        else {
                            y.left         = null;
                        }
                        s.parent       = x.parent;
                        x.right.parent = s;
                        x.left.parent  = s;
                        s.right        = x.right;
                        s.left         = x.left;
                        x.parent.left  = s;
                    }
                    else {
                        y = s;
                        s.parent      = x.parent;
                        x.left.parent = s;
                        s.left        = x.left;
                        x.parent.left = s;
                    }
                }
                else if( x == (x.parent.right) ) {
                    if( s != x.right ) {
                        y = s.parent;
                        if( s.right != null ) {
                            s.right.parent = y;
                            y.left         = s.right;
                        }
                        else {
                            y.left         = null;
                        }
                        s.parent       = x.parent;
                        x.right.parent = s;
                        x.left.parent  = s;
                        s.right        = x.right;
                        s.left         = x.left;
                        s.parent.right = s;
                    }
                    else {
                        y = s;
                        s.parent       = x.parent;
                        x.left.parent  = s;
                        s.left         = x.left;
                        x.parent.right = s;
                    }
                }
            }
            splay(y);
        }

    // decrement the size of the Tree
    size_--;
    }
    /**
     * Searches for and returns the node of the given key. This
     * also performs Splaying operation of the searched for key.
     * @return The node of the given key
     */
    public T search(Comparable<T> key)
    {
        Node<T> x = lookup(key, root_);
        
        if( x != null ) {
            splay(x);
            return x.data;
        }
        else
            return null;
    }
    
    @Override
    public Iterator<T> iterator()
    {
        return new SplayTreeIterator<>(root_);
    }
    
    
    /**
     * Rotates the tree right about the given node, it must
     * be safe to do so. For more information please see:
     * http://en.wikipedia.org/wiki/Splay_tree
     */
    private Node<T> rotateRight(Node<T> node)
    {
        Node<T> x = node.left;
        node.left = x.right;
    
        if( x.right != null )
            x.right.parent = node;
        x.right = node;
        if( node.parent != null ) {
            if( node == node.parent.right )
                node.parent.right = x;
            else
                node.parent.left  = x;
        }
        x.parent    = node.parent;
        node.parent = x;
        if( node == root_ )
            return x;
        else
            return root_;
    }
    /**
     * Rotates the tree left about the given node, it must
     * be safe to do so. For more information please see:
     * http://en.wikipedia.org/wiki/Splay_tree
     */
    private Node<T> rotateLeft(Node<T> node)
    {
        Node<T> x  = node.right;
        node.right = x.left;
    
        if( x.left != null )
            x.left.parent = node;
        x.left = node;
        if( node.parent != null ) {
            if( node == node.parent.left )
                node.parent.left  = x;
            else
                node.parent.right = x;
        }
        x.parent    = node.parent;
        node.parent = x;
        if( node == root_ )
            return x;
        else
            return root_;
    }
    /**
     * Performs the splaying operation, please see
     * http://en.wikipedia.org/wiki/Splay_tree for more
     * information.
     */
    private void  splay(Node<T> x)
    {
        // while the given node is not the root
        while( x != root_ ) {
    
            // Performs Zig step
            if( x.parent == root_ ) {
                if( x == x.parent.left )
                    root_ = rotateRight(root_);
                else
                    root_ = rotateLeft(root_);
            }
            else {
                Node<T>
                    p = x.parent,  // now points to the parent of x
                    g = p.parent;  // now points to parent of x's parent
                // Performs the Zig-zig step when x is left and x's parent is left
                if( x == p.left && p == g.left ) {
                    root_ = rotateRight(g);
                    root_ = rotateRight(p);
                }
                // Performs the Zig-zig step when x is right and x's parent is right
                else if( x == p.right && p == g.right ) {
                    root_ = rotateLeft(g);
                    root_ = rotateLeft(p);
                }
                // Performs the Zig-zag step when x is right and x's parent is left
                else if( x == p.right && p == g.left ) {
                    root_ = rotateLeft(p);
                    root_ = rotateRight(g);
                }
                // Performs the Zig-zag step when x is left and x's parent is right
                else if( x == p.left && p == g.right ) {
                    root_ = rotateRight(p);
                    root_ = rotateLeft(g);
                }
            }
        }
    }
    /**
     * Selects the successor node of the given node.
     * If no such node then a null pointer is returned.
     * @return The successor node or null
     */
    private Node<T> successor(Node<T> x)
    {
        Node<T> temp1, temp2;
        temp1 = temp2 = x.right;
        while( temp1 != null ) {
            temp2 = temp1;
            temp1 = temp1.left;
        }
        return temp2;
    }
    /**
     * Internal search function.
     * @return the node of the given key
     */
    private Node<T> lookup(Comparable<T> value, Node<T> p)
    {
        Node<T> temp = null;
        if( p != null ) {
            temp = p;
            while( temp != null ) {
                int compResult = value.compareTo(temp.data);
                if( compResult == 0 )
                    return temp;
                else if( compResult < 0 )
                    temp = temp.left;
                else if( compResult > 0 )
                    temp = temp.right;
            }
            return temp;
        }
        else {
            throw new RuntimeException("<< Tree is empty >>");
        }
    }
    
    
    
    @Override
    public String toString() {
        if( root_ == null )
            return "SplayTree[]";
        
        String str = new String();
        Node<T> cur, pre = new Node<>(null);
        
        cur = root_;
        while( cur != null ) {
            if( cur.left == null ) {
                str += "," + cur.data;
                cur  = cur.right;
            } else {
                pre = cur.left;
                
                while( pre.right != null && pre.right != cur )
                    pre = pre.right;
                if( pre.right == null ) {
                    pre.right = cur;
                    cur       = cur.left;
                } else {
                    pre.right = null;
                    str      += "," + cur.data;
                    cur       = cur.right;
                }
            }
        }
        return "SplayTree["+ str.substring(1) +"]";
    }
}
