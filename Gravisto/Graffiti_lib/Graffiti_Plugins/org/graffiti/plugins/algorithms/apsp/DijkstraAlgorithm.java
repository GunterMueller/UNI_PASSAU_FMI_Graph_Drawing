// =============================================================================
//
//   DijkstraAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DijkstraAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.apsp;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections15.Buffer;
import org.apache.commons.collections15.buffer.PriorityBuffer;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.util.RedundantMultipleIterator;

/**
 * An implementation of the dijkstra algorithm. Preconditions:
 * 
 * <ul>
 * <li>The source node may not be null.</li>
 * <li>The graph may not contain edges with negative weights.</li>
 * <li>The graph instance may not be null.</li>
 * </ul>
 * 
 * Either use the method <code>setSourceNode</code> to define the start node of
 * the algorithm or define an appropriate regular expression in the source
 * parameter, which matches the label of a node in the given graph.
 * 
 * @version $Revision: 5766 $
 */
public class DijkstraAlgorithm extends AbstractAlgorithm {

    /** Whether the user wants to see the algortihms details. */
    private BooleanParameter debugParam;

    /**
     * The source node. Defaults to <code>null</code>. May be set by
     * <code>setSourceNode</code>.
     */
    private Node sourceNode;

    /** The regular expression for the source node. */
    private StringParameter sourceParam;

    /**
     * Constructs a new instance.
     */
    public DijkstraAlgorithm() {
        sourceParam = new StringParameter("S", "source regexp",
                "the regular expression for the source node");
        debugParam = new BooleanParameter(false, "debug", "whether to display "
                + "the algorithm's details during execution.");
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Dijkstra";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return new Parameter[] { sourceParam, debugParam };
    }

    /**
     * Sets the source node to the given value.
     * 
     * @param n
     *            the new source node.
     */
    public void setSourceNode(Node n) {
        sourceNode = n;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (sourceParam.getValue() == null) {
            errors.add("You must select a start node.");
        }

        if (graph == null) {
            errors.add("The graph instance may not be null.");
            throw errors;
        }

        for (Iterator<Edge> i = graph.getEdgesIterator(); i.hasNext();) {
            Edge e = i.next();

            if (getWeight(e) < 0) {
                errors.add("The weight may not be smaller than zero", e);
            }
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    @SuppressWarnings("unchecked")
    public void execute() {
        if (sourceNode == null) {
            sourceNode = findNode(sourceParam.getString());
        }

        if (sourceNode == null)
            throw new RuntimeException("Did not find the source "
                    + "node, which matches the label: "
                    + sourceParam.getString());

        setColor(sourceNode, "graphics.fillcolor", 0, 255, 0, 255);

        Map<Node, PQEntry> result = new HashMap<Node, PQEntry>();
        Buffer pq = new PriorityBuffer();

        // the current node
        Node v = sourceNode;
        PQEntry best = new PQEntry(null, 0.0);

        if (debugParam.getBoolean().equals(Boolean.TRUE)) {
            graph.getListenerManager().transactionStarted(this);

            for (Iterator<Edge> i = graph.getEdgesIterator(); i.hasNext();) {
                setColor(i.next(), "graphics.framecolor", 0, 0, 0, 0);
            }

            for (Iterator<Node> i = graph.getNodesIterator(); i.hasNext();) {
                setWeight(i.next(), "");
            }

            graph.getListenerManager().transactionFinished(this);
        }

        // start a new transaction since the graph elements may be modified
        // by this algorithm.
        graph.getListenerManager().transactionStarted(this);

        while (v != null) {
            if (!result.containsKey(v)) {
                result.put(v, best);

                Iterator<Edge> i = new RedundantMultipleIterator<Edge>(v
                        .getDirectedOutEdgesIterator(), v
                        .getUndirectedEdgesIterator());

                while (i.hasNext()) {
                    Edge e = i.next();

                    if (debugParam.getBoolean().equals(Boolean.TRUE)) {
                        graph.getListenerManager().transactionStarted(this);
                        setColor(e, "graphics.framecolor", 0, 255, 0, 255);
                        graph.getListenerManager().transactionFinished(this);
                    }

                    pq.add(new PQEntry(e, best.getDistance() + getWeight(e)));
                }
            }

            // relax
            if (!pq.isEmpty()) {
                best = (PQEntry) pq.remove();
                v = best.getEdge().getTarget();

                if (result.containsKey(v)) {
                    v = best.getEdge().getSource();
                }
            } else {
                v = null; // finished
            }
        }

        for (Iterator<Node> i = result.keySet().iterator(); i.hasNext();) {
            Node node = i.next();

            if (node != sourceNode) {
                setWeight(node, result.get(node).toString());
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
        sourceParam.setValue("S");
        debugParam.setValue(Boolean.FALSE);
        sourceNode = null;
    }

    /**
     * Sets the color of the given graph element to the given color.
     * 
     * @param e
     *            DOCUMENT ME!
     * @param p
     *            DOCUMENT ME!
     * @param r
     *            DOCUMENT ME!
     * @param g
     *            DOCUMENT ME!
     * @param b
     *            DOCUMENT ME!
     * @param t
     *            DOCUMENT ME!
     */
    private void setColor(GraphElement e, String p, int r, int g, int b, int t) {
        e.setInteger(p + ".transparency", t);
        e.setInteger(p + ".red", r);
        e.setInteger(p + ".green", g);
        e.setInteger(p + ".blue", b);
    }

    /**
     * Sets the label of the given node to the given value. FIXME: which label
     * (which path)?
     * 
     * @param n
     *            DOCUMENT ME!
     * @param val
     *            DOCUMENT ME!
     */
    private void setWeight(Node n, String val) {
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
     * Returns the weight of the given edge. FIXME: create a parameter for this.
     * 
     * @param e
     *            DOCUMENT ME!
     * 
     * @return the weight/cost of the given edge./
     */
    private double getWeight(Edge e) {
        LabelAttribute label = (LabelAttribute) searchForAttribute(e
                .getAttribute(""), LabelAttribute.class);

        double weight = 1.0; // the default

        if (label != null) {
            try {
                weight = Double.parseDouble(label.getLabel());
            } catch (NumberFormatException ignore) {
            }
        }

        return weight;
    }

    /**
     * Returns the node, which matches the specified regexp. May return
     * <code>null</code>.
     * 
     * @param regexp
     *            the regular expression.
     * 
     * @return the node, which matches the specified regexp.
     */
    private Node findNode(String regexp) {
        Node n = null;

        for (Iterator<Node> i = graph.getNodesIterator(); i.hasNext()
                && (n == null);) {
            Node tmp = i.next();
            LabelAttribute a = (LabelAttribute) searchForAttribute(tmp
                    .getAttribute(""), LabelAttribute.class);

            if ((a != null) && (a.getLabel()).matches(regexp)) {
                n = tmp;
            }
        }

        return n;
    }

    /**
     * Searches for the specified attribute and attribute type.
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
