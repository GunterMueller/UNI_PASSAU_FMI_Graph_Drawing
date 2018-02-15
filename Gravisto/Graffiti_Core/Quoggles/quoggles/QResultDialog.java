package quoggles;

import java.util.Collection;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import quoggles.auxiliary.ResultTable;
import quoggles.auxiliary.RowListener;

/**
 *
 */
public class QResultDialog extends JDialog {

    /** The table where complex results are shown */
    private ResultTable resultTable = 
        new ResultTable(new RowListener());
    
    
    /**
     * Constructor.
     *
     * @see java.util.JDialog 
     */
    public QResultDialog(JDialog parent, String title, boolean modal) {
        super(parent, title, modal);

        setFocusable(false);
        JScrollPane scroll = new JScrollPane(resultTable.getTable());
        getContentPane().add(scroll);
        setSize(getPreferredSize());
    }
    
    
    /**
     * Empty the table.
     */
    public void clearTable() {
        resultTable.clearTable();
    }
    
    /**
     * Assure that the standard cells have correct widths.
     */
    public void updateWidths() {
        resultTable.updateWidths();
    }
    
//    /**
//     * Returns the <code>JTable</code> displaying the added rows.
//     * 
//     * @return the <code>JTable</code> displaying the added rows
//     */
//    public JTable getTable() {
//        return resultTable.getTable();
//    }

    /**
     * Add the contents of the given array as a row.
     * Adds a checkbox in front as the first column.
     * 
     * @param row row to add
     */
    public void addRow(Object[] row) {
        resultTable.addRow(row);
    }
    
    /**
     * Add the contents of the given list as a row.
     * Adds a checkbox in front as the first column.
     * 
     * @param row row to add
     */
    public void addRow(Collection row) {
        resultTable.addRow(row);
    }

}
