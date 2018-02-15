// =============================================================================
//
//   GraphOutput.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.output;

import java.io.IOException;
import java.io.OutputStream;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.io.OutputSerializer;
import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.Data;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class GraphOutput extends BenchmarkOutput {
    protected OutputSerializer serializer;

    public GraphOutput(OutputSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void postConfig(Data data, Assignment assignment)
            throws IOException, BenchmarkException {
        Graph graph = getGraph(data);
        OutputStream outputStream = getOut(data, assignment);
        // TODO: strip benchmark attribute if wanted (options)
        serializer.write(outputStream, graph);
    }

    private Graph getGraph(Data data) throws BenchmarkException {
        if (item.equals("graph"))
            return data.getCurrentGraph();
        else if (item.equals("originalGraph"))
            return data.getOriginalGraph();
        else
            throw new BenchmarkException("error.unknownOutputItem", item);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
