// =============================================================================
//
//   BabaiKucera.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.isomorphism;

import java.awt.Color;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Implements the algorithm for testing graph isomorphism from Laszlo Babai and
 * Ludik Kucera (Canonical Labelling of Graphs in Linear Average Time, 1979).
 * 
 * @author mary-k
 * @version $Revision$ $Date$
 */
public class BabaiKucera extends AbstractIsomorphism {
    // contains all nodes
    private NodeWithClassID[] allNodes;

    // contains nodes of the set U (the nodes that don't have a unique labelling
    // after application of procedureA)
    private NodeWithClassID[] notSingletons;

    // contains all nodes
    private ClassifiedNode[] allNodesUndirected;

    // contains nodes of the set U (the nodes that don't have a unique labelling
    // after application of procedureA)
    private ClassifiedNode[] notSingletonsUndirected;

    // the cardinality of U
    private int cardU;

    // assigns an index to elements of U
    private int[] indices;

    // contains for each equivalence class (with more than one element) the
    // number of elements
    private int[] faculties;

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        if ((Boolean) parameters[0].getValue()) {
            if (g1.getNumberOfNodes() != g2.getNumberOfNodes()) {
                result = "The graphs are not isomorphic!\nThey have a different number of nodes.";
                return;
            }
            if (g1.getNumberOfEdges() != g2.getNumberOfEdges()) {
                result = "The graphs are not isomorphic!\nThey have a different number of edges.";
                return;
            }
            // execute procedure A on both graphs
            HashMap<Node, int[]> h1 = procedureAUndirected(g1);
            HashMap<Node, int[]> h2 = procedureAUndirected(g2);

            // sort and compare classes of procedure A
            int[][] a1 = h1.values().toArray(new int[g1.getNodes().size()][]);
            IntArrayComparator comp = new IntArrayComparator();
            Arrays.sort(a1, comp);
            int[][] a2 = h2.values().toArray(new int[g2.getNodes().size()][]);
            // comp = new IntArrayComparator();
            Arrays.sort(a2, comp);

            if (!comp.equals(a1, a2)) {
                result = "The graphs are not isomorphic.";
                return;
            }
            g1.getListenerManager().transactionStarted(this);
            g2.getListenerManager().transactionStarted(this);
            setNotSingletonsUndirected(g1, h1);

            Boolean b = true;
            if (cardU >= 2 && g1.getNumberOfNodes() <= 36) {
                b = computeRunTime();
            }

            if (cardU < 2) {
                // nur zum F�rben
                setNotSingletonsUndirected(g2, h2);
                result = "The graphs are isomorphic.";
            }
            // nicht-Singletons im Ergebnis: wenn < log n/log log n weiter mit
            // Procedure B
            else if (cardU < Math.log(g1.getNumberOfNodes())
                    / Math.log(Math.log(g1.getNumberOfNodes()) / Math.log(2))
                    || (g1.getNumberOfNodes() <= 36 && b)) {
                result = "";// "The possibility is high that the graphs are
                // isomorphic.";
                int[][] cvc1 = procedureBUndirected();
                setNotSingletonsUndirected(g2, h2);
                int[][] cvc2 = procedureBUndirected();
                if (comp.equals(cvc1, cvc2)) {
                    result += "The graphs are isomorphic.";
                } else {
                    result += "\nThe graphs are not isomorphic.";
                }
            }
            // sonst trace-distiguishing sets
            else {
                // nur zum F�rben
                setNotSingletonsUndirected(g2, h2);

                int s;
                if (g1.getNumberOfNodes() <= 36) {
                    s = new Double(Math.ceil(Math.log(g1.getNumberOfNodes())
                            / Math.log(2))).intValue() + 1;
                } else {
                    s = 3 * new Double(Math.ceil(2 * Math.sqrt(g1
                            .getNumberOfNodes()))).intValue();
                }
                while (true) {
                    int[][] trace1 = procedureDUndirected(g1, s);
                    if (trace1 != null) {
                        int[][] trace2 = procedureDUndirected(g2, s);
                        if (trace2 != null && comp.equals(trace1, trace2)) {
                            result = "The graphs are isomorphic!";
                            return;
                        } else {
                            result = "The graphs are not isomorphic!";
                            return;
                        }
                    }
                    s++;
                }
            }
            g1.getListenerManager().transactionFinished(this);
            g2.getListenerManager().transactionFinished(this);

        } else {
            if (quickCheck())
                return;

            // execute procedure A on both graphs
            HashMap<Node, String> h1 = procedureA(g1);
            HashMap<Node, String> h2 = procedureA(g2);

            // sort and compare classes of procedure A
            String[] a1 = h1.values().toArray(new String[g1.getNodes().size()]);
            Arrays.sort(a1);
            String[] a2 = h2.values().toArray(new String[g2.getNodes().size()]);
            // comp = new IntArrayComparator();
            Arrays.sort(a2);

            if (!Arrays.equals(a1, a2)) {
                result = "The graphs are not isomorphic!";
                return;
            }

            g1.getListenerManager().transactionStarted(this);
            g2.getListenerManager().transactionStarted(this);
            setNotSingletons(g1, h1);

            Boolean b = true;
            if (cardU >= 2 && g1.getNumberOfNodes() <= 36) {
                b = computeRunTime();
            }

            if (cardU < 2) {
                // nur zum F�rben
                setNotSingletons(g2, h2);
                result = "The graphs are isomorphic.";
            }
            // nicht-Singletons im Ergebnis: wenn < log n/log log n weiter mit
            // Procedure B
            else if (cardU < Math.log(g1.getNumberOfNodes())
                    / Math.log(Math.log(g1.getNumberOfNodes()) / Math.log(2))
                    || (g1.getNumberOfNodes() <= 36 && b)) {
                result = "The possibility is high that the graphs are isomorphic.";
                int[][] cvc1 = procedureB();
                setNotSingletons(g2, h2);
                int[][] cvc2 = procedureB();
                IntArrayComparator comp = new IntArrayComparator();
                if (comp.equals(cvc1, cvc2)) {
                    result += "\nThe graphs are isomorphic.";
                } else {
                    result += "\nThe graphs are not isomorphic.";
                }
            }
            // sonst trace-distiguishing sets
            else {
                // nur zum F�rben
                setNotSingletons(g2, h2);
                int s;
                if (g1.getNumberOfNodes() <= 36) {
                    s = new Double(Math.ceil(Math.log(g1.getNumberOfNodes())
                            / Math.log(2))).intValue() + 1;
                } else {
                    s = 3 * new Double(Math.ceil(2 * Math.sqrt(g1
                            .getNumberOfNodes()))).intValue();
                }
                while (true) {
                    int[][] trace1 = procedureD(g1, s);
                    if (trace1 != null) {
                        int[][] trace2 = procedureD(g2, s);
                        IntArrayComparator comp = new IntArrayComparator();
                        if (trace2 != null && comp.equals(trace1, trace2)) {
                            result = "The graphs are isomorphic!";
                            return;
                        } else {
                            result = "The graphs are not isomorphic!";
                            return;
                        }
                    }
                    s++;
                }
            }
            g1.getListenerManager().transactionFinished(this);
            g2.getListenerManager().transactionFinished(this);
        }
    }

    // mit HashMap und int[] (Vector<E>? BitSet? byte[]? ArrayList?)
    private HashMap<Node, String> procedureA(Graph graph) {
        // linear time algorithm that fails sometimes
        // canonical vertex classification

        // f: V -> L Abbildung der Knoten auf eine linear geordnete Menge
        // (i.e. einfach Nummerierung, bzw. man kann auch
        // mehrere Knoten auf eine Nummer schmei�en)
        // dann ist eine Quasi-Ordnung eine Ordnung nach dieser Hierarchie

        // K: Klasse von Graphen
        // P: Relation VxV, also ein Graph
        // P (spaeter): bestimmte Quasiordnung auf den Knoten
        // die bei isomorphen Graphen das Gleiche ergibt
        // (bis auf Umbennenung der Knotenlabel)
        // = Canonical Vertex Classification
        // P': elementary refinement
        // Klassen von P: �quivalenzklassen von !P
        // V: Knoten
        // X: Graphinstanz
        // g: X -> Y Isomorphismus
        // given: classes C_1...C_h
        // N_i(x): number of neighbors mod m in class C_i
        // N_i^m(x): N_i(x) mod m

        // Vorgehen: f�r alle Knoten x N_i^m(x) bestimmen.
        // bisherige Beziehungen zwischen den Knoten bleiben erhalten wie in P,
        // au�erdem kommt Beziehung dazu, wenn (N_1^m(x),...,N_h^m(x)) <
        // (N_1^m(y),...,N_h^m(y))

        // Procedure A
        // Compute P, P', P'' only (P: according to degree)
        // m=4
        HashMap<Node, String> p_0 = new HashMap<Node, String>();
        HashMap<Node, String> p_1 = new HashMap<Node, String>();
        HashMap<Node, String> p_2 = new HashMap<Node, String>();
        // P_0
        for (Node v : graph.getNodes()) {
            // m^m + m + 1
            String classification = v.getAllInNeighbors().size() + "x"
                    + v.getAllOutNeighbors().size() + "x";
            p_0.put(v, classification);
        }
        // P_1
        for (Node v : graph.getNodes()) {
            String a = p_0.get(v);
            LinkedList<String> neighborClass = new LinkedList<String>();
            for (Node n : v.getAllInNeighbors()) {
                neighborClass.add(p_0.get(n));
            }
            Collections.sort(neighborClass);
            for (String s : neighborClass) {
                a += s;
            }
            a += "i";

            neighborClass = new LinkedList<String>();
            for (Node n : v.getAllOutNeighbors()) {
                neighborClass.add(p_0.get(n));
            }
            Collections.sort(neighborClass);
            for (String s : neighborClass) {
                a += s;
            }
            a += "o";

            p_1.put(v, a);
        }
        // P_2
        for (Node v : graph.getNodes()) {
            String a = p_1.get(v);
            LinkedList<String> neighborClass = new LinkedList<String>();
            for (Node n : v.getAllInNeighbors()) {
                neighborClass.add(p_1.get(n));
            }
            Collections.sort(neighborClass);
            for (String s : neighborClass) {
                a += s;
            }
            a += "y";
            neighborClass = new LinkedList<String>();
            for (Node n : v.getAllOutNeighbors()) {
                neighborClass.add(p_1.get(n));
            }
            Collections.sort(neighborClass);
            for (String s : neighborClass) {
                a += s;
            }
            p_2.put(v, a);
        }
        return p_2;
    }

    // Compute P, P', P''
    private HashMap<Node, int[]> procedureAUndirected(Graph graph) {
        HashMap<Node, int[]> classifiedNodes = new HashMap<Node, int[]>();
        final int m = 4;
        // P_0
        for (Node v : graph.getNodes()) {
            // m^m + m + 1
            int[] classification = new int[m * m * m * m + m + 1];
            // je nach Grad in 4 Klassen einteilen (weil m=4)
            // BUG: was passiert mit undirected edges?
            // v.getInDegree() + v.getOutDegree() != v.getNeighbors().size()
            classification[0] = v.getNeighbors().size() % m;
            classifiedNodes.put(v, classification);
        }
        // P_1
        for (Node v : graph.getNodes()) {
            int[] a = classifiedNodes.get(v);
            for (Node n : v.getNeighbors()) {
                // 1 beim Arrayeintrag f�r die Klasse, in der der Nachbar ist,
                // dazuz�hlen
                a[classifiedNodes.get(n)[0] + 1] = (a[classifiedNodes.get(n)[0] + 1] + 1)
                        % m;
            }
        }
        // P_2
        for (Node v : graph.getNodes()) {
            int[] a = classifiedNodes.get(v);
            for (Node n : v.getNeighbors()) {
                int[] b = classifiedNodes.get(n);
                // und auch hier wieder 1 beim Arrayeintrag f�r die Klasse, in
                // der der Nachbar ist, dazuz�hlen
                a[b[1] * m * m * m + b[2] * m * m + b[3] * m + b[4] + m + 1] = (a[b[1]
                        * m * m * m + b[2] * m * m + b[3] * m + b[4] + m + 1] + 1)
                        % m;
            }
        }
        return classifiedNodes;
    }

    private int[][] procedureB() {
        // innerhalb der �quivalenzklassen Permutationen ausprobieren
        // einfach nur orderNumber durch perm[orderNumber] ersetzen bei allen
        // aus u, aber: in BEIDEN Listen!!
        int[] perm = new int[cardU];
        int noOfGroups = faculties.length;

        NodeWithClassID[] notSingletons_alt = new NodeWithClassID[notSingletons.length];
        System.arraycopy(notSingletons, 0, notSingletons_alt, 0,
                notSingletons.length);

        // contains start positions of the groups in array
        int[] sum = new int[noOfGroups];
        sum[0] = 0;
        for (int i = 1; i < noOfGroups; i++) {
            sum[i] = sum[i - 1] + faculties[i - 1];
        }
        PermutationGenerator[] gens = new PermutationGenerator[noOfGroups];
        for (int i = 0; i < noOfGroups; i++) {
            gens[i] = new PermutationGenerator(faculties[i]);
        }

        // perm initialisieren!!
        for (int i = 0; i < gens.length; i++) {
            System.arraycopy(gens[i].getNext(), 0, perm, sum[i], faculties[i]);
        }
        int k = 0;
        for (int i = 0; i < sum.length; i++) {
            for (int j = 0; j < faculties[i]; j++) {
                perm[j + sum[i]] += sum[i];
                k++;
            }
        }

        int[][] cvc = null;
        // weitere Permutationen erzeugen
        while (true) {
            // Elemente vertauschen
            for (int i = 0; i < cardU; i++) {
                notSingletons[i] = notSingletons_alt[perm[i]];
                allNodes[indices[i]] = notSingletons_alt[perm[i]];
                // Achtung: das wirkt sich auch auf den Graphen und Knoten in
                // s�mtlichen Arrays aus
                allNodes[indices[i]].getNode().setInteger("order number",
                        indices[i]);
            }

            // pro Perm. ein Array:
            // a_ij = 1 or 0 according to whether i and j are adjacent or not,
            // and the rows and columns of the array are arranged in the order
            // defined by L.
            int[][] a = new int[notSingletons.length][allNodes.length];
            for (int i = 0; i < cardU; i++) {
                for (Node n : notSingletons[i].getNode().getAllInNeighbors()) {
                    a[i][n.getInteger("order number")] = 1;
                }
                for (Node n : notSingletons[i].getNode().getAllOutNeighbors()) {
                    // 2 stands for undirected edges
                    a[i][n.getInteger("order number")] = (a[i][n
                            .getInteger("order number")] == 1) ? 2 : -1;
                }
                // alle anderen = 0, weil int[] mit 0 initialisiert wird
            }
            // vergleichen mit letzter Runde und kleineres �bernehmen
            IntArrayComparator comp = new IntArrayComparator();
            if (cvc != null) {
                if (comp.compare(a, cvc) < 0) {
                    for (int i = 0; i < a.length; i++) {
                        System.arraycopy(a[i], 0, cvc[i], 0, a[i].length);
                    }
                }
            } else {
                // during first execution of while-loop
                cvc = new int[notSingletons.length][allNodes.length];
                for (int i = 0; i < a.length; i++) {
                    System.arraycopy(a[i], 0, cvc[i], 0, a[i].length);
                }
            }

            // find next permutation
            for (int j = noOfGroups - 1; j >= 0; j--) {
                if (gens[j].hasMore()) {
                    System.arraycopy(gens[j].getNext(), 0, perm, sum[j],
                            faculties[j]);
                    for (int i = sum[j]; i < sum[j] + faculties[j]; i++) {
                        perm[i] += sum[j];
                    }
                    break;
                } else {
                    if (j == 0)
                        return cvc;
                    else {
                        gens[j] = new PermutationGenerator(faculties[j]);
                        System.arraycopy(gens[j].getNext(), 0, perm, sum[j],
                                faculties[j]);
                        for (int i = sum[j]; i < sum[j] + faculties[j]; i++) {
                            perm[i] += sum[j];
                        }
                    }
                }
            }
        }
    }

    private int[][] procedureBUndirected() {
        // innerhalb der �quivalenzklassen Permutationen ausprobieren:
        // orderNumber durch perm[orderNumber] ersetzen bei allen Knoten aus u
        int[] perm = new int[cardU];
        int noOfGroups = faculties.length;

        ClassifiedNode[] notSingletons_alt = new ClassifiedNode[notSingletonsUndirected.length];
        System.arraycopy(notSingletonsUndirected, 0, notSingletons_alt, 0,
                notSingletonsUndirected.length);

        // contains start positions of the groups in array
        int[] sum = new int[noOfGroups];
        sum[0] = 0;
        for (int i = 1; i < noOfGroups; i++) {
            sum[i] = sum[i - 1] + faculties[i - 1];
        }
        PermutationGenerator[] gens = new PermutationGenerator[noOfGroups];
        for (int i = 0; i < noOfGroups; i++) {
            gens[i] = new PermutationGenerator(faculties[i]);
        }

        // perm initialisieren!!
        for (int i = 0; i < gens.length; i++) {
            System.arraycopy(gens[i].getNext(), 0, perm, sum[i], faculties[i]);
        }
        int k = 0;
        for (int i = 0; i < sum.length; i++) {
            for (int j = 0; j < faculties[i]; j++) {
                perm[j + sum[i]] += sum[i];
                k++;
            }
        }

        int[][] cvc = null;
        // weitere Permutationen erzeugen
        while (true) {
            // Elemente vertauschen
            for (int i = 0; i < cardU; i++) {
                notSingletonsUndirected[i] = notSingletons_alt[perm[i]];
                allNodesUndirected[indices[i]] = notSingletons_alt[perm[i]];
                // Achtung: das wirkt sich auch auf den Graphen und Knoten in
                // s�mtlichen Arrays aus
                allNodesUndirected[indices[i]].getNode().setInteger(
                        "order number", indices[i]);
            }

            // pro Perm. ein Array:
            // a_ij = 1 or 0 according to whether i and j are adjacent or not,
            // and the rows and columns of the array are arranged in the order
            // defined by L.
            int[][] a = new int[notSingletonsUndirected.length][allNodesUndirected.length];
            for (int i = 0; i < cardU; i++) {
                for (Node n : notSingletonsUndirected[i].getNode()
                        .getNeighbors()) {
                    a[i][n.getInteger("order number")] = 1;
                }
                // alle anderen = 0, weil int[] mit 0 initialisiert wird
            }
            // vergleichen mit letzter Runde und kleineres �bernehmen
            IntArrayComparator comp = new IntArrayComparator();
            if (cvc != null) {
                if (comp.compare(a, cvc) < 0) {
                    for (int i = 0; i < a.length; i++) {
                        System.arraycopy(a[i], 0, cvc[i], 0, a[i].length);
                    }
                }
            } else {
                // during first execution of while-loop
                cvc = new int[notSingletonsUndirected.length][allNodesUndirected.length];
                for (int i = 0; i < a.length; i++) {
                    System.arraycopy(a[i], 0, cvc[i], 0, a[i].length);
                }
            }

            // find next permutation
            for (int j = noOfGroups - 1; j >= 0; j--) {
                if (gens[j].hasMore()) {
                    System.arraycopy(gens[j].getNext(), 0, perm, sum[j],
                            faculties[j]);
                    for (int i = sum[j]; i < sum[j] + faculties[j]; i++) {
                        perm[i] += sum[j];
                    }
                    break;
                } else {
                    if (j == 0)
                        return cvc;
                    else {
                        gens[j] = new PermutationGenerator(faculties[j]);
                        System.arraycopy(gens[j].getNext(), 0, perm, sum[j],
                                faculties[j]);
                        for (int i = sum[j]; i < sum[j] + faculties[j]; i++) {
                            perm[i] += sum[j];
                        }
                    }
                }
            }
        }
    }

    private void setNotSingletons(Graph g, HashMap<Node, String> h) {
        allNodes = new NodeWithClassID[g.getNumberOfNodes()];
        // allNodes bef�llen
        int j = 0;
        for (Node n : g.getNodes()) {
            n.setBoolean("noSingleton", false);
            allNodes[j] = new NodeWithClassID(n, h.get(n));
            j++;
        }
        // allNodes sortieren
        Arrays.sort(allNodes);
        // not singletons markieren
        int classNumber = 0;
        float hue = 0;
        float s = 0.9f;
        float b = 0.9f;
        NodeWithClassID c = allNodes[0];
        for (int i = 1; i < allNodes.length; i++) {
            if (NodeWithClassID.equals(c, allNodes[i]))
            // assign group number to all members of group
            {
                allNodes[i - 1].getNode().setBoolean("noSingleton", true);
                allNodes[i - 1].getNode().setInteger("class number",
                        classNumber);
                allNodes[i].getNode().setBoolean("noSingleton", true);
                allNodes[i].getNode().setInteger("class number", classNumber);
                setNodeColor(allNodes[i - 1].getNode(), Color.getHSBColor(hue,
                        s, b));
                setNodeColor(allNodes[i].getNode(), Color
                        .getHSBColor(hue, s, b));
            } else if (allNodes[i - 1].getNode().getBoolean("noSingleton"))
            // found end of group, so increment group number
            {
                classNumber++;
                hue += 0.17;
                setNodeColor(allNodes[i].getNode(), Color
                        .getHSBColor(hue, s, b));
            } else {
                hue += 0.17;
                setNodeColor(allNodes[i].getNode(), Color
                        .getHSBColor(hue, s, b));
            }
            c = allNodes[i];
        }
        j = allNodes.length;
        // falls es mind. eine Klasse mit mehr als einem Element gibt oder
        // falls alles eine einzige gro�e Klasse "0" ist
        if (classNumber != 0
                || allNodes[j - 1].getNode().getBoolean("noSingleton")) {
            // compute cardinality of U
            faculties = allNodes[j - 1].getNode().getBoolean("noSingleton") ? new int[classNumber + 1]
                    : new int[classNumber];
            j = 0;
            // count number of nodes per node equivalence class
            for (int i = 0; i < allNodes.length; i++) {
                if (allNodes[i].getNode().getBoolean("noSingleton")) {
                    faculties[allNodes[i].getNode().getInteger("class number")]++;
                    j++;
                }
            }
            notSingletons = new NodeWithClassID[j];
            indices = new int[j];
            cardU = j;
            // notSingletons bef�llen
            j = 0;
            for (int i = 0; i < allNodes.length; i++) {
                // Knoten eine Nummer zuweisen f�r Position im Array a, wenn
                // Nachbarn durchsucht werden
                // Nummer wird sp�ter bei Permutation ge�ndert
                allNodes[i].getNode().setInteger("order number", i);
                if (allNodes[i].getNode().getBoolean("noSingleton")) {
                    notSingletons[j] = allNodes[i];
                    indices[j] = i;
                    j++;
                }
            }
        } else {
            // alles sind singletons!!!
        }
    }

    private void setNotSingletonsUndirected(Graph g, HashMap<Node, int[]> h) {
        allNodesUndirected = new ClassifiedNode[g.getNumberOfNodes()];
        // allNodes bef�llen
        int j = 0;
        for (Node n : g.getNodes()) {
            allNodesUndirected[j] = new ClassifiedNode(n, h.get(n));
            allNodesUndirected[j].getNode().setBoolean("noSingleton", false);
            j++;
        }
        // allNodes sortieren
        Arrays.sort(allNodesUndirected);
        // not singletons markieren
        int classNumber = 0;
        float hue = 0;
        float s = 0.9f;
        float b = 0.9f;
        ClassifiedNode c = allNodesUndirected[0];
        for (int i = 1; i < allNodesUndirected.length; i++) {
            if (ClassifiedNode.equals(c, allNodesUndirected[i]))
            // assign group number to all members of group
            {
                allNodesUndirected[i - 1].getNode().setBoolean("noSingleton",
                        true);
                allNodesUndirected[i - 1].getNode().setInteger("class number",
                        classNumber);
                allNodesUndirected[i].getNode().setBoolean("noSingleton", true);
                allNodesUndirected[i].getNode().setInteger("class number",
                        classNumber);
                setNodeColor(allNodesUndirected[i - 1].getNode(), Color
                        .getHSBColor(hue, s, b));
                setNodeColor(allNodesUndirected[i].getNode(), Color
                        .getHSBColor(hue, s, b));
            } else if (allNodesUndirected[i - 1].getNode().getBoolean(
                    "noSingleton"))
            // found end of group, so increment group number
            {
                classNumber++;
                hue += 0.17;
                setNodeColor(allNodesUndirected[i].getNode(), Color
                        .getHSBColor(hue, s, b));
            } else {
                hue += 0.17;
                setNodeColor(allNodesUndirected[i].getNode(), Color
                        .getHSBColor(hue, s, b));
            }
            // first node might not be coloured, but remains white
            c = allNodesUndirected[i];
        }
        j = allNodesUndirected.length;
        // zweiter Fall, falls alles eine gro�e Gruppe "0" ist
        if (classNumber != 0
                || allNodesUndirected[j - 1].getNode()
                        .getBoolean("noSingleton")) {
            // compute cardinality of U
            faculties = allNodesUndirected[j - 1].getNode().getBoolean(
                    "noSingleton") ? new int[classNumber + 1]
                    : new int[classNumber];
            j = 0;
            // count number of nodes per node equivalence class
            for (int i = 0; i < allNodesUndirected.length; i++) {
                if (allNodesUndirected[i].getNode().getBoolean("noSingleton")) {
                    faculties[allNodesUndirected[i].getNode().getInteger(
                            "class number")]++;
                    j++;
                }
            }
            notSingletonsUndirected = new ClassifiedNode[j];
            indices = new int[j];
            cardU = j;
            // notSingletons bef�llen
            j = 0;
            for (int i = 0; i < allNodesUndirected.length; i++) {
                // Knoten eine Nummer zuweisen f�r Position im Array a, wenn
                // Nachbarn durchsucht werden
                // Nummer wird sp�ter bei Permutation ge�ndert
                allNodesUndirected[i].getNode().setInteger("order number", i);
                if (allNodesUndirected[i].getNode().getBoolean("noSingleton")) {
                    notSingletonsUndirected[j] = allNodesUndirected[i];
                    indices[j] = i;
                    j++;
                }
            }
        } else {
            // alles sind singletons!!!
        }
    }

    /*
     * DISTINGUISHING SUBSETS:
     * 
     * S subset of V, x in V\S
     * 
     * "trace" of x in S: neighbors of x that are in S
     * 
     * S is a "trace-distinguishing set" if all vertices outside S have
     * different traces in S!!
     */
    private Node[] procedureC(int[] S, Node[] g) {
        // Array aufteilen in S und V ohne S
        Node[] snodes = new Node[S.length];
        for (int i = 0; i < S.length; i++) {
            snodes[i] = g[S[i]];
            g[S[i]] = null;
        }
        /*
         * to each v in V\S assign a binary word a(v) = a_1...a_|S| 1 if v and s
         * in S adjacent, 0 oth.
         */
        ClassifiedNode[] cnodes = new ClassifiedNode[g.length - S.length];
        int k = 0;
        for (int i = 0; i < g.length; i++) {
            if (g[i] != null) {
                int[] classification = new int[S.length];
                for (int j = 0; j < S.length; j++) {
                    if (g[i].getAllInNeighbors().contains(snodes[j])) {
                        classification[j] = 1;
                    }
                    if (g[i].getAllOutNeighbors().contains(snodes[j])) {
                        // 2 stands for an undirected edge
                        classification[j] = (classification[j] == 1) ? 2 : -1;
                    }
                }
                cnodes[k] = new ClassifiedNode(g[i], classification);
                k++;
            }
        }
        Arrays.sort(cnodes);
        /*
         * vergleichen, ob alle verschieden ("if all a(v) are different, S is
         * trace-distinguishing")
         */
        for (int i = 0; i < cnodes.length - 1; i++) {
            if (ClassifiedNode.equals(cnodes[i + 1], cnodes[i]))
                // if not different, reject
                return null;
        }
        /*
         * else order V (relative CL w.r. to S):
         * 
         * v < w if a(v) < a(w)
         * 
         * x < y so wie durch S vorgegeben
         * 
         * erst x's, dann v's
         */
        Node[] result = new Node[g.length];
        System.arraycopy(snodes, 0, result, 0, snodes.length);
        for (int i = snodes.length; i < cnodes.length + snodes.length; i++) {
            result[i] = cnodes[i - snodes.length].getNode();
        }
        return result;
    }

    /*
     * DISTINGUISHING SUBSETS
     */
    private Node[] procedureCUndirected(int[] S, Node[] g) {
        // Array aufteilen in S und V ohne S
        Node[] snodes = new Node[S.length];
        for (int i = 0; i < S.length; i++) {
            snodes[i] = g[S[i]];
            g[S[i]] = null;
        }
        /*
         * to each v in V\S assign a binary word a(v) = a_1...a_|S| 1 if v and s
         * in S adjacent, 0 oth.
         */
        ClassifiedNode[] cnodes = new ClassifiedNode[g.length - S.length];
        int k = 0;
        for (int i = 0; i < g.length; i++) {
            if (g[i] != null) {
                int[] classification = new int[S.length];
                for (int j = 0; j < S.length; j++) {
                    classification[j] = (g[i].getNeighbors()
                            .contains(snodes[j])) ? 1 : 0;
                }
                cnodes[k] = new ClassifiedNode(g[i], classification);
                k++;
            }
        }
        Arrays.sort(cnodes);
        /*
         * vergleichen, ob alle verschieden ("if all a(v) are different, S is
         * trace-distinguishing")
         */
        for (int i = 0; i < cnodes.length - 1; i++) {
            if (ClassifiedNode.equals(cnodes[i + 1], cnodes[i]))
                // if not different, reject
                return null;
        }
        /*
         * else order V (relative CL w.r. to S):
         * 
         * v < w if a(v) < a(w)
         * 
         * x < y so wie durch S vorgegeben
         * 
         * erst x's, dann v's
         */
        Node[] result = new Node[g.length];
        System.arraycopy(snodes, 0, result, 0, snodes.length);
        for (int i = snodes.length; i < cnodes.length + snodes.length; i++) {
            result[i] = cnodes[i - snodes.length].getNode();
        }
        return result;
    }

    /*
     * Procedure D:
     * 
     * N = n! / (n-s)!
     * 
     * generate all s-sequences (S_1...S_N) of vertices of G
     * 
     * apply Procedure C to each sequence
     * 
     * if you can find some that are not rejected, output lexicographically
     * first adjacency matrix
     */
    private int[][] procedureD(Graph g, int s) {
        // Knoten von g werden identifiziert durch ihre Nummer im array:
        Node[] nodes = g.getNodes().toArray(new Node[g.getNumberOfNodes()]);

        // generate all s-combinations
        int[] comb = new int[s];
        CombinationGenerator combi = new CombinationGenerator(g
                .getNumberOfNodes(), s);
        // apply Procedure C to each combination
        int[][] result = null;
        int[][] adjacencies;
        Node[] resultC;
        while (combi.hasMore()) {
            comb = combi.getNext();
            PermutationGenerator permi = new PermutationGenerator(s);
            while (permi.hasMore()) {
                int[] perm = permi.getNext();
                int[] combperm = new int[s];
                for (int i = 0; i < s; i++) {
                    combperm[i] = comb[perm[i]];
                }
                Node[] temp = new Node[nodes.length];
                System.arraycopy(nodes, 0, temp, 0, nodes.length);
                resultC = procedureC(combperm, temp);
                if (resultC != null) {
                    /*
                     * let A_i be the adjacency matrix corresponding to the
                     * relative CL and find the lexicographically first one
                     */
                    for (int i = 0; i < resultC.length; i++) {
                        resultC[i].setInteger("node number", i);
                    }
                    adjacencies = new int[g.getNumberOfNodes()][g
                            .getNumberOfNodes()];
                    for (int i = 0; i < resultC.length; i++) {
                        for (Node nb : resultC[i].getAllInNeighbors()) {
                            adjacencies[i][nb.getInteger("node number")] = 1;
                        }
                        /*
                         * for (Node nb: resultC[i].getAllOutNeighbors()) { // 2
                         * stands for an undirected edge
                         * adjacencies[i][nb.getInteger("node number")] =
                         * (adjacencies[i][nb .getInteger("node number")] == 1)
                         * ? 2 : -1; }
                         */
                    }
                    // vergleiche mit vorherigem und behalte lexikographisch
                    // erstes
                    if (result != null) {
                        // result, resultC eins von beiden
                        IntArrayComparator comp = new IntArrayComparator();
                        if (comp.compare(adjacencies, result) < 0) {
                            for (int i = 0; i < result.length; i++) {
                                System.arraycopy(adjacencies[i], 0, result[i],
                                        0, result[i].length);
                            }
                        }
                    } else {
                        result = adjacencies;
                    }
                }
            }
        }
        return result;
    }

    /*
     * Procedure D:
     * 
     * N = n! / (n-s)!
     * 
     * generate all s-sequences (S_1...S_N) of vertices of G
     * 
     * apply Procedure C to each sequence
     * 
     * if you can find some that are not rejected, output lexicographically
     * first adjacency matrix
     */
    private int[][] procedureDUndirected(Graph g, int s) {
        // Knoten von g werden identifiziert durch ihre Nummer im array:
        Node[] nodes = g.getNodes().toArray(new Node[g.getNumberOfNodes()]);

        // generate all s-combinations
        int[] comb = new int[s];
        CombinationGenerator combi = new CombinationGenerator(g
                .getNumberOfNodes(), s);
        // apply Procedure C to each combination
        int[][] result = null;
        int[][] adjacencies;
        Node[] resultC;
        while (combi.hasMore()) {
            comb = combi.getNext();
            PermutationGenerator permi = new PermutationGenerator(s);
            while (permi.hasMore()) {
                int[] perm = permi.getNext();
                int[] combperm = new int[s];
                for (int i = 0; i < s; i++) {
                    combperm[i] = comb[perm[i]];
                }
                Node[] temp = new Node[nodes.length];
                System.arraycopy(nodes, 0, temp, 0, nodes.length);
                resultC = procedureCUndirected(combperm, temp);
                if (resultC != null) {
                    /*
                     * let A_i be the adjacency matrix corresponding to the
                     * relative CL and find the lexicographically first one
                     */
                    for (int i = 0; i < resultC.length; i++) {
                        resultC[i].setInteger("node number", i);
                    }
                    adjacencies = new int[g.getNumberOfNodes()][g
                            .getNumberOfNodes()];
                    for (int i = 0; i < resultC.length; i++) {
                        for (Node nb : resultC[i].getNeighbors()) {
                            adjacencies[i][nb.getInteger("node number")] = 1;
                        }
                    }
                    // vergleiche mit vorherigem und behalte lexikographisch
                    // erstes
                    if (result != null) {
                        IntArrayComparator comp = new IntArrayComparator();
                        if (comp.compare(adjacencies, result) < 0) {
                            for (int i = 0; i < result.length; i++) {
                                System.arraycopy(adjacencies[i], 0, result[i],
                                        0, result[i].length);
                            }
                        }
                    } else {
                        result = adjacencies;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Computes the factorial of an integer n.
     * 
     * @param n
     *            a positive integer n
     * @return the factorial of n
     */
    private long fac(long n) {
        long fact = 1;
        for (long i = n; i > 1; i--) {
            fact *= i;
        }
        return fact;
    }

    // heuristic
    private Boolean computeRunTime() {
        long runTimeB = 1;
        for (int i = 0; i < faculties.length; i++) {
            runTimeB *= fac(faculties[i]);
        }
        long n = g1.getNumberOfNodes();
        long runTimeC = 1;
        long s = new Double(Math.ceil(Math.log(n) / Math.log(2))).longValue() + 2;
        for (long i = n; i > s; i--) {
            runTimeC *= i;
        }
        if (runTimeB < runTimeC)
            return true;
        else
            return false;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Isomorphism: Babai/Kucera";
    }

    /**
     * @see org.graffiti.plugins.algorithms.isomorphism.AbstractIsomorphism#reset()
     */
    @Override
    public void reset() {
        for (Node n : g1.getNodes()) {
            n.setInteger("order number", 0);
            n.removeAttribute("order number");
            n.setInteger("node number", 0);
            n.removeAttribute("node number");
            n.setBoolean("noSingleton", false);
            n.removeAttribute("noSingleton");
            n.setInteger("class number", 0);
            n.removeAttribute("class number");
        }
        for (Node n : g2.getNodes()) {
            n.setInteger("order number", 0);
            n.removeAttribute("order number");
            n.setInteger("node number", 0);
            n.removeAttribute("node number");
            n.setBoolean("noSingleton", false);
            n.removeAttribute("noSingleton");
            n.setInteger("class number", 0);
            n.removeAttribute("class number");
        }
        cardU = 0;
        allNodes = null;
        notSingletons = null;
        allNodesUndirected = null;
        notSingletonsUndirected = null;
        indices = null;
        faculties = null;
        super.reset();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
