package org.graffiti.plugins.scripting;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.ListIterator;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

/**
 * Component providing a console for scripting.
 * 
 * @author Andreas Glei&szlig;ner
 */
public class ConsoleComponent extends JPanel {
    /**
     * 
     */
    private static final long serialVersionUID = -7860558959100441646L;

    /**
     * Count of previously entered commands to remember.
     */
    protected static final int MAX_HISTORY_SIZE = 50;

    /**
     * Style for standard output.
     */
    protected Style outStyle;

    /**
     * Style for error output.
     */
    protected Style errorStyle;

    /**
     * Style for output echoing user commands.
     */
    protected Style userStyle;

    /**
     * Document holding the complete console output.
     */
    private StyledDocument document;

    /**
     * Text field where the user can enter commands.
     */
    protected JTextField field;

    /**
     * Denotes if the last char entered was a newline character.
     */
    protected boolean lastCharWasNewline;

    /**
     * List of the previously entered user commands. Holds at most
     * MAX_HISTORY_SIZE commands.
     */
    protected LinkedList<String> history;

    /**
     * Points to a command in the history if the user uses the arrow keys to
     * repeat a command.
     */
    protected ListIterator<String> historyIterator;

    protected boolean historyIteratorUpwards;

    protected ConsoleScope scope;

    private Console console;

    private CodeCompletionHelper codeCompletionHelper;

    /**
     * Constructs a new {@code Console}.
     * 
     * @param parent
     *            the scope of the executed commands.
     */
    public ConsoleComponent(final Scope parent, String engineId) {
        super(new BorderLayout(5, 5));

        ScriptingEngine engine = ScriptingRegistry.get().getEngine(engineId);

        if (engine == null) {
            scope = null;
            console = null;
        } else {
            scope = new ConsoleScope(parent);
            console = engine.createConsole(scope);
        }

        lastCharWasNewline = true;
        setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        JTextPane textPane = new JTextPane();
        document = textPane.getStyledDocument();
        addStyles();
        textPane.setEditable(false);
        add(new JScrollPane(textPane,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER),
                BorderLayout.CENTER);
        field = new JTextField();

        if (scope == null) {
            codeCompletionHelper = null;
            field.setEnabled(false);
            append(ScriptingPlugin.format("error.engineNotFound", engineId),
                    errorStyle, true);
        } else {
            append(engine.getName(), outStyle, true);
            codeCompletionHelper = new CodeCompletionHelper(field, scope);
            field.setFont(new Font("Monospaced", Font.PLAIN, 12));
            field.setFocusTraversalKeysEnabled(false);
            field.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        escapeCommand();
                        break;
                    case KeyEvent.VK_UP:
                        browseUp();
                        break;
                    case KeyEvent.VK_DOWN:
                        browseDown();
                        break;
                    case KeyEvent.VK_SPACE:
                        if (e.isControlDown()) {
                            e.consume();
                            codeCompletionHelper.showMenu();
                        }
                        break;
                    case KeyEvent.VK_TAB:
                        field.setText(field.getText() + "    ");
                        break;
                    }
                }

                @Override
                public void keyTyped(KeyEvent e) {
                    char ch = e.getKeyChar();
                    if (ch == '\n') {
                        command(e);
                    } else if (ch == '.') {
                        try {
                            field.getDocument().insertString(
                                    field.getCaretPosition(), ".", null);
                            e.consume();
                        } catch (BadLocationException e1) {
                        }
                        codeCompletionHelper.showMenu();
                    }
                }
            });
        }

        add(field, BorderLayout.SOUTH);
        setPreferredSize(new Dimension(0, 200));
        history = new LinkedList<String>();
        historyIterator = history.listIterator();
        historyIteratorUpwards = true;
    }

    private void addStyles() {
        Style def = StyleContext.getDefaultStyleContext().getStyle(
                StyleContext.DEFAULT_STYLE);
        Style regular = document.addStyle("regular", def);
        StyleConstants.setFontFamily(regular, "Monospaced");
        StyleConstants.setFontSize(regular, 12);
        outStyle = document.addStyle("out", regular);
        StyleConstants.setForeground(outStyle, ConsoleOutput.Standard
                .getDefaultColor());
        errorStyle = document.addStyle("error", regular);
        StyleConstants.setForeground(errorStyle, ConsoleOutput.Error
                .getDefaultColor());
        userStyle = document.addStyle("user", regular);
        StyleConstants.setForeground(userStyle, ConsoleOutput.User
                .getDefaultColor());
    }

    protected void append(String string, Style style, boolean inNewLine) {
        try {
            StringBuilder builder = new StringBuilder();
            if (inNewLine && !lastCharWasNewline) {
                builder.append("\n");
                lastCharWasNewline = true;
            }
            builder.append(string);
            document.insertString(document.getLength(), builder.toString(),
                    style);
            lastCharWasNewline = string.length() == 0 ? lastCharWasNewline
                    : string.endsWith("\n");
        } catch (BadLocationException e) {
        }
    }

    public void print(String string, ConsoleOutput kind) {
        switch (kind) {
        case User:
            append(string, userStyle, false);
            return;
        case Error:
            append(string, errorStyle, false);
            return;
        default:
            append(string, outStyle, false);
            return;
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            field.grabFocus();
        }
    }

    protected static String extractWhitespace(String string) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            char ch = string.charAt(i);
            if (Character.isWhitespace(ch)) {
                buf.append(ch);
            } else {
                break;
            }
        }
        if (string.trim().endsWith("{")) {
            for (int i = 0; i < 4; i++) {
                buf.append(' ');
            }
        }
        return buf.toString();
    }

    private void escapeCommand() {
        console.reset();
        field.setText("");
        append("$[ESC]\n", userStyle, true);
        historyIterator = history.listIterator();
        historyIteratorUpwards = true;
    }

    private void browseUp() {
        if (historyIterator.hasNext()) {
            if (!historyIteratorUpwards) {
                historyIterator.next();
            }
            if (historyIterator.hasNext()) {
                field.setText(historyIterator.next());
            }
            historyIteratorUpwards = true;
        }
    }

    private void browseDown() {
        if (historyIterator.hasPrevious()) {
            if (historyIteratorUpwards) {
                historyIterator.previous();
            }
            if (historyIterator.hasPrevious()) {
                field.setText(historyIterator.previous());
            }
            historyIteratorUpwards = false;
        } else {
            field.setText("");
            historyIterator = history.listIterator();
            historyIteratorUpwards = true;
        }
    }

    protected void command(KeyEvent e) {
        if (console == null)
            return;

        final String command = field.getText();
        if (history.isEmpty() || !history.getFirst().equals(command)) {
            history.addFirst(command);
            if (history.size() > MAX_HISTORY_SIZE) {
                history.removeLast();
            }
        }
        historyIterator = history.listIterator();
        historyIteratorUpwards = true;

        boolean isCommandComplete = console.addLine(command,
                new ResultCallback() {
                    private void echo() {
                        append("$ " + command + "\n", userStyle, true);
                        lastCharWasNewline = true;
                    }

                    public void reportError(String message) {
                        echo();
                        append(message, errorStyle, true);
                    }

                    public void reportResult(String value) {
                        echo();
                        append("= " + value, outStyle, true);
                    }

                    public void reportResult() {
                        echo();
                        append("", outStyle, true);
                    }
                });

        if (!isCommandComplete) {
            append("| " + command + "\n", userStyle, true);
            lastCharWasNewline = true;
        }

        field.setText(extractWhitespace(command));
        e.consume();
    }
}
