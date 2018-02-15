// =============================================================================
//
//   StartAddEdgeAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StartAddEdgeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.create;

import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.plugins.views.defaults.DummySupportView;

/**
 * Action for function start-add-edge.
 * 
 * @deprecated
 */
@Deprecated
public class StartAddEdgeAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 7454940332068521065L;

    /** CreateTool-instance assigned to the function */
    private CreateTool createTool;

    private static final Logger logger = Logger
            .getLogger(StartAddEdgeAction.class.getName());

    /**
     * Constructs a new StartAddEdgeAction.
     * 
     * @param createTool
     *            the CreateTool-instance assigned to this Action
     */
    public StartAddEdgeAction(CreateTool createTool) {
        this.createTool = createTool;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public Map<String, Set<Object>> getValidParameters() {
        return construct1To2ParamMap("start-in-multi-command-mode", "yes", "no");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        if (e.getPosition() == null) {
            logger.finer("Can't operate " + "without position!");
        } else {
            Node nodeAtPosition = createTool.getTopNode(e.getPosition());

            if ((nodeAtPosition != null) && !createTool.hasAddEdgeChangedMode()) {
                if (createTool.isInDefaultMode()
                        || (createTool.isInMultiCommandMode()
                                && (createTool.getAddEdgeSourceNode() == null) && startInMultiCommandMode())) {
                    // System.out.println("... executing");
                    createDummyObjects(e.getPosition());
                    createTool.switchToMultiCommandMode();
                    createTool.addEdgeChangedMode();
                    // createTool.unmarkAll();
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param position
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private Node createDummyNode(Point position) {
        CollectionAttribute dummyCol = new HashMapAttribute("");
        NodeGraphicAttribute dummyGraphics = new NodeGraphicAttribute();
        dummyGraphics.getDimension().setHeight(0d);
        dummyGraphics.getDimension().setWidth(0d);
        dummyGraphics.setFrameThickness(0d);

        // ZOOMED
        dummyGraphics.getCoordinate().setCoordinate(position);
        dummyCol.add(dummyGraphics, false);

        return createTool.createDummyNode(dummyCol);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param position
     *            DOCUMENT ME!
     */
    private void createDummyObjects(Point position) {
        Node sourceNode = createTool.getTopNode(position);
        createTool.setAddEdgeSourceNode(sourceNode);

        Node dummyNode = createDummyNode(position);

        CollectionAttribute dummyEdgeCol = new HashMapAttribute("");
        EdgeGraphicAttribute dummyEdgeGraphAttr = new EdgeGraphicAttribute();
        dummyEdgeGraphAttr.setShape("org.graffiti.plugins.views.defaults."
                + "PolyLineEdgeShape");
        dummyEdgeCol.add(dummyEdgeGraphAttr, true);

        Edge dummyEdge = createTool.createDummyEdge(sourceNode, dummyNode,
                dummyEdgeCol);

        List<DummySupportView> views = createTool.getDummySupportViews();

        for (DummySupportView view : views) {
            view.addViewForNode(dummyNode);
            view.addViewForEdge(dummyEdge);
        }

        if (views.isEmpty()) {
            logger.finer("No view supporting dummy-objects!");
        }

        // this.first = clickedNode;
        // sourceCA.setCoordinate(e.getPoint());
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private boolean startInMultiCommandMode() {
        Object paramValue = getValue("start-in-multi-command-mode");

        // default yes
        if (paramValue == null) {
            paramValue = "yes";
        }

        return paramValue.equals("yes");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
