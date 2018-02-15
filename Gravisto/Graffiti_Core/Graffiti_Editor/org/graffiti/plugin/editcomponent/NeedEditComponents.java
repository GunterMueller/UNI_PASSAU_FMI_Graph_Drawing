// =============================================================================
//
//   NeedEditComponents.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NeedEditComponents.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.editcomponent;

import java.util.Map;

/**
 * 
 */
public interface NeedEditComponents {

    /**
     * Set the map that connects attributes and parameters with editcomponents.
     * 
     * @param ecMap
     */
    public void setEditComponentMap(Map<Class<?>, Class<?>> ecMap);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
