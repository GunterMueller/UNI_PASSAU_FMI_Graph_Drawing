package quoggles.stdboxes.booleannot;

import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: a <code>Boolean</code> value<p>
 * Output: a <code>Boolean</code> having the negated value. No implicit 
 * conversion. <code>null</code> values are passed on.
 */
public class BooleanNot_Box extends Box {

    private Boolean boolValue;
    

    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();

//        if (boolValue == null) {
//            outputs = new Object[]{ null };
//        } else {
            outputs = new Boolean[]{ new Boolean(!boolValue.booleanValue()) };
//        }
    }
    
    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ ITypeConstants.BOOLEAN };
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        if (inputs[0] == null) {
            throw new InvalidInputException(
                getId() + " needs a Boolean value as input. Use a" +
                " InterpretAsBoolean_Box to convert \"null\"" + 
                " to a Boolean value.");
//            boolValue = null;
//            return;
        }
        try {
            boolValue = (Boolean)inputs[0];
        } catch (ClassCastException cce) {
            throw new InvalidInputException(
                getId() + " needs a Boolean value as input. Use a" +
                " InterpretAsBoolean_Box to convert " + 
                inputs[0].getClass().getName() +
                " to a Boolean value.");
        }
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