package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.plugins.scripting.Scope;

/**
 * {@code AttributeHandler} for {@code DoubleAttribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 */
final class DoubleAttributeHandler extends AttributeHandler<DoubleAttribute> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object get(DoubleAttribute attribute, Scope scope) {
        return attribute.getDouble();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(DoubleAttribute attribute, Object value) {
        attribute.setDouble(((Number) value).doubleValue());
    }
}