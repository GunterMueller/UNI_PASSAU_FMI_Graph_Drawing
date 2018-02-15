// =============================================================================
//
//   AbortAddEdgeAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbortAddEdgeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.create;

import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;

/**
 * To abort add edge.
 * 
 * @author MH
 * @deprecated
 */
@Deprecated
public class AbortAddEdgeAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = -7276164808550003236L;
    /** Reference to the CreateTool */
    private CreateTool createTool;

    /**
     * Creates a new AbortAddEdgeAction object.
     * 
     * @param createTool
     *            The given CreateTool
     */
    public AbortAddEdgeAction(CreateTool createTool) {
        this.createTool = createTool;
    }

    /**
     * The action of this class
     * 
     * @param e
     *            The given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        // Switching to default-mode: afterEvent checks if addEdgeSourceNode
        // is null and, if yes, switches to default-mode
        if (createTool.isInMultiCommandMode()
                && (createTool.getAddEdgeSourceNode() != null)
                && !createTool.hasAddEdgeChangedMode()) {
            createTool.setAddEdgeSourceNode(null);
            createTool.removeDummyObjects();

            createTool.addEdgeChangedMode();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
