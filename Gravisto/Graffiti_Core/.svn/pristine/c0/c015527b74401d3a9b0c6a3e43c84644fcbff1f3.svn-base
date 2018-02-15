// =============================================================================
//
//   DebugWindow.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.debug;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DebugWindow extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = -6645461311660846786L;

    private static DebugWindow debugWindow;

    private class ShowPdfButtonAction extends AbstractAction implements
            ListSelectionListener {
        /**
         * 
         */
        private static final long serialVersionUID = -6784461044164657381L;
        DebugSession session = null;

        public void actionPerformed(ActionEvent e) {
            displayPdf(session);
        }

        public ShowPdfButtonAction() {
            super("Show PDF");
            setEnabled(false);
        }

        /*
         * @see
         * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.
         * event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e) {
            session = (DebugSession) list.getSelectedValue();
            setEnabled(session != null);
        }
    };

    private ShowPdfButtonAction showPdfButtonAction = new ShowPdfButtonAction();

    private class SavePdfButtonAction extends AbstractAction implements
            ListSelectionListener {
        /**
         * 
         */
        private static final long serialVersionUID = 660754283272819451L;
        DebugSession session = null;

        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(".pdf");
                }

                @Override
                public String getDescription() {
                    return "*.pdf";
                }
            });
            if (fileChooser.showSaveDialog(DebugWindow.this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                if (file.getName().indexOf(".") == -1) {
                    file = new File(file.getName() + ".pdf");
                }
                savePdf(session, fileChooser.getSelectedFile());
            }
        }

        public SavePdfButtonAction() {
            super("Save PDF As...");
            setEnabled(false);
        }

        /*
         * @see
         * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.
         * event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e) {
            session = (DebugSession) list.getSelectedValue();
            setEnabled(session != null);
        }
    };

    private SavePdfButtonAction savePdfButtonAction = new SavePdfButtonAction();

    private class DeleteButtonAction extends AbstractAction implements
            ListSelectionListener {
        /**
         * 
         */
        private static final long serialVersionUID = -1455466816481010644L;
        DebugSession session = null;

        public void actionPerformed(ActionEvent e) {
            DebugSession.remove(session);
            session = null;
        }

        public DeleteButtonAction() {
            super("Delete");
            setEnabled(false);
        }

        /*
         * @see
         * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.
         * event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e) {
            session = (DebugSession) list.getSelectedValue();
            setEnabled(session != null);
        }
    };

    private DeleteButtonAction deleteButtonAction = new DeleteButtonAction();

    private JList list;

    public static DebugWindow get() {
        if (debugWindow == null) {
            debugWindow = new DebugWindow();
        }
        return debugWindow;
    }

    public static boolean exists() {
        return debugWindow != null;
    }

    private DebugWindow() {
        super("Debug");
        setAlwaysOnTop(true);
        setSize(300, 200);
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
        add(buttonPanel, BorderLayout.NORTH);
        buttonPanel.add(new JButton(showPdfButtonAction));
        buttonPanel.add(new JButton(savePdfButtonAction));
        buttonPanel.add(new JButton(deleteButtonAction));
        list = new JList(DebugSession.getSessionListModel());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setPreferredSize(new Dimension(200, 0));
        list.addListSelectionListener(showPdfButtonAction);
        list.addListSelectionListener(savePdfButtonAction);
        list.addListSelectionListener(deleteButtonAction);
        add(list, BorderLayout.CENTER);
    }

    private void displayPdf(DebugSession session) {
        try {
            File tmpFile = File.createTempFile("gravistodebug", ".pdf");
            session.writePdf(new FileOutputStream(tmpFile));
            // TODO: platform independence
            // maybe SWT code (class Process) useful
            // Runtime.getRuntime().exec("cmd.exe /C start "
            // + tmpFile.getAbsolutePath());
            Runtime.getRuntime().exec("evince " + tmpFile.getAbsolutePath());
        } catch (IOException e) {
        }
    }

    private void savePdf(DebugSession session, File file) {
        try {
            session.writePdf(new FileOutputStream(file));
        } catch (IOException e) {
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
