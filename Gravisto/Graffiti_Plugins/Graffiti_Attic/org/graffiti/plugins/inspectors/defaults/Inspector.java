// =============================================================================
//
//   Inspector.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Inspector.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.inspectors.defaults;

import java.util.HashMap;
import java.util.Map;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.event.ListenerManager;
import org.graffiti.event.ListenerNotFoundException;
import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.editcomponent.NeedEditComponents;
import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.plugin.inspector.EditPanel;
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
 * @version $Revision: 5772 $
 */
public class Inspector extends EditorPluginAdapter implements InspectorPlugin,
        SessionListener, SelectionListener, NeedEditComponents {

    /** The default width of the inspector components. */
    public static final int DEFAULT_WIDTH = 120;

    /** DOCUMENT ME! */
    private EdgeTab edgeTab;

    /** DOCUMENT ME! */
    private GraphTab graphTab;

    /** DOCUMENT ME! */
    private InspectorContainer container;

    /** DOCUMENT ME! */
    private ListenerManager listenerManager;

    /** DOCUMENT ME! */
    private Map<InspectorTab, Integer> tabIndexMap = new HashMap<InspectorTab, Integer>();

    /** DOCUMENT ME! */
    private NodeTab nodeTab;

    /**
     * Needed to save the last session when a session changed event is fired.
     * This is in turn needed to remove the inspector as a selection listener
     * from this session.
     */
    private SelectionModel oldSelectionModel = null;

    /** DOCUMENT ME! */
    private Session activeSession;

    /** DOCUMENT ME! */
    private int nextIndex = 0;

    /**
     * Constructs a new ionpector instance.
     */
    public Inspector() {
        super();
        this.container = new InspectorContainer();

        // the container should be made visible, if the
        // session changed. See the sessionChanged method for details (jf).
        this.container.setVisible(false);
        this.guiComponents = new GraffitiComponent[1];
        guiComponents[0] = container;
    }

    /**
     * @see org.graffiti.plugin.editcomponent.NeedEditComponents#setEditComponentMap(java.util.Map)
     */
    public void setEditComponentMap(Map<Class<?>, Class<?>> ecMap) {
        this.valueEditComponents = ecMap;
    }

    /**
     * Returns the <code>InspectorContainer</code>.
     * 
     * @return DOCUMENT ME!
     */
    public InspectorContainer getInspectorContainer() {
        return this.container;
    }

    /**
     * @see org.graffiti.plugin.GenericPlugin#isSelectionListener()
     */
    @Override
    public boolean isSelectionListener() {
        return true;
    }

    /**
     * States whether this class wants to be registered as a
     * <code>SessionListener</code>.
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean isSessionListener() {
        return true;
    }

    /**
     * Returns an array containing all the <code>InspectorTab</code>s of the
     * <code>InspectorPlugin</code>.
     * 
     * @return an array containing all the <code>InspectorTab</code>s of the
     *         <code>InspectorPlugin</code>.
     */
    public InspectorTab[] getTabs() {
        return new InspectorTab[] { edgeTab, nodeTab, graphTab };
    }

    /**
     * Adds another <code>InspectorTab</code> to the current
     * <code>InspectorPlugin</code>.
     * 
     * @param tab
     *            the <code>InspectorTab</code> to be added to the
     *            <code>InspectorPlugin</code>.
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    public void addTab(InspectorTab tab) {
        EditorSession editorSession;

        try {
            editorSession = (EditorSession) activeSession;
        } catch (ClassCastException cce) {
            // No selection is made if no EditorSession is active (?)
            throw new RuntimeException("WARNING: should rarely happen " + cce);
        }

        if (tab instanceof EdgeTab) {
            this.edgeTab = (EdgeTab) tab;
        } else if (tab instanceof NodeTab) {
            this.nodeTab = (NodeTab) tab;
        } else if (tab instanceof GraphTab) {
            this.graphTab = (GraphTab) tab;
        }

        if (tab.getEditPanel() != null) {
            tab.getEditPanel().setEditComponentMap(this.valueEditComponents);
            tab.getEditPanel().setGraphElementMap(
                    editorSession.getGraphElementsMap());
        }

        container.addTab(tab);
        tabIndexMap.put(tab, new Integer(nextIndex++));

        if (listenerManager != null) {
            this.listenerManager.addStrictAttributeListener(tab);
        }
    }

    /**
     * Inspector relies on the edit components to be up-to-date.
     * 
     * @see org.graffiti.plugin.GenericPlugin#needsEditComponents()
     */
    @Override
    public boolean needsEditComponents() {
        return true;
    }

    /**
     * Is called, if something in the selection model changed.
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void selectionChanged(SelectionEvent e) {
        if (this.listenerManager == null)
            return;

        // EditorSession editorSession;
        //
        // try {
        // editorSession = (EditorSession) activeSession;
        // } catch(ClassCastException cce) {
        // // No selection is made if no EditorSession is active (?)
        // throw new RuntimeException("WARNING: should rarely happen " + cce);
        // }
        Selection sel = e.getSelection();

        if (!sel.getEdges().isEmpty()) {
            if (this.edgeTab == null) {
                EdgeTab newEdgeTab = new EdgeTab();
                addTab(newEdgeTab);

                // EditPanel editPanel = newEdgeTab.getEditPanel();
                // editPanel.setListenerManager(this.listenerManager);
                // editPanel.setGraphElementMap(editorSession.getGraphElementsMap());
                newEdgeTab.getEditPanel().setListenerManager(
                        this.listenerManager);
                newEdgeTab.buildTree(sel.getEdges());
            } else {
                container.setEnabledAt(tabIndexMap.get(edgeTab), true);
                this.edgeTab.rebuildTree(sel.getEdges());
            }

            // show edge tab ?!
            if (sel.getNodes().isEmpty()) {
                if (GraffitiSingleton.getInstance().isEditorFrameSelected()) {
                    this.container.setSelectedComponent(this.edgeTab);
                }
            }
        } else if (edgeTab != null) {
            container.setEnabledAt(tabIndexMap.get(edgeTab), false);
        }

        if (!sel.getNodes().isEmpty()) {
            if (this.nodeTab == null) {
                NodeTab newNodeTab = new NodeTab();

                // EditPanel editPanel = newNodeTab.getEditPanel();
                // editPanel.setListenerManager(this.listenerManager);
                // editPanel.setGraphElementMap(editorSession.getGraphElementsMap());
                newNodeTab.getEditPanel().setListenerManager(
                        this.listenerManager);
                addTab(newNodeTab);
                this.nodeTab.buildTree(sel.getNodes());
            } else {
                container.setEnabledAt(tabIndexMap.get(nodeTab), true);
                this.nodeTab.rebuildTree(sel.getNodes());
            }

            // show node tab ?!
            if (GraffitiSingleton.getInstance().isEditorFrameSelected()) {
                this.container.setSelectedComponent(this.nodeTab);
            }
        } else if (nodeTab != null) {
            container.setEnabledAt(tabIndexMap.get(nodeTab), false);
        }

        // if no nodes / edges are selected, show graph tab
        if (sel.getNodes().isEmpty() && sel.getEdges().isEmpty()) {
            if (this.graphTab == null) {
                GraphTab newGraphTab = new GraphTab();

                // EditPanel editPanel = newGraphTab.getEditPanel();
                // editPanel.setListenerManager(this.listenerManager);
                // editPanel.setGraphElementMap(editorSession.getGraphElementsMap());
                newGraphTab.getEditPanel().setListenerManager(
                        this.listenerManager);
                addTab(newGraphTab);
                this.graphTab.buildTree(activeSession.getGraph().getAttribute(
                        ""));
            } else {
                this.graphTab.buildTree(activeSession.getGraph().getAttribute(
                        ""));
            }

            if (GraffitiSingleton.getInstance().isEditorFrameSelected()) {
                this.container.setSelectedComponent(this.graphTab);
            }
        }
    }

    /**
     * @see org.graffiti.selection.SelectionListener#selectionListChanged(org.graffiti.selection.SelectionEvent)
     */
    public void selectionListChanged(SelectionEvent e) {
    }

    /**
     * This method is called when the session changes.
     * 
     * @param s
     *            the new Session.
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    public void sessionChanged(Session s) {
        // there is an active session. So watch it...
        if (s != null) {
            EditorSession editorSession;

            try {
                editorSession = (EditorSession) s;
            } catch (ClassCastException cce) {
                // No selection is made if no EditorSession is active (?)
                throw new RuntimeException("WARNING: should rarely happen "
                        + cce);
            }

            if (this.listenerManager != null) {
                if (this.graphTab != null) {
                    try {
                        this.listenerManager
                                .removeAttributeListener(this.graphTab);
                    } catch (ListenerNotFoundException e) {
                    }
                }

                if (this.nodeTab != null) {
                    try {
                        this.listenerManager
                                .removeAttributeListener(this.nodeTab);
                    } catch (ListenerNotFoundException e) {
                    }
                }

                if (this.edgeTab != null) {
                    try {
                        this.listenerManager
                                .removeAttributeListener(this.edgeTab);
                    } catch (ListenerNotFoundException e) {
                    }
                }
            }

            this.listenerManager = s.getGraph().getListenerManager();

            if (this.graphTab != null) {
                this.listenerManager.addStrictAttributeListener(this.graphTab);

                EditPanel editPanel = graphTab.getEditPanel();
                editPanel.setListenerManager(this.listenerManager);
                editPanel.setGraphElementMap(editorSession
                        .getGraphElementsMap());
            }

            if (this.nodeTab != null) {
                this.listenerManager.addStrictAttributeListener(this.nodeTab);

                EditPanel editPanel = nodeTab.getEditPanel();
                editPanel.setListenerManager(this.listenerManager);
                editPanel.setGraphElementMap(editorSession
                        .getGraphElementsMap());
            }

            if (this.edgeTab != null) {
                this.listenerManager.addStrictAttributeListener(this.edgeTab);

                EditPanel editPanel = edgeTab.getEditPanel();
                editPanel.setListenerManager(this.listenerManager);
                editPanel.setGraphElementMap(editorSession
                        .getGraphElementsMap());
            }

            this.activeSession = s;

            if (this.oldSelectionModel != null) {
                this.oldSelectionModel.removeSelectionListener(this);
            }

            // should be done elsewhere: // why?
            SelectionModel model = null;

            try {
                model = ((EditorSession) s).getSelectionModel();

                // should never be null ....
                if (model != null) {
                    model.addSelectionListener(this);
                    this.oldSelectionModel = model;
                }
            } catch (ClassCastException cce) {
                // No selection is made if no EditorSession is active (?)
                throw new RuntimeException("WARNING: should rarely happen "
                        + cce);
            }

            if (this.graphTab == null) {
                GraphTab newGraphTab = new GraphTab();

                // EditPanel editPanel = newGraphTab.getEditPanel();
                // editPanel.setListenerManager(this.listenerManager);
                // editPanel.setGraphElementMap(editorSession.getGraphElementsMap());
                newGraphTab.getEditPanel().setListenerManager(
                        this.listenerManager);
                addTab(newGraphTab);
                this.graphTab.buildTree(s.getGraph().getAttributes());

                // (new org.graffiti.attributes.HashMapAttribute("graph"));
            }

            Selection actSel = model.getActiveSelection();

            if (actSel == null) {
                this.selectionChanged(new SelectionEvent(new Selection(
                        "__temp__")));
            } else {
                this.selectionChanged(new SelectionEvent(actSel));
            }

            container.setVisible(true);
        } else {
            // there is no active session anymore. Remove the inspector.
            // Or at least make it invisible.
            removeTab(this.graphTab);

            if (this.nodeTab != null) {
                removeTab(this.nodeTab);
            }

            if (this.edgeTab != null) {
                removeTab(this.edgeTab);
            }

            container.setVisible(false);
        }
    }

    /**
     * This method is called when the session data (but not the session's graph
     * data) changed. Actually, it does not do anything.
     * 
     * @param s
     *            DOCUMENT ME!
     */
    public void sessionDataChanged(Session s) {
    }

    /**
     * 
     */
    private void removeTab(InspectorTab tab) {
        if (tab instanceof EdgeTab) {
            this.edgeTab = null;
        } else if (tab instanceof NodeTab) {
            this.nodeTab = null;
        } else {
            this.graphTab = null;
        }

        container.removeTab(tab);

        // shift all tabs to the right one left
        int tabIndex = tabIndexMap.get(tab).intValue();

        try {
            for (Map.Entry<InspectorTab, Integer> entry : tabIndexMap
                    .entrySet()) {
                int index = entry.getValue();

                if (index > tabIndex) {
                    entry.setValue(new Integer(index - 1));
                }
            }

            nextIndex--;

            try {
                this.listenerManager.removeAttributeListener(tab);
            } catch (ListenerNotFoundException e) {
            }
        } catch (Exception e) {
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
