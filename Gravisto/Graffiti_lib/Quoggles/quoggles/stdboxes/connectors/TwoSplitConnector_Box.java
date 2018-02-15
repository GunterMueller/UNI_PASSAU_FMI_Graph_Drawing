package quoggles.stdboxes.connectors;

import quoggles.boxes.Box;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: anything<p>
 * Output: two times the input (unchanged)
 */
public class TwoSplitConnector_Box extends Box {

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof TwoSplitConnector_Rep)) {

            iBoxGRep = new TwoSplitConnector_Rep(this);
        }

        return iBoxGRep;
    }

    /**
     * "Duplicates" the input.
     * 
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException { 
        super.execute();
        
        outputs = new Object[]{ inputs[0], inputs[0] };
    }

    /**
     * "Duplicates" the input type.
     * 
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        int t = getInputTypes()[0];
        return new int[]{ t, t };
    }

    /**
     * Returns 2.
     * 
     * @see quoggles.boxes.IBox#hasSeveralOutputs()
     */
    public int getNumberOfOutputs() {
        return 2;
    }

}
