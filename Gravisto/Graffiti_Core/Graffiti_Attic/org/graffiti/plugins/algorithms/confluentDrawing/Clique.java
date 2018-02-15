package org.graffiti.plugins.algorithms.confluentDrawing;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.graffiti.graph.Node;

public class Clique {

    /** the bipartition of biclique */
    private Set clique;

    private HashMap graphMap;

    /** the size of biclique */
    private int size;

    private Set edges;

    /**
     * Creates a new Biclique object.
     * 
     * @param length
     *            the size of biclique
     */
    public Clique(HashMap map) {
        this.clique = new HashSet();
        this.graphMap = map;
        this.edges = new HashSet();
    }

    /**
     * add a single node into the bipartition
     * 
     * @param x
     *            The bipartition_X to set.
     */
    public void setClique(Node x) {
        this.clique.add(x);
    }

    /**
     * get the clique
     * 
     */
    public Set getClique() {
        return this.clique;
    }

    /**
     * remove a single node into the bipartition
     * 
     * @param x
     *            The bipartition_X to set.
     */
    public void remove(Node x) {
        this.clique.remove(x);
    }

    /**
     * return the size of biclique
     * 
     * @return the size of biclique
     */
    public int size() {
        return this.clique.size();
    }

    @Override
    public Object clone() {
        Clique res = new Clique(this.graphMap);
        Iterator itr = this.clique.iterator();
        while (itr.hasNext()) {
            res.setClique((Node) itr.next());
        }
        return res;
    }

    /**
     * to string
     * 
     * @return string value of biclique
     */
    @Override
    public String toString() {
        String res = "";
        Iterator itr1 = this.clique.iterator();

        while (itr1.hasNext()) {
            res += (itr1.next().toString() + "\t");
        }
        return res;
    }

    /**
     * @param clique2
     */
    public void setCliquen(Set clique2) {
        this.clique = clique2;
    }

    /**
     * return edges in the clique.
     * 
     * @return Returns edges in the clique.
     */
    public Collection getEdges() {
        /*
         * this.edges = new ArrayList();
         * 
         * while (graphEdgeItr.hasNext()) { Edge edge =
         * (Edge)graphEdgeItr.next(); if (this.clique.contains(edge.getSource())
         * && this.clique.contains(edge.getTarget())) { this.edges.add(edge); }
         * }
         */
        this.edges = new HashSet();
        Iterator cItr = this.clique.iterator();
        while (cItr.hasNext()) {
            Node node1 = (Node) cItr.next();
            Iterator cItr1 = this.clique.iterator();
            while (cItr1.hasNext()) {
                Node node2 = (Node) cItr1.next();
                if (this.graphMap.containsKey(node1)
                        && ((HashMap) this.graphMap.get(node1))
                                .containsKey(node2)) {
                    // System.out.println(((HashMap)this.graphMap.get(node1)).get(node2));
                    this.edges.add(((HashMap) this.graphMap.get(node1))
                            .get(node2));

                }
                // if(this.graphMap.containsKey(node2) &&
                // ((HashMap)this.graphMap.get(node2)).containsKey(node1)){
                // this.edges.add(((HashMap)this.graphMap.get(node2)).get(node1));
                // }
            }

        }
        return this.edges;
    }

    public int getNrOfEdges() {
        return this.edges.size();
    }

    /**
     * @return
     */
    public boolean valid() {
        int edgeNr = 0;
        Iterator itr = this.clique.iterator();
        while (itr.hasNext()) {
            Node vertex = (Node) itr.next();
            Iterator itr1 = this.clique.iterator();
            while (itr1.hasNext()) {
                Node vertex2 = (Node) itr1.next();
                if ((vertex != vertex2)
                        && (!isNeighborhood(vertex, vertex2) || !isNeighborhood(
                                vertex2, vertex)))
                    // edgeNr++;
                    return false;
                // this.edges.add(((HashMap)this.graphMap.get(vertex)).get(vertex2));
            }
        }
        // System.out.println("\t\t edge of clique is: " + edgeNr + "\t" +
        // (this.clique.size() * (this.clique.size() - 1) / 2));
        // return edgeNr == (this.clique.size() * (this.clique.size() - 1)) / 2;
        return true;
    }

    public boolean isNeighborhood(Node node, Node node2) {
        // Collection neigbors = node.getNeighbors();
        // HashSet ngHash = new HashSet(node.getNeighbors());
        // return ngHash.contains(node2);
        return (this.graphMap.containsKey(node) && ((HashMap) this.graphMap
                .get(node)).containsKey(node2));
    }
}
