/**
 * the algorithm saturate face
 * 
 * @author jin
 */
package org.graffiti.plugins.algorithms.upward;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;

public class SaturateFace {
    /**
     * the graph
     */
    private Graph graph;

    /**
     * list of new faces from the algorithm
     */
    private LinkedList<MyFace> facesOfGraph;

    /**
     * the added edges of graph
     */
    private LinkedList<Edge> addedEdges;

    /**
     * list of super nodes
     */
    private SuperNode[] sequences;

    /**
     * constructor with parameters
     * 
     * @param graph
     *            the graph
     * @param addedEdges
     *            the added edges of graph
     * @param sequences
     *            list of super nodes
     */
    public SaturateFace(Graph graph, LinkedList<Edge> addedEdges,
            SuperNode[] sequences) {
        this.graph = graph;
        this.addedEdges = addedEdges;
        this.facesOfGraph = new LinkedList<MyFace>();
        this.sequences = sequences;
    }

    /**
     * get list of new faces from the algorithm
     * 
     * @return list of new faces from the algorithm
     */
    public LinkedList<MyFace> getNewFaces() {
        return this.facesOfGraph;
    }

    /**
     * compute the list of symbols (sigma of f)
     * 
     * @param face
     * @return list of sink switch or source switch in counter order
     */
    public LinkedList<Angle> calculateOfSigma(MyFace face) {
        LinkedList<Angle> sigmaOfFace = new LinkedList<Angle>();
        List<Edge> edges = face.getEdges();

        for (int i = 0; i < edges.size(); i++) {
            Edge edge1 = edges.get(i);
            Edge edge2 = edges.get((i + 1) % edges.size());
            Angle angle = new Angle(edge1, edge2);
            angle.execute();

            if (angle.getSourceSwitch() || angle.getSinkSwitch()) {
                sigmaOfFace.add(angle);
            }
        }
        return sigmaOfFace;
    }

    /**
     * split the face to more faces so that all faces have only one sink switch
     * and only one source switch.
     * 
     * @param face
     * @param sigmaOfFace
     *            list of sink switch or source switch
     */
    public void execute(MyFace face, LinkedList<Angle> sigmaOfFace) {
        if (sigmaOfFace.size() != 2) // step 1:
        {
            for (int i = 0; i < sigmaOfFace.size(); i++) {
                // step 2: L-symbol
                if (sigmaOfFace.get(i).getIsLargeAngle()) {
                    Angle angleL = sigmaOfFace.get(i);
                    Angle angleS1 = null;
                    Angle angleS2 = null;
                    int j = (i + 1) % sigmaOfFace.size();
                    int k = (i + 2) % sigmaOfFace.size();
                    angleS1 = sigmaOfFace.get(j);
                    angleS2 = sigmaOfFace.get(k);

                    // step 2: followed by two consecutive S-symbols
                    if (angleS1.getIsSmallAngle() && angleS2.getIsSmallAngle()) {
                        // step 3.1
                        if (angleL.getSourceSwitch() && angleS1.getSinkSwitch()
                                && angleS2.getSourceSwitch()) {
                            // face f split to two faces f' and f''
                            // i.e. add new edge
                            Edge edge = this.graph.addEdge(angleS2.getNode(),
                                    angleL.getNode(), true);

                            this.addedEdges.add(edge);

                            // as red edge
                            this.colorEdge(edge, 1);
                            // new Face f''
                            MyFace newFace = new MyFace();

                            newFace.addNode(angleL.getNode());

                            // step 4 with face f'
                            List<Node> nodesList = face.getNodes();
                            List<Edge> edgesList = face.getEdges();
                            int index = 0;
                            int found = 0;
                            boolean remove = false;
                            int nodeIndex = 0;
                            int edgeIndex = 0;
                            while (found != 2) {
                                if (nodesList.get(index).equals(
                                        angleL.getNode())) {
                                    remove = true;
                                    found++;
                                    nodeIndex = index + 1;
                                    edgeIndex = index + 1;

                                    // --sequence
                                    SuperNode circular = sequences[angleL
                                            .getNode().getInteger("number")];
                                    LinkedList<Edge> edgesOfNode = circular
                                            .getEdges();
                                    int indexOfNode = 0;
                                    int target = index - 1;
                                    if (index < 0) {
                                        target = edgesList.size() - 1;
                                    }
                                    while (true) {
                                        if (edgesList
                                                .get(
                                                        (target + edgesList
                                                                .size())
                                                                % edgesList
                                                                        .size())
                                                .equals(
                                                        edgesOfNode
                                                                .get(indexOfNode))) {
                                            circular.addEdge(edge,
                                                    (indexOfNode + 1)
                                                            % edgesOfNode
                                                                    .size());
                                            break;
                                        }
                                        indexOfNode++;
                                        indexOfNode = indexOfNode
                                                % edgesOfNode.size();
                                    }
                                    // --end sequence

                                    face.addEdge(index, edge);

                                    index++;
                                    index = index % nodesList.size();
                                    nodeIndex = nodeIndex % nodesList.size();
                                    edgeIndex = edgeIndex % edgesList.size();

                                    newFace.addEdge(face.getEdges().get(
                                            edgeIndex));
                                    face.removeEdge(edgeIndex);

                                    newFace.addNode(face.getNodes().get(
                                            nodeIndex));
                                    face.removeNode(nodeIndex);

                                    index = index % nodesList.size();
                                    nodeIndex = nodeIndex % nodesList.size();
                                    edgeIndex = edgeIndex % edgesList.size();

                                } else if (remove) {
                                    if (remove) {
                                        newFace.addEdge(face.getEdges().get(
                                                edgeIndex));
                                        face.removeEdge(edgeIndex);
                                    }
                                    if (nodesList.get(index).equals(
                                            angleS2.getNode())) {
                                        if (remove) {
                                            found++;
                                        }
                                        newFace.addNode(face.getNodes().get(
                                                nodeIndex));

                                        // sequence
                                        SuperNode circular = sequences[angleS2
                                                .getNode().getInteger("number")];
                                        LinkedList<Edge> edgesOfNode = circular
                                                .getEdges();
                                        int indexOfNode = 0;
                                        while (true) {
                                            if (edgesList
                                                    .get(
                                                            edgeIndex
                                                                    % edgesList
                                                                            .size())
                                                    .equals(
                                                            edgesOfNode
                                                                    .get(indexOfNode))) {
                                                circular
                                                        .addEdge(
                                                                edge,
                                                                indexOfNode
                                                                        % edgesOfNode
                                                                                .size());
                                                break;
                                            }
                                            indexOfNode++;
                                            indexOfNode = indexOfNode
                                                    % edgesOfNode.size();
                                        }
                                        remove = false;
                                    }
                                    if (remove) {
                                        newFace.addNode(face.getNodes().get(
                                                nodeIndex));

                                        face.removeNode(nodeIndex);
                                    }
                                    index = index % nodesList.size();
                                    nodeIndex = nodeIndex % nodesList.size();
                                    edgeIndex = edgeIndex % edgesList.size();
                                } else {
                                    index++;
                                    index = index % nodesList.size();
                                }
                            }
                            newFace.addEdge(edge);
                            // recursive
                            sigmaOfFace.remove(i);
                            i = i % sigmaOfFace.size();
                            sigmaOfFace.remove(i);
                            this.facesOfGraph.add(newFace);
                            this.execute(face, sigmaOfFace);

                            break;
                        }
                        // step 3.2
                        if (angleL.getSinkSwitch() && angleS1.getSourceSwitch()
                                && angleS2.getSinkSwitch()) {
                            // face f split to two faces f' and f''
                            // i.e. add new edge
                            Edge edge = this.graph.addEdge(angleL.getNode(),
                                    angleS2.getNode(), true);

                            this.addedEdges.add(edge);

                            // as red edge
                            this.colorEdge(edge, 0);

                            // new Face f''
                            MyFace newFace = new MyFace();

                            newFace.addNode(angleL.getNode());

                            // step 4 with face f'
                            List<Node> nodesList = face.getNodes();
                            List<Edge> edgesList = face.getEdges();
                            int index = 0;
                            int found = 0;
                            boolean remove = false;
                            int nodeIndex = 0;
                            int edgeIndex = 0;
                            while (found != 2) {
                                if (nodesList.get(index).equals(
                                        angleL.getNode())) {
                                    remove = true;
                                    found++;
                                    nodeIndex = index + 1;
                                    edgeIndex = index + 1;

                                    // sequence
                                    SuperNode circular = sequences[angleL
                                            .getNode().getInteger("number")];
                                    LinkedList<Edge> edgesOfNode = circular
                                            .getEdges();
                                    int target = index - 1;
                                    if (target < 0) {
                                        target = edgesList.size() - 1;
                                    }
                                    int indexOfNode = 0;
                                    while (true) {

                                        if (edgesList
                                                .get(target % edgesList.size())
                                                .equals(
                                                        edgesOfNode
                                                                .get(indexOfNode))) {
                                            circular.addEdge(edge,
                                                    (indexOfNode + 1)
                                                            % edgesOfNode
                                                                    .size());
                                            break;
                                        }
                                        indexOfNode++;
                                        indexOfNode = indexOfNode
                                                % edgesOfNode.size();
                                    }
                                    // end

                                    face.addEdge(index, edge);

                                    index++;
                                    index = index % nodesList.size();
                                    nodeIndex = nodeIndex % nodesList.size();
                                    edgeIndex = edgeIndex % edgesList.size();
                                    newFace.addEdge(face.getEdges().get(
                                            edgeIndex));
                                    face.removeEdge(edgeIndex);
                                    newFace.addNode(face.getNodes().get(
                                            nodeIndex));
                                    face.removeNode(nodeIndex);

                                    index = index % nodesList.size();
                                    nodeIndex = nodeIndex % nodesList.size();
                                    edgeIndex = edgeIndex % edgesList.size();

                                } else if (remove) {
                                    if (remove) {
                                        newFace.addEdge(face.getEdges().get(
                                                edgeIndex));
                                        face.removeEdge(edgeIndex);
                                    }
                                    if (nodesList.get(index).equals(
                                            angleS2.getNode())) {
                                        newFace.addNode(face.getNodes().get(
                                                nodeIndex));
                                        if (remove) {
                                            found++;
                                        }

                                        // sequence
                                        SuperNode circular = sequences[angleS2
                                                .getNode().getInteger("number")];
                                        LinkedList<Edge> edgesOfNode = circular
                                                .getEdges();
                                        int indexOfNode = 0;
                                        while (true) {
                                            if (edgesList
                                                    .get(
                                                            edgeIndex
                                                                    % edgesList
                                                                            .size())
                                                    .equals(
                                                            edgesOfNode
                                                                    .get(indexOfNode))) {
                                                circular
                                                        .addEdge(
                                                                edge,
                                                                indexOfNode
                                                                        % edgesOfNode
                                                                                .size());
                                                break;
                                            }
                                            indexOfNode++;
                                            indexOfNode = indexOfNode
                                                    % edgesOfNode.size();
                                        }
                                        // end

                                        remove = false;
                                    }
                                    if (remove) {
                                        newFace.addNode(face.getNodes().get(
                                                nodeIndex));
                                        face.removeNode(nodeIndex);
                                    }
                                    index = index % nodesList.size();
                                    nodeIndex = nodeIndex % nodesList.size();
                                    edgeIndex = edgeIndex % edgesList.size();
                                } else {
                                    index++;
                                    index = index % nodesList.size();
                                }
                            }
                            newFace.addEdge(edge);
                            // recursive
                            sigmaOfFace.remove(i);
                            i = i % sigmaOfFace.size();
                            sigmaOfFace.remove(i);
                            this.facesOfGraph.add(newFace);
                            this.execute(face, sigmaOfFace);

                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * set edge with color.
     * 
     * @param e
     *            the edge
     * @param color
     *            number of color
     */
    private void colorEdge(Edge e, int color) {
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) e
                .getAttribute("graphics");
        ColorAttribute frame = ega.getFramecolor();
        ColorAttribute fill = ega.getFillcolor();
        if (color == 0) {
            frame.setColor(Color.GREEN);
            fill.setColor(Color.GREEN);
        } else if (color == 1) {
            frame.setColor(Color.RED);
            fill.setColor(Color.RED);
        } else {
            frame.setColor(Color.BLUE);
            fill.setColor(Color.BLUE);
        }

    }
}
