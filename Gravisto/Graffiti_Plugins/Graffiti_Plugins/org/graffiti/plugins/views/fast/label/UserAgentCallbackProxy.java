package org.graffiti.plugins.views.fast.label;

import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.resource.CSSResource;
import org.xhtmlrenderer.resource.ImageResource;
import org.xhtmlrenderer.resource.XMLResource;

/**
 * Proxy class implementing {@code UserAgentCallback} that forwards some method
 * calls to a {@code CommandListFactory}. The {@link UserAgentCallback} must
 * previously be set by {@link #setFactory(CommandListFactory)}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class UserAgentCallbackProxy implements UserAgentCallback {
    /**
     * The {@code CommandListFactory} the method calls are forwared to.
     */
    private CommandListFactory<?, ?> factory;

    /**
     * Sets the {@code CommandListFactory} some method calls are forwarded to.
     * 
     * @param factory
     *            the {@code CommandListFactory} some method calls are forwarded
     *            to. May not be {@code null}.
     */
    public void setFactory(CommandListFactory<?, ?> factory) {
        if (factory == null)
            throw new NullPointerException();
        this.factory = factory;
    }

    /**
     * Returns the factory some method calls are forwared to.
     */
    public CommandListFactory<?, ?> getFactory() {
        return factory;
    }

    /**
     * {@inheritDoc} This implementation returns {@code null}.
     * 
     * @return {@code null}.
     */
    public String getBaseURL() {
        return null;
    }

    /**
     * {@inheritDoc} This implementation calls
     * {@link CommandListFactory#getCSSResource(String)}.
     */
    public CSSResource getCSSResource(String uri) {
        return factory.getCSSResource(uri);
    }

    /**
     * {@inheritDoc} This implementation calls
     * {@link CommandListFactory#getImageResource(String)}.
     */
    public ImageResource getImageResource(String uri) {
        return factory.getImageResource(uri);
    }

    /**
     * {@inheritDoc} This implementation calls
     * {@link CommandListFactory#getXMLResource(String)}.
     */
    public XMLResource getXMLResource(String uri) {
        return factory.getXMLResource(uri);
    }

    /**
     * {@inheritDoc} This implementation returns {@code false}.
     * 
     * @return {@code false}.
     */
    public boolean isVisited(String uri) {
        return false;
    }

    /**
     * {@inheritDoc} This implementation returns its argument.
     * 
     * @return its argument.
     */
    public String resolveURI(String uri) {
        return uri;
    }

    /**
     * {@inheritDoc} This implementation does nothing.
     */
    public void setBaseURL(String url) {
    }
}
