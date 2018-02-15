// =============================================================================
//
//   FileSaveAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FileSaveAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.help.HelpContext;
import org.graffiti.managers.IOManager;
import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.plugin.io.OutputSerializer;
import org.graffiti.plugin.view.MessageListener;
import org.graffiti.session.EditorSession;
import org.graffiti.session.SessionManager;

/**
 * The action for saving a graph.
 * 
 * @version $Revision: 5768 $
 */
public class FileSaveAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = -3995072384772454090L;

    /** DOCUMENT ME! */
    private IOManager ioManager;

    /** DOCUMENT ME! */
    private SessionManager sessionManager;

    /**
     * Creates a new FileSaveAction object.
     * 
     * @param mainFrame
     *            DOCUMENT ME!
     * @param ioManager
     *            DOCUMENT ME!
     * @param sessionManager
     *            DOCUMENT ME!
     */
    public FileSaveAction(MainFrame mainFrame, IOManager ioManager,
            SessionManager sessionManager) {
        super("file.save", mainFrame);
        this.ioManager = ioManager;
        this.sessionManager = sessionManager;

        // Used for better description in the context menu
        putValue(NAME, coreBundle.getString("menu." + getName()));
        putValue(SHORT_DESCRIPTION, getName());
        putValue(SMALL_ICON, coreBundle.getIcon("toolbar." + getName()
                + ".icon"));
    }

    /**
     * DOCUMENT ME!
     * 
     * @return <code>true</code>, if the io manager contains a working output
     *         serializer and if the file is writeable.
     */
    @Override
    public boolean isEnabled() {
        String fullName;

        try {
            // these commands fail if the session has not yet been saved to
            // a file
            EditorSession session = (EditorSession) mainFrame
                    .getActiveSession();
            fullName = session.getFileName().getPath();
        } catch (Exception e) {
            return false;
        }

        try {
            String ext = getFileExt(fullName);

            File file = new File(fullName);

            if (file.canWrite()) {
                ioManager.getOutputSerializer("." + ext);

                // runtime error check, if exception, ioManager can not
                // handle current file for saving.
            } else
                return false;
        } catch (Exception e) {
            return false;
        }

        return (ioManager.hasOutputSerializer() && sessionManager
                .isSessionActive());
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
        // CK, 1.Juli.2003 Copied and modified from SaveAsAction
        EditorSession session;
        String fullName;

        try {
            session = (EditorSession) mainFrame.getActiveSession();
            fullName = session.getFileName().getPath();
        } catch (Exception err) {
            mainFrame.showMesssage("Could save graph to file.",
                    MessageListener.ERROR);

            return;
        }

        String ext = getFileExt(fullName);

        File file = new File(fullName);

        if (file.canWrite()) {
            try {
                OutputSerializer os = ioManager.getOutputSerializer("." + ext);

                boolean okSelected = GraffitiSingleton.showParameterDialog(os);
                if (!okSelected)
                    return;

                os.write(new FileOutputStream(file), mainFrame
                        .getActiveSession().getGraph());
                mainFrame.getActiveSession().getGraph().setModified(false);
            } catch (IOException ioe) {
                ioe.printStackTrace();

                // todo: add exception handling
            } catch (IllegalAccessException iae) {
                iae.printStackTrace(System.err);
            } catch (InstantiationException ie) {
                ie.printStackTrace(System.err);
            }

            mainFrame.fireSessionDataChanged(session);
        } else {
            mainFrame.showMesssage("Error: file not writable.",
                    MessageListener.ERROR);
            System.err.println("Error: file not writable. (FileSave-Action).");
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param fileName
     * 
     * @return Returns file extension from a given filename.
     */
    private String getFileExt(String fileName) {
        String workName;

        int lastSep = fileName.lastIndexOf(File.pathSeparator);

        if (lastSep == -1) {
            // no extension
            workName = fileName;
        } else {
            workName = fileName.substring(lastSep + 1);
        }

        int lastDot = workName.lastIndexOf('.');

        if (lastDot == -1)
            return "";
        else {
            String extension = workName.substring(lastDot + 1);

            return extension;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
