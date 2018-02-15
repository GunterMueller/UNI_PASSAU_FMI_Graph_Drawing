// =============================================================================
//
//   AngleLP.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * Used for solving the system Ax=b, where A is the <code>AngleMatrix</code>.
 * 
 * @author Mirka Kossak
 */
public class AngleLP extends LP {
    // Logger used to print information
    public static final Logger logger = Logger
            .getLogger(PlanarAngleGraphAlgorithm.class.getPackage().getName());

    protected AngleMatrix angleMatrix;

    protected Angle[] anglesWithValue;

    private HashMap<Node, ArrayList<Angle>> nodeWithAngles;

    private TestedGraph testedGraph;

    protected double minAngle;

    /**
     * Constructs new lp that gives the values for the angles.
     * 
     * @param angleMatrix
     * @param testedGraph
     * @param minAngle
     */
    public AngleLP(AngleMatrix angleMatrix, TestedGraph testedGraph,
            double minAngle) {
        this.angleMatrix = angleMatrix;
        this.anglesWithValue = angleMatrix.getAngles();
        this.nodeWithAngles = angleMatrix.getNodeAngleMap();
        this.testedGraph = testedGraph;
        this.minAngle = minAngle;
    }

    /**
     * Solves the angle lp.
     * 
     * @throws LpSolveException
     */
    @Override
    public void run() throws LpSolveException {
        LpSolve angleProblem = LpSolve.makeLp(0, angleMatrix
                .getNumberOfColumns());
        angleProblem.setVerbose(2);
        int row = 0;
        for (row = 0; row < testedGraph.getNumberOfNodes(); row++) {
            angleProblem.addConstraint(angleMatrix.getRowToDouble(row),
                    LpSolve.EQ, 360);
            angleProblem.setRowName(row + 1, "node" + row);
        }
        row = testedGraph.getNumberOfNodes();
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
        for (int i = row; i < angleMatrix.getNumberOfRows() - 1; i++) {
            angleProblem.addConstraint(angleMatrix.getRowToDouble(row),
                    LpSolve.EQ, 0);
            angleProblem.setRowName(row++, "angle" + (row - startRow));
        }
        int lowerBoundRow = row;
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

        // angleProblem.printLp();
        // angleProblem.printSolution(1);

        setSol(sol);
        if (angleProblem.solve() == 2) {
            System.out.println("The angle LP is infeasible!!");
        } else {
            this.initAnglesWithValue();
            int optimized = 0;
            optimize(startRow, lowerBoundRow, angleProblem
                    .getPtrPrimalSolution(), angleProblem.getObjective(),
                    angleProblem, optimized);
        }
    }

    /**
     * If there are angles that have the value of the current lower bound and
     * can be optimized, they are going to be optimized here.
     * 
     * @param startRow
     * @param lowerBoundRow
     *            The row of the lower bound.
     * @param solutionOfAngleLP
     * @param minValue
     * @param optimizeProblem
     * @param optimized
     * @throws LpSolveException
     */
    public void optimize(int startRow, int lowerBoundRow,
            double[] solutionOfAngleLP, double minValue,
            LpSolve optimizeProblem, int optimized) throws LpSolveException {
        int solutionIndex = solutionOfAngleLP.length
                - (anglesWithValue.length * 2 + 1);
        int deleted = 0;
        int angleIndex = 0;
        for (int row = startRow; row < angleMatrix.getNumberOfRows() - 1; row++) {
            if (((Math.round(solutionOfAngleLP[solutionIndex])) == Math
                    .round(minValue))
                    && (solutionIndex < (solutionOfAngleLP.length
                            - anglesWithValue.length - 1))
                    && (anglesWithValue[angleIndex].getValue() == -1)) {
                boolean improvable = check(row, minValue, optimizeProblem,
                        deleted);
                if (!improvable) {
                    Angle angle = anglesWithValue[angleIndex];
                    angle.setValue(minValue);
                    optimized++;
                }
                deleted++;
            }
            solutionIndex++;
            angleIndex++;
        }

        if (optimized < anglesWithValue.length) {
            ImproveAngleLP improveAngles = new ImproveAngleLP(angleMatrix,
                    minValue, anglesWithValue);
            improveAngles.run();
            if (improveAngles.getSol() == 0) {
                optimize(startRow, lowerBoundRow,
                        improveAngles.getLPSolution(), improveAngles
                                .getMinAngle(), improveAngles.getProblem(),
                        optimized);
            }
        } else {
            solution();
        }
    }

    /**
     * Checks wheater one angle that has the value of the current lower bound
     * can be improved (= getting a larger value).
     * 
     * @param row
     *            The row of the angle.
     * @param lowerBound
     *            The current lower bound.
     * @param checkProblem
     *            The lp.
     * @param deleted
     *            Stores how many rows of the original lp have been deleted.
     * @return true, if the angle is improvable. false otherwise.
     * @throws LpSolveException
     */
    public boolean check(int row, double lowerBound, LpSolve checkProblem,
            int deleted) throws LpSolveException {
        boolean improvable = false;
        checkProblem.delConstraint(row - deleted);
        checkProblem.addConstraint(angleMatrix.getChangedRow(row), LpSolve.GE,
                lowerBound + 0.1);
        checkProblem.solve();
        double obj = checkProblem.getObjective();
        if (obj >= lowerBound) {
            improvable = true;
        }
        checkProblem.delConstraint(checkProblem.getNrows());
        checkProblem.addConstraint(angleMatrix.getRowToDouble(row), LpSolve.EQ,
                0);
        return improvable;
    }

    /**
     * Inits the array where all angles are stored with its values. Init value
     * is -1.
     * 
     */
    public void initAnglesWithValue() {
        for (int i = 0; i < anglesWithValue.length; i++) {
            anglesWithValue[i].setValue(-1);
        }
    }

    /**
     * Every angle is going to be stored here with its value.
     * 
     */
    public void solution() {
        logger.info("Solution from angle LP:");
        for (int i = 0; i < anglesWithValue.length; i++) {
            Angle currentAngle = anglesWithValue[i];
            Node current = currentAngle.getVertex();
            logger.info(" ");
            logger.info("the node: " + testedGraph.toString(current));
            Edge first = anglesWithValue[i].getFirst();
            logger.info("angle between first: ");
            printOtherNode(first, current);
            Edge second = anglesWithValue[i].getSecond();
            logger.info("and second: ");
            printOtherNode(second, current);
            ArrayList<Angle> currentList = nodeWithAngles.get(current);
            logger.info("has value: " + anglesWithValue[i].getValue());
            logger.info("and index " + i);
            logger.info(" ");
            currentList.add(currentAngle);
        }
    }

    /**
     * Used for debugging only.
     * 
     * @param edge
     * @param current
     */
    public void printOtherNode(Edge edge, Node current) {
        Node source = edge.getSource();
        if (source == current) {
            logger.info(testedGraph.toString(edge.getTarget()));
        } else {
            logger.info(testedGraph.toString(source));
        }
    }

    /**
     * Returns the array in that all angles are stored with their values.
     * 
     * @return The array in that all angles are stored with their values.
     */
    public Angle[] getAnglesWithValue() {
        return this.anglesWithValue;
    }

    /**
     * Returns the map which gives to every node its angles.
     * 
     * @return The map which gives to every node its angles.
     */
    public HashMap<Node, ArrayList<Angle>> getNodeWithAngles() {
        return this.nodeWithAngles;
    }

}
