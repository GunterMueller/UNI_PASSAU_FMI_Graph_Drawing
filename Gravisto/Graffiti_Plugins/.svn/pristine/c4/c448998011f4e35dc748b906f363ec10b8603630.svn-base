/**
 * 
 */
package org.graffiti.plugins.algorithms.graviso;

import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.text.AttributeSet.CharacterAttribute;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.ByteAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.FloatAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.LongAttribute;
import org.graffiti.attributes.ShortAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.algorithm.PreconditionException.Entry;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;

/**
 * This class implements many steps necessary for refinement algorithms taking
 * labels into account. This is for example the functionality to compare the
 * labels of the graphs for a matching.
 * 
 * 
 * @author lenhardt
 * 
 */
public abstract class AbstractRefinementAlgorithmLabels extends
        AbstractRefinementAlgorithm {

    /**
     * The position of the parameters in the parameter array.
     */
    protected static final int P_REGARD_NODE_LABELS = 3;
    protected static final int P_NODE_LABEL_PATH = 4;
    protected static final int P_REGARD_EDGE_LABELS = 5;
    protected static final int P_EDGE_LABEL_PATH = 6;

    /** the total number of parameters */
    protected static int P_NUM_TOTAL = 7;

    /** stores the distinct labels of the graph 1 and 2's nodes and edges */
    protected Hashtable<Integer, Integer> nodeLabelClasses1;
    protected Hashtable<Integer, Integer> nodeLabelClasses2;
    protected Hashtable<Integer, Integer> edgeLabelClasses1;
    protected Hashtable<Integer, Integer> edgeLabelClasses2;

    /**
     * The constructor takes care of the parameters passed to the algorithm
     */
    public AbstractRefinementAlgorithmLabels() {
        super();

        // set up parameters
        BooleanParameter regardDirections = new BooleanParameter(true,
                "regard Direction",
                "check if you want to take edges' direction into account");

        BooleanParameter useBFSInfo = new BooleanParameter(true,
                "use BFS to speed up computation",
                "check if you want to use BFS to calculate result");
        String[] visOpts = { "no animation", "final animation", "step-by-step" };

        StringSelectionParameter visualize = new StringSelectionParameter(
                visOpts, "show node colors after calculation",
                "check if you want the nodes to be colored after calculation");

        parameters = new Parameter[P_NUM_TOTAL];
        parameters[P_VISUALIZE] = visualize;
        parameters[P_REGARD_DIRECTIONS] = regardDirections;
        parameters[P_USE_BFS_INFO] = useBFSInfo;

        BooleanParameter regardEdgeLabels = new BooleanParameter(false,
                "regard edge-labels",
                "check if you want to take edges' labels into account");

        BooleanParameter regardNodeLabels = new BooleanParameter(false,
                "regard node-labels",
                "check if you want to take nodes' labels into account");

        StringParameter nodeLabelPath = new StringParameter("", "node label",
                "which node label should be used for computation");
        StringParameter edgeLabelPath = new StringParameter("", "edge label",
                "which edge label should be used for computation");

        parameters[P_REGARD_NODE_LABELS] = regardNodeLabels;
        parameters[P_NODE_LABEL_PATH] = nodeLabelPath;
        parameters[P_REGARD_EDGE_LABELS] = regardEdgeLabels;
        parameters[P_EDGE_LABEL_PATH] = edgeLabelPath;

    }

    /**
     * Checks, whether the graphs in question might actually be isomorphic. This
     * is done by comparing their labels, in a way similar to the comparison of
     * a degree sequence.
     * 
     * @param g1
     * @param g2
     * @return false, if the graphs are not isomorphic, true if the possibly
     *         could be isomorphic
     */
    protected boolean canBeIsomorphic(Graph g1, Graph g2) {
        if (!super.canBeIsmorphic(g1, g2))
            return false;

        if (((BooleanParameter) parameters[P_REGARD_NODE_LABELS]).getBoolean()) {
            Hashtable<Integer, Integer> labelClasses1 = new Hashtable<Integer, Integer>();
            Hashtable<Integer, Integer> labelClasses2 = new Hashtable<Integer, Integer>();

            // if we have to look at the node labels too, let's see how many
            // distinct labels we have in each graph
            String path = ((StringParameter) parameters[P_NODE_LABEL_PATH])
                    .getString();

            int i = 0;
            for (Node n : g1.getNodes()) {
                n.setInteger("node number", i);
                Attribute a = n.getAttribute(path);
                // while we're already here, let's store the attribute in an
                // array for easier access later:
                // nodeAttributes1[i] = a;
                Object val = a.getValue();
                // from Java API: val1.equals(val2) =>
                // val1.hashCode==val2.hashCode
                int key = val.hashCode();
                int num = labelClasses1.remove(key);
                num++;
                labelClasses1.put(key, num);
                i++;
            }

            // exactly the same for g2:
            i = 0;
            for (Node n : g2.getNodes()) {
                n.setInteger("node number", i);
                Attribute a = n.getAttribute(path);
                // nodeAttributes2[i] = a;
                Object val = a.getValue();
                int key = val.hashCode();
                int num = labelClasses2.remove(key);
                num++;
                labelClasses2.put(key, num);
                i++;
            }

            if (labelClasses1.size() != labelClasses2.size()) {
                result = "The Graphs are not Isomorphic: The number of distinct Node labels is different";
                return false;
            }
            this.nodeLabelClasses1 = labelClasses1;
            this.nodeLabelClasses2 = labelClasses2;
        }

        // exactly the same for edges:
        if (((BooleanParameter) parameters[P_REGARD_EDGE_LABELS]).getBoolean()) {
            Hashtable<Integer, Integer> labelClasses1 = new Hashtable<Integer, Integer>();
            Hashtable<Integer, Integer> labelClasses2 = new Hashtable<Integer, Integer>();

            // if we have to look at the node labels too, let's see how many
            // distinct labels we have in each graph
            String path = ((StringParameter) parameters[P_NODE_LABEL_PATH])
                    .getString();
            for (Edge n : g1.getEdges()) {
                Attribute a = n.getAttribute(path);
                // we use the time to store the attribute in a matrix for easier
                // access later
                // edgeAttributes1[n.getSource().getInteger("node number")][n
                // .getTarget().getInteger("node nuber")] = a;
                Object val = a.getValue();
                int key = val.hashCode();
                int num = labelClasses1.remove(key);
                num++;
                labelClasses1.put(key, num);
            }

            // exactly the same for g2:
            for (Edge n : g2.getEdges()) {
                Attribute a = n.getAttribute(path);
                // edgeAttributes2[n.getSource().getInteger("node number")][n
                // .getTarget().getInteger("node nuber")] = a;
                Object val = a.getValue();
                int key = val.hashCode();
                int num = labelClasses2.remove(key);
                num++;
                labelClasses2.put(key, num);
            }

            if (labelClasses1.size() != labelClasses2.size()) {
                result = "The Graphs are not Isomorphic: The number of distinct Edge labels is different";
                return false;
            }
            this.edgeLabelClasses1 = labelClasses1;
            this.edgeLabelClasses2 = labelClasses2;
        }

        // all tests passed, graphs might be iso:
        return true;
    }

    @Override
    public void reset() {
        super.reset();

        BooleanParameter regardDirections = new BooleanParameter(true,
                "regard Direction",
                "check if you want to take edges' direction into account");

        BooleanParameter useBFSInfo = new BooleanParameter(true,
                "use BFS to speed up computation",
                "check if you want to use BFS to calculate result");
        String[] visOpts = { "no animation", "final animation", "step-by-step" };

        StringSelectionParameter visualize = new StringSelectionParameter(
                visOpts, "show node colors after calculation",
                "check if you want the nodes to be colored after calculation");

        parameters = new Parameter[P_NUM_TOTAL];
        parameters[P_VISUALIZE] = visualize;
        parameters[P_REGARD_DIRECTIONS] = regardDirections;
        parameters[P_USE_BFS_INFO] = useBFSInfo;

        BooleanParameter regardEdgeLabels = new BooleanParameter(false,
                "regard edge-labels",
                "check if you want to take edges' labels into account");
        BooleanParameter regardNodeLabels = new BooleanParameter(false,
                "regard node-labels",
                "check if you want to take nodes' labels into account");
        StringParameter nodeLabelPath = new StringParameter("", "node label",
                "which node label should be used for computation");
        StringParameter edgeLabelPath = new StringParameter("", "edge label",
                "which edge label should be used for computation");

        parameters[P_REGARD_NODE_LABELS] = regardNodeLabels;
        parameters[P_NODE_LABEL_PATH] = nodeLabelPath;
        parameters[P_REGARD_EDGE_LABELS] = regardEdgeLabels;
        parameters[P_EDGE_LABEL_PATH] = edgeLabelPath;

    }

    @Override
    public void check() throws PreconditionException {

        PreconditionException errors = new PreconditionException();

        try {
            super.check();
        } catch (PreconditionException e) {
            e.iterator();
            for (Iterator<Entry> it = e.iterator(); it.hasNext();) {
                Entry ent = it.next();
                errors.add(ent.cause);
            }
        }

        // check if the graphs' attributes we should examine are supported
        if (((BooleanParameter) parameters[P_REGARD_NODE_LABELS]).getBoolean()) {
            Graph[] gs = { g1, g2 };
            for (Graph g : gs) {
                for (Iterator<Node> iterator = g.getNodesIterator(); iterator
                        .hasNext();) {
                    Node n = iterator.next();
                    try {
                        Attribute a = n
                                .getAttribute(((StringParameter) parameters[P_NODE_LABEL_PATH])
                                        .getString());
                        if (a instanceof BooleanAttribute
                                || a instanceof StringAttribute
                                || a instanceof CharacterAttribute
                                || a instanceof IntegerAttribute
                                || a instanceof ByteAttribute
                                || a instanceof LongAttribute
                                || a instanceof ShortAttribute
                                || a instanceof DoubleAttribute
                                || a instanceof FloatAttribute
                                || a instanceof NodeLabelAttribute) {
                            continue;
                        } else {
                            errors
                                    .add("The type of the Node Label you specified is not supported");
                            break;
                        }
                    } catch (AttributeNotFoundException e) {
                        errors
                                .add("The path you specified for the Node Label does not exist on all nodes");
                        break;
                    }
                }
            }
        }

        // check if the graphs' edge labels we should examine are supported:
        if (((BooleanParameter) parameters[P_REGARD_EDGE_LABELS]).getBoolean()) {
            Graph[] gs = { g1, g2 };
            for (Graph g : gs) {
                for (Iterator<Edge> iterator = g.getEdgesIterator(); iterator
                        .hasNext();) {
                    Edge e = iterator.next();
                    try {
                        Attribute a = e
                                .getAttribute(((StringParameter) parameters[P_NODE_LABEL_PATH])
                                        .getString());
                        if (a instanceof BooleanAttribute
                                || a instanceof StringAttribute
                                || a instanceof CharacterAttribute
                                || a instanceof IntegerAttribute
                                || a instanceof ByteAttribute
                                || a instanceof LongAttribute
                                || a instanceof ShortAttribute
                                || a instanceof DoubleAttribute
                                || a instanceof FloatAttribute) {
                            continue;
                        } else {
                            errors
                                    .add("The type of the Edge Label you specified is not supported");
                            break;
                        }
                    } catch (AttributeNotFoundException ex) {
                        errors
                                .add("The path you specified for the Edge Label does not exist on all Edges");
                        break;
                    }
                }
            }
        }
        if (!errors.isEmpty())
            throw errors;

    }
}
