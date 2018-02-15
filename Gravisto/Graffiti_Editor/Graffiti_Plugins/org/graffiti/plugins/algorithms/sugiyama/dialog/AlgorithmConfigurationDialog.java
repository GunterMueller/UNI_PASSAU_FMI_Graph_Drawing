// =============================================================================
//
//   DefaultParameterDialog.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AlgorithmConfigurationDialog.java 5777 2010-05-10 14:13:53Z gleissner $

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
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
 * The default implementation of a parameter dialog.
 * 
 * @version $Revision: 2854 $
 */
public class AlgorithmConfigurationDialog extends AbstractParameterDialog
        implements ActionListener, WindowListener, PropertyChangeListener {
    /**
     * 
     */
    private static final long serialVersionUID = -2345869262484594663L;

    /** The <code>Bundle</code> of the view type chooser. */
    protected static final Bundle coreBundle = Bundle.getCoreBundle();

    /** The panel used to display and change parameter values. */
    protected ParameterEditPanel paramEditPanel;

    /** The algorithm to display settings for. */
    protected Algorithm algorithm;

    /** The list of parameters, the user is editing. */
    protected Parameter<?>[] params;

    /** The list of default parameters. */
    protected Parameter<?>[] defaultParams;

    /** True, if the algorithm supports user prefs. */
    protected boolean showUserPrefs = false;

    /** The value edit component manager, the edit panel needs. */
    private EditComponentManager editComponentManager;

    /** The panel used to display and change parameters for each phase. */
    protected ParameterEditPanel[] paramEditPanels;
    protected GridBagConstraints[] paramEditPanelsConstraints;

    // PAGE ONE
    /** The dialog's buttons. */
    private JButton pageOneCancel;

    /** The dialog's buttons. */
    private JButton pageOneNext;

    /** The dialog's buttons. */
    private JButton pageOneReset;

    /** The dialog's buttons. */
    private JButton pageOneLoadUserPrefs;

    /** Whether to save the settings as user preferences. */
    private JButton pageOneSaveUserPrefs;

    /** The description of this dialog. */
    private JLabel pageOneDescription;

    // PAGE TWO
    private JButton pageTwoCancel;
    private JButton pageTwoBack;
    private JButton pageTwoSearch;
    private JButton pageTwoNext;
    private JButton pageTwoReset;
    private JButton pageTwoLoadUserPrefs;
    private JButton pageTwoSaveUserPrefs;
    private JLabel pageTwoDescription;

    // PAGE THREE
    private JButton pageThreeCancel;
    private JButton pageThreeBack;
    private JButton pageThreeRun;
    private JButton pageThreeReset;
    private JButton pageThreeLoadUserPrefs;
    private JButton pageThreeSaveUserPrefs;

    /** The panel, which contains the parameters. */
    private JPanel paramsPanel;

    /** The current selection. */
    private Selection selection;

    /** The GridBagConstraints for the param panel. */
    private GridBagConstraints listConstraints;

    /** <code>true</code>, if the user selected the ok button in this dialog. */
    private boolean selectedOk = false;

    /** The panel that contains the buttons */
    private JPanel buttonsPanel;

    private JPanel buttonsPanelPrefs;

    /** Selection-parameter */
    private SelectionParameter sel;

    /** The logger */
    private static final Logger logger = Logger
            .getLogger(AlgorithmConfigurationDialog.class.getName());

    private SugiyamaData data;
    private MainFrame parentMainFrame;
    private boolean error;

    /**
     * Constructor for DefaultParameterDialog.
     * 
     * @param editComponentManager
     *            DOCUMENT ME!
     * @param parent
     *            the parent of this dialog.
     * @param algorithm
     *            the algorithm to display parameter settings for
     * @param selection
     *            DOCUMENT ME!
     */
    @SuppressWarnings("cast")
    public AlgorithmConfigurationDialog(
            EditComponentManager editComponentManager, MainFrame parent,
            Algorithm algorithm, Selection selection, SugiyamaData data) {
        super(parent, true);

        this.data = data;
        this.parentMainFrame = parent;
        this.editComponentManager = editComponentManager;
        this.algorithm = algorithm;
        this.params = data.getParameters();

        sel = new SelectionParameter(selection.getName(), "Starting node",
                selection);

        this.defaultParams = data.getDefaultParameters();

        showUserPrefs = data instanceof AbstractAlgorithm
                && ((AbstractAlgorithm) data).getUserParameters() != null;

        this.selection = selection;

        getContentPane().setLayout(new BorderLayout());

        setTitle(algorithm.getName());
        setSize(420, 320);
        setResizable(false);

        pageOneNext = new JButton(coreBundle
                .getString("run.dialog.button.next"));
        pageOneCancel = new JButton(coreBundle
                .getString("run.dialog.button.cancel"));
        pageOneDescription = new JLabel(coreBundle
                .getString("run.dialog.desc.sugiyama.framework"));

        if (defaultParams != null) {
            pageOneReset = new JButton(coreBundle
                    .getIcon("run.dialog.button.reset.icon"));
            pageOneReset.setToolTipText(coreBundle
                    .getString("run.dialog.button.reset"));
        }
        if (showUserPrefs) {
            pageOneLoadUserPrefs = new JButton(coreBundle
                    .getIcon("run.dialog.button.loaduserprefs.icon"));
            pageOneLoadUserPrefs.setToolTipText(coreBundle
                    .getString("run.dialog.button.loaduserprefs"));
            pageOneSaveUserPrefs = new JButton(coreBundle
                    .getIcon("run.dialog.button.saveuserprefs.icon"));
            pageOneSaveUserPrefs.setToolTipText(coreBundle
                    .getString("run.dialog.button.saveuserprefs"));
        }
        buttonsPanel = new JPanel();
        buttonsPanelPrefs = new JPanel();

        if (defaultParams != null) {
            buttonsPanel.add(pageOneReset);
        }
        if (showUserPrefs) {
            buttonsPanel.add(pageOneLoadUserPrefs);
            buttonsPanel.add(pageOneSaveUserPrefs);
        }
        buttonsPanel.add(pageOneNext);
        buttonsPanel.add(pageOneCancel);

        paramsPanel = createValueEditContainer(params, selection);

        pageOneNext.setEnabled(true);

        getRootPane().setDefaultButton(pageOneNext);

        defineLayout();
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
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == pageOneCancel) {
            dispose();
        } else if (src == pageOneReset) {
            this.params = AbstractAlgorithm.copyParameters(data
                    .getDefaultParameters());
            getContentPane().remove(paramsPanel);
            paramsPanel = createValueEditContainer(params, selection);
            getContentPane().add(paramsPanel, listConstraints);
            pageOneSaveUserPrefs.setEnabled(true);
            paramsPanel.validate();
            validate();
            pack();
        } else if (src == pageOneLoadUserPrefs) {
            this.params = ((AbstractAlgorithm) data).getUserParameters();
            getContentPane().remove(paramsPanel);
            paramsPanel = createValueEditContainer(params, selection);
            getContentPane().add(paramsPanel, listConstraints);
            pageOneSaveUserPrefs.setEnabled(false);
            paramsPanel.validate();
            validate();
            pack();
        } else if (src == pageOneSaveUserPrefs) {
            ((AbstractAlgorithm) data).saveUserParameters(paramEditPanel
                    .getUpdatedParameters());
            pageOneSaveUserPrefs.setEnabled(false);
        }
        // proceed to page two
        else if (src == pageOneNext) {
            pageOneNextSelected();
        } else if (src == pageTwoCancel) {
            dispose();
        } else if (src == pageTwoBack) {
            pageTwoBackSelected();
        } else if (src == pageTwoSearch) {
            pageTwoSearchSelected();
        } else if (src == pageTwoNext) {
            pageTwoNextSelected();
        } else if (src == pageTwoReset) {
            this.params = AbstractAlgorithm.copyParameters(defaultParams);
            getContentPane().remove(paramsPanel);
            paramsPanel = createValueEditContainer(params, selection);
            getContentPane().add(paramsPanel, listConstraints);
            pageTwoSaveUserPrefs.setEnabled(true);
            paramsPanel.validate();
            validate();
            pack();
        } else if (src == pageTwoLoadUserPrefs) {
            this.params = ((AbstractAlgorithm) algorithm).getUserParameters();
            getContentPane().remove(paramsPanel);
            paramsPanel = createValueEditContainer(params, selection);
            getContentPane().add(paramsPanel, listConstraints);
            pageOneSaveUserPrefs.setEnabled(false);
            paramsPanel.validate();
            validate();
            pack();
        } else if (src == pageTwoSaveUserPrefs) {
            ((AbstractAlgorithm) algorithm).saveUserParameters(paramEditPanel
                    .getUpdatedParameters());
            pageTwoSaveUserPrefs.setEnabled(false);
        } else if (src == pageThreeCancel) {
            dispose();
        } else if (src == pageThreeBack) {
            pageThreeBackSelected();
        } else if (src == pageThreeRun) {
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
        } else if (src == pageThreeReset) {
            SugiyamaAlgorithm[] algos = data.getSelectedAlgorithms();

            for (SugiyamaAlgorithm algo : algos) {
                algo.setParameters(algo.getDefaultParameters());
            }

            // remove the old algorithm-chooser-panel
            getContentPane().remove(paramsPanel);

            // create the layout for the algorithm-configuration-part
            defineLayoutPageThree();
            setLocationRelativeTo(parentMainFrame);
            paramsPanel.validate();
            validate();
            pack();
        } else if (src == pageThreeLoadUserPrefs) {
            SugiyamaAlgorithm[] algos = data.getSelectedAlgorithms();
            Parameter<?>[] paramTemp;

            for (int i = 0; i < algos.length; i++) {
                paramTemp = algos[i].getParameters();
                if (hasParameters(paramTemp)) {
                    getContentPane().remove(paramEditPanels[i]);
                    paramEditPanels[i] = new ParameterEditPanel(
                            ((AbstractAlgorithm) algos[i]).getUserParameters(),
                            editComponentManager.getEditComponents(), sel
                                    .getSelection());

                    getContentPane().add(paramEditPanels[i],
                            paramEditPanelsConstraints[i]);
                    getContentPane().validate();
                }
            }
            pageThreeSaveUserPrefs.setEnabled(false);
            paramsPanel.validate();
            validate();
            pack();
        } else if (src == pageThreeSaveUserPrefs) {
            SugiyamaAlgorithm[] algos = data.getSelectedAlgorithms();
            Parameter<?>[] paramTemp;

            for (int i = 0; i < algos.length; i++) {
                paramTemp = algos[i].getParameters();
                if (hasParameters(paramTemp)) {
                    ((AbstractAlgorithm) algos[i])
                            .saveUserParameters(paramEditPanels[i]
                                    .getUpdatedParameters());
                }
            }
            pageThreeSaveUserPrefs.setEnabled(false);
        }
    }

    /*
     * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
     * PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == paramEditPanel
                && evt.getPropertyName().compareTo("PARAMS_CHANGED") == 0) {
            pageOneSaveUserPrefs.setEnabled(true);
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
     * Adds the listeners to the dialog.
     */
    private void addListeners() {
        if (defaultParams != null) {
            pageOneReset.addActionListener(this);
        }
        if (showUserPrefs) {
            pageOneLoadUserPrefs.addActionListener(this);
            pageOneSaveUserPrefs.addActionListener(this);
        }

        pageOneCancel.addActionListener(this);
        pageOneNext.addActionListener(this);
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
        if (paramEditPanel != null) {
            paramEditPanel.removePropertyChangeListener(this);
        }
        this.paramEditPanel = new ParameterEditPanel(parameters,
                editComponentManager.getEditComponents(), selection);
        paramEditPanel.addPropertyChangeListener(this);

        return this.paramEditPanel;
    }

    /**
     * Defines the layout of this dialog.
     */
    private void defineLayout() {
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
        getContentPane().add(pageOneDescription, labelConstraints);

        listConstraints = new GridBagConstraints();
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

        GridBagConstraints okConstraints = new GridBagConstraints();
        okConstraints.gridx = 3;
        okConstraints.gridy = 2;
        okConstraints.gridwidth = 1;
        okConstraints.gridheight = 1;
        okConstraints.anchor = GridBagConstraints.EAST;
        okConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(pageOneNext, okConstraints);

        GridBagConstraints cancelConstraints = new GridBagConstraints();
        cancelConstraints.gridx = 2;
        cancelConstraints.gridy = 2;
        cancelConstraints.gridwidth = 1;
        cancelConstraints.gridheight = 1;
        cancelConstraints.anchor = GridBagConstraints.EAST;
        cancelConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(pageOneCancel, cancelConstraints);

        if (defaultParams != null || showUserPrefs) {
            JPanel buttonPanel = new JPanel();

            if (defaultParams != null) {
                buttonPanel.add(pageOneReset);
            }

            if (showUserPrefs) {
                buttonPanel.add(pageOneLoadUserPrefs);
                buttonPanel.add(pageOneSaveUserPrefs);
            }
            add(buttonPanel);

            GridBagConstraints panelConstraints = new GridBagConstraints();
            panelConstraints.gridx = 0;
            panelConstraints.gridy = 2;
            panelConstraints.gridwidth = 1;
            panelConstraints.gridheight = 1;
            panelConstraints.anchor = GridBagConstraints.WEST;
            panelConstraints.insets = new Insets(0, 8, 8, 8);
            getContentPane().add(buttonPanel, panelConstraints);
        }
    }

    /**
     * DOCUMENT ME!
     */
    private void okSelected() {
        selectedOk = true;
        PreferencesUtil.savePreferences(data);
        setParameters();
        dispose();
    }

    private void pageOneNextSelected() {
        // save the framework-parameters
        Parameter<?>[] newParams = ((ParameterEditPanel) paramsPanel)
                .getUpdatedParameters();
        data.setAlgorithmParameters(newParams);

        ArrayList<String[]> phaseAlgos;
        PreferencesUtil.loadPreferences(data);

        phaseAlgos = FindPhaseAlgorithms.getPhaseAlgorithms(data
                .getPhaseAlgorithms(), data.getAlgorithmType());

        error = buildAlgorithmSelection(phaseAlgos);

        this.defaultParams = algorithm.getDefaultParameters();

        // rebuild the dialog
        getContentPane().setLayout(new GridBagLayout());
        Component[] comps = getContentPane().getComponents();
        for (int i = 0; i < comps.length; i++) {
            getContentPane().remove(comps[i]);
        }
        paramsPanel = createValueEditContainer(params, sel.getSelection());

        buttonsPanel.removeAll();
        buttonsPanelPrefs.removeAll();

        pageTwoNext = new JButton(coreBundle
                .getString("run.dialog.button.next"));
        pageTwoCancel = new JButton(coreBundle
                .getString("run.dialog.button.cancel"));
        pageTwoDescription = new JLabel(coreBundle.getString("run.dialog.desc"));
        pageTwoSearch = new JButton(coreBundle
                .getString("run.dialog.button.algorithmsearch"));
        pageTwoBack = new JButton(coreBundle
                .getString("run.dialog.button.back"));

        pageTwoReset = new JButton(coreBundle
                .getIcon("run.dialog.button.reset.icon"));
        pageTwoReset.setToolTipText(coreBundle
                .getString("run.dialog.button.reset"));
        pageTwoLoadUserPrefs = new JButton(coreBundle
                .getIcon("run.dialog.button.loaduserprefs.icon"));
        pageTwoLoadUserPrefs.setToolTipText(coreBundle
                .getString("run.dialog.button.loaduserprefs"));
        pageTwoSaveUserPrefs = new JButton(coreBundle
                .getIcon("run.dialog.button.saveuserprefs.icon"));
        pageTwoSaveUserPrefs.setToolTipText(coreBundle
                .getString("run.dialog.button.saveuserprefs"));

        buttonsPanelPrefs.add(pageTwoReset);
        buttonsPanelPrefs.add(pageTwoLoadUserPrefs);
        buttonsPanelPrefs.add(pageTwoSaveUserPrefs);
        buttonsPanel.add(pageTwoCancel);
        buttonsPanel.add(pageTwoBack);
        buttonsPanel.add(pageTwoSearch);
        buttonsPanel.add(pageTwoNext);

        pageTwoNext.addActionListener(this);
        pageTwoCancel.addActionListener(this);
        pageTwoSearch.addActionListener(this);
        pageTwoBack.addActionListener(this);
        pageTwoReset.addActionListener(this);
        pageTwoLoadUserPrefs.addActionListener(this);
        pageTwoSaveUserPrefs.addActionListener(this);

        if (error) {
            pageTwoNext.setEnabled(false);
        }

        defineLayoutPageTwo();

        pack();
        setLocationRelativeTo(parentMainFrame);

        if (error) {
            String message;
            message = "You are using " + data.getAlgorithmType()
                    + " drawing.\n"
                    + "At least one phase is missing an algorithm\n"
                    + "that supports " + data.getAlgorithmType()
                    + "drawing.\n\n" + "Please reconfigure the framework.";
            parentMainFrame.showMessageDialog(message);
        }
    }

    private void defineLayoutPageTwo() {
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
        getContentPane().add(pageTwoDescription, labelConstraints);

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

        GridBagConstraints panelPrefsConstraints = new GridBagConstraints();
        panelPrefsConstraints.gridx = 0;
        panelPrefsConstraints.gridy = 2;
        panelPrefsConstraints.gridwidth = 1;
        panelPrefsConstraints.gridheight = 1;
        panelPrefsConstraints.anchor = GridBagConstraints.WEST;
        panelPrefsConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(buttonsPanelPrefs, panelPrefsConstraints);

        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.gridx = 3;
        panelConstraints.gridy = 2;
        panelConstraints.gridwidth = 1;
        panelConstraints.gridheight = 1;
        panelConstraints.anchor = GridBagConstraints.EAST;
        panelConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(buttonsPanel, panelConstraints);

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
        this.params = new Parameter[5];

        this.params[0] = sel;
        this.params[1] = selectableAlgorithms[0];
        this.params[2] = selectableAlgorithms[1];
        this.params[3] = selectableAlgorithms[2];
        this.params[4] = selectableAlgorithms[3];
        return ret;
    }

    private void pageTwoBackSelected() {
        this.defaultParams = data.getDefaultParameters();

        // rebuild the dialog from scratch
        getContentPane().setLayout(new GridBagLayout());

        Component[] comps = getContentPane().getComponents();
        for (int i = 0; i < comps.length; i++) {
            getContentPane().remove(comps[i]);
        }

        paramsPanel = createValueEditContainer(data.getAlgorithmParameters(),
                selection);
        buttonsPanel.removeAll();
        buttonsPanelPrefs.removeAll();

        buttonsPanel.add(pageOneReset);
        buttonsPanel.add(pageOneLoadUserPrefs);
        buttonsPanel.add(pageOneSaveUserPrefs);
        buttonsPanel.add(pageOneNext);
        buttonsPanel.add(pageOneCancel);

        pageOneNext.setEnabled(true);
        getRootPane().setDefaultButton(pageOneNext);

        defineLayout();

        pack();
        setLocationRelativeTo(parentMainFrame);
        setVisible(true);
    }

    private void pageTwoSearchSelected() {
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
        if (!error) {
            pageTwoNext.setEnabled(true);
        }

        // remove the old panel with the algorithm, add a new one and
        // re-create the layout of the dialog
        getContentPane().remove(paramsPanel);
        paramsPanel = createValueEditContainer(params, sel.getSelection());
        defineLayoutPageTwo();
        pack();
    }

    private void pageTwoNextSelected() {
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

        buttonsPanel.removeAll();
        buttonsPanelPrefs.removeAll();

        buttonsPanel = new JPanel();
        pageThreeRun = new JButton(coreBundle
                .getString("run.dialog.button.run"));
        pageThreeCancel = new JButton(coreBundle
                .getString("run.dialog.button.cancel"));
        pageThreeBack = new JButton(coreBundle
                .getString("run.dialog.button.back"));

        pageThreeReset = new JButton(coreBundle
                .getIcon("run.dialog.button.reset.icon"));
        pageThreeReset.setToolTipText(coreBundle
                .getString("run.dialog.button.reset"));
        pageThreeLoadUserPrefs = new JButton(coreBundle
                .getIcon("run.dialog.button.loaduserprefs.icon"));
        pageThreeLoadUserPrefs.setToolTipText(coreBundle
                .getString("run.dialog.button.loaduserprefs"));
        pageThreeSaveUserPrefs = new JButton(coreBundle
                .getIcon("run.dialog.button.saveuserprefs.icon"));
        pageThreeSaveUserPrefs.setToolTipText(coreBundle
                .getString("run.dialog.button.saveuserprefs"));

        buttonsPanelPrefs.add(pageThreeReset);
        buttonsPanelPrefs.add(pageThreeLoadUserPrefs);
        buttonsPanelPrefs.add(pageThreeSaveUserPrefs);
        buttonsPanel.add(pageThreeCancel);
        buttonsPanel.add(pageThreeBack);
        buttonsPanel.add(pageThreeRun);

        pageThreeRun.addActionListener(this);
        pageThreeCancel.addActionListener(this);
        pageThreeBack.addActionListener(this);
        pageThreeReset.addActionListener(this);
        pageThreeLoadUserPrefs.addActionListener(this);
        pageThreeSaveUserPrefs.addActionListener(this);

        // create the layout for the algorithm-configuration-part
        defineLayoutPageThree();

        paramEditPanel.validate();
        buttonsPanel.validate();
        buttonsPanelPrefs.validate();
        invalidate();
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

    private void defineLayoutPageThree() {
        // remove everything from the content pane - nothing of this stuff
        // is needed in the third page of the panel
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
                    sel.getSelection());
            paramEditPanels[0].validate();
        }

        labels[1] = new JLabel("Please configure the algorithm "
                + algos[1].getName());
        params = algos[1].getParameters();
        if (hasParameters(params)) {
            hasParams.set(1, true);
            paramEditPanels[1] = new ParameterEditPanel(algos[1]
                    .getParameters(), editComponentManager.getEditComponents(),
                    sel.getSelection());
            paramEditPanels[1].validate();
        }

        labels[2] = new JLabel("Please configure the algorithm "
                + algos[2].getName());
        params = algos[2].getParameters();
        if (hasParameters(params)) {
            hasParams.set(2, true);
            paramEditPanels[2] = new ParameterEditPanel(algos[2]
                    .getParameters(), editComponentManager.getEditComponents(),
                    sel.getSelection());
            paramEditPanels[2].validate();
        }

        labels[3] = new JLabel("Please configure the algorithm "
                + algos[3].getName());
        params = algos[3].getParameters();
        if (hasParameters(params)) {
            hasParams.set(3, true);
            paramEditPanels[3] = new ParameterEditPanel(algos[3]
                    .getParameters(), editComponentManager.getEditComponents(),
                    sel.getSelection());
            paramEditPanels[3].validate();
        }
        if (haveGrid) {
            labels[4] = new JLabel("Please configure the grid");
            hasParams.set(4, true);
            paramEditPanels[4] = getGridParameterPanel();
            if (data.getAlgorithmType().equals(
                    SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)) {
                hasParams.set(4, false);
            } else {
                paramEditPanels[4].validate();
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
            paramEditPanelsConstraints = new GridBagConstraints[5];
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
                    paramEditPanelsConstraints[i] = constraints;
                }
            }
        }
        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.gridx = 0;
        panelConstraints.gridy = offset_ypos;
        panelConstraints.gridwidth = 1;
        panelConstraints.gridheight = 1;
        panelConstraints.anchor = GridBagConstraints.WEST;
        panelConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(buttonsPanelPrefs, panelConstraints);

        GridBagConstraints panelConstraints2 = new GridBagConstraints();
        panelConstraints2.gridx = 3;
        panelConstraints2.gridy = offset_ypos;
        panelConstraints2.gridwidth = 1;
        panelConstraints2.gridheight = 1;
        panelConstraints2.anchor = GridBagConstraints.EAST;
        panelConstraints2.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(buttonsPanel, panelConstraints2);
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

    private ParameterEditPanel getGridParameterPanel() {
        boolean wantGrid = ((BooleanParameter) data.getAlgorithmParameters()[4])
                .getBoolean();
        Parameter<?>[] gridParams;

        if (wantGrid
                && data.getAlgorithmType().equals(
                        SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)) {
            IntegerParameter verticalDistance, horizontalDistance;
            verticalDistance = new IntegerParameter(50, "Cell width",
                    "Width of each grid cell", 2, 100, 2, Integer.MAX_VALUE);
            horizontalDistance = new IntegerParameter(50, "Cell height",
                    "Height of each grid cell", 2, 100, 2, Integer.MAX_VALUE);
            gridParams = new Parameter<?>[] { horizontalDistance,
                    verticalDistance };

            return new ParameterEditPanel(gridParams, editComponentManager
                    .getEditComponents(), sel.getSelection());
        } else
            return null;
    }

    private void pageThreeBackSelected() {
        // rebuild the dialog from scratch
        getContentPane().setLayout(new GridBagLayout());
        Component[] comps = getContentPane().getComponents();
        for (int i = 0; i < comps.length; i++) {
            getContentPane().remove(comps[i]);
        }

        paramsPanel = createValueEditContainer(params, sel.getSelection());

        buttonsPanel.removeAll();
        buttonsPanelPrefs.removeAll();
        buttonsPanelPrefs.add(pageTwoReset);
        buttonsPanelPrefs.add(pageTwoLoadUserPrefs);
        buttonsPanelPrefs.add(pageTwoSaveUserPrefs);
        buttonsPanel.add(pageTwoCancel);
        buttonsPanel.add(pageTwoBack);
        buttonsPanel.add(pageTwoSearch);
        buttonsPanel.add(pageTwoNext);

        defineLayoutPageTwo();

        pack();
        setLocationRelativeTo(parentMainFrame);
        setVisible(true);
    }

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
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
