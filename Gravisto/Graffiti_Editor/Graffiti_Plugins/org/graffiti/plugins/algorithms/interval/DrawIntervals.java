// =============================================================================
//
//   DrawIntervals.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.interval;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.graph.FastGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.session.Session;

/**
 * This class is used to compute and draw an interval representation of a graph,
 * given the maximal cliques as a clique-chain.
 * 
 * @author struckmeier
 */
public class DrawIntervals {
    /**
     * Empty Constructor.
     */
    public DrawIntervals() {

    }

    /**
     * This method is used to calculate the intervals. It uses the clique-chain
     * to determine where to place the intervals in the generated graph. If two
     * nodes are in the same clique, there corresponding intervals will overlap.
     * 
     * @param cliques
     * @param oldGraph
     */
    public void drawIntervals(IntervalSets<CliqueSet> cliques, Graph oldGraph) {
        int maxHeight = 0;
        int numberOfcliques = 0;
        HashMap<Node, Integer> nodeMap = new HashMap<Node, Integer>();
        HashMap<Node, Integer> xMap = new HashMap<Node, Integer>();
        LinkedList<LexBFSNode> allNodes = new LinkedList<LexBFSNode>();
        CliqueSet temp = cliques.getFirst();
        while (temp != null) {
            numberOfcliques++;
            temp = temp.getPost();
        }

        LexBFSNode[] currentNodes = new LexBFSNode[cliques.getNumberOfNodes()];

        CliqueSet currentSet = cliques.getFirst();
        LexBFSClique currentClique = currentSet.getFirst();
        for (int i = 0; i < numberOfcliques; i++) {
            if (i != 0) {
                currentSet = currentSet.getPost();
                currentClique = currentSet.getFirst();
                // abgearbeitete Knoten aus dem Array entfernen
                for (int j = 0; j < maxHeight; j++) {
                    if (!(currentClique.getContaining()
                            .containsKey(currentNodes[j]))
                            && (currentNodes[j] != null)) {
                        currentNodes[j] = null;
                    }
                }
            }
            LinkedList<LexBFSNode> currentLexBFSNodes = currentClique
                    .getNodes();
            Iterator<LexBFSNode> nIt = currentLexBFSNodes.iterator();
            int size = 0;
            while (nIt.hasNext()) {
                LexBFSNode lexNode = nIt.next();
                size++;
                if (!nodeMap.containsKey(lexNode.getNode())) {
                    int j = 0;
                    Boolean notYetDone = true;
                    while (notYetDone) {
                        if (currentNodes[j] == null) {
                            if (maxHeight < (j + 1)) {
                                maxHeight = (j + 1);
                            }
                            currentNodes[j] = lexNode;
                            nodeMap.put(lexNode.getNode(), (j + 1));
                            xMap.put(lexNode.getNode(), (i + 1));
                            allNodes.add(lexNode);
                            lexNode.setLeftEnd(i);
                            lexNode.setRightEnd(i + 1);
                            notYetDone = false;
                        } else {
                            j++;
                        }
                    }
                } else {
                    lexNode.setRightEnd(lexNode.getRightEnd() + 1);
                }
            }
        }

        Graph newGraph = null;
        try {
            MainFrame mf = GraffitiSingleton.getInstance().getMainFrame();
            Session s = mf.addNewSession();
            newGraph = s.getGraph();
        } catch (Exception e) {
            newGraph = new FastGraph();
        }

        Iterator<LexBFSNode> it = allNodes.iterator();
        LinkedList<Node> newNodes = new LinkedList<Node>();
        Node[] nodes = new Node[oldGraph.getNumberOfNodes()];

        int n = 0;
        while (it.hasNext()) {
            LexBFSNode lexNode = it.next();
            Node oldNode = lexNode.getNode();
            CollectionAttribute col = (CollectionAttribute) oldNode
                    .getAttributes().copy();
            Node newNode = newGraph.addNode(col);
            int yPos = nodeMap.get(oldNode);
            int xPos = xMap.get(oldNode);
            double width = lexNode.getRightEnd() - lexNode.getLeftEnd();
            newNode.setDouble("graphics.coordinate.y", yPos * 50.0);
            newNode.setDouble("graphics.coordinate.x",
                    (xPos * 75 + (width + xPos) * 50) / 2);
            newNode.setDouble("graphics.dimension.width", width * 50);

            newNodes.add(newNode);
            nodes[n] = newNode;
            n++;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
