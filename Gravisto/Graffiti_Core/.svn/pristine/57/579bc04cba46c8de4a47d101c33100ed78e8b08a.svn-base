// =============================================================================
//
//   HexaGridPentaTreeDrawingPlugin.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$
package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugins.algorithms.treedrawings.orthTreeDrawingsWinding.OrthogonalUpwardDrawing;

/**
 * Plugin for the HexaGridPentaTreeDrawing class
 */
public class HexaGridPentaTreeDrawingPlugin extends GenericPluginAdapter {

    /**
     * Creates a new HexaGridPentaTreeDrawingPlugin object.
     */
    public HexaGridPentaTreeDrawingPlugin() {
        this.algorithms = new Algorithm[] { new NonConvexContoursAlgorithm(),
                new ConvexContourAlgorithm(), new OrthogonalUpwardDrawing() };

    }

    @Override
    public String getName() {
        return ("5-ary trees on a triangular grid");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
