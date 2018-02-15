// =============================================================================
//
//   PluginDescriptionCollector.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PluginDescriptionCollector.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.managers.pluginmgr;

import java.util.List;

/**
 * Collects plugin description URLs, which can be used by the PluginSelector.
 * 
 * @version $Revision: 5767 $
 * 
 * @see PluginSelector
 * @see org.graffiti.managers.pluginmgr.Entry
 */
public interface PluginDescriptionCollector {

    /**
     * Returns an enumeration of {@link org.graffiti.managers.pluginmgr.Entry}s.
     * 
     * @return DOCUMENT ME!
     */
    public List<Entry> collectPluginDescriptions();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
