package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.ScriptingDelegate;
import org.graffiti.plugins.scripting.delegates.VectorDelegate;

/**
 * {@code AttributeHandler} for {@code CoordinateAttribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 */
public final class CoordinateAttributeHandler extends
        AttributeHandler<CoordinateAttribute> {
    /**
     * {@inheritDoc}
     */
    @Override
    public Object get(final CoordinateAttribute attribute, Scope scope) {
        return new VectorDelegate(scope, attribute.getX(), attribute.getY(),
                attribute.getZ()) {
            @Override
            public void put(int index, Object value) {
                double v = ((Number) value).doubleValue();
                switch (index) {
                case 0:
                    attribute.setX(v);
                    break;
                case 1:
                    attribute.setY(v);
                    break;
                case 2:
                    attribute.setZ(v);
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
    public void set(CoordinateAttribute attribute, Object value) {
        if (value instanceof VectorDelegate) {
            VectorDelegate vector = (VectorDelegate) value;
            Object v = vector.get(0);
            if (v != ScriptingDelegate.UNDEFINED) {
                attribute.setX((Double) v);
            }
            v = vector.get(1);
            if (v != ScriptingDelegate.UNDEFINED) {
                attribute.setY((Double) v);
            }
            v = vector.get(2);
            if (v != ScriptingDelegate.UNDEFINED) {
                attribute.setZ((Double) v);
            }
        } else if (value instanceof Object[]) {
            try {
                Object[] array = (Object[]) value;
                attribute.setX((Double) array[0]);
                attribute.setY((Double) array[1]);
                attribute.setZ((Double) array[2]);
            } catch (Exception e) {
            }
        }
    }
}
