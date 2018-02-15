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
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.graffiti.graph.Node;

/**
 * AtomFinder, is used by layout algorithms to find all atoms of a tree
 * 
 * @author Andreas
 * @version $Revision$ $Date$
 */
public class AtomFinderGeneric implements AtomFinder {
    /**
     * AtomFinderTipover, is used by layout algorithms to find all drawings of a
     * tree that are atoms. This algorithm is very generic and thus can be used
     * for a variety of LayoutCompositions under the following conditions:<BR>
     * <BR>
     * 1. The Layout type has to have subtree-separation.<BR>
     * 2. The seperated areas must be rectangle.<BR>
     * <BR>
     * Because the algorithm in this class is so generic it is also horribly
     * inefficient. It uses the definition of A_u^VER and A_u^HOR (see "Two Tree
     * Drawing Conventions" by Eades et al) to construct the set of atoms.
     * Therefore this algorithm should only be used as a starting point.
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
    public AtomFinderGeneric(LayoutComposition factory, double nodeDistance,
            boolean nodesWithDimensions) {
        this.factory = factory;
        this.nodeDistance = nodeDistance;
        this.nodesWithDimensions = nodesWithDimensions;
    }

    /**
     * This finds all the atoms of the subtree of root and returns them in
     * width-decreasing and - because they are atoms - also in height-increasing
     * order.
     * 
     * @param root
     *            the root node of the subtree we are trying to find the atoms
     *            for.
     * @return list of atoms in width-decreasing order
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
     * This constructs a list of atoms from the horizontally merged and
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
     * This constructs all atoms that are possible by horizontally combining the
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
        // Mapping from width to LayoutComposition
        TreeMap<Double, LayoutComposition> mergeMap = new TreeMap<Double, LayoutComposition>();

        int numberOfChildren = allChildrenAtoms.size();

        int[] indexes = new int[numberOfChildren];

        int[] atomlistSizes = new int[numberOfChildren];

        ArrayList<LayoutComposition> currentAtoms = new ArrayList<LayoutComposition>();
        for (int i = 0; i < numberOfChildren; i++) {
            currentAtoms.add(allChildrenAtoms.get(i).get(0));
            atomlistSizes[i] = allChildrenAtoms.get(i).size();
        }

        boolean finished = false;

        while (!finished) {
            List<LayoutComposition> currentCandidates = factory.instance(
                    subtreeRoot, currentAtoms, true, this.nodeDistance,
                    this.nodesWithDimensions);

            for (LayoutComposition currentCandidate : currentCandidates) {
                double currentWidth = currentCandidate.getWidth();
                LayoutComposition alreadyFound = mergeMap.get(currentWidth);
                if (alreadyFound == null
                        || currentCandidate.getHeight() < alreadyFound
                                .getHeight()) {
                    mergeMap.put(currentWidth, currentCandidate);
                }
            }

            int numberOfCarries = inc(indexes, atomlistSizes);

            if (numberOfCarries == numberOfChildren) {
                finished = true;
            } else {
                for (int i = 0; i <= numberOfCarries; i++) {
                    currentAtoms
                            .set(i, allChildrenAtoms.get(i).get(indexes[i]));
                }
            }

        }

        LinkedList<LayoutComposition> candidates = new LinkedList<LayoutComposition>(
                mergeMap.values());

        LinkedList<LayoutComposition> mergeResult = new LinkedList<LayoutComposition>();

        // remember minimum height found so far...
        double minimumHeightSoFar = Double.MAX_VALUE;

        for (LayoutComposition currentCandidate : candidates) {
            if (currentCandidate.getHeight() < minimumHeightSoFar) {
                mergeResult.addLast(currentCandidate);
                minimumHeightSoFar = currentCandidate.getHeight();
            }
        }

        return mergeResult;

    }

    /**
     * This constructs all atoms that are possible by vertically combining the
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
        // Mapping from height to LayoutComposition
        TreeMap<Double, LayoutComposition> mergeMap = new TreeMap<Double, LayoutComposition>();

        int numberOfChildren = allChildrenAtoms.size();

        // We trust that they are all set to 0 by Java...
        int[] indexes = new int[numberOfChildren];

        int[] atomlistSizes = new int[numberOfChildren];

        ArrayList<LayoutComposition> currentAtoms = new ArrayList<LayoutComposition>();
        for (int i = 0; i < numberOfChildren; i++) {
            currentAtoms.add(allChildrenAtoms.get(i).get(0));
            atomlistSizes[i] = allChildrenAtoms.get(i).size();
        }

        boolean finished = false;

        while (!finished) {
            List<LayoutComposition> currentCandidates = factory.instance(
                    subtreeRoot, currentAtoms, false, this.nodeDistance,
                    this.nodesWithDimensions);

            for (LayoutComposition currentCandidate : currentCandidates) {
                double currentHeight = currentCandidate.getHeight();
                LayoutComposition alreadyFound = mergeMap.get(currentHeight);
                if (alreadyFound == null
                        || currentCandidate.getWidth() < alreadyFound
                                .getWidth()) {
                    mergeMap.put(currentHeight, currentCandidate);
                }
            }

            int numberOfCarries = inc(indexes, atomlistSizes);

            if (numberOfCarries == numberOfChildren) {
                finished = true;
            } else {
                for (int i = 0; i <= numberOfCarries; i++) {
                    currentAtoms
                            .set(i, allChildrenAtoms.get(i).get(indexes[i]));
                }
            }

        }

        LinkedList<LayoutComposition> candidates = new LinkedList<LayoutComposition>(
                mergeMap.values());

        LinkedList<LayoutComposition> mergeResult = new LinkedList<LayoutComposition>();

        // remember minimum width found so far...
        double minimumWidthSoFar = Double.MAX_VALUE;

        // Get rid of dominating compositions...
        for (LayoutComposition currentCandidate : candidates) {
            if (currentCandidate.getWidth() < minimumWidthSoFar) {
                mergeResult.addLast(currentCandidate);
                minimumWidthSoFar = currentCandidate.getWidth();
            }
        }

        return mergeResult;

    }

    /**
     * This method is used to emulate k-times nested loop. In essence this
     * method interprets the content of <code>array</code> as a number and
     * increases it by one everytime it is called. <code>digitRanges</code>
     * determines how far each digit can be counted.
     * 
     * @param array
     *            the array, of which the contents are interpreted as one single
     *            number.
     * @param digitRanges
     *            The ranges of the digits in the array.
     * @return the number of digits that were carried by increasing the number
     *         in <code>array</code> by one.
     */
    int inc(int[] array, int[] digitRanges) {

        boolean finished = false;

        int currentIndex = 0;

        while (!finished) {
            if (array[currentIndex] < digitRanges[currentIndex] - 1) {
                array[currentIndex]++;
                finished = true;
            } else {
                array[currentIndex] = 0;
                currentIndex++;
            }

            if (currentIndex == array.length) {
                finished = true;
            }
        }

        return currentIndex;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
