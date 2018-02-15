package org.graffiti.plugins.algorithms.labeling.finitePositionsIndividualWeighting;

/**
 * This exception type officially states that an elaborated label placement
 * cannot be successfully performed, if there is no way to retrieve a label's
 * height and width. As this feature depends on which View is active, label
 * placement algorithms now are allowed to deny function if the current View is
 * not appropriate.
 * <p>
 * Yet, at the time of development, there is no way to determine, if a View
 * generally provides label widths, so the only accepted one is the
 * <tt>FastView</tt>
 */
public class ViewNotSupportedException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1861621298569201741L;

    public ViewNotSupportedException(String string) {
        super(string);
    }

}
