// =============================================================================
//
//   BufferIndirection.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.buffer;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
final class BufferIndirection {
    private TriangleBuffer buffer;

    protected BufferIndirection(TriangleBuffer buffer) {
        this.buffer = buffer;
    }

    protected void invalidate() {
        buffer = null;
    }

    protected TriangleBuffer get() {
        if (buffer == null)
            throw new InvalidBufferBlockException();
        return buffer;
    }

    protected boolean isValid() {
        return buffer != null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
