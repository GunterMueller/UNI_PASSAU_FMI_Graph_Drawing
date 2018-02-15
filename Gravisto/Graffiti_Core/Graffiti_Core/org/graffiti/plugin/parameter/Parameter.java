// =============================================================================
//
//   Parameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Parameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

import java.awt.image.BufferedImage;
import java.io.Serializable;

import org.graffiti.plugin.Displayable;

/**
 * Interface for a parameter used by an <code>Algorithm</code>.
 * 
 * @version $Revision: 5767 $
 * 
 * @see org.graffiti.plugin.algorithm.Algorithm
 */
public interface Parameter<T> extends Displayable<T>, Serializable {

    /**
     * Returns an image representing the <code>Parameter</code>. May return
     * <code>null</code> if there is no representing image.
     * 
     * @return an image representing the <code>Parameter</code>.
     */
    public BufferedImage getImage();

    public Parameter<T> copy();

    public boolean canCopy();

    public void setObjectValue(Object value);

    /**
     * Sets a depencency. <code>this</code> is only visible in the parameter
     * dialog, if <code>parent</code> is visible and has the value
     * <code>value</code>.
     * 
     * @param parent
     * @param value
     */
    public void setDependency(Parameter<?> parent, Object value);

    public Parameter<?> getDependencyParent();

    public Object getDependencyValue();

    public void setVisible(boolean visible);

    public boolean isVisible();

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
