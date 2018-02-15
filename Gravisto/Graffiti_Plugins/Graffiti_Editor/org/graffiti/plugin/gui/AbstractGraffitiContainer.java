// =============================================================================
//
//   AbstractGraffitiContainer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractGraffitiContainer.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.gui;

/**
 * Abstract class for default containers.
 * 
 * @version $Revision: 5768 $
 */
public abstract class AbstractGraffitiContainer extends
        AbstractGraffitiComponent implements GraffitiContainer {

    /**
     * 
     */
    private static final long serialVersionUID = 3645124966759621427L;
    /** The id of the <code>AbstractGraffitiContainer</code>. */
    protected String id;

    /**
     * Constructs a new <code>AbstractGraffitiContainer</code>.
     */
    protected AbstractGraffitiContainer() {
        super();
    }

    /**
     * Constructs a new <code>AbstractGraffitiContainer</code>.
     * 
     * @param id
     *            DOCUMENT ME!
     * @param prefComp
     *            DOCUMENT ME!
     */
    protected AbstractGraffitiContainer(String id, String prefComp) {
        super(prefComp);
        this.id = id;
    }

    /**
     * Returns an unique identifier for this <code>GraffitiContainer</code>.
     * 
     * @return an unique identifier for this <code>GraffitiContainer</code>.
     */
    public String getId() {
        return this.id;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
