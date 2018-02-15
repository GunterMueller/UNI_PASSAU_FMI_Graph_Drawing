package org.graffiti.plugins.algorithms.springembedderFR;

import java.util.HashMap;
import java.util.Set;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * Wrapper class, to garantee a faster calculation of the forces at the Spring
 * Embedder.
 * 
 * @author matzeder
 */
public class FRNode {
    /**
     * The <code>org.graffiti.graph.Node</code> which corresponds to this node
     */
    private Node originalNode;

    /**
     * Label of the node, for debugging
     */
    private String label;

    /**
     * The original x - position of the node, at beginning of the algorithm
     */
    private double originalXPos;

    /**
     * The original y - position of the node, at beginning of the algorithm
     */
    private double originalYPos;

    /**
     * HashMap with the different forces
     */
    private HashMap<String, GeometricalVector> forces;

    /**
     * HashMap with the different last forces
     */
    private HashMap<String, GeometricalVector> lastForces;

    public final static String SHAPE_CIRCLE = "org.graffiti.plugins.views.defaults.CircleNodeShape";

    public final static String SHAPE_RECTANGLE = "org.graffiti.plugins.views.defaults.RectangleNodeShape";

    public final static String SHAPE_ELLIPSE = "org.graffiti.plugins.views.defaults.EllipseNodeShape";

    private String shape;

    /**
     * x-Position of the node during algorithm
     */
    private double xPos;

    /**
     * y-Position of the node during algorithm
     */
    private double yPos;

    /**
     * The width of this node.
     */
    private double width;

    /**
     * The height of this node.
     */
    private double height;

    private double dimension;

    /**
     * Node is movable, if and only if this is true
     */
    private boolean movable;

    /**
     * Local temperature of this node
     */
    private double localTemperature;

    /**
     * The radian of a circle surrounding a point, this is the nearest
     */
    private double radian1 = 2 * Math.sin(Math.PI / 16);

    /**
     * The radian of a circle surrounding a point, this is the farest
     */
    private double radian7 = 2 * Math.cos(Math.PI / 16);

    /**
     * Zone surrounding the node
     */
    private Zone zone;

    /**
     * Constant for the initial force
     */
    public FRNode(Node originalNode) {
        new FRNode(originalNode, false);
    }

    /**
     * Constructs aa FRNode Object with the specified parameters
     * 
     * @param originalNode
     *            Node this object corresponds to
     */
    public FRNode(Node originalNode, boolean movable) {

        this.originalNode = originalNode;

        xPos = ((NodeGraphicAttribute) this.originalNode.getAttributes()
                .getAttribute(GraphicAttributeConstants.GRAPHICS))
                .getCoordinate().getX();
        yPos = ((NodeGraphicAttribute) this.originalNode.getAttributes()
                .getAttribute(GraphicAttributeConstants.GRAPHICS))
                .getCoordinate().getY();
        this.originalXPos = xPos;
        this.originalYPos = yPos;

        this.movable = movable;

        this.localTemperature = 1.0;
        // after Bertault, there is a zone surruond a node, with 8 sectors
        this.zone = new Zone(8);
        // System.out.println(zone);
        this.forces = new HashMap<String, GeometricalVector>();
        this.lastForces = new HashMap<String, GeometricalVector>();

        this.shape = ((NodeGraphicAttribute) originalNode.getAttributes()
                .getAttribute(GraphicAttributeConstants.GRAPHICS)).getShape();

        NodeGraphicAttribute dim = (NodeGraphicAttribute) this.originalNode
                .getAttributes().getAttribute(
                        GraphicAttributeConstants.GRAPHICS);

        if (shape.equals(SHAPE_CIRCLE)) {

            this.height = dim.getDimension().getHeight();
            this.width = dim.getDimension().getWidth();

        } else {

            this.height = dim.getDimension().getHeight();
            this.width = dim.getDimension().getWidth();

        }

        this.dimension = Math.sqrt(this.height * this.height + this.width
                * this.width) / 2;
        try {
            LabelAttribute labelAttribute = (LabelAttribute) originalNode
                    .getAttributes().getAttribute(
                            GraphicAttributeConstants.LABEL);
            label = labelAttribute.getLabel();
        } catch (AttributeNotFoundException e) {
            label = "kein Knotenlabel";
        }
    }

    /**
     * Returns the x-position
     * 
     * @return Position in x-direction
     */
    public double getXPos() {
        return xPos;
    }

    /**
     * Sets the x-position to pos
     * 
     * @param pos
     */
    public void setXPos(double pos) {
        xPos = pos;
    }

    /**
     * Returns the position in y-direction
     * 
     * @return position in y-direction
     */
    public double getYPos() {
        return yPos;
    }

    /**
     * Sets the position in y-direction to pos
     * 
     * @param pos
     */
    public void setYPos(double pos) {
        yPos = pos;
    }

    /**
     * Returns the original node (<code>org.graffiti.graph.Node</code>) of this
     * FRNode
     * 
     * @return the original Node
     */
    public Node getOriginalNode() {
        return originalNode;
    }

    /**
     * Method for output of a FRNode
     */
    @Override
    public String toString() {

        return ("Node " + label + ":(" + Math.round(xPos * 100) / 100.0 + ","
                + Math.round(yPos * 100) / 100.0 + ") ");

    }

    /**
     * Returns the movable.
     * 
     * @return the movable.
     */
    public boolean isMovable() {
        return movable;
    }

    /**
     * Sets the movable.
     * 
     * @param movable
     *            the movable to set.
     */
    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    /**
     * Resets the forces. Last forces were changed too.
     */
    public void resetForces() {
        // move the forces of the actual HashMap forces into the HashMap
        // lastForces
        Set<String> keys = forces.keySet();
        for (String str : keys) {
            lastForces.put(str, forces.get(str));
        }
        // clear the forces HashMap
        forces.clear();

    }

    /**
     * Returns the height of this node.
     * 
     * @return Height of this node.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Returns the width of this node.
     * 
     * @return Width of this node.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Returns the localTemperature.
     * 
     * @return the localTemperature.
     */
    public double getLocalTemperature() {
        return localTemperature;
    }

    /**
     * Sets the localTemperature.
     * 
     * @param localTemperature
     *            the localTemperature to set.
     */
    public void setLocalTemperature(double localTemperature) {
        this.localTemperature = localTemperature;
    }

    /**
     * Sets the local temperature of this node, dependent on the actual force
     * onto this node. For example, if the node is moved at the last iteration
     * to the right, and now it should be moved more left than it was before the
     * last move, then the local temperature is reduced.
     * 
     * @param newForceVector
     *            The new calculated force vector of this node
     */
    public void setLocalTemperature(GeometricalVector newForceVector) {

        GeometricalVector lastForceVector = this.getSumOfLastForces();

        if (!(lastForceVector.getX() == 0.0 || lastForceVector.getY() == 0.0)) {
            // double oldDelta = Math.sqrt(lastForceVector.getX()
            // * lastForceVector.getX() + lastForceVector.getY()
            // * lastForceVector.getY());
            // double newDelta = Math.sqrt(newForceVector.getX()
            // * newForceVector.getX() + newForceVector.getY()
            // * newForceVector.getY());

            GeometricalVector oldUnitForceVector = lastForceVector
                    .getUnitVector();
            GeometricalVector newUnitForceVector = newForceVector
                    .getUnitVector();

            GeometricalVector sumVector = GeometricalVector.add(
                    newUnitForceVector, oldUnitForceVector);

            double deltaSquare = sumVector.getX() * sumVector.getX()
                    + sumVector.getY() * sumVector.getY();

            // swinging
            if (deltaSquare < radian1 * radian1) {
                // wenn knoten mit gr��erem ausschlag zur�ckschwingt
                if (this.getLocalTemperature() > 1.0) {
                    this.setLocalTemperature(1.0);
                }

                this.setLocalTemperature(this.getLocalTemperature() * 0.5);

            }
            // speeding-up
            else if (deltaSquare > radian7 * radian7) {

            } else {
                this.setLocalTemperature(1.0);
            }

        }

    }

    /**
     * Returns the originalXPos.
     * 
     * @return the originalXPos.
     */
    public double getOriginalXPos() {
        return originalXPos;
    }

    /**
     * Returns the originalYPos.
     * 
     * @return the originalYPos.
     */
    public double getOriginalYPos() {
        return originalYPos;
    }

    public Zone getZone() {
        return zone;
    }

    /**
     * Returns the forces.
     * 
     * @return the forces.
     */
    public HashMap<String, GeometricalVector> getForces() {
        return forces;
    }

    /**
     * Returns the force with Key key
     * 
     * @param key
     * @return Returns the force with Key key
     */
    public GeometricalVector getForce(String key) {

        if (forces.get(key) == null) {
            forces.put(key, new GeometricalVector());
        }
        return forces.get(key);
    }

    public void setForces(String str, GeometricalVector forceVector) {
        forces.put(str, forceVector);

    }

    /**
     * Returns the lastForces.
     * 
     * @return the lastForces.
     */
    public HashMap<String, GeometricalVector> getLastForces() {
        return lastForces;
    }

    /**
     * Sets the lastForces.
     * 
     * @param lastForces
     *            the lastForces to set.
     */
    public void setLastForces(HashMap<String, GeometricalVector> lastForces) {
        this.lastForces = lastForces;
    }

    public GeometricalVector getSumOfForces() {

        GeometricalVector sumVector = new GeometricalVector();
        Set<String> keys = forces.keySet();

        for (String key : keys) {

            GeometricalVector summand = forces.get(key);

            if (summand != null) {
                sumVector = GeometricalVector.add(summand, sumVector);
            }

        }

        return sumVector;

    }

    /**
     * Returns the sum of the last forces.
     * 
     * @return The sum of the last forces.
     */
    public GeometricalVector getSumOfLastForces() {

        GeometricalVector sumVector = new GeometricalVector();
        Set<String> keys = lastForces.keySet();
        for (String key : keys) {
            GeometricalVector summand = lastForces.get(key);
            sumVector = GeometricalVector.add(summand, sumVector);
        }
        return sumVector;
    }

    /**
     * Returns the sum of the forces as String
     * 
     * @return Sum of the forces as String
     */
    public String printForce() {
        return (this + ": " + this.getSumOfForces());
    }

    /**
     * Returns the label.
     * 
     * @return the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns a listing of all forces of this node.
     * 
     * @return Listing of all forces of this node.
     */
    public String printDifferentForces() {

        Set<String> keys = forces.keySet();

        String forcesStr = this + "\t";

        for (String key : keys) {
            forcesStr = forcesStr + " " + key + ": "
                    + (forces.get(key)).toString() + "\t";

        }

        return forcesStr;

    }

    public double getUpperBound(double factor) {

        if (this.shape.equals(SHAPE_RECTANGLE))
            return (this.getYPos() - (this.getHeight() * factor) / 2);
        else if (this.shape.equals(SHAPE_CIRCLE)) {

            double radius = this.getWidth() / 2;

            return (this.getYPos() - (radius * factor));

        } else if (this.shape.equals(SHAPE_ELLIPSE))
            // treated like a rectangle, because of the similarity to ellipses
            return (this.getYPos() - (this.getHeight() * factor) / 2);
        else {
            System.err.println(": " + "Shape " + shape + " not supported!!!");
            return 0.0d;
        }

    }

    public double getLowerBound(double factor) {

        if (this.shape.equals(SHAPE_RECTANGLE))
            return (this.getYPos() + (this.getHeight() * factor) / 2);
        else if (this.shape.equals(SHAPE_CIRCLE)) {

            double radius = this.getWidth() / 2;

            return (this.getYPos() + (radius * factor));

        } else if (this.shape.equals(SHAPE_ELLIPSE))
            // treated like a rectangle, because of the similarity to ellipses
            return (this.getYPos() + (this.getHeight() * factor) / 2);
        else {
            System.err.println(": " + "Shape " + shape + " not supported!!!");
            return 0.0d;
        }

    }

    public double getRightBound(double factor) {
        if (this.shape.equals(SHAPE_RECTANGLE))
            return (this.getXPos() + (this.getWidth() * factor) / 2);
        else if (this.shape.equals(SHAPE_CIRCLE)) {

            double radius = this.getWidth() / 2;

            return (this.getXPos() + (radius * factor));

        } else if (this.shape.equals(SHAPE_ELLIPSE))
            // treated like a rectangle, because of the similarity to ellipses
            return (this.getXPos() + (this.getWidth() * factor) / 2);
        else {
            System.err.println(": " + "Shape " + shape + " not supported!!!");
            return 0.0d;
        }

    }

    public double getLeftBound(double factor) {
        if (this.shape.equals(SHAPE_RECTANGLE))
            return (this.getXPos() - (this.getWidth() * factor) / 2);
        else if (this.shape.equals(SHAPE_CIRCLE)) {

            double radius = this.getWidth() / 2;

            return (this.getXPos() - (radius * factor));

        } else if (this.shape.equals(SHAPE_ELLIPSE))
            // treated like a rectangle, because of the similarity to ellipses
            return (this.getXPos() - (this.getWidth() * factor) / 2);
        else {
            System.err.println(": " + "Shape " + shape + " not supported!!!");
            return 0.0d;
        }

    }

    public double getUpperBound() {

        if (this.shape.equals(SHAPE_RECTANGLE))
            return (this.getYPos() - this.getHeight() / 2);
        else if (this.shape.equals(SHAPE_CIRCLE)) {

            double radius = this.getWidth() / 2;

            return (this.getYPos() - radius);

        } else if (this.shape.equals(SHAPE_ELLIPSE))
            // treated like a rectangle, because of the similarity to ellipses
            return (this.getYPos() - this.getHeight() / 2);
        else {
            System.err.println(": " + "Shape " + shape + " not supported!!!");
            return 0.0d;
        }

    }

    public double getLowerBound() {

        if (this.shape.equals(SHAPE_RECTANGLE))
            return (this.getYPos() + this.getHeight() / 2);
        else if (this.shape.equals(SHAPE_CIRCLE)) {

            double radius = this.getWidth() / 2;

            return (this.getYPos() + radius);

        } else if (this.shape.equals(SHAPE_ELLIPSE))
            // treated like a rectangle, because of the similarity to ellipses
            return (this.getYPos() + this.getHeight() / 2);
        else {
            System.err.println(": " + "Shape " + shape + " not supported!!!");
            return 0.0d;
        }

    }

    public double getRightBound() {
        if (this.shape.equals(SHAPE_RECTANGLE))
            return (this.getXPos() + this.getWidth() / 2);
        else if (this.shape.equals(SHAPE_CIRCLE)) {

            double radius = this.getWidth() / 2;

            return (this.getXPos() + radius);

        } else if (this.shape.equals(SHAPE_ELLIPSE))
            // treated like a rectangle, because of the similarity to ellipses
            return (this.getXPos() + this.getWidth() / 2);
        else {
            System.err.println(": " + "Shape " + shape + " not supported!!!");
            return 0.0d;
        }

    }

    public double getLeftBound() {
        if (this.shape.equals(SHAPE_RECTANGLE))
            return (this.getXPos() - this.getWidth() / 2);
        else if (this.shape.equals(SHAPE_CIRCLE)) {

            double radius = this.getWidth() / 2;

            return (this.getXPos() - radius);

        } else if (this.shape.equals(SHAPE_ELLIPSE))
            // treated like a rectangle, because of the similarity to ellipses
            return (this.getXPos() - this.getWidth() / 2);
        else {
            System.err.println(": " + "Shape " + shape + " not supported!!!");
            return 0.0d;
        }

    }

    /**
     * Returns the shape.
     * 
     * @return the shape.
     */
    public String getShape() {
        return shape;
    }

    public double getDimension() {
        return dimension;
    }

    /**
     * <i>JavaDoc by scholz:</i> <br>
     * <b>Warning:</b> The vector returned by this routine reflects the position
     * before the algorithm was run. To get the momentary position use
     * <tt>getActualPosition()</tt>
     */
    public GeometricalVector getPosition() {
        return new GeometricalVector(this.originalXPos, this.originalYPos);
    }

    /**
     * @return the momentary position set by <tt>setXPos()</tt> and
     *         <tt>setYPos()</tt>. Not to be confused with
     *         <tt>getPosition()</tt>.
     * @author scholz
     */
    public GeometricalVector getActualPosition() {
        return new GeometricalVector(this.xPos, this.yPos);
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Sets the at-start-of-algorithm-x-position. The temporary position is also
     * set.
     * 
     * @author scholz
     */
    public void setOriginalXPos(double originalXPos) {
        this.originalXPos = originalXPos;
        this.xPos = originalXPos;
    }

    /**
     * Sets the at-start-of-algorithm-y-position The temporary position is also
     * set.
     * 
     * @author scholz
     */
    public void setOriginalYPos(double originalYPos) {
        this.originalYPos = originalYPos;
        this.yPos = originalYPos;
    }
}
