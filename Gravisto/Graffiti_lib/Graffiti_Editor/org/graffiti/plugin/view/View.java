// =============================================================================
//
//   View.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: View.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.view;

import java.awt.dnd.Autoscroll;
import java.awt.geom.AffineTransform;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.graffiti.attributes.AttributeConsumer;
import org.graffiti.event.AttributeListener;
import org.graffiti.event.EdgeListener;
import org.graffiti.event.GraphListener;
import org.graffiti.event.NodeListener;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.managers.AttributeComponentManager;

/**
 * Represents a view of a plugin.
 * 
 * @version $Revision: 5768 $
 */
public interface View extends GraphListener, NodeListener, EdgeListener,
        AttributeListener, Autoscroll, AttributeConsumer, Zoomable {
    /** Standard zoom value. */
    public static final AffineTransform NO_ZOOM = new AffineTransform();

    /**
     * Sets the AttributeComponentManager used by this view.
     */
    public void setAttributeComponentManager(AttributeComponentManager acm);

    /**
     * Returns the map mapping <code>GraphElement</code>s with
     * <code>GraphElementComponent</code>s.
     * 
     * @return DOCUMENT ME!
     */
    public Map<GraphElement, GraphElementComponent> getComponentElementMap();

    /**
     * Returns the main <code>GraphElementComponent</code> associated with the
     * given <code>GraphElement</code>.
     * 
     * @param ge
     *            <code>GraphElement</code> for which the component is wanted.
     * 
     * @return the <code>GraphElementComponent</code> used to display the given
     *         <code>GraphELement</code>.
     */
    public GraphElementComponent getComponentForElement(GraphElement ge);

    /**
     * Sets the graph of the view to the specified value.
     * 
     * @param graph
     *            the new value of the graph.
     */
    public void setGraph(Graph graph);

    /**
     * Returns the main component of the view.
     * 
     * @return the main component of the view.
     */
    public JComponent getViewComponent();

    /**
     * Returns the viewName.
     * 
     * @return String
     */
    public String getViewName();

    // /**
    // * Returns the values for horizontal and vertical zoom encapsulated in a
    // * Point2D object. A value of 1.0 means no zoom is applied.
    // *
    // * @return Point2D see method description
    // */
    // public Point2D getZoom();

    /**
     * Adds a message listener to the view. If the view have been started
     * without editor instance, this method may be empty.
     * 
     * @param ml
     *            a message listener
     */
    public void addMessageListener(MessageListener ml);

    /**
     * Closes the current view.
     */
    public void close();

    /**
     * Instructs the view to do completely refresh its contents.
     */
    public void completeRedraw();

    /**
     * Removes a message listener from the view.If the view have been started
     * without editor instance, this method may be empty.
     * 
     * @param ml
     *            a message listener
     */
    public void removeMessageListener(MessageListener ml);

    /**
     * Repaints the given graph element
     * 
     * @param ge
     *            the <code>GraphElement</code> to repaint.
     */
    public void repaint(GraphElement ge);

    /**
     * Assign a unique, as far as the session is concerned, to this view.
     * 
     * @param id
     *            Unique id.
     */
    public void setId(int id);

    /**
     * Get unique id of this view.
     * 
     * @return Unique id.
     */
    public int getId();

    /**
     * Returns if this <code>View</code> shall be embedded in a
     * {@link JScrollPane}.
     * 
     * @return if this <code>View</code> shall be embedded in a
     *         <code>JScrollPane</code>.
     */
    public boolean embedsInJScrollPane();

    /**
     * Returns if this view supports {@link Grid}s.
     * 
     * @return true if this view supports {@code Grid}s, false else.
     * @see #getGrid()
     * @see #setGrid(Grid)
     */
    public boolean supportsGrid();

    /**
     * Returns the current {@code Grid} of this view.
     * 
     * @return the current {@code Grid} of this view.
     * @throws UnsupportedOperationException
     *             if this view does not support grids. If a view supports grids
     *             can be checked by a call to {@link #supportsGrid()}.
     * @see #setGrid(Grid)
     */
    public Grid getGrid();

    /**
     * Sets the current {@code Grid} for this view.
     * 
     * @param grid
     *            the grid to be set for this view.
     * @throws UnsupportedOperationException
     *             if this view does not support grids. If a view supports grids
     *             can be checked by a call to {@link #supportsGrid()}.
     * @see #getGrid()
     */
    public void setGrid(Grid grid);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
