//=============================================================================
//
//   EdgeLabelSegmentEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
// $Id: EdgeLabelSegmentEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.ComboBoxEditComponent;

/**
 * Represents a ComboBox where the user can choose the segment of an edge where
 * the label will be positioned.
 */
public class EdgeLabelSegmentEditComponent extends ComboBoxEditComponent
        implements ActionListener {

    /**
     * Constructs a new disabled Combobox. To enable it, call
     * <code>setNumberOfBends(int)</code>.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public EdgeLabelSegmentEditComponent(Displayable<?> disp) {
        super(disp);
        comboBox = new JComboBox();
        comboBox.addActionListener(this);
        setNumberOfSegments(getBendsCount(disp) + 1);
    }

    /**
     * Constructs a new disabled Combobox. To enable it, call
     * <code>setNumberOfBends(int)</code>.
     * 
     * @param disps
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public EdgeLabelSegmentEditComponent(Displayable<?>[] disps) {
        super(disps);
        comboBox = new JComboBox();
        comboBox.addActionListener(this);
        setNumberOfSegments(getBendsCount(disps) + 1);
    }

    /**
     * Returns the <code>JComponent</code> for editing this edit component.
     * 
     * @return the <code>JComponent</code> for editing this edit component.
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#getComponent()
     */
    @Override
    public JComponent getComponent() {
        return this.comboBox;
    }

    /**
     * Sets the values of the ComboBox depending on the number of segments.
     * 
     * @param segments
     *            the number of segments of the selected edges
     */
    public void setNumberOfSegments(int segments) {
        comboBox.removeAllItems();
        this.comboText = new String[segments + 1];
        this.comboValue = new Integer[segments + 1];
        this.comboText[0] = "none";
        this.comboValue[0] = new Integer(0);
        comboBox.addItem(comboText[0]);
        for (int i = 1; i <= segments; i++) {
            this.comboText[i] = "" + i;
            this.comboValue[i] = new Integer(i);
            comboBox.addItem(comboText[i]);
        }

        this.setEnabled(true);
    }

    /**
     * Is called after the comboBox changes. Informs the editPanel about the
     * change.
     * 
     * @param event
     *            the event describing the action
     */
    public void actionPerformed(ActionEvent event) {
        if (this.showEmpty
                && !this.comboBox.getSelectedItem().equals(EMPTY_STRING)) {

            this.showEmpty = false;
            if (comboBox.getItemAt(0).equals(EMPTY_STRING)) {
                comboBox.removeItemAt(0);
            }
        }

        // inform the editPanel about the change.
        // we don't care about the parameters...
        // this.comboBox.firePropertyChange(VEC_VALUE, true, false);

        fireVECChanged();
    }

    @Override
    public void setEditFieldValue() {
        if (this.displayables != null) {
            setNumberOfSegments(getBendsCount(this.displayables) + 1);
        } else {
            setNumberOfSegments(getBendsCount(this.displayable) + 1);
        }

        setDispEditFieldValue();
    }

    private int getBendsCount(Displayable<?>[] disps) {

        if (disps == null)
            return 0;
        int minBendsCount = Integer.MAX_VALUE, bendsCount;
        for (Displayable<?> disp : disps) {
            bendsCount = getBendsCount(disp);
            if (bendsCount < minBendsCount) {
                minBendsCount = bendsCount;
            }
        }
        return minBendsCount;

    }

    private int getBendsCount(Displayable<?> disp) {
        if (disp == null)
            return 0;

        Attributable attributable = ((Attribute) disp).getAttributable();
        CollectionAttribute bends = (CollectionAttribute) attributable
                .getAttribute(GraphicAttributeConstants.BENDS_PATH);
        return bends.getCollection().values().size();
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
