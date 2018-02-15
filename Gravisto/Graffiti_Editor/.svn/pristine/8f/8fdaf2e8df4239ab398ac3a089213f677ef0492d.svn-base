package quoggles.auxboxes.truemaker;

import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: some objects of arbitrary types<p>
 * Output: the boolean value <code>true</code>.
 */
public class MakeTrue_Box extends Box {

    private int ioNumber = 1;
    

    public MakeTrue_Box() {
        parameters = new Parameter[]{ new IntegerParameter(1,
            "inNumber", "Number of inputs") };
    }
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();

        if (inputs == null) {
            outputs = new Object[]{ null };
            return;
        }
        
        outputs = new Boolean[]{ new Boolean(true) };
    }
    
    /**
     * @see quoggles.boxes.IBox#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        return ioNumber;
    }

    /**
     * @see quoggles.boxes.IBox#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] pars, boolean fromRep) {
        ioNumber = ((IntegerParameter)pars[0]).getInteger().intValue();
        super.setParameters(pars, fromRep);

        // update everything that depends on the number of inputs
        reset();
    }

    /**
     * All inputs can be of general type.
     * 
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        int[] ios = new int[ioNumber];
        for (int i = 0; i < ioNumber; i++) {
            ios[i] = ITypeConstants.GENERAL;
        }
        return ios;
    }

    /**
     * One boolean value.
     * 
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[]{ ITypeConstants.BOOLEAN };
    }

//    /**
//     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
//     */
//    public void setInputs(Object[] inputs) throws InvalidInputException {
//        super.setInputs(inputs);
//    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || !(iBoxGRep instanceof MakeTrue_Rep)) {
            iBoxGRep = new MakeTrue_Rep(this);
        }
        return iBoxGRep;
    }
}