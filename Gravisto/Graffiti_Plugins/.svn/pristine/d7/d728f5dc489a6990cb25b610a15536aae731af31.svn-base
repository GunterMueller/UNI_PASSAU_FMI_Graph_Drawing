package quoggles.stdboxes.input.constant;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.graffiti.plugin.editcomponent.StandardValueEditComponent;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.ObjectParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugins.editcomponents.defaults.BooleanEditComponent;
import org.graffiti.plugins.editcomponents.defaults.DoubleEditComponent;
import org.graffiti.plugins.editcomponents.defaults.IntegerEditComponent;
import org.graffiti.plugins.editcomponents.defaults.StringEditComponent;

import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.parameters.OptionParameter;
import quoggles.representation.BoxRepresentation;
import quoggles.representation.DefaultBoxRepresentation;

/**
 * 
 */
public class Constant_Rep
    extends DefaultBoxRepresentation
    implements ActionListener {

    private JComboBox combo;
    

    public Constant_Rep(IBox box) {
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
        
        ValueEditComponent vec = (ValueEditComponent)
            ((ObjectParameter)parameters[1]).getValue();
//        ValueEditComponent vec = 
//            createVEC(((OptionParameter)parameters[0]).getValue().toString());
        //((ObjectParameter)parameters[1]).setValue(vec);
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
        
        graphicalRep = standardizeBoxRep(graphicalRep, panel);
        graphicalRep.validate();
    }
    
    /**
     * @see quoggles.representation.IBoxRepresentation#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] params, boolean fromBox) {
        if (params[1] == null) {
            // happens after loading since SpinnerEditComponents et al cannot
            // be saved right now ...
            params[1] = new ObjectParameter
                (null, "VEC", "ValueEditComponent to change parameter value");

        }
        ((ObjectParameter)params[1]).setValue
            (createVEC(((OptionParameter)params[0]).getValue().toString()));
        super.setParameters(params, fromBox);
//        combo.setSelectedItem(
//            ((IntegerParameter)parameters[0]).getInteger().toString());
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
        updateGraphicalRep();
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
    
    /**
     * According to give parameter class name, create a
     * <code>ValueEditComponent</code>.
     * 
     * @param parClassName
     * 
     * @return a <code>ValueEditComponent</code>
     */
    private ValueEditComponent createVEC(String parClassName) {
        ValueEditComponent vec = null;

        // TODO use EditComponentManager's mapping
        
        if (ITypeConstants.INTEGER_STR.equals(parClassName)) {
            vec = new IntegerEditComponent
                (new IntegerParameter(0, "", ""));
        } else if (ITypeConstants.DOUBLE_STR.equals(parClassName)) {
            vec = new DoubleEditComponent
                (new DoubleParameter(0d, "", ""));
        } else if (ITypeConstants.STRING_STR.equals(parClassName)) {
            vec = new StringEditComponent
                (new StringParameter("", "", ""));
        } else if (ITypeConstants.BOOLEAN_STR.equals(parClassName)) {
            vec = new BooleanEditComponent
                (new BooleanParameter(false, "", ""));
        } else {
            vec = new StandardValueEditComponent
                (new StringParameter("", "", ""));
        }
        
        return vec;
    }

}