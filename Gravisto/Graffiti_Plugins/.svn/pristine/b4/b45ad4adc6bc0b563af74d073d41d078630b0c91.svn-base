// =============================================================================
//
//   Point2DSlot.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive.slots;

import java.awt.geom.Point2D;
import java.util.prefs.Preferences;

import org.graffiti.plugin.view.interactive.Slot;

/**
 * Slot of type {@code Point2D}, which can store its value in a preferences
 * tree.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Point2DSlot extends Slot<Point2D> {
    /**
     * The default value of this slot.
     */
    private Point2D defaultValue;

    /**
     * Constructs a {@code Point2DSlot}.
     * 
     * @param id
     *            the id of the slot.
     * @param name
     *            the name of the slot as seen by the user when graphically
     *            editing the tools.
     * @param description
     *            the description as seen by the user when graphically editing
     *            the tools.
     * @param defaultValue
     *            the default value.
     */
    public Point2DSlot(String id, String name, String description,
            Point2D defaultValue) {
        super(id, name, Point2D.class, description);
        this.defaultValue = defaultValue;
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation stores its default value in the preferences tree at
     * the keys {@code "valueX"} and {@code "valueY"}.
     */
    @Override
    public void createDefaultPreferences(Preferences preferences) {
        preferences.putDouble("valueX", defaultValue.getX());
        preferences.putDouble("valueY", defaultValue.getY());
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation loads its value from the preferences tree at the keys
     * {@code "valueX"} and {@code "valueY"}.
     */
    @Override
    public Point2D loadValue(Preferences preferences) {
        return new Point2D.Double(preferences.getDouble("valueX", defaultValue
                .getX()), preferences.getDouble("valueY", defaultValue.getY()));
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation stores the specified value in the preferences tree at
     * the keys {@code "valueX"} and {@code "valueY"}.
     */
    @Override
    public void saveValue(Preferences preferences, Point2D value) {
        preferences.putDouble("valueX", value.getX());
        preferences.putDouble("valueY", value.getY());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
