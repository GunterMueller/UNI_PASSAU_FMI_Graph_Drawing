package org.graffiti.plugins.algorithms.treedrawings;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * This works similar to the way in which a JFileChooser operates. A modal
 * dialog (i.e. it is blocking and cannot be sent to the background) is used.
 * Thus, as soon as the getSelectedIndex() method is called and the dialog is
 * displayed, the caller code waits for the user to select an item of a JList
 * and then press either the OK or press the Cancel button or close the dialog.
 * The mentioned JList is created by using a List-object given. After that the
 * selected index is returned. The caller code after that can find out what
 * button was pressed to close the dialog.
 * 
 * @author Andreas
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 */
public class SelectionDialog extends JDialog implements ActionListener {

    /**
     * 
     */
    private static final long serialVersionUID = -4816270017617385794L;

    JButton button1 = new JButton();

    JList list;

    public static int BUTTON_PRESSED_NONE = -1;

    public static int BUTTON_PRESSED_OK = 0;

    public static int BUTTON_PRESSED_CLOSE = 1;

    int buttonPressed = -1;

    /**
     * 
     * @param title
     *            a title for the dialog that will be displayed after the
     *            getSelectedIndex() method of this SelectionDialog is called.
     * @param instructions
     *            some instructions for the user.
     * @param items
     *            that will be used for to create the JList.
     */
    public SelectionDialog(String title, String instructions, List<?> items) {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        this.setSize(new Dimension(300, 250));
        this.setResizable(true);
        this.setTitle(title);
        this.list = new JList(items.toArray());
        JScrollPane scroller = new JScrollPane(this.list);
        JPanel contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(null);
        JLabel label = new JLabel(instructions);
        label.setBounds(new Rectangle(10, 10, 270, 20));
        contentPane.add(label, null);
        scroller.setBounds(new Rectangle(10, 40, 270, 140));
        contentPane.add(scroller, null);
        button1.setText("OK");
        button1.addActionListener(this);
        button1.setBounds(new Rectangle(120, 190, 50, 25));
        contentPane.add(button1, null);
    }

    /**
     * This must be called to display the dialog. After the user has made his
     * selection the selected index of the JList proved is returned..
     * 
     * @return int the index of the selected entry.
     */
    public int getSelectedIndex() {
        this.buttonPressed = SelectionDialog.BUTTON_PRESSED_NONE;
        this.setLocation(300, 200);
        this.setModal(true);
        this.setVisible(true);
        return this.list.getSelectedIndex();
    }

    /**
     * @return the button pressed
     */
    public int getButtonPressed() {
        return this.buttonPressed;
    }

    /** Overridden so we can exit when window is closed */
    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            cancel();
        }
        super.processWindowEvent(e);
    }

    /** Close the dialog */
    void cancel() {
        this.buttonPressed = SelectionDialog.BUTTON_PRESSED_CLOSE;
        dispose();
    }

    /** Close the dialog on a button event */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button1) {
            this.buttonPressed = SelectionDialog.BUTTON_PRESSED_OK;
            dispose();
        }
    }
}
