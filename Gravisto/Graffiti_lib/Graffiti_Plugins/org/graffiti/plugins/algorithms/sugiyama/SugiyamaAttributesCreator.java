// =============================================================================
//
//   SugiyamaAttributesCreator.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SugiyamaAttributesCreator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This plugin adds needed attributes to a <tt>Graph</tt> and its <tt>Node</tt>
 * s.<br>
 * On each <tt>Node</tt> the <tt>HashMapAttribute</tt> "sugiyama" is created,
 * which contains the following <tt>Attribute</tt>s:
 * <ul>
 * <li><b>identifier</b>: A unique identifier on each <tt>Node</tt>, that can be
 * used to create constraints
 * <li><b>constraints</b>: A <tt>HashMapAttribute</tt> that can contain a
 * <tt>String</tt> describing a <tt>SugiyamaConstraint</tt>.
 * </ul>
 * If you select <tt>Node</tt>s, the following <tt>Attribute</tt>s can be added
 * to your <tt>Node</tt>-selection:
 * <ul>
 * <li><b>level</b>: The level on which the selected <tt>Node</tt>s should be
 * placed on
 * <li><b>xpos</b>: The relative x-position of the <tt>Node</tt>. If you select
 * multiple <tt>Node</tt>s, each one will get a different xpos in the order of
 * your selection.
 * <li><b>isDummyNode</b>: Marks the selected <tt>Node</tt>s as dummy-nodes
 * </ul>
 * 
 * @author Ferdinand H&uuml;bner
 */
public class SugiyamaAttributesCreator extends AbstractAlgorithm implements
        SugiyamaAlgorithm {
    /** The name of this algorithm */
    private final String ALGORITHM_NAME = "Sugiyama: Create attributes";
    /** Reference to <tt>SugiyamaData</tt> */
    private SugiyamaData data;
    /** Needed to access a selection of nodes */
    private SelectionParameter selectedNodes;
    /** Controls if the xpos-attribute should be added to a selection */
    private boolean buildXpos = false;
    /** is there any offset wanted by the user */
    private int xposOffset = 0;
    /** controls if the level-attribute should be added to a selection */
    private boolean addLevel = false;
    /** on which level should the selected nodes be put */
    private int level = 0;
    /** controls if the dummy-attribute should be added to the selection */
    private boolean addDummyAttribute;
    /** if any errors occured, they get displayed in a message-window */
    private HashSet<String> errors;

    /**
     * Do some basic checks: The graph must not be null and should at least
     * contain one <tt>Node</tt>.
     */
    @Override
    public void check() throws PreconditionException {
        if (this.graph == null)
            throw new PreconditionException(
                    SugiyamaConstants.ERROR_GRAPH_IS_NULL);
        if (this.graph.getNumberOfNodes() <= 0)
            throw new PreconditionException(
                    SugiyamaConstants.ERROR_GRAPH_IS_EMPTY);
    }

    /**
     * Adds attributes to the sugiyama-attribute-tree
     */
    public void execute() {
        this.graph.getListenerManager().transactionStarted(this);

        errors = new HashSet<String>();
        Iterator<Node> nodeIterator = graph.getNodesIterator();
        Node current;
        GraphLabeller labeller = new GraphLabeller(this.graph);

        // Add sugiyama-attributes to each node
        while (nodeIterator.hasNext()) {
            current = nodeIterator.next();
            // make sure, the sugiyama-attribute-tree exists
            try {
                current.getAttribute(SugiyamaConstants.PATH_SUGIYAMA);
            } catch (AttributeNotFoundException anfe) {
                current.addAttribute(new HashMapAttribute(
                        SugiyamaConstants.PATH_SUGIYAMA), "");
            }
            // add constraint-hashmap
            try {
                current.getAttribute(SugiyamaConstants.PATH_CONSTRAINTS);
            } catch (AttributeNotFoundException anfe) {
                current.addAttribute(new HashMapAttribute(
                        SugiyamaConstants.SUBPATH_CONSTRAINTS),
                        SugiyamaConstants.PATH_SUGIYAMA);
            }
        }
        // add sugiyama-label
        labeller.addIdentifiers();

        // add x-pos
        if (buildXpos) {
            addXpos();
        }

        // add level
        if (addLevel) {
            addLevel();
        }

        // add dummy-attribute
        if (addDummyAttribute) {
            addDummyAttribute();
        }

        this.graph.getListenerManager().transactionFinished(this);

        if (errors.size() > 0) {
            String errorMessage = "The following errors occured: \n\n";
            Iterator<String> iter = errors.iterator();
            while (iter.hasNext()) {
                errorMessage += "- " + iter.next() + "\n";
            }
            throw new RuntimeException(errorMessage);
        }
    }

    /**
     * This method adds the <tt>Attribute</tt> "isDummyNode" to a selection of
     * <tt>Node</tt>s and stores them into <tt>SugiyamaData</tt>
     * 
     * @return Returns <tt>true</tt> if all selected nodes have in/out-degree ==
     *         1, <tt>false</tt> otherwise
     */
    private boolean addDummyAttribute() {
        Iterator<Node> iter = graph.getNodesIterator();
        Node current;
        boolean hasErrors = false;
        List<Node> dummies;

        // Return, if there were no nodes selected
        if (selectedNodes == null
                || selectedNodes.getSelection().getNodes().isEmpty())
            return true;

        dummies = selectedNodes.getSelection().getNodes();

        // Check all dummies - They must only have one outgoing and one
        // incoming edge
        iter = dummies.iterator();
        while (iter.hasNext()) {
            current = iter.next();
            if (current.getInDegree() != 1 || current.getOutDegree() != 1) {
                hasErrors = true;
                errors.add("A node marked as dummy does not have "
                        + "in-degree 1 and out-degree 1!");
            } else {
                current.addBoolean(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_DUMMY, true);
                data.getDummyNodes().add(current);
            }
        }

        return !hasErrors;
    }

    /**
     * This encapsulated class adds unique identifiers to each <tt>Node</tt> in
     * the attached <tt>Graph</tt>.
     * 
     * @author Ferdinand H&uuml;bner
     */
    private class GraphLabeller {
        private Graph graph;
        private HashSet<String> labels;
        private int labelNumber;

        private GraphLabeller(Graph graph) {
            this.graph = graph;
            labels = new HashSet<String>();
            labelNumber = 0;
            this.init();
        }

        /**
         * This method adds a unique identifier to each <tt>Node</tt> in the
         * <tt>Graph</tt>.
         */
        private void addIdentifiers() {
            Iterator<Node> nodes = graph.getNodesIterator();
            Node current;

            while (nodes.hasNext()) {
                current = nodes.next();
                try {
                    current.getString(SugiyamaConstants.PATH_LABEL);
                } catch (AttributeNotFoundException anfe) {
                    current.setString(SugiyamaConstants.PATH_LABEL, getLabel());
                }
            }
        }

        /**
         * This method creates a unique label for a <tt>Node</tt>.
         * 
         * @return Returns a unique label for a <tt>Node</tt>.
         */
        private String getLabel() {
            if (!labels.contains(SugiyamaConstants.PREFIX_SUGIYAMA_NODE_LABEL
                    + labelNumber)) {
                labelNumber++;
                return SugiyamaConstants.PREFIX_SUGIYAMA_NODE_LABEL
                        + (labelNumber - 1);
            } else {
                while (labels
                        .contains(SugiyamaConstants.PREFIX_SUGIYAMA_NODE_LABEL
                                + labelNumber)) {
                    labelNumber++;
                }
                labelNumber++;
                return SugiyamaConstants.PREFIX_SUGIYAMA_NODE_LABEL
                        + (labelNumber - 1);
            }
        }

        /**
         * This method initializes the <tt>GraphLabeller</tt>. Each
         * <tt>Node</tt> is searched for a sugiyama-label. If such a label does
         * exist on a node, it must not be used again.
         */
        private void init() {
            Iterator<Node> nodes = graph.getNodesIterator();
            Node current;
            String label;

            while (nodes.hasNext()) {
                current = nodes.next();

                try {
                    label = current.getString(SugiyamaConstants.PATH_LABEL);
                    this.labels.add(label);
                } catch (AttributeNotFoundException anfe) {

                }
            }
        }
    }

    /**
     * Add a level-attribute to a <tt>Selection</tt> of <tt>Node</tt>s.
     */
    private void addLevel() {
        Iterator<Node> iter = selectedNodes.getSelection().getNodes()
                .iterator();
        Node cur;
        while (iter.hasNext()) {
            cur = iter.next();
            try {
                cur.setInteger(SugiyamaConstants.PATH_LEVEL, level);
            } catch (AttributeNotFoundException anfe) {
                cur.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_LEVEL, level);
            }
        }
    }

    /**
     * Add the xpos-attribute to a <tt>Selection</tt> of <tt>Node</tt>s.
     */
    private void addXpos() {
        Iterator<Node> iter = selectedNodes.getSelection().getNodes()
                .iterator();
        Node cur;
        int counter = xposOffset;
        while (iter.hasNext()) {
            cur = iter.next();
            try {
                cur.setDouble(SugiyamaConstants.PATH_XPOS, counter);
                counter++;
            } catch (AttributeNotFoundException anfe) {
                cur.addDouble(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_XPOS, counter);
                counter++;
            }
        }

    }

    /**
     * Returns the name of this algorithm
     * 
     * @return returns the name of the algorithm
     */
    public String getName() {
        return this.ALGORITHM_NAME;
    }

    public void setData(SugiyamaData data) {
        this.data = data;
    }

    public SugiyamaData getData() {
        return this.data;
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        selectedNodes = new SelectionParameter("Nodes", "Selected Nodes");
        BooleanParameter buildXpos = new BooleanParameter(false, "Add x-pos",
                "Add x-pos value to selected Nodes");
        IntegerParameter xposOffset = new IntegerParameter(0, "x-pos offset",
                "Offset of the x-pos (only used when you want to"
                        + " add xpos to selected nodes");
        BooleanParameter addLevel = new BooleanParameter(false,
                "Add level-attribute",
                "Add the level-attribute to the selected" + " nodes");
        IntegerParameter levelParam = new IntegerParameter(0, "Add this level",
                "Add this level to the selected nodes");
        BooleanParameter addDummyAttribute = new BooleanParameter(false,
                "Add dummy-attribute to selected Nodes", "Mark the selected "
                        + "Nodes as dummy-nodes.");
        return new Parameter[] { selectedNodes, buildXpos, xposOffset,
                addLevel, levelParam, addDummyAttribute };
    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        if (params != null && params.length > 0) {
            if (SelectionParameter.class.isAssignableFrom(params[0].getClass())) {
                selectedNodes = (SelectionParameter) params[0];
            }
            if (BooleanParameter.class.isAssignableFrom(params[1].getClass())) {
                buildXpos = ((BooleanParameter) params[1]).getValue();
                if (buildXpos
                        && selectedNodes.getSelection().getNodes().isEmpty()) {
                    buildXpos = false;
                    System.out
                            .println("WARNING: No nodes selected, but "
                                    + "you want x-pos to be added. Cannot do that without"
                                    + " a selection of nodes!");
                }
            }
            if (IntegerParameter.class.isAssignableFrom(params[2].getClass())) {
                xposOffset = ((IntegerParameter) params[2]).getValue();
            }
            if (BooleanParameter.class.isAssignableFrom(params[3].getClass())) {
                addLevel = ((BooleanParameter) params[3]).getValue();
                if (addLevel
                        && selectedNodes.getSelection().getNodes().isEmpty()) {
                    addLevel = false;
                    System.out
                            .println("WARNING: No nodes selected, but you"
                                    + " want a level to be added to the nodes. Cannot do"
                                    + " that without a selection of Nodes.");
                }
            }
            if (IntegerParameter.class.isAssignableFrom(params[4].getClass())) {
                level = ((IntegerParameter) params[4]).getValue();
            }

            if (BooleanParameter.class.isAssignableFrom(params[5].getClass())) {
                addDummyAttribute = ((BooleanParameter) params[5]).getValue();
            }
        }
    }

    public boolean supportsBigNodes() {
        return true;
    }

    public boolean supportsConstraints() {
        return true;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return true;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
