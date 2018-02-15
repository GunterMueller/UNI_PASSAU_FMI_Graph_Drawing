/**
 * the input of graph is planar.
 * there is two case for the upward planar drawing and connected. 
 * case 1.: there is a planar drawing that is a planar graph
 * case 2.: there is a non planar graph that is a planar graph. 
 * 
 * @author Jin
 */
package org.graffiti.plugins.algorithms.upward;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.util.Queue;

public class UpwardAdministration extends AbstractAlgorithm {
    /**
     * the parameters of algorithm
     */
    private boolean case1;

    /**
     * minimal distance
     */
    private int minDistance;

    /**
     * case 1
     */
    private static final String PLANAR_DRAWING = "planar drawing: use the embedding from the drawing";

    /**
     * case 2
     */
    private static final String NO_PLANAR_DRAWING = "no planar drawing: use an embedding returned by the planarity test";

    /**
     * The logger to inform or warn the user
     */
    private static final Logger logger = Logger
            .getLogger(UpwardAdministration.class.getName());

    /**
     * circular sequence of edges for all nodes
     */
    private SuperNode[] sequences;

    /**
     * list of all faces of the graph
     */
    private LinkedList<MyFace> facesOfGraph;

    /**
     * index of the external face in faceOfGraph
     */
    private int externalFace;

    /**
     * list of added new edges (by graph => st-graph)
     */
    private LinkedList<Edge> addedEdges;

    /**
     * list of all faces of the graph for case 2.
     */
    private Set<Face> setOfFaces;

    /**
     * planarity test.
     */
    private PlanarityAlgorithm pAlgorithm;

    /**
     * constructor
     * 
     */
    public UpwardAdministration() {
        this.addedEdges = new LinkedList<Edge>();
        this.externalFace = -1;
    }

    /**
     * get name of algorithm
     * 
     * @return name
     */
    public String getName() {
        return "Upward Planar Drawing";
    }

    /**
     * set parameters
     * 
     * @param params
     *            list of parameters
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        String param = ((StringSelectionParameter) params[0])
                .getSelectedValue();
        if (param.equals(PLANAR_DRAWING)) {
            case1 = true;
        } else {
            case1 = false;
        }
        minDistance = ((IntegerParameter) params[1]).getInteger().intValue();
    }

    /**
     * get parameters
     * 
     * @return list of parameters
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        Parameter[] parameter = new Parameter[2];
        String[] caseParams = { PLANAR_DRAWING, NO_PLANAR_DRAWING };

        StringSelectionParameter makeParam = new StringSelectionParameter(
                caseParams, "two embedding", "select");

        IntegerParameter minDis = new IntegerParameter(new Integer(50),
                new Integer(40), new Integer(200),
                "minimal distance of two nodes:",
                "minimal distance of two nodes in the drawing");

        parameter[0] = makeParam;
        parameter[1] = minDis;
        return parameter;
    }

    /**
     * Test, whether the graph is empty, too small, connected, directed, simple,
     * planar, acyclic, bimodal, consistent assignment or flow network, than the
     * graph to st-graph.
     */
    @Override
    public void check() throws PreconditionException {
        this.graph = GraffitiSingleton.getInstance().getMainFrame()
                .getActiveSession().getGraph();

        boolean noErorr = true;
        PreconditionException errors = new PreconditionException();

        if (graph.getNumberOfNodes() == 0) {
            noErorr = false;
            errors.add("the graph is empty");
        } else if (graph.getNumberOfNodes() == 1) {
            noErorr = false;
            errors.add("the graph is too small");
        } else if (this.graph.getNumberOfNodes() > 1) {
            if (!isConnected()) {
                noErorr = false;
                errors.add("the graph should be connected");
            }
        } else if (!this.graph.isDirected()) {
            noErorr = false;
            errors.add("the graph must be directed");
        }
        if (noErorr) {
            // remove multi-edges and loops
            this.removeMultiEdgesAndLoops();
        }

        // case 1:
        if (this.case1 && noErorr) {
            // non-planar drawing => error
            if (!this.isPlanarDrawing()) {
                noErorr = false;
                errors
                        .add("The graph isn`t drawn planar. The graph must be drawn planar to run upward planar drawing");
            } else {
                // compute faces of the graph.
                this.executeFaces();
            }

            if (noErorr) {
                // acyclic
                if (!this.isAcyclicFor1()) {
                    noErorr = false;
                    errors
                            .add("The graph contains a cycle. The graph can`t therefore be upward planar");
                }
                // bimodal
                else if (!this.isBimodalFor1()) {
                    noErorr = false;
                    errors.add("The graph dosn't bimodal. Graph must"
                            + " be bimodal to run planar Upward drawing. ");
                }
                // consistent assignment
                else if (!this.consistentForFirst()) {
                    noErorr = false;
                    errors
                            .add("The graph doesn`t allow a consistent assignment of sources and sinks to its faces. The graph can`t therefore be upward planar.");
                }
                // the algorithm assign-upward and the algorithm saturate-face
                // the graph to st-graph.
                else if (noErorr) {
                    AssignUpward assignUpward = new AssignUpward(this.graph,
                            this.addedEdges, this.sequences, this.facesOfGraph,
                            this.externalFace);
                    assignUpward.execute();
                }
            }

        }
        // case 2:
        else if (noErorr) {
            pAlgorithm = new PlanarityAlgorithm();
            pAlgorithm.attach(graph);
            pAlgorithm.testPlanarity();

            // test planarity
            if (!pAlgorithm.isPlanar()) {
                noErorr = false;
                errors
                        .add("The graph is not planar. Graph must be planar to run "
                                + "planar Upward drawing. ");
            } else {
                // calculate faces
                this.setOfFaces = pAlgorithm.getTestedGraph()
                        .getTestedComponents().get(0).getFaces().getFaces();
            }

            if (noErorr) {
                // test acyclic
                if (!this.isAcyclicFor2()) {
                    noErorr = false;
                    errors
                            .add("The graph contains a cycle. The graph can`t therefore be upward planar");
                }

                // test bimodal
                else if (!this.isBimodalFor2()) {
                    noErorr = false;
                    errors.add("The graph dosn't bimodal. Graph must"
                            + " be bimodal to run planar Upward drawing. ");
                }

                // find the external face
                else if (!this.definiteExFace()) {
                    noErorr = false;
                    errors
                            .add("there exists a face in the graph, which hasn`t at least one source and one sink. The graph can`t therefore be upward planar");
                }

                // consistent assignment with flow network.
                else if (!this.consistentWithFlowNetzwerk()) {
                    noErorr = false;
                    errors
                            .add("The graph doesn`t allow a consistent assignment of sources and sinks to its faces. The graph can`t therefore be upward planar.");
                }
                // the graph to st-graph.
                if (noErorr) {
                    this.toStGraphFor2();
                }
            }
        }

        if (noErorr) {
            // TiefenTest
            TiefeTest tiefeTest = new TiefeTest(this.graph);
            if (!tiefeTest.isSimpleUpwardPlanar()) {
                noErorr = false;
                errors
                        .add("The graph contains BFS-intra-level-edges or edges edges spanning over more than one BFS-level. Therefore the graph isn`t simply upward drawable.");
            }
        }

        if (!noErorr) {
            this.removeAddedNewEdges();
            throw errors;
        }
    }

    /**
     * remove all prior added edges from graph
     */
    private void removeAddedNewEdges() {
        int size = this.addedEdges.size();
        for (int i = 0; i < size; i++) {
            this.graph.deleteEdge(this.addedEdges.remove());
        }
    }

    /**
     * Test, whether the graph is connected for case 1 and 2
     * 
     * @return true, when the graph is connected.
     */
    private boolean isConnected() {

        boolean connected = false;
        Node sourceNode = null;
        Iterator<Node> nodeIt = this.graph.getNodesIterator();
        if (nodeIt.hasNext()) {
            sourceNode = nodeIt.next();
        }

        Queue queue = new Queue();

        Set<Node> visited = new HashSet<Node>();
        queue.addLast(sourceNode);
        visited.add(sourceNode);

        while (!queue.isEmpty()) {
            Node node = (Node) queue.removeFirst();

            Iterator<Node> neighbors = node.getNeighborsIterator();
            while (neighbors.hasNext()) {
                Node neighbor = neighbors.next();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.addLast(neighbor);
                }
            }
        }
        if (graph.getNodes().size() == visited.size()) {
            connected = true;
        }
        return connected;
    }

    /**
     * remove multi-edges and loops in same node.
     */
    private void removeMultiEdgesAndLoops() {
        LinkedList<Edge> removedEdges = new LinkedList<Edge>();
        HashList<Edge> edgesToDelete = new HashList<Edge>();
        Iterator<Edge> edgeIt = this.graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();

            if (e.getSource() == e.getTarget()) {
                removedEdges.add(e);
                edgesToDelete.append(e);
            }
        }
        edgeIt = this.graph.getEdgesIterator();
        int i = 0;
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            Collection<Edge> edges = this.graph.getEdges(e.getSource(), e
                    .getTarget());
            if (edges.size() > 1) {
                Iterator<Edge> multiEdgeIt = edges.iterator();
                multiEdgeIt.next();
                while (multiEdgeIt.hasNext()) {
                    Edge multiEdge = multiEdgeIt.next();
                    i = i + 1;
                    removedEdges.add(multiEdge);
                    edgesToDelete.append(multiEdge);
                }
            }
        }
        edgeIt = edgesToDelete.iterator();
        while (edgeIt.hasNext()) {
            this.graph.deleteEdge(edgeIt.next());
        }
    }

    // --------------------------------------------------------------------
    // for case 1:--------------------------start-------------------------
    // --------------------------------------------------------------------

    /**
     * test, weather the graph is a non-planar drawing
     * 
     * @return true, when the graph is a planar drawing
     */
    private boolean isPlanarDrawing() {
        Iterator<Edge> edgesIt = this.graph.getEdgesIterator();
        LinkedList<Edge> edges = new LinkedList<Edge>();
        while (edgesIt.hasNext()) {
            edges.add(edgesIt.next());
        }
        for (int i = 0; i < edges.size() - 1; i++) {
            Edge edge = edges.get(i);
            for (int j = (i + 1); j < edges.size(); j++) {
                Edge target = edges.get(j);
                if (this.isIntersect(edge, target))
                    return false;
            }
        }
        return true;
    }

    /**
     * test, weather a crossing between two edges have
     * 
     * @param edge1
     *            one edge
     * @param edge2
     *            other edge
     * @return true, when the edges have a crossing
     */
    private boolean isIntersect(Edge edge1, Edge edge2) {
        double x1 = edge1.getSource().getDouble(
                GraphicAttributeConstants.COORDX_PATH);
        double y1 = edge1.getSource().getDouble(
                GraphicAttributeConstants.COORDY_PATH);

        double x2 = edge1.getTarget().getDouble(
                GraphicAttributeConstants.COORDX_PATH);
        double y2 = edge1.getTarget().getDouble(
                GraphicAttributeConstants.COORDY_PATH);

        double x3 = edge2.getSource().getDouble(
                GraphicAttributeConstants.COORDX_PATH);
        double y3 = edge2.getSource().getDouble(
                GraphicAttributeConstants.COORDY_PATH);

        double x4 = edge2.getTarget().getDouble(
                GraphicAttributeConstants.COORDX_PATH);
        double y4 = edge2.getTarget().getDouble(
                GraphicAttributeConstants.COORDY_PATH);

        double k1 = 0d;
        if (x1 != x2) {
            k1 = (y2 - y1) / (x2 - x1);
        }
        double k2 = 0d;
        if (x3 != x4) {
            k2 = (y4 - y3) / (x4 - x3);
        }

        double x0 = 0d;
        double y0 = 0d;

        if ((x1 == x2) && (x3 == x4)) {
            double max = y1;
            double min = y1;
            if (y2 > max) {
                max = y2;
            }
            if (y2 < min) {
                min = y2;
            }
            if (y3 > max) {
                max = y3;
            }
            if (y3 < min) {
                min = y3;
            }
            if (y4 > max) {
                max = y4;
            }
            if (y4 < min) {
                min = y4;
            }
            if (((Math.abs(y1 - y2) + Math.abs(y3 - y4)) > Math.abs(max - min))
                    && (x3 == x1))
                return true;
            else
                return false;
        } else if ((x1 == x2) && (x3 != x4)) // ok
        {
            x0 = x1;
            y0 = k2 * (x1 - x3) + y3;
        } else if ((x1 != x2) && (x3 == x4)) // ok
        {
            x0 = x3;
            y0 = k1 * (x3 - x1) + y1;
        } else if ((x1 != x2) && (x3 != x4)) {
            x0 = (k1 * x1 - k2 * x3 + y3 - y1) / (k1 - k2);
            y0 = k1 * (x0 - x1) + y1;
        }
        double abstand1 = Math.abs(Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
                * (y1 - y2))
                - Math.sqrt((x0 - x2) * (x0 - x2) + (y0 - y2) * (y0 - y2))
                - Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)));

        double abstand2 = Math.abs(Math.sqrt((x3 - x4) * (x3 - x4) + (y3 - y4)
                * (y3 - y4))
                - Math.sqrt((x0 - x4) * (x0 - x4) + (y0 - y4) * (y0 - y4))
                - Math.sqrt((x3 - x0) * (x3 - x0) + (y3 - y0) * (y3 - y0)));

        if ((abstand1 < 0.0001)
                && (abstand2 < 0.0001)
                && (!edge1.getSource().equals(edge2.getSource())
                        && !edge1.getSource().equals(edge2.getTarget())
                        && !edge1.getTarget().equals(edge2.getSource()) && !edge1
                        .getTarget().equals(edge2.getTarget())))
            return true;
        return false;
    }

    /**
     * compute list of super nodes. compute all faces of the graph.
     * 
     */
    private void executeFaces() {
        // list of super nodes
        this.sequences = new SuperNode[this.graph.getNodes().size()];
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        int i = 0;
        while (nodesIt.hasNext()) {
            Node node = nodesIt.next();
            node.setInteger("number", i);
            SuperNode sequence = new SuperNode(node);
            Iterator<Edge> edgesIt = node.getEdgesIterator();
            edgesIt.hasNext();
            Edge start = edgesIt.next();
            sequence.addEdge(start);
            sequence.addValue(0d);
            while (edgesIt.hasNext()) {
                Edge edge = edgesIt.next();
                sequence.addEdge(edge);

                // angle between start and edge in counterclockwise order.
                Angle angle = new Angle(start, edge);
                angle.execute();
                sequence.addValue(new Double(angle.getValue()));
            }
            // quick sort edges with angle in counterclockwise order.
            sequence.quickSort(0, sequence.getValues().size() - 1);
            this.sequences[i] = sequence;
            i++;
        }

        // compute faces
        Faces faces = new Faces(this.sequences);
        faces.execute();
        this.facesOfGraph = faces.getFaces();
    }

    /**
     * test, weather the graph is acyclic
     * 
     * @return true, when the graph is acyclic
     */
    private boolean isAcyclicFor1() {
        Iterator<MyFace> facesIt = this.facesOfGraph.iterator();
        while (facesIt.hasNext()) {
            MyFace face = facesIt.next();
            List<Edge> edgesList = face.getEdges();
            boolean isCycle = true;
            for (int i = 0; i < edgesList.size(); i++) {
                Edge edge1 = edgesList.get(i);
                Edge edge2 = edgesList.get((i + 1) % edgesList.size());
                if (edge1.getSource().equals(edge2.getSource())) {
                    isCycle = false;
                    break;
                }
            }
            if (isCycle)
                return false;
        }
        return true;
    }

    /**
     * test, weather the graph is bimodal
     * 
     * @return true, when the graph is bimodal
     */
    private boolean isBimodalFor1() {
        for (int i = 0; i < this.graph.getNodes().size(); i++) {
            Node node = this.graph.getNodes().get(i);
            List<Edge> edges = this.sequences[node.getInteger("number")]
                    .getEdges();

            boolean outgoing = false;
            boolean incoming = false;
            int change = 0;
            for (int j = 0; j < edges.size(); j++) {
                Edge edge = edges.get(j);

                // outgoing edge of the node
                if (edge.getSource().equals(node)) {
                    outgoing = true;
                    if (outgoing && incoming) {
                        change++;
                    }
                    incoming = false;
                }
                // incoming edge of the node
                else {
                    incoming = true;
                    if (outgoing && incoming) {
                        change++;
                    }
                    outgoing = false;
                }
            }
            if (change > 2)
                return false;

        }
        return true;
    }

    /**
     * test, weather the graph have a consistent assignment find the external
     * face.
     * 
     * @return true, when the graph have a consistent assignment
     */
    private boolean consistentForFirst() {
        for (int index = 0; index < this.facesOfGraph.size(); index++) {
            MyFace face = this.facesOfGraph.get(index);

            int numberOfLargeAngle = 0;
            int numberOfSmallAngle = 0;
            int numberOFIncomingEdges = 0;
            double sumOfAngles = 0d;

            List<Edge> edgesList = face.getEdges();
            for (int i = 0; i < edgesList.size(); i++) {
                Edge edge1 = edgesList.get(i);
                Edge edge2 = edgesList.get((i + 1) % edgesList.size());

                Angle angle = new Angle(edge1, edge2);
                angle.execute();
                sumOfAngles = sumOfAngles + angle.getValue();
                if (angle.getValue() == 0d) {

                    numberOfLargeAngle++;
                    sumOfAngles = sumOfAngles + 2 * Math.PI;
                    Collection<Edge> edgesIt = edge1.getSource().getEdges();
                    if (edgesIt.size() > 1) {
                        numberOFIncomingEdges++;
                    }
                } else {
                    // two incoming edges
                    if (angle.getSinkSwitch()) {
                        numberOFIncomingEdges++;
                    }
                    // if large angle. i.e. angle greater then pi.
                    if (angle.getIsLargeAngle()) {
                        numberOfLargeAngle++;
                    }
                    // if small angle. i.e. angle <= pi.
                    if (angle.getIsSmallAngle()) {
                        numberOfSmallAngle++;
                    }
                }
            }
            // visit edges in clockwise order in face
            if (sumOfAngles > (edgesList.size() * 2 * Math.PI - sumOfAngles)) {
                numberOfLargeAngle = numberOfSmallAngle;
                this.externalFace = index;
            }
            if ((numberOFIncomingEdges - 1) != numberOfLargeAngle)
                return false;
        }
        return true;
    }

    // for case 1: --------------------------end ------------------------------

    // -------------------------------------------------------------------------
    // for case 2: ----------------------start---------------------------------
    // -------------------------------------------------------------------------

    /**
     * test, whether the graph is acyclic. create list of all faces of the graph
     * - facesOfGraph.
     * 
     * @return true, create facesOfGraph.
     */
    private boolean isAcyclicFor2() {
        Iterator<Face> facesIt = this.setOfFaces.iterator();

        this.facesOfGraph = new LinkedList<MyFace>();
        while (facesIt.hasNext()) {
            Face face = facesIt.next();

            List<Dart> darts = face.getDarts();

            boolean haveSS = false;

            // my face
            MyFace myFace = new MyFace();
            List<Node> nodesList = face.getNodes();

            for (int i = 0; i < darts.size(); i++) {

                Dart dart1 = darts.get(i);
                Edge edge1 = dart1.getEdge();

                // my face
                myFace.addEdge(edge1);
                myFace.addNode(nodesList.get(i));

                int j = (i + 1) % darts.size();
                Dart dart2 = darts.get(j);
                Edge edge2 = dart2.getEdge();
                if (edge1.getSource().equals(edge2.getSource())
                        || edge1.getTarget().equals(edge2.getTarget())) {
                    haveSS = true;
                }
            }
            // my face
            this.facesOfGraph.add(myFace);

            if (!haveSS)
                return false;
        }
        return true;
    }

    /**
     * test, whether the graph is bimodal. create list of super nodes.
     * 
     * @return true, when the graph is bimodal
     */
    private boolean isBimodalFor2() {

        this.sequences = new SuperNode[this.graph.getNodes().size()];

        for (int i = 0; i < this.graph.getNodes().size(); i++) {
            Node node = this.graph.getNodes().get(i);
            List<Node> neighbors = pAlgorithm.getTestedGraph()
                    .getAdjacencyList(node);
            node.setInteger("number", i);

            SuperNode sequence = new SuperNode(node);
            boolean outgoing = false;
            boolean incoming = false;
            int change = 0;
            for (int j = 0; j < neighbors.size(); j++) {
                Node neighbor = neighbors.get(j);

                Edge edge = this.graph.getEdges(node, neighbor).iterator()
                        .next();
                if (edge.getSource().equals(node)) {
                    outgoing = true;
                    if (outgoing && incoming) {
                        change++;
                    }
                    incoming = false;
                } else {
                    incoming = true;
                    if (outgoing && incoming) {
                        change++;
                    }
                    outgoing = false;
                }
                sequence.addEdge(edge, 0);
            }
            if (change > 2)
                return false;
            this.sequences[i] = sequence;
        }
        return true;
    }

    /**
     * find a face that have a source and a sink of graph. the face is the
     * external face.
     * 
     * @return true, when the graph have a external face
     */
    private boolean definiteExFace() {
        this.externalFace = 0;

        for (int index = 0; index < this.facesOfGraph.size(); index++) {
            MyFace face = this.facesOfGraph.get(index);

            int numOfSink = 0;
            int numOfSource = 0;

            List<Node> nodes = face.getNodes();

            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);

                if (node.getAllInEdges().isEmpty()) {
                    numOfSource++;
                } else if (node.getAllOutEdges().isEmpty()) {
                    numOfSink++;
                }
            }
            if ((numOfSink > 0) && (numOfSource > 0))
                return true;
            externalFace++;
        }
        return false;
    }

    /**
     * test, whether the graph have consistent assignment with flow network.
     * 
     * @return true, when the graph have consistent assignment.
     */
    private boolean consistentWithFlowNetzwerk() {
        // setSS is set of sources and sinks of graph
        HashMap<Integer, Node> setSS = new HashMap<Integer, Node>();
        Iterator<Node> nodes = this.graph.getNodesIterator();
        while (nodes.hasNext()) {
            Node node = nodes.next();
            if (node.getAllInEdges().isEmpty()) {
                setSS.put(new Integer(node.getInteger("number")), node);
            } else if (node.getAllOutEdges().isEmpty()) {
                setSS.put(new Integer(node.getInteger("number")), node);
            }
        }

        // -------- faces: A(f) - 1 = demands or A(f) + 1 = demands----------

        int size = this.graph.getNodes().size();
        LinkedList<Integer>[] faces = new LinkedList[size];

        for (int i = 0; i < this.facesOfGraph.size(); i++) {
            // number of sinks and sources of graph in the face

            int numberOfSS = 0;
            int length = this.graph.getNodes().size();
            boolean[] visited = new boolean[length];
            for (int k = 0; k < length; k++) {
                visited[k] = false;
            }

            MyFace internal = this.facesOfGraph.get(i);
            List<Node> nodesList = internal.getNodes();
            for (int j = 0; j < nodesList.size(); j++) {
                Node node = nodesList.get(j);
                if (setSS.get(new Integer(node.getInteger("number"))) != null) {
                    if (!visited[node.getInteger("number")]) {
                        numberOfSS++;
                        visited[node.getInteger("number")] = true;
                    }
                }
            }

            // A(f)
            List<Edge> edgesList = internal.getEdges();
            int af = 0;
            for (int j = 0; j < edgesList.size(); j++) {
                Edge first = edgesList.get(j);
                Edge second = edgesList.get((j + 1) % edgesList.size());
                if ((first.getTarget().equals(second.getTarget()) && (!first
                        .getSource().equals(second.getSource())))
                        || (first.equals(second) && (first.getTarget()
                                .getNeighbors().size() == 1))) {
                    af++;
                }
            }

            // external face
            if (i == this.externalFace) {
                af = af + 1;
            }
            // internal faces
            else {
                af = af - 1;
            }

            if (numberOfSS < af)
                return false;
            else if (numberOfSS == af) {
                if (af > 0) {
                    for (int k = 0; k < nodesList.size(); k++) {
                        Node node = nodesList.get(k);
                        if (setSS.get(new Integer(node.getInteger("number"))) != null) {
                            setSS
                                    .remove(new Integer(node
                                            .getInteger("number")));
                        }
                    }
                }
            } else // numberOfSS > af
            {
                if (af > 0) {
                    faces[af] = new LinkedList<Integer>();
                    faces[af].add(new Integer(i));
                }
            }
        }

        // repeat
        for (int i = 1; i < faces.length; i++) {
            if (faces[i] != null) {

                for (int j = 0; j < faces[i].size(); j++) {
                    Integer number = faces[i].get(j);
                    MyFace face = this.facesOfGraph.get(number.intValue());
                    List<Node> nodesList = face.getNodes();
                    int until = i;
                    for (int k = 0; k < nodesList.size(); k++) {
                        Node node = nodesList.get(k);
                        if (setSS.get(new Integer(node.getInteger("number"))) != null) {
                            setSS
                                    .remove(new Integer(node
                                            .getInteger("number")));
                            until--;
                        }
                        if (until == 0) {
                            break;
                        }
                    }

                    if (until > 0)
                        return false;

                }
            }
        }
        return true;
    }

    /**
     * create a st-graph from the graph for case 2
     * 
     */
    private void toStGraphFor2() {
        // setSS is set of sources and sinks of graph

        HashMap<Integer, Node> setSS = new HashMap<Integer, Node>();
        Iterator<Node> nodes = this.graph.getNodesIterator();
        while (nodes.hasNext()) {
            Node node = nodes.next();
            if (node.getAllInEdges().isEmpty()) {
                setSS.put(new Integer(node.getInteger("number")), node);
            } else if (node.getAllOutEdges().isEmpty()) {
                setSS.put(new Integer(node.getInteger("number")), node);
            }
        }

        // definite the source and sink in external face

        MyFace external = this.facesOfGraph.get(this.externalFace);
        List<Node> nodesList = external.getNodes();
        Node source = null;
        Node sink = null;

        // number of sources or sinks of the external face.
        int numOfSources = 0;
        int numOfSinks = 0;

        // index of source and sink
        int sourceIndex = 0;
        int sinkIndex = 0;

        // list of sources and sinks.
        LinkedList<Boolean> sourcesAndSinks = new LinkedList<Boolean>();

        for (int i = 0; i < nodesList.size(); i++) {
            Node node = nodesList.get(i);
            // sources
            if (node.getAllInEdges().isEmpty()) {
                if (numOfSources == 0) {
                    source = node;
                    sourceIndex = i;
                    setSS.remove(new Integer(node.getInteger("number")));
                }
                numOfSources++;
                sourcesAndSinks.add(new Boolean(true));
            }

            // sinks
            else if (node.getAllOutEdges().isEmpty()) {
                if (numOfSinks == 0) {
                    sink = node;
                    sinkIndex = i;
                    setSS.remove(new Integer(node.getInteger("number")));
                }
                numOfSinks++;
                sourcesAndSinks.add(new Boolean(false));
            }
        }

        boolean left = false;
        boolean right = false;
        Boolean first = sourcesAndSinks.get(0);
        for (int i = 1; i < sourcesAndSinks.size(); i++) {
            if (!first.equals(sourcesAndSinks.get(i))) {
                // start with source
                if ((i > 1) && first.booleanValue()) {
                    left = true;
                }
                if ((i < (sourcesAndSinks.size() - 1)) && first.booleanValue()) {
                    right = true;
                }

                // start with sink
                if ((i > 1) && !first.booleanValue()) {
                    right = true;
                }
                if ((i < (sourcesAndSinks.size() - 1)) && !first.booleanValue()) {
                    left = true;
                }
            }
        }

        // ----** new external face **----
        if (left || right) {
            // ---* there isn't a edge between the source and sink. *---
            if (this.graph.getEdges(source, sink).isEmpty()) {

                Node source1 = nodesList.get((sourceIndex + 1)
                        % nodesList.size());
                Node source2 = nodesList.get((sourceIndex - 1 + nodesList
                        .size())
                        % nodesList.size());
                Node sink2 = nodesList.get((sinkIndex + 1) % nodesList.size());
                Node sink1 = nodesList.get((sinkIndex - 1 + nodesList.size())
                        % nodesList.size());

                if (left) {
                    Edge newedge1 = this.graph.addEdge(source1, sink1, true);
                    this.addedEdges.add(newedge1);
                    // source1
                    LinkedList<Edge> edges = this.sequences[source1
                            .getInteger("number")].getEdges();
                    for (int j = 0; j < edges.size(); j++) {
                        Edge edge = edges.get(j);
                        if (edge.getSource().equals(source)) {
                            this.sequences[source1.getInteger("number")]
                                    .addEdge(newedge1, j);
                            break;
                        }
                    }
                    // sink1
                    edges = this.sequences[sink1.getInteger("number")]
                            .getEdges();
                    for (int j = 0; j < edges.size(); j++) {
                        Edge edge = edges.get(j);
                        if (edge.getTarget().equals(sink)) {
                            this.sequences[sink1.getInteger("number")].addEdge(
                                    newedge1, (j + 1) % edges.size());
                            break;
                        }
                    }

                    // --< new face >--
                    MyFace newface1 = new MyFace();
                    boolean face1 = false;

                    List<Edge> edgesList = external.getEdges();
                    for (int i = sourceIndex; i < (nodesList.size() + sourceIndex); i++) {
                        int index = i % nodesList.size();
                        // newface1
                        if (nodesList.get(index).equals(source1)) {
                            face1 = true;
                        }
                        if (nodesList.get(index).equals(sink1)) {
                            newface1.addNode(nodesList.get(index));
                            newface1.addEdge(newedge1);
                            face1 = false;
                        }
                        if (face1) {
                            newface1.addNode(nodesList.get(index));
                            newface1.addEdge(edgesList.get(index));
                        }
                    }
                    this.facesOfGraph.add(newface1);
                }

                if (right) {
                    Edge newedge2 = this.graph.addEdge(source2, sink2, true);
                    this.addedEdges.add(newedge2);
                    // source2
                    LinkedList<Edge> edges = this.sequences[source2
                            .getInteger("number")].getEdges();
                    for (int j = 0; j < edges.size(); j++) {
                        Edge edge = edges.get(j);
                        if (edge.getSource().equals(source)) {
                            this.sequences[source2.getInteger("number")]
                                    .addEdge(newedge2, (j + 1) % edges.size());
                            break;
                        }
                    }
                    // sink2
                    edges = this.sequences[sink2.getInteger("number")]
                            .getEdges();
                    for (int j = 0; j < edges.size(); j++) {
                        Edge edge = edges.get(j);
                        if (edge.getTarget().equals(sink)) {
                            this.sequences[sink2.getInteger("number")].addEdge(
                                    newedge2, j);
                            break;
                        }
                    }

                    // --< new face >--
                    MyFace newface2 = new MyFace();
                    boolean face2 = false;

                    List<Edge> edgesList = external.getEdges();
                    for (int i = sourceIndex; i < (nodesList.size() + sourceIndex); i++) {
                        int index = i % nodesList.size();
                        // newface2
                        if (nodesList.get(index).equals(sink2)) {
                            face2 = true;
                        }
                        if (nodesList.get(index).equals(source2)) {
                            newface2.addNode(nodesList.get(index));
                            newface2.addEdge(newedge2);
                            face2 = false;
                        }
                        if (face2) {
                            newface2.addNode(nodesList.get(index));
                            newface2.addEdge(edgesList.get(index));
                        }
                    }
                    this.facesOfGraph.add(newface2);
                }
            }

            // ---* there is a edge between the source and sink. *---
            else {
                for (int i = 0; i < nodesList.size(); i++) {
                    Node node = nodesList.get(i);
                    if (node.equals(source)) {
                        Node next = nodesList.get((i + 1) % nodesList.size());
                        Node vorher = nodesList.get((i - 1 + nodesList.size())
                                % nodesList.size());
                        Edge newedge = null;

                        // --< node >--
                        if (next.equals(sink)) {
                            newedge = this.graph.addEdge(vorher, next, true);
                        } else {
                            newedge = this.graph.addEdge(next, vorher, true);
                        }
                        this.addedEdges.add(newedge);

                        // next node
                        LinkedList<Edge> edges = this.sequences[next
                                .getInteger("number")].getEdges();
                        for (int j = 0; j < edges.size(); j++) {
                            Edge edge = edges.get(j);
                            if (edge.getSource().equals(source)) {
                                this.sequences[next.getInteger("number")]
                                        .addEdge(newedge, j);
                                break;
                            }
                        }

                        // vorher node
                        edges = this.sequences[vorher.getInteger("number")]
                                .getEdges();
                        for (int j = 0; j < edges.size(); j++) {
                            Edge edge = edges.get(j);
                            if (edge.getSource().equals(source)) {
                                this.sequences[vorher.getInteger("number")]
                                        .addEdge(newedge, (j + 1)
                                                % edges.size());
                                break;
                            }
                        }

                        // --< new face >---
                        MyFace newface = external;
                        // remove source
                        newface.removeNode(i);
                        // remove two edges
                        List<Edge> edgesNF = newface.getEdges();
                        int number = 0;
                        for (int k = 0; k < edgesNF.size(); k++) {
                            Edge edgeNF = edgesNF.get(k);
                            if (edgeNF.getSource().equals(source)) {
                                newface.removeEdge(k);
                                number++;
                                if (number == 1) {
                                    newface.addEdge(k, newedge);
                                }
                            }
                            if (number == 2) {
                                break;
                            }
                        }
                        this.facesOfGraph.add(newface);

                        break;
                    }
                }
            }
        }
    }

    // for case 2:-------------------------end-----2 -----------------------

    /**
     * reset
     */
    @Override
    public void reset() {
        this.graph = null;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm #execute()
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        UpwardDrawing up = new UpwardDrawing(this.sequences, this.addedEdges,
                this.graph, this.minDistance, this.facesOfGraph
                        .get(this.externalFace));
        up.init();
        up.drawing();
        graph.getListenerManager().transactionFinished(this);
    }

}
