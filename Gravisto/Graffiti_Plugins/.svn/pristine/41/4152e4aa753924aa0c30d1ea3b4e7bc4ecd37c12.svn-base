package org.graffiti.plugins.algorithms.labeling.finitePositionsIndividualWeighting;

import java.util.List;
import java.util.ListIterator;
import java.util.TreeSet;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * no spatial optimization
 * <p>
 * Upper bound of running time of search is (n*(n-1))/2, where n is the number
 * of candidate positions.
 * 
 * @author scholz
 */
public class NaiveLabelCandidateCollisionStructure extends
        LabelCandidateCollisionStructure {
    List<LabelLocator> locators;

    public NaiveLabelCandidateCollisionStructure(List<LabelLocator> locators,
            Graph graph, boolean penalizeOverlapsWithNodes,
            boolean penalizeOverlapsWithAnyEdges, double interLabelGap) {
        // initialize
        this.locators = locators;

        // find and set collisions
        // (running time: quadratic in the number of label candidates
        // [T((k*l)�)])
        LabelLocator locatorA;
        LabelLocator locatorB;
        LabelPosition positionA;
        LabelPosition positionB;
        for (ListIterator<LabelLocator> locatorItA = locators.listIterator(); locatorItA
                .hasNext();) {
            locatorA = locatorItA.next();
            for (ListIterator<LabelLocator> locatorItB = locators
                    .listIterator(locatorItA.nextIndex()); locatorItB.hasNext();) {
                locatorB = locatorItB.next();

                // test all postitions of locatorA with all position of locatorB
                for (ListIterator<? extends LabelPosition> posItA = locatorA
                        .getCandidatesIterator(); posItA.hasNext();) {
                    positionA = posItA.next();
                    for (ListIterator<? extends LabelPosition> posItB = locatorB
                            .getCandidatesIterator(); posItB.hasNext();) {
                        positionB = posItB.next();

                        // test collision
                        if (testCollision(positionA, positionB, interLabelGap)) {
                            // set collision partners
                            // (state changes in positionA and positionB)
                            positionA.markCollision(positionB);
                            positionB.markCollision(positionA);

                            Statistics.numOverlaps++;
                        }
                    }
                }
            }
        }

        // Penalize label position candidates overlapping nodes
        if (penalizeOverlapsWithNodes) {
            LabelPosition candidate;
            for (LabelLocator locator : locators) {
                for (Node node : graph.getNodes()) {
                    // Check overlaps between locator's positions and edge
                    for (ListIterator<? extends LabelPosition> it = locator
                            .getCandidatesIterator(); it.hasNext();) {
                        candidate = it.next();
                        if (LabelCandidateCollisionStructure
                                .checkOverlapWithNode(candidate, node)) {
                            // Penalize quality
                            candidate.markNodeOverlap();
                        }
                    }
                }
            }
        }

        // Penalize label position candidates overlapping any edges
        if (penalizeOverlapsWithAnyEdges) {
            LabelPosition candidate;
            for (LabelLocator locator : locators) {
                for (Edge edge : graph.getEdges()) {
                    // Check overlaps between locator's positions and edge
                    for (ListIterator<? extends LabelPosition> it = locator
                            .getCandidatesIterator(); it.hasNext();) {
                        candidate = it.next();
                        if (LabelCandidateCollisionStructure
                                .checkOverlapWithEdge(candidate, edge)) {
                            // Penalize quality
                            candidate.markEdgeOverlap();
                        }
                    }
                }
            }
        }

    }

    // @Override
    // public LinkedList<LabelLocator> getPlaceableLocators()
    // {
    // // set placeable locators list
    // // (running time: linear in the number of label candidates [T(k*l)])
    // LinkedList<LabelLocator> placeableLocators =
    // new LinkedList<LabelLocator>();
    // for (LabelLocator locator : locators) {
    // if (locator.isPlaceable())
    // placeableLocators.add(locator);
    // }
    //        
    // return placeableLocators;
    // }

    @Override
    public TreeSet<LabelLocator> getSortedLocators() {
        // set sorted locators list
        // (running time: n*log(n) in the number of label candidates [T(k*l)])
        TreeSet<LabelLocator> sortedLocators = new TreeSet<LabelLocator>();
        for (LabelLocator locator : locators) {
            sortedLocators.add(locator);
        }

        return sortedLocators;
    }

}
