package quoggles.auxboxes.attributefilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

import quoggles.auxiliary.Util;
import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.deprecated.AttributeFilter_Rep;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: One or several <code>Attributables</code>.<p>
 * Output: Empty list, one or several <code>Attributables</code> that match the
 * attribute specification given by the parameters.<p>
 * If the input is <code>null</code>, the output is <code>null</code> as well.<p>
 * Any values within the input list that are not <code>Attributables</code>
 * (this includes <code>null</code> values) are ignored.<p>
 * If the input is not a list and the input object does not match the criteria,
 * the output is <code>null</code>. 
 */
public class AttributeFilter_Box extends Box {

    private OptionParameter attrPath = Util.buildAttrPath();
        
    private Object[] types = new String[]
        { "integer", "floating", "string", "boolean" };
    
    private OptionParameter attrType = 
        new OptionParameter(types, 2, 
            "attrType", "The type of the attribute (for equality test)");
        
    private Object[] relSymbols = new String[]
        { "=", "!=", "<", ">", "<=", ">=" };

    private OptionParameter relation = 
        new OptionParameter(relSymbols, 
            "relation", "The type of the attribute (for equality test)");
        
    private StringParameter attrValue = 
        new StringParameter("2",
            "attrValue", "The value of the attribute");
            
    private Collection inputCol;
    
    private Attributable singleInput = null;
    
    
    /**
     * Constructs the box.
     */
    public AttributeFilter_Box() {
        parameters = 
            new Parameter[]{ attrPath, attrType, relation, attrValue };
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
            Attributable attbl = null;
            try {
                attbl = (Attributable)it.next();
            } catch (ClassCastException cce) {
                // ignore non-attributables
                continue;
            }
            if (attbl == null) {
                // ignore null values
                continue;
            }
            Object obj = null;
            try {
                obj = attbl.getAttribute(
                    ((OptionParameter)parameters[0]).getValue().toString())
                        .getValue();
            } catch (AttributeNotFoundException anfe) {
                // ignore elements that do not have the specified attribute
                obj = quoggles.constants.QConstants.EMPTY;
            }
            try {
                // "remove" first parameter for function call
                Parameter[] valueParams = new Parameter[3];
                System.arraycopy(parameters, 1, valueParams, 0, 3);
                if (Util.match(obj, valueParams).booleanValue()) {
                    outputCol.add(attbl);
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
            outputs = new Attributable[]{ singleInput };
        } else {
            outputs = new Collection[]{ outputCol };
        }
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[] { ITypeConstants.ATTRIBUTABLE +
            ITypeConstants.ATTRIBUTABLES};
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        
        singleInput = null;
        try {
            inputCol = (Collection)inputs[0];
        } catch (ClassCastException cce) {
            try {
                singleInput = (Attributable)inputs[0];
            } catch (ClassCastException cce2) {
                throw new InvalidInputException(getId() + 
                    " needs a collection or one Attributable as input.");
            }
//            inputCol = new ArrayList(1);
//            inputCol.add(inputs[0]);
        }
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof AttributeFilter_Rep)) {

            iBoxGRep = new AttributeFilter_Rep(this);
        }
        return iBoxGRep;
    }
}
