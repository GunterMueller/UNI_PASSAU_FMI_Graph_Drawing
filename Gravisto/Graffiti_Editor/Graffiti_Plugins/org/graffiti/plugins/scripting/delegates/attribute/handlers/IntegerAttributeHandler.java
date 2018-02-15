package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.plugins.scripting.Scope;

/**
 * {@code AttributeHandler} for {@code IntegerAttribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 */
final class IntegerAttributeHandler extends AttributeHandler<IntegerAttribute> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object get(IntegerAttribute attribute, Scope scope) {
        return attribute.getInteger();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(IntegerAttribute attribute, Object value) {
        attribute.setInteger(((Number) value).intValue());
    }
}