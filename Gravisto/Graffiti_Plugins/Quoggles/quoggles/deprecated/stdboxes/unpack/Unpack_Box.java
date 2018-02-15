package quoggles.deprecated.stdboxes.unpack;

import java.util.Collection;

import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: an object<p>
 * Output: <code>null</code> if the input has been <code>null</code>.
 * If the input is a collection of exactly one element, this element
 * is returned<p>
 * All other types of input (empty collections, collections with several
 * elements, objects that are not collections, etc.) produce an error.
 */
public class Unpack_Box extends Box {

    private Collection inputCol;
    
   
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        if (inputCol == null) {
            outputs = new Object[]{ null };
            return;
        }
        
        outputs = new Object[]{ inputCol.iterator().next() };
    }
    
    /**
     * ITypeConstants.COLLECTION
     * 
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ ITypeConstants.COLLECTION };
    }

    /**
     * ITypeConstants.ONEOBJECT
     * 
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[]{ ITypeConstants.ONEOBJECT };
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        try {
            inputCol = (Collection)inputs[0];
            if (inputCol.size() != 1) {
                throw new InvalidInputException(getId() +
                    " needs a collection with ONE ELEMENT not with " +
                    inputCol.size());
            }
        } catch (ClassCastException cce) {
            throw new InvalidInputException(getId() +
                " needs a one element collection, not sth of type " +
                inputs[0].getClass().getName());
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
