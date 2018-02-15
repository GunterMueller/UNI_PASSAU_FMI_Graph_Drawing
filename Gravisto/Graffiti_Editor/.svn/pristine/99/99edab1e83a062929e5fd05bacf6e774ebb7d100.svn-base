// =============================================================================
//
//   FastViewOptions.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import org.graffiti.plugins.scripting.js.JavaScriptEngine;

/**
 * Global options for the {@code FastView}s.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see FastView
 */
public class GlobalFastViewOptions {
    /**
     * Listener to changes of the global {@code FastView} options.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    public static abstract class Listener {
        /**
         * Is called when the default scripting language of the console changes.
         * 
         * @param consoleLanguageId
         *            the new console language.
         */
        public void consoleLanguageChanged(String consoleLanguageId) {
        }
    }

    private static final String CONSOLE_LANGUAGE_KEY = "consoleLanguage";

    private static final String DEFAULT_CONSOLE_LANGUAGE = JavaScriptEngine.LANGUAGE_ID;

    private static GlobalFastViewOptions singleton;

    private Preferences prefs = Preferences.userNodeForPackage(FastView.class);

    private List<Listener> listeners;

    /**
     * Determines the default display quality.
     */
    private OptimizationPolicy defaultOptimizationPolicy;

    /**
     * Default scripting language of the console.
     */
    private String consoleLanguage;

    /**
     * Returns the {@code GlobalFastViewOptions} singleton.
     * 
     * @return the {@code GlobalFastViewOptions} singleton.
     */
    public static GlobalFastViewOptions get() {
        if (singleton == null) {
            singleton = new GlobalFastViewOptions();
        }
        return singleton;
    }

    /**
     * Private constructor of {@code GlobalFastViewOptions}. To obtain an
     * instance, call {@link #get()}.
     */
    private GlobalFastViewOptions() {
        defaultOptimizationPolicy = OptimizationPolicy.DRAFT;

        consoleLanguage = prefs.get(CONSOLE_LANGUAGE_KEY,
                DEFAULT_CONSOLE_LANGUAGE);
    }

    /**
     * Adds the specified listener to be notified when a global {@code FastView}
     * option changes.
     * 
     * @param listener
     *            the listener to add.
     */
    public void addListener(Listener listener) {
        if (listeners == null) {
            listeners = new LinkedList<Listener>();
        }
        listeners.add(listener);
    }

    /**
     * Removes the specified listener.
     * 
     * @param listener
     *            the listener to remove.
     */
    public void removeListener(Listener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    /**
     * Returns the default display quality.
     * 
     * @return the default display quality.
     */
    public OptimizationPolicy getDefaultOptimizationPolicy() {
        return defaultOptimizationPolicy;
    }

    /**
     * Returns the default scripting language of the console.
     * 
     * @return the default scripting language of the console.
     */
    public String getDefaultConsoleLanguage() {
        return consoleLanguage;
    }

    /**
     * Sets the default scripting language of the console.
     * 
     * @param consoleLanguage
     *            the default scripting language of the console.
     */
    public void setDefaultConsoleLanguage(String consoleLanguage) {
        this.consoleLanguage = consoleLanguage;
        prefs.put(CONSOLE_LANGUAGE_KEY, consoleLanguage);

        for (Listener listener : listeners) {
            listener.consoleLanguageChanged(consoleLanguage);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
