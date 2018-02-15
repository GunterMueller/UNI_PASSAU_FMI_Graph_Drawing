//=============================================================================
//
//   AbstractTab.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: AbstractTab.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.graffiti.event.AttributeEvent;
import org.graffiti.event.TransactionEvent;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.inspector.InspectorTab;
import org.graffiti.selection.Selection;

/**
 * Represents an inner tab (i.e. graph/node/edge tab). Provides methods that are
 * called before or after changes (e.g. adding an attribute).
 */
public class AbstractTab extends InspectorTab {

    /**
     * 
     */
    private static final long serialVersionUID = -280765286469724L;

    /** The top panel containing the tree/semantic list. */
    private JPanel topPanel;

    /** The SplitPane containing the panes topScroll and editPanel. */
    private JSplitPane mainSplit;

    /** The type of the tab (e.g. ViewTab.NODE). */
    protected int type;

    /**
     * The path of the attribute of the currently selected node of this tab's
     * attribute tree.
     */
    private String currentSelectionPath;

    /**
     * Creates a new AbstractTab object. Builds the top (tree or semantic list)
     * and bottom (editPanel) panels.
     */
    public AbstractTab() {
        super();

        this.currentSelectionPath = "";

        // build panel
        topPanel = new JPanel();
        JScrollPane topScroll = new JScrollPane(topPanel);
        topScroll.getVerticalScrollBar().setUnitIncrement(10);

        editPanel = new DefaultEditPanel();
        ((DefaultEditPanel) editPanel).reset();
        mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topScroll,
                editPanel);
        mainSplit.setDividerSize(10);
        mainSplit.setDividerLocation(200);
        mainSplit.setOneTouchExpandable(true);
        this.setLayout(new BorderLayout());
        this.add(mainSplit, BorderLayout.CENTER);

    }

    /**
     * Returns the topPanel.
     * 
     * @return the topPanel
     */
    public JPanel getTopPanel() {
        return this.topPanel;
    }

    /**
     * Returns a copy of this AbstractTab.
     */
    @Override
    public Object clone() {
        return new AbstractTab();
    }

    /**
     * Called after an attribute has been added. Sets the currentSelectionPath
     * to the new attribute and rebuilds the topPane.
     * 
     * @param event
     *            the event describing the new attribute
     */
    public void postAttributeAdded(AttributeEvent event) {
        ViewTab parent = (ViewTab) this.getParent();
        if (parent instanceof TreeView) {
            String path = event.getPath();
            if (path == null) {
                path = event.getAttribute().getPath();
            }
            this.currentSelectionPath = path;
        }
        // parent.rebuildTopPane(this.type, new Selection());
        parent.buildTopPane(this.type, new Selection());
    }

    /**
     * Called after an attribute has been changed. Updates the VEC belonging to
     * the changed attribute.
     * 
     * @param event
     *            the event describing the changed attribute
     */
    public void postAttributeChanged(AttributeEvent event) {
        this.editPanel.updateTable(event.getAttribute());
        ViewTab parent = (ViewTab) this.getParent();
        if (parent instanceof SemanticView) {
            SemanticGroup group = ((SemanticView) parent).getCurrentGroup();
            if (group instanceof GroupEdgeLabel
                    || group instanceof GroupNodeLabel) {
                // update the label-list
                parent.rebuildTopPane(this.type, new Selection());
            }
        }
    }

    /**
     * Called after an attribute has been removed. Sets the currentSelectionPath
     * to the parent of the removed attribute.
     * 
     * @param event
     *            the event describing the removed attribute
     */
    public void postAttributeRemoved(AttributeEvent event) {
        ViewTab parent = (ViewTab) this.getParent();
        if (parent instanceof TreeView) {
            String path = event.getPath();
            if (path == null) {
                path = event.getAttribute().getPath();
            }
            this.currentSelectionPath = path;
        }
        parent.rebuildTopPane(this.type, new Selection());
    }

    /**
     * Called before an attribute has been added. Does nothing.
     * 
     * @param event
     *            the event describing the new attribute
     */
    public void preAttributeAdded(AttributeEvent event) {
    }

    /**
     * Called before an attribute has been changed. Does nothing.
     * 
     * @param event
     *            the event describing the changed attribute
     */
    public void preAttributeChanged(AttributeEvent event) {
    }

    /**
     * Called before an attribute has been removed. Does nothing.
     * 
     * @param event
     *            the event describing the removed attribute
     */
    public void preAttributeRemoved(AttributeEvent event) {
    }

    /**
     * Called after a transaction is finished. If the event was fired by
     * DefaultEditPanel or TreeView, the topPane will be rebuilt.
     * 
     * @param event
     *            the event describing the transaction
     */
    public void transactionFinished(TransactionEvent event) {
        Object source = event.getSource();

        // just update if source is the defaultEditPanel or the treePanel.
        // this prevents a slowdown due to the big number of events fired
        if (source instanceof DefaultEditPanel
                || source instanceof TreeView
                // TODO: A.G.: really bad temporary hack as changes to the grid
                // class are wrapped into transactions.
                || source instanceof GridAttribute
                && this.type == ViewTab.GRAPH
                // algorithms may also change attributes
                || source instanceof Algorithm) {
            ViewTab parent = (ViewTab) this.getParent();
            parent.rebuildTopPane(this.type, new Selection());
        }
        validate();
    }

    /**
     * Called when a transaction strats.Does nothing.
     * 
     * @param event
     *            the event describing the transaction
     */
    public void transactionStarted(TransactionEvent event) {
    }

    /**
     * Returns this currentSelectionPath.
     * 
     * @return the path of the attribute of the selected tree node
     */
    public String getCurrentSelectionPath() {
        return this.currentSelectionPath;
    }

    /**
     * Sets this currentSelectionPath.
     * 
     * @param newPath
     *            the new attribute path
     */
    public void setCurrentSelectionPath(String newPath) {
        this.currentSelectionPath = newPath;
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
