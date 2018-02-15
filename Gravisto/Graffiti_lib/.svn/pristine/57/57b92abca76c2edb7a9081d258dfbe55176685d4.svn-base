package org.graffiti.util.xml;

import java.lang.reflect.Method;

import org.graffiti.util.ext.Classes;
import org.graffiti.util.ext.Methods;

/**
 * Handles callbacks fired by an {@link XmlHandler} by reflectively invoking
 * appropriate callbacks in subclasses.
 * 
 * @author Harald Frankenberger
 */
public class CallbackHandler {
    /**
     * Starts the given child-element with this callback-handler. This
     * implementation is empty.
     * 
     * @param child
     *            the child to start with this callback-handler.
     */
    public void start(XmlElement child) {
    }

    /**
     * Reflectively invokes the start-callback for the given child-element on
     * subclasses. This implementation tries to invoke
     * 
     * <pre>
     * start(T)
     * </pre>
     * 
     * where <code>T</code> is the runtime-class of the given <code>child</code>
     * element. If no such method exists the fallback strategy is to invoke
     * {@link #start(XmlElement)}.
     * 
     * @param child
     *            the child whose appropriate callback-handler is to be called.
     */
    public void invokeStart(XmlElement child) {
        Method method_ = Classes.findMethod(this.getClass(), "start", child
                .getClass());
        if (method_ == null) {
            start(child);
        } else {
            Methods.invokeOn(method_, this, child);
        }
    }

    /**
     * Reflectively invokes the end-callback for the given child-element on
     * subclasses. This implementation tries to invoke
     * 
     * <pre>
     * end(T)
     * </pre>
     * 
     * where <code>T</code> is the runtime-class of the given <code>child</code>
     * element. If no such method exists the fallback strategy is to invoke
     * {@link #end(XmlElement)}.
     * 
     * @param child
     *            the child whose appropriate callback-handler is to be called.
     */
    public void invokeEnd(XmlElement child) {
        Method method_ = Classes.findMethod(this.getClass(), "end", child
                .getClass());
        if (method_ == null) {
            method_ = Classes.findMethod(this.getClass(), "end",
                    XmlElement.class);
        }
        Methods.invokeOn(method_, this, child);
    }

    /**
     * Ends the given child-element with this callback-handler. This
     * implementation is empty.
     * 
     * @param child
     *            the child to end with this callback-handler.
     */
    public void end(XmlElement child) {

    }

}
