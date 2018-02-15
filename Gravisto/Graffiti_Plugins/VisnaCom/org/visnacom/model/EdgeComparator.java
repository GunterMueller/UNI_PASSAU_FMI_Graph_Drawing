/*
 * Created on 28.03.2005
 *
 */
package org.visnacom.model;

import java.util.*;

/**
 * @author F. Pfeiffer
 * 
 * Compares edges.
 */
public class EdgeComparator implements Comparator {

	/**
	 * Constructor.
	 *  
	 */
	public EdgeComparator() {

	}

	/**
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		if (o1 instanceof Edge && o2 instanceof Edge) {
			Edge e1 = (Edge) o1;
			Edge e2 = (Edge) o2;
			if (e1.getSource() == e2.getSource()
					&& e1.getTarget() == e2.getTarget()) {
				return 0;
			} else if (e1.getSource() != e2.getSource()) {
				return (e1.getSource().toString()).compareTo(e2.getSource()
						.toString());
			} else {
				return (e1.getTarget().toString()).compareTo(e2.getTarget()
						.toString());
			}
		}
		throw new InvalidEdgeException("No Edge");
	}

}