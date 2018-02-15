// =============================================================================
//
//   Observable.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.util.LinkedList;

/**
 * Class that encapsulates a property and manages a list of {@code Observer}s,
 * which will be informed when that property changes.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @param <T>
 *            The type of the encapsulated property.
 */
public class Observable<T> {
    /**
     * The encapsulated property.
     */
    private T value;

    /**
     * The list of observers that will be informed when the encapsulated
     * property changes.
     */
    private LinkedList<Observer<T>> observers;

    /**
     * Constructs a {@code Observable} and initializes the encapsulated property
     * with the specified value.
     * 
     * @param value
     *            the initial value of the encapsulated property.
     */
    public Observable(T value) {
        this.value = value;
    }

    /**
     * Constructs a {@code Observable}.
     */
    public Observable() {
    }

    /**
     * Returns the value of the encapsulated property.
     * 
     * @return the value of the encapsulated property.
     */
    public T get() {
        return value;
    }

    /**
     * Sets the encapsulated variable to the specified value and notifies all
     * {@link Observer}s about the change.
     * 
     * @param value
     *            the new value assigned to the encapsulated property.
     */
    public void set(T value) {
        this.value = value;
        if (observers != null) {
            for (Observer<T> observer : observers) {
                observer.onChange(value);
            }
        }
    }

    /**
     * Adds an {@code Observer} to the list of observers that will be notified
     * when the encapsulated property is assigned a new value. If an observer is
     * added to this list multiple times, it will be notified accordingly
     * multiple times in response to each single change of the encapsulated
     * property.
     * 
     * @param observer
     *            the observer to be added to the list of observers monitoring
     *            the encapsulated property.
     */
    public void addObserver(Observer<T> observer) {
        if (observers == null) {
            observers = new LinkedList<Observer<T>>();
        }
        observers.add(observer);
    }

    /**
     * Clears the list of observers.
     */
    public void clearObservers() {
        observers = null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
