// =============================================================================
//
//   BFSTopSort.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BFSTopSort.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.bfstopsort;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.util.GeneralEditorUtils;
import org.graffiti.util.Queue;

/**
 * An implementation of the BFSTopSort algorithm.
 * 
 * @version $Revision: 5766 $
 */
public class BFSTopSort extends AbstractAlgorithm {

    /** DOCUMENT ME! */
    public static final String DEFAULT_ID = "_top_sort_num_";

    /** DOCUMENT ME! */
    private final String deletedId = "_deleted_";

    /** The nodes in the topological order */
    private List<Node> sortedNodeList = new LinkedList<Node>();

    // private String attrPath;

    /** DOCUMENT ME! */
    private String attrId;

    /** DOCUMENT ME! */
    private boolean asLabel;

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "BFSTopSort";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;

        // attrPath = ((StringParameter)params[0]).getString();
        attrId = ((StringParameter) params[0]).getString();
        asLabel = ((BooleanParameter) params[1]).getBoolean().booleanValue();

        if ("".equals(attrId) && !asLabel) {
            attrId = DEFAULT_ID;
        }
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        // Parameter attrPathP = new StringParameter("", "attrPath",
        // "Path to attribute where number will be saved.");
        StringParameter attrIdP = new StringParameter(DEFAULT_ID, "attrId",
                "ID of attribute where number will be saved.");

        BooleanParameter asLabelP = new BooleanParameter(true, "as label",
                "If checked, topsort number will be displayed as label "
                        + "and not as attribute.");

        // return new Parameter[] { attrPathP, attrIdP, asLabelP };
        return new Parameter[] { attrIdP, asLabelP };
    }

    /**
     * Returns a list of the node of the graph in (a) topological order
     * calculated by this algorithm.
     * 
     * @return sorted node list
     */
    public List<Node> getSortedNodeList() {
        return sortedNodeList;
    }

    /**
     * The given graph must have at least one node.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        // nodeNrMap contains a mapping from node to an integer, the topnum
        Map<Node, Integer> nodeNrMap = new HashMap<Node, Integer>();

        int topNum = 0;

        while (!fakeIsEmpty(graph)) {
            // get a source
            Node sourceNode = null;

            for (Iterator<Node> iter = graph.getNodesIterator(); iter.hasNext();) {
                Node node = iter.next();

                if (!fakeIsDeleted(node) && (getFakeInDegree(node) == 0)) {
                    sourceNode = node;

                    break;
                }
            }

            if (sourceNode == null)
                throw new RuntimeException("BFSTopSort failed since it "
                        + "detected a cycle.");

            Queue q = new Queue();
            q.addLast(sourceNode);

            while (!q.isEmpty()) {
                Node v = (Node) q.removeFirst();

                if (sortedNodeList.contains(v)) {
                    // already numbered
                    continue;
                }

                // if (v.getInDegree() == 0) {
                if (getFakeInDegree(v) == 0) {
                    nodeNrMap.put(v, new Integer(topNum++));
                    sortedNodeList.add(v);

                    for (Iterator<Node> neighbours = v
                            .getOutNeighborsIterator(); neighbours.hasNext();) {
                        Node neighbour = neighbours.next();

                        if (!fakeIsDeleted(neighbour)) {
                            q.addLast(neighbour);
                        }
                    }

                    fakeDeleteNode(v);
                }
            }
        }

        graph.getListenerManager().transactionStarted(this);

        if (asLabel) {
            for (Iterator<Map.Entry<Node, Integer>> entryIt = nodeNrMap
                    .entrySet().iterator(); entryIt.hasNext();) {
                Map.Entry<Node, Integer> entry = entryIt.next();

                Node n = entry.getKey();

                GeneralEditorUtils.setLabel(n, entry.getValue().toString());
            }
        } else {
            for (Iterator<Map.Entry<Node, Integer>> entryIt = nodeNrMap
                    .entrySet().iterator(); entryIt.hasNext();) {
                Map.Entry<Node, Integer> entry = entryIt.next();

                Node n = entry.getKey();

                n.setInteger(attrId, entry.getValue());
            }
        }

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        for (Iterator<GraphElement> iter = graph.getGraphElements().iterator(); iter
                .hasNext();) {
            iter.next().removeAttribute(deletedId);
        }

        super.reset();

        sortedNodeList.clear();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param node
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private int getFakeInDegree(Node node) {
        int deg = 0;

        for (Iterator<Edge> iter = node.getDirectedInEdgesIterator(); iter
                .hasNext();) {
            Edge edge = iter.next();

            try {
                if (!edge.getBoolean(deletedId)) {
                    deg++;
                }
            } catch (AttributeNotFoundException anfe) {
                // edge lives
                deg++;
            }
        }

        return deg;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param node
     *            DOCUMENT ME!
     */
    private void fakeDeleteNode(Node node) {
        node.setBoolean(deletedId, true);

        for (Iterator<Edge> iter = node.getEdgesIterator(); iter.hasNext();) {
            Edge edge = iter.next();
            edge.setBoolean(deletedId, true);
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param node
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private boolean fakeIsDeleted(Node node) {
        try {
            if (!node.getBoolean(deletedId))
                return false;
        } catch (AttributeNotFoundException anfe) {
            return false;
        }

        return true;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param g
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private boolean fakeIsEmpty(Graph g) {
        for (Iterator<Node> iter = g.getNodesIterator(); iter.hasNext();) {
            Node node = iter.next();

            try {
                if (!node.getBoolean(deletedId))
                    return false;
            } catch (AttributeNotFoundException anfe) {
                return false;
            }
        }

        return true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
