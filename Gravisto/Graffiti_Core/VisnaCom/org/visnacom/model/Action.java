/*
 * Created on 06.04.2005
 *
 *
 */
package org.visnacom.model;

/**
 * @author F.Pfeiffer
 * 
 * This class describes a general action used in observer pattern.
 */
public class Action {

	// object which is affected by action
	Object affected;

	/**
	 * Constructor. Sets affected object.
	 * 
	 * @param aff
	 *            The affected object.
	 */
	public Action(Object aff) {
		affected = aff;
	}

	/**
	 * Gets the affected object.
	 * 
	 * @return The affected object.
	 */
	public Object getAffected() {
		return affected;
	}
}