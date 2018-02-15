// =============================================================================
//
//   ReplacedImageElement.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label;

import java.awt.Point;

import org.graffiti.plugins.views.fast.FastImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.layout.LayoutContext;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ReplacedImageElement<L extends Label<L, LC>, LC extends LabelCommand>
        implements ReplacedElement {
    private FastImage<L, LC> image;
    private int x;
    private int y;

    public ReplacedImageElement(FastImage<L, LC> image) {
        this.image = image;
    }

    public void detach(LayoutContext context) {
    }

    public int getIntrinsicHeight() {
        return image.getHeight();
    }

    public int getIntrinsicWidth() {
        return image.getWidth();
    }

    public Point getLocation() {
        return new Point(x, y);
    }

    public boolean isRequiresInteractivePaint() {
        return true;
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public FastImage<L, LC> getImage() {
        return image;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
