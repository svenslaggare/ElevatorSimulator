package marl.utility.struct;

import java.util.Iterator;

public class RedBlackTree<K extends Comparable<? super K>, V>
    implements Iterable<V>
{
	private static enum Colour {
		RED, BLACK;
	}
	public static final class Node<K extends Comparable<? super K>, V>
	{
		public K          key;
		public V          value;
		public Node<K, V> left;
		public Node<K, V> right;
		public Node<K, V> parent;
		public Colour     colour;
		
		public Node(K key, V value, Colour colour, Node<K, V> left, Node<K, V> right)
		{
			this.key    = key;
			this.value  = value;
			this.colour = colour;
			this.left   = left;
			this.right  = right;
			
			if( left  != null )  left.parent = this;
			if( right != null ) right.parent = this;
		}
		
		
		public Node<K, V> grandparent()
		{
			assert parent != null;         // Not the root node
			assert parent.parent != null;  // Not child of root
			return parent.parent;
		}
		public Node<K, V> sibling()
		{
			assert parent != null;  // Root has no sibling
			if( this == parent.left )
				return parent.right;
			else
				return parent.left;
		}
		public Node<K, V> uncle()
		{
			assert parent != null;         // Root node has no uncle
			assert parent.parent != null;  // Children of root have no uncle
			return parent.sibling();
		}
	
	}
	
	private static final class RedBlackTreeIterator<V> implements Iterator<V>
	{
	    private RedBlackTree.Node<?, V> node = null,
	                                    next = null;
        public RedBlackTreeIterator(RedBlackTree.Node<?, V> node)
        {
            this.next = node;
        }
        @Override
        public boolean hasNext() {
            return next != null;
        }
        @Override
        public V next() {
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
            
            
            return node.value;
        }
        @Override
        public void remove() {
            throw new RuntimeException("Cannot remove items from the tree - tree alters shape when items are removed");
        }
	}
	
	public static final boolean VERIFY_RBTREE = true;
	public void verifyProperties()
	{
		if( VERIFY_RBTREE ) {
			verifyProperty1(root_);
			verifyProperty2(root_);
			// Property 3 is implicit
			verifyProperty4(root_);
			verifyProperty5(root_);
		}
	}
	private static void verifyProperty1(Node<?,?> n)
	{
		assert nodeColour(n) == Colour.RED || nodeColour(n) == Colour.BLACK;
		if( n == null ) return;
		verifyProperty1(n.left);
		verifyProperty1(n.right);
	}
	private static void verifyProperty2(Node<?,?> root)
	{
		assert nodeColour(root) == Colour.BLACK;
	}
	private static Colour nodeColour(Node<?,?> n)
	{
		return n == null ? Colour.BLACK : n.colour;
	}
	private static void verifyProperty4(Node<?,?> n)
	{
		if( nodeColour(n) == Colour.RED ) {
			assert nodeColour(n.left)   == Colour.BLACK;
			assert nodeColour(n.right)  == Colour.BLACK;
			assert nodeColour(n.parent) == Colour.BLACK;
		}
		if( n == null ) return;
		verifyProperty4(n.left);
		verifyProperty4(n.right);
	}
	private static void verifyProperty5(Node<?,?> root)
	{
		verifyProperty5Helper(root, 0, -1);
	}
	private static int verifyProperty5Helper(Node<?,?> n, int blackCount, int pathBlackCount)
	{
		if( nodeColour(n) == Colour.BLACK )
			blackCount++;
		if( n == null ) {
			if( pathBlackCount == -1 )
				pathBlackCount = blackCount;
			else
				assert blackCount == pathBlackCount;
			return pathBlackCount;
		}
		pathBlackCount = verifyProperty5Helper(n.left,  blackCount, pathBlackCount);
		pathBlackCount = verifyProperty5Helper(n.right, blackCount, pathBlackCount);
		return pathBlackCount;
	}
	
	private Node<K, V> root_;
	private int        size_;

	public RedBlackTree()
	{
		root_ = null;
		size_ = 0;
		verifyProperties();
	}
	
	public int size() {
	    return size_;
	}
	
	
	@Override
	public Iterator<V> iterator() {
	    return new RedBlackTreeIterator<>(root_);
	}
	
	
	
	private Node<K, V> lookupNode(Comparable<? super K> key)
	{
		Node<K, V> n = root_;
		while( n != null ) {
			int compResult = key.compareTo(n.key);
			if( compResult == 0 )
				return n;
			else if( compResult < 0 )
				n = n.left;
			else {
				assert compResult > 0;
				n = n.right;
			}
		}
		
		return n;
	}
	public V lookup(K key)
	{
		Node<K, V> n = lookupNode(key);
		return n == null ? null : n.value;
	}
	
	public V search(Comparable<? super K> key) {
        Node<K, V> n = lookupNode(key);
        return n == null ? null : n.value;
	}

	
	private void rotateLeft(Node<K, V> n)
	{
		Node<K, V> r = n.right;
		replaceNode(n, r);
		n.right = r.left;
		if( r.left != null )
			r.left.parent = n;
		
		r.left   = n;
		n.parent = r;
	}
	private void rotateRight(Node<K, V> n)
	{
		Node<K, V> l = n.left;
		replaceNode(n, l);
		n.left = l.right;
		if( l.right != null )
			l.right.parent = n;
		
		l.right  = n;
		n.parent = l;
	}
	private void replaceNode(Node<K, V> oldn, Node<K, V> newn)
	{
		if( oldn.parent == null )
			root_ = newn;
		else {
			if( oldn == oldn.parent.left )
				oldn.parent.left  = newn;
			else
				oldn.parent.right = newn;
		}
		if( newn != null )
			newn.parent = oldn.parent;
	}

	
	public  void insert(K key, V value)
	{
		Node<K, V> insertedNode = new Node<K, V>(key, value, Colour.RED, null, null);
		if( root_ == null )
			root_ = insertedNode;
		else {
			Node<K, V> n = root_;
			while( true ) {
				int compResult = key.compareTo(n.key);
				if( compResult == 0 ) {
					n.value = value;
					return;
				}
				else if( compResult < 0 ) {
					if( n.left == null ) {
						n.left = insertedNode;
						break;
					} else 
						n = n.left;
				}
				else {
					assert compResult > 0;
					if( n.right == null ) {
						n.right = insertedNode;
						break;
					} else
						n = n.right;
				}
			}
			insertedNode.parent = n;
		}
		
		insertCase1(insertedNode);
		size_++;
		verifyProperties();
	}
	private void insertCase1(Node<K, V> n)
	{
		if( n.parent == null )
			n.colour = Colour.BLACK;
		else
			insertCase2(n);
	}
	private void insertCase2(Node<K, V> n)
	{
		if( nodeColour(n.parent) == Colour.BLACK )
			return; // Tree is still valid
		else
			insertCase3(n);
	}
	private void insertCase3(Node<K, V> n)
	{
		if( nodeColour(n.uncle()) == Colour.RED ) {
			n.parent.colour  = Colour.BLACK;
			n.uncle().colour = Colour.BLACK;
			n.grandparent().colour = Colour.RED;
			insertCase1(n.grandparent());
		}
		else
			insertCase4(n);
	}
	private void insertCase4(Node<K, V> n)
	{
		if( n == n.parent.right && n.parent == n.grandparent().left ) {
			rotateLeft(n.parent);
			n = n.left;
		}
		else if( n == n.parent.left && n.parent == n.grandparent().right ) {
			rotateRight(n.parent);
			n = n.right;
		}
		insertCase5(n);
	}
	private void insertCase5(Node<K, V> n)
	{
		n.parent.colour        = Colour.BLACK;
		n.grandparent().colour = Colour.RED;
		if( n == n.parent.left && n.parent == n.grandparent().left )
			rotateRight(n.grandparent());
		else {
			assert n == n.parent.right && n.parent == n.grandparent().right;
			rotateLeft(n.grandparent());
		}
	}
	
	
	public void delete(K key)
	{
		Node<K, V> n = lookupNode(key);
		if( n == null )
			return;  // Key not found, do nothing
		if( n.left != null && n.right != null ) {
			// Copy key/value from predecessor and then delete it instead
			Node<K, V> pred = maximumNode(n.left);
			n.key    = pred.key;
			n.value  = pred.value;
			n = pred;
		}
		
		assert n.left ==  null || n.right == null;
		Node<K, V> child = (n.right == null) ? n.left : n.right;
		if( nodeColour(n) == Colour.BLACK ) {
			n.colour = nodeColour(child);
			deleteCase1(n);
		}
		replaceNode(n, child);
		
		if( nodeColour(root_) == Colour.RED ) {
			root_.colour = Colour.BLACK;
		}
		
		// reduce the size and verify properties
		size_--;
		verifyProperties();
	}
	private static <K extends Comparable<? super K>,V> Node<K,V> maximumNode(Node<K,V> n)
	{
		assert n != null;
		while( n.right != null ) {
			n = n.right;
		}
		return n;
	}
	private void deleteCase1(Node<K,V> n)
	{
		if( n.parent == null )
			return;
		else
			deleteCase2(n);
	}
	private void deleteCase2(Node<K,V> n)
	{
	    if( nodeColour(n.sibling()) == Colour.RED ) {
	        n.parent.colour    = Colour.RED;
	        n.sibling().colour = Colour.BLACK;
	        if( n == n.parent.left )
	            rotateLeft(n.parent);
	        else
	            rotateRight(n.parent);
	    }
	    deleteCase3(n);
	}
	private void deleteCase3(Node<K,V> n)
	{
	    if( nodeColour(n.parent)          == Colour.BLACK &&
	        nodeColour(n.sibling())       == Colour.BLACK &&
	        nodeColour(n.sibling().left)  == Colour.BLACK &&
	        nodeColour(n.sibling().right) == Colour.BLACK )
	    {
	        n.sibling().colour = Colour.RED;
	        deleteCase1(n.parent);
	    }
	    else
	        deleteCase4(n);
	}
	private void deleteCase4(Node<K,V> n)
	{
	    if( nodeColour(n.parent)          == Colour.RED &&
	        nodeColour(n.sibling())       == Colour.BLACK &&
	        nodeColour(n.sibling().left)  == Colour.BLACK &&
	        nodeColour(n.sibling().right) == Colour.BLACK )
	    {
	        n.sibling().colour = Colour.RED;
	        n.parent.colour = Colour.BLACK;
	    }
	    else
	        deleteCase5(n);
	}
	private void deleteCase5(Node<K,V> n)
	{
	    if( n == n.parent.left &&
	        nodeColour(n.sibling())       == Colour.BLACK &&
	        nodeColour(n.sibling().left)  == Colour.RED &&
	        nodeColour(n.sibling().right) == Colour.BLACK )
	    {
	        n.sibling().colour      = Colour.RED;
	        n.sibling().left.colour = Colour.BLACK;
	        rotateRight(n.sibling());
	    }
	    else if( n == n.parent.right &&
	             nodeColour(n.sibling())       == Colour.BLACK &&
	             nodeColour(n.sibling().right) == Colour.RED &&
	             nodeColour(n.sibling().left)  == Colour.BLACK )
	    {
	        n.sibling().colour       = Colour.RED;
	        n.sibling().right.colour = Colour.BLACK;
	        rotateLeft(n.sibling());
	    }
	    deleteCase6(n);
	}
	private void deleteCase6(Node<K,V> n)
	{
	    n.sibling().colour = nodeColour(n.parent);
	    n.parent.colour    = Colour.BLACK;
	    if( n == n.parent.left ) {
	        assert nodeColour(n.sibling().right) == Colour.RED;
	        n.sibling().right.colour = Colour.BLACK;
	        rotateLeft(n.parent);
	    }
	    else
	    {
	        assert nodeColour(n.sibling().left) == Colour.RED;
	        n.sibling().left.colour = Colour.BLACK;
	        rotateRight(n.parent);
	    }
	}
	
	
	
	
	public Node<K, V> getRoot()
	{
	    return root_;
	}

}
