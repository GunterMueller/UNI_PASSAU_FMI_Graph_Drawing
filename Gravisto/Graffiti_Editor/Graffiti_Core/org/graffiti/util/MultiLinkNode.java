// =============================================================================
//
//   ListLink.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MultiLinkNode.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

/**
 * The linking information of a {@link MultiLinkedList}. For each element of the
 * list there is one instance of this class that stores the navigational
 * pointers to the next and previous element in the list.
 * 
 * Elements contained in a {@link MultiLinkedList} must support storage of a
 * {@link MultiLinkNode}, see {@link MultiLinkable}.
 * 
 * @author Michael Forster
 * @version $Revision: 5767 $ $Date: 2006-01-13 12:21:25 +0100 (Fr, 13 Jan 2006)
 *          $
 */
public class MultiLinkNode<E> {
    /** The next node of the doubly linked list */
    MultiLinkNode<E> next;

    /** The previous node of the doubly linked list */
    MultiLinkNode<E> prev;

    /** The content of this list node */
    E content;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
