//==============================================================================
//
//   JOptionPaneWithFocus.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//==============================================================================
// $Id: JOptionPaneWithFocus.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.util;

import java.awt.Component;
import java.awt.HeadlessException;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Extends <tt>JOptionPane</tt> and allows the specification of a component to
 * get the focus.
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5768 $ $Date: 2006-01-09 14:23:12 +0100 (Mo, 09 Jan 2006)
 *          $
 */
public class JOptionPaneWithFocus extends JOptionPane {

    /**
     * 
     */
    private static final long serialVersionUID = -463050361009218213L;

    /**
     * Extends the method showConfirmDialog(Component, Object, String, int) by a
     * new parameter used to define the component which should get the focus.
     * 
     * @see JOptionPane#showConfirmDialog(java.awt.Component, java.lang.Object,
     *      java.lang.String, int)
     */
    public static int showQuestionDialog(Component parentComponent,
            Object message, String title, int optionType, JComponent focus)
            throws HeadlessException {
        JOptionPane pane = new JOptionPane(message, QUESTION_MESSAGE,
                optionType);

        pane
                .setComponentOrientation(((parentComponent == null) ? getRootFrame()
                        : parentComponent).getComponentOrientation());

        JDialog dialog = pane.createDialog(parentComponent, title);

        focus.grabFocus();
        dialog.setVisible(true);
        dialog.dispose();

        Object selectedValue = pane.getValue();

        if (selectedValue == null)
            return CLOSED_OPTION;

        if (selectedValue instanceof Integer)
            return ((Integer) selectedValue).intValue();

        return CLOSED_OPTION;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
