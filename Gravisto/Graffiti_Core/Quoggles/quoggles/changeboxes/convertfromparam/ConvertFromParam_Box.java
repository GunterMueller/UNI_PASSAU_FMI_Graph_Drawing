package quoggles.changeboxes.convertfromparam;

import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;

import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: 
 * Output: 
 */
public class ConvertFromParam_Box extends Box {
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();

        Parameter param = (Parameter)inputs[0];
        if (param instanceof SelectionParameter) {
            outputs = new Object[]{ 
                ((SelectionParameter)param).getSelection().getElements() };
        } else {
            outputs = new Object[]{ ((Parameter)inputs[0]).getValue() };
        }
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof DefaultBoxRepresentation)) {

            iBoxGRep = new DefaultBoxRepresentation(this);
        }
        return iBoxGRep;
    }
    
    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        
        if (!(inputs[0] instanceof Parameter)) {
            throw new InvalidInputException(getId() + ": needs a parameter" +
                " as input");
        }
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ ITypeConstants.PARAMETER };
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[]{ ITypeConstants.GENERAL };
    }
}
