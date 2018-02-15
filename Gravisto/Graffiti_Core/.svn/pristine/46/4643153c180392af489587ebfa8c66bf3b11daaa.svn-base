package quoggles.auxboxes.inducedsubgraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;

import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: one or several graph elements<p>
 * Output: the graph elements belonging to the induced sub graph of the input
 * elements<p>
 * 
 * From a collection of graph elements as input, this box returns a
 * collection of graph elements that belong to the subgraph induced by the
 * given nodes and edges.<p>
 * This implementation constructs the resulting set as follows:<ul>
 * <li>Add all given edges to the result.</li>
 * <li>Add the source and target nodes of these edges.</li>
 * <li>Add all given nodes.
 * <li>Add edges from the graph the source <i>and</i> target nodes are
 * contained in the result set.</li>
 * </ul>
 * 
 */
public class InducedSubGraph_Box extends Box {

    private Collection graphElements;
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        if (graphElements == null) {
            outputs = new Object[]{ null };
            return;
        }

        if (graphElements.isEmpty()) {
            outputs = new List[]{ new ArrayList(0) };
            return;
        }
        
        Set nodes = new HashSet(graphElements.size());
        Set edges = new HashSet(graphElements.size());
        
        for (Iterator iter = graphElements.iterator(); iter.hasNext();) {
            GraphElement ge = (GraphElement)iter.next();
            if (ge == null) {
                throw new QueryExecutionException(getId() + 
                    " null value in" +
                    " input collection encountered. This is forbidden.");
//                continue;
            }
            if (ge instanceof Node) {
                nodes.add(ge);
                for (Iterator eIt = ((Node)ge).getEdgesIterator(); eIt.hasNext();) {
                    Edge edge = (Edge)eIt.next();
                    Node sNode = edge.getSource();
                    if (sNode == ge) {
                        Node tNode = edge.getTarget();
                        if (nodes.contains(tNode)) {
                            edges.add(edge);
                        } else if (graphElements.contains(tNode)) {
                            nodes.add(tNode);
                            edges.add(edge);
                        }
                    } else {
                        if (nodes.contains(sNode)) {
                            edges.add(edge);
                        } else if (graphElements.contains(sNode)) {
                            nodes.add(sNode);
                            edges.add(edge);
                        }
                    }
                }
            } else {
                Edge edge = (Edge)ge;
                Node sNode = edge.getSource();
                Node tNode = edge.getTarget();
                edges.add(edge);
                nodes.add(sNode);
                nodes.add(tNode);
            }
        }
        
        // not very readable but need not copy edge list
        edges.addAll(nodes);
        if (edges.size() == 1) {
            outputs = new GraphElement[]{ 
                (GraphElement)edges.iterator().next() };
        } else {
            outputs = new Collection[]{ edges };
        }
    }
    
    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ ITypeConstants.GRAPH_ELEMENT + 
            ITypeConstants.GRAPH_ELEMENTS};
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        try {
            graphElements = (Collection)inputs[0];
        } catch (ClassCastException cce) {
            try {
                GraphElement ge = (GraphElement)inputs[0];
                graphElements = new ArrayList(1);
                graphElements.add(ge);
            } catch (ClassCastException cce2) {
                throw new InvalidInputException(
                    getId() + 
                    " needs one graph element or a Collection of graph" +
                    " elements) as input");
            }
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
