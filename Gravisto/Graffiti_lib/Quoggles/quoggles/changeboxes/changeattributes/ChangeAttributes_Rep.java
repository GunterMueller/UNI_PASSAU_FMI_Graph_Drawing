package quoggles.changeboxes.changeattributes;

import java.awt.Dimension;
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

import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.parameters.OptionParameter;
import quoggles.representation.DefaultBoxRepresentation;

/**
 *
 */
public class ChangeAttributes_Rep extends DefaultBoxRepresentation 
    implements ActionListener, FocusListener {

    private JComboBox attrType;
    
    private JTextField attrPath;  
    
    
    /**
     * @param representedBox
     */
    public ChangeAttributes_Rep(IBox representedBox) {
        super(representedBox);
    }
    

    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {

        
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        attrType = new JComboBox(
            ((OptionParameter)parameters[0]).getOptions());
        attrType.setBackground(IBoxConstants.PARAM_BACKGROUND);
        attrType.setEditable(((OptionParameter)parameters[0]).isEditable());
        attrType.setSelectedItem(((OptionParameter)parameters[0]).getValue());
        attrType.addActionListener(this);
        
        attrPath = new JTextField(
            ((StringParameter)parameters[1]).getString());
        attrPath.addActionListener(this);
        attrPath.addFocusListener(this);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(attrType);
        panel.add(attrPath);
        
        panel.setPreferredSize(new Dimension(
            Math.max(
                attrType.getPreferredSize().width,
                    attrPath.getPreferredSize().width),
                attrType.getPreferredSize().height +
                    attrPath.getPreferredSize().height));

        graphicalRep = standardizeBoxRep(graphicalRep, panel);
        graphicalRep.validate();
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (attrType == src) {
            ((OptionParameter)parameters[0]).setValue(attrType.getSelectedItem());
        }
        
        updateInputOutput();
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#
     * setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
        super.setParameters(params, fromBox);
        attrType.setSelectedItem(((OptionParameter)parameters[0]).getValue());
        attrPath.setText(((StringParameter)parameters[1]).getString());
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
        if (attrPath.equals(src)) {
            ((StringParameter)parameters[1]).setValue(attrPath.getText());
        }
        
        updateInputOutput();
    }

}
