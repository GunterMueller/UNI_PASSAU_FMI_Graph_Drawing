/*
 * Created on 12.12.2004
 *
 */

package org.visnacom.model;

import java.util.*;
import java.math.BigInteger;

import org.w3c.dom.Document;
import org.w3c.dom.*;

/**
 * @author F. Pfeiffer
 * 
 * This abstract class describes a compound graph.
 */
public abstract class CompoundGraph {

	// Hashmaps relating children and parents
	private HashMap children, parent;

	// Lists containing all nodes, edges of cpg
	private DLL allEdges, allNodes;

	// adjacency lists
	private HashMap inAdj, outAdj;

	// depths of nodes
	private HashMap depth;

	// the root node
	private Node root;

	// counts node ids
	public int idCounter = 0;

	// constants used for maintaining order in children lists
	protected final int AT_BEGINNING = 0, AT_END = 1, AT_GIVEN_POS = 2;

	// used for saving graphs
	protected BigInteger idModifier = new BigInteger("0");

	private BigInteger edgeId = new BigInteger("0");

	protected HashMap modifiedIds = new HashMap();

	protected HashMap edgeIds = new HashMap();

	protected Element graph;

	/**
	 * Constructor.
	 *  
	 */
	protected CompoundGraph() {
		root = newNodeFac();
		makeCPG();
	}

	/**
	 * Constructor with given root.
	 * 
	 * @param r
	 *            The given root node.
	 */
	protected CompoundGraph(Node r) {
		root = r;
		makeCPG();
	}

	/**
	 * Makes a new graph.
	 *  
	 */
	private void makeCPG() {
		parent = new HashMap();
		children = new HashMap();
		children.put(root, new DLL());
		allEdges = new DLL();
		allNodes = new DLL();
		allNodes.add(root);
		inAdj = new HashMap();
		outAdj = new HashMap();
		inAdj.put(root, new DLL());
		outAdj.put(root, new DLL());
		depth = new HashMap();
		depth.put(root, new BigInteger("0", 2));
	}

	/**
	 * Used for copy constructor. Copies inclusion tree.
	 * 
	 * @param cpg
	 *            The graph to be copied.
	 * @param node
	 *            Root of subtree where to start copying of inclusion tree.
	 */
	protected void copyHierarchy(CompoundGraph cpg, Node node) {
		Iterator it = cpg.getChildrenRef(node).iterator();
		while (it.hasNext()) {
			Node child = (Node) it.next();
			newGivenNode(child, node);
			copyHierarchy(cpg, child);
		}
	}

	/**
	 * 
	 * Used for copy constructor. Copies adjacency edges.
	 * 
	 * @param cpg
	 *            The graph to be copied.
	 */
	protected void copyAdjEdges(CompoundGraph cpg) {
		Iterator it = cpg.allEdges.iterator();
		while (it.hasNext()) {
			Edge edg = (Edge) it.next();
			Node source = edg.getSource();
			Node target = edg.getTarget();
			Edge newEdge = newEdgeFac(source, target);

			((List) outAdj.get(source)).add(newEdge);
			((List) inAdj.get(target)).add(newEdge);

			allEdges.add(newEdge);
		}
	}

	/**
	 * Copy-contructor
	 * 
	 * @param cpg
	 *            The graph to be copied.
	 */
	public CompoundGraph(CompoundGraph cpg) {
		this(cpg.getRoot());
		copyHierarchy(cpg, root);
		copyAdjEdges(cpg);
	}

	/**
	 * Checks whether a node is an ancestor of another one.
	 * 
	 * @param anc
	 *            The potential ancestor.
	 * @param desc
	 *            The potential descendant.
	 * @return True if anc is ancestor of desc, false otherwise.
	 */
	public boolean isAncestor(Node anc, Node desc) {
		if (anc == desc) {
			return true;
		}
		Node par = null;
		if (!(desc == root)) {
			par = (Node) parent.get(desc);
		} else {
			return false;
		}

		if (par == anc) {
			return true;
		}
		return isAncestor(anc, par);
	}

	/**
	 * Returns all leaves of a subtree rooted at given node.
	 * 
	 * @param n
	 *            Root of subtree.
	 * @param l
	 *            List which is used for recursive method calling.
	 */
	public void getLeaves(Node n, List l) {
		// first gets all children
		List list = (List) getChildrenRef(n);
		if (!list.isEmpty()) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				// checks if it is a leaf
				if (!hasChildren(node)) {
					l.add(node);
					// recursive call for clusters
				} else {
					getLeaves(node, l);
				}

			}
		} else {
			l.add(n);
		}
	}

	/**
	 * Gets all descendants of given node which are clusters.
	 * 
	 * @param n
	 *            Root of subtree.
	 * @param l
	 *            List used for recursive method calling.
	 */
	private void getDescClusRec(Node n, List l) {
		// first gets all children
		List list = (List) getChildrenRef(n);
		if (!list.isEmpty()) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				// checks if it is a cluster
				if (hasChildren(node)) {
					l.add(node);
					getDescClusRec(node, l);
				}
			}
		}
	}

	/**
	 * Gets all descendants of a node.
	 * 
	 * @param n
	 *            The decendants of this node are looked for.
	 * @return List of all descendants.
	 */
	public List allDescendants(Node n) {
		LinkedList allDesc = new LinkedList();
		getAllDesc(n, allDesc);
		return allDesc;
	}

	/**
	 * Help method for allDescendants. Used for recursve calling.
	 * 
	 * @param n
	 *            Root of subtree.
	 * @param l
	 *            List of descendants.
	 */
	private void getAllDesc(Node n, List l) {
		// first gets all children
		List list = (List) getChildrenRef(n);
		if (!list.isEmpty()) {
			Iterator it = list.iterator();
			while (it.hasNext()) {
				Node node = (Node) it.next();
				l.add(node);
				getAllDesc(node, l);
			}
		}
	}

	/**
	 * Returns those children of a given node which are leaves.
	 * 
	 * @param node
	 *            Node for the children of which one is looking.
	 * @return Children which are leaves.
	 */
	public List getChildLeaves(Node node) {
		LinkedList list = new LinkedList();
		List l = (List) getChildrenRef(node);
		Iterator it = l.iterator();
		// only returns leaves
		while (it.hasNext()) {
			Node n = (Node) it.next();
			if (!hasChildren(n)) {
				list.add(n);
			}
		}
		return list;
	}

	/**
	 * Checks whether a node has any children.
	 * 
	 * @param node
	 *            The node of which one wants to know whether it has children.
	 * @return True if node has children, false otherwise.
	 */
	public boolean hasChildren(Node node) {
		return !(getChildrenRef(node).isEmpty());
	}

	/**
	 * Gets the adjacency edges of a node.
	 * 
	 * @param node
	 *            The node of which the adjacency edges are wanted.
	 * @return List of incident edges.
	 */
	public List getAdjEdges(Node node) {
		List list = new LinkedList();
		list.addAll((List) outAdj.get(node));
		list.addAll((List) inAdj.get(node));
		return list;
	}

	/**
	 * Gets the child nodes of a given parent node.
	 * 
	 * @param par
	 *            The parent node.
	 * @return A collection of child nodes.
	 */
	public List getChildren(Node par) {
		return new DLL((DLL) children.get(par));
	}

	/**
	 * Gets the children list.
	 * 
	 * @param par
	 *            The parent node.
	 * @return The real children collection.
	 */
	private List getChildrenRef(Node par) {
		return (List) children.get(par);
	}

	/**
	 * Gets an iterator for the children list.
	 * 
	 * @param par
	 *            The parent node.
	 * @return Read-only iterator for children.
	 */
	public Iterator getChildrenIterator(Node par) {
		return new ReadOnlyIterator(((List) children.get(par)).iterator());
	}

	/**
	 * Gets the parent node of a given child node.
	 * 
	 * @param child
	 *            The child node.
	 * @return The corresponding parent node.
	 */
	public Node getParent(Node child) {
		Node node = (Node) parent.get(child);
		return node;
	}

	/**
	 * Calculates the height of an inclusion subtree rooted at given node.
	 * 
	 * @param n
	 *            Root of subtree.
	 * @return The height of an inclusion subtree.
	 */
	public BigInteger inclusionHeight(Node n) {
		// standard implementation of height calculation
		// uses depth
		BigInteger h = new BigInteger("0", 2);
		LinkedList l = new LinkedList();
		getLeaves(n, l);
		Iterator it = l.iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			h = h.max(inclusionDepth(node));
		}
		return (h.subtract(inclusionDepth(n)));
	}

	/**
	 * Calculates the depth of a node regarding the inclusion tree.
	 * 
	 * @param n
	 *            The node of which the depth is to be calculated.
	 * @return Said depth.
	 */
	public BigInteger inclusionDepth(Node n) {
		return (BigInteger) depth.get(n);
	}

	/**
	 * Gets the root node.
	 * 
	 * @return The root node.
	 */
	public Node getRoot() {
		return root;
	}

	/**
	 * Returns a list containing all edges.
	 * 
	 * @return List of all edges.
	 */
	public List getAllEdges() {
		return new DLL(allEdges);
	}

	/**
	 * Gets an iterator for the edges list.
	 * 
	 * @return Read-only iterator for edges.
	 */
	public Iterator getAllEdgesIterator() {
		return new ReadOnlyIterator(allEdges.iterator());
	}

	/**
	 * Returns a list of all nodes.
	 * 
	 * @return List of all nodes.
	 */
	public List getAllNodes() {
		return new DLL(allNodes);
	}

	/**
	 * Gets an iterator for the nodes list.
	 * 
	 * @return Read-only iterator for nodes.
	 */
	public Iterator getAllNodesIterator() {
		return new ReadOnlyIterator(allNodes.iterator());
	}

	/**
	 * Reverses the direction of an edge.
	 * 
	 * @param edge
	 *            The edge which is to be reversed.
	 */
	public void reverseEdge(Edge edge) {
		Node target = edge.getTarget();
		Node source = edge.getSource();
		changeTarget(edge, source);
		changeSource(edge, target);
	}

	/**
	 * Node factory.
	 * 
	 * @return Node instance.
	 */
	protected Node newNodeFac() {
		return new Node(idCounter++);
	}

	/**
	 * Edge factory.
	 * 
	 * @param source
	 *            The source of the new edge.
	 * @param target
	 *            The target of the new edge.
	 * @return Edge instance.
	 */
	protected Edge newEdgeFac(Node source, Node target) {
		return new Edge(source, target);
	}

	/**
	 * Returns in-edges of given node.
	 * 
	 * @param n
	 *            Said node.
	 * @return In-edges.
	 */
	public List getInEdges(Node n) {
		return new DLL((DLL) inAdj.get(n));
	}

	/**
	 * Gets an iterator for the in-edges list.
	 * 
	 * @param n
	 *            The node for which one wants the iterator.
	 * @return Read-only iterator for in-edges.
	 */
	public Iterator getInEdgesIterator(Node n) {
		return new ReadOnlyIterator(((List) inAdj.get(n)).iterator());
	}

	/**
	 * Returns out-edges of given node.
	 * 
	 * @param n
	 *            Said node.
	 * @return Out-edges.
	 */
	public List getOutEdges(Node n) {
		return new DLL((DLL) outAdj.get(n));
	}

	/**
	 * Gets an iterator for the out-edges list.
	 * 
	 * @param n
	 *            The node for which one wants the iterator.
	 * @return Read-only iterator for out-edges.
	 */
	public Iterator getOutEdgesIterator(Node n) {
		return new ReadOnlyIterator(((List) outAdj.get(n)).iterator());
	}

	/**
	 * Changes source of edge.
	 * 
	 * @param e
	 *            The given edge.
	 * @param newSource
	 *            The new source.
	 */
	public void changeSource(Edge e, Node newSource) {
		((List) outAdj.get(e.getSource())).remove(e);
		((List) outAdj.get(newSource)).add(e);

		e.setSource(newSource);
	}

	/**
	 * Changes target of edge.
	 * 
	 * @param e
	 *            The given edge.
	 * @param newTarget
	 *            The new target.
	 */
	public void changeTarget(Edge e, Node newTarget) {

		((List) inAdj.get(e.getTarget())).remove(e);
		((List) inAdj.get(newTarget)).add(e);

		e.setTarget(newTarget);
	}

	/**
	 * Inserts a given node into graph.
	 * 
	 * @param newNode
	 *            The node to be inserted.
	 * @param par
	 *            The parent of new node.
	 */
	protected void newGivenNode(Node newNode, Node par) {
		assert checkConsistency();

		inAdj.put(newNode, new DLL());
		outAdj.put(newNode, new DLL());

		// makes leaf a child of given parent node
		if (!children.containsKey(par)) {
			throw new InvalidChildException("No correct children list!");
		}
		this.addChild(par, newNode, AT_END, null);

		// new node has no children so far
		children.put(newNode, new DLL());
		allNodes.add(newNode);

		// sets depth of new node
		BigInteger depthPar = (BigInteger) depth.get(par);
		if (depthPar == null) {
			depth.put(newNode, new BigInteger("0", 2));
		} else {
			depth.put(newNode, depthPar.add(new BigInteger("1", 2)));
		}
		assert checkConsistency();
	}

	/**
	 * Adds a new node (leaf).
	 * 
	 * @param par
	 *            The parent node of the new leaf.
	 * @return The new leaf.
	 */
	public Node newLeaf(Node par) {
		return newLeafSupressDynBinding(par);
	}

	/**
	 * Additional method which is used for supressing dynamic binding in View.
	 * 
	 * @param par
	 *            Parent of the new node.
	 * @return The new leaf.
	 */
	protected Node newLeafSupressDynBinding(Node par) {
		Node newNode = newNodeFac();
		newGivenNode(newNode, par);
		return newNode;
	}

	/**
	 * Makes given edge a new edge in graph.
	 * 
	 * @param newEdge
	 *            The given edge.
	 */
	protected void newGivenEdge(Edge newEdge) {
		// puts edge into adjacency list
		assert checkConsistency();
		((List) (outAdj.get(newEdge.getSource()))).add(newEdge);
		((List) (inAdj.get(newEdge.getTarget()))).add(newEdge);

		allEdges.add(newEdge);
		assert checkConsistency();
	}

	/**
	 * Additional method which is used for supressing dynamic binding in View.
	 * 
	 * @param source
	 *            Source node of edge.
	 * @param target
	 *            Target node of edge.
	 * @return The new edge.
	 */
	protected Edge newEdgeSupressDynBinding(Node source, Node target) {
		if (source == null) {
			throw new InvalidEdgeException("Source must not be null");
		}
		if (target == null) {
			throw new InvalidEdgeException("Target must not be null");
		}
		if (source == target) {
			throw new InvalidEdgeException("Source equals target");
		} else if (this.isAncestor(source, target)) {
			throw new InvalidEdgeException("Connects ancestor and descendant");
		} else if (this.isAncestor(target, source)) {
			throw new InvalidEdgeException("Connects ancestor and descendant");
		}

		Edge newEdge = newEdgeFac(source, target);
		newGivenEdge(newEdge);

		return newEdge;
	}

	/**
	 * Adds a new edge.
	 * 
	 * @param source
	 *            The source node.
	 * @param target
	 *            The target node.
	 * @return The new edge.
	 */
	public Edge newEdge(Node source, Node target) {
		return newEdgeSupressDynBinding(source, target);
	}

	/**
	 * Deletes edge.
	 * 
	 * @param edge
	 *            The edge which is to be deleted.
	 */
	public void deleteEdge(Edge edge) {
		this.deleteEdgeSupressDynBinding(edge);
	}

	/**
	 * Additional method which is used for supressing dynamic binding in View.
	 * 
	 * @param edge
	 *            The edge to be deleted.
	 */
	protected void deleteEdgeSupressDynBinding(Edge edge) {
		assert checkConsistency();
		((List) outAdj.get(edge.getSource())).remove(edge);
		((List) inAdj.get(edge.getTarget())).remove(edge);

		allEdges.remove(edge);
		assert checkConsistency();
	}

	/**
	 * Deletes a leaf.
	 * 
	 * @param leaf
	 *            The leaf to be deleted.
	 */
	public void deleteLeaf(Node leaf) {
		deleteLeafSupressDynBinding(leaf);
	}

	/**
	 * Additional method which is used for supressing dynamic binding in View.
	 * (So that one can use deleteLeaf for deletion in View.) Call
	 * super.deleteLeafSupressDynBinding in subclasses.
	 * 
	 * @param leaf
	 *            The leaf to be deleted.
	 */
	protected void deleteLeafSupressDynBinding(Node leaf) {
		assert checkConsistency();
		if (leaf == root) {
			throw new InvalidLeafException("Cannot delete root");
		}

		if (!getChildrenRef(leaf).isEmpty()) {
			throw new InvalidLeafException("Given node is not a leaf");
		}

		children.remove(leaf);

		List workAdj = (List) this.getAdjEdges(leaf);
		Iterator it = workAdj.iterator();
		while (it.hasNext()) {
			deleteEdgeSupressDynBinding((Edge) it.next());
		}

		inAdj.remove(leaf);
		outAdj.remove(leaf);

		// removes leaf from inclusion graph
		Node par = this.getParent(leaf);
		((List) children.get(par)).remove(leaf);
		parent.remove(leaf);

		allNodes.remove(leaf);

		depth.remove(leaf);

		assert checkConsistency();
	}

	/**
	 * Performs split with given cluster node.
	 * 
	 * @param l
	 *            The children of new cluster.
	 * @param par
	 *            The parent of new cluster.
	 * @param clus
	 *            The cluster node.
	 */
	protected void splitGivenCluster(List l, Node par, Node clus) {
		assert checkConsistency();
		newGivenNode(clus, par);

		Iterator it = new LinkedList(l).iterator();
		while (it.hasNext()) {
			moveNodeSupressDynBinding((Node) it.next(), clus, true);
		}
		assert checkConsistency();
	}

	/**
	 * Makes a new cluster.
	 * 
	 * @param l
	 *            The nodes one wants to make a cluster of.
	 * @return The node representing the cluster.
	 */
	public Node split(List l) {

		Node par = null;
		Iterator it = l.iterator();
		if (!l.isEmpty()) {
			par = (Node) parent.get((Node) it.next());
		} else {
			throw new EmptyClusterException("Cannot build empty cluster");
		}

		Node node = newNodeFac();

		depth.put(node, (BigInteger) depth.get((Node) l.get(0)));

		// first checks whether the selected nodes have a common parent

		it = l.iterator();
		while (it.hasNext()) {
			Node n = (Node) parent.get((Node) it.next());
			if (n != par) {
				throw new NoCommonParentException("No common parent");
			}
		}

		this.splitGivenCluster(l, par, node);

		return node;

	}

	/**
	 * Recalculates depths in an entire subtree.
	 * 
	 * @param n
	 *            Root of subtree.
	 */
	private void changeDepth(Node n) {
		List l = getChildrenRef(n);

		depth.put(n, ((BigInteger) depth.get(getParent(n))).add(new BigInteger(
				"1", 2)));
		Iterator it = l.iterator();
		while (it.hasNext()) {
			Node node = (Node) it.next();
			changeDepth(node);
		}
	}

	/**
	 * Merges a cluster with the rest of the graph. Therefore removes cluster.
	 * 
	 * @param innerNode
	 *            The cluster node.
	 */
	public void merge(Node innerNode) {
		assert checkConsistency();
		Iterator it;

		// deletes incident edges of cluster to be removed
		List adj = getAdjEdges(innerNode);
		it = adj.iterator();
		while (it.hasNext()) {
			deleteEdgeSupressDynBinding((Edge) it.next());
		}

		Node par = getParent(innerNode);
		List desc = (List) getChildrenRef(innerNode);

		this.mergeParentChildren(innerNode);

		LinkedList removed = new LinkedList();
		removed.add(innerNode);
		this.removeChildren(removed, par);
		children.remove(innerNode);
		parent.remove(innerNode);
		allNodes.remove(innerNode);

		// recalculates depths
		it = desc.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			depth.put(n, (BigInteger) depth.get(innerNode));
			changeDepth(n);
		}
		depth.remove(innerNode);
		inAdj.remove(innerNode);
		outAdj.remove(innerNode);
		assert checkConsistency();
	}

	/**
	 * Sets up a new parent/children realtion.
	 * 
	 * @param child
	 *            The child.
	 * @param par
	 *            The parent.
	 * @param pred
	 *            The predecessor of the new child in children list.
	 * @param last
	 *            If this parameter is true, node is placed as last children.
	 */
	protected void setParentChildren(Node child, Node par, Node pred,
			boolean last) {
		DLL c = (DLL) children.get(par);
		if (!last) {
			c.addSucc(pred, child);
		} else {
			c.addSucc(c.getLast(), child);
		}
		parent.put(child, par);
	}

	/**
	 * Removes a parent/children relation
	 * 
	 * @param child
	 *            The child to be removed.
	 */
	protected void delParentChild(Node child) {
		Node par = (Node) parent.get(child);
		parent.remove(child);
		List c = (List) children.get(par);
		c.remove(child);
	}

	/**
	 * Help method for merge.
	 * 
	 * @param innerNode
	 *            The cluster to be merged.
	 */
	protected void mergeParentChildren(Node innerNode) {
		Iterator it;
		Node par = getParent(innerNode);
		it = getChildrenIterator(innerNode);
		// sets new parent and children relation
		while (it.hasNext()) {
			Node node = (Node) it.next();
			this.addChild(par, node, AT_GIVEN_POS, null);
		}
	}

	/**
	 * Moves a node into a new subtree.
	 * 
	 * @param node
	 *            The node to be moved.
	 * @param newParent
	 *            The new parent of the given node.
	 */
	public void moveNode(Node node, Node newParent) {
		moveNodeSupressDynBinding(node, newParent, false);
	}

	/**
	 * Supresses dynamic binding in view.
	 * 
	 * @param node
	 *            The node to be moved.
	 * @param newParent
	 *            The new parent of node.
	 * @param splitMovement
	 *            True iff movement is due to splitting.
	 */
	protected void moveNodeSupressDynBinding(Node node, Node newParent,
			boolean splitMovement) {
		assert checkConsistency();
		Node oldPar = (Node) parent.get(node);
		List oldParChildren = (List) children.get(oldPar);
		oldParChildren.remove(node);
		this.addChild(newParent, node, AT_BEGINNING, null);
		changeDepth(node);
		assert checkConsistency();
	}

	/**
	 * Adds a child to children list.
	 * 
	 * @param par
	 *            The parent.
	 * @param child
	 *            The child node.
	 * @param mode
	 *            Adding mode.
	 * @param pred
	 *            Predecessor of new child in children list.
	 */
	protected void addChild(Node par, Node child, int mode, Node pred) {
		List childList = (List) children.get(par);
		childList.add(child);
		parent.put(child, par);
	}

	/**
	 * Removes children from given parent.
	 * 
	 * @param removed
	 *            A list of children which are to be removed.
	 * @param par
	 *            The parent of the soon to be removed children.
	 *  
	 */
	private void removeChildren(LinkedList removed, Node par) {
		List childr = (List) getChildrenRef(par);
		Iterator it = removed.iterator();
		while (it.hasNext() && childr != null) {
			Node n = (Node) it.next();
			childr.remove(n);
		}
	}

	/**
	 * Gets all edges with specified source and target.
	 * 
	 * @param source
	 *            The source node.
	 * @param target
	 *            The target node.
	 * @return All edes with given end nodes. (List as multiple edges are needed
	 *         for sugiyama.)
	 */
	public List getEdge(Node source, Node target) {
		List result = new DLL();
		if (!containsNode(source) || !containsNode(target)) {
			return result;
		}
		Iterator it = getOutEdgesIterator(source);
		while (it.hasNext()) {
			Edge edge = (Edge) it.next();
			if (edge.getTarget() == target) {
				result.add(edge);
			}
		}
		return result;
	}

	/**
	 * Returns all information about a subtree, i.e edges and child relations.
	 * 
	 * @param rootSubtree
	 * @return List with information about subtree.
	 */
	public List getSubtree(Node rootSubtree) {
		List l = new LinkedList();
		// info about edges
		HashMap edges = new HashMap();
		// info about child/parent relations
		HashMap clusAndChildren = new HashMap();
		LinkedList leaves = new LinkedList();
		this.getLeaves(rootSubtree, leaves);
		LinkedList descClus = new LinkedList();
		this.getDescClusRec(rootSubtree, descClus);

		// gets edges and parent realtions
		Iterator it = descClus.iterator();
		while (it.hasNext()) {
			Node clus = (Node) it.next();
			List childrenOfClus = (List) ((List) this.getChildren(clus));
			clusAndChildren.put(clus, childrenOfClus);
			edges.put(clus, this.getAdjEdges(clus));
		}

		clusAndChildren.put(rootSubtree, (List) ((List) this
				.getChildren(rootSubtree)));

		// gets all descendants
		LinkedList allDesc = new LinkedList();
		allDesc.addAll(leaves);
		allDesc.addAll(descClus);
		it = allDesc.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			edges.put(n, this.getAdjEdges(n));
		}

		edges.put(rootSubtree, this.getAdjEdges(rootSubtree));
		allDesc.add(rootSubtree);
		l.add(clusAndChildren);
		l.add(leaves);
		l.add(edges);
		l.add(allDesc);
		return l;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String result = this.getClass().toString() + "\n";

		for (Iterator it = getAllNodes().iterator(); it.hasNext();) {
			result += constructString((Node) it.next());
		}

		return result;
	}

	/**
	 * Constructs a string description of a node.
	 * 
	 * @param n
	 *            The node to be described.
	 * @return The string description.
	 */
	protected String constructString(Node n) {
		String result = n.toString();
		if (getParent(n) != null) {
			result += " par: " + getParent(n).getId();
		}

		result += " adj: ";
		for (Iterator it = getOutEdges(n).iterator(); it.hasNext();) {
			result += it.next() + " ";
		}

		result += "\n";
		return result;
	}

	/**
	 * Deletes an entire subtree.
	 * 
	 * @param rootSubtree
	 *            Root of subtree to be deleted.
	 */
	public void delSubTree(Node rootSubtree) {
		delSubTreeWithoutRoot(rootSubtree);
		deleteLeafSupressDynBinding(rootSubtree);
	}

	/**
	 * Deletes a subtree (without given root).
	 * 
	 * @param rootSubtree
	 *            The root of the subtree.
	 */
	protected void delSubTreeWithoutRoot(Node rootSubtree) {
		List child = getChildren(rootSubtree);
		Iterator it = child.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			delSubTreeWithoutRoot(n);
			deleteLeafSupressDynBinding(n);
		}
	}

	/**
	 * Returns the nearest common ancestor of two nodes.
	 * 
	 * @param n1
	 *            The first node.
	 * @param n2
	 *            The second node.
	 * @return The nearest common ancestor of n1 and n2.
	 */
	public Node nearestCommonAncestor(Node n1, Node n2) {
		if (n1 == getRoot() || n2 == getRoot()) {
			return getRoot();
		}
		BigInteger level1 = this.inclusionDepth(n1);
		BigInteger level2 = this.inclusionDepth(n2);

		while (level1.compareTo(level2) > 0) {
			n1 = this.getParent(n1);
			level1 = level1.subtract(new BigInteger("1", 2));
		}

		while (level2.compareTo(level1) > 0) {
			n2 = this.getParent(n2);
			level2 = level2.subtract(new BigInteger("1", 2));
		}

		return this.getCommonPar(n1, n2);
	}

	/**
	 * Gets the common parent of two nodes.
	 * 
	 * @param n1
	 *            The first node.
	 * @param n2
	 *            The second node.
	 * @return The common parent of both fiven nodes.
	 */
	private Node getCommonPar(Node n1, Node n2) {
		if (n1 == n2) {
			return n1;
		}
		return this.getCommonPar(this.getParent(n1), this.getParent(n2));
	}

	/**
	 * Checks if graph contains a given node.
	 * 
	 * @param n
	 *            The node to be checked for containment.
	 * @return True iff node is contained.
	 */
	public boolean containsNode(Node n) {
		List adj = (List) outAdj.get(n);
		return (adj != null);
	}

	/**
	 * Checks if graph contains all given node.
	 * 
	 * @param nodes
	 *            The nodes to be checked for containment.
	 * @return True iff all nodes are contained.
	 */
	public boolean containsAllNodes(Collection nodes) {
		return allNodes.containsAll(nodes);
	}

	/**
	 * Checks if given parent has a given child.
	 * 
	 * @param par
	 *            The parent of the child node.
	 * @param child
	 *            The child node.
	 * @return True iff par has given child node.
	 */
	public boolean hasGivenChild(Node par, Node child) {
		List l = (List) children.get(par);
		return l.contains(child);
	}

	/**
	 * Checks if graph contains a given edge.
	 * 
	 * @param edge
	 *            The edge to be checked for containment.
	 * @return True iff edge is contained.
	 */
	public boolean containsEdge(Edge edge) {
		return allEdges.contains(edge);
	}

	/**
	 * 
	 * Read-only iterator.
	 */
	private static class ReadOnlyIterator implements Iterator {

		// actual iterator
		private Iterator it;

		/**
		 * Constructor.
		 * 
		 * @param it
		 *            Iterator.
		 */
		public ReadOnlyIterator(Iterator it) {
			this.it = it;
		}

		/**
		 * @see java.util.Iterator#hasNext()
		 */
		public boolean hasNext() {
			return it.hasNext();
		}

		/**
		 * @see java.util.Iterator#next()
		 */
		public Object next() {
			return it.next();
		}

		/**
		 * @see java.util.Iterator#remove()
		 */
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * Gets the number of nodes in this graph.
	 * 
	 * @return Number of nodes in graph.
	 */
	public int getNumOfNodes() {
		return allNodes.size();
	}

	/**
	 * Gets the number of nodes in this graph.
	 * 
	 * @return Number of nodes in graph.
	 */
	public BigInteger bigNumOfNodes() {
		return allNodes.bigSize();
	}

	/**
	 * Gets the number of children of a given node.
	 * 
	 * @param par
	 *            Parent node of children to be counted.
	 * @return The number of children.
	 */
	public BigInteger bigNumOfChildren(Node par) {
		return ((DLL) children.get(par)).bigSize();
	}

	/**
	 * Saves a graph in XML format.
	 * 
	 * @param doc
	 *            XML document to contain graph description.
	 * @param directed
	 *            True iff edges are directed.
	 * @return Boolean flag signalling whether observers could be saved.
	 */
	public boolean save(Document doc, boolean directed) {
		idModifier = new BigInteger("0");

		edgeId = new BigInteger("0");

		graph.setAttribute("id", "G");
		if (directed) {
			graph.setAttribute("edgedefault", "directed");
		} else {
			graph.setAttribute("edgedefault", "undirected");
		}

		Iterator it = getChildrenIterator(getRoot());
		Element nodeItem = null;

		// iterates the children of root
		while (it.hasNext()) {
			org.visnacom.model.Node node = (org.visnacom.model.Node) it.next();
			nodeItem = this.addNode(node, doc);

			// if child is cluster, recursive method
			if (hasChildren(node)) {
				this.addCluster(node, doc, nodeItem);
			}
			graph.appendChild(nodeItem);
		}

		edgeIds = new HashMap();
		// adds edges to document
		addEdges(doc, graph);
		return true;
	}

	/**
	 * Adds edges to GraphML document.
	 * 
	 * @param doc
	 *            The document.
	 * @param docRoot
	 *            The part of the document where the edges should be appended.
	 */
	private void addEdges(Document doc, Element docRoot) {
		Iterator it = getAllEdgesIterator();
		while (it.hasNext()) {
			Edge edg = (Edge) it.next();
			org.visnacom.model.Node source = edg.getSource();
			org.visnacom.model.Node target = edg.getTarget();
			Element e = doc.createElement("edge");
			e.setAttribute("id", edgeId.toString());
			edgeIds.put(edg, new String(edgeId.toString()));
			edgeId = edgeId.add(new BigInteger("1"));
			e.setAttribute("source", (String) modifiedIds.get(source));
			e.setAttribute("target", (String) modifiedIds.get(target));
			docRoot.appendChild(e);
		}

	}

	/**
	 * Creates node specification in GraphML.
	 * 
	 * @param node
	 *            The node of the compound graph which is to be written in
	 *            GraphML.
	 * @param doc
	 *            The document.
	 * @return The element to be added to document.
	 */
	private Element addNode(org.visnacom.model.Node node, Document doc) {

		Element nodeItem = doc.createElement("node");

		String modifiedNodeId = idModifier.toString();
		modifiedIds.put(node, modifiedNodeId);
		idModifier = idModifier.add(new BigInteger("1"));

		nodeItem.setAttribute("id", modifiedNodeId);
		return nodeItem;
	}

	/**
	 * Recursive method for adding a cluster to document.
	 * 
	 * @param r
	 *            The cluster node.
	 * @param doc
	 *            The document.
	 * @param el
	 *            The document element to which the cluster is to be added.
	 */
	private void addCluster(org.visnacom.model.Node r, Document doc, Element el) {
		Element clusGraph = doc.createElement("graph");
		Element nodeItem = null;
		Iterator it = getChildrenIterator(r);
		while (it.hasNext()) {
			org.visnacom.model.Node node = (org.visnacom.model.Node) it.next();
			// makes GraphML specification of node data
			nodeItem = this.addNode(node, doc);
			clusGraph.appendChild(nodeItem);

			// recursive method calling
			if (hasChildren(node)) {
				this.addCluster(node, doc, nodeItem);
			}
		}
		el.appendChild(clusGraph);
	}

	/**
	 * Testing purpose only.
	 * @return True if test is passed.
	 *  
	 */
	protected boolean checkConsistency() {
		assert (allNodes.size() == inAdj.size());
		assert (allNodes.size() == outAdj.size());
		// for all nodes adj entries exist
		Iterator it = allNodes.iterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			assert (inAdj.containsKey(n));
			assert (outAdj.containsKey(n));
		}
		// all edges have entry in adj lists
		it = allEdges.iterator();
		while (it.hasNext()) {
			Edge edg = (Edge) it.next();
			Node source = edg.getSource();
			Node target = edg.getTarget();
			List adjOut = (List) outAdj.get(source);
			List adjIn = (List) inAdj.get(target);
			assert (adjOut != null);
			assert (adjIn != null);
			assert (allNodes.contains(source));
			assert (allNodes.contains(target));
			assert (adjOut.contains(edg));
			assert (adjIn.contains(edg));
		}
		// children/parents are consistent
		it = children.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Node par = (Node) entry.getKey();
			List c = (List) entry.getValue();
			Iterator it2 = c.iterator();
			while (it2.hasNext()) {
				Node child = (Node) it2.next();
				assert (getParent(child) == par);
			}
		}
		return true;
	}

	/**
	 * Gets mean degree.
	 * 
	 * @return Mean degree.
	 */
	public double getMeanDegree() {
		long numInnerNodes = 0;
		for (Iterator it = allNodes.iterator(); it.hasNext();) {
			if (hasChildren((Node) it.next())) {
				numInnerNodes++;
			}
		}
		return (double) allNodes.size() / (double) numInnerNodes;
	}

	/**
	 * Gets mean complexity.
	 * 
	 * @return Mean complexity.
	 */
	public double getMeanComplexity() {
		BigInteger sum = new BigInteger("0");
		for (Iterator it = getAllEdgesIterator(); it.hasNext();) {
			Edge e = (Edge) it.next();
			Node nca = nearestCommonAncestor(e.getSource(), e.getTarget());
			BigInteger temp = inclusionDepth(e.getSource()).subtract(
					inclusionDepth(nca));
			BigInteger temp2 = inclusionDepth(e.getTarget()).subtract(
					inclusionDepth(nca));
			sum = sum.add(temp.add(temp2));
		}
		return sum.doubleValue() / getNumOfEdges();
	}

	/**
	 * Gets the total number of edges in graph.
	 * 
	 * @return The number of edges.
	 */
	public int getNumOfEdges() {
		return allEdges.size();
	}
}