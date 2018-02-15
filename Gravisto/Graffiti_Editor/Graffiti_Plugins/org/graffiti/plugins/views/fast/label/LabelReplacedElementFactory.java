// =============================================================================
//
//   LabelReplacedElementFactory.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.label;

import java.net.URISyntaxException;

import org.graffiti.graph.GraphElement;
import org.graffiti.plugins.views.fast.FastImage;
import org.graffiti.plugins.views.fast.ImageManager;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class LabelReplacedElementFactory<L extends Label<L, LC>, LC extends LabelCommand>
        implements ReplacedElementFactory {
    private ImageManager<L, LC> imageManager;

    protected LabelReplacedElementFactory(ImageManager<L, LC> imageManager) {
        this.imageManager = imageManager;
    }

    @SuppressWarnings("unchecked")
    public ReplacedElement createReplacedElement(LayoutContext layoutContext,
            BlockBox blockBox, UserAgentCallback userAgent, int cssWidth,
            int cssHeight) {
        L label = ((CommandListFactory<L, LC>) ((UserAgentCallbackProxy) userAgent)
                .getFactory()).getLabel();
        Element element = blockBox.getElement();
        String tagName = element.getTagName();
        // System.out.println("WANT REPLACE " + tagName);
        if (tagName.equals("img"))
            return createImageElement(element, label, cssWidth, cssHeight);
        return null;
    }

    public void remove(Element element) {
    }

    public void reset() {
    }

    private ReplacedImageElement<L, LC> createImageElement(
            Element imageElement, L label, int cssWidth, int cssHeight) {
        GraphElement graphElement = label.getGraphElement();
        FastImage<L, LC> fastImage = imageManager.getErrorImage();
        String src = imageElement.getAttribute("src");
        if (src.startsWith(GravistoAttributeUri.SCHEME)) {
            try {
                GravistoAttributeUri uri = new GravistoAttributeUri(src);
                fastImage = imageManager.getImage(graphElement, uri
                        .getAttributePath(), label, true);
            } catch (URISyntaxException e) {
            }
        } else {
            fastImage = imageManager.getImage(graphElement, src, label, false);
        }
        int width = cssWidth;
        if (width == -1) {
            String widthString = imageElement.getAttribute("width");
            try {
                width = Integer.parseInt(widthString);
            } catch (NumberFormatException e) {
                width = fastImage.getWidth();
            }
        }
        int height = cssHeight;
        if (height == -1) {
            String heightString = imageElement.getAttribute("height");
            try {
                height = Integer.parseInt(heightString);
            } catch (NumberFormatException e) {
                height = fastImage.getHeight();
            }
        }
        fastImage = fastImage.getScaled(width, height);
        return new ReplacedImageElement<L, LC>(fastImage);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
