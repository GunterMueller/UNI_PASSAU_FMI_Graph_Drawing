package org.graffiti.plugins.tools.toolcustomizer.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import org.graffiti.plugin.tool.Tool;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugins.tools.toolcustomizer.CustomizableTool;
import org.graffiti.plugins.tools.toolcustomizer.ToolCustomizerPlugin;
import org.graffiti.plugins.tools.toolcustomizer.ToolEditor;
import org.graffiti.util.VoidCallback;

class BehaviorDialog<T extends InteractiveView<T>> extends JDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 6020548703449703972L;
    private ToolEditor editor;
    private boolean isModified;
    private CustomizableTool tool;

    private JButton saveButton;

    public BehaviorDialog(ConfigurationDialog<T> dialog, CustomizableTool tool) {
        super(dialog, String.format(ToolCustomizerPlugin
                .getString("editwindow.title"), tool
                .getCustomizingDescription(), ((Tool<?>) tool).getName()), true);
        this.tool = tool;
        setLocationByPlatform(true);
        editor = tool.createEditor();
        setSize(new Dimension(800, 600));
        setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        ActionListener saveAction = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        };

        saveButton = new JButton(ToolCustomizerPlugin
                .createIcon("images/save.png"));
        saveButton.setEnabled(false);
        saveButton.addActionListener(saveAction);
        toolBar.add(saveButton);
        Object saveActionKey = new Object();
        saveButton.getInputMap().put(
                KeyStroke.getKeyStroke('s', InputEvent.CTRL_MASK),
                saveActionKey);
        add(toolBar, BorderLayout.NORTH);
        add(editor.getPanel(), BorderLayout.CENTER);
        isModified = false;
        editor.setModificationCallback(new VoidCallback<Object>() {
            public void call(Object t) {
                if (!isModified) {
                    isModified = true;
                    saveButton.setEnabled(true);
                }
            }
        });

    }

    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING && isModified) {
            int res = JOptionPane.showConfirmDialog(this, String.format(
                    ToolCustomizerPlugin
                            .getString("editwindow.confirm.message"),
                    ((Tool<?>) tool).getName()), ToolCustomizerPlugin
                    .getString("editwindow.confirm.title"),
                    JOptionPane.YES_NO_CANCEL_OPTION);

            if (res == JOptionPane.YES_OPTION) {
                saveChanges();
            } else if (res == JOptionPane.CANCEL_OPTION)
                return;
        }
        super.processWindowEvent(e);
    }

    private void saveChanges() {
        editor.saveChanges();
        saveButton.setEnabled(false);
        isModified = false;
    }
}
