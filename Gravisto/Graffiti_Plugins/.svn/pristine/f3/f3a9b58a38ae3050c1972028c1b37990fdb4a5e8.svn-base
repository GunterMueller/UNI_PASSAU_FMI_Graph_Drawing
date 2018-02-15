// =============================================================================
//
//   Switch.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.body;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.Benchmark;
import org.graffiti.plugins.tools.benchmark.BenchmarkCallback;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.Data;
import org.graffiti.plugins.tools.benchmark.LoggingUtil;
import org.graffiti.plugins.tools.benchmark.Seedable;
import org.graffiti.plugins.tools.benchmark.xml.FormatException;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class Switch implements BodyElement, Seedable {
    private Map<String, Sequence> cases;
    private Long fixedSeed;
    private long actualSeed;
    private String variable;

    public Switch(String variable, Assignment assignment)
            throws FormatException {
        if (!assignment.isVariable(variable))
            throw new FormatException("error.variableNotFound", variable);
        this.variable = variable;
        cases = new HashMap<String, Sequence>();
    }

    public Sequence addCase(String value, Assignment assignment) {
        Sequence sequence = new Sequence();
        String val = assignment.substAliases(value);
        boolean isContained = false;
        for (String v : assignment.getDomain(variable)) {
            if (v.equals(val)) {
                isContained = true;
                break;
            }
        }
        if (!isContained) {
            Logger logger = LoggingUtil.getLogger();
            logger.warning(Benchmark.getString("warning.caseNotSatisfied",
                    variable, val));
        }
        // assignment.
        cases.put(val, sequence);
        return sequence;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFixedSeed(long fixedSeed) {
        this.fixedSeed = fixedSeed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNext(BodyElement nextElement) throws BenchmarkException {
        if (cases.isEmpty())
            throw new BenchmarkException("error.emptySwitch");

        for (Sequence sequence : cases.values()) {
            sequence.setNext(nextElement);
        }
    }

    @Override
    public void updateSeed(long seed) {
        actualSeed = fixedSeed == null ? seed : fixedSeed;
        Random random = new Random(actualSeed);
        for (Sequence sequence : cases.values()) {
            sequence.updateSeed(random.nextLong());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Data data, final Assignment assignment)
            throws BenchmarkException {
        if (assignment.isUnassigned(variable)) {
            assignment.execute(variable, new BenchmarkCallback() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void call() throws BenchmarkException {
                    execute(assignment, data.copy());
                }
            });
        } else {
            execute(assignment, data);
        }
    }

    private void execute(Assignment assignment, Data data)
            throws BenchmarkException {
        Sequence sequence = cases.get(assignment.subst(variable));
        if (sequence != null) {
            sequence.execute(data, assignment);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
