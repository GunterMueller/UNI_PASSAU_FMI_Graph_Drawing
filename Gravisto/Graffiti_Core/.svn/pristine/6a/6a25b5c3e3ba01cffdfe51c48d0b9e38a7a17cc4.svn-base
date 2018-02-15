// =============================================================================
//
//   IsomorphismParameterDialog.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.isomorphism;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

import org.graffiti.core.Bundle;
import org.graffiti.editor.GraffitiInternalFrame;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.MainFrame;
import org.graffiti.editor.dialog.AbstractParameterDialog;
import org.graffiti.editor.dialog.ParameterEditPanel;
import org.graffiti.managers.EditComponentManager;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.selection.Selection;

/**
 * This dialog consists of a <code>GraphChoosingPanel</code> and a
 * <code>ParameterEditPanel</code>.
 * <p>
 * It lets you choose a predetermined number of graphs from a list of checkboxes
 * in the GraphChoosingPanel, and specify some additional parameters in the
 * ParameterEditPanel.
 * <p>
 * It's "OK"-button is only activated when the determined number of graphs is
 * selected.
 * <p>
 * When "OK" is pressed, the windows of the selected graphs are tiled.
 * 
 * @author mary-k
 * @version $Revision$ $Date$
 */
public class IsomorphismParameterDialog extends AbstractParameterDialog
        implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = -8496383659160418433L;

    /**
     * Lets you choose graphs from a list of all currently open windows.
     */
    protected GraphChoosingPanel graphPanel;

    /**
     * Lets you determine some parameters for the algorithm.
     */
    protected ParameterEditPanel paramEditPanel;

    /**
     * Necessary buttons...
     */
    protected JButton ok, cancel;

    /**
     * The algorithm, during the execution of which this dialog was opened.
     */
    protected AbstractIsomorphism algorithmType;

    private JLabel instruction, description;

    /**
     * <code>true</code>, if the user selected the "OK"-button in this dialog.
     */
    private Boolean selectedOk = false;

    /**
     * Constructor of this dialog.
     * 
     * @param compManager
     *            maps from a displayable class name to the class name of a
     *            <code>ValueEditComponent</code>
     * @param parent
     *            the parent of this dialog
     * @param params
     *            the array of parameters to edit in this dialog
     * @param sel
     *            currently selected objects in the active window (not needed)
     * @param algoName
     *            the title for this dialog: the name of the algorithm, during
     *            the execution of which this dialog was opened
     * @param algo
     *            the algorithm, during the execution of which this dialog was
     *            opened
     */
    public IsomorphismParameterDialog(EditComponentManager compManager,
            MainFrame parent, Parameter<?>[] params, Selection sel,
            String algoName, AbstractIsomorphism algo) {
        super(parent, true);

        algorithmType = algo;
        paramEditPanel = new ParameterEditPanel(params, compManager
                .getEditComponents(), sel);
        graphPanel = new GraphChoosingPanel(this, algorithmType);

        // TODO: Text in properties einfgen
        Bundle bundle = Bundle.getCoreBundle();
        instruction = new JLabel("Please choose the two graphs "
                + "that you would like to test for isomorphism:");
        description = new JLabel(bundle.getString("run.dialog.desc"));
        ok = new JButton(bundle.getString("run.dialog.button.run"));
        cancel = new JButton(bundle.getString("run.dialog.button.cancel"));
        ok.setEnabled(false);
        ok.addActionListener(this);
        cancel.addActionListener(this);

        setTitle(algoName);
        defineLayout();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void defineLayout() {
        // same size as DefaultParameterDialog
        setSize(420, 320);
        setResizable(false);
        getContentPane().setLayout(new GridBagLayout());

        // add GraphChoosingPanel to this dialog
        GridBagConstraints labelConstraints = new GridBagConstraints(0, 0, 3,
                1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(8, 8, 0, 8), 0, 0);
        add(instruction, labelConstraints);
        GridBagConstraints graphConstraints = new GridBagConstraints(0, 1, 3,
                1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(8, 8, 8, 8), 0, 0);
        add(graphPanel, graphConstraints);

        // add ParameterEditPanel to this dialog
        GridBagConstraints descConstraints = new GridBagConstraints(0, 3, 3, 1,
                1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH,
                new Insets(8, 8, 0, 8), 0, 0);
        add(description, descConstraints);
        GridBagConstraints paramConstraints = new GridBagConstraints(0, 4, 3,
                1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(8, 8, 8, 8), 0, 0);
        add(paramEditPanel, paramConstraints);

        // add "OK"- and "Cancel"-button
        getRootPane().setDefaultButton(ok);
        GridBagConstraints cancelConstraints = new GridBagConstraints(1, 5, 1,
                1, 1, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 8, 8, 8), 0, 0);
        add(cancel, cancelConstraints);
        GridBagConstraints okConstraints = new GridBagConstraints(2, 5, 1, 1,
                0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(0, 8, 8, 8), 0, 0);
        add(ok, okConstraints);
    }

    /**
     * Returns the parameters that have been edited by the user, i.e. the graphs
     * that have been chosen and all other parameters that have been defined.
     * This method returns an array whose first fields contain the graphs, and
     * the rest of the fields contain the values of the other parameters. You
     * should know how many graphs you let the user choose in order to
     * interprete the result of this method correctly.
     * 
     * @see org.graffiti.editor.dialog.ParameterDialog#getEditedParameters()
     */
    public Parameter<?>[] getEditedParameters() {
        Parameter<?>[] graphs = graphPanel.getUpdatedParameters();
        Parameter<?>[] params = paramEditPanel.getUpdatedParameters();
        Parameter<?>[] all = new Parameter[graphs.length + params.length];
        for (int i = 0; i < graphs.length; i++) {
            all[i] = graphs[i];
        }
        for (int i = 0; i < params.length; i++) {
            all[graphs.length + i] = params[i];
        }
        return all;
    }

    /**
     * @see org.graffiti.editor.dialog.ParameterDialog#isOkSelected()
     */
    public boolean isOkSelected() {
        return selectedOk;
    }

    /**
     * Registers and reacts to clicks on "OK"- or "Cancel"-button of this
     * dialog.
     * <p>
     * When "OK" is pressed, the windows of the selected graphs are tiled.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == cancel) {
            dispose();
        } else if (src == ok) {
            selectedOk = true;
            tileWindows();
            dispose();
        }
    }

    // Tiles the windows of the selected graphs.
    private void tileWindows() {
        Parameter<?>[] graphs = graphPanel.getUpdatedParameters();
        GraffitiSingleton single = GraffitiSingleton.getInstance();
        List<GraffitiInternalFrame> frames = single.getMainFrame()
                .getActiveFrames();
        GraffitiInternalFrame[] chosenGraphs = new GraffitiInternalFrame[graphs.length];
        // pick frames of chosenGraphs
        int k = 0;
        for (GraffitiInternalFrame frame : frames) {
            for (int i = 0; i < graphs.length; i++) {
                if ((frame.getSession().getFileNameAsString() + " " + frame
                        .getSession().getId()).equals(graphs[i].getName())) {
                    chosenGraphs[k] = frame;
                    k++;
                    break;
                }
            }
            try {
                frame.setMaximum(false);
                frame.setIcon(false);
            } catch (PropertyVetoException pve) {
                // should not happen
                pve.printStackTrace();
                assert false;
            }
        }
        if (chosenGraphs.length > 0) {
            JInternalFrame dummy = chosenGraphs[0];
            JDesktopPane desktop = dummy.getDesktopPane();
            Dimension deskSize = desktop.getSize();
            Dimension minSize = desktop.getSize();
            for (JInternalFrame frame : chosenGraphs) {
                Dimension min = frame.getMinimumSize();
                minSize.width = Math.min(minSize.width, min.width);
                minSize.height = Math.min(minSize.height, min.height);
            }
            // number of rows & columns
            double maxColumns = (double) deskSize.width / minSize.width;
            double maxRows = (double) deskSize.height / minSize.height;

            int cols = Math.max((int) Math.rint(Math
                    .sqrt((chosenGraphs.length * maxColumns) / maxRows)), 1);
            int rows = (chosenGraphs.length / cols);

            while ((cols * rows) < chosenGraphs.length) {
                rows++;
            }

            // calculate frame positions
            for (int i = 0; i < chosenGraphs.length; i++) {
                JInternalFrame frame = chosenGraphs[i];

                int width = deskSize.width / cols;
                int height = deskSize.height / rows;

                int x = (i % cols) * width;
                int y = (i / cols) * height;

                // fill up last row and column
                if ((i % cols) == (cols - 1)) {
                    width = deskSize.width - x;
                }

                if ((i / cols) == (rows - 1)) {
                    height = deskSize.height - y;
                }

                frame.toFront();
                frame.setBounds(x, y, width, height);
            }
        }
    }

    /**
     * Provides the possibility to enable and disable the "OK"-button of this
     * dialog (e.g. according to the number of currently selected graphs).
     * 
     * @param b
     *            <code>true</code> if the button shall be enabled,
     *            <code>false</code> if the button shall be disabled
     */
    public void setOkEnabled(Boolean b) {
        // Abfrage, ob die richtige Anzahl Graphen gewhlt, knnte man auch in
        // algorithm.check() machen wenn gewnscht: das hier weglassen und
        // einfach eine Fehlermeldung bringen, wenn auf ok geklickt wird
        ok.setEnabled(b);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
