package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import org.graffiti.attributes.FloatAttribute;
import org.graffiti.plugins.scripting.Scope;

/**
 * {@code AttributeHandler} for {@code FloatAttribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 */
final class FloatAttributeHandler extends AttributeHandler<FloatAttribute> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object get(FloatAttribute attribute, Scope scope) {
        return attribute.getFloat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(FloatAttribute attribute, Object value) {
        attribute.setFloat(((Number) value).floatValue());
    }
}