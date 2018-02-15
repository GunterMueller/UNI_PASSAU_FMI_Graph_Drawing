// =============================================================================
//
//   Cnf.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.stackqueuelayout.minisat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class CnfFormula {
    private MiniSat miniSat;
    private List<Clause> clauses;
    private List<Variable> variables;
    private int nextVariableIndex;

    protected CnfFormula(MiniSat miniSat) {
        this.miniSat = miniSat;
        clauses = new LinkedList<Clause>();
        variables = new ArrayList<Variable>();
        nextVariableIndex = 1;
    }

    public Clause addClause() {
        Clause clause = new Clause(this);
        clauses.add(clause);
        return clause;
    }

    public Variable addVariable() {
        Variable variable = new Variable(this, nextVariableIndex);
        nextVariableIndex++;
        variables.add(variable);
        return variable;
    }

    public String toDimac() {
        StringBuilder builder = new StringBuilder("p cnf ");
        builder.append(variables.size());
        builder.append(' ');
        builder.append(clauses.size());
        for (Clause clause : clauses) {
            builder.append('\n');
            builder.append(clause.toDimac());
        }
        builder.append('\n');
        return builder.toString();
    }

    /**
     * Returns a satisfying assignment or {@code null} if the formula is not
     * satisfiable.
     * 
     * @return a satisfying assignment or {@code null} if the formula is not
     *         satisfiable.
     */
    public Map<Variable, Boolean> solve() throws InterruptedException {
        String resultString = miniSat.solve(toDimac());

        if (resultString == null)
            return null;

        String[] results = resultString.split("\\s");

        Map<Variable, Boolean> result = new LinkedHashMap<Variable, Boolean>();

        for (String str : results) {
            if (str.isEmpty()) {
                continue;
            }
            int i = Integer.valueOf(str);

            if (i == 0)
                return result;

            boolean isPositive = i > 0;

            Variable variable = variables.get(Math.abs(i) - 1);

            result.put(variable, isPositive);
        }

        throw new IllegalArgumentException("Unexpected minisat output format.");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
