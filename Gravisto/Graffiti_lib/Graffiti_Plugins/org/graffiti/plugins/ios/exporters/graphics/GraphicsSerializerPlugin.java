//==============================================================================
//
//   GraphicsSerializerPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//==============================================================================
// $Id: GraphicsSerializerPlugin.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.exporters.graphics;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.io.OutputSerializer;

/**
 * The plugin for writing graphic files like pdf or png.
 * 
 * @version $Revision: 5766 $
 */
public class GraphicsSerializerPlugin extends GenericPluginAdapter {

    /**
     * Creates a new GraphicsSerializerPlugin object.
     */
    public GraphicsSerializerPlugin() {
        this.outputSerializers = new OutputSerializer[] { new PngSerializer(),
                new PdfSerializer() };
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
