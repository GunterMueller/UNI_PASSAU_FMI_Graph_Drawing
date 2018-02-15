// =============================================================================
//
//   RandomEdgeWeightsAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RandomEdgeWeightsAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.apsp;

import java.util.Iterator;
import java.util.Random;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

/**
 * Adds random (non negative) weights to all edges of the given graph.
 * 
 * @version $Revision: 5766 $
 */
public class RandomEdgeWeightsAlgorithm extends AbstractAlgorithm {

    /** The random number generator. */
    private Random random;

    /** DOCUMENT ME! */
    private int max = 1000;

    /** DOCUMENT ME! */
    private int min = 0;

    /**
     * Constructs a new instance.
     */
    public RandomEdgeWeightsAlgorithm() {
        random = new Random(System.currentTimeMillis());
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Add random (non negative) weights to all edges";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.min = ((IntegerParameter) params[0]).getInteger().intValue();
        this.max = ((IntegerParameter) params[1]).getInteger().intValue();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter minParam = new IntegerParameter(min, "Min value",
                "Weights will be at least this value.");
        IntegerParameter maxParam = new IntegerParameter(max, "Max value",
                "Weights will be at most this value.");

        return new Parameter[] { minParam, maxParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (graph == null) {
            errors.add("The graph instance may not be null.");
            throw errors;
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);

        for (Iterator<Edge> i = graph.getEdgesIterator(); i.hasNext();) {
            setWeight(i.next(), min + random.nextInt(max - min + 1));
        }

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset() Resets the random
     *      seed.
     */
    @Override
    public void reset() {
        graph = null;
        random = new Random(42);
    }

    /**
     * Adds the given weight to the given edge.
     * 
     * @param e
     *            the edge
     * @param weight
     *            the new weight.
     */
    private void setWeight(Edge e, int weight) {
        String val = weight + "";
        LabelAttribute labelAttr = (LabelAttribute) searchForAttribute(e
                .getAttribute(""), LabelAttribute.class);

        if (labelAttr != null) {
            labelAttr.setLabel(val);
        } else { // no label found
            labelAttr = new EdgeLabelAttribute("weight");
            labelAttr.setLabel(val);
            e.addAttribute(labelAttr, "");
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param attr
     *            DOCUMENT ME!
     * @param attributeType
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private Attribute searchForAttribute(Attribute attr,
            Class<? extends Attribute> attributeType) {
        if (attributeType.isInstance(attr))
            return attr;
        else {
            if (attr instanceof CollectionAttribute) {
                Iterator<Attribute> it = ((CollectionAttribute) attr)
                        .getCollection().values().iterator();

                while (it.hasNext()) {
                    Attribute newAttr = searchForAttribute(it.next(),
                            attributeType);

                    if (newAttr != null)
                        return newAttr;
                }
            } else if (attr instanceof CompositeAttribute)
                // TODO: treat those correctly; some of those have not yet
                // been correctly implemented
                return null;
        }

        return null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
