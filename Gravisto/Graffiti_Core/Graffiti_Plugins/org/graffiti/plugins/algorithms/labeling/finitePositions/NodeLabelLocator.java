package org.graffiti.plugins.algorithms.labeling.finitePositions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import org.graffiti.graphics.LabelAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.graphics.NodeLabelPositionAttribute;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;

public class NodeLabelLocator extends LabelLocator {

    public final NodeLabelAttribute nodeLabel;

    public final ArrayList<NodeLabelPosition> candidatePositions;

    private boolean isAppliedYet;

    /**
     * Creates a node label locator.
     * 
     * @param nodeLabel
     *            - node label to be positioned
     * @param nodeGraphicAttributes
     *            - graphic attributes of the parent node; needed for generation
     *            of position candidates for the label
     * @param isUseOriginalLabelPosition
     */
    public NodeLabelLocator(NodeLabelAttribute nodeLabel,
            NodeGraphicAttribute nodeGraphicAttributes,
            GeometricalVector labelSize, int numberOfCandidatePositions,
            boolean isUseOriginalLabelPosition) {
        super();
        assert (nodeLabel != null);
        this.nodeLabel = nodeLabel;
        this.candidatePositions = generatePositionCandidates(
                nodeGraphicAttributes, labelSize, numberOfCandidatePositions,
                isUseOriginalLabelPosition);
        this.isAppliedYet = false;
    }

    @Override
    public LabelAttribute getLabelAttribute() {
        return nodeLabel;
    }

    /**
     * generates and returns a number of position candidates
     * <p>
     * <tt>this.nodeLabel</tt> has to be set already for this routine to work.
     * <p>
     * <b>Usage</b>: at locator construction (maybe also available by public
     * modifier)
     * <p>
     * <b><i>Implementation note</i></b>: Never generate position candidates
     * that set the node label position attributes "Alignment" or
     * "RelativeOffset". These are not treated correctly by the algorithm.
     * 
     * @param nodeGraphicAttributes
     *            - needed for node size
     * @param labelSize
     *            - it is quite a hack to get the size of a label; it has to be
     *            done by the caller
     * @param numberOfCandidates
     *            - number of position candidates to generate
     * @param isUseOriginalLabelPosition
     *            - true if the original label position should be a candidate
     *            (will not consider "Alignment" or "RelativeOffset")
     * @return a list of label position candidates
     */
    private ArrayList<NodeLabelPosition> generatePositionCandidates(
            NodeGraphicAttribute nodeGraphicAttributes,
            GeometricalVector labelSize, int numberOfCandidates,
            boolean isUseOriginalLabelPosition) {
        // generate candidate list with expected length
        ArrayList<NodeLabelPosition> positionCandidates = new ArrayList<NodeLabelPosition>(
                5);

        // width and height of corresponding label
        GeometricalVector parentNodeSize = new GeometricalVector(
                nodeGraphicAttributes.getDimension().getWidth(),
                nodeGraphicAttributes.getDimension().getHeight());

        // fill candidate list
        if (isUseOriginalLabelPosition || numberOfCandidates <= 0) {
            positionCandidates
                    .add(new NodeLabelPosition(
                            (NodeLabelPositionAttribute) nodeLabel
                                    .getPosition().copy(),
                            nodeGraphicAttributes, labelSize, this, 1d));
        }

        if (numberOfCandidates <= 0)
            return positionCandidates;
        // upper right
        positionCandidates.add(new NodeLabelPosition(
                new NodeLabelPositionAttribute("", "", "", 0, 0,
                        (int) (0.5 * parentNodeSize.getX() + 0.5 * labelSize
                                .getX()),
                        (int) (-0.5 * parentNodeSize.getY() - 0.5 * labelSize
                                .getY())), nodeGraphicAttributes, labelSize,
                this, 0.9d));

        if (numberOfCandidates <= 1)
            return positionCandidates;
        // lower right
        positionCandidates.add(new NodeLabelPosition(
                new NodeLabelPositionAttribute("", "", "", 0, 0,
                        (int) (0.5 * parentNodeSize.getX() + 0.5 * labelSize
                                .getX()),
                        (int) (0.5 * parentNodeSize.getY() + 0.5 * labelSize
                                .getY())), nodeGraphicAttributes, labelSize,
                this, 0.7d));

        if (numberOfCandidates <= 2)
            return positionCandidates;
        // upper left
        positionCandidates.add(new NodeLabelPosition(
                new NodeLabelPositionAttribute("", "", "", 0, 0, (int) (-0.5
                        * parentNodeSize.getX() - 0.5 * labelSize.getX()),
                        (int) (-0.5 * parentNodeSize.getY() - 0.5 * labelSize
                                .getY())), nodeGraphicAttributes, labelSize,
                this, 0.8d));

        if (numberOfCandidates <= 3)
            return positionCandidates;
        // lower left
        positionCandidates.add(new NodeLabelPosition(
                new NodeLabelPositionAttribute("", "", "", 0, 0, (int) (-0.5
                        * parentNodeSize.getX() - 0.5 * labelSize.getX()),
                        (int) (0.5 * parentNodeSize.getY() + 0.5 * labelSize
                                .getY())), nodeGraphicAttributes, labelSize,
                this, 0.6d));

        if (numberOfCandidates <= 4)
            return positionCandidates;
        // right center
        positionCandidates.add(new NodeLabelPosition(
                new NodeLabelPositionAttribute("", "", "", 0, 0,
                        (int) (0.5 * parentNodeSize.getX() + 0.5 * labelSize
                                .getX()), 0), nodeGraphicAttributes, labelSize,
                this, 0.2d));

        if (numberOfCandidates <= 5)
            return positionCandidates;
        // upper center
        positionCandidates.add(new NodeLabelPosition(
                new NodeLabelPositionAttribute("", "", "", 0, 0, 0, (int) (-0.5
                        * parentNodeSize.getY() - 0.5 * labelSize.getY())),
                nodeGraphicAttributes, labelSize, this, 0.5d));

        if (numberOfCandidates <= 6)
            return positionCandidates;
        // left center
        positionCandidates.add(new NodeLabelPosition(
                new NodeLabelPositionAttribute("", "", "", 0, 0, (int) (-0.5
                        * parentNodeSize.getX() - 0.5 * labelSize.getX()), 0),
                nodeGraphicAttributes, labelSize, this, 0.3d));

        if (numberOfCandidates <= 7)
            return positionCandidates;
        // lower center
        positionCandidates.add(new NodeLabelPosition(
                new NodeLabelPositionAttribute("", "", "", 0, 0, 0,
                        (int) (0.5 * parentNodeSize.getY() + 0.5 * labelSize
                                .getY())), nodeGraphicAttributes, labelSize,
                this, 0.4d));

        return positionCandidates;

    }

    @Override
    public LabelPosition getCandidatePosition(int candidateNum) {
        return candidatePositions.get(candidateNum);
    }

    @Override
    public int getNumberOfCandidatePositions() {
        return candidatePositions.size();
    }

    @Override
    public void applyToLabel() {
        assert (candidatePositions.size() > 0);

        // apply best of position candidates to to label
        // lexicographic measure:
        // 2ndOrderCollisionCount > collisionCount > quality
        NodeLabelPosition appliedCandidate = candidatePositions.get(0);
        {
            double currentQuality = Double.NEGATIVE_INFINITY;
            int currentSecondOrderCollisionCount = Integer.MAX_VALUE;
            int currentCollisionCount = Integer.MAX_VALUE;
            int candidatesSecondOrderCollisionCount;
            int candidatesCollisionCount;
            for (NodeLabelPosition candidate : candidatePositions) {
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
        assert (nodeLabel != null);

        nodeLabel.setPosition(appliedCandidate.getPositionAttribute());

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
    public ListIterator<? extends LabelPosition> getCandidatesIterator() {
        return candidatePositions.listIterator();
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
