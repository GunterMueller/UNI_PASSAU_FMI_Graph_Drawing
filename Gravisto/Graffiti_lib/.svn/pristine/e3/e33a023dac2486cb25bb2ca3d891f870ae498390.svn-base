package quoggles.auxboxes.valuefilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
 * Input: one or several objects<p>
 * Output: only those elements from the input that match the value specified
 * via the parameters<p>
 * If the input is not a list and the input object does not match the criteria,
 * the output is <code>null</code>. 
 */
public class ValueFilter_Box extends Box {

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
            
    private Collection inputCol;
    
    private Object singleInput = null;
    
    
    /**
     * Constructs the box.
     */
    public ValueFilter_Box() {
        parameters = new Parameter[]{ attrType, relation, attrValue };
    }
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        if (singleInput != null) {
            inputCol = new ArrayList(1);
            inputCol.add(singleInput);
        }
        
        if (inputCol == null) {
            outputs = new Object[]{ null };
            return;
        }
        
        List outputCol = new LinkedList();
        for (Iterator it = inputCol.iterator(); it.hasNext();) {
            Object obj = it.next();
            try {
                if (Util.match(obj, parameters).booleanValue()) {
                    outputCol.add(obj);
                }
            } catch (Exception ex) {
                throw new QueryExecutionException(getId() + ": " +
                    ex.getMessage());
            }
        }

        //doSetOutput(outputCol);

        // if input has not been a collection, the output is not either
        if (singleInput != null && outputCol.isEmpty()) {
            outputs = new Object[]{ null };
        } else if (singleInput != null && outputCol.size() == 1) {
            outputs = new Object[]{ singleInput };
        } else {
            outputs = new Collection[]{ outputCol };
        }
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        
        singleInput = null;
        if (inputs[0] instanceof Collection) {
            inputCol = (Collection)inputs[0];
        } else {
            singleInput = inputs[0];
//            inputCol = new ArrayList(0);
//            inputCol.add(inputs[0]);
        }
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ 
            ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION };
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof ValueFilter_Rep)) {

            iBoxGRep = new ValueFilter_Rep(this);
        }
        return iBoxGRep;
    }

}
