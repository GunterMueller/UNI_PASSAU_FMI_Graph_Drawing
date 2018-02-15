// =============================================================================
//
//   AbstractTool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractTool.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.deprecated;

import java.awt.Component;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MouseInputAdapter;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.tool.EdgeBorder;
import org.graffiti.plugin.tool.NodeBorder;
import org.graffiti.plugin.view.EdgeComponentInterface;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.plugin.view.NodeComponentInterface;
import org.graffiti.plugin.view.View;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionEvent;
import org.graffiti.selection.SelectionListener;
import org.graffiti.session.EditorSession;
import org.graffiti.session.Session;
import org.graffiti.session.SessionListener;

/**
 * Provides an abstract implementation of the <code>Tool</code> interface.
 * 
 * @see Tool
 * @see javax.swing.event.MouseInputAdapter
 * @deprecated
 */
@Deprecated
public abstract class AbstractTool extends MouseInputAdapter implements Tool,
        SessionListener, SelectionListener {

    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(AbstractTool.class
            .getName());

    /** DOCUMENT ME! */
    /** DOCUMENT ME! */
    protected AffineTransform zoom = View.NO_ZOOM;

    /** The current session that this tool should work on / with. */
    protected EditorSession session;

    /** The graph this tool works on. */
    protected Graph graph;

    /** The preferences of this tool. */
    protected Preferences prefs;

    /** The current selection that this tool should work on / with. */
    protected Selection selection;

    /** Flag set by <code>activate</code> and <code>deactivate</code>. */
    protected boolean isActive;

    /** Used to display marked nodes. */
    /** Size of bullets used to display marked edges. */
    private final int BORDERSIZE = 6;

    // private final LineBorder border = new LineBorder(java.awt.Color.RED, 4);

    /** DOCUMENT ME! */
    private final Border border = new NodeBorder(java.awt.Color.RED, BORDERSIZE);

    // private final Border border = new NodeBorder(java.awt.Color.RED, 8);
    // private final Border border = new TitledBorder("Node");

    /** Used to temporarily highlight nodes. */
    private final Border tempBorder = new NodeBorder(java.awt.Color.ORANGE,
            BORDERSIZE);

    // private final LineBorder tempBorder = new
    // LineBorder(java.awt.Color.ORANGE, 1);

    /** Border for unmarked graph elements. */
    private final EmptyBorder empty = new EmptyBorder(0, 0, 0, 0); // 3, 3, 3,

    // 3 ?

    /**
     * Returns true if this tool has been activated and since then not been
     * deactivated.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isActive() {
        return this.isActive;
    }

    /**
     * Sets the graph of this tool.
     * 
     * @param graph
     *            the graph of this tool.
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    /**
     * Sets the preferences of this tool.
     * 
     * @param p
     *            the preferences of this tool.
     */
    public void setPrefs(Preferences p) {
        this.prefs = p;
    }

    /**
     * States whether this class wants to be registered as a
     * <code>SelectionListener</code>.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isSelectionListener() {
        return true;
    }

    /**
     * States whether this class wants to be registered as a
     * <code>SessionListener</code>.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isSessionListener() {
        return true;
    }

    /**
     * States whether this class wants to be registered as a
     * <code>ViewListener</code>, i.e. if it wants to get informed when another
     * view in the same session becomes active. This method is not called when
     * another session is activated. Implement <code>SessionListener</code> if
     * you are interested in session changed events.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isViewListener() {
        return false;
    }

    /**
     * Classes that overwrite this method should call super.active first.
     * 
     * @see org.graffiti.plugins.modes.deprecated.Tool#activate()
     */
    public void activate() {
        this.isActive = true;

        if (session == null) {
            session = GraffitiSingleton.getInstance().getMainFrame()
                    .getActiveEditorSession();
        }
        if (session == null) {
            System.err
                    .println("Error: Session is null; not set for this tool.");
            System.err.println("Method activate needs to be aborted. (CK)");

            return;
        }

        // this.session.setLastActiveTool(this);
        logger.entering(this.toString(), "  activate");
        displayAsMarked(this.getAllMarkedComps());
    }

    /**
     * Classes that overwrite this method should call super.deactive first.
     * 
     * @see org.graffiti.plugins.modes.deprecated.Tool#deactivate()
     */
    public void deactivate() {
        if (this.selection != null) {
            unDisplayAsMarked(getCompsForElems(this.selection.getElements()));
        }

        this.isActive = false;
        logger.entering(this.toString(), "deactivate");
    }

    /**
     * Show a graph element component as marked.
     * 
     * @param comp
     *            DOCUMENT ME!
     */
    public void displayAsMarked(GraphElementComponent comp) {
        if (comp instanceof NodeComponentInterface) {
            displayAsMarked((NodeComponentInterface) comp);
        } else {
            displayAsMarked((EdgeComponentInterface) comp);
        }
    }

    /**
     * Show a node component as marked.
     * 
     * @param comp
     *            DOCUMENT ME!
     */
    public void displayAsMarked(NodeComponentInterface comp) {
        if (comp != null) {
            ((JComponent) comp).setBorder(border);

            // comp.getParent().repaint();
        }
    }

    /**
     * Show an edge component as marked.
     * 
     * @param comp
     *            DOCUMENT ME!
     */
    public void displayAsMarked(EdgeComponentInterface comp) {
        if (comp != null) {
            ((JComponent) comp).setBorder(new EdgeBorder(java.awt.Color.RED,
                    BORDERSIZE, true));
        }
    }

    /**
     * Display a list of graph element components
     * 
     * @param comps
     *            DOCUMENT ME!
     */
    public void displayAsMarked(List<GraphElementComponent> comps) {
        for (GraphElementComponent gec : comps) {
            displayAsMarked(gec);
        }
    }

    /**
     * Display a component in a special way distinguished from the way
     * <code>displayAsMarked</code> does it. Used for temporarily highlighting a
     * component, e.g. for a mouseMoved action.
     * 
     * @param comp
     *            DOCUMENT ME!
     */
    public void highlight(Component comp) {
        if (comp != null) {
            if (comp instanceof EdgeComponentInterface) {
                // TODO check if can make final var like tempBorder
                ((JComponent) comp).setBorder(new EdgeBorder(
                        java.awt.Color.ORANGE, BORDERSIZE, false));
            } else {
                ((JComponent) comp).setBorder(tempBorder);
            }
        }

        comp.getParent().repaint();
    }

    /**
     * Called when the selection has changed.
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void selectionChanged(SelectionEvent e) {
        Selection sel = e.getSelection();

        if (this.isActive()) {
            if (!sel.equals(this.selection)
                    || (sel.getNewUnmarked().isEmpty() && sel.getNewMarked()
                            .isEmpty())) {
                // must completely renew selection
                if (selection != null) {
                    unDisplayAsMarked(this.getAllMarkedComps());
                }

                displayAsMarked(getCompsForElems(sel.getElements()));
            } else {
                List<GraphElementComponent> list = new LinkedList<GraphElementComponent>();

                for (GraphElement elem : sel.getNewUnmarked().keySet()) {
                    list.addAll(getCompsForElem(elem));
                }

                unDisplayAsMarked(list);

                list = new LinkedList<GraphElementComponent>();

                for (GraphElement elem : sel.getNewMarked().keySet()) {
                    list.addAll(getCompsForElem(elem));
                }

                displayAsMarked(list);
            }

            for (View view : session.getViews()) {
                ((JComponent) view).repaint();
            }
        }

        this.selection = sel;
    }

    /**
     * @see org.graffiti.selection.SelectionListener#selectionListChanged(org.graffiti.selection.SelectionEvent)
     */
    public void selectionListChanged(SelectionEvent e) {
    }

    /**
     * @see org.graffiti.session.SessionListener#sessionChanged(Session)
     */
    public void sessionChanged(Session s) {
        session = (EditorSession) s;
    }

    /**
     * Remove anything that specifies a graph element component as being marked.
     * 
     * @param comp
     *            DOCUMENT ME!
     */
    public void unDisplayAsMarked(GraphElementComponent comp) {
        if (comp instanceof NodeComponentInterface) {
            unDisplayAsMarked((NodeComponentInterface) comp);
        } else {
            unDisplayAsMarked((EdgeComponentInterface) comp);
        }
    }

    /**
     * Remove anything that specifies a node component as being marked.
     * 
     * @param comp
     *            DOCUMENT ME!
     */
    public void unDisplayAsMarked(NodeComponentInterface comp) {
        if (comp != null) {
            ((JComponent) comp).setBorder(empty);
        }
    }

    /**
     * Remove anything that specifies an edge component as being marked.
     * 
     * @param comp
     *            DOCUMENT ME!
     */
    public void unDisplayAsMarked(EdgeComponentInterface comp) {
        if (comp != null) {
            ((JComponent) comp).setBorder(empty);
        }
    }

    /**
     * Call <code>unDisplayAsMarked(GraphElementComponent geComp)</code> on
     * every element of the provided list.
     * 
     * @param comps
     *            DOCUMENT ME!
     */
    public void unDisplayAsMarked(List<GraphElementComponent> comps) {
        for (GraphElementComponent c : comps) {
            unDisplayAsMarked(c);
        }
    }

    /**
     * Returns a list of all <code>GraphElementComponents</code> contained in
     * this selection.
     * 
     * @return DOCUMENT ME!
     */
    protected List<GraphElementComponent> getAllMarkedComps() {
        List<GraphElementComponent> geComps = new LinkedList<GraphElementComponent>();

        if (selection == null)
            return geComps;

        geComps.addAll(getCompsForElems(selection.getNodes()));
        geComps.addAll(getCompsForElems(selection.getEdges()));

        return geComps;
    }

    /**
     * Used method <code>getComponentForElement</code> from the views of the
     * current session to get the <code>GraphElementComponent</code>s for the
     * provided <code>GraphElement</code>.
     * 
     * @param ge
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected List<GraphElementComponent> getCompsForElem(GraphElement ge) {
        if (session != null) {
            List<View> views = session.getViews();

            List<GraphElementComponent> comps = new LinkedList<GraphElementComponent>();

            for (View view : views) {
                comps.add(view.getComponentForElement(ge));
            }

            return comps;
        } else
            return new LinkedList<GraphElementComponent>();
    }

    /**
     * Used method <code>getComponentForElement</code> from the views of the
     * current session to convert the provided list of <code>GraphElement</code>
     * elements to a list of <code>GraphElementComponent</code>s.
     * 
     * @param elems
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected List<GraphElementComponent> getCompsForElems(
            List<? extends GraphElement> elems) {
        if (session != null) {
            List<View> views = session.getViews();

            // View view = session.getActiveView();
            List<GraphElementComponent> comps = new LinkedList<GraphElementComponent>();

            for (GraphElement ge : elems) {
                for (View view : views) {
                    comps.add(view.getComponentForElement(ge));
                }
            }

            return comps;
        } else

            return new LinkedList<GraphElementComponent>();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
