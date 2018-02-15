package quoggles.stdboxes.neighborhood;

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
 *
 */
public class Neighborhood_Rep extends DefaultBoxRepresentation 
    implements ActionListener {

    private JComboBox combo;
    
    /**
     * @param representedBox
     */
    public Neighborhood_Rep(IBox representedBox) {
        super(representedBox);
    }


    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {
        combo = new JComboBox(((OptionParameter)parameters[0]).getOptions());
        combo.setBackground(IBoxConstants.PARAM_BACKGROUND);
        Dimension size = combo.getPreferredSize();
        combo.setSize(size);
        
        combo.setEditable(((OptionParameter)parameters[0]).isEditable());
        combo.setSelectedIndex(0);
        combo.addActionListener(this);
        
        graphicalRep = standardizeBoxRep(graphicalRep, combo);
        graphicalRep.validate();
    }
    
    /**
     * @see quoggles.representation.IBoxRepresentation#
     * setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
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
