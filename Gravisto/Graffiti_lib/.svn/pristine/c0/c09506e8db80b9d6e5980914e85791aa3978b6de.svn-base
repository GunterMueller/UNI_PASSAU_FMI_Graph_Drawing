package org.graffiti.plugins.tools.toolcustomizer.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.graffiti.plugin.tool.Tool;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugins.tools.toolcustomizer.CustomizableTool;
import org.graffiti.plugins.tools.toolcustomizer.ToolCustomizerPlugin;

class ToolPanel<T extends InteractiveView<T>> extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = -6629010055766337875L;

    private static Icon EDIT_ICON = ToolCustomizerPlugin
            .createIcon("images/edit.png");

    private ConfigurationDialog<T> dialog;

    private GridBagLayout gridBagLayout;
    private GridBagConstraints gridBagConstraints;

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JLabel idLabel;
    JButton iconButton;
    private JCheckBox hideCheckBox;
    private JLabel pluginLabel;
    private JPanel behaviorPanel;

    Tool<T> tool;

    public ToolPanel(final ConfigurationDialog<T> dialog) {
        this.dialog = dialog;

        setBorder(BorderFactory.createTitledBorder(ToolCustomizerPlugin
                .getString("window.tool")));

        gridBagLayout = new GridBagLayout();
        gridBagConstraints = new GridBagConstraints();
        setLayout(gridBagLayout);
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);

        nameField = new JTextField();
        nameField.setBackground(Color.WHITE);
        add(ToolCustomizerPlugin.getString("window.toolname"), nameField, true);

        descriptionArea = new JTextArea();
        descriptionArea.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(0, 80));
        add(ToolCustomizerPlugin.getString("window.tooldescription"),
                scrollPane, true);

        idLabel = new JLabel();
        add(ToolCustomizerPlugin.getString("window.toolid"), idLabel, true);

        iconButton = new JButton();
        iconButton.setPreferredSize(new Dimension(32, 32));
        add(ToolCustomizerPlugin.getString("window.toolimage"), iconButton,
                false);
        hideCheckBox = new JCheckBox("");
        add(ToolCustomizerPlugin.getString("window.toolhidden"), hideCheckBox,
                true);
        pluginLabel = new JLabel("");
        add(ToolCustomizerPlugin.getString("window.toolplugin"), pluginLabel,
                true);
        behaviorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        add(ToolCustomizerPlugin.getString("window.toolbehavior"),
                behaviorPanel, true, true);

        // addRestPanel();

        nameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeName();
            }
        });
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                changeName();
            }
        });

        descriptionArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                changeDescription();
            }
        });

        iconButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                changeIcon();
            }
        });

        hideCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tool == null)
                    return;
                tool.setHidden(hideCheckBox.isSelected());
            }
        });
    }

    public void setTool(Tool<T> tool) {
        this.tool = tool;
        if (tool == null) {
            setVisible(false);
            return;
        }
        setVisible(true);
        boolean isReadOnly = tool.isReadOnly();
        nameField.setText(tool.getName());
        nameField.setEditable(!isReadOnly);
        descriptionArea.setText(tool.getDescription());
        descriptionArea.setEditable(!isReadOnly);
        idLabel.setText(tool.isDeleted() ? "(" + tool.getId() + ") DELETED"
                : tool.getId());
        iconButton.setIcon(tool.getIcon());
        iconButton.setEnabled(!isReadOnly);
        hideCheckBox.setSelected(tool.isHidden());

        if (tool.isDeleted()) {
            pluginLabel.setText(ToolCustomizerPlugin
                    .getString("window.toolplugin.deleted"));
        } else {
            String pluginName = tool.getProvidingPlugin();
            if (pluginName == null) {
                pluginName = ToolCustomizerPlugin
                        .getString("window.toolplugin.unknown");
            }
            pluginLabel.setText(pluginName);
        }

        behaviorPanel.removeAll();
        JLabel behaviorLabel = new JLabel("", SwingConstants.LEFT);
        behaviorPanel.add(behaviorLabel, BorderLayout.CENTER);
        if (tool.isDummy()) {
            behaviorLabel.setText(ToolCustomizerPlugin
                    .getString("window.toolbehavior.dummy"));
        } else if (tool.isDeleted()) {
            behaviorLabel.setText(ToolCustomizerPlugin
                    .getString("window.toolbehavior.deleted"));
        } else if (!(tool instanceof CustomizableTool)) {
            behaviorLabel.setText(String.format(ToolCustomizerPlugin
                    .getString("window.toolbehavior.nocustom"), tool.getClass()
                    .getName()));
        } else {
            final CustomizableTool ct = (CustomizableTool) tool;
            behaviorLabel.setText(ct.getCustomizingDescription());
            JButton editButton = new JButton(ToolCustomizerPlugin
                    .getString("window.toolbehavior.edit"), EDIT_ICON);
            editButton.setPreferredSize(new Dimension(80, 30));
            editButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    (new BehaviorDialog<T>(dialog, ct)).setVisible(true);
                }
            });
            behaviorPanel.add(editButton);
            behaviorPanel.invalidate();
        }

        repaint();
    }

    public void toolAppearanceChanged() {
        setTool(tool);
    }

    public void add(String labelText, JComponent component,
            boolean fillHorizontal) {
        add(labelText, component, fillHorizontal, false);
    }

    public void add(String labelText, JComponent component,
            boolean fillHorizontal, boolean fillVertical) {
        JLabel label = new JLabel(labelText, SwingConstants.RIGHT);
        gridBagConstraints.gridwidth = GridBagConstraints.RELATIVE;
        gridBagConstraints.weightx = 0.0;
        gridBagConstraints.fill = GridBagConstraints.BOTH;

        gridBagConstraints.anchor = fillVertical ? GridBagConstraints.NORTHEAST
                : GridBagConstraints.EAST;
        gridBagLayout.setConstraints(label, gridBagConstraints);
        add(label);
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.fill = fillHorizontal ? GridBagConstraints.BOTH
                : GridBagConstraints.NONE;
        gridBagConstraints.anchor = fillVertical ? GridBagConstraints.NORTHWEST
                : GridBagConstraints.WEST;
        if (fillVertical) {
            gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
            gridBagConstraints.weighty = 1;
        }
        gridBagLayout.setConstraints(component, gridBagConstraints);
        add(component);
    }

    private void changeName() {
        if (tool == null || tool.isReadOnly())
            return;
        tool.setName(nameField.getText());
        dialog.toolAppearanceChanged();
    }

    private void changeDescription() {
        if (tool == null || tool.isReadOnly())
            return;
        tool.setDescription(descriptionArea.getText());
    }

    private void changeIcon() {
        if (tool == null || tool.isReadOnly())
            return;
        JFileChooser fileChooser = new JFileChooser();
        String iconPath = tool.getIconPath();
        if (iconPath != null) {
            fileChooser.setCurrentDirectory(new File(iconPath));
        }
        int ret = fileChooser.showOpenDialog(ToolPanel.this.dialog);
        if (ret == JFileChooser.APPROVE_OPTION) {
            tool.setIconPath(fileChooser.getSelectedFile().toString());
            dialog.toolAppearanceChanged();
        }
    }
}
