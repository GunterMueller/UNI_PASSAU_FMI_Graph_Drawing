package quoggles.stdboxes.comparetwovalues;

import java.security.InvalidParameterException;
import java.util.Comparator;

import org.graffiti.plugin.parameter.Parameter;

import quoggles.auxiliary.Comparators;
import quoggles.auxiliary.Util;
import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: two java objects<p>
 * Output: <code>true</code> iff the objects are considered to be equal (this
 * depends on the values of the parameters). Inputs are converted to the type
 * specified by parameter. <code>EMPTY</code> inputs can only be evaluated
 * if comparison is string based. An <code>EMPTY</code> value is then 
 * represented via the empty string <code>""</code>.<p>
 */
public class CompareTwoValues_Box extends Box {

    private Object[] types = new String[]
        { ITypeConstants.STRING_STR, ITypeConstants.INTEGER_STR, 
          ITypeConstants.FLOATING_STR, ITypeConstants.BOOLEAN_STR,
          ITypeConstants.EQUALS_STR, ITypeConstants.SAME_STR };
    
    private OptionParameter compTypeParam = 
        new OptionParameter(types, 0, 
            IBoxConstants.COMP_TYPE, 
            "The type of the comparison");
        
    private Object[] relSymbols = new String[]
        { IBoxConstants.EQUAL_CMP, IBoxConstants.NOTEQUAL_CMP, 
            IBoxConstants.LT_CMP, IBoxConstants.GT_CMP, 
            IBoxConstants.LTE_CMP, IBoxConstants.GTE_CMP };

    private OptionParameter relationParam = 
        new OptionParameter(relSymbols, 
            IBoxConstants.REL_TYPE, "The type of the relation");
        
    private Object value1;
    
    private Object value2;

    
    /**
     * Constructs the box.
     */
    public CompareTwoValues_Box() {
        parameters = new Parameter[]{ compTypeParam, relationParam };
    }
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        Object compType = ((OptionParameter)parameters[0]).getValue();
        
        Comparator compy = null;

//        if (value1 == null && value2 == null) {
//            if (compType.equals(ITypeConstants.STRING_STR)) {
//                outputs = new Boolean[]{ new Boolean(true) };
//            } else {
//                //outputs = new Boolean[]{ new Boolean(false) };
//                // null values can only compared as strings
//                throw new InvalidInputException("This type of " +
//                    "comparison not allowed for this type.");
//            }
//            return;
//        }
        
        if (value1 == null || value2 == null) {
            if (compType.equals(ITypeConstants.STRING_STR)) {
                //compy = Comparators.getStringComparator();
                value1 = value1 == null ? "null" : value1.toString();
                value2 = value2 == null ? "null" : value2.toString();
            } else {
                //outputs = new Boolean[]{ new Boolean(false) };
                // null values can only compared as strings
                throw new InvalidInputException("null input values" +
                    " can only compared as strings (" + 
                    (value1 == null ? "first input is null; " : "") +
                    (value2 == null ? "second input is null" : "") + ").");
            }
        }
        
        try {
            if (compType.equals(ITypeConstants.SAME_STR)) {
                outputs = new Boolean[1];
                switch (((OptionParameter)parameters[1]).getOptionNr()) {
                    case 0 : // EQUAL
                        outputs[0] = new Boolean(value1 == value2);
                        return;
                    case 1 : // NOTEQUAL
                        outputs[0] = new Boolean(value1 != value2);
                        return;
                    default :
                        throw new InvalidParameterException("This type of " +
                            "comparison not allowed for this type.");
                }
                
            } else if (compType.equals(ITypeConstants.EQUALS_STR)) {
                outputs = new Boolean[1];
                switch (((OptionParameter)parameters[1]).getOptionNr()) {
                    case 0 : // EQUAL
                        outputs[0] = new Boolean(value1.equals(value2));
                        return;
                    case 1 : // NOTEQUAL
                        outputs[0] = new Boolean(!value1.equals(value2));
                        return;
                    default :
                        throw new InvalidParameterException("This type of " +
                            "comparison not allowed for this type.");
                }
                
            } else if (compType.equals(ITypeConstants.INTEGER_STR)) {
                compy = Comparators.getIntegerComparator();
                value1 = new Long(value1.toString());
                value2 = new Long(value2.toString());
            
            } else if (compType.equals(ITypeConstants.FLOATING_STR)) {
                compy = Comparators.getFloatingComparator();
                value1 = new Double(value1.toString());
                value2 = new Double(value2.toString());
            
            } else if (compType.equals(ITypeConstants.STRING_STR)) {
                compy = Comparators.getStringComparator();
                value1 = value1.toString();
                value2 = value2.toString();
                    
            } else if (compType.equals(ITypeConstants.BOOLEAN_STR)) {
                boolean boolValue1 = Util.interpretAsBoolean(value1);
                boolean boolValue2 = Util.interpretAsBoolean(value2);
                outputs = new Boolean[1];
                switch (((OptionParameter)parameters[1]).getOptionNr()) {
                    case 0 : // EQUAL
                        outputs[0] = new Boolean(boolValue1 == boolValue2);
                        return;
                    case 1 : // NOTEQUAL
                        outputs[0] = new Boolean(boolValue1 != boolValue2);
                        return;
                    default :
                        throw new InvalidParameterException(getId() + 
                            "This type of comparison not allowed for " +
                            "this type.");
                }
    
            } else {
                throw new RuntimeException(
                    "Wrong/unknown parameter value in " + getId() + ".");
            }
        } catch (NumberFormatException nfe) {
            throw new QueryExecutionException(getId() +
                ": Wrong data format: " + nfe.getLocalizedMessage());
        }        
        boolean holds = false;
        switch (((OptionParameter)parameters[1]).getOptionNr()) {
            case 0 : // EQUAL
                holds = compy.compare(value1, value2) == 0; break;
            case 1 : // NOTEQUAL
                holds = compy.compare(value1, value2) != 0; break;
            case 2 : // LT
                holds = compy.compare(value1, value2) < 0; break;
            case 3 : // GT
                holds = compy.compare(value1, value2) > 0; break;
            case 4 : // LTE
                holds = compy.compare(value1, value2) <= 0; break;
            case 5 : // GTE
                holds = compy.compare(value1, value2) >= 0; break;

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
        return new int[] { ITypeConstants.GENERAL, ITypeConstants.GENERAL };
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
        
        value1 = inputs[0];
        value2 = inputs[1];
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof CompareTwoValues_Rep)) {

            iBoxGRep = new CompareTwoValues_Rep(this);
        }
        return iBoxGRep;
    }
}