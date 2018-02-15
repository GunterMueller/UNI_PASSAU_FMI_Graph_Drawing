package quoggles.changeboxes.createparam;

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
import quoggles.representation.BoxRepresentation;
import quoggles.representation.DefaultBoxRepresentation;

/**
 * 
 */
public class CreateParam_Rep
    extends DefaultBoxRepresentation
    implements ActionListener, FocusListener {

    private JComboBox combo;
    private JTextField pvField;
    	

    public CreateParam_Rep(IBox box) {
        super(box);
    }

    
    /**
     * @see quoggles.representation.IBoxRepresentation#updateGraphicalRep()
     */
    public void updateGraphicalRep() {
        combo = new JComboBox(((OptionParameter)parameters[0]).getOptions());
        combo.setBackground(IBoxConstants.PARAM_BACKGROUND);
        //combo.setRenderer(new StringConstantsRenderer());
        Dimension size = combo.getPreferredSize();
        combo.setSize(size);

        combo.setEditable(true);
        combo.setSelectedItem(((OptionParameter)parameters[0]).getValue());
        combo.addActionListener(this);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.add(combo);
        
// reuse this next part if VECs can be used without tricks
// (esp. the JSpinner in the SpinnerEditComponent)
/*
        ValueEditComponent vec = ((VECParameter)parameters[1]).getVEC();
        JComponent vecComp = null;
        try {
            vecComp = vec.getComponent();
        } catch (Exception ex) {
            vecComp = new JLabel("not editable");
        }
        panel.add(vecComp);
        panel.setPreferredSize(new Dimension(
            Math.max(vecComp.getPreferredSize().width,
                combo.getPreferredSize().width),
            vecComp.getPreferredSize().height + 
                combo.getPreferredSize().height));
*/        

        pvField = new JTextField(((StringParameter)parameters[1]).getString());
        pvField.addActionListener(this);
        pvField.addFocusListener(this);
        
        panel.add(pvField);
        panel.setPreferredSize(new Dimension(
        		Math.max(pvField.getPreferredSize().width,
                    combo.getPreferredSize().width),
                pvField.getPreferredSize().height + 
                    combo.getPreferredSize().height));
        
        
        graphicalRep = standardizeBoxRep(graphicalRep, panel);
        graphicalRep.validate();
    }
    
//    /**
//     * @see quoggles.representation.IBoxRepresentation#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
//     */
//    public void setParameters(Parameter[] params, boolean fromBox) {
//        if (params[1] == null) {
//            // happens after loading since SpinnerEditComponents et al cannot
//            // be saved right now ...
//            params[1] = new VECParameter
//                (null, "VEC", "ValueEditComponent to change parameter value");
//
//        }
//        ((VECParameter)params[1]).setValue
//            (createVEC(((OptionParameter)params[0]).getValue().toString()));
//        super.setParameters(params, fromBox);
////        combo.setSelectedItem(
////            ((IntegerParameter)parameters[0]).getInteger().toString());
//        updateGraphicalRep();
//    }

    /**
     * @see quoggles.representation.IBoxRepresentation#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
    	super.setParameters(params, fromBox);
    	
    	updateGraphicalRep();
    }

    
    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(combo)) {
            ((OptionParameter)parameters[0]).setValue(combo.getSelectedItem());
        } 
// no ActionListener registered on VEC ...
//        else {
//            ValueEditComponent vec = (ValueEditComponent)
//                ((ObjectParameter)parameters[1]).getValue();
//            vec.setValue();
//        }
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
    
//    /**
//     * According to given parameter class name, create a
//     * <code>ValueEditComponent</code>.
//     * 
//     * @param parClassName
//     * 
//     * @return a <code>ValueEditComponent</code>
//     */
//    private ValueEditComponent createVEC(String parClassName) {
//        ValueEditComponent vec = null;
//
//        // TODO implement more generally
//        if (IBoxConstants.INTEGER_PAR.equals(parClassName)) {
//            vec = new IntegerEditComponent
//                (new IntegerParameter(0, "", ""));
//        } else if (IBoxConstants.DOUBLE_PAR.equals(parClassName)) {
//            vec = new DoubleEditComponent
//                (new DoubleParameter(0d, "", ""));
//        } else if (IBoxConstants.STRING_PAR.equals(parClassName)) {
//            vec = new StringEditComponent
//                (new StringParameter("label.label", "", ""));
//        } else if (IBoxConstants.NODE_PAR.equals(parClassName)) {
//            vec = new NodeEditComponent
//                (new NodeParameter(null, "", ""));
//        } else if (IBoxConstants.EDGE_PAR.equals(parClassName)) {
//            vec = new EdgeEditComponent
//                (new EdgeParameter(null, "", ""));
//        } else if (IBoxConstants.BOOLEAN_PAR.equals(parClassName)) {
//            vec = new BooleanEditComponent
//                (new BooleanParameter(true, "", ""));
//        } else {
//            vec = new StandardValueEditComponent
//                (new StringParameter("", "", ""));
//        }
//        
//        return vec;
//    }

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
        if (pvField.equals(src)) {
            ((StringParameter)parameters[1]).setValue(pvField.getText());
        }
        
        updateInputOutput();
    }



}