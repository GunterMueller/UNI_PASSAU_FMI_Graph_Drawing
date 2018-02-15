package org.graffiti.plugins.tools.toolcustomizer.dialog;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.graffiti.plugin.tool.Tool;

public class ToolListRenderer extends JLabel implements ListCellRenderer {
    /**
     * 
     */
    private static final long serialVersionUID = 2144333417274635212L;

    public ToolListRenderer() {
        setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        Tool<?> tool = (Tool<?>) value;

        if (isSelected) {
            if (tool.isDeleted()) {
                setBackground(new Color(150, 0, 0));
            } else {
                setBackground(list.getSelectionBackground());
            }
            setForeground(list.getSelectionForeground());
        } else {
            if (tool.isDeleted()) {
                setBackground(new Color(150, 0, 0, 150));
            } else {
                setBackground(list.getBackground());
            }
            setForeground(list.getForeground());
        }

        setIcon(tool.getIcon());
        setText(tool.getName());
        return this;
    }
}
