// =============================================================================
//
//   GenericFileFilter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GenericFileFilter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.core;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Contains a generic file filter for filtering file extensions in the file
 * chooser dialog.
 * 
 * @version $Revision: 5767 $
 */
public class GenericFileFilter extends FileFilter {
    /** The extension for filtering */
    private String extension;

    /**
     * Constructor for GenericFileFilter.
     * 
     * @param extension
     *            a extension for which the filter will be built.
     */
    public GenericFileFilter(String extension) {
        this.extension = extension;
    }

    /**
     * The description of this filter. For example: ".gml"
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public String getDescription() {
        return "*" + extension;
    }

    /**
     * Return the extension string for files that this filter allows.
     * 
     * @return DOCUMENT ME!
     */
    public String getExtension() {
        return extension;
    }

    /**
     * Whether the given file is accepted by this filter.
     * 
     * @param f
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean accept(File f) {
        boolean accept = f.isDirectory();

        if (!accept) {
            String suffix = getExtension(f);

            if (suffix != null) {
                accept = suffix.equals(extension);
            }
        }

        return accept;
    }

    /**
     * Get the extension of a file.
     * 
     * @param f
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private String getExtension(File f) {
        String ext = null;
        String s = f.getPath();
        int i = s.lastIndexOf('.');

        if ((i > 0) && (i < (s.length() - 1))) {
            ext = s.substring(i).toLowerCase();
        }

        return ext;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
