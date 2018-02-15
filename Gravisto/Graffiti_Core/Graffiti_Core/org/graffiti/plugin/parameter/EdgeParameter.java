// =============================================================================
//
//   EdgeParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

import org.graffiti.graph.Edge;

/**
 * This class contains a single <code>Edge</code>.
 * 
 * @version $Revision: 5767 $
 */
public class EdgeParameter extends AbstractSingleParameter<Edge> {

    /**
     * 
     */
    private static final long serialVersionUID = -4428078484293738661L;

    /**
     * Constructs a new edge parameter.
     * 
     * @param name
     *            the name of this parameter.
     * @param description
     *            the description of this parameter.
     */
    public EdgeParameter(String name, String description) {
        super(name, description);
    }

    /**
     * Constructs a new edge parameter.
     * 
     * @param edge
     *            the edge saved in the parameter
     * @param name
     *            the name of this parameter.
     * @param description
     *            the description of this parameter.
     */
    public EdgeParameter(Edge edge, String name, String description) {
        super(edge, name, description);
    }

    /**
     * Returns the <code>Edge</code> contained in this
     * <code>EdgeParameter</code>.
     * 
     * @return the <code>Edge</code> contained in this
     *         <code>EdgeParameter</code>.
     */
    public Edge getEdge() {
        return getValue();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
