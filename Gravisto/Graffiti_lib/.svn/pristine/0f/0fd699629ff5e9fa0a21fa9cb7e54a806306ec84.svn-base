// =============================================================================
//
//   VF2.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.isomorphism;

import java.awt.Color;

import org.graffiti.graph.Node;

/**
 * Implements the so-called VF2 algorithm for testing graph isomorphism from
 * Luigi P. Cordella, Pasquale Foggia, Carlo Sansone and Mario Vento (A
 * (Sub)Graph Isomorphism Algorithm for Matching Large Graphs, 2004).
 * 
 * @author mary-k
 * @version $Revision$ $Date$
 */
public class VF2 extends AbstractIsomorphism {
    // contain the current mapping (e.g. core_1[v] contains the index of the
    // node paired with v, -1 if there's no match yet)
    private int[] core_1, core_2;

    // describe the memberships of the terminal sets
    // (e.g. in_1[v] contains the depth of the SSR tree of the state in
    // which v entered M1 or T1_in, -1 oth.)
    private int[] in_1, out_1, in_2, out_2, nb_1, nb_2;

    private Node[] nodes1, nodes2;

    private boolean foundMatching = false;

    /*
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
            // ordne den Knoten Nummern zu:
            nodes1 = g1.getNodes().toArray(new Node[g1.getNumberOfNodes()]);
            nodes2 = g2.getNodes().toArray(new Node[g2.getNumberOfNodes()]);
            for (int i = 0; i < nodes1.length; i++) {
                nodes1[i].setInteger("node number", i);
                nodes2[i].setInteger("node number", i);
            }

            core_1 = new int[g1.getNumberOfNodes()];
            core_2 = new int[g2.getNumberOfNodes()];
            nb_1 = new int[g1.getNumberOfNodes()];
            nb_2 = new int[g2.getNumberOfNodes()];
            // alle mit -1 initialisieren, da 0 f�r den Knoten Nr. 0 steht
            for (int i = 0; i < core_1.length; i++) {
                core_1[i] = -1;
                core_2[i] = -1;
                nb_1[i] = -1;
                nb_2[i] = -1;
            }
            matchUndirected(0);
        } else {
            if (quickCheck())
                return;

            // ordne den Knoten Nummern zu:
            nodes1 = g1.getNodes().toArray(new Node[g1.getNumberOfNodes()]);
            nodes2 = g2.getNodes().toArray(new Node[g2.getNumberOfNodes()]);
            for (int i = 0; i < nodes1.length; i++) {
                nodes1[i].setInteger("node number", i);
                nodes2[i].setInteger("node number", i);
            }

            core_1 = new int[g1.getNumberOfNodes()];
            core_2 = new int[g2.getNumberOfNodes()];
            in_1 = new int[g1.getNumberOfNodes()];
            out_1 = new int[g1.getNumberOfNodes()];
            in_2 = new int[g2.getNumberOfNodes()];
            out_2 = new int[g2.getNumberOfNodes()];
            // alle mit -1 initialisieren, da 0 f�r den Knoten Nr. 0 steht
            for (int i = 0; i < core_1.length; i++) {
                core_1[i] = -1;
                core_2[i] = -1;
                in_1[i] = -1;
                out_1[i] = -1;
                in_2[i] = -1;
                out_2[i] = -1;
            }
            match(0);
        }
        // Paare einf�rben
        g1.getListenerManager().transactionStarted(this);
        g2.getListenerManager().transactionStarted(this);
        if (foundMatching) {
            float hue = 0;
            float s = 0.9f;
            float b = 0.9f;
            for (int i = 0; i < nodes1.length; i++) {
                setNodeColor(nodes1[i], Color.getHSBColor(hue, s, b));
                setNodeColor(nodes2[core_1[i]], Color.getHSBColor(hue, s, b));
                hue += 0.17;
            }
        }
        g1.getListenerManager().transactionFinished(this);
        g2.getListenerManager().transactionFinished(this);
        result = foundMatching ? "The graphs are isomorphic!"
                : "The graphs are not isomorphic!";
    }

    private void match(int recursionDepth) {
        // wenn die Rekursion die Tiefe |V| erreicht hat, wurden |V| g�ltige
        // Paare dem Matching hinzugef�gt, womit eine Bijektion gefunden ist
        if (recursionDepth == g1.getNumberOfNodes()) {
            foundMatching = true;
            return;
        }

        // testen ob die out-Mengen Elemente enthalten
        boolean out1Empty = true;
        for (int i = 0; i < out_1.length; i++) {
            if (out_1[i] != -1 && core_1[i] == -1) {
                out1Empty = false;
                break;
            }
        }
        int selectedNodeFromG2 = -1;
        if (!out1Empty) {
            for (int i = 0; i < out_2.length; i++) {
                // es reicht, einen Knoten aus g2 zu w�hlen
                // muss im Rand sein, darf aber noch nicht in M sein
                if (out_2[i] != -1 && core_2[i] == -1) {
                    selectedNodeFromG2 = i;
                    break;
                }
            }
        }

        // wenn die out-Mengen leer sind, testen ob die in-Mengen Elemente
        // enthalten
        boolean in1Empty = true;
        if (out1Empty) {
            for (int i = 0; i < in_1.length; i++) {
                if (in_1[i] != -1 && core_1[i] == -1) {
                    in1Empty = false;
                    break;
                }
            }
            if (!in1Empty) {
                for (int i = 0; i < in_2.length; i++) {
                    // es reicht, einen Knoten aus g2 zu w�hlen
                    // muss im Rand sein, darf aber noch nicht in M sein
                    if (in_2[i] != -1 && core_2[i] == -1) {
                        selectedNodeFromG2 = i;
                        break;
                    }
                }
            }
        }

        // wenn out-Mengen beide nicht-leer sind (sind wg. Regel 5 eigtl.
        // sowieso gleich gro�)...
        if (!out1Empty && selectedNodeFromG2 != -1) {
            // ...bilde Paare aus Randknoten und �berpr�fe, ob Regeln gelten
            for (int i = 0; i < out_1.length; i++) {
                // muss im Rand sein, darf aber noch nicht in M sein
                if (out_1[i] != -1 && core_1[i] == -1) {
                    if (checkRules(i, selectedNodeFromG2)) {
                        // wenn Regeln eingehalten werden, das Paar dem Matching
                        // M hinzuf�gen, implementiert durch core_1&2
                        core_1[i] = selectedNodeFromG2;
                        core_2[selectedNodeFromG2] = i;
                        for (Node n : nodes1[i].getAllInNeighbors()) {
                            if (in_1[n.getInteger("node number")] == -1) {
                                in_1[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        for (Node n : nodes1[i].getAllOutNeighbors()) {
                            if (out_1[n.getInteger("node number")] == -1) {
                                out_1[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2]
                                .getAllInNeighbors()) {
                            if (in_2[n.getInteger("node number")] == -1) {
                                in_2[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2]
                                .getAllOutNeighbors()) {
                            if (out_2[n.getInteger("node number")] == -1) {
                                out_2[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        match(recursionDepth + 1);
                        // wenn weiter unten im Rekursionsbaum ein Matching
                        // gefunden wurde, Rekursion abbrechen
                        if (foundMatching)
                            return;
                        // wenn eingeschlagener Zweig nicht erfolgreich war,
                        // Daten wieder auf den Stand davor bringen
                        // ("restore data" im Paper)
                        core_1[i] = -1;
                        core_2[selectedNodeFromG2] = -1;
                        // alternativ: in_1 etc. traversieren und nach
                        // recursionDepth suchen
                        for (Node n : nodes1[i].getAllInNeighbors()) {
                            if (in_1[n.getInteger("node number")] == recursionDepth) {
                                in_1[n.getInteger("node number")] = -1;
                            }
                        }
                        for (Node n : nodes1[i].getAllOutNeighbors()) {
                            if (out_1[n.getInteger("node number")] == recursionDepth) {
                                out_1[n.getInteger("node number")] = -1;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2]
                                .getAllInNeighbors()) {
                            if (in_2[n.getInteger("node number")] == recursionDepth) {
                                in_2[n.getInteger("node number")] = -1;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2]
                                .getAllOutNeighbors()) {
                            if (out_2[n.getInteger("node number")] == recursionDepth) {
                                out_2[n.getInteger("node number")] = -1;
                            }
                        }
                    }
                }
            }
        }
        // wenn out-Mengen leer sind, stattdessen Paare aus Elementen der
        // in-Mengen bilden
        else if (!in1Empty && selectedNodeFromG2 != -1) {
            for (int i = 0; i < in_1.length; i++) {
                if (in_1[i] != -1 && core_1[i] == -1) {
                    if (checkRules(i, selectedNodeFromG2)) {
                        core_1[i] = selectedNodeFromG2;
                        core_2[selectedNodeFromG2] = i;
                        for (Node n : nodes1[i].getAllInNeighbors()) {
                            if (in_1[n.getInteger("node number")] == -1) {
                                in_1[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        for (Node n : nodes1[i].getAllOutNeighbors()) {
                            if (out_1[n.getInteger("node number")] == -1) {
                                out_1[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2]
                                .getAllInNeighbors()) {
                            if (in_2[n.getInteger("node number")] == -1) {
                                in_2[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2]
                                .getAllOutNeighbors()) {
                            if (out_2[n.getInteger("node number")] == -1) {
                                out_2[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        match(recursionDepth + 1);
                        if (foundMatching)
                            return;
                        core_1[i] = -1;
                        core_2[selectedNodeFromG2] = -1;
                        for (Node n : nodes1[i].getAllInNeighbors()) {
                            if (in_1[n.getInteger("node number")] == recursionDepth) {
                                in_1[n.getInteger("node number")] = -1;
                            }
                        }
                        for (Node n : nodes1[i].getAllOutNeighbors()) {
                            if (out_1[n.getInteger("node number")] == recursionDepth) {
                                out_1[n.getInteger("node number")] = -1;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2]
                                .getAllInNeighbors()) {
                            if (in_2[n.getInteger("node number")] == recursionDepth) {
                                in_2[n.getInteger("node number")] = -1;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2]
                                .getAllOutNeighbors()) {
                            if (out_2[n.getInteger("node number")] == recursionDepth) {
                                out_2[n.getInteger("node number")] = -1;
                            }
                        }
                    }
                }
            }
        } else if (out1Empty && in1Empty) {
            // if both in and out are empty, form pairs of all nodes not
            // yet contained in M
            // first pick an unmatched node from g2...
            for (int i = 0; i < core_2.length; i++) {
                if (core_2[i] == -1) {
                    selectedNodeFromG2 = i;
                    break;
                }
            }
            // ...and try matching it with all unmatched nodes from g1
            for (int i = 0; i < core_1.length; i++) {
                if (core_1[i] == -1) {
                    if (checkRules(i, selectedNodeFromG2)) {
                        core_1[i] = selectedNodeFromG2;
                        core_2[selectedNodeFromG2] = i;
                        for (Node n : nodes1[i].getAllInNeighbors()) {
                            if (in_1[n.getInteger("node number")] == -1) {
                                in_1[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        for (Node n : nodes1[i].getAllOutNeighbors()) {
                            if (out_1[n.getInteger("node number")] == -1) {
                                out_1[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2]
                                .getAllInNeighbors()) {
                            if (in_2[n.getInteger("node number")] == -1) {
                                in_2[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2]
                                .getAllOutNeighbors()) {
                            if (out_2[n.getInteger("node number")] == -1) {
                                out_2[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        match(recursionDepth + 1);
                        if (foundMatching)
                            return;
                        core_1[i] = -1;
                        core_2[selectedNodeFromG2] = -1;
                        for (Node n : nodes1[i].getAllInNeighbors()) {
                            if (in_1[n.getInteger("node number")] == recursionDepth) {
                                in_1[n.getInteger("node number")] = -1;
                            }
                        }
                        for (Node n : nodes1[i].getAllOutNeighbors()) {
                            if (out_1[n.getInteger("node number")] == recursionDepth) {
                                out_1[n.getInteger("node number")] = -1;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2]
                                .getAllInNeighbors()) {
                            if (in_2[n.getInteger("node number")] == recursionDepth) {
                                in_2[n.getInteger("node number")] = -1;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2]
                                .getAllOutNeighbors()) {
                            if (out_2[n.getInteger("node number")] == recursionDepth) {
                                out_2[n.getInteger("node number")] = -1;
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * Regeln f�r (n,m):
     * 
     * 1. f�r alle Vorg�nger von n in M existiert ein Vorg�nger von m in M die
     * jeweils schon gematcht wurden und f�r alle Vorg�nger von m in M existiert
     * ein Vorg�nger von n die schon gemacht sind
     * 
     * 2. f�r alle Nachfolger von n in M existiert ein Nachfolger von m in M und
     * f�r alle Nachfolger von m in M existiert ein Nachfolger von n in M und
     * die Nachfolger sind jeweils gemacht
     * 
     * 3. n und m haben gleich viele Vorg�nger/Nachfolger im Rand der
     * ausgehenden Kanten
     * 
     * 4. n und m haben gleich viele Vorg�nger/Nachfolger im Rand der
     * eingehenden Kanten
     * 
     * 5. n und m haben gleich viele Vorg�nger/Nachfolger au�erhalb des Randes
     */
    private boolean checkRules(int n, int m) {
        // Regel 1
        for (Node nb1 : nodes1[n].getAllInNeighbors()) {
            if (core_1[nb1.getInteger("node number")] != -1) {
                boolean match = false;
                for (Node nb2 : nodes2[m].getAllInNeighbors()) {
                    if (core_1[nb1.getInteger("node number")] == nb2
                            .getInteger("node number")) {
                        match = true;
                        break;
                    }
                }
                if (!match)
                    return false;
            }
        }
        for (Node nb2 : nodes2[m].getAllInNeighbors()) {
            if (core_2[nb2.getInteger("node number")] != -1) {
                boolean match = false;
                for (Node nb1 : nodes1[n].getAllInNeighbors()) {
                    if (core_2[nb2.getInteger("node number")] == nb1
                            .getInteger("node number")) {
                        match = true;
                        break;
                    }
                }
                if (!match)
                    return false;
            }
        }

        // Regel 2
        for (Node nb1 : nodes1[n].getAllOutNeighbors()) {
            if (core_1[nb1.getInteger("node number")] != -1) {
                boolean match = false;
                for (Node nb2 : nodes2[m].getAllOutNeighbors()) {
                    if (core_1[nb1.getInteger("node number")] == nb2
                            .getInteger("node number")) {
                        match = true;
                        break;
                    }
                }
                if (!match)
                    return false;
            }
        }
        for (Node nb2 : nodes2[m].getAllOutNeighbors()) {
            if (core_2[nb2.getInteger("node number")] != -1) {
                boolean match = false;
                for (Node nb1 : nodes1[n].getAllOutNeighbors()) {
                    if (core_2[nb2.getInteger("node number")] == nb1
                            .getInteger("node number")) {
                        match = true;
                        break;
                    }
                }
                if (!match)
                    return false;
            }
        }

        // Regel 3
        int outNeighborsN = 0;
        int outNeighborsM = 0;
        for (Node nb1 : nodes1[n].getAllOutNeighbors()) {
            if (in_1[nb1.getInteger("node number")] != -1
                    && core_1[nb1.getInteger("node number")] == -1) {
                outNeighborsN++;
            }
        }
        for (Node nb2 : nodes2[m].getAllOutNeighbors()) {
            if (in_2[nb2.getInteger("node number")] != -1
                    && core_2[nb2.getInteger("node number")] == -1) {
                outNeighborsM++;
            }
        }
        if (outNeighborsN != outNeighborsM)
            return false;

        int inNeighborsN = 0;
        int inNeighborsM = 0;
        for (Node nb1 : nodes1[n].getAllInNeighbors()) {
            if (in_1[nb1.getInteger("node number")] != -1
                    && core_1[nb1.getInteger("node number")] == -1) {
                inNeighborsN++;
            }
        }
        for (Node nb2 : nodes2[m].getAllInNeighbors()) {
            if (in_2[nb2.getInteger("node number")] != -1
                    && core_2[nb2.getInteger("node number")] == -1) {
                inNeighborsM++;
            }
        }
        if (inNeighborsN != inNeighborsM)
            return false;

        // Regel 4
        outNeighborsN = 0;
        outNeighborsM = 0;
        for (Node nb1 : nodes1[n].getAllOutNeighbors()) {
            if (out_1[nb1.getInteger("node number")] != -1
                    && core_1[nb1.getInteger("node number")] == -1) {
                outNeighborsN++;
            }
        }
        for (Node nb2 : nodes2[m].getAllOutNeighbors()) {
            if (out_2[nb2.getInteger("node number")] != -1
                    && core_2[nb2.getInteger("node number")] == -1) {
                outNeighborsM++;
            }
        }
        if (outNeighborsN != outNeighborsM)
            return false;

        inNeighborsN = 0;
        inNeighborsM = 0;
        for (Node nb1 : nodes1[n].getAllInNeighbors()) {
            if (out_1[nb1.getInteger("node number")] != -1
                    && core_1[nb1.getInteger("node number")] == -1) {
                inNeighborsN++;
            }
        }
        for (Node nb2 : nodes2[m].getAllInNeighbors()) {
            if (out_2[nb2.getInteger("node number")] != -1
                    && core_2[nb2.getInteger("node number")] == -1) {
                inNeighborsM++;
            }
        }
        if (inNeighborsN != inNeighborsM)
            return false;

        // Regel 5
        inNeighborsN = 0;
        inNeighborsM = 0;
        for (Node nb1 : nodes1[n].getAllInNeighbors()) {
            if (in_1[nb1.getInteger("node number")] == -1
                    && out_1[nb1.getInteger("node number")] == -1) {
                inNeighborsN++;
            }
        }
        for (Node nb2 : nodes2[m].getAllInNeighbors()) {
            if (in_2[nb2.getInteger("node number")] == -1
                    && out_2[nb2.getInteger("node number")] == -1) {
                inNeighborsM++;
            }
        }
        if (inNeighborsN != inNeighborsM)
            return false;
        outNeighborsN = 0;
        outNeighborsM = 0;
        for (Node nb1 : nodes1[n].getAllOutNeighbors()) {
            if (in_1[nb1.getInteger("node number")] == -1
                    && out_1[nb1.getInteger("node number")] == -1) {
                outNeighborsN++;
            }
        }
        for (Node nb2 : nodes2[m].getAllOutNeighbors()) {
            if (in_2[nb2.getInteger("node number")] == -1
                    && out_2[nb2.getInteger("node number")] == -1) {
                outNeighborsM++;
            }
        }
        if (outNeighborsN != outNeighborsM)
            return false;

        // all feasibility rules succeed:
        return true;
    }

    private void matchUndirected(int recursionDepth) {
        // wenn die Rekursion die Tiefe |V| erreicht hat, wurden |V| g�ltige
        // Paare dem Matching hinzugef�gt, womit eine Bijektion gefunden ist
        if (recursionDepth == g1.getNumberOfNodes()) {
            foundMatching = true;
            return;
        }

        // testen ob die out-Mengen Elemente enthalten
        boolean nb1Empty = true;
        for (int i = 0; i < nb_1.length; i++) {
            if (nb_1[i] != -1 && core_1[i] == -1) {
                nb1Empty = false;
                break;
            }
        }
        int selectedNodeFromG2 = -1;
        if (!nb1Empty) {
            for (int i = 0; i < nb_2.length; i++) {
                // es reicht, einen Knoten aus g2 zu w�hlen
                // muss im Rand sein, darf aber noch nicht in M sein
                if (nb_2[i] != -1 && core_2[i] == -1) {
                    selectedNodeFromG2 = i;
                    break;
                }
            }
        }

        // wenn Rand-Mengen nicht-leer sind
        if (!nb1Empty && selectedNodeFromG2 != -1) {
            // ...bilde Paare aus Randknoten und �berpr�fe, ob Regeln gelten
            for (int i = 0; i < nb_1.length; i++) {
                // muss im Rand sein, darf aber noch nicht in M sein
                if (nb_1[i] != -1 && core_1[i] == -1) {
                    if (checkRulesUndirected(i, selectedNodeFromG2)) {
                        // wenn Regeln eingehalten werden, das Paar dem Matching
                        // M hinzuf�gen, implementiert durch core_1&2
                        core_1[i] = selectedNodeFromG2;
                        core_2[selectedNodeFromG2] = i;
                        for (Node n : nodes1[i].getNeighbors()) {
                            if (nb_1[n.getInteger("node number")] == -1) {
                                nb_1[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2].getNeighbors()) {
                            if (nb_2[n.getInteger("node number")] == -1) {
                                nb_2[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        matchUndirected(recursionDepth + 1);
                        // wenn weiter unten im Rekursionsbaum ein Matching
                        // gefunden wurde, Rekursion abbrechen
                        if (foundMatching)
                            return;
                        // wenn eingeschlagener Zweig nicht erfolgreich war,
                        // Daten wieder auf den Stand davor bringen
                        // ("restore data" im Paper)
                        core_1[i] = -1;
                        core_2[selectedNodeFromG2] = -1;
                        // alternativ: in_1 etc. traversieren und nach
                        // recursionDepth suchen
                        for (Node n : nodes1[i].getNeighbors()) {
                            if (nb_1[n.getInteger("node number")] == recursionDepth) {
                                nb_1[n.getInteger("node number")] = -1;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2].getNeighbors()) {
                            if (nb_2[n.getInteger("node number")] == recursionDepth) {
                                nb_2[n.getInteger("node number")] = -1;
                            }
                        }
                    }
                }
            }
        } else {
            // if both in and out are empty, form pairs of all nodes not
            // yet contained in M
            // first pick an unmatched node from g2...
            for (int i = 0; i < core_2.length; i++) {
                if (core_2[i] == -1) {
                    selectedNodeFromG2 = i;
                    break;
                }
            }
            // ...and try matching it with all unmatched nodes from g1
            for (int i = 0; i < core_1.length; i++) {
                if (core_1[i] == -1) {
                    if (checkRulesUndirected(i, selectedNodeFromG2)) {
                        core_1[i] = selectedNodeFromG2;
                        core_2[selectedNodeFromG2] = i;
                        for (Node n : nodes1[i].getNeighbors()) {
                            if (nb_1[n.getInteger("node number")] == -1) {
                                nb_1[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2].getNeighbors()) {
                            if (nb_2[n.getInteger("node number")] == -1) {
                                nb_2[n.getInteger("node number")] = recursionDepth;
                            }
                        }
                        matchUndirected(recursionDepth + 1);
                        if (foundMatching)
                            return;
                        core_1[i] = -1;
                        core_2[selectedNodeFromG2] = -1;
                        for (Node n : nodes1[i].getNeighbors()) {
                            if (nb_1[n.getInteger("node number")] == recursionDepth) {
                                nb_1[n.getInteger("node number")] = -1;
                            }
                        }
                        for (Node n : nodes2[selectedNodeFromG2].getNeighbors()) {
                            if (nb_2[n.getInteger("node number")] == recursionDepth) {
                                nb_2[n.getInteger("node number")] = -1;
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * Regeln f�r (n,m):
     * 
     * 1. f�r alle Vorg�nger von n in M existiert ein Vorg�nger von m in M die
     * jeweils schon gematcht wurden und f�r alle Vorg�nger von m in M existiert
     * ein Vorg�nger von n die schon gemacht sind
     * 
     * 2. f�r alle Nachfolger von n in M existiert ein Nachfolger von m in M und
     * f�r alle Nachfolger von m in M existiert ein Nachfolger von n in M und
     * die Nachfolger sind jeweils gemacht
     * 
     * 3. n und m haben gleich viele Vorg�nger/Nachfolger im Rand der
     * ausgehenden Kanten
     * 
     * 4. n und m haben gleich viele Vorg�nger/Nachfolger im Rand der
     * eingehenden Kanten
     * 
     * 5. n und m haben gleich viele Vorg�nger/Nachfolger au�erhalb des Randes
     */
    private boolean checkRulesUndirected(int n, int m) {
        // Regel 1 = 2
        for (Node nb1 : nodes1[n].getNeighbors()) {
            if (core_1[nb1.getInteger("node number")] != -1) {
                boolean match = false;
                for (Node nb2 : nodes2[m].getNeighbors()) {
                    if (core_1[nb1.getInteger("node number")] == nb2
                            .getInteger("node number")) {
                        match = true;
                        break;
                    }
                }
                if (!match)
                    return false;
            }
        }
        for (Node nb2 : nodes2[m].getNeighbors()) {
            if (core_2[nb2.getInteger("node number")] != -1) {
                boolean match = false;
                for (Node nb1 : nodes1[n].getNeighbors()) {
                    if (core_2[nb2.getInteger("node number")] == nb1
                            .getInteger("node number")) {
                        match = true;
                        break;
                    }
                }
                if (!match)
                    return false;
            }
        }

        // Regel 3 = 4
        int neighborsN = 0;
        int neighborsM = 0;
        for (Node nb1 : nodes1[n].getNeighbors()) {
            if (nb_1[nb1.getInteger("node number")] != -1
                    && core_1[nb1.getInteger("node number")] == -1) {
                neighborsN++;
            }
        }
        for (Node nb2 : nodes2[m].getNeighbors()) {
            if (nb_2[nb2.getInteger("node number")] != -1
                    && core_2[nb2.getInteger("node number")] == -1) {
                neighborsM++;
            }
        }
        if (neighborsN != neighborsM)
            return false;

        // Regel 5
        neighborsN = 0;
        neighborsM = 0;
        for (Node nb1 : nodes1[n].getNeighbors()) {
            if (nb_1[nb1.getInteger("node number")] == -1) {
                neighborsN++;
            }
        }
        for (Node nb2 : nodes2[m].getNeighbors()) {
            if (nb_2[nb2.getInteger("node number")] == -1) {
                neighborsM++;
            }
        }
        if (neighborsN != neighborsM)
            return false;

        // all feasibility rules succeed:
        return true;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Isomorphism: VF2";
    }

    /**
     * @see org.graffiti.plugins.algorithms.isomorphism.AbstractIsomorphism#reset()
     */
    public void reset() {
        super.reset();
        foundMatching = false;
        for (int i = 0; i < nodes1.length; i++) {
            nodes1[i].removeAttribute("node number");
            nodes2[i].removeAttribute("node number");
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
