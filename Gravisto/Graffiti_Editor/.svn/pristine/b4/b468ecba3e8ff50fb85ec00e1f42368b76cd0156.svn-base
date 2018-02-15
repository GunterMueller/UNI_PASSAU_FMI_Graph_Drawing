package quoggles.auxboxes.getgraphelements;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.constants.QConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: one or several objects<p>
 * Output: all <code>GraphElement</code>s (or whatever is specified via the 
 * parameter) found in the input.
 * <code>null</code> values are filtered out.
 * <code>null</code> if the input itself is <code>null</code><p>
 * If the input is a non-collection object, the output is <code>null</code> or
 * the object itself, depending on whether the object matches the parameter 
 * type or not.
 * The order is the order in which the lements are found in the input.<p>
 * The box discards any collections within the input as not matching and does 
 * not ascend recursively into them.<p>
 * 
 * From the input this box filters all graph elements, only nodes, only 
 * edges, ... according to the parameter.<p>
 */
public class GetGraphElements_Box extends Box {

    private Collection inputCol = null;
    
    /** Only set if input is not a collection */
    private Object singleInput = null;
    
    
    /**
     * Constructs the box.
     */
    public GetGraphElements_Box() {
        parameters = new Parameter[]{
            new OptionParameter(
                new Integer[]{ new Integer(IBoxConstants.GETNODES), 
                    new Integer(IBoxConstants.GETEDGES),
                    new Integer(IBoxConstants.GETELEMENTS) }, 
                        IBoxConstants.GETELEMENTS,
                            "returns", "Specifies what this box returns.") };

    }
    
    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ 
            ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION };
    }

    /**
     * Returns output types according to parameter.
     * 
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        if (parameters == null) {
            return new int[]{ ITypeConstants.GRAPH_ELEMENT
                + ITypeConstants.GRAPH_ELEMENTS };
        } else {
            Object val = ((OptionParameter)parameters[0]).getValue();
            return val.equals(new Integer(IBoxConstants.GETNODES)) 
                ? new int[]{ ITypeConstants.NODE + ITypeConstants.NODES } 
                : val.equals(new Integer(IBoxConstants.GETEDGES))
                    ? new int[]{ ITypeConstants.EDGE +ITypeConstants.EDGES }
                    : new int[]{ ITypeConstants.GRAPH_ELEMENT
                        + ITypeConstants.GRAPH_ELEMENTS };
        }
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
//            inputCol = new ArrayList(1);
//            inputCol.add(inputs[0]);
            inputCol = null;
            singleInput = inputs[0];
        }
    }

    /**
     * @see quoggles.boxes.IGraphicalBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof GetGraphElements_Rep)) {

            iBoxGRep = new GetGraphElements_Rep(this);
        }
        return iBoxGRep;
    }

    /**
     * Searches in the input for graph elements (according to the value of the
     * parameter). The search is not "recursive" in that collections within 
     * collections are not searched.
     * 
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        List outputCol = new LinkedList();
        
        Object val = ((OptionParameter)parameters[0]).getValue();
        if (singleInput != null) {
            if (val.equals(new Integer(IBoxConstants.GETELEMENTS))) {
                if (singleInput instanceof GraphElement) {
                    //outputCol.add(singleInput);
                    outputs = new GraphElement[]{ (GraphElement)singleInput };
                } else {
                    //outputCol = null;
                    outputs = new Object[]{ QConstants.EMPTY };
                }
        
            } else if (val.equals(new Integer(IBoxConstants.GETEDGES))) {
                if (singleInput instanceof Edge) {
                    //outputCol.add(singleInput);
                    outputs = new Edge[]{ (Edge)singleInput };
                } else {
                    //outputCol = null;
                    outputs = new Object[]{ QConstants.EMPTY };
                }

            } else { // GETNODES
                if (singleInput instanceof Node) {
                    //outputCol.add(singleInput);
                    outputs = new Node[]{ (Node)singleInput };
                } else {
                    //outputCol = null;
                    outputs = new Object[]{ QConstants.EMPTY };
                }
            }
            
            return;
        }

        if (inputCol == null) {
            outputs = new Object[]{ null };
            return;
        }
        
        if (val.equals(new Integer(IBoxConstants.GETELEMENTS))) {
            for (Iterator it = inputCol.iterator(); it.hasNext();) {
                Object obj = it.next();
                if (obj instanceof GraphElement && obj != null) {
                    outputCol.add(obj);
                }
            }
        
        } else if (val.equals(new Integer(IBoxConstants.GETEDGES))) {
            for (Iterator it = inputCol.iterator(); it.hasNext();) {
                Object obj = it.next();
                if (obj instanceof Edge && obj != null) {
                    outputCol.add(obj);
                }
            }

        } else { // GETNODES
            for (Iterator it = inputCol.iterator(); it.hasNext();) {
                Object obj = it.next();
                if (obj instanceof Node && obj != null) {
                    outputCol.add(obj);
                }
            }
        }

        outputs = new Collection[]{ outputCol };
    }
}