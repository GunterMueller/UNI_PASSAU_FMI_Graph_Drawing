// =============================================================================
//
//   PrePostAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PrePostAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;

/**
 */
public class PrePostAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = -5390730593676937479L;

    /** DOCUMENT ME! */
    private FunctionComponent functionComponent;

    /** DOCUMENT ME! */
    private Set<FunctionAction> functionActions = new HashSet<FunctionAction>();

    /**
     * Creates a new PrePostAction object.
     * 
     * @param functionComponent
     *            DOCUMENT ME!
     */
    public PrePostAction(FunctionComponent functionComponent) {
        this.functionComponent = functionComponent;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
        PositionInfo positionInfo = functionComponent.getPositionInfo();
        Point position = positionInfo.getMousePosition();

        functionComponent.beforeEvent(position);

        for (FunctionAction currAction : functionActions) {
            FunctionActionEvent event = new FunctionActionEvent(e.getSource(),
                    ActionEvent.ACTION_PERFORMED, null, position);
            currAction.actionPerformed(event);
        }

        functionComponent.afterEvent(position);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param action
     *            DOCUMENT ME!
     */
    public void addFunctionAction(FunctionAction action) {
        functionActions.add(action);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param action
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean contains(FunctionAction action) {
        return functionActions.contains(action);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
