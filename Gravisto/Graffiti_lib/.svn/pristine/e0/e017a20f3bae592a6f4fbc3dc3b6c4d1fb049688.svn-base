// =============================================================================
//
//   ConfigOutput.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.output;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.Data;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ConfigInfoOutput extends BenchmarkOutput {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void postConfig(Data data, Assignment assignment)
            throws IOException, BenchmarkException {
        PrintWriter out = new PrintWriter(getOut(true), false);

        out.println("--BEGIN-----------------------");
        out.println("Source index: " + data.getSourceIndex());
        out.println("Configuration index: "
                + assignment.getConfigurationIndex());
        out.println("Assignments:");
        for (Map.Entry<String, String> entry : assignment.getAssignments()
                .entrySet()) {
            out.println(entry.getKey() + " = " + entry.getValue());
        }
        out.println("Times:");
        for (Map.Entry<String, Long> entry : data.getTimes().entrySet()) {
            out.println(entry.getKey() + " = " + entry.getValue());
        }
        out.println("Results:");
        for (Map.Entry<String, Object> entry : data.getResults().entrySet()) {
            out.println(entry.getKey() + " = " + entry.getValue());
        }
        out.println("--END-------------------------");

        out.flush();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
