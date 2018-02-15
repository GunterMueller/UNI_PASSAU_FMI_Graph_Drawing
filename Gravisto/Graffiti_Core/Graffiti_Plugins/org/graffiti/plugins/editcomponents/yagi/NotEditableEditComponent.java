// =============================================================================
//
//   NotEditableEditComponent.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NotEditableEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;

/**
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2009-01-07 23:29:26 +0100 (Mi, 07 Jan 2009)
 *          $
 */
public class NotEditableEditComponent extends AbstractValueEditComponent {

    private JLabel label;

    public NotEditableEditComponent(Displayable<?> disp) {
        super(disp);
        label = new JLabel("Not editable here.");
    }

    /*
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#getComponent()
     */
    public JComponent getComponent() {
        return label;
    }

    /*
     * @see
     * org.graffiti.plugin.editcomponent.ValueEditComponent#setEditFieldValue()
     */
    @Override
    protected void setDispEditFieldValue() {
        // do nothing
    }

    /*
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#setValue()
     */
    @Override
    protected void setDispValue() {
        // do nothing
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
