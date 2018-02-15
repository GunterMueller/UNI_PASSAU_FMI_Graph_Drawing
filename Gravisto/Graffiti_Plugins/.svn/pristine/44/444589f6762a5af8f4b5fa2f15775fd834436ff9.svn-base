package quoggles.deprecated.stdboxes.makedistinct;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: a collection of objects of arbitrary types<p>
 * Output: a collection containing the input elements,
 * without any duplicates (tested using <code>equals</code>).
 */
public class MakeDistinct_Box extends Box {

    private Collection[] inputCols;
    
    private int ioNumber = 1;
    

    public MakeDistinct_Box() {
        parameters = new Parameter[]{ new IntegerParameter(1,
            "ioNumber", "Number of inputs (equal to number of outputs") };
    }
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();

        if (inputCols == null) {
            outputs = new Object[]{ null };
            return;
        }
        
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
        
        //doSetOutput(outputCols);
    }
    
    /**
     * @see quoggles.boxes.IBox#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        return ioNumber;
    }

    /**
     * @see quoggles.boxes.IBox#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] pars, boolean fromRep) {
        ioNumber = ((IntegerParameter)pars[0]).getInteger().intValue();
        super.setParameters(pars, fromRep);

        // update everything that depends on the number of inputs
        reset();
    }

    /**
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        int[] ios = new int[ioNumber];
        for (int i = 0; i < ioNumber; i++) {
            ios[i] = ITypeConstants.COLLECTION;
        }
        return ios;
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        
        if (inputs == null) return;
        
        inputCols = new Collection[ioNumber];

        int size = -1;
        for (int i = 0; i < ioNumber; i++) {
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
                    " needs collections as input, not an object of type " +
                    inputs[i].getClass().getName());
            }
        }
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || !(iBoxGRep instanceof MakeDistinct_Rep)) {
            iBoxGRep = new MakeDistinct_Rep(this);
        }
        return iBoxGRep;
    }
}