// =============================================================================
//
//   MaximizeLayout.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MaximizeLayout.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;

import javax.swing.JInternalFrame;

/**
 * Layour wrapper that modifies the behaviour of a
 * {@link java.awt.LayoutManager} for maximized
 * {@link javax.swing.JInternalFrame}s. If the frame is maximized, its title bar
 * is hidden. This is intended to be used together with
 * {@link org.graffiti.util.MaximizeManager}.
 * 
 * @author Michael Forster
 * @version $Revision: 5768 $ $Date: 2006-01-03 14:21:54 +0100 (Di, 03 Jan 2006)
 *          $
 * 
 * @see org.graffiti.util.MaximizeManager
 * @see org.graffiti.util.MaximizeFrame
 */
public class MaximizeLayout implements LayoutManager {

    /** Original layout of the frame. Handles most of the method calls. */
    private LayoutManager originalLayout;

    /**
     * Wrap an existing layout and overide its behaviour for maximized frames.
     * 
     * @param originalLayout
     *            The wrapped layout.
     */
    public MaximizeLayout(LayoutManager originalLayout) {
        this.originalLayout = originalLayout;
    }

    /*
     * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String,
     * java.awt.Component)
     */
    public void addLayoutComponent(String name, Component comp) {
        originalLayout.addLayoutComponent(name, comp);
    }

    /*
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    public void layoutContainer(Container parent) {
        if ((parent != null) && parent instanceof JInternalFrame) {
            JInternalFrame frame = (JInternalFrame) parent;

            if (frame.isMaximum()) {
                frame.getRootPane().setBounds(0, 0, frame.getWidth(),
                        frame.getHeight());

                return;
            }
        }

        originalLayout.layoutContainer(parent);
    }

    /*
     * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
     */
    public Dimension minimumLayoutSize(Container parent) {
        return originalLayout.minimumLayoutSize(parent);
    }

    /*
     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     */
    public Dimension preferredLayoutSize(Container parent) {
        return originalLayout.preferredLayoutSize(parent);
    }

    /*
     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
     */
    public void removeLayoutComponent(Component comp) {
        originalLayout.removeLayoutComponent(comp);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
