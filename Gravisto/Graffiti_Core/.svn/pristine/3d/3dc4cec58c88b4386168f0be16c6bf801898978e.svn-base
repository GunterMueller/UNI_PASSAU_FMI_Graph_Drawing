package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import java.awt.Color;

import org.graffiti.graphics.ColorAttribute;
import org.graffiti.plugins.scripting.DelegateWrapperUtil;
import org.graffiti.plugins.scripting.Scope;

/**
 * {@code AttributeHandler} for {@code ColorAttribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 */
final class ColorAttributeHandler extends AttributeHandler<ColorAttribute> {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Object get(ColorAttribute attribute, Scope scope) {
        return DelegateWrapperUtil.wrap(attribute.getColor(), scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void set(ColorAttribute attribute, Object value) {
        attribute.setColor((Color) value);
    }
}
