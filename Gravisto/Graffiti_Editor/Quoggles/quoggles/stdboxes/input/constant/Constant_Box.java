package quoggles.stdboxes.input.constant;

import java.util.ArrayList;
import java.util.List;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.SpinnerEditComponent;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.ObjectParameter;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.constants.QConstants;
import quoggles.exceptions.BoxNotExecutedException;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Output: A constant value of specified type.
 */
public class Constant_Box extends Box {

    private ValueEditComponent vec;

    private boolean executed = false;
    
    private int nrInputs = 0;
    
    
    public Constant_Box() {
        vec = new SpinnerEditComponent(new IntegerParameter(0, "", ""));
        
        parameters = new Parameter[]{ 
            new OptionParameter(
                IBoxConstants.TYPES, 0, false,
                "Type", "Type of constant to create"),
            new ObjectParameter(vec, "VEC", "ValueEditComponent to change" +
                " constant's value")};
    }
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        String parName = 
            ((OptionParameter)parameters[0]).getValue().toString();
        vec = (ValueEditComponent)((ObjectParameter)parameters[1]).getValue();
        vec.setValue();
        
        Object obj = null;
        
        try {
            Displayable disp = vec.getDisplayable();
            if (ITypeConstants.INTEGER_STR.equals(parName)) {
                obj = (Integer)disp.getValue();
            
            } else if (ITypeConstants.DOUBLE_STR.equals(parName)) {
                obj = (Double)disp.getValue();

            } else if (ITypeConstants.STRING_STR.equals(parName)) {
                obj = (String)disp.getValue();

            } else if (ITypeConstants.BOOLEAN_STR.equals(parName)) {
                obj = (Boolean)disp.getValue();

            } else if (IBoxConstants.EMPTY.equals(parName)) {
                obj = QConstants.EMPTY;

            } else if (ITypeConstants.COLLECTION_STR.equals(parName)) {
                int len = ((Integer)inputs[0]).intValue();
                List lst = new ArrayList(len);
                for (int i = 0; i < len; i++) {
                    lst.add(QConstants.EMPTY);
                }
                obj = lst;
            }
        } catch (Exception e) {
            throw new QueryExecutionException(getId() + ": " + e);
        }
        
        outputs = new Object[]{ obj };
        executed = true;
    }
    
    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        
        if (ITypeConstants.COLLECTION_STR.equals(
            ((OptionParameter)parameters[0]).getValue().toString())) {
            try {
                ((Integer)inputs[0]).intValue();
            } catch (ClassCastException cce) {
                throw new InvalidInputException(getId() + 
                    ": Input must be an integer.");
            }
        }
    }

    /**
     * @see quoggles.boxes.IBox#getOutputs()
     */
    public Object[] getOutputs() throws BoxNotExecutedException {
        if (!executed) {
            throw new BoxNotExecutedException(getId() + 
                ": Box not executed.");
        }
        return outputs;
    }

    /**
     * Returns 0.
     * 
     * @see quoggles.boxes.IBox#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        return nrInputs;
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        if (nrInputs == 1) {
            return new int[]{ ITypeConstants.NUMBER };
        } else {
            return new int[0];
        }
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[]{ ITypeConstants.ONEOBJECT };
    }

    /**
     * @see quoggles.boxes.IBox#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] pars, boolean fromRep) {
        super.setParameters(pars, fromRep);
        
        String parName = 
            ((OptionParameter)parameters[0]).getValue().toString();

        reset();

        if (ITypeConstants.COLLECTION_STR.equals(parName)) {
            nrInputs = 1;
        }   else {
            nrInputs = 0;
        }
        
        getGraphicalRepresentation().updateGraphicalRep();
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || !(iBoxGRep instanceof Constant_Rep)) {
            iBoxGRep = new Constant_Rep(this);
        }
        return iBoxGRep;
    }

    /**
     * @see quoggles.boxes.IBox#reset()
     */
    public void reset() {
        super.reset();
        if (ITypeConstants.COLLECTION_STR.equals(
            ((OptionParameter)parameters[0]).getValue().toString())) {
            nrInputs = 1;
        } else {
            nrInputs = 0;
        }
        executed = false;
    }

    /**
     * @see quoggles.boxes.IBox#reset(int)
     */
    public void reset(int index) {
        super.reset(index);
        if (ITypeConstants.COLLECTION_STR.equals(
            ((OptionParameter)parameters[0]).getValue().toString())) {
            nrInputs = 1;
        } else {
            nrInputs = 0;
        }
        executed = false;
    }
}