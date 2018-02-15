// =============================================================================
//
//   DefaultViewManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DefaultViewManager.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.managers;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.graffiti.managers.pluginmgr.PluginDescription;
import org.graffiti.plugin.GenericPlugin;
import org.graffiti.plugin.view.View;
import org.graffiti.plugin.view.ViewListener;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Manages a list of view types.
 * 
 * @version $Revision: 5779 $
 */
public class DefaultViewManager implements ViewManager {

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(DefaultViewManager.class.getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** Contains the list of listeners. */
    private List<ViewManagerListener> listeners;

    /** Contains the list of listeners. */
    private List<ViewListener> viewListeners;

    /** Contains the class names of the available views. */
    private List<String> views;

    /**
     * Constructs a new view manager.
     */
    public DefaultViewManager() {
        views = new LinkedList<String>();
        listeners = new LinkedList<ViewManagerListener>();
        viewListeners = new LinkedList<ViewListener>();
    }

    /*
     * @see org.graffiti.managers.ViewManager#getViewNames()
     */
    public String[] getViewNames() {
        Object[] names = views.toArray();
        String[] stringNames = new String[names.length];

        for (int i = 0; i < stringNames.length; i++) {
            stringNames[i] = (String) names[i];
        }

        return stringNames;
    }

    /*
     * @seeorg.graffiti.managers.ViewManager#addListener(org.graffiti.managers.
     * ViewManager.ViewManagerListener)
     */
    public void addListener(ViewManagerListener viewManagerListener) {
        listeners.add(viewManagerListener);
    }

    /*
     * @see org.graffiti.managers.ViewManager#addView(java.lang.String)
     */
    public void addView(String viewType) {
        views.add(viewType);
        logger.info("new view registered: " + viewType);

        fireViewTypeAdded(viewType);
    }

    /*
     * @see
     * org.graffiti.managers.ViewManager#addViewListener(org.graffiti.plugin
     * .view.ViewListener)
     */
    public void addViewListener(ViewListener viewListener) {
        viewListeners.add(viewListener);
    }

    /*
     * @see org.graffiti.managers.ViewManager#addViews(java.lang.String[])
     */
    public void addViews(String[] views) {
        for (String element : views) {
            addView(element);
        }
    }

    /*
     * @see org.graffiti.managers.ViewManager#createView(java.lang.String)
     */
    public View createView(String name) throws InstanceCreationException {
        if (views.contains(name)) {
            logger.info("creating view: " + name);

            return (View) InstanceLoader.createInstance(name);
        } else
            return null;
    }

    /*
     * @see org.graffiti.managers.ViewManager#hasViews()
     */
    public boolean hasViews() {
        return !views.isEmpty();
    }

    /*
     * @see
     * org.graffiti.managers.pluginmgr.PluginManagerListener#pluginAdded(org
     * .graffiti.plugin.GenericPlugin,
     * org.graffiti.managers.pluginmgr.PluginDescription)
     */
    public void pluginAdded(GenericPlugin plugin, PluginDescription desc) {
        addViews(plugin.getViews());
    }

    /*
     * @see
     * org.graffiti.managers.ViewManager#removeListener(org.graffiti.managers
     * .ViewManager.ViewManagerListener)
     */
    public boolean removeListener(ViewManagerListener l) {
        return listeners.remove(l);
    }

    /*
     * @see
     * org.graffiti.managers.ViewManager#removeViewListener(org.graffiti.plugin
     * .view.ViewListener)
     */
    public boolean removeViewListener(ViewListener l) {
        return viewListeners.remove(l);
    }

    /*
     * @see
     * org.graffiti.plugin.view.ViewListener#viewChanged(org.graffiti.plugin
     * .view.View)
     */
    public void viewChanged(View newView) {
        for (ViewListener l : viewListeners) {
            l.viewChanged(newView);
        }
    }

    /**
     * Informs all view manager listeners, that the given view type is
     * available.
     * 
     * @param viewType
     *            the new view type.
     */
    private void fireViewTypeAdded(String viewType) {
        for (ViewManagerListener l : listeners) {
            l.viewTypeAdded(viewType);
        }
    }

    /*
     * @see org.graffiti.managers.ViewManager#removeViews()
     */
    public void removeViews() {
        views.clear();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
