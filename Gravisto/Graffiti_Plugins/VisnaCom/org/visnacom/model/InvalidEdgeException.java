/*
 * Created on 28.02.2005
 *
 */
package org.visnacom.model;

/**
 * @author F. Pfeiffer
 *
 * This exception is thrown if an invalid edge is inserted
 * (i.e. source and target are equal or edge connects ancestor with descendant).
 */
public class InvalidEdgeException extends RuntimeException {

	/**
	 * Constructor.
	 * 
	 * @param err
	 *            Error message.
	 */
	public InvalidEdgeException(String err) {
		super(err);
	}
	
}
