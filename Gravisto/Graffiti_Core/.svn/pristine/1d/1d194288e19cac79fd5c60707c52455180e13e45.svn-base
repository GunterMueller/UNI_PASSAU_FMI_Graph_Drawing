// =============================================================================
//
//   ShowPopupMenu.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.commonactions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.graph.Graph;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.GestureFeedbackProvider;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.PopupMenuCompatibleProvider;
import org.graffiti.plugin.view.interactive.PopupMenuItem;
import org.graffiti.plugin.view.interactive.PopupMenuSelectionGesture;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("showPopupMenu")
public class ShowPopupMenu extends CommonAction {
    @InSlot
    public static final Slot<PopupMenuItem> menuSlot = Slot.create("menu",
            PopupMenuItem.class);

    @InSlot
    public static final Slot<Point2D> positionSlot = Slot.create("position",
            Point2D.class);

    private JPopupMenu menu;
    private PopupMenuCompatibleProvider provider;
    private Set<JComponent> currentItems;

    public ShowPopupMenu() {
        MainFrame mainFrame = GraffitiSingleton.getInstance().getMainFrame();

        ActionListener cancellingListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                provider
                        .relayMenuSelectionGesture(new PopupMenuSelectionGesture());
            }
        };

        menu = new JPopupMenu();
        for (Action action : new Action[] { mainFrame.getEditCut(),
                mainFrame.getEditCopy(), mainFrame.getEditPaste(),
                mainFrame.getEditDelete() }) {
            JMenuItem jmi = new JMenuItem(action);
            jmi.addActionListener(cancellingListener);
            menu.add(jmi);
        }
        menu.addSeparator();
        currentItems = new HashSet<JComponent>();
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            InteractiveView<?> view, EditorSession session) {
        Point2D pos = in.get(positionSlot);
        PopupMenuItem pmi = in.get(menuSlot);
        GestureFeedbackProvider gfp = view.getGestureFeedbackProvider();
        if (gfp instanceof PopupMenuCompatibleProvider) {
            for (JComponent component : currentItems) {
                menu.remove(component);
            }
            currentItems.clear();
            Collection<PopupMenuItem> children = pmi.getChildren();
            for (final PopupMenuItem item : children) {
                JComponent component = createMenu(item);
                menu.add(component);
                currentItems.add(component);
            }
            provider = (PopupMenuCompatibleProvider) gfp;
            provider.show(menu, pos.getX(), pos.getY());
        }
    }

    private JComponent createMenu(final PopupMenuItem item) {
        if (item.hasChildren()) {
            JMenu menu = new JMenu(item.getLabel());
            for (PopupMenuItem child : item.getChildren()) {
                menu.add(createMenu(child));
            }
            return menu;
        } else {
            if (item.isSeparator())
                return new JSeparator();
            JMenuItem jmi = new JMenuItem(item.getLabel());
            ActionListener actionListener = item.getActionListener();
            if (actionListener == null) {
                actionListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        provider
                                .relayMenuSelectionGesture(new PopupMenuSelectionGesture(
                                        item));
                    }
                };
            }
            jmi.addActionListener(actionListener);
            return jmi;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
