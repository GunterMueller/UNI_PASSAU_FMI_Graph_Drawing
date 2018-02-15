package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import org.graffiti.attributes.ShortAttribute;
import org.graffiti.plugins.scripting.Scope;

/**
 * {@code AttributeHandler} for {@code ShortAttribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 */
final class ShortAttributeHandler extends AttributeHandler<ShortAttribute> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object get(ShortAttribute attribute, Scope scope) {
        return attribute.getShort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(ShortAttribute attribute, Object value) {
        attribute.setShort(((Number) value).shortValue());
    }
}