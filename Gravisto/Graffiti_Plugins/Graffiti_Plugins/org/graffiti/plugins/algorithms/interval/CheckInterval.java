// =============================================================================
//
//   CheckInterval.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

/**
 * Class implements last step of the interval recognition algorithm by Habib et
 * al.
 * 
 * @author struckmeier
 */
public class CheckInterval {

    private HashMap<LexBFSNode, Boolean> alreadyDone = new HashMap<LexBFSNode, Boolean>();

    /**
     * Empty Constructor
     */
    public CheckInterval() {

    }

    /**
     * This method orders the cliques in a chain if the graph is an interval
     * graph.
     * 
     * @param cliques
     * @return a chain of intervals
     */
    public IntervalSets<CliqueSet> createCliqueSequence(CliqueObject cliques) {
        CliqueSet cliqueSet = cliques.getSet();
        IntervalSets<CliqueSet> set = new IntervalSets<CliqueSet>();
        set.setFirst(cliqueSet);
        set.setLast(cliqueSet);

        Stack<LexBFSNode> pivots = new Stack<LexBFSNode>();
        Boolean intervalGraph = true;
        LinkedList<LexBFSClique> checkCliques = new LinkedList<LexBFSClique>();
        int counter = 1;
        while (!checkSingleton(set)) {
            counter++;
            if (pivots.empty()) {
                CliqueSet current = new CliqueSet();
                Boolean getLastClique = true;
                // get last non-singleton CliqueSet
                while (getLastClique) {
                    CliqueSet temp = set.getLast();
                    while (temp != null) {
                        if (!(temp.getFirst() == temp.getLast())) {
                            getLastClique = false;
                            current = temp;
                            break;
                        }
                        temp = temp.getPre();
                    }
                }
                LexBFSClique clique = current.getLast();
                if (current.getFirst() != clique) {
                    current.setLast(clique.getVorgaenger());
                    clique.getVorgaenger().setNachfolger(null);

                    CliqueSet newSet = new CliqueSet();
                    newSet.setFirst(clique);
                    newSet.setLast(clique);
                    newSet.setPre(current);
                    if (current == set.getLast()) {
                        set.setLast(newSet);
                    } else {
                        current.getPost().setPre(newSet);
                        newSet.setPost(current.getPost());
                    }
                    current.setPost(newSet);
                }

                checkCliques.add(clique);
                clique.setDone(true);
            } else {
                LexBFSNode current = pivots.pop();
                current.setDone(true);
                alreadyDone.put(current, true);

                CliqueSet firstClass = set.getFirst();
                Boolean firstNotFound = true;
                while (firstNotFound) {
                    LexBFSClique cliqueInFirstClass = firstClass.getFirst();
                    Boolean firstCliqueNotFound = true;
                    while (firstCliqueNotFound) {
                        if (cliqueInFirstClass.getContaining().containsKey(
                                current)) {
                            firstCliqueNotFound = false;
                            firstNotFound = false;
                        } else {
                            if (cliqueInFirstClass.getNachfolger() != null) {
                                cliqueInFirstClass = cliqueInFirstClass
                                        .getNachfolger();
                            } else {
                                break;
                            }
                        }
                    }
                    if (firstNotFound) {
                        firstClass = firstClass.getPost();
                    }
                }

                CliqueSet lastClass = set.getLast();
                Boolean lastNotFound = true;
                int count3 = 1;
                while (lastNotFound) {
                    count3++;
                    LexBFSClique cliqueInLastClass = lastClass.getFirst();
                    Boolean lastCliqueNotFound = true;
                    int count4 = 1;
                    while (lastCliqueNotFound) {
                        count4++;
                        HashMap<LexBFSNode, Boolean> map = cliqueInLastClass
                                .getContaining();
                        Boolean containing = map.containsKey(current);
                        if (containing) {
                            lastCliqueNotFound = false;
                            lastNotFound = false;
                        } else {
                            if (cliqueInLastClass.getNachfolger() != null) {
                                cliqueInLastClass = cliqueInLastClass
                                        .getNachfolger();
                            } else {
                                break;
                            }
                        }
                    }
                    if (lastNotFound) {
                        lastClass = lastClass.getPre();
                    }
                }

                // firstClass aufteilen
                CliqueSet firstSplit = new CliqueSet();
                firstSplit.setCliqueCount(0);
                Boolean emptySet = false;
                LexBFSClique firstFoundClique = firstClass.getFirst();
                while (firstFoundClique != null) {
                    if (firstFoundClique.getContaining().containsKey(current)) {
                        firstSplit
                                .setCliqueCount(firstSplit.getCliqueCount() + 1);

                        // altes Seet leeren
                        if (firstFoundClique == firstClass.getFirst()
                                && firstFoundClique == firstClass.getLast()) {
                            firstClass.setFirst(null);
                            firstClass.setLast(null);
                            emptySet = true;
                        } else if (firstFoundClique == firstClass.getFirst()) {
                            firstClass.getFirst().getNachfolger()
                                    .setVorgaenger(null);
                            firstClass.setFirst(firstClass.getFirst()
                                    .getNachfolger());
                        } else if (firstFoundClique == firstClass.getLast()) {
                            firstClass.getLast().getVorgaenger().setNachfolger(
                                    null);
                            firstClass.setLast(firstClass.getLast()
                                    .getVorgaenger());
                        } else {
                            firstFoundClique.getVorgaenger().setNachfolger(
                                    firstFoundClique.getNachfolger());
                            firstFoundClique.getNachfolger().setVorgaenger(
                                    firstFoundClique.getVorgaenger());
                        }

                        // neues Set bef�llen
                        if (firstSplit.getFirst() == null) {
                            firstFoundClique.setVorgaenger(null);
                            firstSplit.setFirst(firstFoundClique);
                            firstSplit.setLast(firstFoundClique);
                        } else {
                            firstSplit.getLast()
                                    .setNachfolger(firstFoundClique);
                            firstFoundClique
                                    .setVorgaenger(firstSplit.getLast());
                            firstSplit.setLast(firstFoundClique);
                        }
                    }
                    firstFoundClique = firstFoundClique.getNachfolger();
                }
                // erst neues Set einf�gen
                if (firstClass == set.getLast()) {
                    set.setLast(firstSplit);
                } else {
                    firstClass.getPost().setPre(firstSplit);
                    firstSplit.setPost(firstClass.getPost());
                }
                firstClass.setPost(firstSplit);
                firstSplit.setPre(firstClass);

                // falls altes Set leer, entfernen
                if (emptySet) {
                    // hat auf alle f�lle einen Nachfolger
                    // also entweder ist es das Erste oder mittendrin
                    if (firstClass == set.getFirst()) {
                        set.setFirst(firstSplit);
                        firstSplit.setPre(null);
                    } else {
                        firstSplit.setPre(firstClass.getPre());
                        firstClass.getPre().setPost(firstSplit);
                    }
                }
                if (firstSplit.getFirst() == firstSplit.getLast()) {
                    checkCliques.add(firstSplit.getFirst());
                    firstSplit.getFirst().setDone(true);
                }

                // falls ungleich, lastClass auch aufteilen
                if (firstClass != lastClass) {
                    // lastClass kann nicht first sein
                    CliqueSet lastSplit = new CliqueSet();
                    lastSplit.setCliqueCount(0);
                    emptySet = false;
                    LexBFSClique lastFoundClique = lastClass.getFirst();
                    int count6 = 1;
                    while (lastFoundClique != null) {
                        count6++;
                        if (lastFoundClique.getContaining()
                                .containsKey(current)) {
                            lastSplit
                                    .setCliqueCount(lastSplit.getCliqueCount() + 1);

                            // altes Seet leeren
                            if (lastFoundClique == lastClass.getFirst()
                                    && lastFoundClique == lastClass.getLast()) {
                                lastClass.setFirst(null);
                                lastClass.setLast(null);
                                lastFoundClique.setNachfolger(null);
                                emptySet = true;
                            } else if (lastFoundClique == lastClass.getFirst()) {
                                lastClass.getFirst().getNachfolger()
                                        .setVorgaenger(null);
                                lastClass.setFirst(lastClass.getFirst()
                                        .getNachfolger());
                            } else if (lastFoundClique == lastClass.getLast()) {
                                lastClass.getLast().getVorgaenger()
                                        .setNachfolger(null);
                                lastClass.setLast(lastClass.getLast()
                                        .getVorgaenger());
                            } else {
                                LexBFSClique lastTemp = lastFoundClique
                                        .getNachfolger();
                                lastFoundClique.getVorgaenger().setNachfolger(
                                        lastTemp);
                                lastTemp.setVorgaenger(lastFoundClique
                                        .getVorgaenger());
                            }

                            // neues Set bef�llen
                            if (lastSplit.getFirst() == null) {
                                lastSplit.setFirst(lastFoundClique);
                                lastSplit.setLast(lastFoundClique);
                                lastFoundClique.setVorgaenger(null);
                            } else {
                                lastSplit.getLast().setNachfolger(
                                        lastFoundClique);
                                lastFoundClique.setVorgaenger(lastSplit
                                        .getLast());
                                lastSplit.setLast(lastFoundClique);
                            }
                        }
                        lastFoundClique = lastFoundClique.getNachfolger();
                    }

                    // erst neues Set einf�gen, kann nicht first werden, kriegt
                    // nachfolger, also mittendrin
                    lastSplit.setPre(lastClass.getPre());
                    lastSplit.getPre().setPost(lastSplit);
                    lastSplit.setPost(lastClass);
                    lastClass.setPre(lastSplit);

                    // falls altes Set leer, entfernen
                    if (emptySet) {
                        // hat auf alle f�lle einen vorg�nger, also last oder
                        // mittendrin
                        if (lastClass == set.getLast()) {
                            set.setLast(lastSplit);
                            lastSplit.setPost(null);
                        } else {
                            lastSplit.setPost(lastClass.getPost());
                            lastClass.getPost().setPre(lastSplit);
                        }
                    }
                }
            }

            if (!checkCliques.isEmpty()) {
                LexBFSClique check = checkCliques.removeFirst();
                if (check.getParent() != null) {
                    pivots.addAll(addToPivots(check, check.getParent()));
                    check.getParent().removeChild(check);
                    check.setParent(null);
                }
                Iterator<LexBFSClique> it = check.getChildren().iterator();
                while (it.hasNext()) {
                    LexBFSClique child = it.next();
                    pivots.addAll(addToPivots(check, child));
                    child.setParent(null);
                    it.remove();
                }
            }
        }
        CliqueSet first = set.getFirst();
        LexBFSNode[] nodes = cliques.getNodesOrder();
        int numberOfNodes = nodes.length - 1;
        for (int i = 0; i <= numberOfNodes; i++) {
            first = set.getFirst();
            LexBFSNode node = nodes[i];
            Boolean firstOccurence = false;
            Boolean firstSequenceInterrupted = false;
            while (first != null) {
                if (first.getFirst().getContaining().containsKey(node)) {
                    if (!firstOccurence) {
                        firstOccurence = true;
                    } else if (firstOccurence && firstSequenceInterrupted) {
                        intervalGraph = false;
                    }
                } else if (firstOccurence) {
                    firstSequenceInterrupted = true;
                }
                first = first.getPost();
            }
        }
        first = set.getFirst();
        while (first != null) {
            first = first.getPost();
        }
        set.setIsInterval(intervalGraph);
        return set;
    }

    /**
     * This method is used to determine which elements of the cliques should be
     * added to the list of pivot-elements.
     * 
     * @param clique
     * @param connectedClique
     * @return a stack of pivot-elements that is to be added to the actual stack
     *         of pivot-elements
     */
    private Stack<LexBFSNode> addToPivots(LexBFSClique clique,
            LexBFSClique connectedClique) {
        Stack<LexBFSNode> pivots = new Stack<LexBFSNode>();
        LinkedList<LexBFSNode> current = clique.getNodes();
        LinkedList<LexBFSNode> parent = connectedClique.getNodes();
        Iterator<LexBFSNode> it = current.iterator();
        Iterator<LexBFSNode> pIt = parent.iterator();

        Boolean moreNodes = true;
        LexBFSNode first = new LexBFSNode();
        LexBFSNode last = new LexBFSNode();
        if (it.hasNext()) {
            first = it.next();
        } else {
            moreNodes = false;
        }
        if (pIt.hasNext()) {
            last = pIt.next();
        } else {
            moreNodes = false;
        }
        while (moreNodes) {
            if ((first.getNumber() == last.getNumber())) {
                first.setDone(true);

                if (!alreadyDone.containsKey(first)) {
                    pivots.add(first);
                }
                if (it.hasNext()) {
                    first = it.next();
                } else {
                    moreNodes = false;
                }
                if (pIt.hasNext()) {
                    last = pIt.next();
                } else {
                    moreNodes = false;
                }
            } else if (first.getNumber() > last.getNumber()) {
                if (pIt.hasNext()) {
                    last = pIt.next();
                } else {
                    moreNodes = false;
                }
            } else if (first.getNumber() < last.getNumber()) {
                if (it.hasNext()) {
                    first = it.next();
                } else {
                    moreNodes = false;
                }
            }
        }
        return pivots;
    }

    /**
     * This method is used to determine whether or not all container classes are
     * singleton.
     * 
     * @param set
     * @return true if every container class contains exactly one element.
     */
    private Boolean checkSingleton(IntervalSets<CliqueSet> set) {
        Boolean isSingleton = true;
        CliqueSet cliques = set.getFirst();
        while (cliques != null) {
            if (cliques.getFirst() != cliques.getLast()) {
                isSingleton = false;
                LexBFSClique firstTemp = cliques.getFirst();
                while (firstTemp != null) {
                    firstTemp = firstTemp.getNachfolger();
                }
                break;
            }
            cliques = cliques.getPost();
        }
        return isSingleton;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
