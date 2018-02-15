package quoggles.deprecated.stdboxes.reverse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: an <code>Object</code> or a <code>Collection</code><p>
 * Output: a <code>Collection</code> with the same elemenst as the input 
 * <code>Collection</code> with the order of teh elements reversed. If the 
 * input is not a <code>Collection</code>, the input object is returned 
 * untouched.
 */
public class Reverse_Box extends Box {

    private Collection inputCol;
    
    /** Only set if input is not a collection */
    private Object singleInput = null;
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        if (singleInput != null) {
            outputs = new Object[]{ singleInput };
            return;
        }        
        
        if (inputCol == null) {
            outputs = new Object[]{ null };
            return;
        }
        
        if (inputCol.isEmpty()) {
            outputs = new Collection[]{ inputCol };
            return;
        }
        
        List list = new ArrayList(inputCol);
        Collections.reverse(list);
        outputs = new List[]{ list };
    }
    
    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ 
            ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION };
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
            inputCol = null;
            singleInput = inputs[0];
        }
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof DefaultBoxRepresentation)) {
            iBoxGRep = new DefaultBoxRepresentation(this);
        }
        return iBoxGRep;
    }
}
