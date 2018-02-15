package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.ImageAttribute;
import org.graffiti.graphics.RenderedImageAttribute;
import org.graffiti.plugins.views.fast.FastView;

/**
 * Handles changes to the {@code RenderedImageAttribute} for background images.
 * 
 * @author Andreas Glei&szlig;ner
 * @see RenderedImageAttribute
 */
public class RenderedImageAttributeHandler extends
        AncestorAttributeHandler<ImageAttribute> {
    /**
     * The handling is delegated to this handler.
     */
    private ImageAttributeHandler handler;

    /**
     * Constructs a {@code RenderedImageAttributeHandler}.
     * 
     * @param handler
     *            the handler to which the reaction is delegated.
     */
    public RenderedImageAttributeHandler(ImageAttributeHandler handler) {
        super(IMAGE);
        this.handler = handler;
    }

    /**
     * {@inheritDoc} This handler accepts the background image attribute, with a
     * path of {@link GraphicAttributeConstants#BGIMAGE_PATH}.
     */
    @Override
    protected boolean acceptsAttribute(Node node, ImageAttribute attribute) {
        return equalsPath(attribute, BGIMAGE_PATH);
    }

    /**
     * {@inheritDoc} This handler accepts the background image attribute, with a
     * path of {@link GraphicAttributeConstants#BGIMAGE_PATH}.
     */
    @Override
    protected boolean acceptsAttribute(Edge edge, ImageAttribute attribute) {
        return equalsPath(attribute, BGIMAGE_PATH);
    }

    /**
     * {@inheritDoc} This handler delegates to the handler passed to the
     * constructor.
     */
    @Override
    protected void onChange(Node node, ImageAttribute attribute,
            FastView fastView) {
        handler.onChange(node, attribute, fastView);
    }

    /**
     * {@inheritDoc} This handler delegates to the handler passed to the
     * constructor.
     */
    @Override
    protected void onChange(Edge edge, ImageAttribute attribute,
            FastView fastView) {
        handler.onChange(edge, attribute, fastView);
    }
}
