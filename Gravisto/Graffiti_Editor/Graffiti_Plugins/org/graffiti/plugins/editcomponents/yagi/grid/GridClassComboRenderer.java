package org.graffiti.plugins.editcomponents.yagi.grid;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

/**
 * {@code ListCellRenderer}, which customizes the rendering of {@code
 * GridClassComboAdapter}s.
 * 
 * @author Andreas Glei&szlig;ner
 * @see GridClassComboAdapter
 */
class GridClassComboRenderer extends JPanel implements ListCellRenderer {
    /**
     * 
     */
    private static final long serialVersionUID = 5079387980578646489L;
    /*
     * Magic thingies used by DefaultListCellRenderer.
     */
    private static final Color DROP_CELL_BACKGROUND = UIManager
            .getColor("List.dropCellBackground");
    private static final Color DROP_CELL_FOREGROUND = UIManager
            .getColor("List.dropCellForeground");
    private static final Border FOCUS_SELECTED_BORDER = UIManager
            .getBorder("List.focusSelectedCellHighlightBorder");
    private static final Border FOCUS_BORDER = UIManager
            .getBorder("List.focusCellHighlightBorder");
    private static final Border NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

    /**
     * Label displaying the icon.
     */
    private JLabel imageLabel;

    /**
     * Label displaying the name.
     */
    private JLabel nameLabel;

    /**
     * Label displaying the description.
     */
    private JLabel descriptionLabel;

    /**
     * Constructs a {@code GridClassComboRenderer}.
     */
    public GridClassComboRenderer() {
        super(new BorderLayout());
        imageLabel = new JLabel();
        imageLabel.setOpaque(true);
        add(imageLabel, BorderLayout.WEST);
        JPanel panel = new JPanel(new BorderLayout());
        nameLabel = new JLabel();
        nameLabel.setOpaque(true);
        Font font = nameLabel.getFont();
        nameLabel
                .setFont(new Font(font.getFamily(), Font.BOLD, font.getSize()));
        panel.add(nameLabel, BorderLayout.NORTH);
        descriptionLabel = new JLabel();
        descriptionLabel.setOpaque(true);
        panel.add(descriptionLabel, BorderLayout.CENTER);
        add(panel, BorderLayout.CENTER);
        setOpaque(true);
    }

    /**
     * {@inheritDoc}
     */
    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
        setComponentOrientation(list.getComponentOrientation());
        Color bg = null;
        Color fg = null;

        JList.DropLocation dropLocation = list.getDropLocation();
        if (dropLocation != null && !dropLocation.isInsert()
                && dropLocation.getIndex() == index) {

            bg = DROP_CELL_BACKGROUND;
            fg = DROP_CELL_FOREGROUND;

            isSelected = true;
        }

        if (isSelected) {
            if (bg == null) {
                bg = list.getSelectionBackground();
            }
            if (fg == null) {
                fg = list.getSelectionForeground();
            }
        } else {
            bg = list.getBackground();
            fg = list.getForeground();
        }

        setEnabled(list.isEnabled());

        Border border = null;
        if (cellHasFocus) {
            if (isSelected) {
                border = FOCUS_SELECTED_BORDER;
            }
            if (border == null) {
                border = FOCUS_BORDER;
            }
        } else {
            border = NO_FOCUS_BORDER;
        }
        setBorder(border);

        if (value instanceof GridClassComboAdapter) {
            GridClassComboAdapter gcca = (GridClassComboAdapter) value;
            imageLabel.setIcon(gcca.getIcon());
            nameLabel.setText(gcca.getName());
            descriptionLabel.setText(gcca.getDescription());
        } else {
            imageLabel.setIcon(GridClassComboAdapter.DEFAULT_ICON);
            nameLabel.setText("");
            descriptionLabel.setText("");
        }
        imageLabel.setBackground(bg);
        nameLabel.setBackground(bg);
        descriptionLabel.setBackground(bg);
        imageLabel.setForeground(fg);
        nameLabel.setForeground(fg);
        descriptionLabel.setForeground(fg);
        return this;
    }
}