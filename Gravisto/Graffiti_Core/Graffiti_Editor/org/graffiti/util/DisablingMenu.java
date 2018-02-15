// =============================================================================
//
//   DisablingMenu.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DisablingMenu.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.util;

import java.awt.Component;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JSeparator;

public class DisablingMenu extends JMenu {
    /**
     * 
     */
    private static final long serialVersionUID = -98621328919065821L;
    /**
     * Contains a set of all currently enabled components.
     */
    protected Set<Component> enabledComponents = new HashSet<Component>();

    /**
     * Constructs a new <code>JMenu</code> with no text.
     */
    public DisablingMenu() {
        super();
        addPopupMenuComponentListener();
    }

    /**
     * Constructs a new <code>JMenu</code> with the supplied string as its text
     * and specified as a tear-off menu or not.
     * 
     * @param s
     *            the text for the menu label
     * @param b
     *            can the menu be torn off (not yet implemented)
     */
    public DisablingMenu(String s, boolean b) {
        super(s, b);
        addPopupMenuComponentListener();
    }

    /**
     * Constructs a new <code>JMenu</code> with the supplied string as its text.
     * 
     * @param s
     *            the text for the menu label
     */
    public DisablingMenu(String s) {
        super(s);
        addPopupMenuComponentListener();
    }

    /**
     * Add a component listener to the popup menu in order to update the menus
     * "enabled" status.
     */
    protected void addPopupMenuComponentListener() {
        getPopupMenu().addContainerListener(new ContainerListener() {

            public void componentAdded(ContainerEvent e) {
                Component comp = e.getChild();
                if (comp instanceof JSeparator)
                    return;
                updateComponent(comp);

                // add a property listener for the "enabled" state of the
                // component
                comp.addPropertyChangeListener("enabled",
                        new PropertyChangeListener() {
                            public void propertyChange(PropertyChangeEvent evt) {
                                updateComponent((Component) evt.getSource());
                            }
                        });
            }

            public void componentRemoved(ContainerEvent e) {
                enabledComponents.remove(e.getComponent());
                updateStatus();
            }

        });
    }

    /**
     * Update enabled status of menu.
     */
    protected void updateStatus() {
        setEnabled(!enabledComponents.isEmpty());
    }

    /**
     * Check enabled state of <tt>component</tt> and update
     * <tt>enabledComponents</tt> accordingly.
     * 
     * @param component
     *            <tt>Component</tt> to check.
     */
    protected void updateComponent(Component component) {
        if (component.isEnabled()) {
            enabledComponents.add(component);
        } else {
            enabledComponents.remove(component);
        }

        updateStatus();
    }

}
