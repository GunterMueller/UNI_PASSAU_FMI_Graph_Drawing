// =============================================================================
//
//   FastViewAdapter.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JPanel;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.event.AttributeEvent;
import org.graffiti.event.EdgeEvent;
import org.graffiti.event.GraphEvent;
import org.graffiti.event.NodeEvent;
import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.managers.AttributeComponentManager;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugin.view.MessageListener;
import org.graffiti.plugin.view.View2D;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.util.MutuallyReferable;
import org.graffiti.util.MutuallyReferableObject;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
abstract class ViewAdapter extends JPanel implements View2D,
        InteractiveView<FastView>, MutuallyReferable {
    /**
     * 
     */
    private static final long serialVersionUID = 7133041941398726585L;
    private LinkedList<MessageListener> messageListeners;
    private int id;

    private MutuallyReferableObject mutuallyReferableObject;

    protected ViewAdapter() {
        super(new BorderLayout());
        messageListeners = new LinkedList<MessageListener>();
    }

    /**
     * {@inheritDoc}
     */
    public void addMessageListener(MessageListener ml) {
        messageListeners.add(ml);
    }

    /**
     * {@inheritDoc}
     */
    public void removeMessageListener(MessageListener ml) {
        messageListeners.remove(ml);
    }

    /**
     * {@inheritDoc}
     */
    public void repaint(GraphElement ge) {
    }

    /**
     * {@inheritDoc}
     */
    public void completeRedraw() {
    }

    /**
     * {@inheritDoc}
     */
    public void setAttributeComponentManager(AttributeComponentManager acm) {
        // Ignore that.
    }

    /**
     * {@inheritDoc}
     */
    public void preEdgeAdded(GraphEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preEdgeRemoved(GraphEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preGraphCleared(GraphEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preNodeAdded(GraphEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preNodeRemoved(GraphEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void postInEdgeAdded(NodeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void postInEdgeRemoved(NodeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void postOutEdgeAdded(NodeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void postOutEdgeRemoved(NodeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void postUndirectedEdgeAdded(NodeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void postUndirectedEdgeRemoved(NodeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preInEdgeAdded(NodeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preInEdgeRemoved(NodeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preOutEdgeAdded(NodeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preOutEdgeRemoved(NodeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preUndirectedEdgeAdded(NodeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preUndirectedEdgeRemoved(NodeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void postDirectedChanged(EdgeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void postSourceNodeChanged(EdgeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void postTargetNodeChanged(EdgeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preDirectedChanged(EdgeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preEdgeReversed(EdgeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preAttributeAdded(AttributeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preAttributeChanged(AttributeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void preAttributeRemoved(AttributeEvent e) {
    }

    /**
     * {@inheritDoc}
     */
    public void autoscroll(Point arg0) {
    }

    /**
     * {@inheritDoc}
     */
    public Insets getAutoscrollInsets() {
        return new Insets(0, 0, 0, 0);
    }

    /**
     * {@inheritDoc}
     */
    public int getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * {@inheritDoc}
     */
    public Map<GraphElement, GraphElementComponent> getComponentElementMap() {
        throw new UnsupportedOperationException("Will not be implemented.");
    }

    /**
     * {@inheritDoc}
     */
    public GraphElementComponent getComponentForElement(GraphElement ge) {
        return new GraphElementComponentFacade(this, ge);
    }

    public Grid getGrid() {
        throw new UnsupportedOperationException();
    }

    public void setGrid(Grid grid) {
        throw new UnsupportedOperationException();
    }

    public boolean supportsGrid() {
        return false;
    }

    public CollectionAttribute getDirectedEdgeAttribute() {
        return new EdgeGraphicAttribute(true);
    }

    public CollectionAttribute getUndirectedEdgeAttribute() {
        return new EdgeGraphicAttribute(false);
    }

    public void addReference(Object o) {
        if (mutuallyReferableObject == null) {
            mutuallyReferableObject = new MutuallyReferableObject();
        }
        mutuallyReferableObject.addReference(o);
    }

    /*
     * @see org.graffiti.plugin.view.View2D#print(java.awt.Graphics2D, int, int)
     */
    public void print(Graphics2D g, int width, int height) {
        throw new UnsupportedOperationException("Not yet implemented.");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
