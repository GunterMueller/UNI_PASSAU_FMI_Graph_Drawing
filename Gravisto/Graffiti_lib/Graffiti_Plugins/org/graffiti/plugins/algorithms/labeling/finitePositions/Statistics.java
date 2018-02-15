package org.graffiti.plugins.algorithms.labeling.finitePositions;

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
    public static void locatorApplied(int collisionCount, double quality) {
        // placeablility
        if (collisionCount == 0) {
            numAppliedPlaceableLocators++;
        } else {
            numAppliedNonPlaceableLocators++;
        }
        // graph overlaps
        if (quality <= 0) {
            numAppliedLowQualityPositions++;
        }
        // average quality
        int numAppliedPositions = numAppliedPlaceableLocators
                + numAppliedNonPlaceableLocators;
        averagePositionQuality = ((numAppliedPositions - 1)
                * averagePositionQuality + quality)
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

    /**
     * incremented whenever a position is applied which overlaps with graph
     * objects (edges and nodes)
     */
    private static int numAppliedLowQualityPositions;

    /** average quality of applied positions */
    private static double averagePositionQuality;

    public static String statisticsString() {
        return "finite positions label placement algorithm runtime statistics:"
                + "\n  number of position candidates: "
                + numCandidates
                + "\n  number of collision tests: "
                + numCollisionTests
                + "\n  number of collision tests with nodes: "
                + numCollisionTestsWithNodes
                + "\n  number of collision tests with edges: "
                + numCollisionTestsWithEdges
                + "\n  number of collision tests: "
                + numCollisionTests
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
                + "\n  number of positions applied with quality < 0 "
                + "(recognized graph object overlap): "
                + numAppliedLowQualityPositions
                + "\n  average quality of applied positions: "
                + averagePositionQuality;
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
        numAppliedLowQualityPositions = 0;
        // averagePositionQuality needs not to be initialized
    }

}
