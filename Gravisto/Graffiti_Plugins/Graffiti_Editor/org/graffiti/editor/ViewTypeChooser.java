// =============================================================================
//
//   ViewTypeChooser.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ViewTypeChooser.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.graffiti.core.Bundle;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 5768 $
 */
public class ViewTypeChooser extends JDialog implements ListSelectionListener,
        ActionListener, MouseListener, WindowListener {

    /**
     * 
     */
    private static final long serialVersionUID = -6887799516700099453L;

    /** The <code>Bundle</code> of the view type chooser. */
    protected static final Bundle bundle = Bundle.getCoreBundle();

    /** The buttons. */
    private JButton cancel;

    /** The buttons. */
    private JButton ok;

    /** The list ui component. */
    private JList list;

    /** The scroll pane of the JList. */
    private JScrollPane scrolledList;

    /** The selected view in the list. */
    private int selectedView = -1;

    /**
     * Constructs a new view type chooser, which is needed if there are several
     * view types available.
     * 
     * @param parent
     *            the parent of this dialog.
     * @param title
     *            the title of this dialog.
     * @param views
     *            the array of views to be displayed in the list.
     */
    public ViewTypeChooser(Frame parent, String title, String[] views) {
        super(parent, true);

        this.setTitle(title);

        getContentPane().setLayout(new BorderLayout());

        setResizable(false);
        setLocationRelativeTo(parent);

        list = new JList(views);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new NameListCellRenderer());

        scrolledList = new JScrollPane(list);

        ok = new JButton(bundle.getString("common.ok"));
        cancel = new JButton(bundle.getString("common.cancel"));

        // description = new JLabel(sBundle.getString("viewTypeChooser.desc"));

        JPanel buttonsPanel = new JPanel();

        buttonsPanel.add(ok);
        buttonsPanel.add(cancel);

        // getContentPane().add(description, BorderLayout.NORTH);
        getContentPane().add(scrolledList, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

        ok.setEnabled(false);

        getRootPane().setDefaultButton(ok);

        // defineLayout();
        addListeners();

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * Returns true, if the list is empty.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isEmpty() {
        return list.getModel().getSize() == 0;
    }

    /**
     * Returns the index of the selected view in the list.
     * 
     * @return the index of the selected view in the list.
     */
    public int getSelectedView() {
        return selectedView;
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == cancel) {
            dispose();
        } else if (src == ok) {
            okSelected();
        }
    }

    /**
     * Updates the ok button.
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void checkEnableOK(ListSelectionEvent e) {
        ok.setEnabled(list.getSelectedIndex() != -1);
    }

    /**
     * @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
     */
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            okSelected();
        }
    }

    /**
     * @see java.awt.event.MouseListener#mouseEntered(MouseEvent)
     */
    public void mouseEntered(MouseEvent e) {
        // do nothing
    }

    /**
     * @see java.awt.event.MouseListener#mouseExited(MouseEvent)
     */
    public void mouseExited(MouseEvent e) {
        // do nothing
    }

    /**
     * @see java.awt.event.MouseListener#mousePressed(MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
        // do nothing
    }

    /**
     * @see java.awt.event.MouseListener#mouseReleased(MouseEvent)
     */
    public void mouseReleased(MouseEvent e) {
        // do nothing
    }

    /**
     * Called, if ok is selected.
     */
    public void okSelected() {
        selectedView = list.getSelectedIndex();

        dispose();
    }

    /**
     * @see javax.swing.event.ListSelectionListener#valueChanged(ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {
        checkEnableOK(e);
    }

    /**
     * @see java.awt.event.WindowListener#windowActivated(WindowEvent)
     */
    public void windowActivated(WindowEvent arg0) {
        // do nothing
    }

    /**
     * @see java.awt.event.WindowListener#windowClosed(WindowEvent)
     */
    public void windowClosed(WindowEvent arg0) {
        // do nothing
    }

    /**
     * @see java.awt.event.WindowListener#windowClosing(WindowEvent)
     */
    public void windowClosing(WindowEvent arg0) {
        dispose();
    }

    /**
     * @see java.awt.event.WindowListener#windowDeactivated(WindowEvent)
     */
    public void windowDeactivated(WindowEvent arg0) {
        // do nothing
    }

    /**
     * @see java.awt.event.WindowListener#windowDeiconified(WindowEvent)
     */
    public void windowDeiconified(WindowEvent arg0) {
        // do nothing
    }

    /**
     * @see java.awt.event.WindowListener#windowIconified(WindowEvent)
     */
    public void windowIconified(WindowEvent arg0) {
        // do nothing
    }

    /**
     * @see java.awt.event.WindowListener#windowOpened(WindowEvent)
     */
    public void windowOpened(WindowEvent arg0) {
        // do nothing
    }

    /**
     * Adds the listeners to the dialog.
     */
    private void addListeners() {
        cancel.addActionListener(this);
        ok.addActionListener(this);
        list.addMouseListener(this);
        list.addListSelectionListener(this);
        addWindowListener(this);
    }

    /**
     * Renders the view names for their displaying without class path.
     * 
     * @version $Revision: 5768 $
     */
    class NameListCellRenderer extends JLabel implements ListCellRenderer {
        /**
         * 
         */
        private static final long serialVersionUID = -2436438479759062017L;

        /** DOCUMENT ME! */
        private Border emptyBorder = BorderFactory
                .createEmptyBorder(2, 2, 2, 2);

        /** DOCUMENT ME! */
        private Border lineBorder = BorderFactory.createLineBorder(Color.blue,
                2);

        /**
         * Creates a new NameListCellRenderer object.
         */
        public NameListCellRenderer() {
            setOpaque(true);
        }

        /**
         * Return a component that has been configured to display the view name
         * without class path. That component's <code>paint</code> method is
         * then called to "render" the cell.
         * 
         * @param list
         *            The JList we're painting.
         * @param value
         *            The value returned by list.getModel().getElementAt(index).
         * @param index
         *            The cells index.
         * @param isSelected
         *            True if the specified cell was selected.
         * @param cellHasFocus
         *            True if the specified cell has the focus.
         * 
         * @return A component whose paint() method will render the view name.
         */
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            ListModel model = list.getModel();

            String viewName = (String) model.getElementAt(index);
            String lastName = "";

            if (!viewName.equals("")) {
                int i = viewName.lastIndexOf('.');

                if ((i >= 0) && (i < viewName.length())) {
                    lastName = viewName.substring(i + 1);
                } else if (i == -1) {
                    lastName = viewName;
                }
            }

            setText(lastName);

            if (isSelected) {
                setForeground(list.getSelectionForeground());
                setBackground(list.getSelectionBackground());
            } else {
                setForeground(list.getForeground());
                setBackground(list.getBackground());
            }

            setBorder(cellHasFocus ? lineBorder : emptyBorder);

            return this;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
