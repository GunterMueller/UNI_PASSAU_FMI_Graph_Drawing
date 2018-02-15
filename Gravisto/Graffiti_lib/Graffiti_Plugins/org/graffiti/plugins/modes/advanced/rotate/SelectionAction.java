package org.graffiti.plugins.modes.advanced.rotate;

import java.awt.Point;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;

/**
 * To mark and unmark GraphElements via MouseClick and via KeyBoard (strg,
 * shift)
 * 
 * @author Marek Piorkowski
 * @deprecated
 */
@Deprecated
public class SelectionAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1098415387849014615L;

    /** The Logger for this class */
    private static final Logger logger = Logger.getLogger(SelectionAction.class
            .getName());

    /** Reference to the AbstractEditingTool */
    private RotationTool tool;

    /**
     * Creates a new SelectionAction
     * 
     * @param tool
     *            the given AbstractEditingTool
     */
    public SelectionAction(RotationTool tool) {
        this.tool = tool;
    }

    /**
     * Creates a Map with parameter names and their correct values
     * 
     * @return Map with parameter names and their correct values
     */
    @Override
    public Map<String, Set<Object>> getValidParameters() {
        return construct2To22ParamMap("remove-old-selection", "yes", "no",
                "connections", "yes", "no");
    }

    /**
     * Performes the action of this class
     * 
     * @param e
     *            the given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        Point position = e.getPosition();

        // Check if the mouse position is inside the editor
        if (position == null) {
            logger
                    .finer("Warning: Can't operate without position assigned to event!");

            return;
        }

        GraphElement gec = tool.getTopGraphElement(position);

        // Check if an additional element is marked or if the
        // selection has to be unmarked
        if (gec == null) {
            tool.unmarkAll();
        } else {
            if (tool.getTopNode(position) == tool.getReferenceNode()) {
                tool.unmarkAll();
                tool.mark(tool.getReferenceNode());
                return;
            }

            if (tool.getSelection().contains(tool.getReferenceNode()))
                return;

            // neither Shift nor Ctrl
            if (removeOldSelection()) {
                tool.unmarkAll();
                tool.mark(gec);
            }

            // not Shift but Ctrl
            else if (!withConnections()) {
                if (tool.getSelection().contains(gec)) {
                    tool.unmark(gec);
                } else {
                    tool.mark(gec);
                }
            }

            // shift, but not Ctrl
            else if (withConnections()) {
                if (gec instanceof Edge) {
                    Edge edge = (Edge) gec;
                    Node target = edge.getTarget();
                    Node source = edge.getSource();

                    if (tool.getSelection().contains(edge)) {
                        tool.unmark(edge);

                        if (tool.getSelection().contains(source)) {
                            tool.unmark(source);
                        }

                        if (tool.getSelection().contains(target)) {
                            tool.unmark(target);
                        }
                    } else {
                        tool.mark(edge);

                        if (!tool.getSelection().contains(source)) {
                            tool.mark(source);
                        }

                        if (!tool.getSelection().contains(target)) {
                            tool.mark(target);
                        }
                    }
                } else if (gec instanceof Node) {
                    Node node = (Node) gec;

                    if (tool.getSelection().contains(node)) {
                        tool.unmark(node);

                        Iterator<Edge> incidents = node.getEdgesIterator();

                        while (incidents.hasNext()) {
                            Edge edge = incidents.next();

                            if (tool.getSelection().contains(edge)) {
                                tool.unmark(edge);
                            }
                        }
                    } else {
                        tool.mark(node);

                        Iterator<Edge> incidents = node.getEdgesIterator();

                        while (incidents.hasNext()) {
                            Edge edge = incidents.next();

                            if (edge.getSource().equals(node)) {
                                if (tool.getSelection().contains(
                                        edge.getTarget())) {
                                    tool.mark(edge);
                                }
                            } else {
                                if (tool.getSelection().contains(
                                        edge.getSource())) {
                                    tool.mark(edge);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks the current value of the parameter "remove-old-selection"
     * 
     * @return true for "yes", else false
     */
    private boolean removeOldSelection() {
        Object removeOldSelection = this.getValue("remove-old-selection");

        if (removeOldSelection == null) {
            removeOldSelection = "yes";
        }

        return removeOldSelection.equals("yes");
    }

    /**
     * Checks the current value of the parameter "connections"
     * 
     * @return true if "yes", else false
     */
    private boolean withConnections() {
        Object connection = this.getValue("connections");

        if (connection == null) {
            connection = "yes";
        }

        return connection.equals("yes");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
