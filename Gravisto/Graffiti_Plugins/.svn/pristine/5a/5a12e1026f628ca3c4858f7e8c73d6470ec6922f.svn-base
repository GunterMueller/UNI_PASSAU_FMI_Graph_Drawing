package org.graffiti.plugins.scripting.delegates.attribute;

import java.lang.reflect.Field;

import org.graffiti.attributes.Attributable;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegates.attribute.handlers.CoordinateAttributeHandler;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;

public class CoordinateAttributeDelegate extends AttributeFieldDelegate<Object> {
    private static final CoordinateAttributeHandler HANDLER = new CoordinateAttributeHandler();

    private CoordinateAttribute coordinate;

    public CoordinateAttributeDelegate(Scope scope, Attributable attributable,
            String path) {
        super(Object.class, scope, attributable, path);
        coordinate = (CoordinateAttribute) attribute;
    }

    public CoordinateAttributeDelegate(ObjectDelegate delegate, Field field) {
        super(Object.class, delegate, field);
        coordinate = (CoordinateAttribute) attribute;
    }

    @Override
    public Object get() throws ScriptingException {
        return HANDLER.get(coordinate, scope);
    }

    @Override
    public void set(Object value) throws ScriptingException {
        HANDLER.set(coordinate, value);
    }
}
