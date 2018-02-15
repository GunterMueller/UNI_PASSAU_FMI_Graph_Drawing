// =============================================================================
//
//   CostFunction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treedrawings.subtreeSeperation;

import java.util.List;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.plugins.algorithms.treedrawings.SelectionDialog;

/**
 * This class represents the cost functions that are available for determining
 * the cost of a layout.
 * 
 * @author Andreas
 * @version $Revision$ $Date$
 */
public class CostFunction {
    public static String[] costFunctionNames = new String[] { "AREA",
            "PERIMTER", "HEIGHT_GIVEN_WIDTH", "WIDTH_GIVEN_HEIGHT",
            "MINIMUM_ENCLOSING_SQUARE", "SIZE_WITH_ASPECT_RATIO",
            "NONE_USER_SELECTION" };

    /**
     * type of the cost function
     */
    protected int costFunctionType = -1;

    /**
     * use the area as cost: width * height
     */
    protected static final int AREA_COST = 0;

    /**
     * use the length of the perimeter as cost: 2 * (width + height)
     */
    protected static final int PERIMETER_COST = 1;

    /**
     * use the height (given a fixed width) as the cost: height if width smaller
     * than a given limit, infinity otherwise
     */
    protected static final int HEIGHT_GIVEN_WIDTH_COST = 2;

    /**
     * use the width (given a fixed height) as the cost: width if height smaller
     * than a given limit, infinity otherwise
     */
    protected static final int WIDTH_GIVEN_HEIGHT_COST = 3;

    /**
     * The width of the minimum square in which the given layout can fit:
     * max(width, height)
     */
    protected static final int MINIMUM_ENCLOSING_SQUARE_COST = 4;

    /**
     * A variant of MINIMUM_ENCLOSING_SQUARE_COST that can be used to find the
     * minimum size drawing given an aspect ratio r (width:height): max(width,
     * height * r)
     */
    protected static final int SIZE_WITH_ASPECT_RATIO_COST = 5;

    /**
     * This cost function type gives the user the chance to choose the optimum
     * dimensions out of the atoms calculated.
     */
    protected static final int NONE_USER_SELECTION = 6;

    /**
     * The additional variable that is used for:<BR>
     * <BR>
     * 1. The given width for HEIGHT_GIVEN_WIDTH_COST<BR>
     * 2. The given height for WIDTH_GIVEN_HEIGHT_COST<BR>
     * 3. The aspect ratio for SIZE_WITH_ASPECT_RATIO_COST
     */
    protected double additionalVariable;

    /**
     * Constrcts the costfunction of the specified type.
     * 
     * @param costFunctionName
     *            the name of the cost function
     * @param additionalVariable
     *            the value of the additional variable
     */
    public CostFunction(String costFunctionName, double additionalVariable) {

        if (costFunctionName.equals("AREA")) {
            this.costFunctionType = AREA_COST;
        } else if (costFunctionName.equals("PERIMTER")) {
            this.costFunctionType = PERIMETER_COST;
        } else if (costFunctionName.equals("HEIGHT_GIVEN_WIDTH")) {
            this.costFunctionType = HEIGHT_GIVEN_WIDTH_COST;
        } else if (costFunctionName.equals("WIDTH_GIVEN_HEIGHT")) {
            this.costFunctionType = WIDTH_GIVEN_HEIGHT_COST;
        } else if (costFunctionName.equals("MINIMUM_ENCLOSING_SQUARE")) {
            this.costFunctionType = MINIMUM_ENCLOSING_SQUARE_COST;
        } else if (costFunctionName.equals("SIZE_WITH_ASPECT_RATIO")) {
            this.costFunctionType = SIZE_WITH_ASPECT_RATIO_COST;
        } else if (costFunctionName.equals("NONE_USER_SELECTION")) {
            this.costFunctionType = NONE_USER_SELECTION;
        }

        this.additionalVariable = additionalVariable;
    }

    /**
     * This selects one out of the given atoms.
     * 
     * @param atoms
     *            of which this cost function selects the best.
     * @return the best LayoutComposition
     * @throws IllegalStateException
     *             if no LayoutComposition could satisfy the cost function
     *             specified.
     */
    public LayoutComposition findBestAtom(List<LayoutComposition> atoms)
            throws IllegalStateException {
        LayoutComposition bestFound = null;

        if (this.costFunctionType == CostFunction.NONE_USER_SELECTION) {
            SelectionDialog tid = new SelectionDialog("Choose Atom",
                    "Dimensions of Atoms (width x height):", atoms);
            int chosenIndex = tid.getSelectedIndex();
            if (tid.getButtonPressed() == SelectionDialog.BUTTON_PRESSED_OK
                    && chosenIndex != -1) {
                bestFound = atoms.get(chosenIndex);
            } else {
                GraffitiSingleton.getInstance().getMainFrame().getStatusBar()
                        .showInfo("Cancelled by user", 60000);
                return null;
            }
        } else {

            double bestFoundCost = Double.MAX_VALUE;
            for (LayoutComposition currentComposition : atoms) {

                double currentCompositionCost = this.costOf(currentComposition);
                if (currentCompositionCost < bestFoundCost) {
                    bestFound = currentComposition;
                    bestFoundCost = currentCompositionCost;
                }
            }

            if (bestFound == null)
                throw new IllegalStateException(
                        "Could not find an atom satisfying the defined cost-function");
        }

        return bestFound;
    }

    /**
     * Determines the cost of a given composition.
     * 
     * @param composition
     *            given
     * @return the cost of the given composition
     */
    public double costOf(LayoutComposition composition) {
        double width = composition.getWidth();
        double height = composition.getHeight();

        switch (this.costFunctionType) {
        case AREA_COST:
            return width * height;
        case PERIMETER_COST:
            return width + height;
        case HEIGHT_GIVEN_WIDTH_COST:
            if (width > this.additionalVariable)
                return Double.MAX_VALUE;
            else
                return height;
        case WIDTH_GIVEN_HEIGHT_COST:
            if (height > this.additionalVariable)
                return Double.MAX_VALUE;
            else
                return width;
        case MINIMUM_ENCLOSING_SQUARE_COST:
            return Math.max(width, height);
        case SIZE_WITH_ASPECT_RATIO_COST:
            return Math.max(width, height * this.additionalVariable);
        }

        return -1;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
