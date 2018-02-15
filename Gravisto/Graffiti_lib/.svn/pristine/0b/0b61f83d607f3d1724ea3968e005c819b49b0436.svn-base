package quoggles;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.view.MessageListener;
import org.graffiti.selection.Selection;

import quoggles.auxiliary.MyList;
import quoggles.auxiliary.RunQuery;
import quoggles.auxiliary.Util;
import quoggles.boxes.IBox;
import quoggles.boxes.IOutputBox;
import quoggles.constants.IBoxConstants;
import quoggles.exceptions.InputNotSetException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.stdboxes.complexfilter.ComplexFilter_Box;
import quoggles.stdboxes.input.Input_Box;
import quoggles.stdboxes.subquery.SubQuery_Box;

/**
 *
 */
public class QRunQuery implements RunQuery {

    /** The result of the query */
    private List queryResult;
    
    /** The graph to work on */
    private Graph graph;
    
    /** Used to communicate between the individual parts of the system */
    private QMain qMain;
    
    /** Dialog that shows the result table */
    private QResultDialog qResultDialog;
    
    public static int minOrderNr = 1;

    
    /**
     * Constructor taking the main component as parameter.
     * 
     * @param q main component used for communication
     */
    public QRunQuery(QMain q) {
        qMain = q;
    }
    
    
    public void setGraph(Graph g) {
        graph = g;
    }
    
    public void reset() {
        queryResult = null;
    }

    /**
     * Returns the result of the query. For this to be valid, 
     * <code>runQuery</code> must have been executed.
     * It is a list of rows, i.e. a list of lists. Even if a row consists
     * only of one entry, it is packed into a list (of size one).
     * 
     * @return the result of the query (list of rows)
     */
    public List getQueryResult() {
        return queryResult;
    }

    /**
     * Sets the input of the given <code>InputBox</code> and executes it.
     * 
     * @param iBox
     * @throws QueryExecutionException
     */
    private void executeInputBox(Input_Box iBox) 
        throws QueryExecutionException{
        
        String name = iBox.getBoxName();
        if (IBoxConstants.GRAPH_INPUT.equals(name)) {
            iBox.setInputBoxInput(graph);
        } else if (IBoxConstants.SELECTION_INPUT.equals(name)) {
            Selection selection = null;
            if (QModes.standAlone) {
                System.out.println("There should not be a selection InputBox" +
                    " in standalone mode. Executed with empty selection.");
                selection = new Selection("__empty__");
            } else {
                if (QModes.followMode) {
////                    if (followSelection == null) {
////                        try {
////                            followSelection = (Selection)GraffitiSingleton
////                                .getInstance().getMainFrame()
////                                    .getActiveEditorSession()
////                                        .getSelectionModel()
////                                            .getActiveSelection().clone();
////                        } catch (CloneNotSupportedException e) {
////                            // selections are cloneable
////                            e.printStackTrace();
////                        }
////                    }
////                    selection = followSelection;
                } else {
                    selection = GraffitiSingleton.getInstance().getMainFrame()
                        .getActiveEditorSession().getSelectionModel()
                            .getActiveSelection();
                }
            }
            iBox.setInputBoxInput(selection);
        }
        
        iBox.execute();
    }    

    /**
     * @see quoggles.auxiliary.RunQuery#runQuery(org.graffiti.graph.Graph, java.util.Collection, boolean, java.util.List, java.util.Stack)
     */
    public List runQuery(Graph qGraph, Collection sourceNodes, 
        boolean internalUseOnly, List result, Stack nodesTodo) 
        throws QueryExecutionException {
       
//        if (executedNodes == null) {
//            executedNodes = new LinkedList();
//        }
//        Stack nodesTodo = new Stack();
//        ArrayList result = new ArrayList();
        
//////        List fillNodes = new LinkedList();
        
        // add input boxes to list
        for (Iterator iNodeIt = sourceNodes.iterator(); iNodeIt.hasNext();) {
            nodesTodo.push(iNodeIt.next());
        }
        
        Util.resetAllBoxesFrom(qGraph, sourceNodes);
        //resetAssignedRows();
        
        // saves temporarily removed edges
        //Collection tempKilledEdges = new ArrayList(1);
        
        try {
        
        while (!nodesTodo.isEmpty()) {
            Node node = (Node)nodesTodo.pop();
            IBox box = Util.getBox(node);
            
            System.out.println(" --- " + box.getId());            
            
            if (box.ignoreBox()) {
                // box has already been executed (sub query); ignore it
                continue;
            }
            
            if (box.needsQueryRunner()) {
                box.setQueryRunner(this);
                box.setCurrentResult(result);
                box.setCurrentNodesTodo(nodesTodo);
            }
            
            if (box.getNumberOfInputs() != node.getInDegree()) {
                // check if the absence of edges can be tolerated i.e. if the
                // input at these indices have already been set (from outside)
                // (first node in subquery has no in-edges)
                boolean[] inputSet = new boolean[box.getNumberOfInputs()];
                for (int i = 0; i < inputSet.length; i++) {
                    inputSet[i] = false;
                }
                Iterator it = node.getDirectedInEdgesIterator();
                while (it.hasNext()) {
                    Edge edge = (Edge)it.next();
                    inputSet[Util.getIOIndex(edge, false)] = true;
                }
                for (int i = 0; i < inputSet.length; i++) {
                    if (!inputSet[i]) {
                        // not set via an existing edge
                        if (!box.isInputSetAt(i)) {
                            // not already set from outside
                            throw new QueryExecutionException(box.getId() +
                                ": No input set at index " + i + 
                                " (index count starts with 0).");
                        }
                    }
                }
            }
            
            try {
                if (box instanceof Input_Box) {
                    // use special setInput method for InputBoxes
                    executeInputBox((Input_Box)box);
                    Util.pushOutputs(node, box);

                } else if (box instanceof IOutputBox) { 
                    box.execute();
                    
                    // add output to result
                    int rowNr = ((OptionParameter)box.getParameters()[0])
                        .getOptionNr();

                    Util.ensureSize(result, rowNr);

//                    result.set(rowNr, box.getOutputAt(0));
                    Object prevOutput = result.get(rowNr);
                    if (prevOutput == null) {
                        // first time this box is executed
                        Object op = box.getOutputAt(0);
                        if (op != null) {
                            if (op instanceof Collection) {
                                result.set(rowNr, op);
                            } else {
                                if (internalUseOnly) {
                                    result.set(rowNr, op);
                                } else {
                                    Collection ml = new MyList(1);
                                    ml.add(op);
                                    result.set(rowNr, ml);
                                    ////result.set(rowNr, box.getOutputAt(0));
                                }
                            }
                        } else {
                            Collection ml = new MyList(1);
                            result.set(rowNr, ml);
                        }
                    } else {
                        // have previously executed this box
                        Object newObj = box.getOutputAt(0);
                        if (prevOutput instanceof MyList) {
                            if (newObj != null) {
                                if (newObj instanceof MyList) {
                                    ((Collection)prevOutput)
                                        .addAll((Collection)newObj);
                                } else {
                                    ((Collection)prevOutput).add(newObj);
                                }
                            }
                        } else if (newObj != null) {
                            Collection col = new MyList();
                            col.add(prevOutput);
                            col.add(newObj);
                            result.set(rowNr, col);
                        }
                    }
                    
                    Util.pushOutputs(node, box);

//               } else if (box instanceof BoolPredicateEnd_Box) {
//                    box.execute();
//                    // add output to result
//                    int rowNr = ((IntegerParameter)box.getParameters()[0])
//                        .getInteger().intValue();
//        
//                    Util.ensureSize(result, rowNr);
//                    result.set(rowNr, box.getOutputAt(0));
//        
//                    Util.pushOutputs(node, box);

                } else if (box instanceof ComplexFilter_Box) {
                    ComplexFilter_Box complBox = (ComplexFilter_Box)box;
                    
                    int orderNr = 
                        ((IntegerParameter)complBox.getParameters()[0])
                            .getInteger().intValue();
                    if (orderNr > minOrderNr) {
                        // have to wait for complex filter box with 
                        // higher priority; readd at bottom of stack
                        nodesTodo.add(0, node);
                        System.out.println("   --- delay execution ...");
                        continue;
//                    } else {
//                        minOrderNr++;
                    }
                    
//                    boolean noPredicate = true;
//                    for (Iterator it = node.getDirectedOutEdgesIterator(); it.hasNext();) {
//                        Edge edge = (Edge)it.next();
//                        int index = Util.getIOIndex(edge, true);
//                        if (index == 1) {
//                            // predicate sub query starts here
//                            complBox.setFirstPredicateNode(edge.getTarget());
//                            complBox.setNextInputIndex(Util.getIOIndex(edge, false));
////                            tempKilledEdges.add(edge);
////                            queryGraph.deleteEdge(edge);
//                            noPredicate = false;
//                            break;
//                        }
//                    }                    
//                    
//                    complBox.setNoPredicate(noPredicate);
                    
                    if (box.isInputSet()) {
                        // should be able to execute; 
                        // complex filter with next higher number can be 
                        // executed from now on
                        minOrderNr++;
                    }
                    
                    box.execute();
                    
// deprecated: outputs directly write to result
//                    List otherResults = complBox.getResult();
//                    List otherResultNrs = complBox.getResultRowNumbers();
//                    if (otherResults != null) {
//                        if (otherResultNrs == null ||
//                            otherResults.size() != otherResultNrs.size()) {
//                            throw new RuntimeException(complBox.getId() +
//                                ": result / -nr lists must have same size");
//                        }
//                        
//                        Iterator rNrIt = otherResultNrs.iterator();
//                        for (Iterator rIt = otherResults.iterator();rIt.hasNext();) {
//                            Object resObj = rIt.next();
//                            int rowNr = ((Integer)rNrIt.next()).intValue();
//    
//                            Util.ensureSize(result, rowNr);
//                            //result.set(rowNr, resObj);
//                            List col = (List)result.get(rowNr);
//                            if (col == null) {
//                                col = new LinkedList();
//                                result.set(rowNr, col);
//                            }
//                            col.add(resObj);
//                        }
//                    }
                    
                    // do not try to push output with index == 1
                    Util.pushOutputs(node, box, 1);
                    
                } else {
                    int saveMinOrderNr = minOrderNr;
                    if (box instanceof SubQuery_Box) {
                        minOrderNr = 1;
                    }
                    box.execute();
                    minOrderNr = saveMinOrderNr;
                    Util.pushOutputs(node, box);
                }
                // successfully executed
                
//                if (executedNodes != null) {
//                    executedNodes.add(node);
//                }
                
                // add out neighbors
                if (box instanceof ComplexFilter_Box) {
                    // don't add predicate; has already been executed by box
                    Iterator it = node.getDirectedOutEdgesIterator();
                    while (it.hasNext()) {
                        Edge edge = (Edge)it.next();
                        int index = Util.getIOIndex(edge, true);
                        if (index != 1) {
                            Node n = edge.getTarget();
                            nodesTodo.remove(n);
                            nodesTodo.push(n);
                        }
                    }                    
                } else {
                    for (Iterator nextNodeIt = node.getOutNeighborsIterator(); 
                        nextNodeIt.hasNext();) {
    
                        Node n = (Node)nextNodeIt.next();
                        nodesTodo.remove(n);
                        nodesTodo.push(n);
                    }
                }                    

            } catch (InputNotSetException inse) {
                // could not execute box, add boxes that provide input
                System.out.println(" ---    input not set");            
                boolean newInput = false;
//                for (Iterator inNeigh = node.getInNeighborsIterator(); 
//                    inNeigh.hasNext();) {
//                    
//                    Node n = (Node)inNeigh.next();
//                    if (!Util.getBox(n).hasBeenExecuted()) {
//                    //if (!executedNodes.contains(n)) {
//                        // if needed box is already on the stack, move it to the
//                        // top else add it to the top
//                        nodesTodo.remove(n);
//                        nodesTodo.push(n);
//                        newInput = true;
//                    }
//                }
                for (Iterator inEdgesIt = node.getDirectedInEdgesIterator(); 
                    inEdgesIt.hasNext();) {
                    
                    Edge e = (Edge)inEdgesIt.next();
                    Node n = e.getSource();
                    IBox nbox = Util.getBox(n);
                    if (nbox instanceof ComplexFilter_Box && 
                        Util.getIOIndex(e, true) == 1 &&
                        !sourceNodes.contains(node)) {
                        ((ComplexFilter_Box)nbox).setNotExecuted();
                    }
                    if (!nbox.hasBeenExecuted()) {
                    //if (!executedNodes.contains(n)) {
                        // if needed box is already on the stack, move it to the
                        // top else add it to the top
                        nodesTodo.remove(n);
                        nodesTodo.push(n);
                        newInput = true;
                    }
                }
                if (!newInput) {
                    // did not find new input box
                    throw new QueryExecutionException(box.getId() +
                        ": Some inputs could not be set.");
                }
//                int inputsSet = 0;
//                for (Iterator inEdgeIt = node.getDirectedInEdgesIterator(); 
//                    inEdgeIt.hasNext();) {
//                    
//                    Edge e = (Edge)inEdgeIt.next();
//                    int index = Util.getIOIndex(e, false);
//                    inputsSet++;
//                    // only add boxes for input that is not set already
//                    if (!box.isInputSetAt(index)) {
//                        Node n = e.getSource();
//                        // if needed box is already in the list, move it to the
//                        // top else push it
//                        nodesTodo.remove(n);
//                        nodesTodo.push(n);
//                    }
//                }
//                if (inputsSet < box.getNumberOfInputs()) {
//                    // no chance to get all inputs
//                    throw new QueryExecutionException(box.getId() +
//                        ": Some inputs could not be set.");
//                }
                    
            } // InvalidInputException / etc. thrown upwards
                    
        }

//        // assure that boxes are reset if those get inputs from other
//        // complex filter boxes
//        resetAllBoxesFrom(qGraph, sourceNodes);

        if (!internalUseOnly) {
            if (qResultDialog != null) {
                qResultDialog.clearTable();
            } else {
                qResultDialog = new QResultDialog
                    (qMain.getDialog(), "Query result:", false);
            }
            qResultDialog.setVisible(true);
            
            // remove entries for BoolPredicateEnd_Box-es
            for (int i = result.size() - 1; i >= QAssign.getMaxAssignedRowNr(); i--) {
                result.remove(i);
            }
            // remove empty "trailing" rows
            int i = result.size() -1;
            while (i >= 0 && result.get(i) == null) {
                result.remove(i--);
            }
            
            int rowCnt = 0;
            for (Iterator rowIt = result.iterator(); rowIt.hasNext();) {
                if (rowCnt >= QAssign.getMaxAssignedRowNr()) {
                    // ignore outputs from non-IOutputBox output boxes
                    // (like BoolEndPredicate_Box)
                    break;
                }
                Object obj = rowIt.next();
//                if (obj instanceof Collection) {
//                    Collection col = (Collection)obj;
//                    if (col.size() == 1) {
//                        obj = col.iterator().next();
//                    }
//                }
                try {
                    qResultDialog.addRow((Collection)obj);
                } catch (ClassCastException cce) {
                    Collection col = new ArrayList(1);
                    col.add(obj);
                    qResultDialog.addRow(col);
                }
                rowCnt++;
            }

            qResultDialog.updateWidths();

    
            // TODO better handling of selection needed
            Selection resSelection = 
                new Selection("_QUOGGLES_" + System.currentTimeMillis());
            
            if (!QModes.standAlone) {
                // NEXT PART ONLY NEEDED IF NOT DISPLAYED MODAL
                GraffitiSingleton.getInstance().getMainFrame().showMesssage
                    ("Quoggles finished", MessageListener.INFO, 1000);
                
                // setActiveSelection calls selectionChanged twice, avoid calling
                // checkSelectionInputBox twice
                QModes.dontCheck = true;
                GraffitiSingleton.getInstance().getMainFrame().getActiveEditorSession()
                    .getSelectionModel().setActiveSelection(resSelection);
                QModes.dontCheck = false;
                qMain.checkSelectionInputBox();
            }
        }
        
        } catch (QueryExecutionException qee) {
            this.queryResult = null;
            throw qee;
        }
//        } finally {
//            // re-add all temporarily removed edges
//            Graph queryGraph = qMain.getQueryGraph();
//            for (Iterator it = tempKilledEdges.iterator(); it.hasNext();) {
//                Edge edge = (Edge)it.next();
//                queryGraph.addEdgeCopy(edge, edge.getSource(), edge.getTarget());
//            }
//
//            //Util.resetAllBoxesFrom(qGraph, sourceNodes);
//        }
        
        // requestFocus etc. do not seem to work; this does:
        qMain.getDialog().setVisible(true);

        this.queryResult = result;
        return this.queryResult;
    }

    /**
     * Called by <code>mouseMoved</code> when in <b>QModes.followMode</b>.
     * Executes query till the box nearest to the given point.
     * 
     * @param mousePnt
     */
    public void runQueryInFollowMode(Point mousePnt) {
        System.out.println("follow mode currently under construction :-)");
////        // find box where mouse pointer is nearest
////        double ndist = Double.POSITIVE_INFINITY;
////        BoxRepresentation nearestRep = null;
////        Graph queryGraph = qMain.getQueryGraph();
////        for (Iterator it = queryGraph.getNodesIterator(); it.hasNext();) {
////            Node node = (Node)it.next();
////            BoxRepresentation boxRep = Util.getBox(node)
////                .getGraphicalRepresentation().getRepresentation();
////            Rectangle bounds = boxRep.getBounds();
////            double x = bounds.getCenterX();
////            double y = bounds.getCenterY();
////            if (boxRep instanceof Input_Box.InputBox_Rep) {
////                x = 0;
////            }
////            double dist = Math.abs((x - mousePnt.x)) +
////                Math.abs((y - mousePnt.y));
////            if (dist < ndist) {
////                ndist = dist;
////                nearestRep = boxRep;
////            }
////        }
////        IBox lastBox = nearestRep.getIBoxRepresentation().getIBox();
////        Node lastNode = lastBox.getNode();
////                
////        // only recalculate when mouse points to a NEW box rep
////        if (nearestRep != lastFollowed) {
////            lastFollowed = nearestRep;
////
////            // remove virtual OutputBoxes
////            if (addedOutputBoxes == null) addedOutputBoxes = new ArrayList(0);
////            for (Iterator it = addedOutputBoxes.iterator(); it.hasNext();) {
////                queryGraph.deleteNode((Node)it.next());
////            }
////            addedOutputBoxes.clear();
////                    
////            // reinsert nodes & edges to reconnect queryGraph
////            Map nodeOrigCopyMap = new HashMap();
////            for (Iterator it = killedNodes.iterator(); it.hasNext();) {
////                Node node = (Node)it.next();
////                nodeOrigCopyMap.put(node, queryGraph.addNodeCopy(node));
////            }
////            killedNodes.clear();
////            for (Iterator it = killedEdges.iterator(); it.hasNext();) {
////                Edge edge = (Edge)it.next();
////                Node source = (Node)nodeOrigCopyMap.get(edge.getSource());
////                if (source == null) {
////                    source = edge.getSource();
////                }
////                Node target = (Node)nodeOrigCopyMap.get(edge.getTarget());
////                if (target == null) {
////                    target = edge.getTarget();
////                }
////                try {
////                    Edge tmpedge = queryGraph.addEdgeCopy(edge, source, target);
////                    try {
////                        tmpedge.getInteger(INPUT_INDEX_ID);
////                    } catch (AttributeNotFoundException anfe) {
////                        System.out.println("badbadbad");
////                    }
////                } catch (GraphElementNotFoundException genfe) {
////                    // prob. bc selection InputBox was deleted meanwhile
////                    System.out.println("GENFE in runQueryInFollowMode ...");
////                }
////            }
////            killedEdges.clear();
////            nodeOrigCopyMap = null;
////            // now queryGraph should be as before, everything undone
////
////            // separate query graph after lastBox
////            for (Iterator it = lastNode.getDirectedOutEdgesIterator(); it.hasNext();) {
////                Edge edge = (Edge)it.next();
////                killedEdges.add(edge);
////                queryGraph.deleteEdge(edge);
////            }
////
////            allPreNodes = new LinkedList();
////            Collection conInputNodes = getSourcesFrom(lastNode, allPreNodes);
////                
////            boolean outputBoxExists = false;
////            for (Iterator it = allPreNodes.iterator(); it.hasNext();) {
////                Node node = (Node)it.next();
////                if (Util.getBox(node) instanceof IOutputBox) {
////                    outputBoxExists = true;
////                    break;
////                }
////            }
////            
////            // add "virtual" OutputBox(es) after followed box if none exists
////            if (!outputBoxExists) {            
////                addedOutputBoxes = new ArrayList(3);
////                for (int i = 0; i < lastBox.getNumberOfOutputs(); i++) {
////                    Node outputBoxNode = queryGraph.addNode();
////                    Edge edge = queryGraph.addEdge(lastNode, outputBoxNode, true);
////                    edge.addInteger("", INPUT_INDEX_ID, 0);
////                    edge.addInteger("", OUTPUT_INDEX_ID, i);
////                    IBox outputBox = new NormalOutput_Box();
////                    Attribute boxAttr = new BoxAttribute
////                        (IBoxConstants.BOX_ATTR_ID, outputBox);
////                    outputBoxNode.addAttribute(boxAttr, "");
////                    addedOutputBoxes.add(outputBoxNode);
////                }
////            }
////                
////            // unmark all box reps
////            for (Iterator it = boxReps.iterator(); it.hasNext(); ) {
////                unMarkBoxRep((BoxRepresentation)it.next());
////            }
////            // mark included boxes
////            for (Iterator it = allPreNodes.iterator(); it.hasNext(); ) {
////                Node node = (Node)it.next();
////                BoxRepresentation boxRep = Util.getBox(node)
////                    .getGraphicalRepresentation().getRepresentation();
////                markBoxRep(boxRep);
////            }                
////
////            // remove all edges from InputBoxes from queryGraph that are 
////            // NOT connected with the followed box
////            for (Iterator it = inputNodes.listIterator(); it.hasNext();) {
////                Node sourceNode = (Node)it.next();
////                if (conInputNodes.contains(sourceNode)) {
////                    // sourceNode is connected => dont delete
////                    continue;
////                }
////                // save and delete all edges
////                for (Iterator eIt = sourceNode.getEdgesIterator(); eIt.hasNext();) {
////                    Edge edge = (Edge)eIt.next();
////                    killedEdges.add(edge);
////                    queryGraph.deleteEdge(edge);
////                }
////            }
////                
////                
////            try {
////                // now execute query (the part relevant for the followed box)
////                // TODO check "new ArrayList()"
////                runQuery(queryGraph, inputNodes, 
////                    false, new ArrayList(), new Stack());
////            } catch (QueryExecutionException e1) {
////                // TODO Auto-generated catch block
////                JOptionPane.showMessageDialog(null, 
////                    e1.getLocalizedMessage(),
////                    "Error:", JOptionPane.ERROR_MESSAGE);
////                e1.printStackTrace();
////            }
////        }
    }

}
