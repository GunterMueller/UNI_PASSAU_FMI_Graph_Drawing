// =============================================================================
//
//   NoDecycling.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.layout;

import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * @author brunner
 * @version $Revision$ $Date$
 */
public class DummyCyclicLayout extends AbstractAlgorithm implements
        LayoutAlgorithm {

    private SugiyamaData data;

    public boolean supportsArbitraryXPos() {
        return false;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.decycling.DecyclingAlgorithm
     * #undo()
     */
    public void undo() {
    }

    /*
     * @see org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#getData()
     */
    public SugiyamaData getData() {
        return data;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#setData(org
     * .graffiti.plugins.algorithms.sugiyama.util.SugiyamaData)
     */
    public void setData(SugiyamaData data) {
        this.data = data;
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#
     * supportsAlgorithmType(java.lang.String)
     */
    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType.equals(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA);
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#supportsBigNodes
     * ()
     */
    public boolean supportsBigNodes() {
        return true;
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#
     * supportsConstraints()
     */
    public boolean supportsConstraints() {
        return true;
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        // Do nothing
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Dummy (do nothing)";
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
