//=============================================================================
//
//   IconEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: IconEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.graffiti.attributes.Attribute;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;

/**
 * This edit component consists of several buttons with icons. The selection of
 * one button turns off all other buttons.
 */
public abstract class IconEditComponent extends AbstractValueEditComponent
        implements ActionListener, SelfLabelingComponent {

    /** The default dimension of a button. */
    public static final Dimension DEFAULT_DIM = new Dimension(56, 56);

    /** The buttons of this edit component. */
    protected JToggleButton[] buttons;

    /** The button group of this editComponent. */
    protected ButtonGroup buttonGroup;

    /** The number of buttons in the buttonGroup. */
    protected int buttonCount;

    /** The images of the buttons. */
    protected ImageIcon[] images;

    /** The JPanel containing the buttons. */
    protected JPanel panel;

    /** The values of the buttons. */
    protected Object[] values;

    /** The value of the currently selected button. */
    protected Object currentValue;

    /**
     * Constructs a new <code>IconEditComponent</code> with several
     * <code>JToggleButtons</code>.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public IconEditComponent(Displayable<?> disp) {
        super(disp);
        this.buttonGroup = new ButtonGroup();
        this.panel = new JPanel(new GridLayout(0, 3));
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createTitledBorder(disp.getName()), BorderFactory
                .createEmptyBorder(5, 5, 5, 5)));

        if (!disp.getDescription().isEmpty()) {
            panel.setToolTipText(disp.getDescription());
        } else if (disp instanceof Attribute) {
            panel.setToolTipText(((Attribute) disp).getPath().substring(1));
        }
    }

    /**
     * Reacts on button clicks by setting the currentValue.
     * 
     * @param event
     *            the ActionEvent
     * @see java.awt.event.ActionListener
     *      #actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {
        int index = (new Integer(event.getActionCommand())).intValue();
        this.currentValue = this.values[index];

        if (this.showEmpty) {
            this.showEmpty = false;
        }

        fireVECChanged();
    }

    /**
     * Returns the <code>JComponent</code> for editing this edit component.
     * 
     * @return the <code>JComponent</code> for editing this edit component.
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#getComponent()
     */
    public JComponent getComponent() {
        return this.panel;
    }

    /**
     * Sets the current value of the <code>Attribute</code> in the corresponding
     * <code>JComponent</code>.
     * 
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent
     *      #setEditFieldValue()
     */
    @Override
    protected void setDispEditFieldValue() {
        if (showEmpty) {

            // select none of the buttons
            this.currentValue = null;
        } else {
            Object value = this.displayable.getValue();
            for (int i = 0; i < buttonCount; i++) {
                Object buttonValue = this.values[i];
                if (value.equals(buttonValue)) {
                    this.buttons[i].setSelected(true);
                    this.currentValue = buttonValue;
                    return;
                }
            }
        }
    }

    /**
     * Sets the value of the displayable to the currently selected button's
     * value.
     * 
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#setValue()
     */
    @Override
    protected void setDispValue() {
        if (!this.showEmpty
                && !this.displayable.getValue().equals(currentValue)) {
            @SuppressWarnings("unchecked")
            Displayable<Object> displayable = (Displayable<Object>) this.displayable;
            displayable.setValue(currentValue);
        }
    }

    /**
     * Specifies whether this component should allow editing.
     * 
     * @param enabled
     *            <code>true</code>, if the component should allow editing,
     *            <code>false</code> if not
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent
     *      #setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        for (int i = 0; i < this.buttonCount; i++) {
            this.buttons[i].setEnabled(enabled);
        }
    }

    /**
     * Returns if this component allows editing.
     * 
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#isEnabled()
     * @return <code>true</code>, if the component allows editing,
     *         <code>false</code> if not
     */
    @Override
    public boolean isEnabled() {
        return this.buttons[0].isEnabled();
    }

    public boolean isSelfLabeling() {
        return true;
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
