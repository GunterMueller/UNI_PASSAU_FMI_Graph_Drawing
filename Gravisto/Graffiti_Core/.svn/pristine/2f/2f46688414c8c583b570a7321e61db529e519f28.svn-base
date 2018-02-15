package quoggles.stdboxes.neighborhood;

import java.util.ArrayList;
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
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: one or several <code>GraphElement</code>s<p>
 * Output: empty collection, one or several incident / adjacent 
 * <code>GraphElement</code>s of the input <code>GraphElement</code>s
 * (depending on the value of the parameter).<p>
 * 
 * If the input is <code>null</code>, the output is <code>null</code> as well.
 * <code>null</code> values in the input collection are passed on.
 */
public class Neighborhood_Box extends Box {

    private Collection inputCol;
    
    /** Used only if input is no collection */
    private GraphElement singleInput = null;
    
    private OptionParameter optParam = new OptionParameter(
        new String[]{ IBoxConstants.NEIGHBORS, IBoxConstants.INC_NODES,
            IBoxConstants.INC_EDGES, IBoxConstants.IN_EDGES, 
            IBoxConstants.OUT_EDGES, IBoxConstants.SOURCE_NODE,
            IBoxConstants.TARGET_NODE },
        "", "Types of elements to retrieve from neighborhood");
    

    /**
     *Constructs the box.
     */
    public Neighborhood_Box() {
        parameters = new Parameter[]{ optParam };
    }
    

    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
//        if (singleInput != null) {
//            inputCol = new LinkedList();
//            inputCol.add(singleInput);
//        }
        
//        if (singleInput == null && inputCol == null) {
//            outputs = new Object[]{ null };
//            return;
//        }
        
        List outputCol = new LinkedList();
        String optString = (String)((OptionParameter)parameters[0]).getValue();
        
        if (IBoxConstants.NEIGHBORS.equals(optString)) {
            if (singleInput != null) {
                try {
                    outputs = new Collection[]{ ((Node)singleInput).getNeighbors() };
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs a node having this parameter");
                }
                return;
            }
            for (Iterator it = inputCol.iterator(); it.hasNext();) {
                Node ge = null;
                try {
                    ge = (Node)it.next();
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs collection of nodes having this parameter");
                }
                if (ge != null) {
                    outputCol.add(ge.getNeighbors());
                } else {
                    outputCol.add(null);
                }
            }
            
        } else if (IBoxConstants.INC_NODES.equals(optString)) {
            if (singleInput != null) {
                try {
                    Edge e = (Edge)singleInput;
                    Collection nodes = new ArrayList(2);
                    nodes.add(e.getSource());
                    nodes.add(e.getTarget());
                    outputs = new Collection[]{ nodes };
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs an edge having this parameter");
                }
                return;
            }
            for (Iterator it = inputCol.iterator(); it.hasNext();) {
                Edge ge = null;
                try {
                    ge = (Edge)it.next();
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs collection of edges having this parameter");
                }
                if (ge != null) {
                    Collection nodes = new ArrayList(2);
                    nodes.add(ge.getSource());
                    nodes.add(ge.getTarget());
                    outputCol.add(nodes);
                } else {
                    outputCol.add(null);
                }
            }
            
        } else if (IBoxConstants.INC_EDGES.equals(optString)) {
            if (singleInput != null) {
                try {
                    outputs = new Collection[]{ ((Node)singleInput).getEdges() };
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs a node having this parameter");
                }
                return;
            }
            for (Iterator it = inputCol.iterator(); it.hasNext();) {
                Node ge = null;
                try {
                    ge = (Node)it.next();
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs collection of nodes having this parameter");
                }
                if (ge != null) {
                    outputCol.add(ge.getEdges());
                } else {
                    outputCol.add(null);
                }
            }
            
        } else if (IBoxConstants.IN_EDGES.equals(optString)) {
            if (singleInput != null) {
                try {
                    outputs = new Collection[]{ 
                        ((Node)singleInput).getAllInEdges() };
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs a node having this parameter");
                }
                return;
            }
            for (Iterator it = inputCol.iterator(); it.hasNext();) {
                Node ge = null;
                try {
                    ge = (Node)it.next();
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs collection of nodes having this parameter");
                }
                if (ge != null) {
                    outputCol.add(ge.getAllInEdges());
                } else {
                    outputCol.add(null);
                }
            }
            
        } else if (IBoxConstants.OUT_EDGES.equals(optString)) {
            if (singleInput != null) {
                try {
                    outputs = new Collection[]{ 
                        ((Node)singleInput).getAllOutEdges() };
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs a node having this parameter");
                }
                return;
            }
            for (Iterator it = inputCol.iterator(); it.hasNext();) {
                Node ge = null;
                try {
                    ge = (Node)it.next();
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs collection of nodes having this parameter");
                }
                if (ge != null) {
                    outputCol.add(ge.getAllOutEdges());
                } else {
                    outputCol.add(null);
                }
            }
            
        } else if (IBoxConstants.SOURCE_NODE.equals(optString)) {
            if (singleInput != null) {
                try {
                    outputs = new Node[]{ ((Edge)singleInput).getSource() };
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs an edge having this parameter");
                }
                return;
            }
            for (Iterator it = inputCol.iterator(); it.hasNext();) {
                Edge ge = null;
                try {
                    ge = (Edge)it.next();
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs collection of edges having this parameter");
                }
                if (ge != null) {
                    outputCol.add(ge.getSource());
                } else {
                    outputCol.add(null);
                }
            }
            
        } else if (IBoxConstants.TARGET_NODE.equals(optString)) {
            if (singleInput != null) {
                try {
                    outputs = new Node[]{ ((Edge)singleInput).getTarget() };
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs an edge having this parameter");
                }
                return;
            }
            for (Iterator it = inputCol.iterator(); it.hasNext();) {
                Edge ge = null;
                try {
                    ge = (Edge)it.next();
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs collection of edges having this parameter");
                }
                if (ge != null) {
                    outputCol.add(ge.getTarget());
                } else {
                    outputCol.add(null);
                }
            }
        }
        
        outputs = new Collection[]{ outputCol };
        
        // if input has not been a collection, the output is not either
//        if (singleInput != null) {
//            outputs = new Object[]{ outputCol.get(0) };
//        } else {
//            outputs = new Collection[]{ outputCol };
//        }
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        String optString = (String)((OptionParameter)parameters[0]).getValue();
        if (IBoxConstants.NEIGHBORS.equals(optString)) {
            return new int[]{ ITypeConstants.NODE + ITypeConstants.NODES };
        } else if (IBoxConstants.INC_NODES.equals(optString)) {
            return new int[]{ ITypeConstants.EDGE + ITypeConstants.EDGES };
        } else if (IBoxConstants.INC_EDGES.equals(optString)) {
            return new int[]{ ITypeConstants.NODE + ITypeConstants.NODES };
        } else if (IBoxConstants.SOURCE_NODE.equals(optString)) {
            return new int[]{ ITypeConstants.EDGE + ITypeConstants.EDGES };
        } else if (IBoxConstants.TARGET_NODE.equals(optString)) {
            return new int[]{ ITypeConstants.EDGE + ITypeConstants.EDGES };
        } else if (IBoxConstants.OUT_EDGES.equals(optString) ||
            IBoxConstants.IN_EDGES.equals(optString)) {
            return new int[]{ ITypeConstants.NODE + ITypeConstants.NODES };
        } else {
          return new int[]{ ITypeConstants.GRAPH_ELEMENT + 
              ITypeConstants.GRAPH_ELEMENTS };
        }
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        String optString = (String)((OptionParameter)parameters[0]).getValue();
        if (IBoxConstants.NEIGHBORS.equals(optString) ||
            IBoxConstants.INC_NODES.equals(optString)) {
            return new int[]{ ITypeConstants.COLOF_NODESET };

        } else if (IBoxConstants.SOURCE_NODE.equals(optString) ||
            IBoxConstants.TARGET_NODE.equals(optString)) {
            return new int[]{ ITypeConstants.NODES };
        } else if (IBoxConstants.INC_EDGES.equals(optString)) {
            return new int[]{ ITypeConstants.COLOF_EDGESET };
        } else if (IBoxConstants.IN_EDGES.equals(optString) ||
            IBoxConstants.OUT_EDGES.equals(optString)) {
            return new int[]{ ITypeConstants.COLOF_EDGESET };
        }

        return getInputTypes();
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
            try {
                singleInput = (GraphElement)inputs[0];
            } catch (ClassCastException cce2) {
                throw new InvalidInputException(getId() +
                    " needs one GraphElement or a collection of" +
                    " GraphElements as input, not an object of type " +
                    inputs[0].getClass().getName());
            }
//            inputCol = new LinkedList();
//            inputCol.add(inputs[0]);
        }
    }

    /**
     * @see quoggles.boxes.IGraphicalBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof Neighborhood_Rep)) {

            iBoxGRep = new Neighborhood_Rep(this);
        }
        return iBoxGRep;
    }
}
