// =============================================================================
//
//   ClassPathPluginDescriptionCollector.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ClassPathPluginDescriptionCollector.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.managers.pluginmgr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Searches for plugin description files in the current <code>CLASSPATH</code>.
 * 
 * @version $Revision: 5767 $
 * 
 * @see PluginDescriptionCollector
 */
public class ClassPathPluginDescriptionCollector implements
        PluginDescriptionCollector {

    /**
     * Constructs a new class path plugin description collector.
     */
    public ClassPathPluginDescriptionCollector() {
    }

    /**
     * Collects all plugin description files from the given class path.
     * 
     * @return An enumeration of all plugin description files from the given
     *         class path.
     */
    public List<Entry> collectPluginDescriptions() {
        HashSet<String> result = new HashSet<String>();

        collectFilesInRoots(splitClassPath(System
                .getProperty("java.class.path"), System
                .getProperty("path.separator")), result);

        // create a list of all plugin entries
        List<Entry> descriptions = new LinkedList<Entry>();

        for (String fileName : result) {
            // create a new parser instance for every xml file.
            // this is necessary to prevent inconsitencies in the
            // state of the XML parser, if the parsing of an xml
            // file fails...
            PluginXMLParser p = new PluginXMLParser();

            InputStream is = null;

            try {

                URL u = new URL(fileName);

                // create a jar url connection, if the protocol
                // starts with is "jar:"
                if (fileName.startsWith("jar:")) {
                    JarURLConnection juc = (JarURLConnection) u
                            .openConnection();
                    is = juc.getInputStream();
                } else {
                    URLConnection uc = u.openConnection();
                    is = uc.getInputStream();
                }

                PluginDescription d = p.parse(is);

                if (d == null) {
                    continue;
                }

                descriptions
                        .add(new DefaultPluginEntry(fileName.toString(), d));

                is.close();
            } catch (Exception e) {
                // do nothing
                e.printStackTrace();
            }
        }

        return descriptions;
    }

    /**
     * Checks if the given <code>fileName</code> looks like a graffiti plugin
     * description file. Does no dtd check or XML parsing.
     * 
     * @param fileName
     *            the name of the file to check.
     * 
     * @return DOCUMENT ME!
     */
    protected boolean isPluginDescription(String fileName) {
        // Maybe rename the string to "plugin.xml" or "graffiti-plugin.xml"
        return fileName.endsWith(".xml"); // TODO remove hard coded string
    }

    /**
     * Splits the <code>CLASSPATH</code> string and returns the elements of the
     * <code>CLASSPATH</code> in a list.
     * 
     * @param classPath
     *            DOCUMENT ME!
     * @param separator
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    List<String> splitClassPath(String classPath, String separator) {
        List<String> result = new LinkedList<String>();

        StringTokenizer tokenizer = new StringTokenizer(classPath, separator);

        // split
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken());
        }

        // logger.info(result.toString());
        return result;
    }

    /**
     * Returns <code>true</code>, if the given file ends with &quot;.jar&quot;
     * or &quot;.zip&quot;.
     * 
     * @param fileName
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    private boolean isPluginFile(String fileName) {
        // TODO remove hard coded strings
        return fileName.endsWith(".jar") || fileName.endsWith(".zip");
    }

    /**
     * Collects all plugin descriptin files from the given roots, recursively.
     * 
     * @param roots
     *            the list of roots, which should be searched for.
     * @param acc
     *            the accumulator, which will contain the result of the
     *            recursive search.
     */
    private void collectFilesInRoots(List<String> roots, HashSet<String> acc) {
        HashSet<String> classpathEntries = new HashSet<String>();

        for (String root : roots) {
            File entry = new File(root);
            if (entry.isFile() && isPluginFile(root)) {
                try {
                    JarFile jar = new JarFile(entry);
                    Manifest manifest = jar.getManifest();
                    if (manifest == null) {
                        continue;
                    }
                    String classpath = manifest.getMainAttributes().getValue(
                            Attributes.Name.CLASS_PATH);
                    if (classpath != null) {
                        List<String> list = splitClassPath(classpath, " ");
                        classpathEntries.addAll(list);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        roots.addAll(classpathEntries);

        for (String root : roots) {
            gatherFiles(new File(root), "", acc);
        }
    }

    /**
     * Checks if the given files contain plugin description files.
     * 
     * @param classRoot
     *            DOCUMENT ME!
     * @param fileName
     *            DOCUMENT ME!
     * @param acc
     *            DOCUMENT ME!
     */
    private void gatherFiles(File classRoot, String fileName,
            HashSet<String> acc) {
        File root = new File(classRoot, fileName);

        if (root.isFile()) {
            // the file is a plugin file. therefore search in the
            // plugin file for plugin descriptions, too.
            if (("".equals(fileName)) && (isPluginFile(root.toString()))) {
                JarFile jarFile = null;

                try {
                    jarFile = new JarFile(root);
                } catch (IOException e) {
                    System.out.println(e);
                }

                Enumeration<JarEntry> entries = jarFile.entries();

                while (entries.hasMoreElements()) {
                    JarEntry jarEntry = entries.nextElement();

                    if (isPluginDescription(jarEntry.getName())) {
                        acc.add("jar:file:" + classRoot.getAbsolutePath()
                                + "!/" + jarEntry.getName());
                    }
                }

                // the file is a plugin description. Add it to the list of
                // plugin descriptions.
            } else if (isPluginDescription(fileName)) {
                acc.add("file:" + root.toString());
            }

            // root is a directory: recursion
        } else {
            String[] contents = root.list();

            if (contents != null) {
                for (String s : contents) {
                    gatherFiles(classRoot, fileName + File.separatorChar + s,
                            acc);
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
