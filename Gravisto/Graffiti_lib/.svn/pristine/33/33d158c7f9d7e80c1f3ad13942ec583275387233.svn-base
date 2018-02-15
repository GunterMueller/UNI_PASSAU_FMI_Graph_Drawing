// =============================================================================
//
//   ComputeCliques.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.plugin.algorithm.PreconditionException;

/**
 * This class is used to compute the maximal cliques of a graph and store them
 * in a clique tree.
 * 
 * @author struckmeier
 */
public class ComputeCliques {

    private static final Logger logger = Logger.getLogger(ComputeCliques.class
            .getName());

    /**
     * Empty Constructor.
     */
    public ComputeCliques() {

    }

    /**
     * This method is used to calculate the maximal cliques of a graph, given a
     * lexicographic order of the nodes.
     * 
     * @param order
     * @return a representation of all the maximal cliques of the graph.
     * @throws PreconditionException
     */
    public CliqueObject computeCliqueTree(LexBFSNode[] order)
            throws PreconditionException {

        logger.log(Level.FINEST, "method computeCliqueTree called.");

        for (int i = order.length - 1; i >= 0; i--) {
            LexBFSNode current = order[i];
            current.setDone(false);

            if (i < order.length - 1) {
                Iterator<LexBFSNode> it = current.getNeighbors().iterator();
                while (it.hasNext()) {
                    LexBFSNode neighbor = it.next();
                    if (neighbor.getNumber() > current.getNumber()) {
                        current.addRightNeighbor(neighbor);
                    }
                }
                if (!current.getRightNeighbors().isEmpty()) {
                    current
                            .setRightNeighbors(sort(current.getRightNeighbors()));
                    current.setParent(current.getRightNeighbors().getFirst());
                }
            }
        }
        for (int i = 0; i < order.length - 1; i++) {
            PreconditionException errors = new PreconditionException();
            LexBFSNode current = order[i];
            if (!isSubset(current)) {
                errors
                        .add("The graph is not a chordal graph, and therefore no "
                                + "interval graph.");
                throw errors;
            }
        }

        CliqueSet cliqueSet = new CliqueSet();
        CliqueTree cliqueTree = new CliqueTree();
        LinkedList<LexBFSClique> allCliques = new LinkedList<LexBFSClique>();
        int number = 0;
        for (int i = order.length - 2; i >= 0; i--) {
            if (i == (order.length - 2)) {
                LexBFSClique clique = new LexBFSClique();
                clique.addNode(order[order.length - 1]);
                clique.setNumber(number);
                number++;
                order[order.length - 1].setClique(clique);
                cliqueSet.increaseCliqueCount();
                allCliques.addLast(clique);
            }
            LexBFSNode current = order[i];
            if (!compareNeighbors(current)) {
                LexBFSClique clique = new LexBFSClique();
                LinkedList<LexBFSNode> rn = new LinkedList<LexBFSNode>();
                rn = current.getRightNeighbors();
                clique.getNodes().addAll(rn);
                clique.addNode(current);
                clique.setNumber(number);
                number++;
                current.setClique(clique);
                if (current.getParent() != null) {
                    clique.setParent(current.getParent().getClique());
                    clique.getParent().addChild(clique);
                }
                cliqueSet.increaseCliqueCount();
                allCliques.addLast(clique);
            } else {
                current.getParent().getClique().addNode(current);
                current.setClique(current.getParent().getClique());
            }
            if (i == 0) {
                setRoot(cliqueTree, current.getClique());
            }
        }

        for (int i = 0; i < allCliques.size() - 1; i++) {
            allCliques.get(i).setNachfolger(allCliques.get(i + 1));
            allCliques.get(i + 1).setVorgaenger(allCliques.get(i));
        }
        Iterator<LexBFSClique> it = allCliques.iterator();
        while (it.hasNext()) {
            LexBFSClique current = it.next();
            current.setNodes(sort(current.getNodes()));
            LinkedList<LexBFSNode> temp = current.getNodes();
            Iterator<LexBFSNode> nIt = temp.iterator();
            while (nIt.hasNext()) {
                LexBFSNode currentNode = nIt.next();
                current.addContaining(currentNode, true);
            }
        }
        cliqueSet.setFirst(allCliques.getFirst());
        cliqueSet.setLast(allCliques.getLast());
        CliqueObject cliques = new CliqueObject(cliqueTree, cliqueSet);

        cliques.setNodesOrder(order);
        return cliques;
    }

    /**
     * this method determines whether or not the right neighbors of a node and
     * its parent-element are equal.
     * 
     * @param node
     * @return true if the right neighbors are equal.
     */
    private Boolean compareNeighbors(LexBFSNode node) {
        Boolean areEqual = true;
        LexBFSNode parent = node.getParent();
        Iterator<LexBFSNode> current = node.getRightNeighbors().iterator();
        if (parent == null)
            return false;
        current.next();
        Iterator<LexBFSNode> rNParent = parent.getRightNeighbors().iterator();
        if (current.hasNext() || rNParent.hasNext()) {
            while (current.hasNext()) {
                if (rNParent.hasNext()) {
                    if (current.next().getNumber() != rNParent.next()
                            .getNumber()) {
                        areEqual = false;
                        break;
                    }
                } else {
                    areEqual = false;
                    break;
                }
            }
            if (rNParent.hasNext()) {
                areEqual = false;
            }
        }
        return areEqual;
    }

    /**
     * This method is used to determine whether or not the right neighbors of a
     * node are a subset of the right neighbors of its parent node.
     * 
     * @param node
     * @return true if the right neighbors are a subset.
     */
    private Boolean isSubset(LexBFSNode node) {
        Boolean isSubset = true;
        LexBFSNode parent = node.getParent();
        Iterator<LexBFSNode> current = node.getRightNeighbors().iterator();
        Iterator<LexBFSNode> rNParent = null;

        if (!(parent == null)) {
            rNParent = parent.getRightNeighbors().iterator();
        } else
            return true;
        current.next();
        int i = 1;
        if (current.hasNext()) {
            i++;
            while (current.hasNext()) {
                LexBFSNode currentNode = current.next();
                if (rNParent.hasNext()) {
                    LexBFSNode parentNode = rNParent.next();
                    if (currentNode.getNumber() < parentNode.getNumber()) {
                        isSubset = false;
                        break;
                    } else if (currentNode.getNumber() > parentNode.getNumber()) {
                        while (rNParent.hasNext()) {
                            if (currentNode.getNumber() == parentNode
                                    .getNumber()) {
                                break;
                            } else if (currentNode.getNumber() < parentNode
                                    .getNumber()) {
                                isSubset = false;
                                break;
                            } else if (!rNParent.hasNext()) {
                                isSubset = false;
                                break;
                            } else {
                                rNParent.next();
                            }
                        }
                    }
                } else {
                    isSubset = false;
                    break;
                }
                if (!isSubset) {
                    break;
                }
            }
        }
        return isSubset;
    }

    /**
     * This method determines the root of the clique-tree.
     * 
     * @param tree
     * @param clique
     */
    private void setRoot(CliqueTree tree, LexBFSClique clique) {
        if (clique.getParent() == null) {
            tree.setRootElement(clique);
        } else {
            setRoot(tree, clique.getParent());
        }
    }

    /**
     * this method sorts the list of right neighbors of a node usind an
     * implementation of radix-sort.
     * 
     * @param rightNeighbors
     * @return the list of right neighbors in sorted ascendeing order.
     */
    private LinkedList<LexBFSNode> sort(LinkedList<LexBFSNode> rightNeighbors) {
        if (rightNeighbors.size() < 2) {
            logger.log(Level.FINEST, "already sorted");
            return rightNeighbors;
        } else {
            logger.log(Level.FINEST, "must be sorted");
            LexBFSNode[] neighbors = new LexBFSNode[rightNeighbors.size()];
            Iterator<LexBFSNode> it = rightNeighbors.iterator();
            int count = 0;
            int digits = 0;
            int max = 0;
            while (it.hasNext()) {
                neighbors[count] = it.next();
                if (neighbors[count].getNumber() > max) {
                    digits = (int) Math.log10(neighbors[count].getNumber()) + 1;
                    max = digits;
                }
                count++;
            }
            rightNeighbors.clear();
            RadixSort sort = new RadixSort();
            LexBFSNode[] neighbors2 = new LexBFSNode[neighbors.length];
            neighbors2 = sort.Radix(neighbors, digits);

            for (int i = neighbors2.length - 1; i >= 0; i--) {
                rightNeighbors.addFirst(neighbors2[i]);
            }
            return rightNeighbors;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
