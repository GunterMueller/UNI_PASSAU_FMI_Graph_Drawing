package org.graffiti.plugins.scripting.delegates.attribute;

import java.awt.Color;
import java.lang.reflect.Field;

import org.graffiti.attributes.Attributable;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;

public class ColorAttributeDelegate extends AttributeFieldDelegate<Color> {
    public ColorAttributeDelegate(Scope scope, Attributable attributable,
            String path) {
        super(Color.class, scope, attributable, path);
    }

    public ColorAttributeDelegate(ObjectDelegate delegate, Field field) {
        super(Color.class, delegate, field);
    }

    @Override
    public Object get() throws ScriptingException {
        return wrap(((ColorAttribute) attribute).getColor());
    }

    @Override
    public void set(Color value) throws ScriptingException {
        ((ColorAttribute) attribute).setColor(value);
    }
}
