package org.graffiti.plugins.algorithms.sugiyama.layout.cyclic;

import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit.isDummy;
import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit.isMarked;
import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit.level;
import static org.graffiti.plugins.algorithms.sugiyama.layout.cyclic.Toolkit.xpos;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;

class DebugToolkit {
    /**
     * Saves the original colors when they are colored.
     */
    private static Map<GraphElement, Color> backupMarked;
    private static Map<GraphElement, Color> backupBlocks;

    static void reset() {
        backupMarked = new HashMap<GraphElement, Color>();
        backupBlocks = new HashMap<GraphElement, Color>();
    }

    /**
     * Paints a GraphElement in a certain color. Saves the original colors.
     */
    private static void paintGraphElement(GraphElement ge, Color color,
            Map<GraphElement, Color> originalColors) {
        ColorAttribute ca = (ColorAttribute) ge
                .getAttribute("graphics.framecolor");
        if (!originalColors.containsKey(ge)) {
            originalColors.put(ge, ca.getColor());
        }
        ca.setColor(color);
    }

    /**
     * Paints all "marked" edges red. Saves the original colors.
     */
    static void paintMarkedEdges(Graph graph) {
        for (Edge edge : graph.getEdges())
            if (isMarked(edge)) {
                paintGraphElement(edge, Color.RED, backupMarked);
            }
    }

    static void unPaintMarkedEdges() {
        unColor(backupMarked);
    }

    /**
     * Paints all blocks green. Saves the original colors.
     */
    static void paintBlocks(Set<Block> blocks) {
        Graph graph = blocks.iterator().next().nodes[0].getGraph();

        for (Block block : blocks) {
            paintGraphElement(block.nodes[0], Color.GREEN, backupBlocks);
            for (int i = 1; i < block.nodes.length; i++) {
                Edge edge = graph.getEdges(block.nodes[i - 1], block.nodes[i])
                        .iterator().next();
                paintGraphElement(edge, Color.GREEN, backupBlocks);
            }
        }
    }

    static void unPaintBlocks() {
        unColor(backupBlocks);
    }

    /**
     * Restores the original color of edges and nodes that have been colored.
     */
    private static void unColor(Map<GraphElement, Color> backup) {
        for (Map.Entry<GraphElement, Color> entry : backup.entrySet()) {
            ((ColorAttribute) entry.getKey()
                    .getAttribute("graphics.framecolor")).setColor(entry
                    .getValue());
        }
        backup.clear();
    }

    static String getNodeLabel(Node node) {
        if (isDummy(node))
            return "Dummy (LEVEL=" + level(node) + " XPOS=" + xpos(node) + ")";
        else {
            try {
                return "Node " + node.getString("label.label");
            } catch (AttributeNotFoundException e) {
                return "Node (LEVEL=" + level(node) + " XPOS=" + xpos(node)
                        + ")";
            }
        }
    }
}
