package org.graffiti.plugins.algorithms.labeling.finitePositionsIndividualWeighting;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.TreeSet;

import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;

/**
 * This class represents a possible or final label position.
 * <p>
 * At this level, there is no distinction between different sorts of labels
 * (such as edge or node labels). All share positioning routines and some
 * routines to measure the position quality. <!---
 * <p>
 * The various parent objects are to be provided and set by inheritors'
 * construction. Every constructor needs to be <i>reversible</i> by the routine
 * <code>applyToLabel()</code>, if the label position was not touched. --->
 * <p>
 * All label are rendered as axis parallel rectangles.
 * <p>
 * <b><i>Implementor's rant</i></b>:
 * <ul>
 * <li>about <tt>PositionAttribute</tt> - this class would be a perfect
 * candidate to host a routine <tt>getPositionAttribute()</tt>, but this would
 * make no sense, since the routines available in <tt>PositionAttribute</tt> are
 * of no use.
 * </ul>
 * 
 * @author scholz
 */
public abstract class LabelPosition {

    /** Overall quality of this label position */
    private double quality;

    /**
     * Preference of this label position; used to discern among different
     * positions that are equal otherwise
     */
    private double preference;

    /** used to determine overlapping label placements */
    public final LabelLocator parentLocator;

    private LinkedList<LabelPosition> collisions;

    /**
     * List of locators of colliding candidates. Every locator counts only once,
     * regardless of how many of its candidates are colliding.
     * <p>
     * <b>Note</b>: As locators change their priorities, only a data structure
     * is to be used which does not rely on the natural ordering of
     * <code>LabelLocator</code>s. <br>
     * However, it is possible to use a comparator-based hierarchical data
     * structure that relies on IDs rather than the natural ordering.
     */
    private LinkedList<LabelLocator> collidingLocators;

    private int secondOrderCollisions;
    private int nodeOverlaps;
    private int edgeOverlaps;

    public LabelPosition(LabelLocator parentLocator, final double preference) {
        collisions = new LinkedList<LabelPosition>();
        collidingLocators = new LinkedList<LabelLocator>();
        this.parentLocator = parentLocator;
        this.preference = preference;
        this.quality = preference;
        Statistics.numCandidates++;
    }

    // /**
    // * Factory method for various types of <tt>LabelAttributes</tt>.
    // * <p>Takes the same absolute offset as the corresponding label.
    // */
    // public static LabelPosition extractFromLabel(LabelAttribute label) {
    // // dynamic type checks
    // if (label instanceof NodeLabelAttribute)
    // return new NodeLabelPosition((NodeLabelAttribute)label);
    // if (label instanceof EdgeLabelAttribute)
    // return new EdgeLabelPosition((EdgeLabelAttribute)label);
    // throw new IllegalArgumentException(
    // "unexpected type of LabelAttribute. Maybe not implemented. ");
    // }
    //    
    // /**
    // * Factory method for various types of <tt>LabelAttributes</tt>.
    // * <p>Overwrites the <tt>absoluteOffset</tt> field.
    // */
    // public static LabelPosition extractFromLabelWithOffset(
    // LabelAttribute label, GeometricalVector absoluteOffset) {
    //        
    // LabelPosition labelPos = extractFromLabel(label);
    //        
    // // set superclass fields
    // labelPos.setAbsoluteOffset(absoluteOffset);
    //        
    // return labelPos;
    // }

    // following method not working, because the parent's parent (Node or Edge)
    // would be needed for position generation.
    // /**
    // * generates a number of candidate positions for the given label
    // * @param quantity - the number of positions to generate
    // * @return a given number of different positions for placing the given
    // * label
    // */
    // public static List<LabelPosition> extractNumberOfPositions(int quantity)
    // {
    // throw new UnsupportedOperationException(
    // ".extractNumberOfPositions() not implemented");
    // }

    // /**
    // * Applies this label position to the corresponding graph label.
    // */
    // public abstract void applyToLabel();

    /**
     * Adds a new collision partner for this label position. Collisions make a
     * position candidate less attractive at selection phase.
     * <p>
     * Since all collisions with position candidates of the same locator should
     * count as one collision. Thus, for collision counting issues, the list of
     * colliding locators is consulted.
     * <p>
     * <b>Usage note</b>: There are no checks for duplicates, so the caller has
     * to make sure that this routine is not called more often than once with
     * the same parameter.
     * 
     * @param collisionPartner
     *            - an overlapping label position originating from a different
     *            label locator (labels overlapping itself should not count); <br>
     *            (the caller has to assure, that the label locator is
     *            different)
     */
    public void markCollision(LabelPosition collisionPartner) {
        if (!collidingLocators.contains(collisionPartner.parentLocator)) {
            collidingLocators.add(collisionPartner.parentLocator);
        }
        collisions.add(collisionPartner);
        recalculateQuality();
    }

    /**
     * Removes a previously marked position candidate overlap.
     * <p>
     * This needs to be done in the candidate selection phase. If a label
     * position is chosen, all other position candidates are dropped and their
     * collisions are removed.
     * <p>
     * <b>Running time</b>: linear to the number of marked collisions (see
     * <tt>getNumberOfCollisions()</tt>)
     * 
     * @param sortedLocators
     */
    public void unmarkCollision(LabelPosition collisionPartner,
            TreeSet<LabelLocator> sortedLocators) {
        LabelLocator partnersLocator = collisionPartner.parentLocator;
        boolean areMoreLocatorPositionsColliding = false;
        boolean isCollisionMarked = false;
        LabelPosition pos;
        for (ListIterator<LabelPosition> it = collisions.listIterator(); it
                .hasNext();) {
            pos = it.next();
            if (pos == collisionPartner) {
                it.remove();
                isCollisionMarked = true;
            } else if (pos.parentLocator == partnersLocator) {
                areMoreLocatorPositionsColliding = true;
            }
        }

        if (!areMoreLocatorPositionsColliding) {
            if (!collidingLocators.remove(partnersLocator))
                throw new IllegalStateException(
                        "inconsistency of colliding locators list."
                                + "Collision partner's locator is not in list. ");
        }

        if (!isCollisionMarked)
            throw new IllegalStateException("collision is not marked "
                    + "with the given collision partner");

        // avoid concurrent modifications
        boolean isRemaining = sortedLocators.remove(parentLocator);
        recalculateQuality();
        if (isRemaining) {
            sortedLocators.add(parentLocator);
        }

    }

    /**
     * Removes all previously marked position candidate overlap with this
     * position.
     * <p>
     * This routine is called upon a dropped position candidate. Obviously, a
     * collision with a dropped candidate needs not to be concerned.
     * 
     * @param sortedLocators
     */
    public final void unmarkAllCollisions(TreeSet<LabelLocator> sortedLocators) {
        for (LabelPosition collisionPartner : collisions) {
            collisionPartner.unmarkCollision(this, sortedLocators);
        }
    }

    /**
     * @return the number of label positions of other label locators this label
     *         position overlaps with
     */
    public int getNumberOfCollisions() {
        return collidingLocators.size();
    }

    /**
     * @return an iterator for read access to the collision partners
     */
    public Iterator<LabelPosition> getCollisionsIterator() {
        return collisions.iterator();
    }

    public void mark2ndOrderCollision() {
        secondOrderCollisions++;
        recalculateQuality();
    }

    public void markNodeOverlap() {
        nodeOverlaps++;
        recalculateQuality();
    }

    public void markEdgeOverlap() {
        edgeOverlaps++;
        recalculateQuality();
    }

    /**
     * @return the number of other applied label locators this label position
     *         overlaps with
     */
    public int getNumberOf2ndOrderCollisions() {
        return secondOrderCollisions;
    }

    public boolean isParentOfCollidingCandidateApplied() {
        for (LabelPosition collidingCandidate : collisions) {
            if (collidingCandidate.parentLocator.isAppliedYet())
                return true;
        }
        return false;
    }

    // POSITIONING ROUTINES

    /**
     * absolute coordinates of the label's upper left vertex (the vertex with
     * minimal x and y values)
     */
    public abstract GeometricalVector getCoordinateUpperLeft();

    /**
     * absolute coordinates of the label's lower right vertex (the vertex with
     * maximal x and y values)
     */
    public abstract GeometricalVector getCoordinateLowerRight();

    public double getPreference() {
        return preference;
    }

    public void setPreference(double preference) {
        this.preference = preference;
    }

    /**
     * Recalculates the candidate quality
     */
    private void recalculateQuality() {
        this.quality = FinitePositionsAlgorithmIndividualWeighting.positionPreferenceWeight
                * preference
                - FinitePositionsAlgorithmIndividualWeighting.labelOverlapWeight
                // -
                // FinitePositionsAlgorithmIndividualWeighting.candidateOverlapWeight)
                * secondOrderCollisions
                - FinitePositionsAlgorithmIndividualWeighting.nodeOverlapWeight
                * nodeOverlaps
                - FinitePositionsAlgorithmIndividualWeighting.edgeOverlapWeight
                * edgeOverlaps
                - FinitePositionsAlgorithmIndividualWeighting.candidateOverlapWeight
                * collidingLocators.size();
        parentLocator.recalculatePlacementQuality();
    }

    /**
     * @return the current overall placement quality of this position candidate
     */
    public double getQuality() {
        return quality;
    }

    public int getEdgeOverlaps() {
        return edgeOverlaps;
    }

    public int getNodeOverlaps() {
        return nodeOverlaps;
    }
}
