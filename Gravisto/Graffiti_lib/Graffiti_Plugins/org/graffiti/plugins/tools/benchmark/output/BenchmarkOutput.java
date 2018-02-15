// =============================================================================
//
//   BenchmarkOutput.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.benchmark.output;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graffiti.plugins.tools.benchmark.Assignment;
import org.graffiti.plugins.tools.benchmark.BenchmarkException;
import org.graffiti.plugins.tools.benchmark.Data;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class BenchmarkOutput {
    private String target;
    private File outputDirectory;

    protected String item;
    protected List<Column> columns;
    protected Map<String, String> options;

    protected OutputStream getOut(Data data, Assignment assignment)
            throws IOException {
        return getOut(data.getSourceIndex() + "_", assignment
                .getConfigurationIndex()
                + "_", false);
    }

    protected OutputStream getOut() throws IOException {
        return getOut(false);
    }

    protected OutputStream getOut(boolean append) throws IOException {
        return getOut("", "", append);
    }

    private OutputStream getOut(String sourcePrefix, String configPrefix,
            boolean append) throws IOException {
        if (target.equals("stdout"))
            return System.out;
        else if (target.equals("stderr"))
            return System.err;
        else {
            StringBuilder builder = new StringBuilder(outputDirectory
                    .getCanonicalPath());
            builder.append(File.separatorChar);
            builder.append(sourcePrefix).append(configPrefix).append(target);
            return new FileOutputStream(builder.toString(), append);
        }
    }

    public final void postConfig(Assignment assignment, Data data)
            throws BenchmarkException {
        if (target == null)
            throw new BenchmarkException("error.outputNotInitialized");

        try {
            postConfig(data, assignment);
        } catch (IOException e) {
            throw new BenchmarkException(e);
        }
    }

    public final void finish() throws BenchmarkException {
        if (target == null)
            throw new BenchmarkException("error.outputNotInitialized");

        try {
            postBenchmark();
        } catch (IOException e) {
            throw new BenchmarkException(e);
        }
    }

    protected void postConfig(Data data, Assignment assignment)
            throws IOException, BenchmarkException {
    }

    public void postBenchmark() throws IOException, BenchmarkException {
    }

    protected void initialize() {
    }

    public final void initialize(String target, String options, String item,
            File outputDirectory) {
        this.target = target;
        this.item = item;
        this.outputDirectory = outputDirectory;
        initialize();
    }

    public final void addColumn(Column column) {
        if (columns == null) {
            columns = new LinkedList<Column>();
        }
        columns.add(column);
    }

    public void prepare(int sourceCount) throws BenchmarkException {
    }

    public void setOption(String key, String value) {
        if (options == null) {
            options = new HashMap<String, String>();
        }
        options.put(key, value);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
