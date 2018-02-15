package quoggles.auxiliary;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.AbstractTableModel;

/**
 * Contains a <code>JTable</code>. Provides <code>addRow</code> methods that
 * insert a checkbox as the first column. The table's model will call methods
 * of a <code>RowListener</code> if the checkboxes in the first column are 
 * clicked.
 */
public class ResultTable {

    /** The table used as result table */
    private ResultJTable table;
    
    /** Map of table rows */
    private Map dataMap;
    
    /** Current number of rows */
    private int rowCnt = 0;
    
    /** Current number of columns */
    private int colCnt = 0;
    
    /** Class that is informed when rows are "marked/unmarked" */
    private RowListener rowListener;
    
    private Object lastSelectedValue;
    
//    private int lastSelectedRowNr;
//    private int lastSelectedColNr;
    
    
    /**
     * Creates a new <code>ResultTable</code> registering the given
     * <code>RowListener</code>.
     */
    public ResultTable(RowListener rl) {
        super();
        table = new ResultJTable(new ResultTableModel());
        table.setPreferredScrollableViewportSize(new Dimension(600, 200));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCellSelectionEnabled(true);
        table.addMouseListener(new RTMouseListener());

        dataMap = new HashMap();
        rowListener = rl;
        lastSelectedValue = null;
//        lastSelectedRowNr = -1;
//        lastSelectedColNr = -1;
    }

    
    /**
     * Assure that the standard cells have correct widths.
     */
    public void updateWidths() {
//        int w0 = 40;
//        int w1 = 60;
//        if (table.getColumnCount() > 1) {
//            table.getColumnModel().getColumn(1).setMaxWidth(w1);
//            table.getColumnModel().getColumn(0).setMaxWidth(w0);
//            table.getColumnModel().getColumn(1).setMinWidth(w1);
//            table.getColumnModel().getColumn(0).setMinWidth(w0);
//            table.getColumnModel().getColumn(1).setPreferredWidth(w1);
//            table.getColumnModel().getColumn(0).setPreferredWidth(w0);
//        }
    }
    
    /**
     * Returns the <code>JTable</code> displaying the added rows.
     * 
     * @return the <code>JTable</code> displaying the added rows
     */
    public JTable getTable() {
        return table;
    }

    /**
     * Empties the table.
     */
    public void clearTable() {
        dataMap.clear();
        rowCnt = 0;
        colCnt = 0;
        ((ResultTableModel)table.getModel())
            .fireTableStructureChanged();
        lastSelectedValue = null;
//        lastSelectedRowNr = -1;
//        lastSelectedColNr = -1;
        rowListener.reset();
    }
    
    /**
     * Add the contents of the given array as a row.
     * Adds a checkbox in front as the first column.
     * 
     * @param row the row to add
     */
    public void addRow(Object[] row) {
        Object[] expRow = null;
        int i = 2;
        if (row == null) {
            expRow = new Object[2];
        } else {
            expRow = new Object[row.length + 2];
            //System.arraycopy(row, 0, expRow, 2, row.length);
            for (int j = 0; j < row.length; j++) {
                if (row[j] != null) {
                    expRow[j + 2] = row[j];
                    i++;
                }
            }
        }
        
        expRow[0] = new Boolean(false);
        expRow[1] = new Integer(rowCnt);
        dataMap.put(new Integer(rowCnt), expRow);
        colCnt = Math.max(colCnt, i);
        rowCnt++;
        ((ResultTableModel)table.getModel())
            .fireTableStructureChanged();
    }
    
    /**
     * Add the contents of the given list as a row.
     * Adds a checkbox in front as the first column.
     * 
     * @param row the row to add
     */
    public void addRow(Collection row) {
        Object[] expRow = null;
        int i = 2;
        if (row == null) {
            expRow = new Object[2];
        } else {
            expRow = new Object[row.size() + 2];
            for (Iterator it = row.iterator(); it.hasNext();) {
                Object o = it.next();
                if (o != null) {
                    expRow[i++] = o;
                }
            }
        }

        expRow[0] = new Boolean(false);
        expRow[1] = new Integer(rowCnt);
        dataMap.put(new Integer(rowCnt), expRow);
        colCnt = Math.max(colCnt, i);
        rowCnt++;
        ((ResultTableModel)table.getModel())
            .fireTableStructureChanged();
    }


    /**
     * The model of the <code>ResultTable</code>.
     */
    class ResultTableModel extends AbstractTableModel {

        /**
         * Gives first two columns special headings.
         * 
         * @see javax.swing.table.TableModel#getColumnName(int)
         */
        public String getColumnName(int column) {
//            if (column == 0) {
//                return "SEL.";
//            } else if (column == 1) {
//                return "ROW NR.";
//            }
            return String.valueOf(column);
            // return super.getColumnName(column);
            //return "RESULT";
        }

        /**
         * Only first row editable.
         * 
         * @see javax.swing.table.TableModel#isCellEditable(int, int)
         */
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (rowIndex == 0) {
                return true;
            }
            return false;
        }

        /**
         * @see javax.swing.table.TableModel#getRowCount()
         */
        public int getRowCount() {
            return colCnt - 1;
        }

        /**
         * @see javax.swing.table.TableModel#getColumnCount()
         */
        public int getColumnCount() {
            return rowCnt;
        }

        /**
         * @see javax.swing.table.TableModel#getValueAt(int, int)
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= 1) {
                // ignore old row nr cells
                rowIndex++;
            }
            Object[] row = (Object[])dataMap.get(new Integer(columnIndex));
            if (row == null) {
                return null;
            }
            if (row.length - 1 < rowIndex) {
                return "";
            } else {
                return row[rowIndex];
            }
        }
        
        /**
         * @see javax.swing.table.TableModel#getColumnClass(int)
         */
        public Class getColumnClass(int columnIndex) {
//            if (columnIndex == 0) {
//                return Boolean.class;
//            } else if (columnIndex == 1) {
//                return String.class;
//            }
            
            Object val = null;
            try {
                val = getValueAt(0, columnIndex);
            } catch (Exception e) {
                return Object.class;
            }
            if (val == null) {
                return Object.class;
            } else {
                return val.getClass();
            }
        }

        /**
         * Additionally calls methods of the registered
         * <code>RowListener</code> if a value on the first row has been changed.
         * 
         * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
         */
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            super.setValueAt(aValue, columnIndex, rowIndex);
            
            if (rowIndex >= 1) {
                // ignore old row nr cells
                rowIndex++;
            }

            Object[] row = (Object[])dataMap.get(new Integer(columnIndex));
            row[rowIndex] = aValue;
            fireTableCellUpdated(columnIndex, rowIndex);
            
            if (rowIndex == 0) {
                if (((Boolean)aValue).booleanValue()) {
                    rowListener.rowMarked(row);
                } else {
                    rowListener.rowUnmarked(row);
                }
            }
        }

    }


    /**
     * Mouse clicks are forwarded to the <code>rowListener</code>. Values found
     * in the table are added to or removed from the selection according to the
     * cell on which the user clicked.
     */
    class RTMouseListener extends MouseInputAdapter {
            
        /**
         * @see java.awt.event.MouseListener#mousePressed
         * (java.awt.event.MouseEvent)
         */
        public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            int rowNr = table.getSelectedColumn();
            int colNr = table.getSelectedRow();
            if (colNr >= 1) {
                // ignore old row nr
                colNr++;
            }
            
            if (colNr == 0) {
                lastSelectedValue = null;
//                lastSelectedRowNr = -1;
//                lastSelectedColNr = -1;
                // done by other listener
                return;
//                // (un)mark whole row
//                if (((Boolean)table.getValueAt(rowNr, 0)).booleanValue()) {
//                }
//                rowListener.rowMarked((Object[])dataMap.get(new Integer(rowNr)));
            }
            if (lastSelectedValue != null) {
//                if (lastSelectedColNr == 1) {
//                    rowListener.rowUnmarked((Object[])dataMap.get
//                        (new Integer(lastSelectedRowNr)));
//                } else {
                    rowListener.cellUnmarked(lastSelectedValue);
//                }
            }
//            if (colNr == 1) {
//                rowListener.rowMarked((Object[])dataMap.get(new Integer(rowNr)));
//                table.getSelectionModel().setSelectionInterval(rowNr, rowNr);
//                return;
//            }
            if (((Boolean)table.getValueAt(0, rowNr)).booleanValue()) {
                table.setValueAt(new Boolean(false), 0, rowNr);
//                rowListener.rowUnmarked
//                    ((Object[])dataMap.get(new Integer(rowNr)));
            }
            try {
                lastSelectedValue = 
                    ((Object[])dataMap.get(new Integer(rowNr)))[colNr];
            } catch (ArrayIndexOutOfBoundsException aioobe) {
                lastSelectedValue = null;
            } catch (NullPointerException npe) {
                lastSelectedValue = null;
            }
//            lastSelectedRowNr = rowNr;
//            lastSelectedColNr = colNr;
            rowListener.cellMarked(lastSelectedValue);
        }
    }

}