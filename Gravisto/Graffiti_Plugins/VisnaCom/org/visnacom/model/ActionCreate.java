/*
 * Created on 06.03.2005
 *
 */
package org.visnacom.model;

/**
 * @author F. Pfeiffer
 * 
 * Specifies a create action.
 */
public class ActionCreate extends Action {

	// the parent of the newly created object (in case of nodes)
	private Object par = null;

	/**
	 * @see org.visnacom.model.Action#Action(java.lang.Object)
	 */
	public ActionCreate(Object affected) {
		super(affected);
	}

	/**
	 * Constructor. Additionally sets parent.
	 * 
	 * @param affected
	 *            The affected node.
	 * @param p
	 *            parent of affected node.
	 */
	public ActionCreate(Object affected, Object p) {
		super(affected);
		par = p;
	}

	/**
	 * Gets parent.
	 * 
	 * @return Parent of affected node.
	 */
	public Object getPar() {
		return par;
	}

}