//=============================================================================
//
//   InspectorContainer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: InspectorContainer.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.undo.UndoableEditSupport;

import org.graffiti.editor.MainFrame;
import org.graffiti.event.ListenerManager;
import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.gui.GraffitiContainer;
import org.graffiti.plugin.inspector.InspectorTab;

/**
 * The root panel of the inspector. Containes the tabs for the semantic and tree
 * views and deligates actions concerning node/edge/graph-tabs to these view
 * tabs.
 */
public class InspectorContainer extends JPanel implements GraffitiContainer {

    /**
     * 
     */
    private static final long serialVersionUID = 4738366683477562532L;

    /** The panel containing the treeView. */
    private TreeView treePanel;

    /** The panel containing the semanticView. */
    private SemanticView semanticPanel;

    /** The main frame of the editor. */
    private MainFrame mainFrame;

    /** The id of this GraffitiContainer. */
    private String id = "inspector";

    /** The title of this GraffitiContainer. */
    private String title = "Inspector";

    /** The id of the component this component wants to be added to. */
    private String preferredComponent = "pluginPanel";

    /**
     * Creates a new InspectorContainer and builds the tabbedPanes containing
     * the treeView/semanticView.
     */
    public InspectorContainer() {

        this.treePanel = new TreeView();
        this.semanticPanel = new SemanticView();

        this.setLayout(new BorderLayout());

        this.add(this.semanticPanel);
    }

    /**
     * Returns the id of the inspector.
     * 
     * @return the inspector's id.
     * @see org.graffiti.plugin.gui.GraffitiContainer#getId()
     */
    public String getId() {
        return id;
    }

    /**
     * Sets mainFrame to the given value.
     * 
     * @param newMainFrame
     *            the new mainFrame
     * @see org.graffiti.plugin.gui.GraffitiComponent
     *      #setMainFrame(org.graffiti.editor.MainFrame)
     */
    public void setMainFrame(MainFrame newMainFrame) {
        mainFrame = newMainFrame;
    }

    /**
     * Returns the preferredComponent of the inspector.
     * 
     * @return the inspector's id.
     * @see org.graffiti.plugin.gui.GraffitiComponent#getPreferredComponent()
     */
    public String getPreferredComponent() {
        return preferredComponent;
    }

    /**
     * Returns the title of the inspector.
     * 
     * @return the inspector's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Adds a new tab (e.g. a NodeTab) to the Inspector. The tab will be added
     * to both the treeView and the semanticView.
     * 
     * @param tab
     *            the new tab
     */
    public void addTab(AbstractTab tab) {
        semanticPanel.addTab(tab);
        treePanel.addTab((AbstractTab) tab.clone());
    }

    /**
     * Removes a tab from the inspector. The tab with type <code>type</code>
     * will be removed from the treeView and the semanticView.
     * 
     * @param type
     *            the type (i.e.: ViewTab.NODE, ViewTab.EDGE, ViewTab.GRAPH) of
     *            the tab to remove
     */
    public void removeTab(int type) {
        treePanel.removeTab(type);
        semanticPanel.removeTab(type);
    }

    /**
     * Repaints the inspector and resets its size.
     * 
     * @param g
     *            the Graphics object to protect
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        setPreferredSize(getParent().getSize());
        revalidate();
    }

    /**
     * Enables or disables the tab with type <code>type</code> in both the
     * semanticView and the treeView.
     * 
     * @param type
     *            the type (i.e.: ViewTab.NODE, ViewTab.EDGE, ViewTab.GRAPH) of
     *            the tab
     * @param enable
     *            <code>true</code> to enable the tab, <code>false</code> to
     *            disable it.
     */
    public void setEnabledTab(int type, boolean enable) {
        this.semanticPanel.setEnabledTab(type, enable);
        this.treePanel.setEnabledTab(type, enable);
    }

    /**
     * Removes the attribute listener of the tab that currently is its owner.
     * 
     * @param lm
     *            the listener manager where the attribute listener will be
     *            removed from
     */
    public void removeAttributeListener(ListenerManager lm) {
        ViewTab.removeAttributeListener(lm);
    }

    /**
     * Initializes a tab in both the semanticView and the treeView by setting
     * the listenerManager, the valueEditComponents and the undoSupport in the
     * corresponding editPanel.
     * 
     * @param lm
     *            the listenerManager
     * @param type
     *            the type of the tab: <code>ViewTab.NODE</code>,
     *            <code>ViewTab.EDGE</code> or <code>ViewTab.GRAPH</code>
     * @param graphElements
     *            the mapping of old and new graphElements (needed for undo
     *            support)
     * @param valueEditComponents
     *            the editComponentsMap of the tab
     */
    public void init(ListenerManager lm, int type,
            Map<GraphElement, GraphElement> graphElements,
            Map<Class<?>, Class<?>> valueEditComponents) {
        UndoableEditSupport undoSupport = mainFrame.getUndoSupport();
        this.semanticPanel.init(lm, type, graphElements, valueEditComponents,
                undoSupport);
        this.treePanel.init(lm, type, graphElements, valueEditComponents,
                undoSupport);
    }

    /**
     * Selects the tab with type <code>type</code> in both the semanticView and
     * the treeView.
     * 
     * @param type
     *            the type (i.e.: ViewTab.NODE, ViewTab.EDGE, ViewTab.GRAPH) of
     *            the tab
     */
    public void setSelected(int type) {
        this.semanticPanel.setSelected(type);
        this.treePanel.setSelected(type);
    }

    /**
     * Returns an array with all tabs of both the semanticView and the treeView.
     * 
     * @return an array containing the node/graph/edge tabs
     */
    public InspectorTab[] getTabs() {
        return new InspectorTab[] { semanticPanel.getEdgeTab(),
                semanticPanel.getNodeTab(), semanticPanel.getGraphTab(),
                treePanel.getEdgeTab(), treePanel.getNodeTab(),
                treePanel.getGraphTab(), };
    }

    /**
     * Returns this container's tree panel.
     * 
     * @return the tree panel
     */
    public TreeView getTreePanel() {
        return this.treePanel;
    }

    /**
     * Returns this container's semantic panel.
     * 
     * @return the semantic panel
     */
    public SemanticView getSemanticPanel() {
        return this.semanticPanel;
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
