package org.graffiti.plugins.algorithms.labeling.finitePositions;

import java.util.ListIterator;

import org.graffiti.graphics.LabelAttribute;

/**
 * A label locator contains a number of weighted position candidates for a given
 * label.
 */
public abstract class LabelLocator implements Comparable<LabelLocator> {
    private final int id;

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

    /**
     * Gives access to position candidates.
     * <p>
     * <b><i>Developer's note</b></i>: Due to subtyping anomalies at container
     * types, there is no routine that retrieves the whole list of position
     * candidates.
     * 
     * @param candidateNum
     *            - number of position candidate to extract; must be in
     *            {0..numberOfCandidatePositions-1}
     * @return the position candidate of this label locator with given number
     */
    public abstract LabelPosition getCandidatePosition(int candidateNum);

    /**
     * <p>
     * <b><i>Developer's note</b></i>: Due to subtyping anomalies at container
     * types, there is no routine that retrieves the whole list of position
     * candidates.
     * 
     * @return the number of candidate positions at this label locator <br>
     *         (at state of implementation this is the same for all label
     *         locators)
     */
    public abstract int getNumberOfCandidatePositions();

    /**
     * @return true - if there is at least one position candidate which does not
     *         overlap another locator's candidate false - else
     */
    public abstract boolean isPlaceable();

    /**
     * Applies the currently best label position candidate to the corresponding
     * graph label.
     * <p>
     * This routine may only be called once (if so, <tt>isAppliedYet()</tt>
     * evaluates false).
     * 
     * @throws IllegalStateException
     *             - if <tt>isAppliedYet()</tt> evaluates true
     */
    public abstract void applyToLabel();

    /**
     * @return true - if <tt>applyToLabel()</tt> has already been called upon
     *         this <tt>LabelLocator</tt> <br>
     *         false - otherwise
     */
    public abstract boolean isAppliedYet();

    public int compareTo(LabelLocator o) {
        return o.id - this.id;
    }

}
