/*
 * Created on 06.03.2005
 *
 */
package org.visnacom.model;

import java.util.*;

/**
 * @author F. Pfeiffer
 * 
 * Specifies an expand action.
 */
public class ActionExpand extends Action {

	//	a list of instances of inner class
	private List mappings;

	// contains edges between children of expanded node
	public Collection contDerEdges;

	// the node to be expanded
	public Node v;


	/**
	 * Constructor. Initializes member variables.
	 * 
	 * @param affected
	 *            The affected object.
	 */
	public ActionExpand(Object affected) {
		super(affected);
		v = (Node) affected;
		mappings = new LinkedList();
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
	 * Adds new mapping to list.
	 * 
	 * @param oEdge
	 *            Old edge before expansion
	 * @param nEdges
	 *            New edges after expansion, resulting from oEdge.
	 * @param oEdgeDel
	 *            True iff old edge has been deleted.
	 */
	public void addMapping(Edge oEdge, List nEdges, boolean oEdgeDel) {
		mappings.add(new Mapping(oEdge, nEdges, oEdgeDel));
	}

	/**
	 * 
	 * @author F. Pfeiffer
	 * 
	 * Inner class implementing a mapping between an old edge before expansion
	 * and the resulting new edges.
	 */
	public class Mapping {

		// old edge before expansion
		public Edge oldEdge;

		// new edges after expansion
		public List newEdges;

		// true iff old edge has been deleted
		public boolean oldEdgeDeleted;

		/**
		 * Constructor. Sets member variables.
		 * 
		 * @param oEdge
		 *            Old edge before expansion.
		 * @param nEdges
		 *            New edges after expansion.
		 * @param oEdgeDel
		 *            True iff old edge has been deleted.
		 */
		public Mapping(Edge oEdge, List nEdges, boolean oEdgeDel) {
			oldEdge = oEdge;
			newEdges = nEdges;
			oldEdgeDeleted = oEdgeDel;
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			String s = "oldEdge: ";
			s += oldEdge.toString();
			s += "\n newEdges: " + newEdges.toString();
			s += "\noldEdgeDeleted: " + oldEdgeDeleted;
			return s;
		}
	}

}