/*
 * Created on 22.04.2005
 *
 */
package org.visnacom.model;

import java.util.*;
import java.math.*;

/**
 * @author F. Pfeiffer
 * 
 * This class implements efficient algorithm for finding ancestors at given
 * level.
 */
public class AncestorAtLevel {

	// jump table containing proper ancestors divisible by 2^i
	private HashMap jump;

	// ancestor at level
	private HashMap levelanc;

	// root node of compound graph
	private Node root;

	// table containing rank of node
	private HashMap rank;

	// table containing size of node
	private HashMap size;

	// compound graph
	private CompoundGraph cpg;

	// number of nodes, used for preprocessing after removeLeaf
	private BigInteger preproSize, currentSize;

	/**
	 * Constructor. Initializes tables.
	 * 
	 * @param c
	 *            The compound graph.
	 */
	public AncestorAtLevel(CompoundGraph c) {
		jump = new HashMap();
		levelanc = new HashMap();
		size = new HashMap();
		rank = new HashMap();
		cpg = c;
		root = cpg.getRoot();
		preproSize = new BigInteger("0", 2);
		currentSize = new BigInteger("0", 2);
	}

	/**
	 * Calculates size of nodes in subtree.
	 * 
	 * @param n
	 *            Root of subtree.
	 */
	private void calcSize(Node n) {
		BigInteger s = new BigInteger("1", 2);
		Iterator it = cpg.getChildrenIterator(n);
		while (it.hasNext()) {
			Node child = (Node) it.next();
			calcSize(child);
			s = s.add((BigInteger) size.get(child));
		}
		size.put(n, s);
	}

	/**
	 * Calculates ranks of nodes in compound graph.
	 *  
	 */
	private void calcRank() {
		Iterator it = cpg.getAllNodesIterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			BigInteger d = (BigInteger) cpg.inclusionDepth(n);
			BigInteger s = (BigInteger) size.get(n);
			int i_max = s.bitLength() - 1;
			int i = 0;
			for (i = 0; i <= i_max && !d.equals(new BigInteger("0", 2)); i++) {
				BigInteger big = (new BigInteger("10", 2)).pow(i);
				BigInteger remainder = d.remainder(big);
				if (!remainder.equals(new BigInteger("0", 2))) {
					i--;
					break;
				}
			}
			if (i > i_max) {
				i = i_max;
			}
			rank.put(n, new Integer(i));
		}
	}

	/**
	 * Creates levelanc table.
	 *  
	 */
	private void makeLevelancTable() {
		Iterator it = cpg.getAllNodesIterator();
		while (it.hasNext()) {
			Node n = (Node) it.next();
			Node node = n;
			int r = ((Integer) rank.get(n)).intValue();
			int l = (int) Math.pow(2, r);
			ArrayList list = new ArrayList(l);
			for (int j = 0; j < l; j++) {
				list.add(j, n);
				n = cpg.getParent(n);
			}
			this.levelanc.put(node, list);
		}
	}

	/**
	 * Creates jump table for subtree.
	 * 
	 * @param n
	 *            Root of subtree.
	 */
	private void makeJumpTable(Node n) {
		Node par = cpg.getParent(n);
		BigInteger big = ((BigInteger) cpg.inclusionDepth(n));

		int i_max = big.bitLength() - 1;
		if (i_max == -1) {
			i_max = 0;
		}
		ArrayList l = new ArrayList(i_max + 1);

		for (int i = 0; i <= i_max && par != null; i++) {
			BigInteger pow = (new BigInteger("10", 2)).pow(i);
			if (big.equals(pow) && i != 0) {
				continue;
			}
			if (((BigInteger) cpg.inclusionDepth(par)).remainder(pow).equals(
					new BigInteger("0", 2))) {
				l.add(i, par);
			} else {
				l.add(i, (Node) ((List) jump.get(par)).get(i));
			}
		}
		jump.put(n, l);
		Iterator it = cpg.getChildrenIterator(n);
		while (it.hasNext()) {
			Node node = (Node) it.next();
			this.makeJumpTable(node);
		}
	}

	/**
	 * Gets the ancestor of a given node at a given level.
	 * 
	 * @param v
	 *            The ancestor of this node is looked for.
	 * @param d
	 *            The level of the ancestor.
	 * @return The ancestor of v at level d.
	 */
	public Node levelAncestor(Node v, BigInteger d) {
		BigInteger x = ((BigInteger) cpg.inclusionDepth(v)).subtract(d);
		int i = x.add(new BigInteger("1", 2)).bitLength() - 1;

		while (((BigInteger) cpg.inclusionDepth(v)).subtract(d).multiply(
				new BigInteger("10", 2)).compareTo(
				(new BigInteger("10", 2)).pow(i)) >= 0) {
			v = (Node) ((List) jump.get(v)).get(i - 1);
		}
		return (Node) ((List) levelanc.get(v)).get((cpg.inclusionDepth(v))
				.subtract(d).intValue());
	}

	/**
	 * Constructs all tables.
	 *  
	 */
	public void preprocess() {
		LinkedList leaves = new LinkedList();
		cpg.getLeaves(root, leaves);
		jump.clear();
		rank.clear();
		size.clear();
		levelanc.clear();
		this.calcSize(root);
		this.calcRank();
		this.makeLevelancTable();
		this.makeJumpTable(root);
		preproSize = new BigInteger("0", 2);
		currentSize = new BigInteger("0", 2);
		Iterator it = cpg.getAllNodesIterator();
		// attributes used for shrinking after deletion of elements
		while (it.hasNext()) {
			it.next();
			preproSize = preproSize.add(new BigInteger("1", 2));
			currentSize = currentSize.add(new BigInteger("1", 2));
		}
	}

	/**
	 * Adds a new leaf to tables.
	 * 
	 * @param v
	 *            The new leaf.
	 * @param par
	 *            Parent of new leaf.
	 */
	public void addLeaf(Node v, Node par) {
		rank.put(v, new BigInteger("0", 2));
		ArrayList l = new ArrayList();
		ArrayList jumpList = new ArrayList();
		jump.put(v, jumpList);
		l.add(v);
		levelanc.put(v, l);

		BigInteger bound = ((BigInteger) cpg.inclusionDepth(v))
				.add(new BigInteger("1", 2));
		int j = bound.bitLength() - 1;
		Node w = null;
		// adds new elements to tables
		for (int i = 0; i <= j; i++) {
			BigInteger pow = (new BigInteger("10", 2)).pow(i);
			BigInteger remainder = ((BigInteger) cpg.inclusionDepth(par))
					.remainder(pow);
			if(pow.compareTo((BigInteger) cpg.inclusionDepth(par))>0) {
				continue;
			}
			if (remainder.equals(new BigInteger("0"))) {
				w = par;
			} else {
				w = (Node) ((ArrayList) jump.get(par)).get(i);
			}
			jumpList.add(w);
			ArrayList wList = (ArrayList) levelanc.get(w);
			int s = wList.size();
			Node last = (Node) wList.get(s - 1);
			Node parOfLast = cpg.getParent(last);
			if (last != cpg.getRoot()) {
				wList.add(parOfLast);
			}
		}
		size.put(v, new BigInteger("1", 2));
		while (par != null) {
			BigInteger parSize = (BigInteger) size.get(par);
			parSize = parSize.add(new BigInteger("1", 2));
			size.put(par, parSize);
			par = cpg.getParent(par);
		}

		preproSize = preproSize.add(new BigInteger("1", 2));
		currentSize = currentSize.add(new BigInteger("1", 2));
	}

	/**
	 * Removes leaf from tables. Calls preprocess after half of the nodes have
	 * been removed by this method
	 * 
	 * @param v
	 *            The Node to be removed.
	 */
	public void removeLeaf(Node v) {
		levelanc.remove(v);
		jump.remove(v);
		rank.remove(v);
		size.remove(v);
		currentSize = currentSize.subtract(new BigInteger("1", 2));
		BigInteger halfOfSize = preproSize.divide(new BigInteger("10", 2));
		if (currentSize.compareTo(halfOfSize) <= 0) {
			preprocess();
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = "Size: ";
		s += size.toString() + " \nRank: ";
		s += rank.toString() + "\nLevelanc: ";
		s += levelanc.toString() + "\nJump: ";
		s += jump.toString();
		//		s += .toString();
		return s;
	}

}