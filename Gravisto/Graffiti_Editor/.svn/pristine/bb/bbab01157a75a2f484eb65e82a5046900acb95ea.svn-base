/*
 * 
 */
package quoggles.stdboxes.comparetwovalues;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.parameters.OptionParameter;
import quoggles.representation.DefaultBoxRepresentation;

/**
 *
 */
public class CompareTwoValues_Rep extends DefaultBoxRepresentation 
    implements ActionListener {

    private JComboBox compType;
    
    private JComboBox relation;
    
    private Point2D input1;
    
    private Point2D input2;
    
    
    /**
     * @param representedBox
     */
    public CompareTwoValues_Rep(IBox representedBox) {
        super(representedBox);
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {

        
        input1 = new Point2D.Double(0d, 0.05d);
        input2 = new Point2D.Double(0d, 0.95d);
        
        JPanel panel = new JPanel();

        compType = new JComboBox(
            ((OptionParameter)parameters[0]).getOptions());
        compType.setBackground(IBoxConstants.PARAM_BACKGROUND);
        compType.setEditable(((OptionParameter)parameters[0]).isEditable());
        compType.setSelectedItem(((OptionParameter)parameters[0]).getValue());
        compType.addActionListener(this);
        
        relation = new JComboBox(
            ((OptionParameter)parameters[1]).getOptions());
        relation.setBackground(IBoxConstants.PARAM_BACKGROUND);
        relation.setEditable(((OptionParameter)parameters[1]).isEditable());
        relation.setSelectedItem(((OptionParameter)parameters[1]).getValue());
        relation.addActionListener(this);
        
        panel.setOpaque(false);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        panel.add(compType);
        panel.add(relation);
//        int tempMax = Math.max(compType.getPreferredSize().height, 
//            relation.getPreferredSize().height);
        panel.setPreferredSize(new Dimension(
            compType.getPreferredSize().width + relation.getPreferredSize().width,
//            IBoxConstants.DEFAULT_CONNECTOR_HEIGHT - tempMax - 24));
            Math.max(compType.getPreferredSize().height, 
                relation.getPreferredSize().height)));
        
        graphicalRep = standardizeBoxRep(graphicalRep, panel);
        graphicalRep.validate();
    }
    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (compType.equals(src)) {
            ((OptionParameter)parameters[0]).setValue(compType.getSelectedItem());
        } else
        if (relation.equals(src)) {
            ((OptionParameter)parameters[1]).setValue(relation.getSelectedItem());
        } else
        
        updateInputOutput();
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#
     * setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
        super.setParameters(params, fromBox);
        compType.setEditable(((OptionParameter)parameters[0]).isEditable());
        compType.setSelectedItem(((OptionParameter)parameters[0]).getValue());
        relation.setEditable(((OptionParameter)parameters[1]).isEditable());
        relation.setSelectedItem(((OptionParameter)parameters[1]).getValue());
    }

    /**
     * @see quoggles.representation.IBoxRepresentation#getRelInputsPos()
     */
    public Point2D[] getRelInputsPos() {
        return new Point2D[]{ input1, input2 };
    }

}
