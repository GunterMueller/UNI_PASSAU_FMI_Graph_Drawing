package quoggles.deprecated;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.auxboxes.getgraphelements.GetGraphElements_Rep;
import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * From the input this box filters all graph elements, only nodes or only 
 * edges according to the parameter.
 * The box searches all collections at any position in the input.
 * 
 * @deprecated use a combination of Flatten_Box and GetGraphElements_Box
 */
public class RecursiveGetGraphElements_Box extends Box {

    private Collection inputCol = null;
    
    
    /**
     * Construct box.
     */
    public RecursiveGetGraphElements_Box() {
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
        return new int[]{ ITypeConstants.GENERAL };
    }

    /**
     * According to parameter.
     * 
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        if (parameters == null) {
            return new int[]{ ITypeConstants.GRAPH_ELEMENTS };
        } else {
            Object val = ((OptionParameter)parameters[0]).getValue();
            return val.equals(new Integer(IBoxConstants.GETNODES)) 
                ? new int[]{ ITypeConstants.NODES } 
                : val.equals(new Integer(IBoxConstants.GETEDGES))
                    ? new int[]{ ITypeConstants.EDGES }
                    : new int[]{ ITypeConstants.GRAPH_ELEMENTS };
        }
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        try {
            inputCol = (Collection)inputs[0];
        } catch (ClassCastException cce) {
            inputCol = new ArrayList(1);
            inputCol.add(inputs[0]);
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

    private void digForGraphElements
        (Collection digInCol, Collection resCol) {

        for (Iterator it = digInCol.iterator(); it.hasNext();) {
            Object obj = it.next();
            if (obj instanceof GraphElement) {
                resCol.add(obj);
            } else if (obj instanceof Collection) {
                digForGraphElements((Collection)obj, resCol);
            }
        }
    }

    private void digForNodes
        (Collection digInCol, Collection resCol) {

        for (Iterator it = digInCol.iterator(); it.hasNext();) {
            Object obj = it.next();
            if (obj instanceof Node) {
                resCol.add(obj);
            } else if (obj instanceof Collection) {
                digForNodes((Collection)obj, resCol);
            }
        }
    }

    private void digForEdges
        (Collection digInCol, Collection resCol) {

        for (Iterator it = digInCol.iterator(); it.hasNext();) {
            Object obj = it.next();
            if (obj instanceof Edge) {
                resCol.add(obj);
            } else if (obj instanceof Collection) {
                digForEdges((Collection)obj, resCol);
            }
        }
    }

    /**
     * Searches in the input for graph elements (according to the value of the
     * parameter). The search is "recursive" in that collections within 
     * collections are searched, too.
     * 
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
            
        super.execute();
        Collection outputCol = new LinkedList();
        Object val = ((OptionParameter)parameters[0]).getValue();
        if (val.equals(new Integer(IBoxConstants.GETELEMENTS))) {
            digForGraphElements(inputCol, outputCol);
        
        } else if (val.equals(new Integer(IBoxConstants.GETEDGES))) {
            digForEdges(inputCol, outputCol);

        } else { // GETNODES
            digForNodes(inputCol, outputCol);
        }

        outputs = new Collection[]{ outputCol };
    }
}