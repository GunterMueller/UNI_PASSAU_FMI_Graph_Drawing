// =============================================================================
//
//   StandardAttributeComponents.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StandardAttributeComponents.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.attributecomponents.simplelabel;

import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.EditorPluginAdapter;

/**
 * This plugin contains the standard attribute-attributeComponent mappings.
 * 
 * @version $Revision: 5766 $
 */
public class StandardAttributeComponents extends EditorPluginAdapter {

    /**
     * Creates a new StandardAttributeComponents object.
     */
    public StandardAttributeComponents() {
        this.attributeComponents.put(NodeLabelAttribute.class,
                LabelComponent.class);
        this.attributeComponents.put(EdgeLabelAttribute.class,
                LabelComponent.class);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
