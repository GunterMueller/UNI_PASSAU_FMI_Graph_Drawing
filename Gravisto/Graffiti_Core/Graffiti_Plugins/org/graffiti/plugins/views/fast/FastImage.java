// =============================================================================
//
//   FastImage.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.graffiti.plugins.views.fast.label.Label;
import org.graffiti.plugins.views.fast.label.LabelCommand;
import org.graffiti.plugins.views.fast.label.LabelManager;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class FastImage<L extends Label<L, LC>, LC extends LabelCommand> {
    private LinkedList<FastImage<L, LC>> derivedImages;
    private Set<L> dependentLabels;

    protected FastImage() {
    }

    protected FastImage(FastImage<L, LC> image) {
        if (image.derivedImages == null) {
            image.derivedImages = new LinkedList<FastImage<L, LC>>();
        }
        image.derivedImages.add(this);
    }

    public void dispose(LabelManager<L, LC> labelManager) {
        onDispose();
        secondaryDispose(labelManager);
    }

    private void secondaryDispose(LabelManager<L, LC> labelManager) {
        if (dependentLabels != null) {
            for (L label : dependentLabels) {
                labelManager.changeFormat(label);
            }
        }
        if (derivedImages != null) {
            for (FastImage<L, LC> image : derivedImages) {
                image.secondaryDispose(labelManager);
            }
        }
    }

    public void addDependentLabel(L label) {
        if (dependentLabels == null) {
            dependentLabels = new HashSet<L>();
        }
        dependentLabels.add(label);
    }

    public abstract FastImage<L, LC> getScaled(int targetWidth, int targetHeight);

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void onDispose();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
