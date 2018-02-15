package quoggles.auxiliary;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 *
 */
public class FlushRightRenderer extends DefaultListCellRenderer {

    /**
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent
     * (javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(JList list,
        Object value, int index, boolean isSelected, boolean cellHasFocus) {
            
        try {
            String vs = (String) value;
            value = vs.substring("org.graffiti.plugin.parameter.".length());
        } catch (ClassCastException cce) {
            // ignore
        }
        return super.getListCellRendererComponent(
            list, value, index, isSelected, cellHasFocus);
    }
}
