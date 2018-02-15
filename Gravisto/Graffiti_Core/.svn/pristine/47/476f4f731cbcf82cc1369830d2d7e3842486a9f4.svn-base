//   NexusReaderPlugin.java

package org.graffiti.plugins.ios.importers.nexus;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.io.InputSerializer;

/**
 * This plugin provides the functionality for reading nexus formatted files.
 * 
 * @author Peter Häring
 */
public class NexusReaderPlugin extends GenericPluginAdapter {

    /**
     * Constructs a new <code>NexusReaderPlugin</code>.
     */
    public NexusReaderPlugin() {
        this.inputSerializers = new InputSerializer[2];
        this.inputSerializers[0] = new NexusReader();
        this.inputSerializers[1] = new NewickReader();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
