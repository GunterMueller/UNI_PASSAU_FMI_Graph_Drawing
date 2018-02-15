// =============================================================================
//
//   Constraint.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.constraint;

import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.xml.FormatException;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class AbstractConstraint implements Constraint {
    protected String[] values;
    protected Constraint[] constraints;

    public static Constraint parse(String string) throws FormatException {
        try {
            return (Constraint) new Parser(new Scanner(string)).parse().value;
        } catch (Exception e) {
            throw new FormatException("error.constraintFormat", string);
        }
    }

    protected AbstractConstraint() {
    }

    protected AbstractConstraint(String... values) {
        this.values = values;
    }

    protected AbstractConstraint(Constraint... constraints) {
        this.constraints = constraints;
    }

    public abstract TriBoolean test(Assignment assignment)
            throws BenchmarkException;

    protected final Double toDouble(String value) {
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected TriBoolean toTri(boolean value) {
        return value ? TriBoolean.True : TriBoolean.False;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
