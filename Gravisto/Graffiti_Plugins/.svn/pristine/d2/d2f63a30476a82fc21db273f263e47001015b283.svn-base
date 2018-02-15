// =============================================================================
//
//   BoundParameter.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark;

import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.tools.benchmark.constraint.Constraint;
import org.graffiti.plugins.tools.benchmark.constraint.TriBoolean;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class BoundParameter {
    private Integer index;
    private String name;
    private Object objectValue;
    private String stringValue;
    private String typeHint;
    private Constraint onlyIf;

    public BoundParameter() {
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Object objectValue) {
        this.objectValue = objectValue;
    }

    public void setValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public void setTypeHint(String typeHint) {
        this.typeHint = typeHint;
    }

    public void setOnlyIf(Constraint onlyIf) {
        this.onlyIf = onlyIf;
    }

    protected void apply(Parameter<?>[] parameters, Assignment assignment,
            long seed) throws BenchmarkException {
        if (onlyIf != null && onlyIf.test(assignment) != TriBoolean.True)
            return;

        Parameter<?> param = null;
        if (index != null) {
            param = parameters[index];
        } else if (name != null) {
            for (Parameter<?> parameter : parameters) {
                String parameterName = parameter.getName();
                if (parameterName != null && parameterName.equals(name)) {
                    param = parameter;
                    break;
                }
            }
            if (param == null)
                throw new BenchmarkException("error.parameterNotFound", name);
        } else
            throw new IllegalStateException(Benchmark
                    .getString("error.unspecifiedParameter"));
        if (objectValue != null) {
            param.setObjectValue(objectValue);
        } else {
            ParameterConverter.apply(param, assignment.subst(stringValue),
                    typeHint);
        }
    }

    protected String findUnboundVariable(Assignment assignment)
            throws BenchmarkException {
        if (stringValue != null
                && (onlyIf == null || onlyIf.test(assignment) != TriBoolean.False)
                && assignment.isUnassigned(stringValue))
            return stringValue;
        else
            return null;
    }

    public String getName() {
        return name;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
