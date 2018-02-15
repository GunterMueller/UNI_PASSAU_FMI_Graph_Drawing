// =============================================================================
//
//   ListenerManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ListenerManager.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Class that is responsible to keep track of all the Listeners that are
 * registered. It therefore provides methods to add and remove certain types of
 * Listeners. It also contains methods representing all available events. When
 * one of these methods is called, the <code>ListenerManager</code> delegates
 * the call to all Listeners registered in the appropriate listener set. When a
 * listener is registered as strict, it does not get any messages during the
 * time a transaction is active (i.e. between <code>transactionStarted</code>
 * and <code>transactionFinished</code>). Non strict listeners receive events
 * independent of transactions. It is not possible to add a Listener both as
 * strict and non strict. At the end of a transaction, a set is passed within a
 * <code>TransactionEvent</code> that contains all objects that (might) have
 * been changed. This set is passed to both, strict and non strict listeners.
 * 
 * @version $Revision: 5767 $
 */
public class ListenerManager {
    /**
     * Holds the list of registered AttributeListeners that receive events even
     * if a transaction is active.
     */
    private LinkedList<AttributeListener> nonstrictAttributeListenerList;

    /**
     * Holds the list of registered EdgeListeners that receive events even if a
     * transaction is active.
     */
    private LinkedList<EdgeListener> nonstrictEdgeListenerList;

    /**
     * Holds the list of registered GraphListeners that receive events even if a
     * transaction is active.
     */
    private LinkedList<GraphListener> nonstrictGraphListenerList;

    /**
     * Holds the list of registered NodeListeners that receive events even if a
     * transaction is active.
     */
    private LinkedList<NodeListener> nonstrictNodeListenerList;

    /**
     * Holds the list of registered AttributeListeners that do not receive any
     * events whenever a transaction is active.
     */
    private LinkedList<AttributeListener> strictAttributeListenerList;

    /**
     * Holds the list of registered EdgeListeners that do not receive any events
     * whenever a transaction is active.
     */
    private LinkedList<EdgeListener> strictEdgeListenerList;

    /**
     * Holds the list of registered GraphListeners that do not receive any
     * events whenever a transaction is active.
     */
    private LinkedList<GraphListener> strictGraphListenerList;

    /**
     * Holds the list of registered NodeListeners that do not receive any events
     * whenever a transaction is active.
     */
    private LinkedList<NodeListener> strictNodeListenerList;

    /**
     * Holds all TransactionListeners and how often they are registered to
     * prevent TransactionEvents from being sent several times to the listeners
     */
    private HashMap<TransactionListener, Integer> transactionListeners;

    /** Logs the objects that get changed during a transaction. */
    private Set<Object> changedObjects;

    /**
     * Indicates whether or, to be more exact, how many
     * <code>transactionStarted</code> events have previously been encountered
     * and strict Listeners not notified of events any longer if that number is
     * greater than zero.
     */
    private int transactionsActive = 0;

    /**
     * Default constructor. Initializes all listener sets as empty hash sets.
     */
    public ListenerManager() {
        strictNodeListenerList = new LinkedList<NodeListener>();
        strictEdgeListenerList = new LinkedList<EdgeListener>();
        strictAttributeListenerList = new LinkedList<AttributeListener>();
        strictGraphListenerList = new LinkedList<GraphListener>();
        nonstrictNodeListenerList = new LinkedList<NodeListener>();
        nonstrictEdgeListenerList = new LinkedList<EdgeListener>();
        nonstrictAttributeListenerList = new LinkedList<AttributeListener>();
        nonstrictGraphListenerList = new LinkedList<GraphListener>();

        transactionListeners = new HashMap<TransactionListener, Integer>();

        changedObjects = new HashSet<Object>();
    }

    private void addToTransactionListeners(TransactionListener l) {
        Integer timesRegistered = transactionListeners.get(l);
        if (timesRegistered == null) {
            transactionListeners.put(l, 1);
        } else {
            transactionListeners.put(l, timesRegistered + 1);
        }
    }

    private void removeFromTransactionListeners(TransactionListener l)
            throws ListenerNotFoundException {
        Integer timesRegistered = transactionListeners.get(l);
        if (timesRegistered == null)
            throw new ListenerNotFoundException("The listener you want "
                    + "to remove cannot be found.");
        else {
            transactionListeners.put(l, timesRegistered - 1);
        }
    }

    /**
     * Registers AttributeListener l by adding it to the list of non-strict and
     * strict transaction AttributeListeners.
     * 
     * @param l
     *            the AttributeListener that is registered.
     * 
     * @throws ListenerRegistrationException
     *             DOCUMENT ME!
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void addNonstrictAttributeListener(AttributeListener l)
            throws ListenerRegistrationException {
        if (l == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (!strictAttributeListenerList.contains(l)) {
            if (!nonstrictAttributeListenerList.contains(l)) {
                nonstrictAttributeListenerList.add(l);
                addToTransactionListeners(l);
            }
        } else
            throw new ListenerRegistrationException(
                    "Listener already registered as strict");
    }

    /**
     * Registers EdgeListener l by adding it to the list of non-strict
     * transaction EdgeListeners.
     * 
     * @param l
     *            the EdgeListener that is registered.
     * 
     * @throws ListenerRegistrationException
     *             DOCUMENT ME!
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void addNonstrictEdgeListener(EdgeListener l)
            throws ListenerRegistrationException {
        if (l == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (!strictEdgeListenerList.contains(l)) {
            if (!nonstrictEdgeListenerList.contains(l)) {
                nonstrictEdgeListenerList.add(l);
                addToTransactionListeners(l);
            }
        } else
            throw new ListenerRegistrationException(
                    "Listener already registered as strict");
    }

    /**
     * Registers GraphListener l by adding it to the list of GraphListeners.
     * 
     * @param l
     *            the GraphListener that is registered.
     * 
     * @throws ListenerRegistrationException
     *             DOCUMENT ME!
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void addNonstrictGraphListener(GraphListener l)
            throws ListenerRegistrationException {
        if (l == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (!strictGraphListenerList.contains(l)) {
            if (!nonstrictGraphListenerList.contains(l)) {
                nonstrictGraphListenerList.add(l);
                addToTransactionListeners(l);
            }
        } else
            throw new ListenerRegistrationException(
                    "Listener already registered as strict");
    }

    /**
     * Registers <code>NodeListener</code> l by adding it to the list of
     * non-strict transaction NodeListeners.
     * 
     * @param l
     *            the NodeListener that is registered.
     * 
     * @throws ListenerRegistrationException
     *             DOCUMENT ME!
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void addNonstrictNodeListener(NodeListener l)
            throws ListenerRegistrationException {
        if (l == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (!strictNodeListenerList.contains(l)) {
            if (!nonstrictNodeListenerList.contains(l)) {
                nonstrictNodeListenerList.add(l);
                addToTransactionListeners(l);
            }
        } else
            throw new ListenerRegistrationException(
                    "Listener already registered as strict");
    }

    /**
     * Registers AttributeListener l by adding it to the list of strict
     * transaction AttributeListeners.
     * 
     * @param l
     *            the AttributeListener that is registered.
     * 
     * @throws ListenerRegistrationException
     *             DOCUMENT ME!
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void addStrictAttributeListener(AttributeListener l)
            throws ListenerRegistrationException {
        if (l == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (!nonstrictAttributeListenerList.contains(l)) {
            if (!strictAttributeListenerList.contains(l)) {
                strictAttributeListenerList.add(l);
                addToTransactionListeners(l);
            }
        } else
            throw new ListenerRegistrationException(
                    "Listener already registered as non strict");
    }

    /**
     * Registers EdgeListener l by adding it to the list of strict transaction
     * EdgeListeners.
     * 
     * @param l
     *            the EdgeListener that is registered.
     * 
     * @throws ListenerRegistrationException
     *             DOCUMENT ME!
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void addStrictEdgeListener(EdgeListener l)
            throws ListenerRegistrationException {
        if (l == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (!nonstrictEdgeListenerList.contains(l)) {
            if (!strictEdgeListenerList.contains(l)) {
                strictEdgeListenerList.add(l);
                addToTransactionListeners(l);
            }
        } else
            throw new ListenerRegistrationException(
                    "Listener already registered as non strict");
    }

    /**
     * Registers GraphListener l by adding it to the list of strict transaction
     * GraphListeners.
     * 
     * @param l
     *            the GraphListener that is registered.
     * 
     * @throws ListenerRegistrationException
     *             DOCUMENT ME!
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void addStrictGraphListener(GraphListener l)
            throws ListenerRegistrationException {
        if (l == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (!nonstrictGraphListenerList.contains(l)) {
            if (!strictGraphListenerList.contains(l)) {
                strictGraphListenerList.add(l);
                addToTransactionListeners(l);
            }
        } else
            throw new ListenerRegistrationException(
                    "Listener already registered as non strict");
    }

    /**
     * Registers <code>NodeListener</code> l by adding it to the list of strict
     * transaction NodeListeners.
     * 
     * @param l
     *            the NodeListener that is registered.
     * 
     * @throws ListenerRegistrationException
     *             DOCUMENT ME!
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void addStrictNodeListener(NodeListener l)
            throws ListenerRegistrationException {
        if (l == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (!nonstrictNodeListenerList.contains(l)) {
            if (!strictNodeListenerList.contains(l)) {
                strictNodeListenerList.add(l);
                addToTransactionListeners(l);
            }
        } else
            throw new ListenerRegistrationException(
                    "Listener already registered as non strict");
    }

    /**
     * Called after an attribute has been added. Calls the same method in all
     * <code>AttributeListeners</code> in the
     * <code>strictAttributeListenerList</code>
     * 
     * @param event
     *            the AttributeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postAttributeAdded(AttributeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (AttributeListener l : strictAttributeListenerList) {
                l.postAttributeAdded(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getPath() != null) {
                changedObjects.add(event.getPath());
            }
        }

        for (AttributeListener l : nonstrictAttributeListenerList) {
            l.postAttributeAdded(event);
        }
    }

    /**
     * Called after an attribute has been changed. Calls the same method in all
     * <code>AttributeListeners</code> in the
     * <code>strictAttributeListenerList</code>
     * 
     * @param event
     *            the AttributeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postAttributeChanged(AttributeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (AttributeListener l : strictAttributeListenerList) {
                l.postAttributeChanged(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getPath() != null) {
                changedObjects.add(event.getPath());
            }
        }

        for (AttributeListener l : nonstrictAttributeListenerList) {
            l.postAttributeChanged(event);
        }
    }

    /**
     * Called after an attribute has been removed. Calls the same method in all
     * <code>AttributeListeners</code> in the
     * <code>strictAttributeListenerList</code>
     * 
     * @param event
     *            the AttributeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postAttributeRemoved(AttributeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (AttributeListener l : strictAttributeListenerList) {
                l.postAttributeRemoved(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getPath() != null) {
                changedObjects.add(event.getPath());
            }
        }

        for (AttributeListener l : nonstrictAttributeListenerList) {
            l.postAttributeRemoved(event);
        }
    }

    /**
     * Called after the edge was set directed or undirected. Calls the same
     * method in all <code>EdgeListeners</code> in the
     * <code>strictEdgeListenerList</code>
     * 
     * @param event
     *            the EdgeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postDirectedChanged(EdgeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (EdgeListener l : strictEdgeListenerList) {
                l.postDirectedChanged(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());
        }

        for (EdgeListener l : nonstrictEdgeListenerList) {
            l.postDirectedChanged(event);
        }
    }

    /**
     * Called after an edge has been added to the graph. Calls the same method
     * in all <code>GraphListeners</code> in the
     * <code>strictGraphListenerList</code>
     * 
     * @param event
     *            the GraphEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postEdgeAdded(GraphEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (GraphListener l : strictGraphListenerList) {
                l.postEdgeAdded(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }

            if (event.getNode() != null) {
                changedObjects.add(event.getNode());
            }

            if (event.getSecondNode() != null) {
                changedObjects.add(event.getSecondNode());
            }
        }

        for (GraphListener l : nonstrictGraphListenerList) {
            l.postEdgeAdded(event);
        }
    }

    /**
     * Called after an edge has been removed from the graph. Calls the same
     * method in all <code>GraphListeners</code> in the
     * <code>strictGraphListenerList</code>
     * 
     * @param event
     *            the GraphEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postEdgeRemoved(GraphEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (GraphListener l : strictGraphListenerList) {
                l.postEdgeRemoved(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }

            if (event.getNode() != null) {
                changedObjects.add(event.getNode());
            }

            if (event.getSecondNode() != null) {
                changedObjects.add(event.getSecondNode());
            }
        }

        for (GraphListener l : nonstrictGraphListenerList) {
            l.postEdgeRemoved(event);
        }
    }

    /**
     * Called after the edge has been reversed. Calls the same method in all
     * <code>EdgeListeners</code> in the <code>strictEdgeListenerList</code>
     * 
     * @param event
     *            the EdgeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postEdgeReversed(EdgeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (EdgeListener l : strictEdgeListenerList) {
                l.postEdgeReversed(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());
        }

        for (EdgeListener l : nonstrictEdgeListenerList) {
            l.postEdgeReversed(event);
        }
    }

    /**
     * Called after method <code>clear()</code> has been called on a graph.
     * Calls the same method in all <code>GraphListeners</code> in the
     * <code>strictGraphListenerList</code>
     * 
     * @param event
     *            the GraphEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postGraphCleared(GraphEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (GraphListener l : strictGraphListenerList) {
                l.postGraphCleared(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }

            if (event.getNode() != null) {
                changedObjects.add(event.getNode());
            }

            if (event.getSecondNode() != null) {
                changedObjects.add(event.getSecondNode());
            }
        }

        for (GraphListener l : nonstrictGraphListenerList) {
            l.postGraphCleared(event);
        }
    }

    /**
     * Called just after an incoming edge has been added to the node. (For
     * undirected edges postUndirectedEdgeAdded is called instead.) Calls the
     * same method in all NodeListeners in the strictNodeListenerList
     * 
     * @param event
     *            the NodeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postInEdgeAdded(NodeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (NodeListener l : strictNodeListenerList) {
                l.postInEdgeAdded(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }
        }

        for (NodeListener l : nonstrictNodeListenerList) {
            l.postInEdgeAdded(event);
        }
    }

    /**
     * Called after an incoming edge has been removed from the node. (For
     * undirected edges postUndirectedEdgeRemoved is called.) Calls the same
     * method in all NodeListeners in the strictNodeListenerList
     * 
     * @param event
     *            the NodeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postInEdgeRemoved(NodeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (NodeListener l : strictNodeListenerList) {
                l.postInEdgeRemoved(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }
        }

        for (NodeListener l : nonstrictNodeListenerList) {
            l.postInEdgeRemoved(event);
        }
    }

    /**
     * Called after an edge has been added to the graph. Calls the same method
     * in all <code>GraphListeners</code> in the
     * <code>strictGraphListenerList</code>
     * 
     * @param event
     *            the GraphEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postNodeAdded(GraphEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (GraphListener l : strictGraphListenerList) {
                l.postNodeAdded(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }

            if (event.getNode() != null) {
                changedObjects.add(event.getNode());
            }

            if (event.getSecondNode() != null) {
                changedObjects.add(event.getSecondNode());
            }
        }

        for (GraphListener l : nonstrictGraphListenerList) {
            l.postNodeAdded(event);
        }
    }

    /**
     * Called after a node has been removed from the graph. Calls the same
     * method in all <code>GraphListeners</code> in the
     * <code>strictGraphListenerList</code>
     * 
     * @param event
     *            the GraphEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postNodeRemoved(GraphEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (GraphListener l : strictGraphListenerList) {
                l.postNodeRemoved(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }

            if (event.getNode() != null) {
                changedObjects.add(event.getNode());
            }

            if (event.getSecondNode() != null) {
                changedObjects.add(event.getSecondNode());
            }
        }

        for (GraphListener l : nonstrictGraphListenerList) {
            l.postNodeRemoved(event);
        }
    }

    /**
     * Called after an outgoing edge has been added to the node. (For undirected
     * edges postUndirectedEdgeAdded is called instead.) Calls the same method
     * in all NodeListeners in the strictNodeListenerList
     * 
     * @param event
     *            the NodeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postOutEdgeAdded(NodeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (NodeListener l : strictNodeListenerList) {
                l.postOutEdgeAdded(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }
        }

        for (NodeListener l : nonstrictNodeListenerList) {
            l.postOutEdgeAdded(event);
        }
    }

    /**
     * Called after an outgoing edge has been removed from the node. (For
     * undirected edges postUndirectedEdgeRemoved is called.) Calls the same
     * method in all NodeListeners in the strictNodeListenerList
     * 
     * @param event
     *            the NodeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postOutEdgeRemoved(NodeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (NodeListener l : strictNodeListenerList) {
                l.postOutEdgeRemoved(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }
        }

        for (NodeListener l : nonstrictNodeListenerList) {
            l.postOutEdgeRemoved(event);
        }
    }

    /**
     * Called after the source node of an edge has changed. Calls the same
     * method in all <code>EdgeListeners</code> in the
     * <code>strictEdgeListenerList</code>
     * 
     * @param event
     *            the EdgeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postSourceNodeChanged(EdgeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (EdgeListener l : strictEdgeListenerList) {
                l.postSourceNodeChanged(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());
        }

        for (EdgeListener l : nonstrictEdgeListenerList) {
            l.postSourceNodeChanged(event);
        }
    }

    /**
     * Called after the target node of an edge has changed. Calls the same
     * method in all <code>EdgeListeners</code> in the
     * <code>strictEdgeListenerList</code>
     * 
     * @param event
     *            the EdgeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postTargetNodeChanged(EdgeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (EdgeListener l : strictEdgeListenerList) {
                l.postTargetNodeChanged(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());
        }

        for (EdgeListener l : nonstrictEdgeListenerList) {
            l.postTargetNodeChanged(event);
        }
    }

    /**
     * Called after an (undirected) edge has been added to the node. (For
     * directed edges pre- In/Out- EdgeAdded is called.) Calls the same method
     * in all NodeListeners in the strictNodeListenerList
     * 
     * @param event
     *            the NodeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postUndirectedEdgeAdded(NodeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (NodeListener l : strictNodeListenerList) {
                l.postUndirectedEdgeAdded(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }
        }

        for (NodeListener l : nonstrictNodeListenerList) {
            l.postUndirectedEdgeAdded(event);
        }
    }

    /**
     * Called after an (undirected) edge has been removed from the node. (For
     * directed edges pre- In/Out- EdgeRemoved is called.) Calls the same method
     * in all NodeListeners in the strictNodeListenerList
     * 
     * @param event
     *            the NodeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void postUndirectedEdgeRemoved(NodeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (NodeListener l : strictNodeListenerList) {
                l.postUndirectedEdgeRemoved(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }
        }

        for (NodeListener l : nonstrictNodeListenerList) {
            l.postUndirectedEdgeRemoved(event);
        }
    }

    /**
     * Called just before an attribute is added. Calls the same method in all
     * <code>AttributeListeners</code> in the
     * <code>strictAttributeListenerList</code>
     * 
     * @param event
     *            the AttributeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preAttributeAdded(AttributeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (AttributeListener l : strictAttributeListenerList) {
                l.preAttributeAdded(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getPath() != null) {
                changedObjects.add(event.getPath());
            }
        }

        for (AttributeListener l : nonstrictAttributeListenerList) {
            l.preAttributeAdded(event);
        }
    }

    /**
     * Called before a change of an attribute takes place. Calls the same method
     * in all <code>AttributeListeners</code> in the
     * <code>strictAttributeListenerList</code>
     * 
     * @param event
     *            the AttributeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preAttributeChanged(AttributeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (AttributeListener l : strictAttributeListenerList) {
                l.preAttributeChanged(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getPath() != null) {
                changedObjects.add(event.getPath());
            }
        }

        for (AttributeListener l : nonstrictAttributeListenerList) {
            l.preAttributeChanged(event);
        }
    }

    /**
     * Called just before an attribute is removed. Calls the same method in all
     * <code>AttributeListeners</code> in the
     * <code>strictAttributeListenerList</code>
     * 
     * @param event
     *            the AttributeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preAttributeRemoved(AttributeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (AttributeListener l : strictAttributeListenerList) {
                l.preAttributeRemoved(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getPath() != null) {
                changedObjects.add(event.getPath());
            }
        }

        for (AttributeListener l : nonstrictAttributeListenerList) {
            l.preAttributeRemoved(event);
        }
    }

    /**
     * Called before the edge is set directed or undirected. Calls the same
     * method in all <code>EdgeListeners</code> in the
     * <code>strictEdgeListenerList</code>
     * 
     * @param event
     *            the EdgeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preDirectedChanged(EdgeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (EdgeListener l : strictEdgeListenerList) {
                l.preDirectedChanged(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());
        }

        for (EdgeListener l : nonstrictEdgeListenerList) {
            l.preDirectedChanged(event);
        }
    }

    /**
     * Called just before an edge is added to the graph. Calls the same method
     * in all <code>GraphListeners</code> in the
     * <code>strictGraphListenerList</code>
     * 
     * @param event
     *            the GraphEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preEdgeAdded(GraphEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (GraphListener l : strictGraphListenerList) {
                l.preEdgeAdded(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }

            if (event.getNode() != null) {
                changedObjects.add(event.getNode());
            }

            if (event.getSecondNode() != null) {
                changedObjects.add(event.getSecondNode());
            }
        }

        for (GraphListener l : nonstrictGraphListenerList) {
            l.preEdgeAdded(event);
        }
    }

    /**
     * Called just before an edge is removed from the graph. Calls the same
     * method in all <code>GraphListeners</code> in the
     * <code>strictGraphListenerList</code>
     * 
     * @param event
     *            the GraphEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preEdgeRemoved(GraphEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (GraphListener l : strictGraphListenerList) {
                l.preEdgeRemoved(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }

            if (event.getNode() != null) {
                changedObjects.add(event.getNode());
            }

            if (event.getSecondNode() != null) {
                changedObjects.add(event.getSecondNode());
            }
        }

        for (GraphListener l : nonstrictGraphListenerList) {
            l.preEdgeRemoved(event);
        }
    }

    /**
     * Called before the edge is going to be reversed. Calls the same method in
     * all <code>EdgeListeners</code> in the <code>strictEdgeListenerList</code>
     * 
     * @param event
     *            the EdgeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preEdgeReversed(EdgeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (EdgeListener l : strictEdgeListenerList) {
                l.preEdgeReversed(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());
        }

        for (EdgeListener l : nonstrictEdgeListenerList) {
            l.preEdgeReversed(event);
        }
    }

    /**
     * Called before method <code>clear()</code> is called on a graph. Calls the
     * same method in all <code>GraphListeners</code> in the
     * <code>strictGraphListenerList</code>
     * 
     * @param event
     *            the GraphEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preGraphCleared(GraphEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (GraphListener l : strictGraphListenerList) {
                l.preGraphCleared(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }

            if (event.getNode() != null) {
                changedObjects.add(event.getNode());
            }

            if (event.getSecondNode() != null) {
                changedObjects.add(event.getSecondNode());
            }
        }

        for (GraphListener l : nonstrictGraphListenerList) {
            l.preGraphCleared(event);
        }
    }

    /**
     * Called just before an incoming edge is added to the node. (For undirected
     * edges preUndirectedEdgeAdded is called instead.) Calls the same method in
     * all NodeListeners in the strictNodeListenerList
     * 
     * @param event
     *            the NodeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preInEdgeAdded(NodeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (NodeListener l : strictNodeListenerList) {
                l.preInEdgeAdded(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }
        }

        for (NodeListener l : nonstrictNodeListenerList) {
            l.preInEdgeAdded(event);
        }
    }

    /**
     * Called just before an incoming edge is removed from the node. (For
     * undirected edges preUndirectedEdgeRemoved is called.) Calls the same
     * method in all NodeListeners in the strictNodeListenerList
     * 
     * @param event
     *            the NodeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preInEdgeRemoved(NodeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (NodeListener l : strictNodeListenerList) {
                l.preInEdgeRemoved(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }
        }

        for (NodeListener l : nonstrictNodeListenerList) {
            l.preInEdgeRemoved(event);
        }
    }

    /**
     * Called just before a node is added to the graph. Calls the same method in
     * all <code>GraphListeners</code> in the
     * <code>strictGraphListenerList</code>
     * 
     * @param event
     *            the GraphEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preNodeAdded(GraphEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (GraphListener l : strictGraphListenerList) {
                l.preNodeAdded(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }

            if (event.getNode() != null) {
                changedObjects.add(event.getNode());
            }

            if (event.getSecondNode() != null) {
                changedObjects.add(event.getSecondNode());
            }
        }

        for (GraphListener l : nonstrictGraphListenerList) {
            l.preNodeAdded(event);
        }
    }

    /**
     * Called just before a node is removed from the graph. Calls the same
     * method in all <code>GraphListeners</code> in the
     * <code>strictGraphListenerList</code>
     * 
     * @param event
     *            the GraphEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preNodeRemoved(GraphEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (GraphListener l : strictGraphListenerList) {
                l.preNodeRemoved(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }

            if (event.getNode() != null) {
                changedObjects.add(event.getNode());
            }

            if (event.getSecondNode() != null) {
                changedObjects.add(event.getSecondNode());
            }
        }

        for (GraphListener l : nonstrictGraphListenerList) {
            l.preNodeRemoved(event);
        }
    }

    /**
     * Called just before an outgoing edge is added to the node. (For undirected
     * edges preUndirectedEdgeAdded is called instead.) Calls the same method in
     * all NodeListeners in the nodeListenerList
     * 
     * @param event
     *            the NodeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preOutEdgeAdded(NodeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (NodeListener l : strictNodeListenerList) {
                l.preOutEdgeAdded(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }
        }

        for (NodeListener l : nonstrictNodeListenerList) {
            l.preOutEdgeAdded(event);
        }
    }

    /**
     * Called just before an outgoing edge is removed from the node. (For
     * undirected edges preUndirectedEdgeRemoved is called.) Calls the same
     * method in all NodeListeners in the strictNodeListenerList
     * 
     * @param event
     *            the NodeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preOutEdgeRemoved(NodeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (NodeListener l : strictNodeListenerList) {
                l.preOutEdgeRemoved(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }
        }

        for (NodeListener l : nonstrictNodeListenerList) {
            l.preOutEdgeRemoved(event);
        }
    }

    /**
     * Called before a change of the source node of an edge takes place. Calls
     * the same method in all <code>EdgeListeners</code> in the
     * <code>strictEdgeListenerList</code>
     * 
     * @param event
     *            the EdgeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preSourceNodeChanged(EdgeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (EdgeListener l : strictEdgeListenerList) {
                l.preSourceNodeChanged(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());
        }

        for (EdgeListener l : nonstrictEdgeListenerList) {
            l.preSourceNodeChanged(event);
        }

    }

    /**
     * Called before a change of the target node of an edge takes place. Calls
     * the same method in all <code>EdgeListeners</code> in the
     * <code>strictEdgeListenerList</code>
     * 
     * @param event
     *            the EdgeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preTargetNodeChanged(EdgeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (EdgeListener l : strictEdgeListenerList) {
                l.preTargetNodeChanged(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());
        }

        for (EdgeListener l : nonstrictEdgeListenerList) {
            l.preTargetNodeChanged(event);
        }
    }

    /**
     * Called just before an (undirected) edge is added to the node. (For
     * directed edges pre- In/Out- EdgeAdded is called.) Calls the same method
     * in all NodeListeners in the strictNodeListenerList
     * 
     * @param event
     *            the NodeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preUndirectedEdgeAdded(NodeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (NodeListener l : strictNodeListenerList) {
                l.preUndirectedEdgeAdded(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }
        }

        for (NodeListener l : nonstrictNodeListenerList) {
            l.preUndirectedEdgeAdded(event);
        }
    }

    /**
     * Called just before an (undirected) edge is removed from the node. (For
     * directed edges pre- In/Out- EdgeRemoved is called.) Calls the same method
     * in all NodeListeners in the strictNodeListenerList
     * 
     * @param event
     *            the NodeEvent detailing the changes.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void preUndirectedEdgeRemoved(NodeEvent event) {
        if (event == null)
            throw new IllegalArgumentException("The argument "
                    + "may not be null");

        if (transactionsActive == 0) {
            for (NodeListener l : strictNodeListenerList) {
                l.preUndirectedEdgeRemoved(event);
            }
        } else {
            // log objects that are (probably) affected
            changedObjects.add(event.getSource());

            if (event.getEdge() != null) {
                changedObjects.add(event.getEdge());
            }
        }

        for (NodeListener l : nonstrictNodeListenerList) {
            l.preUndirectedEdgeRemoved(event);
        }
    }

    /**
     * Unregisters AttributeListener l by removing it from the list of
     * AttributeListeners.
     * 
     * @param l
     *            the AttributeListener that is unregistered.
     * 
     * @exception ListenerNotFoundException
     *                if the listener to delete cannot be found in the listener
     *                list.
     */
    public void removeAttributeListener(AttributeListener l)
            throws ListenerNotFoundException {
        if (strictAttributeListenerList.remove(l)
                || nonstrictAttributeListenerList.remove(l)) {
            removeFromTransactionListeners(l);
        } else
            throw new ListenerNotFoundException("The attr. listener you want "
                    + "to remove cannot be found.");
    }

    /**
     * Unregisters EdgeListener l by removing it from the list of EdgeListeners.
     * 
     * @param l
     *            the EdgeListener that is unregistered.
     * 
     * @exception ListenerNotFoundException
     *                if the listener to delete cannot be found in the listener
     *                list.
     */
    public void removeEdgeListener(EdgeListener l)
            throws ListenerNotFoundException {
        if (strictEdgeListenerList.remove(l)
                || nonstrictEdgeListenerList.remove(l)) {
            removeFromTransactionListeners(l);
        } else
            throw new ListenerNotFoundException("The edge listener you want "
                    + "to remove cannot be found.");
    }

    /**
     * Unregisters GraphListener l by removing it from the list of nonstrict and
     * strict transaction GraphListeners.
     * 
     * @param l
     *            the GraphListener that is unregistered.
     * 
     * @exception ListenerNotFoundException
     *                if the listener to delete cannot be found in the listener
     *                list.
     */
    public void removeGraphListener(GraphListener l)
            throws ListenerNotFoundException {
        if (strictGraphListenerList.remove(l)
                || nonstrictGraphListenerList.remove(l)) {
            removeFromTransactionListeners(l);
        } else
            throw new ListenerNotFoundException("The graph listener you want "
                    + "to remove cannot be found.");
    }

    /**
     * Unregisters <code>NodeListener</code> l by removing it from the list of
     * NodeListeners
     * 
     * @param l
     *            the NodeListener that is unregistered.
     * 
     * @exception ListenerNotFoundException
     *                if the listener to delete cannot be found in the listener
     *                list.
     */
    public void removeNodeListener(NodeListener l)
            throws ListenerNotFoundException {
        if (strictNodeListenerList.remove(l)
                || nonstrictNodeListenerList.remove(l)) {
            removeFromTransactionListeners(l);
        } else
            throw new ListenerNotFoundException("The node listener you want "
                    + "to remove cannot be found.");
    }

    /**
     * Called when a transaction has finished. Changes the event it gets by
     * reusing the <code>source</code> but adding the <code>Set</code> of
     * (probably) changed objects.
     * 
     * @param source
     *            the object, which initiated the end of the transaction.
     */
    public void transactionFinished(Object source) {
        this.transactionsActive--;
        assert this.transactionsActive >= 0;

        TransactionEvent event = new TransactionEvent(source, changedObjects);

        for (TransactionListener l : transactionListeners.keySet()) {
            l.transactionFinished(event);
        }

        // only clear list when no transactions are active
        if (transactionsActive == 0) {
            this.changedObjects = new HashSet<Object>();
        }
    }

    /**
     * Called when a transaction has started.
     * 
     * @param source
     *            the object, which initiated the transaction.
     */
    public void transactionStarted(Object source) {
        TransactionEvent event = new TransactionEvent(source);

        this.transactionsActive++;

        for (TransactionListener l : transactionListeners.keySet()) {
            l.transactionStarted(event);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
