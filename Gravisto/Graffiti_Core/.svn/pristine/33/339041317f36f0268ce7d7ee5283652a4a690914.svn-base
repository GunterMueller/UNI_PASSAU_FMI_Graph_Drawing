// =============================================================================
//
//   TabulatingOutput.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.output;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Graph;
import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.Data;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class TabulatingOutput extends BenchmarkOutput {
    private static final Pattern STRING_PATTERN = Pattern
            .compile("\\\"(.*)\\\"");

    protected abstract void beginTable(List<String> headers)
            throws BenchmarkException;

    protected abstract void addRow(List<String> values)
            throws BenchmarkException;

    protected abstract void endTable() throws IOException, BenchmarkException;

    private Map<String, Map<String, String>> accumulatingMap;

    /**
     * {@inheritDoc}
     */
    @Override
    public final void prepare(int sourceCount) throws BenchmarkException {
        if (columns == null)
            throw new BenchmarkException("error.nocolumns");

        if (options != null && options.containsKey("accumulate")) {
            accumulatingMap = new LinkedHashMap<String, Map<String, String>>();
        }
        if (accumulatingMap == null) {
            List<String> headers = new LinkedList<String>();
            for (Column column : columns) {
                headers.add(column.getName());
            }
            beginTable(headers);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void postConfig(Data data, Assignment assignment)
            throws IOException, BenchmarkException {
        if (accumulatingMap == null) {
            List<String> values = new LinkedList<String>();
            for (Column column : columns) {
                values.add(getValue(column.getItem(), data, assignment));
            }
            addRow(values);
        } else {
            Map<String, String> map = accumulatingMap
                    .get(data.getSourceIndex());
            if (map == null) {
                map = new LinkedHashMap<String, String>();
                accumulatingMap.put(data.getSourceIndex(), map);
            }
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<String, String> entry : assignment.getAssignments()
                    .entrySet()) {
                builder.append(entry.getKey()).append("=").append(
                        entry.getValue()).append("_");
            }
            String configPrefix = builder.toString();
            for (Column column : columns) {
                String item = column.getItem();
                if (isSourceKey(item)) {
                    if (!map.containsKey(item)) {
                        map.put(column.getName(), getValue(column.getItem(),
                                data, assignment));
                    }
                } else {
                    map.put(configPrefix + column.getName(), getValue(column
                            .getItem(), data, assignment));
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void postBenchmark() throws IOException, BenchmarkException {
        if (accumulatingMap != null) {
            List<String> headers = new LinkedList<String>();
            headers.add("sourceIndex");
            for (String key : accumulatingMap.values().iterator().next()
                    .keySet()) {
                headers.add(key);
            }
            beginTable(headers);
            List<String> values = new LinkedList<String>();
            for (Map.Entry<String, Map<String, String>> entry : accumulatingMap
                    .entrySet()) {
                values.clear();
                values.add(entry.getKey());
                Map<String, String> map = entry.getValue();
                Iterator<String> iter = headers.iterator();
                iter.next(); // Skip source index.
                while (iter.hasNext()) {
                    values.add(map.get(iter.next()));
                }
                addRow(values);
            }
        }
        endTable();
    }

    private String getValue(String item, Data data, Assignment assignment) {
        if (item.startsWith("graph."))
            return getGraphValue(data.getCurrentGraph(), item.substring(6));
        else if (item.startsWith("originalGraph."))
            return getGraphValue(data.getOriginalGraph(), item.substring(14));
        else if (item.equals("sourceIndex"))
            return data.getSourceIndex();
        else if (item.equals("configurationIndex"))
            return assignment.getConfigurationIndex();
        Map<String, Long> timesMap = data.getTimes();
        Long time = timesMap.get(item);
        if (time != null)
            return String.format((Locale) null, "%f", time / 1000000000.0);
        Map<String, Object> results = data.getResults();
        Object obj = results.get(item);
        if (obj != null)
            return obj.toString(); // TODO: format string option

        if (assignment.isVariable(item))
            return assignment.getAssignments().get(item);

        Matcher matcher = STRING_PATTERN.matcher(item);
        if (matcher.matches())
            return matcher.group(1);

        return null;
    }

    private String getGraphValue(Graph graph, String subItem) {
        if (subItem.equals("nodes.count"))
            return "" + graph.getNumberOfNodes();
        else if (subItem.equals("edges.count"))
            return "" + graph.getNumberOfEdges();
        else if (subItem.equals("path")) {
            try {
                return graph.getString("benchmark.sources.source0.path");
            } catch (AttributeNotFoundException e) {
                return null;
            }
        } else
            return null;
    }

    private boolean isSourceKey(String key) {
        return key.equals("sourceIndex") || key.startsWith("originalGraph.");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
