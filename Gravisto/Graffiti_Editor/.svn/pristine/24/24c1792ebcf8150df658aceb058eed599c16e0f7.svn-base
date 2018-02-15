package quoggles.stdboxes.listoperations1;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.parameters.OptionParameter;
import quoggles.representation.BoxRepresentation;
import quoggles.representation.DefaultBoxRepresentation;

/**
 * 
 */
public class ListOperations1_Rep
    extends DefaultBoxRepresentation
    implements ActionListener {

    private JComboBox opCombo;
    
    private JComboBox ioCombo;
    
    private String[] numbers; 
    

    public ListOperations1_Rep(IBox box) {
        super(box);
    }

    
    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {
        OptionParameter opParam = (OptionParameter)parameters[0];
        opCombo = new JComboBox(opParam.getOptions());
        opCombo.setBackground(IBoxConstants.PARAM_BACKGROUND);
        Dimension size = opCombo.getPreferredSize();
        opCombo.setSize(size);

        opCombo.setEditable(opParam.isEditable());
        opCombo.setSelectedIndex(opParam.getOptionNr());
        opCombo.addActionListener(this);

        if (parameters.length == 1) {
            graphicalRep = standardizeBoxRep(graphicalRep, opCombo);
            graphicalRep.validate();

        } else {
            if (numbers == null) {
                numbers = new String[]{
                    "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
            } 
            ioCombo = new JComboBox(numbers);
            ioCombo.setBackground(IBoxConstants.PARAM_BACKGROUND);
            //combo.setRenderer(new StringConstantsRenderer());
            size = ioCombo.getPreferredSize();
            ioCombo.setSize(size);
    
            ioCombo.setEditable(true);
            ioCombo.setSelectedItem(
                ((IntegerParameter)parameters[1]).getInteger().toString());
            ioCombo.addActionListener(this);
    
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setOpaque(false);
            panel.add(opCombo);
            panel.add(ioCombo);
            panel.setPreferredSize(new Dimension(
                Math.max(opCombo.getPreferredSize().width,
                    ioCombo.getPreferredSize().width),
                opCombo.getPreferredSize().height + 
                    ioCombo.getPreferredSize().height));
        
            graphicalRep = standardizeBoxRep(graphicalRep, panel);
            graphicalRep.validate();
        }
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
        Object src = e.getSource();
        if (src == opCombo) {
            ((OptionParameter)parameters[0])
                .setValue(opCombo.getSelectedItem());
            String opStr = opCombo.getSelectedItem().toString();
            if (IBoxConstants.MAKEDISTINCT.equals(opStr)) {
                parameters = new Parameter[]{ parameters[0],
                    new IntegerParameter(1, "nrInputs", 
                        "Number of inputs (equal to number of outputs") };                    
            } else if (IBoxConstants.LISTIFY.equals(opStr)) {
                parameters = new Parameter[]{ parameters[0],
                    new IntegerParameter(2, "nrInputs", "Number of inputs") };                    
            } else if (IBoxConstants.UNPACK.equals(opStr) ||
                    IBoxConstants.DELISTIFY.equals(opStr)) {
                parameters = new Parameter[]{ parameters[0],
                    new IntegerParameter(2, "nrOutputs", "Number of outputs") };                    
            } else {
                parameters = new Parameter[]{ parameters[0] };
            }
            
        } else {
            ((IntegerParameter)parameters[1]).setValue(new Integer(
                Integer.parseInt(ioCombo.getSelectedItem().toString())));
        }
        setParameters(parameters, false);
        repaintBoxRep(e);
//      updateGraphicalRep();
//      updateInputOutput();
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