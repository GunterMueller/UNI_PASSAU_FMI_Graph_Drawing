// =============================================================================
//
//   UpdateAddEdgeAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: UpdateAddEdgeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.create;

import java.awt.Point;
import java.util.logging.Logger;

import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5766 $ $Date: 2008-12-31 05:02:05 +0100 (Mi, 31 Dez 2008)
 *          $
 * @deprecated
 */
@Deprecated
public class UpdateAddEdgeAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1760925011744307670L;

    /** DOCUMENT ME! */
    private CreateTool createTool;

    private static final Logger logger = Logger
            .getLogger(UpdateAddEdgeAction.class.getName());

    /**
     * Creates a new UpdateAddEdgeAction object.
     * 
     * @param createTool
     *            DOCUMENT ME!
     */
    public UpdateAddEdgeAction(CreateTool createTool) {
        this.createTool = createTool;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        if (createTool.isInMultiCommandMode()
                && (createTool.getAddEdgeSourceNode() != null)) {
            Point position = e.getPosition();

            if (position == null) {
                logger.finer("Can't operate " + "without position!");
            } else {
                // System.out.println("... executing");
                createTool.setDummyNodePosition(position);
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
