//=============================================================================
//
//   LineModeEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
// $Id: LineModeEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import org.graffiti.core.Bundle;
import org.graffiti.graphics.Dash;
import org.graffiti.plugin.Displayable;

/**
 * The class for editing the linemode of nodes abd edges. It consists of several
 * <code>JToggleButtons</code> that allow the user to choose one mode.
 */
public class LineModeEditComponent extends IconEditComponent {

    /**
     * Constructs a new <code>LineModeEditComponent</code> with several
     * <code>JToggleButtons</code>.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public LineModeEditComponent(Displayable<?> disp) {
        super(disp);

        // create images
        this.images = new ImageIcon[6];
        Bundle bundle = Bundle.getCoreBundle();
        this.images[0] = bundle.getIcon("yagi.linemode.dash0");
        this.images[1] = bundle.getIcon("yagi.linemode.dash1");
        this.images[2] = bundle.getIcon("yagi.linemode.dash2");
        this.images[3] = bundle.getIcon("yagi.linemode.dash3");
        this.images[4] = bundle.getIcon("yagi.linemode.dash4");
        this.images[5] = bundle.getIcon("yagi.linemode.dash5");
        this.buttonCount = this.images.length;

        // create values
        this.values = new Dash[this.buttonCount];
        values[0] = new Dash(null, 0);
        values[1] = new Dash(new float[] { 2, 2 }, 0);
        values[2] = new Dash(new float[] { 4, 4 }, 0);
        values[3] = new Dash(new float[] { 12, 12 }, 0);
        values[4] = new Dash(new float[] { 12, 4 }, 0);
        values[5] = new Dash(new float[] { 4, 4, 12, 4 }, 0);

        this.buttons = new JToggleButton[buttonCount];

        // build buttons
        for (int i = 0; i < buttonCount; i++) {
            JToggleButton button = new JToggleButton(images[i]);
            this.buttons[i] = button;
            button.setActionCommand("" + i);
            button.addActionListener(this);
            this.panel.add(button);
            buttonGroup.add(button);

            button.setMinimumSize(DEFAULT_DIM);
            button.setMaximumSize(DEFAULT_DIM);
            button.setPreferredSize(DEFAULT_DIM);
            button.setSize(DEFAULT_DIM);
        }
    }

}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
