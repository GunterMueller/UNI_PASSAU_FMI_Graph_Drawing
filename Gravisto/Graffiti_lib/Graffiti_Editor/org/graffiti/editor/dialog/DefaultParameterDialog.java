// =============================================================================
//
//   DefaultParameterDialog.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DefaultParameterDialog.java 5792 2010-07-01 14:41:14Z hanauer $

package org.graffiti.editor.dialog;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.graffiti.core.Bundle;
import org.graffiti.editor.MainFrame;
import org.graffiti.managers.EditComponentManager;
import org.graffiti.plugin.AbstractParametrizable;
import org.graffiti.plugin.Parametrizable;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;

/**
 * The default implementation of a parameter dialog.
 * 
 * @version $Revision: 5792 $
 */
public class DefaultParameterDialog extends AbstractParameterDialog implements
        ActionListener, WindowListener, PropertyChangeListener {
    /**
     * 
     */
    private static final long serialVersionUID = -5106442539800167316L;

    /** The <code>Bundle</code> of the view type chooser. */
    protected static final Bundle coreBundle = Bundle.getCoreBundle();

    /** The panel used to display and change parameter values. */
    protected ParameterEditPanel paramEditPanel;

    /** The <code>Parametrizable</code> to display settings for. */
    protected Parametrizable parametrizable;

    /** The list of parameters, the user is editing. */
    protected Parameter<?>[] params;

    /** The list of default parameters. */
    protected Parameter<?>[] defaultParams;

    /** True, if the <code>Parametrizable</code> supports user prefs. */
    protected boolean showUserPrefs = false;

    /** The value edit component manager, the edit panel needs. */
    private EditComponentManager editComponentManager;

    /** The dialog's buttons. */
    private JButton cancel;

    /** The dialog's buttons. */
    private JButton ok;

    /** The dialog's buttons. */
    private JButton reset;

    /** The dialog's buttons. */
    private JButton loadUserPrefs;

    /** Whether to save the settings as user preferences. */
    private JButton saveUserPrefs;

    /** The description of this dialog. */
    private JLabel description;

    /** The panel, which contains the parameters. */
    private JPanel paramsPanel;

    /** The current selection. */
    private Selection selection;

    /** The GridBagConstraints for the param panel. */
    private GridBagConstraints listConstraints;

    /** <code>true</code>, if the user selected the ok button in this dialog. */
    private boolean selectedOk = false;
    
    /**
     * Constructor for DefaultParameterDialog.
     * 
     * @param editComponentManager
     *            DOCUMENT ME!
     * @param parent
     *            the parent of this dialog.
     * @param parametrizable
     *            the parametrizable to display parameter settings for
     * @param selection
     *            DOCUMENT ME!
     */
    public DefaultParameterDialog(EditComponentManager editComponentManager,
            MainFrame parent, Parametrizable parametrizable, Selection selection) {
        super(parent, true);

        this.editComponentManager = editComponentManager;

        this.params = parametrizable.getParameters();
        
        this.defaultParams = parametrizable.getDefaultParameters();
        showUserPrefs = parametrizable instanceof AbstractParametrizable
                && ((AbstractParametrizable) parametrizable)
                        .getUserParameters() != null;
        this.selection = selection;
        this.parametrizable = parametrizable;

        getContentPane().setLayout(new BorderLayout());

        setTitle(parametrizable.getName());
        setSize(420, 320);
        setResizable(false);

        ok = new JButton(coreBundle.getString("run.dialog.button.run"));
        cancel = new JButton(coreBundle.getString("run.dialog.button.cancel"));
        description = new JLabel(coreBundle.getString("run.dialog.desc"));

        if (defaultParams != null) {
            reset = new JButton(coreBundle
                    .getIcon("run.dialog.button.reset.icon"));
            reset.setToolTipText(coreBundle
                    .getString("run.dialog.button.reset"));
        }
        if (showUserPrefs) {
            loadUserPrefs = new JButton(coreBundle
                    .getIcon("run.dialog.button.loaduserprefs.icon"));
            loadUserPrefs.setToolTipText(coreBundle
                    .getString("run.dialog.button.loaduserprefs"));
            saveUserPrefs = new JButton(coreBundle
                    .getIcon("run.dialog.button.saveuserprefs.icon"));
            saveUserPrefs.setToolTipText(coreBundle
                    .getString("run.dialog.button.saveuserprefs"));
        }
        JPanel buttonsPanel = new JPanel();

        if (defaultParams != null) {
            buttonsPanel.add(reset);
        }
        if (showUserPrefs) {
            buttonsPanel.add(loadUserPrefs);
            buttonsPanel.add(saveUserPrefs);
        }
        buttonsPanel.add(ok);
        buttonsPanel.add(cancel);

        paramsPanel = createValueEditContainer(params, selection);

        ok.setEnabled(true);

        getRootPane().setDefaultButton(ok);

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
        Parameter<?>[] params = this.paramEditPanel.getUpdatedParameters();
        // bugfix: preserve selection parameter
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof SelectionParameter) {
                ((SelectionParameter) params[i]).setSelection(selection);
            }
        }
        return params;
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

        if (src == cancel) {
            dispose();
        } else if (src == reset) {
            this.params = AbstractParametrizable.copyParameters(defaultParams);
            getContentPane().remove(paramsPanel);
            paramsPanel = createValueEditContainer(params, selection);
            getContentPane().add(paramsPanel, listConstraints);
            saveUserPrefs.setEnabled(true);
            paramsPanel.validate();
            validate();
            pack();
        } else if (src == loadUserPrefs) {
            this.params = ((AbstractParametrizable) parametrizable)
                    .getUserParameters();
            getContentPane().remove(paramsPanel);
            paramsPanel = createValueEditContainer(params, selection);
            getContentPane().add(paramsPanel, listConstraints);
            saveUserPrefs.setEnabled(false);
            paramsPanel.validate();
            validate();
            pack();
        } else if (src == saveUserPrefs) {
            ((AbstractParametrizable) parametrizable)
                    .saveUserParameters(paramEditPanel.getUpdatedParameters());
            saveUserPrefs.setEnabled(false);
        } else if (src == ok) {
            ParameterEditPanel pep = (ParameterEditPanel) paramsPanel;
            if (!pep.parametersAreValid()) {
                String text = "The following parameters are not valid:\n\n";
                List<String> errorMessages = pep
                        .getErrorMessageListOfInvalidParameters();
                for (String s : errorMessages) {
                    text += s;
                }
                JOptionPane.showMessageDialog(this, text,
                        "Error in parameters", JOptionPane.ERROR_MESSAGE);
                return;
            }

            okSelected();
        }
    }

    /*
     * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
     * PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == paramEditPanel
                && evt.getPropertyName().compareTo("PARAMS_CHANGED") == 0) {
            saveUserPrefs.setEnabled(true);
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
            reset.addActionListener(this);
        }
        if (showUserPrefs) {
            loadUserPrefs.addActionListener(this);
            saveUserPrefs.addActionListener(this);
        }

        cancel.addActionListener(this);
        ok.addActionListener(this);
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
        getContentPane().add(description, labelConstraints);

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
        getContentPane().add(ok, okConstraints);

        GridBagConstraints cancelConstraints = new GridBagConstraints();
        cancelConstraints.gridx = 2;
        cancelConstraints.gridy = 2;
        cancelConstraints.gridwidth = 1;
        cancelConstraints.gridheight = 1;
        cancelConstraints.anchor = GridBagConstraints.EAST;
        cancelConstraints.insets = new Insets(0, 8, 8, 8);
        getContentPane().add(cancel, cancelConstraints);

        if (defaultParams != null || showUserPrefs) {
            JPanel buttonPanel = new JPanel();

            if (defaultParams != null) {
                buttonPanel.add(reset);
            }

            if (showUserPrefs) {
                buttonPanel.add(loadUserPrefs);
                buttonPanel.add(saveUserPrefs);
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
        // System.out.println("ok selected");
        dispose();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
