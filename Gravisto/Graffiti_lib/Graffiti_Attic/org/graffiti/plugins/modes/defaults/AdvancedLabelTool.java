// =============================================================================
//
//   AdvancedLabelTool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AdvancedLabelTool.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.modes.defaults;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.undo.ChangeAttributesEdit;
import org.graffiti.util.GeneralUtils;

/**
 * A tool for creating and editing labels of graphelements.
 * 
 * @author Wirch
 * @version $Revision: 5772 $
 * @deprecated
 */
@Deprecated
public class AdvancedLabelTool extends MegaTools {
    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(AdvancedLabelTool.class.getName());

    /** The value of chosen label */
    protected String labelValue = "";

    /** The standard label path for capacity */
    private final String CAPACITY = ".capacity";

    /** The standard label path */
    private final String LABEL = ".label";

    /** The standard label path for weight */
    private final String WEIGHT = ".weight";

    /** The graph element which will be labeled or whom label will be edited. */
    private GraphElement ge;

    /** The combo box for choosing labels which will be added or edited. */
    private JComboBox labelNamesComboBox;

    /** The label editing dialog */
    private JDialog labelEditingDialog;

    /** The text field for putting in the label value. */
    private JTextField labelTextField;

    /**
     * The label path of chosen label.
     */
    private String labelPath = "";

    /** The array with all stadard label paths */
    private Object[] standardLabelPaths = { LABEL, CAPACITY, WEIGHT };

    /**
     * The boolean flag which will set true if user did'nt canceled the label
     * editing dialog
     */
    private boolean accepted;

    /**
     * Invoked if user presses mouse button.
     * 
     * @param e
     *            the mouse event
     */
    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e))
            return;

        Component clickedComp = this.findComponentAt(e);

        if (clickedComp instanceof GraphElementComponent) {
            ge = ((GraphElementComponent) clickedComp).getGraphElement();

            List<Attribute> attributesList = new LinkedList<Attribute>();
            GeneralUtils.searchForAttributes(ge.getAttribute(""),
                    LabelAttribute.class, attributesList);

            ChangeAttributesEdit edit;

            if (!(attributesList.isEmpty())) {
                // determine the first label attribute in the list which will be
                // displayed as first in the combo box of label editing dialog.
                LabelAttribute labelAttr = (LabelAttribute) attributesList
                        .get(0);
                String oldLabel = labelAttr.getLabel();

                Object[] labelNames = new Object[attributesList.size()];

                int labelPosInList = 0;

                for (Attribute attribute : attributesList) {
                    labelNames[labelPosInList++] = attribute.getPath();
                }

                // display a dialog for editing labels
                showEditDialog(clickedComp, oldLabel, labelAttr.getPath(),
                        labelNames);

                if (!accepted)
                    return;

                try {
                    try {
                        labelAttr = (LabelAttribute) ge.getAttribute(labelPath);
                        oldLabel = labelAttr.getLabel();

                        if (!oldLabel.equals(labelValue)) {
                            edit = new ChangeAttributesEdit(labelAttr, geMap);
                            labelAttr.setLabel(labelValue);
                            undoSupport.postEdit(edit);
                        }
                    } catch (ClassCastException cce) {
                        throw new AttributeNotFoundException("");
                    }
                } catch (AttributeNotFoundException anfe) {
                    createNewLabel(ge, labelPath, labelValue);
                }

                clickedComp.getParent().repaint();
                setFieldsToInitialValues();

            } else { // no label found
                showEditDialog(clickedComp, "", standardLabelPaths[0]
                        .toString(), standardLabelPaths);

                if (!accepted)
                    return;

                createNewLabel(ge, labelPath, labelValue);
                clickedComp.getParent().repaint();
                setFieldsToInitialValues();
            }
        }
    }

    /**
     * Initializes fields with start values
     */
    private void setFieldsToInitialValues() {
        labelValue = "";
        labelPath = "";
        ge = null;
    }

    /**
     * Displays the label editing dialog and saves all done changes at a label.
     * 
     * @param parent
     *            Component which will be parent of the dialog.
     * @param initialText
     *            the initial value of the label, which will be displayed as
     *            first in a combo box of the dialog.
     * @param initialLabelName
     *            the label, which will be displayed as first in a combo box of
     *            the dialog.
     * @param labelNames
     *            all label names which will appear in a combo box of the
     *            dialog.
     */
    protected void showEditDialog(Component parent, String initialText,
            String initialLabelName, Object[] labelNames) {
        accepted = false;
        labelEditingDialog = new JDialog();

        GridBagConstraints gridBagConstraints;
        JPanel dialogPanel = new JPanel();

        labelTextField = new JTextField();

        labelNamesComboBox = new JComboBox(labelNames);

        boolean[] isContainedInComboBox = new boolean[standardLabelPaths.length];

        for (int i = 0; i < isContainedInComboBox.length; i++) {
            isContainedInComboBox[i] = false;
        }

        for (int i = 0; i < labelNames.length; i++) {
            for (int j = 0; j < standardLabelPaths.length; j++) {
                if (labelNames[i].equals(standardLabelPaths[j])) {
                    isContainedInComboBox[j] = true;
                }
            }
        }

        for (int i = 0; i < isContainedInComboBox.length; i++) {
            if (!isContainedInComboBox[i]) {
                labelNamesComboBox.addItem(standardLabelPaths[i]);
            }
        }

        JLabel labelNameLabel = new JLabel();
        JLabel labelValueLabel = new JLabel();

        JPanel buttonsPanel = new javax.swing.JPanel();
        JButton okButton = new javax.swing.JButton();
        JButton cancelButton = new javax.swing.JButton();

        labelEditingDialog.setTitle("Enter a new label");
        labelEditingDialog
                .setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        labelEditingDialog
                .addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent evt) {
                        labelEditingDialog.setVisible(false);
                        labelEditingDialog.dispose();
                    }
                });

        dialogPanel.setLayout(new java.awt.GridBagLayout());

        dialogPanel.setBorder(new javax.swing.border.EtchedBorder());

        labelNameLabel.setText("Path of label");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        dialogPanel.add(labelNameLabel, gridBagConstraints);

        labelValueLabel.setText("label");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        dialogPanel.add(labelValueLabel, gridBagConstraints);

        labelNamesComboBox.setEditable(true);
        labelNamesComboBox.setSelectedItem(initialLabelName);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        dialogPanel.add(labelNamesComboBox, gridBagConstraints);

        labelTextField.setColumns(10);
        labelTextField.setText(initialText);
        labelTextField.setCaretPosition(0);
        labelTextField.moveCaretPosition(initialText.length());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        dialogPanel.add(labelTextField, gridBagConstraints);

        labelEditingDialog.getContentPane().add(dialogPanel,
                java.awt.BorderLayout.NORTH);

        buttonsPanel.setLayout(new java.awt.GridLayout(1, 0));

        okButton.setText("Ok");
        buttonsPanel.add(okButton);
        labelEditingDialog.getRootPane().setDefaultButton(okButton);

        cancelButton.setText("Cancel");
        buttonsPanel.add(cancelButton);

        labelEditingDialog.getContentPane().add(buttonsPanel,
                java.awt.BorderLayout.CENTER);

        okButton.setMnemonic(okButton.getText().charAt(0));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                labelValue = labelTextField.getText();
                labelPath = labelNamesComboBox.getSelectedItem().toString();

                accepted = true;
                labelEditingDialog.setVisible(false);
                labelEditingDialog.dispose();
            }
        });
        okButton.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();

                if (cmd.equals("PressedENTER")) {
                    ((JButton) e.getSource()).doClick();
                }
            }
        }, "PressedENTER", KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        cancelButton.setMnemonic(cancelButton.getText().charAt(0));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                labelEditingDialog.setVisible(false);
                labelEditingDialog.dispose();
            }
        });
        cancelButton.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String cmd = e.getActionCommand();

                if (cmd.equals("PressedESCAPE")) {
                    ((JButton) e.getSource()).doClick();
                }
            }
        }, "PressedESCAPE", KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        labelNamesComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    labelPath = labelNamesComboBox.getSelectedItem().toString();

                    Attribute attribute = ge.getAttribute(labelPath);
                    labelTextField.setText(((LabelAttribute) attribute)
                            .getLabel());
                } catch (AttributeNotFoundException e1) {
                    labelTextField.setText("");
                }
            }
        });

        // labelEditingDialog.setLocationRelativeTo(parent);
        labelEditingDialog.setLocationRelativeTo(null); // center on screen
        labelEditingDialog.setModal(true);
        labelEditingDialog.pack();
        labelEditingDialog.setVisible(true);
    }

    /**
     * Create a new label attribute with given path and value.
     * 
     * @param ge
     *            the GraphElement where a new label will be added.
     * @param labelPath
     *            the path of new label.
     * @param labelValue
     *            the value of new label.
     */
    private void createNewLabel(GraphElement ge, String labelPath,
            String labelValue) {
        ChangeAttributesEdit edit;
        LabelAttribute labelAttr;
        String[] labelAttributePath = labelPath.split("\\"
                + Attribute.SEPARATOR);

        String id = labelAttributePath[labelAttributePath.length - 1];
        logger.finer("The id of Attribute is " + id);

        StringBuffer location = new StringBuffer();
        // location.append(Attribute.SEPARATOR);

        // determine the location where the new attribute will be added.
        // location is either "" or a string composed of many parts, which are
        // separated by "." or of one one part without separator.
        for (int i = 1; i < (labelAttributePath.length - 1); i++) {
            location.append(labelAttributePath[i]);
            if (i + 1 < labelAttributePath.length - 1) {
                location.append(Attribute.SEPARATOR);
            }
        }

        logger.finer("The location of the new attribute is "
                + location.toString());

        if (ge instanceof Node) {
            labelAttr = new NodeLabelAttribute(id);
        } else {
            labelAttr = new EdgeLabelAttribute(id);
        }

        ge.addAttribute(labelAttr, location.toString());
        edit = new ChangeAttributesEdit(labelAttr, geMap);
        labelAttr.setLabel(labelValue);

        undoSupport.postEdit(edit);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
