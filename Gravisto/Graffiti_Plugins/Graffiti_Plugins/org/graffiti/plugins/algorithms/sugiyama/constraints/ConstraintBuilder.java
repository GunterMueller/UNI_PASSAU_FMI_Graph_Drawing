// =============================================================================
//
//   ConstraintBuilder.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ConstraintBuilder.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.constraints;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class provides methods to create constraint-objects from attributes
 * saved in the <tt>Node</tt>s of a <tt>Graph</tt>.
 * 
 * @author Ferdinand H�bner
 */
public class ConstraintBuilder {
    /**
     * This HashMap is used to map an identifier-string to an actual Node in the
     * graph.
     */
    private HashMap<String, Node> nodeMap;
    /** The attached graph */
    private Graph graph;
    /** The constraints are saved in this bean */
    private SugiyamaData data;

    /**
     * Default constructor of a <tt>ConstraintBuilder</tt>. Needs a
     * <tt>Graph</tt> that contains the <tt>Node</tt>s, and a
     * <tt>SugiyamaData</tt>-bean.
     * 
     * @param graph
     *            The <tt>Graph</tt>, that contains the <tt>Node</tt>s
     * @param data
     *            The <tt>SugiyamaData</tt>-Bean, than can store the constraints
     *            and a <tt>HashMap</tt> to map a <tt>String</tt>-identifier to
     *            a <tt>Node</tt>.
     */
    public ConstraintBuilder(Graph graph, SugiyamaData data) {
        this.graph = graph;
        this.data = data;
        nodeMap = new HashMap<String, Node>();
        createNodeMap();
        data.setNodeMap(this.nodeMap);
    }

    /**
     * This method creates <tt>SugiyamaConstraint</tt>-Objects from a
     * <tt>StringAttribute</tt> stored in a <tt>Node</tt>'s <tt>Attribute</tt>
     * -tree.
     * 
     * For each <tt>Node</tt> in the <tt>Graph</tt>, this method tries to access
     * the Constraint-HashMap and extract the <tt>String</tt>s that could
     * represent a <tt>SugiyamaConstraint</tt>.
     */
    public void buildConstraints() {
        HashSet<SugiyamaConstraint> constraints = new HashSet<SugiyamaConstraint>();
        SugiyamaConstraint sConstraint;
        String constraint;
        Iterator<Node> nodeIterator = graph.getNodesIterator();
        Node current;
        Map<String, Attribute> attributeMap;
        Iterator<Map.Entry<String, Attribute>> entrySetIterator;

        while (nodeIterator.hasNext()) {
            current = nodeIterator.next();
            // access the constraint-hashmap in each node, get its underlying
            // collection and extract the constraint-strings in the collection
            try {
                attributeMap = ((HashMapAttribute) current
                        .getAttribute(SugiyamaConstants.PATH_CONSTRAINTS))
                        .getCollection();

                entrySetIterator = attributeMap.entrySet().iterator();
                while (entrySetIterator.hasNext()) {
                    constraint = ((StringAttribute) entrySetIterator.next()
                            .getValue()).getString();
                    sConstraint = getConstraint(constraint, current);
                    if (sConstraint != null) {
                        System.out.println("Added new constraint");
                        constraints.add(sConstraint);
                    }
                }
            } catch (AttributeNotFoundException anfe) {
                // don't do anything
            }
        }
        data.setConstraints(constraints);
    }

    /**
     * This method passes a <tt>String</tt>, that was extracted from a
     * <tt>Node</tt>'s <tt>Attribute</tt>-tree to various implementations of the
     * interface <tt>SugiyamaConstraint</tt>. The implementing classes have to
     * implement a method <tt>isConstraint</tt>, which returns a new
     * <tt>SugiyamaConstraint</tt>-object of the implementing type, if the
     * <tt>String</tt> represents such a constraint. It returns <tt>null</tt>
     * otherwise.
     * 
     * @param stringConstraint
     *            The <tt>String</tt> that was extracted from the <tt>Node</tt>
     *            and that should represent a <tt>SugiyamaConstraint</tt>.
     * @param source
     *            The <tt>Node</tt> that acts as a source for the
     *            <tt>SugiyamaConstraint</tt>.
     * @return Returns a new <tt>SugiyamaConstraint</tt> (actual implementing
     *         class of the interface varies depending on the <tt>String</tt> -
     *         see the implementing classes for formatting-policy of the
     *         <tt>String</tt>s.) or <tt>null</tt>, if the <tt>String</tt> is
     *         malformed or does not describe a <tt>SugiyamaConstraint</tt>.
     */
    private SugiyamaConstraint getConstraint(String stringConstraint,
            Node source) {
        SugiyamaConstraint constraint;
        SugiyamaConstraint newConstraint;

        Iterator<SugiyamaConstraint> iter = data.getConstraints().iterator();
        while (iter.hasNext()) {
            constraint = iter.next();
            if ((newConstraint = constraint.isConstraint(stringConstraint,
                    source, data)) != null)
                return newConstraint;
        }
        return null;

    }

    /**
     * Maps each <tt>Node</tt> in the <tt>Graph</tt> to its unique identifier in
     * the sugiyama-<tt>HashMapAttribute</tt>.
     */
    private void createNodeMap() {
        Iterator<Node> nodeIterator = graph.getNodesIterator();
        Node current;
        String key;

        while (nodeIterator.hasNext()) {
            current = nodeIterator.next();
            try {
                key = current.getString(SugiyamaConstants.PATH_LABEL);
                nodeMap.put(key, current);
            } catch (AttributeNotFoundException anfe) {

            }
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
