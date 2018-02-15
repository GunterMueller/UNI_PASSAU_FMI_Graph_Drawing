package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import org.graffiti.attributes.LongAttribute;
import org.graffiti.plugins.scripting.Scope;

/**
 * {@code AttributeHandler} for {@code LongAttribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 */
final class LongAttributeHandler extends AttributeHandler<LongAttribute> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object get(LongAttribute attribute, Scope scope) {
        return attribute.getLong();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(LongAttribute attribute, Object value) {
        attribute.setLong(((Number) value).longValue());
    }
}