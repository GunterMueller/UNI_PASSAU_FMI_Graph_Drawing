// =============================================================================
//
//   OpenGLFastView.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLFastView extends FastView {
    /**
     * 
     */
    private static final long serialVersionUID = -8419965202626309643L;

    public OpenGLFastView() {
        super(new OpenGLEngine());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
