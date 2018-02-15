package org.graffiti.plugin;

import java.awt.Component;
import java.util.Stack;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author C. Klukas
 * 
 *         Adds recursivly a tool tip text to a JComponent or to JPanels
 *         Sub-Components. If a JCheckbox is found with no Text, the given
 *         toolTipText is used as the new descriptive text. The first character
 *         of the toolTipText is changed to upper case.
 */
public class ToolTipHelper {
    public static void addToolTip(JComponent jcomp, String toolTipText) {
        // C. Klukas: add tooltip to the right column, too
        if (toolTipText == null)
            return;

        if (toolTipText.length() >= 1) {
            toolTipText = toolTipText.substring(0, 1).toUpperCase()
                    + toolTipText.substring(1);
        }

        Stack<Component> s = new Stack<Component>();
        s.add(jcomp);
        while (!s.empty()) {
            Object se = s.pop();
            if (se instanceof JPanel) {
                for (Component c : ((JPanel) se).getComponents()) {
                    s.add(c);
                }
            } else if (se instanceof JComponent) { // && !(se instanceof
                                                   // JCheckBox)
                ((JComponent) se).setToolTipText(toolTipText);
            }
            if (se instanceof JCheckBox) {
                JCheckBox jc = (JCheckBox) se;
                if (jc.getText().length() == 0) {
                    jc.setText(toolTipText);
                    jc.setToolTipText(null);
                }
            }
        }

    }
}
