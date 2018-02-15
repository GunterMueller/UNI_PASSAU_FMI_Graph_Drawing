package quoggles.deprecated.stdboxes.listify;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.representation.BoxRepresentation;
import quoggles.representation.DefaultBoxRepresentation;

/**
 * 
 */
public class Listify_Rep
    extends DefaultBoxRepresentation
    implements ActionListener {

    private JComboBox combo;
    
    private String[] numbers; 
    

    public Listify_Rep(IBox box) {
        super(box);
    }

    
    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {
        if (numbers == null) {
            numbers = new String[]{
                "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        } 
        combo = new JComboBox(numbers);
        combo.setBackground(IBoxConstants.PARAM_BACKGROUND);
        //combo.setRenderer(new StringConstantsRenderer());
        Dimension size = combo.getPreferredSize();
        combo.setSize(size);

        combo.setEditable(true);
        combo.setSelectedItem(
            ((IntegerParameter)parameters[0]).getInteger().toString());
        combo.addActionListener(this);

        graphicalRep = standardizeBoxRep(graphicalRep, combo);
        graphicalRep.validate();
    }
    
    /**
     * @see quoggles.representation.IBoxRepresentation#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
        super.setParameters(params, fromBox);
//        combo.setSelectedItem(
//            ((IntegerParameter)parameters[0]).getInteger().toString());
        updateGraphicalRep();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        ((IntegerParameter)parameters[0]).setValue(new Integer(
            Integer.parseInt(combo.getSelectedItem().toString())));
        setParameters(parameters, false);
        repaintBoxRep(e);
//        updateGraphicalRep();
//        updateInputOutput();
    }

    /**
     * Uses a combo box to let the user change the (only) parameter.
     * 
     * @see quoggles.representation.IBoxRepresentation#getRepresentation()
     */
    public BoxRepresentation getRepresentation() {
        return graphicalRep;
    }

}