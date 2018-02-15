/*
 * Created on 01.03.2005
 *
 */
package org.visnacom.model;

import java.util.*;
import java.util.LinkedList;
import java.util.List;
import java.math.*;

/**
 * @author F. Pfeiffer
 * 
 * Implementation of basegraph. Contains efficient implementation of expandEdge.
 */
public class DynamicLeaves extends BaseCompoundGraph {

	// maps nodes to their corresponding AVLTrees
	private HashMap S_out, S_in;

	// maps nodes to their minimal/maximal element according to
	// total ordering
	private HashMap min, max;

	// maps nodes to bst containing derived edges between children
	private HashMap N;

	// the order maintenance instance
	private OrderMaintenance om;

	// for determining ancestors at given depth
	private AncestorAtLevel ancAtLevel;

	/**
	 * Constructor.
	 *  
	 */
	public DynamicLeaves() {
		super();
		om = new OrderMaintenance();
		S_out = new HashMap();
		S_out.put(getRoot(), new AVLTree(om));
		S_in = new HashMap();
		S_in.put(getRoot(), new AVLTree(om));
		min = new HashMap();
		max = new HashMap();

		N = new HashMap();
		N.put(getRoot(), new TreeSet(new EdgeComparator()));
		min.put(getRoot(), null);
		max.put(getRoot(), null);

		ancAtLevel = new AncestorAtLevel(this);
		ancAtLevel.preprocess();

		om.insert(null, (OrderedNode) getRoot());

	}

	/**
	 * Test purpose only.
	 * 
	 * @param dyn
	 */
	public DynamicLeaves(DynamicLeaves dyn) {
		super(dyn);
	}

	/**
	 * Test purpose only.
	 * 
	 * @param o
	 *            The order maintenance datastructure.
	 */
	public DynamicLeaves(OrderMaintenance o) {
		super();
		om = o;
		S_out = new HashMap();
		S_out.put(getRoot(), new AVLTree(om));
		S_in = new HashMap();
		S_in.put(getRoot(), new AVLTree(om));
		min = new HashMap();
		max = new HashMap();
		N = new HashMap();
		N.put(getRoot(), new TreeSet(new EdgeComparator()));
		min.put(getRoot(), null);
		max.put(getRoot(), null);
		ancAtLevel = new AncestorAtLevel(this);
		ancAtLevel.preprocess();
	}

	/**
	 * Calculates all children of a given node for which there is an induced
	 * edge from the child to the corresponding end node of given edge.
	 * 
	 * @param edge
	 *            The given edge which is to be 'expanded'.
	 * @param node
	 *            The node for the children of which we are looking.
	 * @param isLeaf
	 *            True iff second node of edge is leaf in view.
	 * @return List of children for which there is an induced edge.
	 */
	public List expandEdge(Edge edge, Node node, boolean isLeaf) {
		Iterator it;

		HashMap S = null;
		HashMap otherS = null;

		Node u = edge.getSource();
		Node v = edge.getTarget();

		// gets the used S-set
		Node secNode = null;
		if (node == v) {
			secNode = u;
			S = S_out;
			otherS = S_in;
		} else {
			secNode = v;
			S = S_in;
			otherS = S_out;
		}

		// list of child nodes
		LinkedList result = new LinkedList();

		it = this.getChildren(node).iterator();
		Node min_u = (Node) it.next();
		Node max_u = (Node) ((DLL) this.getChildren(node)).getLast();

		Node t = min_u;
		Node s = null;
		Node iter = min_u;

		// expandEdge-algorithm, see thesis
		do {
			s = this.succ((AVLTree) S.get(secNode), (Node) min.get(t));

			if (om.lessOrEqual((OrderedNode) s, (OrderedNode) max.get(max_u))) {
				if (om.compare(s, (Node) max.get(t)) > 0) {
					t = this.getAncAtLevel(s, inclusionDepth(min_u));
				}
				boolean add = true;
				if (!isLeaf) {
					AVLTree set = (AVLTree) otherS.get(t);
					if (!set.contains(secNode)) {
						add = false;
					}
				}
				if (add) {
					result.add(t);
				}
				if (t != max_u) {
					while (iter != t) {
						iter = (Node) it.next();
					}
					t = (Node) it.next();
					iter = t;
				} else {
					break;
				}
			}
		} while (om.lessOrEqual((OrderedNode) s, (OrderedNode) max.get(max_u)));

		return result;

	}

	/**
	 * Gets the ancestor of a given node at a specified level.
	 * 
	 * @param desc
	 *            The node for the ancestor of which one is looking.
	 * @param level
	 *            The level of the ancestor.
	 * @return The ancestor at given level.
	 */
	private Node getAncAtLevel(Node desc, BigInteger level) {
		Node result = null;
		result = ancAtLevel.levelAncestor(desc, level);
		return result;
	}

	/**
	 * @see org.visnacom.model.CompoundGraph#newLeafSupressDynBinding(org.visnacom.model.Node)
	 */
	protected Node newLeafSupressDynBinding(Node par) {
		OrderedNode n = (OrderedNode) super.newLeafSupressDynBinding(par);

		// maintains correct total ordering
		if (this.bigNumOfNodes().equals(new BigInteger("2"))
				&& par == getRoot()) {
			min.put(getRoot(), n);
			max.put(getRoot(), getRoot());
			om.insert((OrderedNode) getRoot(), n);
			om.swap((OrderedNode) getRoot(), n);
		} else if (this.bigNumOfChildren(par).equals(new BigInteger("1"))) {
			om.insert((OrderedNode) min.get(par), n);
			min.put(par, n);
			om.swap((OrderedNode) par, n);

			// updates min of ancestors if necessary
			Node parent = par;
			while (parent != null) {
				if (min.get(parent) == par) {
					min.put(parent, n);
				}
				parent = getParent(parent);
			}
		} else {
			om.insert((OrderedNode) max.get(par), n);
			om.swap((OrderedNode) max.get(par), n);
		}

		// sets new S-sets
		AVLTree bst_out = new AVLTree(om);
		S_out.put(n, bst_out);
		AVLTree bst_in = new AVLTree(om);
		S_in.put(n, bst_in);

		// sets min/max
		min.put(n, n);
		max.put(n, n);

		// leaf has no children
		N.put(n, new TreeSet(new EdgeComparator()));
		ancAtLevel.addLeaf(n, par);

		return n;
	}

	/**
	 * @see org.visnacom.model.CompoundGraph#deleteLeafSupressDynBinding(org.visnacom.model.Node)
	 */
	protected void deleteLeafSupressDynBinding(Node leafNode) {
		OrderedNode leaf = (OrderedNode) leafNode;

		super.deleteLeafSupressDynBinding(leaf);

		// updates data structures
		ancAtLevel.removeLeaf(leafNode);
		om.delete(leaf);
		changeMin(leaf);
		S_out.remove(leaf);
		S_in.remove(leaf);
	}

	/**
	 * Changes min-mapping due to deletion of leaf.
	 * 
	 * @param n
	 *            The leaf to be deleted.
	 */
	private void changeMin(OrderedNode n) {
		Node par = getParent(n);
		while (par != null) {
			Node valueMin = (Node) min.get(par);
			if (valueMin == n) {
				if (bigNumOfChildren(par).equals(new BigInteger("1"))) {
					min.put(par, par);
				} else {
					min.put(par, n.getSucc());
				}
			}
			par = getParent(par);
		}
	}

	/**
	 * Recalculates min/max mappings from given node upwards to root.
	 * 
	 * @param node
	 *            The node at which the calculation is started.
	 */
	private void changeMinMax(OrderedNode node) {
		Node newPar = node;
		while (newPar != null) {
			Collection allDesc = allDescendants(newPar);
			OrderedNode newMin = node;
			Iterator it = allDesc.iterator();
			while (it.hasNext()) {
				OrderedNode n = (OrderedNode) it.next();
				if (newMin == null || om.compare(n, newMin) < 0) {
					newMin = n;
				}
			}
			min.put(newPar, newMin);
			max.put(newPar, newPar);
			newPar = getParent(newPar);
		}
	}

	/**
	 * Gets the successor which lies within a AVLTree.
	 * 
	 * @param bst
	 *            The datastructure in which the successor is looked for.
	 * @param n
	 *            The successor of this node is looked for.
	 * @return The successor.
	 */
	private Node succ(AVLTree bst, Node n) {
		return (Node) bst.succ(n, false, false);
	}

	/**
	 * @see org.visnacom.model.CompoundGraph#newEdgeSupressDynBinding(org.visnacom.model.Node,
	 *      org.visnacom.model.Node)
	 */
	protected Edge newEdgeSupressDynBinding(Node source, Node target) {
		Edge edg = super.newEdgeSupressDynBinding(source, target);

		Node commonAnc = nearestCommonAncestor(source, target);
		BigInteger cAncLevel = this.inclusionDepth(commonAnc);
		Node sourcePar = this.getAncAtLevel(source, cAncLevel
				.add(new BigInteger("1", 2)));
		Node targetPar = this.getAncAtLevel(target, cAncLevel
				.add(new BigInteger("1", 2)));

		assert (sourcePar != targetPar);

		// updates N
		((TreeSet) N.get(commonAnc)).add(this.newEdgeFac(sourcePar, targetPar));

		// updates S-sets
		addToS(edg);
		return edg;
	}

	/**
	 * Updates S by adding new elements.
	 * 
	 * @param edg
	 *            The edge which causes changes.
	 */
	private void addToS(Edge edg) {

		Node source = edg.getSource();
		Node target = edg.getTarget();

		((AVLTree) S_out.get(source)).insert(target, edg);

		((AVLTree) S_in.get(target)).insert(source, edg);

		// changes S_out upwards till root
		Node par = getParent(source);
		while (par != getRoot()) {
			AVLTree parAVLTree = (AVLTree) S_out.get(par);
			parAVLTree.insert(target, edg);
			par = getParent(par);
		}

		// changes S_in upwards till root
		par = getParent(target);
		while (par != getRoot()) {
			AVLTree parAVLTree = (AVLTree) S_in.get(par);
			parAVLTree.insert(source, edg);
			par = getParent(par);
		}
	}

	/**
	 * Updates S by removing elements.
	 * 
	 * @param edg
	 *            The edge which causes changes.
	 */
	private void delFromS(Edge edg) {
		Node source = edg.getSource();
		Node target = edg.getTarget();
		Node par = null;

		// changes S_out upwards till root
		par = source;
		while (par != getRoot()) {
			AVLTree parAVLTree = (AVLTree) S_out.get(par);
			parAVLTree.remove(target, edg);
			par = getParent(par);
		}

		// changes S_in upwards till root
		par = target;
		while (par != getRoot()) {
			AVLTree parAVLTree = (AVLTree) S_in.get(par);
			parAVLTree.remove(source, edg);
			par = getParent(par);
		}

	}

	/**
	 * @see org.visnacom.model.CompoundGraph#deleteEdgeSupressDynBinding(org.visnacom.model.Edge)
	 */
	protected void deleteEdgeSupressDynBinding(Edge edge) {
		// updates S
		delFromS(edge);

		super.deleteEdgeSupressDynBinding(edge);

		Node source = edge.getSource();
		Node target = edge.getTarget();

		Node commonAnc = nearestCommonAncestor(source, target);
		BigInteger cAncLevel = this.inclusionDepth(commonAnc);

		Node sourcePar = this.getAncAtLevel(source, cAncLevel
				.add(new BigInteger("1", 2)));
		Node targetPar = this.getAncAtLevel(target, cAncLevel
				.add(new BigInteger("1", 2)));

		assert (sourcePar != targetPar);

		// updates N
		Edge e = newEdgeFac(sourcePar, targetPar);
		((TreeSet) N.get(commonAnc)).remove(e);
	}

	/**
	 * @see org.visnacom.model.CompoundGraph#split(java.util.List)
	 */
	public Node split(List l) {
		assert checkConsistency();

		Node splitNode = super.split(l);
		OrderedNode n = (OrderedNode) splitNode;

		AVLTree bst_out = (AVLTree) S_out.get(n);
		AVLTree bst_in = (AVLTree) S_in.get(n);

		// reordering due to altered total ordering
		reorder();

		// children list gets ordered
		List child = getChildren(n);
		TreeSet childOrder = new TreeSet(om);
		Iterator it = child.iterator();
		while (it.hasNext()) {
			Node c = (OrderedNode) it.next();
			childOrder.add(c);
			delParentChild(c);
		}
		it = childOrder.iterator();
		Node pred = null;
		while (it.hasNext()) {
			Node c = (Node) it.next();
			setParentChildren(c, n, pred, false);
			pred = c;
		}

		// S-sets for new inner node are made
		it = getChildrenIterator(n);
		while (it.hasNext()) {
			OrderedNode next = (OrderedNode) it.next();
			AVLTree AVLTree_out = (AVLTree) S_out.get(next);
			Iterator bstIter = AVLTree_out.getAVLIterator();
			while (bstIter.hasNext()) {
				OrderedNode node = (OrderedNode) bstIter.next();
				bst_out.insert(node, AVLTree_out.getMultiplicity(node),
						AVLTree_out.getEdges(node));
			}
			AVLTree AVLTree_in = (AVLTree) S_in.get(next);
			bstIter = AVLTree_in.getAVLIterator();
			while (bstIter.hasNext()) {
				OrderedNode node = (OrderedNode) bstIter.next();
				bst_in.insert(node, AVLTree_in.getMultiplicity(node),
						AVLTree_in.getEdges(node));
			}
		}

		// inner node is placed correctly within ordering
		Node parN = this.getParent(n);
		List childrenContainingClus = this.getChildren(this.getParent(n));
		childrenContainingClus.remove(n);
		super.delParentChild(n);
		it = childrenContainingClus.iterator();
		Node predec = null;
		while (it.hasNext()) {
			OrderedNode next = (OrderedNode) it.next();
			if (om.lessOrEqual(n, next)) {
				break;
			}
			predec = next;
		}

		super.setParentChildren(n, parN, predec, false);

		// N is updated
		N.put(n, getChildEdges(n));
		Node par = this.getParent(n);
		while (par != getRoot()) {
			N.put(par, getChildEdges(par));
			par = this.getParent(par);
			if ((OrderedNode) max.get(par) == (OrderedNode) max.get(n)) {
				max.put(par, n);
			}
		}

		ancAtLevel.preprocess();

		assert checkConsistency();
		return n;
	}

	/**
	 * Performs reordering of all S-sets.
	 *  
	 */
	private void reorder() {
		Iterator it = this.getAllNodesIterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			AVLTree in = (AVLTree) S_in.get(n);
			AVLTree out = (AVLTree) S_out.get(n);
			if (in != null) {
				in.reorder();
			}
			if (out != null) {
				out.reorder();
			}
		}
	}

	/**
	 * @see org.visnacom.model.CompoundGraph#merge(org.visnacom.model.Node)
	 */
	public void merge(Node innerNode) {
		Node par = getParent(innerNode);
		super.merge(innerNode);

		// deletion from total ordering
		om.delete((OrderedNode) innerNode);
		N.remove(innerNode);

		// updates S sets
		S_out.remove(innerNode);
		S_in.remove(innerNode);

		// needs to change min, max (also of parents)
		changeMinMax((OrderedNode) getParent(innerNode));
		min.remove(innerNode);
		max.remove(innerNode);

		// updates N
		N.put(par, getChildEdges(par));

		ancAtLevel.preprocess();
	}

	/**
	 * Initially calculates all interior edges (for split).
	 * 
	 * @param innerNode
	 *            The cluster.
	 * @return Collection of interior edges.
	 */
	private Collection getChildEdges(Node innerNode) {
		TreeSet edges = new TreeSet(new EdgeComparator());
		Object[] child = this.getChildren(innerNode).toArray();
		for (int i = 0; i < child.length; i++) {
			Node n1 = (Node) child[i];
			for (int j = i + 1; j < child.length; j++) {
				Node n2 = (Node) child[j];
				if (this.derivedEdge(n1, n2)) {
					edges.add(this.newEdgeFac(n1, n2));
				}
				if (this.derivedEdge(n2, n1)) {
					edges.add(this.newEdgeFac(n2, n1));
				}
			}
		}
		return edges;
	}

	/**
	 * Gets interior edges.
	 * 
	 * @param innerNode
	 *            The edges contained by this node are looked for.
	 * @return The edges contained by an inner node.
	 */
	public Collection contDerEdges(Node innerNode) {
		return (TreeSet) N.get(innerNode);
	}

	/**
	 * Factory for total ordering.
	 * 
	 * @return OrderedNode instance.
	 */
	protected Node newNodeFac() {
		return new OrderedNode(idCounter++);
	}

	/**
	 * @see org.visnacom.model.BaseCompoundGraph#derivedEdge(org.visnacom.model.Node,
	 *      org.visnacom.model.Node)
	 */
	public boolean derivedEdge(Node u, Node v) {
		OrderedNode succ = (OrderedNode) this.succ((AVLTree) S_out.get(u),
				(Node) min.get(v));
		if (succ == null) {
			return false;
		}
		if (om.lessOrEqual(succ, (OrderedNode) max.get(v))) {
			return true;
		}
		return false;
	}

	/**
	 * @see org.visnacom.model.CompoundGraph#addChild(org.visnacom.model.Node, org.visnacom.model.Node,
	 *      int, org.visnacom.model.Node)
	 */
	protected void addChild(Node par, Node child, int mode, Node pred) {
		DLL childList = (DLL) getChildren(par);
		if (mode == AT_END) {
			if (!hasChildren(par)) {
				setParentChildren(child, par, null, false);
			} else {
				setParentChildren(child, par, (Node) childList.getFirst(), true);
			}
		} else if (mode == AT_BEGINNING) {
			setParentChildren(child, par, null, false);
		} else {
			setParentChildren(child, par, pred, false);
		}
	}

	/**
	 * @see org.visnacom.model.CompoundGraph#moveNodeSupressDynBinding(org.visnacom.model.Node,
	 *      org.visnacom.model.Node, boolean)
	 */
	protected void moveNodeSupressDynBinding(Node node, Node newParent,
			boolean splitMovement) {

		Node oldParent = getParent(node);

		// better runtime for splitting
		if (!splitMovement) {
			moveChanges(node, newParent);
		}

		OrderedNode minNewPar = (OrderedNode) min.get(newParent);

		OrderedNode minNode = (OrderedNode) min.get(node);
		OrderedNode maxNode = (OrderedNode) max.get(node);

		OrderedNode predMinNew = minNewPar.getPred();
		OrderedNode succMinNew = minNewPar.getSucc();
		while (succMinNew != null && om.compare(succMinNew, maxNode) <= 0) {
			succMinNew = succMinNew.getSucc();
		}

		OrderedNode nextPred = minNode;

		assert (minNode != minNode.getSucc());

		// deletes moved nodes from ordering
		LinkedList l = new LinkedList();
		OrderedNode iter = minNode;
		while (iter != null && iter != maxNode && iter != newParent) {
			OrderedNode nextIter = iter.getSucc();
			om.delete(iter);
			l.add(iter);
			iter = nextIter;
		}
		l.add(maxNode);
		om.delete(maxNode);

		// puts nodes into new place in ordering,
		// relative ordering is maintained
		Iterator lIt = l.iterator();
		while (lIt.hasNext()) {
			OrderedNode on = (OrderedNode) lIt.next();
			if (minNewPar == minNode && minNewPar == on) {
				if (predMinNew == null && succMinNew == null) {
					om.insert(null, on);
				} else if (predMinNew == null && succMinNew != null) {
					om.insert(succMinNew, on);
					om.swap(succMinNew, on);
				} else if (predMinNew != null) {
					om.insert(predMinNew, on);
				}
				nextPred = on;
				assert (nextPred.getSucc() != nextPred);
			} else if (minNewPar == minNode) {
				om.insert(nextPred, on);
				nextPred = on;
			} else {
				om.insert(minNewPar, on);
				om.swap(minNewPar, on);
			}
		}

		max.put(newParent, newParent);
		min.put(newParent, minNode);

		// better runtime for splitting
		if (!splitMovement) {
			reorder();
		}

		// needs to be called before changing of min and max, so that
		// min, max are updated after movement;
		// futhermore, notify must only be called after having changed
		// min,max
		super.moveNodeSupressDynBinding(node, newParent, true);

		// updates min/max
		changeMinMax((OrderedNode) newParent);
		changeMinMax((OrderedNode) node);
		changeMinMax((OrderedNode) oldParent);

		// better runtime for splitting
		if (!splitMovement) {
			ancAtLevel.preprocess();
			changeN(getParent(node));
			changeN(newParent);
			changeN(oldParent);
		}
		notify(new ActionMove(node, newParent, oldParent, splitMovement));

		assert (this.isAncestor(newParent, (Node) min.get(newParent)));
		assert (this.isAncestor(oldParent, (Node) min.get(oldParent)));
	}

	/**
	 * Updates N upwards to root.
	 * 
	 * @param n
	 *            Start of upadte.
	 */
	private void changeN(Node n) {
		N.put(n, getChildEdges(n));
		Node par = this.getParent(n);
		while (par != null) {
			N.put(par, getChildEdges(par));
			par = this.getParent(par);
		}
	}

	/**
	 * Performs changes due to movement of node into another inner node.
	 * 
	 * @param node
	 *            The node to be moved.
	 * @param newParent
	 *            The new parent of node.
	 */
	private void moveChanges(Node node, Node newParent) {
		AVLTree avl_out = (AVLTree) S_out.get(node);
		Node parTrav = getParent(node);

		// updates S_out of old ancestors
		while (parTrav != getRoot()) {
			AVLTree avlPar = (AVLTree) S_out.get(parTrav);
			Iterator avlIt = avl_out.getAVLIterator();
			while (avlIt.hasNext()) {
				OrderedNode n = (OrderedNode) avlIt.next();
				BigInteger mult = avl_out.getMultiplicity(n);
				for (BigInteger i = new BigInteger("0", 2); i.compareTo(mult) < 0; i = i
						.add(new BigInteger("1", 2))) {
					avlPar.remove(n, avl_out.getEdges(n));
				}
			}
			parTrav = getParent(parTrav);
		}

		// updates S_in of old ancestors
		AVLTree avl_in = (AVLTree) S_in.get(node);
		parTrav = getParent(node);
		while (parTrav != getRoot()) {
			AVLTree avlPar = (AVLTree) S_in.get(parTrav);
			Iterator avlIt = avl_in.getAVLIterator();
			while (avlIt.hasNext()) {
				OrderedNode n = (OrderedNode) avlIt.next();
				BigInteger mult = avl_in.getMultiplicity(n);
				for (BigInteger i = new BigInteger("0", 2); i.compareTo(mult) < 0; i = i
						.add(new BigInteger("1", 2))) {
					avlPar.remove(n, avl_in.getEdges(n));
				}
			}
			parTrav = getParent(parTrav);
		}

		// updates S_out of new ancestors
		parTrav = newParent;
		while (parTrav != getRoot()) {
			AVLTree avlPar = (AVLTree) S_out.get(parTrav);
			Iterator avlIt = avl_out.getAVLIterator();
			while (avlIt.hasNext()) {
				OrderedNode n = (OrderedNode) avlIt.next();
				BigInteger mult = avl_out.getMultiplicity(n);
				avlPar.insert(n, mult, avl_out.getEdges(n));
			}
			parTrav = getParent(parTrav);
		}

		// updates S_in of new ancestors
		parTrav = newParent;
		while (parTrav != getRoot()) {
			AVLTree avlPar = (AVLTree) S_in.get(parTrav);
			Iterator avlIt = avl_in.getAVLIterator();
			while (avlIt.hasNext()) {
				OrderedNode n = (OrderedNode) avlIt.next();
				BigInteger mult = avl_in.getMultiplicity(n);
				avlPar.insert(n, mult, avl_in.getEdges(n));
			}
			parTrav = getParent(parTrav);
		}
	}

	/**
	 * @see org.visnacom.model.CompoundGraph#splitGivenCluster(java.util.List,
	 *      org.visnacom.model.Node, org.visnacom.model.Node)
	 */
	public void splitGivenCluster(List l, Node par, Node clus) {

		// gets position in total ordering
		OrderedNode biggestNode = (OrderedNode) l.get(0);
		Iterator it = l.iterator();
		while (it.hasNext()) {
			OrderedNode nextNode = (OrderedNode) it.next();
			if (om.compare(nextNode, biggestNode) > 0) {
				biggestNode = nextNode;
			}
		}
		om.insert(biggestNode, (OrderedNode) clus);

		// sets min/max
		min.put(clus, clus);
		max.put(clus, clus);

		// sets S-sets
		AVLTree bst_out = new AVLTree(om);
		S_out.put(clus, bst_out);

		AVLTree bst_in = new AVLTree(om);
		S_in.put(clus, bst_in);

		super.splitGivenCluster(l, par, clus);
	}

	/**
	 * @see org.visnacom.model.CompoundGraph#mergeParentChildren(org.visnacom.model.Node)
	 */
	protected void mergeParentChildren(Node innerNode) {
		Iterator it;
		Node par = getParent(innerNode);
		OrderedNode pred = ((OrderedNode) innerNode).getPred();
		while (pred != null) {
			if (this.isAncestor(par, pred) && !isAncestor(innerNode, pred)) {
				break;
			}
			pred = pred.getPred();
		}

		it = getChildrenIterator(innerNode);
		// sets new parent and children relation
		while (it.hasNext()) {
			OrderedNode node = (OrderedNode) it.next();
			this.addChild(par, node, AT_GIVEN_POS, pred);
			pred = node;
		}
	}

	/**
	 * @see org.visnacom.model.BaseCompoundGraph#edgeReport(org.visnacom.model.Edge)
	 */
	public Collection edgeReport(Edge u) {
		List result = new LinkedList();
		Node source = u.getSource();
		Node target = u.getTarget();
		AVLTree s = (AVLTree) S_out.get(source);
		Node succ = (Node) s.succ((Node) min.get(target), false, false);
		while (succ != null
				&& om
						.compare((OrderedNode) succ, (OrderedNode) max
								.get(target)) <= 0) {
			result.addAll(s.getEdges(succ));
			succ = (Node) s.succConstantRuntime(succ);
		}
		return result;
	}
}