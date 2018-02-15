// =============================================================================
//
//   Zone.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Zone.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.springembedderFR;

/**
 * Zone surrounds a node, with a circle. This circle is splitted in Sectors.
 * 
 * @author matzeder
 */
public class Zone {
    /**
     * An array, where the radius of the sectors is saved.
     */
    private double[] sectors;

    /**
     * Covers the complete zone
     */
    public Zone(int nrSectors) {

        // initialize the array with nrSectors sectors of a zone
        this.sectors = new double[nrSectors];

        for (int i = 0; i < sectors.length; i++) {
            sectors[i] = Double.MAX_VALUE;
        }
    }

    /**
     * Output of a Zone
     */
    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < sectors.length; i++) {
            s += "Radius of sector" + i + ": " + sectors[i] + "\t";

        }
        return s;
    }

    /**
     * Sets the radius of the specified sector.
     * 
     * @param sector
     *            The sector, where to change radius.
     * @param radius
     *            The value of the new radius of the sector.
     */
    public void setRadiusOfSector(int sector, double radius) {
        sectors[getSectorPosition(sector)] = radius;
    }

    /**
     * Returns the radius of the given sector.
     * 
     * @param i
     *            The given sector.
     * @return Returns the radius of the sector
     */
    public double getSectorRadius(int i) {
        return sectors[getSectorPosition(i)];
    }

    /**
     * Returns the sector position. If the given value is bigger than 8 then,
     * return value - 8 (value smaller than 0 then analogous).
     * 
     * @param value
     *            The given value, to get the correct sector position in array.
     * @return The transformed sector position.
     */
    public int getSectorPosition(int value) {
        if (value < 0) {
            value += 8;
        } else if (value >= 8) {
            value -= 8;
        }
        return value;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
