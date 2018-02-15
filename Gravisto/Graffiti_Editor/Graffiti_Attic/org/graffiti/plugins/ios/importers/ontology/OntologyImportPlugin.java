package org.graffiti.plugins.ios.importers.ontology;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.io.InputSerializer;

/**
 * Adds support for importing ontologies as graphs.
 * 
 * @author frankenb
 * @version $Revision$ $Date$
 */
public class OntologyImportPlugin extends GenericPluginAdapter {

    /**
     * Default constructor.
     */
    public OntologyImportPlugin() {
        inputSerializers = new InputSerializer[] { new OntologyReader(), };
    }
}