package org.graffiti.plugins.tools.scripted.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugins.tools.scripted.ScriptedTool;
import org.graffiti.plugins.tools.toolcustomizer.ToolEditor;
import org.graffiti.util.VoidCallback;

public class ScriptedToolEditor<T extends InteractiveView<T>> implements
        ToolEditor {
    private JPanel panel;
    private JTextArea textArea;
    private boolean isReadOnly;
    private ScriptedTool<T> tool;
    private VoidCallback<Object> modificationCallback;

    public ScriptedToolEditor(ScriptedTool<T> tool) {
        this.tool = tool;
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setText(tool.getSource());
        isReadOnly = tool.isReadOnly();

        if (isReadOnly) {
            textArea.setEditable(false);
            textArea.setBackground(Color.WHITE);
        } else {
            textArea.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    changed();
                }

                public void insertUpdate(DocumentEvent e) {
                    changed();
                }

                public void removeUpdate(DocumentEvent e) {
                    changed();
                }
            });
        }
        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    public JPanel getPanel() {
        return panel;
    }

    public void saveChanges() {
        if (isReadOnly)
            return;
        tool.setSource(textArea.getText());
    }

    public void setModificationCallback(VoidCallback<Object> callback) {
        modificationCallback = callback;
    }

    private void changed() {
        if (modificationCallback != null) {
            modificationCallback.call(null);
        }
    }
}
