// =============================================================================
//
//   LockedTreeException.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

/**
 * This exception is thrown when one tries to flip a {@link Tree} or add it to a
 * {@link TreeCombinationStack} or {@link TreeCombinationList} although it or
 * its flipped counterpart is currently contained in such a stack or list.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class LockedTreeException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -5073291460728124153L;

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
