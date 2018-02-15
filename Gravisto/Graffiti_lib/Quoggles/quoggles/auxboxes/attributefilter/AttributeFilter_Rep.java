package quoggles.auxboxes.attributefilter;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

import quoggles.auxiliary.Util;
import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.parameters.OptionParameter;
import quoggles.representation.DefaultBoxRepresentation;

/**
 *
 */
public class AttributeFilter_Rep extends DefaultBoxRepresentation 
    implements ActionListener, FocusListener {

    private JComboBox attrPath;
    
    private JComboBox attrType;
    
    private JComboBox relation;
    
    private JTextField attrValue;  
    
    
    /**
     * @param representedBox
     */
    public AttributeFilter_Rep(IBox representedBox) {
        super(representedBox);
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {

        
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        attrPath = new JComboBox(
            ((OptionParameter)parameters[0]).getOptions());
        attrPath.setEditable(((OptionParameter)parameters[0]).isEditable());
        attrPath.setBackground(IBoxConstants.PARAM_BACKGROUND);
        attrPath.setSelectedItem(((OptionParameter)parameters[0]).getValue());
        attrPath.addActionListener(this);
        
        attrType = new JComboBox(
            ((OptionParameter)parameters[1]).getOptions());
        attrType.setBackground(IBoxConstants.PARAM_BACKGROUND);
        attrType.setEditable(((OptionParameter)parameters[1]).isEditable());
        attrType.setSelectedItem(((OptionParameter)parameters[1]).getValue());
        attrType.addActionListener(this);
        
        relation = new JComboBox(
            ((OptionParameter)parameters[2]).getOptions());
        relation.setBackground(IBoxConstants.PARAM_BACKGROUND);
        relation.setEditable(((OptionParameter)parameters[2]).isEditable());
        relation.setSelectedItem(((OptionParameter)parameters[2]).getValue());
        relation.addActionListener(this);
        
        attrValue = new JTextField(
            ((StringParameter)parameters[3]).getString());
        attrValue.addActionListener(this);
        attrValue.addFocusListener(this);

        JPanel comparePanel = new JPanel();
        comparePanel.setOpaque(false);
        comparePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        comparePanel.add(attrType);
        comparePanel.add(relation);
        comparePanel.setPreferredSize(new Dimension(
            attrType.getPreferredSize().width + relation.getPreferredSize().width,
            Math.max(attrType.getPreferredSize().height, 
                relation.getPreferredSize().height)));
        
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(attrPath);
        panel.add(comparePanel);
        panel.add(attrValue);
        
        panel.setPreferredSize(new Dimension(
            Util.max(Math.min(attrPath.getPreferredSize().width, 
                        IBoxConstants.DEFAULT_BOX_WIDTH), 
                comparePanel.getPreferredSize().width,
                attrValue.getPreferredSize().width, 0),
            attrPath.getPreferredSize().height + 
                comparePanel.getPreferredSize().height +
                attrValue.getPreferredSize().height));

        graphicalRep = standardizeBoxRep(graphicalRep, panel);
        graphicalRep.validate();
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (attrPath.equals(src)) {
            ((OptionParameter)parameters[0]).setValue(attrPath.getSelectedItem());
        } else
        if (attrType.equals(src)) {
            ((OptionParameter)parameters[1]).setValue(attrType.getSelectedItem());
        } else
        if (relation.equals(src)) {
            ((OptionParameter)parameters[2]).setValue(relation.getSelectedItem());
        } else
        if (attrValue.equals(src)) {
            ((StringParameter)parameters[3]).setValue(attrValue.getText());
        }
        
        updateInputOutput();
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
        super.setParameters(params, fromBox);
        attrPath.setSelectedItem(((OptionParameter)parameters[0]).getValue());
        attrType.setSelectedItem(((OptionParameter)parameters[1]).getValue());
        relation.setSelectedItem(((OptionParameter)parameters[2]).getValue());
        attrValue.setText(((StringParameter)parameters[3]).getString());
    }

    /**
     * Empty.
     * 
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e) {
    }

    /**
     * Similar to <code>actionPerformed</code>. Shows a possible change
     * in a parameter. Needed for text areas. 
     * 
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e) {
        Object src = e.getSource();
        if (attrValue.equals(src)) {
            ((StringParameter)parameters[3]).setValue(attrValue.getText());
        }
        
        updateInputOutput();
    }

}
