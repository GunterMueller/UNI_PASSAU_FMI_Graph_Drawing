// =============================================================================
//
//   DoubleLinkedList.java
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
public class DoubleLinkedList<T> {
    protected ListEntry<T> first;

    protected ListEntry<T> last;

    protected int size;

    public DoubleLinkedList() {
        first = null;
        last = null;
        size = 0;
    }

    public ListEntry<T> addLast(T element) {
        size++;

        if (first == null) {
            first = new ListEntry<T>(this, null, null, element);
            last = first;
        } else {
            ListEntry<T> node = new ListEntry<T>(this, last, null, element);
            last.next = node;
            last = node;
        }

        return last;
    }

    public void addLast(ListEntry<T> entry) {
        size++;
        entry.list = this;
        entry.prev = last;
        entry.next = null;

        if (last != null) {
            last.next = entry;
        }

        if (first == null) {
            first = entry;
        }

        last = entry;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
