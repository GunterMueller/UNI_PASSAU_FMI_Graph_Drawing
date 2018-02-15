// =============================================================================
//
//   BFS.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BFS.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.HighDimEmbed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.util.Queue;

//import org.graffiti.event.AbstractAttributeListener;
//import org.graffiti.event.AttributeEvent;

/**
 * An implementation of the BFS algorithm for the high-Embedding Algorithm. (it
 * implements CalculatingAlgortihm to implement getResult())
 * 
 * @version $Revision: 5766 $
 */
public class BFS extends AbstractAlgorithm implements CalculatingAlgorithm {

    /** DOCUMENT ME! */
    Collection<Node> resultNodes = new ArrayList<Node>();

    /** Map that contains the bfs-num as result of class */
    Map<Node, Integer> resultCompNumbers = new HashMap<Node, Integer>();

    /** Map that contains the bfs-num as result of class */
    Map<Node, Integer> resultNumbers;

    /** DOCUMENT ME! */
    private Node sourceNode = null;

    /** DOCUMENT ME! */
    // private Selection selection;

    public BFS() {
    }

    /**
     * Set the new Labels of nodes according to their bfs-num.
     * 
     * @param n
     *            Node to be labeled
     * @param val
     *            String as new node label
     * @param col
     *            DOCUMENT ME!
     */
    public void setLabel(Node n, String val, ColorAttribute col) {
        LabelAttribute labelAttr1 = (LabelAttribute) searchForAttribute(n
                .getAttribute(""), LabelAttribute.class);

        // CoordinateAttribute coordAttr;
        // NodeLabelPositionAttribute labPosAttr = new
        // NodeLabelPositionAttribute(
        // "");
        //
        // coordAttr = (CoordinateAttribute)n
        // .getAttribute(GraphicAttributeConstants.COORD_PATH);

        if (labelAttr1 != null) {
            labelAttr1.setLabel(val);
            labelAttr1.setTextcolor(col);
        } else { // no label found

            NodeLabelAttribute labelAttr = new NodeLabelAttribute("label");

            labelAttr.setLabel(val);
            labelAttr.setTextcolor(col);

            n.addAttribute(labelAttr, "");
        }

        // return labelAttr;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "BFS";
    }

    /**
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        // selection = ((SelectionParameter)params[0]).getSelection();
    }

    /**
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "BFS will start with the only selected node.");

        return new Parameter[] { selParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.CalculatingAlgorithm#getResult()
     */
    public AlgorithmResult getResult() {
        AlgorithmResult aresult = new DefaultAlgorithmResult();

        aresult.addToResult("BfsDistances", this.resultNumbers);
        aresult.addToResult("BfsCompNumbers", this.resultCompNumbers);
        aresult.addToResult("BfsNodes", this.resultNodes);

        return aresult;
    }

    /**
     * Set the source node for bfs to start with.
     * 
     * @param n
     *            Node to be labeled
     */
    public void setSourceNode(Node n) {
        sourceNode = n;
        attach(this.graph);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute() The given graph
     *      must have at least one node.
     */
    public void execute() {
        Map<Node, Integer> bfsNumbers = new HashMap<Node, Integer>();
        Map<Node, Integer> bfsCompNumbers = new HashMap<Node, Integer>();
        Collection<Node> bfsNodes = new ArrayList<Node>();

        if (sourceNode == null)
            throw new RuntimeException("Must call method \"check\" before "
                    + " calling \"execute\".");

        Queue q = new Queue();

        q.addLast(sourceNode);
        bfsNumbers.put(sourceNode, new Integer(0));
        bfsCompNumbers.put(sourceNode, new Integer(0));

        int compNumber = 0;
        bfsNodes.add(sourceNode);

        while (!q.isEmpty()) {
            Node v = (Node) q.removeFirst();

            // mark all neighbours and add all unmarked neighbours
            // of v to the queue
            for (Iterator<?> neighbours = v.getNeighborsIterator(); neighbours
                    .hasNext();) {
                Node neighbour = (Node) neighbours.next();

                if (!bfsNumbers.containsKey(neighbour)) {
                    Integer bfsNum = new Integer(
                            (bfsNumbers.get(v)).intValue() + 1);
                    bfsNumbers.put(neighbour, bfsNum);

                    bfsCompNumbers.put(neighbour, new Integer(++compNumber));

                    bfsNodes.add(neighbour);
                    q.addLast(neighbour);
                }
            }

            resultNumbers = bfsNumbers;
            resultCompNumbers = bfsCompNumbers;
            resultNodes = bfsNodes;
        }

        /**
         * delete label nodes to ensure deletion of ""
         */

        /*
         * for(Iterator nodes = graph.getNodesIterator(); nodes.hasNext();) {
         * Node n = (Node) nodes.next();
         * 
         * 
         * if(bfsNumbers.containsKey(n)) { setLabel(n, "0"); } }
         */
    }

    /**
     * Checks post conditions of bfs
     * 
     * @param tDParams
     *            DOCUMENT ME!
     * @param hEDParams
     *            DOCUMENT ME!
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    public void postCheck(IntegerParameter tDParams, IntegerParameter hEDParams)
            throws RuntimeException {
        int bfsNrNodes = ((Collection<?>) this.getResult().getResult().get(
                "BfsNodes")).size();

        /**
         * restrict maximum high-embedding dimensions by number of nodes
         */
        if ((new Integer(bfsNrNodes).compareTo(hEDParams.getInteger()) < 0))
            throw new RuntimeException(
                    "High-embedding dimensions may not be greater than number of "
                            + "nodes in chosen subgraph."
                            + "\nCurrent maximum is " + bfsNrNodes + ".");

        /**
         * restrict maximum target dimensions by number of nodes
         */
        if (new Integer(bfsNrNodes).compareTo(tDParams.getInteger()) < 0)
            throw new RuntimeException(
                    "Target dimensions may not be greater "
                            + "                    than number of nodes in chosen subgraph.");
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
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
    public Attribute searchForAttribute(Attribute attr, Class<?> attributeType) {
        if (attributeType.isInstance(attr))
            return attr;
        else {
            if (attr instanceof CollectionAttribute) {
                Iterator<?> it = ((CollectionAttribute) attr).getCollection()
                        .values().iterator();

                while (it.hasNext()) {
                    Attribute newAttr = searchForAttribute((Attribute) it
                            .next(), attributeType);

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
