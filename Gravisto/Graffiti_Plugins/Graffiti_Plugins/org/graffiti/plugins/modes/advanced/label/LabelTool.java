// =============================================================================
//
//   LabelTool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LabelTool.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.label;

import java.util.logging.Logger;

import javax.swing.JComponent;

import org.graffiti.plugins.modes.advanced.AbstractEditingTool;
import org.graffiti.plugins.modes.advanced.FunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionComponent;
import org.graffiti.plugins.modes.advanced.InvalidInputEventException;
import org.graffiti.plugins.modes.advanced.InvalidParameterException;
import org.graffiti.plugins.modes.advanced.NoSuchFunctionActionException;
import org.graffiti.plugins.modes.advanced.PositionInfo;
import org.graffiti.plugins.modes.advanced.ShortCutAction;
import org.graffiti.plugins.modes.advanced.ShowPopupMenuAction;
import org.graffiti.plugins.modes.advanced.ToolPlugin;

/**
 * Tool to create and change the labels on nodes and edges.
 * 
 * @deprecated
 */
@Deprecated
public class LabelTool extends AbstractEditingTool {

    /** The logger of this class */
    private static final Logger logger = Logger.getLogger(LabelTool.class
            .getName());

    /**
     * Creates a new LabelTool object.
     * 
     * @param toolPlugin
     *            The given ToolPlugin
     * @param positionInfo
     *            The given PositionInfo
     */
    public LabelTool(ToolPlugin toolPlugin, PositionInfo positionInfo) {
        super(toolPlugin, positionInfo);

        try {
            functionManager.addFunction("popupTrigger", "show-popup-menu");

            functionManager.addFunction(
                    "mouse 1x not_shift not_ctrl clicked button1",
                    "start-edit-label");

            // function for popupMenu
            functionManager.addFunction("popupTrigger", "show-popup-menu");
            functionManager.addFunction("typed m", "show-popup-menu");
            functionManager.addFunction("shift DELETE", "linux-shortcuts",
                    "action", "cut");
            functionManager.addFunction("ctrl DELETE", "linux-shortcuts",
                    "action", "copy");
            functionManager.addFunction("shift INSERT", "linux-shortcuts",
                    "action", "paste");
        } catch (InvalidInputEventException e) {
            logger.finer("Input-event " + e.getEvent()
                    + ", assigned to function " + e.getFunction()
                    + " has invalid syntax!");
            System.exit(-1);
        } catch (NoSuchFunctionActionException e) {
            logger.finer("Can't find " + " Action assigned to function "
                    + e.getFunction());
            System.exit(-1);
        } catch (InvalidParameterException e) {
            logger.finer(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param functionName
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public FunctionAction getFunctionAction(String functionName) {
        FunctionAction superAction = super.getFunctionAction(functionName);

        if (superAction != null)
            return superAction;
        else if (functionName.equals("show-popup-menu"))
            return new ShowPopupMenuAction(this);
        else if (functionName.equals("start-edit-label"))
            return new StartEditLabelAction(this);
        else if (functionName.equals("linux-shortcuts"))
            return new ShortCutAction(this);
        else
            return null;
    }

    /**
     * Implementation of FunctionComponent
     * 
     * @param name
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public FunctionComponent getSubComponent(String name) {
        return null;
    }

    /**
     * Activates the key bindings
     * 
     * @param viewComponent
     *            The given view component
     */
    @Override
    protected void activateKeyBindings(JComponent viewComponent) {
        functionManager.activateAllKeyBindings(viewComponent);
    }

    /**
     * Activates the key bindings
     * 
     * @param viewComponent
     *            The given view component
     */
    @Override
    protected void deactivateKeyBindings(JComponent viewComponent) {
        functionManager.deactivateAllKeyBindings(viewComponent);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
