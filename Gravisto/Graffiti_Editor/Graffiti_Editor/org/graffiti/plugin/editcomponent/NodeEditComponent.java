// =============================================================================
//
//   NodeEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeEditComponent.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.editcomponent;

import javax.swing.JComponent;

import org.graffiti.plugin.Displayable;

/**
 * This class provides an edit component for selecting a single node.
 * 
 * @see org.graffiti.graph.Node
 * @see SingleGraphElementEditComponent
 */
public class NodeEditComponent extends SingleGraphElementEditComponent {

    /**
     * Constructs a new <code>NodeEditComponent</code>.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public NodeEditComponent(Displayable<?> disp) {
        super(disp);
    }

    /**
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#getComponent()
     */
    @Override
    public JComponent getComponent() {
        return super.getComponent();
    }

    /*
     * @see
     * org.graffiti.plugin.editcomponent.AbstractValueEditComponent#setDisplayable
     * (org.graffiti.plugin.Displayable)
     */
    @Override
    public void setDisplayable(Displayable<?> attr) {
    }

    /**
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#setValue()
     */
    @Override
    protected void setDispValue() {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
