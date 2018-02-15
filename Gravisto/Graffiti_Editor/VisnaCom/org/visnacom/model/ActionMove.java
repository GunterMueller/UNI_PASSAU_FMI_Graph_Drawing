/*
 * Created on 03.04.2005
 *
 * 
 */
package org.visnacom.model;

/**
 * @author F. Pfeiffer
 * 
 * Specifies movement of node to another parent.
 */
public class ActionMove extends Action {

	// new parent of affected node
	private Object newParent;

	// old parent of affected node
	private Object oldParent;

	// true iff movement is due to splitting process
	private boolean split;

	/**
	 * Constructor. Sets member variables.
	 * 
	 * @param affected
	 *            The affected node.
	 * @param newPar
	 *            The new parent node.
	 * @param oldPar
	 *            The old parent node.
	 * @param s
	 *            True iff movement is due to splitting.
	 */
	public ActionMove(Object affected, Object newPar, Object oldPar, boolean s) {
		super(affected);
		newParent = newPar;
		oldParent = oldPar;
		split = s;
	}

	/**
	 * Gets new parent.
	 * 
	 * @return New parent node.
	 */
	public Object getNewPar() {
		return newParent;
	}

	/**
	 * Gets old parent.
	 * 
	 * @return Old parent.
	 */
	public Object getOldPar() {
		return oldParent;
	}

	/**
	 * Returns if movement is due to splitting.
	 * 
	 * @return True iff movement is due to splitting.
	 */
	public boolean isSplitting() {
		return split;
	}

}