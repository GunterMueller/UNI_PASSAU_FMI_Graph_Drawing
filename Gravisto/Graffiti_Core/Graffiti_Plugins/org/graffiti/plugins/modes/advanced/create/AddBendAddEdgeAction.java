// =============================================================================
//
//   AddBendAddEdgeAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AddBendAddEdgeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.create;

import java.awt.Point;
import java.util.logging.Logger;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.undo.ChangeAttributesEdit;

/**
 * To add bend, while adding an edge.
 * 
 * @deprecated
 */
@Deprecated
public class AddBendAddEdgeAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = -1851325627457221309L;

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(ChangeAttributesEdit.class.getName());

    /** Reference to the CreateTool */
    private CreateTool createTool;

    /**
     * Creates a new AddBendAddEdgeAction object.
     * 
     * @param createTool
     *            The given CreateTool
     */
    public AddBendAddEdgeAction(CreateTool createTool) {
        this.createTool = createTool;
    }

    /**
     * The action of this class.
     * 
     * @param e
     *            The given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        if (!createTool.isInMultiCommandMode()
                || (createTool.getAddEdgeSourceNode() == null)
                || createTool.hasAddEdgeChangedMode())
            return;

        Point position = e.getPosition();

        if (position == null) {
            logger.finer("AddBendAddEdgeAction says: "
                    + "Can't operate without position!!!");
        } else {
            if (createTool.isInMultiCommandMode()
                    && (createTool.getAddEdgeSourceNode() != null)) {
                if (createTool.getTopNode(position) == null) {
                    Edge dummyEdge = createTool.getDummyEdge();
                    CollectionAttribute attributes = dummyEdge.getAttributes();
                    EdgeGraphicAttribute edgeAttributes = (EdgeGraphicAttribute) attributes
                            .getAttribute(GraphicAttributeConstants.GRAPHICS);
                    SortedCollectionAttribute bends = edgeAttributes.getBends();

                    if (bends == null) {
                        bends = new LinkedHashMapAttribute(
                                GraphicAttributeConstants.BENDS);
                        edgeAttributes.setBends(bends);
                    }

                    int numberOfBends = edgeAttributes.getNumberOfBends();
                    bends.add(new CoordinateAttribute("bend" + numberOfBends,
                            position));
                    // createTool.unmarkAll();
                } else {
                    Node destNode = createTool.getTopNode(position);
                    Edge addedEdge = createTool.addEdge(destNode);

                    CollectionAttribute attributes = addedEdge.getAttributes();
                    EdgeGraphicAttribute edgeAttributes = (EdgeGraphicAttribute) attributes
                            .getAttribute(GraphicAttributeConstants.GRAPHICS);
                    SortedCollectionAttribute bends = edgeAttributes.getBends();

                    if (!((destNode == createTool.getAddEdgeSourceNode()) && bends
                            .isEmpty())) {
                        createTool.setAddEdgeSourceNode(null);

                        createTool.switchToDefaultMode();

                        createTool.addEdgeChangedMode();
                    }

                    // createTool.unmarkAll();
                }
            }
        }
    }
}
