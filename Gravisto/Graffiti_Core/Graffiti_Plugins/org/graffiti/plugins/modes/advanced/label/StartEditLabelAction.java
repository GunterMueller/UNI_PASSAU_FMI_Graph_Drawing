// =============================================================================
//
//   StartEditLabelAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StartEditLabelAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.label;

import java.awt.Component;
import java.awt.Point;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.undo.UndoableEditSupport;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.undo.ChangeAttributesEdit;

/**
 * @deprecated
 */
@Deprecated
public class StartEditLabelAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 3164927046430141222L;

    private static final Logger logger = Logger
            .getLogger(StartEditLabelAction.class.getName());

    /** Reference to the LabelTool */
    private LabelTool labelTool;

    private String labelName = "label0";

    /**
     * Creates a new StartEditLabelAction object.
     * 
     * @param labelTool
     *            The given LabelTool
     */
    public StartEditLabelAction(LabelTool labelTool) {
        this.labelTool = labelTool;
    }

    /**
     * The action of this class
     * 
     * @param e
     *            The given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        Point position = e.getPosition();

        if (position == null) {

            logger.finer("Can't operate without position!!!");
        } else {
            GraphElement graphElement = labelTool.getTopGraphElement(position);

            if (graphElement != null) {
                LabelAttribute labelAttr = null;
                String oldLabel = "";
                try {
                    labelAttr = (LabelAttribute) graphElement
                            .getAttribute(labelName);
                    oldLabel = labelAttr.getLabel();
                } catch (Exception ex) {
                }

                ChangeAttributesEdit edit;

                String results[] = showEditDialog(labelTool.getViewComponent(),
                        oldLabel);
                if (results[0] == null)
                    return;
                labelName = results[0];
                String newLabel = results[1];

                labelAttr = null;
                try {
                    labelAttr = (LabelAttribute) graphElement
                            .getAttribute(labelName);
                    oldLabel = labelAttr.getLabel();
                } catch (Exception ex) {
                }

                if (labelAttr != null) {

                    if (!oldLabel.equals(newLabel)) {
                        Map<GraphElement, GraphElement> geMap = labelTool
                                .getGEMap();
                        UndoableEditSupport undoSupport = labelTool
                                .getUndoSupport();
                        edit = new ChangeAttributesEdit(labelAttr, geMap);
                        labelAttr.setLabel(newLabel);
                        undoSupport.postEdit(edit);
                    }

                    // clickedComp.getParent().repaint();
                } else { // no label found

                    if (graphElement instanceof Node) {
                        labelAttr = new NodeLabelAttribute(labelName);
                    } else {
                        labelAttr = new EdgeLabelAttribute(labelName);
                    }

                    Map<GraphElement, GraphElement> geMap = labelTool
                            .getGEMap();
                    UndoableEditSupport undoSupport = labelTool
                            .getUndoSupport();

                    graphElement.addAttribute(labelAttr, "");

                    edit = new ChangeAttributesEdit(labelAttr, geMap);
                    labelAttr.setLabel(newLabel);

                    undoSupport.postEdit(edit);

                    // clickedComp.getParent().repaint();
                }
            }
        }
    }

    /**
     * Searches for attributes of an label
     * 
     * @param attr
     *            The given Attribute
     * @param attributeType
     *            The given AttributeType
     * 
     * @return Attribute of an label
     */
    protected Attribute searchForAttribute(Attribute attr,
            Class<?> attributeType) {
        if (attributeType.isInstance(attr))
            return attr;
        else {
            if (attr instanceof CollectionAttribute) {
                for (Attribute attribute : ((CollectionAttribute) attr)
                        .getCollection().values()) {
                    Attribute newAttr = searchForAttribute(attribute,
                            attributeType);

                    if (newAttr != null)
                        return newAttr;
                }
            } else if (attr instanceof CompositeAttribute)
                // TODO: treat those correctly; some of those have not yet
                // been correctly implemented
                return null;
        }

        return null;
    }

    /**
     * Shows the dialog to enter the new label of the Component
     * 
     * @param parent
     *            The given Component
     * @param initialText
     *            The text
     * 
     * @return The dialog value
     */
    /*
     * private String showEditDialog(Component parent, String initialText) {
     * String returnValue = JOptionPane.showInputDialog(parent,
     * "Enter new label: ", initialText);
     * 
     * if (returnValue == null) { return initialText; } else {
     * 
     * return returnValue; } }
     */

    private String[] showEditDialog(Component parent, String initialText) {

        JTextField labelNameField = new JTextField(labelName);
        JTextField labelValueField = new JTextField(initialText);

        JOptionPane creator = new JOptionPane(new Object[] {
                "Enter the label name:", labelNameField,
                "Enter the label value:", labelValueField },
                JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
        JDialog dialog = creator.createDialog(parent, UIManager
                .getString("OptionPane.inputDialogTitle"));
        labelValueField.requestFocusInWindow();
        dialog.setVisible(true);
        labelName = labelNameField.getText();
        String labelValue = labelValueField.getText();

        if (labelValue != null)
            // System.out.println("Set " + labelName + " to " + labelValue);
            return new String[] { labelName, labelValue };
        return new String[] { null, null };
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
