// =============================================================================
//
//   TestTool.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestTool.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.tools.test;

import java.awt.Component;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.session.Session;

/**
 * This is an example for a tool. It changes the node color to an arbitrary one
 * by clicking on it.
 * 
 * @author chris
 */
public class TestTool {

    /**
     * Called when a mouse button is pressed.
     * 
     * @param e
     *            event
     */
    public void mousePressed(MouseEvent e) {
        if (!SwingUtilities.isLeftMouseButton(e))
            return;

        System.out.println("left clicked");

        Component clickedComp = ((Component) e.getSource()).getComponentAt(e
                .getPoint());

        if (clickedComp instanceof GraphElementComponent) {
            System.out.println("clicked on a graph element");

            GraphElement ge = ((GraphElementComponent) clickedComp)
                    .getGraphElement();

            if (ge instanceof Node) {
                Node n = (Node) ge;
                System.out.println("clicked on a node");
                n.setInteger("graphics.fillcolor.red", (int) Math.round(Math
                        .random() * 255));
                n.setInteger("graphics.fillcolor.green", (int) Math.round(Math
                        .random() * 255));
                n.setInteger("graphics.fillcolor.blue", (int) Math.round(Math
                        .random() * 255));
            }
        }
    }

    /**
     * Called when the session data (not the session's graph data!) changed.
     * 
     * @param s
     *            current session
     */
    public void sessionDataChanged(Session s) {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
