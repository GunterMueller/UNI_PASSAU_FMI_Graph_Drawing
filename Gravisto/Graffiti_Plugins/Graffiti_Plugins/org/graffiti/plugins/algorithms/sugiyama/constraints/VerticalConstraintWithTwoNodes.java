// =============================================================================
//
//   VerticalConstraintWithTwoNodes.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: VerticalConstraintWithTwoNodes.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.constraints;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements a vertica <tt>Node</tt>-constraint with two
 * participating <tt>Node</tt>s. If you want to save a constraint in a
 * <tt>Node</tt> with a <tt>StringAttribute</tt> in the <tt>Node</tt>'s
 * attribute-tree, the <tt>String</tt> must contain the following:
 * <ul>
 * <li><tt>VERTICAL_TWO_NODES_</tt>: This part of the <tt>String</tt> identifies
 * the <tt>SugiyamaConstraint</tt> as a
 * <tt>HorizontalConstraintWithTwoNodes</tt>
 * </ul>
 * <b>Exactly one</b> of the following three <tt>String</tt>s to define the
 * nature of the constraint:
 * <ul>
 * <li><tt>ABOVE_</tt>: Source-node is above the target-node
 * <li><tt>BELOW_</tt>: The source-node is below the target-node
 * <li><tt>EQUAL_Y_</tt>: Source- and target-node have the same y-coordinates
 * <li><tt>NONEQUAL_Y</tt>: Source- and target-node don't have the same
 * y-coordinates
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
 * The following <tt>String</tt> describes a vertical constraint with two nodes,
 * where the source-<tt>Node</tt> is above the target-</tt>Node</tt> identified
 * by the <tt>SugiyamaLabel</tt> <tt>sugiyamaNode34</tt> and the constraint is a
 * mandatory constraint:<br>
 * <tt>VERTICAL_TWO_NODES_ABOVE_MANDATORY_sugiyamaNode34</tt>
 * 
 * @author Ferdinand H�bner
 */
public class VerticalConstraintWithTwoNodes extends
        SugiyamaConstraintWithTwoNodes {
    private final double EPSILON = 0.1;

    /** source is above target */
    private boolean isAbove;

    /** source is below target */
    private boolean isBelow;

    /** both nodes don't have the same y-coordinates */
    private boolean nonequalY;

    /**
     * This constructor is needed, to check if this constraint is assignable
     * from the interface <tt>SugiyamaConstraint</tt>.
     */
    public VerticalConstraintWithTwoNodes() {
        source = null;
        target = null;
        mandatory = false;
    }

    /**
     * Default constructor
     * 
     * @param s
     *            source-node
     * @param t
     *            target-node
     * @param m
     */
    public VerticalConstraintWithTwoNodes(Node s, Node t, boolean m) {
        source = s;
        target = t;
        mandatory = m;
    }

    /**
     * Check if the constraint is a constraint "source is above target"
     * 
     * @return Returns isAbove
     */
    public boolean isAbove() {
        return isAbove;
    }

    /**
     * Check if the constraint is a constraint "source is below target"
     * 
     * @return Returns isBelow
     */
    public boolean isBelow() {
        return isBelow;
    }

    /**
     * Check if the constraint is a constraint "both nodes have the same
     * y-coordinates"
     * 
     * @return isEqualY
     */
    public boolean isEqualY() {
        return (!isAbove && !isBelow);
    }

    /**
     * Check if the constraint is a constraint "the nodes have different
     * y-coodinates"
     * 
     * @return nonequalY
     */
    public boolean isNonequalY() {
        return nonequalY;
    }

    /**
     * Mark this constraint as "source is above target"
     */
    public void setIsAbove() {
        isAbove = true;
        isBelow = false;
        nonequalY = false;
    }

    /**
     * Mark this constraint as "source is below target"
     */
    public void setIsBelow() {
        isAbove = false;
        isBelow = true;
        nonequalY = false;
    }

    /**
     * Mark this constraint as "both nodes have the same y-coordinates"
     */
    public void setIsEqualY() {
        isAbove = false;
        isBelow = false;
        nonequalY = false;
    }

    /**
     * Mark this constraint as "both nodes have different y-coordinates"
     */
    public void setNonequalY() {
        isAbove = false;
        isBelow = false;
        nonequalY = true;
    }

    /**
     * Checks, if the constraint is satisfied
     */
    @Override
    public String check() {
        String sourceName;
        String targetName;

        if (source == null || target == null)
            return null;

        double source_y;
        double target_y;

        try {
            source_y = source.getDouble(GraphicAttributeConstants.COORDY_PATH);
            target_y = target.getDouble(GraphicAttributeConstants.COORDY_PATH);
        } catch (AttributeNotFoundException anfe) {
            return null;
        }
        sourceName = this.getName(source);
        targetName = this.getName(target);

        if (isAbove) {
            if (source_y < target_y)
                return null;
            else
                return "Node " + sourceName + " is not above Node "
                        + targetName;
        } else if (isBelow) {
            if (source_y > target_y)
                return null;
            else
                return "Node " + sourceName + " is not below Node "
                        + targetName;
        } else if (nonequalY) {
            if (Math.abs(source_y - target_y) < EPSILON)
                return "Both nodes have the same y-coordinates";
            else
                return null;
        } else if (Math.abs(source_y - target_y) < EPSILON)
            return null;
        else
            return "Node " + sourceName + "is not at same y-position as Node "
                    + targetName;

    }

    /**
     * This method returns a new <tt>VerticalConstraintWithTwoNodes</tt>, if the
     * passed <tt>String</tt> describes such a constraint.
     */
    public VerticalConstraintWithTwoNodes isConstraint(String s, Node source,
            SugiyamaData data) {
        // check for a VerticalConstraintWithTwoNodes
        if (!s.contains(SugiyamaConstants.CONSTRAINT_VERTICAL_TWO_NODES))
            return null;

        boolean above = false;
        boolean below = false;
        boolean equal = false;
        boolean nonequal = false;
        boolean mandatory = false;
        String targetNodeName = null;
        VerticalConstraintWithTwoNodes constraint;

        // build the constraint - check for the type of constraint
        if (s.contains(SugiyamaConstants.CONSTRAINT_ABOVE)) {
            above = true;
        } else if (s.contains(SugiyamaConstants.CONSTRAINT_BELOW)) {
            below = true;
        } else if (s.contains(SugiyamaConstants.CONSTRAINT_EQUAL_Y)) {
            equal = true;
        } else if (s.contains(SugiyamaConstants.CONSTRAINT_NONEQUAL_Y)) {
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

        // Node was not found, cannot build a constraint
        if (targetNodeName == null)
            return null;

        // Search the Node in the node-map
        if (!data.getNodeMap().containsKey(targetNodeName))
            return null;

        Node targetNode;
        targetNode = data.getNodeMap().get(targetNodeName);

        constraint = new VerticalConstraintWithTwoNodes(source, targetNode,
                mandatory);

        if (above) {
            constraint.setIsAbove();
        } else if (below) {
            constraint.setIsBelow();
        } else if (equal) {
            constraint.setIsEqualY();
        } else if (nonequal) {
            constraint.setNonequalY();
        } else
            return null;

        return constraint;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
