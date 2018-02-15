// =============================================================================
//
//   FinishAddEdgeAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FinishAddEdgeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.create;

import java.awt.Point;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.undo.ChangeAttributesEdit;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5766 $ $Date: 2008-12-31 05:02:05 +0100 (Mi, 31 Dez 2008)
 *          $
 * @deprecated
 */
@Deprecated
public class FinishAddEdgeAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = -5454671447456276282L;

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(ChangeAttributesEdit.class.getName());

    /** DOCUMENT ME! */
    private CreateTool createTool;

    /**
     * Creates a new FinishAddEdgeAction object.
     * 
     * @param createTool
     *            DOCUMENT ME!
     */
    public FinishAddEdgeAction(CreateTool createTool) {
        this.createTool = createTool;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public Map<String, Set<Object>> getValidParameters() {
        return construct3To233ParamMap("add-node-if-necessary", "yes", "no",
                "mark-added-node", "only", "additionally", "no",
                "mark-added-edge", "only", "additionally", "no");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        if (!createTool.isInMultiCommandMode()
                || (createTool.getAddEdgeSourceNode() == null)
                || createTool.hasAddEdgeChangedMode())
            return;

        Point position = e.getPosition();

        if (position == null) {
            logger.finer("FinishAddEdgeAction says: Can't operate "
                    + "without position!!!");
        } else {
            // System.out.println("... executing");
            Object addNodeOption = getValue("add-node-if-necessary");
            Object markNodeOption = getValue("mark-added-node");
            Object markEdgeOption = getValue("mark-added-edge");

            // set default-values, if necessary
            if (addNodeOption == null) {
                addNodeOption = "no";
            }

            if (markNodeOption == null) {
                markNodeOption = "no";
            }

            if (markEdgeOption == null) {
                markEdgeOption = "only";
            }
            boolean nodeAdded = false;
            Node destNode = createTool.getTopNode(position);

            if ((destNode == null) && addNodeOption.equals("yes")) {
                destNode = AddNodeAction.addNode(createTool, position);
                nodeAdded = true;
            }

            if (destNode != null) {
                Edge addedEdge = createTool.addEdge(destNode);

                if ((nodeAdded && markNodeOption.equals("only"))
                        || markEdgeOption.equals("only")) {
                    createTool.unmarkAll();
                }

                if (nodeAdded) {
                    // createTool.unmarkAll();

                    if (markNodeOption.equals("only")) {
                        createTool.unmarkAll();
                        createTool.mark(destNode);
                    } else if (markNodeOption.equals("additionally")) {
                        createTool.mark(destNode);
                    } else if (markNodeOption.equals("no")) {
                        createTool.unmarkAll();
                    } else {
                        createTool.unmarkAll();
                    }
                }

                if (markEdgeOption.equals("only")) {
                    createTool.unmarkAll();
                    createTool.mark(addedEdge);
                } else if (markEdgeOption.equals("additionally")) {
                    createTool.mark(addedEdge);
                } else if (markEdgeOption.equals("no")) {
                    createTool.unmarkAll();
                } else {
                    createTool.unmarkAll();
                }

                createTool.setAddEdgeSourceNode(null);

                createTool.switchToDefaultMode();

                createTool.addEdgeChangedMode();
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
