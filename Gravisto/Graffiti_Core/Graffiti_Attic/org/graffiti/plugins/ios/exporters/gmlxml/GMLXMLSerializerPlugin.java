// =============================================================================
//
//   GMLSerializerPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GMLXMLSerializerPlugin.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.ios.exporters.gmlxml;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.io.OutputSerializer;

/**
 * Provides a GML serializer. See http://infosun.fmi.uni-passau.de/Graphlet/GML/
 * for more details.
 * 
 * @version $Revision: 5772 $
 */
public class GMLXMLSerializerPlugin extends GenericPluginAdapter {

    /**
     * Constructor for GMLSerializerPlugin.
     */
    public GMLXMLSerializerPlugin() {
        super();

        // TODO perhaps: merge this and
        // org.graffiti.plugins.io.exporter.gml.GMLReaderPlugin.
        outputSerializers = new OutputSerializer[1];
        outputSerializers[0] = new GMLXMLWriter();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
