// =============================================================================
//
//   MegaTools.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MegaTools.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.modes.defaults;

import java.awt.Component;
import java.awt.event.MouseEvent;

import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.view.AttributeComponent;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.plugin.view.View;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionModel;
import org.graffiti.session.EditorSession;
import org.graffiti.session.Session;

import com.lowagie.tools.plugins.AbstractTool;

/**
 * DOCUMENT ME!
 * 
 * @author holleis
 * @version $Revision: 5772 $
 * @deprecated
 */
@Deprecated
public abstract class MegaTools extends AbstractUndoableTool {

    // protected EditorSession session;

    /** DOCUMENT ME! */
    protected SelectionModel selectionModel;

    /** DOCUMENT ME! */
    protected final String ACTIVE = "active";

    /** DOCUMENT ME! */
    protected Component lastSelectedComp;

    // protected Component lastSelectedComp;
    // protected List selectedComps;
    public MegaTools() {
        // //// MegaTools.selection = new Selection(ACTIVE);
    }

    /**
     * DOCUMENT ME!
     */
    public void fireSelectionChanged() {
        selectionModel.selectionChanged();
    }

    /**
     * Temporarily marks the component under cursor.
     * 
     * @param e
     *            the mouse event
     */
    public void mouseMoved(MouseEvent e) {
        Component src = this.findComponentAt(e);

        if (!src.equals(lastSelectedComp)) {
            if ((lastSelectedComp != null)
                    && !selectedContain(lastSelectedComp)) {
                unDisplayAsMarked((GraphElementComponent) lastSelectedComp);
                src.getParent().repaint();
            }

            if (src instanceof View) {
                lastSelectedComp = null;
            } else {
                lastSelectedComp = src;

                if (!selectedContain(lastSelectedComp)) {
                    highlight(src);
                    src.getParent().repaint();
                }
            }
        }
    }

    /**
     * Called when the active session is changed.
     * 
     * @param s
     *            DOCUMENT ME!
     */
    public void sessionChanged(Session s) {
        super.sessionChanged(s);
        this.session = (EditorSession) s;

        // there is a new active session. Change the selection
        // model therefore
        if (s != null) {
            this.selectionModel = this.session.getSelectionModel();
            this.selection = selectionModel.getActiveSelection();
        } else {
            // there is currently no active session.
            // therefore there is no selection model
            this.selectionModel = null;
            this.selection = new Selection(ACTIVE);
        }
    }

    /**
     * Called when the session data (not the session's graph data!) changed.
     * 
     * @param s
     *            DOCUMENT ME!
     */
    public void sessionDataChanged(Session s) {
        super.sessionDataChanged(s);
        this.sessionChanged(s);

        // ?
    }

    /**
     * DOCUMENT ME!
     * 
     * @param me
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected boolean isControlDown(MouseEvent me) {
        return me.isControlDown() || me.isShiftDown();
    }

    /**
     * Returns Component found at position and in source indicated by the given
     * mouse event. Ignores everything but nodes, edges and the view.
     * 
     * @param me
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected Component findComponentAt(MouseEvent me) {
        Component src = null;

        // // while (true) {
        // View view = null;
        // try {
        // view = (View)me.getSource();
        // } catch (ClassCastException cce) {
        // throw new RuntimeException
        // ("The MouseEvent should have a View as source; " + cce);
        //									
        // }
        src = ((Component) me.getSource()).getComponentAt(me.getPoint());

        // System.out.println("found ::: " + src);
        if (src instanceof View || src instanceof GraphElementComponent)
            // System.out.println("okfound");
            return src;
        else if (src instanceof AttributeComponent) {
            View view = session.getActiveView();

            Component comp = view
                    .getComponentForElement((GraphElement) ((AttributeComponent) src)
                            .getAttribute().getAttributable());

            if (comp == null)
                return (Component) me.getSource();
            else
                return comp;
        } else
            return (Component) me.getSource();

        // // }
    }

    /**
     * Add component to selection.
     * 
     * @param geComp
     *            the comp holding the element to add to the selection.
     * @param ctrlPressed
     *            true if the ctrl-key has been pressed
     * @param caller
     *            DOCUMENT ME!
     */
    protected void mark(GraphElementComponent geComp, boolean ctrlPressed,
            AbstractTool caller) {
        if (geComp != null) {
            GraphElement ge = geComp.getGraphElement();

            if (selection.getNodes().contains(ge)
                    || selection.getEdges().contains(ge)) {
                if (ctrlPressed) {
                    // ctrl and marked => unmark node
                    unmark(geComp);
                }
            } else {
                if (!ctrlPressed) {
                    // unselect all previously selected and create new selection
                    caller.unDisplayAsMarked(getAllMarkedComps());

                    // selection = new Selection(ACTIVE);
                    // // resetSelectedComps();
                    // selectionModel.add(selection);
                    // selectionModel.setActiveSelection(ACTIVE);
                    selection.clear();

                    // System.out.println(" new selection!");
                }

                // add graphelement to selection
                // selectedComps.add(geComp);
                selection.add(ge);
                caller.displayAsMarked(geComp);

                // selectionModel.selectionChanged();
            }
        }
    }

    /**
     * Returns true if the current selection contains the given
     * <code>GraphElement</code>.
     * 
     * @param ge
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected boolean selectedContain(GraphElement ge) {
        if (selection.getNodes().contains(ge)
                || selection.getEdges().contains(ge))
            return true;

        return false;
    }

    /**
     * Returns true if the current selection contains the given
     * <code>GraphElementComponent</code>.
     * 
     * @param gec
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected boolean selectedContain(Component gec) {
        if (gec != null) {
            try {
                GraphElement ge = ((GraphElementComponent) gec)
                        .getGraphElement();

                if (selection.getNodes().contains(ge)
                        || selection.getEdges().contains(ge))
                    return true;
            } catch (ClassCastException cce) {
                return false;
            }
        }

        return false;
    }

    /**
     * Removes the graphelement from the selection.
     * 
     * @param geComp
     *            the comp holding the element to add to the selection.
     */
    protected void unmark(GraphElementComponent geComp) {
        if (geComp != null) {
            GraphElement ge = geComp.getGraphElement();

            // assert selectedComps.contains(ge);
            selection.remove(ge);

            // selectedComps.remove(geComp);
            unDisplayAsMarked(geComp);

            // selectionModel.selectionChanged();
        }
    }

    /**
     * Clears the selection. Does not fire a selectionChanged event.
     */
    protected void unmarkAll() {
        unDisplayAsMarked(getAllMarkedComps());

        // System.out.println("deselecting everything");
        // // selection = new Selection(ACTIVE);
        // //// resetSelectedComps();
        // // selectionModel.add(selection);
        // // selectionModel.setActiveSelection(ACTIVE);
        selection.clear();

        // selectionModel.selectionChanged();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
