// =============================================================================
//
//   NewLexBFS.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * This class implements a version of lexicographic BreadthFirstSearch as
 * suggested by Habib et al. The returned order of the nodes is a perfect
 * elimination order if the graph is an interval graph.
 * 
 * @author struckmeier
 */
public class LexBFS {
    private static final Logger logger = Logger.getLogger(LexBFS.class
            .getName());

    /**
     * Empty Constructor.
     */
    public LexBFS() {
    }

    /**
     * This method is used to call the ordering algorithm
     * 
     * @param g
     * @return a lexicographic order of the nodes of a graph.
     */
    public LexBFSNode[] getOrder(Graph g) {

        List<Node> allNodes = g.getNodes();
        logger.log(Level.FINEST, "Calculating Perfect Elimination Oder ...");
        LexBFSNode[] order = order(allNodes);

        logger.log(Level.FINEST, "returning order of length " + order.length);
        return order;
    }

    /**
     * This method is used to calculate a lexicographic ordering of the nodes of
     * a graph. The order will be a perfect elimination order if the graph is an
     * interval graph.
     * 
     * @param allNodes
     * @return a lexicographic order of the nodes of a graph.
     */
    private LexBFSNode[] order(List<Node> allNodes) {

        HashMap<Node, LexBFSNode> mapping = new HashMap<Node, LexBFSNode>();
        LinkedList<LexBFSNode> set = new LinkedList<LexBFSNode>();
        IntervalSets<LexBFSSet> sets = new IntervalSets<LexBFSSet>();
        for (Node n : allNodes) {
            LexBFSNode lex = new LexBFSNode();
            lex.setNode(n);
            mapping.put(n, lex);
            set.add(lex);
        }
        for (int i = 0; i < set.size() - 1; i++) {
            set.get(i).setNachfolger(set.get(i + 1));
            set.get(i + 1).setVorgaenger(set.get(i));
        }
        LexBFSSet lexClass = new LexBFSSet();
        lexClass.setFirst(set.getFirst());
        lexClass.setLast(set.getLast());
        lexClass.setTimestamp(-1);
        sets.setFirst(lexClass);
        sets.setLast(lexClass);

        for (LexBFSNode n : set) {
            n.setLexBFSClass(lexClass);
        }
        for (Node n : allNodes) {
            for (Node m : n.getNeighbors()) {
                mapping.get(n).addNeighbor(mapping.get(m));
            }
        }

        LexBFSNode[] result = new LexBFSNode[allNodes.size()];
        for (int i = 0; i < allNodes.size(); i++) {
            LexBFSNode current = sets.getLast().getLast();
            current.setDone(true);
            if (sets.getLast().getFirst() != current) {
                current.getVorgaenger().setNachfolger(null);
                sets.getLast().setLast(current.getVorgaenger());
            } else {
                if (sets.getLast() == sets.getFirst()) {
                    // sets.getLast().getVorgaenger().setNachfolger(null);
                } else {
                    sets.getLast().getVorgaenger().setNachfolger(null);
                    sets.setLast(sets.getLast().getVorgaenger());
                }
            }
            current.setNumber(result.length - 1 - i);
            result[result.length - 1 - i] = current;
            for (LexBFSNode l : current.getNeighbors()) {

                if (l.getDone()) {
                    continue;
                }

                if (l.getLexBFSClass().getNachfolger() == null) {
                    LexBFSSet replacement = new LexBFSSet();
                    replacement.setTimestamp(i);
                    replacement.setOldStamp(l.getLexBFSClass().getTimestamp());
                    replacement.setVorgaenger(l.getLexBFSClass());
                    l.getLexBFSClass().setNachfolger(replacement);
                    sets.setLast(replacement);
                }
                if ((i != l.getLexBFSClass().getNachfolger().getTimestamp())) {
                    LexBFSSet replacement = new LexBFSSet();
                    replacement.setTimestamp(i);
                    replacement.setOldStamp(l.getLexBFSClass().getTimestamp());
                    replacement.setVorgaenger(l.getLexBFSClass());
                    replacement.setNachfolger(replacement.getVorgaenger()
                            .getNachfolger());
                    l.getLexBFSClass().setNachfolger(replacement);
                    replacement.getNachfolger().setVorgaenger(replacement);
                } else if (l.getLexBFSClass().getTimestamp() != l
                        .getLexBFSClass().getNachfolger().getOldStamp()) {
                    LexBFSSet replacement = new LexBFSSet();
                    replacement.setTimestamp(i);
                    replacement.setOldStamp(l.getLexBFSClass().getTimestamp());
                    replacement.setVorgaenger(l.getLexBFSClass());
                    replacement.setNachfolger(replacement.getVorgaenger()
                            .getNachfolger());
                    l.getLexBFSClass().setNachfolger(replacement);
                    replacement.getNachfolger().setVorgaenger(replacement);
                }

                if (l.getLexBFSClass().getFirst() == l
                        && l.getLexBFSClass().getLast() != l) {
                    l.getLexBFSClass().setFirst(l.getNachfolger());
                    l.getNachfolger().setVorgaenger(null);
                } else if (l.getLexBFSClass().getFirst() != l
                        && l.getLexBFSClass().getLast() == l) {
                    l.getLexBFSClass().setLast(l.getVorgaenger());
                    l.getVorgaenger().setNachfolger(null);
                } else if (l.getLexBFSClass().getFirst() == l
                        && l.getLexBFSClass().getLast() == l) {
                    if (l.getLexBFSClass() == sets.getFirst()
                            && l.getLexBFSClass() != sets.getLast()) {
                        l.getLexBFSClass().getNachfolger().setVorgaenger(null);
                        sets.setFirst(l.getLexBFSClass().getNachfolger());
                    } else if (l.getLexBFSClass() != sets.getFirst()
                            && l.getLexBFSClass() == sets.getLast()) {
                        l.getLexBFSClass().getVorgaenger().setNachfolger(null);
                        sets.setLast(l.getLexBFSClass().getVorgaenger());
                    } else if (l.getLexBFSClass() == sets.getFirst()
                            && l.getLexBFSClass() == sets.getLast()) {
                        // sets.remove(l.getLexBFSClass());
                    } else {
                        l.getLexBFSClass().getVorgaenger().setNachfolger(
                                l.getLexBFSClass().getNachfolger());
                        l.getLexBFSClass().getNachfolger().setVorgaenger(
                                l.getLexBFSClass().getVorgaenger());
                        // sets.remove(l.getLexBFSClass());
                    }
                } else {
                    l.getVorgaenger().setNachfolger(l.getNachfolger());
                    l.getNachfolger().setVorgaenger(l.getVorgaenger());
                }
                LexBFSSet replacement = l.getLexBFSClass().getNachfolger();

                l.setLexBFSClass(replacement);
                if (replacement.getFirst() == null) {
                    replacement.setFirst(l);
                    l.setVorgaenger(null);
                    l.setNachfolger(null);
                    replacement.setLast(l);
                } else {
                    replacement.getLast().setNachfolger(l);
                    l.setVorgaenger(replacement.getLast());
                    replacement.setLast(l);
                    l.setNachfolger(null);
                }
            }
        }
        return result;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
