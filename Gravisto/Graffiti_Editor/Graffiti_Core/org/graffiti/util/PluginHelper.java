// =============================================================================
//
//   PluginHelper.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PluginHelper.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.graffiti.core.Bundle;
import org.graffiti.managers.pluginmgr.PluginDescription;
import org.graffiti.managers.pluginmgr.PluginManagerException;
import org.graffiti.managers.pluginmgr.PluginXMLParser;
import org.xml.sax.SAXException;

/**
 * 
 */
public class PluginHelper {

    /**
     * Reads and returns the plugin description of the plugin from the given
     * URL.
     * 
     * @param pluginLocation
     *            the URL to the plugin.
     * 
     * @return DOCUMENT ME!
     * 
     * @exception PluginManagerException
     *                if an error occurrs while loading the plugin description.
     */
    public static PluginDescription readPluginDescription(URL pluginLocation)
            throws PluginManagerException {
        if (pluginLocation == null)
            throw new PluginManagerException("exception.MalformedURL", "null");

        String fileName = pluginLocation.toString();
        InputStream input;

        if (fileName.toLowerCase().endsWith(".xml")) {
            try {
                if (fileName.startsWith("jar:")) {
                    JarURLConnection juc = (JarURLConnection) pluginLocation
                            .openConnection();
                    input = juc.getInputStream();
                } else {
                    URLConnection uc = pluginLocation.openConnection();
                    input = uc.getInputStream();
                }
            } catch (IOException ioe) {
                throw new PluginManagerException("exception.IO");
            }

            // directly read from the jar or zip file
        } else if (fileName.toLowerCase().endsWith(".jar")
                || fileName.toLowerCase().endsWith(".zip")) {
            try {
                JarFile file = new JarFile(new File(new URI(pluginLocation
                        .toString())));
                Bundle bundle = Bundle.getCoreBundle();
                ZipEntry entry = file.getEntry(bundle
                        .getString("plugin.xml.filename"));

                if (entry != null) {
                    // create an input stream from this entry.
                    input = file.getInputStream(entry);
                } else
                    throw new PluginManagerException("exception.IO");
            } catch (MalformedURLException mue) {
                throw new PluginManagerException("exception.MalformedURL");
            } catch (URISyntaxException use) {
                throw new PluginManagerException("exception.URISyntax");
            } catch (IOException ioe) {
                throw new PluginManagerException("exception.IO");
            }
        } else
            throw new PluginManagerException("exception.unknownFileType",
                    fileName);

        PluginDescription description = null;

        try {
            PluginXMLParser parser = new PluginXMLParser();
            description = parser.parse(input);
        } catch (IOException ioe) {
            throw new PluginManagerException("exception.IO", ioe.getMessage());
        } catch (SAXException saxe) {
            saxe.printStackTrace();

            // throw new PluginManagerException(
            // "exception.SAX", "\n" + saxe.getMessage() + "");
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        return description;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
