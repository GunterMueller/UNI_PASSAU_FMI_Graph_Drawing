package quoggles;

import quoggles.constants.QConstants;

/**
 * Holds static row assignment arrays.
 */
public class QAssign {

    /**
     * An entry at <code>i</code> is true if an <code>IOuputBox</code> sets its
     * output at row <code>i</code>.
     */ 
    private static boolean[] assignedRows = new boolean[QConstants.MAX_ROWS];
    
    /** 
     * An entry at <code>i</code> is true if an 
     * <code>BoolPredicateEnd_Box</code> sets its output at row <code>i</code>.
     */ 
    private static boolean[] assignedBEPRows = 
        new boolean[QConstants.MAX_ROWS];
    
    /**
     * Returns <code>true</code> if the given row number has been assigned by
     * an <code>IOutputBox</code>.
     * 
     * @param rowNr
     * 
     * @return <code>true</code> if the given row number has been assigned by
     * an <code>IOutputBox</code>
     */
    public static boolean getRowAssignment(int rowNr) {
        return assignedRows[rowNr];
    }
    
    /**
     * Setes all entries in the <code>assignedRows</code> array to 
     * <code>false</code>.
     */
    public static void resetAssignedRows() {
        for (int i = 0; i < assignedRows.length; i++) {
            assignedRows[i] = false;
        }
        for (int i = 0; i < assignedBEPRows.length; i++) {
            assignedBEPRows[i] = false;
        }
    }
    
    /**
     * Specifies that the given row number has been assigned by
     * an <code>IOutputBox</code> or that it has been freed (depending on the
     * boolean parameter).
     * 
     * @param rowNr
     * @param assign
     */
    public static void assignRow(int rowNr, boolean assign) {
        assignedRows[rowNr] = assign;
    }
    
    /**
     * Returns <code>true</code> if the given row number has been assigned by
     * a <code>BoolPredicateEnd_Box</code>.
     * 
     * @param rowNr
     * 
     * @return true if the row with the given index has been assigned for a
     * <code>BoolPredicateEnd_Box</code>
     */
    public static boolean getBEPRowAssignment(int rowNr) {
        return assignedBEPRows[rowNr - assignedRows.length];
    }
    
    /**
     * Specifies that the given row number has been assigned by
     * an <code>IOutputBox</code> or that it has been freed (depending on the
     * boolean parameter).
     * 
     * @param rowNr
     * @param assign
     */
    public static void assignBEPRow(int rowNr, boolean assign) {
        assignedBEPRows[rowNr - assignedRows.length] = assign;
    }
    
    /**
     * Returns the highest possible row assignement number.
     * 
     * @return the highest possible row assignement number
     */
    public static int getMaxAssignedRowNr() {
        return assignedRows.length;
    }
    
    /**
     * Returns the highest possible row assignement number for
     * <code>BoolPredicateEnd_Box</code>es.
     * 
     * @return the highest possible row assignement number for
     * <code>BoolPredicateEnd_Box</code>es
     */
    public static int getMaxAssignedBEPRowNr() {
        return assignedBEPRows.length + assignedRows.length;
    }
    
    /**
     * Get the first row number that is not assigned to any 
     * <code>IOutputBox</code> box, i.e.
     * the first number <code>i</code> where 
     * <code>assignedRows[i] == false</code>.
     * Returns <code>-1</code> if no such number can be found (overflow).
     * 
     * @return <code>-1</code> if no free number available.
     */
    public static int getNextFreeRowNumber() {
        for (int i = 0; i < assignedRows.length; i++) {
            if (!assignedRows[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get the first row number that is not assigned to any 
     * <code>BoolPredicateEnd_Box</code> box, i.e.
     * the first number <code>i</code> where 
     * <code>assignedRows[i] == false</code>.
     * Returns <code>-1</code> if no such number can be found (overflow).
     * 
     * @return <code>-1</code> if no free number available.
     */
    public static int getNextFreeBEPRowNumber() {
        for (int i = 0; i < assignedBEPRows.length; i++) {
            if (!assignedBEPRows[i]) {
                return i + assignedRows.length;
            }
        }
        return -1;
    }
    
    /**
     * This method may only be used to easily make a copy. Use get / set
     * methods for read / write access.
     * 
     * @return copy of assigned rows array
     */
    public static boolean[] getAssignedRowsCopy() {
        boolean[] assignedRowsCopy = new boolean[assignedRows.length];
        System.arraycopy(assignedRows, 0, assignedRowsCopy, 0, 
            assignedRows.length);
        return assignedRowsCopy;
    }

    /**
     * This method may only be used to easily make a copy. Use get / set
     * methods for read / write access.
     * 
     * @return copy of assigned BEP rows array
     */
    public static boolean[] getAssignedBEPRowsCopy() {
        boolean[] assignedBEPRowsCopy = 
            new boolean[assignedBEPRows.length];
        System.arraycopy(assignedBEPRows, 0, assignedBEPRowsCopy, 0, 
            assignedRows.length);
        return assignedBEPRowsCopy;
    }

    /**
     * This method may only be used with a parameter previously retrieved
     * via <code>getAssignedBEPRowsCopy()</code>.
     * 
     * @param ar copy of assigned BEP rows array
     */
    public static void setAssignedBEPRows(boolean[] ar) {
        assignedBEPRows = ar;
    }

    /**
     * This method may only be used with a parameter previously retrieved
     * via <code>getAssignedRowsCopy()</code>.
     * 
     * @param ar copy of assigned BEP rows array
     */
    public static void setAssignedRows(boolean[] ar) {
        assignedRows = ar;
    }

}
