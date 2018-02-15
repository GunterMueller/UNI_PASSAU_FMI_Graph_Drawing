package quoggles.stdboxes.listoperations2;

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
public class ListOperations2_Rep
    extends DefaultBoxRepresentation
    implements ActionListener {

    private JComboBox combo;
    
    private JComboBox ioCombo;
    
    private String[] numbers;


    public ListOperations2_Rep(IBox box) {
        super(box);
    }


    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {
        combo = new JComboBox(((OptionParameter)parameters[1]).getOptions());
        combo.setBackground(IBoxConstants.PARAM_BACKGROUND);
        Dimension size = combo.getPreferredSize();
        combo.setSize(size);

        combo.setEditable(((OptionParameter)parameters[1]).isEditable());
        combo.setSelectedItem(((OptionParameter)parameters[1]).getValue());
        combo.addActionListener(this);

        if (numbers == null) {
            numbers = new String[]{ "2", "4", "6", "8", "10" };
        } 

        ioCombo = new JComboBox(numbers);
        ioCombo.setBackground(IBoxConstants.PARAM_BACKGROUND);
        size = ioCombo.getPreferredSize();
        ioCombo.setSize(size);

        ioCombo.setEditable(true);
        ioCombo.setSelectedItem(
            ((IntegerParameter)parameters[0]).getInteger().toString());
        ioCombo.addActionListener(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(ioCombo);
        panel.add(combo);
        panel.setPreferredSize(new Dimension(
            Math.max(ioCombo.getPreferredSize().width,
                combo.getPreferredSize().width),
            ioCombo.getPreferredSize().height + 
                combo.getPreferredSize().height));
        
        graphicalRep = standardizeBoxRep(graphicalRep, panel);
        graphicalRep.validate();
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(combo)) {
            ((OptionParameter)parameters[1]).setValue(combo.getSelectedItem());
            updateInputOutput();
        } else {
            int newIONumber = 
                Integer.parseInt(ioCombo.getSelectedItem().toString());
            if (newIONumber % 2 != 0) {
                // number of inputs must be multiple of 2
                newIONumber++;
            }
            
            ((IntegerParameter)parameters[0]).setValue(new Integer(
                newIONumber));
            setParameters(parameters, false);
            repaintBoxRep(e);
        }
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
        super.setParameters(params, fromBox);
        combo.setSelectedItem(((OptionParameter)parameters[1]).getValue());
        updateGraphicalRep();
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