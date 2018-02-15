// =============================================================================
//
//   OutputSerializerTestPlugin.java
//
//   Copyright (c) 2001-2003 Gravisto Team, Uni Passau
//
// =============================================================================
//  $

package de.chris.plugins.inputserializers.test;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.io.InputSerializer;

/**
 * This is a simple example for an outputserializer plugin in Gravisto.
 */
public class InputSerializerTestPlugin extends GenericPluginAdapter {

    /**
     * Creates a new OutputSerialzerTestPlugin object.
     */
    public InputSerializerTestPlugin() {
        super();

        inputSerializers = new InputSerializer[1];
        inputSerializers[0] = new TestInputSerializer();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
