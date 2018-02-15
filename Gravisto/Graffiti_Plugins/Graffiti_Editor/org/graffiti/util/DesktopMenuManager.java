// =============================================================================
//
//   DesktopMenuManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DesktopMenuManager.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.util;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.graffiti.core.Bundle;

/**
 * Manages menu entries for the internal frames contained in a desktop pane. A
 * MenuManager is associated with a {@link javax.swing.JDesktopPane} and a
 * {@link javax.swing.JMenu}. The associated menu is always updated to contain
 * entries for all internal frames in the desktop pane. Selecting such a frame
 * entry selects the corresponding internal frame. In addition, actions for
 * arranging the frames are added to the menu.
 * 
 * @author Michael Forster
 * @version $Revision: 5768 $ $Date: 2008-11-17 15:30:14 +0100 (Mon, 17 Nov
 *          2008) $
 */
public class DesktopMenuManager implements MenuListener {
    /** A enum is better than three int constants. */
    private enum TileMode {
        TILE, TILE_HORIZONTALLY, TILE_VERTICALLY;
    }

    /** The bundle of this class. */
    private static final Bundle bundle = Bundle.getCoreBundle();

    /** The associated desktop */
    private JDesktopPane desktop;

    /** The associated menu */
    private JMenu menu;

    /** Comparator used to order the window menu items. */
    private Comparator<JInternalFrame> order;

    /** Menu items created by this manager */
    private List<JComponent> windowItems = new LinkedList<JComponent>();

    /**
     * Create a MenuManager object and associate it with a desktop and a menu.
     * 
     * @param desktop
     *            The associated desktop
     * @param menu
     *            The associated menu
     * @param order
     *            Comparator used to order the window menu items.
     * 
     * @throws NullPointerException
     *             if a passed parameter is null
     */
    public DesktopMenuManager(JDesktopPane desktop, JMenu menu,
            Comparator<JInternalFrame> order) {
        if (desktop == null)
            throw new NullPointerException("desktop must not be null");

        if (menu == null)
            throw new NullPointerException("menu must not be null");

        this.desktop = desktop;
        this.menu = menu;
        this.order = order;

        menu.addMenuListener(this);
    }

    /**
     * Dispose this manager. Reset the menu, remove all listeners and make this
     * class eligible for garbage collection.
     */
    public void dispose() {
        clearMenu();
        menu.removeMenuListener(this);
    }

    /**
     * Ignored.
     * 
     * @see javax.swing.event.MenuListener#menuCanceled(javax.swing.event.MenuEvent)
     */
    public void menuCanceled(MenuEvent e) {
    }

    /**
     * Ignored.
     * 
     * @see javax.swing.event.MenuListener#menuDeselected(javax.swing.event.MenuEvent)
     */
    public void menuDeselected(MenuEvent e) {
    }

    /**
     * Updates the associated menu.
     * 
     * @see javax.swing.event.MenuListener#menuSelected(javax.swing.event.MenuEvent)
     */
    public void menuSelected(MenuEvent e) {
        clearMenu();
        fillMenu();
    }

    /**
     * Add a separator to the associated menu.
     */
    private void addSeparator() {
        JSeparator sep = new JPopupMenu.Separator();
        menu.add(sep);
        windowItems.add(sep);
    }

    /**
     * Arrange all internal frames in cascading order.
     */
    private void cascade() {
        final int DX = 26; // horizontal displacement
        final int DY = 26; // vertical displacement

        restoreFrames();

        JInternalFrame[] frames = desktop.getAllFrames();

        Dimension deskSize = desktop.getSize();
        Dimension minSize = minimumFrameSize();

        // number of frames to be placed in one turn
        int horizontal = 1 + ((deskSize.width - minSize.width) / DX);
        int vertical = 1 + ((deskSize.height - minSize.height) / DY);
        int framesCount = Math.min(frames.length, Math
                .min(horizontal, vertical));

        // calculate frame positions
        for (int i = 0; i < frames.length; i++) {
            JInternalFrame frame = frames[i];

            int x = ((frames.length - i - 1) % framesCount * DX);
            int y = ((frames.length - i - 1) % framesCount * DY);
            int width = deskSize.width - (DX * (framesCount - 1));
            int height = deskSize.height - (DY * (framesCount - 1));

            frame.setBounds(x, y, width, height);
        }
    }

    /**
     * Remove all created menu entries.
     */
    private void clearMenu() {
        for (JComponent component : windowItems) {
            menu.remove(component);
        }

        windowItems.clear();
    }

    /**
     * Fill entries into the menu.
     */
    private void fillMenu() {
        if (menu.getMenuComponentCount() > 0) {
            addSeparator();
        }

        JMenuItem item = new JMenuItem(bundle.getString("menu.window.tile"));
        menu.add(item);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                tile(TileMode.TILE);
            }
        });
        windowItems.add(item);

        item = new JMenuItem(bundle.getString("menu.window.htile"));
        menu.add(item);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                tile(TileMode.TILE_HORIZONTALLY);
            }
        });
        windowItems.add(item);

        item = new JMenuItem(bundle.getString("menu.window.vtile"));
        menu.add(item);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                tile(TileMode.TILE_VERTICALLY);
            }
        });
        windowItems.add(item);

        item = new JMenuItem(bundle.getString("menu.window.cascade"));
        menu.add(item);
        item.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                cascade();
            }
        });
        windowItems.add(item);

        JInternalFrame[] frames = desktop.getAllFrames();
        Arrays.sort(frames, order);

        if (frames.length > 0) {
            addSeparator();
        }

        JInternalFrame currentFrame = desktop.getSelectedFrame();
        for (final JInternalFrame frame : frames) {
            item = new FrameMenuItem(frame);
            item.setSelected(frame == currentFrame);
            menu.add(item);
            windowItems.add(item);
        }
    }

    /**
     * Calculate the smalles minimum size of all frames.
     * 
     * @return the smalles minimum size of all frames
     */
    private Dimension minimumFrameSize() {
        JInternalFrame[] frames = desktop.getAllFrames();

        Dimension result = desktop.getSize();

        for (JInternalFrame frame : frames) {
            Dimension minSize = frame.getMinimumSize();

            result.width = Math.min(result.width, minSize.width);
            result.height = Math.min(result.height, minSize.height);
        }

        return result;
    }

    /**
     * De-iconify and de-maximize all frames.
     */
    private void restoreFrames() {
        JInternalFrame[] frames = desktop.getAllFrames();
        try {
            for (JInternalFrame frame : frames) {
                frame.setMaximum(false);
                frame.setIcon(false);
            }
        } catch (PropertyVetoException e) {
            e.printStackTrace();
            throw new AssertionError("PropertyVetoException caught.");
        }
    }

    /**
     * Arrange all internal frames in grid fashion.
     */
    private void tile(TileMode mode) {
        restoreFrames();

        JInternalFrame[] frames = desktop.getAllFrames();

        Dimension deskSize = desktop.getSize();

        int cols = 1;
        int rows = 1;

        switch (mode) {
        case TILE:
            cols = (int) Math.ceil(Math.sqrt(frames.length));
            rows = (int) Math.ceil(frames.length / (double) cols);
            break;
        case TILE_HORIZONTALLY:
            cols = frames.length;
            rows = 1;
            break;
        case TILE_VERTICALLY:
            cols = 1;
            rows = frames.length;
            break;
        default:
            throw new AssertionError("Missing case: " + mode);
        }

        // calculate frame positions
        for (int i = 0; i < frames.length; i++) {
            JInternalFrame frame = frames[i];

            int width = deskSize.width / cols;
            int height = deskSize.height / rows;

            int x = (i % cols) * width;
            int y = (i / cols) * height;

            // fill up last row and column
            if ((i % cols) == (cols - 1)) {
                width = deskSize.width - x;
            }

            if ((i / cols) == (rows - 1)) {
                height = deskSize.height - y;
            }

            frame.setBounds(x, y, width, height);
        }
    }

    /**
     * A menu item associated to a frame. Selecting the item selects the
     * associated frame
     * 
     * @author Michael Forster
     * @version $Revision: 5768 $ $Date: 2008-11-17 15:30:14 +0100 (Mon, 17 Nov
     *          2008) $
     */
    class FrameMenuItem extends JRadioButtonMenuItem implements ActionListener {
        /**
         * 
         */
        private static final long serialVersionUID = 3294720195507912252L;
        /** The associated frame */
        private JInternalFrame frame;

        /**
         * Create a WindowMenuItem objectand associated it to a frame.
         * 
         * @param frame
         *            The associated frame.
         */
        public FrameMenuItem(JInternalFrame frame) {
            super(frame.getTitle());
            this.frame = frame;
            addActionListener(this);
        }

        /**
         * Selects the associated frame
         * 
         * @param event
         *            ignored
         */
        public void actionPerformed(ActionEvent event) {
            JDesktopPane parent = (JDesktopPane) frame.getParent();
            parent.getDesktopManager().activateFrame(frame);
            // BugFix for id #43:
            // #activateFrame only deselects the previous active frame,
            // but does not select the new one. (WHY??)
            // Manually select it in order to trigger necessary events.
            try {
                frame.setSelected(true);
            } catch (PropertyVetoException e) {
                e.printStackTrace(); // better use logger instead?
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
