//==============================================================================
//
//   BKConst.java
//
//   Copyright (c) 2001-2003 Graffiti Team, Uni Passau
//
//==============================================================================
// $Id: BKConst.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.brandeskoepf;

/**
 * This class contains every necessary value of the algorithm
 * 
 * @author Florian Fischer
 */
public class BKConst {
    // ~ Static fields/initializers
    // =============================================

    /** The path to the level attribute (Node) */
    private static String PATH_LEVEL;

    /** The path to the order attribute (Node) */
    private static String PATH_ORDER;

    /** The path to the dummy attribute (Node) */
    private static String PATH_DUMMY;

    /** The path to the x coordinate attribute (Node) */
    private static String PATH_COORD_X;

    /** The path to the y coordinate attribute (Node) */
    private static String PATH_COORD_Y;

    /** The path to the shape attributes */
    private static String PATH_SHAPE;

    /**
     * The path to the marked attribute (Edge). Their is no setter for this
     * attribute, because it is an intern setted attribute
     */
    private static String PATH_EDGEMARKED_GET = "BKLayoutAlgorithm.florian.marked";

    /**
     * The path to the marked attribute (Edge). Their is no setter for this
     * attribute, because it is an intern setted attribute
     */
    private static String PATH_OWN_ATTRIBUTES1 = "BKLayoutAlgorithm.florian";

    /** The path to the container with all algorithm set attributes */
    private static String PATH_OWN_ATTRIBUTES2 = "BKLayoutAlgorithm";

    /** The path to the 'cut edge' attribute (Edge) */
    private static String PATH_CUTEDGE;

    /**
     * The path to the shiftSet attribute (Node). Their is no setter for this
     * attribute, because it is an intern setted attribute
     */
    private static String PATH_SHIFTSET_GET = "BKLayoutAlgorithm.florian.shiftSet";

    /**
     * The path to the shiftSet attribute (Node). Their is no setter for this
     * attribute, because it is an intern setted attribute
     */
    private static String PATH_COORD_INITIAL = "BKLayoutAlgorithm.florian.coordInitial";

    /** The path to the bends attribute (Edge) */
    private static String PATH_BENDS;

    /** The offset of a bend (Edge) */
    private static String BEND_I;

    /** The path to the edge shape 'polyline' (Edge) */
    private static String PATH_SHAPE_POLY;

    /** The path to the edge shape 'smoothline' (Edge) */
    private static String PATH_SHAPE_SMOOTH;

    /** The minimum distance between the nodes */
    private static double MINDIST;

    /**
     * The minimum distance between the nodes in the radial layout. The value is
     * the distance on a circular arc
     */
    private static double RADIAL_MINDIST;

    /** The distance to the left border */
    private static double LEFT_DIST;

    /** The distance to the top border */
    private static double TOP_DIST;

    /** The distance between the levels */
    private static double LEVEL_DIST;

    /** The distance between the levels in the radial layout */
    private static double RADIAL_LEVEL_DIST;

    /** The add on, if it is neccessary to scale */
    private static double RADIAL_SCALE_STEP;

    /**
     * 0 = the first run of the algorithm, up to 3 = the fourth run of the
     * algorithm, Layout: horizontal 4 = Layout: horizontal 5 = Layout: radial
     */
    private static int DRAW;

    /**
     * The type of sampling point computing 0 = fixed number of points, 1 =
     * dynamic number of points, depending on edge length and radius
     */
    private static int SAMPLING_TYPE;

    /** 0=compute long span edges, 1=let dummy-nodes remaining */
    private static int COMPUT_LONG_SPAN_EDGES;

    /**
     * The calibration 0 = move the four layouts with their center to 0, 1 =
     * align to assignment of smallest width 2 = align to the balance point
     */
    private static int CALIBRATION;

    /** The coordinate assignment 0 = average of medians 1 = average of all */
    private static int COORD_ASSIGNMENT;

    /** The initial value of the coordinates */
    private static double COORD_INITIAL = -100000;

    /** The initial value of the shifts */
    private static double SHIFT_INITIAL = 1000000;

    // ~ Constructors
    // ===========================================================

    /**
     * Creates a new BKConst object.
     */
    public BKConst() {
        // setDefault();
    }

    // ~ Methods
    // ================================================================

    /**
     * Sets the offset for the name of the folder which contains the bends. For
     * ex. 'bend'
     * 
     * @param bend_i
     *            The string of the offset
     */
    public static void setBEND_I(String bend_i) {
        BEND_I = bend_i;
    }

    /**
     * Returns the offset for the name of the folder which contains the bends.
     * For ex. 'bend'
     * 
     * @return the string of the offset
     */
    public static String getBEND_I() {
        return BEND_I;
    }

    /**
     * Sets the calibration type.<br>
     * 0 = move the four layouts with their center to 0<br>
     * 1 = align to assignment of smallest width<br>
     * 2 = align to the balance point
     * 
     * @param calibration
     *            The int of the calibration type
     */
    public static void setCALIBRATION(int calibration) {
        CALIBRATION = calibration;
    }

    /**
     * Gets the calibration type
     * 
     * @return Returns the calibration type as a int.
     */
    public static int getCALIBRATION() {
        return CALIBRATION;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param comput_long_span_edges
     *            The cOMPUT_LONG_SPAN_EDGES to set.
     */
    public static void setCOMPUT_LONG_SPAN_EDGES(int comput_long_span_edges) {
        COMPUT_LONG_SPAN_EDGES = comput_long_span_edges;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return Returns the cOMPUT_LONG_SPAN_EDGES.
     */
    public static int getCOMPUT_LONG_SPAN_EDGES() {
        return COMPUT_LONG_SPAN_EDGES;
    }

    /**
     * Sets the coordinate assignment type<br>
     * 0 = average of medians<br>
     * 1 = average of all
     * 
     * @param coord_assignment
     *            The int of the coordinate assignment type
     */
    public static void setCOORD_ASSIGNMENT(int coord_assignment) {
        COORD_ASSIGNMENT = coord_assignment;
    }

    /**
     * Returns the coordinate assignment type
     * 
     * @return Returns the coordinate assignment type as a int.
     */
    public static int getCOORD_ASSIGNMENT() {
        return COORD_ASSIGNMENT;
    }

    /**
     * Returns the value, which is assigned to the nodes initially
     * 
     * @return The inital coordinate value
     */
    public static double getCOORD_INITIAL() {
        return COORD_INITIAL;
    }

    /**
     * Sets the representaion type 0 = the first run of the algorithm, up to 3 =
     * the fourth run of the algorithm, Layout: horizontal 4 = Layout:
     * horizontal 5 = Layout: radial
     * 
     * @param draw
     *            The representation type.
     */
    public static void setDRAW(int draw) {
        DRAW = draw;
    }

    /**
     * Returns the representaion type
     * 
     * @return The representaion type as a int.
     */
    public static int getDRAW() {
        return DRAW;
    }

    /**
     * This method sets all of the default values, which should be set when the
     * plugin is loaded.
     */
    public static void setDefault() {
        setPATH_LEVEL("graphics.level");
        setPATH_ORDER("graphics.order");
        setPATH_DUMMY("graphics.dummy");
        setPATH_COORD_X("graphics.coordinate.x");
        setPATH_COORD_Y("graphics.coordinate.y");
        setPATH_SHAPE("graphics.shape");
        setPATH_CUTEDGE("graphics.cutEdge");
        setPATH_BENDS("graphics.bends");
        setBEND_I("bend");
        setPATH_SHAPE_POLY("org.graffiti.plugins.views.defaults.PolyLineEdgeShape");
        setPATH_SHAPE_SMOOTH("org.graffiti.plugins.views.defaults.SmoothLineEdgeShape");

        setMINDIST(80);
        setRADIAL_MINDIST(40);
        setLEFT_DIST(20);
        setTOP_DIST(80);
        setLEVEL_DIST(80);
        setRADIAL_LEVEL_DIST(80);
        setRADIAL_SCALE_STEP(5);

        setCOMPUT_LONG_SPAN_EDGES(0);
        setSAMPLING_TYPE(0);
        setDRAW(4);
        setCALIBRATION(0);
        setCOORD_ASSIGNMENT(0);
    }

    /**
     * Sets the distance to the left border of the drwaing area
     * 
     * @param left_dist
     *            The distance to the left border.
     */
    public static void setLEFT_DIST(double left_dist) {
        LEFT_DIST = left_dist;
    }

    /**
     * Returns the distance to the left border of the drwaing area
     * 
     * @return The distance to the left border
     */
    public static double getLEFT_DIST() {
        return LEFT_DIST;
    }

    /**
     * Sets the distance between the level in pixel
     * 
     * @param level_dist
     *            The level distance.
     */
    public static void setLEVEL_DIST(double level_dist) {
        LEVEL_DIST = level_dist;
    }

    /**
     * Returns the distance between the level in pixel
     * 
     * @return Returns the level distance.
     */
    public static double getLEVEL_DIST() {
        return LEVEL_DIST;
    }

    /**
     * Sets the minimum distance between the nodes
     * 
     * @param mindist
     *            The minimum distance between the nodes
     */
    public static void setMINDIST(double mindist) {
        MINDIST = mindist;
    }

    /**
     * Returns the minimum distance between the nodes
     * 
     * @return Returns the minimum distance
     */
    public static double getMINDIST() {
        return MINDIST;
    }

    /**
     * Sets the gravisto-path to the bend container
     * 
     * @param path_bends
     *            The path.
     */
    public static void setPATH_BENDS(String path_bends) {
        PATH_BENDS = path_bends;
    }

    /**
     * Returns the gravisto-path to the bend container
     * 
     * @return Returns the path
     */
    public static String getPATH_BENDS() {
        return PATH_BENDS;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return Returns the path to the coordInitial attribute.
     */
    public static String getPATH_COORD_INITIAL() {
        return PATH_COORD_INITIAL;
    }

    /**
     * Sets the gravisto-path to the X-coordinate
     * 
     * @param path_coord_x
     *            The path
     */
    public static void setPATH_COORD_X(String path_coord_x) {
        PATH_COORD_X = path_coord_x;
    }

    /**
     * Returns the gravisto-path to the X-coordinate
     * 
     * @return Returns the path
     */
    public static String getPATH_COORD_X() {
        return PATH_COORD_X;
    }

    /**
     * Sets the gravisto-path to the Y-coordinate
     * 
     * @param path_coord_y
     *            The path
     */
    public static void setPATH_COORD_Y(String path_coord_y) {
        PATH_COORD_Y = path_coord_y;
    }

    /**
     * Returns the gravisto-path to the Y-coordinate
     * 
     * @return Returns the path
     */
    public static String getPATH_COORD_Y() {
        return PATH_COORD_Y;
    }

    /**
     * Sets the gravisto-path to the cutEdge attribute. If you change the path,
     * you have to apply these changes also in the initialise()-Method of the
     * algorithm
     * 
     * @param path_cutedge
     *            The path
     */
    public static void setPATH_CUTEDGE(String path_cutedge) {
        PATH_CUTEDGE = path_cutedge;
    }

    /**
     * Returns the gravisto-path to the cutEdge attribute.
     * 
     * @return Returns the path
     */
    public static String getPATH_CUTEDGE() {
        return PATH_CUTEDGE;
    }

    /**
     * Sets the gravisto-path to the dummy attribute
     * 
     * @param path_dummy
     *            The path
     */
    public static void setPATH_DUMMY(String path_dummy) {
        PATH_DUMMY = path_dummy;
    }

    /**
     * Returns the gravisto-path to the dummy attribute
     * 
     * @return Returns the path
     */
    public static String getPATH_DUMMY() {
        return PATH_DUMMY;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return Returns the path to the marked attribute
     */
    public static String getPATH_EDGEMARKED_GET() {
        return PATH_EDGEMARKED_GET;
    }

    /**
     * Sets the path for the level-attribute, which is set by the user or a
     * upstreames algorithm
     * 
     * @param path_level
     *            The path
     */
    public static void setPATH_LEVEL(String path_level) {
        PATH_LEVEL = path_level;
    }

    /**
     * Returns the path for the level-attribute
     * 
     * @return Returns the path
     */
    public static String getPATH_LEVEL() {
        return PATH_LEVEL;
    }

    /**
     * Sets the path for the order-attribute, which is set by the user or a
     * upstreames algorithm
     * 
     * @param path_order
     *            The path
     */
    public static void setPATH_ORDER(String path_order) {
        PATH_ORDER = path_order;
    }

    /**
     * Returns the path for the order-attribute, which is set by the user or a
     * upstreames algorithm
     * 
     * @return Returns the path
     */
    public static String getPATH_ORDER() {
        return PATH_ORDER;
    }

    /**
     * Returns the path to the higher attribute container
     * 
     * @return Returns the path.
     */
    public static String getPATH_OWN_ATTRIBUTES1() {
        return PATH_OWN_ATTRIBUTES1;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return Returns the path to the lower attribute folder.
     */
    public static String getPATH_OWN_ATTRIBUTES2() {
        return PATH_OWN_ATTRIBUTES2;
    }

    /**
     * Sets the path to the shape attributes
     * 
     * @param path_shape
     *            The path
     */
    public static void setPATH_SHAPE(String path_shape) {
        PATH_SHAPE = path_shape;
    }

    /**
     * Returns the path to the shape attributes
     * 
     * @return Returns the path
     */
    public static String getPATH_SHAPE() {
        return PATH_SHAPE;
    }

    /**
     * Sets the gravisto-path to the shape attribute of the edges
     * 
     * @param path_shape_poly
     *            The path
     */
    public static void setPATH_SHAPE_POLY(String path_shape_poly) {
        PATH_SHAPE_POLY = path_shape_poly;
    }

    /**
     * Returns the gravisto-path to the shape attribute of the edges
     * 
     * @return Returns the path
     */
    public static String getPATH_SHAPE_POLY() {
        return PATH_SHAPE_POLY;
    }

    /**
     * Sets the gravisto-path to the edges shape 'smooth line'
     * 
     * @param path_shape_smooth
     *            The path
     */
    public static void setPATH_SHAPE_SMOOTH(String path_shape_smooth) {
        PATH_SHAPE_SMOOTH = path_shape_smooth;
    }

    /**
     * Returns the gravisto-path to the edges shape 'smooth line'
     * 
     * @return Returns the path
     */
    public static String getPATH_SHAPE_SMOOTH() {
        return PATH_SHAPE_SMOOTH;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return Returns the path to the shiftset attribute.
     */
    public static String getPATH_SHIFTSET_GET() {
        return PATH_SHIFTSET_GET;
    }

    /**
     * Sets the level distance for the radial layout
     * 
     * @param radial_level_dist
     *            The distance.
     */
    public static void setRADIAL_LEVEL_DIST(double radial_level_dist) {
        RADIAL_LEVEL_DIST = radial_level_dist;
    }

    /**
     * Returns the level distance for the radial layout
     * 
     * @return Returns the distance
     */
    public static double getRADIAL_LEVEL_DIST() {
        return RADIAL_LEVEL_DIST;
    }

    /**
     * Sets the minimum node distance in the radial layout
     * 
     * @param radial_mindist
     *            The minimum distance
     */
    public static void setRADIAL_MINDIST(double radial_mindist) {
        RADIAL_MINDIST = radial_mindist;
    }

    /**
     * Returns the minimum node distance in the radial layout
     * 
     * @return Returns the minimum distance
     */
    public static double getRADIAL_MINDIST() {
        return RADIAL_MINDIST;
    }

    /**
     * Sets the scale step for the scaling algorithm. These step is added if the
     * minimum node distance is fallen short of.
     * 
     * @param radial_scale_step
     *            The scale step
     */
    public static void setRADIAL_SCALE_STEP(double radial_scale_step) {
        RADIAL_SCALE_STEP = radial_scale_step;
    }

    /**
     * Returns the scale step for the scaling algorithm.
     * 
     * @return Returns the scale step
     */
    public static double getRADIAL_SCALE_STEP() {
        return RADIAL_SCALE_STEP;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param sampling_type
     *            The sAMPLING_TYPE to set.
     */
    public static void setSAMPLING_TYPE(int sampling_type) {
        SAMPLING_TYPE = sampling_type;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return Returns the sAMPLING_TYPE.
     */
    public static int getSAMPLING_TYPE() {
        return SAMPLING_TYPE;
    }

    /**
     * Returns the initial value of the shift
     * 
     * @return Returns the initial shift value
     */
    public static double getSHIFT_INITIAL() {
        return SHIFT_INITIAL;
    }

    /**
     * Sets the distance for the top border of the drawing area
     * 
     * @param top_dist
     *            The distance
     */
    public static void setTOP_DIST(double top_dist) {
        TOP_DIST = top_dist;
    }

    /**
     * Returns the distance for the top border of the drawing area
     * 
     * @return Returns the distance
     */
    public static double getTOP_DIST() {
        return TOP_DIST;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
