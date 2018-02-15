// =============================================================================
//
//   GraphMLWriterPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphMLWriterPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.exporters.graphml;

import org.graffiti.plugin.GenericPluginAdapter;

/**
 * The plugin class for the graphML writing package.
 * 
 * @author ruediger
 */
public class GraphMLWriterPlugin extends GenericPluginAdapter {

    /**
     * Constructs a new <code>GraphMLWriterPlugin</code>.
     */
    public GraphMLWriterPlugin() {
        super();
        this.outputSerializers = new GraphMLWriter[1];
        this.outputSerializers[0] = new GraphMLWriter();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
