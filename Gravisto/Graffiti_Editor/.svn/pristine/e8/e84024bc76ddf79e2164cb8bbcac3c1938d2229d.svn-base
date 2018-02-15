package org.graffiti.util.ext;

import java.lang.reflect.Field;

/**
 * Extension methods for {@link Field}s.
 * 
 * @author Harald Frankenberger
 */
public class Fields {

    private Fields() {
    }

    /**
     * Returns <code>true</code> if this field has the given name.
     * 
     * @param this_
     *            this field
     * @param name
     *            the string to check against this field's name
     * @return <code>true</code> if this field has the given name
     */
    public static boolean hasName(Field this_, String name) {
        return Objects.equals(name, this_.getName());
    }

    /**
     * Sets this field on the given target object to the given value.
     * 
     * @param this_
     *            this field
     * @param target
     *            the target to set this field on
     * @param value
     *            this fields new value
     * @throws RuntimeException
     *             wrapping {@link IllegalAccessException} if this field is not
     *             accessible on the given target object
     */
    public static void set(Field this_, Object target, Object value) {
        try {
            this_.set(target, value);
        } catch (IllegalAccessException e) {
            throw Throwables.asRuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(Field this_, Object source) {
        try {
            return (T) this_.get(source);
        } catch (IllegalAccessException e) {
            throw Throwables.asRuntimeException(e);
        }
    }

}
