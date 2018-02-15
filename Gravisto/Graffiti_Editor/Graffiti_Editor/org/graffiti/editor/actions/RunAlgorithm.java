// =============================================================================
//
//   RunAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RunAlgorithm.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.managers.EditComponentManager;
import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Runs an algorithm.
 * 
 * @version $Revision: 5768 $
 */
public class RunAlgorithm extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = -6626539181047681600L;

    /** The class name of the algorithm to run. */
    private String algorithmClassName;

    private Algorithm algorithm;

    /**
     * Constructor for RunAlgorithm.
     * 
     * @param algorithmClassName
     *            DOCUMENT ME!
     * @param name
     * @param mainFrame
     * @param editComponentManager
     *            DOCUMENT ME!
     */
    public RunAlgorithm(String algorithmClassName, String name,
            MainFrame mainFrame, EditComponentManager editComponentManager,
            Algorithm instance) {
        super(name, mainFrame);
        this.algorithmClassName = algorithmClassName;
        this.algorithm = instance;
    }

    /**
     * Get the name of the algorithm that this action will run.
     * 
     * @return the name of the algorithm that this action will run.
     */
    public String getAlgorithmClassName() {
        return algorithmClassName;
    }

    /**
     * @see javax.swing.Action#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        return mainFrame.isSessionActive();
    }

    /**
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent a) {
        GraffitiSingleton.runAlgorithm(algorithm);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
