// =============================================================================
//
//   ViewManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ViewManager.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.managers;

import org.graffiti.managers.pluginmgr.PluginManagerListener;
import org.graffiti.plugin.view.View;
import org.graffiti.plugin.view.ViewListener;
import org.graffiti.util.InstanceCreationException;

/**
 * Provides an interface for managing a list of view types.
 * 
 * @version $Revision: 5768 $
 */
public interface ViewManager extends PluginManagerListener, ViewListener {

    /**
     * Returns the class names of the registered views.
     * 
     * @return the class names of the registered views.
     */
    public String[] getViewNames();

    /**
     * Adds the given view type (class name) to the map of view types.
     * 
     * @param className
     *            the new view to add to the map.
     */
    public void addView(String className);

    /**
     * Adds the given list of views types (class names) to the list of available
     * view types.
     * 
     * @param classNames
     *            the list of classNames to add to the view type list.
     */
    public void addViews(String[] classNames);

    /**
     * Returns a new instance of the specified view.
     * 
     * @param className
     *            the class name of the view.
     * 
     * @return a new instance of the specified view.
     */
    public View createView(String className) throws InstanceCreationException;

    /**
     * Returns <code>true</code>, if there is at least one registered view
     * plugin available. /
     * 
     * @return DOCUMENT ME!
     */
    public boolean hasViews();

    /**
     * Registers the given <code>ViewManagerListener</code>.
     * 
     * @param viewManagerListener
     *            the listener to register.
     */
    void addListener(ViewManagerListener viewManagerListener);

    /**
     * Registers the given <code>ViewListener</code>.
     * 
     * @param viewListener
     *            the listener to register.
     */
    void addViewListener(ViewListener viewListener);

    /**
     * Returns <code>true</code>, if the given view manager listener was in the
     * list of listeners and could be removed.
     * 
     * @param l
     *            the view manager listener to remove.
     * 
     * @return DOCUMENT ME!
     */
    boolean removeListener(ViewManagerListener l);

    /**
     * Returns <code>true</code>, if the given view listener was in the list of
     * listeners and could be removed.
     * 
     * @param l
     *            the view manager listener to remove.
     * 
     * @return DOCUMENT ME!
     */
    boolean removeViewListener(ViewListener l);

    /**
     * Interfaces a view manager listener.
     * 
     * @version $Revision: 5768 $
     */
    public interface ViewManagerListener {
        /**
         * Called, if a new view type is added to view manager.
         * 
         * @param viewType
         *            the type of the new view.
         */
        void viewTypeAdded(String viewType);
    }

    /**
     * Removes the known list of views from the internal memory. Makes it
     * possible to remove and re-add views.
     */
    public void removeViews();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
