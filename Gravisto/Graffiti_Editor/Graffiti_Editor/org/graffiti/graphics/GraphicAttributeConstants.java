// =============================================================================
//
//   GraphicAttributeConstants.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphicAttributeConstants.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;

import org.graffiti.attributes.Attribute;

/**
 * Defines constants used when accessing graphics attributes.
 */
public interface GraphicAttributeConstants {

    // the names of the graphic attributes

    /** DOCUMENT ME! */
    public static final String GRAPHICS = "graphics";

    /** DOCUMENT ME! */
    public static final String BGIMAGE = "backgroundImage";

    /** DOCUMENT ME! */
    public static final String FRAMECOLOR = "framecolor";

    /** DOCUMENT ME! */
    public static final String FILLCOLOR = "fillcolor";

    /** DOCUMENT ME! */
    public static final String ARROWHEAD = "arrowhead";

    /** DOCUMENT ME! */
    public static final String ARROWTAIL = "arrowtail";

    /** DOCUMENT ME! */
    public static final String THICKNESS = "thickness";

    /** DOCUMENT ME! */
    public static final String DOCKING = "docking";

    /** DOCUMENT ME! */
    public static final String LINETYPE = "linetype";

    /**
     * Identifies the depth attribute of an edge.
     * 
     * @see EdgeGraphicAttribute#getDepth()
     */
    public static final String DEPTH = "depth";

    /** DOCUMENT ME! */
    public static final String BENDS = "bends";

    /** DOCUMENT ME! */
    public static final String LABEL = "label";

    /** DOCUMENT ME! */
    public static final String FRAMETHICKNESS = "frameThickness";

    /** DOCUMENT ME! */
    public static final String LINEMODE = "linemode";

    /** DOCUMENT ME! */
    public static final String COORDINATE = "coordinate";

    /** DOCUMENT ME! */
    public static final String DIMENSION = "dimension";

    /** DOCUMENT ME! */
    public static final String SHAPE = "shape";

    /** DOCUMENT ME! */
    public static final String SHAPEDESCRIPTION = "shapedescription";

    /** DOCUMENT ME! */
    public static final String PORT = "port";

    /** DOCUMENT ME! */
    public static final String PORTS = "ports";

    /** DOCUMENT ME! */
    public static final String SOURCE = "source";

    /** DOCUMENT ME! */
    public static final String TARGET = "target";

    /** DOCUMENT ME! */
    public static final String RED = "red";

    /** DOCUMENT ME! */
    public static final String GREEN = "green";

    /** DOCUMENT ME! */
    public static final String BLUE = "blue";

    /** DOCUMENT ME! */
    public static final String OPAC = "transparency";

    /** DOCUMENT ME! */
    public static final String X = "x";

    /** DOCUMENT ME! */
    public static final String Y = "y";

    /**
     * Identifies the z-component (depth value) of a {@link CoordinateAttribute}
     * .
     */
    public static final String Z = "z";

    /** DOCUMENT ME! */
    public static final String HEIGHT = "height";

    /** DOCUMENT ME! */
    public static final String WIDTH = "width";

    /** DOCUMENT ME! */
    public static final String NAME = "name";

    /** DOCUMENT ME! */
    public static final String TILED = "tiled";

    /** DOCUMENT ME! */
    public static final String MAXIMIZE = "maximize";

    /** DOCUMENT ME! */
    public static final String IMAGE = "image";

    /** DOCUMENT ME! */
    public static final String REF = "reference";

    /** DOCUMENT ME! */
    public static final String POSITION = "position";

    /** DOCUMENT ME! */
    public static final String ALIGNMENT = "alignment";

    /** DOCUMENT ME! */
    public static final String FONT = "font";

    public static final String FONT_SIZE = "fontSize";

    public static final String MAX_WIDTH = "maxWidth";

    /** DOCUMENT ME! */
    public static final String TEXTCOLOR = "textcolor";

    // for NodeLabelPosition:

    public static final String ABSOLUTE_X_OFFSET = "absoluteXOffset";
    public static final String ABSOLUTE_Y_OFFSET = "absoluteYOffset";
    public static final String RELATIVE_X_OFFSET = "relativeXOffset";
    public static final String RELATIVE_Y_OFFSET = "relativeYOffset";
    public static final String ALIGNMENT_X = "alignmentX";
    public static final String ALIGNMENT_Y = "alignmentY";
    public static final String ROTATION = "rotation";

    // for EdgeLabelPosition:

    /** DOCUMENT ME! */
    public static final String RELATIVE_ALIGNMENT = "relativeAlignment";

    /** DOCUMENT ME! */
    public static final String ALIGNMENT_SEGMENT = "alignSegment";

    /** DOCUMENT ME! */
    public static final String IN = "ingoing";

    /** DOCUMENT ME! */
    public static final String OUT = "outgoing";

    /** DOCUMENT ME! */
    public static final String COMMON = "common";

    /** DOCUMENT ME! */
    public static final String STRAIGHTLINE = "straightline";

    /** DOCUMENT ME! */
    public static final String POLYLINE = "polyline";

    /** DOCUMENT ME! */
    public static final String SQUARESPLINE = "squarespline";

    /** The id of the circle center belonging to the CircleLineSegmentationShape */
    public static final String CIRCLE_CENTER = "circlecenter";

    /** DOCUMENT ME! */
    public static final String CUBICSPLINE = "cubicspline";

    /** DOCUMENT ME! */
    public static final String SMOOTHLINE = "smoothline";

    /** Constants used to specify the position of a label according to a node. */
    public static final String CENTERED = "centered";
    public static final String LEFT_OUTSIDE = "left outside";
    public static final String LEFT_INSIDE = "left inside";
    public static final String RIGHT_INSIDE = "right inside";
    public static final String RIGHT_OUTSIDE = "right outside";
    public static final String TOP_OUTSIDE = "top outside";
    public static final String TOP_INSIDE = "top inside";
    public static final String BOTTOM_INSIDE = "bottom inside";
    public static final String BOTTOM_OUTSIDE = "bottom outside";

    /** Constant used to specify the position of a label according to a node. */
    public static final String NEARSOURCE = "nearsource";

    /** Constant used to specify the position of a label according to a node. */
    public static final String NEARTARGET = "neartarget";

    /**
     * Distance between a label and the surrounding rectangle of the according
     * node.
     */
    public static final double LABEL_DISTANCE = 4.0d;

    /**
     * Path to the background image.
     */
    public static final String BGIMAGE_PATH = GRAPHICS + Attribute.SEPARATOR
            + BGIMAGE;

    /**
     * Path at which label attributes are placed in the attribute hierarchy by
     * default.
     */
    public static final String LABEL_ATTRIBUTE_PATH = "";

    /** Path to coordinate attribute */
    public static final String COORD_PATH = GRAPHICS + Attribute.SEPARATOR
            + COORDINATE;

    /** Path to x-coordinate attribute */
    public static final String COORDX_PATH = COORD_PATH + Attribute.SEPARATOR
            + X;

    /** Path to y-coordinate attribute */
    public static final String COORDY_PATH = COORD_PATH + Attribute.SEPARATOR
            + Y;

    /** Path to z-coordinate (depth) attribute */
    public static final String COORDZ_PATH = COORD_PATH + Attribute.SEPARATOR
            + Z;

    /** Path to dimension attribute */
    public static final String DIM_PATH = GRAPHICS + Attribute.SEPARATOR
            + DIMENSION;

    /** Path to width attribute */
    public static final String DIMW_PATH = DIM_PATH + Attribute.SEPARATOR
            + WIDTH;

    /** Path to height attribute */
    public static final String DIMH_PATH = DIM_PATH + Attribute.SEPARATOR
            + HEIGHT;

    /** Path to fill color attribute */
    public static final String FILLCOLOR_PATH = GRAPHICS + Attribute.SEPARATOR
            + FILLCOLOR;

    /** Path to outline color attribute */
    public static final String OUTLINE_PATH = GRAPHICS + Attribute.SEPARATOR
            + FRAMECOLOR;

    /** Path to line width attribute */
    public static final String LINEWIDTH_PATH = GRAPHICS + Attribute.SEPARATOR
            + FRAMETHICKNESS;

    /** Path to line mode attribute */
    public static final String LINEMODE_PATH = GRAPHICS + Attribute.SEPARATOR
            + LINEMODE;

    /**
     * Path to depth attribute.
     * 
     * @see EdgeGraphicAttribute#getDepth()
     * @see #DEPTH
     */
    public static final String DEPTH_PATH = GRAPHICS + Attribute.SEPARATOR
            + DEPTH;

    /** Path to shape attribute */
    public static final String SHAPE_PATH = GRAPHICS + Attribute.SEPARATOR
            + SHAPE;

    /** Path to shape description attribute */
    public static final String SHAPE_DESC_PATH = GRAPHICS + Attribute.SEPARATOR
            + SHAPEDESCRIPTION;

    /** Path to bends attribute */
    public static final String BENDS_PATH = GRAPHICS + Attribute.SEPARATOR
            + BENDS;

    /** Path to ports attribute */
    public static final String PORTS_PATH = GRAPHICS + Attribute.SEPARATOR
            + PORTS;

    /** Path to docking attribute */
    public static final String DOCKING_PATH = GRAPHICS + Attribute.SEPARATOR
            + DOCKING;

    /** Path to frame thickness attribute */
    public static final String FRAMETHICKNESS_PATH = GRAPHICS
            + Attribute.SEPARATOR + FRAMETHICKNESS;

    /** Path to thickness attribute */
    public static final String THICKNESS_PATH = GRAPHICS + Attribute.SEPARATOR
            + THICKNESS;

    /**
     * The path to the CoordinateAttribute of the center of the circle of a
     * CircleLineSegmentationShape.
     */
    public static final String CIRCLE_CENTER_PATH = GRAPHICS
            + Attribute.SEPARATOR + CIRCLE_CENTER;

    /** rectangle node shape class name */
    public static final String RECTANGLE_CLASSNAME = "org.graffiti.plugins.views.defaults.RectangleNodeShape";

    /** ellipse node shape class name */
    public static final String ELLIPSE_CLASSNAME = "org.graffiti.plugins.views.defaults.EllipseNodeShape";

    /** closed polygonal shape class name */
    public static final String POLYCLOSED_CLASSNAME = "org.graffiti.plugins.shapes.nodes.polygon.PolygonalNodeShape";

    /** polyline edge shape class name */
    public static final String POLYLINE_CLASSNAME = "org.graffiti.plugins.views.defaults.PolyLineEdgeShape";

    /** simple straight line edge shape class name */
    public static final String STRAIGHTLINE_CLASSNAME = "org.graffiti.plugins.views.defaults.StraightLineEdgeShape";

    public static final String CIRCLE_CLASSNAME = "org.graffiti.plugins.views.defaults.CircleNodeShape";

    /** smooth line edge shape class name */
    public static final String SMOOTH_CLASSNAME = "org.graffiti.plugins.views.defaults.SmoothLineEdgeShape";

    /** quadratic spline edge shape class name */
    public static final String SQUARESPLINE_CLASSNAME = "org.graffiti.plugins.views.defaults.QuadCurveEdgeShape";

    /** cubic spline edge shape class name; not yet implemented. */
    public static final String CUBICSPLINE_CLASSNAME = "org.graffiti.plugins.views.defaults.CubicCurveEdgeShape";

    /** spiral edge shape class name */
    public static final String SPIRAL_CLASSNAME = "org.graffiti.plugins.algorithms.core.SpiralShape";

    /** circle line segmentation edge shape class name */
    public static final String CIRCLE_LINE_SEGMENTATION_CLASSNAME = "org.graffiti.plugins.shapes.edges.circleLineSegmentationShape.CircleLineSegmentationShape";

    /** intra level edge shape class name */
    public static final String INTRA_LEVEL_CLASSNAME = "org.graffiti.plugins.algorithms.core.IntraLevelShape";

    /** offset of an edge */
    public static final String OFFSET = "offset";

    /** start angle of an edge */
    public static final String START_ANGLE = "startAngle";

    /** end angle of an edge */
    public static final String END_ANGLE = "endAngle";

    /** level of a node */
    public static final String LEVEL = "level";

    /**
     * The cap used by default.
     * 
     * @see java.awt.BasicStroke
     */
    public static int DEFAULT_CAP = BasicStroke.CAP_BUTT;

    /**
     * The join used by default.
     * 
     * @see java.awt.BasicStroke
     */
    public static int DEFAULT_JOIN = BasicStroke.JOIN_ROUND;

    /**
     * The miter limit used by default.
     * 
     * @see java.awt.BasicStroke
     */
    public static float DEFAULT_MITER = 10.0f;

    /**
     * The default node size.
     */
    public static final Dimension DEFAULT_NODE_SIZE = new Dimension(25, 25);

    /**
     * The default graphic element frame thickness.
     */
    public static final double DEFAULT_GRAPHIC_ELEMENT_FRAMETHICKNESS = 3.0;

    /**
     * The default node frame thickness.
     */
    public static final double DEFAULT_NODE_FRAMETHICKNESS = 3.0;

    /**
     * The default edge frame thickness.
     */
    public static final double DEFAULT_EDGE_FRAMETHICKNESS = 1.0;

    /**
     * The default edge thickness.
     */
    public static final double DEFAULT_EDGE_THICKNESS = 1.0;

    /**
     * The default node shape.
     */
    public static final String DEFAULT_NODE_SHAPE = RECTANGLE_CLASSNAME;

    /**
     * The default graphic element frame color..
     */
    public static final Color DEFAULT_GRAPHIC_ELEMENT_FRAMECOLOR = Color.BLACK;

    /**
     * The default edge fill color.
     */
    public static final Color DEFAULT_EDGE_FILLCOLOR = Color.BLACK;

    /**
     * The default edge frame color.
     */
    public static final Color DEFAULT_EDGE_FRAMECOLOR = Color.BLACK;

    /**
     * The default node frame color.
     */
    public static final Color DEFAULT_NODE_FRAMECOLOR = Color.BLACK;

    /**
     * The default node fill color.
     */
    public static final Color DEFAULT_NODE_FILLCOLOR = Color.WHITE;

    public static final int DEFAULT_FONT_SIZE = 12;

    public static final double DEFAULT_MAX_WIDTH = 1000.0;

    public static final String BEND = "bend";

    public static final String EDGE = "edge";

    public static final String NODE = "node";

    public static final String QUAD_CURVE_EDGE_SHAPE = "QuadCurveEdgeShape";

    public static final String SMOOTH_LINE_EDGE_SHAPE = "SmoothLineEdgeShape";

    public static final String STRAIGHT_LINE_EDGE_SHAPE = "StraightLineEdgeShape";

    /**
     * The name of the CircleLineSegmentationShapePlugin.
     */
    public static final String CIRCLE_LINE_SEGMENTATION_SHAPE = "Circle and Line Segmenentation";

    public static final String LINE_POINT_PATH = GRAPHICS + Attribute.SEPARATOR
            + "Line" + Attribute.SEPARATOR + "point";

    public static final String ARROWSHAPE_CLASSNAME = "org.graffiti.plugins.views.defaults.StandardArrowShape";

    public static final String GRID = "grid";

    public static final String GRID_PATH = GRAPHICS + Attribute.SEPARATOR
            + GRID;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
