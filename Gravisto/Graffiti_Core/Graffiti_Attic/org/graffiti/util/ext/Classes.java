package org.graffiti.util.ext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Extension methods for {@link Class}es.
 * 
 * @author Harald Frankenberger
 */
public class Classes {

    private Classes() {
    }

    /**
     * Returns this class's field with the given name or <code>null</code> if no
     * such field exists.
     * 
     * @param this_
     *            this class
     * @param name
     *            the name of the field to be found
     * @return this class's field with the given name or <code>null</code> if no
     *         such field exists
     */
    public static Field findField(Class<?> this_, String name) {
        for (Field each : this_.getFields())
            if (Fields.hasName(each, name))
                return each;
        return null;
    }

    /**
     * Returns the method with the given signature of this class or
     * <code>null</code> if no such method exists.
     * 
     * @param this_
     *            this class
     * @param returnType
     *            the return type of the method to be found
     * @param name
     *            the name of the method to be found
     * @param parameterTypes
     *            the parameter types of the method to be found
     * @return the method with the given signature of this class or
     *         <code>null</code> if no such method exists
     */
    public static Method findMethod(Class<?> this_, Class<?> returnType,
            String name, Class<?>... parameterTypes) {
        for (Method each : this_.getMethods()) {
            if (Methods.hasSignature(each, returnType, name, parameterTypes))
                return each;
        }
        return null;
    }

    /**
     * Returns this class's void method with the given parameter types or
     * <code>null</code> if no such method exists.
     * 
     * @param this_
     *            this class
     * @param name
     *            the name of the method to be found
     * @param parameterTypes
     *            the parameter types of the method to be found
     * @return this class's void method with the given parameter types or
     *         <code>null</code> if no such method exists
     */
    public static Method findMethod(Class<?> this_, String name,
            Class<?>... parameterTypes) {
        return findMethod(this_, Void.TYPE, name, parameterTypes);
    }

    /**
     * Returns the method of this class, with the given return-type, name and
     * parameter-types; throws {@link RuntimeException} wrapping
     * {@link NoSuchMethodException} if no such method exists.
     * 
     * @param this_
     *            this class.
     * @param returnType
     *            the return-type of the method to return
     * @param name
     *            the name of the method to return
     * @param parameterTypes
     *            the parameter-types of the method to return
     * @return the method of this class, with the given return-type, name and
     *         parameter-types
     * @throws RuntimeException
     *             wrapping {@link NoSuchMethodException} if no method exists
     *             with the givne signature exists
     */
    public static Method getMethod(Class<?> this_, Class<String> returnType,
            String name, Class<String>... parameterTypes) {
        try {
            return this_.getMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            throw Throwables.asRuntimeException(e);
        }
    }

    /**
     * Returns a new instance of this class.
     * 
     * @param <T>
     *            The type of the object to create
     * @param this_
     *            this class
     * @return a new instance of this class
     * @throws RuntimeException
     *             wrapping {@link InstantiationException} if this class cannot
     *             be instantiated.
     * @throws RuntimeException
     *             wrapping {@link IllegalAccessException} if this class or its
     *             nullary constructor is not accessible.
     * @see Class#newInstance()
     */
    public static <T> T newInstance(Class<T> this_) {
        try {
            return this_.newInstance();
        } catch (InstantiationException e) {
            throw Throwables.asRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw Throwables.asRuntimeException(e);
        }
    }

}
