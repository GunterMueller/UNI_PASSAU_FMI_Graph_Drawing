package quoggles.stdboxes.output;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JOptionPane;

import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

import quoggles.QAssign;
import quoggles.auxiliary.Util;
import quoggles.boxes.Box;
import quoggles.exceptions.BoxNotExecutedException;
import quoggles.exceptions.QueryExecutionException;
import quoggles.representation.DefaultBoxRepresentation;
import quoggles.representation.IBoxRepresentation;
import quoggles.stdboxes.complexfilter.ComplexFilter_Box;

/**
 * Input: anything<p>
 * ( Output: see quoggles.auxiliary.Util.interpretAsBoolean(Object) )
 */
public class BoolPredicateEnd_Box extends Box {

    private IntegerParameter outPosParam;
    
    /**
     * A set of <code>ComplexFilter_Box</code>es that are notified when this
     * box calculates a <code>true</code> value.
     */ 
    private Set complexFilterBoxes = new HashSet(2);


    /**
     * Constructs the box.
     */
    public BoolPredicateEnd_Box() {
        super();
        
        int rowNr = QAssign.getNextFreeBEPRowNumber();
        if (rowNr < 0) {
            JOptionPane.showMessageDialog(null, "Not more than " + 
                QAssign.getMaxAssignedRowNr() + " output boxes allowed in " +
                " one query." +
                " Please remove one of the boxes or the query might yield" +
                " unexpected results.", "Error: Too many output boxes",
                JOptionPane.ERROR_MESSAGE);
            rowNr = 0;
        }
        QAssign.assignBEPRow(rowNr, true);
        outPosParam = new IntegerParameter(rowNr, "row number",
             "The number of the row where the ouput from this box is placed.");
        
        parameters = new Parameter[]{ outPosParam };
    }

    
    /**
     * Registers a <code>ComplexFilter_Box</code> that will be informed when this box
     * calculates a <code>true</code> value.
     * 
     * @param box the box to be registered
     */
    public void registerComplexFilter(ComplexFilter_Box box) {
        complexFilterBoxes.add(box);
    }
    
    /**
     * Evaluates the input to a boolean value. See interpretAsBoolean for a
     * formal definition.
     * 
     * @see quoggles.auxiliary.Util#interpretAsBoolean(Object)
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        boolean result = Util.interpretAsBoolean(inputs[0]);
        outputs = new Boolean[]{ new Boolean(result) };
            
        if (result) {
            // inform complex filters
            for (Iterator it = complexFilterBoxes.iterator(); it.hasNext();) {
                ComplexFilter_Box box = (ComplexFilter_Box)it.next();
                box.currentTestSucceded();
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
//        if (iBoxGRep == null || 
//            !(iBoxGRep instanceof BoolPredicateEnd_Rep)) {
//
//            iBoxGRep = new BoolPredicateEnd_Rep(this);
//        }
//        return iBoxGRep;
    }
    
    /**
     * @see quoggles.boxes.IBox#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] pars, boolean fromRep) {
        int oldPos = ((IntegerParameter)parameters[0]).getInteger().intValue();

        // update
        super.setParameters(pars, fromRep);

        int newPos = ((IntegerParameter)parameters[0]).getInteger().intValue();
        if (newPos != oldPos) {
            // row number changed; reflect changes
            QAssign.assignBEPRow(oldPos, false);
            if (QAssign.getBEPRowAssignment(newPos)) {
                // already assigned by some other output box
                newPos = QAssign.getNextFreeBEPRowNumber();
                // save newly chosen value into parameter
                ((IntegerParameter)parameters[0]).setValue
                    (new Integer(newPos));
            }
                    
            if (oldPos != newPos) {
                QAssign.assignBEPRow(newPos, true);
            }
        }
    }

    /**
     * Returns 0.
     * 
     * @see quoggles.boxes.IBox#getNumberOfOutputs()
     */
    public int getNumberOfOutputs() {
        return 0;
    }

    /**
     * Returns empty array.
     * 
     * @see quoggles.boxes.IBox#getOutputTypes()
     */
    public int[] getOutputTypes() {
        return new int[]{ };
    }
        
    /**
     * Needed to be overwritten since the default implementation would fail
     * since this box has no (publicly visible) outputs.
     * 
     * @param index the index of the required output (must be zero)
     * 
     * @see quoggles.boxes.IBox#getOutputAt(int)
     */
    public Object getOutputAt(int index) throws BoxNotExecutedException {
        if (!hasBeenExecuted()) {
            throw new BoxNotExecutedException(getId() + 
                ": Box not executed.");
        }
        return outputs[index];
    }
        
    /**
     * Removes the given box from the set of registered 
     * <code>ComplexFilter_Box</code>es.
     * 
     * @param cfb the box that will be removed
     */
    public void removeRegistered(ComplexFilter_Box cfb) {
        complexFilterBoxes.remove(cfb);
    }
    
    /**
     * Clears the list of registered <code>ComplexFilter_Box</code>es.
     */
    public void clearRegistered() {
        complexFilterBoxes.clear();
    }
}
