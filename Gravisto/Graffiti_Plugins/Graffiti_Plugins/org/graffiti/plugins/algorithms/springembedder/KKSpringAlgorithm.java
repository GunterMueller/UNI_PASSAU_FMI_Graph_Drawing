// =============================================================================
//
//   KKSpringAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: KKSpringAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.springembedder;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugins.algorithms.apsp.PQEntry;
import org.graffiti.plugins.algorithms.trivialgrid.TrivialGridAlgorithm;
import org.graffiti.selection.Selection;
import org.graffiti.util.Pair;

/**
 * An inefficient spring embedder algorithm loosely based on Kamada & Kawai.
 */
public class KKSpringAlgorithm extends AbstractAlgorithm {

    /** DOCUMENT ME! */
    private Collection<Node> ignoreNodes;

    /** DOCUMENT ME! */
    private Map<Node, Map<Node, Double>> nodeMap;

    /** DOCUMENT ME! */
    private Rectangle largeRectangle = new Rectangle(0, 0, 80, 80);

    /** DOCUMENT ME! */
    private Selection selection;

    /** DOCUMENT ME! */
    private final boolean TWICE = false;

    /** DOCUMENT ME! */
    private boolean tighten;

    /** DOCUMENT ME! */
    private double CONSTANT = 2;

    /** DOCUMENT ME! */
    private double DEPS = 0.5;

    /** DOCUMENT ME! */
    private double EPS = 1;

    /** DOCUMENT ME! */
    private double EPS2 = 1;

    /** DOCUMENT ME! */
    private double IDEAL = 40;

    /** DOCUMENT ME! */
    private double IDEAL_MULT = 5;

    /** DOCUMENT ME! */
    private double ZERO_GRAPH_DIST = 2d;

    /** DOCUMENT ME! */
    private int MAX_NODES = 20;

    /**
     * Constructs a new instance.
     */
    public KKSpringAlgorithm() {
    }

    /**
     * This is used when the algorithm uses a grid instead of a spring embedder
     * because there are too many nodes.
     * 
     * @param rect
     */
    public void setBoundingRectangle(Rectangle rect) {
        this.largeRectangle = rect;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "KK-Spring-Embedder";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
        IDEAL = ((DoubleParameter) params[1]).getDouble().doubleValue();
        IDEAL_MULT = ((DoubleParameter) params[2]).getDouble().doubleValue();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Selection",
                "<html>The selection to work on.<p>If empty, "
                        + "the whole graph is used.</html>");
        selParam.setSelection(new Selection("_temp_"));

        DoubleParameter doubleParam = new DoubleParameter("Ideal length",
                "Constant used to calculate "
                        + "the ideal length of an edge (linear).");
        doubleParam.setDouble(IDEAL);

        DoubleParameter doubleParam2 = new DoubleParameter(
                "Length / node",
                "Constant used to calculate "
                        + "the ideal length of an edge (multiplied by number of nodes).");
        doubleParam2.setDouble(IDEAL_MULT);

        return new Parameter[] { selParam, doubleParam, doubleParam2 };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        Collection<Node> nodes;
        Collection<Edge> edges;

        if (selection.isEmpty()) {
            nodes = this.graph.getNodes();
            edges = this.graph.getEdges();
        } else {
            nodes = selection.getNodes();
            edges = selection.getEdges();
        }

        int nrNodes = nodes.size();

        if (selection.isEmpty()) {
            selection = new Selection("_temp_");

            for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
                selection.add(it.next());
            }

            for (Iterator<Edge> it = edges.iterator(); it.hasNext();) {
                selection.add(it.next());
            }
        }

        // too many nodes => use grid algorithm
        if (nrNodes > MAX_NODES) {
            // System.out.println("using gridlayout since " + nrNodes + "
            // nodes");
            TrivialGridAlgorithm gridAlg = new TrivialGridAlgorithm();
            gridAlg.attach(graph);

            SelectionParameter selParam = new SelectionParameter("", "");
            selParam.setSelection(selection);
            gridAlg.setAlgorithmParameters(new Parameter[] { selParam });

            gridAlg.setBoundingBox(largeRectangle);

            try {
                gridAlg.check();
            } catch (PreconditionException e) {
                throw new RuntimeException(e);
            }

            gridAlg.execute();

            return;
        }

        if (nrNodes < 1)
            return;

        IDEAL = (nrNodes * IDEAL_MULT) + IDEAL;

        UnDirDijkstraAlgorithm dijkstra = new UnDirDijkstraAlgorithm();

        nodeMap = new HashMap<Node, Map<Node, Double>>();

        Map<Node, Double> vlMap;
        Node node;

        for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
            node = it.next();

            vlMap = nodeMap.get(node);

            if (vlMap == null) {
                vlMap = new HashMap<Node, Double>();
                nodeMap.put(node, vlMap);
            }

            dijkstra.attach(this.graph);
            dijkstra.setSelection(selection);
            dijkstra.setOnlyResult(true);
            dijkstra.setSourceNode(node);

            try {
                dijkstra.check();
            } catch (PreconditionException e) {
                // TODO: react appropriately
            }

            dijkstra.execute();

            Map<Node, PQEntry> dNodes = dijkstra.getWeightsMap();

            dijkstra.reset();

            Set<Map.Entry<Node, PQEntry>> dNodesSet = dNodes.entrySet();

            for (Iterator<Map.Entry<Node, PQEntry>> dit = dNodesSet.iterator(); dit
                    .hasNext();) {
                Map.Entry<Node, PQEntry> entry = dit.next();
                Node targetNode = entry.getKey();

                if (!node.equals(targetNode)) {
                    vlMap.put(targetNode, new Double((entry.getValue())
                            .getDistance()));

                    Map<Node, Double> vlMap2 = nodeMap.get(targetNode);

                    if (vlMap2 == null) {
                        vlMap2 = new HashMap<Node, Double>();
                        nodeMap.put(targetNode, vlMap2);
                    }

                    vlMap2
                            .put(node, new Double(entry.getValue()
                                    .getDistance()));
                }
            }
        }

        // main part after perprocessing:
        double energy;
        double maxEnergy = 0.0;
        double graphEnergy = Double.POSITIVE_INFINITY;
        Node maxEnergyNode = null;
        ignoreNodes = new LinkedList<Node>();

        for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
            node = it.next();

            if (!ignoreNodes.contains(node)) {
                energy = calcNodeEnergy(node, nodes);

                if (energy > maxEnergy) {
                    maxEnergyNode = node;
                    maxEnergy = energy;
                }
            }
        }

        graph.getListenerManager().transactionStarted(this);

        boolean firsttry = true;

        while (graphEnergy > EPS) {
            if (maxEnergyNode != null) {
                moveNode(maxEnergyNode, nodes);
            }

            if (TWICE) {
                if ((ignoreNodes.size() == nrNodes) && firsttry) {
                    nodes = ignoreNodes;
                    ignoreNodes = new LinkedList<Node>();
                    firsttry = false;
                    System.out.println("----------- trying again -----------");
                }
            }

            Pair<Double, Node> pair = calcGraphEnergy(nodes);
            graphEnergy = (pair.getFst()).doubleValue();
            System.out.println("remaining: " + (nrNodes - ignoreNodes.size()));

            // System.out.println("graphEnergy: " + graphEnergy);
            maxEnergyNode = pair.getSnd();
        }

        ignoreNodes = new LinkedList<Node>();

        Pair<Double, Node> pair = calcGraphEnergy(nodes);
        graphEnergy = (pair.getFst()).doubleValue();
        System.out.println("remaining graphEnergy: " + graphEnergy);

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
        selection = null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param dx
     *            DOCUMENT ME!
     * @param dy
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private double getEuklidDist(double dx, double dy) {
        return Math.sqrt(getSqr(dx) + getSqr(dy));
    }

    /**
     * DOCUMENT ME!
     * 
     * @param u
     *            DOCUMENT ME!
     * @param v
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private double getEuklidDist(Node u, Node v) {
        CoordinateAttribute uCoord = (CoordinateAttribute) u
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        CoordinateAttribute vCoord = (CoordinateAttribute) v
                .getAttribute(GraphicAttributeConstants.COORD_PATH);

        return Math.sqrt(getSqr(uCoord.getX() - vCoord.getX())
                + getSqr(uCoord.getY() - vCoord.getY()));
    }

    /**
     * DOCUMENT ME!
     * 
     * @param u
     *            DOCUMENT ME!
     * @param v
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private double getKuv(Node u, Node v) {
        Map<Node, Double> vlMap = nodeMap.get(u);

        if (vlMap != null) {
            Double kuvD = vlMap.get(v);

            if (kuvD != null)
                return kuvD.doubleValue();
        }

        return 0.0;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param a
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private double getSqr(double a) {
        return a * a;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param node
     *            DOCUMENT ME!
     * @param nNode
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private double calcEdgeEnergy(Node node, Node nNode) {
        if (node.equals(nNode))
            return 0.0;

        double graphDist = getKuv(node, nNode);

        if (graphDist == 0.0) {
            graphDist = ZERO_GRAPH_DIST;
        }

        double error = (getEuklidDist(node, nNode) / graphDist) - IDEAL;
        tighten = error >= 0;

        return CONSTANT / 2 * getSqr(error);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param nodes
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private Pair<Double, Node> calcGraphEnergy(Collection<Node> nodes) {
        double energy = 0.0;
        double maxEnergy = 0.0;
        double tempEnergy;
        Node maxEnergyNode = null;
        Node node;

        for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
            node = it.next();

            if (!ignoreNodes.contains(node)) {
                tempEnergy = calcNodeEnergy(node, nodes);

                if (tempEnergy > maxEnergy) {
                    maxEnergyNode = node;
                    maxEnergy = tempEnergy;
                }

                energy += tempEnergy;
            }
        }

        // every edge counted twice therefore "/ 2d"
        return new Pair<Double, Node>(new Double(energy / 2d), maxEnergyNode);
    }

    /**
     * Calculates energy of one node, i.e. sum of energies of all edges of the
     * node.
     * 
     * @param node
     * @param nodes
     * 
     * @return double
     */
    private double calcNodeEnergy(Node node, Collection<Node> nodes) {
        double energy = 0.0;
        Node nNode;

        for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
            nNode = it.next();

            if (node.equals(nNode)) {
                continue;
            }

            energy += calcEdgeEnergy(node, nNode);
        }

        return energy;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param node
     *            DOCUMENT ME!
     * @param nodes
     *            DOCUMENT ME!
     */
    private void moveNode(Node node, Collection<Node> nodes) {
        CoordinateAttribute nodeCoord = (CoordinateAttribute) node
                .getAttribute(GraphicAttributeConstants.COORD_PATH);

        double energy = 0.0;
        double dx = 0.0;
        double dy = 0.0;
        double dnNx;
        double dnNy;
        double euklDist;
        Node nNode;
        CoordinateAttribute nNodeCoord;

        for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
            nNode = it.next();
            energy = Math.sqrt(calcEdgeEnergy(node, nNode));

            if (energy > EPS2) {
                nNodeCoord = (CoordinateAttribute) nNode
                        .getAttribute(GraphicAttributeConstants.COORD_PATH);
                dnNx = nNodeCoord.getX() - nodeCoord.getX();
                dnNy = nNodeCoord.getY() - nodeCoord.getY();
                euklDist = getEuklidDist(dnNx, dnNy);

                if (tighten) {
                    dx += ((dnNx * energy) / euklDist);
                    dy += ((dnNy * energy) / euklDist);
                } else {
                    dx -= ((dnNx * energy) / euklDist);
                    dy -= ((dnNy * energy) / euklDist);
                }
            }
        }

        int nrNodes = nodes.size();
        dx = dx / nrNodes;
        dy = dy / nrNodes;
        nodeCoord.setX(nodeCoord.getX() + dx);
        nodeCoord.setY(nodeCoord.getY() + dy);

        // try {
        // System.out.println(" - moved node: " +
        // ((NodeLabelAttribute)node.getAttribute("label")).getLabel() +
        // " by (" + dx + ", " + dy + ")");
        // } catch (Exception e) {}
        if ((Math.abs(dx) < DEPS) && (Math.abs(dy) < DEPS)) {
            // System.out.println("probably swinging ... giving up");
            // return false; // probably swinging ...
            // try {
            // System.out.println("probably swinging ... ignore node: " +
            // ((NodeLabelAttribute)node.getAttribute("label"))
            // .getLabel());
            // } catch (Exception e) {}
            ignoreNodes.add(node);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
