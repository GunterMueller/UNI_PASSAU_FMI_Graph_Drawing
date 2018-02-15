// =============================================================================
//
//   LPMaximumEdge.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

/**
 * Used for solving the system Ax=b, where A is the <code>EdgeMatrix</code>.
 * 
 * @author Mirka Kossak
 */
public class EdgeLP extends LP {
    private EdgeMatrix edgeMatrix;

    private int maxEdgeSum;

    private int minEdge;

    /**
     * Constructor for the lp which calculates the edges.
     * 
     * @param edgeMatrix
     *            The matrix which is used to build the edges lp.
     * @param maxEdgeSum
     *            The maximum sum of all edges.
     * @param minEdge
     *            The minimum length that every edge has to have.
     */
    public EdgeLP(EdgeMatrix edgeMatrix, int maxEdgeSum, int minEdge) {
        this.edgeMatrix = edgeMatrix;
        this.maxEdgeSum = maxEdgeSum;
        this.minEdge = minEdge;
        setLPSolution(null);
    }

    /**
     * Solves the edge LP.
     * 
     * @throws LpSolveException
     */
    @Override
    public void run() throws LpSolveException {
        LpSolve problem = LpSolve.makeLp(0, edgeMatrix.getNumberOfColumns());
        problem.setVerbose(2); // only errors are reported
        int row = 0;
        problem.addConstraint(edgeMatrix.getRowToDouble(row++), LpSolve.EQ,
                maxEdgeSum);
        problem.setRowName(row, "firstX");
        problem.addConstraint(edgeMatrix.getRowToDouble(row++), LpSolve.EQ,
                maxEdgeSum);
        problem.setRowName(row, "firstY");
        for (int i = 0; i < 3 * edgeMatrix.getNumberOfEdges(); i++) {
            problem.addConstraint(edgeMatrix.getRowToDouble(row++), LpSolve.EQ,
                    0);
            problem.setRowName(row, "co" + i);
        }
        problem.addConstraint(edgeMatrix.getRowToDouble(row++), LpSolve.LE,
                maxEdgeSum);
        problem.setRowName(row, "edgeSum");
        problem.addConstraint(edgeMatrix.getRowToDouble(row), LpSolve.GE,
                minEdge);
        problem.setObjFn(edgeMatrix.getRowToDouble(row));
        problem.setRowName((row + 1), "lowBound");

        problem.setMaxim();

        for (int i = 1; i <= edgeMatrix.getNumberOfCoordinates(); i++) {
            if (i % 2 == 1) {
                problem.setColName(i, "X" + (i / 2));
            } else {
                problem.setColName(i, "Y" + ((i - 1) / 2));
            }
        }

        int col = edgeMatrix.getNumberOfCoordinates();
        for (int i = 1; i <= edgeMatrix.getNumberOfEdges(); i++) {
            problem.setColName(i + col, "edge" + i);
        }
        col += edgeMatrix.getNumberOfEdges() + 1;
        problem.setColName(col, "shortest");
        for (int j = 1; j <= edgeMatrix.getNumberOfEdges(); j++) {
            problem.setColName(j + col, "slack" + j);
        }
        int sol = problem.solve();
        setSol(sol);
        if (problem.solve() == 2) {
            System.out.println("Edge LP is infeasible! no solution found.");
        } else {
            setLPSolution(problem.getPtrPrimalSolution());
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
