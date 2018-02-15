// =============================================================================
//
//   ConsoleToggleButton.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ConsoleToggleButton extends JToggleButton {
    /**
     * 
     */
    private static final long serialVersionUID = -2953597980880778912L;

    public ConsoleToggleButton(final FastView fastView) {
        super(new ImageIcon(FastViewPlugin.class
                .getResource("images/console.png")));
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fastView.setConsoleVisible(isSelected());
            }
        });
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
