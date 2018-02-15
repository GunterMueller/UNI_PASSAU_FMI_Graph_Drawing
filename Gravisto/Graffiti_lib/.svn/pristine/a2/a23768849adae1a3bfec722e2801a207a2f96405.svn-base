// =============================================================================
//
//   GlobalBarycenter.java
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
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.plugins.algorithms.sugiyama.util.XPosComparator;

/**
 * <p>
 * This class implements the barycenter heuristic for crossing minimization by
 * using <code>CrossMinObjects</code>.
 * </p>
 * <p>
 * The barycenter of a <code>CrossMinObject</code> is defined as the sum of the
 * barycenter values of the object's upper and lower neighbors divided through
 * the degree of the object.
 * </p>
 * 
 */
public class GlobalBarycenter extends AbstractAlgorithm implements
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

        OBJECTS_SIZE = this.objects.size();

        for (int sweep = 0; sweep < sweeps; sweep++) {
            for (int i = 0; i < OBJECTS_SIZE; i++) {
                computeBarycenter(objects.get(i));
            }
            Collections.sort(objects, new CrossMinObjectBarycenterComparator());
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

    /**
     * Computes the barycenter for the object
     * 
     * @param object
     *            the object
     */
    private void computeBarycenter(CrossMinObject object) {
        int degree = object.inNeighbors.elementCount
                + object.outNeighbors.elementCount;

        if (degree == 0) {
            object.barycenter = 0;
            return;
        }
        double barycenter = 0;
        for (int i = 0; i < object.inNeighbors.elementCount; i++) {
            barycenter += object.inNeighbors.get(i).xPos;
        }
        for (int i = 0; i < object.outNeighbors.elementCount; i++) {
            barycenter += object.outNeighbors.get(i).xPos;
        }
        object.barycenter = (barycenter / degree);
    }

    /**
     * This class implements a simple comparator to compare two objects by their
     * barycenter
     */
    private class CrossMinObjectBarycenterComparator implements
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
        return "Global Barycenter";
    }

}
