// =============================================================================
//
//   GestureFeedbackProvider.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

/**
 * Provides feedback for {@code UserGesture}s raised by {@code InteractiveView}
 * s. Views implementing {@link InteractiveView} hand out a {@code
 * GestureFeedbackProvider} by
 * {@link InteractiveView#getGestureFeedbackProvider()} in order to allow
 * actions and tools to show feedback that is not reflected in the state of the
 * graph, selection, or attribute system, e.g. showing a selection rectangle,
 * temporarily highlighting a specific node, setting the mouse cursor etc. This
 * concept allows views with different identifying classes to share the same
 * actions for common user feedbacks.
 * <p>
 * <a href="package-summary.html#ConceptOverview"> Overview of the
 * trigger/action paradigm</a>.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface GestureFeedbackProvider {
    /**
     * Resets the state of the {@code GestureFeedbackProvider}. Should be called
     * on the {@code GestureFeedbackProvider} of the currently active view when
     * a tool is activated or deactivated or a sequence of user gestures
     * belonging together is completed. Clears all temporary feedbacks of the
     * related view.
     */
    public void reset();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
