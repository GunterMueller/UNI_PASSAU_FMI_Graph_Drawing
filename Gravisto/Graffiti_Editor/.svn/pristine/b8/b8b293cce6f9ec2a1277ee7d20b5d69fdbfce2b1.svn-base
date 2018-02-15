package quoggles.auxiliary;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import quoggles.constants.BoxStringConstants;

/**
 *
 */
public class StringConstantsRenderer extends DefaultListCellRenderer {

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent
     * (javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(
        JList list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus) {
        return super.getListCellRendererComponent(
            list,
            BoxStringConstants.get(((Integer)value).intValue()),
            index,
            isSelected,
            cellHasFocus);
    }
}
