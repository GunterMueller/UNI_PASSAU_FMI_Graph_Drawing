// =============================================================================
//
//   GraffitiComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraffitiComponent.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.gui;

import org.graffiti.editor.MainFrame;

/**
 * Interface for all GUIComponents used in the editor. Provides the
 * <code>getPreferredComponent()</code> method.
 * 
 * @version $Revision: 5768 $
 */
public interface GraffitiComponent {

    /**
     * Sets the mainframe. The component's action can then work on it.
     * 
     * @param mf
     *            see description.
     */
    public void setMainFrame(MainFrame mf);

    /**
     * Returns the id of the component this component should be placed in.
     * 
     * @return the id of the component this component should be placed in.
     */
    public String getPreferredComponent();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
