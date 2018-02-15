// =============================================================================
//
//   PopupMenuCompatible.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import javax.swing.JPopupMenu;

/**
 * {@code GestureFeedbackProvider}s implementing {@code
 * PopupMenuCompatibleProvider} support showing a popup menu.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see PopupMenuItem
 * @see PopupMenuSelectionGesture
 */
public interface PopupMenuCompatibleProvider extends GestureFeedbackProvider {
    /**
     * Shows the specified popup menu over the main component of the view that
     * handed out this {@code GestureFeedbackProvider}.
     * 
     * @param menu
     *            the popup menu to be shown.
     * @param x
     *            the x coordinate position to popup the menu. The referred
     *            coordinate system is left to the {@link InteractiveView} that
     *            handed out this {@code GestureFeedbackProvider}.
     * @param y
     *            the y coordinate position to popup the menu.
     */
    public void show(JPopupMenu menu, double x, double y);

    /**
     * Makes the {@code InteractiveView} that handed out this {@code
     * GestureFeedbackProvider} pass the specified {@code
     * PopupMenuCompatibleProvider} to its dispatcher. This mechanism allows
     * actions to create very generic popup menu items. These items are then not
     * hard-wired with specific actions but rather correspond to user gestures,
     * which can be dynamically linked to actions by the <a
     * href="package-summary.html#Overview">trigger/action/tool system</a>.
     * 
     * @param pmsg
     *            the {@code PopupMenuSelectionGesture} to pass to the
     *            dispatcher.
     * @see InteractiveView#setUserGestureDispatcher(UserGestureListener)
     */
    public void relayMenuSelectionGesture(PopupMenuSelectionGesture pmsg);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
