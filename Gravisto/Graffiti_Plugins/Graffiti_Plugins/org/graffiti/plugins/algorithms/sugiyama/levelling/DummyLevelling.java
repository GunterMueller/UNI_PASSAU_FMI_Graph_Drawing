// =============================================================================
//
//   DummyLevelling.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DummyLevelling.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.levelling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class is a dummy-implementation. It doesn't modify the graph, it just
 * checks, if the following is true:
 * <ul>
 * <li>Each node has an <tt>IntegerAttribute</tt> <tt>sugiyama.level</tt>
 * <li>The <tt>IntegerAttribute</tt> <tt>sugiyama.level</tt> is not negative
 * <li>No level is empty
 * <li><tt>Edge</tt>s do not point upwards in the graph (e.g. from level i+1 to
 * level i)
 * <li><tt>Edge</tt>s do not point to the same level
 * </ul>
 * Additionally, it creates the data-structure <tt>NodeLayers</tt> according to
 * the <tt>Attribute</tt>s found in the graph.
 * 
 * @author Ferdinand H&uuml;bner, Raymund F&uuml;l&ouml;p
 */
public class DummyLevelling extends AbstractCyclicLevelingAlgorithm implements
        LevellingAlgorithm {
    private final String ALGORITHM_NAME = "Dummy-Levelling (Only checks for valid node-attributes)";

    @Override
    protected void levelNodes() {
        graph.getListenerManager().transactionStarted(this);

        HashSet<String> messages = new HashSet<String>();
        boolean errors = false;
        HashMap<Integer, Integer> levels = new HashMap<Integer, Integer>();
        int maxLevel = 0;
        boolean levelAttributeNotFound = false;
        boolean negativeLevel = false;
        boolean same = false;
        boolean upwards = false;
        HashSet<Edge> longEdges = new HashSet<Edge>();

        // check for level-attribute on each node
        Iterator<Node> nodeIter = graph.getNodesIterator();
        Node cur;
        int currentLevel;
        while (nodeIter.hasNext()) {
            cur = nodeIter.next();

            try {
                if (cur.getBoolean(SugiyamaConstants.PATH_DUMMY)) {
                    data.getDummyNodes().add(cur);
                }
            } catch (AttributeNotFoundException anfe) {
                // that's okay, the node just is no dummy node
            }
            try {
                currentLevel = cur.getInteger(SugiyamaConstants.PATH_LEVEL);
                if (currentLevel > numberOfLevels - 1) {
                    numberOfLevels = currentLevel + 1;
                }
                if (currentLevel < 0) {
                    if (!negativeLevel) {
                        negativeLevel = true;
                        messages.add("The attribute "
                                + SugiyamaConstants.PATH_LEVEL
                                + " must not be negative.");
                        errors = true;
                        continue;
                    }
                }
                if (levels.containsKey(new Integer(currentLevel))) {
                    Integer tmp = levels.remove(new Integer(currentLevel));
                    levels.put(new Integer(currentLevel), new Integer(tmp
                            .intValue() + 1));
                } else {
                    levels.put(new Integer(currentLevel), new Integer(1));
                }
                if (currentLevel > maxLevel) {
                    maxLevel = currentLevel;
                }
            } catch (AttributeNotFoundException anfe) {
                errors = true;
                if (!levelAttributeNotFound) {
                    messages.add("The attribute "
                            + SugiyamaConstants.PATH_LEVEL
                            + " was not found on at least one node.");
                    levelAttributeNotFound = true;
                }
            }
        }
        // check for edges that connect more than one level
        Iterator<Edge> edges = graph.getEdgesIterator();
        Edge tmp;
        int level1, level2;
        while (edges.hasNext()) {
            tmp = edges.next();
            level1 = tmp.getSource().getInteger(SugiyamaConstants.PATH_LEVEL);
            level2 = tmp.getTarget().getInteger(SugiyamaConstants.PATH_LEVEL);
            if (level2 < level1) {
                if (level2 == 0 && level1 == maxLevel) {
                    continue;
                }

                if (!upwards
                        && !data.getAlgorithmType().equals(
                                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)) {
                    errors = true;
                    messages
                            .add("Edges must not point to an upper level in the"
                                    + " graph.");
                    upwards = true;
                }
            } else if (level2 == level1) {
                if (!same) {
                    errors = true;
                    messages
                            .add("Edges must not point to a node on the same level.");
                    same = true;
                }
            } else if (level2 - level1 > 1) {
                longEdges.add(tmp);
            }
        }
        // not necessary - superclass takes care of that:
        // addDummies(longEdges);
        if (errors) {
            String message = "Cannot proceed due to the following errors:\n\n";
            Iterator<String> iter = messages.iterator();
            while (iter.hasNext()) {
                message += "- " + iter.next() + "\n";
            }
            throw new RuntimeException(message);
        }

        graph.getListenerManager().transactionFinished(this);
    }

    // /**
    // * This method adds dummy-nodes to the graph, so that no <tt>Edge</tt>
    // spans
    // * over more than one level.
    // * @param edges A <tt>HashSet</tt> of <tt>Edge</tt>s, that contains all
    // * the "long" edges.
    // */
    // private void addDummies(HashSet<Edge> edges)
    // {
    // Iterator<Edge> iter = edges.iterator();
    // Edge current;
    // Node originalSource;
    // Node originalTarget;
    // Node target;
    // int levelSource;
    // int levelTarget;
    // Edge lastEdge;
    //
    // while (iter.hasNext())
    // {
    // current = iter.next();
    //            
    // originalSource = current.getSource();
    // originalTarget = current.getTarget();
    // levelSource = originalSource.getInteger(SugiyamaConstants.PATH_LEVEL);
    // levelTarget = originalTarget.getInteger(SugiyamaConstants.PATH_LEVEL);
    //            
    // lastEdge = current;
    // Node oldTarget;
    //            
    // for (int i = 1; i < (levelTarget - levelSource); i++)
    // {
    // target = graph.addNode();
    // target.addAttribute(new HashMapAttribute(
    // SugiyamaConstants.PATH_SUGIYAMA), "");
    // target.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
    // SugiyamaConstants.SUBPATH_LEVEL, i + levelSource);
    // AbstractCyclicLevelingAlgorithm.setDummyShape(target);
    // data.getLayers().getLayer(i + levelSource).add(target);
    // data.getDummyNodes().add(target);
    // oldTarget = lastEdge.getTarget();
    // lastEdge.setTarget(target);
    // lastEdge = graph.addEdge(target, oldTarget, true);
    // }
    // }
    //
    //        
    // }

    public String getName() {
        return this.ALGORITHM_NAME;
    }

    @Override
    public void setData(SugiyamaData data) {
        this.data = data;
    }

    @Override
    public SugiyamaData getData() {
        return this.data;
    }

    public boolean supportsBigNodes() {
        return false;
    }

    public boolean supportsConstraints() {
        return false;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)
                || algorithmType
                        .equals(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
