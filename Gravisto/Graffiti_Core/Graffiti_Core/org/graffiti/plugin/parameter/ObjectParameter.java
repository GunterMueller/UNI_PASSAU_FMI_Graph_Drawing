// =============================================================================
//
//   ObjectParameter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ObjectParameter.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.plugin.parameter;

/**
 * Parameter that contains an <code>Integer</code> value.
 * 
 * @version $Revision: 5767 $
 */
public class ObjectParameter extends AbstractSingleParameter<Object> {

    /**
     * 
     */
    private static final long serialVersionUID = 3558349561893823243L;

    /**
     * Constructs a new integer parameter.
     * 
     * @param value
     *            DOCUMENT ME!
     * @param name
     *            DOCUMENT ME!
     * @param description
     *            DOCUMENT ME!
     */
    public ObjectParameter(Object value, String name, String description) {
        super(value, name, description);
    }

    /**
     * Constructs a new integer parameter.
     * 
     * @param name
     *            DOCUMENT ME!
     * @param description
     *            DOCUMENT ME!
     */
    public ObjectParameter(String name, String description) {
        super(name, description);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
