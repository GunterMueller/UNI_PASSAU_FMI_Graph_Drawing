// =============================================================================
//
//   SugiyamaConstraintWithTwoNodes.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SugiyamaConstraintWithTwoNodes.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.constraints;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;

/**
 * This abstract class represents a constraint on two <code>Nodes</code>. An
 * actual implementation has to overwrite the method check.
 * 
 * Source and Target of a constraint should be interpreted the following way:
 * Assume you have a constraint like "is left of". So let <i>a</i> be the source
 * of the constraint, <i>b</i> the target. The semantic of such a constraint
 * would be: <i>Node a is left of Node b</i>. Which can either be
 * <code>true</code> (which should be returned by check) or <code>false</code>
 * 
 * @author Ferdinand H�bner
 */
public abstract class SugiyamaConstraintWithTwoNodes implements
        SugiyamaConstraint {
    protected boolean mandatory;

    protected Node source;

    protected Node target;

    /**
     * Accessor for the boolean "mandatory". If this is <code>true</code>, the
     * constraint is a <b>must</b>, otherwise it's optional.
     * 
     * @return Returns whether this constraint is a mandatory constraint
     */
    public boolean isMandatory() {
        return mandatory;
    }

    /**
     * Setter-method for the boolean "mandatory"
     * 
     * @param b
     *            Whether the constraint is a mandatory constraint or not
     */
    public void setMandatory(boolean b) {
        mandatory = b;
    }

    /**
     * This method checks if the constraint is satisfied. Overwrite this method
     * in an actual implementation of a constraint.
     * 
     * @return Returns a <tt>String</tt> with an error-message that can be
     *         displayed to the user, if the constraint has been violated,
     *         <tt>null</tt> otherwise.
     */
    public String check() {
        return null;
    }

    /**
     * Accessor to get the target of a constraint.
     * 
     * @return Returns the target of the constraint.
     */
    public Node getTarget() {
        return target;
    }

    /**
     * Setter to store the target of a constraint
     * 
     * @param t
     *            The target of the constraint
     */
    public void setTarget(Node t) {
        target = t;
    }

    /**
     * Accessor to get the source of a constraint
     * 
     * @return The source of a constraint
     */
    public Node getSource() {
        return source;
    }

    /**
     * Setter to store the source of a constraint
     * 
     * @param s
     *            The source of the constraint
     */
    public void setSource(Node s) {
        source = s;
    }

    /**
     * This method tries to return the label of the <tt>Node</tt>. At first, it
     * tries to access "label0" from graphics. If that fails, the
     * sugiyama-identifier is returned. If the <tt>Node</tt> does not have a
     * sugiyama-identifier, the <tt>String</tt> "without label" is returned.
     * 
     * @param n
     *            This <tt>Node</tt>'s label is attempted to be resolved
     * @return A <tt>String</tt> with the label of this <tt>Node</tt>.
     */
    protected String getName(Node n) {
        String name = null;
        try {
            name = n.getString("label0.label");
        } catch (AttributeNotFoundException anfe) {
            try {
                name = n.getString(SugiyamaConstants.PATH_LABEL);
            } catch (AttributeNotFoundException anfe2) {
                name = "without label";
            }
        }

        return name;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
