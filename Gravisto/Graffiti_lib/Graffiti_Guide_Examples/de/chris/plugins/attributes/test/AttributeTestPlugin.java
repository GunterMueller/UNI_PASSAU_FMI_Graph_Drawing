// =============================================================================
//
//   AttributeTestPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeTestPlugin.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.attributes.test;

import org.graffiti.plugin.GenericPluginAdapter;

/**
 * DOCUMENT ME!
 * 
 * @author chris
 * @version $Revision: 5769 $ $Date: 2006-01-04 10:21:57 +0100 (Mi, 04 Jan 2006)
 *          $
 */
public class AttributeTestPlugin extends GenericPluginAdapter {

    /**
     * Creates a new AttributeTestPlugin object.
     */
    public AttributeTestPlugin() {
        this.attributes = new Class[1];
        this.attributes[0] = TestAttribute.class;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
