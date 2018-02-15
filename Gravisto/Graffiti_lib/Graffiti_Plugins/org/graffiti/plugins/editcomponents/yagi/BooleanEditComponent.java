//=============================================================================
//
//   BooleanEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: BooleanEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;

/**
 * Represents a component which can edit a boolean value.
 */
public class BooleanEditComponent extends AbstractValueEditComponent implements
        ItemListener {

    /** The gui element of this component. */
    private JCheckBox checkBox;

    /**
     * Constructs a new boolean edit component, referencing the given
     * displayable.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public BooleanEditComponent(Displayable<?> disp) {
        super(disp);
        this.checkBox = new JCheckBox();
        this.checkBox.addItemListener(this);
    }

    /**
     * Returns the <code>JComponent</code> for editing this edit component.
     * 
     * @return the <code>JComponent</code> for editing this edit component.
     */
    public JComponent getComponent() {
        return this.checkBox;
    }

    /**
     * Sets the current value of the attribute in the corresponding
     * <code>JComponent</code>.
     */
    @Override
    protected void setDispEditFieldValue() {
        this.checkBox.setSelected(((Boolean) this.displayable.getValue())
                .booleanValue());
    }

    /**
     * Sets the value of the displayable specified in the
     * <code>JComponent</code>.
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

    /**
     * Reacts on selection/deselection of the checkBox.
     * 
     * @param event
     *            the event describing the change
     */
    public void itemStateChanged(ItemEvent event) {
        fireVECChanged();
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
