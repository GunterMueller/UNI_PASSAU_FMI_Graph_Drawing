// =============================================================================
//
//   GMLReaderPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GDCReaderPlugin.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.ios.importers.gdc;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.io.InputSerializer;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5772 $ $Date: 2006-02-01 21:31:23 +0100 (Mi, 01 Feb 2006)
 *          $
 */
public class GDCReaderPlugin extends GenericPluginAdapter {

    /**
     * Creates a new GMLReaderPlugin object.
     */
    public GDCReaderPlugin() {
        this.inputSerializers = new InputSerializer[1];
        this.inputSerializers[0] = new GDCReader();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
