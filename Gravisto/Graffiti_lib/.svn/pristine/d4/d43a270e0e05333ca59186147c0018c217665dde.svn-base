// =============================================================================
//
//   AtomFinder.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation;

import java.util.ArrayList;
import java.util.List;

import org.graffiti.graph.Node;

/**
 * AtomFinder, is used by layout algorithms to find all atoms of a tree. N.B.:
 * This is the algorithm as proposed by Eades et al in "Minimum size
 * h-v-Drawings". This was proven to miss some atoms and produce suboptimal
 * results (see my Diplomarbeit "Platzsparendes Visualisieren von Hierarchischer
 * Information"). The algorithm described in this paper has been generalized to
 * k-nary trees.
 * 
 * @author Andreas
 * @version $Revision$ $Date$
 */
public class AtomFinderHeuristic1 implements AtomFinder {
    /**
     * factory is used to instantiate LayoutComposition in this AtomFinder.
     */
    protected LayoutComposition factory;

    /**
     * the distance between nodes in LayoutCompositions
     */
    protected double nodeDistance;

    /**
     * nodesWithDimensions determines whether we treat nodes as dimensionless
     * points or consider their actual dimensions.
     */
    protected boolean nodesWithDimensions;

    /**
     * Constructs a new AtomFinder, which is used by LayoutAlgorithms to find
     * all atoms of a tree
     * 
     * @param factory
     *            is used to instantiate LayoutComposition in this AtomFinder.
     * @param nodeDistance
     *            the distance between nodes in LayoutCompositions
     * @param nodesWithDimensions
     *            determines whether we treat nodes as dimensionless points or
     *            consider their actual dimensions.
     */
    public AtomFinderHeuristic1(LayoutComposition factory, double nodeDistance,
            boolean nodesWithDimensions) {
        this.factory = factory;
        this.nodeDistance = nodeDistance;
        this.nodesWithDimensions = nodesWithDimensions;
    }

    /**
     * This finds drawings of the subtree of root and returns them in
     * width-decreasing and height-increasing order.
     * 
     * @param root
     * @return list of atoms of root
     */
    public ArrayList<LayoutComposition> findAtoms(Node root) {
        if (root.getOutDegree() == 0) {
            ArrayList<LayoutComposition> leafComposition = new ArrayList<LayoutComposition>();
            leafComposition.add(this.factory.instance(root, null, false,
                    this.nodeDistance, this.nodesWithDimensions).get(0));
            return leafComposition;
        } else {

            ArrayList<ArrayList<LayoutComposition>> allChildrenAtoms = new ArrayList<ArrayList<LayoutComposition>>();

            for (Node currentChild : root.getAllOutNeighbors()) {
                allChildrenAtoms.add(findAtoms(currentChild));
            }

            return this.atomMerge(horizontalMerge(root, allChildrenAtoms),
                    verticalMerge(root, allChildrenAtoms));
        }

    }

    /**
     * This constructs a list of drawings from the horizontally merged and
     * vertically merged lists of LayoutCompositions. It eliminates all
     * dominating pairs provided that the given lists of LayoutCompositions are
     * as described below.
     * 
     * @param horizontallyMerged
     *            horizontal LayoutCompositions in height-decreasing order
     * @param verticallyMerged
     *            vertical LayoutCompositions in width-decreasing order
     * @return A list of atoms in width-decreasing and height-increasing order.
     */
    protected ArrayList<LayoutComposition> atomMerge(
            List<LayoutComposition> horizontallyMerged,
            List<LayoutComposition> verticallyMerged) {

        ArrayList<LayoutComposition> mergeResult = new ArrayList<LayoutComposition>();

        LayoutComposition lastInserted = null;

        LayoutComposition currentHorizontal = null;
        LayoutComposition currentVertical = null;

        int i = horizontallyMerged.size() - 1;
        int j = 0;
        int l = verticallyMerged.size();

        while (i >= 0 && j < l) {
            currentHorizontal = horizontallyMerged.get(i);
            currentVertical = verticallyMerged.get(j);

            if (currentHorizontal.getWidth() <= currentVertical.getWidth()) {
                if (currentHorizontal.getHeight() <= currentVertical
                        .getHeight()) {
                    if (!currentHorizontal.dominates(lastInserted)) {
                        mergeResult.add(currentHorizontal);
                        lastInserted = currentHorizontal;
                    }
                    i--;
                } else {
                    if (!currentVertical.dominates(lastInserted)) {
                        mergeResult.add(currentVertical);
                        lastInserted = currentVertical;
                    }
                }
                j++;
            } else {
                if (currentHorizontal.getHeight() >= currentVertical
                        .getHeight()) {
                    if (!currentVertical.dominates(lastInserted)) {
                        mergeResult.add(currentVertical);
                        lastInserted = currentVertical;
                    }
                    j++;
                } else {
                    if (!currentHorizontal.dominates(lastInserted)) {
                        mergeResult.add(currentHorizontal);
                        lastInserted = currentHorizontal;
                    }
                }
                i--;
            }
        }

        if (i >= 0) {
            while (i >= 0) {
                LayoutComposition currentComposition = horizontallyMerged
                        .get(i);
                if (!currentComposition.dominates(lastInserted)) {
                    mergeResult.add(currentComposition);
                }
                i--;
            }
        } else {
            while (j < l) {
                LayoutComposition currentComposition = verticallyMerged.get(j);
                if (!currentComposition.dominates(lastInserted)) {
                    mergeResult.add(currentComposition);
                }
                j++;
            }
        }

        return mergeResult;
    }

    /**
     * This constructs layouts that are possible by horizontally combining the
     * given subtreeRoot and the atoms of its subtrees and returns them in
     * height-decreasing order. This is achieved by a k-way merge algorithm,
     * where k should be the number of children of this subtree. We start by
     * constructing the highest possible LayoutComposition and then, by
     * exchanging the highest child atom by the next atom of the child's list of
     * atoms, work our way through to the lowest possible horizontal
     * LayoutComposition.
     * 
     * @param subtreeRoot
     * @param allChildrenAtoms
     *            the atoms of the children's subtrees. It is assumed by the
     *            algorithm that they are in strictly decreasing width-order -
     *            and, because they are atoms, also in strictly increasing
     *            height-order.
     * @return a list of horizontal LayoutCompositions in height-decreasing
     *         order.
     */
    protected List<LayoutComposition> horizontalMerge(Node subtreeRoot,
            ArrayList<ArrayList<LayoutComposition>> allChildrenAtoms) {
        ArrayList<LayoutComposition> mergeResult = new ArrayList<LayoutComposition>();

        int numberOfChildren = allChildrenAtoms.size();

        int[] currentIndices = new int[numberOfChildren];
        // we have to set the current indices to the last element

        for (int i = 0; i < numberOfChildren; i++) {
            currentIndices[i] = allChildrenAtoms.get(i).size() - 1;
        }

        ArrayList<LayoutComposition> currentAtoms = new ArrayList<LayoutComposition>();
        for (int i = 0; i < numberOfChildren; i++) {
            currentAtoms.add(allChildrenAtoms.get(i).get(currentIndices[i]));
        }

        boolean allHaveMoreAtoms = true;

        LayoutComposition lastInserted = null;

        while (allHaveMoreAtoms) {

            LayoutComposition currentCandidate = this.factory.instance(
                    subtreeRoot, currentAtoms, true, this.nodeDistance,
                    this.nodesWithDimensions).get(0);

            if (!currentCandidate.dominates(lastInserted)) {
                mergeResult.add(currentCandidate);
                lastInserted = currentCandidate;
            }

            // Look for the child whose currentAtom has maximum height...

            double maximumHeight = -1;
            int maximumHeightIndex = -1;

            for (int i = 0; i < numberOfChildren; i++) {
                LayoutComposition currentComposition = currentAtoms.get(i);
                double currentCompositionHeight = currentComposition
                        .getHeight();
                if (maximumHeight < currentCompositionHeight) {
                    maximumHeight = currentCompositionHeight;
                    maximumHeightIndex = i;
                }
            }

            // Now decrease the index in the found child by one

            currentIndices[maximumHeightIndex]--;

            // Does it still have atoms left now?
            if (currentIndices[maximumHeightIndex] < 0) {
                allHaveMoreAtoms = false;
            } else {
                // change currentAtoms
                currentAtoms.set(maximumHeightIndex, allChildrenAtoms.get(
                        maximumHeightIndex).get(
                        currentIndices[maximumHeightIndex]));
            }

        }

        return mergeResult;

    }

    /**
     * This constructs layouts that are possible by vertically combining the
     * given subtreeRoot and the atoms of its subtrees and returns them in
     * width-decreasing order. This is achieved by a k-way merge algorithm,
     * where k should be the number of children of this subtree. We start by
     * constructing the widest possible LayoutComposition and then, by
     * exchanging the widest child atom by the next atom of the child's list of
     * atoms, work our way through to the narrowest possible horizontal
     * LayoutComposition.
     * 
     * @param subtreeRoot
     * @param allChildrenAtoms
     *            the atoms of the children's subtrees. It is assumed by the
     *            algorithm that they are in strictly decreasing width-order -
     *            and because they are atom also in strictly increasing
     *            height-order.
     * @return a list of vertical LayoutCompositions in width decreasing order.
     */
    protected List<LayoutComposition> verticalMerge(Node subtreeRoot,
            ArrayList<ArrayList<LayoutComposition>> allChildrenAtoms) {
        ArrayList<LayoutComposition> mergeResult = new ArrayList<LayoutComposition>();

        int numberOfChildren = allChildrenAtoms.size();

        // we trust that they are all set to 0 by Java...
        int[] currentIndices = new int[numberOfChildren];

        ArrayList<LayoutComposition> currentAtoms = new ArrayList<LayoutComposition>();
        for (int i = 0; i < numberOfChildren; i++) {
            currentAtoms.add(allChildrenAtoms.get(i).get(0));
        }

        boolean allHaveMoreAtoms = true;

        LayoutComposition lastInserted = null;

        while (allHaveMoreAtoms) {

            LayoutComposition currentCandidate = this.factory.instance(
                    subtreeRoot, currentAtoms, false, this.nodeDistance,
                    this.nodesWithDimensions).get(0);

            if (!currentCandidate.dominates(lastInserted)) {
                mergeResult.add(currentCandidate);
                lastInserted = currentCandidate;
            }

            // Look for the child whose currentAtom has maximum width...

            double maximumWidth = -1;
            int maximumWidthIndex = -1;

            for (int i = 0; i < numberOfChildren; i++) {
                LayoutComposition currentComposition = currentAtoms.get(i);
                double currentCompositionWidth = currentComposition.getWidth();
                if (maximumWidth < currentCompositionWidth) {
                    maximumWidth = currentCompositionWidth;
                    maximumWidthIndex = i;
                }
            }

            // Now advance the index in the found child by one

            currentIndices[maximumWidthIndex]++;

            // Does it still have atoms left now?
            if (currentIndices[maximumWidthIndex] >= allChildrenAtoms.get(
                    maximumWidthIndex).size()) {
                allHaveMoreAtoms = false;
            } else {
                // change currentAtoms
                currentAtoms.set(maximumWidthIndex, allChildrenAtoms.get(
                        maximumWidthIndex).get(
                        currentIndices[maximumWidthIndex]));
            }

        }

        return mergeResult;

    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
