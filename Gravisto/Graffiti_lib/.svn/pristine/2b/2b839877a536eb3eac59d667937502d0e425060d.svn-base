package quoggles.auxiliary;

import java.awt.Component;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.util.GeneralUtils;


/**
 * A JTable with special renderers for (collections of) 
 * <code>GraphElements</code>.
 */
public class ResultJTable extends JTable {
    
    private final TableCellRenderer collectionRenderer =
        new MyCollectionCellRenderer();
    
    private final TableCellRenderer graphElementRenderer =
        new MyGraphElementCellRenderer();
    
    private final TableCellRenderer boolElementRenderer =
        new MyBoolElementCellRenderer();
    
//    private final TableCellRenderer centerStringRenderer =
//        new MyCenterStringCellRenderer();
        
    private final MyCenterCellRenderer centerRenderer =
        new MyCenterCellRenderer();
        
    private final MyNullCellRenderer nullCellRenderer =
        new MyNullCellRenderer();
        
    private final TableCellRenderer emptyCellRenderer =
        new MyEmptyCellRenderer();
    
    private final int showSizeTill = 0;

    private int recDepth = 0;
    

    /**
     * @param dm
     */
    public ResultJTable(TableModel dm) {
        super(dm);
    }
    

    /**
     * @see javax.swing.JTable#getCellRenderer(int, int)
     */
    public TableCellRenderer getCellRenderer(int row, int column) {
        Object val = getValueAt(row, column);
        if (val == null) {
            return nullCellRenderer;
            //return super.getCellRenderer(row, column);
        } else if (val == quoggles.constants.QConstants.EMPTY) {
            return emptyCellRenderer;
        }
        
        
//        if (column == 1) {
//            return centerStringRenderer;
//        } else
        if (val instanceof Collection) {
            return collectionRenderer;
        } else if (val instanceof GraphElement) {
            return graphElementRenderer;
        } else if (row > 0 && val instanceof Boolean) {
            return boolElementRenderer;
        } else if (row > 0) {
            return centerRenderer;
        } else {
            return super.getCellRenderer(row, column);
        }
    }
    
    /**
     * Sets colors for selected and not selected cells respectively.
     * 
     * @param label
     * @param isSelected
     */
    private void setLabelProps(JLabel label, boolean isSelected) {
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        if (isSelected) {
           label.setForeground(getSelectionForeground());
           label.setBackground(getSelectionBackground());
        } else {
            label.setForeground(getForeground());
            label.setBackground(getBackground());
        }
    }

    /**
     * Returns a better <code>String</code> representation than 
     * <code>toString</code> would yield.
     * 
     * @param col
     * @return
     */
    private String renderCollection(Collection col) {
        int size = col.size();
        if (size == 0) {
            if (recDepth <= showSizeTill) {
                return " 0: [] ";
            } else {
                return "[] ";
            }
        } else {
            StringBuffer strList = null;
            if (recDepth <= showSizeTill) {
                strList = new StringBuffer(" " + size + ": [ ");
            } else {
                strList = new StringBuffer("[");
            }
            for (Iterator it = col.iterator(); it.hasNext();) {
                Object obj = it.next();
                if (obj == null) {
                    strList.append(renderNull() + ", ");

                } else if (obj == quoggles.constants.QConstants.EMPTY) {
                    strList.append(renderEmpty() + ", ");

                } else if (obj instanceof GraphElement) {
                    strList.append(renderGraphElement((GraphElement)obj) +
                        ", ");
                    
                } else if (obj instanceof Collection) {
                    recDepth++;
                    strList.append(renderCollection((Collection)obj) + 
                        ", ");
                    recDepth--;
                        
                } else if (obj instanceof Boolean) {
                    strList.append(renderBoolean((Boolean)obj) + ", ");
                    
                } else {
                    strList.append(obj.toString() + ", ");
                }
            }
            strList.setLength(strList.length() - 2);
            
            return strList.toString() + " ] ";
        }
    }

    /**
     * Returns a better <code>String</code> representation than 
     * <code>toString</code> would yield.
     * 
     * @param col
     * @return
     */
    private String renderGraphElement(GraphElement ge) {
        Attribute attr = GeneralUtils.searchForAttribute
            (ge.getAttributes(), LabelAttribute.class);
        if (attr != null) {
            StringBuffer str = new StringBuffer();
            if (ge instanceof Node) {
                str.append("Node (");
            } else {
                str.append("Edge (");
            }
            str.append(((LabelAttribute)attr).getLabel() + ")");
            return str.toString();
        } else {
            if (ge instanceof Node) {
                return "Node";
            } else {
                return "Edge";
            }
        }
    }
        
    /**
     * Returns a better <code>String</code> representation than 
     * <code>toString</code> would yield.
     * 
     * @param col
     * @return
     */
    private String renderBoolean(Boolean bool) {
        return bool.toString();
    }
        
    /**
     * Returns a better <code>String</code> representation than 
     * <code>toString</code> would yield.
     * 
     * @param col
     * @return
     */
    private String renderNull() {
        return "-null-";
    }
        
    /**
     * Returns a better <code>String</code> representation than 
     * <code>toString</code> would yield.
     * 
     * @param col
     * @return
     */
    private String renderEmpty() {
        return "-";
    }
        

    /**
     * Renderer used for arbitrary types (centers <code>toString()</code>).
     */
    class MyCenterCellRenderer implements TableCellRenderer {

        /**
         * @see javax.swing.table.TableCellRenderer#
         * getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, 
         * boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent
            (JTable tab, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column) {

            JLabel label = new JLabel(value.toString());
            setLabelProps(label, isSelected);

            return label;
        }
    }


    /**
     * Renderer used cells containing <code>null</code>.
     */
    class MyNullCellRenderer implements TableCellRenderer {

        /**
         * @see javax.swing.table.TableCellRenderer#
         * getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, 
         * boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent
            (JTable tab, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column) {

            JLabel label = new JLabel(renderNull());
            setLabelProps(label, isSelected);

            return label;
        }
    }


    /**
     * Renderer used for <code>Collection</code>.
     */
    class MyCollectionCellRenderer implements TableCellRenderer {

        /**
         * @see javax.swing.table.TableCellRenderer#
         * getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, 
         * boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent
            (JTable tab, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column) {

            JLabel label = new JLabel(renderCollection((Collection)value));
            setLabelProps(label, isSelected);

            return label;
        }
    }


    /**
     * Renderer used for <code>GraphElement</code>s.
     */
    class MyGraphElementCellRenderer implements TableCellRenderer {

        /**
         * @see javax.swing.table.TableCellRenderer#
         * getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, 
         * boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent
            (JTable tab, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column) {

            JLabel label = new JLabel(
                renderGraphElement((GraphElement)value));
            setLabelProps(label, isSelected);
        
            return label;
        }
    }


    /**
     * Renderer used for <code>Boolean</code>s outside first column.
     */
    class MyBoolElementCellRenderer implements TableCellRenderer {

        /**
         * @see javax.swing.table.TableCellRenderer#
         * getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, 
         * boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent
            (JTable tab, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column) {

            JLabel label = new JLabel(renderBoolean((Boolean)value));
            setLabelProps(label, isSelected);

            return label;
        }
    }


    /**
     * Renderer used to center <code>String</code>s in second column.
     */
    class MyCenterStringCellRenderer implements TableCellRenderer {

        /**
         * @see javax.swing.table.TableCellRenderer#
         * getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, 
         * boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent
            (JTable tab, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column) {

            JLabel label = new JLabel(value.toString());
            setLabelProps(label, isSelected);
        
            return label;
        }
    }


    /**
     * Renderer used to display empty cells.
     */
    class MyEmptyCellRenderer implements TableCellRenderer {

        /**
         * @see javax.swing.table.TableCellRenderer#
         * getTableCellRendererComponent(javax.swing.JTable, java.lang.Object, 
         * boolean, boolean, int, int)
         */
        public Component getTableCellRendererComponent
            (JTable tab, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column) {

            JLabel label = new JLabel(renderEmpty());
            setLabelProps(label, isSelected);
         
            return label;
        }
    }
}