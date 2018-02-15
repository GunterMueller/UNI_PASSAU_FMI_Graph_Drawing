package quoggles.stdboxes.connectors;

import quoggles.boxes.Box;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: anything<p>
 * Output: same as input
 * 
 * Connects two boxes. Acts as an identity operator.
 */
public class OneOneConnector_Box extends Box {

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof OneOneConnector_Rep)) {

            iBoxGRep = new OneOneConnector_Rep(this);
        }

        return iBoxGRep;
    }

}
