// =============================================================================
//
//   GmlReaderPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlReaderPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.gmlReader;

import org.graffiti.plugin.GenericPluginAdapter;

/**
 * This class provides the plugin for writing a graph to a file in the GML file
 * format.
 * 
 * @author ruediger
 */
public class GmlReaderPlugin extends GenericPluginAdapter {

    /**
     * Constructs a new <code>GmlReaderPlugin</code>.
     */
    public GmlReaderPlugin() {
        super();
        this.inputSerializers = new GmlReader[1];
        this.inputSerializers[0] = new GmlReader();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
