// =============================================================================
//
//   InspectorTab.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: InspectorTab.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.inspector;

import javax.swing.JComponent;

import org.graffiti.event.AttributeListener;

/**
 * An <code>InspectorTab</code> is a generic component for an
 * <code>InspectorPlugin</code>.
 * 
 * @see JComponent
 * @see InspectorPlugin
 */
public abstract class InspectorTab extends JComponent implements
        AttributeListener {

    /**
     * 
     */
    private static final long serialVersionUID = 4641333512717983353L;

    /**
     * The panel that holds the table of the attributes and the buttons for
     * adding and removing attributes as well as the "apply" button.
     */
    protected EditPanel editPanel;

    /**
     * The title of the <code>InspectorTab</code> which will appear as the title
     * of the tab.
     */
    protected String title;

    /**
     * Returns the EditPanel of this tab.
     * 
     * @return DOCUMENT ME!
     */
    public EditPanel getEditPanel() {
        return this.editPanel;
    }

    /**
     * Returns the title of the current <code>InspectorTab</code>.
     * 
     * @return the title of the current <code>InspectorTab</code>.
     */
    public String getTitle() {
        return this.title;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
