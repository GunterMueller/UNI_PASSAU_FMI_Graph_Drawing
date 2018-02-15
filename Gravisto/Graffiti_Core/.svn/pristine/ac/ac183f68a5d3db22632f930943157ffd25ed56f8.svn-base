// =============================================================================
//
//   DFSTree.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.fas;

import java.util.HashMap;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.SchnyderRealizer.HashList;

/**
 * @author hofmeier
 * @version $Revision$ $Date$
 */
public class DFSTree {

    // private Graph graph;
    // private FASRelatedAlgorithms fra;
    private HashMap<Node, DFSTreeNode> mapping = new HashMap<Node, DFSTreeNode>();
    private DFSTreeNode root;

    public DFSTree(Graph g, FASRelatedAlgorithms f) {
        // this.graph = g;
        // this.fra = f;
    }

    public void addEdge(Edge e) {
        if (this.root == null) {
            this.root = new DFSTreeNode(e.getSource(), null);
            this.mapping.put(e.getSource(), this.root);
        }
        DFSTreeNode father = this.mapping.get(e.getSource());
        DFSTreeNode child = new DFSTreeNode(e.getTarget(), father);
        this.mapping.put(e.getTarget(), child);
        father.addChild(child);
    }

    public LinkedList<Node> getPathToRoot(Node n) {
        LinkedList<Node> path = new LinkedList<Node>();
        path.add(n);
        while (!this.root.getSaved().equals(n)) {
            n = this.mapping.get(n).getFather().getSaved();
            path.add(n);
        }
        return path;
    }

    private class DFSTreeNode {
        private Node saved;
        private DFSTreeNode father;
        private HashList<DFSTreeNode> children = new HashList<DFSTreeNode>();

        public DFSTreeNode(Node s, DFSTreeNode f) {
            this.saved = s;
            this.father = f;
        }

        // public boolean isRoot() {
        // return this.father == null;
        // }

        public Node getSaved() {
            return this.saved;
        }

        public void addChild(DFSTreeNode child) {
            this.children.append(child);
        }

        public DFSTreeNode getFather() {
            return this.father;
        }

    }
}
