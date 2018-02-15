package quoggles.auxboxes.sizeof;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;

/**
 * Input: one object or a collection
 * Output: the size of the input as an <code>Integer</code>. If it is one
 * object, the output is <code>1</code>. Yields
 * <code>0</code> for a <code>null</code> input.
 */
public class SizeOf_Box extends Box {

    private Collection inputCol;
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        boolean allSubCols = true;
        Iterator it = inputCol.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof Collection)) {
                allSubCols = false;
                break;
            }
        }
        
        if (allSubCols) {
            ArrayList outCol = new ArrayList(inputCol.size());
            it = inputCol.iterator();
            while (it.hasNext()) {
                outCol.add(new Integer(((Collection)it.next()).size()));
            }
            
            outputs = new Collection[]{ outCol };
        } else
            outputs = new Integer[]{ new Integer(inputCol.size()) };
    }
    
    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ 
            ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION };
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
        if (inputs[0] == null) {
            inputCol = new ArrayList(0);
        } else {
            try {
                inputCol = (Collection)inputs[0];
            } catch (ClassCastException cce) {
                inputCol = new ArrayList(1);
                inputCol.add(inputs[0]);
            }
        }
    }
}
