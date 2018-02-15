package quoggles.changeboxes.converttoparam;

import org.graffiti.plugin.parameter.Parameter;

import quoggles.auxiliary.Util;
import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: 
 * Output: 
 */
public class ConvertToParam_Box extends Box {
    
    /**
     * Constructs the box.
     */
    public ConvertToParam_Box() {
        super();

        OptionParameter parParam = new OptionParameter(
            IBoxConstants.PARTYPES, 1, false,
            "Parameter", 
            "The parameter type to which the input is convert to.");
        parameters = new Parameter[]{ parParam };
    }


    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();

        String parType = 
            ((OptionParameter)parameters[0]).getValue().toString();
        try {
            outputs = new Parameter[]{
                Util.convertToParameter(inputs[0], parType) };
        } catch (Exception e) {
            throw new QueryExecutionException(getId() + ": " + e);
        }
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof ConvertToParam_Rep)) {

            iBoxGRep = new ConvertToParam_Rep(this);
        }
        return iBoxGRep;
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[]{ ITypeConstants.PARAMETER };
    }
}
