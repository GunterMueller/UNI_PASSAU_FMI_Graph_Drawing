package quoggles.changeboxes.createparam;

import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.ObjectParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: a collection of objects of arbitrary types<p>
 * Output: a collection containing the input elements,
 * without any duplicates (tested using <code>equals</code>).
 */
public class CreateParam_Box extends Box {

///    private ValueEditComponent vec;

//	/**
//	 * Flag showing if the box has been executed.
//	 * Local variable needed because execute() method of superclass cannot be
//	 * called in local execute() since it would throw an  
//	 */
//	private boolean executed = false;

    
    public CreateParam_Box() {
///        vec = new SpinnerEditComponent(new IntegerParameter(0, "", ""));
        
        parameters = new Parameter[]{ 
            new OptionParameter(
                IBoxConstants.PARTYPES, 0, false,
                "Parameter", "Parameter to create"),
            new StringParameter(null, "Value", "Value of parameter")};
///            new VECParameter(vec, "VEC", "ValueEditComponent to change" +
///                " parameter value")};
    }
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        try {
			setInputs(new Object[]{ });
		} catch (InvalidInputException e) { }

		super.execute();
    	
        String parName = 
            ((OptionParameter)parameters[0]).getValue().toString();
        String parValue = ((StringParameter)parameters[1]).getString();

        Parameter par = null;
        String name = "autoPar";
        String desc = "automatically generated parameter";

        try {
            if (IBoxConstants.INTEGER_PAR.equals(parName)) {
                par = new IntegerParameter(new Integer(parValue), name, desc);
            } else if (IBoxConstants.DOUBLE_PAR.equals(parName)) {
                par = new DoubleParameter
					(new Double(parValue).doubleValue(), name, desc);
            } else if (IBoxConstants.STRING_PAR.equals(parName)) {
                par = new StringParameter(parValue, name, desc);
            } else if (IBoxConstants.BOOLEAN_PAR.equals(parName)) {
                par = new BooleanParameter(new Boolean(parValue), name, desc);
            } else if (IBoxConstants.OBJECT_PAR.equals(parName)) {
                par = new ObjectParameter(parValue, name, desc);
            }
            // TODO implement for other types
        } catch (Exception e) {
            throw new QueryExecutionException(getId() + ": " + e);
        }


/*
        vec = ((VECParameter)parameters[1]).getVEC();
        vec.setValue();
        
        try {
            Displayable disp = vec.getDisplayable();
            if (IBoxConstants.INTEGER_PAR.equals(parName)) {
                par = new IntegerParameter
                    (((Number)disp.getValue()).intValue(),
                     disp.getName(), disp.getDescription());
            
            } else if (IBoxConstants.DOUBLE_PAR.equals(parName)) {
                par = new DoubleParameter(
                    ((Number)vec.getDisplayable().getValue()).doubleValue(), 
                    disp.getName(), disp.getDescription());

            } else if (IBoxConstants.STRING_PAR.equals(parName)) {
                par = new StringParameter(
                    vec.getDisplayable().getValue().toString(), 
                    disp.getName(), disp.getDescription());

            } else if (IBoxConstants.BOOLEAN_PAR.equals(parName)) {
                par = new BooleanParameter((Boolean)vec.getDisplayable().getValue(), 
                    disp.getName(), disp.getDescription());

            } else if (IBoxConstants.NODE_PAR.equals(parName)) {
                par = new NodeParameter(
                    (Node)vec.getDisplayable().getValue(), 
                    disp.getName(), disp.getDescription());

            } else if (IBoxConstants.EDGE_PAR.equals(parName)) {
                par = new EdgeParameter(
                    (Edge)vec.getDisplayable().getValue(), 
                    disp.getName(), disp.getDescription());

            } else if (IBoxConstants.OBJECT_PAR.equals(parName)) {
                par = new ObjectParameter(
                    vec.getDisplayable().getValue(), 
                    disp.getName(), disp.getDescription());

            } else if (IBoxConstants.SELECTION_PAR.equals(parName)) {
                par = new SelectionParameter(
                    (Selection)vec.getDisplayable().getValue(), 
                    disp.getName(), disp.getDescription());

            }
            // TODO implement for other types
        } catch (Exception e) {
            throw new QueryExecutionException(getId() + ": " + e);
        }
*/
        
        outputs = new Parameter[]{ par };

//        executed = true;
    }
    
//    /**
//     * @see quoggles.boxes.IBox#getOutputs()
//     */
//    public Object[] getOutputs() throws BoxNotExecutedException {
//        if (!executed) {
//            throw new BoxNotExecutedException(getId() + 
//                ": Box not executed.");
//        }
//        return outputs;
//    }

    /**
     * Returns 0.
     * 
     * @see quoggles.boxes.IBox#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        return 0;
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[]{ ITypeConstants.PARAMETER };
    }

//    /**
//     * @see quoggles.boxes.IBox#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
//     */
//    public void setParameters(Parameter[] pars, boolean fromRep) {
//        super.setParameters(pars, fromRep);
//
//        reset();
//    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || !(iBoxGRep instanceof CreateParam_Rep)) {
            iBoxGRep = new CreateParam_Rep(this);
        }
        return iBoxGRep;
    }

//    /**
//     * @see quoggles.boxes.IBox#reset()
//     */
//    public void reset() {
//        super.reset();
//        executed = false;
//    }

//    /**
//     * @see quoggles.boxes.IBox#reset(int)
//     */
//    public void reset(int index) {
//        super.reset(index);
//        executed = false;
//    }
}