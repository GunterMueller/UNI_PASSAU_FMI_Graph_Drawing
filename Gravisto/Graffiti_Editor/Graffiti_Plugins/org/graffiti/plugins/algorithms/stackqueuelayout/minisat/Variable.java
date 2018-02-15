// =============================================================================
//
//   Variable.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout.minisat;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class Variable {
    protected CnfFormula formula;
    private int index;

    protected Variable(CnfFormula formula, int index) {
        this.formula = formula;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String toDimac() {
        return String.valueOf(index);
    }

    @Override
    public String toString() {
        return "var" + index;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
