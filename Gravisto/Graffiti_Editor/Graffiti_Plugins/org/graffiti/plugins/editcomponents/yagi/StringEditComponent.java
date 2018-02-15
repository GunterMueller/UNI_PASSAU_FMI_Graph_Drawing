//=============================================================================
//
//   StringEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
// $Id: StringEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;

/**
 * <code>StringEditComponent</code> provides an edit component for editing
 * strings. The edit field has just one line.
 * 
 * @see org.graffiti.plugin.editcomponent.AbstractValueEditComponent
 */
public class StringEditComponent extends AbstractValueEditComponent implements
        ActionListener, FocusListener, KeyListener {

    /** The text field containing the value of the displayable. */
    protected JTextField textField;

    /** The default dimension of the textfield. */
    protected final static Dimension DEFAULT_DIM = new Dimension(140, 20);

    /**
     * Constructs a new <code>StringEditComponent</code>.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public StringEditComponent(Displayable<?> disp) {
        super(disp);

        this.textField = new JTextField();
        this.textField.setMinimumSize(DEFAULT_DIM);
        this.textField.setPreferredSize(DEFAULT_DIM);
        this.textField.setMaximumSize(DEFAULT_DIM);
        this.textField.addActionListener(this);
        this.textField.addFocusListener(this);
        this.textField.addKeyListener(this);
    }

    /**
     * Returns the <code>JComponent</code> of this edit component.
     * 
     * @return the <code>JComponent</code> of this edit component.
     */
    public JComponent getComponent() {
        return this.textField;
    }

    /**
     * Sets the current value of the displayable in the corresponding
     * <code>JComponent</code>.
     */
    @Override
    protected void setDispEditFieldValue() {
        if (showEmpty) {
            this.textField.setText(EMPTY_STRING);
        } else {
            this.textField.setText(displayable.getValue().toString());
        }
    }

    /**
     * Sets the value of the displayable specified in the
     * <code>JComponent</code>.
     */
    @Override
    protected void setDispValue() {
        String text = this.textField.getText();

        if (!text.equals(EMPTY_STRING)
                && !this.displayable.getValue().toString().equals(text)) {
            @SuppressWarnings("unchecked")
            Displayable<String> displayable = (Displayable<String>) this.displayable;
            displayable.setValue(text);
        }
    }

    /**
     * Is called after the textField changes. Informs the editPanel about the
     * change.
     * 
     * @param event
     *            the event describing the action
     */
    public void actionPerformed(ActionEvent event) {
        if (this.showEmpty) {
            this.showEmpty = false;
        }

        fireVECChanged();
    }

    /**
     * Is called after the textField gained focus. Does nothing.
     * 
     * @param event
     *            the event describing the action
     */
    public void focusGained(FocusEvent event) {
        // do nothing
    }

    /**
     * Is called after the textField lost focus. Informs the editPanel.
     * 
     * @param event
     *            the event describing the action
     */
    public void focusLost(FocusEvent event) {
        if (this.showEmpty) {
            this.showEmpty = false;
        }
    }

    public void keyPressed(KeyEvent arg0) {
        // empty
    }

    public void keyReleased(KeyEvent arg0) {
        fireVECChanged();
    }

    public void keyTyped(KeyEvent arg0) {
        // empty
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
