// =============================================================================
//
//   GraphMLReaderPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphMLReaderPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.graphml;

import org.graffiti.plugin.GenericPluginAdapter;

/**
 * This plugin provides the functionality for reading graphML files.
 * 
 * @author ruediger
 */
public class GraphMLReaderPlugin extends GenericPluginAdapter {

    /**
     * Constructs a new <code>GraphMLReaderPlugin</code>.
     */
    public GraphMLReaderPlugin() {
        super();
        this.inputSerializers = new GraphMLReader[1];
        this.inputSerializers[0] = new GraphMLReader();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
