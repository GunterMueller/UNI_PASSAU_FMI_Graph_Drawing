// =============================================================================
//
//   EditorAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EditorAlgorithm.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.algorithm;

import org.graffiti.editor.dialog.ParameterDialog;
import org.graffiti.selection.Selection;

/**
 * 
 */
public interface EditorAlgorithm extends Algorithm {

    /**
     * Returns a custom <code>ParameterDialog</code> if the algorithm wants to
     * provide one. If this method returns null, a generic dialog will be
     * generated using standard <code>EditComponent</code>s.
     * 
     * @return DOCUMENT ME!
     */
    public ParameterDialog getParameterDialog(Selection sel);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
