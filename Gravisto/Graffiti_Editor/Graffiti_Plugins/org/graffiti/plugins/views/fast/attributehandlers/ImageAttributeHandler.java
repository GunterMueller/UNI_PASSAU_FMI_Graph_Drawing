// =============================================================================
//
//   RenderedImageAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ImageAttribute;
import org.graffiti.graphics.RenderedImageAttribute;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ImageAttributeHandler extends AttributeHandler<ImageAttribute> {
    public ImageAttributeHandler() {
        super(BGIMAGE);
    }

    @Override
    protected boolean acceptsAttribute(Edge edge, ImageAttribute attribute) {
        return true;
    }

    @Override
    protected boolean acceptsAttribute(Node node, ImageAttribute attribute) {
        return true;
    }

    @Override
    protected void onChange(Edge edge, ImageAttribute attribute,
            FastView fastView) {
        fastView.getImageManager().dropImage(edge,
                getNormalizedPath(attribute.getImage()));
    }

    @Override
    protected void onChange(Node node, ImageAttribute attribute,
            FastView fastView) {
        RenderedImageAttribute image = attribute.getImage();
        fastView.getImageManager().dropImage(node,
                getNormalizedPath(attribute.getImage()));
        fastView.getNodeChangeListener()
                .onChangeBackgroundImage(node, image.getImage(),
                        attribute.getMaximize(), attribute.getTiled());
    }

    @Override
    protected void onDelete(Edge edge, ImageAttribute attribute,
            FastView fastView) {
        fastView.getImageManager().dropImage(edge,
                getNormalizedPath(attribute.getImage()));
    }

    @Override
    protected void onDelete(Node node, ImageAttribute attribute,
            FastView fastView) {
        RenderedImageAttribute image = attribute.getImage();
        fastView.getImageManager().dropImage(node, getNormalizedPath(image));
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
