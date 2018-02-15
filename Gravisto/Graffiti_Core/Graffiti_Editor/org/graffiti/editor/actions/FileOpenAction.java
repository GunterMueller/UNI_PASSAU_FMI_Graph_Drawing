// =============================================================================
//
//   FileOpenAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FileOpenAction.java 5887 2011-05-03 10:39:41Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;

import org.graffiti.core.Bundle;
import org.graffiti.core.GenericFileFilter;
import org.graffiti.core.MultiFileFilter;
import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.managers.IOManager;
import org.graffiti.managers.ViewManager;
import org.graffiti.plugin.actions.GraffitiAction;

/**
 * The action for the file open dialog.
 */
public class FileOpenAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = -5210057290870019873L;

    /** DOCUMENT ME! */
    private IOManager ioManager;

    /**
     * The bundle used by this action.
     */
    private Bundle bundle;

    /** DOCUMENT ME! */
    private ViewManager viewManager;

    /**
     * Creates a new FileOpenAction object.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     * @param ioManager
     *            DOCUMENT ME!
     * @param viewManager
     *            DOCUMENT ME!
     * @param bundle
     *            the bundle used by this action.
     */
    public FileOpenAction(MainFrame mainFrame, IOManager ioManager,
            ViewManager viewManager, Bundle bundle) {
        super("file.open", mainFrame);
        this.ioManager = ioManager;
        this.viewManager = viewManager;
        this.bundle = bundle;

        // Used for better description in the context menu
        putValue(NAME, bundle.getString("menu." + getName()));
        putValue(SHORT_DESCRIPTION, getName());
        putValue(SMALL_ICON, bundle.getIcon("toolbar." + getName() + ".icon"));
    }

    /**
     * This action is enabled, if the editor's io manager contains an input
     * serializer.
     * 
     * @return <code>true</code>, if the io manager contains at least one input
     *         serializer.
     */
    @Override
    public boolean isEnabled() {
        return ioManager.hasInputSerializer() && viewManager.hasViews();
    }

    /**
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
        JFileChooser fc = ioManager.createOpenFileChooser();

        // fc.resetChoosableFileFilters();
        int returnVal = fc.showDialog(mainFrame, bundle
                .getString("menu.file.open"));

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            if (file.getName().indexOf(".") == -1) {
                String extension = "";
                if (fc.getFileFilter() instanceof GenericFileFilter) {
                    extension = ((GenericFileFilter) fc.getFileFilter())
                            .getExtension();
                } else if (fc.getFileFilter() instanceof MultiFileFilter) {
                    extension = ".graphml";
                    String[] extensions = ((MultiFileFilter) fc.getFileFilter())
                            .getAllExtensions();
                    for (int i = 0; i < extensions.length; i++) {
                        File tempFile = new File(file + "." + extensions[i]);
                        if (tempFile.exists()) {
                            extension = "." + extensions[i];
                            // System.out.println(extension);
                            i = extensions.length;
                        }
                    }
                }

                file = new File(file + extension);
            }
            ioManager.storeSelectedFolder(file.getParent());

            boolean succeeded = mainFrame.loadGraph(file);

            if (succeeded && (mainFrame != null)
                    && (mainFrame.getActiveSession() != null)
                    && (mainFrame.getActiveSession().getGraph() != null)) {
                mainFrame.getActiveSession().getGraph().setModified(false);
            }

        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
