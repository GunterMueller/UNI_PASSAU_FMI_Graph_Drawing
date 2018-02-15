// =============================================================================
//
//   AbstractFunctionAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractFunctionAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 * Abstract implementation of the FunctionAction-interface.
 */
public abstract class AbstractFunctionAction extends AbstractAction implements
        FunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = -5991357355742410066L;

    /**
     * Constructs an AbstractFunctionAction, using the default-constructor of
     * class javax.swing.AbstractAction.
     */
    public AbstractFunctionAction() {
        super();
    }

    /**
     * Constructs an AbstractFunctionAction with the given name.
     * 
     * @param name
     *            name of the Action
     */
    public AbstractFunctionAction(String name) {
        super(name);
    }

    /**
     * Constructs an AbstractFunctionAction with the given name and icon.
     * 
     * @param name
     *            name of the Action
     * @param icon
     *            icon to be passed to the Action
     */
    public AbstractFunctionAction(String name, Icon icon) {
        super(name, icon);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Map<String, Set<Object>> getValidParameters() {
        // default: we have no parameters...
        return new HashMap<String, Set<Object>>();
    }

    /**
     * Implementation of the usual actionPerformed-method. Using dynamic binding
     * and overloading, it is never called if the passed parameter is a
     * FunctionActionEvent.
     * 
     * @param e
     *            any ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
        System.out.println("AbstractFunctionAction."
                + " actionPerformed(ActionEvent e) called");
    }

    /**
     * Implementation of the actionPerformed-method used within the
     * function-concept. Should always be overridden by subclasses.
     * 
     * @param e
     *            any FunctionActionEvent
     */
    public void actionPerformed(FunctionActionEvent e) {
        System.out.println("AbstractFunctionAction."
                + "actionPerformed(FunctionActionEvent e) called");
    }

    /**
     * Helper-method for subclasses for constructing a parameter-map as returned
     * by getValidParameters. This version is for one parameter having two
     * possible values.
     * 
     * @param name1
     *            name of parameter 1
     * @param value1a
     *            first possible value of parameter 1
     * @param value1b
     *            second possible value of parameter 1
     * 
     * @return the constructed parameter-map
     */
    protected Map<String, Set<Object>> construct1To2ParamMap(String name1,
            Object value1a, Object value1b) {
        Set<Object> values1 = new HashSet<Object>();
        values1.add(value1a);
        values1.add(value1b);

        Map<String, Set<Object>> retMap = new HashMap<String, Set<Object>>();
        retMap.put(name1, values1);

        return retMap;
    }

    /**
     * Helper-method for subclasses for constructing a parameter-map as returned
     * by getValidParameters. This version is for one parameter having five
     * possible values.
     * 
     * @param name1
     *            name of parameter 1
     * @param value1a
     *            first possible value of parameter 1
     * @param value1b
     *            second possible value of parameter 1
     * @param value1c
     *            third possible value of parameter 1
     * 
     * @return the constructed parameter-map
     */
    protected Map<String, Set<Object>> construct1To3ParamMap(String name1,
            Object value1a, Object value1b, Object value1c) {
        Set<Object> values1 = new HashSet<Object>();
        values1.add(value1a);
        values1.add(value1b);
        values1.add(value1c);

        Map<String, Set<Object>> retMap = new HashMap<String, Set<Object>>();
        retMap.put(name1, values1);

        return retMap;
    }

    // ---------------------------------[MH]-----------------------------------

    /**
     * Method is analog to construct1To2ParamMap's documentation
     * 
     * @param name1
     *            DOCUMENT ME!
     * @param value1a
     *            DOCUMENT ME!
     * @param value1b
     *            DOCUMENT ME!
     * @param value1c
     *            DOCUMENT ME!
     * @param value1d
     *            DOCUMENT ME!
     * @param value1e
     *            DOCUMENT ME!
     * @param value1f
     *            DOCUMENT ME!
     * @param value1g
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected Map<String, Set<Object>> construct1To7ParamMap(String name1,
            Object value1a, Object value1b, Object value1c, Object value1d,
            Object value1e, Object value1f, Object value1g) {
        Set<Object> values1 = new HashSet<Object>();
        values1.add(value1a);
        values1.add(value1b);
        values1.add(value1c);
        values1.add(value1d);
        values1.add(value1e);
        values1.add(value1f);
        values1.add(value1g);

        Map<String, Set<Object>> retMap = new HashMap<String, Set<Object>>();
        retMap.put(name1, values1);

        return retMap;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param name1
     *            DOCUMENT ME!
     * @param value1a
     *            DOCUMENT ME!
     * @param value1b
     *            DOCUMENT ME!
     * @param name2
     *            DOCUMENT ME!
     * @param value2a
     *            DOCUMENT ME!
     * @param value2b
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected Map<String, Set<Object>> construct2To22ParamMap(String name1,
            Object value1a, Object value1b, String name2, Object value2a,
            Object value2b) {
        Set<Object> values1 = new HashSet<Object>();
        values1.add(value1a);
        values1.add(value1b);

        Set<Object> values2 = new HashSet<Object>();
        values2.add(value2a);
        values2.add(value2b);

        Map<String, Set<Object>> retMap = new HashMap<String, Set<Object>>();
        retMap.put(name1, values1);
        retMap.put(name2, values2);

        return retMap;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param name1
     *            DOCUMENT ME!
     * @param value1a
     *            DOCUMENT ME!
     * @param value1b
     *            DOCUMENT ME!
     * @param name2
     *            DOCUMENT ME!
     * @param value2a
     *            DOCUMENT ME!
     * @param value2b
     *            DOCUMENT ME!
     * @param value2c
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected Map<String, Set<Object>> construct2To23ParamMap(String name1,
            Object value1a, Object value1b, String name2, Object value2a,
            Object value2b, Object value2c) {
        Set<Object> values1 = new HashSet<Object>();
        values1.add(value1a);
        values1.add(value1b);

        Set<Object> values2 = new HashSet<Object>();
        values2.add(value2a);
        values2.add(value2b);
        values2.add(value2c);

        Map<String, Set<Object>> retMap = new HashMap<String, Set<Object>>();
        retMap.put(name1, values1);
        retMap.put(name2, values2);

        return retMap;
    }

    /**
     * Method is analog to construct1To2ParamMap's documentation
     * 
     * @param name1
     *            DOCUMENT ME!
     * @param value1a
     *            DOCUMENT ME!
     * @param value1b
     *            DOCUMENT ME!
     * @param value1c
     *            DOCUMENT ME!
     * @param value1d
     *            DOCUMENT ME!
     * @param name2
     *            DOCUMENT ME!
     * @param value2a
     *            DOCUMENT ME!
     * @param value2b
     *            DOCUMENT ME!
     * @param value2c
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected Map<String, Set<Object>> construct2To43ParamMap(String name1,
            Object value1a, Object value1b, Object value1c, Object value1d,
            String name2, Object value2a, Object value2b, Object value2c) {
        Set<Object> values1 = new HashSet<Object>();
        values1.add(value1a);
        values1.add(value1b);
        values1.add(value1c);
        values1.add(value1d);

        Set<Object> values2 = new HashSet<Object>();
        values2.add(value2a);
        values2.add(value2b);
        values2.add(value2c);

        Map<String, Set<Object>> retMap = new HashMap<String, Set<Object>>();
        retMap.put(name1, values1);
        retMap.put(name2, values2);

        return retMap;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param name1
     *            DOCUMENT ME!
     * @param value1a
     *            DOCUMENT ME!
     * @param value1b
     *            DOCUMENT ME!
     * @param name2
     *            DOCUMENT ME!
     * @param value2a
     *            DOCUMENT ME!
     * @param value2b
     *            DOCUMENT ME!
     * @param value2c
     *            DOCUMENT ME!
     * @param name3
     *            DOCUMENT ME!
     * @param value3a
     *            DOCUMENT ME!
     * @param value3b
     *            DOCUMENT ME!
     * @param value3c
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    protected Map<String, Set<Object>> construct3To233ParamMap(String name1,
            Object value1a, Object value1b, String name2, Object value2a,
            Object value2b, Object value2c, String name3, Object value3a,
            Object value3b, Object value3c) {
        Set<Object> values1 = new HashSet<Object>();
        values1.add(value1a);
        values1.add(value1b);

        Set<Object> values2 = new HashSet<Object>();
        values2.add(value2a);
        values2.add(value2b);
        values2.add(value2c);

        Set<Object> values3 = new HashSet<Object>();
        values3.add(value3a);
        values3.add(value3b);
        values3.add(value3c);

        Map<String, Set<Object>> retMap = new HashMap<String, Set<Object>>();
        retMap.put(name1, values1);
        retMap.put(name2, values2);
        retMap.put(name3, values3);

        return retMap;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
