package quoggles.stdboxes.isinstanceof;

import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: one object<p>
 * Output: a boolean indicating whether or not the input object is an
 * instance of the class or interface specified by the parameter<p>
 */
public class IsInstanceof_Box extends Box {

    /**
     * Constructs the box.
     */
    public IsInstanceof_Box() {
        parameters = new Parameter[]{
            new OptionParameter(
                new String[]{ new String(IBoxConstants.NODE_CLASSNAME), 
                    new String(IBoxConstants.EDGE_CLASSNAME),
                    new String(IBoxConstants.GRAPHELEMENT_CLASSNAME), 
                    new String(IBoxConstants.ATTRIBUTE_CLASSNAME),
                    new String(IBoxConstants.ATTRIBUTABLE_CLASSNAME),
                    new String(IBoxConstants.STRING_CLASSNAME),
                    new String(IBoxConstants.NUMBER_CLASSNAME),
                    new String(IBoxConstants.COLLECTION_CLASSNAME) }, 
                2, true, "returns", 
                "Specifies what this box returns.") };

    }
    
    /**
     * One object.
     * 
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ ITypeConstants.ONEOBJECT };
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
//
//        input = inputs[0];
//    }

    /**
     * @see quoggles.boxes.IGraphicalBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof IsInstanceof_Rep)) {

            iBoxGRep = new IsInstanceof_Rep(this);
        }
        return iBoxGRep;
    }

    /**
     * Searches in the input for graph elements (according to the value of the
     * parameter). The search is not "recursive" in that collections within 
     * collections are not searched.
     * 
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
//        if (inputs[0] == null) {
//            outputs = new Object[]{ null };
//            return;
//        }
        
        String val = ((OptionParameter)parameters[0]).getValue().toString();
        
        try {
            outputs = new Boolean[]{ new Boolean(
                Class.forName(val).isAssignableFrom(inputs[0].getClass())) };
        } catch (ClassNotFoundException cnfe) {
            throw new QueryExecutionException(getId() +
                ": canot find the specified class. Did you specify the fully" +
                " quantified class name? Is it in the classpath?");
        }
    }
}