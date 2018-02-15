// =============================================================================
//
//   DOTSerializerPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DOTSerializerPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.exporters.graphviz;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.io.OutputSerializer;

/**
 * The plugin for reading and writing files from the Graphviz tools. Graphviz -
 * Graph Drawing Programs from AT&amp;T Research and Lucent Bell Labs.
 * 
 * @version $Revision: 5766 $
 */
public class DOTSerializerPlugin extends GenericPluginAdapter {

    /**
     * Creates a new DOTSerializerPlugin object.
     */
    public DOTSerializerPlugin() {
        this.outputSerializers = new OutputSerializer[1];
        this.outputSerializers[0] = new DOTSerializer();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
