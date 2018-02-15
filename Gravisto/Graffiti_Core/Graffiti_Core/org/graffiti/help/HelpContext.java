// =============================================================================
//
//   HelpContext.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: HelpContext.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.help;

import java.net.URL;

import javax.swing.JComponent;

/**
 * Represents the help context of an algorithm or a gui element in the editor.
 * 
 * @author flierl
 * @version $Revision: 5767 $
 */
public class HelpContext {

    /** The java help ID of this help context. */
    private String helpID;

    /** The URL of this help context. */
    private URL helpURL;

    /**
     * Constructs a new help context from the given URL.
     * 
     * @param helpURL
     *            the URL to create a help context from.
     */
    public HelpContext(URL helpURL) {
    }

    /**
     * Constructs a new help context from the given helpID.
     * 
     * @param helpID
     *            the javahelp help id.
     */
    public HelpContext(String helpID) {
    }

    /**
     * Constructs a new help context from the given class.
     * 
     * @param clazz
     *            the class to construct a help context from.
     */
    public HelpContext(Class<?> clazz) {
    }

    /**
     * Returns the java help id of this help context.
     * 
     * @return the java help id of thie help context.
     */
    public String getHelpID() {
        return helpID;
    }

    /**
     * Returns the url of this help context.
     * 
     * @return the url of this help context.
     */
    public java.net.URL getHelpURL() {
        return helpURL;
    }

    /**
     * Returns a help context for the given gui component.
     * 
     * @param comp
     *            the component to search the help context for.
     * 
     * @return DOCUMENT ME!
     */
    public HelpContext findHelp(JComponent comp) {
        return null; // TODO
    }

    /**
     * Returns a human readable string of this help context. This method is for
     * debugging purposes only.
     * 
     * @return a human readable string of this help context.
     */
    @Override
    public String toString() {
        return null; // TODO
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
