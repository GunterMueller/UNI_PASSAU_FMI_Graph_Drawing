package quoggles.stdboxes.sortby;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.representation.BoxRepresentation;
import quoggles.representation.DefaultBoxRepresentation;

/**
 * 
 */
public class SortBy_Rep
    extends DefaultBoxRepresentation
    implements ActionListener {

    private JComboBox combo;
    
    private JComboBox combo2;
    
    private String[] numbers;
    

    /**
     * Constructs the box.
     * 
     * @param box
     */
    public SortBy_Rep(IBox box) {
        super(box);
    }

    
    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {
        if (numbers == null) {
            numbers = new String[]{ "1", "2", "3" };
        } 
        combo = new JComboBox(numbers);
        combo.setBackground(IBoxConstants.PARAM_BACKGROUND);
        Dimension size = combo.getPreferredSize();
        size.setSize(
            Math.min(size.width, IBoxConstants.DEFAULT_BOX_WIDTH), 
            size.height);
        combo.setSize(size);
        combo.setPreferredSize(size);

        combo.setEditable(true);
        combo.setSelectedItem
            (((IntegerParameter)parameters[0]).getInteger().toString());
        combo.addActionListener(this);

        combo2 = new JComboBox(numbers);
        combo2.setBackground(IBoxConstants.PARAM_BACKGROUND);
        size = combo2.getPreferredSize();
        size.setSize(
            Math.min(size.width, IBoxConstants.DEFAULT_BOX_WIDTH), 
            size.height);
        combo2.setSize(size);
        combo2.setPreferredSize(size);

        combo2.setEditable(true);
        combo2.setSelectedItem(
            ((IntegerParameter)parameters[1]).getInteger().toString());
        combo2.addActionListener(this);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
        panel1.add(new JLabel("#to sort:"));
        panel1.add(combo);
        
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        panel2.add(new JLabel("#sort by:"));
        panel2.add(combo2);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(panel1);
        panel.add(panel2);
        panel.setPreferredSize(new Dimension(
            Math.max(combo2.getPreferredSize().width,
                combo.getPreferredSize().width),
            combo2.getPreferredSize().height + 
                combo.getPreferredSize().height));

        graphicalRep = standardizeBoxRep(graphicalRep, panel);
        graphicalRep.validate();
    }
    
    /**
     * @see quoggles.representation.IBoxRepresentation#
     * setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
        super.setParameters(params, fromBox);
//        combo.setSelectedItem(
//            ((IntegerParameter)parameters[0]).getInteger().toString());
//        combo2.setSelectedItem(
//            ((IntegerParameter)parameters[1]).getInteger().toString());
        updateGraphicalRep();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == combo) {
            ((IntegerParameter)parameters[0]).setValue(new Integer(
                Integer.parseInt(combo.getSelectedItem().toString())));
        } else {
            ((IntegerParameter)parameters[1]).setValue(new Integer(
                Integer.parseInt(combo2.getSelectedItem().toString())));
        }
        setParameters(parameters, false);
        repaintBoxRep(e);
    }

    /**
     * Uses a combo box to let the user change the parameters.
     * 
     * @see quoggles.representation.IBoxRepresentation#getRepresentation()
     */
    public BoxRepresentation getRepresentation() {
        return graphicalRep;
    }

}