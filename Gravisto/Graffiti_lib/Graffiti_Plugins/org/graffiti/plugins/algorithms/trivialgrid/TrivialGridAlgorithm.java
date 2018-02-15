// =============================================================================
//
//   TrivialGridAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TrivialGridAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.trivialgrid;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;

/**
 * An inefficient spring embedder algorithm loosely based on Kamada & Kawai.
 */
public class TrivialGridAlgorithm extends AbstractAlgorithm {

    /** DOCUMENT ME! */
    private Rectangle boundingBox = new Rectangle(50, 50);

    /** DOCUMENT ME! */
    private Selection selection;

    /**
     * Constructs a new instance.
     */
    public TrivialGridAlgorithm() {
    }

    /**
     * DOCUMENT ME!
     * 
     * @param rect
     *            DOCUMENT ME!
     */
    public void setBoundingBox(Rectangle rect) {
        this.boundingBox = rect;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Place on grid (trivial)";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Selection",
                "<html>The selection to work on.<p>If empty, "
                        + "the whole graph is used.</html>");
        selParam.setSelection(new Selection("_temp_"));

        return new Parameter[] { selParam };
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        // if (selection == null || boundingBox == null) {
        // throw new PreconditionException("The algorithm needs both a " +
        // "selection and a bounding box.");
        // }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        Collection<Node> nodes;

        // Collection edges;
        if (selection.isEmpty()) {
            nodes = this.graph.getNodes();

            // edges = this.graph.getEdges();
        } else {
            nodes = selection.getNodes();

            // edges = selection.getEdges();
        }

        // if(boundingBox == null)
        // {
        // boundingBox = new Rectangle(50, 50, 200, 200);
        // }
        int nrNodes = nodes.size();

        double xs = Math.ceil(Math.sqrt((nrNodes * boundingBox.getWidth())
                / boundingBox.getHeight()));
        double ys = Math.ceil(nrNodes / xs);

        double stepX = boundingBox.getWidth() / xs;
        double stepY = boundingBox.getHeight() / ys;

        double startX = boundingBox.getX() + (stepX / 2d);
        double coordX = startX;
        double coordY = boundingBox.getY() + (stepY / 2d);

        double sizeX = Math.min(12d, stepX - 3d);
        double sizeY = Math.min(12d, stepY - 3d);

        int x = 0;

        for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
            x++;

            Node node = it.next();
            ((DimensionAttribute) node
                    .getAttribute(GraphicAttributeConstants.DIM_PATH))
                    .setDimension(sizeX, sizeY);
            ((CoordinateAttribute) node
                    .getAttribute(GraphicAttributeConstants.COORD_PATH))
                    .setCoordinate(new Point2D.Double(coordX, coordY));

            if (x > xs) {
                coordX = startX;
                coordY += stepY;
                x = 0;
            } else {
                coordX += stepX;
            }
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
        selection = null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
