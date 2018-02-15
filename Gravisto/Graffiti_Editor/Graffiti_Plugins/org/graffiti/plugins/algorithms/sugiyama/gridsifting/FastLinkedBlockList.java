// =============================================================================
//
//   FastLinkedList.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class FastLinkedBlockList {

    protected FastLinkedBlockListNode first;
    protected FastLinkedBlockListNode last;

    public FastLinkedBlockList() {
    }

    public void addLast(Block<?> element) {
        FastLinkedBlockListNode newNode = new FastLinkedBlockListNode(element);

        if (last != null) {
            last.next = newNode;
        } else {
            first = newNode;
        }

        last = newNode;
    }

    public void clear() {
        first = null;
        last = null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
