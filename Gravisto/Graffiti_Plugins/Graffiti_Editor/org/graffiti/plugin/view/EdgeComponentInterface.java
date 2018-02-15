// =============================================================================
//
//   EdgeComponentInterface.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EdgeComponentInterface.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.view;

/**
 * This component represents a <code>org.graffiti.graph.Edge</code>.
 * 
 * @version $Revision: 5768 $
 */
public interface EdgeComponentInterface extends GraphElementComponentInterface {

    /**
     * Sets the source component.
     * 
     * @param snc
     *            the source component to be set.
     */
    public void setSourceComponent(NodeComponentInterface snc);

    /**
     * Sets the source component.
     * 
     * @param tnc
     *            the source component to be set.
     */
    public void setTargetComponent(NodeComponentInterface tnc);

    /**
     * Calls buildShape if no NodeShapes have changed.
     */
    public void updateShape();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
