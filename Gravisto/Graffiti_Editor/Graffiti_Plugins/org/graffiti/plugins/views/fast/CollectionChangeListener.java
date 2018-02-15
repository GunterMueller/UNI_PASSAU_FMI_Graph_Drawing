// =============================================================================
//
//   CollectionChangeListener.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public interface CollectionChangeListener<T> {
    public void onAdd(T t);

    public void onRemove(T t);

    public void onClear();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
