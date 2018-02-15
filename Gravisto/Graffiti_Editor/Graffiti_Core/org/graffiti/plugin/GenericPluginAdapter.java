// =============================================================================
//
//   GenericPluginAdapter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GenericPluginAdapter.java 6353 2015-03-14 17:59:07Z hanauer $

package org.graffiti.plugin;

import java.util.prefs.Preferences;

import javax.swing.ImageIcon;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.io.InputSerializer;
import org.graffiti.plugin.io.OutputSerializer;

/**
 * An adapter class for the generic plugin interface.
 * 
 * @version $Revision: 6353 $
 */
public abstract class GenericPluginAdapter implements GenericPlugin {
    /**
     * The default plugin icon for plugin implementations, which do not
     * overwrite the <code>getIcon</code> method in this class.
     */
    private static final ImageIcon DEFAULT_ICON = Bundle.getCoreBundle()
            .getIcon("icon.plugin.default");

    /** The preferences for this plugin. */
    protected Preferences prefs;

    /** The algorithms the plugin provides. */
    protected Algorithm[] algorithms;

    /** The attribute types the plugin provides. */
    protected Class<?>[] attributes;

    /** The plugin's dependencies. */
    protected String[] dependencies;

    /** The input serializers of this plugin. */
    protected InputSerializer[] inputSerializers;

    /** The output serializers of this plugin. */
    protected OutputSerializer[] outputSerializers;

    /** The views the plugin provides (class names of the views). */
    protected String[] views;

    protected final String BETA = "Beta versions";

    protected final String DELETE = "Algorithms to delete";

    protected final String TREES = "Trees";

    protected final String PLANAR = "Planar Graphs";

    protected final String ISOMORPHISM = "Isomorphism";

    protected final String MST = "MST";

    protected final String ORTHOGONAL = "Orthogonal";

    protected final String NETWORK_FLOW = "Network Flow";

    protected final String ARBITRARY = "Arbitrary Graphs";
    
    protected final String DIRECTED = "Directed Graphs";

    protected final String SUGIYAMA = "Sugiyama";

    /**
     * Constructs a new <code>GenericPluginAdapter</code>.
     */
    protected GenericPluginAdapter() {
        this.algorithms = new Algorithm[0];
        this.attributes = new Class[0];
        this.dependencies = new String[0];

        // this.vecs = new ValueEditComponent[0];
        this.views = new String[0];
        this.inputSerializers = new InputSerializer[0];
        this.outputSerializers = new OutputSerializer[0];
    }

    /**
     * Returns the array of <code>org.graffiti.algorithm.Algorithm</code>s the
     * plugin contains.
     * 
     * @return the array of <code>org.graffiti.algorithm.Algorithm</code>s the
     *         plugin contains.
     */
    public Algorithm[] getAlgorithms() {
        return this.algorithms;
    }

    /**
     * Returns the attribute types provided by this plugin.
     * 
     * @return the attribute types provided by this plugin.
     */
    public Class<?>[] getAttributes() {
        return this.attributes;
    }

    /**
     * Returns the array containing the names of the plugin classes the current
     * plugin depends on.
     * 
     * @return the array containing the names of the plugin classes the current
     *         plugin depends on.
     */
    public String[] getDependencies() {
        return this.dependencies;
    }

    /**
     * Returns the default icon for a plugin, which does not overwrite this
     * method.
     * 
     * @return the default plugin icon for a plugin implementation, which does
     *         not overwrite this method.
     */
    public ImageIcon getIcon() {
        return DEFAULT_ICON;
    }

    /**
     * Returns the input serializers the plugin provides.
     * 
     * @return the input serializers the plugin provides.
     */
    public InputSerializer[] getInputSerializers() {
        return this.inputSerializers;
    }

    /**
     * Returns the output serializers the plugin provides.
     * 
     * @return the output serializers the plugin provides.
     */
    public OutputSerializer[] getOutputSerializers() {
        return this.outputSerializers;
    }

    /**
     * States whether this class wants to be registered as a
     * <code>SelectionListener</code>.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isSelectionListener() {
        return false;
    }

    /**
     * States whether this class wants to be registered as a
     * <code>SessionListener</code>.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isSessionListener() {
        return false;
    }

    /**
     * @see org.graffiti.plugin.GenericPlugin#isViewListener()
     */
    public boolean isViewListener() {
        return false;
    }

    /**
     * Returns the array of <code>org.graffiti.plugin.view.View</code>s the
     * plugin contains.
     * 
     * @return the array of <code>org.graffiti.plugin.view.View</code>s the
     *         plugin contains.
     */
    public String[] getViews() {
        return this.views;
    }

    /**
     * Runs configuration routines for the plugin, e.g. load preferences etc.
     * 
     * @param p
     *            DOCUMENT ME!
     */
    public void configure(Preferences p) {
        prefs = p;
    }

    /**
     * The routines to perform before the editor will exit.
     */
    public void doBeforeExit() {
    }

    /**
     * Interrupts the running plugin.
     */
    public void interrupt() {
    }

    /**
     * States whether this class needs up-to-date information about the current
     * editcomponents. If this method returns <code>true</code>, it must
     * implement interface <code>NeedEditComponents</code>.
     * 
     * @return DOCUMENT ME!
     */
    public boolean needsEditComponents() {
        return false;
    }

    /**
     * Stops a running plugin. Performs exit routines.
     */
    public void stop() {
    }

    /**
     * Returns the plugin's name. If the plugin has a name, a submenu in the
     * plugin menu of Gravisto is created.
     * 
     * @return The plugin's name.
     */
    public String getName() {
        return null;
    }

    /**
     * Used to specify whether a <code>JSeparator</code> should be added after
     * an algorithm in the plugin menu of Gravisto. <code>JSeparator</code>s are
     * added when <code>getName</code> is implemented only.
     * 
     * @param index
     *            The index of the algorithm.
     * @return <code>false</code>
     * @see GenericPlugin#addJSeparatorAfterAlgorithm(int)
     */
    public boolean addJSeparatorAfterAlgorithm(int index) {
        return false;
    }

    public PluginPathNode getPathInformation() {
        return null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
