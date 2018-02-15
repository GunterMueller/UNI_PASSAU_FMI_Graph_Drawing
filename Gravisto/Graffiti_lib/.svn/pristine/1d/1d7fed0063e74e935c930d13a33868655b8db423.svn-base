/*
 * Created on 12.12.2004
 *
 */

package org.visnacom.model;

import java.util.*;

import org.w3c.dom.*;

import org.w3c.dom.Document;

/**
 * @author F. Pfeiffer
 * 
 * This abstract class describes a compound graph, which is part of an observer
 * pattern.
 */
public abstract class ObservableCompoundGraph extends CompoundGraph {

	// list of observers
	private LinkedList Observers;

	private int observerCount = 0;

	/**
	 * Standard constructor.
	 *  
	 */
	protected ObservableCompoundGraph() {
		super();
		Observers = new LinkedList();
	}

	/**
	 * Copy constructor. Also copies observer list.
	 * 
	 * @param ocpg
	 *            The graph to be copied.
	 */
	protected ObservableCompoundGraph(ObservableCompoundGraph ocpg) {
		super(ocpg);
		this.Observers = (LinkedList) ocpg.Observers.clone();
	}

	/**
	 * Constructor with given root node.
	 * 
	 * @param root
	 *            Given root node.
	 * @see org.visnacom.model.CompoundGraph#CompoundGraph(org.visnacom.model.Node)
	 */
	protected ObservableCompoundGraph(Node root) {
		super(root);
		Observers = new LinkedList();
	}

	/**
	 * Attaches new observer.
	 * 
	 * @param observer
	 *            The new observer.
	 */
	public void attach(Observer observer) {
		Observers.add(observer);
		observerCount++;
	}

	/**
	 * Removes an observer.
	 * 
	 * @param observer
	 *            The observer to be removed.
	 */
	public void detach(Observer observer) {
		Observers.remove(observer);
	}

	/**
	 * Notifies all observer of changes.
	 * 
	 * @param action
	 *            The kind of change which was performed.
	 */
	protected void notify(Action action) {
		Iterator it = Observers.iterator();
		while (it.hasNext()) {
			Observer o = (Observer) it.next();
			o.update(action);
		}
	}

	/**
	 * Adds a new leaf. Makes notification.
	 * 
	 * @param par
	 *            The parent node of the new leaf.
	 * @return The new leaf.
	 * @see org.visnacom.model.CompoundGraph#newLeafSupressDynBinding(org.visnacom.model.Node)
	 */
	protected Node newLeafSupressDynBinding(Node par) {
		Node newNode = super.newLeafSupressDynBinding(par);
		notify(new ActionCreate(newNode));
		return newNode;
	}

	/**
	 * Adds a new edge. Makes notification.
	 * 
	 * @param source
	 *            The source node.
	 * @param target
	 *            The target node.
	 * @return The new edge.
	 * @see org.visnacom.model.CompoundGraph#newEdgeSupressDynBinding(org.visnacom.model.Node,
	 *      org.visnacom.model.Node)
	 */
	protected Edge newEdgeSupressDynBinding(Node source, Node target) {
		Edge newEdge = super.newEdgeSupressDynBinding(source, target);
		notify(new ActionCreate(newEdge));
		return newEdge;
	}

	/**
	 * Deletes edge. Makes notification.
	 * 
	 * @param edge
	 *            The edge which is to be deleted.
	 * @see org.visnacom.model.CompoundGraph#deleteEdgeSupressDynBinding(org.visnacom.model.Edge)
	 */
	protected void deleteEdgeSupressDynBinding(Edge edge) {
		super.deleteEdgeSupressDynBinding(edge);
		notify(new ActionDelete(edge));
	}

	/**
	 * Deletes a leaf. Makes notification.
	 * 
	 * @param leaf
	 *            The leaf to be deleted.
	 * @see org.visnacom.model.CompoundGraph#deleteLeafSupressDynBinding(org.visnacom.model.Node)
	 */
	protected void deleteLeafSupressDynBinding(Node leaf) {
		super.deleteLeafSupressDynBinding(leaf);
		notify(new ActionDelete(leaf));
	}

	/**
	 * Makes a new cluster. Makes notification.
	 * 
	 * @param c
	 *            The nodes one wants to make a cluster of.
	 * @return The node representing the cluster.
	 * @see org.visnacom.model.CompoundGraph#split(java.util.List)
	 */
	public Node split(List c) {
		Node node = super.split(c);
		notify(new ActionInclude(node));
		return node;
	}

	/**
	 * Merges a cluster with the rest of the graph. Therefore removes cluster.
	 * Makes notification.
	 * 
	 * @param innerNode
	 *            The cluster node.
	 * @see org.visnacom.model.CompoundGraph#merge(org.visnacom.model.Node)
	 */
	public void merge(Node innerNode) {
		//notify befor actual merge, so that inner node is still there
		// for update if it is contracted in view and needs to be expanded
		// before merging
		notify(new ActionDelete(innerNode));
		super.merge(innerNode);
	}


	/**
	 * Moves node. Makes notification.
	 * 
	 * @see org.visnacom.model.CompoundGraph#moveNodeSupressDynBinding(org.visnacom.model.Node,
	 *      org.visnacom.model.Node, boolean)
	 */
	protected void moveNodeSupressDynBinding(Node node, Node newParent,
			boolean splitMovement) {
		Node oldPar = getParent(node);
		super.moveNodeSupressDynBinding(node, newParent, splitMovement);
		notify(new ActionMove(node, newParent, oldPar, splitMovement));
	}

	/**
	 * Saves a graph in XML format.
	 * 
	 * @param doc
	 *            XML document to contain graph description.
	 * @param directed
	 *            True iff edges are directed.
	 * @return True if observers could be saved.
	 */
	public boolean save(Document doc, boolean directed) {
		Element XMLRoot = doc.createElement("graphml");
		XMLRoot.setAttribute("xmlns", "http://graphml.graphdrawing.org/xmlns");
		graph = doc.createElement("graph");
		XMLRoot.appendChild(graph);
		doc.appendChild(XMLRoot);
		super.save(doc, directed);
		Iterator views = Observers.iterator();
		while (views.hasNext()) {
			Object next = views.next();
			if (next instanceof View) {
				View v = (View) next;
				boolean b = v.saveView(doc, graph, modifiedIds, edgeIds);
				if(!b) {
					return false;
				}
			}
		}
		return true;
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
			Iterator adj = getAdjEdges(n).iterator();
			while (adj.hasNext()) {
				Edge e = (Edge) adj.next();
				notify(new ActionDelete(e, true));
			}
			deleteLeafSupressDynBinding(n);
		}
	}

	/**
	 * Gets list with observers.
	 * 
	 * @return List with observers.
	 */
	protected List getObservers() {
		return new LinkedList(Observers);
	}

	/** 
	 * Gets the current observer count.
	 * @return The current observer count.
	 */
	protected int getObserverCount() {
		return observerCount;
	}
}