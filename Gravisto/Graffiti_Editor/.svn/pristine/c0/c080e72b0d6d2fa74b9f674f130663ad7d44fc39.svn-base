// =============================================================================
//
//   Java2DFastView.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.torical;

import java.awt.geom.Point2D;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphGraphicAttribute;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.plugin.view.interactive.ViewFamily;
import org.graffiti.plugins.grids.ToricalGrid;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.java2d.Java2DFastView;

/**
 * @author Wolfgang Brunner
 * @version $Revision$ $Date$
 */
public class ToricalFastView extends Java2DFastView {

    /**
     * 
     */
    private static final long serialVersionUID = 4451189883309744833L;
    public static final ToricalFastViewFamily TORICAL_FAST_VIEW_FAMILY = new ToricalFastViewFamily();

    static {
        new WindAttributeHandler();
    }

    public ToricalFastView() {
        super(new ToricalEngine());
        engine = (ToricalEngine) getGraphicsEngine();
        ((ToricalEngine) engine).setView(this);
        ToricalGrid tg = new ToricalGrid();
        grid = tg;
    }

    protected Point2D transformOnTorus(Point2D p) {
        Point2D newPoint = new Point2D.Double(p.getX(), p.getY());
        double torusWidth = ((ToricalEngine) engine).getTorusWidth();
        double torusHeight = ((ToricalEngine) engine).getTorusHeight();
        if (torusWidth != 0) {
            newPoint
                    .setLocation(
                            ((newPoint.getX() + 25 + torusWidth * 1000) % torusWidth) - 25,
                            newPoint.getY());
        }
        if (torusHeight != 0) {
            newPoint
                    .setLocation(
                            newPoint.getX(),
                            ((newPoint.getY() + 25 + torusHeight * 1000) % torusHeight) - 25);
        }
        return newPoint;
    }

    protected Point2D roundToNearestCopy(Point2D toRound, Point2D target) {
        double torusWidth = ((ToricalEngine) engine).getTorusWidth();
        double torusHeight = ((ToricalEngine) engine).getTorusHeight();
        double dx = Math.round(((toRound.getX() - target.getX()) / torusWidth))
                * torusWidth;
        double dy = Math
                .round(((toRound.getY() - target.getY()) / torusHeight))
                * torusHeight;
        return new Point2D.Double(target.getX() + dx, target.getY() + dy);
    }

    protected int calculateWindX(double startNode, double start,
            double endNode, double end) {
        double torusWidth = ((ToricalEngine) engine).getTorusWidth();
        return (int) Math.round(((startNode - start) - (endNode - end))
                / torusWidth);
    }

    protected int calculateWindY(double startNode, double start,
            double endNode, double end) {
        double torusHeight = ((ToricalEngine) engine).getTorusHeight();
        return (int) Math.round(((startNode - start) - (endNode - end))
                / torusHeight);
    }

    @Override
    public ViewFamily<FastView> getFamily() {
        return TORICAL_FAST_VIEW_FAMILY;
    }

    @Override
    public CollectionAttribute getGraphAttribute() {
        GraphGraphicAttribute gga = new GraphGraphicAttribute();
        GridAttribute gridAttr = new GridAttribute("grid");
        gridAttr.setGrid(new ToricalGrid());
        gga.remove("grid");
        gga.add(gridAttr, false);
        return gga;
    }

    private void changeToToricalEdge(EdgeGraphicAttribute ega) {
        ega.remove("shape");
        ega
                .add(new StringAttribute("shape",
                        "org.graffiti.plugins.shapes.edges.toricalEdgeShape.ToricalEdgeShape"));
        ega.add(new IntegerAttribute("windX", 0));
        ega.add(new IntegerAttribute("windY", 0));

    }

    @Override
    public CollectionAttribute getDirectedEdgeAttribute() {
        EdgeGraphicAttribute ega = new EdgeGraphicAttribute(true);
        changeToToricalEdge(ega);
        return ega;
    }

    @Override
    public CollectionAttribute getUndirectedEdgeAttribute() {
        EdgeGraphicAttribute ega = new EdgeGraphicAttribute(false);
        changeToToricalEdge(ega);
        return ega;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
