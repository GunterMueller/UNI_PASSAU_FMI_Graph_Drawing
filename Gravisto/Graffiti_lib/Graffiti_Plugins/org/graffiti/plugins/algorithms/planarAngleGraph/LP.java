// =============================================================================
//
//   LP.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

import lpsolve.LpSolveException;

/**
 * Solves lp models.
 * 
 * @author Mirka Kossak
 */
public abstract class LP {
    // return value of solving the lp model
    private int sol;

    // solution array of the lp
    private double[] lpSolution;

    /**
     * Runs the lp solver.
     * 
     * @throws LpSolveException
     */
    public abstract void run() throws LpSolveException;

    /**
     * Sets the return value of solving the lp model.
     * 
     * @param sol
     */
    public void setSol(int sol) {
        this.sol = sol;
    }

    /**
     * Returns the return value of solving the lp model.
     * 
     * @return The return value of solving the lp model.
     */
    public int getSol() {
        return sol;
    }

    /**
     * Sets the solution array of the lp.
     * 
     * @param lpSolution
     */
    public void setLPSolution(double[] lpSolution) {
        this.lpSolution = lpSolution;
    }

    /**
     * Returns the solution array of the lp.
     * 
     * @return The solution of the lp.
     */
    public double[] getLPSolution() {
        return lpSolution;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
