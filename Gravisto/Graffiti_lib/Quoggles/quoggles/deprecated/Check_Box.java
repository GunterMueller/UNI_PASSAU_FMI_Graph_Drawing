/*
 * 
 */
package quoggles.deprecated;

import java.util.Comparator;

import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

import quoggles.auxiliary.Comparators;
import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 *
 */
public class Check_Box extends Box {

    private Object[] types = new String[]
        { ITypeConstants.INTEGER_STR, ITypeConstants.FLOATING_STR };
    
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
            
    private Number number;
    
    
    /**
     *
     */
    public Check_Box() {
        parameters = new Parameter[]{ attrType, relation, attrValue };
    }
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
            
        super.execute();
        
        Number paramNumber = null;
        Object val = ((OptionParameter)parameters[0]).getValue();
        String attrString = ((StringParameter)parameters[2]).getString();
        Comparator compy = null;
        try {        
            if (val.equals(ITypeConstants.INTEGER_STR)) {
                compy = Comparators.getIntegerComparator();
                paramNumber = new Integer(attrString);
            } else if (val.equals(ITypeConstants.FLOATING_STR)) {
                compy = Comparators.getFloatingComparator();
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
        switch (((OptionParameter)parameters[1]).getOptionNr()) {
            case 0 : // EQUAL
                holds = compy.compare(number, paramNumber) == 0; break;
            case 1 : // NOTEQUAL
                holds = compy.compare(number, paramNumber) != 0; break;
            case 2 : // LT
                holds = compy.compare(number, paramNumber) < 0; break;
            case 3 : // GT
                holds = compy.compare(number, paramNumber) > 0; break;
            case 4 : // LTE
                holds = compy.compare(number, paramNumber) <= 0; break;
            case 5 : // GTE
                holds = compy.compare(number, paramNumber) >= 0; break;

            default :
                throw new RuntimeException(
                    "Wrong/unknown parameter value in " + getId() + ".");
        }
        
        outputs = new Boolean[]{ new Boolean(holds) };
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[] { ITypeConstants.NUMBER };
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
        
        try {
            number = (Number)inputs[0];
        } catch (ClassCastException cce){
            try {
                number = new Integer(inputs[0].toString());
            } catch (NumberFormatException nfe) {
                try {
                    number = new Double(inputs[0].toString());
                } catch (NumberFormatException nfe2) {
                    throw new InvalidInputException(
                        getId() + " needs a number (Integer / Double / ...)" +
                            " as input");
                }
            }
        }
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof Check_Rep)) {

            iBoxGRep = new Check_Rep(this);
        }

        return iBoxGRep;
    }

}
