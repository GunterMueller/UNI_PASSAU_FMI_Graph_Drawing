package org.graffiti.plugins.algorithms.sugiyama.crossmin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.sugiyama.util.LazyIntArrayList;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements various flavours of the sifting heuristic for crossing
 * minimization. This implementation has been heavily optimized for speed.
 * 
 * When sifting a node on its layer, the classic approach of sifting computes
 * the difference in the number of edge intersections by taking the in-neighbors
 * (top-down) or out-neighbors (bottom-up) of the current node into account.
 * This limited view can increase the number of edge intersections on the other
 * layer without any control.
 * 
 * The "optimized" version of this algorithm (select "optimized" as the option
 * for "node placement") computes the difference in the number of edge
 * intersections by taking both in- and out-neighbors into account. That means
 * that the optimized version will never increase the number of edge
 * intersections. The optimized version might need more sweeps to produce a
 * better result.
 * 
 * Another flavour of sifting is called k-layer Sifting. The original approach
 * sorts the nodes of the graph descending by their degree and sifts the nodes
 * in that order. After the nodes have been sifted, all nodes are being sifted
 * in the reversed order. The original approach of the algorithm uses "classic"
 * node placement as described above. We can combine k-layer sifting and
 * "optimized" node placement to increase the efficiency of the k-layer sifting
 * algorithm.
 * 
 * @author Ferdinand H&uuml;bner
 */
public class Sifting extends AbstractAlgorithm implements CrossMinAlgorithm {
    /** SugiyamaData bean */
    private SugiyamaData data;

    /** The layers in of the graph */
    private ArrayList<SiftingList> layers;

    /** Maps a node to a SiftingObject */
    private Hashtable<Node, SiftingObject> nodeToObject;

    /** Maps a SiftingObject to a node */
    private Hashtable<SiftingObject, Node> objectToNode;

    /** Number of sifting sweeps */
    private int siftingSweeps = 5;

    /** Number of sifting rounds */
    private int siftingRounds = 1;

    /** Initialization method: random or barycenter */
    private String initMethod = "Random";

    /** Use k-layer sifting or not */
    private boolean klevel;

    private boolean optimized;

    /** Is it a top-down iteration or not */
    boolean topDown;

    /** Used to determine if this is a cyclic drawing or not */
    private boolean cyclicLayout;

    // caches
    private LazyIntArrayList positionInA;
    private LazyIntArrayList positionInB;
    private LazySiftingObjectArrayList commonNeighbors;
    private LazyIntArrayList neighborsA;
    private LazyIntArrayList neighborsB;

    public void execute() {
        graph.getListenerManager().transactionStarted(this);

        if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)) {
            cyclicLayout = true;
        } else {
            cyclicLayout = false;
        }

        initialize();

        if (klevel) {
            executeKLevel();
        } else {
            executeClassic();
        }

        cleanup();

        graph.getListenerManager().transactionFinished(this);
    }

    public void executeKLevel() {
        topDown = true;
        ArrayList<SiftingObject> nodes = new ArrayList<SiftingObject>();
        for (int i = 0; i < layers.size(); i++) {
            SiftingList list = layers.get(i);
            SiftingObject current = list.head;
            while (current != null) {
                nodes.add(current);
                current = current.next;
            }
        }
        Collections.sort(nodes, new NodeDegreeComparator());
        int delta;
        boolean reversed = false;
        SiftingObject current;

        for (int i = 0; i < siftingSweeps; i++) {
            delta = 0;
            if (reversed) {
                for (int j = nodes.size() - 1; j >= 0; j--) {
                    current = nodes.get(j);
                    delta += siftingStep(current, current.level);
                }

                if (delta == 0) {
                    for (int j = 0; j < nodes.size(); j++) {
                        current = nodes.get(j);
                        siftingStep(current, current.level);
                    }
                } else {
                    for (int j = nodes.size() - 1; j >= 0; j--) {
                        current = nodes.get(j);
                        siftingStep(current, current.level);
                    }
                }
            } else {
                for (int j = 0; j < nodes.size(); j++) {
                    current = nodes.get(j);
                    delta += siftingStep(current, current.level);
                }
                if (delta == 0) {
                    for (int j = nodes.size() - 1; j >= 0; j--) {
                        current = nodes.get(j);
                        siftingStep(current, current.level);
                    }
                } else {
                    for (int j = 0; j < nodes.size(); j++) {
                        current = nodes.get(j);
                        siftingStep(current, current.level);
                    }
                }
                reversed = !reversed;
            }
        }
    }

    public void executeClassic() {
        int numberOfLayers = layers.size();
        int layerSize;

        for (int sweep = 0; sweep < siftingSweeps; sweep++) {
            if (cyclicLayout) {
                // if the graph is cyclic, we iterate top-down, starting from a
                // random
                // level of the graph; afterwards we iterate bottom-up, starting
                // from another random layer of the graph

                // top-down
                int startingLayer = (int) (Math
                        .floor((Math.random() * (numberOfLayers - 1)) + 0.5d));
                for (int i = startingLayer; i < numberOfLayers; i++) {
                    for (int round = 0; round < siftingRounds; round++) {
                        LazySiftingObjectArrayList clonedLayer = layers.get(i)
                                .getArrayCopy();
                        layerSize = clonedLayer.elementCount;
                        for (int j = 0; j < layerSize; j++) {
                            siftingStep(clonedLayer.get(j), i);
                        }
                    }
                }
                if (startingLayer != 0) {
                    for (int i = 0; i < startingLayer; i++) {
                        for (int round = 0; round < siftingRounds; round++) {
                            LazySiftingObjectArrayList clonedLayer = layers
                                    .get(i).getArrayCopy();
                            layerSize = clonedLayer.elementCount;
                            for (int j = 0; j < layerSize; j++) {
                                siftingStep(clonedLayer.get(j), i);
                            }
                        }
                    }
                }
                // bottom-up

                startingLayer = (int) (Math
                        .floor((Math.random() * (numberOfLayers - 1)) + 0.5d));

                for (int i = startingLayer; i >= 0; i--) {
                    for (int round = 0; round < siftingRounds; round++) {
                        LazySiftingObjectArrayList clonedLayer = layers.get(i)
                                .getArrayCopy();
                        layerSize = clonedLayer.elementCount;
                        for (int j = 0; j < layerSize; j++) {
                            siftingStep(clonedLayer.get(j), i);
                        }
                    }
                }
                if (startingLayer != numberOfLayers - 1) {
                    for (int i = numberOfLayers - 1; i > startingLayer; i--) {
                        for (int round = 0; round < siftingRounds; round++) {
                            LazySiftingObjectArrayList clonedLayer = layers
                                    .get(i).getArrayCopy();
                            layerSize = clonedLayer.elementCount;
                            for (int j = 0; j < layerSize; j++) {
                                siftingStep(clonedLayer.get(j), i);
                            }
                        }
                    }
                }
            } else {
                // top down
                topDown = true;
                for (int i = 1; i < numberOfLayers; i++) {
                    for (int round = 0; round < siftingRounds; round++) {
                        LazySiftingObjectArrayList clonedLayer = layers.get(i)
                                .getArrayCopy();
                        layerSize = clonedLayer.elementCount;
                        for (int j = 0; j < layerSize; j++) {
                            siftingStep(clonedLayer.get(j), i);
                        }
                    }
                }
                // bottom up
                topDown = false;
                for (int i = numberOfLayers - 1; i >= 0; i--) {
                    for (int round = 0; round < siftingRounds; round++) {

                        LazySiftingObjectArrayList clonedLayer = layers.get(i)
                                .getArrayCopy();
                        layerSize = clonedLayer.elementCount;
                        for (int j = 0; j < layerSize; j++) {
                            siftingStep(clonedLayer.get(j), i);
                        }
                    }
                }
            }
        }

        cleanup();
    }

    /**
     * This method writes the actual xpos-values that have been computed to the
     * sugiyama.xpos attribute of the node
     */
    private void cleanup() {
        for (int i = 0; i < layers.size(); i++) {
            data.getLayers().getLayer(i).clear();
            SiftingObject object = layers.get(i).head;
            while (object != null) {
                data.getLayers().getLayer(i).add(object.node);
                object.node.setDouble(SugiyamaConstants.PATH_XPOS, object.xPos);
                object = object.next;
            }
        }
    }

    /**
     * Executes a single sifting step: - move the node to the rightmost position
     * - iteratively swap it with its neighbor and count the difference in edge
     * intersections - move the node to its best position
     */
    private int siftingStep(SiftingObject object, int layerIndex) {
        SiftingList layer = layers.get(layerIndex);
        int originalPosition = object.xPos;
        int delta;
        int bestDelta;
        int bestPosition;
        int limit;
        int nextIndex;

        // move the object to position 0
        // this can be done in O(1) using the special list-implementation
        // SiftingList
        if (originalPosition != 0) {
            layer.moveToHead(object);
            object.xPos = 0;
        }
        // update the xpos of all objects before the original position
        int index = 1;
        SiftingObject current = object.next;
        while (index <= originalPosition) {
            current.xPos = index;
            current = current.next;
            ++index;
        }

        initializeStepLazy(object);

        // sift the object to the right and compute difference in the number
        // of edge intersections
        bestDelta = 0;
        delta = 0;
        bestPosition = 0;
        layer.bestPosition = null;
        limit = layer.length - 1;
        for (int i = 0; i < limit; i++) {
            nextIndex = i + 1;
            SiftingObject otherObject = object.next;

            if (klevel) {
                delta += getDeltaOptimized(object, otherObject);
            } else {
                if (optimized) {
                    delta += getDeltaOptimized(object, otherObject);
                } else {
                    delta += getDelta(object, otherObject);
                }
            }

            if (delta < bestDelta) {
                bestDelta = delta;
                bestPosition = nextIndex;
                layer.bestPosition = otherObject;
            }
            layer.swap(object, otherObject);
            object.xPos = nextIndex;
            otherObject.xPos = i;

            updateObjects(object, otherObject, topDown);
        }

        // O(1)
        layer.moveToBestPosition(object);

        // update the xpos of all other objects
        index = bestPosition;
        current = object;
        while (current != null) {
            current.xPos = index;
            current = current.next;
            ++index;
        }
        finalizeStepLazy(object);
        return bestDelta;

    }

    private void initializeStepLazy(SiftingObject object) {

        int originalPosition, tmp;
        SiftingObject neighbor, otherNeighbor;

        for (int i = 0; i < object.inNeighbors.elementCount; i++) {
            neighbor = object.inNeighbors.get(i);
            originalPosition = object.inNeighborPositions[i];

            // move all objects that are left of o one position to the right
            for (int j = originalPosition; j > 0; j--) {
                otherNeighbor = neighbor.outNeighbors.get(j - 1);
                tmp = neighbor.outNeighborPositions[j - 1];
                neighbor.outNeighbors.set(j, otherNeighbor);
                neighbor.outNeighborPositions[j] = tmp;
                otherNeighbor.inNeighborPositions[tmp] = j;
            }
            neighbor.outNeighbors.set(0, object);
            neighbor.outNeighborPositions[0] = i;
            object.inNeighborPositions[i] = 0;
        }

        for (int i = 0; i < object.outNeighbors.elementCount; i++) {
            neighbor = object.outNeighbors.get(i);
            originalPosition = object.outNeighborPositions[i];

            // move all objects that are left of o one position to the right
            for (int j = originalPosition; j > 0; j--) {
                otherNeighbor = neighbor.inNeighbors.get(j - 1);
                tmp = neighbor.inNeighborPositions[j - 1];
                neighbor.inNeighbors.set(j, otherNeighbor);
                neighbor.inNeighborPositions[j] = tmp;
                otherNeighbor.outNeighborPositions[tmp] = j;
            }
            neighbor.inNeighbors.set(0, object);
            neighbor.inNeighborPositions[0] = i;
            object.outNeighborPositions[i] = 0;
        }

    }

    private void finalizeStepLazy(SiftingObject object) {

        SiftingObject neighbor, otherNeighbor;
        int originalPosition, newPosition, tmp;

        for (int i = 0; i < object.inNeighbors.elementCount; i++) {
            neighbor = object.inNeighbors.get(i);
            originalPosition = object.inNeighborPositions[i];

            // move o in the adjacency list of its neighbor to the left until
            // it is at the right position
            newPosition = 0;
            for (int j = originalPosition; j > 0; j--) {
                otherNeighbor = neighbor.outNeighbors.get(j - 1);

                // this object has to stay before o
                if (otherNeighbor.xPos < object.xPos) {
                    newPosition = j;
                    break;
                } else {
                    // move the object to the right
                    neighbor.outNeighbors.set(j, otherNeighbor);
                    tmp = neighbor.outNeighborPositions[j - 1];
                    neighbor.outNeighborPositions[j] = tmp;
                    // and update the index in the adjacency list of the object
                    otherNeighbor.inNeighborPositions[tmp] = j;
                }
            }
            // move o to the right position
            neighbor.outNeighbors.set(newPosition, object);
            neighbor.outNeighborPositions[newPosition] = i;
            object.inNeighborPositions[i] = newPosition;
        }
        for (int i = 0; i < object.outNeighbors.elementCount; i++) {
            neighbor = object.outNeighbors.get(i);
            originalPosition = object.outNeighborPositions[i];

            // move o in the adjacency list of its neighbor to the left until
            // it is at the right position
            newPosition = 0;
            for (int j = originalPosition; j > 0; j--) {
                otherNeighbor = neighbor.inNeighbors.get(j - 1);

                // this object has to stay before o
                if (otherNeighbor.xPos < object.xPos) {
                    newPosition = j;
                    break;
                } else {
                    // move the object to the right
                    neighbor.inNeighbors.set(j, otherNeighbor);
                    tmp = neighbor.inNeighborPositions[j - 1];
                    neighbor.inNeighborPositions[j] = tmp;
                    // and update the index in the adjacency list of the object
                    otherNeighbor.outNeighborPositions[tmp] = j;
                }
            }
            // move o to the right position
            neighbor.inNeighbors.set(newPosition, object);
            neighbor.inNeighborPositions[newPosition] = i;
            object.outNeighborPositions[i] = newPosition;
        }
    }

    /**
     * Computes the difference in the number of edge intersections when swapping
     * a and b
     */
    private int getDeltaOptimized(SiftingObject a, SiftingObject b) {
        int delta = 0;

        // in-neighbors
        getNeighborsA(a, true);
        getNeighborsB(b, true);

        int r = neighborsA.elementCount;
        int s = neighborsB.elementCount;
        int c = 0;
        int i = 0;
        int j = 0;
        while (i < r && j < s) {
            if (neighborsA.get(i) < neighborsB.get(j)) {
                c += (s - j);
                ++i;
            } else if (neighborsB.get(j) < neighborsA.get(i)) {
                c -= (r - i);
                ++j;
            } else {
                c += (s - j) - (r - i);
                ++i;
                ++j;
            }
        }
        delta += c;

        // out-neighbors
        getNeighborsA(a, false);
        getNeighborsB(b, false);

        r = neighborsA.elementCount;
        s = neighborsB.elementCount;
        c = 0;
        i = 0;
        j = 0;
        while (i < r && j < s) {
            if (neighborsA.get(i) < neighborsB.get(j)) {
                c += (s - j);
                ++i;
            } else if (neighborsB.get(j) < neighborsA.get(i)) {
                c -= (r - i);
                ++j;
            } else {
                c += (s - j) - (r - i);
                ++i;
                ++j;
            }
        }
        delta += c;

        return delta;
    }

    /**
     * Computes the difference in the number of edge intersections when swapping
     * a and b
     */
    private int getDelta(SiftingObject a, SiftingObject b) {

        getNeighborsA(a, topDown);
        getNeighborsB(b, topDown);

        int r = neighborsA.elementCount;
        int s = neighborsB.elementCount;
        int c = 0;
        int i = 0;
        int j = 0;
        while (i < r && j < s) {
            if (neighborsA.get(i) < neighborsB.get(j)) {
                c += (s - j);
                ++i;
            } else if (neighborsB.get(j) < neighborsA.get(i)) {
                c -= (r - i);
                ++j;
            } else {
                c += (s - j) - (r - i);
                ++i;
                ++j;
            }
        }

        return c;
    }

    /**
     * Returns an ordered int[] with the xpos-values of the neighbors of the
     * node
     */
    private LazyIntArrayList getNeighborsA(SiftingObject object, boolean topDown) {
        if (topDown) {
            neighborsA.clear();
            for (int i = 0; i < object.inNeighbors.elementCount; i++) {
                neighborsA.add(object.inNeighbors.get(i).xPos);
            }
        } else {
            neighborsA.clear();
            for (int i = 0; i < object.outNeighbors.elementCount; i++) {
                neighborsA.add(object.outNeighbors.get(i).xPos);
            }
        }
        return neighborsA;
    }

    /**
     * Returns an ordered int[] with the xpos-values of the neighbors of the
     * node
     */
    private LazyIntArrayList getNeighborsB(SiftingObject object, boolean topDown) {
        if (topDown) {
            neighborsB.clear();
            for (int i = 0; i < object.inNeighbors.elementCount; i++) {
                neighborsB.add(object.inNeighbors.get(i).xPos);
            }
        } else {
            neighborsB.clear();
            for (int i = 0; i < object.outNeighbors.elementCount; i++) {
                neighborsB.add(object.outNeighbors.get(i).xPos);
            }
        }
        return neighborsB;
    }

    private void updateObjects(SiftingObject a, SiftingObject b, boolean topDown) {
        // those lists are being used to determine the position of their common
        // neighbor in the adjacency list of a or b in constant time
        positionInA.clear();
        positionInB.clear();

        commonNeighbors = getCommonInNeighbors(a, b, positionInA, positionInB);

        SiftingObject neighbor;
        int commonNeighborsSize = commonNeighbors.elementCount;

        for (int i = 0; i < commonNeighborsSize; i++) {
            neighbor = commonNeighbors.get(i);
            // get the index of a and b in the adjacency list of their common
            // neighbor
            int idxA = a.inNeighborPositions[positionInA.get(i)];
            int idxB = b.inNeighborPositions[positionInB.get(i)];

            // swap the neighbors
            neighbor.outNeighbors.set(idxA, b);
            neighbor.outNeighbors.set(idxB, a);

            // update indices on the neighbor
            int tmp;
            tmp = neighbor.outNeighborPositions[idxA];
            neighbor.outNeighborPositions[idxA] = neighbor.outNeighborPositions[idxB];
            neighbor.outNeighborPositions[idxB] = tmp;

            // update indices on a and b
            a.inNeighborPositions[positionInA.get(i)] = idxB;
            b.inNeighborPositions[positionInB.get(i)] = idxA;

        }

        positionInA.clear();
        positionInB.clear();
        commonNeighbors = getCommonOutNeighbors(a, b, positionInA, positionInB);
        commonNeighborsSize = commonNeighbors.elementCount;

        for (int i = 0; i < commonNeighborsSize; i++) {
            neighbor = commonNeighbors.get(i);
            int idxA = a.outNeighborPositions[positionInA.get(i)];
            int idxB = b.outNeighborPositions[positionInB.get(i)];

            // swap the neighbors
            // Collections.swap(neighbor.inNeighbors, idxA, idxB);
            neighbor.inNeighbors.set(idxA, b);
            neighbor.inNeighbors.set(idxB, a);

            // update indices on the neighbor
            int tmp;
            tmp = neighbor.inNeighborPositions[idxA];
            neighbor.inNeighborPositions[idxA] = neighbor.inNeighborPositions[idxB];
            neighbor.inNeighborPositions[idxB] = tmp;

            // update indices on a and b
            a.outNeighborPositions[positionInA.get(i)] = idxB;
            b.outNeighborPositions[positionInB.get(i)] = idxA;

        }
    }

    /**
     * Returns a list of common out-neighbors of the nodes a and b
     */
    private LazySiftingObjectArrayList getCommonOutNeighbors(SiftingObject a,
            SiftingObject b, LazyIntArrayList positionInA,
            LazyIntArrayList positionInB) {
        commonNeighbors.clear();

        SiftingObject neighbor;
        int ptrA = 0;
        int ptrB = 0;
        int xPosA, xPosB;
        int limitA = a.outNeighbors.elementCount;
        int limitB = b.outNeighbors.elementCount;
        while (ptrA < limitA && ptrB < limitB) {
            neighbor = a.outNeighbors.get(ptrA);
            xPosA = neighbor.xPos;
            xPosB = b.outNeighbors.get(ptrB).xPos;
            if (xPosA == xPosB) {
                commonNeighbors.add(neighbor);
                positionInA.add(ptrA);
                positionInB.add(ptrB);
                ++ptrA;
                ++ptrB;
            } else {
                if (xPosA < xPosB) {
                    ++ptrA;
                } else if (xPosA == xPosB) {
                    ++ptrA;
                    ++ptrB;
                } else {
                    ++ptrB;
                }
            }
        }
        return commonNeighbors;
    }

    /**
     * Returns a list of common in-neighbors of the nodes a and b
     */
    private LazySiftingObjectArrayList getCommonInNeighbors(SiftingObject a,
            SiftingObject b, LazyIntArrayList positionInA,
            LazyIntArrayList positionInB) {
        commonNeighbors.clear();
        SiftingObject neighbor;
        int ptrA = 0;
        int ptrB = 0;
        int xPosA, xPosB;
        int limitA = a.inNeighbors.elementCount;
        int limitB = b.inNeighbors.elementCount;

        while (ptrA < limitA && ptrB < limitB) {
            neighbor = a.inNeighbors.get(ptrA);
            xPosA = neighbor.xPos;
            xPosB = b.inNeighbors.get(ptrB).xPos;
            if (xPosA == xPosB) {
                commonNeighbors.add(neighbor);
                positionInA.add(ptrA);
                positionInB.add(ptrB);
                ++ptrA;
                ++ptrB;
            } else {
                if (xPosA < xPosB) {
                    ++ptrA;
                } else if (xPosA == xPosB) {
                    ++ptrA;
                    ++ptrB;
                } else {
                    ++ptrB;
                }
            }
        }
        return commonNeighbors;
    }

    /**
     * Initialize the algorithm: - create special list-implementation for each
     * layer - create SiftingObjects for each node
     */
    private void initialize() {
        // run barycenter before sifting
        if (initMethod.equals("Barycenter")) {
            BaryCenter b = new BaryCenter();
            b.setData(data);
            b.attach(graph);
            Parameter<?>[] bParams = b.getAlgorithmParameters();
            IntegerParameter iterations = (IntegerParameter) bParams[0];
            iterations.setValue(siftingSweeps);
            b.setAlgorithmParameters(bParams);
            b.execute();
        }

        layers = new ArrayList<SiftingList>();
        nodeToObject = new Hashtable<Node, SiftingObject>((int) (graph
                .getNumberOfNodes() * 1.25));
        objectToNode = new Hashtable<SiftingObject, Node>((int) (graph
                .getNumberOfNodes() * 1.25));

        for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
            layers.add(new SiftingList(data.getLayers().getLayer(i), i));
        }

        initializeObjects();
    }

    /**
     * Initialize the SiftingObject datastructure: - create an ordered adjacency
     * list for each object - add indices in the adjacency lists for each object
     */
    private void initializeObjects() {
        int maxDegree = 0;
        // create adjacency lists for each object
        for (int i = 0; i < layers.size(); i++) {
            SiftingObject o = layers.get(i).head;

            while (o != null) {
                o.inNeighborsEager = new ArrayList<SiftingObject>(o.node
                        .getInDegree());
                for (Node n : o.node.getInNeighbors()) {
                    if (!o.inNeighborsEager.contains(nodeToObject.get(n))) {
                        o.inNeighborsEager.add(nodeToObject.get(n));
                    }
                }
                o.outNeighborsEager = new ArrayList<SiftingObject>(o.node
                        .getOutDegree());

                for (Node n : o.node.getOutNeighbors()) {
                    if (!o.outNeighborsEager.contains(nodeToObject.get(n))) {
                        o.outNeighborsEager.add(nodeToObject.get(n));
                    }
                }

                Collections.sort(o.inNeighborsEager);
                Collections.sort(o.outNeighborsEager);

                o.inNeighbors = new LazySiftingObjectArrayList(o.node
                        .getInDegree());
                for (SiftingObject n : o.inNeighborsEager) {
                    o.inNeighbors.add(n);
                }

                o.outNeighbors = new LazySiftingObjectArrayList(o.node
                        .getOutDegree());
                for (SiftingObject n : o.outNeighborsEager) {
                    o.outNeighbors.add(n);
                }

                o = o.next;
            }
        }

        for (int i = 0; i < layers.size(); i++) {
            SiftingObject object = layers.get(i).head;
            while (object != null) {
                if (object.inNeighbors.elementCount > maxDegree) {
                    maxDegree = object.inNeighbors.elementCount;
                }
                if (object.outNeighbors.elementCount > maxDegree) {
                    maxDegree = object.outNeighbors.elementCount;
                }

                object.outNeighborPositions = new int[object.outNeighbors.elementCount];
                object.inNeighborPositions = new int[object.inNeighbors.elementCount];

                for (int k = 0; k < object.outNeighbors.elementCount; k++) {
                    SiftingObject neighbor = object.outNeighbors.get(k);
                    object.outNeighborPositions[k] = neighbor.inNeighborsEager
                            .indexOf(object);
                }
                for (int k = 0; k < object.inNeighbors.elementCount; k++) {
                    SiftingObject neighbor = object.inNeighbors.get(k);
                    object.inNeighborPositions[k] = neighbor.outNeighborsEager
                            .indexOf(object);
                }
                object = object.next;
            }
        }
        positionInA = new LazyIntArrayList(maxDegree);
        positionInB = new LazyIntArrayList(maxDegree);
        commonNeighbors = new LazySiftingObjectArrayList(2 * maxDegree);
        neighborsA = new LazyIntArrayList(maxDegree);
        neighborsB = new LazyIntArrayList(maxDegree);
    }

    public SugiyamaData getData() {
        return data;
    }

    public void setData(SugiyamaData data) {
        this.data = data;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)
                || algorithmType
                        .equals(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA);
    }

    public boolean supportsBigNodes() {
        return false;
    }

    public boolean supportsConstraints() {
        return false;
    }

    public String getName() {
        if (klevel)
            return "k-level Sifting";
        else {
            if (optimized)
                return "Optimized Sifting";
            else
                return "Sifting";
        }

    }

    /**
     * This wrapper-class is used to add attributes to the node without using
     * the (slower) gravisto attribute-system
     */
    private class SiftingObject implements Comparable<SiftingObject> {
        protected Node node;
        protected LazySiftingObjectArrayList inNeighbors;
        protected LazySiftingObjectArrayList outNeighbors;

        protected ArrayList<SiftingObject> inNeighborsEager;
        protected ArrayList<SiftingObject> outNeighborsEager;

        // protected LazySiftingObjectArrayList inNeighborsOld;
        // protected LazySiftingObjectArrayList outNeighborsOld;
        protected int[] inNeighborPositions;
        protected int[] outNeighborPositions;
        protected int xPos;
        protected int id;
        protected int level;

        protected SiftingObject next;
        protected SiftingObject previous;

        public SiftingObject(Node node, int xPos, int level, int id) {
            this.node = node;
            this.id = id;
            this.xPos = xPos;
            this.level = level;
        }

        public int compareTo(SiftingObject other) {
            return this.xPos - other.xPos;
        }
    }

    /**
     * Implements a special version of a double-linked list. All of the
     * operations (except creation of the list and copying the list, which runs
     * in O(n)) run in O(1) time. The list itself needs O(1) space as well.
     */
    private class SiftingList {
        protected SiftingObject head;
        protected SiftingObject tail;
        protected SiftingObject bestPosition;
        protected int length;
        // protected int index;
        private LazySiftingObjectArrayList copy;

        /**
         * Create a new SiftingList by adding pointers to the Nodes in the
         * original ArrayList
         */
        public SiftingList(ArrayList<Node> origList, int index) {
            int idCounter = 0;
            // this.index = index;
            ArrayList<SiftingObject> objects = new ArrayList<SiftingObject>();
            SiftingObject object;
            for (int i = 0; i < origList.size(); i++) {
                object = new SiftingObject(origList.get(i), i, index, idCounter);
                ++idCounter;

                object.inNeighbors = new LazySiftingObjectArrayList(object.node
                        .getInDegree());
                object.outNeighbors = new LazySiftingObjectArrayList(
                        object.node.getOutDegree());
                // object.inNeighborsOld =
                new LazySiftingObjectArrayList(object.node.getInDegree());
                // object.outNeighborsOld =
                new LazySiftingObjectArrayList(object.node.getOutDegree());
                object.inNeighborPositions = new int[object.node.getInDegree()];
                object.outNeighborPositions = new int[object.node
                        .getOutDegree()];

                nodeToObject.put(origList.get(i), object);
                objectToNode.put(object, origList.get(i));
                objects.add(object);
            }

            length = origList.size();
            head = objects.get(0);
            tail = objects.get(origList.size() - 1);
            for (int i = 1; i < origList.size() - 1; i++) {
                objects.get(i).previous = objects.get(i - 1);
                objects.get(i).next = objects.get(i + 1);
            }
            head.previous = null;
            if (length == 1) {
                head.next = null;
                tail.next = null;
                tail.previous = null;
                tail = head;
            } else {
                head.next = objects.get(1);
                tail.next = null;
                tail.previous = objects.get(origList.size() - 2);
            }
            copy = new LazySiftingObjectArrayList(origList.size());
        }

        /**
         * Returns a copy of the list as an array-list
         */
        public LazySiftingObjectArrayList getArrayCopy() {
            // ArrayList<SiftingObject> ret = new ArrayList<SiftingObject>();
            copy.clear();
            SiftingObject current = head;
            while (current != null) {
                copy.add(current);
                current = current.next;
            }
            return copy;
        }

        /**
         * Move the object to the head of the list. This runs in O(1)
         */
        public void moveToHead(SiftingObject object) {
            if (object.next == null) {
                tail = object.previous;
                object.previous.next = null;

                head.previous = object;
                object.previous = null;
                object.next = head;
                head = object;
            }
            if (object.previous != null) {
                object.next.previous = object.previous;
                object.previous.next = object.next;
                head.previous = object;
                object.next = head;
                object.previous = null;
                head = object;
            }
        }

        /**
         * Swap the position of the two objects in the list. This runs in O(1)
         */
        public void swap(SiftingObject object, SiftingObject neighbor) {
            if (object.previous == null) {
                head = object.next;
                object.next = head.next;
                if (object.next != null) {
                    object.next.previous = object;
                } else {
                    tail = object;
                }

                head.previous = null;
                head.next = object;
                object.previous = head;
            } else {
                object.next = neighbor.next;
                if (object.next != null) {
                    object.next.previous = object;
                } else {
                    tail = object;
                }

                neighbor.previous = object.previous;
                neighbor.previous.next = neighbor;

                neighbor.next = object;
                object.previous = neighbor;
            }
        }

        /**
         * Move the object to its best position in the list Runs in O(1) because
         * of the pointer "bestPosition"
         */
        public void moveToBestPosition(SiftingObject object) {
            if (object == head && object == tail)
                return;
            if (bestPosition == null) {
                tail = object.previous;
                tail.next = null;

                object.next = head;
                head.previous = object;
                object.previous = null;
                head = object;
            } else {
                if (bestPosition.next.id == object.id)
                    return;
                else {
                    object.previous.next = null;
                    tail = object.previous;

                    object.next = bestPosition.next;
                    bestPosition.next.previous = object;

                    bestPosition.next = object;
                    object.previous = bestPosition;
                }

            }
        }
    }

    private class LazySiftingObjectArrayList {
        private SiftingObject[] elements;
        public int elementCount;

        public LazySiftingObjectArrayList(int size) {
            elements = new SiftingObject[size];
            elementCount = 0;
        }

        public SiftingObject get(int index) {
            return elements[index];
        }

        public void add(SiftingObject element) {
            elements[elementCount++] = element;
        }

        public void clear() {
            elementCount = 0;
        }

        public void set(int index, SiftingObject element) {
            elements[index] = element;
        }
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        StringSelectionParameter sParam = new StringSelectionParameter(
                new String[] { "Random", "Barycenter" },
                "Initialization method", "Initialization method");
        IntegerParameter lParameter = new IntegerParameter(siftingRounds, 0,
                100, "Sifting rounds", "Number of sifting rounds");
        IntegerParameter sweeps = new IntegerParameter(siftingSweeps, 0, 100,
                "Sifting sweeps", "Number of sifting sweeps");
        StringSelectionParameter nodePlacement = new StringSelectionParameter(
                new String[] { "classic", "k-layer" }, "Sifting method",
                "Sifting method");
        StringSelectionParameter optParam = new StringSelectionParameter(
                new String[] { "classic", "optimized" }, "Node placement",
                "Node placement");
        optParam.setDependency(nodePlacement, "classic");
        this.parameters = new Parameter[] { sParam, lParameter, sweeps,
                nodePlacement, optParam };
        return this.parameters;
    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        initMethod = ((StringSelectionParameter) params[0]).getSelectedValue();
        siftingRounds = ((IntegerParameter) params[1]).getValue();
        siftingSweeps = ((IntegerParameter) params[2]).getValue();

        StringSelectionParameter s = (StringSelectionParameter) params[3];
        if (s.getValue().equals("k-layer")) {
            klevel = true;
        } else {
            klevel = false;
        }

        s = (StringSelectionParameter) params[4];
        if (s.getValue().equals("optimized")) {
            optimized = true;
        } else {
            optimized = false;
        }
    }

    private class NodeDegreeComparator implements Comparator<SiftingObject> {
        @Override
        public int compare(SiftingObject o1, SiftingObject o2) {
            int deg1 = o1.inNeighbors.elementCount
                    + o2.outNeighbors.elementCount;
            int deg2 = o2.inNeighbors.elementCount
                    + o2.outNeighbors.elementCount;

            return (deg2 - deg1);
        }
    }

}