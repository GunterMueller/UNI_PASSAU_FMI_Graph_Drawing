package quoggles.auxboxes.booleanop;

import org.graffiti.plugin.parameter.Parameter;

import quoggles.auxiliary.Util;
import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: two <code>Boolean</code> values<p>
 * Output: a <code>Boolean</code> that is the result of applying an operation 
 * on the two input <code>Boolean</code> values (specified via a parameter).
 * Inputs are implicitly converted into booleans using
 * <code>Util.interpretAsBoolen></code>.
 */
public class BooleanOp_Box extends Box {

    private Object[] operSymbols = new String[]
        { IBoxConstants.AND_STR, IBoxConstants.OR_STR, 
            IBoxConstants.XOR_STR, IBoxConstants.NOR_STR };

    private OptionParameter relation = 
        new OptionParameter(operSymbols, 
            IBoxConstants.REL_TYPE, "The type of the relation");
        
    private Boolean bool1;
    
    private Boolean bool2;

    
    /**
     * Constructs the box.
     */
    public BooleanOp_Box() {
        parameters = new Parameter[]{ relation };
    }
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
//        if (bool1 == null || bool2 == null) {
//            outputs = new Object[]{ null };
//            return;
//        }
        
        boolean holds = false;
        boolean bv1 = bool1.booleanValue();
        boolean bv2 = bool2.booleanValue();
        switch (((OptionParameter)parameters[0]).getOptionNr()) {
            case 0 : // AND
                holds = bv1 && bv2; break;
            case 1 : // OR
                holds = bv1 || bv2; break;
            case 2 : // XOR
                holds = bv1 ^ bv2; break;
            case 3 : // NOR
                holds = !(bv1 || bv2); break;

            default :
                throw new RuntimeException(
                    "Wrong/unknown parameter value in " + getId() + ".");
        }
        
        outputs = new Boolean[]{ new Boolean(holds) };
    }

    /**
     * Returns 2.
     * 
     * @see quoggles.boxes.Box#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        return 2;
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[] { ITypeConstants.BOOLEAN, ITypeConstants.BOOLEAN };
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[] { ITypeConstants.BOOLEAN };
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        
        bool1 = new Boolean(Util.interpretAsBoolean(inputs[0]));
        bool2 = new Boolean(Util.interpretAsBoolean(inputs[1]));
        // next bit would be more "typesafe" but experience shows that the
        // implicit converison is appropriate
		//        try {
		//            bool1 = (Boolean)inputs[0];
		//            bool2 = (Boolean)inputs[1];
		//        } catch (ClassCastException cce){
		//            throw new InvalidInputException(
		//                getId() + 
		//                " needs two Boolean values as input");
		//        }
		//        if (bool1 == null || bool2 == null) {
		//            throw new InvalidInputException(
		//                getId() + 
		//                " input may not be null");
		//        }
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof BooleanOp_Rep)) {

            iBoxGRep = new BooleanOp_Rep(this);
        }
        return iBoxGRep;
    }
}
