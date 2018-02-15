// =============================================================================
//
//   AbstractEditorAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractEditorAlgorithm.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.algorithm;

import org.graffiti.editor.dialog.ParameterDialog;
import org.graffiti.selection.Selection;

/**
 * DOCUMENT ME!
 * 
 * @author $Author: gleissner $
 * @version $Revision: 5768 $ $Date: 2006-01-03 14:21:54 +0100 (Di, 03 Jan 2006)
 *          $
 */
public abstract class AbstractEditorAlgorithm extends AbstractAlgorithm
        implements EditorAlgorithm {

    /*
     * @see
     * org.graffiti.plugin.algorithm.EditorAlgorithm#getParameterDialog(org.
     * graffiti.selection.Selection)
     */
    public ParameterDialog getParameterDialog(Selection s) {
        return null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
