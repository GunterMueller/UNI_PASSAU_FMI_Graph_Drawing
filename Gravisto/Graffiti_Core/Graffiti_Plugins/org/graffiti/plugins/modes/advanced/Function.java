// =============================================================================
//
//   Function.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Function.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.Action;

/**
 * One function a FunctionComponent offers. Usually managed by the
 * FunctionManager of the FunctionComponent. This class stores the name of a
 * function, the associated FunctionAction, parameters for the function and
 * (possibly existing) bindings for that function (for example "ctrl-alt-Q
 * causes CivQuest to quit").
 */
public class Function {

    /**
     * The String which separates components and function-names from each other
     * in a String referencing a function (like
     * "civquest.quadmap.whateverFunction").
     */
    public static final String SEPARATOR = ".";

    /** FunctionAction associated to this function */
    private FunctionAction action;

    /**
     * Map "parameter-name (String) ---> parameter-value (Object)" defining
     * parameters for this function. For example, a function "print-some-text"
     * might have a mapping color ---> red. Which (and if any) parameters exist
     * is function-dependent.
     */
    private Map<String, Object> parameters = new HashMap<String, Object>();

    /**
     * Set of Strings. Each String defines one binding for this function. See
     * the texinfo-docs for a definition of the syntax of these Strings
     */
    private Set<String> bindings = new HashSet<String>();

    /** Name of the function, may not contain SEPARATOR */
    private String name;

    /**
     * Constructs a new Function-object for the function with the given name.
     * 
     * @param name
     *            name of the function
     */
    public Function(String name) {
        this.name = name;
    }

    /**
     * Sets the FunctionAction associated to this function.
     * 
     * @param action
     *            the FunctionAction to be associated to this function
     */
    public void setAction(FunctionAction action) {
        this.action = action;
    }

    /**
     * Returns the FunctionAction associated to this function.
     * 
     * @return the FunctionAction associated to this function.
     */
    public FunctionAction getAction() {
        return action;
    }

    /**
     * Returns an Iterator over all bindings of this function.
     * 
     * @return an Iterator over all bindings of this function.
     */
    public Iterator<String> getBindingIterator() {
        return bindings.iterator();
    }

    /**
     * Returns the name of this function.
     * 
     * @return the name of this function
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the parameter with the given name to the given value.
     * 
     * @param name
     *            name of a parameter
     * @param value
     *            new value of the parameter
     */
    public void setParameter(String name, Object value) {
        parameters.put(name, value);
    }

    /**
     * Returns an iterator over all names (Strings) of parameters currently set
     * in this Function.
     * 
     * @return an iterator over all parameter-names
     */
    public Iterator<String> getParameterIterator() {
        return parameters.keySet().iterator();
    }

    /**
     * Returns the value of the given parameter, if it exists, null otherwise.
     * 
     * @param name
     *            name of a parameter
     * 
     * @return value of the parameter, if it exists, null otherwise
     */
    public Object getParameterValue(String name) {
        return parameters.get(name);
    }

    /**
     * Sets all (parameter -> value) - mappings the given Map contains.
     * 
     * @param newParameters
     *            a Map containing (parameter (String) -> value (Object)) -
     *            mappings
     */
    public void setParameters(Map<String, Object> newParameters) {
        for (String s : newParameters.keySet()) {
            parameters.put(s, newParameters.get(s));
        }
    }

    /**
     * Adds the given event to the set of bindings.
     * 
     * @param event
     *            String defining an event - see the texinfo-docs for the syntax
     */
    public void addBinding(String event) {
        bindings.add(event);
    }

    /**
     * Adds all given events to the set of bindings
     * 
     * @param events
     *            a Set of event-Strings (see the texinfo-docs for the syntax)
     */
    public void addBindings(Set<String> events) {
        bindings.addAll(events);
    }

    /**
     * Adds the given String as a super-component-prefix to the name of this
     * function. In other words, transforms foo to prefix.foo
     * 
     * @param superComponent
     *            name of the super-component
     */
    public void addSuperComponentPrefix(String superComponent) {
        name = superComponent + Function.SEPARATOR + name;
    }

    /**
     * Adds all parameter-mappings to the FunctionAction of this function. So
     * the action-reference must already be set!!! Returns a Set with all keys
     * which interfere with the predefined keys in javax.swing.Action. (this is
     * done this way because setting these predefined keys/values this way may
     * be wanted, but may also be done accidentally with strange results).
     * 
     * @return Set with all keys interfering with the predefined keys in
     *         javax.swing.Action
     */
    public Set<String> putParametersIntoAction() {
        Set<String> predefinedKeys = new HashSet<String>();

        for (String key : parameters.keySet()) {
            if (key.equals(Action.ACCELERATOR_KEY)
                    || key.equals(Action.ACTION_COMMAND_KEY)
                    || key.equals(Action.LONG_DESCRIPTION)
                    || key.equals(Action.MNEMONIC_KEY)
                    || key.equals(Action.NAME)
                    || key.equals(Action.SHORT_DESCRIPTION)
                    || key.equals(Action.SMALL_ICON)) {
                predefinedKeys.add(key);
            }

            action.putValue(key, parameters.get(key));
        }

        return predefinedKeys;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
