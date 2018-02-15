// =============================================================================
//
//   RunTimeTests.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation;

import java.io.IOException;
import java.util.logging.LogManager;

import org.graffiti.editor.GraffitiEditor;
import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.generators.CompleteBinaryTreeGraphGenerator;
import org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation.tipover.TipoverLayout;

/**
 * Used to do runtime tests for Tipover-drawings. Uses the TipoverLayout
 * algorithm. It uses graph in which the nodes have random node sizes.
 * 
 * @author Andreas
 * @version $Revision$ $Date$
 */
public class RunTimeTestsTipover {
    public static void main(String[] argv) {

        // reading the logging config file
        try {
            LogManager.getLogManager().readConfiguration(
                    GraffitiEditor.class
                            .getResourceAsStream("Logging.properties"));
        } catch (IOException e) {

        }

        for (int size = 100; size <= 2000; size += 100) {
            Graph testGraph = new FastGraph();

            System.out
                    .println("====================================================");
            System.out.println("Size: " + size);
            System.out
                    .println("====================================================");

            CompleteBinaryTreeGraphGenerator generator = new CompleteBinaryTreeGraphGenerator();
            generator.attach(testGraph);
            generator.setDirected(true);
            generator.setNumOfNodes(size);
            generator.execute();

            // random node sizes...
            for (Node currentNode : testGraph.getNodes()) {
                double randomWidth = Math.random() * 100 + 100;
                double randomHeight = Math.random() * 100 + 100;
                DimensionAttribute dimensionAttribute = (DimensionAttribute) currentNode
                        .getAttribute("graphics.dimension");
                dimensionAttribute.setWidth(randomWidth);
                dimensionAttribute.setHeight(randomHeight);
            }

            TipoverLayout layouter = new TipoverLayout();
            layouter.attach(testGraph);
            layouter.setNodeDistance(25);
            layouter.setCostFunctionName("AREA");
            layouter.setNodesWithDimensions(true);

            System.out.println("------------------------");

            try {
                layouter.check();
            } catch (PreconditionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            for (int i = 0; i < 3; i++) {
                layouter.execute();
            }

        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
