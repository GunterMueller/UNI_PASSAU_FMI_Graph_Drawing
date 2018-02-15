/*
 * Created on 31.01.2005
 *
 */

package org.visnacom.view;

import org.visnacom.model.*;

/**
 * @author F. Pfeiffer
 * 
 * Drawing style/layout algorithm.
 */
public abstract class DrawingStyle {

	// not needed ?
	protected boolean isPreviousResultValid;

	// reference to old geometry
	protected Geometry geometry;
	
	protected DLL notToBeExpanded, notToBeContracted;

	/**
	 * Constructor.
	 * 
	 * @param geo
	 *            Geometry object.
	 */
	public DrawingStyle(Geometry geo) {
		isPreviousResultValid = true;
		geometry = geo;
		
		notToBeExpanded = new DLL();
		notToBeContracted = new DLL();
	}

	/**
	 * Not needed.
	 *  
	 */
	public void invalidate() {
		isPreviousResultValid = false;
	}

	/**
	 * Redraws using layout algorithm
	 * 
	 * @param newGeo
	 *            Geometry object which will contain new geometrical information
	 */
	abstract public void draw(Geometry newGeo);

	/**
	 * Layout algorithm for collapsing nodes.
	 * 
	 * @param clus
	 *            The node to be collapsed.
	 * @param newGeo
	 *            Geometry object which will contain new geometrical information
	 * @param action
	 *            Contains info about collapsing action.
	 */
	abstract public void contract(Node clus, Geometry newGeo,
			ActionContract action);

	/**
	 * Layout algorithm for expanding nodes.
	 * 
	 * @param clus
	 *            The node to be expanded.
	 * @param newGeo
	 *            Geometry object which will contain new geometrical information
	 * @param action
	 *            Contains info about expanding action.
	 */
	abstract public void expand(Node clus, Geometry newGeo, ActionExpand action);

	/**
	 * Checks if drawing style is capable of dealing with expansion of given
	 * node.
	 * 
	 * @param n
	 *            The node to be checked.
	 * @return True iff node can be expanded.
	 */
	public boolean canBeExpanded(Node n) {
		return !notToBeExpanded.contains(n);
	}
	
	/**
	 * Checks if drawing style is capable of dealing with contaction of given
	 * node.
	 * 
	 * @param n
	 *            The node to be checked.
	 * @return True iff node can be collapsed.
	 */
	public boolean canBeContracted(Node n) {
		return !notToBeContracted.contains(n);
	}
	
}