// =============================================================================
//
//   Clause.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout.minisat;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class Clause {
    private CnfFormula formula;
    private Set<Variable> variables;
    private List<Boolean> isPositives;

    protected Clause(CnfFormula formula) {
        this.formula = formula;
        variables = new LinkedHashSet<Variable>();
        isPositives = new LinkedList<Boolean>();
    }

    public void addLiteral(Variable variable, boolean isPositive) {
        if (variable.formula != formula)
            throw new IllegalArgumentException(
                    "Clause and variable belong to a different formula.");

        if (!variables.add(variable))
            throw new IllegalArgumentException(
                    "Clause may contain each variable at most once.");

        isPositives.add(isPositive);
    }

    public String toDimac() {
        StringBuilder builder = new StringBuilder();
        Iterator<Variable> varIter = variables.iterator();
        Iterator<Boolean> isPositiveIter = isPositives.iterator();

        while (varIter.hasNext()) {
            boolean isPositive = isPositiveIter.next();
            Variable variable = varIter.next();
            if (!isPositive) {
                builder.append('-');
            }
            builder.append(variable.toDimac());
            builder.append(' ');
        }

        builder.append('0');

        return builder.toString();
    }

    @Override
    public String toString() {
        return "Clause " + toDimac();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
