package org.graffiti.util.ext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Extension methods for {@link Method}s.
 * 
 * @author Harald Frankenberger
 */
public class Methods {

    private Methods() {
    }

    /**
     * Returns <tt>true</tt> if this method has the specified signature. That
     * is: It returns the specified type, has the specified name and accepts the
     * specified formal parameters in the specified order.
     * 
     * @param this_
     *            this method
     * @param returnType
     *            this method's return type
     * @param name
     *            this method's name
     * @param parameterTypes
     *            this method's parameter types
     * @return <tt>true</tt> if this method has the specified signature
     */
    public static boolean hasSignature(Method this_, Class<?> returnType,
            String name, Class<?>... parameterTypes) {
        return Objects.equals(returnType, this_.getReturnType())
                && Objects.equals(name, this_.getName())
                && Arrays.equals(parameterTypes, this_.getParameterTypes());
    }

    /**
     * Invokes this method on the specified target with the specified actual
     * parameters.
     * 
     * @param this_
     *            this method.
     * @param target
     *            the target to invoke this method on
     * @param parameters
     *            the actual parameters of this method
     * @return the return value of this method or <tt>null</tt> if this method
     *         is void
     * @throws RuntimeException
     *             wrapping {@link IllegalAccessException} if this Method is not
     *             accessible on the given target object
     * @throws RuntimeException
     *             if invoking this method on the given target object throws an
     *             exception; {@link RuntimeException#getCause()} will return
     *             the exception thrown
     */
    public static Object invokeOn(Method this_, Object target,
            Object... parameters) {
        try {
            return this_.invoke(target, parameters);
        } catch (IllegalAccessException e) {
            throw Throwables.asRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw Throwables.asRuntimeException(e.getCause());
        }
    }

}
