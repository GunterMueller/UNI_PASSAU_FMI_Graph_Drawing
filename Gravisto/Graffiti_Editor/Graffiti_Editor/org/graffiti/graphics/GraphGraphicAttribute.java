// =============================================================================
//
//   GraphGraphicAttribute.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.graphics;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graphics.grid.GridAttribute;

/**
 * @author kaljinka
 * @version $Revision$ $Date$
 */
public class GraphGraphicAttribute extends HashMapAttribute {

    /**
     * @param id
     */
    public GraphGraphicAttribute(String id) {
        super(id);

        add(new GridAttribute(GraphicAttributeConstants.GRID));
    }

    public GraphGraphicAttribute() {
        this(GraphicAttributeConstants.GRAPHICS);
    }

    @Override
    public Object copy() {
        GraphGraphicAttribute gga = new GraphGraphicAttribute(getId());

        gga.remove("grid");
        gga.add((Attribute) getAttribute("grid").copy());

        return gga;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
