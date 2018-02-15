/*
 * Created on 04.03.2005
 *
 * 
 */
package org.visnacom.model;

import java.util.*;

/**
 * @author F. Pfeiffer
 * 
 * Adds expandEdge, edgeReport and isDerivedEdge to ObservableCompoundGraph.
 */
public abstract class BaseCompoundGraph extends ObservableCompoundGraph {

	// maps elements to their text labels
	private HashMap leafLabel, clusterLabel, edgeLabel;

	/**
	 * Constructor.
	 *  
	 */
	protected BaseCompoundGraph() {
		super();
		leafLabel = new HashMap();
		clusterLabel = new HashMap();
		edgeLabel = new HashMap();
	}

	/**
	 * Copy-Constructor.
	 * 
	 * @param bcpg
	 *            The BaseCompoundGraph to be copied.
	 */
	protected BaseCompoundGraph(BaseCompoundGraph bcpg) {
		super(bcpg);
		leafLabel = new HashMap();
		clusterLabel = new HashMap();
		edgeLabel = new HashMap();
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
	abstract public List expandEdge(Edge edge, Node node, boolean isLeaf);

	/**
	 * Gets the edges between children of a given inner node.
	 * 
	 * @param innerNode
	 *            The given inner node.
	 * @return The edges between given inner node.
	 */
	abstract public Collection contDerEdges(Node innerNode);

	/**
	 * Checks if there is a derived edge from u to v.
	 * 
	 * @param u
	 *            Source of potential derived edge.
	 * @param v
	 *            Target of potential derived edge.
	 * @return True if there is a derived edge from u to v; false otherwise.
	 */
	abstract public boolean derivedEdge(Node u, Node v);

	/**
	 * Gets all edges in basegraph that case given derived edge.
	 * 
	 * @param u
	 *            Given derived edge.
	 * @return List of edges that result in given derived edge.
	 */
	abstract public Collection edgeReport(Edge u);

	/**
	 * Used for writing titles of internal frames.
	 * 
	 * @return Integer id for last viewframe.
	 */
	public int numOfViews() {
		return getObserverCount();
	}

	/**
	 * Sets text label for leaves.
	 * 
	 * @param n
	 *            The leaf to be labelled.
	 * @param s
	 *            The label.
	 */
	public void putLeafLabel(Node n, String s) {
		leafLabel.put(n, s);
	}

	/**
	 * Sets text label for innner node.
	 * 
	 * @param n
	 *            The cluster to be labelled.
	 * @param s
	 *            The label.
	 */
	public void putClusLabel(Node n, String s) {
		clusterLabel.put(n, s);
	}

	/**
	 * Sets text label for edges.
	 * 
	 * @param e
	 *            The edge to be labelled.
	 * @param s
	 *            The label.
	 */
	public void putEdgeLabel(Edge e, String s) {
		Edge edg = (Edge) getEdge(e.getSource(), e.getTarget()).get(0);
		edgeLabel.put(edg, s);
	}

	/**
	 * Gets the label for given node.
	 * 
	 * @param n
	 *            The node.
	 * @return Label of node.
	 */
	public String getLeafLabel(Node n) {
		return (String) leafLabel.get(n);
	}

	/**
	 * Gets the label for given node.
	 * 
	 * @param n
	 *            The node.
	 * @return Label of node.
	 */
	public String getClusLabel(Node n) {
		return (String) clusterLabel.get(n);
	}

	/**
	 * Gets the label for given edge.
	 * 
	 * @param e
	 *            The edge.
	 * @return Label of edge.
	 */
	public String getEdgeLabel(Edge e) {
		Edge edg = (Edge) getEdge(e.getSource(), e.getTarget()).get(0);
		return (String) edgeLabel.get(edg);
	}

}