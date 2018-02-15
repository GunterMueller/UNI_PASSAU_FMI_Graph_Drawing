// =============================================================================
//
//   KeyHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.graffiti.plugin.view.interactive.KeyPressGesture;
import org.graffiti.plugin.view.interactive.KeyReleaseGesture;
import org.graffiti.plugin.view.interactive.UserGestureListener;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class KeyHandler implements KeyListener {
    private FastView fastView;
    private UserGestureListener dispatcher;

    protected KeyHandler(FastView fastView) {
        this.fastView = fastView;
        fastView.addKeyListener(this);
    }

    protected void setUserGestureDispatcher(UserGestureListener dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_DEAD_CIRCUMFLEX) {
            e.consume();
            fastView.setConsoleVisible(!fastView.isConsoleVisible());
            return;
        }
        if (dispatcher != null) {
            if (keyCode == KeyEvent.VK_ESCAPE) {
                dispatcher.canceled(fastView);
                e.consume();
            } else {
                dispatcher.gesturePerformed(fastView, new KeyPressGesture(e
                        .getKeyCode(), e.getModifiersEx()));
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_ESCAPE && dispatcher != null) {
            dispatcher.gesturePerformed(fastView, new KeyReleaseGesture(e
                    .getKeyCode(), e.getModifiersEx()));
        }
    }

    public void keyTyped(KeyEvent e) {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
