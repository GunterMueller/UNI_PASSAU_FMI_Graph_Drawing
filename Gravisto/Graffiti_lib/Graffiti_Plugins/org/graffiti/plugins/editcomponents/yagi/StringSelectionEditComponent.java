// =============================================================================
//
//   StringSelectionEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StringSelectionEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;
import org.graffiti.plugin.parameter.StringSelectionParameter;

/**
 * DOCUMENT ME!
 * 
 * @author Marek Piorkowski
 */
public class StringSelectionEditComponent extends AbstractValueEditComponent
        implements ActionListener {
    protected StringSelectionParameter slp;

    private JComboBox comboBox;

    public StringSelectionEditComponent(Displayable<?> disp) {
        super(disp);
        slp = (StringSelectionParameter) disp;
        comboBox = new JComboBox(slp.getParams());
        comboBox.setMinimumSize(new Dimension(50, 30));
        comboBox.setPreferredSize(new Dimension(50, 30));
        comboBox.setMaximumSize(new Dimension(2000, 30));
        comboBox.addActionListener(this);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public JComponent getComponent() {
        return comboBox;
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    protected void setDispEditFieldValue() {
        comboBox.setSelectedIndex(slp.getSelectedIndex());
    }

    /**
     * DOCUMENT ME!
     */
    @Override
    protected void setDispValue() {
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(comboBox)) {
            slp
                    .setSelectedValue(((JComboBox) e.getSource())
                            .getSelectedIndex());
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
