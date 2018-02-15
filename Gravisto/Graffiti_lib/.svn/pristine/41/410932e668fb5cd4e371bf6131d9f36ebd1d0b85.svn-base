/*
 * Created on 21.03.2005
 *
 */
package org.visnacom.model;
import java.math.*;

/**
 * @author F. Pfeiffer
 *
 * Node supporting total ordering.
 */
public class OrderedNode extends Node {
	
	// tag representing position in total ordering
	private BigInteger tag;
	
	// predecessor and successor nodes
	private OrderedNode pred, succ;
	
	/**
	 * Constructor.
	 *
	 */
	public OrderedNode() {
		super();
		tag = new BigInteger("-1");
		pred = null;
		succ = null;
	}
	
	/**
	 * Constructor. Sets id.
	 * @param ID The node id.
	 */
	public OrderedNode(int ID) {
		super(ID);
		tag = new BigInteger("-1");
		pred = null;
		succ = null;
	}
	
	/**
	 * Sets tag.
	 * @param newTag The new tag.
	 */
	public void setTag(BigInteger newTag) {
		this.tag = newTag;
	}
	
	/**
	 * Gets tag.
	 * @return The node tag.
	 */
	public BigInteger getTag() {
		return tag;
	}
	
	/**
	 * Gets node predecessor.
	 * @return Predecessor of node.
	 */
	public OrderedNode getPred() {
		return pred;
	}
	
	/**
	 * Gets node successor.
	 * @return Successor of node.
	 */
	public OrderedNode getSucc() {
		return succ;
	}
	
	/**
	 * Sets node predecessor.
	 * @param n Predecessor of node.
	 */
	public void setPred(OrderedNode n) {
		pred = n;
	}
	
	/**
	 * Sets node successor.
	 * @param n Successor of node.
	 */
	public void setSucc(OrderedNode n) {
		succ = n;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String s = super.toString();
		s+= " (tag: "+tag+")";
		return s;
	}

}
