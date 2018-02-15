package quoggles.auxboxes.valuecompare;

import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

import quoggles.auxiliary.Util;
import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: one object<p>
 * Output: a <code>Boolean</code> value that is the result of comparing the 
 * input with a value specified via the parameters<p>
 * 
 * It does not implicitly check all elements of a collection.
 * 
 * @see quoggles.auxiliary.Util#match(Object, org.graffiti.plugin.parameter.Parameter[])
 */
public class ValueCompare_Box extends Box {

    private Object[] types = new String[]
        { ITypeConstants.STRING_STR, ITypeConstants.INTEGER_STR,
          ITypeConstants.FLOATING_STR, ITypeConstants.BOOLEAN_STR };
    
    private OptionParameter attrType = 
        new OptionParameter(types, 0, 
            IBoxConstants.ATTR_TYPE, 
            "The type for comparison");
        
    private Object[] relSymbols = new String[]
        { IBoxConstants.EQUAL_CMP, IBoxConstants.NOTEQUAL_CMP, 
            IBoxConstants.LT_CMP, IBoxConstants.GT_CMP, 
            IBoxConstants.LTE_CMP, IBoxConstants.GTE_CMP };

    private OptionParameter relation = 
        new OptionParameter(relSymbols, 
            IBoxConstants.REL_TYPE, "The type of the relation");
        
    private StringParameter attrValue = 
        new StringParameter("0",
            IBoxConstants.ATTR_VALUE, "The value of the attribute");
            
    private Object value;
    
    
    /**
     * Constructs the box.
     */
    public ValueCompare_Box() {
        parameters = new Parameter[]{ attrType, relation, attrValue };
    }
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        try {
            outputs[0] = Util.match(value, parameters);
        } catch (Exception ex) {
            throw new QueryExecutionException(getId() + ": " +
                ex.getMessage());
        }
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        
        value = inputs[0];
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[] { ITypeConstants.GENERAL };
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[] { ITypeConstants.BOOLEAN };
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof ValueCompare_Rep)) {

            iBoxGRep = new ValueCompare_Rep(this);
        }
        return iBoxGRep;
    }
}
