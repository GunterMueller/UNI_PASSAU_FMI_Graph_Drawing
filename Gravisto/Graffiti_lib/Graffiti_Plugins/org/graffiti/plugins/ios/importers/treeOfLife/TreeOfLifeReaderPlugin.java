// =============================================================================
//
//   GraphMLReaderPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphMLReaderPlugin.java 1009 2006-01-04 09:21:57Z forster $

package org.graffiti.plugins.ios.importers.treeOfLife;

import org.graffiti.plugin.GenericPluginAdapter;

/**
 * This plugin provides the functionality for reading TreeML files.
 * 
 * @author haeringp
 */
public class TreeOfLifeReaderPlugin extends GenericPluginAdapter {

    /**
     * Constructs a new <code>{@link TreeOfLifeReaderPlugin}</code>.
     */
    public TreeOfLifeReaderPlugin() {
        super();
        this.inputSerializers = new ToLReader[1];
        this.inputSerializers[0] = new ToLReader();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
