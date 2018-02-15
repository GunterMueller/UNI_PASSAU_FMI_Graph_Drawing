package org.graffiti.plugins.modes.advanced.rotate;

import java.awt.Point;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.plugins.modes.advanced.AbstractEditingTool;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;

/**
 * Implementation of an Action which starts a rectangle selection
 * 
 * @author Marek Piorkowski
 * @deprecated
 */
@Deprecated
public class StartSelectRectAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = -8635072042150256593L;

    private static final Logger logger = Logger
            .getLogger(StartSelectRectAction.class.getName());

    /** Reference to the AbstractEditingTool */
    private RotationTool rotationTool;

    /**
     * Creates a new StartSelectRectAction
     * 
     * @param rotationTool
     *            the given AbstractEditingTool
     */
    public StartSelectRectAction(RotationTool rotationTool) {
        this.rotationTool = rotationTool;
    }

    /**
     * Creates a Map with parameter names and their correct values
     * 
     * @return Map with parameter names and their correct values
     */
    @Override
    public Map<String, Set<Object>> getValidParameters() {
        return construct1To2ParamMap("remove-old-selection", "yes", "no");
    }

    /**
     * Performs the action of this class
     * 
     * @param e
     *            the given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        Point position = e.getPosition();

        // Check if the mouse position is inside the editor
        if (position == null) {
            logger.finer("Warning: Can't " + "operate without position!");

            return;
        }

        Iterator<Edge> edgesIterator = rotationTool.getGraph()
                .getEdgesIterator();

        while (edgesIterator.hasNext()) {
            Edge edge = edgesIterator.next();
            Map<String, Attribute> bends = rotationTool.getBendsOfEdge(edge);

            for (String s : bends.keySet()) {
                CoordinateAttribute moveBend = (CoordinateAttribute) bends
                        .get(s);

                // checks if the mouse position is "near" the actual bend
                if ((position.x <= (moveBend.getX() + 6))
                        && (position.x >= (moveBend.getX() - 6))
                        && (position.y >= (moveBend.getY() - 6))
                        && (position.y <= (moveBend.getY() + 6)))
                    return;
            }
        }

        // check if mouse over an element
        if (rotationTool.getTopGEComponent(position) == null) {
            if (rotationTool.getMode() == AbstractEditingTool.DEFAULT) {
                if (!removeOldSelection()) {
                    Iterator<Node> checkNodes = rotationTool.getGraph()
                            .getNodesIterator();

                    while (checkNodes.hasNext()) {
                        Node node = checkNodes.next();

                        if (rotationTool.getCurrentSelectedElements().contains(
                                node)) {
                            rotationTool.getFormerSelectedElements().add(node);
                        }
                    }

                    Iterator<Edge> checkEdges = rotationTool.getGraph()
                            .getEdgesIterator();

                    while (checkEdges.hasNext()) {
                        Edge edge = checkEdges.next();

                        if (rotationTool.getCurrentSelectedElements().contains(
                                edge)) {
                            rotationTool.getFormerSelectedElements().add(edge);
                        }
                    }
                } else {
                    rotationTool.unmarkAll();
                }

                rotationTool.setStartSelectRectPosition(position);
                rotationTool.setCurrentSelectRectPosition(position);
                rotationTool.setModeToRect();
            }
        }
    }

    /**
     * Checks the current value of the parameter "remove-old-selection"
     * 
     * @return true for "yes", else false
     */
    private boolean removeOldSelection() {
        Object value = this.getValue("remove-old-selection");

        if (value == null) {
            value = "yes";
        }

        return value.equals("yes");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
