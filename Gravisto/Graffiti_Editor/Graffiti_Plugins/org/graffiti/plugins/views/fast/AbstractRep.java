// =============================================================================
//
//   AbstractRep.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugins.views.fast.label.Label;
import org.graffiti.plugins.views.fast.label.LabelCommand;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class AbstractRep<L extends Label<L, LC>, LC extends LabelCommand> {
    protected LinkedList<L> labels;
    protected double depth;

    /**
     * Number of node and edge representations already generated
     */
    protected static int globalNumberOfReps = 0;

    /**
     * The number of this representation. Used to sort the representations from
     * older to newer while drawing
     */
    public int repNumber;

    protected AbstractRep() {
        globalNumberOfReps++;
        repNumber = globalNumberOfReps;
    }

    public final void addLabel(L label) {
        if (labels == null) {
            labels = new LinkedList<L>();
        }
        labels.add(label);
        onAddLabel(label);
    }

    protected void onAddLabel(L label) {
    };

    public final L getLabel(LabelAttribute attribute) {
        if (labels == null)
            return null;
        String path = attribute.getPath();
        for (L label : labels) {
            if (label.represents(path))
                return label;
        }
        return null;
    }

    public void onChangeLabel(L label) {
    };

    public final void removeLabel(LabelAttribute attribute) {
        if (labels == null)
            return;
        String path = attribute.getPath();
        for (Iterator<L> iter = labels.iterator(); iter.hasNext();) {
            L label = iter.next();
            if (label.represents(path)) {
                iter.remove();
                return;
            }
        }
    }

    public final void removeLabel(L label) {
        if (labels == null)
            return;
        labels.remove(label);
        if (labels.isEmpty()) {
            labels = null;
        }
        onRemoveLabel(label);
    }

    protected void onRemoveLabel(L label) {
    };

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public double getDepth() {
        return depth;
    }

    public int getRepNumber() {
        return repNumber;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
