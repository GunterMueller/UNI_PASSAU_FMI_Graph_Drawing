// =============================================================================
//
//   PreferencesUtil.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PreferencesUtil.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.sugiyama.constraints.HorizontalConstraintWithTwoNodes;
import org.graffiti.plugins.algorithms.sugiyama.constraints.SugiyamaConstraint;
import org.graffiti.plugins.algorithms.sugiyama.constraints.VerticalConstraintWithTwoNodes;
import org.graffiti.plugins.grids.OrthogonalGrid;
import org.graffiti.plugins.grids.RadialGrid;

/**
 * This class provides methods to save and load data from the user's
 * java-preferences.
 * 
 * @author Ferdinand H&uuml;bner
 */
public class PreferencesUtil {
    /**
     * Load the saved preferences from the system and store them in a
     * <code>SugiyamaData</code>-Bean
     * 
     * @param data
     *            The <code>SugiyamaData</code>-Bean, where the loaded
     *            preferences get stored in
     */
    public static void loadPreferences(SugiyamaData data) {
        // Load preferences-node for Sugiyama
        Preferences prefs = Preferences
                .userNodeForPackage(org.graffiti.plugins.algorithms.sugiyama.Sugiyama.class);
        HashMap<String, String> algos = new HashMap<String, String>();
        int[] numAlgos = new int[4];

        try {
            String[] keys = prefs.keys();

            // Collect saved values
            for (int i = 0; i < keys.length; i++) {
                if (keys[i]
                        .startsWith(SugiyamaConstants.KEY_FRAMEWORK_GRID_TYPE)) {
                    String gridInPrefs = prefs.get(keys[i], null);
                    if (gridInPrefs == null) {
                        data.setGridType(null);
                    } else if (gridInPrefs
                            .equals(SugiyamaConstants.PARAM_GRID_ORTHOGONAL)) {
                        data.setGridType(new OrthogonalGrid());
                    } else if (gridInPrefs
                            .equals(SugiyamaConstants.PARAM_GRID_RADIAL)) {
                        data.setGridType(new RadialGrid());
                    } else {
                        data.setGridType(null);
                    }
                }
                if (keys[i].startsWith(SugiyamaConstants.KEY_PREFIX_DECYCLING)) {
                    algos.put(prefs.get(keys[i], null), "DecyclingAlgorithm");
                    numAlgos[0]++;
                } else if (keys[i]
                        .startsWith(SugiyamaConstants.KEY_PREFIX_LEVELLING)) {
                    algos.put(prefs.get(keys[i], null), "LevellingAlgorithm");
                    numAlgos[1]++;
                } else if (keys[i]
                        .startsWith(SugiyamaConstants.KEY_PREFIX_CROSSMIN)) {
                    algos.put(prefs.get(keys[i], null), "CrossMinAlgorithm");
                    numAlgos[2]++;
                } else if (keys[i]
                        .startsWith(SugiyamaConstants.KEY_PREFIX_LAYOUT)) {
                    algos.put(prefs.get(keys[i], null), "LayoutAlgorithm");
                    numAlgos[3]++;
                } else if (keys[i]
                        .startsWith(SugiyamaConstants.KEY_SELECTED_DECYCLING)) {
                    data.getLastSelectedAlgorithms()[0] = prefs.get(keys[i],
                            null);
                } else if (keys[i]
                        .startsWith(SugiyamaConstants.KEY_SELECTED_LEVELLING)) {
                    data.getLastSelectedAlgorithms()[1] = prefs.get(keys[i],
                            null);
                } else if (keys[i]
                        .startsWith(SugiyamaConstants.KEY_SELECTED_CROSSMIN)) {
                    data.getLastSelectedAlgorithms()[2] = prefs.get(keys[i],
                            null);
                } else if (keys[i]
                        .startsWith(SugiyamaConstants.KEY_SELECTED_LAYOUT)) {
                    data.getLastSelectedAlgorithms()[3] = prefs.get(keys[i],
                            null);
                } else if (keys[i]
                        .startsWith(SugiyamaConstants.PREFIX_CONSTRAINT)) {
                    try {
                        data.getConstraints().add(
                                (SugiyamaConstraint) Class.forName(keys[i])
                                        .newInstance());
                    } catch (Exception e) {

                    }
                }
            }
        } catch (BackingStoreException ex) {
            // don't handle this - default algorithms are put anyway
        }
        if (numAlgos[0] == 0) {
            algos.put(SugiyamaConstants.DEFAULT_DECYCLING_ALGORITHM,
                    "DecyclingAlgorithm");
        }
        if (numAlgos[1] == 0) {
            algos.put(SugiyamaConstants.DEFAULT_LEVELLING_ALGORITHM,
                    "LevellingAlgorithm");
        }
        if (numAlgos[2] == 0) {
            algos.put(SugiyamaConstants.DEFAULT_CROSSMIN_ALGORITHM,
                    "CrossMinAlgorithm");
        }
        if (numAlgos[3] == 0) {
            algos.put(SugiyamaConstants.DEFAULT_LAYOUT_ALGORITHM,
                    "LayoutAlgorithm");
        }

        validateConstraints(data);
        data.setPhaseAlgorithms(algos);
    }

    public static void loadFrameworkParameters(SugiyamaData data) {
        // Load preferences-node for Sugiyama
        Preferences prefs = Preferences
                .userNodeForPackage(org.graffiti.plugins.algorithms.sugiyama.Sugiyama.class);

        ((BooleanParameter) data.getAlgorithmParameters()[0]).setValue(prefs
                .getInt(SugiyamaConstants.KEY_FRAMEWORK_ANIMATED,
                        SugiyamaConstants.DEFAULT_ANIMATION_POLICY) == 1);

        ((StringSelectionParameter) data.getAlgorithmParameters()[1])
                .setSelectedValue(prefs.getInt(
                        SugiyamaConstants.KEY_FRAMEWORK_DRAWING, 0));

        ((StringSelectionParameter) data.getAlgorithmParameters()[2])
                .setSelectedValue(prefs.getInt(
                        SugiyamaConstants.KEY_FRAMEWORK_BIG_NODE_POLICY,
                        SugiyamaConstants.DEFAULT_BIG_NODE_POLICY));

        ((StringSelectionParameter) data.getAlgorithmParameters()[3])
                .setSelectedValue(prefs.getInt(
                        SugiyamaConstants.KEY_FRAMEWORK_CONSTRAINT_POLICY,
                        SugiyamaConstants.DEFAULT_CONSTRAINT_POLICY));

    }

    public static void saveFrameworkParameters(SugiyamaData data) {
        Preferences prefs = Preferences
                .userNodeForPackage(org.graffiti.plugins.algorithms.sugiyama.Sugiyama.class);

        if (((BooleanParameter) data.getAlgorithmParameters()[0]).getBoolean()) {
            prefs.putInt(SugiyamaConstants.KEY_FRAMEWORK_ANIMATED, 1);
        } else {
            prefs.putInt(SugiyamaConstants.KEY_FRAMEWORK_ANIMATED, 0);
        }

        prefs.putInt(SugiyamaConstants.KEY_FRAMEWORK_DRAWING,
                ((StringSelectionParameter) data.getAlgorithmParameters()[1])
                        .getSelectedIndex());

        prefs.putInt(SugiyamaConstants.KEY_FRAMEWORK_BIG_NODE_POLICY,
                ((StringSelectionParameter) data.getAlgorithmParameters()[2])
                        .getSelectedIndex());

        prefs.putInt(SugiyamaConstants.KEY_FRAMEWORK_CONSTRAINT_POLICY,
                ((StringSelectionParameter) data.getAlgorithmParameters()[3])
                        .getSelectedIndex());

        if (((BooleanParameter) data.getAlgorithmParameters()[4]).getBoolean()) {
            if (data.getAlgorithmType().equals(
                    SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)) {
                prefs.put(SugiyamaConstants.KEY_FRAMEWORK_GRID_TYPE,
                        SugiyamaConstants.GRID_CLASSNAME_RADIAL);
            } else if (data.getAlgorithmType().equals(
                    SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)) {
                prefs.put(SugiyamaConstants.KEY_FRAMEWORK_GRID_TYPE,
                        SugiyamaConstants.GRID_CLASSNAME_ORTHOGONAL);
            } else {
                prefs.put(SugiyamaConstants.KEY_FRAMEWORK_GRID_TYPE,
                        SugiyamaConstants.GRID_CLASSNAME_NONE);
            }
        } else {
            prefs.put(SugiyamaConstants.KEY_FRAMEWORK_GRID_TYPE,
                    SugiyamaConstants.GRID_CLASSNAME_NONE);
        }

    }

    /**
     * Save the current preferences of the Sugiyama-Framework into the user's
     * java-preferences
     * 
     * @param data
     *            <code>SugiyamaData</code>-Bean, that currently holds the
     *            preferences (i.e. available phase-algorithms and stuff)
     */
    public static void savePreferences(SugiyamaData data) {
        Preferences prefs = Preferences
                .userNodeForPackage(org.graffiti.plugins.algorithms.sugiyama.Sugiyama.class);
        try {
            prefs.clear();
        } catch (BackingStoreException ex) {
            System.err.println("WARNING: Exception while trying to clear "
                    + "preference-node: " + ex.getMessage());
        }
        ArrayList<String[]> algos = FindPhaseAlgorithms.getPhaseAlgorithms(data
                .getPhaseAlgorithms());

        // save decycling-algorithms
        for (int i = 0; i < algos.get(0).length; i++) {
            prefs.put(SugiyamaConstants.KEY_PREFIX_DECYCLING + i,
                    algos.get(0)[i]);
        }
        // save levelling-algorithms
        for (int i = 0; i < algos.get(1).length; i++) {
            prefs.put(SugiyamaConstants.KEY_PREFIX_LEVELLING + i,
                    algos.get(1)[i]);
        }
        // save crossmin-algorithms
        for (int i = 0; i < algos.get(2).length; i++) {
            prefs.put(SugiyamaConstants.KEY_PREFIX_CROSSMIN + i,
                    algos.get(2)[i]);
        }
        // save layout-algorithms
        for (int i = 0; i < algos.get(3).length; i++) {
            prefs.put(SugiyamaConstants.KEY_PREFIX_LAYOUT + i, algos.get(3)[i]);
        }
        // save last selected algorithms
        if (data.getLastSelectedAlgorithms()[0] != null) {
            prefs.put(SugiyamaConstants.KEY_SELECTED_DECYCLING, data
                    .getLastSelectedAlgorithms()[0]);
        }
        if (data.getLastSelectedAlgorithms()[1] != null) {
            prefs.put(SugiyamaConstants.KEY_SELECTED_LEVELLING, data
                    .getLastSelectedAlgorithms()[1]);
        }
        if (data.getLastSelectedAlgorithms()[2] != null) {
            prefs.put(SugiyamaConstants.KEY_SELECTED_CROSSMIN, data
                    .getLastSelectedAlgorithms()[2]);
        }
        if (data.getLastSelectedAlgorithms()[3] != null) {
            prefs.put(SugiyamaConstants.KEY_SELECTED_LAYOUT, data
                    .getLastSelectedAlgorithms()[3]);
        }

        // save grid type
        if (data.getGridType() == null) {
            prefs.put(SugiyamaConstants.KEY_FRAMEWORK_GRID_TYPE,
                    SugiyamaConstants.PARAM_GRID_NONE);
        } else if (data.getGridType().getClass().isAssignableFrom(
                new OrthogonalGrid().getClass())) {
            prefs.put(SugiyamaConstants.KEY_FRAMEWORK_GRID_TYPE,
                    SugiyamaConstants.PARAM_GRID_ORTHOGONAL);
        } else if (data.getGridType().getClass().isAssignableFrom(
                new RadialGrid().getClass())) {
            prefs.put(SugiyamaConstants.KEY_FRAMEWORK_GRID_TYPE,
                    SugiyamaConstants.PARAM_GRID_RADIAL);
        } else {
            prefs.put(SugiyamaConstants.KEY_FRAMEWORK_GRID_TYPE,
                    SugiyamaConstants.PARAM_GRID_NONE);
        }

        // save constraints
        int counter = 0;
        Iterator<SugiyamaConstraint> constraints = data.getConstraints()
                .iterator();
        while (constraints.hasNext()) {
            prefs.put(SugiyamaConstants.PREFIX_CONSTRAINT + counter,
                    constraints.next().getClass().getName());
            counter++;
        }

        try {
            prefs.flush();
        } catch (BackingStoreException bse) {
            System.err.println("WARNING: Error while saving preferences: "
                    + bse.getMessage());
        }
    }

    /**
     * This method adds the two default constraints
     * "HorizontalConstraintWithTwoNodes" and "VerticalConstraintWithTwoNodes"
     * to the list of constraints, if they weren't loaded through the
     * preferences.
     */
    public static void validateConstraints(SugiyamaData data) {
        HashSet<SugiyamaConstraint> constraints = data.getConstraints();
        if (constraints.size() < 2) {
            constraints.clear();
            constraints.add(new HorizontalConstraintWithTwoNodes());
            constraints.add(new VerticalConstraintWithTwoNodes());
        }
    }

    /**
     * This method tests, if the algorithms stored in the
     * <code>SugiyamaData</code>-Bean can be loaded by a classloader. If an
     * algorithm cannot be loaded, it gets removed from the bean
     * 
     * @param data
     *            The <code>SugiyamaData</code>-Bean, that stores the algorithms
     *            to be checked.
     * @return Returns <code>null</code>, if a classloader can successfully load
     *         all algorithms, a <code>HashSet&gt;String&lt;</code> containing
     *         the binary-names of the algorithms that could not be loaded
     *         otherwise.
     */
    public static HashSet<String> validateAlgorithms(SugiyamaData data) {
        HashMap<String, String> algos = data.getPhaseAlgorithms();
        Iterator<String> algoIter = algos.keySet().iterator();
        HashSet<String> messages = new HashSet<String>();
        int errors = 0;
        String algorithm;

        while (algoIter.hasNext()) {
            algorithm = algoIter.next();
            try {
                Class.forName(algorithm);
            } catch (ClassNotFoundException cnfe) {
                messages.add(algorithm);
                errors++;
            }
        }
        if (errors != 0) {
            algoIter = messages.iterator();
            while (algoIter.hasNext()) {
                algorithm = algoIter.next();
                algos.remove(algos.get(algorithm));
            }
            data.setPhaseAlgorithms(algos);
            return messages;
        } else
            return null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
