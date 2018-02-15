package org.graffiti.plugins.algorithms.labeling.finitePositionsIndividualWeighting;

import java.util.ListIterator;
import java.util.TreeSet;

import org.graffiti.graphics.LabelAttribute;

/**
 * A label locator contains a number of weighted position candidates for a given
 * label.
 * <p>
 * The implementation of the <code>Comparable</code> interface is consistent
 * with equals.
 */
public abstract class LabelLocator implements Comparable<LabelLocator> {
    private final int id;

    /**
     * Reflects the current position quality if this locator would be applied.
     * <p>
     * <b>Usage note</b>: This field should only be accessed by inheriting
     * classes, not externally. Unfortunately, there is no modifier for this
     * usage.
     * <p>
     * Several aspects influence position quality. The importance of any of them
     * is adjustible via the algorithm parameters.
     * <ul>
     * <li>Overlaps with placed labels
     * <li>Node overlaps
     * <li>Edge overlaps
     * <li>Overlaps with candidate positions
     * <li>Position preference ("quality" for <code>LabelPosition</code>s)
     * </ul>
     * <p>
     * The value is only valid after some collision detection (see
     * <code>LabelCandidateCollisionStructure</code>).
     * <p>
     * <b>Implementation note</b>: In the original design, quality was
     * determined lexicographically by the number of overlaps with placed labels
     * and by the number of candidate overlaps. At some later development stage
     * this lexicographic ordering was redesigned to allow priority inversions.
     */
    protected double placementQuality;

    protected LabelLocator() {
        this.id = Statistics.getUniqueLocatorID();
    }

    public abstract LabelAttribute getLabelAttribute();

    /**
     * <p>
     * <b><i>Developer's note</b></i>: Due to subtyping anomalies at container
     * types, there is no routine that retrieves the whole list of position
     * candidates.
     * 
     * @return a list iterator to iterate over the position candidates of this
     *         label locator
     */
    public abstract ListIterator<? extends LabelPosition> getCandidatesIterator();

    // /**
    // * <br><b>Note</b>: Immediately after construction a call to this function
    // * returns false values - a collision calculation has to be done before.
    // * @return true - if there is at least one position candidate which does
    // * not overlap another locator's candidate
    // * false - else
    // */
    // public abstract boolean isPlaceable();

    // /**
    // * <br><b>Note</b>: Immediately after construction a call to this function
    // * returns false values - a collision calculation has to be done before.
    // * @return the minimum number of different locators' candidates any of the
    // * locator's position candidates overlaps
    // * @see LabelCandidateCollisionStructure
    // */
    // public abstract int getNumberOfCollisions();
    //    

    /**
     * Recalculates the placement quality if this locator would be applied.
     * Changes the internal state: <tt>placementQuality</tt> is updated.
     * <p>
     * Reflects the current position quality if this locator would be applied.
     * <p>
     * Several aspects influence position quality. The importance of any of them
     * is adjustible via the algorithm parameters.
     * <ul>
     * <li>Overlaps with placed labels
     * <li>Node overlaps
     * <li>Edge overlaps
     * <li>Overlaps with candidate positions
     * <li>Position preference ("quality" for <code>LabelPosition</code>s)
     * </ul>
     * <p>
     * The value is only valid after some collision detection (see
     * <code>LabelCandidateCollisionStructure</code>).
     */
    protected final void recalculatePlacementQuality() {
        double candidatesQuality;
        double currentQuality = Double.NEGATIVE_INFINITY;
        for (ListIterator<? extends LabelPosition> it = getCandidatesIterator(); it
                .hasNext();) {
            candidatesQuality = it.next().getQuality();
            if (candidatesQuality > currentQuality) {
                currentQuality = candidatesQuality;
            }
        }
        this.placementQuality = currentQuality;
    }

    /**
     * Applies the currently best label position candidate to the corresponding
     * graph label.
     * <p>
     * This routine may only be called once (if so, <tt>isAppliedYet()</tt>
     * evaluates false).
     * 
     * @param sortedLocators
     *            - contains a sorted subset of all locators <br>
     *            <b>Note</b>: sorting will change during call
     * @throws IllegalStateException
     *             - if <tt>isAppliedYet()</tt> evaluates true
     */
    public abstract void applyToLabel(TreeSet<LabelLocator> sortedLocators);

    /**
     * @return true - if <tt>applyToLabel()</tt> has already been called upon
     *         this <tt>LabelLocator</tt> <br>
     *         false - otherwise
     */
    public abstract boolean isAppliedYet();

    public int compareTo(LabelLocator o) {
        if (o.placementQuality == this.placementQuality)
            return o.id - this.id;
        else if (o.placementQuality > this.placementQuality)
            return 1;
        else
            return -1;
    }

    public final double getPlacementQuality() {
        return placementQuality;
    }

}
