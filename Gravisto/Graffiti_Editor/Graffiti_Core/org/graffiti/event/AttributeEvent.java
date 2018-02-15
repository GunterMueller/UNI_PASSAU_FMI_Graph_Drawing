// =============================================================================
//
//   AttributeEvent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeEvent.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

import org.graffiti.attributes.Attribute;

/**
 * Contains an attribute event.
 * 
 * @version $Revision: 5767 $
 */
public class AttributeEvent extends AbstractEvent {
    /**
     * 
     */
    private static final long serialVersionUID = -5710830705401988630L;
    /** The path that has been assigned to the attribute by the event. */
    private String path;

    /**
     * Contructor that is called when one attribute is concerned.
     * 
     * @param attribute
     *            the attribute, which was altered.
     */
    public AttributeEvent(Attribute attribute) {
        super(attribute);
    }

    /**
     * Contructor that is called when one composite attribute is concerned,
     * where it is comfortable to pass the path of attribute, too.
     * 
     * @param path
     *            the path to the attribute that was altered.
     * @param attribute
     *            the attribute, which was altered.
     */
    public AttributeEvent(String path, Attribute attribute) {
        super(attribute);
        this.path = path;
    }

    /**
     * Returns the attribute that has been changed by this event.
     * 
     * @return the attribute that has been changed by this event.
     */
    public Attribute getAttribute() {
        return (Attribute) getSource();
    }

    /**
     * Returns the path to the attribute that has been changed by this event.
     * 
     * @return the path to the attribute that has been changed by this event.
     */
    public String getPath() {
        return path;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
