// =============================================================================
//
//   PluginFileFilter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PluginFileFilter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.managers.pluginmgr;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import org.graffiti.core.Bundle;

/**
 * Represents a file filter for graffiti plugins.
 * 
 * @version $Revision: 5767 $
 */
public class PluginFileFilter extends FileFilter {
    /** The description of this file filter. */
    private String description;

    /** The list of extensions of this file to filter. */
    private String[] extensions = null;

    /**
     * Constructor for PluginFileFilter.
     * 
     * @param extension
     *            DOCUMENT ME!
     */
    public PluginFileFilter(String extension) {
        this(new String[] { extension });
    }

    /**
     * Constructs a new plugin file filter from the given array of extensions.
     * 
     * @param extensions
     *            the array of extensions (<tt>String</tt>) to filter.
     */
    public PluginFileFilter(String[] extensions) {
        super();

        this.extensions = extensions;

        StringBuffer exts = new StringBuffer();

        for (String ext : extensions) {
            exts.append(ext);
        }

        description = Bundle.getCoreBundle().getString(
                "plugin.filter.description." + exts.toString());
    }

    /**
     * @see javax.swing.filechooser.FileFilter#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Returns the extension of the selected file.
     * 
     * @param f
     *            DOCUMENT ME!
     * 
     * @return the extension of the selected file.
     */
    public String getExtension(File f) {
        String ext = null;
        String s = f.getName();

        int i = s.lastIndexOf('.');

        if ((i > 0) && (i < (s.length() - 1))) {
            ext = s.substring(i + 1).toLowerCase();
        }

        return ext;
    }

    /**
     * @see javax.swing.filechooser.FileFilter#accept(File)
     */
    @Override
    public boolean accept(File f) {
        if (f.isDirectory())
            return true;

        String extension = getExtension(f);

        if (extension == null)
            return false;

        for (String ext : extensions) {
            if (ext.compareTo(extension) == 0)
                return true;
        }

        return false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
