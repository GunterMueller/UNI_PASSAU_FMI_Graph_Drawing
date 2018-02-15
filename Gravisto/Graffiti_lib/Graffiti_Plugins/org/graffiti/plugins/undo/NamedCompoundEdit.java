package org.graffiti.plugins.undo;

import javax.swing.UIManager;
import javax.swing.undo.CompoundEdit;

public class NamedCompoundEdit extends CompoundEdit {
    /**
     * 
     */
    private static final long serialVersionUID = 4990172723845924505L;
    private String name;

    public NamedCompoundEdit(String name) {
        this.name = name;
    }

    @Override
    public String getPresentationName() {
        return name;
    }

    @Override
    public String getUndoPresentationName() {
        return UIManager.getString("AbstractUndoableEdit.undoText") + " "
                + getPresentationName();
    }

    @Override
    public String getRedoPresentationName() {
        return UIManager.getString("AbstractUndoableEdit.redoText") + " "
                + getPresentationName();
    }

}
