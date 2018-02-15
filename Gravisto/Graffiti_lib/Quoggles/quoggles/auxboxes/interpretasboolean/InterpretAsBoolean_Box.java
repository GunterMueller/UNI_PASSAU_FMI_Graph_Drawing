package quoggles.auxboxes.interpretasboolean;

import quoggles.auxiliary.Util;
import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: an arbitrary object<p>
 * Output: a <code>Boolean</code> that results from intepreting the input as a
 * boolean value
 */
public class InterpretAsBoolean_Box extends Box {

    private Object input = null;
    

    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();

        outputs = new Boolean[]{ 
            new Boolean(Util.interpretAsBoolean(input)) };
    }
    
    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ ITypeConstants.ONEOBJECT };
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[]{ ITypeConstants.BOOLEAN };
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        input = inputs[0];
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null) {
            iBoxGRep = new DefaultBoxRepresentation(this);
        }
        return iBoxGRep;
    }
}