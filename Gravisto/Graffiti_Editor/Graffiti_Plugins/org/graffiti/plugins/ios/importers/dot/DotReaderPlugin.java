// =============================================================================
//
//   GraphMLReaderPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DotReaderPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.importers.dot;

import org.graffiti.plugin.GenericPluginAdapter;

/**
 * This plugin provides the functionality for reading dot files.
 * 
 * @author keilhaue
 */
public class DotReaderPlugin extends GenericPluginAdapter {

    /**
     * Constructs a new <code>DotReaderPlugin</code>.
     */
    public DotReaderPlugin() {
        super();
        this.inputSerializers = new DotReader[1];
        this.inputSerializers[0] = new DotReader();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
