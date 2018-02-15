// =============================================================================
//
//   SugiyamaConstraint.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SugiyamaConstraint.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.constraints;

import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This interface represents a constraint in a <tt>Graph</tt>.
 * <p>
 * Each constraint has to implement the method <i>check()</i>. In this method,
 * the constraint has to check if it is satisfied.
 * <p>
 * The method <i>isConstraint()</i> has to be implemented to extract a textual
 * description from a <tt>Node</tt> and translate it into an actual
 * <tt>SugiyamaConstraint</tt>-Object. If the passed <tt>String</tt> describes
 * your implementation of a constraint, the method has to return a new
 * constraint-object.
 * 
 * @author Ferdinand H�bner
 */
public interface SugiyamaConstraint {
    /**
     * Return <tt>true</tt>, if the constraint is mandatory, <tt>false</tt>
     * otherwise
     */
    public boolean isMandatory();

    /**
     * Setter-method to toggle the mandatory-<tt>boolean</tt>.
     * 
     * @param b
     *            Sets, whether the constraint is a mandatory constraint, or not
     */
    public void setMandatory(boolean b);

    /**
     * Checks, if the constraint is satisfied.
     * 
     * @return Returns a <tt>String</tt> with a message that can be displayed to
     *         the user, if the constraint has been violated, <tt>null</tt>
     *         otherwise.
     */
    public String check();

    /**
     * This method checks, if the <tt>String</tt> <tt>s</tt> represents a
     * <tt>SugiyamaConstraint</tt> of the implementing type.
     * 
     * @param s
     *            The <tt>String</tt>, that possibly represents a constraint.
     * @param source
     *            The source-<tt>Node</tt> of the constraint
     * @return Returns a new <tt>SugiyamaConstraint</tt>, if the <tt>String</tt>
     *         represents a constraint of the constraint-type, <tt>null</tt>
     *         otherwise.
     */
    public SugiyamaConstraint isConstraint(String s, Node source,
            SugiyamaData data);

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
