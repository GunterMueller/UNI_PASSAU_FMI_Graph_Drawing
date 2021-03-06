/* Copyright (c) 2003 IPK Gatersleben
 * $Id: RandomEdgeLabelingAlgorithm.java 5772 2010-05-07 18:47:22Z gleissner $
 */

//
// =============================================================================
// $Id: RandomEdgeLabelingAlgorithm.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.algorithms.randomizedlabeling;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.util.GeneralUtils;

/**
 * Add random nummerical labels to edges
 * 
 * @version $Revision: 5772 $
 */
public class RandomEdgeLabelingAlgorithm extends AbstractAlgorithm {

    /**
     * The random number generator.
     */
    private Random random;

    /**
     * DOCUMENT ME!
     */
    private String labelName = "";

    /**
     * DOCUMENT ME!
     */
    private int max = 1000;

    /**
     * DOCUMENT ME!
     */
    private int min = 0;

    /**
     * Constructs a new instance.
     */
    public RandomEdgeLabelingAlgorithm() {
        random = new Random(System.currentTimeMillis());
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Add random nummerical labels to edges";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.labelName = ((StringParameter) params[0]).getString();
        this.min = ((IntegerParameter) params[1]).getValue();
        this.max = ((IntegerParameter) params[2]).getValue();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        StringParameter labelNameParam = new StringParameter(labelName,
                "Label name", "The name of label to be added or set");
        IntegerParameter minParam = new IntegerParameter(min, "Min value",
                "Weights will be at least this value.");
        IntegerParameter maxParam = new IntegerParameter(max, "Max value",
                "Weights will be at most this value.");

        return new Parameter[] { labelNameParam, minParam, maxParam };
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

        // if ((min < 0) || (max < 0))
        // {
        // errors.add("Min or max value may not be negative.");
        // throw errors;
        // }
        if (labelName.compareTo("") == 0) {
            errors.add("The label name field may not be empty.");
            throw errors;
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);

        for (Iterator<Edge> i = graph.getEdgesIterator(); i.hasNext();) {
            Edge e = i.next();

            setLabel(e, labelName, min + random.nextInt(max - min + 1));
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
     * @param name
     *            the name of the label
     * @param value
     *            the new weight
     */
    private void setLabel(Edge e, String name, int value) {
        LabelAttribute labelAttr = null;
        String val = value + "";
        List<Attribute> attributesList = new LinkedList<Attribute>();

        GeneralUtils.searchForAttributes(e.getAttribute(""),
                LabelAttribute.class, attributesList);

        Iterator<Attribute> listIterator = attributesList.iterator();
        boolean found = false;

        while (listIterator.hasNext() && !found) {
            Attribute attr = listIterator.next();

            if ((attr.getId().equals(name))
                    && (attr.getParent().getId().equals(""))) {
                labelAttr = (LabelAttribute) attr;
                found = true;
            }
        }

        if (labelAttr != null) {
            labelAttr.setLabel(val);
        } else { // no label found
            labelAttr = new EdgeLabelAttribute(name);
            labelAttr.setLabel(val);
            e.addAttribute(labelAttr, "");
        }
    }

    // /**
    // * DOCUMENT ME!
    // *
    // * @param e DOCUMENT ME!
    // *
    // * @return DOCUMENT ME!
    // */
    // private double getWeight(Edge e)
    // {
    // LabelAttribute label = (LabelAttribute)
    // searchForAttribute(e.getAttribute(
    // ""), LabelAttribute.class);
    //
    // double weight = Double.MAX_VALUE;
    //
    // if (label != null)
    // {
    // weight = Double.parseDouble(label.getLabel());
    // }
    //
    // return weight;
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
