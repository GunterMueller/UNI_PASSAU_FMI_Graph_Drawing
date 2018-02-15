// =============================================================================
//
//   MarkUnderlyingNodeAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MarkUnderlyingNodeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.selection;

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.graffiti.graph.Node;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.undo.ChangeAttributesEdit;

/**
 * To turn to the next underlying node.
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5766 $ $Date: 2008-12-31 05:02:05 +0100 (Mi, 31 Dez 2008)
 *          $
 * @deprecated
 */
@Deprecated
public class MarkUnderlyingNodeAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 4100325072623637347L;

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(ChangeAttributesEdit.class.getName());

    /** Reference to the SelectionTool */
    private SelectionTool selectionTool;

    /**
     * Creates a new MarkUnderlyingNodeAction object.
     * 
     * @param selectionTool
     *            The given SelectionTool
     */
    public MarkUnderlyingNodeAction(SelectionTool selectionTool) {
        this.selectionTool = selectionTool;
    }

    /**
     * Returns the parameters which are valid.
     * 
     * @return Map with the valid parameters
     */
    @Override
    public Map<String, Set<Object>> getValidParameters() {
        return construct1To2ParamMap("remove-old-selection", "yes", "no");
    }

    /**
     * The action of this class.
     * 
     * @param e
     *            The given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        Point pos = e.getPosition();

        // checks if mouse has a position
        if (pos == null) {
            logger.finer("Can't operate without position!");

            return;
        }

        // the nodes under the mouse-position
        List<Node> nodes = selectionTool.getNodesUnderMouse(pos);

        // no node is under the mouse-position
        if (nodes.isEmpty()) {
            logger.finer("Can't operate without nodes under mouse position!");

            return;
        }

        // the list within the marked nodes and under the mouse-position
        List<Node> markedNodesUnder = new LinkedList<Node>();

        for (Node node : nodes) {
            // adds the node if it is in the selection (to know which
            // nodes under mouse position is/are marked
            if (selectionTool.getSelection().getNodes().contains(node)) {
                markedNodesUnder.add(node);
            }
        }

        // if old selection has to be removed
        if (removeOldSelection()) {
            selectionTool.unmarkAll();
        }

        // if the nodes under mouse are not marked OR more than one marked
        if (markedNodesUnder.isEmpty() || (markedNodesUnder.size() > 1)) {
            for (Node node : markedNodesUnder) {
                selectionTool.unmark(node);
            }

            selectionTool.mark(nodes.get(0));
        }

        // there is exact one node under mouse-position marked
        else {
            Iterator<Node> nodesIt = nodes.iterator();

            boolean markedFound = false;

            // all nodes of the graph
            while (nodesIt.hasNext() && !markedFound) {
                Node node = nodesIt.next();

                // check if the node is under mouse AND marked
                if (markedNodesUnder.contains(node)) {
                    // the marked node is found, unmark it and mark the next
                    // if end of the list then mark the first of list
                    markedFound = true;
                    selectionTool.unmark(node);

                    Node nextNode;

                    if (nodesIt.hasNext()) {
                        nextNode = nodesIt.next();
                        selectionTool.mark(nextNode);
                    } else {
                        // end of list then the first one becomes marked
                        nextNode = nodes.get(0);
                        selectionTool.mark(nextNode);
                    }
                }
            }
        }
    }

    /**
     * Returns true if the old selection should be removed, else false.
     * 
     * @return True if the old selection should be removed, else false.
     */
    private boolean removeOldSelection() {
        Object value = this.getValue("remove-old-selection");

        return (value.toString() == "yes");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
