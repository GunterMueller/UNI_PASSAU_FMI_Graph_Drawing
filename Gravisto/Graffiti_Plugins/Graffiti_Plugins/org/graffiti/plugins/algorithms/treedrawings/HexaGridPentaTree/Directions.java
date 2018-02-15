// =============================================================================
//
//   Directions.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;
/**
 * Jedesmal wenn man in einer Richtung sich "translieren" muss oder eine
 * "Rotation" darstellen muss, werden die Directions benutzt 
 */

public enum Directions
{

    STRAIGHT, STRAIGHT_RIGHT30, STRAIGHT_RIGHT, NORTH, BACK_RIGHT, BACK_RIGHT30, 
    BACK, BACK_LEFT30, BACK_LEFT, SOUTH, STRAIGHT_LEFT, STRAIGHT_LEFT30;    
    //6 moegliche Richtungen parallel  zu den Achsen und 6 mehr "dazwischen"    
    ;

    /* Prueft ob first parallel zu last ist
     */
    public static boolean parallel(Directions first, Directions last) {
        return ((first == last) || (Directions.reverse(first) == last));
    }

    /**
     * Gibt die entgegengesetzte Richtung zurueck
     */
    public static Directions reverse(Directions dire) {
        return intToDirections((dire.ordinal() + 6) % 12);
    }

    /**
     *  Gibt die um 90% im Uhrzeigersinn rotierte Richtung zurueck
     */
    public static Directions perpendicular(Directions dire) {
        return intToDirections((dire.ordinal() + 3) % 12);
    }

    /**
     * Addiert rechts und links Richtung zusammen
     */
    public static Directions add(Directions rechts, Directions links) {
        return intToDirections((rechts.ordinal() + links.ordinal()) % 12);
    }

    /**
     * Subtrahiert links von rechts
     */
    public static Directions subtract(Directions rechts, Directions links) {
        
        int diff = Math.abs(12 - rechts.ordinal());
        int newDire2 = (links.ordinal() + diff) % 12;
        if (newDire2 == 8) {
            newDire2 = 4;
        }
        if (newDire2 == 10) {
            newDire2 = 2;
        }
        return intToDirections(newDire2);       
    }

    /**
     * Bildet die Zahlen von 0..11 auf die 12 möglichen Richtungen
     */
    public static Directions intToDirections(int i){
        switch (i) {
            case 0: return STRAIGHT; 
            case 1: return STRAIGHT_RIGHT30;
            case 2: return STRAIGHT_RIGHT;
            case 3: return NORTH;
            case 4: return BACK_RIGHT;
            case 5: return BACK_RIGHT30;            
            case 6: return BACK;
            case 7: return BACK_LEFT30;
            case 8: return BACK_LEFT;
            case 9: return SOUTH;
            case 10: return STRAIGHT_LEFT;
            case 11: return STRAIGHT_LEFT30;
        }
        throw new UnsupportedOperationException("invalid direction was used");
    }

    /**
     * Wenn man sich in einer Richtung translieren will, wird hier ein 
     * entsprechender Richtungsvektor zurückgegeben
     */
    public static Vector toVector(Directions directions) {
        Vector vector = new Vector();
        switch(directions) {
            case STRAIGHT:
                vector = new Vector(-1,0); break;
            case STRAIGHT_RIGHT30:
                vector = new Vector(-1,-0.5); break;
            case STRAIGHT_RIGHT:
                vector = new Vector(-1,-1); break;
            case NORTH:
                vector = new Vector(-0.5,-1); break;
            case BACK_RIGHT:
                vector = new Vector(0,-1); break;
            case BACK_RIGHT30:
                vector = new Vector(0.5,-0.5); break;
            case BACK:
                vector = new Vector(1,0); break;
            case BACK_LEFT30:
                vector = new Vector(1,0.5); 
                break;
            case BACK_LEFT:
                vector = new Vector(1,1); break;
            case SOUTH:
                vector = new Vector(0.5, 1); break;
            case STRAIGHT_LEFT:
                vector = new Vector(0,1); break;
            case STRAIGHT_LEFT30:
                vector = new Vector(-0.5,0.5); break;
        }
        return vector;
    }
}