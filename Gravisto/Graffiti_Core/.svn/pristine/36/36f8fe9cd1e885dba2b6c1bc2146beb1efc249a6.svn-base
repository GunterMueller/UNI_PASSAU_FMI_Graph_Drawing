/*
 * Created on 03.04.2005
 *
 */
package org.visnacom.model;

import java.util.*;
import java.math.BigInteger;

/**
 * @author F. Pfeiffer
 * 
 * Compares nodes according to their depth in inclusion tree.
 */
public class DepthComparator implements Comparator {

	HashMap depths;

	/**
	 * Constructor.
	 * 
	 * @param d
	 *            HashMap containing depths for nodes.
	 */
	public DepthComparator(HashMap d) {
		depths = d;
	}

	/**
	 * Compares objects according to depths.
	 * 
	 * @param o1
	 *            First object.
	 * @param o2
	 *            Second object.
	 * @return Comparison result.
	 */
	public int compare(Object o1, Object o2) {
		Node n1 = (Node) o1;
		Node n2 = (Node) o2;
		if (((BigInteger) depths.get(n1))
				.compareTo((BigInteger) depths.get(n2)) > 0) {
			return -1;
		} else if (((BigInteger) depths.get(n1)).compareTo((BigInteger) depths
				.get(n2)) > 0) {
			return 0;
		} else {
			return 1;
		}
	}

}