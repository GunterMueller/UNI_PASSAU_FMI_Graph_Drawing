// =============================================================================
//
//   GeoThicknessCalculationAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//   Created on Jun 22, 2005
// =============================================================================

package org.graffiti.plugins.algorithms.GeoThickness;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;

/**
 * @author ma
 * 
 *         test of Algorithms of Computation the geometrical thickness of a
 *         graph with three heuristics.
 */
public class GeoThicknessCalculationAlgorithm extends AbstractAlgorithm {

    /** Parameter array */
    private Parameter<?>[] Params;

    /** number of the approach */
    private int numberofapp;

    /** Parameters */
    private BooleanParameter algorithmus1, algorithmus2, algorithmus3,
            algorithmus4;

    /** an instance of class PlanarGraphSeek */
    private PlanarGraphSeek<?> planarGraphSeek = null;

    /** whether the program still runs */
    private boolean runFlag = true;

    private HashMap<Integer, LocalEdge> edgeList;

    private long runtime;

    private long runtimforCorss;

    private int cross, thickness;

    /** Constractor */
    public GeoThicknessCalculationAlgorithm() {
    }

    /**
     * @see AbstractAlgorithm#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        if (((BooleanParameter) params[0]).getBoolean().booleanValue()) {
            this.numberofapp = 1;
        } else if (((BooleanParameter) params[1]).getBoolean().booleanValue()) {
            this.numberofapp = 2;
        } else if (((BooleanParameter) params[2]).getBoolean().booleanValue()) {
            this.numberofapp = 3;
        } else if (((BooleanParameter) params[3]).getBoolean().booleanValue()) {
            this.numberofapp = 4;
        } else {
            this.numberofapp = 5;
        }
    }

    /*
     * 
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        // TODO Auto-generated method stub
        return "Geometric Thickness";
    }

    /*
     * 
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        BooleanParameter algorithmus1 = new BooleanParameter(false,
                "heuristic one",
                "search maximum planar subgraph with x coordinate");

        BooleanParameter algorithmus2 = new BooleanParameter(false,
                "heuristic two",
                "search maximum planar subgraph with sorted number of cross 1");

        BooleanParameter algorithmus3 = new BooleanParameter(false,
                "heuristic three",
                "search maximum planar subgraph with sorted number of cross 2");

        BooleanParameter algorithmus4 = new BooleanParameter(false,
                "heuristic four",
                "search maximum planar subgraph with edges cross");

        BooleanParameter algorithmus5 = new BooleanParameter(false,
                "heuristic five",
                "search maximum planar subgraph with sorted lang of edge");

        return new Parameter[] { algorithmus1, algorithmus2, algorithmus3,
                algorithmus4, algorithmus5 };
    }

    public void setApporch(int numberofapp) {
        this.numberofapp = numberofapp;
    }

    /*
     * 
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        this.Params = getAlgorithmParameters();

        algorithmus1 = (BooleanParameter) Params[0];
        algorithmus2 = (BooleanParameter) Params[1];
        algorithmus3 = (BooleanParameter) Params[2];
        algorithmus4 = (BooleanParameter) Params[3];
        // algorithmus5 = (BooleanParameter)Params[4];

        // The graph is inherited from AbstractAlgorithm.
        if (graph == null) {
            errors.add("The graph is empty.");
        }

        if ((algorithmus1.getBoolean().booleanValue() && algorithmus2
                .getBoolean().booleanValue())
                || (algorithmus1.getBoolean().booleanValue() && algorithmus3
                        .getBoolean().booleanValue())
                || (algorithmus1.getBoolean().booleanValue() && algorithmus4
                        .getBoolean().booleanValue())
                || (algorithmus2.getBoolean().booleanValue() && algorithmus3
                        .getBoolean().booleanValue())
                || (algorithmus2.getBoolean().booleanValue() && algorithmus4
                        .getBoolean().booleanValue())
                || (algorithmus3.getBoolean().booleanValue() && algorithmus4
                        .getBoolean().booleanValue())) {
            errors.add("one can select everyone times only one method. ");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /*
     * 
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        // TODO Auto-generated method stub
        this.runFlag = true;
        Iterator<Edge> edgeIt = this.graph.getEdgesIterator();

        while (edgeIt.hasNext()) {
            Edge edge = edgeIt.next();
            try {
                edge.removeAttribute("id");
            } catch (Exception e) {

            }
        }

        this.edgeList = new HashMap<Integer, LocalEdge>();
        scaleGraph();
        initMethodAndGraph(numberofapp);
        calcauationGeoThickness();
    }

    /**
     * Computation the geometrical thickness of a graph with determines
     * heuristic.
     */
    private void calcauationGeoThickness() {
        int gThickness = 0;

        this.runtime = System.currentTimeMillis();
        System.out.println("run time begging: " + this.runtime);

        if (numberofapp != 4) {
            while (this.runFlag == true) {
                if (numberofapp == 1) {
                    this.planarGraphSeek.setNodeList();
                }

                @SuppressWarnings("unchecked")
                PlanarGraphSeek<LocalEdge> pgs = (PlanarGraphSeek<LocalEdge>) planarGraphSeek;

                Collection<LocalEdge> subEdgeList = pgs.getPlanarGraph();

                this.runFlag = planarGraphSeek.isRun();

                if (subEdgeList != null) {
                    setEdgesColor(subEdgeList.iterator());
                    gThickness += 1;
                }
            }
        } else {
            @SuppressWarnings("unchecked")
            PlanarGraphSeek<ThicknessList> pgs = (PlanarGraphSeek<ThicknessList>) planarGraphSeek;
            Collection<ThicknessList> edgeList = pgs.getPlanarGraph();
            Iterator<ThicknessList> edgeIt = edgeList.iterator();
            while (edgeIt.hasNext()) {
                ThicknessList thicknessList = edgeIt.next();
                Collection<LocalEdge> subEdgeList = thicknessList.getEdgeSet();
                gThickness += 1;
                setEdgesColor(subEdgeList.iterator());
            }

        }

        this.thickness = gThickness;
        System.out.println("geometric thickness is:  " + gThickness);

        this.runtime = System.currentTimeMillis() - this.runtime;

        System.out.println("run time end: " + System.currentTimeMillis());
    }

    /**
     * this method decides, which method is for search for maximum planar graphs
     * to use
     * 
     * @param number
     *            int the number of the method
     */
    private void initMethodAndGraph(int number) {

        setEdgeList();

        switch (number) {
        case 1:
            this.planarGraphSeek = new PlanarGraphWithXCoordinate(this.edgeList);
            System.out.println("heuristic one");
            break;

        case 2:
            this.planarGraphSeek = new PlanarGraphWithSeqEdgeCross(
                    this.edgeList, number);
            this.planarGraphSeek.setNodeList();
            this.planarGraphSeek.resetHashMap();
            this.runtimforCorss = System.currentTimeMillis();
            System.out.println("set cross nummber time begging: "
                    + this.runtimforCorss);
            this.cross = this.planarGraphSeek.setCrossNummber();
            this.runtimforCorss = System.currentTimeMillis()
                    - this.runtimforCorss;
            System.out.println("set cross nummber time Endding: "
                    + System.currentTimeMillis());
            System.out.println("heuristic two");
            break;

        case 3:
            this.planarGraphSeek = new PlanarGraphWithSeqEdgeCross(
                    this.edgeList, number);
            this.planarGraphSeek.setNodeList();
            this.planarGraphSeek.resetHashMap();
            this.runtimforCorss = System.currentTimeMillis();
            System.out.println("set cross nummber time begging: "
                    + this.runtimforCorss);
            this.cross = this.planarGraphSeek.setCrossNummber();
            this.runtimforCorss = System.currentTimeMillis()
                    - this.runtimforCorss;
            System.out.println("set cross nummber time Endding: "
                    + System.currentTimeMillis());
            System.out.println("heuristic three");
            break;
        case 4:
            this.planarGraphSeek = new PlanarGraphWithEdgeCross(this.edgeList);
            this.planarGraphSeek.setNodeList();
            this.planarGraphSeek.resetHashMap();
            this.runtimforCorss = System.currentTimeMillis();
            System.out.println("set cross nummber time begging: "
                    + this.runtimforCorss);
            this.cross = this.planarGraphSeek.setCrossNummber();
            this.runtimforCorss = System.currentTimeMillis()
                    - this.runtimforCorss;
            System.out.println("set cross nummber time Endding: "
                    + System.currentTimeMillis());
            System.out.println("heuristic four");
            break;
        case 5:
            this.planarGraphSeek = new PlanarGraphWithLangOfEdge(this.edgeList,
                    number);
            this.planarGraphSeek.setNodeList();
            this.planarGraphSeek.resetHashMap();
            this.runtimforCorss = System.currentTimeMillis();
            System.out.println("set cross nummber time begging: "
                    + this.runtimforCorss);
            this.cross = this.planarGraphSeek.setCrossNummber();
            this.runtimforCorss = System.currentTimeMillis()
                    - this.runtimforCorss;
            System.out.println("set cross nummber time Endding: "
                    + System.currentTimeMillis());
            System.out.println("heuristic five");
            break;
        }
    }

    public int getThickness() {
        return this.thickness;
    }

    public int getCross() {
        return this.cross;
    }

    public long getRuntime() {
        return this.runtime;
    }

    public long getRuntimeforCross() {
        return this.runtimforCorss;
    }

    /**
     * give all edge an color
     * 
     * @param edges
     *            Iterator a edge list
     */
    private void setEdgesColor(Iterator<LocalEdge> edges) {
        int red = getRandomInt();
        int green = getRandomInt();
        int blue = getRandomInt();

        while (edges.hasNext()) {
            LocalEdge localEdge = edges.next();
            Iterator<Edge> edgeIt = this.graph.getEdgesIterator();
            while (edgeIt.hasNext()) {
                Edge edge = edgeIt.next();
                int edgeId = edge.getInteger("id");
                if (localEdge.getID().intValue() == edgeId) {
                    ColorAttribute edgeg = (ColorAttribute) edge
                            .getAttribute(GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.FRAMECOLOR);
                    edgeg.setColor(new Color(red, green, blue));
                    break;
                }

            }

        }
    }

    private int getRandomInt() {
        double random = Math.random();
        Float flo = new Float(255 * random);
        int result = Math.round(flo.floatValue());
        return result;
    }

    /** get the Information of edges into object LocalEdge */
    private void setEdgeList() {
        int id = 0;
        Iterator<Edge> edgeIt = this.graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge edge = edgeIt.next();
            Node sourceNode = edge.getSource();
            Node targetNode = edge.getTarget();
            Integer idStr = new Integer(id++);
            edge.addInteger("", "id", idStr.intValue());
            if (sourceNode.getDouble(GraphicAttributeConstants.COORDX_PATH) < targetNode
                    .getDouble(GraphicAttributeConstants.COORDX_PATH)) {
                LocalEdge localEdge = new LocalEdge(
                        idStr,
                        sourceNode
                                .getDouble(GraphicAttributeConstants.COORDX_PATH),
                        sourceNode
                                .getDouble(GraphicAttributeConstants.COORDY_PATH),
                        targetNode
                                .getDouble(GraphicAttributeConstants.COORDX_PATH),
                        targetNode
                                .getDouble(GraphicAttributeConstants.COORDY_PATH));
                this.edgeList.put(idStr, localEdge);
            } else {
                LocalEdge localEdge = new LocalEdge(
                        idStr,
                        targetNode
                                .getDouble(GraphicAttributeConstants.COORDX_PATH),
                        targetNode
                                .getDouble(GraphicAttributeConstants.COORDY_PATH),
                        sourceNode
                                .getDouble(GraphicAttributeConstants.COORDX_PATH),
                        sourceNode
                                .getDouble(GraphicAttributeConstants.COORDY_PATH));
                this.edgeList.put(idStr, localEdge);
            }
        }
    }

    private void scaleGraph() {

        List<Node> nodeList = this.graph.getNodes();

        HeapList heapNode;

        heapNode = new HeapList(1, 1);

        this.graph.getListenerManager().transactionStarted(this);

        for (int i = 0; i < nodeList.size(); i++) {
            heapNode.setElement(nodeList.get(i));
        }

        setXcoor(heapNode);

        heapNode = new HeapList(1, 2);

        for (int i = 0; i < nodeList.size(); i++) {
            heapNode.setElement(nodeList.get(i));
        }

        setYcoor(heapNode);

        this.graph.getListenerManager().transactionFinished(this);
    }

    private void setXcoor(HeapList heapNode) {
        Node node1 = null, node2 = null;

        CoordinateAttribute ca;

        this.graph.getListenerManager().transactionStarted(this);

        do {
            if (node1 == null) {
                node1 = (Node) heapNode.getElement();
            }

            if (!heapNode.isEmpty()) {
                node2 = (Node) heapNode.getElement();
            } else {
                node2 = null;
            }

            if (node2 == null) {
                break;
            }

            NumberFormat formatter = new DecimalFormat("0.000");

            double node1x = new Double(formatter.format(node1
                    .getDouble(GraphicAttributeConstants.COORDX_PATH)))
                    .doubleValue();
            double node2x = new Double(formatter.format(node2
                    .getDouble(GraphicAttributeConstants.COORDX_PATH)))
                    .doubleValue();

            // System.out.println("node1X: " + node1x + " node2X: " + node2x);

            if (node1x == node2x) {
                ca = (CoordinateAttribute) node1
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
                ca.setCoordinate(new Point2D.Double(node1x + 10, node1
                        .getDouble(GraphicAttributeConstants.COORDY_PATH)));
                heapNode.setElement(node1);
            }

            node1 = node2;

        } while (!heapNode.isEmpty());

        this.graph.getListenerManager().transactionFinished(this);

    }

    private void setYcoor(HeapList heapNode) {
        Node node1 = null, node2 = null;
        CoordinateAttribute ca;

        this.graph.getListenerManager().transactionStarted(this);

        do {
            if (node1 == null) {
                node1 = (Node) heapNode.getElement();
            }

            if (!heapNode.isEmpty()) {
                node2 = (Node) heapNode.getElement();
            } else {
                node2 = null;
            }

            if (node2 == null) {
                break;
            }

            NumberFormat formatter = new DecimalFormat("0.000");

            double node1y = new Double(formatter.format(node1
                    .getDouble(GraphicAttributeConstants.COORDY_PATH)))
                    .doubleValue();
            double node2y = new Double(formatter.format(node2
                    .getDouble(GraphicAttributeConstants.COORDY_PATH)))
                    .doubleValue();

            if (node1y == node2y) {
                ca = (CoordinateAttribute) node1
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
                ca.setCoordinate(new Point2D.Double(node1
                        .getDouble(GraphicAttributeConstants.COORDX_PATH),
                        node1y + 10));
                heapNode.setElement(node1);
            }

            node1 = node2;

        } while (!heapNode.isEmpty());

        this.graph.getListenerManager().transactionFinished(this);
    }
}
