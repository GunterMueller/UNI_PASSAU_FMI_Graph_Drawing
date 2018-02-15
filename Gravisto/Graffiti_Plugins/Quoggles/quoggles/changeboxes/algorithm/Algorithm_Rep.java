package quoggles.changeboxes.algorithm;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;

import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.parameters.OptionParameter;
import quoggles.representation.DefaultBoxRepresentation;

/**
 * Represents a sub query saved in a file.
 */
public class Algorithm_Rep extends DefaultBoxRepresentation 
    implements ActionListener {

    private JComboBox combo;
    
    
    /**
     * Constructor of the box.
     * 
     * @param representedBox
     */
    public Algorithm_Rep(IBox representedBox) {
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
        //combo.setSelectedItem(((OptionParameter)parameters[0]).getValue());
        updateGraphicalRep();
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        ((OptionParameter)parameters[0]).setValue(combo.getSelectedItem());
        //updateInputOutput();
        setParameters(parameters, false);
        repaintBoxRep(e);
    }

    /**
     * After a change in the parameters, the input / output type may have
     * changed. This method takes care of changing the labels in the box.
     */
    protected void updateInputOutput() {
        Algorithm alg = ((Algorithm_Box)getIBox()).getAlgorithm();
        // indicator if a dummy in- and output is needed
        int needDummy = 1;
        Parameter[] pars = alg.getParameters();
        if (pars == null) {
            pars = new Parameter[0];
        }
        for (int i = 0; i < pars.length; i++) {
            if (pars[i] instanceof SelectionParameter) {
                needDummy = 0;
                break;
            }
        }
        int nrInputs = box.getNumberOfInputs();
        int nrOutputs = box.getNumberOfOutputs();
        String[] descs = null;
        if (nrOutputs > nrInputs) {
            // calculating algorithm
            descs = new String[pars.length + needDummy + 1];
            descs[descs.length - 1] = "result";
        } else {
            // no calculateing algorithm
            descs = new String[pars.length + needDummy];
        }
        if (needDummy > 0) descs[0] = "dummy";
        for (int i = 0; i < pars.length; i++) {
            descs[i + needDummy] = pars[i].getName();
        }
        
        // set input
        if (inputLabel != null) {
            StringBuffer ioList = new StringBuffer();
            if (nrInputs > 1) {
                ioList.append("<html>" + INPUTLABEL);
            } else {
                ioList.append(INPUTLABEL);
            }

            for (int i = 0; i < nrInputs -1; i++) {
                ioList.append(descs[i]);
                ioList.append("//<p style=\"margin-left:15pt\">");
            }
            if (nrInputs >= 1) {
                ioList.append(descs[nrInputs -1]);
            }
            if (nrInputs > 1) {
                ioList.append("</html>");
            }

            inputLabel.setText(ioList.toString());
        }

        // set output
        if (outputLabel != null) {
            StringBuffer ioList = new StringBuffer();
            if (((Algorithm_Box)getIBox()).getAlgorithm() 
                instanceof CalculatingAlgorithm) {

                //calc
                descs[0] = (String)ITypeConstants.intStringMap
                    .get(new Integer(ITypeConstants.GENERAL));
            } else {
                // not calc
                descs[0] = "dummy";
            }
            if (nrOutputs > 1) {
                ioList.append("<html>" + OUTPUTLABEL);
            } else {
                ioList.append(OUTPUTLABEL);
            }
            for (int i = 0; i < nrOutputs -1; i++) {
                ioList.append(descs[i]);
                ioList.append("//<p style=\"margin-left:15pt\">");
            }
            if (nrOutputs >= 1) {
                ioList.append(descs[nrOutputs -1]);
            }
            if (nrOutputs > 1) {
                ioList.append("</html>");
            }

            outputLabel.setText(ioList.toString());
        }
    }

}
