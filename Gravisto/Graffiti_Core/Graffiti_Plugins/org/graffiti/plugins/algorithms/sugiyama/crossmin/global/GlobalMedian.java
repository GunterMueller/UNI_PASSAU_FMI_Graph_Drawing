// =============================================================================
//
//   GlobalMedian.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$
package org.graffiti.plugins.algorithms.sugiyama.crossmin.global;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.CrossMinAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.LazyCrossMinObjectArrayList;
import org.graffiti.plugins.algorithms.sugiyama.util.LazyIntArrayList;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.plugins.algorithms.sugiyama.util.XPosComparator;

/**
 * <p>
 * This class implements the median heuristic for crossing minimization by using
 * <code>CrossMinObjects</code>.
 * </p>
 */
public class GlobalMedian extends AbstractAlgorithm implements
        CrossMinAlgorithm {

    /** SugiyamaData bean */
    private SugiyamaData data;

    /** The CrossMinObjects */
    private ArrayList<CrossMinObject> objects;

    /** Number of objects */
    private int OBJECTS_SIZE;

    /** Number of sweeps */
    private int sweeps = 5;

    protected CrossMinObjectCollector collector;

    protected LazyIntArrayList adjacency;

    /**
     * Accessor to get the list of CrossMinObjects
     */
    public ArrayList<CrossMinObject> getObjects() {
        return objects;
    }

    /**
     * <p>
     * Executes the algorithm.
     * </p>
     * <p>
     * One sweep computes the barycenter value for each object and sorts the
     * list of objects by that value
     * </p>
     */
    public void execute() {
        collector = new CrossMinObjectCollector(data.getGraph(), data);
        graph.getListenerManager().transactionStarted(this);
        this.objects = collector.collectObjects();
        adjacency = new LazyIntArrayList(2 * collector.maxDegree);
        for (CrossMinObject o : objects) {
            o.inNeighborsOld = new LazyCrossMinObjectArrayList(
                    o.inNeighbors.elementCount);
            o.outNeighborsOld = new LazyCrossMinObjectArrayList(
                    o.outNeighbors.elementCount);
        }

        OBJECTS_SIZE = this.objects.size();

        for (int sweep = 0; sweep < sweeps; sweep++) {
            sortAdjacencies();

            for (int i = 0; i < OBJECTS_SIZE; i++) {
                computeMedian(objects.get(i));
            }
            Collections.sort(objects, new CrossMinObjectMedianComparator());
            for (int i = 0; i < OBJECTS_SIZE; i++) {
                objects.get(i).xPos = i;
            }
        }

        for (CrossMinObject object : objects) {
            for (Node n : object.getNodes()) {
                n.setDouble(SugiyamaConstants.PATH_XPOS, object.xPos);
            }
        }

        for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
            Collections
                    .sort(data.getLayers().getLayer(i), new XPosComparator());
        }
        for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
            for (int j = 0; j < data.getLayers().getLayer(i).size(); j++) {
                data.getLayers().getLayer(i).get(j).setDouble(
                        SugiyamaConstants.PATH_XPOS, j);
            }
        }

        graph.getListenerManager().transactionFinished(this);

    }

    private void sortAdjacencies() {
        int OBJECTS_SIZE = objects.size();
        CrossMinObject current, neighbor;
        LazyCrossMinObjectArrayList swap;

        for (int i = 0; i < OBJECTS_SIZE; i++) {
            current = objects.get(i);

            swap = current.outNeighbors;
            current.outNeighborsOld.clear();
            current.outNeighbors = current.outNeighborsOld;
            current.outNeighborsOld = swap;

            swap = current.inNeighbors;
            current.inNeighborsOld.clear();
            current.inNeighbors = current.inNeighborsOld;
            current.inNeighborsOld = swap;
        }

        // re-create adjacency lists
        for (int i = 0; i < OBJECTS_SIZE; i++) {
            current = objects.get(i);

            for (int j = 0; j < current.inNeighborsOld.elementCount; j++) {
                neighbor = current.inNeighborsOld.get(j);
                neighbor.outNeighbors.add(current);
            }
            for (int j = 0; j < current.outNeighborsOld.elementCount; j++) {
                neighbor = current.outNeighborsOld.get(j);
                neighbor.inNeighbors.add(current);
            }
        }
    }

    /**
     * Computes the median for the object
     * 
     * @param object
     *            the crossing minimization object
     */
    private void computeMedian(CrossMinObject object) {
        if (object.inNeighbors.elementCount == 0
                && object.outNeighbors.elementCount == 0) {
            object.barycenter = 0;
            return;
        }
        adjacency.clear();

        LazyCrossMinObjectArrayList in = object.inNeighbors;
        LazyCrossMinObjectArrayList out = object.outNeighbors;

        if (in.elementCount == 0) {
            if (out.elementCount % 2 == 0) {
                object.barycenter = 0;
                object.barycenter += out.get(out.elementCount / 2).xPos;
                object.barycenter += out.get((out.elementCount / 2) - 1).xPos;
                object.barycenter = object.barycenter / 2;
                return;
            } else {
                object.barycenter = out.get((out.elementCount - 1) / 2).xPos;
                return;
            }
        } else if (out.elementCount == 0) {
            if (in.elementCount % 2 == 0) {
                object.barycenter = 0;
                object.barycenter += in.get(in.elementCount / 2).xPos;
                object.barycenter += in.get((in.elementCount / 2) - 1).xPos;
                object.barycenter = object.barycenter / 2;
                return;
            } else {
                object.barycenter = in.get((in.elementCount - 1) / 2).xPos;
                return;
            }
        }
        int degree = in.elementCount + out.elementCount;
        int indexMedian;
        boolean averageMedian;

        if (degree % 2 == 0) {
            indexMedian = (degree / 2) - 1;
            averageMedian = true;
        } else {
            indexMedian = (degree - 1) / 2;
            averageMedian = false;
        }

        int ptrA = 0;
        int ptrB = 0;
        int index = 0;

        while (ptrA < in.elementCount && ptrB < out.elementCount) {
            ++index;
            if (in.get(ptrA).xPos < out.get(ptrB).xPos) {
                adjacency.add(in.get(ptrA++).xPos);
            } else {
                adjacency.add(out.get(ptrB++).xPos);
            }

            if (index == indexMedian && !averageMedian) {
                break;
            }

            if (index == indexMedian + 1 && averageMedian) {
                break;
            }
        }
        if (index == indexMedian && !averageMedian) {
            object.barycenter = adjacency.get(indexMedian);
            return;
        } else if (index == indexMedian + 1 && averageMedian) {
            object.barycenter = adjacency.get(index - 1);
            object.barycenter += adjacency.get(index);
            object.barycenter /= 2;
        } else {
            if (ptrA >= in.elementCount) {
                while (index != indexMedian) {
                    ++index;
                    ++ptrB;
                }

                if (averageMedian) {
                    object.barycenter = out.get(ptrB++).xPos;
                    object.barycenter += out.get(ptrB).xPos;
                    object.barycenter /= 2;
                    return;
                } else {
                    object.barycenter = out.get(ptrB).xPos;
                    return;
                }
            } else {
                while (index != indexMedian) {
                    ++index;
                    ++ptrA;
                }

                if (averageMedian) {
                    object.barycenter = in.get(ptrA++).xPos;
                    object.barycenter += in.get(ptrA).xPos;
                    object.barycenter /= 2;
                    return;
                } else {
                    object.barycenter = in.get(ptrA).xPos;
                    return;
                }
            }
        }
    }

    /**
     * This class implements a simple comparator to compare two objects by their
     * barycenter
     */
    private class CrossMinObjectMedianComparator implements
            Comparator<CrossMinObject> {

        public int compare(CrossMinObject o1, CrossMinObject o2) {
            if (o1.barycenter < o2.barycenter)
                return -1;
            else if (o1.barycenter > o2.barycenter)
                return 1;
            else
                return 0;
        }

    }

    /**
     * Access available parameters to tweak settings of the algorithm
     * 
     * @return Returns an array of valid parameters for this algorithm
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter sweepParam = new IntegerParameter(sweeps, 1, 1000,
                "Sweeps", "Number of sweeps");
        this.parameters = new Parameter[] { sweepParam };
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
        sweeps = ((IntegerParameter) params[0]).getValue();
    }

    public SugiyamaData getData() {
        return this.data;
    }

    public void setData(SugiyamaData data) {
        this.data = data;
    }

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

    public String getName() {
        return "Global Median";
    }

}
