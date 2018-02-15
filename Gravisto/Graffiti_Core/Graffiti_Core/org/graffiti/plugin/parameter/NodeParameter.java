// =============================================================================
//
//   NodeParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

import org.graffiti.graph.Node;

/**
 * This class contains a single <code>Node</code>.
 * 
 * @version $Revision: 5767 $
 */
public class NodeParameter extends AbstractSingleParameter<Node> {

    /**
     * 
     */
    private static final long serialVersionUID = 3118264639591131106L;

    /**
     * Constructs a new node parameter.
     * 
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     */
    public NodeParameter(String name, String description) {
        super(name, description);
    }

    /**
     * Constructs a new node parameter.
     * 
     * @param name
     *            the name of the parameter.
     * @param description
     *            the description of the parameter.
     */
    public NodeParameter(Node node, String name, String description) {
        super(node, name, description);
    }

    /**
     * Returns the <code>Node</code> contained in this
     * <code>NodeParameter</code>.
     * 
     * @return the <code>Node</code> contained in this
     *         <code>NodeParameter</code>.
     */
    public Node getNode() {
        return getValue();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
