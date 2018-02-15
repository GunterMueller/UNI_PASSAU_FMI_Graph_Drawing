package quoggles.deprecated.stdboxes.flatten;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: an object<p>
 * Output: <code>null</code> if the input has been <code>null</code>.
 * If the input is a collection, a flat structure of that collection
 * is returned (this flattenig is applied recursively)<p>
 *
 * From its hierarchical input (a <code>Collection</code> holding 
 * <code>Collections</code>), a flat structure is retrieved.<p>
 * <b>Example</b>: If the input is a list of the form<p>
 * <code>  [ [A1, A2], B, [C1, A1, C2] ]</code><p>
 * then the result list will look like this:<p>
 * <code>  [ A1, A2, B, C1, A1, C2 ]</code><p>
 * 
 * <code>null</code> values are removed.<p>
 *  
 * The order will be preserved (if the given 
 * <code>Collections</code>s guarantee an order).<p>
 * Duplicates are not removed.<p>
 * <code>null</code> as input is passed on.<p>
 * A non-collection input is put into a one element list.
 */
public class Flatten_Box extends Box {

    private Collection inputCol;
    
    /** Only set if input is not a collection */
    private Object singleInput = null;
    
    
    /**
     * Flattens a hierarchical structure.
     * 
     * @param in the object to flatten
     * @return <code>Collection</code> that does not hold any 
     * <code>Collection</code> as element; the input object itself if it had
     * not been a collection
     */
    private Object flatten(Object in) {
        if (in instanceof Collection) {
            List outCol = new LinkedList();
            for (Iterator it = ((Collection)in).iterator(); it.hasNext();) {
                Object elem = it.next();
//                if (elem == null) {
//                    return outCol;
//                }
                // recursively flatten
                Object sub = flatten(elem);
                if (sub instanceof Collection) {
                    // add each element
                    outCol.addAll((Collection)sub);
                } else {
                    // add this (non-collection) element
                    outCol.add(sub);
                }
            }        
            return outCol;
        } else {
            return in;
        }
    }
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        if (singleInput != null) {
            outputs = new Object[]{ flatten(singleInput) };
            return;
        }        
        
        if (inputCol == null) {
            outputs = new Object[]{ null };
            return;
        }
        
        if (inputCol.isEmpty()) {
            outputs = new Collection[]{ new ArrayList(0) };
            return;
        }
        
        outputs = new Object[]{ flatten(inputCol) };
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
