// =============================================================================
//
//   AttributeMapping.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeMapping.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml.attributemapping;

/**
 * This interface defines a mapping from GML key paths to Gravisto attribute
 * paths and vice versa. We have to consider whole paths in order to take into
 * account the fact that a key may appear at different places (i.e. namespaces).
 * 
 * @author ruediger
 */
public interface AttributeMapping {

    /**
     * Returns the GML key path corresponding to the given Gravisto attribute
     * path. If there is no mapping, <code>null</code> will be returned.
     * 
     * @param gravistoPath
     *            the Gravisto attribute path.
     * 
     * @return the corresponding GML key path.
     */
    public String getGMLPath(String gravistoPath);

    /**
     * Returns the Gravisto attribute path corresponding to the given GML key
     * path. If there is no mapping, <code>null</code> will be returned.
     * 
     * @param gmlPath
     *            the GML key path.
     * 
     * @return the corresponding Gravisto path.
     */
    public String getGravistoPath(String gmlPath);

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
    public boolean isIgnorableGML(String gmlPath);

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
    public boolean isIgnorableGravisto(String gravistoPath);

    /**
     * Adds a GML attribute path that is ignorable when writing a GML file.
     * 
     * @param gmlPath
     *            the GML attribute path to be added.
     */
    public void addIgnorableGML(String gmlPath);

    /**
     * Adds a Gravisto attribute path that is ignorable.
     * 
     * @param gravistoPath
     *            the Gravisto attribute path to be added.
     */
    public void addIgnorableGravisto(String gravistoPath);

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
     *            indicates whether special treatment is required upon a
     *            transformation.
     */
    public void addMapping(String gmlPath, String gravistoPath,
            boolean requiresSpecialTreatment);

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
    public boolean requiresSpecialTreatment(String gmlPath);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
