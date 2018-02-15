package org.graffiti.util.ext;

/**
 * Extension methods for {@link Object} arrays representing the state of an
 * object.
 * 
 * @author Harald Frankenberger
 */
public class State {

    private State() {
    }

    /**
     * Throws {@link IllegalStateException} if this state has <code>null</code>
     * elements.
     * 
     * @param this_
     *            this state.
     * @throws IllegalStateException
     *             if this state has <code>null</code> elements
     */
    public static void checkNotNull(Object... this_) {
        if (Arrays.someNull(this_))
            throw new IllegalStateException();
    }

}
