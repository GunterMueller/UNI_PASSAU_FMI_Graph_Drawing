// =============================================================================
//
//   AtomicConstraints.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.constraint;

import java.util.Set;

import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class AtomicConstraints {
    private AtomicConstraints() {
    }

    public static class Equals extends AbstractConstraint {
        public Equals(String first, String second) {
            super(first, second);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TriBoolean test(Assignment assignment) throws BenchmarkException {
            if (assignment.isUnassigned(values))
                return TriBoolean.Maybe;
            return toTri(assignment.subst(values[0]).equals(
                    assignment.subst(values[1])));
        }
    }

    public static class NotEquals extends AbstractConstraint {
        public NotEquals(String first, String second) {
            super(first, second);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TriBoolean test(Assignment assignment) throws BenchmarkException {
            if (assignment.isUnassigned(values))
                return TriBoolean.Maybe;
            return toTri(!assignment.subst(values[0]).equals(
                    assignment.subst(values[1])));
        }
    }

    public static abstract class NumberConstraint extends AbstractConstraint {
        public NumberConstraint(String first, String second) {
            super(first, second);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public final TriBoolean test(Assignment assignment)
                throws BenchmarkException {
            if (assignment.isUnassigned(values))
                return TriBoolean.Maybe;
            Double first = toDouble(assignment.subst(values[0]));
            if (first == null)
                throw new BenchmarkException("error.intFormat", values[0]);
            Double second = toDouble(assignment.subst(values[1]));
            if (second == null)
                throw new BenchmarkException("error.intFormat", values[1]);
            return toTri(test(first, second));
        }

        public abstract boolean test(double first, double second);
    }

    public static class LessThan extends NumberConstraint {
        public LessThan(String first, String second) {
            super(first, second);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(double first, double second) {
            return first < second;
        }
    }

    public static class LessThanOrEqual extends NumberConstraint {
        public LessThanOrEqual(String first, String second) {
            super(first, second);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(double first, double second) {
            return first <= second;
        }
    }

    public static class GreaterThan extends NumberConstraint {
        public GreaterThan(String first, String second) {
            super(first, second);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(double first, double second) {
            return first > second;
        }
    }

    public static class GreaterThanOrEqual extends NumberConstraint {
        public GreaterThanOrEqual(String first, String second) {
            super(first, second);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean test(double first, double second) {
            return first >= second;
        }
    }

    public static class SetConstraint extends AbstractConstraint {
        public SetConstraint(String first, Set<String> second) {
            super(toArray(first, second));
        }

        private static String[] toArray(String first, Set<String> set) {
            String[] array = new String[set.size() + 1];
            array[0] = first;
            int i = 1;
            for (String value : set) {
                array[i] = value;
                i++;
            }
            return array;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public TriBoolean test(Assignment assignment) throws BenchmarkException {
            if (assignment.isUnassigned(values))
                return TriBoolean.Maybe;
            String value = assignment.subst(values[0]);
            for (int i = 1; i < values.length; i++) {
                if (assignment.subst(values[i]).equals(value))
                    return TriBoolean.True;
            }
            return TriBoolean.False;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
