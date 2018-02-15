// =============================================================================
//
//   LabelDialog.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugins.views.fast.FastViewPlugin;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class LabelDialog extends JDialog {
    /**
     * 
     */
    private static final long serialVersionUID = -4394995564190397912L;
    private static final Pattern ENCOURAGED_ID_PATTERN = Pattern
            .compile("[0-9a-zA-Z]+");
    private static final Pattern TAG_SPACE_PATTERN = Pattern
            .compile(".*\\>[ ]*");

    private static enum StatusType {
        OK(null), WARNING("warning"), ERROR("error");

        private String icon;

        private StatusType(String icon) {
            this.icon = icon;
        }

        public ImageIcon getIcon() {
            if (icon == null)
                return null;
            else
                return new ImageIcon(FastViewPlugin.class.getResource("images/"
                        + icon + ".png"));
        }
    };

    private static enum IdStatus {
        OK(StatusType.OK), EMPTY(StatusType.ERROR), EXISTING_ATTRIBUTE(
                StatusType.ERROR), EXISTING_LABEL(StatusType.ERROR), DOTS(
                StatusType.ERROR), GRAPHICS(StatusType.WARNING), ALPHANUM(
                StatusType.WARNING);

        private StatusType type;

        private IdStatus(StatusType type) {
            this.type = type;
        }

        public String getDescription() {
            return FastViewPlugin.getString("labelDialog.idstatus." + name());
        }

        public ImageIcon getIcon() {
            return type.getIcon();
        }

        public StatusType getType() {
            return type;
        }
    };

    private static enum TextStatus {
        OK(StatusType.OK), CONFLICTING(StatusType.WARNING);

        private StatusType type;

        private TextStatus(StatusType type) {
            this.type = type;
        }

        public String getDescription() {
            return FastViewPlugin.getString("labelDialog.textstatus." + name());
        }

        public ImageIcon getIcon() {
            return type.getIcon();
        }

        public StatusType getType() {
            return type;
        }
    };

    private static final int GAP = 5;
    private static final Dimension BUTTON_SIZE = new Dimension(90, 24);
    private static final String ID_PREFIX = "label";
    private static LabelDialog singleton;
    private JButton okButton;
    private JButton resetButton;
    private JLabel statusLabel;
    private JTextField idField;
    private JTextArea textArea;
    private TextComponentProxy idFieldProxy;
    private TextComponentProxy textAreaProxy;
    private String resetId;
    private String resetText;
    private String currentId;
    private boolean conflictingTexts;
    private Set<GraphElement> elements;
    private IdStatus idStatus;
    private TextStatus textStatus;

    public static LabelDialog get() {
        if (singleton == null) {
            singleton = new LabelDialog();
        }
        return singleton;
    }

    private LabelDialog() {
        super(GraffitiSingleton.getInstance().getMainFrame(), FastViewPlugin
                .getString("labelDialog.title.create"), true);
        idStatus = IdStatus.OK;
        textStatus = TextStatus.OK;
        setLocationByPlatform(true);
        JPanel contentPane = new JPanel();
        contentPane.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP,
                GAP));
        setContentPane(contentPane);
        setLayout(new BorderLayout(GAP, GAP));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, GAP,
                GAP));
        buttonPanel.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        JButton cancelButton = new JButton(FastViewPlugin
                .getString("labelDialog.buttons.cancel"));
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setVisible(false);
            }
        });
        cancelButton.setPreferredSize(BUTTON_SIZE);
        buttonPanel.add(cancelButton);
        okButton = new JButton(FastViewPlugin
                .getString("labelDialog.buttons.ok"));
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                apply();
            }
        });
        okButton.setPreferredSize(BUTTON_SIZE);
        buttonPanel.add(okButton);
        resetButton = new JButton(FastViewPlugin
                .getString("labelDialog.buttons.reset"));
        resetButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resetComponents();
            }
        });
        resetButton.setPreferredSize(BUTTON_SIZE);
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);
        statusLabel = new JLabel("");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP,
                GAP));
        statusLabel.setVisible(false);
        add(statusLabel, BorderLayout.NORTH);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel idLabel = new JLabel(FastViewPlugin.getString("labelDialog.id"));
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(idLabel);
        idField = new JTextField();
        idField.setAlignmentX(Component.LEFT_ALIGNMENT);
        idField.setMinimumSize(new Dimension(400, 25));
        idField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        Font monospacedFont = new Font("Monospaced", Font.PLAIN, 12);
        idField.setFont(monospacedFont);
        idFieldProxy = new TextComponentProxy(idField) {
            @Override
            protected void onChange() {
                resetButton.setEnabled(true);
                checkId();
            }
        };
        panel.add(idField);
        panel.add(Box.createRigidArea(new Dimension(0, GAP)));
        JLabel textLabel = new JLabel(FastViewPlugin
                .getString("labelDialog.text"));
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(textLabel);
        textArea = new JTextArea();
        JScrollPane textPane = new JScrollPane(textArea);
        textPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        textPane.setMinimumSize(new Dimension(400, 75));
        textPane.setPreferredSize(new Dimension(400, 120));
        textArea.setFont(monospacedFont);
        textArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER
                        && (e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK) {
                    e.consume();
                    textArea.replaceSelection("\n");
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                if ((e.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK)
                    return;
                if (e.getKeyChar() == '\n') {
                    int pos = textArea.getSelectionStart();
                    String prefix = textArea.getText().substring(0, pos - 1);
                    if (!TAG_SPACE_PATTERN.matcher(prefix).matches()) {
                        textArea.insert("<br>", pos - 1);
                    }
                }
            }
        });
        textAreaProxy = new TextComponentProxy(textArea) {
            @Override
            protected void onChange() {
                resetButton.setEnabled(true);
                checkText();
            }
        };
        panel.add(textPane);
        add(panel, BorderLayout.CENTER);
        pack();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                textArea.grabFocus();
            }
        });
    }

    public void showCreate(Set<GraphElement> elements) {
        conflictingTexts = false;
        this.elements = elements;
        int i;
        boolean isIdContained = true;
        for (i = 0; isIdContained; i++) {
            isIdContained = false;
            for (GraphElement element : elements) {
                if (element.getAttributes().getCollection().containsKey(
                        ID_PREFIX + i)) {
                    isIdContained = true;
                    break;
                }
            }
        }
        resetId = ID_PREFIX + (i - 1);
        currentId = null;
        resetText = "";
        setTitle(FastViewPlugin.getString("labelDialog.title.create"));
        resetComponents();
        setVisible(true);
        this.elements = null;
    }

    public void showEdit(Set<GraphElement> elements, String id) {
        resetId = id;
        currentId = id;
        this.elements = elements;
        String text = null;
        conflictingTexts = false;
        for (GraphElement element : elements) {
            // Handle elements tolerantly. If they do not have the label
            // attributes, simply ignore them.
            try {
                Attribute attribute = element.getAttribute(id);
                if (!(attribute instanceof LabelAttribute)) {
                    continue;
                }
                LabelAttribute la = (LabelAttribute) attribute;
                if (text == null) {
                    text = la.getLabel();
                } else {
                    // Conflicting texts.
                    if (!text.equals(la.getLabel())) {
                        text = null;
                        conflictingTexts = true;
                        break;
                    }
                }
            } catch (AttributeNotFoundException e) {
            }
        }
        resetText = text == null ? "" : text;
        setTitle(FastViewPlugin.getString("labelDialog.title.edit"));
        resetComponents();
        setVisible(true);
        this.elements = null;
    }

    private void resetComponents() {
        idFieldProxy.setText(resetId);
        textAreaProxy.setText(resetText);
        resetButton.setEnabled(false);
        textArea.requestFocusInWindow();
        checkId();
        checkText();
    }

    private void checkId() {
        String id = idField.getText();
        if (id.length() == 0) {
            setIdStatus(IdStatus.EMPTY);
            return;
        }
        if (id.indexOf('.') != -1) {
            setIdStatus(IdStatus.DOTS);
            return;
        }
        if (!id.equals(resetId)) {
            for (GraphElement element : elements) {
                Attribute attribute = element.getAttributes().getCollection()
                        .get(id);
                if (attribute != null) {
                    if (attribute instanceof LabelAttribute) {
                        setIdStatus(IdStatus.EXISTING_LABEL);
                    } else {
                        setIdStatus(IdStatus.EXISTING_ATTRIBUTE);
                    }
                    return;
                }
            }
        }
        if (id.equals(GraphicAttributeConstants.GRAPHICS)) {
            setIdStatus(IdStatus.GRAPHICS);
        } else if (!ENCOURAGED_ID_PATTERN.matcher(id).matches()) {
            setIdStatus(IdStatus.ALPHANUM);
        } else {
            setIdStatus(IdStatus.OK);
        }
    }

    private void checkText() {
        if (conflictingTexts && !resetButton.isEnabled()) {
            setTextStatus(TextStatus.CONFLICTING);
        } else {
            setTextStatus(TextStatus.OK);
        }
    }

    void setIdStatus(IdStatus idStatus) {
        if (idStatus != this.idStatus) {
            this.idStatus = idStatus;
            onStatusChange();
        }
    }

    void setTextStatus(TextStatus textStatus) {
        if (textStatus != this.textStatus) {
            this.textStatus = textStatus;
            onStatusChange();
        }
    }

    private void onStatusChange() {
        StatusType idType = idStatus.getType();
        StatusType textType = textStatus.getType();
        if (idType == StatusType.ERROR
                || (textType != StatusType.ERROR && (idType == StatusType.WARNING || textType != StatusType.WARNING))) {
            statusLabel.setText(idStatus.getDescription());
            statusLabel.setIcon(idStatus.getIcon());
        } else {
            statusLabel.setText(textStatus.getDescription());
            statusLabel.setIcon(textStatus.getIcon());
        }
        if ((statusLabel.getText().length() == 0) == statusLabel.isVisible()) {
            statusLabel.setVisible(statusLabel.getText().length() != 0);
            validate();
        }
        okButton.setEnabled(idType != StatusType.ERROR
                && textType != StatusType.ERROR);
    }

    private void apply() {
        if (idStatus.getType() == StatusType.ERROR
                || textStatus.getType() == StatusType.ERROR)
            return;
        String id = idField.getText();
        String text = textArea.getText();
        boolean renameId = !id.equals(currentId);
        if (currentId == null || renameId) {
            for (GraphElement element : elements) {
                element.addAttribute(
                        element instanceof Node ? new NodeLabelAttribute(id,
                                text) : new EdgeLabelAttribute(id, text), "");
                if (currentId != null) {
                    element.removeAttribute(currentId);
                }
            }
        } else {
            for (GraphElement element : elements) {
                Attribute attribute = element.getAttributes().getCollection()
                        .get(currentId);
                if (!(attribute instanceof LabelAttribute)) {
                    continue;
                }
                ((LabelAttribute) attribute).setLabel(text);
            }
        }
        setVisible(false);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
