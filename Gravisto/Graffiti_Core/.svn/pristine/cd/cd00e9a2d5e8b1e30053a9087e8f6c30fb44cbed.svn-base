package quoggles;

import java.awt.Point;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graph.OptAdjListGraph;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;

import quoggles.auxiliary.RootGraphicAttribute;
import quoggles.auxiliary.Util;
import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.event.RepChangeEvent;
import quoggles.event.RepChangeListener;
import quoggles.exceptions.LoadFailedException;
import quoggles.querygraph.BoxAttribute;
import quoggles.representation.BoxRepresentation;
import quoggles.stdboxes.input.Input_Box;

/**
 *
 */
public class QGraph implements RepChangeListener {

    /** Used to communicate between the individual parts of the system */
    private QMain qMain;

    /** The graph underlying the query boxes */
    private Graph queryGraph = new OptAdjListGraph();
    
    /** All BoxRepresentations that appear in the active query graph */
    private Collection boxReps = new LinkedList();
    
    /** Distinct number for the boxes */
    public static int nextBoxNr = 1;

    /** Holds all nodes that belong to input boxes */
    private Set inputNodes = new HashSet();
    
            
    public QGraph(QMain q) {
        qMain = q;
    }

    /**
     * Add graph input box to system. Add a node for that to query graph.
     * 
     * @return the new selection input box
     */
    public BoxRepresentation addGraphInputBox() {
        // add "graph" as possible input
        Input_Box graphInputBox = new Input_Box(IBoxConstants.GRAPH_INPUT);
        BoxRepresentation boxRep = graphInputBox.getGraphicalRepresentation()
            .getRepresentation();
        boxRep.setLocation(0, IBoxConstants.GRAPH_INPUT_Y);
        boxReps.add(graphInputBox.getGraphicalRepresentation()
            .getRepresentation());
        Node inputNode = queryGraph.addNode(new RootGraphicAttribute(""));
        ((CoordinateAttribute)inputNode.getAttribute
            (GraphicAttributeConstants.COORD_PATH)).setCoordinate
                (new Point(0, IBoxConstants.GRAPH_INPUT_Y));
        Util.addLabel(graphInputBox, inputNode);
        Attribute boxAttr = 
            new BoxAttribute(IBoxConstants.BOX_ATTR_ID, graphInputBox);
        graphInputBox.setNode(inputNode);
        inputNode.addAttribute(boxAttr, "");
        inputNodes.add(inputNode);
        
        return boxRep;
    }

    public Node addBoxRep(BoxRepresentation boxRep, Node boxNode) {
        if (boxNode == null) {
            boxNode = queryGraph.addNode(new RootGraphicAttribute(""));
            Util.addLabel(boxRep.getIBoxRepresentation().getIBox(), boxNode);
            Attribute boxAttr = 
                new BoxAttribute(IBoxConstants.BOX_ATTR_ID, 
                    boxRep.getIBoxRepresentation().getIBox());
            boxNode.addAttribute(boxAttr, "");
        }
        boxReps.add(boxRep);
        boxRep.getIBoxRepresentation().getIBox().setNode(boxNode);
        
        return boxNode;
    }
    
    public void reset() {
        nextBoxNr = 1;
        boxReps.clear();
        inputNodes.clear();
        queryGraph.clear();
    }

    /**
     * @see quoggles.event.RepChangeListener#repChanged(quoggles.event.RepChangeEvent)
     */
    public void repChanged(RepChangeEvent event) {
        // TODO check if anything else to do
        
        // remove all edges (might be wrong now)
        for (Iterator it = queryGraph.getEdgesIterator(); it.hasNext();) {
            queryGraph.deleteEdge((Edge)it.next());
        }
        
        // check all connections and add correct edges
        qMain.checkConnections(true);
    }

    /**
     * Add graph input box to system. Add a node for that to query graph.
     * 
     * @return the new selection input box
     */
    public IBox addSelectionInputBox() {
        // add "graph" as possible input
        IBox selectionInputBox = 
            new Input_Box(IBoxConstants.SELECTION_INPUT);
        BoxRepresentation selectionInputBoxRep = selectionInputBox
            .getGraphicalRepresentation().getRepresentation();
        selectionInputBoxRep.setLocation(0, IBoxConstants.SELECTION_INPUT_Y);
        boxReps.add(selectionInputBoxRep);
        Node selectionInputNode = queryGraph.addNode
            (new RootGraphicAttribute(""));
        Util.addLabel(selectionInputBox, selectionInputNode);
        ((CoordinateAttribute)selectionInputNode.getAttribute
            (GraphicAttributeConstants.COORD_PATH)).setCoordinate
                (new Point(0, IBoxConstants.SELECTION_INPUT_Y));
        BoxAttribute boxAttr = new BoxAttribute
            (IBoxConstants.BOX_ATTR_ID, selectionInputBox);
        selectionInputBox.setNode(selectionInputNode);
        selectionInputNode.addAttribute(boxAttr, "");
        inputNodes.add(selectionInputNode);
        
        return selectionInputBox;
    }

    public void removeInputBox(IBox box) {
        boxReps.remove(box);
        queryGraph.deleteNode(box.getNode());
        inputNodes.remove(box.getNode());
    }
    
    public void removeBox(IBox box) {
        boxReps.remove(box);
        queryGraph.deleteNode(box.getNode());
    }
    
    public Graph getQueryGraph() {
        return queryGraph;
    }
    
    /**
     * Set a new query graph.<p>
     * This method calls <code>reset()</code> prior to setting the new graph.
     * Afterwards it uses <code>updateQueryGraph(Graph)</code> to update local 
     * information.<p>
     * <code>checkConnections(boolean)</code> is <b>not</b> called.
     * 
     * @param graph new query graph
     */
    public void setQueryGraph(Graph qGraph) throws LoadFailedException {
        reset();
        queryGraph = qGraph;
        updateQueryGraph(queryGraph);
    }
    
    public Set getInputNodes() {
        return inputNodes;
    }
    
    /**
     * Add boxes etc. according to given graph.
     * 
     * @param newQueryGraph new query graph
     * 
     * @throws LoadFailedException
     */
    private void updateQueryGraph(Graph newQueryGraph)
        throws LoadFailedException {
        
        // build everything according to new query graph
        for (Iterator nIt = newQueryGraph.getNodesIterator(); nIt.hasNext();) {
            Node node = (Node)nIt.next();
            IBox iBox = null;
            try {
                iBox = Util.getBox(node);
            } catch (AttributeNotFoundException anfe) {
                throw new LoadFailedException(anfe.getLocalizedMessage());
            }

            // input boxes are no longer be saved / ignored
            if (iBox instanceof Input_Box) {
                newQueryGraph.deleteNode(node);
                continue;
            }

            BoxRepresentation boxRep = 
                iBox.getGraphicalRepresentation().getRepresentation();
            // this updates: boxNodeMap, boxReps, mainPanel
            qMain.addBoxRep(boxRep, node);
            iBox.setBoxNumber(QGraph.nextBoxNr++);
            boxRep.getIBoxRepresentation().updateId();
            Util.addLabel(iBox, node);
        }

        // remove all edges; checkConnections will add edges
        for (Iterator it = newQueryGraph.getEdgesIterator(); it.hasNext();) {
            Edge edge = (Edge)it.next();
            newQueryGraph.deleteEdge(edge);
        }
    }
    
}
