package org.graffiti.plugins.scripting.delegates.attribute;

import java.lang.reflect.Field;

import org.graffiti.attributes.Attributable;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegates.attribute.handlers.DimensionAttributeHandler;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;

public class DimensionAttributeDelegate extends AttributeFieldDelegate<Object> {
    private static final DimensionAttributeHandler HANDLER = new DimensionAttributeHandler();

    private DimensionAttribute dimension;

    public DimensionAttributeDelegate(Scope scope, Attributable attributable,
            String path) {
        super(Object.class, scope, attributable, path);
        dimension = (DimensionAttribute) attribute;
    }

    public DimensionAttributeDelegate(ObjectDelegate delegate, Field field) {
        super(Object.class, delegate, field);
        dimension = (DimensionAttribute) attribute;
    }

    @Override
    public Object get() throws ScriptingException {
        return HANDLER.get(dimension, scope);
    }

    @Override
    public void set(Object value) throws ScriptingException {
        HANDLER.set(dimension, value);
    }
}
