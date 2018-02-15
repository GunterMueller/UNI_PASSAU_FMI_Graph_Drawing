// =============================================================================
//
//   Session.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Session.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.session;

import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.plugin.algorithm.Algorithm;
import org.graffiti.plugin.algorithm.animation.Animation;
import org.graffiti.plugin.view.View;
import org.graffiti.util.MutuallyReferableObject;

/**
 * Contains a session. A session consists of a
 * <code>org.graffiti.graph.Graph</code> and a list of corresponding
 * <code>org.graffiti.plugin.view.View</code>s. Every <code>Session</code>
 * contains a <code>GraphConstraintChecker</code> which checks the constraints
 * defined by the current mode.
 * 
 * @see org.graffiti.graph.Graph
 * @see org.graffiti.plugin.view.View
 */
public class Session extends MutuallyReferableObject implements
        ConstraintCheckerListener {

    /** The graph object of this session. */
    protected Graph graph;

    protected List<Animation> animations = new java.util.ArrayList<Animation>(2);

    private boolean startedNewAnimation = false;

    /** The constraint checker of the graph. */
    protected GraphConstraintChecker constraintChecker;

    /** The list of views of this session. */
    protected List<View> views;

    /** The active view in this session. */
    protected View activeView;

    /** Unique id to assign to next session. */
    private static int nextSessionId = 1;

    /** Unique id to assign to next view of this session. */
    private int nextViewId = 1;

    /** Unique id for this session. */
    protected int id;

    /**
     * Constructs a new session instance with an empty graph and the
     * corresponding constraint checker.
     */
    public Session() {
        this(new AdjListGraph());
    }

    /**
     * Constructs a new session instance with the given graph.
     * 
     * @param graph
     *            the graph to be used for this session.
     */
    public Session(Graph graph) {
        this.graph = graph;
        this.id = getNextSessionId();
        this.views = new LinkedList<View>();
        this.constraintChecker = new GraphConstraintChecker(graph, this);
    }

    /**
     * Return the next unique session id to use.
     * 
     * @return Next session id.
     */
    protected synchronized int getNextSessionId() {
        return nextSessionId++;
    }

    /**
     * Return the next unique view id to use for this session.
     * 
     * @return Next view id.
     */
    public synchronized int getNextViewId() {
        return nextViewId++;
    }

    /**
     * Get unique id for this session.
     * 
     * @return Unique id.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the activeView.
     * 
     * @param activeView
     *            The activeView to set
     */
    public void setActiveView(View activeView) {
        this.activeView = activeView;
    }

    /**
     * Returns the activeView.
     * 
     * @return View
     */
    public View getActiveView() {
        return activeView;
    }

    /**
     * Returns the class name of the specified algorithm. Using the
     * <code>InstanceLoader</code> an instance of this <code>Algorithm</code>
     * can be created.
     * 
     * @param algorithm
     *            the <code>Algorithm</code> of which to get the class name.
     * 
     * @return the class name of the specified algorithm.
     * 
     * @throws RuntimeException
     *             DOCUMENT ME!
     */
    public String getClassName(Algorithm algorithm) {
        throw new RuntimeException("Implement me");
    }

    /**
     * Returns the graph of this session.
     * 
     * @return the graph of this session.
     */
    public Graph getGraph() {
        return this.graph;
    }

    public void setActiveAlgorithm(Algorithm a) {
        if (a == null)
            throw new NullPointerException();
        if (a.supportsAnimation()) {
            animations.add(0, a.getAnimation());
            startedNewAnimation = true;
        }
    }

    /**
     * Returns <code>true</code>, if the graph in this session has been
     * modified.
     * 
     * @return True, if the graph has been modified. False, if not.
     */
    public boolean isModified() {
        return graph.isModified();
    }

    /**
     * Returns the list of views in the manager.
     * 
     * @return the list of views in the manager.
     */
    public List<View> getViews() {
        return views;
    }

    /**
     * Adds a new View to the inner list of views.
     * 
     * @param view
     *            a view to be added.
     */
    public void addView(View view) {
        views.add(view);
    }

    /**
     * Handles the failed constraint check.
     * 
     * @param msg
     *            tells details about the unsatisfied constraints.
     */
    public void checkFailed(String msg) {
    }

    /**
     * Closes this session. Closes all the views of this session.
     */
    public void close() {
        for (View v : views) {
            v.close();
        }
    }

    /**
     * Removes the given view from this session.
     * 
     * @param view
     *            the view to be removed from this session.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void removeView(View view) {
        if (view == null)
            throw new IllegalArgumentException(
                    "trying to remove a view, which is null.");

        views.remove(view);
    }

    /**
     * Checks whether the graph satisfies all the constraints.
     * 
     * @throws UnsatisfiedConstraintException
     *             if there es a constraint which is not satisfied.
     */
    public void validateConstraints() throws UnsatisfiedConstraintException {
        constraintChecker.checkConstraints();
    }

    public boolean hasActiveAnimation() {
        return !animations.isEmpty();
    }

    private Animation getFirstAnimation() {
        return animations.get(0);
    }

    public Animation getActiveAnimation() {
        if (animations.isEmpty())
            throw new IllegalStateException(
                    "This session does not have an active animation.");
        return getFirstAnimation();
    }

    public void discardActiveAnimation() {
        if (animations.isEmpty())
            throw new IllegalStateException();
        animations.remove(0);
    }

    public void approveStartOfNewAnimation() {
        if (!startedNewAnimation)
            throw new IllegalStateException();
        startedNewAnimation = false;
    }

    public boolean hasStartedNewAnimation() {
        return startedNewAnimation;
    }

    public boolean hasPassiveAnimation() {
        return animations.size() > 1;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
