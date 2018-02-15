/*
 * 
 */
package quoggles.auxboxes.booleanop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JComboBox;

import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.parameters.OptionParameter;
import quoggles.representation.DefaultBoxRepresentation;

/**
 *
 */
public class BooleanOp_Rep extends DefaultBoxRepresentation 
    implements ActionListener {

    private JComboBox operCombo;
    
    private Point2D input1;
    
    private Point2D input2;
    
    
    /**
     * @param representedBox
     */
    public BooleanOp_Rep(IBox representedBox) {
        super(representedBox);
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {

        
        input1 = new Point2D.Double(0d, 0.05d);
        input2 = new Point2D.Double(0d, 0.95d);
        
        operCombo = new JComboBox(
            ((OptionParameter)parameters[0]).getOptions());
        operCombo.setBackground(IBoxConstants.PARAM_BACKGROUND);
        operCombo.setEditable(((OptionParameter)parameters[0]).isEditable());
        operCombo.setSelectedItem(((OptionParameter)parameters[0]).getValue());
        operCombo.addActionListener(this);
        
        operCombo.setPreferredSize(operCombo.getPreferredSize());
        operCombo.setSize(operCombo.getPreferredSize());
        
        graphicalRep = standardizeBoxRep(graphicalRep, operCombo);
        graphicalRep.validate();
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (operCombo.equals(src)) {
            ((OptionParameter)parameters[0]).setValue(operCombo.getSelectedItem());
        } else
        
        updateInputOutput();
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#
     * setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
        super.setParameters(params, fromBox);
        operCombo.setSelectedItem(((OptionParameter)parameters[0]).getValue());
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#getRelInputsPos()
     */
    public Point2D[] getRelInputsPos() {
        return new Point2D[]{ input1, input2 };
    }

}
