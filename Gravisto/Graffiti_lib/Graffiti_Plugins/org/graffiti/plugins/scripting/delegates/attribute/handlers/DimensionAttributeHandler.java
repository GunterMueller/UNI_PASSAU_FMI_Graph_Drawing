package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.ScriptingDelegate;
import org.graffiti.plugins.scripting.delegates.VectorDelegate;

/**
 * {@code AttributeHandler} for {@code DimensionAttribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 */
public final class DimensionAttributeHandler extends
        AttributeHandler<DimensionAttribute> {
    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(final DimensionAttribute attribute, Scope scope) {
        return new VectorDelegate(scope, attribute.getWidth(), attribute
                .getHeight()) {
            @Override
            public void put(int index, Object value) {
                double v = ((Number) value).doubleValue();
                switch (index) {
                case 0:
                    attribute.setWidth(v);
                    break;
                case 1:
                    attribute.setHeight(v);
                    break;
                }
                super.put(index, value);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void set(DimensionAttribute attribute, Object value) {
        if (value instanceof VectorDelegate) {
            VectorDelegate vector = (VectorDelegate) value;
            Object v = vector.get(0);
            if (v != ScriptingDelegate.UNDEFINED) {
                attribute.setWidth((Double) v);
            }
            v = vector.get(1);
            if (v != ScriptingDelegate.UNDEFINED) {
                attribute.setHeight((Double) v);
            }
        } else if (value instanceof Object[]) {
            try {
                Object[] array = (Object[]) value;
                attribute.setWidth((Double) array[0]);
                attribute.setHeight((Double) array[1]);
            } catch (Exception e) {
            }
        }
    }
}
