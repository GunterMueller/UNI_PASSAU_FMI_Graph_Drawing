/*
 * Created on 10.03.2005
 *
 */
package org.visnacom.model;

/**
 * @author F. Pfeiffer
 *
 * Excetion thrown if two elements are not comparable.
 */
public class NotComparableException extends RuntimeException {

	/**
	 * Constructor.
	 * 
	 * @param err
	 *            Error message.
	 */
	public NotComparableException(String err) {
		super(err);
	}
	
}
