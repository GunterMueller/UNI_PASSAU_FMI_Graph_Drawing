package org.graffiti.plugins.algorithms.labeling.finitePositions;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graphics.EdgeLabelPositionAttribute;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;

/**
 * Edge label position candidate
 */
public class EdgeLabelPosition extends LabelPosition {

    /**
     * coordinates relative to parent; these might change (so it's important to
     * make a copy to not change the parent)
     */
    private EdgeLabelPositionAttribute position;

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
     * Creates an edge label position candidate
     * 
     * @param labelPosition
     *            - make sure by supplying a clone if it is taken elsewhere
     */
    public EdgeLabelPosition(EdgeLabelPositionAttribute labelPosition,
            GeometricalVector parentSourcePos,
            GeometricalVector parentTargetPos, GeometricalVector labelSize,
            LabelLocator parentLocator, double quality) {
        super(parentLocator, quality);
        this.position = labelPosition;

        GeometricalVector absoluteLabelPos = GeometricalVector.add(
                parentSourcePos, GeometricalVector.add(GeometricalVector.mult(
                        GeometricalVector.subtract(parentTargetPos,
                                parentSourcePos), labelPosition
                                .getRelativeAlignment()),
                        new GeometricalVector(labelPosition
                                .getAbsoluteXOffset(), labelPosition
                                .getAbsoluteYOffset())));

        this.upperLeft = GeometricalVector.subtract(absoluteLabelPos,
                GeometricalVector.mult(labelSize, 0.5d));
        this.lowerRight = GeometricalVector.add(absoluteLabelPos,
                GeometricalVector.mult(labelSize, 0.5d));

        // TODO: test edge label candidate positions
    }

    // POSITIONING ROUTINES

    public CollectionAttribute getPositionAttribute() {
        return position;
    }

    @Override
    public GeometricalVector getCoordinateLowerRight() {
        return lowerRight;
    }

    @Override
    public GeometricalVector getCoordinateUpperLeft() {
        return upperLeft;
    }

}
