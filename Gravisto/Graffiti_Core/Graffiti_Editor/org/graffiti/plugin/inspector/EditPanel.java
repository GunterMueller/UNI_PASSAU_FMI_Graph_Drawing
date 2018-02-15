// =============================================================================
//
//   EditPanel.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EditPanel.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.inspector;

import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.undo.UndoableEditSupport;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.event.ListenerManager;
import org.graffiti.graph.GraphElement;

/**
 * Represents the edit panel in the inspector.
 * 
 * @version $Revision: 5768 $
 */
public abstract class EditPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 6370300057502630726L;

    /** a reference to the map between old and new graph elements */
    protected Map<GraphElement, GraphElement> geMap;

    /**
     * Reference to the UndoableEditSupport contained in the MainFrame and
     * needed for undo operations.
     */
    protected UndoableEditSupport undoSupport;

    /**
     * Sets the map of editcomponents to the given map.
     * 
     * @param map
     *            DOCUMENT ME!
     */
    public abstract void setEditComponentMap(Map<Class<?>, Class<?>> map);

    /**
     * Sets the ListenerManager.
     * 
     * @param lm
     *            DOCUMENT ME!
     */
    public abstract void setListenerManager(ListenerManager lm);

    /**
     * Builds the table that is used for editing attributes from scratch.
     * 
     * @param treeNode
     *            root attribute.
     * @param graphElements
     *            DOCUMENT ME!
     */
    public abstract void buildTable(DefaultMutableTreeNode treeNode,
            List<? extends Attributable> graphElements);

    /**
     * DOCUMENT ME!
     * 
     * @param attr
     *            DOCUMENT ME!
     */
    public abstract void updateTable(Attribute attr);

    /**
     * Sets a reference to the map between old and new graph elements needed by
     * edit panel for creation of undo edits.
     * 
     * @param geMap
     *            a reference to the map between old and new graph elements
     */
    public void setGraphElementMap(Map<GraphElement, GraphElement> geMap) {
        this.geMap = geMap;
    }

    /**
     * Sets <code>UndoableEditSupport</code> for undo operations inside of edit
     * panels.
     * 
     * @param undoSupport
     *            a <code>UndoableEditSupport</code> object contained in the
     *            MainFrame.
     */
    public void setUndoSupport(UndoableEditSupport undoSupport) {
        this.undoSupport = undoSupport;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
