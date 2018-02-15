package org.graffiti.plugins.views.fast.label;

import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.UserInterface;

/**
 * Dummy {@code UserInterface} that returns {@code false} on all methods as the
 * user is not supposed to interact with the HTML content of labels.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Label
 * @see CommandListFactory
 */
final public class NullUserInterface implements UserInterface {
    /**
     * {@inheritDoc}
     * 
     * @return {@code false}.
     */
    public boolean isHover(Element e) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code false}.
     */
    public boolean isActive(Element e) {
        return false;
    }

    /**
     * {@inheritDoc}
     * 
     * @return {@code false}.
     */
    public boolean isFocus(Element e) {
        return false;
    }
}
