// =============================================================================
//
//   ListEntry.java
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
public class ListEntry<T> {
    protected DoubleLinkedList<T> list;

    protected ListEntry<T> next;

    protected ListEntry<T> prev;

    protected T element;

    public ListEntry(DoubleLinkedList<T> list, ListEntry<T> prev,
            ListEntry<T> next, T element) {
        this.list = list;
        this.prev = prev;
        this.next = next;
        this.element = element;
    }

    public void detach() {
        if (prev != null) {
            prev.next = next;
        }

        if (next != null) {
            next.prev = prev;
        }

        if (list.first == this) {
            list.first = next;
        }

        if (list.last == this) {
            list.last = prev;
        }
    }

    public void removeFromList() {
        detach();

        prev = null;
        next = null;
        list.size--;
    }

    public void setRaw(ListEntry<T> prev, ListEntry<T> next) {
        this.prev = prev;
        this.next = next;
    }

    public void setToFirst() {
        detach();
        prev = null;
        next = list.first;

        if (list.first != null) {
            list.first.prev = this;
        }

        list.first = this;
    }

    public void setToLast() {
        detach();
        prev = list.last;
        next = null;

        if (list.last != null) {
            list.last.next = this;
        }

        list.last = this;
    }

    public void insertAfter(ListEntry<T> entry) {
        ListEntry<T> nextElement = next;

        if (nextElement != null) {
            nextElement.prev = entry;
        }

        entry.next = nextElement;

        next = entry;
        entry.prev = this;

        if (this == list.last) {
            list.last = entry;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
