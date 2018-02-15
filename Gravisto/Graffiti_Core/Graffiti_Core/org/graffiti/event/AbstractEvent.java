// =============================================================================
//
//   AbstractEvent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractEvent.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

import java.util.EventObject;

/**
 * This class is merely meant to group all the Node-/Edge-/ etc. events. One
 * could imagine that common things could be added right there (for example the
 * timestamp of the event). Otherwise the class is empty.
 * 
 * @version $Revision: 5767 $
 */
public abstract class AbstractEvent extends EventObject {
    /**
     * 
     */
    private static final long serialVersionUID = -7349407827759426673L;

    /**
     * Constructs an AbstractEvent with object o as source.
     * 
     * @param o
     *            the object that is considered as source of the event.
     */
    public AbstractEvent(Object o) {
        super(o);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
