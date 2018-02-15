// =============================================================================
//
//   KeyMap.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: KeyMap.java 6113 2012-04-12 14:46:20Z gleissner $

package org.graffiti.plugins.ios.exporters.graphml;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This class provides a mapping used for key declarations in a graphml-file.
 * These key declarations are generated considering all attributes the graph or
 * elements of it have. Note that the types of attributes at a given path are
 * not unique. Therefore the paths are mapped to lists of types while at the
 * same time numeric ids are generated which are unique for each pair of
 * attribute type and attribute path.
 * 
 * @author ruediger
 */
class KeyMap {

    /** Maps attribute paths to lists of types. */
    private HashMap<String, List<KeyData>> map;

    /** A counter used for generating ids. */
    private int count;

    /**
     * Constructs a new <code>KeyMap</code>.
     */
    KeyMap() {
        this.map = new HashMap<String, List<KeyData>>();
        this.count = -1;
    }

    /**
     * Returns an array containing all the paths that have been detected.
     * 
     * @return an array containing all the paths that have been detected.
     */
    String[] getPaths() {
        String[] paths = new String[this.map.size()];
        int i = -1;

        for (String string : map.keySet()) {
            paths[++i] = string;
        }

        return paths;
    }

    /**
     * Returns all the possible types an attribute at the given path can have
     * along with the corresponding id.
     * 
     * @param path
     *            the path for which to determine the possible types.
     * 
     * @return an array containing all the types along with the corresponding
     *         id.
     */
    KeyData[] getTypes(String path) {
        List<KeyData> l = this.map.get(path);

        if (l == null)
            return null;
        else {
            KeyData[] kd = new KeyData[l.size()];
            int i = -1;

            for (KeyData keyData : l) {
                kd[++i] = keyData;
            }

            return kd;
        }
    }

    /**
     * Adds a new path and the corresponding type to the mapping if it does not
     * exist there yet. Returns the id of the mapping if it already exists, if
     * not the id is generated before.
     * 
     * @param path
     *            the path of the attribute.
     * @param type
     *            the type of the attribute.
     * 
     * @return the id of the mapping that has been added.
     */
    int add(String path, Class<?> type) {
        if (path.startsWith(".")) {
            path = path.substring(1);
        }
        List<KeyData> list = this.map.get(path);

        // list of types for the given path is empty: create a new entry
        if (list == null) {
            List<KeyData> l = new LinkedList<KeyData>();
            l.add(new KeyData(new Integer(++count), type));
            this.map.put(path, l);

            return count;
        }

        // list of types for the given path is not empty: check whether the
        // path is already in the list and if not add it to the list and
        // generate a new id
        else {
            boolean notDetected = true;
            int id = -1;

            for (KeyData kd : list) {
                if (kd.getType().equals(type)) {
                    notDetected = false;
                    id = kd.getId();
                }
            }

            if (notDetected) {
                list.add(new KeyData(new Integer(++count), type));
                id = count;
            }

            assert id != -1 : "id not set correctly.";

            return id;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
