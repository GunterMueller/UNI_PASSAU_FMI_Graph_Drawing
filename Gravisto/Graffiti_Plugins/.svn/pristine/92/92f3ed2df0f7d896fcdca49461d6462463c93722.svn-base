package org.graffiti.plugins.algorithms.labeling.finitePositions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.EdgeLabelPositionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;

public class EdgeLabelLocator extends LabelLocator {

    private EdgeLabelAttribute edgeLabel;
    private ArrayList<EdgeLabelPosition> candidatePositions;
    private boolean isAppliedYet;

    public EdgeLabelLocator(EdgeLabelAttribute edgeLabel, Edge edge,
            GeometricalVector labelSize, int numberOfCandidatePositions,
            boolean isUseOriginalLabelPosition) {
        super();
        this.edgeLabel = edgeLabel;
        this.candidatePositions = generatePositionCandidates(edge, labelSize,
                numberOfCandidatePositions, isUseOriginalLabelPosition);
        this.isAppliedYet = false;
    }

    /**
     * generates and returns a number of position candidates
     * <p>
     * <tt>this.edgeLabel</tt> has to be set already for this routine to work.
     * <p>
     * <b>Usage</b>: at locator construction (maybe also available by public
     * modifier)
     * <p>
     * Though this function does not consider edge segments, it may still work
     * smoothly with them.
     * 
     * @param labelSize
     * @param numberOfCandidates
     *            - number of position candidates to generate
     * @param isUseOriginalLabelPosition
     *            - true if the original label position should be a candidate
     * @return a list of label position candidates
     */
    private ArrayList<EdgeLabelPosition> generatePositionCandidates(
            Edge parent, GeometricalVector labelSize, int numberOfCandidates,
            boolean isUseOriginalLabelPosition) {
        // generate candidate list with expected length
        ArrayList<EdgeLabelPosition> positionCandidates = new ArrayList<EdgeLabelPosition>(
                5);

        // TODO: size of parent edge
        GeometricalVector parentSourcePos = new GeometricalVector(
                ((NodeGraphicAttribute) parent.getSource().getAttributes()
                        .getAttribute(GraphicAttributeConstants.GRAPHICS))
                        .getCoordinate().getX(), ((NodeGraphicAttribute) parent
                        .getSource().getAttributes().getAttribute(
                                GraphicAttributeConstants.GRAPHICS))
                        .getCoordinate().getY());
        GeometricalVector parentTargetPos = new GeometricalVector(
                ((NodeGraphicAttribute) parent.getTarget().getAttributes()
                        .getAttribute(GraphicAttributeConstants.GRAPHICS))
                        .getCoordinate().getX(), ((NodeGraphicAttribute) parent
                        .getTarget().getAttributes().getAttribute(
                                GraphicAttributeConstants.GRAPHICS))
                        .getCoordinate().getY());

        GeometricalVector edgeDirection = GeometricalVector.subt(
                parentTargetPos, parentSourcePos).getUnitVector();
        // edge normal vector (clockwise quadrant)
        GeometricalVector edgeNormal = new GeometricalVector(-edgeDirection
                .getY(), -edgeDirection.getX());
        boolean isEdgeNormalXpositive = edgeNormal.getX() >= 0;
        boolean isEdgeNormalYpositive = edgeNormal.getY() >= 0;

        // fill candidate list
        if (isUseOriginalLabelPosition || numberOfCandidates <= 0) {
            positionCandidates
                    .add(new EdgeLabelPosition(
                            (EdgeLabelPositionAttribute) edgeLabel
                                    .getPosition().copy(), parentSourcePos,
                            parentTargetPos, labelSize, this, 0d));
        }

        if (numberOfCandidates <= 0)
            return positionCandidates;
        // center above/lower (which one not tested)
        positionCandidates.add(new EdgeLabelPosition(
                new EdgeLabelPositionAttribute("", 0.5d, 0,
                        (int) (0.5 * (isEdgeNormalXpositive ? -1
                                - labelSize.getX() : 1 + labelSize.getX())),
                        (int) (0.5 * (isEdgeNormalYpositive ? 1 + labelSize
                                .getY() : -1 - labelSize.getY()))),
                parentSourcePos, parentTargetPos, labelSize, this, 1.0d));

        if (numberOfCandidates <= 1)
            return positionCandidates;
        // center lower/above (which one not tested)
        positionCandidates.add(new EdgeLabelPosition(
                new EdgeLabelPositionAttribute("", 0.5d, 0,
                        (int) (0.5 * (isEdgeNormalXpositive ? 1 + labelSize
                                .getX() : -1 - labelSize.getX())),
                        (int) (0.5 * (isEdgeNormalYpositive ? -1
                                - labelSize.getY() : 1 + labelSize.getY()))),
                parentSourcePos, parentTargetPos, labelSize, this, 1.0d));

        if (numberOfCandidates <= 2)
            return positionCandidates;

        // last third lower/above (which one not tested)
        positionCandidates.add(new EdgeLabelPosition(
                new EdgeLabelPositionAttribute("", 0.70d, 0,
                        (int) (0.5 * (isEdgeNormalXpositive ? 1 + labelSize
                                .getX() : -1 - labelSize.getX())),
                        (int) (0.5 * (isEdgeNormalYpositive ? -1
                                - labelSize.getY() : 1 + labelSize.getY()))),
                parentSourcePos, parentTargetPos, labelSize, this, 0.6d));

        if (numberOfCandidates <= 3)
            return positionCandidates;
        // first third above/lower (which one not tested)
        positionCandidates.add(new EdgeLabelPosition(
                new EdgeLabelPositionAttribute("", 0.30d, 0,
                        (int) (0.5 * (isEdgeNormalXpositive ? -1
                                - labelSize.getX() : 1 + labelSize.getX())),
                        (int) (0.5 * (isEdgeNormalYpositive ? 1 + labelSize
                                .getY() : -1 - labelSize.getY()))),
                parentSourcePos, parentTargetPos, labelSize, this, 0.6d));

        if (numberOfCandidates <= 4)
            return positionCandidates;
        // last third above/lower (which one not tested)
        positionCandidates.add(new EdgeLabelPosition(
                new EdgeLabelPositionAttribute("", 0.60d, 0,
                        (int) (0.5 * (isEdgeNormalXpositive ? -1
                                - labelSize.getX() : 1 + labelSize.getX())),
                        (int) (0.5 * (isEdgeNormalYpositive ? 1 + labelSize
                                .getY() : -1 - labelSize.getY()))),
                parentSourcePos, parentTargetPos, labelSize, this, 0.6d));

        if (numberOfCandidates <= 5)
            return positionCandidates;
        // first third lower/above (which one not tested)
        positionCandidates.add(new EdgeLabelPosition(
                new EdgeLabelPositionAttribute("", 0.40d, 0,
                        (int) (0.5 * (isEdgeNormalXpositive ? 1 + labelSize
                                .getX() : -1 - labelSize.getX())),
                        (int) (0.5 * (isEdgeNormalYpositive ? -1
                                - labelSize.getY() : 1 + labelSize.getY()))),
                parentSourcePos, parentTargetPos, labelSize, this, 0.6d));

        if (numberOfCandidates <= 6)
            return positionCandidates;

        // The following also serve to "push" node labels from outgoing edges.

        // last fifth lower/above (which one not tested)
        positionCandidates.add(new EdgeLabelPosition(
                new EdgeLabelPositionAttribute("", 0.85d, 0,
                        (int) (0.5 * (isEdgeNormalXpositive ? -1
                                - labelSize.getX() : 1 + labelSize.getX())),
                        (int) (0.5 * (isEdgeNormalYpositive ? 1 + labelSize
                                .getY() : -1 - labelSize.getY()))),
                parentSourcePos, parentTargetPos, labelSize, this, 0.1d));
        if (numberOfCandidates <= 7)
            return positionCandidates;
        // first fifth lower/above (which one not tested)
        positionCandidates.add(new EdgeLabelPosition(
                new EdgeLabelPositionAttribute("", 0.15d, 0,
                        (int) (0.5 * (isEdgeNormalXpositive ? 1 + labelSize
                                .getX() : -1 - labelSize.getX())),
                        (int) (0.5 * (isEdgeNormalYpositive ? -1
                                - labelSize.getY() : 1 + labelSize.getY()))),
                parentSourcePos, parentTargetPos, labelSize, this, 0.1d));

        return positionCandidates;
    }

    @Override
    public void applyToLabel() {
        assert (candidatePositions.size() > 0);

        // apply best of position candidates to to label
        // lexicographic measure:
        // 2orderCollisionCount > collisionCount > quality
        EdgeLabelPosition appliedCandidate = candidatePositions.get(0);
        {
            double currentQuality = Double.NEGATIVE_INFINITY;
            int currentSecondOrderCollisionCount = Integer.MAX_VALUE;
            int currentCollisionCount = Integer.MAX_VALUE;
            int candidatesSecondOrderCollisionCount;
            int candidatesCollisionCount;
            for (EdgeLabelPosition candidate : candidatePositions) {
                candidatesSecondOrderCollisionCount = candidate
                        .getNumberOf2ndOrderCollisions();
                candidatesCollisionCount = candidate.getNumberOfCollisions();
                if (candidatesSecondOrderCollisionCount < currentSecondOrderCollisionCount
                        || candidatesSecondOrderCollisionCount == currentSecondOrderCollisionCount
                        && candidatesCollisionCount < currentCollisionCount
                        || candidatesSecondOrderCollisionCount == currentSecondOrderCollisionCount
                        && candidatesCollisionCount == currentCollisionCount
                        && candidate.getQuality() > currentQuality) {
                    appliedCandidate = candidate;
                    currentQuality = appliedCandidate.getQuality();
                    currentCollisionCount = candidatesCollisionCount;
                    currentSecondOrderCollisionCount = candidatesSecondOrderCollisionCount;
                }
            }
        }

        assert (appliedCandidate != null);
        assert (appliedCandidate.getPositionAttribute() != null);
        assert (edgeLabel != null);

        edgeLabel.setPosition(appliedCandidate.getPositionAttribute());

        Statistics.locatorApplied(appliedCandidate.getNumberOfCollisions(),
                appliedCandidate.getQuality());

        // Mark still residing conflicts as second order conflicts
        for (Iterator<LabelPosition> conflictPartnerIt = appliedCandidate
                .getCollisionsIterator(); conflictPartnerIt.hasNext();) {
            conflictPartnerIt.next().mark2ndOrderCollision();
        }

        // Mark overlap if there is a colliding candidate left whose locator
        // has been applied yet
        Statistics.secondOrderCollisionPositionApplied(appliedCandidate
                .getNumberOf2ndOrderCollisions());

        // Remove all other candidates from other collisions
        for (LabelPosition candidate : candidatePositions) {
            if (candidate != appliedCandidate) {
                candidate.unmarkAllCollisions();
            }
        }

        isAppliedYet = true;
    }

    @Override
    public boolean isAppliedYet() {
        return isAppliedYet;
    }

    @Override
    public LabelPosition getCandidatePosition(int candidateNum) {
        return candidatePositions.get(candidateNum);
    }

    @Override
    public LabelAttribute getLabelAttribute() {
        return edgeLabel;
    }

    @Override
    public ListIterator<? extends LabelPosition> getCandidatesIterator() {
        return candidatePositions.listIterator();
    }

    @Override
    public int getNumberOfCandidatePositions() {
        return candidatePositions.size();
    }

    @Override
    public boolean isPlaceable() {
        for (LabelPosition candidate : candidatePositions) {
            if (candidate.getNumberOfCollisions() == 0)
                return true;
        }
        return false;
    }

}
