package crossing;

/**
 *  Status structure, that stores segments maintaining the order, in which the
 *  sweep line passes them.
 *  Segments are stored in LinkNodes, which themselves are only stored in the
 *  leaves of the TreeNode.
 *  Internal TreeNodes only contain keys that guide searches to the correct 
 *  leaf. At each internal node, the key of the rightmost leaf in its left 
 *  subtree is stored.
 *   
 * @author Daniel Hanisch
 */
public class TreeNode {
		
	private TreeNode leftChild;			// left TreeNode child of the TreeNode
	private TreeNode rightChild;		// right TreeNode child of the TreeNode
	private TreeNode parent;			// parent TreeNode
	private LinkNode node;				// LinkNode containing a segment
    
	/** Key of rightmost child of left subtree */
	protected TreeKey key;				
	private boolean isEmpty;			    
	/** Flag set, if a left child exists */
	protected boolean hasLeft = false;    
    /** Flag set, if a right child exists */     
	protected boolean hasRight = false;
				
	/**
	 * Constructor. Creates an empty TreeNode 
	 */
	public TreeNode() {
		setLeftChild(null);
		setRightChild(null);
		setParent(null);
		setNode(null);
		setKey(null);
		isEmpty = true;
	}
	
	/** 
	 * Constructor. Creates a new leaf TreeNode with the contents of its parent
	 * @param s segment that will be stored in the TreeNodes LinkNode
	 * @param current parent of this TreeNode
	 */
	public TreeNode(Segment s, TreeNode current) {
		setLeftChild(null);
		setRightChild(null);
		setParent(current);
		setNode(new LinkNode(s));
		setKey(s.getKey());
		isEmpty = false;
	}
	 
	/**
	 * Constructor. Used for moving link nodes in the tree
	 * @param link the LinkNode that is stored in this node
	 * @param current parent of this TreeNode
	 */
	public TreeNode(LinkNode link, TreeNode current) {
		setLeftChild(null);
		setRightChild(null);
		setParent(current);
		setNode(link);
		setKey(getNode().getSeg().getKey());
		isEmpty = false;
	}

	/**
	 * Inserts a new Segment in the tree. Segments are added with their 
	 * start-coordinate and gradient as a key
	 * 
	 * @param s inserted segment
	 */
	public void insert(Segment s) {
		TreeKey c = s.getKey();
			
		//inserts first Segment in tree
		if (isEmpty) {			
			setKey(c);
			setNode(new LinkNode(s));					
			isEmpty = false;			
			return;
		}
		
		/*
		 * inserts a smaller Segment in the Tree as a left child
		 * (compared to key stored in current TreeNode)
		 * recursive search for insert-position until a leaf-node is reached
		 */		
		switch (key.compareTo(c)) {
		case -1 :
			// follow tree structure until a leaf is reached
			if (hasLeft) leftChild.insert(s);
			
			// ended up at a leaf 
			else {
				
				// current entries of TreeNode forms new rightChild
				setRightChild(new TreeNode(node,this));
				hasRight = true; 
				
				// inserted Segment forms new leftChild
				setLeftChild(new TreeNode(s,this));
				hasLeft = true;

				// inner Nodes only store key values							
				setNode(null);  	
				setKey(c);
				
				//Update of Nodelist-neighbours:
				LinkNode gamma = getRightChild().getNode();
				LinkNode alpha = gamma.getLeft();
				LinkNode beta = getLeftChild().getNode();
				
				// Update LinkNode neighbors (if they exist) 
				try {				
					alpha.setRight(beta); 
				} catch (Exception e) {};
				beta.setLeft(alpha);
				beta.setRight(gamma);
				gamma.setLeft(beta);
			}
			return;
		/*
		 * inserts a bigger Segment in the Tree as a right child
		 * (compared to key stored in current TreeNode)
		 * recursive search for insert-position until a leaf-node is reached
		 */
		case 1:
			if (hasRight) rightChild.insert(s);
			
			/*
			 *  this case can only be reached, when a new maximum segment is
			 *  inserted in the tree. Otherwise it will always be added as a 
			 *  left child
			 */
			else { 
				setLeftChild(new TreeNode(node,this));
				hasLeft = true;		
				setRightChild(new TreeNode(s,this));				
				hasRight = true;					
				setNode(null);				
				//Update of Nodelist-Neighbours: 
				LinkNode gamma = getRightChild().getNode();
				LinkNode beta = getLeftChild().getNode();
				beta.setRight(gamma);
				gamma.setLeft(beta);	
			}	  		
			return;				
		default:
			System.out.println("Could not insert Segment. Key already exists");
			return;			
		}
	}
	
	/**
	 * Deletes a Segment from the Tree.
	 * @param s deleted Segment
	 */
	public void delete(Segment s) {
		TreeKey c = s.getKey();	
		
	switch (key.compareTo(c)){
			/* 
			 * subtree reached where rightmost child of left subtree equals 
			 *chosen key
			 */
			case 0:
				// only the case for rightmost child in tree
				if (!hasLeft) {				
					TreeNode tmp = getParent();				
					// parent was not root
					if (tmp == null) {
						getNode().removeFromList();
						setKey(null);
						setNode(null);
						isEmpty = true;
						return;
					}						
					// parent of tmp node is not root
					if (tmp.getParent() != null){
						tmp.getParent().setRightChild(tmp.getLeftChild());
						tmp.getLeftChild().setParent(tmp.getParent());
						if (tmp.getLeftChild().getNode() != null) 
							tmp.getLeftChild().getNode().setRight(null);
						else {
							TreeNode current=tmp.getLeftChild().getRightChild();
							while (current.hasRight) {
								current = current.getRightChild();
							}
							current.getNode().setRight(null);
						}
						tmp.setLeftChild(null);
						tmp.setRightChild(null);
						return;				
						
					// case: parent of reached node is root 
					} else {
						tmp.setNode(tmp.getLeftChild().getNode());
						tmp.setKey(tmp.getLeftChild().getKey());
						tmp.getRightChild().getNode().removeFromList();
						
						if (!tmp.getLeftChild().hasLeft) {
							tmp.setRightChild(null);
							tmp.hasRight = false;
							tmp.setLeftChild(null);
							tmp.hasLeft = false;					
						} else {
							tmp.getLeftChild().getLeftChild().setParent(tmp);
							tmp.getLeftChild().getRightChild().setParent(tmp);
							tmp.setRightChild(tmp.getLeftChild().getRightChild());
							tmp.setLeftChild(tmp.getLeftChild().getLeftChild());				
						}															
						return;} 					
				}					
				
				// leftChild is a leaf in the tree
				if (!getLeftChild().hasLeft) {
					//updates list structure
					getLeftChild().getNode().removeFromList();
					setLeftChild(null);
					hasLeft = false;
		
					// normal case: inner node
					if (getRightChild().hasRight) {					
						if (getParent() != null) {
							// find correct position
							if (getParent().getKey().compareTo(c) == -1) {																		
								getParent().setLeftChild(getRightChild());
								getRightChild().setParent(getParent());
								setKey(getRightChild().getKey());
								setParent(null);
								setLeftChild(null);
								setRightChild(null);
								return;
							} else {
								getParent().setRightChild(getRightChild());
								getRightChild().setParent(getParent());
								setParent(null);
								setRightChild(null);
								setLeftChild(null);
								return;
							}
						// deletion of root
						} else {
							TreeNode tmp = getRightChild();
							setKey(tmp.getKey());
							setLeftChild(tmp.getLeftChild());
							hasLeft = true;
							setRightChild(tmp.getRightChild());							
							tmp.setParent(null);
							getLeftChild().setParent(this);
							getRightChild().setParent(this);							
							setParent(null);
							return;
						} 														
					}						
					// moves contents of right child up one level 
					setNode(getRightChild().getNode());
					setKey(getNode().getSeg().getKey());
					setRightChild(null);
					hasRight = false;
					return;     
				}					
				// only other possibility: found value at inner node whose
                // left child is also a inner node
				 TreeNode tmp = getLeftChild();
				 while (tmp.hasRight) {
				 	tmp = tmp.getRightChild();
				 }
				 tmp = tmp.getParent();
				 if (!tmp.getLeftChild().hasLeft){
				 	tmp.setNode(tmp.getLeftChild().getNode());
					tmp.setKey(tmp.getNode().getSeg().getKey());
					tmp.getRightChild().getNode().removeFromList();
					tmp.setLeftChild(null);
					tmp.setRightChild(null);
					tmp.hasLeft = false;
					tmp.hasRight = false;
					setKey(tmp.getKey());			 
					return;
				 } else {
				 	tmp.getRightChild().getNode().removeFromList();
				 	tmp.getLeftChild().setParent(tmp.getParent());
				 	if (tmp.getParent().getLeftChild() == tmp ) {
				 		tmp.getParent().setLeftChild(tmp.getLeftChild());
					 	tmp.getParent().setKey(tmp.getKey());
					 	return;
				 	} else {
				 		tmp.getParent().setRightChild(tmp.getLeftChild());
				 		TreeNode back = tmp;
				 		while (back.getKey().compareTo(
                                tmp.getRightChild().getKey()) !=0) {
				 			back = back.getParent();
				 		}
				 		back.setKey(tmp.getKey());
				 		return;
				 	}
				 	
				 }			 
				 							
//		 follow left subtree if compared key is smaller
			case -1:
				getLeftChild().delete(s);
				return;
//		follow right subtree if compared key is bigger
			case 1:
				getRightChild().delete(s);
				return;
		}
	}
	
	/**
	 * Returns the LinkNode in which a Segment is stored.
	 * @param seg wanted Segment
	 * @return LinkNode containing wanted segment
	 */
	public LinkNode find(Segment seg) {
		TreeKey c = seg.getKey();
		
		switch (key.compareTo(c)) {
//		follow left subtree if compared key is smaller
		case -1:
			return getLeftChild().find(seg);
//		follow right subtree if compared key is bigger
		case 1:
			return getRightChild().find(seg);
//		subtree reached where rightmost child of left subtree equals chosen key
		case 0:
			// only the case for rightmost child in tree
			if (!hasLeft) {
				return getNode();			
			} 			
			// leftChild is a leaf in the tree
			if (!getLeftChild().hasLeft) {
				return getLeftChild().getNode();
			} else {
				TreeNode tmp = getLeftChild();
				while (tmp.hasRight) {
					tmp = tmp.getRightChild();
				}
				return tmp.getNode();
			}
		default:
			System.out.println ("Error: Could not find Segment in tree");
			return null;			
		}	
	}
			
	/**
	 * Returns the next Segment to the left of an EventPoint ep.
	 * If no left neighbor exists, null is returned.
	 * @param ep EventPoint
	 * @return Segment to the left of this EventPoint
	 */
	public Segment findLeft(EventPoint ep) {
		TreeKey eventPointKey=new TreeKey(ep.getCoordinate(),Double.MAX_VALUE);
		
		if (key.compareTo(eventPointKey) <= 0 & this.hasLeft) {
			return this.getLeftChild().findLeft(ep);		
		}
		else if (key.compareTo(eventPointKey) == 1 & this.hasRight) {
			return this.getRightChild().findLeft(ep);
		}
		// search ended up at a leaf and returns the Segment stored 
		// in its left neighbour
		if (this.getNode().left != null) 
			return this.getNode().getLeft().getSeg();
		else return null;
		
	}
	
	/**
	 * Returns the next Segment to the right of an EventPoint ep.
	 * If no right neighbor exists, null is returned.
	 * @param ep EventPoint
	 * @return Segment to the right of this EventPoint
	 */
	public Segment findRight(EventPoint ep) {
		TreeKey eventPointKey=new TreeKey(ep.getCoordinate(),Double.MIN_VALUE);
		
		if (key.compareTo(eventPointKey) <= 0 & this.hasLeft) {
			return this.getLeftChild().findRight(ep);		
		}
		else if (key.compareTo(eventPointKey) == 1 & this.hasRight) {
			return this.getRightChild().findRight(ep);
		}
		// search ended up at a leaf and returns the Segment stored 
		// in its right neighbour
		if (this.getNode() != null) 
			return this.getNode().getSeg();
		else return null;
	}
	
				
	/**
     * Returns TreeKey of the node
	 * @return key
	 */
	public TreeKey getKey() {
		return key;
	}

	/**
     * Returns left child of the node
	 * @return leftChild
	 */
	public TreeNode getLeftChild() {
		return leftChild;
	}

	/**
     * Returns LinkNode stored in the node
	 * @return node
	 */
	public LinkNode getNode() {
		return node;
	}

	/**
     * Returns right child of the node
	 * @return rightChild
	 */
	public TreeNode getRightChild() {
		return rightChild;
	}

	/**
     * Sets TreeKey of the node
	 * @param coordinate
	 */
	public void setKey(TreeKey coordinate) {
		key = coordinate;
	}

	/**
     * Sets left child of the node
	 * @param node
	 */
	public void setLeftChild(TreeNode node) {
		leftChild = node;
	}

	/**
     * Sets LinkNode of the node (only leaves store LinkNodes)
	 * @param node
	 */
	public void setNode(LinkNode node) {
		this.node = node;
	}

	/**
     * Sets right child of the node
	 * @param node
	 */
	public void setRightChild(TreeNode node) {
		rightChild = node;
	}

	/**
     * Returns the parent of the node
	 * @return parent
	 */
	public TreeNode getParent() {
		return parent;
	}

	/**
     * Sets the parent of the node
	 * @param node
	 */
	public void setParent(TreeNode node) {
		parent = node;
	}
	

	/**
     * Returns true/false if the TreeNode datastructure has entries or not.
	 * @return true, if the whole TreeNode is empty
	 */
	public boolean isEmpty() {
		return isEmpty;
	}
}




