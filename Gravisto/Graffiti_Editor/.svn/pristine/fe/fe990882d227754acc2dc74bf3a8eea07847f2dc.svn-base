package org.graffiti.plugins.algorithms.kandinsky;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class is used for calculating the coordinates of the nodes. A Bar
 * contains the nodes with the same x-/y-coordiante.
 */
public class Bar {
    /**
     * The label of the reference NormNode.
     */
    private String label = "";

    /** X-coordinate of the node. */
    private int x = 0;

    /** Y-coordinate of the node. */
    private int y = 0;

    /** True, if this bar contains nodes with the same x-coordinate. */
    private boolean isX;

    /** Set of NormNodes with the same x-/y-coordinate. */
    private HashSet<NormNode> nodes;

    /** List of adjacent bars which are reached by UP-NormArcs. */
    private LinkedList<Bar> listUpArcs;

    /** Set of adjacent bars which are reached by DOWN-NormArcs. */
    private LinkedList<Bar> listDownArcs;

    /** Set of adjacent bars which are reached by RIGHT-NormArcs. */
    private LinkedList<Bar> listRightArcs;

    /** List of adjacent bars which are reached by LEFT-NormArcs. */
    private LinkedList<Bar> listLeftArcs;

    /**
     * Creates a bar for the nodes with the same x-/y-coordinates of the
     * normalized orthogonal representation.
     * 
     * @param label
     *            The label of the first NormNode.
     * @param isX
     *            True, if this bar contains nodes with the same x-coordinate;
     *            False, if the bar contains nodes with the same y-coordinate.
     */
    public Bar(String label, boolean isX) {
        this.label = label;
        this.isX = isX;
        nodes = new HashSet<NormNode>();
        listUpArcs = new LinkedList<Bar>();
        listDownArcs = new LinkedList<Bar>();
        listRightArcs = new LinkedList<Bar>();
        listLeftArcs = new LinkedList<Bar>();
    }

    /**
     * Returns the label of the bar.
     * 
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label of the bar.
     * 
     * @param label
     *            The label to set.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Adds a NormNode to the Bar.
     * 
     * @param node
     *            the NormNode with the same coordinate to add.
     */
    public void addNode(NormNode node) {
        // Wenn es sich um eine Bar f�r x-Koordinaten handelt
        if (isX) {
            // Knoten noch nicht enthalten
            if (!this.nodes.contains(node)) {
                this.nodes.add(node);
                // Einf�gen der Knoten, die durch auf- oder abw�rts gerichtete
                // Kanten verbunden sind, da diselbe x-Koordinate
                for (NormArc a : node.getUpArcs()) {
                    addNode(a.getTo());
                }
                for (NormArc a : node.getDownArcs()) {
                    addNode(a.getTo());
                }
                // Weise dem Knoten dieses x-Bar zu
                node.setXBar(this);
            }
        } else {
            // Wenn es sich um eine Bar f�r y-Koordinaten handelt
            // Knoten noch nicht enthalten
            if (!this.nodes.contains(node)) {
                this.nodes.add(node);
                // Einf�gen der Knoten, die durch links- oder rechts gerichtete
                // Kanten verbunden sind, da diselbe y-Koordinate
                for (NormArc a : node.getRightArcs()) {
                    addNode(a.getTo());
                }
                for (NormArc a : node.getLeftArcs()) {
                    addNode(a.getTo());
                }
                // Weise dem Knoten dieses y-Bar zu
                node.setYBar(this);
            }
        }
    }

    /**
     * Creates the list of Bars which are above this bar.
     */
    protected void addUpArcs() {
        for (NormNode n : nodes) {
            // f�gt alle Bars ein, die �ber dieser hier liegen
            for (NormArc a : n.getUpArcs()) {
                if (a.getTo().getYBar() != this) {
                    if (a.getTo().getYBar() == null) {
                        System.out.println("In Bar " + label + ": Knoten "
                                + a.getTo() + " ohne Bar"); // Fehlerfall
                    }
                    this.listUpArcs.add(a.getTo().getYBar());
                }
            }
        }
    }

    /**
     * Creates the list of Bars which are under this bar.
     */
    protected void addDownArcs() {
        for (NormNode n : nodes) {
            // f�gt alle Bars ein, die unter dieser hier liegen
            for (NormArc a : n.getDownArcs()) {
                if (a.getTo().getYBar() != this) {
                    if (a.getTo().getYBar() == null) {
                        System.out.println("In Bar " + label + ": Knoten "
                                + a.getTo() + " ohne Bar"); // Fehlerfall
                    }
                    this.listDownArcs.add(a.getTo().getYBar());
                }
            }
        }
    }

    /**
     * Creates the list of Bars which are on the right side of this bar.
     */
    protected void addRightArcs() {
        for (NormNode n : nodes) {
            // f�gt alle Bars ein, die rechts von dieser hier liegen
            for (NormArc a : n.getRightArcs()) {
                if (a.getTo().getXBar() != this) {
                    if (a.getTo().getXBar() == null) // Fehlerfall
                    {
                        System.out.println("In Bar " + label + ": Knoten "
                                + a.getTo() + " ohne Bar");
                    }
                    this.listRightArcs.add(a.getTo().getXBar());
                }
            }
        }
    }

    /**
     * Creates the list of Bars which are on the left side of this bar.
     */
    protected void addLeftArcs() {
        for (NormNode n : nodes) {
            // f�gt alle Bars ein, die links von dieser hier liegen
            for (NormArc a : n.getLeftArcs()) {
                if (a.getTo().getXBar() != this) {
                    if (a.getTo().getXBar() == null) // Fehler
                    {
                        System.out.println("In Bar " + label + ": Knoten "
                                + a.getTo() + " ohne Bar");
                    }
                    this.listLeftArcs.add(a.getTo().getXBar());
                }
            }
        }
    }

    /**
     * Gets the set of adjacent Bars on the upper side.
     */
    public LinkedList<Bar> getUpBars() {
        return listUpArcs;
    }

    /**
     * Gets the list of adjacent Bars on the lower side.
     */
    public LinkedList<Bar> getDownBars() {
        return listDownArcs;
    }

    /**
     * Gets the list of adjacent Bars on the right side.
     */
    public LinkedList<Bar> getRightBars() {
        return listRightArcs;
    }

    /**
     * Gets the set of adjacent Bars on the left side.
     */
    public LinkedList<Bar> getLeftBars() {
        return listLeftArcs;
    }

    /** Sets the coordinates for every NormNode which belongs to this bar. */
    public void setCoordinates(int coord) {
        if (isX) {
            this.x = coord;
            for (NormNode node : nodes) {
                node.setX(coord);
            }
        } else {
            this.y = coord;
            for (NormNode node : nodes) {
                node.setY(coord);
            }
        }
    }

    /**
     * Returns the relative x-coordinate of this bar.
     * 
     * @return the x-coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the relative y-coordinate of this bar.
     * 
     * @return the y-coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Returns an iterator of the NormNodes of this Bar.
     * 
     * @return the iterator of the nodes.
     */
    public Iterator<NormNode> getNodes() {
        return nodes.iterator();
    }

    /**
     * Removes the Arcs.
     */
    /*
     * Wenn in CompactRepresentation ein Bar keine Kanten nach links/oben hat,
     * bekommt es die n�chste Koordinate zugewiesen. Dann m�ssen alle von diesem
     * aus nach rechts/unten gehenden Kanten gel�scht werden. Und
     * dementsprechend mu� dieses Bar in den Bars, zu denen diese Kanten gehen,
     * entfernt werden.
     */
    public void removeArcs() {
        if (isX) {
            // keine Kanten nach links mehr vorhanden
            this.listLeftArcs = new LinkedList<Bar>();
            // l�sche alle Kanten nach rechts komplett
            for (Bar bar : listRightArcs) {
                bar.removeLeftArc(this);
            }
            this.listRightArcs = new LinkedList<Bar>();
        } else {
            // keine Kanten nach oben mehr vorhanden
            this.listUpArcs = new LinkedList<Bar>();
            // l�sche alle Kanten nach unten komplett
            for (Bar bar : listDownArcs) {
                bar.removeUpArc(this);
            }
            this.listDownArcs = new LinkedList<Bar>();
        }
    }

    /**
     * Removes a Bar from the set of adjacent Bars.
     */
    private void removeLeftArc(Bar bar) {
        listLeftArcs.remove(bar);
    }

    /**
     * Removes a Bar from the set of adjacent Bars.
     */
    private void removeUpArc(Bar bar) {
        listUpArcs.remove(bar);
    }
}
