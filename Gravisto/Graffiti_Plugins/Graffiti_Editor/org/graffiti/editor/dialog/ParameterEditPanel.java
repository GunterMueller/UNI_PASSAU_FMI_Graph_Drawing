// =============================================================================
//
//   ParameterEditPanel.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ParameterEditPanel.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.graffiti.plugin.ToolTipHelper;
import org.graffiti.plugin.editcomponent.StandardValueEditComponent;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;

/**
 * Represents a parameter edit panel.
 * 
 * @version $Revision: 5768 $
 */
public class ParameterEditPanel extends JPanel implements ActionListener,
        DocumentListener {

    /**
     * 
     */
    private static final long serialVersionUID = -7718760565916732242L;

    /** Holds the edit fields cells. */
    private JPanel editFieldPanel;

    /** Holds the id cells. */
    private JPanel idPanel;

    /** DOCUMENT ME! */
    private JScrollPane tableScroll;

    /** Splits table into id and edit field. */
    private JSplitPane tableSplit;

    /** DOCUMENT ME! */
    private List<ValueEditComponent> displayedVEC;

    /**
     * Maps from an displayable class name to the class name of a
     * <code>ValueEditComponent</code>.
     */
    private Map<Class<?>, Class<?>> editTypeMap;

    /** The list of parameters to display and edit. */
    private Parameter<?>[] parameters;

    /** The current selection */
    private Selection selection;

    private boolean parameterDialogUpdatePending;

    /**
     * Instantiates a new edit panel.
     * 
     * @param parameters
     *            DOCUMENT ME!
     * @param editTypes
     *            DOCUMENT ME!
     * @param selection
     *            DOCUMENT ME!
     */
    public ParameterEditPanel(Parameter<?>[] parameters,
            Map<Class<?>, Class<?>> editTypes, Selection selection) {
        super();

        this.parameters = parameters;

        this.selection = selection;

        parameterDialogUpdatePending = false;

        this.displayedVEC = new LinkedList<ValueEditComponent>();
        setEditTypeMap(editTypes);

        idPanel = new JPanel();
        idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.Y_AXIS));

        editFieldPanel = new JPanel();
        editFieldPanel
                .setLayout(new BoxLayout(editFieldPanel, BoxLayout.Y_AXIS));

        tableSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, idPanel,
                editFieldPanel);
        tableSplit.setDividerLocation(120); // 75
        tableSplit.setDividerSize(3);
        tableSplit.setOneTouchExpandable(true);

        tableScroll = new JScrollPane(tableSplit);
        // tableScroll.setPreferredSize(new Dimension(200, 200));

        this.setLayout(new BorderLayout());
        this.add(tableScroll, BorderLayout.CENTER);

        buildTable(selection);

        this.revalidate();
    }

    /**
     * Sets the map of displayable types to the given map.
     * 
     * @param map
     *            DOCUMENT ME!
     */
    public void setEditTypeMap(Map<Class<?>, Class<?>> map) {
        this.editTypeMap = map;
    }

    /**
     * Sets the paramter array this panel displays.
     * 
     * @param params
     */
    public void setParameters(Parameter<?>[] params) {
        this.parameters = params;
    }

    /**
     * Returns the array of parameters with the values updated from the dialog.
     * 
     * @return Parameter[]
     */
    public Parameter<?>[] getUpdatedParameters() {
        for (ValueEditComponent vec : displayedVEC) {
            vec.setValue();
        }

        return this.parameters;
    }

    /**
     * Builds the table that is used for editing parameters
     * 
     * @param selection
     *            list of parameters.
     */
    public void buildTable(Selection selection) {
        idPanel.removeAll();
        editFieldPanel.removeAll();
        displayedVEC = new LinkedList<ValueEditComponent>();
        addValueEditComponents(idPanel, editFieldPanel, selection);
        revalidate();
    }

    /**
     * Returns a (noneditable) textfield showing the value of the
     * <code>toString</code> method of the parameter. Used when there is no
     * registered <code>ValueEditComponent</code>.
     * 
     * @param parameter
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private ValueEditComponent getStandardEditComponent(Parameter<?> parameter) {
        ValueEditComponent vec = new StandardValueEditComponent(parameter);

        // JTextField textField = new
        // JTextField(parameter.getValue().toString());
        JTextField textField = (JTextField) vec.getComponent();
        textField.setEditable(false);
        textField.setMinimumSize(new Dimension(0, 20));
        textField.setPreferredSize(new Dimension(100, 30));
        textField.setMaximumSize(new Dimension(2000, 40));

        return vec;
    }

    /**
     * Add one row in the panel.
     * 
     * @param idPanel
     * @param editFieldPanel
     * @param parameter
     * @param ecClass
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    private void addRow(JPanel idPanel, JPanel editFieldPanel,
            Parameter<?> parameter, Class<?> ecClass) {
        JTextField textField = new JTextField(parameter.getName());
        textField.setToolTipText(parameter.getDescription());
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        textField.setEditable(false);

        ValueEditComponent editComp = null;

        try {
            try {
                // try to find a constructor matching the concrete parameter
                // class
                editComp = (ValueEditComponent) InstanceLoader.createInstance(
                        ecClass, parameter);
            } catch (InstanceCreationException ice) {
                // if we did not find such a constructor, try to locate a
                // contructor for org.graffiti.plugin.Displayable -> this
                // is necessary since java reflection only locates a
                // constructor exactly matching the specified parameter
                // classes, and no best matching constructor (java bug 4287725)
                editComp = (ValueEditComponent) InstanceLoader.createInstance(
                        ecClass, "org.graffiti.plugin.Displayable", parameter);
            }
            ToolTipHelper.addToolTip(editComp.getComponent(), parameter
                    .getDescription());
        } catch (InstanceCreationException ice) {
            throw new RuntimeException(
                    "Could not create an instance of a ValueEditComponent class. "
                            + ice);
        }

        editComp.setDisplayable(parameter);
        editComp.setEditFieldValue();

        JComponent editCompComp = editComp.getComponent();
        textField.setMinimumSize(new Dimension(0, (int) editCompComp
                .getMinimumSize().getHeight()));
        textField.setPreferredSize(editCompComp.getPreferredSize());
        textField.setMaximumSize(new Dimension(2000, (int) editCompComp
                .getMaximumSize().getHeight()));
        idPanel.add(textField);
        editFieldPanel.add(editCompComp);
        displayedVEC.add(editComp);
    }

    /**
     * Add one row in the panel using a standard edit component.
     * 
     * @param parameter
     * @param idPanel
     * @param editFieldPanel
     */
    private void addStandardRow(Parameter<?> parameter, JPanel idPanel,
            JPanel editFieldPanel) {
        ValueEditComponent editComp = getStandardEditComponent(parameter);
        displayedVEC.add(editComp);

        JTextField textField;
        textField = new JTextField(parameter.getName());
        textField.setToolTipText(parameter.getDescription());
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        textField.setEditable(false);

        JComponent editCompComp = editComp.getComponent();
        editCompComp.setToolTipText(parameter.getDescription());
        textField.setMinimumSize(new Dimension(0, (int) editCompComp
                .getMinimumSize().getHeight()));
        textField.setMaximumSize(new Dimension(2000, (int) editCompComp
                .getMaximumSize().getHeight()));
        idPanel.add(textField);
        editFieldPanel.add(editCompComp);
    }

    /**
     * Puts text fields for the IDs in a panel.
     * 
     * @param idPanel
     *            the list of parameters
     * @param editFieldPanel
     *            DOCUMENT ME!
     * @param selection
     *            DOCUMENT ME!
     */
    private void addValueEditComponents(JPanel idPanel, JPanel editFieldPanel,
            Selection selection) {
        for (Parameter<?> param : parameters) {
            if (param instanceof SelectionParameter) {
                continue;
            }

            /*
             * check whether there exists a ValueEditComponent, if not use
             * standard edit component
             */
            Class<?> ecClass = this.editTypeMap.get(param.getClass());

            Parameter<?> parent = param.getDependencyParent();
            if (parent != null) {
                if (!parent.isVisible()
                        || !parent.getValue()
                                .equals(param.getDependencyValue())) {
                    param.setVisible(false);
                    continue;
                }
            }
            param.setVisible(true);
            if (ecClass != null) {
                // if we have a registered component to display it, add it
                this.addRow(idPanel, editFieldPanel, param, ecClass);
            } else {
                // no component registered for this basic displayable
                addStandardRow(param, idPanel, editFieldPanel);
            }
            for (ValueEditComponent vec : displayedVEC) {
                JComponent comp = vec.getComponent();
                if (comp instanceof JCheckBox) {
                    ((JCheckBox) comp).addActionListener(this);
                }
                if (comp instanceof JComboBox) {
                    ((JComboBox) comp).addActionListener(this);
                }
                if (comp instanceof JTextField) {
                    ((JTextField) comp).addActionListener(this);
                }
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel) comp;
                    for (Component c : panel.getComponents()) {
                        if (c instanceof JTextField) {
                            ((JTextField) c).getDocument().addDocumentListener(
                                    this);
                            ((JTextField) c).addActionListener(this);
                        }
                    }
                }
            }
        }
    }

    public boolean parametersAreValid() {
        if (displayedVEC == null)
            return true;

        for (ValueEditComponent vec : displayedVEC) {
            if (!vec.isValid())
                return false;
        }
        return true;
    }

    public List<String> getErrorMessageListOfInvalidParameters() {
        LinkedList<String> result = new LinkedList<String>();
        if (displayedVEC == null)
            return result;

        int i = 0;
        for (ValueEditComponent vec : displayedVEC) {
            if (!vec.isValid()) {
                result.add("The parameter \"" + parameters[i].getName() + "\" "
                        + vec.getErrorMessageOfInvalidParameter() + ".\n");
            }
            i++;
        }
        return result;
    }

    private boolean parametersRebuildNecessary() {
        for (Parameter<?> param : parameters) {
            if (param instanceof SelectionParameter) {
                continue;
            }
            boolean shouldBeVisible = true;
            Parameter<?> parent = param.getDependencyParent();
            if (parent != null) {
                if (!parent.isVisible()
                        || !parent.getValue()
                                .equals(param.getDependencyValue())) {
                    shouldBeVisible = false;
                }
            }
            if (shouldBeVisible != param.isVisible())
                return true;
        }
        return false;
    }

    private void updateParameterDialog() {
        if (parameterDialogUpdatePending)
            return;
        parameterDialogUpdatePending = true;
        final JPanel self = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setParameters(getUpdatedParameters());
                if (parametersRebuildNecessary()) {
                    buildTable(selection);
                    ((JDialog) getRootPane().getParent()).pack();
                }
                parameterDialogUpdatePending = false;
                self.firePropertyChange("PARAMS_CHANGED", false, true);
            }
        });
    }

    /*
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {
        updateParameterDialog();
    }

    /*
     * @seejavax.swing.event.DocumentListener#changedUpdate(javax.swing.event.
     * DocumentEvent)
     */
    public void changedUpdate(DocumentEvent arg0) {
        updateParameterDialog();
    }

    /*
     * @seejavax.swing.event.DocumentListener#insertUpdate(javax.swing.event.
     * DocumentEvent)
     */
    public void insertUpdate(DocumentEvent arg0) {
        updateParameterDialog();
    }

    /*
     * @seejavax.swing.event.DocumentListener#removeUpdate(javax.swing.event.
     * DocumentEvent)
     */
    public void removeUpdate(DocumentEvent arg0) {
        updateParameterDialog();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
