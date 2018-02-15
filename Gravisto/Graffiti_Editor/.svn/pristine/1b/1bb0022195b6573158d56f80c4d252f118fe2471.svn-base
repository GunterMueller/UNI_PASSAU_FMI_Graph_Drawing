// =============================================================================
//
//   LPImproveAngle.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

/**
 * Is used for getting better angle value results.
 * 
 * @author Mirka Kossak
 */
public class ImproveAngleLP extends LP {
    private LpSolve angleProblem;

    private AngleMatrix angleMatrix;

    protected Angle[] anglesWithValue;

    private double minAngle;

    /**
     * Constructs new lp that improves the values of the angles.
     * 
     * @param angleMatrix
     * @param minAngle
     */
    public ImproveAngleLP(AngleMatrix angleMatrix, double minAngle,
            Angle[] anglesWithValue) {
        this.angleMatrix = angleMatrix;
        this.minAngle = minAngle;
        this.anglesWithValue = anglesWithValue;
        setLPSolution(null);
    }

    /**
     * Solves the lp and returns the array with the values of the improved
     * angles.
     * 
     * @throws LpSolveException
     */
    @Override
    public void run() throws LpSolveException {
        double[] solution = null;
        angleProblem = LpSolve.makeLp(0, angleMatrix.getNumberOfColumns());
        angleProblem.setVerbose(2);
        int row = 0;
        for (row = 0; row < angleMatrix.getGraph().getNumberOfNodes(); row++) {
            angleProblem.addConstraint(angleMatrix.getRowToDouble(row),
                    LpSolve.EQ, 360);
            angleProblem.setRowName(row + 1, "node" + row);
        }
        row = angleMatrix.getGraph().getNumberOfNodes();
        Face[] faces = angleMatrix.getFaces();
        int name = row + 1;
        for (int j = 0; j < angleMatrix.getNumberOfFaces(); j++) {
            if (angleMatrix.getOuterFaceIndex() != j) {
                int numberOfNodes = faces[j].getNumberOfNodes();
                angleProblem.addConstraint(angleMatrix.getRowToDouble(row + j),
                        LpSolve.EQ, (numberOfNodes - 2) * 180);
                angleProblem.setRowName(name++, "face" + j);
            }
        }
        row += angleMatrix.getNumberOfFaces();

        int startRow = row;
        int angleIndex = 0;
        for (int i = row; i < angleMatrix.getNumberOfRows() - 1; i++) {
            double improvable = anglesWithValue[angleIndex++].getValue();
            if (improvable == (-1)) {
                angleProblem.addConstraint(angleMatrix.getRowToDouble(row),
                        LpSolve.EQ, 0);
            } else {
                angleProblem.addConstraint(angleMatrix.getChangedRow(row),
                        LpSolve.EQ, improvable);
            }
            angleProblem.setRowName(row++, "angle" + (row - startRow));
        }
        angleProblem.addConstraint(angleMatrix.getRowToDouble(row), LpSolve.GE,
                minAngle);
        angleProblem.setObjFn(angleMatrix.getRowToDouble(row));
        angleProblem.setRowName(row, "smallestAngle");

        angleProblem.setMaxim();

        // set column names
        int col = 1;
        for (int ang = col; ang <= angleMatrix.getAngles().length; ang++) {
            angleProblem.setColName(col, "angle" + col++);
        }
        angleProblem.setColName(col, "lowerBound");
        for (int slack = 1; slack <= angleMatrix.getAngles().length; slack++) {
            angleProblem.setColName(col + slack, "slack" + slack);
        }
        int sol = angleProblem.solve();
        setSol(sol);
        if (angleProblem.solve() != 2) {
            solution = angleProblem.getPtrPrimalSolution();
            setMinAngle(angleProblem.getObjective());
        }
        setLPSolution(solution);
    }

    /**
     * Sets the value that every angle should have at least.
     * 
     * @param minAngle
     */
    public void setMinAngle(double minAngle) {
        this.minAngle = minAngle;
    }

    /**
     * Returns the value that every angle should have at least.
     * 
     * @return The value that every angle should have at least.
     */
    public double getMinAngle() {
        return this.minAngle;
    }

    /**
     * Returns the angle lp.
     * 
     * @return The angle lp.
     */
    public LpSolve getProblem() {
        return this.angleProblem;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
