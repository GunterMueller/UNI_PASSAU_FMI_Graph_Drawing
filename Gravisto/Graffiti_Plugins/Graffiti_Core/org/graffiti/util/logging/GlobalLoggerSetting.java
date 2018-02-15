// =============================================================================
//
//   GlobalLoggerSetting.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util.logging;

import java.util.logging.Level;

/**
 * Encapsulates the global logging level for all loggers in the core.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class GlobalLoggerSetting {
    public static final Level LOGGER_LEVEL = Level.SEVERE;
    
    private GlobalLoggerSetting() {
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
