package org.graffiti.plugins.algorithms.kandinsky;

/**
 * The arc of the normalized network which is used to calculate the normalized
 * orthogonal representation of the graph. The arc has a status, which stores
 * the direction (UP, DOWN, LEFT, RIGHT) of this edge.
 */
public class NormArc {
    /**
     * The id for the new label attributes on edges which arise as result of
     * this algorithm
     */
    private String label = "";

    /** The node where the edge starts. */
    private NormNode from;

    /** The node where the edge ends. */
    private NormNode to;

    /** The <code>Edge</code> of the graph. */
    private OrthEdge edge;

    /** True, if it is the direction of the dart. */
    private boolean direction = true;

    /** The angle to the next node. */
    private int angle;

    /** The status of the NormArc, i.e. the absolute diretion of the arc. */
    private Status status;

    /** The next NormArc of the face. */
    private NormArc next = null;

    /** The NormArc in the opposite direction. */
    private NormArc opposite = null;

    /** Total number of bends and current position. */
    private int total, pos;

    /** The status of the NormNode. */
    enum Status {
        UP, DOWN, LEFT, RIGHT
    };

    /**
     * Constructs a Normarc.
     * 
     * @param label
     *            The label of the arc.
     * @param start
     *            The starting node of the arc.
     * @param end
     *            The target node of the arc.
     * @param edge
     *            The corresponding <code>Edge</code> of the graph.
     * @param dir
     *            If the arc is in the direction of the edge.
     * @param angle
     *            The angle to the following NormArc.
     * @param total
     *            The total number of NormArcs, which were constructed to
     *            replace an edge with bends.
     * @param pos
     *            The actual position within "total".
     */
    public NormArc(String label, NormNode start, NormNode end, OrthEdge edge,
            boolean dir, int angle, int total, int pos) {
        this.label = label;
        this.from = start;
        this.to = end;
        this.edge = edge;
        this.direction = dir;
        this.angle = angle;
        this.total = total;
        this.pos = pos;
    }

    /**
     * Returns the label of the arc.
     * 
     * @return The label of the arc.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the starting node of the edge.
     * 
     * @return the from as <code>MCMFNode</code>.
     */
    public NormNode getFrom() {
        return from;
    }

    /**
     * Returns the target node of the edge.
     * 
     * @return The <code>MCMFNode</code>.
     */
    public NormNode getTo() {
        return to;
    }

    /**
     * Returns the <code>OrthEdge</code> of the NormArc.
     * 
     * @return The <code>OrthEdge</code> of the NormArc.
     */
    public OrthEdge getEdge() {
        return edge;
    }

    /**
     * Returns true, if the NormArc is a DummyArc.
     * 
     * @return True, if the NormArc is a DummyArc.
     */
    public boolean isDummy() {
        if (edge == null)
            return true;
        return false;
    }

    /**
     * Returns the angle to the next node.
     * 
     * @return the angle to the next node.
     */
    public int getAngle() {
        return angle;
    }

    /**
     * Sets the angle to the next node.
     * 
     * @param angle
     *            the angle to the next node to set.
     */
    protected void setAngle(int angle) {
        this.angle = angle;
    }

    /**
     * Gets the direction of the edge.
     * 
     * @return if the arc is in the direction of the edge.
     */
    public boolean getDirection() {
        return direction;
    }

    /**
     * Is true, if the status of the NormArc has already been calculated.
     * 
     * @return if the status has alredy been calculated
     */
    public boolean hasStatus() {
        if (this.status != null)
            return true;
        else
            return false;
    }

    /**
     * Prints the status of the NormArc.
     * 
     * @return the Status.
     */
    protected String printStatus() {
        String label = "";
        if (status == Status.RIGHT) {
            label = "RIGHT";
        }
        if (status == Status.LEFT) {
            label = "LEFT";
        }
        if (status == Status.UP) {
            label = "UP";
        }
        if (status == Status.DOWN) {
            label = "DOWN";
        }
        return label;
    }

    /**
     * Returns the status of the NormArc.
     * 
     * @return the Status.
     */
    protected Status getStatus() {
        return status;
    }

    /**
     * Sets the status of the NormArc.
     * 
     * @param status
     *            the Status to set.
     */
    protected void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Calculates the status of the NormArc.
     * 
     * @param prev
     *            the status of the previous NormArc.
     * @param angle
     *            The angle of the previous NormArc.
     * @return if the status is not valid
     */
    public boolean calculateStatus(Status prev, int angle) {
        if (this.status != null)
            return true;
        else {
            Status status = null;
            if (prev == Status.RIGHT) {
                if ((angle == 0) || (angle == 4)) {
                    status = Status.LEFT;
                }
                if (angle == 1) {
                    status = Status.DOWN;
                }
                if (angle == 2) {
                    status = Status.RIGHT;
                }
                if (angle == 3) {
                    status = Status.UP;
                }
            }
            if (prev == Status.LEFT) {
                if ((angle == 0) || (angle == 4)) {
                    status = Status.RIGHT;
                }
                if (angle == 1) {
                    status = Status.UP;
                }
                if (angle == 2) {
                    status = Status.LEFT;
                }
                if (angle == 3) {
                    status = Status.DOWN;
                }
            }
            if (prev == Status.UP) {
                if ((angle == 0) || (angle == 4)) {
                    status = Status.DOWN;
                }
                if (angle == 1) {
                    status = Status.RIGHT;
                }
                if (angle == 2) {
                    status = Status.UP;
                }
                if (angle == 3) {
                    status = Status.LEFT;
                }
            }
            if (prev == Status.DOWN) {
                if ((angle == 0) || (angle == 4)) {
                    status = Status.UP;
                }
                if (angle == 1) {
                    status = Status.LEFT;
                }
                if (angle == 2) {
                    status = Status.DOWN;
                }
                if (angle == 3) {
                    status = Status.RIGHT;
                }
            }
            this.setStatus(status);
            this.getFrom().addStatusArc(this);
            setOppositeStatus(status);
            return false;
        }
    }

    /**
     * Sets the status of the opposite NormArc.
     * 
     * @param status
     *            the Status to set.
     */
    // Setzt den Status der Kante in Gegenrichtung
    protected void setOppositeStatus(Status status) {
        if (status == Status.RIGHT) {
            this.opposite.setStatus(Status.LEFT);
        }
        if (status == Status.LEFT) {
            this.opposite.setStatus(Status.RIGHT);
        }
        if (status == Status.UP) {
            this.opposite.setStatus(Status.DOWN);
        }
        if (status == Status.DOWN) {
            this.opposite.setStatus(Status.UP);
        }
        this.opposite.getFrom().addStatusArc(this.opposite);
    }

    /**
     * Returns the next NormArc of the face.
     * 
     * @return the next.
     */
    public NormArc getNext() {
        return next;
    }

    /**
     * Sets the next NormArc of the face.
     * 
     * @param next
     *            the next to set.
     */
    public void setNext(NormArc next) {
        this.next = next;
    }

    /**
     * Returns the opposite NormArc of the edge.
     * 
     * @return the opposite.
     */
    public NormArc getOpposite() {
        return opposite;
    }

    /**
     * Sets the opposite NormArc of the edge.
     * 
     * @param opposite
     *            the opposite to set.
     */
    public void setOpposite(NormArc opposite) {
        this.opposite = opposite;
    }

    /**
     * Returns the total number of bends.
     * 
     * @return the total number of bends.
     */
    public int getTotal() {
        return total;
    }

    /**
     * Sets the total number of bends.
     * 
     * @param total
     *            the total number of bends to set.
     */
    public void setTotal(int total) {
        this.total = total;
    }

    /**
     * Returns the position of the current bend.
     * 
     * @return the number of the current bend.
     */
    public int getPos() {
        return pos;
    }

    /**
     * Sets the position of the current bend.
     * 
     * @param pos
     *            the number of the current bend to set.
     */
    public void setPos(int pos) {
        this.pos = pos;
    }

    /**
     * Returns the label, the angle to the following NormArc and the status of
     * the arc.
     */
    @Override
    public String toString() {
        String data = "(" + label + ", " + angle + ", " + status + ")";
        return data;
    }
}
