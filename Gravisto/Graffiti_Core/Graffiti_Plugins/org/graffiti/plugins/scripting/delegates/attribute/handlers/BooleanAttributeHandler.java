package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.plugins.scripting.Scope;

/**
 * {@code AttributeHandler} for {@code BooleanAttribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 */
final class BooleanAttributeHandler extends AttributeHandler<BooleanAttribute> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object get(BooleanAttribute attribute, Scope scope) {
        return attribute.getBoolean();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(BooleanAttribute attribute, Object value) {
        attribute.setBoolean((Boolean) value);
    }
}