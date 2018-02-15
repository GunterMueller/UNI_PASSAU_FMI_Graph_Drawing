/*==============================================================================
*
*   DummyPicture.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: DummyPicture.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.eval;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.jfree.chart.JFreeChart;
import org.visnacom.model.Edge;
import org.visnacom.model.Node;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;
import org.visnacom.sugiyama.algorithm.MetricLayout;
import org.visnacom.sugiyama.model.*;
import org.visnacom.view.Geometry;
import org.visnacom.view.Polyline;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.*;

/*
 * There are in the meanwhile several possiblities of drawing a graph.
 *
 * 1. SugiCompoundGraph s = new SugiCompoundGraph();
 * s.setDrawingStyle(SugiyamaDrawingStyle.FINAL_STYLE);
             Hierarchization.hierarchize(s);
             Normalization.normalize(s);
                   VertexOrdering.order(s);
                   MetricLayout.layout(s);
                   DummyPicture.show(s);
 *
 * 2.  CompoundGraph c = new Static();
 *  ...
 *  SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(c,
 *                      SugiyamaDrawingStyle.DEBUG_STYLE);
 *  sds.drawImpl();
 *  sds.show();
 *
 * 3. fills a Geometry object with coordinates and paints that.
 *   Geometry geo = new Geometry();
 *   CompoundGraph c = geo.getView();
 *   ...
 *   SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(geo);
 *   sds.draw(geo);
 *   DummyPicture.show(geo);
 *
 * 4. ViewPanel panel = new ViewPanel();
 *         Geometry geo = panel.getGeometry();
 *         geo.redraw();
 *  DummyPicture.show(geo);
 */

/**
 * My own Implementation of graphical representation. Either on the screen or
 * in a file.
 */
public class DummyPicture {
    //~ Static fields/initializers =============================================

    private static final float textSize = 10.0f;

    //        private static final double horizontalscl = 2.0;
    //        private static final double verticalscl = 2.0;
    private static double arrowLength = 6.0;
    private static double arrowWidth = 2.0;

    //borders for the compoundgraph
    static int leftmargin = 2;
    static int rightmargin = 2;
    static int topmargin = 2;
    static int bottommargin = 2;
    private static int roundRect_arc = 20;

    //used for the grey dashed rectangles that represent the levels
    private static final int leftGapLevel_Graph = 20;
    private static final int rightGapLevel_Graph = 5;
    private static float[] dash1 = {3.0f};
    private static BasicStroke dashed =
        new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
            3.0f, dash1, 0.0f);
    private static int levelStringRightGap = 5;
    private static int levelStringLeftGap = 5;
    private static int numIgnoredLetters = 2;

    /** indicates whether the id's of the nodes should be drawn. */
    public static boolean showIds = true;

    /** indicates whether the compoundlevel numbers should be drawn. */
    public static boolean showLevelNumbers = true;

    /** indicates whether the compoundlevels should be drawn. */
    public static boolean showLevels = false;
    private static SugiNode localHierarchyToDraw = null;
    public static final Rectangle DEFAULT_DIAGRAMM_SIZE =
        new Rectangle(250, 167);
    public static final Rectangle WIDER_DIAGRAMM_SIZE = new Rectangle(350, 167);

    //~ Methods ================================================================

    /**
     * shows the given object in a JFrame on the screen.
     *
     * @param toDraw is either a SugiCompoundGraph or a Geometry object or a
     *        JFreeChart
     */
    public static void show(Object toDraw) {
        JFrame frame = new JFrame();
        ImagePanel ip = new ImagePanel(frame, toDraw);
        frame.setContentPane(new JScrollPane(ip));

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.show();
        //        frame.pack();
        ip.repaint();
    }

    /**
     * shows the local hierarchy of v. (horizontal edges are not implemented).
     *
     * @param s DOCUMENT ME!
     * @param v DOCUMENT ME!
     */
    public static void show(SugiCompoundGraph s, SugiNode v) {
        localHierarchyToDraw = v;
        show(s);
        localHierarchyToDraw = null;
    }

    /**
     * writes the given compoundgraph into a file.
     *
     * @param s DOCUMENT ME!
     * @param filename DOCUMENT ME!
     * @param type either "pdf", "svg", or "png"
     */
    public static void write(SugiCompoundGraph s, String filename, String type) {
        AffineTransform graphPlacing = new AffineTransform();
        Rectangle rootRect = calculateSpace(s, graphPlacing);
        if(type.equals("pdf")) {
            writePdf(s, rootRect, graphPlacing, filename);
        } else {
            writePNG(s, rootRect, graphPlacing, filename);
        }
    }

    /**
     * writes the local hierarchy of v.
     *
     * @param s DOCUMENT ME!
     * @param v DOCUMENT ME!
     * @param filename DOCUMENT ME!
     * @param type DOCUMENT ME!
     */
    public static void write(SugiCompoundGraph s, SugiNode v, String filename,
        String type) {
        localHierarchyToDraw = v;
        write(s, filename, type);
        localHierarchyToDraw = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param geo DOCUMENT ME!
     * @param filename DOCUMENT ME!
     * @param type must be either "pdf" or "png"
     */
    public static void write(Geometry geo, String filename, String type) {
        AffineTransform graphPlacing = new AffineTransform();
        Rectangle rootRect = calculateSpace(geo, graphPlacing);
        if(type.equals("pdf")) {
            writePdf(geo, rootRect, graphPlacing, filename);
        } else {
            writePNG(geo, rootRect, graphPlacing, filename);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param chart DOCUMENT ME!
     * @param filename DOCUMENT ME!
     * @param type DOCUMENT ME!
     * @param size DOCUMENT ME!
     */
    public static void write(JFreeChart chart, String filename, String type,
        Rectangle size) {
        AffineTransform graphPlacing = new AffineTransform();
        graphPlacing.setToIdentity();

        //        Rectangle size = new Rectangle(150, 100);
        if(type == "pdf") {
            writePdf(chart, size, graphPlacing, filename);
        } else {
            System.err.println("writing of type (" + type + ") not implemented");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     * @param graphPlacing DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static Rectangle calculateSpace(SugiCompoundGraph s,
        AffineTransform graphPlacing) {
        Rectangle imageBounds;
        if(showLevels) {
            //        if(s.getDrawingStyle() == SugiyamaDrawingStyle.DEBUG_STYLE) {
            layoutLevels(s, graphPlacing);
            imageBounds = s.getMetricRoot().getClev().getBoundingRect();
        } else {
            imageBounds = s.getMetricRoot().getRect();
        }

        addBorderAndScale(imageBounds, graphPlacing);
        return imageBounds;
    }

    /**
     * DOCUMENT ME!
     *
     * @param geo DOCUMENT ME!
     * @param graphPlacing DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static Rectangle calculateSpace(Geometry geo, AffineTransform graphPlacing) {
        graphPlacing.setToIdentity();

        Rectangle imageBounds;
        if(showLevels) {
            SugiCompoundGraph s =
                ((SugiyamaDrawingStyle) geo.getDrawingStyle()).s;
            layoutLevels(s, graphPlacing);
            imageBounds = s.getMetricRoot().getClev().getBoundingRect();
        } else {
            imageBounds = new Rectangle(geo.shape(geo.getView().getRoot()));
        }

        addBorderAndScale(imageBounds, graphPlacing);
        return imageBounds;
    }

    /**
     * this method is used both from show and from write. delegates the call
     * depending on the runtime type of the given Object. The given
     * graphPlacing  transformation is used to draw the graph in the correct
     * place. g2 must have a clipRect, that matches the given transformation.
     * That means: the graph is always drawn with upper left corner in (0,0).
     * If that is not  wished, a AffineTransform can be passed. But then the
     * clippingArea must be set to indicate the user space.
     *
     * @param toDraw DOCUMENT ME!
     * @param graphPlacing DOCUMENT ME!
     * @param g2 DOCUMENT ME!
     */
    static void paintOnGraphics(Object toDraw, AffineTransform graphPlacing,
        Graphics2D g2) {
        float strokeWidth = 1.0f;

        assert g2.getClip() != null;
        //	liniendicke
        g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_MITER));
        // textgroesse
        g2.setFont(g2.getFont().deriveFont((float) 10.0));
        g2.setColor(Color.white);

        Rectangle savedClips = g2.getClipBounds();

        g2.transform(graphPlacing); //the transform corrupst my clipRect

        //        Rectangle image2 = g2.getClipBounds();
        g2.setClip(savedClips);
        g2.fill(savedClips);

        if(toDraw instanceof JFreeChart) {
            ((JFreeChart) toDraw).draw(g2, savedClips);
        } else if(toDraw instanceof SugiCompoundGraph) {
            paintOnGraphicsLevel((SugiCompoundGraph) toDraw, g2);
            paintOnGraphicsSugi((SugiCompoundGraph) toDraw, g2);
        } else if(toDraw instanceof Geometry) {
            Geometry geo = (Geometry) toDraw;
            SugiCompoundGraph s =
                ((SugiyamaDrawingStyle) geo.getDrawingStyle()).s;
            paintOnGraphicsLevel(s, g2);
            paintOnGraphicsGeo((Geometry) toDraw, g2);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param imageBounds DOCUMENT ME!
     * @param graphPlacing DOCUMENT ME!
     */
    private static void addBorderAndScale(Rectangle imageBounds,
        AffineTransform graphPlacing) {
        imageBounds.x -= leftmargin;
        imageBounds.y -= topmargin;
        imageBounds.width += leftmargin + rightmargin;
        imageBounds.height += topmargin + bottommargin;
        graphPlacing.translate(leftmargin, topmargin);

        //the scale does not work correctly. but is not necessary
        //        imageBounds.width = (int) (imageBounds.width * horizontalscl);
        //        imageBounds.height = (int) (imageBounds.height * verticalscl);
        //        graphPlacing.scale(horizontalscl, verticalscl);
    }

    /**
     * I can't explain the procedure intuitivly, but it works.
     *
     * @param startX DOCUMENT ME!
     * @param startY DOCUMENT ME!
     * @param endX DOCUMENT ME!
     * @param endY DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static double calculateAngle(double startX, double startY,
        double endX, double endY) {
        if(endY <= startY) {
            if(endX > startX) {
                //quadrant I
                return Math.atan((endY - startY) / (endX - startX));
            } else if(endX == startX) {
                return -Math.PI / 2.0;
            } else {
                //quadrant II
                return -Math.PI + Math.atan((endY - startY) / (endX - startX));
            }
        } else {
            if(endX < startX) {
                //quadrant III
                return -Math.PI + Math.atan((endY - startY) / (endX - startX));
            } else if(endX == startX) {
                return Math.PI / 2.0;
            } else {
                //quadrant IV
                return -Math.PI * 2
                + Math.atan((endY - startY) / (endX - startX));
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param level DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static int calculateLeftSpace(CompoundLevel level) {
        TextLayout layout = createLevelString(level);
        Rectangle2D bounds = layout.getBounds();
        return (int) Math.rint(bounds.getWidth()) + levelStringLeftGap
        + levelStringRightGap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param level DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static int calculateRightSpace(CompoundLevel level) {
        return 10;
    }

    /**
     * DOCUMENT ME!
     *
     * @param level DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static TextLayout createLevelString(CompoundLevel level) {
        Hashtable map = new Hashtable();
        map.put(TextAttribute.SIZE, new Float(textSize));

        Font font = Font.getFont(map);
        FontRenderContext frc = new FontRenderContext(null, false, false);
        String levelString = level.toString();
        if(levelString.length() >= numIgnoredLetters && showLevelNumbers) {
            levelString = levelString.substring(numIgnoredLetters);
        } else {
            levelString = " ";
        }

        return new TextLayout(levelString, font, frc);
    }

    /**
     * DOCUMENT ME!
     *
     * @param g2
     * @param start start point of the line
     * @param end end point of the line
     */
    private static void drawArrowHead(Graphics2D g2, Point2D start, Point2D end) {
        double startX = start.getX();
        double startY = start.getY();

        double endX = end.getX();
        double endY = end.getY();

        double alpha = calculateAngle(startX, startY, endX, endY);

        double tipX = endX + Math.cos(alpha) * arrowLength;
        double tipY = endY + Math.sin(alpha) * arrowLength;

        double arrowLeftX = endX + Math.cos(alpha + Math.PI / 2.0) * arrowWidth;
        double arrowLeftY = endY + Math.sin(alpha + Math.PI / 2.0) * arrowWidth;

        double arrowRightX =
            endX - Math.cos(alpha + Math.PI / 2.0) * arrowWidth;
        double arrowRightY =
            endY - Math.sin(alpha + Math.PI / 2.0) * arrowWidth;

        GeneralPath arrow = new GeneralPath();
        arrow.moveTo((float) tipX, (float) tipY);
        arrow.lineTo((float) arrowLeftX, (float) arrowLeftY);
        arrow.lineTo((float) arrowRightX, (float) arrowRightY);
        arrow.closePath();

        g2.setColor(Color.black);
        g2.fill(arrow);
    }

    /**
     * DOCUMENT ME!
     *
     * @param g2 DOCUMENT ME!
     * @param from DOCUMENT ME!
     * @param to DOCUMENT ME!
     * @param ctrlPts DOCUMENT ME!
     */
    private static void drawEdge(Graphics2D g2, Rectangle from, Rectangle to,
        List ctrlPts) {
        Point2D.Double start;
        Point2D.Double end;
        if(from.y < to.y) {
            start =
                new Point2D.Double(from.x + from.width * 0.5,
                    from.y + from.height);
            end = new Point2D.Double(to.x + to.width * 0.5, to.y);
        } else {
            start = new Point2D.Double(from.x + from.width * 0.5, from.y);
            end = new Point2D.Double(to.x + to.width * 0.5, to.y + to.height);
        }

        if(start.equals(end)) {
            return;
        }

        g2.setColor(Color.black);

        //start
        GeneralPath path = new GeneralPath();

        //        path.moveTo( (float)Math.rint(start.x), (float)Math.rint(start.y));
        path.moveTo((float) (start.x), (float) (start.y));

        //middlepart
        for(Iterator it = ctrlPts.iterator(); it.hasNext();) {
            Double nextP = (Double) it.next();

            if(!nextP.equals(start) && !nextP.equals(end)) {
                start = nextP;
                path.lineTo((float) start.getX(), (float) start.getY());
            } // else {

            //}
        }

        //end

        /* make the last segment shorter for a additional arrow head with the
         * formula   //a + (b-a)*(1-c/l) */
        double length = start.distance(end);
        Point2D.Double endOfLine = (Double) end.clone();
        endOfLine.x =
            start.x + (end.x - start.x) * (1.0 - arrowLength / length);
        endOfLine.y =
            start.y + (end.y - start.y) * (1.0 - arrowLength / length);
        path.lineTo((float) endOfLine.x, (float) endOfLine.y);

        g2.draw(path);
        drawArrowHead(g2, start, endOfLine);
    }

    /**
     * draws a gray dashed rectangle and a label of the level this methode is
     * not called for the root and the metricroot. the root is omitted in
     * layoutLevels, the metric root is omitted in paintOnGraphicsSugi. the
     * two initial levels are hidden during createLevelString.
     *
     * @param level DOCUMENT ME!
     * @param g2 DOCUMENT ME!
     */
    private static void drawLevel(CompoundLevel level, Graphics2D g2) {
        Rectangle rect = level.getDrawingRect();
        g2.setColor(Color.GRAY);

        Stroke oldStroke = g2.getStroke();
        g2.setStroke(dashed);
        g2.draw(rect);
        g2.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER));
        g2.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height);
        g2.setStroke(oldStroke);

        TextLayout layout = createLevelString(level);
        Point2D loc =
            new Point2D.Double(level.leftX + levelStringLeftGap,
                level.height / 2 + level.y + layout.getBounds().getHeight() / 2);
        layout.draw(g2, (float) loc.getX(), (float) loc.getY());
    }

    /**
     * draws a picture of a node.
     *
     * @param rect the shape of the node
     * @param isInnerNode indicates whether the nodes has children
     * @param isDummyNode indicates whether the node is a dummy node
     * @param id the node's id. if set to -1, no id is drawn.
     * @param g2 the Graphics object to draw in.
     */
    private static void drawNode(Rectangle rect, boolean isInnerNode,
        boolean isDummyNode, int id, Graphics2D g2) {
        if(isInnerNode) {
            if(isDummyNode) {
                g2.setColor(Color.GRAY.brighter());
                g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height,
                    roundRect_arc, roundRect_arc);
            }

            g2.setColor(Color.black);
            g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height,
                roundRect_arc, roundRect_arc);
        } else {
            if(isDummyNode) {
                g2.setColor(Color.GRAY);
                g2.fillRect(rect.x, rect.y, rect.width, rect.height);
            }

            g2.setColor(Color.black);
            g2.drawRect(rect.x, rect.y, rect.width, rect.height);
        }

        if(showIds) {
            if(id != -1) {
                g2.drawString("" + id, rect.x + 2, 10 + rect.y);
            }
        }
    }

    /**
     * calculates the rectangles of the levels. sets the transformation so that
     * the graph is drawn in correct place, the (0,0) is meant to be in the
     * upper left corner of the graph. So, the left bounds of the levels
     * should have negative coordinates
     *
     * @param s DOCUMENT ME!
     * @param transform DOCUMENT ME!
     */
    private static void layoutLevels(SugiCompoundGraph s,
        AffineTransform transform) {
        /* es ist ein zweistufiges verfahren
         * 1. relatives horizontales layout bezüglich des root-striches sowohl linker Rand als auch rechter.
         * 2. globales layout: für graph platz schaffen */
        ((SugiNode) s.getRoot()).getClev().resetAttributes();

        SortedSet set = new TreeSet();

        //the unused compoundlevels get now filtered out.
        for(Iterator it = s.getAllNodesIterator(); it.hasNext();) {
            SugiNode sn = (SugiNode) it.next();
            CompoundLevel clev = sn.getClev();
            if(s.getRoot() == s.getMetricRoot() || s.getRoot() != sn) {
                clev.initializeAttributes(sn.getAbsoluteY(), sn.getHeight());
                set.add(clev);
            }
        }

        Iterator setIt = set.iterator();

        CompoundLevel root = (CompoundLevel) setIt.next();
        assert root == s.getMetricRoot().getClev();

        // compute local coordinates
        List l = new LinkedList();
        l.add(root);
        localLayout(0, 0, l);

        int maxLeftX = 0;
        int minRightX = 0;
        for(Iterator it = set.iterator(); it.hasNext();) {
            CompoundLevel level = (CompoundLevel) it.next();
            maxLeftX = Math.max(maxLeftX, level.leftXafterLabel);
            minRightX = Math.min(minRightX, level.rightX);
        }

        //the origin of the graphics will be translated to the right
        //left end of rectangles to be moved to the left
        //right end of rectangles to be moved to the right
        int leftShift = maxLeftX + leftGapLevel_Graph;
        int rightShift =
            s.getMetricRoot().getWidth() - minRightX + rightGapLevel_Graph;
        for(Iterator it = set.iterator(); it.hasNext();) {
            CompoundLevel level = (CompoundLevel) it.next();
            level.leftX -= leftShift;
            level.leftXafterLabel -= leftShift;
            level.rightX += rightShift;
        }

        transform.setToTranslation(leftShift, 0);
    }

    /**
     * calculates local coordinates referring to the root level
     *
     * @param leftOffset the x value where the drawing of the children should
     *        start
     * @param rightOffset DOCUMENT ME!
     * @param levelList contains all level of the same depth
     */
    private static void localLayout(int leftOffset, int rightOffset,
        List levelList) {
        assert rightOffset <= 0;
        assert leftOffset >= 0;

        int maxLeftSpace = 10;
        int maxRightSpace = 0;
        for(Iterator it = levelList.iterator(); it.hasNext();) {
            CompoundLevel level = (CompoundLevel) it.next();
            maxLeftSpace = Math.max(maxLeftSpace, calculateLeftSpace(level));
            maxRightSpace = Math.max(maxRightSpace, calculateRightSpace(level));
        }

        List l = new LinkedList();
        for(Iterator it = levelList.iterator(); it.hasNext();) {
            CompoundLevel level = (CompoundLevel) it.next();
            l.addAll(level.getChildren());
            level.leftX = leftOffset;
            level.leftXafterLabel = leftOffset + maxLeftSpace;
            level.rightX = rightOffset;
            localLayout(maxLeftSpace + leftOffset, rightOffset - maxRightSpace,
                l);
        }
    }

    /**
     * paints a picture of the given geometry object.
     *
     * @param geo DOCUMENT ME!
     * @param g2 DOCUMENT ME!
     */
    private static void paintOnGraphicsGeo(Geometry geo, Graphics2D g2) {
        for(Iterator it = geo.getView().getAllNodesIterator(); it.hasNext();) {
            Node n = (Node) it.next();
            Rectangle rect = geo.shape(n);
            drawNode(rect, geo.getView().hasChildren(n), false, n.getId(), g2);
        }

        for(Iterator it = geo.getView().getAllEdgesIterator(); it.hasNext();) {
            Polyline pl = geo.shape((Edge) it.next());
            drawEdge(g2, pl.getStart(), pl.getEnd(), pl.getControlPoints());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     * @param g2 DOCUMENT ME!
     */
    private static void paintOnGraphicsLevel(SugiCompoundGraph s, Graphics2D g2) {
        if(!showLevels) {
            return;
        }

        List l = new LinkedList();
        s.getMetricRoot().getClev().getLevelSubTree(l);
        for(Iterator it = l.iterator(); it.hasNext();) {
            CompoundLevel level = (CompoundLevel) it.next();

            if(level.isUsed()) {
                drawLevel(level, g2);
            }
        }
    }

    /**
     * paints an image of the given sugicompoundgraph. The picture will contain
     * all dummy nodes.
     *
     * @param s DOCUMENT ME!
     * @param g2 DOCUMENT ME!
     */
    private static void paintOnGraphicsSugi(SugiCompoundGraph s, Graphics2D g2) {
        for(Iterator it = s.getAllNodes().iterator(); it.hasNext();) {
            SugiNode n = (SugiNode) it.next();
            if(n != s.getMetricRoot() && n != s.getRoot()) {
                if(localHierarchyToDraw == null || n == localHierarchyToDraw) {
                    Rectangle rect = n.getRect();
                    drawNode(rect, s.hasChildren(n), n.isDummyNode(),
                        n.getId(), g2);
                } else {
                    if(s.getParent(n) == localHierarchyToDraw) {
                        //children of n to draw tight
                        Rectangle rect = n.getRect();
                        int oldWidth = rect.width;
                        rect.width =
                            Math.min(rect.width,
                                (int) (MetricLayout.basicWidth * 1.5));
                        rect.x -= (rect.width - oldWidth) / 2;
                        drawNode(rect, s.hasChildren(n), n.isDummyNode(),
                            n.getId(), g2);
                    }
                }
            }
        }

        /* kanten malen */
        List emptyList = new LinkedList();
        for(Iterator it = s.getAllEdges().iterator(); it.hasNext();) {
            SugiEdge e = (SugiEdge) it.next();
            drawEdge(g2, ((SugiNode) e.getSource()).getRect(),
                ((SugiNode) e.getTarget()).getRect(), emptyList);
        }
    }

    //    private static void testArrowHead() {
    //        double r = 40.0;
    //
    //        for(double theta = -Math.PI; theta < Math.PI; theta += 0.25) {
    //            JFrame frame = new JFrame();
    ////            ImagePanel ip = new ImagePanel(frame, null);
    //            JPanel view = new JPanel();
    //            frame.setContentPane(view);
    //            frame.pack();
    //            BufferedImage image =
    //                (BufferedImage) view.createImage((100),
    //                    (100));
    //            Graphics2D g2 = image.createGraphics();
    //            g2.setTransform(AffineTransform.getScaleInstance(horizontalscl,
    //                    verticalscl));
    //            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
    //                RenderingHints.VALUE_ANTIALIAS_ON);
    //            g2.setBackground(Color.white);
    //            g2.clearRect(0, 0, image.getWidth(), image.getHeight());
    //
    //            Point2D start =
    //                new Point2D.Double(50.0 + r * Math.cos(theta),
    //                    50.0 + r * Math.sin(theta));
    //            drawArrowHead(g2, start, new Point2D.Double(50.0, 50.0));
    //            frame.pack();
    //            frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    //            frame.show();
    //        }
    //      Point2D start = new Point2D.Double(50.0,50.0);
    //      for(int i = 0; i <= 100; i+=10) {
    //          for(int j = 0; j <= 100; j+=10) {
    //              Point2D end = new Point2D.Double(i,j);
    //              System.out.println(end + " -> " + Math.toDegrees(calculateAngle(start, end)));
    //          }
    //      }
    //    }

    /**
     * DOCUMENT ME!
     *
     * @param toWrite DOCUMENT ME!
     * @param imageSize DOCUMENT ME!
     * @param graphPlacing DOCUMENT ME!
     * @param filename DOCUMENT ME!
     */
    private static void writePNG(Object toWrite, Rectangle imageSize,
        AffineTransform graphPlacing, String filename) {
        JFrame frame = new JFrame();
        Container jp = frame.getContentPane();
        frame.pack();

        BufferedImage result =
            (BufferedImage) jp.createImage(imageSize.width, imageSize.height);
        Graphics2D g2 = result.createGraphics();

        g2.setClip(imageSize);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);

        paintOnGraphics(toWrite, graphPlacing, g2);

        try {
            ImageIO.write(result, "png", new File(filename + ".png"));
        } catch(IOException e) {
            System.err.println(e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param toWrite DOCUMENT ME!
     * @param imageSize DOCUMENT ME!
     * @param graphPlacing DOCUMENT ME!
     * @param filename DOCUMENT ME!
     */
    private static void writePdf(Object toWrite, Rectangle imageSize,
        AffineTransform graphPlacing, String filename) {
        com.lowagie.text.Rectangle pdfSize =
            new com.lowagie.text.Rectangle(imageSize.width, imageSize.height);

        Document document = new Document(pdfSize, 0.0f, 0.0f, 0.0f, 0.0f);

        //        Document.compress = false;
        try {
            PdfWriter writer =
                PdfWriter.getInstance(document,
                    new FileOutputStream(filename + ".pdf"));

            document.open();

            PdfContentByte cb = writer.getDirectContent();
            Graphics2D g2d =
                cb.createGraphics(pdfSize.width(), pdfSize.height(),
                    new DefaultFontMapper());

            g2d.setClip(imageSize);

            paintOnGraphics(toWrite, graphPlacing, g2d);
            g2d.dispose();
        } catch(DocumentException de) {
            de.printStackTrace();
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }

        document.close();
    }

    //~ Inner Classes ==========================================================

    /**
     *
     */
    public static class ImagePanel extends JPanel {
        //public Geometry geo;
        public JFrame frame;
        public Object toDraw;
        private BufferedImage image;

        /**
         * Creates a new ImagePanel object.
         *
         * @param frame DOCUMENT ME!
         * @param toDraw DOCUMENT ME!
         */
        public ImagePanel(JFrame frame, Object toDraw) {
            image = null;
            this.toDraw = toDraw;
            this.frame = frame;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public Dimension getPreferredSize() {
            if(image != null) {
                return new Dimension(image.getWidth(), image.getHeight());
            } else {
                return new Dimension(0, 0);
            }
        }

        /**
         * @see java.awt.Component#createImage(int, int)
         */
        public Image createImage(int width, int height) {
            BufferedImage i = (BufferedImage) super.createImage(width, height);
            image = i;
            setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
            return i;
        }

        /**
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setBackground(Color.white);
            super.paintComponent(g);
            if(image != null) {
                g2.drawImage(image, null, null);
            }
        }

        /**
         * @see java.awt.Component#repaint()
         */
        public void repaint() {
            if(toDraw != null) {
                AffineTransform graphPlacing = new AffineTransform();
                int oldWidth = 0;
                int oldHeight = 0;
                if(image != null) {
                    oldWidth = image.getWidth();
                    oldHeight = image.getHeight();
                }

                Rectangle imageSize;

                if(toDraw instanceof SugiCompoundGraph) {
                    imageSize =
                        calculateSpace((SugiCompoundGraph) toDraw, graphPlacing);
                } else if(toDraw instanceof Geometry) {
                    imageSize = calculateSpace((Geometry) toDraw, graphPlacing);
                } else if(toDraw instanceof JFreeChart) {
                    imageSize = new Rectangle(600, 400);
                } else {
                    imageSize = null;
                }

                BufferedImage result =
                    (BufferedImage) createImage(imageSize.width,
                        imageSize.height);

                Graphics2D g2 = result.createGraphics();
                g2.setClip(imageSize);

                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

                //                Graphics2D g3 = new DebugGraphics(g2);
                paintOnGraphics(toDraw, graphPlacing, g2); //g3

                if(oldWidth != imageSize.getWidth()
                    || oldHeight != imageSize.getHeight()) {
                    if(frame != null) {
                        frame.pack();
                    }
                }
            }

            //            super.repaint();
        }
    }
}
