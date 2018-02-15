package quoggles.stdboxes.output;

import java.util.Collection;

import javax.swing.JOptionPane;

import org.graffiti.plugin.parameter.Parameter;

import quoggles.QAssign;
import quoggles.boxes.Box;
import quoggles.boxes.IOutputBox;
import quoggles.constants.IBoxConstants;
import quoggles.exceptions.QueryExecutionException;
import quoggles.parameters.OptionParameter;
import quoggles.representation.IBoxRepresentation;

/**
 * Input: anything
 * ( Output: same as input )
 */
public class NormalOutput_Box extends Box implements IOutputBox {

    /** Save last row assignment before a change in the parameter */
    private int lastRowNr = -1;
    

    /**
     * Constructs the box.
     */
    public NormalOutput_Box() {
        lastRowNr = -1;
        int rowNr = QAssign.getNextFreeRowNumber();
        if (rowNr < 0) {
            JOptionPane.showMessageDialog(null, "Not more than " + 
                QAssign.getMaxAssignedRowNr() + " output boxes allowed in" + 
                " one query." +
                " Please remove one of the boxes or the query might yield" +
                " unexpected results.", "Error: Too many output boxes",
                JOptionPane.ERROR_MESSAGE);
            rowNr = 0;
        }
        lastRowNr = rowNr;
        QAssign.assignRow(rowNr, true);
        Parameter outPosParam = new OptionParameter
            (IBoxConstants.ROWNUMBERS, 
             rowNr, false, "col. number",
             "The number of the column where the ouput from this box is placed.");
        parameters = new Parameter[]{ outPosParam };
    }


    /**
     * Sets the last row number that this box was assigned to.
     */
    public void setLastRowNumber(int lr) {
        lastRowNr = lr;        
    }

    /**
     * @see quoggles.boxes.IBox#setParameters(org.graffiti.plugin.parameter.Parameter[], boolean)
     */
    public void setParameters(Parameter[] pars, boolean fromRep) {
        super.setParameters(pars, fromRep);

        int newPos = 0;
        if (parameters.length > 0) {
            newPos = ((OptionParameter)parameters[0]).getOptionNr();
        }
        int oldPos = newPos;
        if (getLastRowNumber() != -1) {
            oldPos = getLastRowNumber();
        } else if (iBoxGRep == null) {
            // first time, rep has not assigned anything
//            QAssign.assignRow(oldPos, false);
            if (QAssign.getRowAssignment(newPos)) {
                // already assigned by some other output box
                newPos = QAssign.getNextFreeRowNumber();
            }
                    
//            if (oldPos != newPos) {
//                outPosCombo.setSelectedIndex(newPos);
                QAssign.assignRow(newPos, true);
//                ((OptionParameter)parameters[0])
//                    .setValue(outPosCombo.getSelectedItem());
//            }
            oldPos = newPos;
        }
        if (newPos != oldPos) {
            // row number changed; reflect changes
            QAssign.assignRow(oldPos, false);
            if (QAssign.getRowAssignment(newPos)) {
                // already assigned by some other output box
                newPos = QAssign.getNextFreeRowNumber();
//                if (oldPos != newPos) {
//                    outPosCombo.setSelectedIndex(newPos);
//                }
            }
                    
            if (oldPos != newPos) {
//                outPosCombo.setSelectedIndex(newPos);
                QAssign.assignRow(newPos, true);
//                ((OptionParameter)parameters[0])
//                    .setValue(outPosCombo.getSelectedItem());
            }
        }
        setLastRowNumber(newPos);
    }

    /**
     * Returns the last row number that this box was assigned to.
     * 
     * @return the last row number that this box was assigned to
     */
    protected int getLastRowNumber() {
        return lastRowNr;
    }
    
    /**
     * @see quoggles.boxes.IBox#getGraphicalRepresentation()
     * 
     * @return the graphical representation of this box
     */
    public IBoxRepresentation getGraphicalRepresentation() {
        if (iBoxGRep == null || 
            !(iBoxGRep instanceof NormalOutput_Rep)) {

            iBoxGRep = new NormalOutput_Rep(this);
        }
        return iBoxGRep;
    }
    
    /**
     * @see quoggles.boxes.Box#getClassName()
     */
    protected String getClassName() {
        return "Out";
    }
    
    /**
     * @see quoggles.boxes.IBox#execute()
     */
    public void execute() throws QueryExecutionException {
        super.execute();
        
        if (inputs[0] instanceof Collection) {
            outputs = new Collection[]{ removeNullValues((Collection)inputs[0]) };
        }
    }

}