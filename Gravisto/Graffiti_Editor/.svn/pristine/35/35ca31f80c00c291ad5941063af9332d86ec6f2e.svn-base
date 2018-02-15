// =============================================================================
//
//   Assignment.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.graffiti.plugins.tools.benchmark.constraint.Constraint;
import org.graffiti.plugins.tools.benchmark.constraint.TriBoolean;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class Assignment {
    private Map<String, String> variables;
    private Map<String, String> aliases;
    private Map<String, Set<String>> domains;
    private List<Constraint> constraints;
    private String indexFormat;
    private int configurationIndex;

    protected Assignment() {
        variables = new HashMap<String, String>();
        aliases = new HashMap<String, String>();
        domains = new HashMap<String, Set<String>>();
        constraints = new LinkedList<Constraint>();
    }

    /**
     * Returns if the specified value is a variable.
     * 
     * @param name
     *            the name of the variable in question.
     * @return {@code true}, if the specified value is a variable.
     */
    public boolean isVariable(String name) {
        return domains.containsKey(name);
    }

    /**
     * Returns if one of the specified values is an unassigned variable.
     * 
     * @param values
     *            the values.
     * @return {@code true}, if one of the specified values is an unassigned
     *         variable.
     */
    public boolean isUnassigned(String... values) {
        for (String value : values) {
            if (domains.containsKey(value) && !variables.containsKey(value))
                return true;
        }
        return false;
    }

    /**
     * Substitutes the specified value if it is an alias.
     * 
     * @param value
     *            the value to substitute.
     * @return the substitute if the specified value is an alias, else the
     *         specified value.
     */
    public String substAliases(String value) {
        String v = aliases.get(value);
        if (v == null)
            return value;
        else
            return v;
    }

    public String subst(String value) throws BenchmarkException {
        String v = variables.get(value);
        if (v == null) {
            if (domains.containsKey(value))
                throw new BenchmarkException("error.unassignedVariable", value);
            v = value;
        }
        return substAliases(v);
    }

    public void addAlias(String name, String value) {
        aliases.put(name, value);
    }

    public void addVariable(String name, Set<String> domain) {
        domains.put(name, domain);
    }

    public void addConstraint(Constraint constraint) {
        constraints.add(constraint);
    }

    public void execute(String variableName, BenchmarkCallback callback)
            throws BenchmarkException {
        Set<String> domain = domains.get(variableName);

        if (domain == null || variables.containsKey(variableName))
            throw new IllegalArgumentException();

        for (String value : domain) {
            variables.put(variableName, value);
            boolean isSatisfied = true;
            for (Constraint constraint : constraints) {
                TriBoolean result = constraint.test(this);
                if (result == TriBoolean.False) {
                    isSatisfied = false;
                    break;
                }
            }

            if (isSatisfied) {
                callback.call();
            }

            variables.remove(variableName);
        }
    }

    /*
     * private void execute(final String[] variableNames, final int
     * variableIndex, final BenchmarkCallback callback) throws
     * BenchmarkException { if (variableIndex < variableNames.length) {
     * execute(variableNames[variableIndex], new BenchmarkCallback() {
     * 
     * @Override public void call() throws BenchmarkException {
     * execute(variableNames, variableIndex + 1, callback); } }); } else {
     * callback.call(); } }
     */

    /*
     * public void execute(String[] variableNames, BenchmarkCallback callback)
     * throws BenchmarkException { execute(variableNames, 0, callback); }
     */

    public Map<String, String> getAssignments() {
        return variables;
    }

    public Set<String> getDomain(String variable) {
        return domains.get(variable);
    }

    protected void prepare() {
        double estimatedLength = 0;
        for (Set<String> domain : domains.values()) {
            estimatedLength += Math.log10(domain.size());
        }
        indexFormat = "%0" + Math.max(1, (int) Math.ceil(estimatedLength))
                + "d";
    }

    protected void resetConfigurationIndex() {
        configurationIndex = 0;
    }

    public void incConfigurationIndex() {
        configurationIndex++;
    }

    public String getConfigurationIndex() {
        return String.format(indexFormat, configurationIndex);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
