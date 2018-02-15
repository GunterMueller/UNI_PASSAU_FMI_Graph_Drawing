// =============================================================================
//
//   GMLReaderPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GMLReaderPlugin.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.ios.importers.gml;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.io.InputSerializer;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5772 $ $Date: 2006-03-02 17:58:05 +0100 (Do, 02 Mrz 2006)
 *          $
 */
public class GMLReaderPlugin extends GenericPluginAdapter {

    /**
     * Creates a new GMLReaderPlugin object.
     */
    public GMLReaderPlugin() {
        this.inputSerializers = new InputSerializer[1];
        this.inputSerializers[0] = new GMLReader();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
