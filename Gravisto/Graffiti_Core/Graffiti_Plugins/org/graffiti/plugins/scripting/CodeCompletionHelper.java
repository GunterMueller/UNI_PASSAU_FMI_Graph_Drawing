package org.graffiti.plugins.scripting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.MenuElement;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;

/**
 * Helper class for the code completion.
 * 
 * @author Andreas Glei&szlig;ner
 * @see CodeCompletion
 */
public class CodeCompletionHelper {
    private class Item extends JMenuItem {
        /**
         * 
         */
        private static final long serialVersionUID = 738190343462942547L;
        private String insertion;

        public Item(CodeCompletion.Entry entry) {
            super(getCaption(entry));
            this.insertion = entry.getInsertion();
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(200, 15));
            String description = entry.getDescription();
            if (description.length() != 0) {
                setToolTipText("<html><body>"
                        + description.replaceAll("\\n", "<br>")
                        + "</body></html>");
            }
            addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    doAction();
                }
            });
        }

        public void doAction() {
            insert(insertion);
        }
    }

    private JTextField field;
    private Scope scope;

    public CodeCompletionHelper(JTextField field, Scope scope) {
        this.field = field;
        this.scope = scope;
    }

    private static String getCaption(CodeCompletion.Entry entry) {
        StringBuffer buffer = new StringBuffer(entry.getCaption());
        if (entry.isFunction()) {
            buffer.append("()");
        }
        String summary = entry.getSummary();
        if (summary == null || summary.length() != 0) {
            buffer.append(" - ").append(summary);
        }
        return buffer.toString();
    }

    public void showMenu() {
        int caretPosition = field.getCaretPosition();
        List<CodeCompletion.Entry> list = CodeCompletion.getCompletions(scope,
                field.getText().substring(0, caretPosition));
        if (list.isEmpty())
            return;
        final JPopupMenu menu = new JPopupMenu();
        MenuKeyListener menuKeyListener = new MenuKeyListener() {
            public void menuKeyPressed(MenuKeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                case KeyEvent.VK_BACK_SPACE:
                case KeyEvent.VK_DELETE:
                    menu.setVisible(false);
                    e.consume();
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_ENTER:
                case KeyEvent.VK_ESCAPE:
                    return;
                }
                dispatch(e);
            }

            public void menuKeyReleased(MenuKeyEvent e) {
                dispatch(e);
            }

            public void menuKeyTyped(MenuKeyEvent e) {
                menu.setVisible(false);
                char ch = e.getKeyChar();
                if (ch == '.') {
                    e.consume();
                    MenuElement[] path = e.getPath();
                    if (path.length > 0) {
                        MenuElement element = path[path.length - 1];
                        if (element instanceof Item) {
                            ((Item) element).doAction();
                        }
                    }
                }
                dispatch(e);
                showMenu();
            }

            private void dispatch(MenuKeyEvent e) {
                field.dispatchEvent(new KeyEvent(field, e.getID(), e.getWhen(),
                        e.getModifiers(), e.getKeyCode(), e.getKeyChar()));
            }
        };

        for (final CodeCompletion.Entry entry : list) {
            Item item = new Item(entry);
            item.addMenuKeyListener(menuKeyListener);
            menu.add(item);
        }

        try {
            Point p = field.modelToView(caretPosition).getLocation();
            menu.show(field, p == null ? 0 : p.x, p == null ? 0 : p.y);
        } catch (BadLocationException e1) {
        }
    }

    private void insert(String s) {
        try {
            Caret caret = field.getCaret();
            int dot = caret.getDot();
            int mark = caret.getMark();
            int offs = Math.min(dot, mark);
            Document document = field.getDocument();
            if (dot != mark) {
                document.remove(offs, Math.abs(dot - mark));
            }
            document.insertString(offs, s, null);
        } catch (BadLocationException e1) {
        }
    }
}
