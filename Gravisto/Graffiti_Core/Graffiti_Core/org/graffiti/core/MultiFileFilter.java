// =============================================================================
//
//   MultiFileFilter.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MultiFileFilter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.core;

import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.filechooser.FileFilter;

/**
 * This file filter allows showing more than one file extension in a filechooser
 * dialog.
 * 
 * @author Marek Piorkowski
 * @version $Revision 1.0$ $Date: 2010-05-07 14:42:02 -0400 (Fri, 07 May 2010) $
 */
public class MultiFileFilter extends FileFilter {

    private Hashtable<String, FileFilter> filters;

    private String description;

    private String fullDescription;

    private boolean useExtensionsInDescription = true;

    /**
     * Creates a file filter. If no filters are added, then all files are
     * accepted.
     * 
     */
    public MultiFileFilter() {
        this.filters = new Hashtable<String, FileFilter>();
    }

    /**
     * Creates a file filter that accepts files with the given extension.
     * 
     */
    public MultiFileFilter(String extension) {
        this(extension, null);
    }

    /**
     * Creates a file filter that accepts the given file type.
     * 
     * Note that the "." before the extension is not needed. If provided, it
     * will be ignored.
     * 
     */
    public MultiFileFilter(String extension, String description) {
        this();
        if (extension != null) {
            addExtension(extension);
        }
        if (description != null) {
            setDescription(description);
        }
    }

    /**
     * Creates a file filter from the given string array.
     * 
     * Note that the "." before the extension is not needed adn will be ignored.
     */
    public MultiFileFilter(String[] filters) {
        this(filters, null);
    }

    /**
     * Creates a file filter from the given string array and description. Note
     * that the "." before the extension is not needed and will be ignored.
     * 
     */
    public MultiFileFilter(String[] filters, String description) {
        this();
        for (int i = 0; i < filters.length; i++) {
            // add filters one by one
            addExtension(filters[i]);
        }
        if (description != null) {
            setDescription(description);
        }
    }

    /**
     * Return true if this file should be shown in the directory pane, false if
     * it shouldn't.
     * 
     * Files that begin with "." are ignored.
     * 
     */
    @Override
    public boolean accept(File f) {
        if (f != null) {
            if (f.isDirectory())
                return true;
            String extension = getExtension(f);
            if (extension != null && filters.get(getExtension(f)) != null)
                return true;
        }
        return false;
    }

    /**
     * Return the extension portion of the file's name .
     * 
     */
    public String getExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1)
                return filename.substring(i + 1).toLowerCase();
        }
        return null;
    }

    /**
     * Returns all extensions in an array.
     * 
     * @return All extensions in an array.
     */
    public String[] getAllExtensions() {
        String[] extensions = new String[filters.values().size()];
        Enumeration<String> keys = filters.keys();
        int i = 0;
        while (keys.hasMoreElements()) {
            extensions[i] = keys.nextElement();
            i++;
        }
        return extensions;
    }

    /**
     * Adds a filetype "dot" extension to filter against.
     * 
     * Note that the "." before the extension will be ignored. So both, ".xxx"
     * and "xxx" will be accepted here.
     */
    public void addExtension(String extension) {
        if (extension.length() > 0) {
            if (filters == null) {
                filters = new Hashtable<String, FileFilter>(5);
            }
            if (extension.charAt(0) == '.') {
                filters.put(extension.toLowerCase().substring(1,
                        extension.length()), this);
            } else {
                filters.put(extension.toLowerCase(), this);
            }
            fullDescription = null;
        }
    }

    /**
     * Returns the human readable description of this filter.
     * 
     */
    @Override
    public String getDescription() {
        if (fullDescription == null) {
            if (description == null || isExtensionListInDescription()) {
                fullDescription = description == null ? "" : description + " (";
                // build the description from the extension list
                Enumeration<String> extensions = filters.keys();
                if (extensions != null) {
                    fullDescription += "*." + extensions.nextElement();
                    while (extensions.hasMoreElements()) {
                        fullDescription += ", *." + extensions.nextElement();
                    }
                }
                fullDescription += description == null ? "" : ")";
            } else {
                fullDescription = description;
            }
        }
        return fullDescription;
    }

    /**
     * Sets the human readable description of this filter.
     * 
     */
    public void setDescription(String description) {
        this.description = description;
        fullDescription = null;
    }

    /**
     * Determines whether the extension list should show up in the human
     * readable description.
     * 
     * Only relevent if a description was provided in the constructor or using
     * setDescription();
     * 
     */
    public void setExtensionListInDescription(boolean b) {
        useExtensionsInDescription = b;
        fullDescription = null;
    }

    /**
     * Returns whether the extension list should show up in the human readable
     * description.
     * 
     * Only relevent if a description was provided in the constructor or using
     * setDescription();
     * 
     */
    public boolean isExtensionListInDescription() {
        return useExtensionsInDescription;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
