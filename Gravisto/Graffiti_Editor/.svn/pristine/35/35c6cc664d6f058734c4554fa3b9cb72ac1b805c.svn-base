package org.graffiti.plugins.scripting.delegates.attribute.handlers;

import java.util.HashMap;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.FloatAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.LongAttribute;
import org.graffiti.attributes.ShortAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.LineModeAttribute;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.delegate.ScriptingDelegate;

public abstract class AttributeHandler<T extends Attribute> {
    private static Map<Class<? extends Attribute>, AttributeHandler<?>> map;

    static {
        map = new HashMap<Class<? extends Attribute>, AttributeHandler<?>>();
        add(BooleanAttribute.class, new BooleanAttributeHandler());
        add(CollectionAttribute.class, new CollectionAttributeHandler());
        add(ColorAttribute.class, new ColorAttributeHandler());
        add(CoordinateAttribute.class, new CoordinateAttributeHandler());
        add(DimensionAttribute.class, new DimensionAttributeHandler());
        add(DoubleAttribute.class, new DoubleAttributeHandler());
        add(FloatAttribute.class, new FloatAttributeHandler());
        add(IntegerAttribute.class, new IntegerAttributeHandler());
        add(LineModeAttribute.class, new LineModeAttributeHandler());
        add(LongAttribute.class, new LongAttributeHandler());
        add(ShortAttribute.class, new ShortAttributeHandler());
        add(StringAttribute.class, new StringAttributeHandler());
        add(GridAttribute.class, new GridAttributeHandler());
    }

    private static <T extends Attribute> void add(Class<T> attributeClass,
            AttributeHandler<T> attribute) {
        map.put(attributeClass, attribute);
    }

    AttributeHandler() {
    }

    public static Object get(Scope scope, Attribute attribute) {
        Class<?> attributeClass = attribute.getClass();

        while (Attribute.class.isAssignableFrom(attributeClass)) {
            AttributeHandler<?> handler = map.get(attributeClass);
            if (handler != null)
                return handler.get(attribute, scope);
            for (Class<?> attributeCls : attributeClass.getInterfaces()) {
                AttributeHandler<?> h = map.get(attributeCls);
                if (h != null)
                    return h.get(attribute, scope);
            }
            attributeClass = attributeClass.getSuperclass();
        }

        return ScriptingDelegate.UNDEFINED;
    }

    public static void set(Object value, Attribute attribute) {
        Class<?> attributeClass = attribute.getClass();

        while (Attribute.class.isAssignableFrom(attributeClass)) {
            AttributeHandler<?> handler = map.get(attributeClass);
            if (handler != null) {
                handler.set(attribute, value);
                return;
            }
            attributeClass = attributeClass.getSuperclass();
        }
    }

    protected abstract Object get(T attribute, Scope scope);

    private Object get(Object attribute, Scope scope) {
        // Save because of the add method.
        @SuppressWarnings("unchecked")
        T t = (T) attribute;
        return get(t, scope);
    }

    protected abstract void set(T attribute, Object value);

    private void set(Object attribute, Object value) {
        @SuppressWarnings("unchecked")
        T t = (T) attribute;
        set(t, value);
    }
}
