// =============================================================================
//
//   InspectorPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: InspectorPlugin.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.inspector;

import org.graffiti.plugin.EditorPlugin;

/**
 * Provides a general interface for components to be plugged into an inspector.
 * Any component being able to be plugged into an inspector has to implement
 * this interface. As inspector the inspector shows attributes of graph
 * components this inspector plugins also have to be
 * <code>org.graffiti.event.AttributeListener</code>. An instance of an
 * <code>InspectorPlugin</code> contains a set of <code>InspectorTab</code>
 * -instances which will then be plugged into the inspector.
 * 
 * @see org.graffiti.plugin.GenericPlugin
 * @see org.graffiti.event.AttributeListener
 * @see org.graffiti.plugin.inspector.InspectorTab
 */
public interface InspectorPlugin extends EditorPlugin {

    /**
     * Returns an array containing all the <code>InspectorTab</code>s of the
     * <code>InspectorPlugin</code>.
     * 
     * @return an array containing all the <code>InspectorTab</code>s of the
     *         <code>InspectorPlugin</code>.
     */
    public InspectorTab[] getTabs();

    /**
     * Adds another <code>InspectorTab</code> to the current
     * <code>InspectorPlugin</code>.
     * 
     * @param tab
     *            the <code>InspectorTab</code> to be added to the
     *            <code>InspectorPlugin</code>.
     */
    public void addTab(InspectorTab tab);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
