package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import org.graffiti.attributes.StringAttribute;
import org.graffiti.plugins.scripting.Scope;

/**
 * {@code AttributeHandler} for {@code StringAttribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 */
final class StringAttributeHandler extends AttributeHandler<StringAttribute> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object get(StringAttribute attribute, Scope scope) {
        return attribute.getString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(StringAttribute attribute, Object value) {
        attribute.setString((String) value);
    }
}