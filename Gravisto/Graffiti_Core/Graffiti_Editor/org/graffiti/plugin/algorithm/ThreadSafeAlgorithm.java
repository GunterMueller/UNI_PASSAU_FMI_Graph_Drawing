// =============================================================================
//
//   ThreadSafeAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================

package org.graffiti.plugin.algorithm;

import javax.swing.JComponent;

import org.graffiti.graph.Graph;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision: 5768 $
 */
public abstract class ThreadSafeAlgorithm implements Algorithm {
    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Graph getGraph() {
        return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public String toString() {
        return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param jc
     * 
     * @return true, if an GUI was set, false if no interface is needed
     */
    abstract public boolean setControlInterface(
            final ThreadSafeOptions options, JComponent jc);

    abstract public void executeThreadSafe(ThreadSafeOptions options);

    abstract public void resetDataCache(ThreadSafeOptions options);
}
