/*
 * Created on 06.03.2005
 *
 */
package org.visnacom.model;

/**
 * @author F. Pfeiffer
 * 
 * Specifies a delete action.
 */
public class ActionDelete extends Action {

	// signals whether deleted edges should be stored in CPG.View.Geometry until
	// next updateCollapse
	// or updateExpand
	private boolean rememberEdge = false;

	/**
	 * @see org.visnacom.model.Action#Action(java.lang.Object)
	 */
	public ActionDelete(Object affected) {
		super(affected);
	}

	/**
	 * Constructor. Sets rememberEdge.
	 * 
	 * @param affected
	 *            The affected object.
	 * @param r
	 *            True iff edges are to be remebered.
	 */
	public ActionDelete(Object affected, boolean r) {
		super(affected);
		rememberEdge = r;
	}

	/**
	 * Checks if edges are to be remembered.
	 * 
	 * @return True iff edges are to be remembered.
	 */
	public boolean remember() {
		return rememberEdge;
	}

}