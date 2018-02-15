package org.graffiti.plugins.algorithms.treedrawings.orthTreeDrawingsWinding;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * @author matezder
 * 
 */
public class OrthogonalWindingPlugin extends GenericPluginAdapter {

    /**
     * 
     */
    public OrthogonalWindingPlugin() {
        this.algorithms = new Algorithm[] { new OrthogonalUpwardDrawing() // ,
        // new OrthogonalUpwardDrawingWinding()
        };

    }

    @Override
    public String getName() {
        return ("Orthogonal Planar Winding");
    }

}
