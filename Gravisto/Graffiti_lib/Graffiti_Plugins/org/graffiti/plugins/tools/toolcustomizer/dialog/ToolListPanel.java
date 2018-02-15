package org.graffiti.plugins.tools.toolcustomizer.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.graffiti.plugin.tool.Tool;
import org.graffiti.plugin.tool.ToolFactory;
import org.graffiti.plugin.tool.ToolRegistry;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.ViewFamily;
import org.graffiti.plugins.tools.toolcustomizer.CustomizableToolFactory;
import org.graffiti.plugins.tools.toolcustomizer.ToolCustomizerPlugin;

class ToolListPanel<T extends InteractiveView<T>> extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1803683939419127147L;

    private class DeleteAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 7027255995624726065L;

        private final String confirmMessage;

        private final String confirmTitle;

        public DeleteAction() {
            super(ToolCustomizerPlugin.getString("window.deletetool"),
                    ToolCustomizerPlugin.createIcon("images/delete.png"));
            confirmMessage = ToolCustomizerPlugin
                    .getString("window.confirm.deletetoolmessage");
            confirmTitle = ToolCustomizerPlugin
                    .getString("window.confirm.deletetooltitle");
        }

        public void actionPerformed(ActionEvent e) {
            Tool<?> tool = (Tool<?>) list.getSelectedValue();
            if (tool == null)
                return;
            int res = JOptionPane.showConfirmDialog(dialog, confirmMessage,
                    confirmTitle, JOptionPane.YES_NO_OPTION);
            if (res != JOptionPane.YES_OPTION)
                return;
            tool.delete();
            dialog.toolAppearanceChanged();
        }
    };

    private class UpAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 149408547048402731L;

        public UpAction() {
            super(ToolCustomizerPlugin.getString("window.uptool"),
                    ToolCustomizerPlugin.createIcon("images/up.png"));
        }

        public void actionPerformed(ActionEvent e) {
            int selectedIndex = list.getSelectedIndex();
            if (selectedIndex > 0) {
                Tool<?> tool = (Tool<?>) list.getSelectedValue();
                tool.moveUp();
                exchange(selectedIndex, selectedIndex - 1);
                list.setSelectedIndex(selectedIndex - 1);
            }
        }
    };

    private class DownAction extends AbstractAction {
        /**
         * 
         */
        private static final long serialVersionUID = 1865385697231298229L;

        public DownAction() {
            super(ToolCustomizerPlugin.getString("window.downtool"),
                    ToolCustomizerPlugin.createIcon("images/down.png"));
        }

        public void actionPerformed(ActionEvent e) {
            int selectedIndex = list.getSelectedIndex();
            if (selectedIndex != -1 && selectedIndex < listModel.getSize() - 1) {
                Tool<?> tool = (Tool<?>) list.getSelectedValue();
                tool.moveDown();
                exchange(selectedIndex, selectedIndex + 1);
                list.setSelectedIndex(selectedIndex + 1);
            }
        }
    };

    private ConfigurationDialog<T> dialog;

    private DeleteAction deleteAction;

    private UpAction upAction;

    private DownAction downAction;

    private DefaultListModel listModel;

    private JList list;

    private JPopupMenu popupMenu;

    public ToolListPanel(ViewFamily<T> viewFamily,
            final ConfigurationDialog<T> dialog) {
        super(ConfigurationDialog.createBorderLayout());
        this.dialog = dialog;
        setBorder(BorderFactory.createTitledBorder(""));
        add(new JLabel(ToolCustomizerPlugin.getString("window.availabletools"),
                SwingConstants.CENTER), BorderLayout.NORTH);

        createPopupMenu(viewFamily);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        upAction = new UpAction();
        buttonPanel.add(createButton(upAction));
        downAction = new DownAction();
        buttonPanel.add(createButton(downAction));
        add(buttonPanel, BorderLayout.SOUTH);
        listModel = new DefaultListModel();
        for (Tool<T> tool : ToolRegistry.get().getTools(viewFamily)) {
            listModel.addElement(tool);
        }
        list = new JList(listModel);
        list.setCellRenderer(new ToolListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting())
                            return;
                        updateActions();
                        @SuppressWarnings("unchecked")
                        Tool<T> tool = (Tool<T>) list.getSelectedValue();
                        dialog.setTool(tool);
                    }
                });
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                showPopupIfApt(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showPopupIfApt(e);
            }

            private void showPopupIfApt(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    Point location = new Point(e.getX(), e.getY());
                    int index = list.locationToIndex(location);
                    Rectangle rectangle = list.getCellBounds(index, index);
                    if (index != -1 && rectangle != null
                            && rectangle.contains(location)) {
                        list.setSelectedIndex(index);
                    } else {
                        list.clearSelection();
                    }
                    popupMenu.show(list, location.x, location.y);
                }
            }
        });
        add(new JScrollPane(list), BorderLayout.CENTER);

        setMinimumSize(new Dimension(200, 0));
        setPreferredSize(new Dimension(200, 0));

        updateActions();
    }

    private void createPopupMenu(final ViewFamily<T> viewFamily) {
        popupMenu = new JPopupMenu();
        JMenu addMenu = new JMenu(ToolCustomizerPlugin
                .getString("window.addtool"));
        addMenu.setIcon(ToolCustomizerPlugin.createIcon("images/add.png"));
        addMenu.setEnabled(false);
        for (final ToolFactory factory : ToolRegistry.get().getToolFactories()) {
            if (factory.acceptsViewFamily(viewFamily)
                    && (factory instanceof CustomizableToolFactory)) {
                addMenu.setEnabled(true);
                CustomizableToolFactory ctf = (CustomizableToolFactory) factory;
                Icon icon = ctf.getAddMenuIcon();
                JMenuItem item = icon == null ? new JMenuItem(ctf
                        .getAddMenuText()) : new JMenuItem(
                        ctf.getAddMenuText(), icon);
                item.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        Tool<T> tool = ToolRegistry.get().createTool(
                                viewFamily, factory.getId());
                        tool.setName(ToolCustomizerPlugin
                                .getString("customToolName"));
                        listModel.addElement(tool);
                        setTool(tool);
                    }
                });
                addMenu.add(item);
            }
        }
        // if (addMenu.getS)
        popupMenu.add(addMenu);
        deleteAction = new DeleteAction();
        popupMenu.add(deleteAction);
    }

    private void updateActions() {
        int selectedIndex = list.getSelectedIndex();
        upAction.setEnabled(selectedIndex > 0);
        downAction.setEnabled(selectedIndex != -1
                && selectedIndex < listModel.getSize() - 1);
    }

    private JButton createButton(Action action) {
        JButton button = new JButton(action);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        return button;
    }

    public void setTool(Tool<T> tool) {
        list.setSelectedValue(tool, true);
    }

    private void exchange(int index1, int index2) {
        Tool<?> tool1 = (Tool<?>) listModel.get(index1);
        Tool<?> tool2 = (Tool<?>) listModel.get(index2);
        list.setValueIsAdjusting(true);
        listModel.set(index1, tool2);
        listModel.set(index2, tool1);
        list.setValueIsAdjusting(false);
    }

    protected void toolAppearanceChanged() {
        int index = list.getSelectedIndex();
        if (index == -1)
            return;
        listModel.set(index, listModel.elementAt(index));
    }
}
