// =============================================================================
//
//   HbgfPlugin.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.hbgf;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.io.InputSerializer;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class HbgfReaderPlugin extends GenericPluginAdapter {
    public HbgfReaderPlugin() {
        inputSerializers = new InputSerializer[] {
                new HbgfReader()
        };
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
