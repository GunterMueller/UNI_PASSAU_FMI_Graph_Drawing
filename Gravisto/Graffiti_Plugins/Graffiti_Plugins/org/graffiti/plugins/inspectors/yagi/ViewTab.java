//=============================================================================
//
//   ViewTab.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: ViewTab.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JTabbedPane;
import javax.swing.undo.UndoableEditSupport;

import org.graffiti.attributes.Attributable;
import org.graffiti.event.ListenerManager;
import org.graffiti.event.ListenerNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugin.inspector.EditPanel;
import org.graffiti.plugin.inspector.InspectorTab;
import org.graffiti.selection.Selection;

/**
 * The super-class of the outer tabs (i.e. semanticView and treeView). Provides
 * basic methods for inner tabs (i.e. graph/node/edge tabs).
 */
public abstract class ViewTab extends JTabbedPane {

    /**
     * 
     */
    private static final long serialVersionUID = 6044639882405286345L;

    /**
     * The list containing the currently selected attributables of the selected
     * tab. Thus they are all from the same type (e.g. nodes).
     */
    protected List<? extends Attributable> attributables;

    /** The currently selected nodes. */
    protected static List<Node> currentNodes;

    /** The currently selected edges. */
    protected static List<Edge> currentEdges;

    /** The current graph. */
    protected static List<Graph> currentGraph;

    /** Constant representing a node. */
    public final static int NODE = 0;

    /** Constant representing an edge. */
    public final static int EDGE = 1;

    /** Constant representing a graph. */
    public final static int GRAPH = 2;

    /** The paths to all node attributes that are present by default. */
    protected static HashSet<String> defaultNodePaths;

    /** The paths to all edge attributes that are present by default. */
    protected static HashSet<String> defaultEdgePaths;

    /** The paths to all graph attributes that are present by default. */
    protected static HashSet<String> defaultGraphPaths;

    /**
     * The tab that is currently registered as an AttributeListener and thus
     * will receive attribute events.
     */
    private static AbstractTab listenerOwner;

    /** The edge tab of this view tab. */
    protected EdgeTab edgeTab;

    /** The node tab of this view tab. */
    protected NodeTab nodeTab;

    /** The graph tab of this view tab. */
    protected GraphTab graphTab;

    /**
     * Constructs a new ViewTab.
     */
    public ViewTab() {
        super();
    }

    /**
     * Fills the topPanel of the GraphTab with content.
     * 
     * @param graph
     *            the graph of the current session
     */
    public void buildTopPane(Graph graph) {
        currentGraph = new LinkedList<Graph>();
        currentGraph.add(graph);
        this.buildTopPane(ViewTab.GRAPH, new Selection());
    }

    /**
     * Fills the topPanel of an AbstractTab with content (e.g. the attribute
     * tree).
     * 
     * @param type
     *            the type of the current selected tab (e.g. NODE)
     * @param sel
     *            the current selection
     */
    public abstract void buildTopPane(int type, Selection sel);

    /**
     * Rebuilds the topPane.
     * 
     * @param type
     *            the type of the current selected tab (e.g. NODE)
     * @param sel
     *            the current selection
     */
    public abstract void rebuildTopPane(int type, Selection sel);

    /**
     * Adds a new tab (e.g. a nodeTab) to the panel.
     * 
     * @param tab
     *            the new tab
     */
    public void addTab(InspectorTab tab) {

        if (tab instanceof EdgeTab) {
            this.edgeTab = (EdgeTab) tab;
        } else if (tab instanceof NodeTab) {
            this.nodeTab = (NodeTab) tab;
        } else if (tab instanceof GraphTab) {
            this.graphTab = (GraphTab) tab;
        } else
            return;

        this.add(tab.getTitle(), tab);
    }

    /**
     * Removes a tab from the panel.
     * 
     * @param type
     *            the type of the tab to remove
     */
    public void removeTab(int type) {
        if (type == EDGE) {
            this.remove(edgeTab);
            if (this.edgeTab == listenerOwner) {
                listenerOwner = null;
            }
            this.edgeTab = null;
        } else if (type == NODE) {
            this.remove(nodeTab);
            if (this.nodeTab == listenerOwner) {
                listenerOwner = null;
            }
            this.nodeTab = null;
        } else if (type == GRAPH) {
            this.remove(graphTab);
            if (this.graphTab == listenerOwner) {
                listenerOwner = null;
            }
            this.graphTab = null;
        }
    }

    /**
     * Returns the tabbedPane of this SemanticView.
     * 
     * @return this tabbedPane
     */
    public JTabbedPane getTabbedPane() {
        return this;
    }

    /**
     * Removes the attributeListener of the current listenerOwner.
     * 
     * @param lm
     *            the listenerManager from which the listenerOwner will be
     *            deregistered
     */
    public static void removeAttributeListener(ListenerManager lm) {
        try {
            if (listenerOwner != null) {
                lm.removeAttributeListener(listenerOwner);
                listenerOwner = null;
            }
        } catch (ListenerNotFoundException e) {
            // should not happen
        }
    }

    /**
     * Adds a strictAttributeListener to a tab. Sets the listenerOwner to this
     * tab.
     * 
     * @param lm
     *            the listenerManager
     * @param type
     *            the type of the tab that will be registered
     */
    public void addAttributeListener(ListenerManager lm, int type) {
        try {
            if (listenerOwner != null) {
                lm.removeAttributeListener(listenerOwner);
            }
        } catch (ListenerNotFoundException e) {
            // should not happen
            System.err.println("ViewTab.addAttributeListener(): " + e);
        }
        if (type == EDGE) {
            lm.addStrictAttributeListener(this.edgeTab);
            listenerOwner = this.edgeTab;
        } else if (type == NODE) {
            lm.addStrictAttributeListener(this.nodeTab);
            listenerOwner = this.nodeTab;
        } else if (type == GRAPH) {
            lm.addStrictAttributeListener(this.graphTab);
            listenerOwner = this.graphTab;
        }
    }

    /**
     * Initializes a tab by setting the listenerManager, the valueEditComponents
     * and the undoSupport in the corresponding editPanel.
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
     * @param undoSupport
     *            the support class for undo/redo
     */
    public void init(ListenerManager lm, int type,
            Map<GraphElement, GraphElement> graphElements,
            Map<Class<?>, Class<?>> valueEditComponents,
            UndoableEditSupport undoSupport) {

        EditPanel editPanel;
        if (type == EDGE) {
            editPanel = this.edgeTab.getEditPanel();
        } else if (type == NODE) {
            editPanel = this.nodeTab.getEditPanel();
        } else if (type == GRAPH) {
            editPanel = this.graphTab.getEditPanel();
        } else
            return;

        editPanel.setListenerManager(lm);
        editPanel.setGraphElementMap(graphElements);
        editPanel.setEditComponentMap(valueEditComponents);
        editPanel.setUndoSupport(undoSupport);
    }

    /**
     * Selects the tab with type <code>type</code>.
     * 
     * @param type
     *            the type of the tab: <code>ViewTab.NODE</code>,
     *            <code>ViewTab.EDGE</code> or <code>ViewTab.GRAPH</code>
     */
    public void setSelected(int type) {
        if (type == EDGE) {
            this.setSelectedComponent(this.edgeTab);
        } else if (type == NODE) {
            this.setSelectedComponent(this.nodeTab);
        } else if (type == GRAPH) {
            this.setSelectedComponent(this.graphTab);
        }
    }

    /**
     * Returns the integer constant belonging to the currently selected tab.
     * 
     * @return <code>EDGE</code> if the edge tab is selected, <code>NODE</code>
     *         if the node tab is selected, <code>GRAPH</code> if the graph tab
     *         is selected, -1 if no tab is selected
     */
    public int getSelected() {
        AbstractTab selected = (AbstractTab) this.getSelectedComponent();
        if (selected == null)
            return -1;
        if (selected == this.edgeTab)
            return EDGE;
        else if (selected == this.nodeTab)
            return NODE;
        else if (selected == this.graphTab)
            return GRAPH;
        else
            return -1;
    }

    /**
     * Enables or disables a tab.
     * 
     * @param type
     *            the type of the tab: <code>ViewTab.NODE</code>,
     *            <code>ViewTab.EDGE</code> or <code>ViewTab.GRAPH</code>
     * @param enable
     *            <code>true</code> to enable the tab, <code>false</code> to
     *            disable it
     */
    public void setEnabledTab(int type, boolean enable) {

        if (type == EDGE) {
            this.setEnabledAt(this.indexOfComponent(edgeTab), enable);
        } else if (type == NODE) {
            this.setEnabledAt(this.indexOfComponent(nodeTab), enable);
        } else if (type == GRAPH) {
            this.setEnabledAt(this.indexOfComponent(graphTab), enable);
        }
    }

    /**
     * Returns this edgeTab.
     * 
     * @return the edgeTab of this ViewTab
     */
    public EdgeTab getEdgeTab() {
        return this.edgeTab;
    }

    /**
     * Returns this nodeTab.
     * 
     * @return the nodeTab of this ViewTab
     */
    public NodeTab getNodeTab() {
        return this.nodeTab;
    }

    /**
     * Returns this graphTab.
     * 
     * @return the graphTab of this ViewTab
     */
    public GraphTab getGraphTab() {
        return this.graphTab;
    }

    /**
     * Returns the default edge paths.
     * 
     * @return the default edge paths
     */
    public static HashSet<String> getDefaultEdgePaths() {
        return defaultEdgePaths;
    }

    /**
     * Returns the default node paths.
     * 
     * @return the default node paths
     */
    public static HashSet<String> getDefaultNodePaths() {
        return defaultNodePaths;
    }

    /**
     * Returns the default graph paths.
     * 
     * @return the default graph paths
     */
    public static HashSet<String> getDefaultGraphPaths() {
        return defaultGraphPaths;
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
