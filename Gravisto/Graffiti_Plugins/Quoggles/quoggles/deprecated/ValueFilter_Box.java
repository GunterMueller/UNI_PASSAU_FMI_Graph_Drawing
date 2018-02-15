/*
 * 
 */
package quoggles.deprecated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attributable;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

import quoggles.auxiliary.Comparators;
import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.InvalidParameterException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 *
 */
public class ValueFilter_Box extends Box {

    private Object[] types = new String[]
        { ITypeConstants.STRING_STR, ITypeConstants.INTEGER_STR,
          ITypeConstants.FLOATING_STR, ITypeConstants.BOOLEAN_STR };
    
    private OptionParameter attrType = 
        new OptionParameter(types, 0, 
            IBoxConstants.ATTR_TYPE, 
            "The type of the attribute (for comparison)");
        
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
            
    private List attblsList;

    private List attrValuesList;
    
    
    /**
     *
     */
    public ValueFilter_Box() {
        parameters = new Parameter[]{ attrType, relation, attrValue };
    }
    
    
    private boolean match(Object compObject) 
        throws InvalidInputException, InvalidParameterException {
            
        Object compValue = null;
        Object val = ((OptionParameter)parameters[0]).getValue();
        String attrString = ((StringParameter)parameters[2]).getString();
        Comparator compy = null;
        try {        
            if (val.equals(ITypeConstants.INTEGER_STR)) {
                compy = Comparators.getIntegerComparator();
                compValue = new Long(attrString);
                
            } else if (val.equals(ITypeConstants.FLOATING_STR)) {
                compValue = new Double(attrString);
                compy = Comparators.getFloatingComparator();
                
            } else if (val.equals(ITypeConstants.STRING_STR)) {
                compValue = attrString;
                compy = Comparators.getStringComparator();
                
            } else if (val.equals(ITypeConstants.BOOLEAN_STR)) {
                // TODO convert values like 0, 1 etc correctly
                boolean boolValue = new Boolean(attrString).booleanValue();
                switch (((OptionParameter)parameters[1]).getOptionNr()) {
                    case 0 : // EQUAL
                        return ((Boolean)compObject).booleanValue() == 
                            boolValue;
                    case 1 : // NOTEQUAL
                        return ((Boolean)compObject).booleanValue() != 
                            boolValue;
                    default :
                        throw new InvalidParameterException("This type of " +
                            "comparison not allowed for this type.");
                }

            } else {
                throw new RuntimeException(
                    "Wrong/unknown parameter value in " + getId() + ".");
            }
        } catch (NumberFormatException nfe) {
            throw new InvalidInputException(
                "Type of value and type parameter do not match in " + 
                getId() + ".");
        }        
        
        boolean holds = false;
        try {
            switch (((OptionParameter)parameters[1]).getOptionNr()) {
                case 0 : // EQUAL
                    holds = compy.compare(compObject, compValue) == 0; break;
                case 1 : // NOTEQUAL
                    holds = compy.compare(compObject, compValue) != 0; break;
                case 2 : // LT
                    holds = compy.compare(compObject, compValue) < 0; break;
                case 3 : // GT
                    holds = compy.compare(compObject, compValue) > 0; break;
                case 4 : // LTE
                    holds = compy.compare(compObject, compValue) <= 0; break;
                case 5 : // GTE
                    holds = compy.compare(compObject, compValue) >= 0; break;
    
                default :
                    throw new RuntimeException(
                        "Wrong/unknown parameter value in " + getId() + ".");
            }
        } catch (ClassCastException cce) {
            throw new InvalidInputException(
                "Type of input and type parameter do not match in " + 
                getId() + ".");
        }
        
        return holds;
    }

    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
            
        super.execute();
        
        if (attblsList.size() != attrValuesList.size()) {
            throw new InvalidInputException(getId() +
                " needs as inputs: Either one element each or two Lists of" +
                " equal size.");
        }
        
        List outputList = new LinkedList();
        Iterator attblIt = attblsList.iterator();
        for (Iterator it = attrValuesList.iterator(); it.hasNext();) {
            Object attr = it.next();
            try {
                Attributable attbl = (Attributable)attblIt.next();
                if (match(attr)) {
                    outputList.add(attbl);
                }
            } catch (ClassCastException cce) {
                throw new InvalidInputException(getId() +
                    ": First input must be one or a List of Attributables.");
            }
        }
        
        if (outputList.size() == 1) {
            outputs = new Attributable[]{ (Attributable)outputList.get(0) };
        } else {
            outputs = new List[] { outputList };
        }
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        
        if (inputs[0] instanceof Collection) {
            attblsList = (List)inputs[0];
        } else {
            attblsList = new ArrayList(1);
            attblsList.add(inputs[0]);
        }
        if (inputs[1] instanceof Collection) {
            attrValuesList = (List)inputs[1];
        } else {
            attrValuesList = new ArrayList(1);
            attrValuesList.add(inputs[1]);
        }
    }

    /**
     * @see quoggles.boxes.IBox#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        return 2;
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[] { 
            ITypeConstants.ATTRIBUTABLES + ITypeConstants.ATTRIBUTABLE,
            ITypeConstants.GENERAL };
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[] { 
            ITypeConstants.ATTRIBUTABLES + ITypeConstants.ATTRIBUTABLE };
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
