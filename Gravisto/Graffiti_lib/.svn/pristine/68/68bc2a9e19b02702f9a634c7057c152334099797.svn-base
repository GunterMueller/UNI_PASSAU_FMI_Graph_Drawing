package quoggles.auxboxes.getproperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.Box;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.InvalidParameterException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: graph element(s)<p>
 * Output: a property or a list of properties (the type of property is 
 * specified via a parameter) like degree<p>
 * 
 * <code>null</code> values are not put into the output list.<p>
 * If the resulting list contains only one element, this element is returned
 * (not a one-element list).
 */
public class GetProperty_Box extends Box {

    private String[] options = new String[]{
        IBoxConstants.DEGREE, 
        IBoxConstants.INDEGREE, 
        IBoxConstants.OUTDEGREE };
    
    private OptionParameter attrPath = new OptionParameter
        (options, 0, false, "Property", "Name of the Property");

    private Collection inputCol;
    
    private GraphElement singleInput = null;
    

    /**
     * Constructs the box.
     */
    public GetProperty_Box() {
        parameters = new Parameter[]{ attrPath };
    }
    
    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
//        return new int[]{ ITypeConstants.GRAPH_ELEMENTS +
//            ITypeConstants.GRAPH_ELEMENT };
        return new int[]{ ITypeConstants.NODE + ITypeConstants.NODES };
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

        singleInput = null;
        try {
            inputCol = (Collection)inputs[0];
            for (Iterator it = inputCol.iterator(); it.hasNext();) {
                singleInput = (Node)it.next();
            }
            singleInput = null;
        } catch (ClassCastException cce) {
            try {
                singleInput = (Node)inputs[0];
//                inputCol = new ArrayList(1);
//                inputCol.add(ge);
            } catch (ClassCastException cce2) {
                throw new InvalidInputException(
                    getId() +
                    " needs one or a Collection of Nodes as input");
            }
        }
    }

    /**
     * @see quoggles.boxes.IGraphicalBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof GetProperty_Rep)) {
            iBoxGRep = new GetProperty_Rep(this);
        }
        return iBoxGRep;
    }

    /**
     * Returns the property associated with the current parameter value.
     * 
     * @param ge
     * @return
     * @throws InvalidParameterException
     */
    private Object getProperty(GraphElement ge)
        throws InvalidParameterException {

        String paramValue = 
            ((OptionParameter)parameters[0]).getValue().toString();
        try {
            if (IBoxConstants.DEGREE.equals(paramValue)) {
                Node node = ((Node)ge);
                return new Integer(node.getDirectedInEdges().size() + 
                    node.getDirectedOutEdges().size() +
                    node.getUndirectedEdges().size());
            } else if (IBoxConstants.INDEGREE.equals(paramValue)) {
                return new Integer(((Node)ge).getInDegree());
            } else if (IBoxConstants.OUTDEGREE.equals(paramValue)) {
                return new Integer(((Node)ge).getOutDegree());
            } else {
                throw new InvalidParameterException(getId() +
                    ": Unknown parameter.");
            }
        } catch (ClassCastException cce) {
            throw new InvalidParameterException(getId() +
                ": Invalid parameter for this type of input ( " +
                ge.getClass().getName() + "), or v.v.");
        }
    }
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        if (singleInput != null) {
            inputCol = new ArrayList(1);
            inputCol.add(singleInput);
        }
        
        if (inputCol == null) {
            outputs = new Object[]{ null };
            return;
        }
        
        List outputCol = new ArrayList(inputCol.size());

        for (Iterator it = inputCol.iterator(); it.hasNext();) {
            GraphElement ge = null;
            try {
                ge = (GraphElement)it.next();
                if (ge == null) {
                    throw new QueryExecutionException(getId() + 
                        " null value in" +
                        " input collection encountered. This is forbidden.");
//                    outputCol.add(null);
//                    continue;
                }
            } catch (ClassCastException cce) {
                throw new InvalidParameterException(getId() +
                    ": Invalid parameter for this type of input ( " +
                    ge.getClass().getName() + "), or v.v.");
            }
            Object prop = getProperty(ge);
            if (prop != null) {
                outputCol.add(prop);
            }
        }
        
        //doSetOutput(outputCol);

        // if input has not been a collection, the output is not either
        if (singleInput != null && outputCol.size() == 1) {
            outputs = new Object[]{ outputCol.get(0) };
        } else {
            outputs = new Collection[]{ outputCol };
        }
    }
}