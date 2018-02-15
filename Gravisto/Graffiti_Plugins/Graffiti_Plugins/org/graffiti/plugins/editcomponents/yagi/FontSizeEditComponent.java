//=============================================================================
//
//   FontSizeEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: FontSizeEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import org.graffiti.plugin.Displayable;

/**
 * Provides a SliderEditComponent for changing a font's size.
 */
public class FontSizeEditComponent extends IntegerEditComponent {

    /**
     * Constructs a new IntegerEditComponent with the possible values from 1 to
     * 7.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public FontSizeEditComponent(Displayable<?> disp) {
        super(disp);

        this.setLimits(1, 100, 1, Integer.MAX_VALUE);
    }

    /**
     * Specifies if the component shows a value or a dummy (e.g. "---").
     * 
     * @param showEmpty
     *            <code>true</code>, if the component should display a dummy,
     *            <code>false</code> if not
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent
     *      #setShowEmpty(boolean)
     */
    @Override
    public void setShowEmpty(boolean showEmpty) {
        if (this.showEmpty != showEmpty) {
            this.showEmpty = showEmpty;
            this.setEditFieldValue();
        }
    }

}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
