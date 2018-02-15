package org.graffiti.plugins.scripting.delegates.attribute;

import java.lang.reflect.Field;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.plugins.scripting.DelegateWrapperUtil;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.FieldDelegate;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.Unwrappable;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;

public class AttributeFieldDelegate<T> extends FieldDelegate<T> {
    protected Scope scope;
    protected Attributable attributable;
    protected Attribute attribute;

    public AttributeFieldDelegate(Class<T> type, Scope scope,
            Attributable attributable, String path) {
        super(type);
        this.scope = scope;
        this.attributable = attributable;
        initializeAttribute(path);
    }

    public AttributeFieldDelegate(Class<T> type, ObjectDelegate delegate,
            Field field) {
        super(type);
        this.scope = delegate.getScope();
        this.attributable = (Attributable) ((Unwrappable<?>) delegate).unwrap();
        AttributePath path = field.getAnnotation(AttributePath.class);
        if (path == null)
            throw new IllegalArgumentException(
                    "Field must be annotated with AttributePath");
        else {
            initializeAttribute(path.value());
        }
    }

    private void initializeAttribute(String path) {
        try {
            attribute = attributable.getAttribute(path);
        } catch (AttributeNotFoundException e) {
        }
    }

    @Override
    public Object get() throws ScriptingException {
        return wrap(attribute.getValue());
    }

    @Override
    public void set(T value) throws ScriptingException {
        attribute.setValue(DelegateWrapperUtil.unwrap(value, type));
    }

    protected final Object wrap(Object value) {
        return DelegateWrapperUtil.wrap(value, scope);
    }
}
