// =============================================================================
//
//   ElementResolver.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.util.Callback;

/**
 * Subclasses of {@code GraphElementFinder} provide methods for detecting
 * {@code GraphElement}s that lay on the top at a specific position or intersect
 * a specific rectangle. Instances of {@code GraphElementFinder} are usually
 * handed out by <code>InteractiveView</code>s on call to
 * {@link InteractiveView#getGraphElementFinder()}. On some {@code
 * InteractiveView}s, obtaining a {@link GraphElement} by its position is most
 * efficiently done during the drawing process. When called via the {@code
 * deferred...} methods, {@code GraphElementFinder} is free to decide whether
 * the search and the result callback should be performed immediately (before
 * the method returns) or at a more convenient point in time. {@code get...}
 * methods always perform searches and return results immediately.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class GraphElementFinder {
    /**
     * Returns the topmost {@code GraphElement} lying near {@code position}.
     * 
     * @param position
     *            the logical coordinate where graph elements are searched at.
     * @param edgeTolerance
     *            determines how far the edges may be apart from position to be
     *            considered. Nodes must be exactly at the position to be
     *            returned.
     * @return the topmost {@code GraphElement} lying near {@code position}.
     */
    public GraphElement getElementAt(Point2D position, double edgeTolerance) {
        Node node = getNodeAt(position);
        if (node != null)
            return node;
        else
            return getEdgeAt(position, edgeTolerance);
    }

    /**
     * Returns the topmost {@code Node} lying at {@code position}.
     * 
     * @param position
     *            the logical coordinate where nodes are searched at.
     * @return the topmost {@code Node} lying at {@code position}.
     */
    public abstract Node getNodeAt(Point2D position);

    /**
     * Returns the topmost {@code Edge} lying near {@code position}.
     * 
     * @param position
     *            the logical coordinate where edges are searched at.
     * @param tolerance
     *            determines how far the edges may be apart from position to be
     *            considered.
     * @return the topmost {@code Edge} lying near {@code position}.
     */
    public abstract Edge getEdgeAt(Point2D position, double tolerance);

    /**
     * Returns all {@code GraphElement}s intersecting {@code rectangle}. The
     * default implementation creates a new {@code HashSet} and fills it by
     * calls to {@link #addIntersectingNodes(Rectangle2D, Set)} and
     * {@link #addIntersectingEdges(Rectangle2D, Set)}.
     * 
     * @param rectangle
     *            the rectangle that intersects the {@link GraphElement}s to be
     *            returned.
     * @return all {@code GraphElement}s intersecting {@code rectangle}.
     */
    public Set<GraphElement> getIntersectingElements(Rectangle2D rectangle) {
        Set<GraphElement> set = new HashSet<GraphElement>();
        addIntersectingNodes(rectangle, set);
        addIntersectingEdges(rectangle, set);
        return set;
    }

    /**
     * Returns all {@code Node}s intersecting {@code rectangle}. The default
     * implementation creates a new {@code HashSet} and fills it by a call to
     * {@link #addIntersectingNodes(Rectangle2D, Set)}.
     * 
     * @param rectangle
     *            the rectangle that intersects the {@link Node}s to be
     *            returned.
     * @return all {@code Node}s intersecting {@code rectangle}.
     */
    public Set<Node> getIntersectingNodes(Rectangle2D rectangle) {
        Set<Node> set = new HashSet<Node>();
        addIntersectingNodes(rectangle, set);
        return set;
    }

    /**
     * Returns all {@code Edge}s intersecting {@code rectangle}. The default
     * implementation creates a new {@code HashSet} and fills it by a call to
     * {@link #addIntersectingEdges(Rectangle2D, Set)}.
     * 
     * @param rectangle
     *            the rectangle that intersects the {@link Edge}s to be
     *            returned.
     * @return all {@code Edge}s intersecting {@code rectangle}.
     */
    public Set<Edge> getIntersectingEdges(Rectangle2D rectangle) {
        Set<Edge> set = new HashSet<Edge>();
        addIntersectingEdges(rectangle, set);
        return set;
    }

    /**
     * Adds all {@code Node}s intersecting {@code rectangle} to {@code set}.
     * 
     * @param rectangle
     *            the rectangle which intersects the nodes to be added to
     *            {@code set}.
     * @param set
     *            the set the intersecting nodes are added to.
     */
    protected abstract void addIntersectingNodes(Rectangle2D rectangle,
            Set<? super Node> set);

    /**
     * Adds all {@code Edge}s intersecting {@code rectangle} to {@code set}.
     * 
     * @param rectangle
     *            the rectangle which intersects the edges to be added to
     *            {@code set}.
     * @param set
     *            the set the intersecting edges are added to.
     */
    protected abstract void addIntersectingEdges(Rectangle2D rectangle,
            Set<? super Edge> set);

    /**
     * Decides if {@code position} is near the shape border of {@code node}.
     * Efficiency hint: Implementors can assume that {@code position} lies
     * within the boundary box of the node shape.
     * 
     * @param position
     *            the tested position in logical coordinates.
     * @param node
     *            the node.
     * @param tolerance
     *            determines how far {@code position} may be apart from shape
     *            border for positive result.
     * @return if {@code position} is near the shape border of {@code node}.
     */
    public abstract boolean isOnShapeBorder(Point2D position, Node node,
            double tolerance);

    /**
     * Returns which bend of {@code edge} lies near {@code position}.
     * 
     * @param position
     *            the position in logical coordinates the bend is searched at.
     * @param edge
     *            the edge whose bends are considered.
     * @param tolerance
     *            determines how far a bend may be apart from {@code position}
     *            to be returned.
     * @return the bend which lies near {@code position} or {@code ""} if no
     *         bend was found.
     * @see EdgeGraphicAttribute#getBends()
     */
    public abstract String getBend(Point2D position, Edge edge, double tolerance);

    /**
     * The deferred variant of {@link #getElementAt(Point2D, double)}. The view
     * decides for the most suitable moment to communicate the result. The
     * default implementation calls back before the method returns.
     * 
     * @param position
     *            the position to search at.
     * @param edgeTolerance
     *            the tolerance.
     * @param callback
     *            is communicated the result.
     */
    public void deferredTellElementAt(Point2D position, double edgeTolerance,
            Callback<?, ? super GraphElement> callback) {
        // Default implementation: Tell immediately.
        callback.call(getElementAt(position, edgeTolerance));
    }

    /**
     * The deferred variant of {@link #getNodeAt(Point2D)}. The view decides for
     * the most suitable moment to communicate the result. The default
     * implementation calls back before the method returns.
     * 
     * @param position
     *            the position to search at.
     * @param callback
     *            is communicated the result.
     */
    public void deferredTellNodeAt(Point2D position,
            Callback<?, ? super Node> callback) {
        // Default implementation: Tell immediately.
        callback.call(getNodeAt(position));
    }

    /**
     * The deferred variant of {@link #getEdgeAt(Point2D, double)}. The view
     * decides for the most suitable moment to communicate the result. The
     * default implementation calls back before the method returns.
     * 
     * @param position
     *            the position to search at.
     * @param callback
     *            is communicated the result.
     */
    public void deferredEdgeNodeAt(Point2D position, double tolerance,
            Callback<?, ? super Edge> callback) {
        // Default implementation: Tell immediately.
        callback.call(getEdgeAt(position, tolerance));
    }

    /**
     * The deferred variant of {@link #getIntersectingElements(Rectangle2D)}.
     * The view decides for the most suitable moment to communicate the result.
     * The default implementation calls back before the method returns.
     * 
     * @param rectangle
     *            the rectangle to search at.
     * @param callback
     *            is communicated the result.
     */
    public void deferredTellIntersectingElements(Rectangle2D rectangle,
            Callback<?, ? super Set<GraphElement>> callback) {
        // Default implementation: Tell immediately.
        callback.call(getIntersectingElements(rectangle));
    }

    /**
     * The deferred variant of {@link #getIntersectingNodes(Rectangle2D)}. The
     * view decides for the most suitable moment to communicate the result. The
     * default implementation calls back before the method returns.
     * 
     * @param rectangle
     *            the rectangle to search at.
     * @param callback
     *            is communicated the result.
     */
    public void deferredTellIntersectingNodes(Rectangle2D rectangle,
            Callback<?, ? super Set<Node>> callback) {
        // Default implementation: Tell immediately.
        callback.call(getIntersectingNodes(rectangle));
    }

    /**
     * The deferred variant of {@link #getIntersectingEdges(Rectangle2D)}. The
     * view decides for the most suitable moment to communicate the result. The
     * default implementation calls back before the method returns.
     * 
     * @param rectangle
     *            the rectangle to search at.
     * @param callback
     *            is communicated the result.
     */
    public void deferredTellIntersectingEdges(Rectangle2D rectangle,
            Callback<?, ? super Set<Edge>> callback) {
        // Default implementation: Tell immediately.
        callback.call(getIntersectingEdges(rectangle));
    }

    /**
     * The deferred variant of {@link #isOnShapeBorder(Point2D, Node, double)}.
     * The view decides for the most suitable moment to communicate the result.
     * The default implementation calls back before the method returns.
     * 
     * @param position
     *            the position to search at.
     * @param node
     *            the node.
     * @param tolerance
     *            the tolerance.
     * @param callback
     *            is communicated the result.
     */
    public void deferredIsOnShapeBorder(Point2D position, Node node,
            double tolerance, Callback<?, Boolean> callback) {
        callback.call(isOnShapeBorder(position, node, tolerance));
    }

    /**
     * The deferred variant of {@link #getBend(Point2D, Edge, double)}. The view
     * decides for the most suitable moment to communicate the result. The
     * default implementation calls back before the method returns.
     * 
     * @param position
     *            the position.
     * @param edge
     *            the edge.
     * @param tolerance
     *            the tolerance.
     * @param callback
     *            is communicated the result.
     */
    public void deferredGetBend(Point2D position, Edge edge, double tolerance,
            Callback<?, String> callback) {
        callback.call(getBend(position, edge, tolerance));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
