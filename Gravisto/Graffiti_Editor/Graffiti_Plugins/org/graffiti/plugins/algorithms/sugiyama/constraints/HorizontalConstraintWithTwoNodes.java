// =============================================================================
//
//   HorizontalConstraintWithTwoNodes.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: HorizontalConstraintWithTwoNodes.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.constraints;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements a horizontal <tt>Node</tt>-constraint with two
 * participating <tt>Node</tt>s. If you want to save a constraint in a
 * <tt>Node</tt> with a <tt>StringAttribute</tt> in the <tt>Node</tt>'s
 * attribute-tree, the <tt>String</tt> must contain the following:
 * <ul>
 * <li><tt>HORIZONTAL_TWO_NODES_</tt>: This part of the <tt>String</tt>
 * identifies the <tt>SugiyamaConstraint</tt> as a
 * <tt>HorizontalConstraintWithTwoNodes</tt>
 * </ul>
 * <b>Exactly one</b> of the following three <tt>String</tt>s to define the
 * nature of the constraint:
 * <ul>
 * <li><tt>LEFT_OF_</tt>: Source-node is left of the target-node
 * <li><tt>RIGHT_OF_</tt>: The source-node is right of the target-node
 * <li><tt>EQUAL_X_</tt>: Source- and target-node have the same x-coordinates
 * <li><tt>NONEQUAL_X</tt>: Source- and target-node don't have the same
 * x-coordinate
 * </ul>
 * The constraint can optionally be marked as mandatory, when the following
 * <tt>String</tt> is found in the constraint:
 * <ul>
 * <li>MANDATORY_</tt>
 * </ul>
 * To identify the target-<tt>Node</tt> of the constraint, you must specify the
 * target's unique <tt>Sugiyama</tt>-identifier, that always starts with
 * <ul>
 * <li><tt>sugiyamaNode</tt> followed by an integer
 * </ul>
 * <p>
 * <b>Example</b>:<br>
 * <br>
 * The following <tt>String</tt> describes a horizontal constraint with two
 * nodes, where the source-<tt>Node</tt> is left of the target-</tt>Node</tt>
 * identified by the <tt>SugiyamaLabel</tt> <tt>sugiyamaNode34</tt> and the
 * constraint is a mandatory constraint:<br>
 * <tt>HORIZONTAL_TWO_NODES_LEFT_OF_MANDATORY_sugiyamaNode34</tt>
 * 
 * @author Ferdinand H�bner
 */
public class HorizontalConstraintWithTwoNodes extends
        SugiyamaConstraintWithTwoNodes {
    private final double EPSILON = 0.1;

    /**
     * This boolean stores, if the constraint is a constraint: source is left of
     * target
     */
    private boolean isLeftOf;

    /**
     * This boolean stores, if the constraint is a constraint: source is right
     * of the target
     */
    private boolean isRightOf;

    /**
     * Source and target don't have the same x-coordinates
     */
    private boolean isNonequalX;

    /**
     * This constructor is needed to test, if this constraint is assignable from
     * the interface <tt>SugiyamaConstraint</tt>.
     */
    public HorizontalConstraintWithTwoNodes() {
        source = null;
        target = null;
        mandatory = false;
        isLeftOf = false;
        isRightOf = false;
        isNonequalX = false;
    }

    /**
     * Default constructor for a <code>HorizontalConstraintWithTwoNodes</code>.
     * 
     * @param source
     *            Source-node of the constraint
     * @param target
     *            Target-node of the constraint
     * @param mandatory
     *            is the constraint mandatory (true), or optional
     */
    public HorizontalConstraintWithTwoNodes(Node source, Node target,
            boolean mandatory) {
        this.source = source;
        this.target = target;
        this.mandatory = mandatory;
        isLeftOf = false;
        isRightOf = false;
    }

    /**
     * Make this constraint a "is left of"-constraint
     */
    public void setIsLeftOf() {
        isLeftOf = true;
        isRightOf = false;
        isNonequalX = false;
    }

    /**
     * Make this constraint a "is right of"-constraint
     * 
     */
    public void setIsRightOf() {
        isLeftOf = false;
        isRightOf = true;
        isNonequalX = false;
    }

    /**
     * Make this constraint a "both nodes have the same x-position"-constraint
     * 
     */
    public void setEqualX() {
        isLeftOf = false;
        isRightOf = false;
        isNonequalX = false;
    }

    /**
     * Make this constraint a constraint: "the nodes don't have the same
     * x-coordinate"
     */
    public void setNonequalX() {
        isLeftOf = false;
        isRightOf = false;
        isNonequalX = true;
    }

    /**
     * Check if the constraint is a "is left of"-constraint
     * 
     * @return Returns whether the constraint is a "is left of"-constraint, or
     *         not
     */
    public boolean isLeftOf() {
        return isLeftOf;
    }

    /**
     * Check if the constraint is a "is right of"-constraint
     * 
     * @return Returns <code>true</code> if the constraint is a "is right
     *         of"-constraint, <code>false</code> otherwise
     */
    public boolean isRightOf() {
        return isRightOf;
    }

    /**
     * Check if the constraint is a constraint: "both nodes have the same
     * x-position"
     * 
     * @return Returns <code>true</code> if the constraint is a constraint:
     *         "both nodes have the same x-position"
     */
    public boolean isEqualX() {
        return (!isLeftOf && !isRightOf);
    }

    /**
     * Check if the constraint is a constraint: "the nodes don't have the same
     * x-coordinates"
     * 
     * @return Returns <tt>true</tt>, if the constraint is a constraint:
     *         "The nodes don't have the same x-coordinates", <tt>false</tt>
     *         otherwise
     */
    public boolean isNonequalX() {
        return isNonequalX;
    }

    /**
     * This method checks, if the constraint is satisfied.
     */
    @Override
    public String check() {
        String sourceName;
        String targetName;

        if (source == null || target == null)
            return null;
        double source_x;
        double target_x;
        try {
            source_x = source.getDouble(GraphicAttributeConstants.COORDX_PATH);
            target_x = target.getDouble(GraphicAttributeConstants.COORDX_PATH);
        } catch (AttributeNotFoundException anfe) {
            return null;
        }
        sourceName = this.getName(source);
        targetName = this.getName(target);

        if (isLeftOf) {
            if (source_x < target_x)
                return null;
            else
                return "Node " + sourceName + " is not left of Node "
                        + targetName;
        } else if (isRightOf) {
            if (source_x > target_x)
                return null;
            else
                return "Node " + sourceName + " is not right of Node "
                        + targetName;
        } else if (isNonequalX) {
            if (Math.abs(source_x - target_x) < EPSILON)
                return "Both nodes have the same x-coordinates";
            else
                return null;
        } else if (Math.abs(source_x - target_x) < EPSILON)
            return null;
        else
            return "Node " + sourceName + " is not at the same x-position as "
                    + "Node " + targetName;
    }

    /**
     * This method returns a new <tt>HorizontalConstraintWithTwoNodes</tt>, if
     * the passed <tt>String</tt> describes such a constraint.
     */
    public HorizontalConstraintWithTwoNodes isConstraint(String s, Node source,
            SugiyamaData data) {
        // Check if s represents a HorizontalConstraintWithTwoNodes
        if (!s.contains(SugiyamaConstants.CONSTRAINT_HORIZONTAL_TWO_NODES))
            return null;
        else {
            boolean left = false;
            boolean right = false;
            boolean equal = false;
            boolean nonequal = false;
            boolean mandatory = false;
            String targetNodeName = null;
            HorizontalConstraintWithTwoNodes constraint;

            // what type of constraint
            if (s.contains(SugiyamaConstants.CONSTRAINT_LEFT_OF)) {
                left = true;
            } else if (s.contains(SugiyamaConstants.CONSTRAINT_RIGHT_OF)) {
                right = true;
            } else if (s.contains(SugiyamaConstants.CONSTRAINT_EQUAL_X)) {
                equal = true;
            } else if (s.contains(SugiyamaConstants.CONSTRAINT_NONEQUAL_X)) {
                nonequal = true;
            } else
                return null;

            // check for mandatory constraint
            if (s.contains(SugiyamaConstants.CONSTRAINT_MANDATORY)) {
                mandatory = true;
            }

            // find the target node of the constraint
            String[] parts = s.split("_");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i]
                        .startsWith(SugiyamaConstants.PREFIX_SUGIYAMA_NODE_LABEL)) {
                    targetNodeName = parts[i];
                    break;
                }
            }
            // No target found - no constraint can be constructed
            if (targetNodeName == null)
                return null;

            // Node not mapped in the node-map - cannot construct constraint
            if (!data.getNodeMap().containsKey(targetNodeName))
                return null;

            Node targetNode;
            targetNode = data.getNodeMap().get(targetNodeName);
            constraint = new HorizontalConstraintWithTwoNodes(source,
                    targetNode, mandatory);

            if (left) {
                constraint.setIsLeftOf();
            } else if (right) {
                constraint.setIsRightOf();
            } else if (equal) {
                constraint.setEqualX();
            } else if (nonequal) {
                constraint.setNonequalX();
            } else
                return null;

            return constraint;
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
