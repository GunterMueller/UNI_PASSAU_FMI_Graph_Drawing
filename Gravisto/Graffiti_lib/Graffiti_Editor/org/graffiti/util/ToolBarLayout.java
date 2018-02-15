// =============================================================================
//
//   ToolBarLayout.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ToolBarLayout.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

/**
 * An extension of {@link java.awt.FlowLayout} that correctly calculates the
 * height of a toolbar panel.
 * 
 * @author Michael Forster
 * @version $Revision: 5768 $ $Date: 2006-01-03 17:20:07 +0100 (Di, 03 Jan 2006)
 *          $
 */
public class ToolBarLayout extends FlowLayout {

    /**
     * 
     */
    private static final long serialVersionUID = -9094581492654351756L;

    /*
     * @see FlowLayout#FlowLayout()
     */
    public ToolBarLayout() {
        super(LEFT);
    }

    /*
     * @see FlowLayout#FlowLayout(int)
     */
    public ToolBarLayout(int align) {
        super(align);
    }

    /*
     * @see FlowLayout#FlowLayout(int, int, int)
     */
    public ToolBarLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    /*
     * @see java.awt.FlowLayout#preferredLayoutSize(java.awt.Container)
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int maxwidth = target.getWidth()
                    - (insets.left + insets.right + (getHgap() * 2));
            int x = 0;
            int y = insets.top + insets.bottom + (getVgap() * 2);
            int rowHeight = 0;

            for (int i = 0; i < target.getComponentCount(); i++) {
                Component m = target.getComponent(i);

                if (m.isVisible()) {
                    Dimension d = m.getPreferredSize();
                    m.setSize(d.width, d.height);

                    if ((x == 0) || ((x + d.width) <= maxwidth)) // same row
                    {
                        if (x > 0) {
                            x += getHgap();
                        }

                        x += d.width;
                        rowHeight = Math.max(rowHeight, d.height);
                    } else
                    // new row
                    {
                        y += (getVgap() + rowHeight);

                        x = d.width;
                        rowHeight = d.height;
                    }
                }
            }

            return new Dimension(target.getWidth(), y + rowHeight);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
