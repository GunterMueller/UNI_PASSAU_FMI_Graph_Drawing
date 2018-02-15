// =============================================================================
//
//   GmlConstants.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GmlConstants.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.ios.gml;

import org.graffiti.attributes.Attribute;
import org.graffiti.graphics.GraphicAttributeConstants;

/**
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2006-03-24 21:04:58 +0100 (Fr, 24 Mrz 2006)
 *          $
 */
public interface GmlConstants {
    public static final String ID = "id";

    public static final String WEIGHT = "weight";

    public static final String CAPACITY = "capacity";

    public static final String GML_QUOTES = "\"";

    public static final String GRAPHIC_PATH = Attribute.SEPARATOR
            + GraphicAttributeConstants.GRAPHICS + Attribute.SEPARATOR;

    public static final String FILLCOLOR_PATH = Attribute.SEPARATOR
            + GraphicAttributeConstants.FILLCOLOR_PATH;

    public static final String FRAMECOLOR_PATH = GRAPHIC_PATH
            + GraphicAttributeConstants.FRAMECOLOR;

    public static final String FOREGROUND_PATH = GRAPHIC_PATH + "foreground";

    public static final String BACKGROUND_PATH = GRAPHIC_PATH + "background";

    public static final String FRAMETHICKNESS_PATH = Attribute.SEPARATOR
            + GraphicAttributeConstants.FRAMETHICKNESS_PATH;

    public static final String SHAPE_PATH = Attribute.SEPARATOR
            + GraphicAttributeConstants.SHAPE_PATH;

    public static final String LABEL_TEXTCOLOR_PATH = Attribute.SEPARATOR
            + GraphicAttributeConstants.LABEL + Attribute.SEPARATOR
            + GraphicAttributeConstants.TEXTCOLOR;

    public static final String LABEL_LABEL_PATH = Attribute.SEPARATOR
            + GraphicAttributeConstants.LABEL + Attribute.SEPARATOR
            + GraphicAttributeConstants.LABEL;

    public static final String LABEL_PATH = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + GraphicAttributeConstants.LABEL;

    public static final String WEIGHT_LABEL_PATH = Attribute.SEPARATOR + WEIGHT
            + Attribute.SEPARATOR + GraphicAttributeConstants.LABEL;

    public static final String CAPACITY_LABEL_PATH = Attribute.SEPARATOR
            + CAPACITY + Attribute.SEPARATOR + GraphicAttributeConstants.LABEL;

    public static final String ARROW_PATH = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + "arrow";

    public static final String TYPE_PATH = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + "type";

    public static final String JOINSTYLE_PATH = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + "joinstyle";

    public static final String BENDS_PATH_START = Attribute.SEPARATOR
            + GraphicAttributeConstants.BENDS_PATH + Attribute.SEPARATOR;

    public static final String SOURCE_PATH = GRAPHIC_PATH
            + GraphicAttributeConstants.DOCKING + Attribute.SEPARATOR
            + GraphicAttributeConstants.SOURCE;

    public static final String TARGET_PATH = GRAPHIC_PATH
            + GraphicAttributeConstants.DOCKING + Attribute.SEPARATOR
            + GraphicAttributeConstants.TARGET;

    public static final String ARROWHEAD_PATH = GRAPHIC_PATH
            + GraphicAttributeConstants.ARROWHEAD;

    public static final String ARROWTAIL_PATH = GRAPHIC_PATH
            + GraphicAttributeConstants.ARROWTAIL;

    public static final String THICKNESS_PATH = GRAPHIC_PATH
            + GraphicAttributeConstants.THICKNESS;

    public static final String LINETYPE_PATH = GRAPHIC_PATH
            + GraphicAttributeConstants.LINETYPE;

    public static final String DIRECTED = Attribute.SEPARATOR + "directed";

    public static final String SMOOTH_PATH = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + "smooth";

    public static final String COORDZ_PATH = GraphicAttributeConstants.COORD_PATH
            + Attribute.SEPARATOR + "z";

    public static final String DIMD_PATH = GraphicAttributeConstants.DIM_PATH
            + Attribute.SEPARATOR + "depth";

    public static final String GRAPHICS_X = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + GraphicAttributeConstants.X;

    public static final String GRAPHICS_Y = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + GraphicAttributeConstants.Y;

    public static final String GRAPHICS_Z = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + "z";

    public static final String GRAPHICS_W = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + "w";

    public static final String GRAPHICS_H = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + "h";

    public static final String GRAPHICS_D = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + "d";

    public static final String ARROWHEAD = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + GraphicAttributeConstants.ARROWHEAD;

    public static final String TRANSPARENCY = "transparency";

    public static final String _TRANSPARENCY = "_" + TRANSPARENCY;

    public static final String TRANSPARENCY_PATH = Attribute.SEPARATOR
            + TRANSPARENCY;

    public static final String RED = "red";

    public static final String BLUE = "blue";

    public static final String GREEN = "green";

    public static final String STYLE = "style";

    public static final String GML_WIDTH_PATH = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + GraphicAttributeConstants.WIDTH;

    public static final String GML_BACKGROUND_PATH = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + "background";

    public static final String GML_FOREGROUND_PATH = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + "foreground";

    public static final String GML_FILL_PATH = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + "fill";

    public static final String GML_OUTLINE_PATH = GraphicAttributeConstants.GRAPHICS
            + Attribute.SEPARATOR + "outline";

    public static final String GML_LABELFILL_PATH = "label_graphics"
            + Attribute.SEPARATOR + "fill";

    // public static final String

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
