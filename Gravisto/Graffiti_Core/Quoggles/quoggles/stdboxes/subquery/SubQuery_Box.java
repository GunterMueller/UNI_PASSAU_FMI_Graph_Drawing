package quoggles.stdboxes.subquery;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.ObjectParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.util.Pair;

import quoggles.QAssign;
import quoggles.auxiliary.FileUtil;
import quoggles.auxiliary.RunQuery;
import quoggles.auxiliary.Util;
import quoggles.boxes.Box;
import quoggles.boxes.IBox;
import quoggles.boxes.IOutputBox;
import quoggles.constants.ITypeConstants;
import quoggles.constants.QConstants;
import quoggles.exceptions.InvalidParameterException;
import quoggles.exceptions.LoadFailedException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;
import quoggles.stdboxes.output.NormalOutput_Box;

/**
 * Input: depends on the loaded sub query<p>
 * Output: depends on the loaded sub query<p>
 * 
 * Allows specification of a file that contains a saved query. The box then
 * represents this whole loaded query. Parameters of the query cannot be 
 * changed.
 */
public class SubQuery_Box extends Box {

    StringParameter fileNameParam = new StringParameter(
        "...", "file name", "The name of the file that should be loaded.");
        
    ObjectParameter graphParam = new ObjectParameter("graph",
        "The graph that represents the sub query of this box.");
    
    private int[] inputTypes = new int[]{ ITypeConstants.GENERAL };
    
    private int[] outputTypes = new int[]{ ITypeConstants.GENERAL };

    /** Graüh holding the sub query */
    private Graph subQueryGraph = null;
    
    /**
     * A list of <code>Pair</code>s (of boxes that need inputs and the index
     * where the box needs the input).
     */
    private List inputBoxAndNumber = new LinkedList();
    
    /**
     * A list of <code>Pair</code>s (of boxes that yield outputs and the index
     * where the box's output is).
     */
    private List outputBoxAndNumber = new LinkedList();
    
    private RunQuery queryRunner;
    
    /**
     * The current result table.
     */
    private List currentResult = null;
    
    
    /**
     * Constructs the box.
     */
    public SubQuery_Box() {
        super();
        parameters = new Parameter[]{ fileNameParam, graphParam };
    }


    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();

        if (subQueryGraph == null) {
            throw new InvalidParameterException("Must choose a file first.");
        }
        
        Collection rowsToReset = new LinkedList();
        
        outputBoxAndNumber.clear();
        ArrayList outTypes = new ArrayList();

        // reset all boxes in the sub query
        // (not done by runQuery since the method used there does not reset
        // nodes given to runQuery as second parameter)
        for (Iterator it = subQueryGraph.getNodesIterator(); it.hasNext();) {
            Node node = (Node)it.next();
            IBox box = Util.getBox(node);
            box.reset();
            
            // since assignments of rows have been ignored for all boxes within
            // a sub query, we have to assign them free rows now
//            if (box instanceof IOutputBox) {
//                int newPos = QAssign.getNextFreeRowNumber();
//                QAssign.assignRow(newPos, true);
//                Parameter[] params = box.getParameters();
//                ((OptionParameter)params[0]).setValue(
//                    ((OptionParameter)params[0]).getOptions()[newPos]);
//                box.setParameters(params, false);
//                rowsToReset.add(new Integer(newPos));
//                
//                //System.out.println("assign row " + newPos + " in sub query");
//            }





            // add this box's free outputs
            int overflowCnt = 0;
            if (box instanceof IOutputBox) {
                CollectionAttribute ioNrsAttr = null;
                try {
                    ioNrsAttr = (CollectionAttribute)
                        node.getAttribute(QConstants.MARKED_OUTPUT_NRS_ID);
                } catch (AttributeNotFoundException anfe) {
                    ioNrsAttr = new HashMapAttribute
                        (QConstants.MARKED_OUTPUT_NRS_ID);
                    node.addAttribute(ioNrsAttr, "");
                }

                Map indexNrMap = ioNrsAttr.getCollection();
                int outNr = 0;
                if (indexNrMap.get("o" + String.valueOf(0)) == null) {
                    // no order number saved for this ...
                    // TODO remove this restriction
                    outNr = 20 + overflowCnt++;
                } else {
                    outNr = ((IntegerAttribute)indexNrMap
                        .get("o" + String.valueOf(0))).getInteger() - 1;
                }

                Util.ensureSize(outputBoxAndNumber, outNr);
                outputBoxAndNumber.set(outNr,
                    new Pair(box, new Integer(0)));
                        
                Util.ensureSize(outTypes, outNr);
                outTypes.set(outNr, new Integer(ITypeConstants.GENERAL));
                
                ((NormalOutput_Box)box).setLastRowNumber(-1);
                
                continue;
            }            
            
//            int boxIONumber = box.getNumberOfOutputs();
//            if (node.getOutDegree() < boxIONumber) {
//                CollectionAttribute ioNrsAttr = null;
//                try {
//                    ioNrsAttr = (CollectionAttribute)
//                        node.getAttribute(QConstants.MARKED_OUTPUT_NRS_ID);
//                } catch (AttributeNotFoundException anfe) {
//                    ioNrsAttr = new HashMapAttribute
//                        (QConstants.MARKED_OUTPUT_NRS_ID);
//                    node.addAttribute(ioNrsAttr, "");
//                }
//                Map indexNrMap = ioNrsAttr.getCollection();
//                
//                boolean[] free = new boolean[boxIONumber];
//                for (int i = 0; i < free.length; i++) {
//                    free[i] = true;
//                }
//                Iterator eIt = node.getDirectedOutEdgesIterator();
//                while (eIt.hasNext()) {
//                    Edge edge = (Edge)eIt.next();
//                    free[Util.getIOIndex(edge, true)] = false;
//                }
//                // add this box's free outputs
//                int[] types = box.getOutputTypes();
//                for (int i = 0; i < free.length; i++) {
//                    if (free[i]) {
//                        int outNr = 0;
//                        if (indexNrMap.get("o" + String.valueOf(i)) == null) {
//                            // no order number saved for this ...
//                            // TODO remove this restriction
//                            outNr = 20 + overflowCnt++;
//                        } else {
//                            outNr = ((IntegerAttribute)indexNrMap
//                                .get("o" + String.valueOf(i))).getInteger() - 1;
//                        }
//
//                        Util.ensureSize(outputBoxAndNumber, outNr);
//                        outputBoxAndNumber.set(outNr,
//                            new Pair(box, new Integer(i)));
//                        
//                        Util.ensureSize(outTypes, outNr);
//                        outTypes.set(outNr, new Integer(types[i]));
//                    }
//                }
//            }
        }

//        if (box instanceof IOutputBox) {
//            int newPos = QAssign.getNextFreeRowNumber();
//            QAssign.assignRow(newPos, true);
//            Parameter[] params = box.getParameters();
//            ((OptionParameter)params[0]).setValue(
//                ((OptionParameter)params[0]).getOptions()[newPos]);
//            box.setParameters(params, false);
//            rowsToReset.add(new Integer(newPos));
//                
//            //System.out.println("assign row " + newPos + " in sub query");
//        }

//        outputs = new Object[getOutputTypes().length];
        for (Iterator it = outputBoxAndNumber.iterator(); it.hasNext();) {
            Pair pair = (Pair)it.next();
            if (pair != null) {
                IBox box = (IBox)pair.getFst();
                if (box instanceof NormalOutput_Box) {
                    int newPos = QAssign.getNextFreeRowNumber();
    //                QAssign.assignRow(newPos, true);
                    Parameter[] params = box.getParameters();
                    ((OptionParameter)params[0]).setValue(
                        ((OptionParameter)params[0]).getOptions()[newPos]);
                    box.setParameters(params, false);
                    rowsToReset.add(new Integer(newPos));
                }
            }
        }












        // redirect "edges" to this box to boxes in sub query that need them
        // i.e. inputs to this box are inputs to the boxes in the sub query
        Collection sourceBoxes = new ArrayList(inputBoxAndNumber.size());
        int i = 0;
        for (Iterator it = inputBoxAndNumber.iterator(); it.hasNext();) {
            Pair pair = (Pair)it.next();
            if (pair != null) {
                IBox iBox = (IBox)pair.getFst();
                sourceBoxes.add(iBox);
                int iIndex = ((Integer)pair.getSnd()).intValue();
                iBox.setInputAt(inputs[i++], iIndex);
            }
        }
        
        // find nodes that represent the input boxes
        Collection sourceNodes = new ArrayList(sourceBoxes.size());
        for (Iterator it = sourceBoxes.iterator(); it.hasNext();) {
            IBox box = (IBox)it.next();
            sourceNodes.add(box.getNode());
        }

        assert (sourceBoxes.size() == sourceNodes.size());
        
        // run sub query
        try {
            currentResult =
                queryRunner.runQuery(subQueryGraph, sourceNodes, 
                    true, currentResult, new Stack());
        } catch (QueryExecutionException qee) {
            // reset all assigned rows within this sub query
            for (Iterator it = rowsToReset.iterator(); it.hasNext();) {
                QAssign.assignRow(((Integer)it.next()).intValue(), false);
            }
            throw qee;
        }
//////        currentResult =
//////           queryRunner.runQuery(subQueryGraph, subQueryGraph.getNodes(), 
//////            true, currentResult, currentNodesTodo, false);
        //System.out.println("SubQuery_Box: result = " + currentResult);
        
        
        // set outputs
        i = 0;
        outputs = new Object[getOutputTypes().length];
        for (Iterator it = outputBoxAndNumber.iterator(); it.hasNext();) {
            Pair pair = (Pair)it.next();
            if (pair != null) {
                IBox box = (IBox)pair.getFst();
//                int newPos = QAssign.getNextFreeRowNumber();
                if (box instanceof NormalOutput_Box) {
                    int rNr = ((OptionParameter)box.getParameters()[0])
                        .getOptionNr();
                    Object res = currentResult.get(rNr);
    //                if (res != null) {
                    outputs[i++] = res;
                    // remove outputs from within sub query from result
                    currentResult.set(rNr, null);
//                } else {
//                    int oIndex = ((Integer)pair.getSnd()).intValue();
//                    outputs[i++] = box.getOutputAt(oIndex);
                }
            }
        }
//        i = 0;
//        outputs = new Object[getOutputTypes().length];
//        for (Iterator it = outputBoxAndNumber.iterator(); it.hasNext();) {
//            Pair pair = (Pair)it.next();
//            if (pair != null) {
//                IBox oBox = (IBox)pair.getFst();
//                int oIndex = ((Integer)pair.getSnd()).intValue();
//                if (oBox instanceof IOutputBox) {
//                    int rNr = ((OptionParameter)oBox.getParameters()[0])
//                        .getOptionNr();
//                    Object res = currentResult.get(rNr);
//                    if (res != null) {
//                        outputs[i] = res;
//                        // remove outputs from within sub query from result
//                        currentResult.set(rNr, null);
//                    } else {
//                        outputs[i] = oBox.getOutputAt(oIndex);
//                    }
//                } else {
//                    outputs[i] = oBox.getOutputAt(oIndex);
//                }
//                i++;
//            }
//        }
        
        Util.resetAllBoxesFrom(subQueryGraph, sourceNodes);
        
        // reset all assigned rows within this sub query
        for (Iterator it = rowsToReset.iterator(); it.hasNext();) {
            QAssign.assignRow(((Integer)it.next()).intValue(), false);
        }
    }

    /**
     * @see quoggles.boxes.IBox#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] pars, boolean fromRep) {
        super.setParameters(pars, fromRep);
        
        boolean[] assignedRowsCopy = QAssign.getAssignedRowsCopy();
        boolean[] assignedBEPRowsCopy = QAssign.getAssignedBEPRowsCopy();
//////        QAssign.resetAssignedRows();

        String fileName = ((StringParameter)parameters[0]).getString();
        
        if (parameters.length < 2 || parameters[1] == null ||
            !(((ObjectParameter)parameters[1]).getValue() instanceof Graph)) {
            
            // happens after loading; load sub graph from fileName
            try {
                subQueryGraph = FileUtil.getQueryFromFile(new File(fileName), false);
            } catch (FileNotFoundException fnfe) {
                subQueryGraph = null;
            } catch (LoadFailedException lfe) {
                subQueryGraph = null;
            }
        } else {
            subQueryGraph = 
                (Graph)((ObjectParameter)parameters[1]).getValue();
        }

        if (subQueryGraph == null) {
            return;
        }
        
        // (1) find out numbers and types of input(s) / output(s)
        inputBoxAndNumber.clear();
        outputBoxAndNumber.clear();

        ArrayList inTypes = new ArrayList();
        ArrayList outTypes = new ArrayList();
        for (Iterator it = subQueryGraph.getNodesIterator(); it.hasNext();) {
            Node node = (Node)it.next();
            IBox iBox = Util.getBox(node);

            // add this box's free inputs
            int boxIONumber = iBox.getNumberOfInputs();
            if (node.getInDegree() < boxIONumber) {
                CollectionAttribute ioNrsAttr = null;
                try {
                    ioNrsAttr = (CollectionAttribute)
                        node.getAttribute(QConstants.MARKED_INPUT_NRS_ID);
                } catch (AttributeNotFoundException anfe) {
                    ioNrsAttr = new HashMapAttribute
                        (QConstants.MARKED_INPUT_NRS_ID);
                    node.addAttribute(ioNrsAttr, "");
                }
                Map indexNrMap = ioNrsAttr.getCollection();
                
                boolean[] free = new boolean[boxIONumber];
                for (int i = 0; i < free.length; i++) {
                    free[i] = true;
                }
                Iterator eIt = node.getDirectedInEdgesIterator();
                while (eIt.hasNext()) {
                    Edge edge = (Edge)eIt.next();
                    free[Util.getIOIndex(edge, false)] = false;
                }
                int[] types = iBox.getInputTypes();
                int overflowCnt = 0;
                for (int i = 0; i < free.length; i++) {
                    if (free[i]) {
                        int inNr = 0;
                        if (indexNrMap.get("i" + String.valueOf(i)) == null) {
                            // no order number saved for this ...
                            // TODO remove this restriction
                            inNr = 20 + overflowCnt++;
                        } else {
                            inNr = ((IntegerAttribute)indexNrMap
                                .get("i" + String.valueOf(i))).getInteger() - 1;
                        }
    
                        Util.ensureSize(inputBoxAndNumber, inNr);
                        inputBoxAndNumber.set(inNr,
                            new Pair(iBox, new Integer(i)));
                        
                        Util.ensureSize(inTypes, inNr);
                        inTypes.set(inNr, new Integer(types[i]));
                    }
                }
            }

            // add this box's free outputs
            int overflowCnt = 0;
            if (iBox instanceof IOutputBox) {
                CollectionAttribute ioNrsAttr = null;
                try {
                    ioNrsAttr = (CollectionAttribute)
                        node.getAttribute(QConstants.MARKED_OUTPUT_NRS_ID);
                } catch (AttributeNotFoundException anfe) {
                    ioNrsAttr = new HashMapAttribute
                        (QConstants.MARKED_OUTPUT_NRS_ID);
                    node.addAttribute(ioNrsAttr, "");
                }
                Map indexNrMap = ioNrsAttr.getCollection();
                int outNr = 0;
                if (indexNrMap.get("o" + String.valueOf(0)) == null) {
                    // no order number saved for this ...
                    // TODO remove this restriction
                    outNr = 20 + overflowCnt++;
                } else {
                    outNr = ((IntegerAttribute)indexNrMap
                        .get("o" + String.valueOf(0))).getInteger() - 1;
                }

                Util.ensureSize(outputBoxAndNumber, outNr);
                outputBoxAndNumber.set(outNr,
                    new Pair(iBox, new Integer(0)));
                        
                Util.ensureSize(outTypes, outNr);
                outTypes.set(outNr, new Integer(ITypeConstants.GENERAL));
                
                continue;
            }            
            
//            boxIONumber = iBox.getNumberOfOutputs();
//            if (node.getOutDegree() < boxIONumber) {
//                CollectionAttribute ioNrsAttr = null;
//                try {
//                    ioNrsAttr = (CollectionAttribute)
//                        node.getAttribute(QConstants.MARKED_OUTPUT_NRS_ID);
//                } catch (AttributeNotFoundException anfe) {
//                    ioNrsAttr = new HashMapAttribute
//                        (QConstants.MARKED_OUTPUT_NRS_ID);
//                    node.addAttribute(ioNrsAttr, "");
//                }
//                Map indexNrMap = ioNrsAttr.getCollection();
//                
//                boolean[] free = new boolean[boxIONumber];
//                for (int i = 0; i < free.length; i++) {
//                    free[i] = true;
//                }
//                Iterator eIt = node.getDirectedOutEdgesIterator();
//                while (eIt.hasNext()) {
//                    Edge edge = (Edge)eIt.next();
//                    free[Util.getIOIndex(edge, true)] = false;
//                }
//                // add this box's free outputs
//                int[] types = iBox.getOutputTypes();
//                for (int i = 0; i < free.length; i++) {
//                    if (free[i]) {
//                        int outNr = 0;
//                        if (indexNrMap.get("o" + String.valueOf(i)) == null) {
//                            // no order number saved for this ...
//                            // TODO remove this restriction
//                            outNr = 20 + overflowCnt++;
//                        } else {
//                            outNr = ((IntegerAttribute)indexNrMap
//                                .get("o" + String.valueOf(i))).getInteger() - 1;
//                        }
//
//                        Util.ensureSize(outputBoxAndNumber, outNr);
//                        outputBoxAndNumber.set(outNr,
//                            new Pair(iBox, new Integer(i)));
//                        
//                        Util.ensureSize(outTypes, outNr);
//                        outTypes.set(outNr, new Integer(types[i]));
//                    }
//                }
//            }
        }
        int[] inputTypesL = new int[inTypes.size()];
        int i = 0;
        for (Iterator it = inTypes.iterator(); it.hasNext();) {
            Integer nextInt = (Integer)it.next();
            if (nextInt == null) {
                continue;
            }
            inputTypesL[i++] = nextInt.intValue();
        }
        inputs = new Object[i];
        inputTypes = new int[i];
        System.arraycopy(inputTypesL, 0, inputTypes, 0, i);
        
        int[] outputTypesL = new int[outTypes.size()];
        i = 0;
        for (Iterator it = outTypes.iterator(); it.hasNext();) {
            Integer nextInt = (Integer)it.next();
            if (nextInt == null) {
                continue;
            }
            outputTypesL[i++] = nextInt.intValue();
        }
        outputs = new Object[i];
        outputTypes = new int[i];
        System.arraycopy(outputTypesL, 0, outputTypes, 0, i);
        

        // update representation
        if (iBoxGRep != null) {
            iBoxGRep.updateGraphicalRep();
        }

        QAssign.setAssignedRows(assignedRowsCopy);
        QAssign.setAssignedBEPRows(assignedBEPRowsCopy);
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof SubQuery_Rep)) {

            iBoxGRep = new SubQuery_Rep(this);
        }
        return iBoxGRep;
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        return inputTypes;
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return outputTypes;
    }
    
    /**
     * Returns <code>true</code>.
     * 
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

    /**
     * @see quoggles.boxes.IBox#setCurrentResult(java.util.List)
     */
    public void setCurrentResult(List res) {
        super.setCurrentResult(res);
        currentResult = res;
    }
}
