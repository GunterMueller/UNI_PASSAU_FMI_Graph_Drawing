package quoggles.auxboxes.equals;

import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;

/**
 * Input: two objects<p>
 * Output: boolean value indicating if the two inputs are equal.<p>
 * 
 * For a definition of what is meant by "equal", see the javadoc of the
 * <code>execute</code> method (basically, the <code>equals</code> method 
 * is used).
 */
public class Equals_Box extends Box {

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

        if (obj1 == null || obj2 == null) {
            if (obj1 == obj2) {
                outputs = new Boolean[]{ new Boolean(true) };
            } else {
                outputs = new Boolean[]{ new Boolean(false) };
            }
        } else {        
            outputs = new Boolean[]{ new Boolean(obj1.equals(obj2)) };
        }
    }
    
    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ 
            ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION,
            ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION };
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