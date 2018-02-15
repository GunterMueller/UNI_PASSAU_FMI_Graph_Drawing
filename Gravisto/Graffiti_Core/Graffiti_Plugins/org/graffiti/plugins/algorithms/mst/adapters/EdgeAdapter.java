package org.graffiti.plugins.algorithms.mst.adapters;

import java.awt.Color;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.GraphElementGraphicAttribute;
import org.graffiti.plugins.algorithms.mst.adapters.attribute.WeightAttribute;

/**
 * Proxy object for an {@link org.graffiti.graph.Edge}; provides customized
 * methods for minimum spanning tree algorithms and their animations.
 * <p>
 * 
 * @author Harald
 * @version $Revision$ $Date$
 */
public class EdgeAdapter {
    //
    // The ids of the attributes used by this edge proxy.
    //

    /**
     * The path to the collection attribute containing all attributes
     * 
     * @see org.graffiti.attributes.CollectionAttribute
     */
    private static final String EMPTY_PATH = "";

    /**
     * The id of the collection attribute containing all attributes used by
     * minimum spanning tree algorithms.
     * 
     * @see org.graffiti.attributes.CollectionAttribute
     */
    public static final String ROOT_ATTRIBUTE = "mst";

    /**
     * The id of the boolean attribute indicating whether an edge is part of a
     * minimum spanning tree.
     * 
     * @see org.graffiti.attributes.BooleanAttribute
     */
    public static final String TREE_EDGE_ATTRIBUTE = "isTreeEdge";

    /**
     * The id of the weight attribute holding an edge's weight.
     * 
     * @see org.graffiti.plugins.algorithms.mst.adapters.attribute.WeightAttribute
     */
    public static final String WEIGHT_ATTRIBUTE = "weight";

    //
    // Paths to the attributes used by this edge proxy.
    //

    /**
     * The separator used to form paths from ids.
     */
    public static final String SEPARATOR = ".";

    /**
     * The path to the collection attribute containing all attributes used by
     * minimum spanning tree algorithms. This coincides with the id of the root
     * attribute itself.
     * 
     * @see #ROOT_ATTRIBUTE
     * @see org.graffiti.attributes.CollectionAttribute
     */
    public static final String PATH_TO_ROOT_ATTRIBUTE = ROOT_ATTRIBUTE;

    /**
     * The path to the boolean attribute indicating whether an edge is part of a
     * minimum spanning tree. This path is formed from the root attribute's id
     * followed by the separator character followed by the id of the tree edge
     * attribute.
     * 
     * @see #ROOT_ATTRIBUTE
     * @see #SEPARATOR
     * @see #TREE_EDGE_ATTRIBUTE
     */
    public static final String PATH_TO_TREE_EDGE_ATTRIBUTE = ROOT_ATTRIBUTE
            + SEPARATOR + TREE_EDGE_ATTRIBUTE;

    /**
     * The path to the weight attribute holding an edge's weight. This path is
     * formed by the root attribute's id followed by the separator character
     * followed by the weight attribute's id.
     * 
     * @see #ROOT_ATTRIBUTE
     * @see #SEPARATOR
     * @see #WEIGHT_ATTRIBUTE
     */
    public static final String PATH_TO_WEIGHT_ATTRIBUTE = ROOT_ATTRIBUTE
            + SEPARATOR + WEIGHT_ATTRIBUTE;

    /**
     * The edge this proxy object stands for.
     */
    private Edge edge = null;

    /**
     * Default weight for unlabelled edges.
     */
    private float defaultWeight = Float.NaN;

    /**
     * <tt>true</tt> if this edge is colored; <tt>false</tt> otherwise.
     */
    private boolean isColored = false;

    /**
     * This constructor calls <tt>this(null,1,false)</tt>.
     * 
     */
    protected EdgeAdapter() {
        this(null, 1, false);
    }

    /**
     * Sole constructor; initializes this proxy with the specified edge, default
     * weight and appearence. This proxy does not allow <tt>null</tt> values for
     * edges.
     * 
     * @param e
     *            the edge this proxy stands for.
     * @param defaultWeight
     *            default weight for unlabelled edges.
     * @param colored
     *            <tt>true</tt> if this edge is colored; false otherwise.
     * 
     * @throws NullPointerException
     *             if the specified edge is <tt>null</tt>.
     */
    protected EdgeAdapter(Edge e, float defaultWeight, boolean colored) {
        edge = e;
        this.defaultWeight = defaultWeight;
        this.isColored = colored;
    }

    /**
     * Returns the weight of this edge.
     * <p>
     * This implementation returns the value of the weight attribute of the edge
     * corresponing to this edge proxy. The weight attribute's path is indicated
     * by <tt>PATH_TO_WEIGHT_ATTRIBUTE</tt>.
     * 
     * @see org.graffiti.plugins.algorithms.mst.adapters.attribute.WeightAttribute
     * @see #PATH_TO_WEIGHT_ATTRIBUTE
     * 
     * @return the weight of this edge
     */
    public float getWeight() {
        return (Float) weightAttribute().getValue();
    }

    /**
     * Returns the <tt>WeightAttribute</tt> of the edge corresponding to this
     * proxy.
     * 
     * @see org.graffiti.plugins.algorithms.mst.adapters.attribute.WeightAttribute
     * 
     * @return the <tt>WeightAttribute</tt> of this edge proxy.
     */
    private Attribute weightAttribute() {
        return edge.getAttribute(PATH_TO_WEIGHT_ATTRIBUTE);
    }

    /**
     * Selects this edge proxy; i.e. makes its corresponding edge an edge of a
     * minimum spanning tree.
     * <p>
     * This implementation sets the <tt>BooleanAttribute</tt> found at the
     * location indicated by <tt>PATH_TO_TREE_EDGE_ATTRIBUTE</tt> to
     * <tt>true</tt>. If this edge proxy is colored, its corresponding edge's
     * color is set to red.
     * 
     * @see #PATH_TO_TREE_EDGE_ATTRIBUTE
     */
    public void select() {
        treeEdgeAttribute().setBoolean(true);
        if (isColored) {
            colorAttribute().setColor(Color.RED);
        }
    }

    /**
     * Returns the tree edge attribute of the edge this proxy stands for.
     * 
     * @return the tree edge attribute of the edge this proxy stands for.
     */
    private BooleanAttribute treeEdgeAttribute() {
        return (BooleanAttribute) edge
                .getAttribute(PATH_TO_TREE_EDGE_ATTRIBUTE);
    }

    /**
     * Unselects this edge proxy; i.e. makes its corresponding edge a non-tree
     * edge of a minimum spanning tree.
     * <p>
     * This implementation sets the <tt>BooleanAttribute</tt> found at the
     * location indicated by <tt>PATH_TO_TREE_EDGE_ATTRIBUTE</tt> to
     * <tt>false</tt>. If this edge proxy is colored, its corresponding edge's
     * color is set to black.
     * 
     * @see #PATH_TO_TREE_EDGE_ATTRIBUTE
     */
    public void unselect() {
        treeEdgeAttribute().setBoolean(false);
        if (isColored) {
            colorAttribute().setColor(Color.BLACK);
        }
    }

    /**
     * Returns the framecolor attribute of the edge this proxy stands for.
     * 
     * @return the framecolor attribute of the edge this proxy stands for.
     */
    private ColorAttribute colorAttribute() {
        return ((GraphElementGraphicAttribute) edge.getAttribute("graphics"))
                .getFramecolor();
    }

    /**
     * Initializes this edge proxy.
     * <p>
     * This implementation first removes all attributes found at the location
     * indicated by <tt>MST_ROOT</tt>. Then it adds three attributes to the edge
     * corresponing this proxy and unselects this edge proxy. The three
     * attributes are:
     * <ol>
     * <li>A <tt>CollectionAttribute</tt> at <tt>ROOT_ATTRIBUTE</tt> containing
     * all other attributes.
     * <li>A customized float attribute at <tt>PATH_TO_WEIGHT_ATTRIBUTE</tt>.
     * <li>A <tt>BooleanAttribute</tt> at <tt>PATH_TO_TREE_EDGE_ATTRIBUTE</tt>.
     * </ol>
     * 
     * @see org.graffiti.attributes.CollectionAttribute
     * @see org.graffiti.plugins.algorithms.mst.adapters.attribute.WeightAttribute
     * @see org.graffiti.attributes.BooleanAttribute
     * @see #ROOT_ATTRIBUTE
     * @see #PATH_TO_WEIGHT_ATTRIBUTE
     * @see #PATH_TO_TREE_EDGE_ATTRIBUTE
     * @see #unselect()
     */
    public void init() {
        try {
            edge.removeAttribute(ROOT_ATTRIBUTE);
        } catch (AttributeNotFoundException ignored) {
        }
        edge.addAttribute(new HashMapAttribute(ROOT_ATTRIBUTE), EMPTY_PATH);
        edge.addAttribute(new WeightAttribute(WEIGHT_ATTRIBUTE, edge,
                defaultWeight), ROOT_ATTRIBUTE);
        edge.addAttribute(new BooleanAttribute(TREE_EDGE_ATTRIBUTE),
                ROOT_ATTRIBUTE);
        unselect();
    }

    /**
     * Cleans this edge proxy of unnecessary attributes processing information.
     * <p>
     * This implementation removes the <tt>WeightAttribute</tt> from the edge
     * corresponding to this proxy. This <tt>WeightAttribute</tt> is located at
     * the path given by <tt>PATH_TO_WEIGHT_ATTRIBUTE</tt>.
     * 
     * @see org.graffiti.plugins.algorithms.mst.adapters.attribute.WeightAttribute
     * @see #PATH_TO_WEIGHT_ATTRIBUTE
     */
    public void clean() {
        try {
            edge.removeAttribute(PATH_TO_WEIGHT_ATTRIBUTE);
        } catch (AttributeNotFoundException ignored) {
        }
    }

    /**
     * Clears this proxy of all processing information.
     * <p>
     * This implementation unselects this edge proxy and removes all attributes
     * located at the path given by <tt>ROOT_ATTRIBUTE</tt>.
     * 
     * @see #ROOT_ATTRIBUTE
     */
    public void clear() {
        try {
            unselect();
            edge.removeAttribute(ROOT_ATTRIBUTE);
        } catch (AttributeNotFoundException ignored) {
        }
    }

    /**
     * Returns <tt>true</tt> if this edge proxy is selected.
     * <p>
     * This implementation returns the value of the attribute located at the
     * path given by <tt>PATH_TO_TREE_EDGE_ATTRIBUTE</tt>.
     * 
     * @see #PATH_TO_TREE_EDGE_ATTRIBUTE
     * @return <tt>true</tt> if this edge proxy is selected.
     */
    public boolean isSelected() {
        return treeEdgeAttribute().getBoolean();
    }

    /**
     * Returns <tt>true</tt> if this edge adapter's adaptee equals the specified
     * edge.
     * 
     * @param e
     *            the edge to be tested for equality with this edge adapters
     *            adaptee.
     * @return <tt>true</tt> if this edge adapter's adaptee equals the specified
     *         edge.
     */
    public boolean equalsEdge(Edge e) {
        if (e == null)
            return false;
        return e.equals(edge);
    }

    /**
     * Returns <tt>true</tt> if this edge will be colored when it is selected.
     * 
     * @return <tt>true</tt> if this edge will be colored when it is selected.
     */
    public boolean isColored() {
        return this.isColored;
    }

    @Override
    public String toString() {
        return edge.toString();
    }
}
