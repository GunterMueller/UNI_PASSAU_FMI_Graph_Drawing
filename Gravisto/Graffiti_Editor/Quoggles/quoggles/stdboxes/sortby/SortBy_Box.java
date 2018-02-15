package quoggles.stdboxes.sortby;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.auxiliary.Comparators;
import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: first x inputs will be sorted; second y inputs are used to define 
 * the permutation<p>
 * Output: the x sorted inputs
 */
public class SortBy_Box extends Box {
    
    /** <code>true</code> iff no input is a collection */
    private boolean singleInputs;
    
    
    public SortBy_Box() {
        parameters = new Parameter[]{ 
            new IntegerParameter(1,
                "# to sort", "Number of inputs to sort"),
            new IntegerParameter(1,
                "# sort by", "Number of inputs used to get permutation") };
    }

    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        if (singleInputs) {
            int nrO = getNumberOfOutputs();
            outputs = new Object[nrO];
            for (int i = 0; i < nrO; i++) {
                outputs[i] = inputs[i];
            }
            return;
        }        
        
        List table = new ArrayList(((Collection)inputs[0]).size());
        
        int colNr = inputs.length;
        
        int sortNr = ((IntegerParameter)parameters[0]).getInteger().intValue();
        int sortByNr = 
            ((IntegerParameter)parameters[1]).getInteger().intValue();

        // initialize iterators for each column
        // sortBy columns first
        Iterator[] its = new Iterator[colNr];
        for (int i = sortNr; i < colNr; i++) {
            its[i - sortNr] = ((Collection)inputs[i]).iterator();
        }
        for (int i = 0; i < sortNr; i++) {
            its[colNr - sortNr + i] = ((Collection)inputs[i]).iterator();
        }
        
        // iterate through columns creating rows (lists)
        while (its[0].hasNext()) {
            List list = new ArrayList(colNr);
            for (int i = 0; i < colNr; i++) {
                list.add(its[i].next());
            }
            table.add(list);
        }
        

        Comparator tableComp = new Comparators.TableComparator(sortByNr);
        
        Collections.sort(table, tableComp);


        // unpack
        Collection[] outputCols = new Collection[colNr];
        for (int i = 0; i < sortNr; i++) {
            outputCols[i] = new ArrayList(table.size());
        }
        
        for (Iterator it = table.iterator(); it.hasNext();) {
            List list = (List)it.next();
            list = list.subList(sortByNr, list.size());
            for (int i = 0; i < sortNr; i++) {
                outputCols[i].add(list.get(i));
            }
        }
        
        outputs = outputCols;
    }
    
    /**
     * @see quoggles.boxes.IBox#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        return ((IntegerParameter)parameters[0]).getInteger().intValue() +
            ((IntegerParameter)parameters[1]).getInteger().intValue();
    }

    /**
     * @see quoggles.boxes.IBox#getNumberOfOutputs()
     */
    public int getNumberOfOutputs() {
        return ((IntegerParameter)parameters[0]).getInteger().intValue();
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        int[] its = new int[getNumberOfInputs()];
        for (int i = 0; i < its.length; i++) {
            its[i] = ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION;
        }
        return its;
    }

    /**
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        int[] its = new int[getNumberOfOutputs()];
        for (int i = 0; i < its.length; i++) {
            its[i] = ITypeConstants.ONEOBJECT + ITypeConstants.COLLECTION;
        }
        return its;
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        int size = -1;
        
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i] instanceof Collection) {
                if (size == -1) {
                    size = ((Collection)inputs[0]).size();
                } else if (size != ((Collection)inputs[0]).size()) {
                    throw new InvalidInputException(getId() +
                        " requires that all inputs are of the same size.");
                }
            } else {
                if (size == -1) {
                    size = 0;
                } else if (size != 0) {
                    throw new InvalidInputException(getId() +
                        " requires that all inputs are of the same size.");
                }
            }
        }
        singleInputs = size == 0;



//        singleInput = null;
//        try {
//            inputCol = (Collection)inputs[0];
//        } catch (ClassCastException cce) {
//            inputCol = null;
//            singleInput = inputs[0];
//        }
    }

    /**
     * @see quoggles.boxes.IBox#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] pars, boolean fromRep) {
        super.setParameters(pars, fromRep);

        // update everything that depends on the number of inputs
        reset();
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || !(iBoxGRep instanceof SortBy_Rep)) {
            iBoxGRep = new SortBy_Rep(this);
        }
        return iBoxGRep;
    }
}
