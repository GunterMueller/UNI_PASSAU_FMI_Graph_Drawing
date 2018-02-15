/**
 * 
 */
package org.graffiti.plugins.algorithms.graviso;

import java.util.Enumeration;
import java.util.LinkedList;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.StringParameter;

/**
 * Implements the backtracking part of a refinement algorithm to test graph
 * isomorphism. This is the version integrating information from the labels of a
 * graph.
 * 
 * @author lenhardt
 * @version $Revision: 1002 $
 */
public class RefinementAlgorithmLabelsEqual extends
        AbstractRefinementAlgorithmLabels {

    public RefinementAlgorithmLabelsEqual() {
        super();
    }

    /*
     * returns the name of the algorithm to gravisto
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    @Override
    public String getName() {
        return "Graph Isomorphism Test (Refinement) - Labels compared for Equality";
    }

    @Override
    protected boolean canBeIsomorphic(Graph g1, Graph g2) {
        if (!super.canBeIsmorphic(g1, g2))
            return false;

        // for the labeled version, we additionally check if the labels of
        // isomorphic nodes are the same
        if (((BooleanParameter) parameters[P_REGARD_NODE_LABELS]).getBoolean()) {
            Enumeration<Integer> keys1 = nodeLabelClasses1.keys();
            // therefore we check if both nodelabelclasses are the same
            while (keys1.hasMoreElements()) {
                int key1 = keys1.nextElement();
                Integer v2 = nodeLabelClasses2.get(key1);
                if (v2 != null) {
                    int val2 = v2;
                    int val1 = nodeLabelClasses1.get(key1);
                    if (val1 != val2) {
                        result = "There is a different number of Nodes carrying a specific label";
                        return false;
                    }
                } else {
                    result = "A matching of the Node labels could not be established";
                    return false;
                }
            }
        }

        // the same is being done with the edge labels
        if (((BooleanParameter) parameters[P_REGARD_EDGE_LABELS]).getBoolean()) {
            Enumeration<Integer> keys1 = edgeLabelClasses1.keys();
            while (keys1.hasMoreElements()) {
                int key1 = keys1.nextElement();
                Integer v2 = edgeLabelClasses2.get(key1);
                if (v2 != null) {
                    int val2 = v2;
                    int val1 = edgeLabelClasses1.get(key1);
                    if (val1 != val2) {
                        result = "There is a different number of Edges carrying a specific label";
                        return false;
                    }
                } else {
                    result = "A matching of the Edge labels could not be established";
                    return false;
                }
            }

        }

        // all tests passed:
        return true;

    }

    public void execute() {
        // bookkeeping of backtracking calls & refinement step
        btCounter = 0;
        int res = 0;

        // preliminary checks
        if (!canBeIsmorphic(g1, g2)) {
            res = R_NOT_ISO;
            // result is set in canBeIsomorphic
            // result = "Graphs are not ISO";
            return;
        }

        try {
            IsoGraphLabelsEqual refinement1, refinement2;
            refinement1 = new IsoGraphLabelsEqual(this.g1,
                    ((BooleanParameter) parameters[P_REGARD_DIRECTIONS])
                            .getBoolean(),
                    ((BooleanParameter) parameters[P_REGARD_EDGE_LABELS])
                            .getBoolean(),
                    ((StringParameter) parameters[P_EDGE_LABEL_PATH])
                            .getString(),
                    ((BooleanParameter) parameters[P_REGARD_NODE_LABELS])
                            .getBoolean(),
                    ((StringParameter) parameters[P_NODE_LABEL_PATH])
                            .getString());
            refinement2 = new IsoGraphLabelsEqual(this.g2,
                    ((BooleanParameter) parameters[P_REGARD_DIRECTIONS])
                            .getBoolean(),
                    ((BooleanParameter) parameters[P_REGARD_EDGE_LABELS])
                            .getBoolean(),
                    ((StringParameter) parameters[P_EDGE_LABEL_PATH])
                            .getString(),
                    ((BooleanParameter) parameters[P_REGARD_NODE_LABELS])
                            .getBoolean(),
                    ((StringParameter) parameters[P_NODE_LABEL_PATH])
                            .getString());

            // start the algorithm
            res = backtrack(refinement1, refinement2);

            if (res == R_ISO) {
                colorNodes(finalG1Colors, finalG2Colors);
            }

        } catch (GravIsoException e) {
            res = R_MAYBE;
            result = "Fehler!\n" + e.getLocalizedMessage() + " ";
            e.printStackTrace();
        } catch (CloneNotSupportedException e) {
            res = R_MAYBE;
            result = "Programm-Fehler!\n" + e.getLocalizedMessage() + " ";
            e.printStackTrace();
        }
        if (res == R_NOT_ISO) {
            result = "Graphs are not ISO";
        } else if (res == R_MAYBE) {
            // we had an exception
            result += "Graphs might be ISO";
        } else {
            result = "Graphs are ISO";
        }
    }

    protected int backtrack(IsoGraphLabelsEqual graph1,
            IsoGraphLabelsEqual graph2) throws CloneNotSupportedException,
            GravIsoException {

        btCounter++; // for debugging and statistics

        boolean stable1 = false;
        boolean stable2 = false;
        do {
            stable1 = graph1.refine();
            stable2 = graph2.refine();
            showMessage(graph1.getNodeColors(), graph2.getNodeColors());
            // DEBUG
            refCounter++;

            if (stable1 != stable2) {
                // if one stabilizes before the other, they're not ISO either
                break;
            }
        } while (!(stable1 && stable2));

        int res = graph1.isIsomorphicTo(graph2);
        // we pick a color class in each graph (the "best one", as it is)
        LinkedList<SortableNode> c1Nodes = graph1.computeBestColorClass();

        if ((res == R_ISO) || (c1Nodes.size() < 2) || (res == R_NOT_ISO)) {
            // we're on a leaf in the backtracking tree: no more further
            // variations possible (nodes.size) or needed
            if (res == R_ISO) {
                // we found a matching
                finalG1Colors = graph1.getNodeColors();
                finalG2Colors = graph2.getNodeColors();
                return R_ISO;
            } else
                // if there's no more room for variation (only singleton color
                // classes), or the test reveals the graphs can not be ISO under
                // that coloring, this coloring yields not_iso, because there
                // are no more feasible children on the bt tree
                return R_NOT_ISO;
        } else {
            // we have to look at the children of the bt node
            LinkedList<SortableNode> c2Nodes = graph2.getColorClasses().get(
                    graph1.nodeColors[c1Nodes.getFirst().getNodeNumber()]);

            // this must not happen, must have been caught by isIsomorphicTo
            if (c1Nodes.size() != c2Nodes.size())
                throw new GravIsoException(
                        "The number of nodes in the color class is not the same!");
            if (graph1.getNodeColors()[c1Nodes.getFirst().getNodeNumber()] != graph2
                    .getNodeColors()[c2Nodes.getFirst().getNodeNumber()])
                throw new GravIsoException(
                        "We didn't receive the same color class from the 2 graphs");

            // we enumerate all BT children by fixing nodes in g1 and g2 to the
            // same color
            for (SortableNode c1Node : c1Nodes) {
                for (SortableNode c2Node : c2Nodes) {

                    // assign this c1Node a new color
                    IsoGraphLabelsEqual newGraph1 = graph1.clone();
                    // without clone
                    newGraph1.nodeColors[c1Node.getNodeNumber()] = newGraph1.numberOfColors;
                    newGraph1.numberOfColors++;
                    newGraph1.degreeVectors = newGraph1.computeDegreeVectors();

                    // accordingly, we do the same with a node in graph
                    // 2, assigning it the same new color as the node in g1
                    IsoGraphLabelsEqual newGraph2 = graph2.clone();
                    newGraph2.nodeColors[c2Node.getNodeNumber()] = newGraph2.numberOfColors;
                    newGraph2.numberOfColors++;
                    newGraph2.degreeVectors = newGraph2.computeDegreeVectors();

                    if (backtrack(newGraph1, newGraph2) == R_ISO)
                        // if the newly generated BT child node is feasible, so
                        // is the parent
                        return R_ISO;
                }
            }
            // we enumerated all children and none of them yielded feasible
            return R_NOT_ISO;
        }

    }
}
