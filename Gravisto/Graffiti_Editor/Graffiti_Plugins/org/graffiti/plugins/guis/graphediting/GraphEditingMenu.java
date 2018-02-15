// =============================================================================
//   SelectMenu.java
//
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphEditingMenu.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import java.awt.event.InputEvent;

import javax.swing.KeyStroke;

import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.plugin.gui.GraffitiContainer;
import org.graffiti.plugin.gui.GraffitiMenu;
import org.graffiti.plugin.gui.GraffitiMenuItem;

/**
 * This menu contains some graph editing functions.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2006-11-06 16:48:42 +0100 (Mon, 06 Nov
 *          2006) $
 */
public class GraphEditingMenu extends GraffitiMenu implements GraffitiContainer {
    /**
     * 
     */
    private static final long serialVersionUID = -2549368353134546669L;
    /** This class's location */
    public static final String ID = "org.graffiti.plugin.guis.graphediting.GraphEditingMenu";

    /**
     * Creates a new Graph Editing Menu
     * 
     */
    public GraphEditingMenu() {
        super();
        setName("GraphEditing");
        setText(GraphEditingBundle.getString("menu.text"));
        // setMnemonic('');
        addItems();
    }

    /*
     * adds the items to this menu
     */
    private void addItems() {
        GraffitiMenuItem selectAllItem = new GraffitiMenuItem(ID,
                new SelectAllAction());
        selectAllItem.setText(((GraffitiAction) selectAllItem.getAction())
                .getName());

        GraffitiMenuItem selectAllNodesItem = new GraffitiMenuItem(ID,
                new SelectAllNodesAction());
        selectAllNodesItem.setText(((GraffitiAction) selectAllNodesItem
                .getAction()).getName());

        GraffitiMenuItem selectAllEdgesItem = new GraffitiMenuItem(ID,
                new SelectAllEdgesAction());
        selectAllEdgesItem.setText(((GraffitiAction) selectAllEdgesItem
                .getAction()).getName());

        GraffitiMenuItem selectIncidentEdgesItem = new GraffitiMenuItem(ID,
                new SelectIncidentEdgesAction());
        selectIncidentEdgesItem
                .setText(((GraffitiAction) selectIncidentEdgesItem.getAction())
                        .getName());

        GraffitiMenuItem selectIngoingEdgesItem = new GraffitiMenuItem(ID,
                new SelectIncomingEdgesAction());
        selectIngoingEdgesItem.setText(((GraffitiAction) selectIngoingEdgesItem
                .getAction()).getName());

        GraffitiMenuItem selectOutgoingEdgesItem = new GraffitiMenuItem(ID,
                new SelectOutgoingEdgesAction());
        selectOutgoingEdgesItem
                .setText(((GraffitiAction) selectOutgoingEdgesItem.getAction())
                        .getName());

        GraffitiMenuItem selectAllNeighboursItem = new GraffitiMenuItem(ID,
                new SelectAllNeighborsAction());
        selectAllNeighboursItem
                .setText(((GraffitiAction) selectAllNeighboursItem.getAction())
                        .getName());

        GraffitiMenuItem selectInNeighboursItem = new GraffitiMenuItem(ID,
                new SelectIncomingEdgeNeighborsAction());
        selectInNeighboursItem.setText(((GraffitiAction) selectInNeighboursItem
                .getAction()).getName());

        GraffitiMenuItem selectOutNeighboursItem = new GraffitiMenuItem(ID,
                new SelectOutgoingEdgeNeighborsAction());
        selectOutNeighboursItem
                .setText(((GraffitiAction) selectOutNeighboursItem.getAction())
                        .getName());

        GraffitiMenuItem selectIsolatedNodesItem = new GraffitiMenuItem(ID,
                new SelectIsolatedNodesAction());
        selectIsolatedNodesItem
                .setText(((GraffitiAction) selectIsolatedNodesItem.getAction())
                        .getName());

        GraffitiMenuItem removeIsolatedNodesItem = new GraffitiMenuItem(ID,
                new RemoveIsolatedNodesAction());
        removeIsolatedNodesItem
                .setText(((GraffitiAction) removeIsolatedNodesItem.getAction())
                        .getName());

        GraffitiMenuItem setNodeLabelsToNodeDegreeItem = new GraffitiMenuItem(
                ID, new LabelNodesByDegreeAction());
        setNodeLabelsToNodeDegreeItem
                .setText(((GraffitiAction) setNodeLabelsToNodeDegreeItem
                        .getAction()).getName());

        GraffitiMenuItem setNodeLabelsToNodeInDegreeItem = new GraffitiMenuItem(
                ID, new LabelNodesByInDegreeAction());
        setNodeLabelsToNodeInDegreeItem
                .setText(((GraffitiAction) setNodeLabelsToNodeInDegreeItem
                        .getAction()).getName());

        GraffitiMenuItem setNodeLabelsToNodeOutDegreeItem = new GraffitiMenuItem(
                ID, new LabelNodesByOutDegreeAction());
        setNodeLabelsToNodeOutDegreeItem
                .setText(((GraffitiAction) setNodeLabelsToNodeOutDegreeItem
                        .getAction()).getName());

        GraffitiMenuItem setDistinctIntegerNodeLabelsItem = new GraffitiMenuItem(
                ID, new SetDistinctIntegerNodeLabelsAction());
        setDistinctIntegerNodeLabelsItem
                .setText(((GraffitiAction) setDistinctIntegerNodeLabelsItem
                        .getAction()).getName());

        GraffitiMenuItem selectSelfloopsItem = new GraffitiMenuItem(ID,
                new SelectSelfLoopsAction());
        selectSelfloopsItem.setText(((GraffitiAction) selectSelfloopsItem
                .getAction()).getName());

        GraffitiMenuItem selectMultipleEdgesItem = new GraffitiMenuItem(ID,
                new SelectMultipleEdgesAction());
        selectMultipleEdgesItem
                .setText(((GraffitiAction) selectMultipleEdgesItem.getAction())
                        .getName());

        GraffitiMenuItem removeBendsItem = new GraffitiMenuItem(ID,
                new RemoveBendsAction());
        removeBendsItem.setText(((GraffitiAction) removeBendsItem.getAction())
                .getName());

        GraffitiMenuItem removeSelfloopsItem = new GraffitiMenuItem(ID,
                new RemoveSelfloopsAction());
        removeSelfloopsItem.setText(((GraffitiAction) removeSelfloopsItem
                .getAction()).getName());

        GraffitiMenuItem removeMultipleEdgesItem = new GraffitiMenuItem(ID,
                new RemoveMultipleEdgesAction());
        removeMultipleEdgesItem
                .setText(((GraffitiAction) removeMultipleEdgesItem.getAction())
                        .getName());

        GraffitiMenuItem removeEdgeLabelsItem = new GraffitiMenuItem(ID,
                new RemoveEdgeLabelsAction());
        removeEdgeLabelsItem.setText(((GraffitiAction) removeEdgeLabelsItem
                .getAction()).getName());

        GraffitiMenuItem removeNodeLabelsItem = new GraffitiMenuItem(ID,
                new RemoveNodeLabelsAction());
        removeNodeLabelsItem.setText(((GraffitiAction) removeNodeLabelsItem
                .getAction()).getName());

        GraffitiMenuItem splitEdgeItem = new GraffitiMenuItem(ID,
                new SplitEdgeAction());
        splitEdgeItem.setText(((GraffitiAction) splitEdgeItem.getAction())
                .getName());

        GraffitiMenuItem revertEdgeItem = new GraffitiMenuItem(ID,
                new RevertEdgeAction());
        revertEdgeItem.setText(((GraffitiAction) revertEdgeItem.getAction())
                .getName());

        GraffitiMenuItem removeNodeAttributeActionItem = new GraffitiMenuItem(
                ID, new RemoveNodeAttributeAction());
        removeNodeAttributeActionItem
                .setText(((GraffitiAction) removeNodeAttributeActionItem
                        .getAction()).getName());

        GraffitiMenuItem removeEdgeAttributeActionItem = new GraffitiMenuItem(
                ID, new RemoveEdgeAttributeAction());
        removeEdgeAttributeActionItem
                .setText(((GraffitiAction) removeEdgeAttributeActionItem
                        .getAction()).getName());

        GraffitiMenuItem selectReachableSubgraphItem = new GraffitiMenuItem(ID,
                new SelectReachableSubgraphAction());
        selectReachableSubgraphItem
                .setText(((GraffitiAction) selectReachableSubgraphItem
                        .getAction()).getName());

        GraffitiMenuItem selectConnectedComponentItem = new GraffitiMenuItem(
                ID, new SelectConnectedComponentAction());
        selectConnectedComponentItem
                .setText(((GraffitiAction) selectConnectedComponentItem
                        .getAction()).getName());

        add(selectAllItem);
        add(selectAllNodesItem);
        add(selectAllEdgesItem);
        addSeparator();
        add(selectIncidentEdgesItem);
        add(selectIngoingEdgesItem);
        add(selectOutgoingEdgesItem);
        addSeparator();
        add(selectAllNeighboursItem);
        add(selectInNeighboursItem);
        add(selectOutNeighboursItem);
        addSeparator();
        add(selectReachableSubgraphItem);
        selectReachableSubgraphItem.setAccelerator(KeyStroke.getKeyStroke('S',
                InputEvent.ALT_MASK));
        add(selectConnectedComponentItem);
        selectConnectedComponentItem.setAccelerator(KeyStroke.getKeyStroke('C',
                InputEvent.ALT_MASK));
        addSeparator();
        add(selectIsolatedNodesItem);
        add(removeIsolatedNodesItem);
        addSeparator();
        add(selectMultipleEdgesItem);
        add(removeMultipleEdgesItem);
        addSeparator();
        add(selectSelfloopsItem);
        add(removeSelfloopsItem);
        addSeparator();
        add(setNodeLabelsToNodeDegreeItem);
        add(setNodeLabelsToNodeInDegreeItem);
        add(setNodeLabelsToNodeOutDegreeItem);
        add(setDistinctIntegerNodeLabelsItem);
        add(removeNodeLabelsItem);
        addSeparator();
        add(removeEdgeLabelsItem);
        add(removeBendsItem);
        addSeparator();
        add(removeNodeAttributeActionItem);
        add(removeEdgeAttributeActionItem);
        addSeparator();
        add(splitEdgeItem);
        add(revertEdgeItem);
    }

    /**
     * @see org.graffiti.plugin.gui.GraffitiContainer#getId()
     */
    public String getId() {
        return ID;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
