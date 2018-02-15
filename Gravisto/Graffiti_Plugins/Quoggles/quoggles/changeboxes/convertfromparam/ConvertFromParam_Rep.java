package quoggles.changeboxes.convertfromparam;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.parameters.OptionParameter;
import quoggles.representation.DefaultBoxRepresentation;

/**
 * Represents a sub query saved in a file.
 */
public class ConvertFromParam_Rep extends DefaultBoxRepresentation 
    implements ActionListener {

    private JComboBox combo;
    
    
    /**
     * Constructor of the box.
     * 
     * @param representedBox
     */
    public ConvertFromParam_Rep(IBox representedBox) {
        super(representedBox);
    }

    
    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {
        combo = new JComboBox(((OptionParameter)parameters[0]).getOptions());
        combo.setBackground(IBoxConstants.PARAM_BACKGROUND);
//        combo.setRenderer(new StringConstantsRenderer());
        Dimension size = combo.getPreferredSize();
        combo.setSize(size);

        combo.setEditable(((OptionParameter)parameters[0]).isEditable());
        combo.setSelectedItem(((OptionParameter)parameters[0]).getValue());
        combo.addActionListener(this);

        graphicalRep = standardizeBoxRep(graphicalRep, combo);
        graphicalRep.validate();
    }
    
    
    /**
     * @see quoggles.representation.IBoxRepresentation#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
        super.setParameters(params, fromBox);
        combo.setSelectedItem(((OptionParameter)parameters[0]).getValue());
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        ((OptionParameter)parameters[0]).setValue(combo.getSelectedItem());
        updateInputOutput();
    }
}
