package org.graffiti.plugins.algorithms.labeling.finitePositionsIndividualWeighting;

/**
 * provides statistic data for the finite positions label placement algorithm
 */
public final class Statistics {
    /** Don't allow instances */
    private Statistics() {
    }

    /**
     * number of detected position candidate collisions (may contain duplicates)
     */
    public static int numOverlaps;

    /** incremented whenever a position candidate is generated */
    public static int numCandidates;

    /**
     * incremented whenever a position candidate is generated
     * <p>
     * Also serves as unique ID distribution counter (locators need to be
     * comparable)
     */
    private static int numLocators;

    /** returns and increments the number of locators */
    public static int getUniqueLocatorID() {
        numLocators++;
        return numLocators - 1;
    }

    /**
     * incremented whenever a collision test is performed among a pair of
     * position candidates
     */
    public static int numCollisionTests;
    public static int numCollisionTestsWithNodes;
    public static int numCollisionTestsWithEdges;

    private static int numAppliedPlaceableLocators;
    private static int numAppliedNonPlaceableLocators;

    /**
     * Whenever a locator applies a position to its label, this routine is
     * called.
     */
    public static void locatorApplied(double quality, int labelOverlapCount,
            int nodeOverlapCount, int edgeOverlapCount,
            int candidateOverlapCount, double positionPreference) {
        // placeablility
        if (candidateOverlapCount == 0) {
            numAppliedPlaceableLocators++;
        } else {
            numAppliedNonPlaceableLocators++;
        }

        // min, max and average quality
        if (quality < lowestAppliedQuality) {
            lowestAppliedQuality = quality;
        }
        if (quality > highestAppliedQuality) {
            highestAppliedQuality = quality;
        }
        int numAppliedPositions = numAppliedPlaceableLocators
                + numAppliedNonPlaceableLocators;
        averagePositionQuality = ((numAppliedPositions - 1)
                * averagePositionQuality + quality)
                / numAppliedPositions;
        // average overlap counts
        averageAppliedLabelOverlaps = ((numAppliedPositions - 1)
                * averageAppliedLabelOverlaps + labelOverlapCount)
                / numAppliedPositions;
        averageAppliedNodeOverlaps = ((numAppliedPositions - 1)
                * averageAppliedNodeOverlaps + nodeOverlapCount)
                / numAppliedPositions;
        averageAppliedEdgeOverlaps = ((numAppliedPositions - 1)
                * averageAppliedEdgeOverlaps + edgeOverlapCount)
                / numAppliedPositions;
        averageAppliedCandidateOverlaps = ((numAppliedPositions - 1)
                * averageAppliedCandidateOverlaps + candidateOverlapCount)
                / numAppliedPositions;
        averageAppliedPositionPreference = ((numAppliedPositions - 1)
                * averageAppliedPositionPreference + positionPreference)
                / numAppliedPositions;

    }

    private static int[] numAppliedOverlappingPositions = { 0, 0, 0, 0, 0 };

    /**
     * Whenever a position which overlaps an already placed position, this
     * routine is called with a parameter != 0.
     * 
     * @param i
     *            - number of second order collisions are marked for this
     *            applied label position (with how many labels the applied
     *            locator overlaps); in {0..inf}
     */
    public static void secondOrderCollisionPositionApplied(int i) {
        if (i > 4) {
            i = 4;
        }
        numAppliedOverlappingPositions[i]++;
    }

    private static double highestAppliedQuality;
    private static double lowestAppliedQuality;
    /** average quality of applied positions */
    private static double averagePositionQuality;
    private static double averageAppliedLabelOverlaps;
    private static double averageAppliedNodeOverlaps;
    private static double averageAppliedEdgeOverlaps;
    private static double averageAppliedCandidateOverlaps;
    private static double averageAppliedPositionPreference;

    public static String statisticsString() {
        return "finite positions label placement algorithm runtime statistics:"
                + "\n  individually weighted placement criteria: "
                + "\n       overlaps with labels: "
                + FinitePositionsAlgorithmIndividualWeighting.labelOverlapWeight
                + "\n       overlaps with nodes: "
                + FinitePositionsAlgorithmIndividualWeighting.nodeOverlapWeight
                + "\n       overlaps with edges: "
                + FinitePositionsAlgorithmIndividualWeighting.edgeOverlapWeight
                + "\n       overlaps with candidates: "
                + FinitePositionsAlgorithmIndividualWeighting.candidateOverlapWeight
                + "\n       position preference: "
                + FinitePositionsAlgorithmIndividualWeighting.positionPreferenceWeight
                + "\n  number of placed labels: "
                + numLocators
                + "\n  number of position candidates: "
                + numCandidates
                + "\n  number of candidate collision tests: "
                + numCollisionTests
                + "\n  number of collision tests with nodes: "
                + numCollisionTestsWithNodes
                + "\n  number of collision tests with edges: "
                + numCollisionTestsWithEdges
                + "\n  number of detected candidate overlaps: "
                + numOverlaps
                + "\n  number of free label positions applied: "
                + numAppliedPlaceableLocators
                + "\n  number of possibly overlapping label positions applied: "
                + numAppliedNonPlaceableLocators
                + "\n  number of positions applied with no label overlaps: "
                + numAppliedOverlappingPositions[0]
                + "\n  number of positions applied with 1 label overlap: "
                + numAppliedOverlappingPositions[1]
                + "\n  number of positions applied with 2 label overlaps: "
                + numAppliedOverlappingPositions[2]
                + "\n  number of positions applied with 3 label overlaps: "
                + numAppliedOverlappingPositions[3]
                + "\n  number of positions applied with 4 or more label overlaps: "
                + numAppliedOverlappingPositions[4]
                + "\n  lowest applied position quality: "
                + lowestAppliedQuality
                + "\n  highest applied position quality: "
                + highestAppliedQuality
                + "\n  average quality of applied positions: "
                + averagePositionQuality
                + "\n  number of label overlaps: "
                + Math.round(averageAppliedLabelOverlaps * numLocators)
                + " (average per label: "
                + averageAppliedLabelOverlaps
                + ")"
                + "\n  number of label - node overlaps: "
                + Math.round(averageAppliedNodeOverlaps * numLocators)
                + " (average per label: "
                + averageAppliedNodeOverlaps
                + ")"
                + "\n  number of label - edge overlaps: "
                + Math.round(averageAppliedEdgeOverlaps * numLocators)
                + " (average per label: "
                + averageAppliedEdgeOverlaps
                + ")"
                + "\n  number of label - candidate overlaps: "
                + Math.round(averageAppliedCandidateOverlaps * numLocators)
                + " (average per label: "
                + averageAppliedCandidateOverlaps
                + ")"
                + "\n  average position preference of applied positions: "
                + averageAppliedPositionPreference;
    }

    public static void reset() {
        numCandidates = 0;
        numLocators = 0;
        numCollisionTests = 0;
        numCollisionTestsWithNodes = 0;
        numCollisionTestsWithEdges = 0;
        numOverlaps = 0;
        numAppliedPlaceableLocators = 0;
        numAppliedNonPlaceableLocators = 0;
        for (int i = 0; i < 5; i++) {
            numAppliedOverlappingPositions[i] = 0;
        }
        highestAppliedQuality = Double.NEGATIVE_INFINITY;
        lowestAppliedQuality = Double.POSITIVE_INFINITY;
        // average values needs not to be initialized
    }

}
