// =============================================================================
//
//   SelectionMenu.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectionMenu.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.switchselections;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;
import javax.swing.JMenuItem;

import org.graffiti.core.Bundle;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.plugin.gui.GraffitiMenu;
import org.graffiti.plugin.gui.GraffitiMenuItem;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionEvent;
import org.graffiti.selection.SelectionListener;
import org.graffiti.session.EditorSession;
import org.graffiti.session.Session;
import org.graffiti.session.SessionListener;

/**
 * A menu providing entries to manage selections.
 * 
 * @author $Author: gleissner $
 */
public class SelectionMenu extends GraffitiMenu implements GraffitiComponent,
        SelectionListener, SessionListener {
    /**
     * 
     */
    private static final long serialVersionUID = -3115246899381717621L;

    /** The action for the "save as ..." menu item. */
    private final Action saveAction;

    /** The menu item for the saveAction. */
    private final GraffitiMenuItem saveItem;

    /** The <code>Bundle</code> for the string constants. */
    private static final Bundle bundle = Bundle.getCoreBundle();

    /** Save last active session */
    private EditorSession activeSession;

    /** DOCUMENT ME! */
    private Map<EditorSession, Selection> lastSelMap = new HashMap<EditorSession, Selection>();

    /** Saves entries for each session. */
    private Map<EditorSession, Map<String, ItemPositionPair>> sessionItemsMap = new HashMap<EditorSession, Map<String, ItemPositionPair>>();

    /**
     * Creates a new SelectionMenu object.
     */
    public SelectionMenu() {
        super();
        setName(bundle.getString("menu.selections"));
        setText(bundle.getString("menu.selections"));
        setEnabled(false);

        saveAction = new SelectionSaveAction(bundle
                .getString("menu.saveselection"));
        saveAction.setEnabled(true);
        saveAction
                .putValue(Action.NAME, bundle.getString("menu.saveselection"));
        saveItem = new GraffitiMenuItem(bundle.getString("menu.selections"),
                saveAction);
        saveItem.setToolTipText("Adds the current selection to the list "
                + "of loadable selections in this menu.\nOnly available till "
                + "the session is closed.\nDoes not survive undo / redo "
                + "operations on graph elements inside the selection.");
    }

    /**
     * @see org.graffiti.selection.SelectionListener#selectionChanged(org.graffiti.selection.SelectionEvent)
     */
    public void selectionChanged(SelectionEvent e) {
        activeSession = GraffitiSingleton.getInstance().getMainFrame()
                .getActiveEditorSession();

        Map<String, ItemPositionPair> nameItemMap = sessionItemsMap
                .get(activeSession);

        Selection lastSel = lastSelMap.get(activeSession);
        assert bundle.getString("activeSelection").equals(lastSel.getName());

        JMenuItem comp = nameItemMap.get(lastSel.getName()).getMenuItem();

        Selection clonedLastSel = null;

        try {
            clonedLastSel = (Selection) lastSel.clone();
            clonedLastSel.setName(bundle.getString("activeSelection"));
        } catch (CloneNotSupportedException cnse) {
            // well, it is clonable ...
        }

        // setEnabled(true);
        if (comp != null) {
            // already there => remove old, add new; maybe can replace
            // differently
            remove(comp);
        }

        SelectionChangeAction selAction = new SelectionChangeAction(
                clonedLastSel, activeSession);
        selAction.setEnabled(true);

        // selAction.putValue(Action.NAME, lastSel.getName());
        selAction.putValue(Action.NAME, bundle.getString("activeSelection"));

        GraffitiMenuItem item = new GraffitiMenuItem(bundle
                .getString("menu.selections"), selAction);
        insert(item, 1);

        nameItemMap.put(bundle.getString("activeSelection"),
                new ItemPositionPair(item, 1));

        if (!e.getSelection().isEmpty()) {
            try {
                lastSel = (Selection) e.getSelection().clone();
                lastSel.setName(bundle.getString("activeSelection"));
                lastSelMap.put(activeSession, lastSel);
            } catch (CloneNotSupportedException cnse) {
                // should be clonable ...
            }
        }

        validate();
    }

    /**
     * @see org.graffiti.selection.SelectionListener#selectionListChanged(org.graffiti.selection.SelectionEvent)
     */
    public void selectionListChanged(SelectionEvent e) {
        Selection clonedSel = null;

        try {
            clonedSel = (Selection) e.getSelection().clone();
            clonedSel.setName(e.getSelection().getName());
        } catch (CloneNotSupportedException cnse) {
            // should be clonable ...
        }

        activeSession = GraffitiSingleton.getInstance().getMainFrame()
                .getActiveEditorSession();

        Map<String, ItemPositionPair> nameItemMap = sessionItemsMap
                .get(activeSession);

        if (nameItemMap == null) {
            nameItemMap = new HashMap<String, ItemPositionPair>();
            sessionItemsMap.put(activeSession, nameItemMap);

            add(saveItem);
            nameItemMap.put(saveItem.getActionCommand(), new ItemPositionPair(
                    saveItem, this.getItemCount() - 1));
        }

        ItemPositionPair itp = nameItemMap.get(e.getSelection().getName());
        Component comp = null;

        if (itp != null) {
            comp = itp.getMenuItem();
        }

        // setEnabled(true);
        if (e.toBeAdded()) {
            if (comp != null) {
                // already there => remove old, add new
                remove(comp);
            }

            Action selAction = new SelectionChangeAction(clonedSel,
                    activeSession);
            selAction.setEnabled(true);
            selAction.putValue(Action.NAME, clonedSel.getName());

            GraffitiMenuItem item = new GraffitiMenuItem(bundle
                    .getString("menu.selections"), selAction);
            this.add(item);
            nameItemMap.put(clonedSel.getName(), new ItemPositionPair(item,
                    this.getItemCount() - 1));

            if (!isEnabled()) {
                setEnabled(true);
            }
        } else {
            remove(comp);
        }

        if (bundle.getString("activeSelection").equals(clonedSel.getName())) {
            lastSelMap.put(activeSession, clonedSel);
        }

        validate();
    }

    /**
     * @see org.graffiti.session.SessionListener#sessionChanged(org.graffiti.session.Session)
     */
    public void sessionChanged(Session s) {
        if (activeSession != s) {
            // switch to the set of selections associated with the new session
            removeAll();

            Map<String, ItemPositionPair> nameItemMap = sessionItemsMap.get(s);

            if (nameItemMap != null) {
                ArrayList<JMenuItem> items = new ArrayList<JMenuItem>(
                        nameItemMap.size());

                // ensure that size = nameItemMap.size(); strange isn't it?!
                for (int i = 0; i < nameItemMap.size(); i++) {
                    items.add(null);
                }

                for (ItemPositionPair ipp : nameItemMap.values()) {
                    items.set(ipp.getPosition(), ipp.getMenuItem());
                }

                for (JMenuItem item : items) {
                    add(item);
                }

                if (!nameItemMap.isEmpty()) {
                    setEnabled(true);
                }
            }

            if (s == null) {
                setEnabled(false);
            }

            activeSession = GraffitiSingleton.getInstance().getMainFrame()
                    .getActiveEditorSession();
        }

        validate();
    }

    /**
     * @see org.graffiti.session.SessionListener#sessionDataChanged(org.graffiti.session.Session)
     */
    public void sessionDataChanged(Session s) {
    }

    /**
     * DOCUMENT ME!
     * 
     * @author $Author: gleissner $
     * @version $Revision: 5766 $ $Date: 2009-10-23 13:07:21 +0200 (Fr, 23 Okt
     *          2009) $
     */
    class ItemPositionPair {
        /** DOCUMENT ME! */
        private JMenuItem mi;

        /** DOCUMENT ME! */
        private int pos;

        /**
         * Creates a new ItemPositionPair object.
         * 
         * @param jmi
         *            DOCUMENT ME!
         * @param posi
         *            DOCUMENT ME!
         */
        public ItemPositionPair(JMenuItem jmi, int posi) {
            mi = jmi;
            pos = posi;
        }

        /**
         * DOCUMENT ME!
         * 
         * @param mi
         */
        public void setMenuItem(JMenuItem mi) {
            this.mi = mi;
        }

        /**
         * DOCUMENT ME!
         * 
         * @return mi
         */
        public JMenuItem getMenuItem() {
            return mi;
        }

        /**
         * DOCUMENT ME!
         * 
         * @param pos
         */
        public void setPosition(int pos) {
            this.pos = pos;
        }

        /**
         * DOCUMENT ME!
         * 
         * @return pos
         */
        public int getPosition() {
            return pos;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
