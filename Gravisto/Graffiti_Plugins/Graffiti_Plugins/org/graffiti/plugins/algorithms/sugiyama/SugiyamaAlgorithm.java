// =============================================================================
//
//   SugiyamaAlgorithm.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SugiyamaAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama;

import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This interface extends the interface <code>Algorithm</code>.
 * 
 * A <code>SugiyamaAlgorithm</code> is an <code>Algorithm</code>, that also
 * stores the Sugiyama-Bean <code>SugiyamaData</code>. An algorithm in the
 * Sugiyama-Framework has to implement this interface. Additionally, you have to
 * implement the following methods to indicate support for big nodes,
 * constraints or radial drawing:
 * <ul>
 * <li><b>supportsBigNodes</b>: Return <tt>true</tt> here, if your algorithm can
 * handle big nodes, <tt>false</tt> otherwise
 * <li><b>supportsConstraints</b>: Return <tt>true</tt> here, if your algorithm
 * supports constraints, <tt>false</tt> otherwise
 * <li><b>supportsHorizontalSugiyama</b>: Return <tt>true</tt> here, if your
 * algorithm implements the "classic" sugiyama-algorithm, with horizontal
 * layers, <tt>false</tt> otherwise
 * <li><b>supportsRadialSugiyama</b>: Return <tt>true</tt> here, if your
 * algorithm supports a radial layout, <tt>false</tt> otherwise.
 * </ul>
 * Note that you have to support either horizontal or radial sugiyama. Otherwise
 * your algorithm is unusable for the framework.
 * 
 * @author Ferdinand H&uuml;bner
 */
public interface SugiyamaAlgorithm extends Algorithm {

    /**
     * Getter-method to access the algorithm's <code>SugiyamaData</code>-Bean.
     * 
     * @return Returns this algorithm's <code>SugiyamaData</code>-Bean.
     */
    public SugiyamaData getData();

    /**
     * Setter-method to store a <code>SugiyamaData</code>-Bean
     * 
     * @param data
     *            The <code>SugiyamaData</code>-Bean to store
     */
    public void setData(SugiyamaData data);

    /**
     * This method resets the implementing algorithm's parameters to its default
     * state instead of displaying old parameters that the user entered
     * previously.
     */
    // public void setDefaultParameters();

    /** Return whehter your algorithm supports big nodes or not */
    public boolean supportsBigNodes();

    /** Return whether your algorithm supports constraints or not */
    public boolean supportsConstraints();

    /**
     * Return whether your algorithm supports the given algorithm type
     * 
     * @param algorithmType
     *            <code>SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA</code>,
     *            <code>SugiyamaConstants.PARAM_RADIAL_SUGIYAMA</code> or
     *            <code>SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA</code>
     */
    public boolean supportsAlgorithmType(String algorithmType);

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
