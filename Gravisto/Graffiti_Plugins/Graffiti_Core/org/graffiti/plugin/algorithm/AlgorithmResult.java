// =============================================================================
//
//   AlgorithmResult.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AlgorithmResult.java 5767 2010-05-07 18:42:02Z gleissner $

/*
 * $Id
 */

package org.graffiti.plugin.algorithm;

import java.util.Map;

/**
 * An <code>AlgorithmResult</code> is a map of results that were computed by a
 * <code>CalculatingAlgorithm</code>. It maps the name of a result to the
 * corresponding value.
 * 
 * @version $Revision: 5767 $
 * 
 * @see CalculatingAlgorithm
 */
public interface AlgorithmResult {
    /**
     * Returns the <code>Map</code>. This function is intended to be used by
     * other components that want to display the results.
     * 
     * @return DOCUMENT ME!
     */
    public Map<String, Object> getResult();

    /**
     * Adds a key-value pair to the <code>Map</code>.
     * 
     * @param key
     *            the key for the result.
     * @param value
     *            the value of the result.
     */
    public void addToResult(String key, Object value);

    public void setComponentsForJDialog(Object[] comps);

    public void setComponentForJDialog(Object comp);

    public Object[] getComponentsForJDialog();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
