package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Logger;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Class implements an algorithm by Brehm, which calculates all realizers in a
 * given graph by starting with the leftmost realizer (that is the realizer
 * calculated from the leftmost canonical order) and flipping three-colored cw
 * faces and three-colored separating triangles.
 * 
 * @author hofmeier
 */
public class BrehmAllRealizers extends BrehmOneRealizer {

    /** The separating triangles of the graph */
    protected HashList<SeparatingTriangle> separatingTriangles = new HashList<SeparatingTriangle>();

    /** The logger to inform and warn the user */
    private static final Logger logger = Logger
            .getLogger(BrehmAllRealizers.class.getName());

    /**
     * Creates a new instance of the class.
     * 
     * @param g
     *            the graph, the algorithm is adapted on.
     * @param m
     *            the maximum number of realizers (not used in here).
     */
    public BrehmAllRealizers(Graph g, int m) {
        super(g, m);
    }

    /**
     * Executes the algorithm by calling the execute method from the super
     * class, which calculates the leftmost realizer. After that all other
     * realizers are calculated by flipping three-colored cw faces and
     * three-colored cw separating triangles.
     */
    @Override
    public void execute() {
        // Calculates the leftmost realizer
        super.execute();

        // Calculates all realizers
        this.calculateAllRealizers();

        // Writes the realizers data to the database
        if (SchnyderRealizerAdministration.writeToFile) {
            DataToDB dtdb = new DataToDB(this);
            dtdb.writeToDB();
        }
    }

    /**
     * Calculates all realizers by taking a given one, looking for all flippable
     * faces and separating triangles and for each one create a new realizer
     * with the face / separating triangles flipped.
     */
    private void calculateAllRealizers() {

        // As these are not saved anywhere yet, first get all separating
        // triangles of
        // the graph
        calculateSeparatingTriangles();

        Stack<Realizer> realizerStack = new Stack<Realizer>();

        // Push the leftmost realizer onto the stack
        realizerStack.push(this.realizers.getFirst());

        // As long as there are realizers on the stack...
        while (!realizerStack.isEmpty()) {
            // ...get one ...
            Realizer current = realizerStack.pop();
            Iterator<Face> it = current.getFlippableFaces().iterator();
            // ... and for each of its flippable faces create a new realizer.
            while (it.hasNext()) {
                Face flippable = it.next();
                Realizer newRealizer = current.clone();
                newRealizer.flip(flippable);

                // If this realizer is not existing yet...
                if (!alreadyExists(newRealizer)) {
                    realizerStack.push(newRealizer);
                    // ...save it.
                    this.realizers.add(newRealizer);
                    this.barycentricReps.add(new BarycentricRepresentation(
                            newRealizer, this.graph, this.outerNodes));
                    if (this.realizers.size() + 1 > this.maxNumberOfRealizers) {
                        logger.finest(AbstractDrawingAlgorithm.MAX_MESSAGE);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Walks through the list of already found realizers and checks if a given
     * realizer has already been calculated before. This is necessary because a
     * realizer can be calculated by different sequences of flip operations.
     * 
     * @param r
     *            the given realizer.
     * @return true if the realizer has already been calculated.
     */
    public boolean alreadyExists(Realizer r) {
        Iterator<Realizer> it = this.realizers.iterator();
        while (it.hasNext()) {
            if (r.compareTo(it.next()) == 0)
                return true;
        }
        return false;
    }

    /**
     * Calculates the separating triangles of the graph. By checking for each
     * node, if there are two of its neighbors, which are not next to each other
     * in its adjacence list, but are connected by an edge.
     */
    public void calculateSeparatingTriangles() {

        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        // to avoid finding a separating triangle more than once
        HashList<Node> finishedNodes = new HashList<Node>();

        // Check for each node...
        while (nodesIt.hasNext()) {
            Node node = nodesIt.next();
            finishedNodes.append(node);
            HashList<Node> al = this.adjacenceLists.get(node);
            Node it = al.getFirst();
            // (to avoid finding a separating triangle more than once)
            HashList<Node> tempFinishedNodes = new HashList<Node>();

            // ... and for each neighbor of the node ...
            for (int i = 0; i < al.size(); i++) {
                if (!finishedNodes.contains(it)) {
                    tempFinishedNodes.append(it);

                    Node it2 = al.getFirst();
                    // ... if there is a neighbor connected to it by an edge but
                    // is not next to it in the adjacence list of the node
                    for (int j = 0; j < al.size(); j++) {
                        if (!finishedNodes.contains(it2)
                                && (!tempFinishedNodes.contains(it2))) {
                            if ((it != it2) && (al.getNextNeighbor(it) != it2)
                                    && (al.getPredecessor(it) != it2)) {
                                if (this.graph.getEdges(it, it2).size() > 0) {
                                    Edge e1 = graph.getEdges(node, it)
                                            .iterator().next();
                                    Edge e2 = graph.getEdges(it, it2)
                                            .iterator().next();
                                    Edge e3 = graph.getEdges(it2, node)
                                            .iterator().next();
                                    SeparatingTriangle triangle = new SeparatingTriangle(
                                            node, it, it2, e1, e2, e3, this);
                                    this.separatingTriangles.append(triangle);
                                    this.faces.append(triangle);
                                }
                            }
                        }
                        it2 = al.getNextNeighbor(it2);
                    }
                }
                it = al.getNextNeighbor(it);
            }
        }
    }

    /**
     * Gets the separating triangles of the graph.
     * 
     * @return the separating triangles of the graph
     */
    public HashList<SeparatingTriangle> getSeparatingTriangles() {
        return this.separatingTriangles;
    }
}
