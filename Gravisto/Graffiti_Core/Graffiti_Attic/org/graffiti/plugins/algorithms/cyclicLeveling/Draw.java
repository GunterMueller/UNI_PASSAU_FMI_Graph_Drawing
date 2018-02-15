// =============================================================================
//
//   Draw.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.cyclicLeveling;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;

/**
 * @author Gerg�
 * @version $Revision$ $Date$
 */
public class Draw {
    /* the graph which has to be draw */
    private Graph graph;

    private HashSet<Node>[] levels;

    /**
     * @param graph
     * @param levels
     */
    public Draw(Graph graph, HashSet<Node>[] levels) {
        this.graph = graph;
        this.levels = levels;

    }

    /** Assigns to each node a x an y coordinate */
    public void computePosition(DoubleParameter centerX,
            DoubleParameter centerY, IntegerParameter minDistance) {
        double[] angles = new double[levels.length];

        /* Compute the angle for each level */
        for (int i = 0; i < levels.length; i++) {
            angles[i] = (360.0 / levels.length) * i;
        }

        /* Assign coordinates to each node on each level */
        for (int l = 0; l < levels.length; l++) {
            Iterator<Node> iterator = levels[l].iterator();
            int i = 2;
            double d = minDistance.getInteger();

            double m = Math.sin(Math.toRadians(angles[l]))
                    / Math.cos(Math.toRadians(angles[l]));

            double x = 0;
            double y = 0;
            while (iterator.hasNext()) {
                x = 0;
                y = 0;
                Node node = iterator.next();
                node.setDouble("graphics.angle", angles[l]);
                if (angles[l] == 0) {
                    x = minDistance.getInteger() * (i);
                    y = 0;
                    /* 1st quadrant */
                } else if (angles[l] > 0 && angles[l] < 90) {
                    x = Math.sqrt(Math.pow(d * i, 2) / (Math.pow(m, 2) + 1));
                    y = m * x;

                } else if (angles[l] == 90) {
                    x = 0;
                    y = minDistance.getInteger() * (i);
                    /* 2nd quadrant */
                } else if (angles[l] > 90 && angles[l] < 180) {

                    x = Math.sqrt(Math.pow(d * i, 2) / (Math.pow(m, 2) + 1));
                    y = m * x;
                    x = x * (-1);
                    y = y * (-1);

                } else if (angles[l] == 180) {
                    x = minDistance.getInteger() * (-i);
                    y = 0;
                    /* 3rd quadrant */
                } else if (angles[l] > 180 && angles[l] < 270) {
                    x = Math.sqrt(Math.pow(d * i, 2) / (Math.pow(m, 2) + 1));
                    y = m * x;
                    x = x * (-1);
                    y = y * (-1);

                } else if (angles[l] == 270) {
                    x = 0;
                    y = minDistance.getInteger() * (-i);
                    /* 4th quadrant */
                } else if (angles[l] > 270 && angles[l] < 360) {
                    x = Math.sqrt(Math.pow(d * i, 2) / (Math.pow(m, 2) + 1));
                    y = m * x;
                }

                x = x + centerX.getDouble();
                y = centerY.getDouble() - y;

                node.changeDouble("graphics.coordinate.x", x);
                node.changeDouble("graphics.coordinate.y", y);
                i++;
            }
        }
    }

    /**
     * Changes the shape of the edges to a spiral shape
     */
    public void drawEdges() {

        Iterator<Edge> it = graph.getEdgesIterator();
        while (it.hasNext()) {
            Edge edge = it.next();
            edge
                    .changeString(GraphicAttributeConstants.SHAPE_PATH,
                            "org.graffiti.plugins.algorithms.cyclicLeveling.spiralshape.SpiralShape");
            edge.setString("label.label", lengthOfEdge(edge) + "");
        }

        changeColor();

    }

    /**
     * Changes the color of the edges
     */
    private void changeColor() {
        Iterator<Node> iterator = graph.getNodesIterator();

        while (iterator.hasNext()) {
            Node node = iterator.next();

            int red = (int) Math.round(Math.random() * 220);
            int blue = (int) Math.round(Math.random() * 220);
            int green = (int) Math.round(Math.random() * 220);

            Collection<Edge> edges = node.getAllOutEdges();
            Iterator<Edge> edgesIt = edges.iterator();
            while (edgesIt.hasNext()) {
                Edge edge = edgesIt.next();
                edge.setInteger("graphics.framecolor.blue", blue);
                edge.setInteger("graphics.fillcolor.blue", blue);
                edge.setInteger("graphics.framecolor.green", green);
                edge.setInteger("graphics.fillcolor.green", green);
                edge.setInteger("graphics.framecolor.red", red);
                edge.setInteger("graphics.fillcolor.red", red);
            }
        }
    }

    protected void printSumOfEdges(String algName) {
        int sum = lengthOfEdges();
        System.out.println(algName + " Gesamtl�nge: " + sum);
    }

    protected int lengthOfEdges() {
        int sum = 0;
        Iterator<Edge> it = graph.getEdgesIterator();
        while (it.hasNext()) {
            Edge edge = it.next();
            sum += lengthOfEdge(edge);
        }
        return sum;
    }

    /**
     * Computes the length of an Edges
     */
    public int lengthOfEdge(Edge e) {

        int sLevel, tLevel, edgeLength;
        sLevel = e.getSource().getInteger("level");
        tLevel = e.getTarget().getInteger("level");

        if (tLevel > sLevel) {
            edgeLength = tLevel - sLevel;
        } else {
            edgeLength = levels.length - (sLevel - tLevel);
        }
        return edgeLength;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
