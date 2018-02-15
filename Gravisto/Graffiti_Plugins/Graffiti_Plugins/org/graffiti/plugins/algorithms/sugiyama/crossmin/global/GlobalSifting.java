// =============================================================================
//
//   GlobalSifting.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$
package org.graffiti.plugins.algorithms.sugiyama.crossmin.global;

import java.util.ArrayList;
import java.util.Collections;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.CrossMinAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.gridsifting.benchmark.SugiyamaBenchmarkAdapter;
import org.graffiti.plugins.algorithms.sugiyama.util.LazyCrossMinObjectArrayList;
import org.graffiti.plugins.algorithms.sugiyama.util.LazyIntArrayList;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.plugins.algorithms.sugiyama.util.XPosComparator;

/**
 * This class implements a crossing minimization algorithm based on the sifting
 * heuristic. In contrast to classic implementations of the sifting heuristic,
 * this implementation performs crossing minimization on a global scale by
 * sifting <code>CrossMinObjects</code> instead of <code>Nodes</code>.
 * 
 * @author Ferdinand H&uuml;bner
 */
public class GlobalSifting extends AbstractAlgorithm implements
        CrossMinAlgorithm {
    // //////////////////////////////////////////////////////////////////////////
    // Constants //
    // //////////////////////////////////////////////////////////////////////////

    /** The name of this algorithm */
    private final String ALGORITHM_NAME = "Global sifting";

    /** Maximum number of layers that might cross when sifting two objects */
    private final int MAX_CROSSING_LAYERS = 4;

    /**
     * When computing edge intersections between two levels, this boolean is
     * used if the neighbors on the layer above the current layer are being
     * considered
     */
    private final boolean DIRECTION_UP = true;

    /**
     * When computing edge intersections between two levels, this boolean is
     * used if the neighbors on the layer below the current layer are being
     * considered
     */
    private final boolean DIRECTION_DOWN = false;

    /** Number of CrossMin objects */
    private int OBJECTS_SIZE;

    // //////////////////////////////////////////////////////////////////////////
    // Helper objects //
    // //////////////////////////////////////////////////////////////////////////

    /** SugiyamaData */
    private SugiyamaData data;

    /** Parameters of this algorithm */
    private Parameter<?>[] parameters;

    /** An ArrayList storing the CrossMinObjects */
    private SiftingList objects;

    /** number of sifting rounds */
    private int siftingRounds = 5;

    /** initialization method */
    StringSelectionParameter initMethod;

    // //////////////////////////////////////////////////////////////////////////
    // Caches //
    // //////////////////////////////////////////////////////////////////////////
    private boolean[][] objectsCross;

    // those are class-variables for performance-reasons
    private LazyIntArrayList positionInA;
    private LazyIntArrayList positionInB;
    private LazyCrossMinObjectArrayList commonNeighbors;
    private int[] level;
    private boolean[] direction;
    private LazyIntArrayList[] neighbors;
    private CrossMinObject[] myObjects;

    // private long initStep;
    // private long finalizeStep;
    // //////////////////////////////////////////////////////////////////////////
    // algorithm-specific methods //
    // //////////////////////////////////////////////////////////////////////////

    /**
     * This method sifts one object: - move the object to the leftmost position
     * (xpos 0) - swap the object with its right neighbor and compute the
     * difference in edge intersecionts (delta) until the object is at the
     * rightmost position - move the object to the position where delta is
     * minimal
     * 
     * @param object
     *            The CrossMinObject being sifted
     * @return Returns the difference in edge intersections after the object has
     *         been placed at its optimal position
     */
    private int siftingStep(CrossMinObject object) {
        int delta;
        int bestDelta;
        int bestPosition;
        int originalPosition = object.xPos;
        int limit;
        int nextIndex;

        // move the object to position 0
        if (originalPosition != 0) {
            objects.moveToHead(object);
            object.xPos = 0;
        }

        // update the xpos of all objects before the original position
        int index = 1;
        CrossMinObject current = object.next;
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
        objects.bestPosition = null;
        limit = OBJECTS_SIZE - 1;
        for (int i = 0; i < limit; i++) {
            nextIndex = i + 1;
            CrossMinObject otherObject = object.next;
            delta += getDelta(object, otherObject);
            if (delta < bestDelta) {
                bestDelta = delta;
                bestPosition = nextIndex;
                objects.bestPosition = otherObject;
            }

            objects.swap(object, otherObject);
            object.xPos = nextIndex;
            otherObject.xPos = i;

            if (objectsCross[object.id][otherObject.id]) {
                updateObjects(object, otherObject);
            }
        }
        objects.moveToBestPosition(object);

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

    /**
     * This method updates the objects a and b after they have been swapped.
     * After swapping the objects, the position of a and b in the adjacency
     * lists of their common neighbors is wrong. The objects have to be swapped
     * in the adjacency list of each common neighbor.
     * 
     * @param a
     *            first object
     * @param b
     *            second object
     */
    private void updateObjects(CrossMinObject a, CrossMinObject b) {
        // those lists are being used to determine the position of their common
        // neighbor in the adjacency list of a or b in constant time
        getCommonInNeighbors(a, b, positionInA, positionInB);

        CrossMinObject neighbor;
        int commonNeighborsSize = commonNeighbors.elementCount;

        int posInA, posInB;
        for (int i = 0; i < commonNeighborsSize; i++) {
            neighbor = commonNeighbors.get(i);
            // get the index of a and b in the adjacency list of their common
            // neighbor
            posInA = positionInA.get(i);
            posInB = positionInB.get(i);
            int idxA = a.inNeighborPositions.get(posInA);
            int idxB = b.inNeighborPositions.get(posInB);

            // swap the neighbors
            neighbor.outNeighbors.set(idxA, b);
            neighbor.outNeighbors.set(idxB, a);

            // update indices on the neighbor
            int tmp;
            tmp = neighbor.outNeighborPositions.get(idxA);
            neighbor.outNeighborPositions.set(idxA,
                    neighbor.outNeighborPositions.get(idxB));
            neighbor.outNeighborPositions.set(idxB, tmp);

            // update indices on a and b
            a.inNeighborPositions.set(posInA, idxB);
            b.inNeighborPositions.set(posInB, idxA);

        }

        getCommonOutNeighbors(a, b, positionInA, positionInB);
        commonNeighborsSize = commonNeighbors.elementCount;
        for (int i = 0; i < commonNeighborsSize; i++) {
            neighbor = commonNeighbors.get(i);
            posInA = positionInA.get(i);
            posInB = positionInB.get(i);
            int idxA = a.outNeighborPositions.get(posInA);
            int idxB = b.outNeighborPositions.get(posInB);

            // swap the neighbors
            neighbor.inNeighbors.set(idxA, b);
            neighbor.inNeighbors.set(idxB, a);

            // update indices on the neighbor
            int tmp;
            tmp = neighbor.inNeighborPositions.get(idxA);
            neighbor.inNeighborPositions.set(idxA, neighbor.inNeighborPositions
                    .get(idxB));
            neighbor.inNeighborPositions.set(idxB, tmp);

            // update indices on a and b
            a.outNeighborPositions.set(posInA, idxB);
            b.outNeighborPositions.set(posInB, idxA);

        }
    }

    private void getCommonOutNeighbors(CrossMinObject a, CrossMinObject b,
            LazyIntArrayList positionInA, LazyIntArrayList positionInB) {
        commonNeighbors.clear();
        positionInA.clear();
        positionInB.clear();
        CrossMinObject neighbor;
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
        // return commonNeighbors;
    }

    private void getCommonInNeighbors(CrossMinObject a, CrossMinObject b,
            LazyIntArrayList positionInA, LazyIntArrayList positionInB) {
        commonNeighbors.clear();
        positionInA.clear();
        positionInB.clear();
        CrossMinObject neighbor;
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
        // return commonNeighbors;
    }

    /**
     * This method initializes a sifting step. <i>Object</i> is being moved to
     * the front of the list and the x-coordinates change. This method fixes the
     * sorting in the adjacency lists.
     * 
     * @param object
     *            The object that is being sifted
     */
    private void initializeStepLazy(CrossMinObject object) {
        // checkObjects();
        // Date initStepStart = new Date();

        int originalPosition, tmp;
        CrossMinObject neighbor, otherNeighbor;

        for (int i = 0; i < object.inNeighbors.elementCount; i++) {
            neighbor = object.inNeighbors.get(i);
            originalPosition = object.inNeighborPositions.get(i);

            // move all objects that are left of o one position to the right
            for (int j = originalPosition; j > 0; j--) {
                otherNeighbor = neighbor.outNeighbors.get(j - 1);
                tmp = neighbor.outNeighborPositions.get(j - 1);
                neighbor.outNeighbors.set(j, otherNeighbor);
                neighbor.outNeighborPositions.set(j, tmp);
                otherNeighbor.inNeighborPositions.set(tmp, j);
            }
            neighbor.outNeighbors.set(0, object);
            neighbor.outNeighborPositions.set(0, i);
            object.inNeighborPositions.set(i, 0);
        }

        for (int i = 0; i < object.outNeighbors.elementCount; i++) {
            neighbor = object.outNeighbors.get(i);
            originalPosition = object.outNeighborPositions.get(i);

            // move all objects that are left of o one position to the right
            for (int j = originalPosition; j > 0; j--) {
                otherNeighbor = neighbor.inNeighbors.get(j - 1);
                tmp = neighbor.inNeighborPositions.get(j - 1);
                neighbor.inNeighbors.set(j, otherNeighbor);
                neighbor.inNeighborPositions.set(j, tmp);
                otherNeighbor.outNeighborPositions.set(tmp, j);
            }
            neighbor.inNeighbors.set(0, object);
            neighbor.inNeighborPositions.set(0, i);
            object.outNeighborPositions.set(i, 0);
        }

        // Date initStepEnd = new Date();
        // initStep += (initStepEnd.getTime() - initStepStart.getTime());
        // checkObjects();
    }

    /**
     * This method finalizes a sifting step. After <i>object</i> has been moved
     * to the optimal position, adjacency lists of the object and its neighbors
     * have to be updated in order to reflect the change in the x-coordinates.
     * 
     * @param object
     *            The object being moved to its optimal position
     */
    private void finalizeStepLazy(CrossMinObject object) {
        // checkObjects();
        // Date finalizeStepStart = new Date();

        CrossMinObject neighbor, otherNeighbor;
        int originalPosition, newPosition, tmp;

        for (int i = 0; i < object.inNeighbors.elementCount; i++) {
            neighbor = object.inNeighbors.get(i);
            originalPosition = object.inNeighborPositions.get(i);

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
                    tmp = neighbor.outNeighborPositions.get(j - 1);
                    neighbor.outNeighborPositions.set(j, tmp);
                    // and update the index in the adjacency list of the object
                    otherNeighbor.inNeighborPositions.set(tmp, j);
                }
            }
            // move o to the right position
            neighbor.outNeighbors.set(newPosition, object);
            neighbor.outNeighborPositions.set(newPosition, i);
            object.inNeighborPositions.set(i, newPosition);
        }
        for (int i = 0; i < object.outNeighbors.elementCount; i++) {
            neighbor = object.outNeighbors.get(i);
            originalPosition = object.outNeighborPositions.get(i);

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
                    tmp = neighbor.inNeighborPositions.get(j - 1);
                    neighbor.inNeighborPositions.set(j, tmp);
                    // and update the index in the adjacency list of the object
                    otherNeighbor.outNeighborPositions.set(tmp, j);
                }
            }
            // move o to the right position
            neighbor.inNeighbors.set(newPosition, object);
            neighbor.inNeighborPositions.set(newPosition, i);
            object.outNeighborPositions.set(i, newPosition);
        }

        // Date finalizeStepEnd = new Date();
        // finalizeStep += (finalizeStepEnd.getTime() -
        // finalizeStepStart.getTime());

        // checkObjects();
    }

    /**
     * Returns the difference in the number of edge intersecions if object a is
     * being swapped with object b
     * 
     * @param a
     *            first object
     * @param b
     *            second object - adjacent to object a
     * @return Returns the difference in the number of edge intersections if
     *         object a is being swapped with object b
     */
    private int getDelta(CrossMinObject a, CrossMinObject b) {
        // objects don't cross - delta is 0
        if (!objectsCross[a.id][b.id])
            return 0;

        // get the layers that can cause edge intersections
        int crossingLayers = getCrossingLayers(level, direction, a, b);

        int delta = 0;

        // compute the delta value for each layers that can cause edge
        // intersections
        for (int crossing = 0; crossing < crossingLayers; crossing++) {
            getNeighbors(a, b, level[crossing], direction[crossing]);

            LazyIntArrayList posA, posB;
            posA = neighbors[0];
            posB = neighbors[1];

            int r = posA.elementCount;
            int s = posB.elementCount;
            int c = 0;
            int i = 0;
            int j = 0;
            while (i < r && j < s) {
                if (posA.get(i) < posB.get(j)) {
                    c += (s - j);
                    ++i;
                } else if (posB.get(j) < posA.get(i)) {
                    c -= (r - i);
                    ++j;
                } else {
                    c += (s - j) - (r - i);
                    ++i;
                    ++j;
                }
            }
            delta += c;
        }
        return delta;
    }

    /**
     * Returns an integer matrix, or to be more precise: two integer arrays
     * storing the indices of the neighbors of object a and b on the level
     * defined by the parameters "level" and "direction"
     * 
     * @param a
     *            first object
     * @param b
     *            second object
     * @param level
     *            the level on which both objects have a real node
     * @param direction
     *            if this is <code>true</code> the neighbors on the layer
     *            level-1 (the layer above the layer "level") are being
     *            returned, the neighbors on layer level+1 (the layer below the
     *            layer "level") otherwise
     */
    private void getNeighbors(CrossMinObject a, CrossMinObject b, int level,
            boolean direction) {
        // int[][] neighbors = new int[2][];

        // CrossMinObject[] myObjects = new CrossMinObject[] { a, b };
        myObjects[0] = a;
        myObjects[1] = b;

        LazyCrossMinObjectArrayList neighborArray;

        for (int i = 0; i < 2; i++) {
            if (myObjects[i].isNode()) {
                if (level != myObjects[i].getLevel()) {
                    neighbors[i].clear();
                } else {
                    if (direction == DIRECTION_UP) {
                        neighborArray = myObjects[i].inNeighbors;
                    } else {
                        neighborArray = myObjects[i].outNeighbors;
                    }

                    neighbors[i].clear();

                    for (int j = 0; j < neighborArray.elementCount; j++) {
                        neighbors[i].add(neighborArray.get(j).xPos);
                    }
                }
            } else {
                if (level == myObjects[i].getMinLevel()) {
                    if (direction == DIRECTION_UP) {
                        neighborArray = myObjects[i].inNeighbors;
                    } else {
                        neighborArray = null;
                    }
                } else if (level == myObjects[i].getMaxLevel()) {
                    if (direction == DIRECTION_UP) {
                        neighborArray = null;
                    } else {
                        neighborArray = myObjects[i].outNeighbors;
                    }
                } else {
                    neighborArray = null;
                }

                if (neighborArray == null) {
                    neighbors[i].clear();
                    neighbors[i].add(myObjects[i].xPos);
                } else {
                    neighbors[i].clear();
                    for (int j = 0; j < neighborArray.elementCount; j++) {
                        neighbors[i].add(neighborArray.get(j).xPos);
                    }
                }
            }
        }
        // return neighbors;
    }

    /**
     * This method computes the layers on which edges from the objects a and b
     * intersect. The layers on which these edge intersections happen are stored
     * in the two arrays "level" and "direction".
     * 
     * @param level
     *            The level on which both objects have a node
     * @param direction
     *            if this is <code>true</code> the neighbors on layer level-1
     *            cause edge intersections with the nodes on layer level,
     *            otherwise the neighbors on layer level+1.
     * @param a
     *            first object
     * @param b
     *            second object
     * @return Returns the number of layers where edges between the two objects
     *         intersect. The actual layers are being saved in the arrays level
     *         and direction
     */
    private int getCrossingLayers(int[] level, boolean[] direction,
            CrossMinObject a, CrossMinObject b) {
        int index = 0;

        // straight-forward if both objects are nodes or a is a node and b an
        // inner segment: there are two levels that can cause edge intersections
        if (a.isNode() && b.isNode() || a.isNode() && b.isInnerSegment()) {
            index = 1;
            level[0] = a.getLevel();
            level[1] = level[0];
            direction[0] = DIRECTION_UP;
            direction[1] = DIRECTION_DOWN;

            return index + 1;
        }
        // same as above
        else if (a.isInnerSegment() && b.isNode()) {
            index = 1;
            level[0] = b.getLevel();
            level[1] = level[0];
            direction[0] = DIRECTION_UP;
            direction[1] = DIRECTION_DOWN;

            return index + 1;
        }
        // if two inner segments are involved, there might be up to four levels
        // where edges can intersect
        else {
            int aMin, aMax, bMin, bMax;
            aMin = a.getMinLevel();
            aMax = a.getMaxLevel();
            bMin = b.getMinLevel();
            bMax = b.getMaxLevel();
            // if both objects do not contain an edge from level k to level 0,
            // there are exactly two conflicts
            if (aMin < aMax && bMin < bMax) {
                if (aMin >= bMin) {
                    level[index] = aMin;
                    direction[index] = DIRECTION_UP;
                } else {
                    level[index] = bMin;
                    direction[index] = DIRECTION_UP;
                }
                ++index;

                if (aMax <= bMax) {
                    level[index] = aMax;
                    direction[index] = DIRECTION_DOWN;
                } else {
                    level[index] = bMax;
                    direction[index] = DIRECTION_DOWN;
                }

                return index + 1;
            }
            // if one object contains an edge from level k to level 0, there
            // might be up to four conflicts - check if they are valid
            else {
                if (bMin > bMax) {
                    if (aMin <= bMax || aMin >= bMin) {
                        level[index] = aMin;
                        direction[index] = DIRECTION_UP;
                        ++index;
                    }

                    if (aMax <= bMax || aMax >= bMin) {
                        level[index] = aMax;
                        direction[index] = DIRECTION_DOWN;
                        ++index;
                    }
                } else {
                    if (aMin >= bMin && aMin <= bMax) {
                        level[index] = aMin;
                        direction[index] = DIRECTION_UP;
                        ++index;
                    }

                    if (aMax >= bMin && aMax <= bMax) {
                        level[index] = aMax;
                        direction[index] = DIRECTION_DOWN;
                        ++index;
                    }
                }

                if (aMin > aMax) {
                    if (bMin <= aMax || bMin >= aMin) {
                        level[index] = bMin;
                        direction[index] = DIRECTION_UP;
                        ++index;
                    }

                    if (bMax <= aMax || bMax >= aMin) {
                        level[index] = bMax;
                        direction[index] = DIRECTION_DOWN;
                        ++index;
                    }

                } else {
                    if (bMin >= aMin && bMin <= aMax) {
                        level[index] = bMin;
                        direction[index] = DIRECTION_UP;
                        ++index;
                    }

                    if (bMax >= aMin && bMax <= aMax) {
                        level[index] = bMax;
                        direction[index] = DIRECTION_DOWN;
                        ++index;
                    }
                }
            }
            return index;
        }
    }

    /**
     * This method is used to determine if the edges adjacent to the objects can
     * intersect or not.
     * 
     * @param a
     *            first object
     * @param b
     *            second object
     * @return returns <code>true</code> if the edges adjacent to the objects
     *         can intersect, <code>false</code> otherwise.
     */
    private boolean objectsCross(CrossMinObject a, CrossMinObject b) {
        boolean aNode, bNode;
        int aLevel, bLevel;
        int aMin, aMax, bMin, bMax;
        aNode = a.isNode();
        bNode = b.isNode();
        // two nodes have to be on the same level for their edges to intersect
        if (aNode && bNode) {
            aLevel = a.getLevel();
            bLevel = b.getLevel();
            if (aLevel != bLevel)
                return false;
        }
        // one node and one inner segment: the node has to be on the same level
        // as one node of the inner segment
        else if (aNode && !bNode) {
            bMin = b.getMinLevel();
            bMax = b.getMaxLevel();
            aLevel = a.getLevel();
            if (bMin > bMax) {
                if (aLevel < bMin && aLevel > bMax)
                    return false;
            } else {
                if (aLevel > bMax || aLevel < bMin)
                    return false;
            }
        }
        // same as above
        else if (!aNode && bNode) {
            aMin = a.getMinLevel();
            aMax = a.getMaxLevel();
            bLevel = b.getLevel();
            if (aMin > aMax) {
                if (bLevel < aMin && bLevel > aMax)
                    return false;
            } else {
                if (bLevel > aMax || bLevel < aMin)
                    return false;
            }
        }
        // two inner segments
        else {
            aMin = a.getMinLevel();
            aMax = a.getMaxLevel();
            bMin = b.getMinLevel();
            bMax = b.getMaxLevel();
            // a contains an edge from layer k to layer 0
            if (aMin > aMax) {
                if (bMin < bMax) {
                    if (bMax < aMin && bMin > aMax)
                        return false;
                }
            }
            // a does not contain an edge from layer k to layer 0
            else {
                if (bMin > bMax) {
                    if (aMax < bMin && aMin > bMax)
                        return false;
                } else {
                    if (aMin > bMax || aMax < bMin)
                        return false;
                }
            }
        }
        return true;
    }

    /**
     * This method initializes the helper objects
     */
    private void initialize() {
        ArrayList<CrossMinObject> origList;

        level = new int[MAX_CROSSING_LAYERS];
        direction = new boolean[MAX_CROSSING_LAYERS];
        CrossMinObjectCollector collector;

        if (initMethod.getSelectedValue().equals("Global Barycenter")) {
            GlobalBarycenter b = new GlobalBarycenter();
            Parameter<?>[] bparams = b.getAlgorithmParameters();
            IntegerParameter i = (IntegerParameter) bparams[0];
            i.setValue(siftingRounds);
            b.setAlgorithmParameters(bparams);
            b.setData(data);
            b.attach(graph);
            b.execute();
            collector = b.collector;
            origList = b.getObjects();
            this.objects = new SiftingList(origList);
        } else {
            collector = new CrossMinObjectCollector(data.getGraph(), data);
            origList = collector.collectObjects();
            objects = new SiftingList(origList);
        }

        OBJECTS_SIZE = origList.size();

        positionInA = new LazyIntArrayList(collector.maxDegree);
        positionInB = new LazyIntArrayList(collector.maxDegree);
        commonNeighbors = new LazyCrossMinObjectArrayList(
                2 * collector.maxDegree);
        neighbors = new LazyIntArrayList[2];
        neighbors[0] = new LazyIntArrayList(collector.maxDegree);
        neighbors[1] = new LazyIntArrayList(collector.maxDegree);
        myObjects = new CrossMinObject[2];

        objectsCross = new boolean[OBJECTS_SIZE][OBJECTS_SIZE];

        for (int i = 0; i < OBJECTS_SIZE; i++) {
            CrossMinObject objectI = origList.get(i);
            int idI = objectI.id;
            for (int j = i; j < OBJECTS_SIZE; j++) {
                CrossMinObject objectJ = origList.get(j);
                int idJ = objectJ.id;
                objectsCross[idI][idJ] = objectsCross(objectI, objectJ);
                objectsCross[idJ][idI] = objectsCross[idI][idJ];
            }
        }
    }

    /**
     * Save the xpos value to the attribute tree of the node and sort the
     * NodeLayers data structure
     */
    private void cleanup() {
        CrossMinObject object = objects.head;
        while (object != null) {
            for (Node n : object.getNodes()) {
                n.setDouble(SugiyamaConstants.PATH_XPOS, object.xPos);
            }
            object = object.next;
        }

        NodeLayers layers = data.getLayers();
        int numberOfLayers = layers.getNumberOfLayers();
        for (int i = 0; i < numberOfLayers; i++) {
            Collections.sort(layers.getLayer(i), new XPosComparator());
        }

    }

    // //////////////////////////////////////////////////////////////////////////
    // Methods inherited from AbstractAlgorithm/CrossMinAlgorithm //
    // //////////////////////////////////////////////////////////////////////////

    /**
     * Execute the algorithm
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);

        // initStep = 0;

        // Date initStart = new Date();
        this.initialize();

        long startTime = System.nanoTime();
        // Date initEnd = new Date();

        // Date siftingStart = new Date();
        for (int round = 0; round < siftingRounds; round++) {
            LazyCrossMinObjectArrayList clonedObjects = objects.getArrayCopy();
            for (int i = 0; i < OBJECTS_SIZE; i++) {
                siftingStep(clonedObjects.get(i));
            }
        }
        // Date siftingEnd = new Date();

        long endTime = System.nanoTime();

        data.putObject(SugiyamaBenchmarkAdapter.CROSSMIN_TIME_KEY, endTime
                - startTime);

        // Date cleanupStart = new Date();
        this.cleanup();
        // Date cleanupEnd = new Date();

        // String out = "Initialization : " + (initEnd.getTime() -
        // initStart.getTime()) + "ms\n";
        // out += "Sifting        : " + (siftingEnd.getTime() -
        // siftingStart.getTime()) + "ms (" + initStep + "ms initStep, " +
        // finalizeStep + "ms finalizeStep)\n";
        // out += "Cleanup        : " + (cleanupEnd.getTime() -
        // cleanupStart.getTime()) + "ms";
        // System.out.println(out);

        graph.getListenerManager().transactionFinished(this);

    }

    public SugiyamaData getData() {
        return this.data;
    }

    public void setData(SugiyamaData data) {
        this.data = data;
    }

    public String getName() {
        return this.ALGORITHM_NAME;
    }

    /**
     * Access available parameters to tweak settings of the algorithm
     * 
     * @return Returns an array of valid parameters for this algorithm
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        String[] params = new String[] { "Random", "Global Barycenter" };
        initMethod = new StringSelectionParameter(params,
                "Initialization method",
                "Specifies how the sifting objects should" + " be initialized");
        IntegerParameter lParameter = new IntegerParameter(siftingRounds, 0,
                100, "Sifting rounds", "Number of sifting rounds");
        this.parameters = new Parameter[] { initMethod, lParameter };
        return this.parameters;
    }

    /**
     * Store a modified array of parameters.
     * 
     * @param params
     *            New array of algorithm parameters
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        initMethod = ((StringSelectionParameter) params[0]);
        siftingRounds = ((IntegerParameter) params[1]).getValue();
    }

    // //////////////////////////////////////////////////////////////////////////
    // Capabilities of this algorithm //
    // //////////////////////////////////////////////////////////////////////////
    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType.equals(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)
                || algorithmType
                        .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
    }

    public boolean supportsBigNodes() {
        return false;
    }

    public boolean supportsConstraints() {
        return false;
    }

    private class SiftingList {
        protected CrossMinObject head;
        protected CrossMinObject tail;
        protected CrossMinObject bestPosition;
        private LazyCrossMinObjectArrayList copy;

        public SiftingList(ArrayList<CrossMinObject> origList) {
            head = origList.get(0);
            tail = origList.get(origList.size() - 1);
            for (int i = 1; i < origList.size() - 1; i++) {
                origList.get(i).previous = origList.get(i - 1);
                origList.get(i).next = origList.get(i + 1);
            }
            head.previous = null;
            head.next = origList.get(1);
            tail.next = null;
            tail.previous = origList.get(origList.size() - 2);
            copy = new LazyCrossMinObjectArrayList(origList.size());
        }

        public LazyCrossMinObjectArrayList getArrayCopy() {
            copy.clear();

            CrossMinObject current = head;
            while (current != null) {
                copy.add(current);
                current = current.next;
            }
            return copy;
        }

        public void moveToHead(CrossMinObject object) {
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

        public void swap(CrossMinObject object, CrossMinObject neighbor) {
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

        public void moveToBestPosition(CrossMinObject object) {
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

    // private void checkObjects() {
    // LazyCrossMinObjectArrayList list = objects.getArrayCopy();
    // for (int x = 0; x < list.elementCount; x++) {
    // CrossMinObject o = list.get(x);
    // for(int i = 0; i < o.inNeighbors.elementCount; i++) {
    // CrossMinObject n = o.inNeighbors.get(i);
    // int nPos = o.inNeighborPositions.get(i);
    // if (n.outNeighbors.elementCount <= nPos || n.outNeighbors.get(nPos) != o)
    // {
    // throw new IllegalStateException("1");
    // }
    // if (n.outNeighborPositions.elementCount <= nPos ||
    // n.outNeighborPositions.get(nPos) != i) {
    // throw new IllegalStateException("2");
    // }
    // }
    // for(int i = 0; i < o.outNeighbors.elementCount; i++) {
    // CrossMinObject n = o.outNeighbors.get(i);
    // int nPos = o.outNeighborPositions.get(i);
    // if (n.inNeighbors.elementCount <= nPos || n.inNeighbors.get(nPos) != o) {
    // throw new IllegalStateException("3");
    // }
    // if (n.inNeighborPositions.elementCount <= nPos ||
    // n.inNeighborPositions.get(nPos) != i) {
    // throw new IllegalStateException("4");
    // }
    // }
    // }
    // }
}
