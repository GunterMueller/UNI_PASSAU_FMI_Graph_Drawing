// =============================================================================
//
//   RefinementaAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id $

/**
 * 
 */
package org.graffiti.plugins.algorithms.graviso;

import java.util.LinkedList;

import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.EditorAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;

/**
 * This class implements the part responsible for backtracking in the most basic
 * version of a refinement algorithm.
 * 
 * @version $Revision: 158 $
 * @author lenhardt
 */
public class RefinementAlgorithmSimple extends AbstractRefinementAlgorithm
        implements EditorAlgorithm, CalculatingAlgorithm {

    /**
     * Starts the backtracking process of the refinement algorithm.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {

        // Debugging & Visualization
        /** number of backtracking calls */
        btCounter = 0;
        /** number of refinement steps */
        refCounter = 0;
        /** widget pops up when btCounter == btStop */
        btStop = 1;
        /** widget pops up when refCounter == refStop */
        refStop = 1;

        int res = 0;

        // perform preliminary checks
        if (!canBeIsmorphic(g1, g2)) {
            res = R_NOT_ISO;
            // result is set in canBeIsomorphic
            // result = "Graphs are not ISO";
            return;
        }
        IsoGraphSimple refinement1, refinement2;

        try {
            if (((BooleanParameter) parameters[P_USE_BFS_INFO]).getBoolean()) {
                // the BFS version is to be used
                refinement1 = new IsoGraphBFS(this.g1,
                        ((BooleanParameter) parameters[P_REGARD_DIRECTIONS])
                                .getBoolean(), 0);
                refinement2 = new IsoGraphBFS(this.g2,
                        ((BooleanParameter) parameters[P_REGARD_DIRECTIONS])
                                .getBoolean(), 1);
            } else {
                // standard refinement is to be used
                refinement1 = new IsoGraphSimple(this.g1,
                        ((BooleanParameter) parameters[P_REGARD_DIRECTIONS])
                                .getBoolean());
                refinement2 = new IsoGraphSimple(this.g2,
                        ((BooleanParameter) parameters[P_REGARD_DIRECTIONS])
                                .getBoolean());
            }

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
            // we had an exception, this will not happen for normal execution
            result += "Graphs might be ISO";
        } else {
            result = "Graphs are ISO";
        }
    }

    /**
     * The heart of the algorithm, this method is being called recursively,
     * until a mapping is produced or until it is proven that the graphs are not
     * isomorphic.
     * 
     * @param graph1
     * @param graph2
     * @return one of R_ISO, R_NOT_ISO
     * @throws CloneNotSupportedException
     * @throws GravIsoException
     */
    protected int backtrack(IsoGraphSimple graph1, IsoGraphSimple graph2)
            throws CloneNotSupportedException, GravIsoException {

        btCounter++; // for debugging and statistics

        boolean stable1 = false;
        boolean stable2 = false;
        do {
            // stabilize the colorings
            stable1 = graph1.refine();
            stable2 = graph2.refine();
            refCounter++;

            // if refCounter == refStop or btCounter = btStop, a window pops up
            // and the nodes are colored
            showMessage(graph1.getNodeColors(), graph2.getNodeColors());

            if (stable1 != stable2) {
                // if one stabilizes before the other, they're not ISO either
                break;
            }
        } while (!(stable1 && stable2));

        int res = R_MAYBE;

        // compare color class size vector and matrix
        if (((BooleanParameter) parameters[P_USE_BFS_INFO]).getBoolean()) {
            res = ((IsoGraphBFS) graph1).isIsomorphicTo(graph2);
        } else {
            res = graph1.isIsomorphicTo(graph2);
        }

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
                // are no more feasible children on the backtracking tree
                return R_NOT_ISO;
        } else {
            // we have to look at the children of the backtracking tree
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
                    IsoGraphSimple newGraph1;
                    if (((BooleanParameter) parameters[P_USE_BFS_INFO])
                            .getBoolean()) {
                        newGraph1 = ((IsoGraphBFS) graph1).clone();
                    } else {
                        newGraph1 = graph1.clone();
                    }
                    newGraph1.nodeColors[c1Node.getNodeNumber()] = newGraph1.numberOfColors;
                    newGraph1.numberOfColors++;
                    newGraph1.degreeVectors = newGraph1.computeDegreeVectors();

                    // accordingly, we do the same with a node in graph
                    // 2, assigning it the same new color as the node in g1
                    IsoGraphSimple newGraph2;
                    if (((BooleanParameter) parameters[P_USE_BFS_INFO])
                            .getBoolean()) {
                        newGraph2 = ((IsoGraphBFS) graph2).clone();
                    } else {
                        newGraph2 = graph2.clone();
                    }
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