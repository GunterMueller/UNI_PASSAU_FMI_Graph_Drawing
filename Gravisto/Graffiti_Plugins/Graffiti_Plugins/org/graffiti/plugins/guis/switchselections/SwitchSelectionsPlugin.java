// =============================================================================
//
//   SwitchSelectionsPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SwitchSelectionsPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.switchselections;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.selection.SelectionEvent;
import org.graffiti.selection.SelectionListener;

/**
 * Provides a spring embedder algorithm a la KK.
 * 
 * @version $Revision: 5766 $
 */
public class SwitchSelectionsPlugin extends EditorPluginAdapter implements
        SelectionListener {

    /** DOCUMENT ME! */
    SelectionMenu selMenu = new SelectionMenu();

    /**
     * Creates a new TrivialGridRestrictedPlugin object.
     */
    public SwitchSelectionsPlugin() {
        this.guiComponents = new GraffitiComponent[1];
        this.guiComponents[0] = selMenu;
    }

    /**
     * @see org.graffiti.plugin.GenericPluginAdapter#isSelectionListener()
     */
    @Override
    public boolean isSelectionListener() {
        return true;
    }

    /**
     * @see org.graffiti.selection.SelectionListener#selectionChanged(org.graffiti.selection.SelectionEvent)
     */
    public void selectionChanged(SelectionEvent e) {
        ((SelectionListener) selMenu).selectionChanged(e);
    }

    /**
     * @see org.graffiti.selection.SelectionListener#selectionListChanged(org.graffiti.selection.SelectionEvent)
     */
    public void selectionListChanged(SelectionEvent e) {
        ((SelectionListener) selMenu).selectionListChanged(e);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
