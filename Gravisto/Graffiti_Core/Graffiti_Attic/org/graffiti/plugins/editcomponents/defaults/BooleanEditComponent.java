// =============================================================================
//
//   BooleanEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: BooleanEditComponent.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.editcomponents.defaults;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;

/**
 * Represents a component, which can edit a boolean value.
 * 
 * @version $Revision: 5772 $
 */
public class BooleanEditComponent extends AbstractValueEditComponent {

    /** DOCUMENT ME! */
    private Icon defaultIcon;

    /** DOCUMENT ME! */
    private Icon defaultSelectedIcon;

    /** DOCUMENT ME! */
    private Icon emptyIcon;

    // /** DOCUMENT ME! */
    // private ImageBundle iBundle;

    /** The gui element of this component. */
    private JCheckBox checkBox;

    /**
     * Constructs a new boolean edit component, referencing the given
     * displayable.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public BooleanEditComponent(Displayable<?> disp) {
        super(disp);
        this.checkBox = new JCheckBox();
        this.checkBox.setAlignmentX(0.5f);

        // iBundle = org.graffiti.core.ImageBundle.getInstance();
        // defaultIcon = new ImageIcon(iBundle.getImage("bool.notselected"));
        // emptyIcon = new ImageIcon(iBundle.getImage("bool.half.notselected"));
        // defaultSelectedIcon = new
        // ImageIcon(iBundle.getImage("bool.selected"));

        checkBox.setPressedIcon(emptyIcon);

        checkBox.addActionListener(new AbstractAction() {
            /**
             * 
             */
            private static final long serialVersionUID = 7242397962869029336L;

            public void actionPerformed(ActionEvent e) {
                if (showEmpty) {
                    showEmpty = false;
                    setEditFieldValue();
                }
            }
        });
    }

    /**
     * Returns the <code>JComponent</code> for editing this edit component.
     * 
     * @return the <code>JComponent</code> for editing this edit component.
     */
    public JComponent getComponent() {
        return checkBox;
    }

    /**
     * Sets the current value of the <code>Attribute</code> in the corresponding
     * <code>JComponent</code>.
     */
    @Override
    protected void setDispEditFieldValue() {
        if (showEmpty) {
            checkBox.setSelectedIcon(emptyIcon);
            checkBox.setIcon(emptyIcon);
        } else {
            checkBox.setSelectedIcon(defaultSelectedIcon);
            checkBox.setIcon(defaultIcon);
        }

        checkBox.setSelected(((Boolean) this.displayable.getValue())
                .booleanValue());
    }

    /**
     * Sets the value of the displayable specified in the
     * <code>JComponent</code>. But only if it is different.
     */
    @Override
    protected void setDispValue() {
        if (!((Boolean) this.displayable.getValue()).booleanValue() == this.checkBox
                .isSelected()) {
            @SuppressWarnings("unchecked")
            Displayable<Boolean> displayable = (Displayable<Boolean>) this.displayable;
            displayable.setValue(new Boolean(checkBox.isSelected()));
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
