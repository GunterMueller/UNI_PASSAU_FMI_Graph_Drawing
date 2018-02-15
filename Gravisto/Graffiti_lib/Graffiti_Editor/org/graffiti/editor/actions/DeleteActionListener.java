package org.graffiti.editor.actions;

import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.SelectionEvent;
import org.graffiti.selection.SelectionListener;

/**
 * Class represents a listener, who is activated everytime the selection of the
 * graph changes, due to enable or disable the Cut-, Copy- or Paste-Button
 * correctly.
 * 
 * @author MH
 */
public class DeleteActionListener implements SelectionListener {
    /** Reference to the SelectionAction, which creates the listener * */
    private SelectionAction selectionAction;

    /**
     * Creates a new SelectionActionListener
     * 
     * @param selectionAction
     *            the SelectionAction which creates the Listener
     */
    public DeleteActionListener(SelectionAction selectionAction) {
        this.selectionAction = selectionAction;
    }

    /**
     * Calls the update-method of the SelectionAction-class, which checks if the
     * Cut-, Copy- or Paste-Button has to be enabled or disabled
     * 
     * @param e
     *            not used
     */
    public void selectionChanged(SelectionEvent e) {
        selectionAction.update();
    }

    /**
     * Calls the update-method of the SelectionAction-class, which checks if the
     * Cut-, Copy- or Paste-Button has to be enabled or disabled
     * 
     * @param e
     *            not used
     */
    public void selectionListChanged(SelectionEvent e) {
        selectionAction.update();
    }
}
