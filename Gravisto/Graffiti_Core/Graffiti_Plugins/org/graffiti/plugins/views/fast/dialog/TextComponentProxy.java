// =============================================================================
//
//   TextComponentProxy.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.dialog;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class TextComponentProxy implements DocumentListener {
    private JTextComponent component;
    private boolean isSuspended;

    public TextComponentProxy(JTextComponent component) {
        this.component = component;
        isSuspended = false;
        component.getDocument().addDocumentListener(this);
    }

    public final void changedUpdate(DocumentEvent e) {
        update();
    }

    public final void insertUpdate(DocumentEvent e) {
        update();
    }

    public final void removeUpdate(DocumentEvent e) {
        update();
    }

    private void update() {
        if (isSuspended)
            return;
        onChange();
    }

    public final void setText(String text) {
        isSuspended = true;
        component.setText(text);
        isSuspended = false;
    }

    protected abstract void onChange();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
