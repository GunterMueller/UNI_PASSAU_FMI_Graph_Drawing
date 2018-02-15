// =============================================================================
//
//   MultiLinkable.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MultiLinkable.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

/**
 * Interface that must be implemented by classes that are to be stored in a
 * {@link MultiLinkedList}.
 * 
 * For efficient access of the {@link MultiLinkNode} that corresponds to a list
 * element (this is needed for instance in the
 * {@link MultiLinkedList#remove(Object)} method), each list element must store
 * a pointer to the respective node. Access to this pointer is by the
 * {@link #getLinkNode(Object)} and {@link #setLinkNode(Object, MultiLinkNode)}
 * methods.
 * 
 * @param <Element>
 *            The type of the elements contained in the list.
 * @param <LinkType>
 *            The class (typically an enum) that is used to specify which list
 *            to use if the element is contained in more than one
 *            {@link MultiLinkedList}.
 * 
 * @author Michael Forster
 * @version $Revision: 5767 $ $Date: 2006-01-13 12:21:25 +0100 (Fr, 13 Jan 2006)
 *          $
 */
public interface MultiLinkable<Element, LinkType> {
    /**
     * Returns the link node corresponding to the list identified by the given
     * link type.
     * 
     * @param linkType
     *            Identification of the respective list.
     * @return The corresponding link node.
     */
    MultiLinkNode<Element> getLinkNode(LinkType linkType);

    /**
     * Sets the link node corresponding to the list identified by the given link
     * type.
     * 
     * @param linkType
     *            Identification of the respective list.
     * @param linkNode
     *            The corresponding link node.
     */
    void setLinkNode(LinkType linkType, MultiLinkNode<Element> linkNode);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
