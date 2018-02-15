package org.graffiti.plugins.scripting;

import java.awt.geom.Point2D;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.Dash;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugin.view.interactive.SlotAssignmentException;
import org.graffiti.plugin.view.interactive.SlotMap;
import org.graffiti.plugins.scripting.delegate.BlackBoxDelegate;
import org.graffiti.plugins.scripting.delegate.Unwrappable;
import org.graffiti.plugins.scripting.delegates.BufferedCollectionDelegate;
import org.graffiti.plugins.scripting.delegates.DashDelegate;
import org.graffiti.plugins.scripting.delegates.EdgeDelegate;
import org.graffiti.plugins.scripting.delegates.NodeDelegate;
import org.graffiti.plugins.scripting.delegates.VectorDelegate;

/**
 * Utility class for wrapping and unwrapping objects in delegates.
 * 
 * @author Andreas Glei&szlig;ner
 */
public class DelegateWrapperUtil {
    public static void put(SlotMap slotMap, String id, Object object) {
        slotMap.put(id, unwrap(object, (Class<?>) null));
    }

    public static <S> void put(SlotMap slotMap, Slot<S> slot, Object object) {

        S value = unwrap(object, slot);
        if (value == null)
            throw new SlotAssignmentException(slot.getId(), object.getClass(),
                    slot.getType());
        slotMap.put(slot, value);
    }

    private static <S> S unwrap(Object object, Slot<S> slot) {
        Object result = unwrap(object, slot.getType());
        Class<S> targetHint = slot.getType();
        if (targetHint.isInstance(result))
            return targetHint.cast(result);
        else
            return null;
    }

    public static Object unwrap(Object object, Class<?> targetHint) {
        if (object == null)
            return null;
        else if (targetHint == null
                && ((object instanceof Boolean) || (object instanceof Number) || (object instanceof String)))
            return object;
        else if (targetHint != null && targetHint.isInstance(object))
            return object;
        else if (object instanceof Unwrappable<?>)
            return ((Unwrappable<?>) object).unwrap();
        else if (targetHint != null
                && (targetHint.isPrimitive() || Number.class
                        .isAssignableFrom(targetHint))
                && object instanceof Number) {
            Number number = (Number) object;
            if (targetHint.equals(byte.class) || targetHint.equals(Byte.class))
                return number.byteValue();
            else if (targetHint.equals(double.class)
                    || targetHint.equals(Double.class))
                return number.doubleValue();
            else if (targetHint.equals(float.class)
                    || targetHint.equals(Float.class))
                return number.floatValue();
            else if (targetHint.equals(int.class)
                    || targetHint.equals(Integer.class))
                return number.intValue();
            else if (targetHint.equals(long.class)
                    || targetHint.equals(Long.class))
                return number.longValue();
        }
        try {
            if (targetHint != null && targetHint.isEnum()
                    && object instanceof String)
                return unwrapEnum(targetHint, (String) object);
            return object;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static <S> S unwrapEnum(Class<S> target, String name) {
        @SuppressWarnings("unchecked")
        Class enumClass = target;

        @SuppressWarnings("unchecked")
        S result = (S) Enum.valueOf(enumClass, name);

        return result;
    }

    public static Object get(SlotMap slotMap, String id, Scope scope) {
        return wrap(slotMap.get(id), scope);
    }

    public static Object wrap(Object object, Scope scope) {
        if (object == null)
            return null;
        else if ((object instanceof Boolean) || (object instanceof Number)
                || (object instanceof String))
            return object;
        else if (object instanceof Node)
            return scope.getCanonicalDelegate((Node) object,
                    new NodeDelegate.Factory(scope));
        else if (object instanceof Edge)
            return scope.getCanonicalDelegate((Edge) object,
                    new EdgeDelegate.Factory(scope));
        else if (object instanceof Dash)
            return new DashDelegate(scope, (Dash) object);
        else if (object instanceof Point2D) {
            Point2D point = (Point2D) object;
            return new VectorDelegate(scope, point.getX(), point.getY());
        } else if (object instanceof Set<?>) {
            NodeDelegate.Factory nodeFactory = new NodeDelegate.Factory(scope);
            EdgeDelegate.Factory edgeFactory = new EdgeDelegate.Factory(scope);
            BufferedCollectionDelegate bcd = new BufferedCollectionDelegate(
                    scope);
            for (Object o : (Set<?>) object) {
                if (o instanceof Node) {
                    bcd.add(scope.getCanonicalDelegate((Node) o, nodeFactory));
                } else if (o instanceof Edge) {
                    bcd.add(scope.getCanonicalDelegate((Edge) o, edgeFactory));
                } else
                    return null;
            }
            return bcd;
        }
        return new BlackBoxDelegate<Object>(object);
    }
}
