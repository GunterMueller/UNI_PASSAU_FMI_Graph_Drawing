// =============================================================================
//
//   WaitGesture.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

/**
 * {@code UserGesture} representing doing nothing for a defined period of time.
 * It is provided for convenience and its use by {@link InteractiveView}s is not
 * prescribed.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class WaitGesture implements UserGesture {
    /**
     * Elapsed time since last time a {@code WaitGesture} was created for the
     * same period of inactivity.
     */
    private double deltaTime;

    /**
     * Elapsed time since the last {@code UserGesture} was created that was not
     * a {@code WaitGesture}.
     */
    private double totalTime;

    /**
     * Constructs a {@code WaitGesture}.
     * 
     * @param deltaTime
     *            elapsed time since last time a {@code WaitGesture} was created
     *            for the same period of inactivity.
     * @param totalTime
     *            elapsed time since the last {@code UserGesture} was created
     *            that was not a {@code WaitGesture}.
     */
    public WaitGesture(double deltaTime, double totalTime) {
        this.deltaTime = deltaTime;
        this.totalTime = totalTime;
    }

    /**
     * Returns the elapsed time since last time a {@code WaitGesture} was
     * created for the same period of inactivity.
     * 
     * @return the elapsed time since last time a {@code WaitGesture} was
     *         created for the same period of inactivity.
     */
    public double getDeltaTime() {
        return deltaTime;
    }

    /**
     * Returns the elapsed time since the last {@code UserGesture} was created
     * that was not a {@code WaitGesture}.
     * 
     * @return the elapsed time since the last {@code UserGesture} was created
     *         that was not a {@code WaitGesture}.
     */
    public double getTotalTime() {
        return totalTime;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
