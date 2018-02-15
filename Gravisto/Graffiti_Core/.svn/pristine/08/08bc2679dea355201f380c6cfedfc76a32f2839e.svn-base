package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import org.graffiti.attributes.ByteAttribute;
import org.graffiti.plugins.scripting.Scope;

/**
 * {@code AttributeHandler} for {@code ByteAttribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 */
final class ByteAttributeHandler extends AttributeHandler<ByteAttribute> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object get(ByteAttribute attribute, Scope scope) {
        return attribute.getByte();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(ByteAttribute attribute, Object value) {
        attribute.setByte(((Number) value).byteValue());
    }
}