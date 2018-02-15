package quoggles.auxiliary;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.GraphElement;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionModel;


/**
 * Changes the selection in the Graffiti system according to the rows or cells
 * that get marked / unmarked.
 */
public class RowListener {

    /**
     * Count number of marked rows.
     */
    private int markedRows = 0;
    
    /**
     * Saves the number of times an element is marked. Avoids removing an
     * element from the selection too early.
     */
    private Map selElementsCnt = new HashMap();
    
    
    /**
     * Resets the listener.
     */
    public void reset() {
        selElementsCnt.clear();
        markedRows = 0;
    }

    /**
     * Add the element to the list of selected elements. If it already 
     * registered, add the saved number by one.
     * 
     * @param ge
     */
    private void incElementCount(Object ge) {
        Integer cnt = (Integer)selElementsCnt.get(ge);
        if (cnt == null) {
            selElementsCnt.put(ge, new Integer(1));
        } else {
            selElementsCnt.put(ge, new Integer(cnt.intValue() + 1));
        }
    }
    
    /**
     * If the element has previously been selected, decrease the number of 
     * times it had been selected by one. If this number then reaches zero,
     * the element should vanish from the selection and this method returns
     * true.
     * 
     * @param ge
     * @return true if the element should be removed from the selection.
     */
    private boolean decElementCount(Object ge) {
        Integer cnt = (Integer)selElementsCnt.get(ge);
        if (cnt != null) {
            int newVal = cnt.intValue() - 1;
            selElementsCnt.put(ge, new Integer(newVal));
            if (newVal > 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Every graph element found in the given object (searched recursively if
     * any collections are involved) is added to or removed from the given
     * selection, according to the given boolean value.
     * Updates <code>selElementsCnt</code>.
     * 
     * @param val the object to be searched
     * @param sel the selection to be changed
     * @param add if <code>true</code>, elements are added, otherwise they are
     * removed from the selection
     */
    private void changeSelectionCell(Object val, Selection sel, boolean add) {
        if (val instanceof GraphElement) {
            if (add) {
                incElementCount(val);
                sel.add((GraphElement)val);
            } else if (decElementCount(val)) {
                sel.remove((GraphElement)val);
            }

        } else if (val instanceof Collection) {
            Iterator it = ((Collection)val).iterator();
            while (it.hasNext()) {
                Object o = it.next();
                changeSelectionCell(o, sel, add);
            }
        }
    }
    
    /**
     * Get the selection model of the active editor session.
     * 
     * @return
     */
    private SelectionModel getSelectionModel() {
        SelectionModel selModel = GraffitiSingleton.getInstance()
            .getMainFrame().getActiveEditorSession().getSelectionModel();
        return selModel;
    }

    /**
     * Called if one single cell is marked.
     * 
     * @param val
     */
    public void cellMarked(Object val) {
        SelectionModel selModel = getSelectionModel();
        changeSelectionCell(val, selModel.getActiveSelection(), true);
        selModel.selectionChanged();
    }
    
    /**
     * Called if one single cell is unmarked.
     * 
     * @param val
     */
    public void cellUnmarked(Object val) {
        SelectionModel selModel = getSelectionModel();
        changeSelectionCell(val, selModel.getActiveSelection(), false);
        selModel.selectionChanged();
    }
    
    /**
     * Add <code>GraphElement</code>s contained in the row to the active 
     * selection.
     * 
     * @param row
     */
    public void rowMarked(Object[] row) {
        SelectionModel selModel = getSelectionModel();
        Selection selection = selModel.getActiveSelection();
        
        if (markedRows <= 0) {
            selection.clear();
        }
        markedRows++;
        
        for (int i = 2; i < row.length; i++) {
            changeSelectionCell(row[i], selection, true);
        }
        selModel.selectionChanged();
    }

    /**
     * Remove <code>GraphElement</code>s contained in the row from the active 
     * selection (if not selected by other rows).
     * 
     * @param row
     */
    public void rowUnmarked(Object[] row) {
        SelectionModel selModel = getSelectionModel();
        Selection selection = selModel.getActiveSelection();

        markedRows--;
        if (markedRows < 0) {
            markedRows = 0;
        }
        if (markedRows == 0) {
            selection.clear();
            selElementsCnt.clear();
        } else {
            for (int i = 1; i < row.length; i++) {
                changeSelectionCell(row[i], selection, false);
            }
        }

        selModel.selectionChanged();
    }

}
