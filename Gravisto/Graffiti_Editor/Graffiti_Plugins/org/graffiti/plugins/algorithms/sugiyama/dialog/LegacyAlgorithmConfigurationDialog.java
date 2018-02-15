// =============================================================================
//
//   AlgorithmConfigurationDialog.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LegacyAlgorithmConfigurationDialog.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.graffiti.core.Bundle;
import org.graffiti.editor.MainFrame;
import org.graffiti.editor.dialog.AbstractParameterDialog;
import org.graffiti.editor.dialog.ParameterEditPanel;
import org.graffiti.managers.EditComponentManager;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.CrossMinAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.layout.LayoutAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.levelling.LevellingAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.ComparableClassParameter;
import org.graffiti.plugins.algorithms.sugiyama.util.FindPhaseAlgorithms;
import org.graffiti.plugins.algorithms.sugiyama.util.PreferencesUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.selection.Selection;

/**
 * Deprecated implementation of a parameter dialog.
 * 
 * @deprecated kept old version of the algorithm configuration dialog for
 *             compatibility reasons (incremental sugiyama)
 */
@Deprecated
public class LegacyAlgorithmConfigurationDialog extends AbstractParameterDialog
        implements ActionListener, WindowListener {

    /**
     * 
     */
    private static final long serialVersionUID = -8110825911674928797L;

    /** The <code>Bundle</code> of the view type chooser. */
    protected static final Bundle bundle = Bundle.getCoreBundle();

    /** The panel used to display and change parameter values. */
    protected ParameterEditPanel paramEditPanel;

    /** The panel used to display and change parameters for each phase. */
    protected ParameterEditPanel[] paramEditPanels;

    /** The list of parameters, the user is editing. */
    protected Parameter<?>[] params;

    /** The value edit component manager, the edit panel needs. */
    private EditComponentManager editComponentManager;

    /** The dialog's cancel-button */
    private JButton cancel;

    /** The dialog's next-buttons. */
    private JButton next;

    /** The dialog's search-button */
    private JButton search;

    /** The dialog's ok-button */
    private JButton ok;

    /** The dialog's back-button */
    private JButton back;

    /** The dialog's default-button */
    private JButton defaultButton;

    /** The dialog's next-button on the first page */
    private JButton cont;

    /** The dialog's back-button on the second page */
    private JButton back2;

    /** The description of this dialog. */
    private JLabel description;

    /** The panel, which contains the parameters. */
    private JPanel paramsPanel;

    /** The panel that contains the buttons */
    private JPanel buttonsPanel;

    /** Selection-parameter */
    private Selection sel;

    /** <code>true</code>, if the user selected the ok button in this dialog. */
    private boolean selectedOk = false;

    private MainFrame parentMainFrame;

    boolean error;

    private SugiyamaData data;

    /** The logger */
    private static final Logger logger = Logger
            .getLogger(AlgorithmConfigurationDialog.class.getName());

    /**
     * Constructor for DefaultParameterDialog.
     * 
     * @param editComponentManager
     *            DOCUMENT ME!
     * @param parent
     *            the parent of this dialog.
     * @param parameters
     *            the array of parameters to edit in this dialog.
     * @param selection
     *            DOCUMENT ME!
     * @param algorithmName
     *            the name of the algorithm, to edit the parameters for.
     */
    public LegacyAlgorithmConfigurationDialog(
            EditComponentManager editComponentManager, MainFrame parent,
            Parameter<?>[] parameters, Selection selection,
            String algorithmName, SugiyamaData d) {
        super(parent, true);

        this.data = d;

        this.parentMainFrame = parent;

        this.editComponentManager = editComponentManager;

        this.params = parameters;

        getContentPane().setLayout(new BorderLayout());

        setTitle(algorithmName);
        setResizable(false);

        // TODO: add the button-text for "Next" and "Search for algorithms"
        // in the stringbundle
        next = new JButton("Next");
        cont = new JButton("Next");
        cancel = new JButton(bundle.getString("run.dialog.button.cancel"));
        search = new JButton("Search for algorithms");
        back = new JButton("Back");
        back2 = new JButton("Back");
        defaultButton = new JButton("Default parameters");
        // description = new
        // JLabel("Please select an algorithm for each phase.");
        description = new JLabel("Please configure the framework.");

        buttonsPanel = new JPanel();

        buttonsPanel.add(cont);
        buttonsPanel.add(cancel);
        // buttonsPanel.add(search);

        sel = selection;
        if (sel != null) {
            SelectionParameter selparam = (SelectionParameter) parameters[0];
            parameters[0] = new SelectionParameter(sel.getName(), selparam
                    .getDescription(), sel);
            if (!sel.getNodes().isEmpty()) {
                data.setStartNode(sel.getNodes().get(0));
            }
        }

        // paramsPanel = createValueEditContainer(params, selection);
        paramsPanel = new ParameterEditPanel(data.getAlgorithmParameters(),
                editComponentManager.getEditComponents(), sel);

        cont.setEnabled(true);

        getRootPane().setDefaultButton(cont);

        // defineLayoutAlgorithmChooser();
        defineLayoutFrameworkConfiguration();
        addListeners();

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * @see org.graffiti.editor.dialog.ParameterDialog#getEditedParameters()
     */
    public Parameter<?>[] getEditedParameters() {
        return this.paramEditPanel.getUpdatedParameters();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean isOkSelected() {
        return selectedOk;
    }

    /**
     * Toggles the action performed by the dialog if a button is pressed.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        // the user wants to cancel
        if (src == cancel) {
            dispose();
        }
        // next means, that the user has selected the algorithms and wants to
        // configure them now
        else if (src == next) {
            nextSelected();
        }
        // run sugiyama
        else if (src == ok) {
            boolean valid = true;
            for (ParameterEditPanel pep : paramEditPanels) {

                if ((pep != null) && (!pep.parametersAreValid())) {
                    valid = false;
                }
            }

            if (!valid) {
                String text = "The following parameters are not valid:\n";
                int i = 0;
                SugiyamaAlgorithm[] algos = data.getSelectedAlgorithms();
                for (ParameterEditPanel pep : paramEditPanels) {
                    if (pep != null) {
                        List<String> errorMessages = pep
                                .getErrorMessageListOfInvalidParameters();
                        if (errorMessages.size() > 0) {
                            text += "\nIn algorithm \"" + algos[i].getName()
                                    + "\":\n";
                        }
                        for (String s : errorMessages) {
                            text += s;
                        }
                    }
                    i++;
                }
                JOptionPane.showMessageDialog(this, text,
                        "Error in parameters", JOptionPane.ERROR_MESSAGE);
                return;
            }

            okSelected();
        }
        // the user wants to re-select the algorithms; the dialog has to be
        // rebuilt from scratch
        else if (src == back) {
            backSelected();
        }
        // the user wants to re-configure the framework
        else if (src == back2) {
            back2Selected();
        }
        // reset all algorithms to their default parameters
        else if (src == defaultButton) {
            defaultSelected();
        }
        // first page on the dialog - continue to the algorithm chooser now
        else if (src == cont) {
            continueSelected();
        }
        // search for algorithms and constraints
        else if (src == search) {
            parentMainFrame
                    .showMessageDialog("Your classpath will be searched"
                            + " for algorithms that\nimplement the SugiyamaAlgorithm-interf"
                            + "ace and\nclasses that implement the SugiyamaConstraint-inter"
                            + "face.\nThis might take a while, please be patient.\n\n"
                            + "Click the button below to start searching!");
            Cursor hourglass = new Cursor(Cursor.WAIT_CURSOR);
            setCursor(hourglass);
            // search the classpath for sugiyama-algorithms
            HashMap<String, String> algorithms = FindPhaseAlgorithms
                    .discoverAlgorithms(data);
            data.setPhaseAlgorithms(algorithms);
            ArrayList<String[]> alg;
            alg = FindPhaseAlgorithms.getPhaseAlgorithms(algorithms, data
                    .getAlgorithmType());

            HashSet<String> msg = FindPhaseAlgorithms.displayNewComponents(alg,
                    data);
            if (msg.size() > 0) {
                String message = "The following new algorithms were found:\n\n";
                Iterator<String> iter = msg.iterator();
                while (iter.hasNext()) {
                    message += "- " + iter.next() + "\n";
                }
                parentMainFrame.showMessageDialog(message);
            } else {
                parentMainFrame.showMessageDialog("No new algorithms found.");
            }
            Cursor normal = new Cursor(Cursor.DEFAULT_CURSOR);
            setCursor(normal);
            error = buildAlgorithmSelection(alg);

            // remove the old panel with the algorithm, add a new one and
            // re-create the layout of the dialog
            getContentPane().remove(paramsPanel);
            paramsPanel = createValueEditContainer(params, sel);
            defineLayoutAlgorithmChooser();
            pack();

        }
    }

    /**
     * @see java.awt.event.WindowListener#windowActivated(WindowEvent)
     */
    public void windowActivated(WindowEvent arg0) {
    }

    /**
     * @see java.awt.event.WindowListener#windowClosed(WindowEvent)
     */
    public void windowClosed(WindowEvent arg0) {
    }

    /**
     * @see java.awt.event.WindowListener#windowClosing(WindowEvent)
     */
    public void windowClosing(WindowEvent arg0) {
        dispose();
    }

    /**
     * @see java.awt.event.WindowListener#windowDeactivated(WindowEvent)
     */
    public void windowDeactivated(WindowEvent arg0) {
    }

    /**
     * @see java.awt.event.WindowListener#windowDeiconified(WindowEvent)
     */
    public void windowDeiconified(WindowEvent arg0) {
    }

    /**
     * @see java.awt.event.WindowListener#windowIconified(WindowEvent)
     */
    public void windowIconified(WindowEvent arg0) {
    }

    /**
     * @see java.awt.event.WindowListener#windowOpened(WindowEvent)
     */
    public void windowOpened(WindowEvent arg0) {
    }

    /**
     * Adds the listeners to the dialog - don't call this more than once, or the
     * dialog will start to lag if you add this as a listener multiple times. If
     * a button has two listeners, its triggered action will be performed twice!
     * Because of this behaviour, the method checks if you have already added
     * the AlgorithmConfigurationDialog as a listener.
     */
    private void addListeners() {
        boolean hasListener = false;
        for (int i = 0; i < cancel.getActionListeners().length; i++)
            if (cancel.getActionListeners()[i] == this) {
                hasListener = true;
            }
        if (!hasListener) {
            cancel.addActionListener(this);
        }

        hasListener = false;
        for (int i = 0; i < next.getActionListeners().length; i++)
            if (next.getActionListeners()[i] == this) {
                hasListener = true;
            }
        if (!hasListener) {
            next.addActionListener(this);
        }

        hasListener = false;
        for (int i = 0; i < search.getActionListeners().length; i++)
            if (search.getActionListeners()[i] == this) {
                hasListener = true;
            }
        if (!hasListener) {
            search.addActionListener(this);
        }

        hasListener = false;
        for (int i = 0; i < back.getActionListeners().length; i++)
            if (back.getActionListeners()[i] == this) {
                hasListener = true;
            }
        if (!hasListener) {
            back.addActionListener(this);
        }

        hasListener = false;
        for (int i = 0; i < defaultButton.getActionListeners().length; i++)
            if (defaultButton.getActionListeners()[i] == this) {
                hasListener = true;
            }
        if (!hasListener) {
            defaultButton.addActionListener(this);
        }

        hasListener = false;
        for (int i = 0; i < cont.getActionListeners().length; i++)
            if (cont.getActionListeners()[i] == this) {
                hasListener = true;
            }
        if (!hasListener) {
            cont.addActionListener(this);
        }

        hasListener = false;
        for (int i = 0; i < back2.getActionListeners().length; i++)
            if (back2.getActionListeners()[i] == this) {
                hasListener = true;
            }
        if (!hasListener) {
            back2.addActionListener(this);
        }

        addWindowListener(this);
    }

    /**
     * Creates and returns a value edit container for the given parameters.
     * 
     * @param parameters
     *            the list of parameters, the user wants to edit.
     * @param selection
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private JPanel createValueEditContainer(Parameter<?>[] parameters,
            Selection selection) {
        this.paramEditPanel = new ParameterEditPanel(parameters,
                editComponentManager.getEditComponents(), selection);

        return this.paramEditPanel;
    }

    /**
     * This method creates the layout for the configuration of the individual
     * phase-algorithms
     */
    private void defineLayoutAlgorithmConfiguration() {
        // remove everything from the content pane - nothing of this stuff
        // is needed in the second "page" of the panel
        getContentPane().setLayout(new GridBagLayout());
        Component[] comps = getContentPane().getComponents();
        for (int i = 0; i < comps.length; i++) {
            getContentPane().remove(comps[i]);
        }

        // save the y-offset
        int offset_ypos = 0;

        // get the selected algorithms
        SugiyamaAlgorithm[] algos = data.getSelectedAlgorithms();

        // configure the grid
        boolean haveGrid = ((BooleanParameter) data.getAlgorithmParameters()[4])
                .getBoolean();

        // add a text and a new panel for each algorithm
        Parameter<?>[] params;

        int configPanels = 4;
        if (haveGrid) {
            configPanels++;
        }

        BitSet hasParams = new BitSet(configPanels);
        JLabel[] labels = new JLabel[configPanels];
        paramEditPanels = new ParameterEditPanel[configPanels];

        labels[0] = new JLabel("Please configure the algorithm "
                + algos[0].getName());
        params = algos[0].getParameters();
        if (hasParameters(params)) {
            hasParams.set(0, true);
            paramEditPanels[0] = new ParameterEditPanel(algos[0]
                    .getParameters(), editComponentManager.getEditComponents(),
                    sel);
        }

        labels[1] = new JLabel("Please configure the algorithm "
                + algos[1].getName());
        params = algos[1].getParameters();
        if (hasParameters(params)) {
            hasParams.set(1, true);
            paramEditPanels[1] = new ParameterEditPanel(algos[1]
                    .getParameters(), editComponentManager.getEditComponents(),
                    sel);
        }

        labels[2] = new JLabel("Please configure the algorithm "
                + algos[2].getName());
        params = algos[2].getParameters();
        if (hasParameters(params)) {
            hasParams.set(2, true);
            paramEditPanels[2] = new ParameterEditPanel(algos[2]
                    .getParameters(), editComponentManager.getEditComponents(),
                    sel);
        }

        labels[3] = new JLabel("Please configure the algorithm "
                + algos[3].getName());
        params = algos[3].getParameters();
        if (hasParameters(params)) {
            hasParams.set(3, true);
            paramEditPanels[3] = new ParameterEditPanel(algos[3]
                    .getParameters(), editComponentManager.getEditComponents(),
                    sel);
        }
        if (haveGrid) {
            labels[4] = new JLabel("Please configure the grid");
            hasParams.set(4, true);
            paramEditPanels[4] = getGridParameterPanel();
            if (data.getAlgorithmType().equals(
                    SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)) {
                hasParams.set(4, false);
            }
        }

        // if no algorithm has anything to configure, display a message to
        // the user to inform him about that
        if (hasParams.cardinality() == 0) {
            // add a text to notify the user that nothing can be configured
            JLabel nothing = new JLabel("The selected algorithms do not"
                    + " require further configuration.");

            GridBagConstraints lConstraints = new GridBagConstraints();
            lConstraints.gridx = 0;
            lConstraints.gridy = offset_ypos;
            lConstraints.gridwidth = 1;
            lConstraints.gridheight = 1;
            lConstraints.fill = GridBagConstraints.BOTH;
            lConstraints.anchor = GridBagConstraints.WEST;
            lConstraints.weightx = 1.0;
            lConstraints.weighty = 0.0;
            lConstraints.insets = new Insets(8, 8, 0, 8);
            getContentPane().add(nothing, lConstraints);
            offset_ypos++;
        } else {
            // add a label saying "please configure $algorithm" and the
            // actual configuration-panel itself
            for (int i = 0; i < 5; i++) {
                if (hasParams.get(i)) {
                    paramEditPanels[i].setName("phase" + i);
                    // add the label
                    GridBagConstraints lConstraints = new GridBagConstraints();
                    lConstraints.gridx = 0;
                    lConstraints.gridy = offset_ypos;
                    lConstraints.gridwidth = 1;
                    lConstraints.gridheight = 1;
                    lConstraints.fill = GridBagConstraints.BOTH;
                    lConstraints.anchor = GridBagConstraints.WEST;
                    lConstraints.weightx = 1.0;
                    lConstraints.weighty = 0.0;
                    lConstraints.insets = new Insets(8, 8, 0, 8);
                    getContentPane().add(labels[i], lConstraints);

                    offset_ypos++;

                    // add the parameter-panel
                    GridBagConstraints constraints = new GridBagConstraints();
                    constraints.gridx = 0;
                    constraints.gridy = offset_ypos;
                    constraints.gridwidth = 4;
                    constraints.gridheight = 1;
                    constraints.fill = GridBagConstraints.BOTH;
                    constraints.anchor = GridBagConstraints.CENTER;
                    constraints.weightx = 1.0;
                    constraints.weighty = 1.0;
                    constraints.insets = new Insets(8, 8, 8, 8);

                    offset_ypos++;
                    getContentPane().add(paramEditPanels[i], constraints);
                }
            }
        }

        // the default-button
        GridBagConstraints defaultConstraints = new GridBagConstraints();
        defaultConstraints.gridx = 2;
        defaultConstraints.gridy = offset_ypos;
        defaultConstraints.gridwidth = 1;
        defaultConstraints.gridheight = 1;
        defaultConstraints.anchor = GridBagConstraints.WEST;
        defaultConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(defaultButton, defaultConstraints);

        // the run-button
        GridBagConstraints okConstraints = new GridBagConstraints();
        okConstraints.gridx = 3;
        okConstraints.gridy = offset_ypos;
        okConstraints.gridwidth = 1;
        okConstraints.gridheight = 1;
        okConstraints.anchor = GridBagConstraints.EAST;
        okConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(ok, okConstraints);

        // the cancel-button
        GridBagConstraints cancelConstraints = new GridBagConstraints();
        cancelConstraints.gridx = 0;
        cancelConstraints.gridy = offset_ypos;
        cancelConstraints.gridwidth = 1;
        cancelConstraints.gridheight = 1;
        cancelConstraints.anchor = GridBagConstraints.WEST;
        cancelConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(cancel, cancelConstraints);

        // the back-button
        GridBagConstraints backConstraints = new GridBagConstraints();
        backConstraints.gridx = 1;
        backConstraints.gridy = offset_ypos;
        backConstraints.gridwidth = 1;
        backConstraints.gridheight = 1;
        backConstraints.anchor = GridBagConstraints.WEST;
        backConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(back, backConstraints);
    }

    private ParameterEditPanel getGridParameterPanel() {
        boolean wantGrid = ((BooleanParameter) data.getAlgorithmParameters()[4])
                .getBoolean();
        Parameter<?>[] gridParams;

        if (wantGrid
                && data.getAlgorithmType().equals(
                        SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)) {
            DoubleParameter verticalDistance, horizontalDistance;
            verticalDistance = new DoubleParameter(50.0, "Vertical distance",
                    "Vertical grid distance", 10.0, 1000.0);
            horizontalDistance = new DoubleParameter(50.0,
                    "Horizontal distance", "Horizontal grid distance", 10.0,
                    1000.0);
            gridParams = new Parameter<?>[] { horizontalDistance,
                    verticalDistance };

            return new ParameterEditPanel(gridParams, editComponentManager
                    .getEditComponents(), sel);
        } else
            return null;
    }

    /**
     * Defines the layout of this dialog - displays the algorithm-chooser
     */
    private void defineLayoutAlgorithmChooser() {
        getContentPane().setLayout(new GridBagLayout());

        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.gridwidth = 1;
        labelConstraints.gridheight = 1;
        labelConstraints.fill = GridBagConstraints.BOTH;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.weightx = 1.0;
        labelConstraints.weighty = 0.0;
        labelConstraints.insets = new Insets(8, 8, 0, 8);
        getContentPane().add(description, labelConstraints);

        GridBagConstraints listConstraints = new GridBagConstraints();
        listConstraints.gridx = 0;
        listConstraints.gridy = 1;
        listConstraints.gridwidth = 4;
        listConstraints.gridheight = 1;
        listConstraints.fill = GridBagConstraints.BOTH;
        listConstraints.anchor = GridBagConstraints.CENTER;
        listConstraints.weightx = 1.0;
        listConstraints.weighty = 1.0;
        listConstraints.insets = new Insets(8, 8, 8, 8);
        getContentPane().add(paramsPanel, listConstraints);

        if (!error) {
            GridBagConstraints nextConstraints = new GridBagConstraints();
            nextConstraints.gridx = 3;
            nextConstraints.gridy = 2;
            nextConstraints.gridwidth = 1;
            nextConstraints.gridheight = 1;
            nextConstraints.anchor = GridBagConstraints.EAST;
            nextConstraints.insets = new Insets(0, 8, 8, 8);
            getContentPane().add(next, nextConstraints);
        }

        GridBagConstraints cancelConstraints = new GridBagConstraints();
        cancelConstraints.gridx = 0;
        cancelConstraints.gridy = 2;
        cancelConstraints.gridwidth = 1;
        cancelConstraints.gridheight = 1;
        cancelConstraints.anchor = GridBagConstraints.WEST;
        cancelConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(cancel, cancelConstraints);

        GridBagConstraints back2Constraints = new GridBagConstraints();
        back2Constraints.gridx = 1;
        back2Constraints.gridy = 2;
        back2Constraints.gridwidth = 1;
        back2Constraints.gridheight = 1;
        back2Constraints.anchor = GridBagConstraints.EAST;
        back2Constraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(back2, back2Constraints);

        GridBagConstraints searchConstraints = new GridBagConstraints();
        searchConstraints.gridx = 2;
        searchConstraints.gridy = 2;
        searchConstraints.gridwidth = 1;
        searchConstraints.gridheight = 1;
        searchConstraints.anchor = GridBagConstraints.EAST;
        searchConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(search, searchConstraints);
    }

    private void defineLayoutFrameworkConfiguration() {
        getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = 0;
        labelConstraints.gridwidth = 1;
        labelConstraints.gridheight = 1;
        labelConstraints.fill = GridBagConstraints.BOTH;
        labelConstraints.anchor = GridBagConstraints.WEST;
        labelConstraints.weightx = 1.0;
        labelConstraints.weighty = 0.0;
        labelConstraints.insets = new Insets(8, 8, 0, 8);
        getContentPane().add(description, labelConstraints);

        GridBagConstraints listConstraints = new GridBagConstraints();
        listConstraints.gridx = 0;
        listConstraints.gridy = 1;
        listConstraints.gridwidth = 4;
        listConstraints.gridheight = 1;
        listConstraints.fill = GridBagConstraints.BOTH;
        listConstraints.anchor = GridBagConstraints.CENTER;
        listConstraints.weightx = 1.0;
        listConstraints.weighty = 1.0;
        listConstraints.insets = new Insets(8, 8, 8, 8);
        getContentPane().add(paramsPanel, listConstraints);

        GridBagConstraints nextConstraints = new GridBagConstraints();
        nextConstraints.gridx = 3;
        nextConstraints.gridy = 2;
        nextConstraints.gridwidth = 1;
        nextConstraints.gridheight = 1;
        nextConstraints.anchor = GridBagConstraints.EAST;
        nextConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(cont, nextConstraints);

        GridBagConstraints cancelConstraints = new GridBagConstraints();
        cancelConstraints.gridx = 0;
        cancelConstraints.gridy = 2;
        cancelConstraints.gridwidth = 1;
        cancelConstraints.gridheight = 1;
        cancelConstraints.anchor = GridBagConstraints.WEST;
        cancelConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(cancel, cancelConstraints);
    }

    private void continueSelected() {
        // save the framework-parameters
        Parameter<?>[] newParams = ((ParameterEditPanel) paramsPanel)
                .getUpdatedParameters();
        data.setAlgorithmParameters(newParams);

        ArrayList<String[]> phaseAlgos;
        PreferencesUtil.loadPreferences(data);

        phaseAlgos = FindPhaseAlgorithms.getPhaseAlgorithms(data
                .getPhaseAlgorithms(), data.getAlgorithmType());

        error = buildAlgorithmSelection(phaseAlgos);

        // rebuild the dialog
        getContentPane().setLayout(new GridBagLayout());
        Component[] comps = getContentPane().getComponents();
        for (int i = 0; i < comps.length; i++) {
            getContentPane().remove(comps[i]);
        }
        description = new JLabel("Please select an algorithm for each phase.");
        paramsPanel = createValueEditContainer(params, sel);

        if (!error) {
            buttonsPanel.add(next);
        }

        buttonsPanel.add(cancel);
        buttonsPanel.add(search);
        defineLayoutAlgorithmChooser();
        pack();
        setLocationRelativeTo(parentMainFrame);

        if (error) {
            String message;
            message = "You are using " + data.getAlgorithmType()
                    + " drawing.\n"
                    + "At least one phase is missing an algorithm\n"
                    + "that supports radial drawing.\n\n"
                    + "Please reconfigure the framework.";
            parentMainFrame.showMessageDialog(message);
        }
    }

    /**
     * This method is called, if the run-button is selected after configuration
     * of the algorithms. The method disposes the panel and calls setParameters
     * on the algorithms
     */
    private void okSelected() {
        selectedOk = true;
        logger.log(Level.FINE, "ok selected");
        PreferencesUtil.savePreferences(data);
        setParameters();
        dispose();
    }

    private void back2Selected() {
        // rebuild the dialog from scratch
        getContentPane().setLayout(new GridBagLayout());
        Component[] comps = getContentPane().getComponents();
        for (int i = 0; i < comps.length; i++) {
            getContentPane().remove(comps[i]);
        }
        paramsPanel = new ParameterEditPanel(data.getAlgorithmParameters(),
                editComponentManager.getEditComponents(), sel);
        buttonsPanel = new JPanel();
        buttonsPanel.add(cont);
        buttonsPanel.add(cancel);
        defineLayoutFrameworkConfiguration();
        addListeners();

        pack();
        setLocationRelativeTo(parentMainFrame);
        setVisible(true);
    }

    /**
     * This method is called, if the back-button is selected, so the user can
     * re-select the algorithms
     */
    private void backSelected() {

        // rebuild the dialog from scratch
        getContentPane().setLayout(new GridBagLayout());
        Component[] comps = getContentPane().getComponents();
        for (int i = 0; i < comps.length; i++) {
            getContentPane().remove(comps[i]);
        }

        paramsPanel = createValueEditContainer(params, sel);
        buttonsPanel.add(next);
        buttonsPanel.add(cancel);
        buttonsPanel.add(search);
        defineLayoutAlgorithmChooser();
        pack();
        setLocationRelativeTo(parentMainFrame);
    }

    /**
     * This method is called, when the next-button is selected after the
     * algorithms for each phase in the sugiyama-algorithm have been selected.
     * 
     * It saves the selected algorithms in the <code>SugiyamaData</code>-Bean,
     * removes the old parameters-panel and calls
     * defineLayoutAlgorithmConfiguration(). This method creates the layout for
     * the configuration.
     * 
     */
    private void nextSelected() {
        // save the selected algorithms
        saveSelectedAlgorithms();
        // save the algorithms that have been selected
        String[] sel = data.getLastSelectedAlgorithms();
        sel[0] = ((StringSelectionParameter) params[1]).getSelectedValue();
        sel[1] = ((StringSelectionParameter) params[2]).getSelectedValue();
        sel[2] = ((StringSelectionParameter) params[3]).getSelectedValue();
        sel[3] = ((StringSelectionParameter) params[4]).getSelectedValue();

        // remove the old algorithm-chooser-panel
        getContentPane().remove(paramsPanel);

        // create a new buttons-panel with two buttons - cancel and run
        getContentPane().remove(buttonsPanel);
        buttonsPanel = new JPanel();

        // create the ok-button, add this dialog as action-listener and
        // add it to the buttonsPanel
        ok = new JButton(bundle.getString("run.dialog.button.run"));
        ok.addActionListener(this);
        buttonsPanel.add(ok);
        buttonsPanel.add(cancel);

        // create the layout for the algorithm-configuration-part
        defineLayoutAlgorithmConfiguration();
        pack();
        setLocationRelativeTo(parentMainFrame);
        // get the selected algorithms
        SugiyamaAlgorithm[] algos = data.getSelectedAlgorithms();

        String messages = "";
        String subMessage = "";
        boolean warning = false;

        if (data.getBigNodesPolicy() == SugiyamaConstants.BIG_NODES_HANDLE) {
            for (int i = 0; i < algos.length; i++)
                if (!algos[i].supportsBigNodes()) {
                    subMessage += "- " + algos[i].getName() + "\n";
                }
        }

        if (subMessage != "") {
            warning = true;
            messages += "You have selected the following policy for Big Nodes: handle\n";
            messages += "Please note, that the following algorithms don't support this policy:\n\n";
            messages += new String(subMessage) + "\n";

        }
        subMessage = "";

        if (data.getConstraintPolicy() == SugiyamaConstants.CONSTRAINTS_HANDLE) {
            for (int i = 0; i < algos.length; i++)
                if (!algos[i].supportsConstraints()) {
                    subMessage += "- " + algos[i].getName() + "\n";
                }
        }

        if (subMessage != "") {
            warning = true;
            messages += "You have selected the following constraint-policy: handle\n";
            messages += "Please note, that the following algorithms don't support this policy:\n\n";
            messages += new String(subMessage) + "\n";
        }

        if (warning) {
            parentMainFrame.showMessageDialog(messages);
        }

    }

    /**
     * Save the selected algoritms for each phase in the bean
     * 
     */
    private void saveSelectedAlgorithms() {
        String className;
        SugiyamaAlgorithm[] selectedAlgorithms = new SugiyamaAlgorithm[4];

        try {
            className = data.getAlgorithmBinaryNames().get(0)[((StringSelectionParameter) params[1])
                    .getSelectedIndex()];
            if (data.getAlgorithmMap().containsKey(className)) {
                selectedAlgorithms[0] = data.getAlgorithmMap().get(className);
            } else {
                selectedAlgorithms[0] = (SugiyamaAlgorithm) Class.forName(
                        className).newInstance();
                data.getAlgorithmMap().put(className, selectedAlgorithms[0]);
                logger.log(Level.FINE, "Put an algorithm into cache");
            }
            logger
                    .log(Level.FINE, "Selected Decycling-Algorithm: "
                            + className);

            className = data.getAlgorithmBinaryNames().get(1)[((StringSelectionParameter) params[2])
                    .getSelectedIndex()];
            if (data.getAlgorithmMap().containsKey(className)) {
                selectedAlgorithms[1] = data.getAlgorithmMap().get(className);
            } else {
                selectedAlgorithms[1] = (LevellingAlgorithm) Class.forName(
                        className).newInstance();
                data.getAlgorithmMap().put(className, selectedAlgorithms[1]);
                logger.log(Level.FINE, "Put an algorithm into cache");
            }
            logger
                    .log(Level.FINE, "Selected Decycling-Algorithm: "
                            + className);

            className = data.getAlgorithmBinaryNames().get(2)[((StringSelectionParameter) params[3])
                    .getSelectedIndex()];
            if (data.getAlgorithmMap().containsKey(className)) {
                selectedAlgorithms[2] = data.getAlgorithmMap().get(className);
            } else {
                selectedAlgorithms[2] = (CrossMinAlgorithm) Class.forName(
                        className).newInstance();
                data.getAlgorithmMap().put(className, selectedAlgorithms[2]);
                logger.log(Level.FINE, "Put an algorithm into cache");
            }
            logger.log(Level.FINE, "Selected CrossMin-Algorithm: " + className);

            className = data.getAlgorithmBinaryNames().get(3)[((StringSelectionParameter) params[4])
                    .getSelectedIndex()];
            if (data.getAlgorithmMap().containsKey(className)) {
                selectedAlgorithms[3] = data.getAlgorithmMap().get(className);
            } else {
                selectedAlgorithms[3] = (LayoutAlgorithm) Class.forName(
                        className).newInstance();
                data.getAlgorithmMap().put(className, selectedAlgorithms[3]);
                logger.log(Level.FINE, "Put an algorithm into cache");
            }
            logger.log(Level.FINE, "Selected Layout-Algorithm: " + className);

            for (int i = 0; i < 4; i++) {
                selectedAlgorithms[i].setData(data);
            }
            data.setSelectedAlgorithms(selectedAlgorithms);
        } catch (IllegalAccessException iae) {

        } catch (InstantiationException ie) {

        } catch (ClassNotFoundException cnfe) {

        }
    }

    /**
     * Checks if a <code>Parameter[]</code> contains configurable parameters
     * 
     * @param param
     *            The <code>Parameter[]</code> to check
     * @return Returns <code>true</code> if the <code>Parameter[]</code> is not
     *         <code>null</code> or does contain parameters other than a
     *         <code>SelectionParameter</code>.
     */
    private boolean hasParameters(Parameter<?>[] param) {
        int parameters = 0;

        if (param == null)
            return false;
        else {
            for (int i = 0; i < param.length; i++) {
                if (!SelectionParameter.class.isAssignableFrom(param[i]
                        .getClass())) {
                    parameters++;
                    return true;
                }
            }
        }
        return parameters != 0;
    }

    /**
     * Set the updated parameters
     */
    private void setParameters() {
        SugiyamaAlgorithm[] algos = data.getSelectedAlgorithms();

        // get all components in the panel...
        Component[] comps = getContentPane().getComponents();
        for (int i = 0; i < comps.length; i++) {
            // test for a parametereditpanel
            if (ParameterEditPanel.class.isAssignableFrom(comps[i].getClass())) {
                // get the panel's parameters
                Parameter<?>[] updatedParams = ((ParameterEditPanel) comps[i])
                        .getUpdatedParameters();
                // set the parameters to the algorithm they belong to
                if (comps[i].getName().equals("phase0")) {
                    algos[0].setParameters(updatedParams);
                }
                if (comps[i].getName().equals("phase1")) {
                    algos[1].setParameters(updatedParams);
                }
                if (comps[i].getName().equals("phase2")) {
                    algos[2].setParameters(updatedParams);
                }
                if (comps[i].getName().equals("phase3")) {
                    algos[3].setParameters(updatedParams);
                }
                // grid
                if (comps[i].getName().equals("phase4")) {
                    data.setGridParameters(updatedParams);
                }
                if (comps[i].getName().equals("framework")) {
                    data.setAlgorithmParameters(updatedParams);
                }
            }
        }
    }

    private void defaultSelected() {
        SugiyamaAlgorithm[] algos = data.getSelectedAlgorithms();
        for (SugiyamaAlgorithm algo : algos) {
            algo.setParameters(algo.getDefaultParameters());
        }

        // remove the old algorithm-chooser-panel
        getContentPane().remove(paramsPanel);

        // create a new buttons-panel with two buttons - cancel and run
        getContentPane().remove(buttonsPanel);
        buttonsPanel = new JPanel();

        // create the ok-button, add this dialog as action-listener and
        // add it to the buttonsPanel
        ok = new JButton(bundle.getString("run.dialog.button.run"));
        ok.addActionListener(this);
        buttonsPanel.add(ok);
        buttonsPanel.add(cancel);

        // create the layout for the algorithm-configuration-part
        defineLayoutAlgorithmConfiguration();
        pack();
        setLocationRelativeTo(parentMainFrame);

    }

    private boolean buildAlgorithmSelection(ArrayList<String[]> phaseAlgorithms) {
        boolean ret = false;
        // some general initialization
        String algoName;
        ArrayList<String[]> algBinaryNames = new ArrayList<String[]>();
        ComparableClassParameter[] sortMe;
        String[] phaseNames = new String[] { "Decycling-algorithm",
                "Levelling-algorithm", "CrossMin-algorithm", "Layout-algorithm" };
        String[] phaseDescr = new String[] { "Select the decycling-algorithm",
                "Select the levelling-algorithm",
                "Select the crossmin-algorithm", "Select the layout-algorithm" };
        String[] selection;

        // The StringSelections that will be returned in the Parameter[]
        StringSelectionParameter[] selectableAlgorithms;
        selectableAlgorithms = new StringSelectionParameter[4];

        // Build the StringSelectionParameter for each phase
        for (int i = 0; i < phaseAlgorithms.size(); i++) {
            if (phaseAlgorithms.get(i) != null) {
                // Create an array of ComparableClassParameters to build the
                // StringSelectionParameter for each phase in alphabetical order
                sortMe = new ComparableClassParameter[phaseAlgorithms.get(i).length];
                // Iterate through the available algorithms in phase i
                for (int j = 0; j < phaseAlgorithms.get(i).length; j++) {
                    algoName = phaseAlgorithms.get(i)[j];
                    try {
                        // Access the algorithm's getName()-Method (used for
                        // description of the algorithm
                        algoName = ((SugiyamaAlgorithm) Class.forName(algoName)
                                .newInstance()).getName();
                        sortMe[j] = new ComparableClassParameter(
                                phaseAlgorithms.get(i)[j], algoName);
                    } catch (Exception ex) {
                        // This should not happen
                    }
                }
                // check if an entry of the array is null (could happen, if an
                // algorithm has been removed but was still loaded from
                // preferences
                int nullalgos = 0;
                ComparableClassParameter[] sortMe2;
                for (int j = 0; j < sortMe.length; j++) {
                    if (sortMe[j] == null) {
                        nullalgos++;
                    }
                }
                // rebuild array if there's a null-value in it
                if (nullalgos != 0) {
                    sortMe2 = new ComparableClassParameter[sortMe.length
                            - nullalgos];
                    int counter = 0;
                    for (int j = 0; j < sortMe.length; j++) {
                        if (sortMe[j] != null) {
                            sortMe2[counter] = sortMe[j];
                            counter++;
                        }
                    }
                    sortMe = sortMe2;
                }
                // An array of ComparableClassParameters has been build now,
                // sort
                // it and build a StringSelectionParameter
                Arrays.sort(sortMe);
                algBinaryNames.add(i, new String[sortMe.length]);
                selection = new String[sortMe.length];
                for (int j = 0; j < sortMe.length; j++) {
                    selection[j] = sortMe[j].description;
                    algBinaryNames.get(i)[j] = sortMe[j].binaryName;
                }
                selectableAlgorithms[i] = new StringSelectionParameter(
                        selection, phaseNames[i], phaseDescr[i]);
            } else {
                ret = true;
                algBinaryNames.add(i, new String[] { "" });
                selectableAlgorithms[i] = new StringSelectionParameter(
                        new String[] { "No algorithms available" },
                        phaseNames[i], phaseDescr[i]);
            }
        }

        // save the binary-names of the algorithms in the bean
        data.setAlgorithmBinaryNames(algBinaryNames);

        // check if there had been any algorithms pre-selected by the user
        // and set this to the selected value
        String[] para;
        for (int i = 0; i < 4; i++) {
            if (data.getLastSelectedAlgorithms()[i] != null) {
                para = selectableAlgorithms[i].getParams();
                for (int j = 0; j < para.length; j++) {
                    if (data.getLastSelectedAlgorithms()[i].equals(para[j])) {
                        selectableAlgorithms[i].setSelectedValue(j);
                    }
                }
            }
        }
        this.params[1] = selectableAlgorithms[0];
        this.params[2] = selectableAlgorithms[1];
        this.params[3] = selectableAlgorithms[2];
        this.params[4] = selectableAlgorithms[3];
        return ret;
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
