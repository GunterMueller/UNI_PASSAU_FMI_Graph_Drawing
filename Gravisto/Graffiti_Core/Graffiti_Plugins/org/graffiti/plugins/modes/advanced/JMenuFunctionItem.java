// =============================================================================
//
//   JMenuFunctionItem.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: JMenuFunctionItem.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;

/**
 * A MenuItem with support for the function-concept. You just need to pass the
 * name of the function you want to execute to its constructor.
 */
public class JMenuFunctionItem extends JMenuItem {

    /**
     * 
     */
    private static final long serialVersionUID = -1547774532578522051L;
    /** Name of the function the menu-item executes */
    private String functionName;

    /**
     * Constructs a new menu-item with support for the function-concept.
     * 
     * @param name
     *            label of the menu-item
     * @param functionName
     *            name of the function to execute
     * @param functionComponent
     *            FunctionComponent to be used for accessing the given function
     * @param paramMap
     *            Map with (parameter (Strings) --> value (Object)-mappings
     * 
     * @throws NoSuchFunctionActionException
     *             if no function with the given name can be found using the
     *             given FunctionComponent
     * @throws InvalidParameterException
     *             if either one of the parameter-names does not exist, or one
     *             of the parameter-values is invalid
     */
    public JMenuFunctionItem(String name, String functionName,
            FunctionComponent functionComponent, Map<String, Object> paramMap)
            throws NoSuchFunctionActionException, InvalidParameterException {
        // we use a special wrapper-action (see below)
        super(getWrapperFunctionAction(name, functionName, functionComponent,
                paramMap));

        this.functionName = functionName;
    }

    /**
     * Returns the name of the function this JMenuFunctionItem executes.
     * 
     * @return name of the function this JMenuFunctionItem executes
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * Returns a wrapper-action which calls the given function.
     * 
     * @param displayName
     *            name of the Action
     * @param functionName
     *            name of the function
     * @param functionComponent
     *            FunctionComponent to use for accessing the function
     * @param paramMap
     *            Map with (parameter (Strings) --> value (Object)-mappings
     * 
     * @return wrapper-Action which calls the given function
     * 
     * @throws NoSuchFunctionActionException
     *             if no function with the given name can be found using the
     *             given FunctionComponent
     * @throws InvalidParameterException
     *             if either one of the parameter-names does not exist, or one
     *             of the parameter-values is invalid
     */
    private static Action getWrapperFunctionAction(String displayName,
            String functionName, FunctionComponent functionComponent,
            Map<String, Object> paramMap) throws NoSuchFunctionActionException,
            InvalidParameterException {
        // get Action for functionName
        FunctionAction functionAction = FunctionManager.getFunctionAction(
                functionName, functionComponent);

        if (functionAction == null)
            throw new NoSuchFunctionActionException(functionName);

        Function function = new Function(functionName);
        function.setAction(functionAction);
        function.setParameters(paramMap);

        // check params, here the InvalidParameterException comes from
        FunctionManager.checkParameters(function);

        // Parameters where loaded before the FunctionAction and saved only
        // inside the Function-object up to now. However, we need
        // them inside the FunctionAction-object.
        Set<String> predefinedParamKeys = function.putParametersIntoAction();

        if (!predefinedParamKeys.isEmpty()) {
            System.err.println("Warning: Parameters " + predefinedParamKeys
                    + " to function " + function.getName() + " may interfere "
                    + "with built-in swing-keys!!!");
        }

        // Get the FunctionComponent which really provides the given function.
        // (if functionName is a.b.c.some-function, c is the providingComponent)
        FunctionComponent providingComponent = FunctionManager
                .getProvidingComponent(functionComponent, functionName);

        return new WrapperFunctionAction(displayName, function,
                providingComponent);
    }

    /**
     * A wrapper-Action for calling a function you pass to it.
     */
    private static class WrapperFunctionAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = -3782567487147574731L;

        /** The function to call */
        private Function function;

        /** The FunctionComponent providing the function */
        private FunctionComponent functionComponent;

        /**
         * Constructs a new WrapperFunctionAction.n
         * 
         * @param displayName
         *            name of the Action
         * @param function
         *            reference to the Function
         * @param functionComponent
         *            FunctionComponent providing the function
         */
        private WrapperFunctionAction(String displayName, Function function,
                FunctionComponent functionComponent) {
            this.function = function;
            this.functionComponent = functionComponent;
            this.putValue(Action.NAME, displayName);
        }

        /**
         * DOCUMENT ME!
         * 
         * @param e
         *            DOCUMENT ME!
         */
        public void actionPerformed(ActionEvent e) {
            Point lastPopupPosition = functionComponent.getPositionInfo()
                    .getLastPopupPosition();

            // System.out.println("WrapperFctAction: " + lastPopupPosition);
            FunctionAction functionAction = function.getAction();
            FunctionActionEvent newEvent = new FunctionActionEvent(e
                    .getSource(), e.getID(), e.getActionCommand(), e.getWhen(),
                    e.getModifiers(), lastPopupPosition);

            functionComponent.beforeEvent(lastPopupPosition);
            functionAction.actionPerformed(newEvent);
            functionComponent.afterEvent(lastPopupPosition);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
