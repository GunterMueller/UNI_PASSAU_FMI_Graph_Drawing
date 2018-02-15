// =============================================================================
//
//   LogicConstraints.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.constraint;

import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class LogicConstraints {
    private LogicConstraints() {
    }

    public static class Not extends AbstractConstraint {
        public Not(Constraint constraint) {
            super(constraint);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TriBoolean test(Assignment assignment) throws BenchmarkException {
            switch (constraints[0].test(assignment)) {
            case True:
                return TriBoolean.False;
            case False:
                return TriBoolean.True;
            default:
                return TriBoolean.Maybe;
            }
        }
    }

    public static class Or extends AbstractConstraint {
        public Or(Constraint first, Constraint second) {
            super(first, second);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TriBoolean test(Assignment assignment) throws BenchmarkException {
            TriBoolean first = constraints[0].test(assignment);
            if (first == TriBoolean.True)
                return TriBoolean.True;
            TriBoolean second = constraints[1].test(assignment);
            if (second == TriBoolean.True)
                return TriBoolean.True;
            if (first == TriBoolean.False && second == TriBoolean.False)
                return TriBoolean.False;
            else
                return TriBoolean.Maybe;
        }
    }

    public static class And extends AbstractConstraint {
        public And(Constraint first, Constraint second) {
            super(first, second);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TriBoolean test(Assignment assignment) throws BenchmarkException {
            TriBoolean first = constraints[0].test(assignment);
            if (first == TriBoolean.False)
                return TriBoolean.False;
            TriBoolean second = constraints[1].test(assignment);
            if (second == TriBoolean.False)
                return TriBoolean.False;
            if (first == TriBoolean.True && second == TriBoolean.True)
                return TriBoolean.True;
            else
                return TriBoolean.Maybe;
        }
    }

    public static class Implies extends AbstractConstraint {
        public Implies(Constraint first, Constraint second) {
            super(first, second);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TriBoolean test(Assignment assignment) throws BenchmarkException {
            TriBoolean first = constraints[0].test(assignment);
            if (first == TriBoolean.False)
                return TriBoolean.True;
            TriBoolean second = constraints[1].test(assignment);
            if (second == TriBoolean.True)
                return TriBoolean.True;
            if (first == TriBoolean.True && second == TriBoolean.False)
                return TriBoolean.False;
            else
                return TriBoolean.Maybe;
        }
    }

    public static class Equiv extends AbstractConstraint {
        public Equiv(Constraint first, Constraint second) {
            super(first, second);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TriBoolean test(Assignment assignment) throws BenchmarkException {
            TriBoolean first = constraints[0].test(assignment);
            if (first == TriBoolean.Maybe)
                return TriBoolean.Maybe;
            TriBoolean second = constraints[1].test(assignment);
            if (second == TriBoolean.Maybe)
                return TriBoolean.Maybe;
            return toTri(first == second);
        }
    }

    public static class Xor extends AbstractConstraint {
        public Xor(Constraint first, Constraint second) {
            super(first, second);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TriBoolean test(Assignment assignment) throws BenchmarkException {
            TriBoolean first = constraints[0].test(assignment);
            if (first == TriBoolean.Maybe)
                return TriBoolean.Maybe;
            TriBoolean second = constraints[1].test(assignment);
            if (second == TriBoolean.Maybe)
                return TriBoolean.Maybe;
            return toTri(first != second);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
