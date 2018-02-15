package org.graffiti.plugins.algorithms.labeling.finitePositionsIndividualWeighting;

import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelPositionAttribute;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;

public class NodeLabelPosition extends LabelPosition {
    /**
     * coordinates relative to parent; these may change (so it's important to
     * make a copy to not change the parent)
     */
    private NodeLabelPositionAttribute positionAttribute;

    /**
     * global coordinates of upper left vertex of label; is set after
     * construction
     */
    private GeometricalVector upperLeft;

    /**
     * global coordinates of lower right vertex of label; is set after
     * construction
     */
    private GeometricalVector lowerRight;

    /**
     * Creates a node label position candidate
     * <p>
     * <b><i>Usage note</i></b>: Never generate position candidates that set the
     * node label position attributes "Alignment" or "RelativeOffset". These are
     * not treated correctly by the algorithm.
     * 
     * @param labelPosition
     *            - make sure by supplying a clone if it is taken elsewhere
     */
    public NodeLabelPosition(NodeLabelPositionAttribute labelPosition,
            NodeGraphicAttribute parentPosition, GeometricalVector labelSize,
            LabelLocator parentLocator, double quality) {
        super(parentLocator, quality);
        assert (labelPosition != null);
        this.positionAttribute = labelPosition;
        GeometricalVector parentPos = new GeometricalVector(parentPosition
                .getCoordinate().getX(), parentPosition.getCoordinate().getY());
        GeometricalVector absoluteLabelPos = GeometricalVector.add(parentPos,
                new GeometricalVector(positionAttribute.getAbsoluteXOffset(),
                        positionAttribute.getAbsoluteYOffset()));
        this.upperLeft = GeometricalVector.subtract(absoluteLabelPos,
                GeometricalVector.mult(labelSize, 0.5d));
        this.lowerRight = GeometricalVector.add(absoluteLabelPos,
                GeometricalVector.mult(labelSize, 0.5d));

        // TODO: test node label candidate positions
    }

    // POSITIONING ROUTINES

    @Override
    public GeometricalVector getCoordinateLowerRight() {
        return lowerRight;
    }

    @Override
    public GeometricalVector getCoordinateUpperLeft() {
        return upperLeft;
    }

    public NodeLabelPositionAttribute getPositionAttribute() {
        return positionAttribute;
    }

}
