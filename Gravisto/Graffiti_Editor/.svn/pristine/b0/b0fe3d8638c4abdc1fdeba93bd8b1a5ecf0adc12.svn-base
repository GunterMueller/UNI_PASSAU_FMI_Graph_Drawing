// =============================================================================
//
//   AbstractRepDepthComparator.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.util.Comparator;

import org.graffiti.plugins.views.fast.label.Label;
import org.graffiti.plugins.views.fast.label.LabelCommand;

/**
 * Orders {@link AbstractRep} objects by their depth value (z-coordinate) while
 * maintaing the consistence with {@link Object#equals(Object)}. Objects with
 * higher depth values are considered smaller.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class AbstractRepDepthComparator<L extends Label<L, LC>, LC extends LabelCommand>
        implements Comparator<AbstractRep<L, LC>> {
    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(AbstractRep<L, LC> arg0, AbstractRep<L, LC> arg1) {
        double depth0 = arg0.getDepth();
        double depth1 = arg1.getDepth();
        if (depth0 > depth1)
            return -1;
        if (depth0 < depth1)
            return 1;
        return arg0.getRepNumber() - arg1.getRepNumber();
        // return objectReferenceComparator.compare(arg0, arg1);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
