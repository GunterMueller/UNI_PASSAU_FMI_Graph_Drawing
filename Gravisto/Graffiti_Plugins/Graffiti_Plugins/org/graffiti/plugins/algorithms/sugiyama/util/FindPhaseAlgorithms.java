// =============================================================================
//
//   FindPhaseAlgorithms.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FindPhaseAlgorithms.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.graffiti.plugins.algorithms.sugiyama.constraints.SugiyamaConstraint;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.CrossMinAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.decycling.DecyclingAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.layout.LayoutAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.levelling.LevellingAlgorithm;

/**
 * This class finds algorithms that implement the sugiyama-interfaces.
 * 
 * @author Ferdinand H�bner
 */
public class FindPhaseAlgorithms {

    private static final String PACKAGE_SUGIYAMA = "org.graffiti.plugins."
            + "algorithms.sugiyama.";

    public static ArrayList<String[]> getPhaseAlgorithms(
            HashMap<String, String> algorithms, String algorithmType) {
        ArrayList<String[]> algos = new ArrayList<String[]>();
        ArrayList<String> phase1 = new ArrayList<String>();
        ArrayList<String> phase2 = new ArrayList<String>();
        ArrayList<String> phase3 = new ArrayList<String>();
        ArrayList<String> phase4 = new ArrayList<String>();

        Map.Entry<String, String> entry;
        Iterator<Map.Entry<String, String>> setIter = algorithms.entrySet()
                .iterator();
        while (setIter.hasNext()) {
            entry = setIter.next();
            if (entry.getValue().equalsIgnoreCase("decyclingalgorithm")) {
                phase1.add(entry.getKey());
            } else if (entry.getValue().equalsIgnoreCase("levellingalgorithm")) {
                phase2.add(entry.getKey());
            } else if (entry.getValue().equalsIgnoreCase("crossminalgorithm")) {
                phase3.add(entry.getKey());
            } else if (entry.getValue().equalsIgnoreCase("layoutalgorithm")) {
                phase4.add(entry.getKey());
            }
        }
        String[] phase;

        // phase 1 - decycling
        int radialAlgorithms = 0;
        DecyclingAlgorithm d;
        BitSet support = new BitSet(phase1.size());
        for (int i = 0; i < phase1.size(); i++) {
            try {
                d = (DecyclingAlgorithm) Class.forName(phase1.get(i))
                        .newInstance();
                if (d.supportsAlgorithmType(algorithmType)) {
                    support.set(i);
                    radialAlgorithms++;
                }
            } catch (Exception e) {

            }
        }
        int index;
        if (radialAlgorithms == 0) {
            phase = null;
        } else {
            phase = new String[radialAlgorithms];
            index = 0;
            for (int i = 0; i < phase1.size(); i++) {
                if (support.get(i)) {
                    phase[index] = phase1.get(i);
                    index++;
                }
            }
        }
        algos.add(0, phase);

        // phase 2 - levelling
        radialAlgorithms = 0;
        LevellingAlgorithm l;
        support = new BitSet(phase2.size());
        for (int i = 0; i < phase2.size(); i++) {
            try {
                l = (LevellingAlgorithm) Class.forName(phase2.get(i))
                        .newInstance();
                if (l.supportsAlgorithmType(algorithmType)) {
                    support.set(i);
                    radialAlgorithms++;
                }
            } catch (Exception e) {

            }
        }
        if (radialAlgorithms == 0) {
            phase = null;
        } else {
            phase = new String[radialAlgorithms];
            index = 0;
            for (int i = 0; i < phase2.size(); i++) {
                if (support.get(i)) {
                    phase[index] = phase2.get(i);
                    index++;
                }
            }
        }
        algos.add(1, phase);

        // phase 3 - crossmin
        radialAlgorithms = 0;
        CrossMinAlgorithm c;
        support = new BitSet(phase3.size());
        for (int i = 0; i < phase3.size(); i++) {
            try {
                c = (CrossMinAlgorithm) Class.forName(phase3.get(i))
                        .newInstance();
                if (c.supportsAlgorithmType(algorithmType)) {
                    support.set(i);
                    radialAlgorithms++;
                }
            } catch (Exception e) {

            }
        }
        if (radialAlgorithms == 0) {
            phase = null;
        } else {
            phase = new String[radialAlgorithms];
            index = 0;
            for (int i = 0; i < phase3.size(); i++) {
                if (support.get(i)) {
                    phase[index] = phase3.get(i);
                    index++;
                }
            }
        }
        algos.add(2, phase);

        // phase 4 - layout
        radialAlgorithms = 0;
        LayoutAlgorithm la;
        support = new BitSet(phase4.size());
        for (int i = 0; i < phase4.size(); i++) {
            try {
                la = (LayoutAlgorithm) Class.forName(phase4.get(i))
                        .newInstance();
                if (la.supportsAlgorithmType(algorithmType)) {
                    support.set(i);
                    radialAlgorithms++;
                }
            } catch (Exception e) {

            }
        }
        if (radialAlgorithms == 0) {
            phase = null;
        } else {
            phase = new String[radialAlgorithms];
            index = 0;
            for (int i = 0; i < phase4.size(); i++) {
                if (support.get(i)) {
                    phase[index] = phase4.get(i);
                    index++;
                }
            }
        }
        algos.add(3, phase);

        return algos;
    }

    /**
     * This method scans through the HashMap and returns an ArrayList of
     * String[], where the first entry in the ArrayList is the first phase in
     * the sugiyama-algorithm and so on.
     * 
     */
    public static ArrayList<String[]> getPhaseAlgorithms(
            HashMap<String, String> algorithms) {
        ArrayList<String[]> algos = new ArrayList<String[]>();
        ArrayList<String> phase1 = new ArrayList<String>();
        ArrayList<String> phase2 = new ArrayList<String>();
        ArrayList<String> phase3 = new ArrayList<String>();
        ArrayList<String> phase4 = new ArrayList<String>();

        Map.Entry<String, String> entry;
        Iterator<Map.Entry<String, String>> setIter = algorithms.entrySet()
                .iterator();
        while (setIter.hasNext()) {
            entry = setIter.next();
            if (entry.getValue().equalsIgnoreCase("decyclingalgorithm")) {
                phase1.add(entry.getKey());
            } else if (entry.getValue().equalsIgnoreCase("levellingalgorithm")) {
                phase2.add(entry.getKey());
            } else if (entry.getValue().equalsIgnoreCase("crossminalgorithm")) {
                phase3.add(entry.getKey());
            } else if (entry.getValue().equalsIgnoreCase("layoutalgorithm")) {
                phase4.add(entry.getKey());
            }
        }
        String[] phase;

        phase = new String[phase1.size()];
        for (int i = 0; i < phase1.size(); i++) {
            phase[i] = phase1.get(i);
        }
        algos.add(0, phase);

        phase = new String[phase2.size()];
        for (int i = 0; i < phase2.size(); i++) {
            phase[i] = phase2.get(i);
        }
        algos.add(1, phase);

        phase = new String[phase3.size()];
        for (int i = 0; i < phase3.size(); i++) {
            phase[i] = phase3.get(i);
        }
        algos.add(2, phase);

        phase = new String[phase4.size()];
        for (int i = 0; i < phase4.size(); i++) {
            phase[i] = phase4.get(i);
        }
        algos.add(3, phase);

        return algos;

    }

    /**
     * This class returns the default algorithms in a HashMap
     * 
     * @return Returns the default algorithms in a HashMap
     */
    public static HashMap<String, String> getAlgorithms() {
        HashMap<String, String> algorithms = new HashMap<String, String>();
        algorithms.put(SugiyamaConstants.DEFAULT_CROSSMIN_ALGORITHM,
                "CrossMinAlgorithm");
        algorithms.put(SugiyamaConstants.DEFAULT_DECYCLING_ALGORITHM,
                "DecyclingAlgorithm");
        algorithms.put(SugiyamaConstants.DEFAULT_LAYOUT_ALGORITHM,
                "LayoutAlgorithm");
        algorithms.put(SugiyamaConstants.DEFAULT_LEVELLING_ALGORITHM,
                "LevellingAlgorithm");

        return algorithms;
    }

    /**
     * Recursive list a directory and append all the files it contains to the
     * HashSet listing. If another directory is found, the method calls itself
     * 
     * @param listing
     *            A <code>HashSet</code> that contains the name of all files in
     *            a directory-tree
     * @param directory
     *            The <code>File</code> that represents the current directory.
     */
    private static void recursiveList(HashSet<File> listing, File directory) {
        File[] contents = directory.listFiles();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i].isFile()) {
                listing.add(contents[i]);
            } else {
                recursiveList(listing, contents[i]);
            }
        }
    }

    /**
     * This method tries to discover implementations of the sugiyama-interface
     * inside the users classpath and returns a HashMap of the following form:
     * 
     * "name of the phase", "binary name of the algorithm"
     * 
     * @return Returns a HashMap to map phases to the available algorithms in
     *         the user-classpath.
     */
    public static HashMap<String, String> discoverAlgorithms(SugiyamaData data) {
        File f;
        JarInputStream jar;
        JarEntry jarEntry;
        HashSet<File> classes = new HashSet<File>();
        HashMap<String, String> algorithms = new HashMap<String, String>();

        // get the runtime-bean
        RuntimeMXBean b = ManagementFactory.getRuntimeMXBean();

        // build the classpath
        String classpath = b.getClassPath();
        // classpath += File.pathSeparator + b.getBootClassPath();
        String[] entries = classpath.split(File.pathSeparator);

        // find classes in the classpath
        for (int i = 0; i < entries.length; i++) {
            f = new File(entries[i]);
            if (f.isDirectory()) {
                recursiveList(classes, f);
            } else if (f.getName().endsWith(".jar")) {
                classes.add(f);
            }
        }
        Iterator<File> fileIterator = classes.iterator();
        String className;

        while (fileIterator.hasNext()) {
            f = fileIterator.next();
            if (f.getName().endsWith(".jar")) {
                try {
                    jar = new JarInputStream(new FileInputStream(f
                            .getAbsoluteFile()));
                    while (true) {
                        jarEntry = jar.getNextJarEntry();
                        if (jarEntry == null) {
                            break;
                        }

                        if (jarEntry.getName().endsWith(".class")) {
                            char[] name = jarEntry.getName().toCharArray();
                            for (int i = 0; i < name.length; i++)
                                if (name[i] == File.separatorChar) {
                                    name[i] = '.';
                                }

                            className = new String(name);

                            if (!Class.forName(className).isInterface()) {
                                if (Class.forName(
                                        PACKAGE_SUGIYAMA + "decycling"
                                                + ".DecyclingAlgorithm")
                                        .isAssignableFrom(
                                                Class.forName(className))) {

                                    algorithms.put(className,
                                            "DecyclingAlgorithm");
                                    System.out.println(className);

                                }
                                if (Class.forName(
                                        PACKAGE_SUGIYAMA + "levelling"
                                                + ".LevellingAlgorithm")
                                        .isAssignableFrom(
                                                Class.forName(className))) {

                                    algorithms.put(className,
                                            "LevellingAlgorithm");

                                }
                                if (Class.forName(
                                        PACKAGE_SUGIYAMA + "crossmin"
                                                + ".CrossMinAlgorithm")
                                        .isAssignableFrom(
                                                Class.forName(className))) {

                                    algorithms.put(className,
                                            "LevellingAlgorithm");

                                }
                                if (Class.forName(
                                        PACKAGE_SUGIYAMA + "layout"
                                                + ".LayoutAlgorithm")
                                        .isAssignableFrom(
                                                Class.forName(className))) {

                                    algorithms
                                            .put(className, "LayoutAlgorithm");

                                }
                                if (Class
                                        .forName(
                                                PACKAGE_SUGIYAMA
                                                        + "constraints.SugiyamaConstraint")
                                        .isAssignableFrom(
                                                Class.forName(className))) {

                                    putConstraint(className, data);

                                }
                            }
                        }
                    }
                } catch (IOException ioe) {

                } catch (Exception e) {

                }
            } else if (f.getName().endsWith(".class")) {
                className = getBinaryName(f);

                try {
                    if (!Class.forName(className).isInterface()
                            && !className.contains("Abstract")) {
                        if (Class.forName(
                                PACKAGE_SUGIYAMA + "decycling"
                                        + ".DecyclingAlgorithm")
                                .isAssignableFrom(Class.forName(className))) {

                            algorithms.put(className, "DecyclingAlgorithm");
                            System.out.println("Found algorithm: " + className);

                        } else if (Class.forName(
                                PACKAGE_SUGIYAMA + "levelling"
                                        + ".LevellingAlgorithm")
                                .isAssignableFrom(Class.forName(className))) {

                            algorithms.put(className, "LevellingAlgorithm");
                            System.out.println("Found algorithm: " + className);

                        } else if (Class.forName(
                                PACKAGE_SUGIYAMA + "crossmin"
                                        + ".CrossMinAlgorithm")
                                .isAssignableFrom(Class.forName(className))) {

                            algorithms.put(className, "CrossMinAlgorithm");
                            System.out.println("Found algorithm: " + className);

                        } else if (Class.forName(
                                PACKAGE_SUGIYAMA + "layout"
                                        + ".LayoutAlgorithm").isAssignableFrom(
                                Class.forName(className))) {

                            algorithms.put(className, "LayoutAlgorithm");
                            System.out.println("Found algorithm: " + className);
                        } else if (Class.forName(
                                PACKAGE_SUGIYAMA + "constraints"
                                        + ".SugiyamaConstraint")
                                .isAssignableFrom(Class.forName(className))) {
                            putConstraint(className, data);
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
        return algorithms;
    }

    /**
     * This method takes the binary name of a constraint, instanciates it and
     * adds the instance to SugiyamaData.
     * 
     * @param className
     *            The full binary-name of the constraint-implementation
     */
    private static void putConstraint(String className, SugiyamaData data) {
        HashSet<SugiyamaConstraint> constraints = data.getConstraints();
        HashSet<String> names = new HashSet<String>();

        Iterator<SugiyamaConstraint> iter = constraints.iterator();
        SugiyamaConstraint constraint;
        String name;

        while (iter.hasNext()) {
            constraint = iter.next();
            name = constraint.getClass().getName();
            if (!names.contains(name)) {
                names.add(name);
            }
        }
        if (!names.contains(className)) {
            try {
                constraints.add((SugiyamaConstraint) Class.forName(className)
                        .newInstance());
                System.out.println("Found constraint: " + className);
            } catch (Exception e) {

            }
        }
    }

    /**
     * This method tries to "guess" the binary-name of a class located in the
     * file f. All File.seperators are replaced with . and the binary-name of
     * the class is "guessed" by iteratively removing the first part of the
     * possible binary name
     * 
     * @param f
     *            The file of the class
     * @return Returns the binary name of the class, or <tt>null</tt> if the
     *         method was unable to guess the binary-name.
     */
    private static String getBinaryName(File f) {
        String className;
        char[] name = f.getAbsolutePath().toCharArray();
        for (int i = 0; i < name.length; i++)
            if (name[i] == File.separatorChar) {
                name[i] = '.';
            }
        className = new String(name);
        className = className.replaceAll(".class", "");
        boolean suc = false;

        while (true) {
            try {
                Class.forName(className);
                suc = true;
                break;
            } catch (ClassNotFoundException cnfe) {
                className = className.substring(className.indexOf(".") + 1);
                // System.out.println(className);
                if (!className.contains(".")) {
                    break;
                }
            }
        }
        if (suc)
            return className;
        else
            return null;

    }

    /**
     * This method returns a HashSet&gt;String&lt; that describe the new
     * algorithms found in discoverAlgorithms()
     * 
     * @param newAlgos
     *            All currently available algorithms
     * 
     * @return Returns a HashSet with the binary-names of the new algorithms
     */
    public static HashSet<String> displayNewComponents(
            ArrayList<String[]> newAlgos, SugiyamaData data) {
        HashSet<String> messages = new HashSet<String>();
        ArrayList<String[]> oldAlgos;
        oldAlgos = data.getAlgorithmBinaryNames();
        boolean found;

        for (int i = 0; i < newAlgos.size(); i++) {
            if (newAlgos.get(i) != null) {
                for (int j = 0; j < newAlgos.get(i).length; j++) {
                    found = false;
                    for (int k = 0; k < oldAlgos.get(i).length; k++) {
                        if (newAlgos.get(i)[j].matches(oldAlgos.get(i)[k])) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        messages.add(newAlgos.get(i)[j]);
                    }
                }
            }
        }
        return messages;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
