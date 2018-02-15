// =============================================================================
//
//   ShortCutAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ShortCutAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.util.Map;
import java.util.Set;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;

/**
 * ShowPopupMenuAction.java
 * 
 * @deprecated
 */
@Deprecated
public class ShortCutAction extends AbstractFunctionAction {
    /**
     * 
     */
    private static final long serialVersionUID = -6038743866161902975L;

    /**
     * Creates a new ShortCutAction object.
     * 
     * @param tool
     *            Given Tool
     */
    public ShortCutAction(AbstractEditingTool tool) {
    }

    /**
     * Returns the valid parameters
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public Map<String, Set<Object>> getValidParameters() {
        return construct1To3ParamMap("action", "cut", "copy", "paste");
    }

    /**
     * The action of this class
     * 
     * @param e
     *            The given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        MainFrame mainFrame = GraffitiSingleton.getInstance().getMainFrame();
        Object paramValue = getValue("action");

        if (paramValue == null)
            return;

        if (paramValue.equals("cut")) {
            mainFrame.getEditCut().actionPerformed(e);
        } else if (paramValue.equals("copy")) {
            mainFrame.getEditCopy().actionPerformed(e);
        } else if (paramValue.equals("paste")) {
            mainFrame.getEditPaste().actionPerformed(e);
        } else if (paramValue.equals("undo")) {
            mainFrame.getEditUndo().actionPerformed(e);
        } else if (paramValue.equals("redo")) {
            mainFrame.getEditRedo().actionPerformed(e);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
