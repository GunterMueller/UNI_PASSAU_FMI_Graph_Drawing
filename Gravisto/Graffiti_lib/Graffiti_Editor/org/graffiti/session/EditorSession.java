// =============================================================================
//
//   EditorSession.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EditorSession.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.session;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.swing.undo.UndoManager;

import org.graffiti.core.Bundle;
import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.tool.ToolRegistry;
import org.graffiti.plugin.view.View;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.selection.SelectionModel;

/**
 * Contains an editor session. An editor session contains a list of views, which
 * can manipulate the graph object. It also contains the current editor mode and
 * the selection model.
 * 
 * @version $Revision: 5768 $
 * 
 * @see org.graffiti.session.Session
 */
public class EditorSession extends Session implements ActionListener {
    /**
     * The map between new and old graph elements for proper undoing of their
     * deleting
     */
    private Map<GraphElement, GraphElement> graphElementsMap;

    /**
     * The selectionModel in this session.
     */
    private SelectionModel selectionModel;

    /**
     * The file name of the graph object, if available. Else <code>null</code>.
     */
    private URI fileName;

    // /**
    // * The list of views.
    // *
    // * @see org.graffiti.view.GraffitiView
    // */
    // private ArrayList views;

    /** The undoManager for this session. */
    private UndoManager um;

    /**
     * The &quot;closing&quot; state of this session. <code>true</code>, if this
     * session is currently closing.
     */
    private boolean closing = false;

    /**
     * Constructs a new <code>EditorSession</code> with an empty graph instance.
     */
    public EditorSession() {
        this(new FastGraph());
        // this.selectionModel = new SelectionModel();
    }

    /**
     * Constructs a new <code>EditorSession</code>.
     * 
     * @param graph
     *            the <code>Graph</code> object for this session.
     */
    public EditorSession(Graph graph) {
        super(graph);
        um = new UndoManager();
        graphElementsMap = new HashMap<GraphElement, GraphElement>();
        // this.selectionModel = new SelectionModel();
        // this.selectionModel.add(new Selection(ACTIVE));
        // this.selectionModel.setActiveSelection(ACTIVE);
    }

    /**
     * Sets the closing state of this session. This may only be done once.
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    public void setClosing() {
        if (closing)
            throw new RuntimeException("The session \"" + this.toString()
                    + "\" is already in the closing state.");
        else {
            closing = true;
        }
    }

    /**
     * Returns <code>true</code>, if the session is currently closing.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isClosing() {
        return closing;
    }

    /**
     * Sets the fileName.
     * 
     * @param fileName
     *            The fileName to set
     */
    public void setFileName(URI fileName) {
        this.fileName = fileName;
    }

    /**
     * Returns the fileName of this session's graph.
     * 
     * @return the fileName of this session's graph.
     */
    public URI getFileName() {
        return fileName;
    }

    public String getFileNameForSaveDialog() {
        String fileName = getFileNameAsString();
        Bundle bundle = Bundle.getCoreBundle();
        String defaultName = bundle.getString("menu.file.new.defaultName");
        if (fileName.equals(defaultName)) {
            fileName = bundle.getString("menu.file.new.defaultFileName");
            fileName += getId();
        }
        return fileName;
    }

    /**
     * An auxillary method for quering for the string name of graph file of this
     * session.
     * 
     * @return a name of the graph file as string
     */
    public String getFileNameAsString() {
        String name = Bundle.getCoreBundle().getString(
                "menu.file.new.defaultName");

        if (fileName != null) {
            String path = fileName.getPath();
            int idx = path.lastIndexOf('/');
            name = path.substring(idx + 1);
        }

        return name;
    }

    /**
     * Returns the graphElementMap.
     * 
     * @return Map
     */
    public Map<GraphElement, GraphElement> getGraphElementsMap() {
        return graphElementsMap;
    }

    /**
     * Sets the selectionModel.
     * 
     * @param selectionModel
     *            The selectionModel to set
     */
    public void setSelectionModel(SelectionModel selectionModel) {
        this.selectionModel = selectionModel;
        for (View view : views) {
            if (view instanceof InteractiveView<?>) {
                ((InteractiveView<?>) view).setSelectionModel(selectionModel);
            }
        }
    }

    /**
     * Returns the selectionModel.
     * 
     * @return DOCUMENT ME!
     */
    public SelectionModel getSelectionModel() {
        return this.selectionModel;
    }

    /**
     * Returns the undoManager for this session.
     * 
     * @return the undoManager for this session.
     */
    public UndoManager getUndoManager() {
        return um;
    }

    /**
     * Registrates the selected <code>Tool</code> as an
     * <code>MouseInputListener</code> at the view.
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addView(View view) {
        super.addView(view);
        if (view instanceof InteractiveView<?>) {
            InteractiveView<?> interactiveView = (InteractiveView<?>) view;
            interactiveView.setEditorSession(this);
            interactiveView.setSelectionModel(selectionModel);
            interactiveView.setUserGestureDispatcher(ToolRegistry.get());
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
