package quoggles.stdboxes.listoperations1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.graffiti.plugin.parameter.IntegerParameter;
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
 * FLATTEN:<p>
 * 
 * Input: an object<p>
 * Output: <code>null</code> if the input has been <code>null</code>.
 * If the input is a collection, a flat structure of that collection
 * is returned (this flattenig is applied recursively)<p>
 *
 * From its hierarchical input (a <code>Collection</code> holding 
 * <code>Collections</code>), a flat structure is retrieved.<p>
 * <b>Example</b>: If the input is a list of the form<p>
 * <code>  [ [A1, A2], B, [C1, A1, C2] ]</code><p>
 * then the result list will look like this:<p>
 * <code>  [ A1, A2, B, C1, A1, C2 ]</code><p>
 * 
 * <code>null</code> values are removed.<p>
 *  
 * The order will be preserved (if the given 
 * <code>Collections</code>s guarantee an order).<p>
 * Duplicates are not removed.<p>
 * <code>null</code> as input is passed on.<p>
 * A non-collection input is put into a one element list.
 *
 * <p>LISTIFY:<p>
 * 
 * Input: one or several collections<p>
 * Output: a list where the i'th element is a list made up from the i'th
 * elements in the input lists (in the order of the inputs).
 * 
 * <p>MAKEDISINCT:<p>
 * 
 * Input: a collection of objects of arbitrary types<p>
 * Output: a collection containing the input elements,
 * without any duplicates (tested using <code>equals</code>).
 *
 * <p>REVERSE:<p>
 * 
 * Input: an <code>Object</code> or a <code>Collection</code><p>
 * Output: a <code>Collection</code> with the same elemenst as the input 
 * <code>Collection</code> with the order of the elements reversed. If the 
 * input is not a <code>Collection</code>, the input object is returned 
 * untouched.
 *
 * <p>DELISTIFY:<p>
 * 
 * Reverses LISTIFY.<p>
 * 
 * Input: a collection (of collections of size == number of outputs
 * of the box<p>
 * Output: Input is a collection of tuples. All tuples must have same size s.
 * Box must have s outputs. Output number i produces a list of the i^th 
 * elements of the tuples.  
 * 
 * <p>UNPACK:<p>
 * 
 * Similar to DELISTIFY; for one input tuple.<p>
 * 
 * Input: a collection #nrOutputs objects<p>
 * Output: Input is one tuple of size s. Box must have s outputs. 
 * Output number i produces the i^th element of the tuple.  
 * 
 */
public class ListOperations1_Box extends Box {

    /** Only set if input is not a collection */
    private Object singleInput = null;
    
    /** Used if several inputs */
    private Collection[] inputCols;
    
    /** Used if one input */
    private Collection inputCol;
    
    /** Number of inputs */
    private int nrInputs = 1;
    
    /** Number of outputs */
    private int nrOutputs = 1;
    
    /** Used by listify operation */
    private int longestInput = 0;
    
    private Parameter chooseOp = new OptionParameter
        (IBoxConstants.LISTOPERS, 0, false,
            "Operation", "Operation that shall be applied");

    /**
     *
     */
    public ListOperations1_Box() {
        parameters = new Parameter[]{ chooseOp };

//        parameters = new Parameter[]{ new IntegerParameter(1,
//            "ioNumber", "Number of inputs (equal to number of outputs") };
    }
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();

//        if (inputCols == null) {
//            outputs = new Object[]{ null };
//            return;
//        }
        
        String oper = ((OptionParameter)parameters[0]).getValue().toString();
        
        if (IBoxConstants.MAKEDISTINCT.equals(oper)) {
            if (nrInputs == 1) {
                // only one input
                if (singleInput != null) {
                    outputs = new Object[]{ singleInput };
                } else {        
                    Set outputCol = new LinkedHashSet(inputCol);
                    outputs = new Set[]{ outputCol };
                }
                return;
            }

            // several inputs:

            Set outputCol = new LinkedHashSet(inputCols[0].size());
            
            int colNr = inputCols.length;
            
            // initialize iterators for each column
            Iterator[] its = new Iterator[colNr];
            for (int i = 0; i < colNr; i++) {
                its[i] = inputCols[i].iterator();
            }
            
            // iterate through columns creating rows (lists)
            // this automatically removes duplicates
            while (its[0].hasNext()) {
                List list = new ArrayList(colNr);
                for (int i = 0; i < colNr; i++) {
                    list.add(its[i].next());
                }
                outputCol.add(list);
            }
            
            // unpack
            Collection[] outputCols = new Collection[colNr];
            for (int i = 0; i < colNr; i++) {
                outputCols[i] = new ArrayList(outputCol.size());
            }
            
            for (Iterator it = outputCol.iterator(); it.hasNext();) {
                List list = (List)it.next();
                for (int i = 0; i < colNr; i++) {
                    outputCols[i].add(list.get(i));
                }
            }
            
            outputs = outputCols;
            return;

        } 

        if (IBoxConstants.REVERSE.equals(oper)) { 
            if (singleInput != null) {
                outputs = new Object[]{ singleInput };
                return;
            }        
            if (inputCol.isEmpty()) {
                outputs = new Collection[]{ inputCol };
                return;
            }
            List list = new ArrayList(inputCol);
            Collections.reverse(list);
            outputs = new List[]{ list };
            return;

        } 

        if (IBoxConstants.FLATTEN.equals(oper)) { 
            if (singleInput != null) {
                outputs = new Object[]{ flatten(singleInput) };
                return;
            }        
            if (inputCol.isEmpty()) {
                outputs = new Collection[]{ inputCol };
                return;
            }
            outputs = new Object[]{ flatten(inputCol) };
            return;

        } 

        if (IBoxConstants.DELISTIFY.equals(oper)) { 
            if (singleInput != null) {
                outputs = new Object[]{ singleInput };
                return;
            }        
//            if (inputCol.size() != 1) {
//                throw new InvalidInputException(getId() +
//                    " needs a collection with ONE ELEMENT not with " +
//                    inputCol.size());
//            }

////            outputs = new Object[nrOutputs];
////            Iterator it = inputCol.iterator();
////            for (int i = 0; i < outputs.length; i++) {
////                outputs[i] = it.next();
////            }
////            return;

            Collection[] colOutputs = new Collection[nrOutputs];
            outputs = colOutputs;
            for (int i = 0; i < colOutputs.length; i++) {
                colOutputs[i] = new LinkedList();
            }
            for (Iterator it = inputCol.iterator(); it.hasNext();) {
                Object elem = it.next();
                if (elem instanceof Collection) {
                    Collection col = (Collection)elem;
                    if (col.size() != nrOutputs) {
                        throw new InvalidInputException(getId() +
                            ": Elements of input collection must be" +
                            " collections of size " + nrOutputs + ", not of size "
                            + col.size() + ".");
                    }
                    Iterator colIt = col.iterator();
                    for (int i = 0; i < outputs.length; i++) {
                        colOutputs[i].add(colIt.next());
                    }
                } else {
                    throw new InvalidInputException(getId() +
                        ": Elements of input collection must be" +
                        " collections of size " + nrOutputs + ", not objects" +
                        " of type " + elem.getClass().getName());
                }
            }
            return;
        } 

        if (IBoxConstants.UNPACK.equals(oper)) { 
            if (singleInput != null) {
                outputs = new Object[]{ singleInput };
                return;
            }
            
            outputs = new Object[nrOutputs];
            Iterator colIt = inputCol.iterator();
            for (int i = 0; i < outputs.length; i++) {
                outputs[i] = colIt.next();
            }
            
            return;
        }        

        if (IBoxConstants.LISTIFY.equals(oper)) { 
            if (nrInputs == 1) {
                // only one input
                Collection outputCol = new ArrayList(1);
                if (singleInput != null) {
                    outputCol.add(singleInput);
                } else {
                    outputCol.add(inputCol);
                }
                outputs = new Collection[]{ outputCol };
////                if (singleInput != null) {
////                    outputs = new Object[]{ singleInput };
////                } else {        
////                    outputs = new Collection[]{ inputCol };
////                }
                return;
            }

            // several inputs:

            List outputCol = new ArrayList(inputCols[longestInput].size());
            int colNr = inputs.length;
        
            // initialize iterators for each column
            Iterator[] its = new Iterator[colNr];
            for (int i = 0; i < colNr; i++) {
                its[i] = inputCols[i].iterator();
            }

            // iterate through columns creating rows (lists)
            while (its[longestInput].hasNext()) {
                List list = new ArrayList(colNr);
                for (int i = 0; i < colNr; i++) {
                    if (its[i].hasNext()) {
                        list.add(its[i].next());
                    } else {
                        list.add(QConstants.EMPTY);
                    }
                }
                outputCol.add(list);
            }
        
            if (singleInput != null) {
                outputs = new List[]{ (List)outputCol.get(0) };
            } else {
                outputs = new List[]{ outputCol };
            }
            return;
        }

        if (IBoxConstants.REMOVEEMPTY.equals(oper)) { 
            if (singleInput != null) {
                if (singleInput == quoggles.constants.QConstants.EMPTY) {
                    outputs = new List[]{ new ArrayList(0) };
                } else {
                    outputs = new Object[]{ singleInput };
                }
                return;
            }        
            if (inputCol.isEmpty()) {
                outputs = new Collection[]{ inputCol };
                return;
            }
            List outputCol = new LinkedList();
            for (Iterator it = inputCol.iterator(); it.hasNext();) {
                Object obj = it.next();
                if (obj != quoggles.constants.QConstants.EMPTY &&
                    !(obj instanceof Collection && 
                        ((Collection)obj).isEmpty())) {
                    outputCol.add(obj);
                }
            }
            outputs = new List[]{ outputCol };
            return;
        } 
    }
    
    /**
     * Flattens a hierarchical structure.
     * 
     * @param in the object to flatten
     * @return <code>Collection</code> that does not hold any 
     * <code>Collection</code> as element; the input object itself if it had
     * not been a collection
     */
    private Object flatten(Object in) {
        if (in instanceof Collection) {
            List outCol = new LinkedList();
            for (Iterator it = ((Collection)in).iterator(); it.hasNext();) {
                Object elem = it.next();
//                if (elem == null) {
//                    return outCol;
//                }
                // recursively flatten
                Object sub = flatten(elem);
                if (sub instanceof Collection) {
                    // add each element
                    outCol.addAll((Collection)sub);
                } else {
                    // add this (non-collection) element
                    outCol.add(sub);
                }
            }        
            return outCol;
        } else {
            return in;
        }
    }
    
    /**
     * @see quoggles.boxes.IBox#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        return nrInputs;
    }

    /**
     * @see quoggles.boxes.IBox#getNumberOfOutputs()
     */
    public int getNumberOfOutputs() {
        return nrOutputs;
    }

    /**
     * @see quoggles.boxes.IBox#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] pars, boolean fromRep) {
        if (pars.length > 1) {
            String oper = ((OptionParameter)pars[0]).getValue().toString();
        
            if (IBoxConstants.UNPACK.equals(oper) ||
                IBoxConstants.DELISTIFY.equals(oper)) {
                nrOutputs = 
                    ((IntegerParameter)pars[1]).getInteger().intValue();
                nrInputs = 1;
            } else {
                nrInputs = ((IntegerParameter)pars[1]).getInteger().intValue();
                if (IBoxConstants.MAKEDISTINCT.equals(oper)) {
                    nrOutputs = 
                        ((IntegerParameter)pars[1]).getInteger().intValue();
                } else {
                    nrOutputs = 1;
                }
            }
        } else {
            nrInputs = 1;
            nrOutputs = 1;
        }
        super.setParameters(pars, fromRep);

        // update everything that depends on the number of inputs
        reset();
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        String oper = ((OptionParameter)parameters[0]).getValue().toString();
        
        if (IBoxConstants.DELISTIFY.equals(oper)) {
            int[] ios = new int[nrInputs];
            for (int i = 0; i < nrInputs; i++) {
                ios[i] = ITypeConstants.COLOF_COLS;
            }
            return ios;
        }

        int[] ios = new int[nrInputs];
        for (int i = 0; i < nrInputs; i++) {
            ios[i] = ITypeConstants.COLLECTION;
        }
        return ios;
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        String oper = ((OptionParameter)parameters[0]).getValue().toString();
        
        if (IBoxConstants.UNPACK.equals(oper) ||
            IBoxConstants.DELISTIFY.equals(oper)) {

            int[] outs = new int[nrOutputs];
            for (int i = 0; i < outs.length; i++) {
                outs[i] = ITypeConstants.GENERAL;
            }
            return outs;
        } else if (IBoxConstants.LISTIFY.equals(oper)) {
            return new int[]{ ITypeConstants.COLLECTION };
        }

        return super.getOutputTypes();
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        
//        if (inputs == null) return;
        
        singleInput = null;
        inputCol = null;
        inputCols = new Collection[nrInputs];
        String oper = ((OptionParameter)parameters[0]).getValue().toString();
        
        if (nrInputs == 1) {
            if (IBoxConstants.UNPACK.equals(oper)) {
                try {
                    inputCol = (Collection)inputs[0];
                    if (inputCol.size() != nrOutputs) {
                        throw new InvalidInputException(getId() +
                            ": input collection must have " + nrOutputs +
                            " elements since the box has " + nrOutputs + 
                            " outputs. (This collection has " +
                            inputCol.size() + " elements.)");
                    }
                } catch (ClassCastException cce) {
                    if (nrOutputs == 1) {
                        inputCol = null;
                        singleInput = inputs[0];
                    } else throw new InvalidInputException(getId() +
                        " needs a collection as input, not an object of" +
                        " type " + inputs[0].getClass().getName());
                }
                return;
            }

            if (IBoxConstants.DELISTIFY.equals(oper)) {
                try {
                    inputCol = (Collection)inputs[0];
                } catch (ClassCastException cce) {
                    if (nrOutputs == 1) {
                        inputCol = null;
                        singleInput = inputs[0];
                    } else throw new InvalidInputException(getId() +
                        " needs a collection as input, not an object of" +
                        " type " + inputs[0].getClass().getName());
                }
                return;
            }

            try {
                inputCol = (Collection)inputs[0];
            } catch (ClassCastException cce) {
                inputCol = null;
                singleInput = inputs[0];
            }
            return;
        }
        
        // several inputs:

        if (IBoxConstants.LISTIFY.equals(oper)) {
            longestInput = -1;
            int max = 0;
            
            boolean allOne = true;
            
            for (int i = 0; i < inputs.length; i++) {
                try {
                    inputCols[i] = (Collection)inputs[i];
                    allOne = false;
                    int length = inputCols[i].size();
                    if (length > max) {
                        max = length;
                        longestInput = i;
                    }
                } catch (ClassCastException cce) {
                    inputCols[i] = new ArrayList(1);
                    inputCols[i].add(inputs[i]);
                    // length of list is 1
                    if (1 > max) {
                        max = 1;
                        longestInput = i;
                    }
                }
            }
            if (allOne) {
                // just a signal
                singleInput = new Integer(1);
            }

        } else if (IBoxConstants.MAKEDISTINCT.equals(oper)) {
            int size = -1;
            for (int i = 0; i < nrInputs; i++) {
                try {
                    inputCols[i] = (Collection)inputs[i];
                    if (size == -1) {
                        size = inputCols[i].size();
                    } else if (inputCols[i].size() != size) {
                        throw new InvalidInputException(getId() +
                            ": all input collections must be of same size (" +
                            inputCols[i].size() + " != " + size + ")");
                    }
                } catch (ClassCastException cce) {
                    throw new InvalidInputException(getId() +
                        " needs collection(s) as input, not an object of type " +
                        inputs[i].getClass().getName());
                }
            }
        } else {
            throw new InvalidInputException(getId() +
                " unknown parameter.");
        }
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || !(iBoxGRep instanceof ListOperations1_Rep)) {
            iBoxGRep = new ListOperations1_Rep(this);
        }
        return iBoxGRep;
    }
}