// =============================================================================
//
//   ValueEditComponentListener.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.editcomponent;

import java.util.EventListener;

/**
 * @author hanauer
 * @version $Revision$ $Date$
 */
public interface ValueEditComponentListener extends EventListener {

    public void vecChanged(VECChangeEvent event);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
