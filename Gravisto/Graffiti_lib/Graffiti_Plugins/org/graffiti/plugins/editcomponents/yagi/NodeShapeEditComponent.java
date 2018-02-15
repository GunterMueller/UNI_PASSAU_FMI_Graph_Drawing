//=============================================================================
//
//   NodeShapeEditComponent.java
//
// Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: NodeShapeEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.Displayable;

/**
 * The class for editing the shape of nodes. It consists of several
 * <code>JToggleButtons</code> that allow the user to choose one shape.
 */
public class NodeShapeEditComponent extends IconEditComponent {

    /**
     * Constructs a new <code>NodeShapeEditComponent</code> with several
     * <code>JToggleButtons</code>.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public NodeShapeEditComponent(Displayable<?> disp) {
        super(disp);

        this.images = new ImageIcon[3];
        Bundle bundle = Bundle.getCoreBundle();
        this.images[0] = bundle.getIcon("yagi.nodeshape.rectangle");
        this.images[1] = bundle.getIcon("yagi.nodeshape.circle");
        this.images[2] = bundle.getIcon("yagi.nodeshape.ellipse");
        this.buttonCount = this.images.length;

        this.values = new String[this.buttonCount];
        this.values[0] = "org.graffiti.plugins.views.defaults.RectangleNodeShape";
        this.values[1] = "org.graffiti.plugins.views.defaults.CircleNodeShape";
        this.values[2] = "org.graffiti.plugins.views.defaults.EllipseNodeShape";

        this.buttons = new JToggleButton[buttonCount];

        // build buttons
        for (int i = 0; i < buttonCount; i++) {
            JToggleButton button = new JToggleButton(images[i]);
            this.buttons[i] = button;
            button.setActionCommand("" + i);
            button.addActionListener(this);
            this.panel.add(button);
            buttonGroup.add(button);
            button.setPreferredSize(DEFAULT_DIM);
        }
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
