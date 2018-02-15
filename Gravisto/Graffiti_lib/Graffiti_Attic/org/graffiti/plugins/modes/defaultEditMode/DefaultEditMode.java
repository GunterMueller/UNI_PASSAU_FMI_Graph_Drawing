// =============================================================================
//
//   DefaultEditMode.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DefaultEditMode.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.modes.defaultEditMode;

import java.util.ArrayList;

import org.graffiti.plugin.mode.GraphConstraint;

/**
 * @author Andreas Pick
 * @version $Revision: 5772 $ $Date: 2008-12-31 05:02:05 +0100 (Mi, 31 Dez 2008)
 *          $
 * @deprecated
 */
@Deprecated
public class DefaultEditMode extends AbstractMode {
    public final static String sid = "org.graffiti.plugins.modes.defaultEditMode";

    public DefaultEditMode() {
        this.id = sid;
        this.constraints = new GraphConstraint[0];
        this.tools = new ArrayList<Tool>();
    }
}
