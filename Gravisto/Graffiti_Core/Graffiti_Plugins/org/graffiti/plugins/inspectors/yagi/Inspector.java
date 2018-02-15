//=============================================================================
//
//   Inspector.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: Inspector.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.inspectors.yagi;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.event.ListenerManager;
import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.editcomponent.NeedEditComponents;
import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.plugin.inspector.InspectorPlugin;
import org.graffiti.plugin.inspector.InspectorTab;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionEvent;
import org.graffiti.selection.SelectionListener;
import org.graffiti.selection.SelectionModel;
import org.graffiti.session.EditorSession;
import org.graffiti.session.Session;
import org.graffiti.session.SessionListener;

/**
 * Represents the main class of the inspector plugin.
 * 
 */
public class Inspector extends EditorPluginAdapter implements InspectorPlugin,
        SessionListener, SelectionListener, NeedEditComponents, ChangeListener,
        ItemListener {
    /** States if there is an edgeTab in the inspector. */
    private boolean edgeTabActive = false;

    /** States if there is a nodeTab in the inspector. */
    private boolean nodeTabActive = false;

    /** States if there is a graphTab in the inspector. */
    private boolean graphTabActive = false;

    /** The current selection. */
    private Selection currentSelection;

    /**
     * States if <code>stateChanged(javax.swing.event.ChangeEvent)</code> should
     * update (i.e. rebuild) the editPanel or not.
     */
    private boolean doUpdate;

    /** This inspector's container. */
    private InspectorContainer container;

    /** The listener manager of this inspector. */
    private ListenerManager listenerManager;

    /** The current session. */
    private Session activeSession;

    /** The selectionModel of the last active session. */
    private SelectionModel oldSelectionModel;

    /** The menu for switching between basic and expert mode. */
    private YagiMenu menu;

    /**
     * Constructs a new inspector.
     */
    public Inspector() {
        super();

        this.doUpdate = true;
        this.container = new InspectorContainer();
        this.menu = new YagiMenu(this);
        this.menu.setEnabled(false);
        // the container should be made visible, if the
        // session changed. See the sessionChanged method for details.
        this.container.setVisible(false);
        this.guiComponents = new GraffitiComponent[2];
        this.guiComponents[0] = this.container;
        this.guiComponents[1] = this.menu;
    }

    /**
     * Returns an array containing all the <code>InspectorTab</code>s of the
     * <code>InspectorPlugin</code>.
     * 
     * @return an array containing all the <code>InspectorTab</code>s of the
     *         <code>InspectorPlugin</code>.
     */
    public InspectorTab[] getTabs() {
        return this.container.getTabs();
    }

    /**
     * Adds another <code>InspectorTab</code> to the current
     * <code>InspectorPlugin</code>.
     * 
     * @param tab
     *            the <code>InspectorTab</code> to be added to the
     *            <code>InspectorPlugin</code>.
     */
    public void addTab(InspectorTab tab) {
        EditorSession editorSession;
        try {
            editorSession = (EditorSession) activeSession;
        } catch (ClassCastException cce) {
            throw new RuntimeException("Should not happen " + cce);
        }

        // prevent inspector from rebuilding the topPane
        this.doUpdate = false;
        this.container.addTab((AbstractTab) tab);

        // initialise new tab
        if (tab instanceof EdgeTab) {
            this.edgeTabActive = true;
            this.container.init(listenerManager, ViewTab.EDGE, editorSession
                    .getGraphElementsMap(), valueEditComponents);
        } else if (tab instanceof NodeTab) {
            this.nodeTabActive = true;
            this.container.init(listenerManager, ViewTab.NODE, editorSession
                    .getGraphElementsMap(), valueEditComponents);
        } else if (tab instanceof GraphTab) {
            this.graphTabActive = true;
            this.container.init(listenerManager, ViewTab.GRAPH, editorSession
                    .getGraphElementsMap(), valueEditComponents);
        }
        this.doUpdate = true;
    }

    /**
     * Removes a tab from the inspector.
     * 
     * @param type
     *            the type of the tab to be removed
     */
    public void removeTab(int type) {
        if (type == ViewTab.EDGE) {
            this.edgeTabActive = false;
        } else if (type == ViewTab.NODE) {
            this.nodeTabActive = false;
        } else {
            this.graphTabActive = false;
        }

        // prevent inspector from rebuilding the topPane
        this.doUpdate = false;
        this.container.removeAttributeListener(listenerManager);
        this.container.removeTab(type);
        this.doUpdate = true;
    }

    /**
     * This method is called when the session changes. If there is an active
     * session, the current tabs will be registered to it. If not, the inspector
     * will be removed.
     * 
     * @param s
     *            the new Session.
     */
    public void sessionChanged(Session s) {
        // there is an active session
        if (s != null) {
            EditorSession editorSession;

            try {
                editorSession = (EditorSession) s;
            } catch (ClassCastException cce) {
                throw new RuntimeException("Should not happen " + cce);
            }

            // get the session's listenerManager
            if (this.listenerManager != null) {
                this.container.removeAttributeListener(listenerManager);
            }
            this.listenerManager = s.getGraph().getListenerManager();

            // register tabs
            if (this.graphTabActive) {
                this.container.init(listenerManager, ViewTab.GRAPH,
                        editorSession.getGraphElementsMap(),
                        valueEditComponents);

                // update graph tab
                this.container.getTreePanel().buildTopPane(
                        editorSession.getGraph());
                this.container.getSemanticPanel().buildTopPane(
                        editorSession.getGraph());
            }

            if (this.nodeTabActive) {
                this.container.init(listenerManager, ViewTab.NODE,
                        editorSession.getGraphElementsMap(),
                        valueEditComponents);
            }

            if (this.edgeTabActive) {
                this.container.init(listenerManager, ViewTab.EDGE,
                        editorSession.getGraphElementsMap(),
                        valueEditComponents);
            }

            this.activeSession = s;
            if (this.oldSelectionModel != null) {
                this.oldSelectionModel.removeSelectionListener(this);
            }
            SelectionModel model = null;

            try {
                model = ((EditorSession) s).getSelectionModel();

                // should never be null....
                if (model != null) {
                    model.addSelectionListener(this);
                    this.oldSelectionModel = model;
                }
            } catch (ClassCastException cce) {
                throw new RuntimeException("Should not happen " + cce);
            }

            Selection actSel = model.getActiveSelection();

            // call selectionChanged() to update the topPane
            if (actSel == null) {
                this.selectionChanged(new SelectionEvent(new Selection()));
            } else {
                this.selectionChanged(new SelectionEvent(actSel));
            }

            // add ChangeListeners to listen to selection changes of the tabs
            this.container.getTreePanel().addChangeListener(this);
            this.container.getSemanticPanel().addChangeListener(this);

            container.setVisible(true);
            this.menu.setEnabled(true);
        } else {
            // there is no active session anymore
            // -> remove active tabs and make the inspector invisible
            if (this.graphTabActive) {
                removeTab(ViewTab.GRAPH);
            }
            if (this.nodeTabActive) {
                removeTab(ViewTab.NODE);
            }
            if (this.edgeTabActive) {
                removeTab(ViewTab.EDGE);
            }
            container.setVisible(false);
            this.menu.setEnabled(false);
            this.currentSelection = null;
        }
    }

    /**
     * This method is called when the session data (but not the session's graph
     * data) changed. Actually, it does not do anything.
     * 
     * @param s
     *            the session
     */
    public void sessionDataChanged(Session s) {
    }

    /**
     * Is called, if something in the selection model changed. Depending on the
     * selected attributables it will add, select, enable or disable
     * graph/node/edge tabs. If a new tab is added, the topPane of both the
     * semantic and tree view will be built. The currently displayed tab will be
     * rebuilt, if it is not a new one.
     * 
     * @param e
     *            the selectionModel
     */
    public void selectionChanged(SelectionEvent e) {
        if (this.listenerManager == null)
            return;

        Selection selection = e.getSelection();

        if ((this.currentSelection != null)
                && selection.getElements().equals(
                        this.currentSelection.getElements())
                && !selection.isEmpty())
            // skip updating as nothing changed.
            // this prevents expensive updating if an attributable was
            // pointed (this also fires a selectionEvent) but not selected
            return;
        try {
            this.currentSelection = (Selection) selection.clone();
        } catch (CloneNotSupportedException cnse) {
            // this never ever happens!
        }

        // the currently selected ViewTab (i.e. SemanticView or TreeView)
        ViewTab selected = (ViewTab) container.getComponent(0);

        if (!this.currentSelection.getEdges().isEmpty()) {
            // selection containes edge(s)
            if (!this.edgeTabActive) {
                // add new edgeTabs and initialize their top panels
                EdgeTab newEdgeTab = new EdgeTab();
                addTab(newEdgeTab);
                if (this.currentSelection.getNodes().isEmpty()
                        && GraffitiSingleton.getInstance()
                                .isEditorFrameSelected()) {
                    // select edgeTab
                    this.doUpdate = false;
                    this.container.setSelected(ViewTab.EDGE);
                    this.doUpdate = true;
                    // register an attribute listener to the edge tab
                    selected.addAttributeListener(this.listenerManager,
                            ViewTab.EDGE);
                }
                this.container.getTreePanel().buildTopPane(ViewTab.EDGE,
                        this.currentSelection);
                this.container.getSemanticPanel().buildTopPane(ViewTab.EDGE,
                        this.currentSelection);
            } else {
                // enable edgeTab
                this.container.setEnabledTab(ViewTab.EDGE, true);
                if (this.currentSelection.getNodes().isEmpty()
                        && GraffitiSingleton.getInstance()
                                .isEditorFrameSelected()) {
                    // select edgeTab and rebuild topPane
                    this.doUpdate = false;
                    this.container.setSelected(ViewTab.EDGE);
                    this.doUpdate = true;
                    selected.addAttributeListener(this.listenerManager,
                            ViewTab.EDGE);
                    selected
                            .rebuildTopPane(ViewTab.EDGE, this.currentSelection);
                }
            }

        } else if (edgeTabActive) {
            // disable edgeTab
            this.container.setEnabledTab(ViewTab.EDGE, false);
        }

        if (!this.currentSelection.getNodes().isEmpty()) {
            // selection containes nodes
            if (!this.nodeTabActive) {
                // add new nodeTabs and initialize their top panels
                NodeTab newNodeTab = new NodeTab();
                addTab(newNodeTab);
                if (GraffitiSingleton.getInstance().isEditorFrameSelected()) {
                    // select nodeTab
                    this.doUpdate = false;
                    this.container.setSelected(ViewTab.NODE);
                    this.doUpdate = true;
                    // register an attribute listener to the node tab
                    selected.addAttributeListener(this.listenerManager,
                            ViewTab.NODE);
                }
                this.container.getTreePanel().buildTopPane(ViewTab.NODE,
                        this.currentSelection);
                this.container.getSemanticPanel().buildTopPane(ViewTab.NODE,
                        this.currentSelection);
            } else {
                // enable nodeTab
                this.container.setEnabledTab(ViewTab.NODE, true);
                if (GraffitiSingleton.getInstance().isEditorFrameSelected()) {
                    // select nodeTab and rebuild topPane
                    this.doUpdate = false;
                    this.container.setSelected(ViewTab.NODE);
                    this.doUpdate = true;
                    selected.addAttributeListener(this.listenerManager,
                            ViewTab.NODE);
                    selected
                            .rebuildTopPane(ViewTab.NODE, this.currentSelection);
                }
            }

        } else if (nodeTabActive) {
            // disable nodeTab
            this.container.setEnabledTab(ViewTab.NODE, false);
        }

        if (this.currentSelection.getNodes().isEmpty()
                && this.currentSelection.getEdges().isEmpty()) {
            // if no nodes/edges are selected, show graphTab
            if (!this.graphTabActive) {
                // add new graphTabs and initialize their top panels
                GraphTab newGraphTab = new GraphTab();
                addTab(newGraphTab);
                this.container.getTreePanel().buildTopPane(
                        activeSession.getGraph());
                this.container.getSemanticPanel().buildTopPane(
                        activeSession.getGraph());
                if (GraffitiSingleton.getInstance().isEditorFrameSelected()) {
                    // select graphTab
                    this.doUpdate = false;
                    this.container.setSelected(ViewTab.GRAPH);
                    this.doUpdate = true;
                    // register an attribute listener to the graph tab
                    selected.addAttributeListener(this.listenerManager,
                            ViewTab.GRAPH);
                }
            } else /*
                    * if
                    * (GraffitiSingleton.getInstance().isEditorFrameSelected())
                    */
            {
                // select graphTab and rebuild topPane
                this.doUpdate = false;
                this.container.setSelected(ViewTab.GRAPH);
                this.doUpdate = true;
                selected.addAttributeListener(this.listenerManager,
                        ViewTab.GRAPH);
                selected.rebuildTopPane(ViewTab.GRAPH, this.currentSelection);
            }
        }
    }

    /**
     * This method is called when the selection list changed. Actually, it does
     * not do anything.
     * 
     * @param e
     *            the selectionEvent
     * @see org.graffiti.selection.SelectionListener
     *      #selectionListChanged(org.graffiti.selection.SelectionEvent)
     */
    public void selectionListChanged(SelectionEvent e) {
    }

    /**
     * Sets a new editComponentMap.
     * 
     * @see org.graffiti.plugin.editcomponent.NeedEditComponents
     *      #setEditComponentMap(java.util.Map)
     */
    public void setEditComponentMap(Map<Class<?>, Class<?>> ecMap) {
        this.valueEditComponents = ecMap;
    }

    /**
     * Inspector relies on the edit components to be up-to-date.
     * 
     * @see org.graffiti.plugin.GenericPlugin#needsEditComponents()
     * @return <code>true</code>
     */
    @Override
    public boolean needsEditComponents() {
        return true;
    }

    /**
     * States whether this class wants to be registered as a
     * <code>SelectionListener</code>.
     * 
     * @see org.graffiti.plugin.GenericPlugin#isSelectionListener()
     * @return <code>true</code>
     */
    @Override
    public boolean isSelectionListener() {
        return true;
    }

    /**
     * States whether this class wants to be registered as a
     * <code>SessionListener</code>.
     * 
     * @return <code>true</code>
     */
    @Override
    public boolean isSessionListener() {
        return true;
    }

    /**
     * Called when the user switches to another tab. Whenever the currently
     * displayed abstractTab changes, its content will be updated.
     * 
     * @param event
     *            the event describing the change
     */
    public void stateChanged(ChangeEvent event) {

        if (!this.doUpdate)
            // prevent rebuilding of the topPane
            return;
        ViewTab selected = (ViewTab) this.container.getComponent(0);
        int selectedTab = selected.getSelected();
        if (selectedTab != -1) {
            // register an attribute listener to the currently selected tab
            selected.addAttributeListener(this.listenerManager, selectedTab);
            // rebuild tab
            if (selectedTab == ViewTab.GRAPH) {
                selected.rebuildTopPane(selectedTab, new Selection());
            } else {
                selected.rebuildTopPane(selectedTab, this.currentSelection);
            }
        }
    }

    /**
     * Reacts on changes of the inspector menu. Displays the expert or basic
     * mode.
     */
    public void itemStateChanged(ItemEvent event) {
        int state = event.getStateChange();
        if (state == ItemEvent.SELECTED) {
            // expert mode
            this.container.remove(0);
            this.container.add(this.container.getTreePanel());
        } else {
            // basic mode
            this.container.remove(0);
            this.container.add(this.container.getSemanticPanel());
        }
        this.stateChanged(new ChangeEvent(this));
        this.container.repaint();
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
