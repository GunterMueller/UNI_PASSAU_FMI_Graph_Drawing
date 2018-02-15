package quoggles.deprecated.stdboxes.listify;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.boxes.Box;
import quoggles.constants.ITypeConstants;
import quoggles.exceptions.InvalidInputException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: one or several collections<p>
 * Output: a list where the i'th element is a list made up from the i'th
 * elements in the input lists (in the order of the inputs).
 */
public class Listify_Box extends Box {

    private int ioNumber = 1;
    
    private int longestInput = 0;
    
    private Collection[] inputCols;
    

    public Listify_Box() {
        parameters = new Parameter[]{ new IntegerParameter(2,
            "inNumber", "Number of inputs") };
    }
    
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();

        if (inputs == null) {
            outputs = new Object[]{ null };
            return;
        }
        
        List outputCol = new ArrayList(inputCols[longestInput].size());
        int colNr = inputs.length;
        
        // initialize iterators for each column
        Iterator[] its = new Iterator[colNr];
        for (int i = 0; i < colNr; i++) {
            its[i] = inputCols[i].iterator();
        }
        
        // iterate through columns creating rows (lists)
        // this automatically removes duplicates
        while (its[longestInput].hasNext()) {
            List list = new ArrayList(colNr);
            for (int i = 0; i < colNr; i++) {
                if (its[i].hasNext()) {
                    list.add(its[i].next());
                } else {
                    list.add(null);
                }
            }
            outputCol.add(list);
        }
        
        outputs = new List[]{ outputCol };
    }
    
    /**
     * @see quoggles.boxes.IBox#getNumberOfInputs()
     */
    public int getNumberOfInputs() {
        return ioNumber;
    }

    /**
     * @see quoggles.boxes.IBox#setInputs(java.lang.Object[])
     */
    public void setInputs(Object[] inputs) throws InvalidInputException {
        super.setInputs(inputs);
        
        longestInput = -1;
        int max = 0;
        
        inputCols = new Collection[inputs.length];
        
        for (int i = 0; i < inputs.length; i++) {
            try {
                inputCols[i] = (Collection)inputs[i];
                int length = inputCols[i].size();
                if (length > max) {
                    max = length;
                    longestInput = i;
                }
            } catch (ClassCastException cce) {
                inputCols[i] = new ArrayList(1);
                inputCols[i].add(inputs[i]);
                if (1 > max) {
                    max = 1;
                    longestInput = i;
                }
            }
        }
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
     * All inputs can be of general type.
     * 
     * @see quoggles.boxes.IBox#getInputTypes()
     */
    public int[] getInputTypes() {
        int[] ios = new int[ioNumber];
        for (int i = 0; i < ioNumber; i++) {
            ios[i] = ITypeConstants.GENERAL;
        }
        return ios;
    }

    /**
     * One boolean value.
     * 
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[]{ ITypeConstants.COLLECTION };
    }

    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || !(iBoxGRep instanceof Listify_Rep)) {
            iBoxGRep = new Listify_Rep(this);
        }
        return iBoxGRep;
    }
}