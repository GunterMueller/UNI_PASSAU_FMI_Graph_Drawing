/**
 * the algorithm assign upward
 * 
 * @author jin
 */
package org.graffiti.plugins.algorithms.upward;

import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;

public class AssignUpward {
    /**
     * the graph
     */
    private Graph graph;

    /**
     * the added edges that later removed
     */
    private LinkedList<Edge> addedEdges;

    /**
     * List of super nodes
     */
    private SuperNode[] sequences;

    /**
     * List of faces of graph
     */
    private LinkedList<MyFace> facesOfGraph;

    /**
     * index of list of faces of graph.
     */
    private int externalFace;

    /**
     * constructor with parameters
     * 
     * @param graph
     *            the graph
     * @param addedEdges
     *            added edges
     * @param sequences
     *            list of the super nodes
     */
    public AssignUpward(Graph graph, LinkedList<Edge> addedEdges,
            SuperNode[] sequences, LinkedList<MyFace> facesOfGraph,
            int externalFace) {
        this.graph = graph;
        this.addedEdges = addedEdges;
        this.sequences = sequences;
        this.facesOfGraph = facesOfGraph;
        this.externalFace = externalFace;
    }

    /**
     * execute the algorithm.
     */
    public void execute() {
        // internal face:
        SaturateFace saturateFace = new SaturateFace(this.graph,
                this.addedEdges, this.sequences);

        for (int i = 0; i < this.facesOfGraph.size(); i++) {
            MyFace face = this.facesOfGraph.get(i);
            // only internal face
            if (externalFace != i) {
                LinkedList<Angle> angles = saturateFace.calculateOfSigma(face);
                saturateFace.execute(face, angles);
            }
        }
        Iterator<MyFace> facesIt = saturateFace.getNewFaces().iterator();
        while (facesIt.hasNext()) {
            this.facesOfGraph.add(facesIt.next());
        }

        // external face:
        if (this.facesOfGraph.size() != 0) {
            this.canceledSourcesAndSinksInEx(this.facesOfGraph
                    .get(this.externalFace));
        }
    }

    /**
     * cancel the source and sinks of external face except for s and t
     * 
     * @param face
     *            the external face
     */
    private void canceledSourcesAndSinksInEx(MyFace face) {
        List<Edge> edgesList = face.getEdges();
        List<Node> nodesList = face.getNodes();

        List<Node> sources = new LinkedList<Node>();
        List<Node> sinks = new LinkedList<Node>();

        int indexOfEdges = 0;
        boolean start = false;
        boolean startSource = false;
        boolean startSink = false;

        while (true) {
            Edge edge1 = edgesList.get(indexOfEdges % edgesList.size());
            int next = (indexOfEdges + 1) % edgesList.size();
            Edge edge2 = edgesList.get(next);
            Angle angle = new Angle(edge1, edge2);
            angle.execute();

            if ((angle.getIsLargeAngle() && angle.getSourceSwitch())
                    || ((edge1.equals(edge2)) && (edge1.getSource()
                            .getNeighbors().size() == 1))) {
                startSource = true;
                if (startSink && (sources.size() > 0) && (sinks.size() > 0)) {
                    break;
                }
                if (start) {
                    startSink = false;
                    sources.add(nodesList.get(next));
                }

            } else if ((angle.getIsLargeAngle() && angle.getSinkSwitch())
                    || ((edge1.equals(edge2)) && (edge1.getTarget()
                            .getNeighbors().size() == 1))) {
                startSink = true;
                if (startSource && (sources.size() > 0) && (sinks.size() > 0)) {
                    break;
                }
                if (start) {
                    startSource = false;
                    sinks.add(nodesList.get(next));
                }
            }
            if (startSource && startSink) {
                start = true;
                indexOfEdges--;
            }
            indexOfEdges++;
        }

        int result = 0;
        int index = 0;
        while (true) {
            index = index % edgesList.size();
            result = (index + 1) % edgesList.size();
            if (sources.get(0).equals(edgesList.get(index).getSource())
                    && sources.get(0).equals(edgesList.get(result).getSource())) {
                break;
            }
            index++;
        }

        if (sources.size() > 1) {
            for (int i = 1; i < sources.size(); i++) {
                Edge edge = this.graph.addEdge(sources.get(0), sources.get(i),
                        true);
                this.addedEdges.add(edge);
                this.colorEdge(edge, i);

                // new face
                MyFace newFace = new MyFace();
                newFace.addNode(sources.get(0));

                index = result;

                while (true) {
                    index = index % edgesList.size();
                    int next = (index + 1) % edgesList.size();

                    newFace.addNode(nodesList.get(next));
                    newFace.addEdge(edgesList.get(index));

                    if (nodesList.get(next).equals(sources.get(i))) {
                        break;
                    }
                    index++;
                }

                // sequence
                SuperNode circular = sequences[sources.get(0).getInteger(
                        "number")];
                LinkedList<Edge> edgesOfNode = circular.getEdges();
                int indexOfNode = 0;
                int target = result - 1;
                if (target < 0) {
                    target = edgesList.size() - 1;
                }
                while (true) {
                    if (edgesList.get(target % edgesList.size()).equals(
                            edgesOfNode.get(indexOfNode))) {
                        circular.addEdge(edge, (indexOfNode + 1)
                                % edgesOfNode.size());
                        break;
                    }
                    indexOfNode++;
                    indexOfNode = indexOfNode % edgesOfNode.size();
                }

                newFace.addEdge(edge);
                this.facesOfGraph.add(newFace);

                // change external face
                index = result;
                int next = (index + 1);
                while (true) {
                    index = index % edgesList.size();
                    next = next % edgesList.size();
                    edgesList.remove(index);

                    if (nodesList.get(next).equals(sources.get(i))) {
                        break;
                    }
                    nodesList.remove(next);
                }

                // sequence
                SuperNode circular2 = sequences[sources.get(i).getInteger(
                        "number")];
                LinkedList<Edge> edgesOfNode2 = circular2.getEdges();
                indexOfNode = 0;
                target = 0;
                if (result < next) {
                    target = result;
                }
                while (true) {
                    if (edgesList.get(target % edgesList.size()).equals(
                            edgesOfNode2.get(indexOfNode))) {
                        circular2.addEdge(edge, indexOfNode
                                % edgesOfNode2.size());
                        break;
                    }
                    indexOfNode++;
                    indexOfNode = indexOfNode % edgesOfNode2.size();
                }

                if (result > next) {
                    edgesList.add(edge);
                } else {
                    edgesList.add((result % edgesList.size()), edge);
                }
            }
        }

        int resultS = 0;
        int indexS = 0;
        while (true) {
            indexS = indexS % edgesList.size();
            resultS = (indexS + 1) % edgesList.size();
            if (sinks.get(0).equals(edgesList.get(indexS).getTarget())
                    && sinks.get(0).equals(edgesList.get(resultS).getTarget())) {
                break;
            }
            indexS++;
        }
        if (sinks.size() > 1) {
            for (int i = 1; i < sinks.size(); i++) {
                Edge edge = this.graph
                        .addEdge(sinks.get(i), sinks.get(0), true);
                this.addedEdges.add(edge);
                this.colorEdge(edge, i);

                resultS = resultS % edgesList.size();

                // new face

                MyFace newFace = new MyFace();

                indexS = resultS;

                while (true) {
                    indexS = indexS % edgesList.size();

                    newFace.addNode(nodesList.get(indexS));

                    if (nodesList.get(indexS).equals(sinks.get(i))) {
                        // sequence
                        SuperNode circular2 = sequences[sinks.get(i)
                                .getInteger("number")];
                        LinkedList<Edge> edgesOfNode2 = circular2.getEdges();
                        int indexOfNode = 0;
                        int target = indexS - 1;
                        if (target < 0) {
                            target = edgesList.size() - 1;
                        }
                        while (true) {
                            if (edgesList.get(target).equals(
                                    edgesOfNode2.get(indexOfNode))) {
                                circular2.addEdge(edge, (indexOfNode + 1)
                                        % edgesOfNode2.size());
                                break;
                            }
                            indexOfNode++;
                            indexOfNode = indexOfNode % edgesOfNode2.size();
                        }
                        // end
                        break;
                    }
                    newFace.addEdge(edgesList.get(indexS));

                    indexS++;
                }

                // sequence
                SuperNode circular = sequences[sinks.get(0)
                        .getInteger("number")];
                LinkedList<Edge> edgesOfNode = circular.getEdges();
                int indexOfNode = 0;
                int target = resultS - 1;
                if (target < 0) {
                    target = edgesList.size() - 1;
                }
                while (true) {
                    if (edgesList.get(target).equals(
                            edgesOfNode.get(indexOfNode))) {
                        circular.addEdge(edge, (indexOfNode + 1)
                                % edgesOfNode.size());
                        break;
                    }
                    indexOfNode++;
                    indexOfNode = indexOfNode % edgesOfNode.size();
                }
                // end

                newFace.addEdge(edge);

                // change external face
                indexS = resultS;
                int next = indexS + 1;
                while (true) {
                    indexS = indexS % edgesList.size();
                    next = next % edgesList.size();
                    edgesList.remove(indexS);

                    if (nodesList.get(next).equals(sinks.get(i))) {
                        break;
                    }

                    nodesList.remove(next);
                }

                edgesList.add((resultS % edgesList.size()), edge);
                this.facesOfGraph.add(newFace);
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
        } else if (color == 2) {
            frame.setColor(Color.BLUE);
            fill.setColor(Color.BLUE);
        } else {
            frame.setColor(Color.ORANGE);
            fill.setColor(Color.ORANGE);
        }
    }

}
