package crossing;

/**
 * Doubly linked list that stores segments. Left to right order represents the
 * order of the segments as the sweep line passes them. Each element of this 
 * list is stored in a leaf in the TreeNode datastructure. 
 * 
 * @author Daniel Hanisch
 */
public class LinkNode {
    // left neighbor
	LinkNode left;
    // right neighbor
	LinkNode right;
	// stored Segment
	Segment seg;

	/**
	 * Constructor
	 * @param s Segment
	 */
	public LinkNode(Segment s) {
		setSeg(s);
	}

	/**
	 * Eliminates current LinkNode from the LinkNode list 
	 */
	public void removeFromList() {
		if (getLeft() != null)
			getLeft().setRight(getRight());
		if (getRight() != null)
			getRight().setLeft(getLeft());
	}

	/**
     * Returns the left neighbor
	 * @return left neighbor
	 */
	public LinkNode getLeft() {
		return left;
	}

	/**
     * Returns the right neighbor
	 * @return right neighbor
	 */
	public LinkNode getRight() {
		return right;
	}

	/**
	 * Sets left neighbor
	 * @param node left neighbor
	 */
	public void setLeft(LinkNode node) {
		left = node;
	}

	/**
	 * Sets right neighbor
	 * @param node right neighbor
	 */
	public void setRight(LinkNode node) {
		right = node;
	}

	/**
     * Returns stored Segment 
	 * @return Segment stored in node
	 */
	public Segment getSeg() {
		return seg;
	}

	/**
	 * Sets segment
	 * @param segment
	 */
	public void setSeg(Segment segment) {
		seg = segment;
	}

}
