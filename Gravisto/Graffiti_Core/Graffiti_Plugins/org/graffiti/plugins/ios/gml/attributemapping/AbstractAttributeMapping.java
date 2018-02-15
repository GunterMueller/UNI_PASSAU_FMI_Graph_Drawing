// =============================================================================
//
//   AbstractAttributeMapping.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractAttributeMapping.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.attributemapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This class provides an abstract implementation of a mapping from GML keys
 * paths to Gravisto attribute paths and vice versa. We have to consider whole
 * paths in order to take into account the fact that a key may appear at
 * different places (namespaces). Two maps are provided which contain the two
 * mappings. Therefore upon creation there is some overhead creating the second
 * mapping from the first. However, this will only have to be done once. Getting
 * the corresponding path then only takes O(1) in both cases.
 * 
 * @author ruediger
 */
public abstract class AbstractAttributeMapping implements AttributeMapping {

    /** The collection of GML ignorable attributes. */
    private HashSet<String> gmlIgnorables;

    /** The collection of Gravisto ignorable attributes. */
    private HashSet<String> gravistoIgnorables;

    /** Maps Gml keys paths to Gravisto attribute paths. */
    private Map<String, CorrespondingPath> gml2gravistoMap;

    /** Maps Gravisto attribute paths to Gml keys paths. */
    private Map<String, CorrespondingPath> gravisto2gmlMap;

    /**
     * Constructs a new <code>AbstractAttributeMapping</code>.
     */
    public AbstractAttributeMapping() {
        this.gml2gravistoMap = new HashMap<String, CorrespondingPath>();
        this.gravisto2gmlMap = new HashMap<String, CorrespondingPath>();
        this.gmlIgnorables = new HashSet<String>();
        this.gravistoIgnorables = new HashSet<String>();
    }

    /**
     * Returns the GML key path corresponding to the given Gravisto attribute
     * path. If there is no mapping, <code>null</code> will be returned.
     * 
     * @param gravistoPath
     *            the Gravisto attribute path.
     * 
     * @return the corresponding GML key path.
     */
    public String getGMLPath(String gravistoPath) {
        CorrespondingPath cp = gravisto2gmlMap.get(gravistoPath);

        if (cp == null)
            return null;
        else
            return cp.getPath();
    }

    /**
     * Returns the Gravisto attribute path corresponding to the given GML key
     * path. If there is no mapping, <code>null</code> will be returned.
     * 
     * @param gmlPath
     *            the GML key path.
     * 
     * @return the corresponding Gravisto path.
     */
    public String getGravistoPath(String gmlPath) {
        CorrespondingPath cp = gml2gravistoMap.get(gmlPath);

        if (cp == null)
            return null;
        else
            return cp.getPath();
    }

    /**
     * Returns <code>true</code> if the attribute at the specified GML attribute
     * path is ignorable, <code>false</code> otherwise.
     * 
     * @param gmlPath
     *            the GML attribute path for which to check whether the
     *            attribute is ignorable.
     * 
     * @return <code>true</code> if the attribute at the specified GML attribute
     *         path is ignorable, <code>false</code> otherwise.
     */
    public boolean isIgnorableGML(String gmlPath) {
        return gmlIgnorables.contains(gmlPath);
    }

    /**
     * Returns <code>true</code> if the attribute at the specified Gravisto
     * attribute path is ignorable, <code>false</code> otherwise.
     * 
     * @param gravistoPath
     *            the Gravisto attribute path for which to check whether the
     *            attribute is ignorable.
     * 
     * @return <code>true</code> if the attribute at the specified Gravisto
     *         attribute path is ignorable, <code>false</code> otherwise.
     */
    public boolean isIgnorableGravisto(String gravistoPath) {
        return gravistoIgnorables.contains(gravistoPath);
    }

    /**
     * Adds a GML attribute path that is ignorable when writing a GML file.
     * 
     * @param gmlPath
     *            GML attribute path to be added.
     */
    public void addIgnorableGML(String gmlPath) {
        this.gmlIgnorables.add(gmlPath);
    }

    /**
     * Adds a Gravisto attribute path that is ignorable.
     * 
     * @param gravistoPath
     *            the Gravisto attribute path to be added.
     */
    public void addIgnorableGravisto(String gravistoPath) {
        this.gravistoIgnorables.add(gravistoPath);
    }

    /**
     * Adds a mapping from a GML key path to a Gravisto attribute path
     * indicating whether a transformation requires some extra work as opposed
     * to just changing the path.
     * 
     * @param gmlPath
     *            the GML key path.
     * @param gravistoPath
     *            the Gravisto attribute path.
     * @param requiresSpecialTreatment
     *            the transformation flag.
     */
    public void addMapping(String gmlPath, String gravistoPath,
            boolean requiresSpecialTreatment) {
        this.gml2gravistoMap.put(gmlPath, new CorrespondingPath(gravistoPath,
                requiresSpecialTreatment));
        this.gravisto2gmlMap.put(gravistoPath, new CorrespondingPath(gmlPath,
                requiresSpecialTreatment));
    }

    /**
     * Indicates whether some specific transformation work needs to be done when
     * transforming from one representation to the other.
     * 
     * @param gmlPath
     *            the GML key path for which to check the necessity of extra
     *            work.
     * 
     * @return <code>true</code> some specific transformation work needs to be
     *         done when transforming from one representation to the other,
     *         <code>false</code> otherwise.
     */
    public boolean requiresSpecialTreatment(String gmlPath) {
        CorrespondingPath cp = gml2gravistoMap.get(gmlPath);

        if (cp == null)
            return false;
        else
            return cp.getSpecialTreatment();
    }

    /**
     * Class <code>CorrespondingPath</code> is a wrapper class for storing a
     * path corrsponding to another one along with a flag indicating the special
     * treatment requirement for transformations.
     * 
     * @author ruediger
     */
    static class CorrespondingPath {
        /** The corresponding path. */
        private String path;

        /**
         * Indicates whether the transformation requires some special treatment.
         */
        private boolean specialTreatment;

        /**
         * Creates a new <code>CorrespondingAttribute</code> from a given path
         * and the corresponding transformation flag.
         * 
         * @param path
         *            the corresponding path.
         * @param specialTreatment
         *            the flag for the transformation.
         */
        CorrespondingPath(String path, boolean specialTreatment) {
            this.path = path;
            this.specialTreatment = specialTreatment;
        }

        /**
         * Returns the path.
         * 
         * @return the path.
         */
        String getPath() {
            return this.path;
        }

        /**
         * Returns the transformation flag.
         * 
         * @return the transformation flag.
         */
        boolean getSpecialTreatment() {
            return this.specialTreatment;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
