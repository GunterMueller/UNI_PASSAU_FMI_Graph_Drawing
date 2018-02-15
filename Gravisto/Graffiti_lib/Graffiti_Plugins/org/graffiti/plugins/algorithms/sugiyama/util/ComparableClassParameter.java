// =============================================================================
//
//   ClassParameter.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ComparableClassParameter.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.util;

/**
 * This class implements a Comparable. A ComparableClassParameter consists of
 * two strings: The class' BinaryName (this has the form full.package.classname)
 * and a textual description of the class. For example, this class has the
 * binary name
 * org.graffiti.plugins.algorithms.sugiyama.util.ComparableClassParameter
 * 
 * This Comparable is needed to sort classes alphabetically.
 * 
 * @author Ferdinand H�bner
 */
public class ComparableClassParameter implements
        Comparable<ComparableClassParameter> {
    public String binaryName;
    public String description;

    public ComparableClassParameter(String binaryName, String description) {
        this.binaryName = binaryName;
        this.description = description;
    }

    public int compareTo(ComparableClassParameter ccp) {
        return this.description.compareTo(ccp.description);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
