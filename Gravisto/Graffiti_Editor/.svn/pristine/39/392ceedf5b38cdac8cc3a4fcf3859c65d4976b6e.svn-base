/*
 * Created on 06.03.2005
 *
 */
package org.visnacom.model;

import java.util.*;

/**
 * @author F. Pfeiffer
 * 
 * Specifies a contraction.
 */
public class ActionContract extends Action {

	// the children of the node to be coontracted
	List children;

	// a list of instances of inner class
	private List mappings;

	// maps new edges to old edges
	private HashMap map;

	/**
	 * Constructor. Initializes member variables.
	 * 
	 * @param affected
	 *            The affected object.
	 */
	public ActionContract(Object affected) {
		super(affected);
		children = new LinkedList();
		mappings = new LinkedList();
		map = new HashMap();
	}

	/**
	 * Clears list of children.
	 *  
	 */
	public void clear() {
		children.clear();
	}

	/**
	 * Adds further child to list.
	 * 
	 * @param n
	 *            Child node.
	 */
	public void add(Node n) {
		children.add(n);
	}

	/**
	 * Adds a list to the list of children.
	 * 
	 * @param l
	 *            List to be added.
	 */
	public void addAll(List l) {
		children.addAll(l);
	}

	/**
	 * Gets the list of children of the node to be collapsed.
	 * 
	 * @return List of children of collapsed node.
	 */
	public List getChildren() {
		return children;
	}

	/**
	 * Gets iterator for list of mappings.
	 * 
	 * @return Iterator for list of mappings.
	 */
	public Iterator getMappinsIterator() {
		return mappings.iterator();
	}

	/**
	 * Adds a new mapping from new edge to old edges.
	 * 
	 * @param oEdges
	 *            List of old edges.
	 * @param nEdge
	 *            New edge after contraction.
	 * @param nEdgeIns
	 *            True if new edge was inserted for old edges.
	 * @return The new mapping.
	 */
	public Mapping addMapping(List oEdges, Edge nEdge, boolean nEdgeIns) {
		Mapping m = new Mapping(oEdges, nEdge, nEdgeIns);
		mappings.add(m);
		return m;
	}

	/**
	 * Adds only one further old edge to mapping.
	 * 
	 * @param oEdge
	 *            Further old edge.
	 * @param nEdge
	 *            New edge after contraction.
	 * @param nEdgeIns
	 *            True if new edge was inserted for old edges.
	 */
	public void addMapping(Edge oEdge, Edge nEdge, boolean nEdgeIns) {
		Object m = map.get(nEdge);
		if (m == null) {
			LinkedList l = new LinkedList();
			l.add(oEdge);
			Mapping ma = this.addMapping(l, nEdge, nEdgeIns);
			map.put(nEdge, ma);
		} else {
			Mapping mapping = (Mapping) m;
			mapping.oldEdges.add(oEdge);
			mapping.newEdgeInserted = mapping.newEdgeInserted || nEdgeIns;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return mappings.toString();
	}

	/**
	 * 
	 * @author F. Pfeiffer
	 * 
	 * Inner class describing mapping from new edge to old edges.
	 */
	public class Mapping {

		// new edge after contraction
		public Edge newEdge;

		// old edges before contraction
		public List oldEdges;

		// signals whether new edge has been inserted
		public boolean newEdgeInserted;

		/**
		 * Constructor. Sets member variables.
		 * 
		 * @param oEdges
		 *            List of old edges.
		 * @param nEdge
		 *            New edge.
		 * @param nEdgeIns
		 *            T
		 */
		public Mapping(List oEdges, Edge nEdge, boolean nEdgeIns) {
			newEdge = nEdge;
			oldEdges = oEdges;
			newEdgeInserted = nEdgeIns;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			String s = "oldEdge: ";
			s += oldEdges.toString();
			s += "\n newEdges: " + newEdge.toString();
			s += "\n newEdgeInserted: " + newEdgeInserted;
			return s;
		}
	}

}