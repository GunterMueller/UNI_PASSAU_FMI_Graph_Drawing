package org.graffiti.plugins.algorithms.kandinsky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import org.graffiti.plugins.algorithms.kandinsky.NormArc.Status;

/**
 * This class contains the nodes which are used to compute a normalized
 * representation of the orthogonal represenation. It stores the outgoing
 * NormArcs depending of their direction.
 * 
 * @author Sonja
 * @version $Revision$ $Date$
 */
public class NormNode {

    /**
     * The new label on edges which arise as result of this algorithm.
     */
    private String label = "";

    /** The <code>MCMFNode</Code>. */
    private GraphNode element;

    /** X-coordinate of the node. */
    private int x = 0;

    /** Y-coordinate of the node. */
    private int y = 0;

    /** List of adjacent UP-NormArcs of a NormNode. */
    private LinkedList<NormArc> listUpArcs;

    /** List of adjacent DOWN-NormArcs of a node. */
    private LinkedList<NormArc> listDownArcs;

    /** List of adjacent RIGHT-NormArcs of a node. */
    private LinkedList<NormArc> listRightArcs;

    /** List of adjacent LEFT-NormArcs of a node. */
    private LinkedList<NormArc> listLeftArcs;

    /** List of adjacent ingoing NormArcs of a node. */
    private LinkedList<NormArc> listInArcs;

    /** List of adjacent ingoing NormArcs of a node. */
    private LinkedList<NormArc> listOutArcs;

    /** Is true, if the node is a dummy node. */
    private boolean isDummy = false;

    /** The Bar the NormNode with the same x-coordinate belongs to. */
    private Bar xBar = null;

    /** The Bar the NormNode with the same y-coordinate belongs to. */
    private Bar yBar = null;

    /** Left part of the upper ports. */
    // linker Teil der Ports oben
    protected ArrayList<NormArc> top_one = null;

    /** Right part of the upper ports. */
    protected ArrayList<NormArc> top_two = null; // rechts

    /** Left part of the lower ports. */
    protected ArrayList<NormArc> bottom_one = null; // li Teil der Ports unten

    /** Right part of the lower ports. */
    protected ArrayList<NormArc> bottom_two = null; // rechts

    /** Upper part of the left ports. */
    // oberer Teil der Ports links
    protected ArrayList<NormArc> left_one = null;

    /** Lower part of the left ports. */
    protected ArrayList<NormArc> left_two = null; // unten

    /** Upper part of the right ports. */
    protected ArrayList<NormArc> right_one = null; // oberer Teil der Ports re

    /** Lower part of the right ports. */
    protected ArrayList<NormArc> right_two = null; // unten

    /**
     * Creates a node for the normalized orthogonal representation for each
     * GraphNode.
     * 
     * @param node
     *            The GraphNode.
     */
    public NormNode(GraphNode node) {
        this.label = node.getLabel();
        listUpArcs = new LinkedList<NormArc>();
        listDownArcs = new LinkedList<NormArc>();
        listRightArcs = new LinkedList<NormArc>();
        listLeftArcs = new LinkedList<NormArc>();
        listInArcs = new LinkedList<NormArc>();
        listOutArcs = new LinkedList<NormArc>();
        top_one = new ArrayList<NormArc>();
        top_two = new ArrayList<NormArc>();
        bottom_one = new ArrayList<NormArc>();
        bottom_two = new ArrayList<NormArc>();
        left_one = new ArrayList<NormArc>();
        left_two = new ArrayList<NormArc>();
        right_one = new ArrayList<NormArc>();
        right_two = new ArrayList<NormArc>();
        this.element = node;
        this.isDummy = false;
    }

    /**
     * Creates a dummy node for the normalized orthogonal representation for
     * example for each BendNode.
     * 
     * @param label
     *            Label of node.
     */
    public NormNode(String label) {
        this.label = label;
        listUpArcs = new LinkedList<NormArc>();
        listDownArcs = new LinkedList<NormArc>();
        listRightArcs = new LinkedList<NormArc>();
        listLeftArcs = new LinkedList<NormArc>();
        listInArcs = new LinkedList<NormArc>();
        listOutArcs = new LinkedList<NormArc>();
        top_one = new ArrayList<NormArc>();
        top_two = new ArrayList<NormArc>();
        bottom_one = new ArrayList<NormArc>();
        bottom_two = new ArrayList<NormArc>();
        left_one = new ArrayList<NormArc>();
        left_two = new ArrayList<NormArc>();
        right_one = new ArrayList<NormArc>();
        right_two = new ArrayList<NormArc>();
        this.element = null;
        this.isDummy = true;
    }

    /**
     * Returns the label of the node.
     * 
     * @return Returns the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Adds a NormArcs to the list of the UP-NormArcs.
     * 
     * @param edge
     *            the adjacent NormArc to add.
     */
    protected void addUpArc(NormArc edge) {
        this.listUpArcs.add(edge);
    }

    /**
     * Adds a NormArcs to the list of the DOWN-NormArcs.
     * 
     * @param edge
     *            the adjacent NormArc to add.
     */
    protected void addDownArc(NormArc edge) {
        this.listDownArcs.add(edge);
    }

    /**
     * Adds a NormArcs to the list of the RIGHT-NormArcs.
     * 
     * @param edge
     *            the adjacent NormArc to add.
     */
    protected void addRightArc(NormArc edge) {
        this.listRightArcs.add(edge);
    }

    /**
     * Adds a NormArc to the list of the LEFT-NormArcs.
     * 
     * @param edge
     *            the adjacent NormArc to add.
     */
    protected void addLeftArc(NormArc edge) {
        this.listLeftArcs.add(edge);
    }

    /**
     * Sets the list of the adjacent ingoing NormArcs.
     * 
     * @param edge
     *            the adjacent ingoing NormArc to set.
     */
    public void addInArc(NormArc edge) {
        this.listInArcs.add(edge);
    }

    /**
     * Adds an outgoing NormArc to its status list.
     * 
     * @param edge
     *            the adjacent outgoing NormArc to set.
     */
    public void addStatusArc(NormArc edge) {
        if (edge.getStatus() == Status.UP) {
            addUpArc(edge);
        }
        if (edge.getStatus() == Status.DOWN) {
            addDownArc(edge);
        }
        if (edge.getStatus() == Status.RIGHT) {
            addRightArc(edge);
        }
        if (edge.getStatus() == Status.LEFT) {
            addLeftArc(edge);
        }
    }

    /**
     * Sets the list of the adjacent outgoing NormArcs.
     * 
     * @param edge
     *            the adjacent outgoing NormArc to set.
     */
    public void addOutArc(NormArc edge) {
        this.listOutArcs.add(edge);
    }

    /**
     * Gets the list of the adjacent out-going NormArcs.
     */
    public LinkedList<NormArc> getOutArcs() {
        return listOutArcs;
    }

    /**
     * Gets the list of the adjacent in-going NormArcs.
     */
    public LinkedList<NormArc> getInArcs() {
        return listInArcs;
    }

    /**
     * Removes an outgoing NormArc out of its status list.
     * 
     * @param edge
     *            the adjacent outgoing NormArc to remove.
     */
    public void removeStatusArc(NormArc edge) {
        if (edge.getStatus() == Status.UP) {
            removeUpArc(edge);
        }
        if (edge.getStatus() == Status.DOWN) {
            removeDownArc(edge);
        }
        if (edge.getStatus() == Status.RIGHT) {
            removeRightArc(edge);
        }
        if (edge.getStatus() == Status.LEFT) {
            removeLeftArc(edge);
        }
    }

    /**
     * Removes a NormArcs from the list of UP-NormArcs.
     * 
     * @param edge
     *            the adjacent NormArc to remove.
     */
    private void removeUpArc(NormArc edge) {
        this.listUpArcs.remove(edge);
    }

    /**
     * Removes a NormArcs from the list of DOWN-NormArcs.
     * 
     * @param edge
     *            the adjacent NormArc to remove.
     */
    private void removeDownArc(NormArc edge) {
        this.listDownArcs.remove(edge);
    }

    /**
     * Removes a NormArc from the list of LEFT-NormArcs.
     * 
     * @param edge
     *            the adjacent NormArc to remove.
     */
    private void removeLeftArc(NormArc edge) {
        this.listLeftArcs.remove(edge);
    }

    /**
     * Removes a NormArcs from the list of RIGHT-NormArcs.
     * 
     * @param edge
     *            the adjacent NormArc to remove.
     */
    private void removeRightArc(NormArc edge) {
        this.listRightArcs.remove(edge);
    }

    /**
     * Gets the number of NormArcs on this side of the NormNode.
     */
    public int getNumberOfArcsOnThisSide(Status status) {
        if (status == Status.DOWN)
            return listDownArcs.size();
        if (status == Status.UP)
            return listUpArcs.size();
        if (status == Status.LEFT)
            return listLeftArcs.size();
        if (status == Status.RIGHT)
            return listRightArcs.size();
        return 0;
    }

    /**
     * Gets the list of the adjacent UP-NormArcs.
     */
    public LinkedList<NormArc> getUpArcs() {
        return listUpArcs;
    }

    /**
     * Gets the list of the adjacent DOWN-NormArcs.
     */
    public LinkedList<NormArc> getDownArcs() {
        return listDownArcs;
    }

    /**
     * Gets the list of the adjacent RIGHT-NormArcs.
     */
    public LinkedList<NormArc> getRightArcs() {
        return listRightArcs;
    }

    /**
     * Gets the list of the adjacent LEFT-NormArcs.
     */
    public LinkedList<NormArc> getLeftArcs() {
        return listLeftArcs;
    }

    /**
     * Returns the <code>GraphNode</code> for which the NormNode was
     * constructed.
     * 
     * @return the element.
     */
    public GraphNode getElement() {
        return element;
    }

    /**
     * Returns true, if the node of the normalized representation is a dummy
     * node.
     * 
     * @return if the node is a dummy node.
     */
    public boolean isDummy() {
        return isDummy;
    }

    /**
     * Returns the relative x-coordinate of the node.
     * 
     * @return the x.
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the relative x-coordinate of the node.
     * 
     * @param x
     *            the x to set.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Returns the relative y-coordinate of the node.
     * 
     * @return the y.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the relative y-coordinate of the node.
     * 
     * @param y
     *            the y to set.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Returns the x-Bar to which the node belongs to.
     * 
     * @return the x-Bar.
     */
    public Bar getXBar() {
        return xBar;
    }

    /**
     * Sets the x-Bar to which the node belongs to.
     * 
     * @param bar
     *            the x-Bar to set.
     */
    public void setXBar(Bar bar) {
        this.xBar = bar;
    }

    /**
     * Returns the y-Bar to which the node belongs to.
     * 
     * @return the y-Bar.
     */
    public Bar getYBar() {
        return yBar;
    }

    /**
     * Sets the y-Bar to which the node belongs to.
     * 
     * @param bar
     *            the y-Bar to set.
     */
    public void setYBar(Bar bar) {
        this.yBar = bar;
    }

    /**
     * Gets the remaining <code>OrthEdge</code> with the biggest y-coordinate on
     * the left side of the lower ports.
     * 
     * @return The <code>OrthEdge</code> with the biggest y-coordinate on the
     *         left side.
     */
    private OrthEdge getBiggestBottom_One() {
        if (!bottom_one.isEmpty()) {
            Collections.sort(bottom_one, new YComparator());
            OrthEdge e = bottom_one.get(bottom_one.size() - 1).getEdge();
            bottom_one.remove(bottom_one.get(bottom_one.size() - 1));
            return e;
        } else
            return null;
    }

    /**
     * Gets the remaining <code>OrthEdge</code> with the biggest y-coordinate on
     * the right side of the lower ports.
     * 
     * @return The <code>OrthEdge</code> with the biggest y-coordinate on the
     *         right side.
     */
    private OrthEdge getBiggestBottom_Two() {
        if (!bottom_two.isEmpty()) {
            Collections.sort(bottom_two, new YComparator());
            OrthEdge e = bottom_two.get(bottom_two.size() - 1).getEdge();
            bottom_two.remove(bottom_two.get(bottom_two.size() - 1));
            return e;
        } else
            return null;
    }

    /**
     * Gets the remaining <code>OrthEdge</code> with the smallest y-coordinate
     * on the left side of the upper ports.
     * 
     * @return The <code>OrthEdge</code> with the smallest y-coordinate on the
     *         left side.
     */
    private OrthEdge getSmallestTop_One() {
        if (!top_one.isEmpty()) {
            Collections.sort(top_one, new YComparator());
            OrthEdge e = top_one.get(0).getEdge();
            top_one.remove(top_one.get(0));
            return e;
        } else
            return null;
    }

    /**
     * Gets the remaining <code>OrthEdge</code> with the smallest y-coordinate
     * on the right side of the upper ports.
     * 
     * @return The <code>OrthEdge</code> with the smallest y-coordinate on the
     *         left side.
     */
    private OrthEdge getSmallestTop_Two() {
        if (!top_two.isEmpty()) {
            Collections.sort(top_two, new YComparator());
            OrthEdge e = top_two.get(0).getEdge();
            top_two.remove(top_two.get(0));
            return e;
        } else
            return null;
    }

    /**
     * Gets the remaining <code>OrthEdge</code> with the smallest x-coordinate
     * on the upper side of the ports on the left.
     * 
     * @return The <code>OrthEdge</code> with the smallest x-coordinate on the
     *         upper side.
     */
    private OrthEdge getSmallestLeft_One() {
        if (!left_one.isEmpty()) {
            Collections.sort(left_one, new XComparator());
            OrthEdge e = left_one.get(0).getEdge();
            left_one.remove(left_one.get(0));
            return e;
        } else
            return null;
    }

    /**
     * Gets the remaining <code>OrthEdge</code> with the smallest x-coordinate
     * on the lower side of the ports on the left.
     * 
     * @return The <code>OrthEdge</code> with the smallest x-coordinate on the
     *         lower side.
     */
    private OrthEdge getSmallestLeft_Two() {
        if (!left_two.isEmpty()) {
            Collections.sort(left_two, new XComparator());
            OrthEdge e = left_two.get(0).getEdge();
            left_two.remove(left_two.get(0));
            return e;
        } else
            return null;
    }

    /**
     * Gets the remaining <code>OrthEdge</code> with the biggest x-coordinate on
     * the upper side of the ports on the right.
     * 
     * @return The <code>OrthEdge</code> with the biggest x-coordinate on the
     *         upper side.
     */
    private OrthEdge getBiggestRight_One() {
        if (!right_one.isEmpty()) {
            Collections.sort(right_one, new XComparator());
            OrthEdge e = right_one.get(right_one.size() - 1).getEdge();
            right_one.remove(right_one.get(right_one.size() - 1));
            return e;
        } else
            return null;
    }

    /**
     * Gets the remaining <code>OrthEdge</code> with the biggest x-coordinate on
     * the lower side of the ports on the right.
     * 
     * @return The <code>OrthEdge</code> with the biggest x-coordinate on the
     *         lower side.
     */
    private OrthEdge getBiggestRight_Two() {
        if (!right_two.isEmpty()) {
            Collections.sort(right_two, new XComparator());
            OrthEdge e = right_two.get(right_two.size() - 1).getEdge();
            right_two.remove(right_two.get(right_two.size() - 1));
            return e;
        } else
            return null;
    }

    /**
     * Returns the next edge, to which a port is to be assigned. The ports are
     * assigned starting next to the middle edge and continue until the end of
     * the side.
     * 
     * @param side
     *            the side of the port (top 't', bottom 'b', left 'l', right
     *            'r')
     * @param isOne
     *            if it is next edge of the first part on this side
     * @return the next <code>OrthEdge</code>
     */
    public OrthEdge getNextEdge(char side, boolean isOne) {
        OrthEdge oe;
        switch (side) {
        case 't':
            if (isOne) // oben links
            {
                oe = this.getSmallestTop_One();
            } else
            // oben rechts
            {
                oe = this.getSmallestTop_Two();
            }
            break;
        case 'b':
            if (isOne) // unten links
            {
                oe = this.getBiggestBottom_One();
            } else
            // unten rechts
            {
                oe = this.getBiggestBottom_Two();
            }
            break;
        case 'l':
            if (isOne) // links oben
            {
                oe = this.getSmallestLeft_One();
            } else
            // links unten
            {
                oe = this.getSmallestLeft_Two();
            }
            break;
        case 'r':
            if (isOne) // rechts oben
            {
                oe = this.getBiggestRight_One();
            } else
            // rechts unten
            {
                oe = this.getBiggestRight_Two();
            }
            break;
        default:
            oe = null;
            System.out.println("Fehler: Falsche Port-Seite angegeben.");
            System.exit(-1);
            break;
        }
        return oe;
    }

    @Override
    public String toString() {
        return this.getLabel();
    }
}
