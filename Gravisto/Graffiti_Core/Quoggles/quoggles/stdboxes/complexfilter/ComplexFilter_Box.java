package quoggles.stdboxes.complexfilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.util.Pair;

import quoggles.QAssign;
import quoggles.QRunQuery;
import quoggles.auxiliary.RunQuery;
import quoggles.auxiliary.Util;
import quoggles.boxes.Box;
import quoggles.boxes.IBox;
import quoggles.constants.IBoxConstants;
import quoggles.constants.ITypeConstants;
import quoggles.constants.QConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.querygraph.BoxAttribute;
import quoggles.representation.IBoxRepresentation;
import quoggles.stdboxes.output.BoolPredicateEnd_Box;

/**
 * Input: one or several objects<p>
 * Output:
 * <ol>
 *   <li><code>null</code> if the input has been <code>null</code></li>
 *   <li>only those elements from the input for which holds: When set as input
 * to the sub query starting at the second output of this box, this sub query
 * yields a result that can be interpreted as <code>true</code>.</li>
 *   <li>every element from the input (or the input itself, if it is not a
 * collection) is used as the input to the sub query starting at this 
 * output.</li>
 * </ol>
 * 
 * The sub query rooted at the second output of this box is searched for sinks
 * (i.e. boxes that have free outputs). There must not be any sinks. If there
 * is exactly one sink found (with only one free output) a 
 * <code>BoolPredicateEnd_Box</code> is implicitly added. Otherwise, an error is 
 * generated.<p>
 * The output is a collection of input elements for which the predicate holds
 * (i.e. the sub query yields a <code>true</code> result).<p>
 * If the resulting list contains only one element, this element is returned
 * (not a one-element list).
 */
public class ComplexFilter_Box extends Box {
    
    /** The input collection */
    private Collection inputCol = null;
    
    /** Only set if input is not a collection */
    private Object singleInput = null;
    
    /** Node representing the first box of the predicate of this filter */
    private Node firstPredNode = null;
    
    private RunQuery queryRunner = null;
    
    /** @see setnextinputIndex(int) */
    private int nextInIndex = -1;
    
    /** Holds additional output */
    private List resultRows = new ArrayList(0);
    
    private List resultRowNrs = new ArrayList(0);
    
    /** Specifies whether or not a predicate exists */
    private boolean predicateExists = true;
    
    /**
     * If this number is greater than 0, the currently tested object has passed
     * the test (predicate yielded true for this).
     */
    private int currentTestCount = 0;
    
    /**
     * The current result table.
     */
    private List currentResult = null;
    
    /**
     * The nodes that still have to be processed.
     */
    private Stack currentNodesTodo = null;
    
    private Parameter orderParam = new IntegerParameter
        (1, "order", "Specifies the order in which the complex filter" +
        " boxes are executed.");
    
    
    /**
     * Default constructor.
     */
    public ComplexFilter_Box() {
        parameters = new Parameter[]{ orderParam };
    }
    
    /**
     * An entry in the list is created for each <code>IOutput_Box</code> that
     * is found in the predicate query.
     * 
     * @return
     */
    public List getResult() {
        return resultRows;
    }
    
    /**
     * An entry in the list is created for each <code>IOutput_Box</code> that
     * is found in the predicate query. It is the number of the row where the
     * respective output should be put.
     * 
     * @return
     * @see getResult()
     */
    public List getResultRowNumbers() {
        return resultRowNrs;
    }
    
    /**
     * @see quoggles.boxes.IBox#neeedsQueryRunner()
     */
    public boolean needsQueryRunner() {
        return true;
    }

    /**
     * @see quoggles.boxes.IBox#setQueryRunner(quoggles.auxiliary.RunQuery)
     */
    public void setQueryRunner(RunQuery qr) {
        super.setQueryRunner(qr);
        queryRunner = qr;
    }

//    /**
//     * Specifies the first node of the sub query graph rooted at the second
//     * output of this box.
//     * 
//     * @param node the first node of the sub query graph rooted at the second
//     * output of this box
//     */
//    public void setFirstPredicateNode(Node node) {
//        firstPredNode = node;
//    }
    
//    /**
//     * Specifies whether or not this box actually has a predicate or not.<p>
//     * The default value is <code>true</code>.
//     * 
//     * @param pred <code>true</code> if the filter has a predicate
//     */
//    public void setNoPredicate(boolean pred) {
//        predicateExists = !pred;
//    }
    
//    /**
//     * Set the index of the input of the box that is associated with the first
//     * predicate node.
//     * 
//     * @param inIndex index of the input of the box that is associated with the
//     * first predicate node
//     */
//    public void setNextInputIndex(int inIndex) {
//        nextInIndex = inIndex;
//    }
    
    /**
     * If a BoolPredicateEnd_Box finds out that the test succeded, it calls
     * this method of all its registered complex filters. This indicates that
     * the currently tested object will be added to th eoutput of this filter.
     * Calling this method several times will cause the filter to add the 
     * element several times.
     */
    public void currentTestSucceded() {
        currentTestCount++;
    }
    
    public void setNotExecuted() {
        boxExecuted = false;
    }
    
    /**
     * @see quoggles.boxes.IBox#reset()
     */
    public void reset() {
        super.reset();
        firstPredNode = null;
        inputCol = null;
        queryRunner = null;
        nextInIndex = -1;
        resultRows.clear();
        resultRowNrs.clear();
        currentTestCount = 0;
    }

    /**
     * Is called before an exception is throws. Ensures that the query is in
     * a valid state.
     * 
     * @param n the node that holds the <code>BoolPredicateEnd_Box</code>
     */
    private static void beforeException(Node n) {
        if (n != null) {
            System.out.println("removing virtual BoolPredicateEnd_Box");
            IBox box = Util.getBox(n);
            QAssign.assignBEPRow(((IntegerParameter)box.getParameters()[0])
                .getInteger().intValue(), false);
            n.getGraph().deleteNode(n);
        }
    }
    
    /**
     * Assures that there is exactly one <code>BoolPredicateEnd_Box</code> in 
     * the sub query. The node containing this box is the first entry in the 
     * <code>Pair</code> that is returned. The second entry indicates whether
     * or not a new boolean predicate end box has been added automatically.
     * 
     * @param fstPredNode the node where the predicate starts
     * @param thisBox the box for which the search is started. Used to prevent
     * that the predicates of nested complex filter boxes are checked in this
     * call already.
     */
    public static Pair checkSinks(Node fstPredNode, IBox thisBox) 
        throws QueryExecutionException {

        Node outputNode = null;
        IBox outputBox = null;
        int otherSinks = 0;
        Node sinkNode = null;
        boolean newAdded = false;
        boolean excIfNotOneBPB = false;
        String excIfNotOneBPB_msg = "";
        Graph subQGraph = fstPredNode.getGraph();
        Collection visitedNodes = new ArrayList(subQGraph.getNumberOfNodes());

        Stack copyNodes = new Stack();
        copyNodes.push(fstPredNode);

//        for (Iterator it = subQGraph.getNodesIterator(); it.hasNext();) {
//            Node node = (Node)it.next();
        // DFS for sinks in relevant sub query graph part
        while (!copyNodes.isEmpty()) {
            Node node = (Node)copyNodes.pop();
            visitedNodes.add(node);
            IBox box = Util.getBox(node);
            int outDeg = node.getOutDegree();
            int boxOutputs = box.getNumberOfOutputs();
            if (box instanceof ComplexFilter_Box) {
                // predicates of complex filter boxes do not count
                boxOutputs--;
            }
            if (outDeg == 0 || outDeg < boxOutputs) {
                if (box instanceof BoolPredicateEnd_Box) {
                    if (outputNode != null) {
                        // have at least two BoolPredicateEnd_Box-es
                        beforeException(outputNode);
                        throw new QueryExecutionException("The" +
                            " predicate query must not have more than one" +
                            " BoolPredicateEnd_Box as sink.\n" +
                            " Use a BooleanOp_Box or SetOperation_Box" +
                            " to concatenate several sinks. (" + 
                            Util.getBox(outputNode).getId() + " and " +
                            box.getId() + ")");
                    }
                    outputNode = node;
                } else {
                    if (box.getNumberOfOutputs() - outDeg > 1) {
                        // one box has several empty outputs
                        excIfNotOneBPB = true;
                        excIfNotOneBPB_msg = "The" +
                          " predicate query must not have more than one" +
                          " sinks.\n" +
                          " Use a BooleanOp_Box or SetOperation_Box" +
                          " to concatenate several sinks. (" + 
                          box.getId() + 
                          " has several sinks)";
////                        beforeException(outputNode);
////                        throw new QueryExecutionException("The" +
////                            " predicate query must not have more than one" +
////                            " sinks." +
////                            " Use a BooleanOp_Box or SetOperation_Box" +
////                            " to concatenate several sinks. (" + 
////                            box.getId() + 
////                            " has several sinks)");
                    } else if (box.getNumberOfOutputs() > 0) {
                        // this box has exactly one unoccupied output
                        otherSinks++;
                        sinkNode = node;
                    }
                    // add out-neighbors (if any) to stack
                    Iterator it = node.getDirectedOutEdgesIterator();
                    while (it.hasNext()) {
                        Edge edge = (Edge)it.next();
                        Node targetNode = edge.getTarget();
                        if (!copyNodes.contains(targetNode) && 
                            !visitedNodes.contains(targetNode)) {
    
                            copyNodes.push(targetNode);
                        }
                    }
                }
            } else {
                // add out-neighbors to stack
                if (box != thisBox && box instanceof ComplexFilter_Box) {
                   // false if complex filter has no out edges except the one
                    // to its predicate
                    boolean found = false;
                    Iterator it = node.getDirectedOutEdgesIterator();
                    while (it.hasNext()) {
                        Edge edge = (Edge)it.next();
                        int index = Util.getIOIndex(edge, true);
                        if (index != 1) {
                            found = true;
                            // do not check predicate of the complex filter box
                            Node targetNode = edge.getTarget();
                            if (!copyNodes.contains(targetNode) && 
                                !visitedNodes.contains(targetNode)) {
        
                                copyNodes.push(targetNode);
                            }
                        }
                    }
                    if (!found) {
                        otherSinks++;
                        sinkNode = node;
                    }
                } else {
                    Iterator it = node.getDirectedOutEdgesIterator();
                    while (it.hasNext()) {
                        Edge edge = (Edge)it.next();
                        Node targetNode = edge.getTarget();
                        if (!copyNodes.contains(targetNode) && 
                            !visitedNodes.contains(targetNode)) {
    
                            copyNodes.push(targetNode);
                        }
                    }
                }
            }
        }
        
        if (excIfNotOneBPB && outputNode == null) {
            throw new QueryExecutionException(excIfNotOneBPB_msg);
        }
        
        if (outputNode == null) {
            if (otherSinks == 1) {
                // if outdegree == 0, cannot add box;
                // TODO check if that can be handled better;
                // must hope that it is a valid predicate;
                // is found out at runtime
                if (Util.getBox(sinkNode).getNumberOfOutputs() != 0) {
                    // can unambiguously add a virtual BoolPredicateEnd_Box
                    System.out.println("adding a BoolPredicateEnd_Box");
                    outputBox = new BoolPredicateEnd_Box();
                    outputNode = subQGraph.addNode();
                    outputBox.setNode(outputNode);
                    Attribute boxAttr = 
                        new BoxAttribute(IBoxConstants.BOX_ATTR_ID, outputBox);
                    outputNode.addAttribute(boxAttr, "");
                    Edge edge = subQGraph.addEdge(sinkNode, outputNode, true);
                    edge.addInteger("", QConstants.INPUT_INDEX_ID, 0);
                    edge.addInteger("", QConstants.OUTPUT_INDEX_ID, 0);
                    newAdded = true;
                } else {
                    // last pred box no outputs, but no BoolPredicateEnd_Box
                    System.out.println("last predicate box no outputs ...");
                    outputNode = 
                        Util.findMaybeNestedPredicateEnd(sinkNode, false);
                    if (outputNode == null) {
                        beforeException(outputNode);
                        throw new QueryExecutionException("Could not find" +
                            " a BoolPredicateEnd_Box in the predicate part" +
                            " (not even hidden inside SubQuery_Box-es).");
                    }
                }
            } else {
                beforeException(outputNode);
                throw new QueryExecutionException("The" +
                    " predicate query must have exactly one" +
                    " (BoolPredicateEnd_Box" +
                    "  as) sink (not " + Util.getBox(sinkNode).getId() +
                    " ...).");
            }
        }

        return new Pair(outputNode, new Boolean(newAdded));
    }
    
    /**
     * See <code>interpretAsBoolean</code> for a definition of what it means
     * that a sub query evaluates to <code>true</code>.
     * 
     * @see quoggles.boxes.IBox#execute()
     * @see quoggles.auxialiary.Util#interpretAsBoolean(Object)
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        if (singleInput != null) {
            inputCol = new ArrayList(1);
            inputCol.add(inputs[0]);
        }
        
//        if (inputCol == null) {
//            outputs = new Object[]{ null };
//            return;
//        }

        List outputCol = new LinkedList();
        outputs = new Object[]{ outputCol };
        
        
        firstPredNode = null;
        predicateExists = false;
        Node nd = getNode();
        for (Iterator it = nd.getAllOutEdges().iterator(); it.hasNext();) {
            Edge edge = (Edge)it.next();
            int index = Util.getIOIndex(edge, true);
            if (index == 1) {
                firstPredNode = edge.getTarget();
                predicateExists = true;
                nextInIndex = Util.getIOIndex(edge, false);
            }
        }

        
        if (firstPredNode == null && predicateExists) {
            throw new QueryExecutionException(getId() +
                ": setFirstPredicateNode(...)" +
                " must be called prior to execution or" +
                "setNoPredicate(false) must be called.");
        }
        if (nextInIndex < 0 && predicateExists) {
            throw new QueryExecutionException(getId() +
                ": setNextInputIndex(int)" +
                " must be called prior to execution with a positive integer.");
        }

        if (!predicateExists) {
            if (Util.interpretAsBoolean(inputs[0])) {
                // whole input
                outputs[0] = inputs[0];
            } else {
                // empty list
                outputs[0] = outputCol;
            }
            return;
        }

        Graph subQGraph = firstPredNode.getGraph();

        if (queryRunner == null) {
            throw new QueryExecutionException(getId() + 
                ": setQueryRunner(...)" +
                " must be called prior to execution.");
        }
            
//        int resultRowNr = Quoggles.getNextFreeRowNumber();
//        if (resultRowNr < 0) {
//            throw new QueryExecutionException(getId() +
//                ": Not more than " + Quoggles.assignedRows.length + 
//                " output boxes allowed in one query." +
//                " An output box is implicitly added if the sub query" +
//                " for the predicate is not closed with one." +
//                " Please remove one of the boxes or the query might yield" +
//                " unexpected results.");
//        }
            
        // see if there is an output box at the end
        Pair pair = checkSinks(firstPredNode, this);

        Node outputNode = (Node)pair.getFst();
        boolean newAdded = ((Boolean)pair.getSnd()).booleanValue();
        
        BoolPredicateEnd_Box bpeb = 
            (BoolPredicateEnd_Box)Util.getBox(outputNode);
        bpeb.registerComplexFilter(this);

        // must call beforeException(Node) before throwing an exception
        try {

//        int resultRowNr = ((IntegerParameter)Util.getBox(outputNode)
//            .getParameters()[0]).getInteger().intValue();
            
        Map objCount = new LinkedHashMap(inputCol.size());
        
        for (Iterator mainIt = inputCol.iterator(); mainIt.hasNext();) {
            Object obj = mainIt.next();
            currentTestCount = 0;
            //if (0==0) return;
            // (1) execute query part beginning at output #2 using obj
            
            // feed sub query with input (obj)
            IBox firstBox = Util.getBox(firstPredNode);
            firstBox.setInputAt(obj, nextInIndex);
                  
            // run sub query
            Collection sourceNodes = new ArrayList(1);
            sourceNodes.add(firstPredNode);
            int saveOrderNr = QRunQuery.minOrderNr;
            
//            Collection executedNodes = new LinkedList();
            // copy current stack
////            Stack oldStackNodes = new Stack();
////            while (!currentNodesTodo.isEmpty()) {
////                oldStackNodes.push(currentNodesTodo.pop());
////            }
////            //currentNodesTodo.clear();
            
////            currentResult = queryRunner.runQuery(subQGraph, sourceNodes, 
////                true, currentResult, currentNodesTodo, executedNodes);


            currentResult = queryRunner.runQuery(subQGraph, sourceNodes, 
                true, currentResult, new Stack());

            for (Iterator it = currentNodesTodo.iterator(); it.hasNext();) {
                if (Util.getBox((Node)it.next()).hasBeenExecuted()) {
                //if (executedNodes.contains(it.next())) {
                    it.remove();
                }
            }

//            currentResult = queryRunner.runQuery(subQGraph, sourceNodes, 
//                true, currentResult, currentNodesTodo, executedNodes);


////            // re-add old elements if they have not already been executed
////            // in the sub query
////            while (!oldStackNodes.isEmpty()) {
////                Object node = oldStackNodes.pop();
////                if (!executedNodes.contains(node)) {
////                    currentNodesTodo.push(node);
////                }
////            }

            QRunQuery.minOrderNr = saveOrderNr;
            
            Util.resetAllBoxesFrom(subQGraph, sourceNodes);

            // (2) if execution evaluates to "true", add a new entry for obj
            
////            Boolean resultRow = null;
////            try {
////                resultRow = (Boolean)((List)rows).get(resultRowNr);
////            } catch (Exception e) {
////                throw new QueryExecutionException(getId() + 
////                    ": Predicate query could not be evaluated to a single" +
////                    "Boolean value ... ");
////            }
////            if (resultRow != null) {
////                if (resultRow.booleanValue()) {
////                    outputCol.add(obj);
////                }
            
            Pair uniquePair = new Pair(obj, Util.UNIQUE);
            objCount.put(uniquePair, new Integer(currentTestCount));
            //for (int i = 1; i <= currentTestCount; i++) {
            //    outputCol.add(obj);
            //}
//            if (currentTestCount > 0 && rows.size() > 1) {
//                // (3) if any output boxes are present in the 
//                // predicate, save the results
//                for (int i = 0; i < rows.size(); i++) {
//                    Object ithRow = rows.get(i);
//                    if (i != resultRowNr && ithRow != null) {
//                        resultRows.add(ithRow);
//                        resultRowNrs.add(new Integer(i));
//                    }
//                }
//            }
        }
        
        
        boolean several = false;
        List toAdd = new LinkedList();
        for (Iterator it = objCount.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            int count = ((Integer)entry.getValue()).intValue();
            if (count > 1) {
                several = true;
                break;
            } else if (count == 1) {
                toAdd.add(((Pair)entry.getKey()).getFst());
            }
        }
        
        if (several) {
            for (Iterator it = objCount.entrySet().iterator(); it.hasNext();) {
                Map.Entry entry = (Map.Entry)it.next();
                int cnt = ((Integer)entry.getValue()).intValue();
                ArrayList al = new ArrayList(cnt);
                outputCol.add(al);
                for (int i = 0; i < cnt; i++) {
                    al.add(((Pair)entry.getKey()).getFst());
                }
            }
        } else {
//            outputCol.addAll(objCount.keySet());
            outputCol.addAll(toAdd);
        }
        
        
        
        
        
        
        ((BoolPredicateEnd_Box)Util.getBox(outputNode)).removeRegistered(this);
        
        if (newAdded) {
            // free row
            IBox box = Util.getBox(outputNode);
            QAssign.assignBEPRow(((IntegerParameter)box.getParameters()[0])
                .getInteger().intValue(), false);
            subQGraph.deleteNode(outputNode);
            System.out.println("removing virtual BoolPredicateEnd_Box");
        }
        
//        Object prevObj = null;
//        try {
//            prevObj = outputs[0];
//        } catch (Exception ex) {
//            // ignore
//        }
        
//        Object newObj = null;
        
        //doSetOutput(outputCol);
        if (singleInput != null) {
            if (outputCol.isEmpty()) {
                outputs = new Object[]{ null };
            } else  if (outputCol.size() == 1) {
                outputs = new Object[]{ outputCol.get(0) };
            } else {
                outputs = new Collection[]{ outputCol };
            }
        } else {
            outputs = new Collection[]{ outputCol };
        }
        
//        if (outputCol.size() == 1) {
//            outputs = new Object[]{ outputCol.get(0) };
//            //newObj = outputCol.get(0);
//        } else {
//            outputs = new List[]{ outputCol };
//            //newObj = outputCol;
//        }
        
//        if (prevObj == null) {
//            outputs = new Object[]{ newObj };
//        } else {
//            if ()
//        }
        
        // reset predicate sub query
        Stack copyNodes = new Stack();
        Stack indices = new Stack();
        copyNodes.push(firstPredNode);
        // find out index from this box to first predicate node
        Iterator it = firstPredNode.getDirectedInEdgesIterator();
        while (it.hasNext()) {
            Edge edge = (Edge)it.next();
            Node n = edge.getSource();
            IBox nBox = Util.getBox(n);
            if (nBox == this) {
                indices.push(new Integer(Util.getIOIndex(edge, false)));
                break;
            }
        }
        
        while (!copyNodes.isEmpty()) {
            Node node = (Node)copyNodes.pop();
            int resetAt = ((Integer)indices.pop()).intValue();
            Util.getBox(node).reset(resetAt);

            it = node.getDirectedOutEdgesIterator();
            while (it.hasNext()) {
                Edge edge = (Edge)it.next();
                int index = Util.getIOIndex(edge, false);
                Node targetNode = edge.getTarget();
                if (!copyNodes.contains(targetNode)) {
                    copyNodes.push(targetNode);
                    indices.push(new Integer(index));
                }
            }
        }

//        // specify that the sub query should not be executed any more
//        Stack copyNodes = new Stack();
//        copyNodes.push(firstPredNode);
//        while (!copyNodes.isEmpty()) {
//            Node node = (Node)copyNodes.pop();
//            Util.getBox(node).setIgnoreBox(true);
//            for (Iterator it = node.getDirectedOutEdgesIterator(); it.hasNext();) {
//                Edge edge = (Edge)it.next();
//                Node targetNode = edge.getTarget();
//                if (!copyNodes.contains(targetNode)) {
//                    copyNodes.push(targetNode);
//                }
//            }
//        }

        } catch (QueryExecutionException ex) {
            if (newAdded) {
                // remove new virtual box
                beforeException(outputNode);
            }
            throw ex;
        }
    }

    /**
     * @see quoggles.boxes.IBox#setCurrentResult(java.util.List)
     */
    public void setCurrentResult(List res) {
        super.setCurrentResult(res);
        currentResult = res;
    }

    /**
     * @see quoggles.boxes.IBox#setCurrentNodesTodo(java.util.Stack)
     */
    public void setCurrentNodesTodo(Stack nodesTodo) {
        super.setCurrentNodesTodo(nodesTodo);
        currentNodesTodo = nodesTodo;
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof ComplexFilter_Rep)) {
            iBoxGRep = new ComplexFilter_Rep(this);
        }
        return iBoxGRep;
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return new int[]{ 
            ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION };
    }

    /**
     * Returns 2.
     * 
     * @see quoggles.boxes.IBox#getNumberOfOutputs()
     */
    public int getNumberOfOutputs() {
        return 2;
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[]{ 
            ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION,
            ITypeConstants.ONEOBJECT };
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
            singleInput = inputs[0];
//            inputCol = new ArrayList(1);
//            inputCol.add(inputs[0]);
        }
    }
}
