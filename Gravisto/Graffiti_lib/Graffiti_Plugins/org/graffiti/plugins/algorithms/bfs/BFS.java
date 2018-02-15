// =============================================================================
//
//   BFS.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BFS.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.bfs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;
import org.graffiti.util.Queue;

/**
 * An implementation of the BFS algorithm.
 * 
 * @version $Revision: 5766 $
 */
public class BFS extends AbstractAlgorithm {
    /** DOCUMENT ME! */
    private Node sourceNode = null;

    /** DOCUMENT ME! */
    private Selection selection;

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "BFS";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Start node",
                "BFS will start with the only selected node.");

        return new Parameter[] { selParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    "The graph is empty. Cannot run BFS.");

        if ((selection == null) || (selection.getNodes().size() != 1))
            throw new PreconditionException(
                    "BFS needs exactly one selected node.");

        sourceNode = selection.getNodes().get(0);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute() The given graph
     *      must have at least one node.
     */
    public void execute() {
        if (sourceNode == null)
            throw new RuntimeException("Must call method \"check\" before "
                    + " calling \"execute\".");

        Queue q = new Queue();

        // d contains a mapping from node to an integer, the bfsnum
        Map<Node, Integer> d = new HashMap<Node, Integer>();

        q.addLast(sourceNode);
        d.put(sourceNode, new Integer(0));

        while (!q.isEmpty()) {
            Node v = (Node) q.removeFirst();

            // mark all neighbours and add all unmarked neighbours
            // of v to the queue
            for (Iterator<Node> neighbours = v.getNeighborsIterator(); neighbours
                    .hasNext();) {
                Node neighbour = neighbours.next();

                if (!d.containsKey(neighbour)) {
                    Integer bfsNum = new Integer(d.get(v) + 1);
                    d.put(neighbour, bfsNum);
                    q.addLast(neighbour);
                }
            }
        }

        graph.getListenerManager().transactionStarted(this);

        for (Iterator<Node> nodes = graph.getNodesIterator(); nodes.hasNext();) {
            Node n = nodes.next();

            if (d.containsKey(n)) {
                setLabel(n, d.get(n) + "");
            }
        }

        graph.getListenerManager().transactionFinished(this);
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
     * @param n
     *            DOCUMENT ME!
     * @param val
     *            DOCUMENT ME!
     */
    private void setLabel(Node n, String val) {
        LabelAttribute labelAttr = (LabelAttribute) searchForAttribute(n
                .getAttribute(""), LabelAttribute.class);

        if (labelAttr != null) {
            labelAttr.setLabel(val);
        } else { // no label found
            labelAttr = new NodeLabelAttribute("label");
            labelAttr.setLabel(val);
            n.addAttribute(labelAttr, "");
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
