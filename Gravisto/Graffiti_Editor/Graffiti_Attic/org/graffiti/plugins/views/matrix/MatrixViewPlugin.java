// =============================================================================
//
//   MatrixViewPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MatrixViewPlugin.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.views.matrix;

import org.graffiti.plugin.EditorPluginAdapter;

/**
 * Provides a matrix view implementation.
 * 
 * @version $Revision: 5772 $
 */
public class MatrixViewPlugin extends EditorPluginAdapter {

    /**
     * Constructs a new matrix view plugin.
     */
    public MatrixViewPlugin() {
        this.views = new String[1];
        this.views[0] = "org.graffiti.plugins.views.matrix.MatrixView";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
