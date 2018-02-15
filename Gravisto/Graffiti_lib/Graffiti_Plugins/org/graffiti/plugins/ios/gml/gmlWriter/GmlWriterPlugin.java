// =============================================================================
//
//   GmlWriterPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlWriterPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlWriter;

import org.graffiti.plugin.GenericPluginAdapter;

/**
 * This class provides a plugin for writing graphs from a file in GML format.
 * 
 * @author ruediger
 */
public class GmlWriterPlugin extends GenericPluginAdapter {

    /**
     * Constructs a new <code>GmlWriterPlugin</code>.
     */
    public GmlWriterPlugin() {
        this.outputSerializers = new GmlWriter[1];
        this.outputSerializers[0] = new GmlWriter();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
