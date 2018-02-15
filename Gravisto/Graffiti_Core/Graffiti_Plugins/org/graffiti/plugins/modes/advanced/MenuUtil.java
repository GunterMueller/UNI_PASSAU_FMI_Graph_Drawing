// =============================================================================
//
//   MenuUtil.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MenuUtil.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/**
 * Helper-class containing methods for dealing with menus, especially in
 * conjunction with the function-concept.
 */
public class MenuUtil {

    /**
     * Adds a new menu-item which calls the given function with no parameters to
     * the given popup-menu.
     * 
     * @param menu
     *            any popup-menu
     * @param text
     *            label for the menu-item
     * @param functionName
     *            name of the function
     * @param functionComponent
     *            FunctionComponent to be used for accessing the function
     * 
     * @throws NoSuchFunctionActionException
     *             if no function with the given name can be found
     * @throws InvalidParameterException
     *             should never be thrown by this class, as we pass no
     *             parameters
     */
    public static void addItem(JPopupMenu menu, String text,
            String functionName, FunctionComponent functionComponent)
            throws NoSuchFunctionActionException, InvalidParameterException {
        JMenuFunctionItem item = new JMenuFunctionItem(text, functionName,
                functionComponent, new HashMap<String, Object>());

        menu.add(item);
    }

    /**
     * Adds a new menu-item which calls the given function with one parameter to
     * the given popup-menu.
     * 
     * @param menu
     *            any popup-menu
     * @param text
     *            label for the menu-item
     * @param functionName
     *            name of the function
     * @param functionComponent
     *            FunctionComponent to be used for accessing the function
     * @param paramOneKey
     *            name of the parameter
     * @param paramOneValue
     *            value assigned to the parameter
     * 
     * @throws NoSuchFunctionActionException
     *             if no function with the given name can be found
     * @throws InvalidParameterException
     *             if either a given parameter doesn�t exist, or you try to
     *             assign an invalid value to it
     */
    public static void addItem(JPopupMenu menu, String text,
            String functionName, FunctionComponent functionComponent,
            String paramOneKey, Object paramOneValue)
            throws NoSuchFunctionActionException, InvalidParameterException {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(paramOneKey, paramOneValue);

        JMenuFunctionItem item = new JMenuFunctionItem(text, functionName,
                functionComponent, paramMap);

        menu.add(item);
    }

    /**
     * Adds a new menu-item which calls the given function with one parameter to
     * the given popup-menu.
     * 
     * @param menu
     *            any popup-menu
     * @param text
     *            label for the menu-item
     * @param functionName
     *            name of the function
     * @param functionComponent
     *            FunctionComponent to be used for accessing the function
     * @param paramOneKey
     *            name of the parameter
     * @param paramOneValue
     *            value assigned to the parameter
     * @param toolTip
     *            DOCUMENT ME!
     * 
     * @throws NoSuchFunctionActionException
     *             if no function with the given name can be found
     * @throws InvalidParameterException
     *             if either a given parameter doesn�t exist, or you try to
     *             assign an invalid value to it
     */
    public static void addItem(JPopupMenu menu, String text,
            String functionName, FunctionComponent functionComponent,
            String paramOneKey, Object paramOneValue, String toolTip)
            throws NoSuchFunctionActionException, InvalidParameterException {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(paramOneKey, paramOneValue);

        JMenuFunctionItem item = new JMenuFunctionItem(text, functionName,
                functionComponent, paramMap);
        item.setToolTipText("toolTip");
        menu.add(item);
    }

    // ------------------------------------[MH]----------------------------------

    /**
     * The method adds the given subMenu the text, tooltip and the function
     * which is called from this menu. [MH]
     * 
     * @param subMenu
     *            The given menu
     * @param text
     *            The text
     * @param functionName
     *            The name of the function, which has to be called
     * @param functionComponent
     *            The function component
     * @param paramOneKey
     *            The key of the first parameter
     * @param paramOneValue
     *            The value of the first parameter
     * @param toolTipText
     *            The tooltip Text
     * 
     * @throws NoSuchFunctionActionException
     *             If for the called function the action doesn't exist
     * @throws InvalidParameterException
     *             If the parameters of the function are not valid
     */
    public static void addSubItemWith1Param(JMenu subMenu, String text,
            String functionName, FunctionComponent functionComponent,
            String paramOneKey, Object paramOneValue, String toolTipText)
            throws NoSuchFunctionActionException, InvalidParameterException {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(paramOneKey, paramOneValue);

        JMenuFunctionItem item = new JMenuFunctionItem(text, functionName,
                functionComponent, paramMap);

        item.setToolTipText(toolTipText);
        subMenu.add(item);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param subMenu
     *            the given Menu
     * @param text
     *            The text
     * @param functionName
     *            The name of the function, which has to be called
     * @param functionComponent
     *            The function component
     * @param paramOneKey1
     *            The key of the first parameter
     * @param paramOneValue1
     *            The value of the first parameter
     * @param paramOneKey2
     *            The key of the second parameter
     * @param paramOneValue2
     *            The value of the second parameter
     * @param toolTipText
     *            The toolTipText
     * 
     * @throws NoSuchFunctionActionException
     *             If for the called function the action doesn't exist
     * @throws InvalidParameterException
     *             If the parameters of the function are not valid
     */
    public static void addSubItemWith2Params(JMenu subMenu, String text,
            String functionName, FunctionComponent functionComponent,
            String paramOneKey1, Object paramOneValue1, String paramOneKey2,
            Object paramOneValue2, String toolTipText)
            throws NoSuchFunctionActionException, InvalidParameterException {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put(paramOneKey1, paramOneValue1);
        paramMap.put(paramOneKey2, paramOneValue2);

        JMenuFunctionItem item = new JMenuFunctionItem(text, functionName,
                functionComponent, paramMap);

        item.setToolTipText(toolTipText);
        subMenu.add(item);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
