// =============================================================================
//
//   UnDirDijkstraAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: UnDirDijkstraAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.springembedderrestricted;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugins.algorithms.apsp.PQEntry;
import org.graffiti.selection.Selection;

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
public class UnDirDijkstraAlgorithm extends AbstractAlgorithm implements
        CalculatingAlgorithm {

    /** DOCUMENT ME! */
    private Map<Node, PQEntry> result = new HashMap<Node, PQEntry>();

    /**
     * The source node. Defaults to <code>null</code>. May be set by
     * <code>setSourceNode</code>.
     */
    private Node sourceNode;

    /** DOCUMENT ME! */
    private Selection selection;

    /** The regular expression for the source node. */
    private StringParameter sourceParam;

    /** DOCUMENT ME! */
    private boolean onlyResult = false;

    /**
     * Constructs a new instance.
     */
    public UnDirDijkstraAlgorithm() {
        sourceParam = new StringParameter("S", "source regexp",
                "the regular expression for the source node");
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Dijkstra";
    }

    /**
     * Sets the selection on which the algorithm works.
     * 
     * @param onlyResult
     *            the selection
     */
    public void setOnlyResult(boolean onlyResult) {
        this.onlyResult = onlyResult;
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        sourceParam = (StringParameter) this.parameters[0];
        selection = ((SelectionParameter) this.parameters[1]).getSelection();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Selection",
                "The selection to work on");

        return new Parameter[] { sourceParam, selParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.CalculatingAlgorithm#getResult()
     */
    public AlgorithmResult getResult() {
        AlgorithmResult aresult = new DefaultAlgorithmResult();
        aresult.addToResult("visitedNodes", this.result.keySet());
        aresult.addToResult("weightsMap", this.result);

        return aresult;
    }

    /**
     * Get the result map in a type safe way. Only useful after calling execute.
     * 
     * @return Result of calcuation.
     */
    public Map<Node, PQEntry> getWeightsMap() {
        return result;
    }

    /**
     * Sets the selection on which the algorithm works.
     * 
     * @param selection
     *            the selection
     */
    public void setSelection(Selection selection) {
        this.selection = selection;
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
    public void execute() {
        if (sourceNode == null) {
            sourceNode = findNode(sourceParam.getString());
        }

        if (sourceNode == null)
            throw new RuntimeException("Did not find the source "
                    + "node with label: " + sourceParam.getString());

        List<Node> nodes = selection.getNodes();
        List<Edge> edges = selection.getEdges();

        if (!nodes.contains(sourceNode))
            return;

        if (!this.onlyResult) {
            setColor(sourceNode, "graphics.fillcolor", 0, 255, 0, 255);
        }

        Buffer<PQEntry> pq = new PriorityBuffer<PQEntry>();

        // the current node
        Node v = sourceNode;
        PQEntry best = new PQEntry(null, 0.0);

        if (!this.onlyResult) {
            graph.getListenerManager().transactionStarted(this);
        }

        while (v != null) {
            if (!result.containsKey(v)) {
                result.put(v, best);

                // for (Iterator i = new MultipleIterator
                // (v.getDirectedOutEdgesIterator(),
                // v.getUndirectedEdgesIterator()); i.hasNext();){
                for (Iterator<Edge> i = v.getEdgesIterator(); i.hasNext();) {
                    Edge e = i.next();

                    if (edges.contains(e) && nodes.contains(e.getTarget())
                            && nodes.contains(e.getSource())) {
                        // setColor(e, "graphics.framecolor", 0, 255, 0, 255);
                        pq
                                .add(new PQEntry(e, best.getDistance()
                                        + getWeight(e)));
                    }
                }
            }

            // relax
            if (!pq.isEmpty()) {
                best = pq.remove();
                v = best.getEdge().getTarget();

                if (result.containsKey(v)) {
                    v = best.getEdge().getSource();
                }
            } else {
                v = null; // finished
            }
        }

        if (!onlyResult) {
            for (Iterator<Node> i = result.keySet().iterator(); i.hasNext();) {
                Node node = i.next();

                if (node != sourceNode) {
                    setWeight(node, result.get(node).toString());
                }
            }
        }

        if (!this.onlyResult) {
            graph.getListenerManager().transactionFinished(this);
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
        sourceParam.setValue("S");
        this.result = new HashMap<Node, PQEntry>();
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
            weight = Double.parseDouble(label.getLabel());
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
