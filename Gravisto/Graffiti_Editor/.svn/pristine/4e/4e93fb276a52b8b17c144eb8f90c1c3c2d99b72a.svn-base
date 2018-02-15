/*
 * Created on Dec 9, 2004
 *
 */

package org.visnacom.model;

/**
 * @author F. Pfeiffer
 * 
 * This class implements the edges of a graph.
 */
public class Edge {

	// the source and target nodes
	private Node source, target;

	/**
	 * Standard constructor.
	 *  
	 */
	public Edge() {
	}

	/**
	 * Constructor. Sets source and target node.
	 * 
	 * @param s
	 *            The source node.
	 * @param t
	 *            The target node.
	 */
	public Edge(Node s, Node t) {
		source = s;
		target = t;
	}

	/**
	 * Gets the source node.
	 * 
	 * @return The source node.
	 */
	public Node getSource() {
		return source;
	}

	/**
	 * Gets the target node.
	 * 
	 * @return The target node.
	 */
	public Node getTarget() {
		return target;
	}

	/**
	 * Sets source node of edge.
	 * @param s New source node.
	 */
	public void setSource(Node s) {
		this.source = s;
	}

	/**
	 * Sets target node of edge.
	 * @param t New target node.
	 */
	public void setTarget(Node t) {
		this.target = t;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String result = "";
		result += "(";
		result += getSource().getId() + "," + getTarget().getId() + ")";
		return result;
	}

}

