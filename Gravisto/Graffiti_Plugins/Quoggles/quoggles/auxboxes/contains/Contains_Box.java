package quoggles.auxboxes.contains;

import java.util.Collection;

import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;

/**
 * Input: two objects (first will be a collection most times)<p>
 * Output: boolean value indicating if the two inputs are equal or the first
 * actually is a collection and contains the second (the collection is not
 * implicitly flattened). If first input is <code>null</code>, the result
 * will only be true if the second input is <code>null</code> as well.<p>
 * 
 * For a definition of what is meant by "equal", see the javadoc of the
 * <code>execute</code> method (basically, the <code>equals</code> method 
 * is used).
 */
public class Contains_Box extends Box {

    private Object obj1;

    private Object obj2;

    
    /**
     * Two objects are equal if <code>obj1.equals(obj2)</code> holds.<p>
     * This means that two <code>List</code>s are equal only if they contain 
     * the same elements (tested via equals) in the same order.
     * 
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();

        if (areAllInputsNull()) {
            outputs = new Object[]{ null };
            return;
        }
        
        if (obj1 == null) {
            outputs = new Boolean[]{ new Boolean(false) };
            return;
        }
        
        boolean holds = obj1.equals(obj2) || 
            (obj1 instanceof Collection && ((Collection)obj1).contains(obj2));
        
        outputs = new Boolean[]{ new Boolean(holds) };
    }
    
    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ 
            ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION,
            ITypeConstants.ONEOBJECT };
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[]{ ITypeConstants.BOOLEAN }; 
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        obj1 = inputs[0];
        obj2 = inputs[1];
    }
    
    /**
     * Returns 2.
     * 
     * @see quoggles.boxes.IBox#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        return 2;
    }
}