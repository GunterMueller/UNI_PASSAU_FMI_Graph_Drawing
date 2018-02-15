// =============================================================================
//
//   DefaultAlgorithmResult.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DefaultAlgorithmResult.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.algorithm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * DOCUMENT ME!
 * 
 * @author Paul
 */
public class DefaultAlgorithmResult implements AlgorithmResult {

    /** DOCUMENT ME! */
    protected Map<String, Object> resultMap = new LinkedHashMap<String, Object>();

    private Object[] comps = null;

    private boolean specialComponentsSet = false;
    private boolean defaultComponentsUpdateNecessary = true;

    /**
     * Constructor for DefaultAlgorithmResult.
     */
    public DefaultAlgorithmResult() {
        super();
    }

    /**
     * Constructor for DefaultAlgorithmResult.
     * 
     * @param resultMap
     *            DOCUMENT ME!
     */
    public DefaultAlgorithmResult(Map<String, Object> resultMap) {
        super();
        this.resultMap = resultMap;
    }

    /**
     * @see org.graffiti.plugin.algorithm.AlgorithmResult#getResult()
     */
    public Map<String, Object> getResult() {
        return this.resultMap;
    }

    /*
     * @see
     * org.graffiti.plugin.algorithm.AlgorithmResult#addToResult(java.lang.String
     * , java.lang.Object)
     */
    public void addToResult(String key, Object value) {
        this.resultMap.put(key, value);
        defaultComponentsUpdateNecessary = true;
    }

    public void setComponentsForJDialog(Object[] comps) {
        this.comps = comps;
        specialComponentsSet = true;
    }

    public void setComponentForJDialog(Object comp) {
        setComponentsForJDialog(new Object[] { comp });
    }

    public Object[] getComponentsForJDialog() {
        if ((specialComponentsSet || !defaultComponentsUpdateNecessary)
                && comps != null)
            return comps;
        String comp = "";
        for (String key : resultMap.keySet()) {
            comp += key + ": " + resultMap.get(key) + "\n";
        }
        comps = new Object[] { comp };
        defaultComponentsUpdateNecessary = false;
        return comps;
    }

    /**
     * By default return the string representation of the internal map.
     */
    @Override
    public String toString() {
        return getResult().toString();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
