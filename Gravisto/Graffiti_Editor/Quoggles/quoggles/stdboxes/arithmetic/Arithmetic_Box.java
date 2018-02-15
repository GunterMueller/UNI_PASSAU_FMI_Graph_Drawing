package quoggles.stdboxes.arithmetic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.constants.QConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.InvalidParameterException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: one or several <code>Number</code>s<p>
 * Output: the result of the function (specified by the parameter)
 * <code>null</code> if the input itself is <code>null</code><p>
 */
public class Arithmetic_Box extends Box {

    /** The input collection */
    private Collection inputCol;
    
    /** Only set if input is not a collection */
    private Number singleInput = null;
    
    private Number num1;
    
    private Number num2;
    

    /**
     * Constructs the box.
     */
    public Arithmetic_Box() {
        parameters = new Parameter[]{ new OptionParameter(
            IBoxConstants.FUNS, 0, false, "function",
            "Function used by the box") };
    }
    
    
    /**
     * Returns true iff the box's parameter is set so that an aggregate 
     * function is calculated.
     * 
     * @return true iff the box's parameter is set so that an aggreate 
     * function is calculated.
     */
    private boolean isAggregate() {
        String pVal = 
            ((OptionParameter)parameters[0]).getValue().toString();
        return !(pVal.equals(IBoxConstants.FUN_PLUS) ||
            pVal.equals(IBoxConstants.FUN_MINUS) ||
            pVal.equals(IBoxConstants.FUN_TIMES) ||
            pVal.equals(IBoxConstants.FUN_DIVIDE));
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        if (isAggregate()) {
            if (IBoxConstants.FUN_COUNT.equals(
                ((OptionParameter)parameters[0]).getValue().toString())) {

                return new int[]{ ITypeConstants.COLLECTION };
            } else {
                return new int[]{ ITypeConstants.COLOF_NUMBER };
            }
        } else {
            return new int[]{ ITypeConstants.NUMBER, ITypeConstants.NUMBER };
        }
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[]{ ITypeConstants.NUMBER };
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);

        String parVal = ((OptionParameter)parameters[0]).getValue().toString();

        singleInput = null;
        
        if (!isAggregate()) {
            try {
                num1 = (Number)inputs[0];
            } catch (ClassCastException cce) {
                try {
                    num1 = new Integer(Integer.parseInt(inputs[0].toString()));
                } catch (NumberFormatException nfe) {
                    try {
                        num1 = new Double
                            (Double.parseDouble(inputs[0].toString()));
                    } catch (NumberFormatException nfe2) {
                        throw new InvalidInputException(
                            getId() + ": first input must be of type Number or" +
                            " least convertible to one (have " +
                            inputs[0].getClass().getName() + ")");
                    } 
                }
            }
            try {
                num2 = (Number)inputs[1];
            } catch (ClassCastException cce) {
                try {
                    num2 = new Integer(Integer.parseInt(inputs[1].toString()));
                } catch (NumberFormatException nfe) {
                    try {
                        num2 = new Double
                            (Double.parseDouble(inputs[1].toString()));
                    } catch (NumberFormatException nfe2) {
                        throw new InvalidInputException(
                            getId() + ": second input must be of type Number or" +
                            " least convertible to one (have " +
                            inputs[1].getClass().getName() + ")");
                    } 
                }
            }
            
            return;
        }
        
        // if (isAggregate()) :
        
        try {
            if (IBoxConstants.FUN_COUNT.equals(parVal)) {
                inputCol = (Collection)inputs[0];
            } else {
                inputCol = new ArrayList(((Collection)inputs[0]).size());

                Iterator it = ((Collection)inputs[0]).iterator();
                while (it.hasNext()) {
                    Object elem = it.next();
                    if (!(elem instanceof Number)) {
                        try {
                            elem = new Integer(Integer.parseInt(elem.toString()));
                        } catch (NumberFormatException nfe) {
                            try {
                                elem = new Double(Double.parseDouble(elem.toString()));
                            } catch (NumberFormatException nfe2) {
                                throw new InvalidInputException(
                                    getId() + ": all elements of input" +
                                        " collection need to be Numbers or at" +
                                        " least convertable to a Number; not of" +
                                        " type " + elem.getClass().getName());
                            }
                            
                        }
                    }
                    if (elem != quoggles.constants.QConstants.EMPTY) {
                        inputCol.add(elem);
                    }
                }
            }
        } catch (ClassCastException cce) {
            inputCol = null;
            Object elem = inputs[0];
            if (IBoxConstants.FUN_COUNT.equals(parVal)) {
                inputCol = new ArrayList(1);
                inputCol.add(elem);
            } else {
                if (elem == quoggles.constants.QConstants.EMPTY) {
                    inputCol = new ArrayList(0);
                } else if (!(elem instanceof Number)) {
                    try {
                        singleInput = 
                            new Integer(Integer.parseInt(elem.toString()));
                    } catch (NumberFormatException nfe) {
                        try {
                            singleInput = 
                                new Double(Double.parseDouble(elem.toString()));
                        } catch (NumberFormatException nfe2) {
                            throw new InvalidInputException(
                                getId() + " needs one or a collection of" +
                                " Numbers as input, not " +
                                elem.getClass().getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * @see quoggles.boxes.IGraphicalBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || !(iBoxGRep instanceof Arithmetic_Rep)) {
            iBoxGRep = new Arithmetic_Rep(this);
        }
        return iBoxGRep;
    }

    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        String parVal = ((OptionParameter)parameters[0]).getValue().toString();
        Number output = null;
        
        if (!isAggregate()) {
            if (parVal.equals(IBoxConstants.FUN_PLUS)) {
                if (num1 instanceof Integer && num2 instanceof Integer) {
                    output = new Integer(num1.intValue() + num2.intValue());
                } else {
                    output = 
                        new Double(num1.doubleValue() + num2.doubleValue());
                }
            } else if (parVal.equals(IBoxConstants.FUN_MINUS)) {
                if (num1 instanceof Integer && num2 instanceof Integer) {
                    output = new Integer(num1.intValue() - num2.intValue());
                } else {
                    output = 
                        new Double(num1.doubleValue() - num2.doubleValue());
                }
            } else if (parVal.equals(IBoxConstants.FUN_TIMES)) {
                if (num1 instanceof Integer && num2 instanceof Integer) {
                    output = new Integer(num1.intValue() * num2.intValue());
                } else {
                    output = 
                        new Double(num1.doubleValue() * num2.doubleValue());
                }
            } else if (parVal.equals(IBoxConstants.FUN_DIVIDE)) {
                if (num1 instanceof Integer && num2 instanceof Integer) {
                    output = new Integer(num1.intValue() / num2.intValue());
                } else {
                    output = 
                        new Double(num1.doubleValue() / num2.doubleValue());
                }
            } else {
                throw new InvalidParameterException(getId() + 
                    ": invalid value of the parameter: " + parVal);
            }

            outputs = new Number[]{ output };

            return;
        }
        
        // if (isAggregate()) :
        
        if (singleInput != null) {
            outputs = new Number[]{ singleInput };
            return;
        }
        
//        if (inputCol == null) {
//            outputs = new Object[]{ null };
//            return;
//        }

        if (inputCol.isEmpty() && !IBoxConstants.FUN_COUNT.equals(parVal)) {
            outputs = new Object[]{ quoggles.constants.QConstants.EMPTY };
            return;
        }

        if (IBoxConstants.FUN_SUM.equals(parVal)) {
            output = sum(inputCol);
            
        } else if (IBoxConstants.FUN_AVG.equals(parVal)) {
            output = new Double(
                sum(inputCol).doubleValue() / (1.0 * count(inputCol)));
            
        } else if (IBoxConstants.FUN_MIN.equals(parVal)) {
            output = (Number)Collections.min(inputCol);
            
        } else if (IBoxConstants.FUN_MAX.equals(parVal)) {
            output = (Number)Collections.max(inputCol);
            
        } else if (IBoxConstants.FUN_COUNT.equals(parVal)) {
            output = new Integer(count(inputCol));    
                    
        } else {
            throw new InvalidParameterException(getId() + 
                ": invalid value of the parameter: " + parVal);
        }
        
        outputs = new Number[]{ output };
    }
    
    /**
     * Counts the number of elements not equal to <code>QConstants.EMPTY</code>
     * contained in the given collection.
     * 
     * @param col collection of which the elements should be counted 
     * 
     * @return number of elements
     */
    private int count(Collection col) {
        int cnt = 0;
        for (Iterator it = col.iterator(); it.hasNext();) {
            if (it.next() != QConstants.EMPTY) {
                cnt++;
            }
        }
        return cnt;
    }
    
    /**
     * Returns the sum of all (Number) elements in the given collection.
     * @param col
     * 
     * @return the sum of all (Number) elements in the given collection.
     */
    private Number sum(Collection col) {
        if (col.isEmpty()) {
            return new Integer(0);
        }

        int sumInt = 0;
        double sumDouble = 0d;
        
        for (Iterator it = col.iterator(); it.hasNext();) {
            Number num = (Number)it.next();
            if (num instanceof Integer) {
                sumInt += num.intValue();
            } else {
                sumDouble += num.doubleValue();
            }
        }
        if (sumDouble == 0d) {
            return new Integer(sumInt);
        } else {
            return new Double(sumInt + sumDouble);
        }
    }
}