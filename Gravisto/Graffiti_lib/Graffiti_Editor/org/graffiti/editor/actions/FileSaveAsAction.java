// =============================================================================
//
//   FileSaveAsAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FileSaveAsAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.graffiti.core.Bundle;
import org.graffiti.core.GenericFileFilter;
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
 * The action for saving a graph to a named file.
 * 
 * @version $Revision: 5768 $
 */
public class FileSaveAsAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = 2433257469576696651L;

    /** DOCUMENT ME! */
    private IOManager ioManager;

    /** DOCUMENT ME! */
    private SessionManager sessionManager;

    /** DOCUMENT ME! */
    private Bundle bundle;

    /** Remember return value of last file chooser dialog. */
    private int returnValue;

    // private JFileChooser fc;
    public FileSaveAsAction(MainFrame mainFrame, IOManager ioManager,
            SessionManager sessionManager, Bundle bundle) {
        super("file.saveAs", mainFrame);
        this.ioManager = ioManager;
        this.sessionManager = sessionManager;
        this.bundle = bundle;

        // Used for better description in the context menu
        putValue(NAME, bundle.getString("menu." + getName()));
        putValue(SHORT_DESCRIPTION, getName());
        putValue(SMALL_ICON, bundle.getIcon("toolbar." + getName() + ".icon"));

        // fc = new JFileChooser();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean isEnabled() {
        return ioManager.hasOutputSerializer()
                && sessionManager.isSessionActive();
    }

    /**
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /**
     * Get return value of last file chooser dialog.
     * 
     * @return Return value of file chooser.
     */
    public int getReturnValue() {
        return returnValue;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
        JFileChooser fc = ioManager.createSaveFileChooser();

        EditorSession session = (EditorSession) mainFrame.getActiveSession();
        String lastFileName = session.getFileNameForSaveDialog();
        File lastFile = new File(lastFileName);
        FileFilter[] fileFilters = fc.getChoosableFileFilters();
        for (FileFilter ff : fileFilters) {
            if (ff.accept(lastFile)) {
                fc.setFileFilter(ff);
                break;
            }
        }

        int lastDotIndex = lastFileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            lastFileName = lastFileName.substring(0, lastDotIndex);
        }
        fc.setSelectedFile(new File(lastFileName));

        boolean needFile = true;

        while (needFile) {
            returnValue = fc.showDialog(mainFrame, bundle
                    .getString("menu.file.saveAs"));

            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                ioManager.storeSelectedFolder(file.getParent());
                String fileName = file.getName();

                // System.err.println(fileName);
                String ext = "";

                if (fileName.indexOf(".") == -1) {
                    ext = ((GenericFileFilter) fc.getFileFilter())
                            .getExtension();
                    fileName = file.getName() + ext;
                    file = new File(file.getAbsolutePath() + ext);
                } else {
                    ext = fileName.substring(fileName.lastIndexOf("."));
                }

                // System.err.println(fileName);
                if (file.exists()) {
                    if (JOptionPane.showConfirmDialog(mainFrame,
                            "Do you want to overwrite the existing file "
                                    + fileName + "?", "Overwrite File?",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                        needFile = false;
                    }
                } else {
                    needFile = false;
                }

                if (!needFile) {
                    try {
                        OutputSerializer os = ioManager
                                .getOutputSerializer(ext);

                        boolean okSelected = GraffitiSingleton
                                .showParameterDialog(os);
                        if (!okSelected)
                            return;

                        os.write(new FileOutputStream(file), mainFrame
                                .getActiveSession().getGraph());
                        mainFrame.getActiveSession().getGraph().setModified(
                                false);
                    } catch (IOException ioe) {
                        mainFrame.showMesssage("Error: Could not save file.",
                                MessageListener.ERROR);
                        ioe.printStackTrace();

                        // todo: add exception handling
                    } catch (IllegalAccessException iae) {
                        mainFrame.showMesssage("Error: Could not save file.",
                                MessageListener.ERROR);
                        iae.printStackTrace(System.err);
                    } catch (InstantiationException ie) {
                        mainFrame.showMesssage("Error: Could not save file.",
                                MessageListener.ERROR);
                        ie.printStackTrace(System.err);
                    }

                    session.setFileName(file.toURI());
                    mainFrame.fireSessionDataChanged(session);
                }
            } else {
                // leave loop
                needFile = false;
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
