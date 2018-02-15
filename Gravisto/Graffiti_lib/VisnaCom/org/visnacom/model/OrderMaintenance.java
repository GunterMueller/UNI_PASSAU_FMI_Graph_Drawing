/*
 * Created on 09.03.2005
 *
 */
package org.visnacom.model;

import java.util.*;
import java.math.*;

/**
 * @author F. Pfeiffer
 * 
 * Datastructure for maintaining total ordering of nodes.
 */
public class OrderMaintenance implements Comparator {

	// current number of leaves
	private BigInteger size;

	// constant between 1.0 and 2.0 (determines how full list can get)
	private static final double T = 1.5;

	// tag universe
	private BigInteger u;

	// the first node according to ordering
	private OrderedNode firstNode;

	/**
	 * Constructor.
	 *  
	 */
	public OrderMaintenance() {
		u = new BigInteger("100", 2);
		size = new BigInteger("0", 2);

	}

	/**
	 * Inserts a new OrderedNode into datastructure.
	 * 
	 * @param pred
	 *            The OrderedNode after which the new OrderedNode will be
	 *            inserted in the total order.
	 * @param n
	 *            The OrderedNode to be inserted.
	 */
	public void insert(OrderedNode pred, OrderedNode n) {

		assert (pred != n);
		assert (pred == null || pred != pred.getPred());

		//insert first leaf
		if (size.equals(new BigInteger("0", 2))) {
			n.setTag(new BigInteger("0", 2));
			firstNode = n;

			//insert second leaf
		} else if (size.equals(new BigInteger("1", 2))) {

			BigInteger i = u.divide(new BigInteger("10", 2));
			i = i.subtract(new BigInteger("1", 2));

			n.setTag(i);
			n.setPred(firstNode);
			firstNode.setSucc(n);

			// inserts further leaves
		} else {
			// tag of new leaf
			BigInteger succTag = null;
			if (pred.getSucc() == null) {
				//maximum element as pred
				succTag = u.subtract(new BigInteger("1", 2));
			} else {
				succTag = pred.getSucc().getTag();
			}
			BigInteger avg = (succTag.add(pred.getTag()).divide(new BigInteger(
					"2")));

			// checks if relabeling is necessary
			if ((succTag.subtract(pred.getTag()).equals(new BigInteger("1", 2)))
					|| (succTag.subtract(pred.getTag()).equals(new BigInteger(
							"0", 2)))) {
				avg = relabel(pred);
			}

			OrderedNode succ = pred.getSucc();
			assert (succ != n);
			if (!(succ == null)) {
				succ.setPred(n);
			}
			pred.setSucc(n);
			n.setPred(pred);
			n.setSucc(succ);

			// sets new tag
			n.setTag(avg);
		}

		assert (pred == null || pred != pred.getPred());
		assert (pred == null || pred != pred.getSucc());
		assert (n != n.getPred());
		assert (n != n.getSucc());
		size = size.add(new BigInteger("1", 2));
	}

	/**
	 * Performs tag relabeling.
	 * 
	 * @param pred
	 *            The OrderedNode representing the first enclosing tag range.
	 * @return The tag of the new node after relabeling.
	 */
	private BigInteger relabel(OrderedNode pred) {
		BigInteger tagMin = pred.getTag();
		OrderedNode left = pred;
		OrderedNode right = pred.getSucc();
		OrderedNode relabelStart = left;
		OrderedNode relabelEnd = right;

		int i;
		BigInteger range = new BigInteger("0", 2);
		BigInteger occupied = new BigInteger("0", 2);
		BigInteger tagStart = new BigInteger("0", 2);
		BigInteger tagEnd = new BigInteger("0", 2);
		double density = 1.0;
		double threshold = 0.0;

		BigInteger newTag = new BigInteger("0", 2);

		// gets enclosing range
		for (i = 1; (density > threshold) && range.compareTo(u) <= 0; i++) {
			range = new BigInteger("10", 2).pow(i);

			// root range overflow requires rebuild
			if (range.compareTo(u) > 0) {
				rebuild();
				tagMin = tagMin.multiply(new BigInteger("10", 2));
			}

			tagStart = tagMin;
			tagEnd = tagMin;
			for (int m = 0; m < i; m++) {
				tagStart = tagStart.clearBit(m);
				tagEnd = tagEnd.setBit(m);
			}

			// counts number of occupied tags
			while (left != null && left.getTag().compareTo(tagStart) >= 0) {
				relabelStart = left;
				occupied = occupied.add(new BigInteger("1", 2));
				assert (left.getPred() != left);
				left = left.getPred();
			}

			// counts number of occupied tags
			while (right != null && right.getTag().compareTo(tagEnd) <= 0) {
				occupied = occupied.add(new BigInteger("1", 2));
				relabelEnd = right;
				assert (right != right.getSucc());
				right = right.getSucc();
			}

			BigDecimal occ = new BigDecimal(occupied);
			BigDecimal ran = new BigDecimal(range);

			BigDecimal dec = occ.divide(ran, 30, BigDecimal.ROUND_HALF_UP);

			density = dec.doubleValue();
			threshold = Math.pow(T, -1.0 * i);
		}

		BigInteger relabelDist = range.divide(occupied.add(new BigInteger("1",
				2)));
		OrderedNode iter = relabelStart;
		BigInteger currentTag = tagStart;

		// if pred is last element relabel dist must be reduced so that
		// new element does not get tag greater than u
		if (pred.getSucc() == null
				&& !relabelDist.equals(new BigInteger("1", 2))) {
			relabelDist = relabelDist.subtract(new BigInteger("1", 2));
		}

		// in case that pred is last element
		if (relabelEnd == null && pred.getSucc() == null) {
			relabelEnd = pred;
		}

		// relabel
		while (iter != null && relabelEnd != null
				&& compare(iter, relabelEnd) <= 0) {
			iter.setTag(currentTag);
			if (iter != pred) {
				currentTag = currentTag.add(relabelDist);
			} else {
				currentTag = currentTag.add(relabelDist);
				newTag = currentTag;
				currentTag = currentTag.add(relabelDist);
			}
			iter = iter.getSucc();
		}

		// insert node with highest tag
		if (relabelEnd == null) {
			newTag = pred.getTag().add(new BigInteger("1", 2));
		}

		return newTag;
	}

	/**
	 * Rebuilds datastructure so that root range does not overflow.
	 *  
	 */
	private void rebuild() {
		u = u.multiply(new BigInteger("10", 2));
		OrderedNode iterator = firstNode;
		while (iterator != null) {
			iterator
					.setTag(iterator.getTag().multiply(new BigInteger("10", 2)));
			iterator = iterator.getSucc();
		}
	}

	/**
	 * Shrinks maximum tag range when only few elements are contained.
	 *  
	 */
	private void shrink() {
		if (u.compareTo(new BigInteger("10", 2)) > 0) {
			u = u.divide(new BigInteger("10", 2));
			OrderedNode iterator = firstNode;
			BigInteger newTag = new BigInteger("0", 2);
			while (iterator != null) {
				iterator.setTag(newTag);
				newTag = newTag.add(new BigInteger("10", 2));
				iterator = iterator.getSucc();
			}
		}
	}

	/**
	 * Removes leaf from datastructure.
	 * 
	 * @param n
	 *            The leaf to be removed.
	 */
	public void delete(OrderedNode n) {

		// actual deletion
		OrderedNode pred = n.getPred();
		OrderedNode succ = n.getSucc();
		if (pred != null) {
			pred.setSucc(succ);
		}
		if (succ != null) {
			succ.setPred(pred);
		}
		size = size.subtract(new BigInteger("1"));
		if (n == firstNode) {
			firstNode = succ;
		}

		// calculates shrinking threshold
		BigDecimal dens = (new BigDecimal(size)).divide(new BigDecimal(u), 30,
				BigDecimal.ROUND_HALF_UP);
		int k = u.bitLength() - 1;
		double shrinkThres = Math.pow(T, -(k - 1));

		// shrinking condition
		if (dens.doubleValue() < shrinkThres) {
			this.shrink();
		}

		if (size.equals(new BigInteger("1"))) {
			firstNode.setTag(new BigInteger("0", 2));
		}

		n.setPred(null);
		n.setSucc(null);
	}

	/**
	 * Compares to leaves according to total order.
	 * @param o1 First object.
	 * @param o2 Second object.
	 * @return Comparison result.
	 */
	public int compare(Object o1, Object o2) {
		if (!(o1 instanceof OrderedNode) || !(o2 instanceof OrderedNode)) {
			throw new NotComparableException(
					"Only OrderedNodes can be compared!");
		}

		OrderedNode n1 = (OrderedNode) o1;
		OrderedNode n2 = (OrderedNode) o2;

		BigInteger tag1 = n1.getTag();
		BigInteger tag2 = n2.getTag();

		return tag1.compareTo(tag2);
	}

	/**
	 * Less or equal comparison.
	 * 
	 * @param n1
	 *            First node to be compared.
	 * @param n2
	 *            Second node to be compared.
	 * @return True iff n1 is less or equal n2.
	 */
	public boolean lessOrEqual(OrderedNode n1, OrderedNode n2) {
		if (n1 == null || n2 == null) {
			return false;
		}
		int i = compare(n1, n2);
		return (i <= 0);
	}

	/**
	 * Swaps two elements in datastructure.
	 * 
	 * @param n1
	 *            The first element to be swapped.
	 * @param n2
	 *            The second element to be swapped.
	 */
	public void swap(OrderedNode n1, OrderedNode n2) {

		assert (n1.getPred() != n1);
		assert (n2.getPred() != n2);

		BigInteger tag = n1.getTag();
		n1.setTag(n2.getTag());
		n2.setTag(tag);

		// updates firstNode if necessary
		if (n1 == firstNode) {
			firstNode = n2;
		} else if (n2 == firstNode) {
			firstNode = n1;
		}

		// successors, predecessors of nodes to be swapped
		OrderedNode n1succ = n1.getSucc();
		OrderedNode n1pred = n1.getPred();
		OrderedNode n2succ = n2.getSucc();
		OrderedNode n2pred = n2.getPred();

		// actual swapping
		if (n1pred != null) {
			n1pred.setSucc(n2);
		}
		if (n1succ != null) {
			n1succ.setPred(n2);
		}
		if (n2pred != null) {
			n2pred.setSucc(n1);
		}
		if (n2succ != null) {
			n2succ.setPred(n1);
		}
		if (n2succ == n1) {
			n1.setSucc(n2);
		} else {
			n1.setSucc(n2succ);
		}
		if (n2pred == n1) {
			n1.setPred(n2);
		} else {
			n1.setPred(n2pred);
		}
		if (n1succ == n2) {
			n2.setSucc(n1);
		} else {
			n2.setSucc(n1succ);
		}
		if (n1pred == n2) {
			n2.setPred(n1);
		} else {
			n2.setPred(n1pred);
		}

	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "OrderMain ";
		OrderedNode iter = firstNode;
		while (iter != null) {
			s += iter.toString() + " pred: " + iter.getPred() + " succ: "
					+ iter.getSucc() + " | ";
			iter = iter.getSucc();
		}
		return s;
	}
}