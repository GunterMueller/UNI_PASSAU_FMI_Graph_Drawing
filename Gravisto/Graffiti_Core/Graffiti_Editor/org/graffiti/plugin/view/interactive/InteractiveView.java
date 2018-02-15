// =============================================================================
//
//   UserGestureSource.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import org.graffiti.plugin.view.View;
import org.graffiti.selection.SelectionModel;
import org.graffiti.session.EditorSession;
import org.graffiti.util.MutuallyReferable;

/**
 * Views implementing {@code InteractiveView} support the <a
 * href="package-summary.html#ConceptOverview">trigger/action paradigm</a>. The
 * tool system automatically shows only the tools created for this view.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @param <T>
 *            the common superclass of all views of the view family.
 * @see ViewFamily#getCommonSuperClass()
 */
public interface InteractiveView<T extends InteractiveView<T>> extends View,
        MutuallyReferable {
    /**
     * Sets the dispatcher of {@code UserGesture}s. When a new
     * {@link UserGesture} occurs, this view will pass it to the dispatcher. The
     * specified dispatcher needs not to be an instance of
     * {@link UserGestureDispatcher} as it can delegate the user gestures to a
     * real dispatcher.
     * 
     * @param dispatcher
     *            the dispatcher which the upcoming user gestures are passed to
     *            by this view.
     */
    public void setUserGestureDispatcher(UserGestureListener dispatcher);

    /**
     * Gets the dispatcher that is currently passed the user gestures to by this
     * view.
     * 
     * @return the dispatcher that is currently passed the user gestures to by
     *         this view.
     */
    public UserGestureListener getUserGestureDispatcher();

    /**
     * Is called when the {@code SelectionModel} has changed.
     * 
     * @param selectionModel
     *            the new {@link SelectionModel}.
     */
    public void setSelectionModel(SelectionModel selectionModel);

    /**
     * Is called when the {@code EditorSession} has changed.
     * 
     * @param editorSession
     *            the new {@link EditorSession}.
     */
    public void setEditorSession(EditorSession editorSession);

    /**
     * Returns the {@code EditorSession}.
     * 
     * @return the {@link EditorSession} containing this view.
     */
    public EditorSession getEditorSession();

    /**
     * Returns the {@code GraphElementFinder} provided by this view.
     * 
     * @return the {@code GraphElementFinder} provided by this view.
     */
    public GraphElementFinder getGraphElementFinder();

    /**
     * Returns the {@code GestureFeedbackProvider} provided by this view.
     * 
     * @return the {@code GestureFeedbackProvider} provided by this view.
     */
    public GestureFeedbackProvider getGestureFeedbackProvider();

    /**
     * Returns the view family. The returned object is shared by all views that
     * use the same tools, triggers and actions.
     * 
     * @return the view family.
     */
    public ViewFamily<T> getFamily();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
