// =============================================================================
//
//   InvalidBufferBlockException.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl.buffer;

/**
 * {@code InvalidBufferBlockException} is thrown when one tries to access a
 * {@link BufferBlock} that was allocated from a {@link TriangleBuffer} that has
 * been reset in the meantime.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class InvalidBufferBlockException extends IllegalStateException {

    /**
     * 
     */
    private static final long serialVersionUID = -7597703242201160286L;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
