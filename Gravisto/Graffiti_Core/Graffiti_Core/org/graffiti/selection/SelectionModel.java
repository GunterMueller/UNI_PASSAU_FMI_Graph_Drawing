// =============================================================================
//
//   SelectionModel.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectionModel.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.selection;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.event.AbstractGraphListener;
import org.graffiti.event.GraphEvent;

/**
 * Contains a list of selections and a reference to the current selection.
 * 
 * @author flierl
 * @version $Revision: 5767 $
 */
public class SelectionModel extends AbstractGraphListener {

    /** DOCUMENT ME! */
    public static final String ACTIVE = "active";

    /**
     * The list of selections. Maps a <code>Selection.name</code> to a
     * <code>Selection</code> instance.
     */
    private Hashtable<String, Selection> selections;

    /**
     * The list of listeners, that want to be informed about changes in the
     * selection model.
     * 
     * @see SelectionListener
     */
    private List<SelectionListener> listeners;

    /** The current active selection. */
    private String activeSelection;

    /**
     * Constructs a new <code>SelectionModel</code>.
     */
    public SelectionModel() {
        selections = new Hashtable<String, Selection>();
        listeners = new LinkedList<SelectionListener>();
    }

    /**
     * Sets the active selection to the given value. Informs all listeners about
     * the change.
     * 
     * @param selectionName
     *            the name of the new selection.
     */
    public void setActiveSelection(String selectionName) {
        if (this.activeSelection != null) {
            Selection oldSel = this.selections.get(this.activeSelection);
            oldSel.clear();
            this.selectionChanged();
        }

        this.activeSelection = selectionName;
        this.selectionChanged();
    }

    /**
     * Sets the active selection to the given value. Informs all listeners about
     * the change.
     * 
     * @param sel
     *            the name of the new selection.
     */
    public void setActiveSelection(Selection sel) {
        if (this.activeSelection != null) {
            Selection oldSel = this.selections.get(this.activeSelection);
            oldSel.clear();
            this.selectionChanged();
        }

        this.activeSelection = sel.getName();

        if (selections.put(sel.getName(), sel) == null) {
            SelectionEvent selectionEvent = new SelectionEvent(sel);
            selectionEvent.setAdded(true);

            for (SelectionListener l : listeners) {
                l.selectionListChanged(selectionEvent);
            }
        }

        this.selectionChanged();
    }

    /**
     * Returns the active selection.
     * 
     * @return DOCUMENT ME!
     */
    public Selection getActiveSelection() {
        if (activeSelection == null)
            return null;
        else
            return selections.get(activeSelection);
    }

    // /**
    // * Creates a new empty selection and sets this as the new active
    // selection.
    // * Returns this new empty, active selection. Informs all
    // * <code>SelectionListener</code>s.
    // *
    // * @return DOCUMENT ME!
    // */
    // public Selection setEmptySelection()
    // {
    // Selection newSel = new Selection(ACTIVE);
    //
    // // record that all previously marked elements have now been unmarked
    // Selection oldSel = (Selection) selections.get(activeSelection);
    // Map newUnmarked = new HashMap();
    //
    // for(Iterator it = oldSel.getNodes().iterator(); it.hasNext();)
    // {
    // newUnmarked.put(it.next(), null);
    // }
    //
    // for(Iterator it = oldSel.getEdges().iterator(); it.hasNext();)
    // {
    // newUnmarked.put(it.next(), null);
    // }
    //
    // newUnmarked.putAll(oldSel.getNewUnmarked());
    // newSel.setNewUnmarked(newUnmarked);
    // newSel.setNewMarked(oldSel.getNewMarked());
    //
    // this.add(newSel);
    // this.setActiveSelection(ACTIVE);
    //
    // return this.getActiveSelection();
    // }
    //
    // /**
    // * Creates a new empty selection and sets this as the new active
    // selection.
    // * Returns this new empty, active selection. Does not inform any
    // * <code>SelectionListener</code>s.
    // *
    // * @return DOCUMENT ME!
    // */
    // public Selection setEmptySelectionNonNotify()
    // {
    // Selection newSel = new Selection(ACTIVE);
    //
    // // record that all previously marked elements have now been unmarked
    // Selection oldSel = (Selection) selections.get(activeSelection);
    // Map newUnmarked = new HashMap();
    //
    // for(Iterator it = oldSel.getNodes().iterator(); it.hasNext();)
    // {
    // newUnmarked.put(it.next(), null);
    // }
    //
    // for(Iterator it = oldSel.getEdges().iterator(); it.hasNext();)
    // {
    // newUnmarked.put(it.next(), null);
    // }
    //
    // newUnmarked.putAll(oldSel.getNewUnmarked());
    // newSel.setNewUnmarked(newUnmarked);
    // newSel.setNewMarked(oldSel.getNewMarked());
    //
    // this.add(newSel);
    // this.activeSelection = ACTIVE;
    //
    // // this.setActiveSelection(ACTIVE);
    // return newSel;
    // }

    /**
     * Adds the given selection to the list of selections.
     * 
     * @param selection
     *            the selection object to add.
     */
    public void add(Selection selection) {
        selections.put(selection.getName(), selection);

        SelectionEvent selectionEvent = new SelectionEvent(selection);
        selectionEvent.setAdded(true);

        for (SelectionListener l : listeners) {
            l.selectionListChanged(selectionEvent);
        }
    }

    /**
     * Adds the given selection listener to the list of listeners.
     * 
     * @param listener
     *            the selection listener to add.
     */
    public void addSelectionListener(SelectionListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Removes the given selection for the list of selections.
     * 
     * @param selection
     *            the selection to remove from the list.
     */
    public void remove(Selection selection) {
        selections.remove(selection.getName());

        SelectionEvent selectionEvent = new SelectionEvent(selection);
        selectionEvent.setAdded(false);

        for (SelectionListener l : listeners) {
            l.selectionListChanged(selectionEvent);
        }
    }

    /**
     * Removes the given selection listener from the list of listeners.
     * 
     * @param listener
     *            the selection listener to remove.
     */
    public void removeSelectionListener(SelectionListener listener) {
        listeners.remove(listener);
    }

    /**
     * Informs the registered listeners that the active session has changed.
     */
    public void selectionChanged() {
        Selection activeSel = selections.get(activeSelection);
        SelectionEvent selectionEvent = new SelectionEvent(activeSel);

        for (SelectionListener l : listeners) {
            l.selectionChanged(selectionEvent);
        }

        activeSel.committedChanges();
    }

    /**
     * DOCUMENT ME!
     */
    public void updateLastActive() {
    }

    /*
     * @see
     * org.graffiti.event.AbstractGraphListener#postNodeRemoved(org.graffiti
     * .event.GraphEvent)
     */
    @Override
    public void postNodeRemoved(GraphEvent e) {
        Selection activeSel = selections.get(activeSelection);
        if (activeSel.contains(e.getNode())) {
            activeSel.remove(e.getNode());
            selectionChanged();
        }
    }

    /*
     * @see
     * org.graffiti.event.AbstractGraphListener#postEdgeRemoved(org.graffiti
     * .event.GraphEvent)
     */
    @Override
    public void postEdgeRemoved(GraphEvent e) {
        Selection activeSel = selections.get(activeSelection);
        if (activeSel.contains(e.getEdge())) {
            activeSel.remove(e.getEdge());
            selectionChanged();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
